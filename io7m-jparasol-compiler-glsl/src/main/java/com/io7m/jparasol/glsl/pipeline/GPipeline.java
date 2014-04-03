/*
 * Copyright © 2014 <code@io7m.com> http://io7m.com
 * 
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.jparasol.glsl.pipeline;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Pair;
import com.io7m.jlog.Log;
import com.io7m.jparasol.UIError;
import com.io7m.jparasol.glsl.GFFIError;
import com.io7m.jparasol.glsl.GTransform;
import com.io7m.jparasol.glsl.GVersion;
import com.io7m.jparasol.glsl.GVersion.GVersionES;
import com.io7m.jparasol.glsl.GVersion.GVersionFull;
import com.io7m.jparasol.glsl.GVersionChecker;
import com.io7m.jparasol.glsl.GVersionCheckerError;
import com.io7m.jparasol.glsl.GVersionsSupported;
import com.io7m.jparasol.glsl.ast.GASTShader.GASTShaderFragment;
import com.io7m.jparasol.glsl.ast.GASTShader.GASTShaderVertex;
import com.io7m.jparasol.typed.Referenced;
import com.io7m.jparasol.typed.Topology;
import com.io7m.jparasol.typed.ast.TASTCompilation;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShader;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragment;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderProgram;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderVertex;
import com.io7m.jparasol.typed.ast.TASTShaderName;
import com.io7m.jparasol.typed.ast.TASTShaderNameFlat;

public final class GPipeline
{
  private final @Nonnull TASTCompilation typed;
  private final @Nonnull Log             log;
  private final @Nonnull GVersionChecker checker;

  private GPipeline(
    final @Nonnull TASTCompilation in_typed,
    final @Nonnull Log in_log)
    throws ConstraintError
  {
    this.typed = Constraints.constrainNotNull(in_typed, "Typed AST");
    this.log = new Log(in_log, "gpipeline");
    this.checker = GVersionChecker.newVersionChecker(in_log);
  }

  public static @Nonnull GPipeline newPipeline(
    final @Nonnull TASTCompilation typed,
    final @Nonnull Log log)
    throws ConstraintError
  {
    return new GPipeline(typed, log);
  }

  public @Nonnull
    Map<GVersion, Pair<GASTShaderVertex, GASTShaderFragment>>
    makeProgram(
      final @Nonnull TASTShaderNameFlat name,
      final @Nonnull SortedSet<GVersionES> required_versions_es,
      final @Nonnull SortedSet<GVersionFull> required_versions_full)
      throws UIError,
        ConstraintError,
        GFFIError,
        GVersionCheckerError
  {
    final TASTDShader s = this.typed.lookupShader(name);
    if (s == null) {
      throw UIError.shaderProgramNonexistent(name, this.typed);
    }

    if (s instanceof TASTDShaderProgram) {
      final TASTDShaderProgram p = (TASTDShaderProgram) s;
      final TASTShaderName vn = p.getVertexShader();
      final TASTShaderName fn = p.getFragmentShader();
      final TASTShaderNameFlat vnf = TASTShaderNameFlat.fromShaderName(vn);
      final TASTShaderNameFlat fnf = TASTShaderNameFlat.fromShaderName(fn);

      final Map<GVersion, GASTShaderVertex> v_map =
        this.makeVertexShader(
          vnf,
          required_versions_es,
          required_versions_full);

      final Map<GVersion, GASTShaderFragment> f_map =
        this.makeFragmentShader(
          fnf,
          required_versions_es,
          required_versions_full);

      final Set<GVersion> p_set = GPipeline.programVersions(v_map, f_map);

      final HashMap<GVersion, Pair<GASTShaderVertex, GASTShaderFragment>> programs =
        new HashMap<GVersion, Pair<GASTShaderVertex, GASTShaderFragment>>();

      for (final GVersion version : p_set) {
        assert v_map.containsKey(version);
        assert f_map.containsKey(version);
        final GASTShaderVertex vs = v_map.get(version);
        final GASTShaderFragment fs = f_map.get(version);
        final Pair<GASTShaderVertex, GASTShaderFragment> pair =
          new Pair<GASTShaderVertex, GASTShaderFragment>(vs, fs);
        programs.put(version, pair);
      }

      assert programs.size() == p_set.size();
      return programs;
    }

    throw UIError.shaderProgramNotProgram(name, s, this.typed);
  }

  /**
   * The versions supported by a program is the intersection of the versions
   * supported by its vertex and fragment shaders.
   */

  private static @Nonnull Set<GVersion> programVersions(
    final @Nonnull Map<GVersion, GASTShaderVertex> v_map,
    final @Nonnull Map<GVersion, GASTShaderFragment> f_map)
  {
    final Set<GVersion> v_set = v_map.keySet();
    final Set<GVersion> f_set = f_map.keySet();
    final HashSet<GVersion> p_set = new HashSet<GVersion>();

    for (final GVersion v : v_set) {
      if (f_set.contains(v)) {
        p_set.add(v);
      }
    }

    return p_set;
  }

  private @Nonnull Map<GVersion, GASTShaderVertex> makeVertexShader(
    final @Nonnull TASTShaderNameFlat name,
    final @Nonnull SortedSet<GVersionES> required_versions_es,
    final @Nonnull SortedSet<GVersionFull> required_versions_full)
    throws ConstraintError,
      GFFIError,
      GVersionCheckerError
  {
    final TASTDShader s = this.typed.lookupShader(name);
    Constraints.constrainNotNull(s, "Shader");
    Constraints.constrainArbitrary(
      s instanceof TASTDShaderVertex,
      "Shader is vertex shader");

    final TASTDShaderVertex v = (TASTDShaderVertex) s;
    final Referenced referenced =
      Referenced.fromShader(this.typed, name, this.log);
    final Topology topo =
      Topology.fromShader(this.typed, referenced, name, this.log);

    final GVersionsSupported supported =
      this.checker.checkVertexShader(
        v,
        required_versions_full,
        required_versions_es);

    final HashMap<GVersion, GASTShaderVertex> produced =
      new HashMap<GVersion, GASTShaderVertex>();

    for (final GVersionES version : supported.getESVersions()) {
      assert produced.containsKey(version) == false;
      produced
        .put(version, GTransform.transformVertex(
          this.typed,
          topo,
          name,
          version,
          this.log));
    }

    for (final GVersionFull version : supported.getFullVersions()) {
      assert produced.containsKey(version) == false;
      produced
        .put(version, GTransform.transformVertex(
          this.typed,
          topo,
          name,
          version,
          this.log));
    }

    return produced;
  }

  private @Nonnull Map<GVersion, GASTShaderFragment> makeFragmentShader(
    final @Nonnull TASTShaderNameFlat name,
    final @Nonnull SortedSet<GVersionES> required_versions_es,
    final @Nonnull SortedSet<GVersionFull> required_versions_full)
    throws ConstraintError,
      GFFIError,
      GVersionCheckerError
  {
    final TASTDShader s = this.typed.lookupShader(name);
    Constraints.constrainNotNull(s, "Shader");
    Constraints.constrainArbitrary(
      s instanceof TASTDShaderFragment,
      "Shader is fragment shader");

    final TASTDShaderFragment f = (TASTDShaderFragment) s;
    final Referenced referenced =
      Referenced.fromShader(this.typed, name, this.log);
    final Topology topo =
      Topology.fromShader(this.typed, referenced, name, this.log);

    final GVersionsSupported supported =
      this.checker.checkFragmentShader(
        f,
        required_versions_full,
        required_versions_es);

    final HashMap<GVersion, GASTShaderFragment> produced =
      new HashMap<GVersion, GASTShaderFragment>();

    for (final GVersionES version : supported.getESVersions()) {
      assert produced.containsKey(version) == false;
      produced.put(version, GTransform.transformFragment(
        this.typed,
        topo,
        name,
        version,
        this.log));
    }

    for (final GVersionFull version : supported.getFullVersions()) {
      assert produced.containsKey(version) == false;
      produced.put(version, GTransform.transformFragment(
        this.typed,
        topo,
        name,
        version,
        this.log));
    }

    return produced;
  }
}
