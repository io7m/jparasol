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

package com.io7m.jparasol.core;

import java.util.SortedSet;

import com.io7m.jequality.annotations.EqualityStructural;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;

/**
 * Metadata for a compiled program shader.
 */

@EqualityStructural public final class UncompactedProgramShaderMeta implements
  CompiledShaderMetaType
{
  /**
   * Construct a shader.
   * 
   * @param in_name
   *          The fully-qualified shader name.
   * @param in_supports_es
   *          The supported GLSL ES versions.
   * @param in_supports_full
   *          The supported GLSL versions.
   * @param in_fragment_shader
   *          The fragment shader.
   * @param in_vertex_shaders
   *          The vertex shaders.
   * 
   * @return A shader.
   */

  public static UncompactedProgramShaderMeta newMetadata(
    final String in_name,
    final SortedSet<GVersionES> in_supports_es,
    final SortedSet<GVersionFull> in_supports_full,
    final String in_fragment_shader,
    final SortedSet<String> in_vertex_shaders)
  {
    return new UncompactedProgramShaderMeta(
      in_name,
      in_supports_es,
      in_supports_full,
      in_fragment_shader,
      in_vertex_shaders);
  }

  private final String                  fragment_shader;
  private final String                  name;
  private final SortedSet<GVersionES>   supports_es;
  private final SortedSet<GVersionFull> supports_full;
  private final SortedSet<String>       vertex_shaders;

  private UncompactedProgramShaderMeta(
    final String in_name,
    final SortedSet<GVersionES> in_supports_es,
    final SortedSet<GVersionFull> in_supports_full,
    final String in_fragment_shader,
    final SortedSet<String> in_vertex_shaders)
  {
    this.name = NullCheck.notNull(in_name, "Name");
    this.supports_es =
      NullCheck.notNullAll(in_supports_es, "GLSL ES versions");
    this.supports_full =
      NullCheck.notNullAll(in_supports_full, "GLSL versions");
    this.fragment_shader =
      NullCheck.notNull(in_fragment_shader, "Fragment shader");
    this.vertex_shaders =
      NullCheck.notNullAll(in_vertex_shaders, "Vertex shaders");
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
    final UncompactedProgramShaderMeta other =
      (UncompactedProgramShaderMeta) obj;
    return this.fragment_shader.equals(other.fragment_shader)
      && this.name.equals(other.name)
      && this.supports_es.equals(other.supports_es)
      && this.supports_full.equals(other.supports_full)
      && this.vertex_shaders.equals(other.vertex_shaders);
  }

  /**
   * @return The name of the fragment shader.
   */

  public String getFragmentShader()
  {
    return this.fragment_shader;
  }

  @Override public String getName()
  {
    return this.name;
  }

  @Override public SortedSet<GVersionES> getSupportsES()
  {
    return this.supports_es;
  }

  @Override public SortedSet<GVersionFull> getSupportsFull()
  {
    return this.supports_full;
  }

  /**
   * @return The name of the vertex shaders.
   */

  public SortedSet<String> getVertexShaders()
  {
    return this.vertex_shaders;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.fragment_shader.hashCode();
    result = (prime * result) + this.name.hashCode();
    result = (prime * result) + this.supports_es.hashCode();
    result = (prime * result) + this.supports_full.hashCode();
    result = (prime * result) + this.vertex_shaders.hashCode();
    return result;
  }
}
