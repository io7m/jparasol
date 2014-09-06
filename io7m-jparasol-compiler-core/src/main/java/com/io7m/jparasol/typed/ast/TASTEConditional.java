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

package com.io7m.jparasol.typed.ast;

import java.io.File;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.lexer.Position;
import com.io7m.jparasol.typed.TType;
import com.io7m.jparasol.typed.TType.TBoolean;

// CHECKSTYLE_JAVADOC:OFF

@EqualityReference public final class TASTEConditional implements
  TASTExpressionType
{
  private final TASTExpressionType condition;
  private final TASTExpressionType left;
  private final TASTExpressionType right;

  public TASTEConditional(
    final TASTExpressionType in_condition,
    final TASTExpressionType in_left,
    final TASTExpressionType in_right)
  {
    this.condition = NullCheck.notNull(in_condition, "Condition");
    this.left = NullCheck.notNull(in_left, "Left");
    this.right = NullCheck.notNull(in_right, "Right");

    assert (this.condition.getType().equals(TBoolean.get()));
    assert (this.left.getType().equals(this.right.getType()));
  }

  @Override public
    <A, C, L, E extends Throwable, V extends TASTExpressionVisitorType<A, C, L, E>>
    A
    expressionVisitableAccept(
      final V v)
      throws E
  {
    final boolean traverse = v.expressionVisitConditionalPre(this);

    A c = null;
    A l = null;
    A r = null;

    if (traverse) {
      v.expressionVisitConditionalConditionPre(this);
      c = this.condition.expressionVisitableAccept(v);
      v.expressionVisitConditionalConditionPost(this);

      v.expressionVisitConditionalLeftPre(this);
      l = this.left.expressionVisitableAccept(v);
      v.expressionVisitConditionalLeftPost(this);

      v.expressionVisitConditionalRightPre(this);
      r = this.right.expressionVisitableAccept(v);
      v.expressionVisitConditionalRightPost(this);
    }

    return v.expressionVisitConditional(c, l, r, this);
  }

  public TASTExpressionType getCondition()
  {
    return this.condition;
  }

  @Override public File getFile()
  {
    return this.condition.getFile();
  }

  public TASTExpressionType getLeft()
  {
    return this.left;
  }

  @Override public Position getPosition()
  {
    return this.condition.getPosition();
  }

  public TASTExpressionType getRight()
  {
    return this.right;
  }

  @Override public TType getType()
  {
    return this.left.getType();
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[TASTEConditional ");
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
