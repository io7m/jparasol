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

import com.io7m.jequality.annotations.EqualityStructural;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;

/**
 * A fragment shader output.
 */

@EqualityStructural public final class JPFragmentOutput implements
  Comparable<JPFragmentOutput>
{
  /**
   * Construct an output.
   * 
   * @param in_name
   *          The output name.
   * @param in_index
   *          The output index.
   * @param in_type
   *          The output type.
   * @return An input.
   */

  public static JPFragmentOutput newOutput(
    final String in_name,
    final Integer in_index,
    final String in_type)
  {
    return new JPFragmentOutput(in_index, in_name, in_type);
  }

  private final Integer index;
  private final String  name;
  private final String  type;

  JPFragmentOutput(
    final Integer in_index,
    final String in_name,
    final String in_type)
  {
    this.index = NullCheck.notNull(in_index, "Index");
    this.name = NullCheck.notNull(in_name, "Output name");
    this.type = NullCheck.notNull(in_type, "Output type");
  }

  @Override public int compareTo(
    final JPFragmentOutput o)
  {
    return this.name.compareTo(o.name);
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
    final JPFragmentOutput other = (JPFragmentOutput) obj;
    if (!this.index.equals(other.index)) {
      return false;
    }
    if (!this.name.equals(other.name)) {
      return false;
    }
    if (!this.type.equals(other.type)) {
      return false;
    }
    return true;
  }

  /**
   * @return The integer index of the output. This is used to address the
   *         output on older versions of OpenGL that do not support named
   *         fragment shader outputs.
   */

  public Integer getIndex()
  {
    return this.index;
  }

  /**
   * @return The name of the output. This is used to address the output on
   *         newer versions of OpenGL that support named fragment shader
   *         outputs.
   */

  public String getName()
  {
    return this.name;
  }

  /**
   * @return The name of the type of the output. This is the type as it
   *         appears in GLSL. For example, most fragment shaders have outputs
   *         of type <code>"vec4"</code>.
   */

  public String getType()
  {
    return this.type;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.index.hashCode();
    result = (prime * result) + this.name.hashCode();
    result = (prime * result) + this.type.hashCode();
    return result;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[FragmentOutput ");
    builder.append(this.index);
    builder.append(" ");
    builder.append(this.name);
    builder.append(" ");
    builder.append(this.type);
    builder.append("]");
    final String r = builder.toString();
    assert r != null;
    return r;
  }
}
