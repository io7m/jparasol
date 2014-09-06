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
 * A conditional statement.
 */

@EqualityReference public final class GASTStatementConditional implements
  GASTStatementType
{
  private final GASTExpressionType condition;
  private final GASTStatementType  left;
  private final GASTStatementType  right;

  /**
   * Construct a statement.
   *
   * @param in_condition
   *          The condition
   * @param in_left
   *          The left branch
   * @param in_right
   *          The right branch
   */

  public GASTStatementConditional(
    final GASTExpressionType in_condition,
    final GASTStatementType in_left,
    final GASTStatementType in_right)
  {
    this.condition = NullCheck.notNull(in_condition, "Condition");
    this.left = NullCheck.notNull(in_left, "Left branch");
    this.right = NullCheck.notNull(in_right, "Right branch");
  }

  /**
   * @return The condition
   */

  public GASTExpressionType getCondition()
  {
    return this.condition;
  }

  /**
   * @return The left branch
   */

  public GASTStatementType getLeft()
  {
    return this.left;
  }

  /**
   * @return The right branch
   */

  public GASTStatementType getRight()
  {
    return this.right;
  }

  @Override public
    <A, C, E extends Throwable, V extends GASTStatementVisitorType<A, C, E>>
    A
    statementVisitableAccept(
      final V v)
      throws E
  {
    v.statementVisitConditionalPre(this);

    v.statementVisitConditionalLeftPre(this);
    final A l = this.left.statementVisitableAccept(v);
    v.statementVisitConditionalLeftPost(this);

    v.statementVisitConditionalRightPre(this);
    final A r = this.right.statementVisitableAccept(v);
    v.statementVisitConditionalRightPost(this);

    return v.statementVisitConditional(l, r, this);
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[GASTConditional ");
    builder.append(this.condition);
    builder.append(" ");
    builder.append(this.left);
    builder.append(" ");
    builder.append(this.right);
    builder.append("]");
    final String r = builder.toString();
    assert r != null;
    return r;
  }
}
