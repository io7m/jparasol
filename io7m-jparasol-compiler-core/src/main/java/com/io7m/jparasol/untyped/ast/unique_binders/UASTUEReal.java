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

package com.io7m.jparasol.untyped.ast.unique_binders;

import java.math.BigDecimal;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.lexer.TokenLiteralReal;

// CHECKSTYLE_JAVADOC:OFF

@EqualityReference public final class UASTUEReal implements
  UASTUExpressionType
{
  private final TokenLiteralReal token;

  public UASTUEReal(
    final TokenLiteralReal in_token)
  {
    this.token = NullCheck.notNull(in_token, "Token");
  }

  @Override public
    <A, C, L, E extends Throwable, V extends UASTUExpressionVisitorType<A, C, L, E>>
    A
    expressionVisitableAccept(
      final V v)
      throws E
  {
    return v.expressionVisitReal(this);
  }

  public TokenLiteralReal getToken()
  {
    return this.token;
  }

  public BigDecimal getValue()
  {
    return this.token.getValue();
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[UASTUEReal ");
    builder.append(this.token.getValue());
    builder.append("]");
    return builder.toString();
  }
}
