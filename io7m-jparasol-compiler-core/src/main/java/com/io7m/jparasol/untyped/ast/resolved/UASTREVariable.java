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

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jnull.NullCheck;

@EqualityReference public final class UASTREVariable implements
  UASTRExpressionType
{
  private final UASTRTermName name;

  public UASTREVariable(
    final UASTRTermName in_name)
  {
    this.name = NullCheck.notNull(in_name, "Name");
  }

  @Override public
    <A, C, L, E extends Throwable, V extends UASTRExpressionVisitorType<A, C, L, E>>
    A
    expressionVisitableAccept(
      final V v)
      throws E
  {
    return v.expressionVisitVariable(this);
  }

  public UASTRTermName getName()
  {
    return this.name;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[UASTREVariable ");
    builder.append(this.name.show());
    builder.append("]");
    return builder.toString();
  }
}
