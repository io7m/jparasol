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
import com.io7m.jparasol.glsl.ast.GTermName.GTermNameLocal;

/**
 * A local variable.
 */

@EqualityReference public final class GASTStatementLocalVariable implements
  GASTStatementType
{
  private final GASTExpressionType expression;
  private final GTermNameLocal     name;
  private final GTypeName          type;

  /**
   * Construct a statement.
   *
   * @param in_name
   *          The name
   * @param in_type
   *          The type name
   * @param in_expression
   *          The body
   */

  public GASTStatementLocalVariable(
    final GTermNameLocal in_name,
    final GTypeName in_type,
    final GASTExpressionType in_expression)
  {
    this.name = NullCheck.notNull(in_name, "Name");
    this.type = NullCheck.notNull(in_type, "Type");
    this.expression = NullCheck.notNull(in_expression, "Expression");
  }

  /**
   * @return The expression
   */

  public GASTExpressionType getExpression()
  {
    return this.expression;
  }

  /**
   * @return The name
   */

  public GTermNameLocal getName()
  {
    return this.name;
  }

  /**
   * @return The type
   */

  public GTypeName getType()
  {
    return this.type;
  }

  @Override public
    <A, C, E extends Throwable, V extends GASTStatementVisitorType<A, C, E>>
    A
    statementVisitableAccept(
      final V v)
      throws E
  {
    return v.statementVisitLocalVariable(this);
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[GASTLocalVariable ");
    builder.append(this.name);
    builder.append(" ");
    builder.append(this.type);
    builder.append(" ");
    builder.append(this.expression);
    builder.append("]");
    final String r = builder.toString();
    assert r != null;
    return r;
  }

}
