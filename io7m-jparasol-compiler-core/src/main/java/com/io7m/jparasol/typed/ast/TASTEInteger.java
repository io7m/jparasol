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

package com.io7m.jparasol.typed.ast;

import java.io.File;
import java.math.BigInteger;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.lexer.Position;
import com.io7m.jparasol.lexer.TokenLiteralIntegerType;
import com.io7m.jparasol.lexer.TokenType;
import com.io7m.jparasol.typed.TType;
import com.io7m.jparasol.typed.TType.TInteger;

// CHECKSTYLE_JAVADOC:OFF

@EqualityReference public final class TASTEInteger implements
  TASTExpressionMatchConstantType
{
  private final TokenLiteralIntegerType token;

  public TASTEInteger(
    final TokenLiteralIntegerType in_token)
  {
    this.token = NullCheck.notNull(in_token, "Token");
  }

  @Override public
    <A, C, L, E extends Throwable, V extends TASTExpressionVisitorType<A, C, L, E>>
    A
    expressionVisitableAccept(
      final V v)
      throws E
  {
    return v.expressionVisitInteger(this);
  }

  @Override public File getFile()
  {
    return this.token.getFile();
  }

  @Override public Position getPosition()
  {
    return this.token.getPosition();
  }

  @Override public TokenType getToken()
  {
    return this.token;
  }

  public TokenLiteralIntegerType getTokenInteger()
  {
    return this.token;
  }

  @Override public TType getType()
  {
    return TInteger.get();
  }

  public BigInteger getValue()
  {
    return this.token.getValue();
  }

  @Override public
    <A, E extends Throwable, V extends TASTExpressionMatchConstantVisitorType<A, E>>
    A
    matchConstantVisitableAccept(
      final V v)
      throws E
  {
    return v.expressionVisitInteger(this);
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[TASTEInteger ");
    builder.append(this.getValue());
    builder.append("]");
    final String r = builder.toString();
    assert r != null;
    return r;
  }
}
