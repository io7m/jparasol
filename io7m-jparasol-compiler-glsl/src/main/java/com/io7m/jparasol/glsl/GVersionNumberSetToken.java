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

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.lexer.Position;

public abstract class GVersionNumberSetToken
{
  public static final class TokenComma extends GVersionNumberSetToken
  {
    @SuppressWarnings("synthetic-access") public TokenComma(
      final @Nonnull File file,
      final @Nonnull Position position)
      throws ConstraintError
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

  public static final class TokenEOF extends GVersionNumberSetToken
  {
    @SuppressWarnings("synthetic-access") public TokenEOF(
      final @Nonnull File file,
      final @Nonnull Position position)
      throws ConstraintError
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

  public interface TokenLiteralInteger
  {
    public @Nonnull BigInteger getValue();
  }

  public static final class TokenLiteralIntegerDecimal extends
    GVersionNumberSetToken implements TokenLiteralInteger
  {
    public static @Nonnull TokenLiteralIntegerDecimal newIntegerDecimal(
      final @Nonnull File file,
      final @Nonnull Position position,
      final @Nonnull String text)
      throws ConstraintError
    {
      Constraints.constrainNotNull(text, "Text");
      return new TokenLiteralIntegerDecimal(file, position, new BigInteger(
        text));
    }

    private final @Nonnull BigInteger value;

    @SuppressWarnings("synthetic-access") private TokenLiteralIntegerDecimal(
      final @Nonnull File file,
      final @Nonnull Position position,
      final @Nonnull BigInteger in_value)
      throws ConstraintError
    {
      super(Type.TOKEN_LITERAL_INTEGER_DECIMAL, file, position);
      this.value = in_value;
    }

    @Override public boolean equals(
      final Object obj)
    {
      if (this == obj) {
        return true;
      }
      if (!super.equals(obj)) {
        return false;
      }
      if (this.getClass() != obj.getClass()) {
        return false;
      }
      final TokenLiteralIntegerDecimal other =
        (TokenLiteralIntegerDecimal) obj;
      if (!this.value.equals(other.value)) {
        return false;
      }
      return true;
    }

    @Override public @Nonnull BigInteger getValue()
    {
      return this.value;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = super.hashCode();
      result =
        (prime * result) + ((this.value == null) ? 0 : this.value.hashCode());
      return result;
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

  public static final class TokenRoundLeft extends GVersionNumberSetToken
  {
    @SuppressWarnings("synthetic-access") public TokenRoundLeft(
      final @Nonnull File file,
      final @Nonnull Position position)
      throws ConstraintError
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

  public static final class TokenRoundRight extends GVersionNumberSetToken
  {
    @SuppressWarnings("synthetic-access") public TokenRoundRight(
      final @Nonnull File file,
      final @Nonnull Position position)
      throws ConstraintError
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

  public static final class TokenSquareLeft extends GVersionNumberSetToken
  {
    @SuppressWarnings("synthetic-access") public TokenSquareLeft(
      final @Nonnull File file,
      final @Nonnull Position position)
      throws ConstraintError
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

  public static final class TokenSquareRight extends GVersionNumberSetToken
  {
    @SuppressWarnings("synthetic-access") public TokenSquareRight(
      final @Nonnull File file,
      final @Nonnull Position position)
      throws ConstraintError
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

  public static enum Type
  {
    TOKEN_COMMA("comma"),
    TOKEN_EOF("EOF"),
    TOKEN_LITERAL_INTEGER_DECIMAL("integer literal (decimal)"),
    TOKEN_ROUND_LEFT("'('"),
    TOKEN_ROUND_RIGHT("')'"),
    TOKEN_SQUARE_LEFT("'['"),
    TOKEN_SQUARE_RIGHT("']'");

    private final @Nonnull String description;

    private Type(
      final @Nonnull String in_description)
    {
      this.description = in_description;
    }

    public @Nonnull String getDescription()
    {
      return this.description;
    }
  }

  private final @Nonnull File     file;
  private final @Nonnull Position position;
  private final @Nonnull Type     type;

  private GVersionNumberSetToken(
    final @Nonnull Type in_type,
    final @Nonnull File in_file,
    final @Nonnull Position in_position)
    throws ConstraintError
  {
    this.file = Constraints.constrainNotNull(in_file, "File");
    this.position = Constraints.constrainNotNull(in_position, "Position");
    this.type = Constraints.constrainNotNull(in_type, "Type");
  }

  @Override public boolean equals(
    final Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    final GVersionNumberSetToken other = (GVersionNumberSetToken) obj;
    if (!this.file.equals(other.file)) {
      return false;
    }
    if (!this.position.equals(other.position)) {
      return false;
    }
    if (this.type != other.type) {
      return false;
    }
    return true;
  }

  public @Nonnull final File getFile()
  {
    return this.file;
  }

  public @Nonnull final Position getPosition()
  {
    return this.position;
  }

  public @Nonnull final Type getType()
  {
    return this.type;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.file.hashCode();
    result = (prime * result) + this.position.hashCode();
    result = (prime * result) + this.type.hashCode();
    return result;
  }
}
