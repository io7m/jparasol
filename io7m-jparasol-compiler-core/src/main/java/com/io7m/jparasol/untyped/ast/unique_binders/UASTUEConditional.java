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
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITWHETHER IN AN ACTION
 * OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN
 * CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.jparasol.untyped.ast.unique_binders;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.lexer.TokenIf;

// CHECKSTYLE_JAVADOC:OFF

@EqualityReference public final class UASTUEConditional implements
  UASTUExpressionType
{
  private final UASTUExpressionType condition;
  private final UASTUExpressionType left;
  private final UASTUExpressionType right;
  private final TokenIf             token;

  public UASTUEConditional(
    final TokenIf in_token,
    final UASTUExpressionType in_condition,
    final UASTUExpressionType in_left,
    final UASTUExpressionType in_right)
  {
    this.token = NullCheck.notNull(in_token, "Token");
    this.condition = NullCheck.notNull(in_condition, "Condition");
    this.left = NullCheck.notNull(in_left, "Left");
    this.right = NullCheck.notNull(in_right, "Right");
  }

  @Override public
    <A, C, L, E extends Throwable, V extends UASTUExpressionVisitorType<A, C, L, E>>
    A
    expressionVisitableAccept(
      final V v)
      throws E
  {
    v.expressionVisitConditionalPre(this);
    final A c = this.condition.expressionVisitableAccept(v);
    final A l = this.left.expressionVisitableAccept(v);
    final A r = this.right.expressionVisitableAccept(v);
    return v.expressionVisitConditional(c, l, r, this);
  }

  public UASTUExpressionType getCondition()
  {
    return this.condition;
  }

  public TokenIf getIf()
  {
    return this.token;
  }

  public UASTUExpressionType getLeft()
  {
    return this.left;
  }

  public UASTUExpressionType getRight()
  {
    return this.right;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[UASTUEConditional ");
    builder.append(this.condition);
    builder.append(" ");
    builder.append(this.left);
    builder.append(" ");
    builder.append(this.right);
    builder.append("]");
    return builder.toString();
  }
}
