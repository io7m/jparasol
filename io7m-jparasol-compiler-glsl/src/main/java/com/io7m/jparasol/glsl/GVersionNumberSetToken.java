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

package com.io7m.jparasol.glsl;

import java.io.File;
import java.math.BigInteger;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.lexer.Position;

/**
 * A version number set token.
 */

// CHECKSTYLE_JAVADOC:OFF

@EqualityReference public abstract class GVersionNumberSetToken
{
  @EqualityReference public static final class TokenComma extends
    GVersionNumberSetToken
  {
    @SuppressWarnings("synthetic-access") public TokenComma(
      final File file,
      final Position position)

    {
      super(Type.TOKEN_COMMA, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenComma []");
      return builder.toString();
    }
  }

  @EqualityReference public static final class TokenEOF extends
    GVersionNumberSetToken
  {
    @SuppressWarnings("synthetic-access") public TokenEOF(
      final File file,
      final Position position)

    {
      super(Type.TOKEN_EOF, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenEOF []");
      return builder.toString();
    }
  }

  @EqualityReference public static final class TokenLiteralIntegerDecimal extends
    GVersionNumberSetToken implements TokenLiteralIntegerType
  {
    public static TokenLiteralIntegerDecimal newIntegerDecimal(
      final File file,
      final Position position,
      final String text)
    {
      NullCheck.notNull(text, "Text");
      return new TokenLiteralIntegerDecimal(file, position, new BigInteger(
        text));
    }

    private final BigInteger value;

    @SuppressWarnings("synthetic-access") private TokenLiteralIntegerDecimal(
      final File file,
      final Position position,
      final BigInteger in_value)

    {
      super(Type.TOKEN_LITERAL_INTEGER_DECIMAL, file, position);
      this.value = in_value;
    }

    @Override public BigInteger getValue()
    {
      return this.value;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenLiteralIntegerDecimal [value=");
      builder.append(this.value);
      builder.append("]");
      return builder.toString();
    }
  }

  public interface TokenLiteralIntegerType
  {
    BigInteger getValue();
  }

  @EqualityReference public static final class TokenRoundLeft extends
    GVersionNumberSetToken
  {
    @SuppressWarnings("synthetic-access") public TokenRoundLeft(
      final File file,
      final Position position)

    {
      super(Type.TOKEN_ROUND_LEFT, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenRoundLeft []");
      return builder.toString();
    }
  }

  @EqualityReference public static final class TokenRoundRight extends
    GVersionNumberSetToken
  {
    @SuppressWarnings("synthetic-access") public TokenRoundRight(
      final File file,
      final Position position)

    {
      super(Type.TOKEN_ROUND_RIGHT, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenRoundRight []");
      return builder.toString();
    }
  }

  @EqualityReference public static final class TokenSquareLeft extends
    GVersionNumberSetToken
  {
    @SuppressWarnings("synthetic-access") public TokenSquareLeft(
      final File file,
      final Position position)

    {
      super(Type.TOKEN_SQUARE_LEFT, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenSquareLeft []");
      return builder.toString();
    }
  }

  @EqualityReference public static final class TokenSquareRight extends
    GVersionNumberSetToken
  {
    @SuppressWarnings("synthetic-access") public TokenSquareRight(
      final File file,
      final Position position)
    {
      super(Type.TOKEN_SQUARE_RIGHT, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenSquareRight []");
      return builder.toString();
    }
  }

  /**
   * Token types.
   */

  public static enum Type
  {
    TOKEN_COMMA("comma"),
    TOKEN_EOF("EOF"),
    TOKEN_LITERAL_INTEGER_DECIMAL("integer literal (decimal)"),
    TOKEN_ROUND_LEFT("'('"),
    TOKEN_ROUND_RIGHT("')'"),
    TOKEN_SQUARE_LEFT("'['"),
    TOKEN_SQUARE_RIGHT("']'");

    private final String description;

    private Type(
      final String in_description)
    {
      this.description = in_description;
    }

    public String getDescription()
    {
      return this.description;
    }
  }

  private final File     file;
  private final Position position;
  private final Type     type;

  private GVersionNumberSetToken(
    final Type in_type,
    final File in_file,
    final Position in_position)
  {
    this.file = NullCheck.notNull(in_file, "File");
    this.position = NullCheck.notNull(in_position, "Position");
    this.type = NullCheck.notNull(in_type, "Type");
  }

  public final File getFile()
  {
    return this.file;
  }

  public final Position getPosition()
  {
    return this.position;
  }

  public final Type getType()
  {
    return this.type;
  }
}
