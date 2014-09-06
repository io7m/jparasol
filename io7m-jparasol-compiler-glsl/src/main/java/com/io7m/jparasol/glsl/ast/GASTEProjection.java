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
import com.io7m.jparasol.typed.TType;

/**
 * The type of record projections.
 */

@EqualityReference public final class GASTEProjection implements
  GASTExpressionType
{
  private final GASTExpressionType body;
  private final GFieldName         field;
  private final TType              type;

  /**
   * Construct a record projection.
   *
   * @param in_body
   *          The left-hand side of the projection
   * @param in_field
   *          The field
   * @param in_type
   *          The type
   */

  public GASTEProjection(
    final GASTExpressionType in_body,
    final GFieldName in_field,
    final TType in_type)
  {
    this.body = NullCheck.notNull(in_body, "Body");
    this.field = NullCheck.notNull(in_field, "Field");
    this.type = NullCheck.notNull(in_type, "Type");
  }

  @Override public
    <A, E extends Throwable, V extends GASTExpressionVisitorType<A, E>>
    A
    expressionVisitableAccept(
      final V v)
      throws E
  {
    v.expressionProjectionVisitPre(this);
    final A x = this.body.expressionVisitableAccept(v);
    return v.expressionProjectionVisit(x, this);
  }

  /**
   * @return The body
   */

  public GASTExpressionType getBody()
  {
    return this.body;
  }

  /**
   * @return The field
   */

  public GFieldName getField()
  {
    return this.field;
  }

  /**
   * @return The type
   */

  public TType getType()
  {
    return this.type;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[GASTEProjection ");
    builder.append(this.body);
    builder.append(" ");
    builder.append(this.field);
    builder.append(" ");
    builder.append(this.type);
    builder.append("]");
    final String r = builder.toString();
    assert r != null;
    return r;
  }
}
