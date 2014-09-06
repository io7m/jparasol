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

package com.io7m.jparasol.untyped.ast.checked;

// CHECKSTYLE_JAVADOC:OFF

import java.util.ArrayList;
import java.util.List;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jnull.NullCheck;

@EqualityReference public final class UASTCEApplication implements
  UASTCExpressionType
{
  private final List<UASTCExpressionType> arguments;
  private final UASTCValuePath            name;

  public UASTCEApplication(
    final UASTCValuePath in_name,
    final List<UASTCExpressionType> in_arguments)
  {
    this.name = NullCheck.notNull(in_name, "Name");
    this.arguments = NullCheck.notNull(in_arguments, "Arguments");
  }

  @Override public
    <A, C, L, E extends Throwable, V extends UASTCExpressionVisitorType<A, C, L, E>>
    A
    expressionVisitableAccept(
      final V v)
      throws E
  {
    v.expressionVisitApplicationPre(this);
    final List<A> args = new ArrayList<A>();
    for (final UASTCExpressionType a : this.arguments) {
      final A x = a.expressionVisitableAccept(v);
      args.add(x);
    }
    return v.expressionVisitApplication(args, this);
  }

  public List<UASTCExpressionType> getArguments()
  {
    return this.arguments;
  }

  public UASTCValuePath getName()
  {
    return this.name;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[UASTCEApplication ");
    builder.append(this.name.show());
    builder.append(" ");
    builder.append(this.arguments);
    builder.append("]");
    return builder.toString();
  }
}
