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

package com.io7m.jparasol.untyped.ast.resolved;

// CHECKSTYLE_JAVADOC:OFF

import java.util.ArrayList;
import java.util.List;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jnull.NullCheck;

@EqualityReference public final class UASTRENew implements
  UASTRExpressionType
{
  private final List<UASTRExpressionType> arguments;
  private final UASTRTypeName             name;

  public UASTRENew(
    final UASTRTypeName in_name,
    final List<UASTRExpressionType> in_arguments)
  {
    this.name = NullCheck.notNull(in_name, "Name");
    this.arguments = NullCheck.notNull(in_arguments, "Arguments");
  }

  @Override public
    <A, C, L, E extends Throwable, V extends UASTRExpressionVisitorType<A, C, L, E>>
    A
    expressionVisitableAccept(
      final V v)
      throws E
  {
    final List<A> args = new ArrayList<A>();
    for (final UASTRExpressionType b : this.arguments) {
      final A x = b.expressionVisitableAccept(v);
      args.add(x);
    }
    return v.expressionVisitNew(args, this);
  }

  public List<UASTRExpressionType> getArguments()
  {
    return this.arguments;
  }

  public UASTRTypeName getName()
  {
    return this.name;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[UASTRENew ");
    builder.append(this.name.show());
    builder.append(" ");
    builder.append(this.arguments);
    builder.append("]");
    return builder.toString();
  }
}
