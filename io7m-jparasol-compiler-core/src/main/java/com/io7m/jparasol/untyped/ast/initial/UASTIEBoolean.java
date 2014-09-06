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

package com.io7m.jparasol.untyped.ast.initial;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.lexer.TokenLiteralBoolean;

// CHECKSTYLE_JAVADOC:OFF

@EqualityReference public final class UASTIEBoolean implements
  UASTIExpressionMatchConstantType
{
  private final TokenLiteralBoolean token;

  public UASTIEBoolean(
    final TokenLiteralBoolean in_token)
  {
    this.token = NullCheck.notNull(in_token, "Token");
  }

  @Override public
    <A, C, L, E extends Throwable, V extends UASTIExpressionVisitorType<A, C, L, E>>
    A
    expressionVisitableAccept(
      final V v)
      throws E
  {
    return v.expressionVisitBoolean(this);
  }

  public TokenLiteralBoolean getToken()
  {
    return this.token;
  }

  public boolean getValue()
  {
    return this.token.getValue();
  }

  @Override public
    <A, E extends Throwable, V extends UASTIExpressionMatchConstantVisitorType<A, E>>
    A
    matchConstantVisitableAccept(
      final V v)
      throws E
  {
    return v.expressionVisitBoolean(this);
  }
}
