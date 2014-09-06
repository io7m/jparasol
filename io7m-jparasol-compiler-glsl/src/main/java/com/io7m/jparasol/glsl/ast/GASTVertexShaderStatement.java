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

package com.io7m.jparasol.glsl.ast;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jnull.NullCheck;

/**
 * A vertex shader statement.
 */

@EqualityReference public abstract class GASTVertexShaderStatement
{
  /**
   * An output assignment.
   */

  @EqualityReference public static final class GASTVertexOutputAssignment extends
    GASTVertexShaderStatement
  {
    private final GShaderOutputName name;
    private final GTermName         value;

    /**
     * Construct a statement.
     *
     * @param in_name
     *          The name
     * @param in_value
     *          The value
     */

    public GASTVertexOutputAssignment(
      final GShaderOutputName in_name,
      final GTermName in_value)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.value = NullCheck.notNull(in_value, "Value");
    }

    /**
     * @return The output name
     */

    public GShaderOutputName getName()
    {
      return this.name;
    }

    /**
     * @return The value
     */

    public GTermName getValue()
    {
      return this.value;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[GASTVertexOutputAssignment ");
      builder.append(this.name);
      builder.append(" ");
      builder.append(this.value);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }
}
