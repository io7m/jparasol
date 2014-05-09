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

package com.io7m.jparasol.glsl;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.glsl.ast.GASTExpression;

/**
 * The type of FFI expressions.
 */

@EqualityReference public abstract class GFFIExpression
{
  /**
   * The type of built-in FFI expressions.
   */

  @EqualityReference public static final class GFFIExpressionBuiltIn extends
    GFFIExpression
  {
    private final GASTExpression expression;

    /**
     * Construct an expression.
     * 
     * @param in_expression
     *          The actual expression
     */

    public GFFIExpressionBuiltIn(
      final GASTExpression in_expression)
    {
      this.expression = NullCheck.notNull(in_expression, "Expression");
    }

    @Override public
      <A, E extends Throwable, V extends GFFIExpressionVisitorType<A, E>>
      A
      ffiExpressionAccept(
        final V v)
        throws E
    {
      return v.ffiExpressionVisitBuiltIn(this);
    }

    /**
     * @return The expression
     */

    public GASTExpression getExpression()
    {
      return this.expression;
    }
  }

  /**
   * The type of defined FFI expressions.
   */

  @EqualityReference public static final class GFFIExpressionDefined extends
    GFFIExpression
  {
    /**
     * Construct an expression.
     */

    public GFFIExpressionDefined()
    {
      // Nothing
    }

    @Override public
      <A, E extends Throwable, V extends GFFIExpressionVisitorType<A, E>>
      A
      ffiExpressionAccept(
        final V v)
        throws E
    {
      return v.ffiExpressionVisitDefined(this);
    }
  }

  /**
   * Accept a generic visitor.
   * 
   * @param v
   *          The visitor
   * @return The value returned by the visitor
   * @throws E
   *           If the visitor raises <code>E</code>
   */

  public abstract
    <A, E extends Throwable, V extends GFFIExpressionVisitorType<A, E>>
    A
    ffiExpressionAccept(
      final V v)
      throws E;
}
