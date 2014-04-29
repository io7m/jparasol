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

package com.io7m.jparasol.xml;

import com.io7m.jequality.annotations.EqualityStructural;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;

/**
 * A pair of shaders.
 */

@EqualityStructural public final class CompactedShaders
{
  private final String fragment_shader;
  private final String vertex_shader;

  CompactedShaders(
    final String in_vertex_shader,
    final String in_fragment_shader)
  {
    this.vertex_shader = NullCheck.notNull(in_vertex_shader, "Vertex shader");
    this.fragment_shader =
      NullCheck.notNull(in_fragment_shader, "Fragment shader");
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
    final CompactedShaders other = (CompactedShaders) obj;
    if (!this.fragment_shader.equals(other.fragment_shader)) {
      return false;
    }
    if (!this.vertex_shader.equals(other.vertex_shader)) {
      return false;
    }
    return true;
  }

  /**
   * @return The fragment shader
   */

  public String getFragmentShader()
  {
    return this.fragment_shader;
  }

  /**
   * @return The vertex shader
   */

  public String getVertexShader()
  {
    return this.vertex_shader;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.fragment_shader.hashCode();
    result = (prime * result) + this.vertex_shader.hashCode();
    return result;
  }
}
