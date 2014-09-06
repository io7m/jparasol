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

import java.util.List;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.lexer.TokenIdentifierLower;

@EqualityReference public final class UASTCESwizzle implements
  UASTCExpressionType
{
  private final UASTCExpressionType        expression;
  private final List<TokenIdentifierLower> fields;

  public UASTCESwizzle(
    final UASTCExpressionType in_expression,
    final List<TokenIdentifierLower> in_fields)
  {
    this.expression = NullCheck.notNull(in_expression, "Expression");
    this.fields = NullCheck.notNull(in_fields, "Fields");
  }

  @Override public
    <A, C, L, E extends Throwable, V extends UASTCExpressionVisitorType<A, C, L, E>>
    A
    expressionVisitableAccept(
      final V v)
      throws E
  {
    v.expressionVisitSwizzlePre(this);
    final A x = this.expression.expressionVisitableAccept(v);
    return v.expressionVisitSwizzle(x, this);
  }

  public UASTCExpressionType getExpression()
  {
    return this.expression;
  }

  public List<TokenIdentifierLower> getFields()
  {
    return this.fields;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[UASTCESwizzle ");
    builder.append(this.expression);
    builder.append(" [");
    for (final TokenIdentifierLower f : this.fields) {
      builder.append(f.getActual());
      builder.append(" ");
    }
    builder.append("]]");
    return builder.toString();
  }
}
