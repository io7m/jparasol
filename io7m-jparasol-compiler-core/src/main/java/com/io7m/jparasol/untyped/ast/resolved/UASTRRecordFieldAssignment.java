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

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.lexer.TokenIdentifierLower;

// CHECKSTYLE_JAVADOC:OFF

@EqualityReference public final class UASTRRecordFieldAssignment
{
  private final UASTRExpressionType  expression;
  private final TokenIdentifierLower name;

  public UASTRRecordFieldAssignment(
    final TokenIdentifierLower in_name,
    final UASTRExpressionType in_expression)
  {
    this.name = NullCheck.notNull(in_name, "Name");
    this.expression = NullCheck.notNull(in_expression, "Expression");
  }

  public UASTRExpressionType getExpression()
  {
    return this.expression;
  }

  public TokenIdentifierLower getName()
  {
    return this.name;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[UASTRRecordFieldAssignment ");
    builder.append(this.name.getActual());
    builder.append(" ");
    builder.append(this.expression);
    builder.append("]");
    return builder.toString();
  }
}
