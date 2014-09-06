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

package com.io7m.jparasol.lexer;

import java.io.File;
import java.math.BigInteger;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jnull.NullCheck;

// CHECKSTYLE_JAVADOC:OFF

@EqualityReference public final class TokenLiteralIntegerDecimal implements
  TokenLiteralIntegerType
{
  public static TokenLiteralIntegerDecimal newIntegerDecimal(
    final File file,
    final Position position,
    final String text)
  {
    NullCheck.notNull(text, "Text");
    return new TokenLiteralIntegerDecimal(
      file,
      position,
      new BigInteger(text));
  }

  private final File          file;
  private final Position      position;
  private final TokenTypeEnum type;
  private final BigInteger    value;

  public TokenLiteralIntegerDecimal(
    final File in_file,
    final Position in_position,
    final BigInteger in_value)
  {
    this.file = NullCheck.notNull(in_file, "File");
    this.position = NullCheck.notNull(in_position, "Position");
    this.type = TokenTypeEnum.TOKEN_LITERAL_INTEGER_DECIMAL;
    this.value = NullCheck.notNull(in_value, "Value");
  }

  @Override public File getFile()
  {
    return this.file;
  }

  @Override public Position getPosition()
  {
    return this.position;
  }

  @Override public TokenTypeEnum getType()
  {
    return this.type;
  }

  @Override public BigInteger getValue()
  {
    return this.value;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[TokenLiteralIntegerDecimal ");
    builder.append(this.value);
    builder.append("]");
    final String r = builder.toString();
    assert r != null;
    return r;
  }
}
