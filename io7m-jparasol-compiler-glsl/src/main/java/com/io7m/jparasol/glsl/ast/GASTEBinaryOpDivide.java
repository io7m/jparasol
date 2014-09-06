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

/**
 * The type of divisions.
 */

@EqualityReference public final class GASTEBinaryOpDivide extends
  GASTEBinaryOp
{
  /**
   * Construct a binary op expression.
   *
   * @param left
   *          The left expression
   * @param right
   *          The right expression
   */

  public GASTEBinaryOpDivide(
    final GASTExpressionType left,
    final GASTExpressionType right)
  {
    super(left, right);
  }

  @Override public
    <A, E extends Throwable, V extends GASTExpressionVisitorType<A, E>>
    A
    expressionVisitableAccept(
      final V v)
      throws E
  {
    v.expressionBinaryOpDivideVisitPre(this);
    final A l = this.getLeft().expressionVisitableAccept(v);
    final A r = this.getRight().expressionVisitableAccept(v);
    return v.expressionBinaryOpDivideVisit(l, r, this);
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[GASTEBinaryOpDivide ");
    builder.append(this.getLeft());
    builder.append(" ");
    builder.append(this.getRight());
    builder.append("]");
    final String r = builder.toString();
    assert r != null;
    return r;
  }
}
