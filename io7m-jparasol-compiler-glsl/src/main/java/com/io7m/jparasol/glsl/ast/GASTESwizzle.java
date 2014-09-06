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

import java.util.List;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.typed.TType;

/**
 * The type of swizzle expressions.
 */

@EqualityReference public final class GASTESwizzle implements
  GASTExpressionType
{
  private final GASTExpressionType body;
  private final List<GFieldName>   fields;
  private final TType              type;

  /**
   * Construct a swizzle expression.
   *
   * @param in_body
   *          The body
   * @param in_fields
   *          The list of fields
   * @param in_type
   *          The resulting type
   */

  public GASTESwizzle(
    final GASTExpressionType in_body,
    final List<GFieldName> in_fields,
    final TType in_type)
  {
    this.body = NullCheck.notNull(in_body, "Body");
    this.fields = NullCheck.notNull(in_fields, "Fields");
    this.type = NullCheck.notNull(in_type, "Type");
  }

  @Override public
    <A, E extends Throwable, V extends GASTExpressionVisitorType<A, E>>
    A
    expressionVisitableAccept(
      final V v)
      throws E
  {
    v.expressionSwizzleVisitPre(this);
    final A x = this.body.expressionVisitableAccept(v);
    return v.expressionSwizzleVisit(x, this);
  }

  /**
   * @return The expression body
   */

  public GASTExpressionType getBody()
  {
    return this.body;
  }

  /**
   * @return The list of fields
   */

  public List<GFieldName> getFields()
  {
    return this.fields;
  }

  /**
   * @return The resulting type
   */

  public TType getType()
  {
    return this.type;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[GASTESwizzle ");
    builder.append(this.body);
    builder.append(" ");
    builder.append(this.fields);
    builder.append(" ");
    builder.append(this.type);
    builder.append("]");
    final String r = builder.toString();
    assert r != null;
    return r;
  }
}
