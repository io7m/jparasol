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
import com.io7m.jparasol.glsl.ast.GTermName.GTermNameGlobal;

/**
 * The type of value declarations.
 */

@EqualityReference public final class GASTTermValue implements
  GASTTermDeclarationType
{
  private final GASTExpressionType expression;
  private final GTermNameGlobal    name;
  private final GTypeName          type;

  /**
   * Construct a value declaration.
   *
   * @param in_name
   *          The name
   * @param in_type
   *          The type
   * @param in_expression
   *          The body
   */

  public GASTTermValue(
    final GTermNameGlobal in_name,
    final GTypeName in_type,
    final GASTExpressionType in_expression)
  {
    this.name = NullCheck.notNull(in_name, "Name");
    this.type = NullCheck.notNull(in_type, "Type");
    this.expression = NullCheck.notNull(in_expression, "Body");
  }

  /**
   * @return The body
   */

  public GASTExpressionType getExpression()
  {
    return this.expression;
  }

  @Override public GTermNameGlobal getName()
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
    <A, E extends Throwable, V extends GASTTermDeclarationVisitorType<A, E>>
    A
    termDeclarationVisitableAccept(
      final V v)
      throws E
  {
    return v.termVisitValue(this);
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[GASTTermValue ");
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
