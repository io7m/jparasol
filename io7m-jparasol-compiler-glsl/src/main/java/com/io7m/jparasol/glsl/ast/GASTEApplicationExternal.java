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

import java.util.ArrayList;
import java.util.List;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.glsl.ast.GTermName.GTermNameExternal;
import com.io7m.jparasol.typed.TType;

/**
 * The type of (external) function applications.
 */

@EqualityReference public final class GASTEApplicationExternal implements
  GASTExpressionType
{
  private final List<GASTExpressionType> arguments;
  private final GTermNameExternal        name;
  private final TType                    type;

  /**
   * Construct an expression.
   *
   * @param in_name
   *          The function name
   * @param in_type
   *          The type of expression
   * @param in_arguments
   *          The arguments
   */

  public GASTEApplicationExternal(
    final GTermNameExternal in_name,
    final TType in_type,
    final List<GASTExpressionType> in_arguments)
  {
    this.name = NullCheck.notNull(in_name, "Name");
    this.type = NullCheck.notNull(in_type, "Type");
    this.arguments = NullCheck.notNull(in_arguments, "Arguments");
  }

  @Override public
    <A, E extends Throwable, V extends GASTExpressionVisitorType<A, E>>
    A
    expressionVisitableAccept(
      final V v)
      throws E
  {
    v.expressionApplicationExternalVisitPre(this);
    final List<A> args = new ArrayList<A>();
    for (final GASTExpressionType a : this.arguments) {
      final A x = a.expressionVisitableAccept(v);
      args.add(x);
    }
    return v.expressionApplicationExternalVisit(args, this);
  }

  /**
   * @return The arguments
   */

  public List<GASTExpressionType> getArguments()
  {
    return this.arguments;
  }

  /**
   * @return The function name
   */

  public GTermNameExternal getName()
  {
    return this.name;
  }

  /**
   * @return The return type
   */

  public TType getType()
  {
    return this.type;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[GASTEApplicationExternal ");
    builder.append(this.name);
    builder.append(" ");
    builder.append(this.type);
    builder.append(" ");
    builder.append(this.arguments);
    builder.append("]");
    final String r = builder.toString();
    assert r != null;
    return r;
  }
}
