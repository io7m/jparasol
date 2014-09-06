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
 * The type of variables.
 */

@EqualityReference public final class GASTEVariable implements
  GASTExpressionType
{
  private final GTermName term;
  private final GTypeName type;

  /**
   * Construct a new variable.
   *
   * @param in_type
   *          The type
   * @param in_term
   *          The term
   */

  public GASTEVariable(
    final GTypeName in_type,
    final GTermName in_term)
  {
    this.type = NullCheck.notNull(in_type, "Type");
    this.term = NullCheck.notNull(in_term, "Term");
  }

  @Override public
    <A, E extends Throwable, V extends GASTExpressionVisitorType<A, E>>
    A
    expressionVisitableAccept(
      final V v)
      throws E
  {
    return v.expressionVariableVisit(this);
  }

  /**
   * @return The variable name
   */

  public GTermName getTerm()
  {
    return this.term;
  }

  /**
   * @return The type name
   */

  public GTypeName getType()
  {
    return this.type;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[GASTEVariable ");
    builder.append(this.type);
    builder.append(" ");
    builder.append(this.term);
    builder.append("]");
    final String r = builder.toString();
    assert r != null;
    return r;
  }
}
