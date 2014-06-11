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

import com.io7m.jequality.annotations.EqualityStructural;
import com.io7m.jnull.Nullable;
import com.io7m.jparasol.glsl.GVersion;
import com.io7m.jparasol.glsl.ast.GASTShader.GASTShaderFragment;
import com.io7m.jparasol.glsl.ast.GASTShader.GASTShaderVertex;
import com.io7m.jparasol.typed.ast.TASTShaderNameFlat;

/**
 * The result of transforming a set of programs.
 */

@EqualityStructural public final class GCompilation
{
  private final Map<TASTShaderNameFlat, Map<GVersion, GASTShaderFragment>> shaders_fragment;
  private final Map<TASTShaderNameFlat, GCompiledProgram>                  shaders_program;
  private final Map<TASTShaderNameFlat, Map<GVersion, GASTShaderVertex>>   shaders_vertex;

  GCompilation(
    final Map<TASTShaderNameFlat, Map<GVersion, GASTShaderVertex>> in_shaders_vertex,
    final Map<TASTShaderNameFlat, Map<GVersion, GASTShaderFragment>> in_shaders_fragment,
    final Map<TASTShaderNameFlat, GCompiledProgram> in_shaders_program)
  {
    this.shaders_vertex = in_shaders_vertex;
    this.shaders_fragment = in_shaders_fragment;
    this.shaders_program = in_shaders_program;
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
    final GCompilation other = (GCompilation) obj;
    return this.shaders_fragment.equals(other.shaders_fragment)
      && this.shaders_program.equals(other.shaders_program)
      && this.shaders_vertex.equals(other.shaders_vertex);
  }

  /**
   * @return A map of the compiled fragment shaders.
   */

  public
    Map<TASTShaderNameFlat, Map<GVersion, GASTShaderFragment>>
    getShadersFragment()
  {
    return this.shaders_fragment;
  }

  /**
   * @return A map of the compiled program shaders.
   */

  public Map<TASTShaderNameFlat, GCompiledProgram> getShadersProgram()
  {
    return this.shaders_program;
  }

  /**
   * @return A map of the compiled vertex shaders.
   */

  public
    Map<TASTShaderNameFlat, Map<GVersion, GASTShaderVertex>>
    getShadersVertex()
  {
    return this.shaders_vertex;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.shaders_fragment.hashCode();
    result = (prime * result) + this.shaders_program.hashCode();
    result = (prime * result) + this.shaders_vertex.hashCode();
    return result;
  }
}
