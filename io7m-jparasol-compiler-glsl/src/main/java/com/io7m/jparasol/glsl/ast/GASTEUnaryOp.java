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
 * The type of unary op expressions.
 */

@EqualityReference public abstract class GASTEUnaryOp implements
  GASTExpressionType
{
  /**
   * The type of negations.
   */

  @EqualityReference public static final class GASTEUnaryOpNegate extends
    GASTEUnaryOp
  {
    /**
     * Construct a negation.
     *
     * @param body
     *          The body of the expression
     */

    public GASTEUnaryOpNegate(
      final GASTExpressionType body)
    {
      super(body);
    }

    @Override public
      <A, E extends Throwable, V extends GASTExpressionVisitorType<A, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      v.expressionUnaryOpNegateVisitPre(this);
      final A x = this.getBody().expressionVisitableAccept(v);
      return v.expressionUnaryOpNegateVisit(x, this);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[GASTEUnaryOpNegate ");
      builder.append(this.getBody());
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  private final GASTExpressionType body;

  protected GASTEUnaryOp(
    final GASTExpressionType in_body)
  {
    this.body = NullCheck.notNull(in_body, "Body");
  }

  /**
   * @return The expression body
   */

  public final GASTExpressionType getBody()
  {
    return this.body;
  }
}
