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

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.io7m.jequality.annotations.EqualityStructural;
import com.io7m.jlog.LogUsableType;
import com.io7m.jnull.Nullable;
import com.io7m.jparasol.core.GVersionES;
import com.io7m.jparasol.core.GVersionFull;
import com.io7m.jparasol.core.JPUncompactedProgramShaderMeta;
import com.io7m.jparasol.typed.ast.TASTShaderNameFlat;

/**
 * A set of names for a compiled program.
 */

@EqualityStructural public final class GCompiledProgram
{
  private final TASTShaderNameFlat                             name;
  private final GCompiledFragmentShader                        shader_fragment;
  private final Map<TASTShaderNameFlat, GCompiledVertexShader> shaders_vertex;
  private final SortedSet<GVersionES>                          versions_es;
  private final SortedSet<GVersionFull>                        versions_full;

  GCompiledProgram(
    final Map<TASTShaderNameFlat, GCompiledVertexShader> in_shaders_vertex,
    final GCompiledFragmentShader in_shader_fragment,
    final TASTShaderNameFlat in_name,
    final SortedSet<GVersionES> in_versions_es,
    final SortedSet<GVersionFull> in_versions_full)
  {
    this.shaders_vertex = in_shaders_vertex;
    this.shader_fragment = in_shader_fragment;
    this.name = in_name;
    this.versions_es = in_versions_es;
    this.versions_full = in_versions_full;
  }

  @Override public boolean equals(
    final @Nullable Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    final GCompiledProgram other = (GCompiledProgram) obj;
    return this.name.equals(other.name)
      && this.shader_fragment.equals(other.shader_fragment)
      && this.shaders_vertex.equals(other.shaders_vertex)
      && this.versions_es.equals(other.versions_es)
      && this.versions_full.equals(other.versions_full);
  }

  /**
   * Produce a flattened version of the given shader (a shader with the
   * majority of the semantic information stripped out, ready for
   * serialization).
   * 
   * @param log
   *          A log interface.
   * @return A flattened shader.
   */

  public JPUncompactedProgramShaderMeta flatten(
    final LogUsableType log)
  {
    final SortedSet<String> vertex_names = new TreeSet<String>();
    for (final TASTShaderNameFlat v_name : this.shaders_vertex.keySet()) {
      vertex_names.add(v_name.show());
    }

    return JPUncompactedProgramShaderMeta.newMetadata(
      this.name.show(),
      this.versions_es,
      this.versions_full,
      this.shader_fragment.getName().show(),
      vertex_names);
  }

  /**
   * @return The current program's fully-qualified name.
   */

  public TASTShaderNameFlat getName()
  {
    return this.name;
  }

  /**
   * @return The current program's fragment shader.
   */

  public GCompiledFragmentShader getShaderFragment()
  {
    return this.shader_fragment;
  }

  /**
   * @return The set of vertex shaders against which the current program's
   *         fragment shader has been type checked.
   */

  public Map<TASTShaderNameFlat, GCompiledVertexShader> getShadersVertex()
  {
    return this.shaders_vertex;
  }

  /**
   * @return The GLSL ES versions supported by the current program (the
   *         intersection [of the intersection] of the vertex shader versions,
   *         and fragment shader versions).
   */

  public SortedSet<GVersionES> getVersionsES()
  {
    return this.versions_es;
  }

  /**
   * @return The GLSL full versions supported by the current program (the
   *         intersection [of the intersection] of the vertex shader versions,
   *         and fragment shader versions).
   */

  public SortedSet<GVersionFull> getVersionsFull()
  {
    return this.versions_full;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.name.hashCode();
    result = (prime * result) + this.shader_fragment.hashCode();
    result = (prime * result) + this.shaders_vertex.hashCode();
    result = (prime * result) + this.versions_es.hashCode();
    result = (prime * result) + this.versions_full.hashCode();
    return result;
  }
}
