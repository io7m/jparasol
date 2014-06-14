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
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jlog.LogUsableType;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.CompilerError;
import com.io7m.jparasol.UIError;
import com.io7m.jparasol.core.GVersionES;
import com.io7m.jparasol.core.GVersionFull;
import com.io7m.jparasol.core.GVersionType;
import com.io7m.jparasol.glsl.GFFIError;
import com.io7m.jparasol.glsl.GTransform;
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
import com.io7m.jparasol.typed.ast.TASTShaderNameFlat;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * A GLSL pipeline.
 */

@EqualityReference @SuppressWarnings("synthetic-access") public final class GPipeline
{
  private static
    void
    cancelAll(
      final Map<TASTShaderNameFlat, Future<GCompiledVertexShader>> futures_vertex,
      final Map<TASTShaderNameFlat, Future<GCompiledFragmentShader>> futures_fragment)
  {
    for (final TASTShaderNameFlat name : futures_vertex.keySet()) {
      final Future<GCompiledVertexShader> f = futures_vertex.get(name);
      f.cancel(true);
    }

    for (final TASTShaderNameFlat name : futures_fragment.keySet()) {
      final Future<GCompiledFragmentShader> f = futures_fragment.get(name);
      f.cancel(true);
    }
  }

  private static void collectVersions(
    final Set<GVersionType> v_versions,
    final Set<GVersionType> f_versions,
    final SortedSet<GVersionES> versions_es,
    final SortedSet<GVersionFull> versions_full)
  {
    final Set<GVersionES> v_es = new HashSet<GVersionES>();
    final Set<GVersionFull> v_full = new HashSet<GVersionFull>();
    final Set<GVersionES> f_es = new HashSet<GVersionES>();
    final Set<GVersionFull> f_full = new HashSet<GVersionFull>();

    for (final GVersionType v : v_versions) {
      if (v instanceof GVersionES) {
        v_es.add((GVersionES) v);
      } else {
        v_full.add((GVersionFull) v);
      }
    }

    for (final GVersionType f : f_versions) {
      if (f instanceof GVersionES) {
        f_es.add((GVersionES) f);
      } else {
        f_full.add((GVersionFull) f);
      }
    }

    versions_es.clear();
    versions_es.addAll(GPipeline.setIntersection(v_es, f_es));
    versions_full.clear();
    versions_full.addAll(GPipeline.setIntersection(v_full, f_full));
  }

  /**
   * Construct a new pipeline.
   * 
   * @param typed
   *          The typed AST.
   * @param log
   *          A log interface
   * @return A new pipeline
   */

  public static GPipeline newPipeline(
    final TASTCompilation typed,
    final ExecutorService exec,
    final LogUsableType log)
  {
    return new GPipeline(typed, exec, log);
  }

  private static GCompiledProgram processProgram(
    final Map<TASTShaderNameFlat, TASTDShaderProgram> shaders_program,
    final Map<TASTShaderNameFlat, GCompiledVertexShader> results_vertex,
    final Map<TASTShaderNameFlat, GCompiledFragmentShader> results_fragment,
    final TASTShaderNameFlat name)
  {
    final TASTDShaderProgram program = shaders_program.get(name);
    assert program != null;

    final TASTShaderNameFlat v_name =
      TASTShaderNameFlat.fromShaderName(program.getVertexShader());
    final TASTShaderNameFlat f_name =
      TASTShaderNameFlat.fromShaderName(program.getFragmentShader());

    final GCompiledVertexShader vertex = results_vertex.get(v_name);
    final GCompiledFragmentShader fragment = results_fragment.get(f_name);

    final Set<GVersionType> v_versions = vertex.getSources().keySet();
    final Set<GVersionType> f_versions = fragment.getSources().keySet();
    assert v_versions != null;
    assert f_versions != null;

    final SortedSet<GVersionES> versions_es = new TreeSet<GVersionES>();
    final SortedSet<GVersionFull> versions_full = new TreeSet<GVersionFull>();

    GPipeline.collectVersions(
      v_versions,
      f_versions,
      versions_es,
      versions_full);

    final Map<TASTShaderNameFlat, GCompiledVertexShader> vertex_shaders =
      new HashMap<TASTShaderNameFlat, GCompiledVertexShader>();
    vertex_shaders.put(v_name, vertex);

    final GCompiledProgram r =
      new GCompiledProgram(
        vertex_shaders,
        fragment,
        name,
        versions_es,
        versions_full);
    return r;
  }

  private static <A> SortedSet<A> setIntersection(
    final Set<A> v_set,
    final Set<A> f_set)
  {
    final SortedSet<A> p_set = new TreeSet<A>();

    for (final A v : v_set) {
      if (f_set.contains(v)) {
        p_set.add(v);
      }
    }

    return p_set;
  }

  private final GVersionChecker checker;
  private final ExecutorService exec;
  private final LogUsableType   log;
  private final TASTCompilation typed;

  private GPipeline(
    final TASTCompilation in_typed,
    final ExecutorService in_exec,
    final LogUsableType in_log)
  {
    this.typed = NullCheck.notNull(in_typed, "Typed AST");
    this.exec = NullCheck.notNull(in_exec, "Executor");
    this.log = NullCheck.notNull(in_log, "Log").with("gpipeline");
    this.checker = GVersionChecker.newVersionChecker(this.log);
  }

  private void collectShaders(
    final Set<TASTShaderNameFlat> program_names,
    final Map<TASTShaderNameFlat, TASTDShaderProgram> shaders_program,
    final Map<TASTShaderNameFlat, TASTDShaderVertex> shaders_vertex,
    final Map<TASTShaderNameFlat, TASTDShaderFragment> shaders_fragment)
    throws UIError
  {
    for (final TASTShaderNameFlat name : program_names) {
      assert name != null;
      final TASTDShader shader = this.typed.lookupShader(name);

      if (shader == null) {
        throw UIError.shaderProgramNonexistent(name, this.typed);
      }

      if (shader instanceof TASTDShaderProgram) {
        final TASTDShaderProgram program = (TASTDShaderProgram) shader;

        final TASTShaderNameFlat vertex =
          TASTShaderNameFlat.fromShaderName(program.getVertexShader());
        final TASTShaderNameFlat fragment =
          TASTShaderNameFlat.fromShaderName(program.getFragmentShader());

        shaders_vertex.put(
          vertex,
          (TASTDShaderVertex) this.typed.lookupShader(vertex));
        shaders_fragment.put(
          fragment,
          (TASTDShaderFragment) this.typed.lookupShader(fragment));
        shaders_program.put(name, program);

      } else {
        throw UIError.shaderProgramNotProgram(name, shader, this.typed);
      }
    }
  }

  /**
   * @return The executor for the pipeline.
   */

  public ExecutorService getExecutor()
  {
    return this.exec;
  }

  private GCompiledFragmentShader makeFragmentShader(
    final TASTShaderNameFlat name,
    final TASTDShaderFragment f,
    final SortedSet<GVersionES> required_versions_es,
    final SortedSet<GVersionFull> required_versions_full)
    throws GFFIError,
      GVersionCheckerError
  {
    final Referenced referenced =
      Referenced.fromShader(this.typed, name, this.log);
    final Topology topo =
      Topology.fromShader(this.typed, referenced, name, this.log);

    final GVersionsSupported supported =
      this.checker.checkFragmentShader(
        f,
        required_versions_full,
        required_versions_es);

    final Map<GVersionType, GASTShaderFragment> produced =
      new HashMap<GVersionType, GASTShaderFragment>();

    for (final GVersionES version : supported.getESVersions()) {
      assert version != null;
      assert produced.containsKey(version) == false;
      produced.put(version, GTransform.transformFragment(
        this.typed,
        topo,
        name,
        version,
        this.log));
    }

    for (final GVersionFull version : supported.getFullVersions()) {
      assert version != null;
      assert produced.containsKey(version) == false;
      produced.put(version, GTransform.transformFragment(
        this.typed,
        topo,
        name,
        version,
        this.log));
    }

    return GCompiledFragmentShader.newShader(name, produced);
  }

  private GCompiledVertexShader makeVertexShader(
    final TASTShaderNameFlat name,
    final TASTDShaderVertex v,
    final SortedSet<GVersionES> required_versions_es,
    final SortedSet<GVersionFull> required_versions_full)
    throws GFFIError,
      GVersionCheckerError
  {
    final Referenced referenced =
      Referenced.fromShader(this.typed, name, this.log);
    final Topology topo =
      Topology.fromShader(this.typed, referenced, name, this.log);

    final GVersionsSupported supported =
      this.checker.checkVertexShader(
        v,
        required_versions_full,
        required_versions_es);

    final Map<GVersionType, GASTShaderVertex> produced =
      new HashMap<GVersionType, GASTShaderVertex>();

    for (final GVersionES version : supported.getESVersions()) {
      assert version != null;
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
      assert version != null;
      assert produced.containsKey(version) == false;
      produced
        .put(version, GTransform.transformVertex(
          this.typed,
          topo,
          name,
          version,
          this.log));
    }

    return GCompiledVertexShader.newShader(name, produced);
  }

  private GCompilation processAll(
    final SortedSet<GVersionES> required_versions_es,
    final SortedSet<GVersionFull> required_versions_full,
    final Map<TASTShaderNameFlat, TASTDShaderVertex> shaders_vertex,
    final Map<TASTShaderNameFlat, TASTDShaderFragment> shaders_fragment,
    final Map<TASTShaderNameFlat, TASTDShaderProgram> shaders_program)
    throws CompilerError
  {
    final Map<TASTShaderNameFlat, Future<GCompiledVertexShader>> futures_vertex =
      new HashMap<TASTShaderNameFlat, Future<GCompiledVertexShader>>();
    final Map<TASTShaderNameFlat, Future<GCompiledFragmentShader>> futures_fragment =
      new HashMap<TASTShaderNameFlat, Future<GCompiledFragmentShader>>();

    try {
      this.submitShadersVertex(
        required_versions_es,
        required_versions_full,
        shaders_vertex,
        futures_vertex);

      this.submitShadersFragment(
        required_versions_es,
        required_versions_full,
        shaders_fragment,
        futures_fragment);

      final Map<TASTShaderNameFlat, GCompiledVertexShader> results_vertex =
        new HashMap<TASTShaderNameFlat, GCompiledVertexShader>();
      final Map<TASTShaderNameFlat, GCompiledFragmentShader> results_fragment =
        new HashMap<TASTShaderNameFlat, GCompiledFragmentShader>();

      for (final TASTShaderNameFlat name : futures_vertex.keySet()) {
        assert name != null;
        assert futures_vertex.containsKey(name);
        final Future<GCompiledVertexShader> future = futures_vertex.get(name);

        try {
          final GCompiledVertexShader r = future.get();
          results_vertex.put(name, r);
        } catch (final InterruptedException e) {
          throw new UnreachableCodeException(e);
        } catch (final ExecutionException e) {
          final CompilerError x = (CompilerError) e.getCause();
          throw x;
        }
      }

      for (final TASTShaderNameFlat name : futures_fragment.keySet()) {
        assert name != null;
        assert futures_fragment.containsKey(name);
        final Future<GCompiledFragmentShader> future =
          futures_fragment.get(name);

        try {
          final GCompiledFragmentShader r = future.get();
          results_fragment.put(name, r);
        } catch (final InterruptedException e) {
          throw new UnreachableCodeException(e);
        } catch (final ExecutionException e) {
          final CompilerError x = (CompilerError) e.getCause();
          throw x;
        }
      }

      final Map<TASTShaderNameFlat, GCompiledProgram> results_program =
        new HashMap<TASTShaderNameFlat, GCompiledProgram>();

      for (final TASTShaderNameFlat name : shaders_program.keySet()) {
        assert name != null;

        final GCompiledProgram r =
          GPipeline.processProgram(
            shaders_program,
            results_vertex,
            results_fragment,
            name);

        results_program.put(name, r);
      }

      return new GCompilation(
        results_vertex,
        results_fragment,
        results_program);

    } catch (final CompilerError e) {
      assert futures_vertex != null;
      assert futures_fragment != null;
      GPipeline.cancelAll(futures_vertex, futures_fragment);
      throw e;
    }
  }

  private
    void
    submitShadersFragment(
      final SortedSet<GVersionES> required_versions_es,
      final SortedSet<GVersionFull> required_versions_full,
      final Map<TASTShaderNameFlat, TASTDShaderFragment> shaders_fragment,
      final Map<TASTShaderNameFlat, Future<GCompiledFragmentShader>> futures_fragment)
  {
    for (final TASTShaderNameFlat name : shaders_fragment.keySet()) {
      assert name != null;
      final TASTDShaderFragment f = shaders_fragment.get(name);
      assert f != null;

      final Future<GCompiledFragmentShader> future =
        this.exec.submit(new Callable<GCompiledFragmentShader>() {
          @Override public GCompiledFragmentShader call()
            throws Exception
          {
            return GPipeline.this.makeFragmentShader(
              name,
              f,
              required_versions_es,
              required_versions_full);
          }
        });

      futures_fragment.put(name, future);
    }
  }

  private
    void
    submitShadersVertex(
      final SortedSet<GVersionES> required_versions_es,
      final SortedSet<GVersionFull> required_versions_full,
      final Map<TASTShaderNameFlat, TASTDShaderVertex> shaders_vertex,
      final Map<TASTShaderNameFlat, Future<GCompiledVertexShader>> futures_vertex)
  {
    for (final TASTShaderNameFlat name : shaders_vertex.keySet()) {
      assert name != null;
      final TASTDShaderVertex v = shaders_vertex.get(name);
      assert v != null;

      final Future<GCompiledVertexShader> future =
        this.exec.submit(new Callable<GCompiledVertexShader>() {
          @Override public GCompiledVertexShader call()
            throws Exception
          {
            return GPipeline.this.makeVertexShader(
              name,
              v,
              required_versions_es,
              required_versions_full);
          }
        });

      futures_vertex.put(name, future);
    }
  }

  /**
   * Transform the given set of programs to GLSL, assuming the given required
   * versions.
   * 
   * @param program_names
   *          The set of program names.
   * @param required_versions_es
   *          The required GLSL ES versions.
   * @param required_versions_full
   *          The required GLSL versions.
   * @return A compilation.
   * @throws CompilerError
   *           If an error occurs, the specific subtype of which gives
   *           details.
   */

  public GCompilation transformPrograms(
    final Set<TASTShaderNameFlat> program_names,
    final SortedSet<GVersionES> required_versions_es,
    final SortedSet<GVersionFull> required_versions_full)
    throws CompilerError
  {
    NullCheck.notNullAll(program_names, "Program names");
    NullCheck.notNullAll(required_versions_es, "Required ES versions");
    NullCheck.notNullAll(required_versions_full, "Required full versions");

    final Map<TASTShaderNameFlat, TASTDShaderProgram> shaders_program =
      new HashMap<TASTShaderNameFlat, TASTDShaderProgram>();
    final Map<TASTShaderNameFlat, TASTDShaderVertex> shaders_vertex =
      new HashMap<TASTShaderNameFlat, TASTDShaderVertex>();
    final Map<TASTShaderNameFlat, TASTDShaderFragment> shaders_fragment =
      new HashMap<TASTShaderNameFlat, TASTDShaderFragment>();

    this.collectShaders(
      program_names,
      shaders_program,
      shaders_vertex,
      shaders_fragment);

    return this.processAll(
      required_versions_es,
      required_versions_full,
      shaders_vertex,
      shaders_fragment,
      shaders_program);
  }
}
