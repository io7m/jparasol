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
import java.math.BigDecimal;
import java.math.BigInteger;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;

public abstract class Token
{
  public static final class TokenAs extends Token
  {
    @SuppressWarnings("synthetic-access") public TokenAs(
      final @Nonnull File file,
      final @Nonnull Position position)
      throws ConstraintError
    {
      super(Type.TOKEN_AS, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenAs []");
      return builder.toString();
    }
  }

  public static final class TokenColon extends Token
  {
    @SuppressWarnings("synthetic-access") public TokenColon(
      final @Nonnull File file,
      final @Nonnull Position position)
      throws ConstraintError
    {
      super(Type.TOKEN_COLON, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenColon []");
      return builder.toString();
    }
  }

  public static final class TokenComma extends Token
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

  public static final class TokenCurlyLeft extends Token
  {
    @SuppressWarnings("synthetic-access") public TokenCurlyLeft(
      final @Nonnull File file,
      final @Nonnull Position position)
      throws ConstraintError
    {
      super(Type.TOKEN_CURLY_LEFT, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenCurlyLeft []");
      return builder.toString();
    }
  }

  public static final class TokenCurlyRight extends Token
  {
    @SuppressWarnings("synthetic-access") public TokenCurlyRight(
      final @Nonnull File file,
      final @Nonnull Position position)
      throws ConstraintError
    {
      super(Type.TOKEN_CURLY_RIGHT, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenCurlyRight []");
      return builder.toString();
    }
  }

  public static final class TokenDiscard extends Token
  {
    @SuppressWarnings("synthetic-access") public TokenDiscard(
      final @Nonnull File file,
      final @Nonnull Position position)
      throws ConstraintError
    {
      super(Type.TOKEN_DISCARD, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenDiscard []");
      return builder.toString();
    }
  }

  public static final class TokenDot extends Token
  {
    @SuppressWarnings("synthetic-access") public TokenDot(
      final @Nonnull File file,
      final @Nonnull Position position)
      throws ConstraintError
    {
      super(Type.TOKEN_DOT, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenDot []");
      return builder.toString();
    }
  }

  public static final class TokenElse extends Token
  {
    @SuppressWarnings("synthetic-access") public TokenElse(
      final @Nonnull File file,
      final @Nonnull Position position)
      throws ConstraintError
    {
      super(Type.TOKEN_ELSE, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenElse []");
      return builder.toString();
    }
  }

  public static final class TokenEnd extends Token
  {
    @SuppressWarnings("synthetic-access") public TokenEnd(
      final @Nonnull File file,
      final @Nonnull Position position)
      throws ConstraintError
    {
      super(Type.TOKEN_END, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenEnd []");
      return builder.toString();
    }
  }

  public static final class TokenEOF extends Token
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

  public static final class TokenEquals extends Token
  {
    @SuppressWarnings("synthetic-access") public TokenEquals(
      final @Nonnull File file,
      final @Nonnull Position position)
      throws ConstraintError
    {
      super(Type.TOKEN_EQUALS, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenEquals []");
      return builder.toString();
    }
  }

  public static final class TokenExternal extends Token
  {
    @SuppressWarnings("synthetic-access") public TokenExternal(
      final @Nonnull File file,
      final @Nonnull Position position)
      throws ConstraintError
    {
      super(Type.TOKEN_EXTERNAL, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenExternal []");
      return builder.toString();
    }
  }

  public static final class TokenFragment extends Token
  {
    @SuppressWarnings("synthetic-access") public TokenFragment(
      final @Nonnull File file,
      final @Nonnull Position position)
      throws ConstraintError
    {
      super(Type.TOKEN_FRAGMENT, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenFragment []");
      return builder.toString();
    }
  }

  public static final class TokenFunction extends Token
  {
    @SuppressWarnings("synthetic-access") public TokenFunction(
      final @Nonnull File file,
      final @Nonnull Position position)
      throws ConstraintError
    {
      super(Type.TOKEN_FUNCTION, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenFunction []");
      return builder.toString();
    }
  }

  public static abstract class TokenIdentifier extends Token
  {
    @SuppressWarnings("synthetic-access") protected TokenIdentifier(
      final @Nonnull Type type,
      final @Nonnull File file,
      final @Nonnull Position position)
      throws ConstraintError
    {
      super(type, file, position);
    }

    public abstract @Nonnull String getActual();
  }

  public static final class TokenIdentifierLower extends TokenIdentifier
  {
    private final @Nonnull String text;

    public TokenIdentifierLower(
      final @Nonnull File file,
      final @Nonnull Position position,
      final @Nonnull String text)
      throws ConstraintError
    {
      super(Type.TOKEN_IDENTIFIER_LOWER, file, position);
      this.text = Constraints.constrainNotNull(text, "Text");
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
      final TokenIdentifierLower other = (TokenIdentifierLower) obj;
      if (!this.text.equals(other.text)) {
        return false;
      }
      return true;
    }

    @Override public @Nonnull String getActual()
    {
      return this.text;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = super.hashCode();
      result = (prime * result) + this.text.hashCode();
      return result;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenIdentifierLower [text=");
      builder.append(this.text);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class TokenIdentifierUpper extends TokenIdentifier
  {
    private final @Nonnull String text;

    public TokenIdentifierUpper(
      final @Nonnull File file,
      final @Nonnull Position position,
      final @Nonnull String text)
      throws ConstraintError
    {
      super(Type.TOKEN_IDENTIFIER_UPPER, file, position);
      this.text = Constraints.constrainNotNull(text, "Text");
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
      final TokenIdentifierUpper other = (TokenIdentifierUpper) obj;
      if (!this.text.equals(other.text)) {
        return false;
      }
      return true;
    }

    @Override public @Nonnull String getActual()
    {
      return this.text;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = super.hashCode();
      result = (prime * result) + this.text.hashCode();
      return result;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenIdentifierUpper [text=");
      builder.append(this.text);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class TokenIf extends Token
  {
    @SuppressWarnings("synthetic-access") public TokenIf(
      final @Nonnull File file,
      final @Nonnull Position position)
      throws ConstraintError
    {
      super(Type.TOKEN_IF, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenIf []");
      return builder.toString();
    }
  }

  public static final class TokenImport extends Token
  {
    @SuppressWarnings("synthetic-access") public TokenImport(
      final @Nonnull File file,
      final @Nonnull Position position)
      throws ConstraintError
    {
      super(Type.TOKEN_IMPORT, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenImport []");
      return builder.toString();
    }
  }

  public static final class TokenIn extends Token
  {
    @SuppressWarnings("synthetic-access") public TokenIn(
      final @Nonnull File file,
      final @Nonnull Position position)
      throws ConstraintError
    {
      super(Type.TOKEN_IN, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenIn []");
      return builder.toString();
    }
  }

  public static final class TokenIs extends Token
  {
    @SuppressWarnings("synthetic-access") public TokenIs(
      final @Nonnull File file,
      final @Nonnull Position position)
      throws ConstraintError
    {
      super(Type.TOKEN_IS, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenIs []");
      return builder.toString();
    }
  }

  public static final class TokenLet extends Token
  {
    @SuppressWarnings("synthetic-access") public TokenLet(
      final @Nonnull File file,
      final @Nonnull Position position)
      throws ConstraintError
    {
      super(Type.TOKEN_LET, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenLet []");
      return builder.toString();
    }
  }

  public static final class TokenLiteralBoolean extends Token
  {
    private final boolean value;

    @SuppressWarnings("synthetic-access") public TokenLiteralBoolean(
      final @Nonnull File file,
      final @Nonnull Position position,
      final boolean value)
      throws ConstraintError
    {
      super(Type.TOKEN_LITERAL_BOOLEAN, file, position);
      this.value = value;
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
      final TokenLiteralBoolean other = (TokenLiteralBoolean) obj;
      if (this.value != other.value) {
        return false;
      }
      return true;
    }

    public boolean getValue()
    {
      return this.value;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = super.hashCode();
      result = (prime * result) + (this.value ? 1231 : 1237);
      return result;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenLiteralBoolean [value=");
      builder.append(this.value);
      builder.append("]");
      return builder.toString();
    }
  }

  public interface TokenLiteralInteger
  {
    public @Nonnull BigInteger getValue();
  }

  public static final class TokenLiteralIntegerDecimal extends Token implements
    TokenLiteralInteger
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
      final @Nonnull BigInteger value)
      throws ConstraintError
    {
      super(Type.TOKEN_LITERAL_INTEGER_DECIMAL, file, position);
      this.value = value;
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

  public static final class TokenLiteralReal extends Token
  {
    public static @Nonnull TokenLiteralReal newReal(
      final @Nonnull File file,
      final @Nonnull Position position,
      final @Nonnull String text)
      throws ConstraintError
    {
      Constraints.constrainNotNull(text, "Text");
      return new TokenLiteralReal(file, position, new BigDecimal(text));
    }

    private final @Nonnull BigDecimal value;

    @SuppressWarnings("synthetic-access") private TokenLiteralReal(
      final @Nonnull File file,
      final @Nonnull Position position,
      final @Nonnull BigDecimal value)
      throws ConstraintError
    {
      super(Type.TOKEN_LITERAL_REAL, file, position);
      this.value = Constraints.constrainNotNull(value, "Value");
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
      final TokenLiteralReal other = (TokenLiteralReal) obj;
      if (!this.value.equals(other.value)) {
        return false;
      }
      return true;
    }

    public @Nonnull BigDecimal getValue()
    {
      return this.value;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = super.hashCode();
      result = (prime * result) + this.value.hashCode();
      return result;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenLiteralReal [value=");
      builder.append(this.value);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class TokenModule extends Token
  {
    @SuppressWarnings("synthetic-access") public TokenModule(
      final @Nonnull File file,
      final @Nonnull Position position)
      throws ConstraintError
    {
      super(Type.TOKEN_MODULE, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenModule []");
      return builder.toString();
    }
  }

  public static final class TokenNew extends Token
  {
    @SuppressWarnings("synthetic-access") public TokenNew(
      final @Nonnull File file,
      final @Nonnull Position position)
      throws ConstraintError
    {
      super(Type.TOKEN_NEW, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenNew []");
      return builder.toString();
    }
  }

  public static final class TokenOut extends Token
  {
    @SuppressWarnings("synthetic-access") public TokenOut(
      final @Nonnull File file,
      final @Nonnull Position position)
      throws ConstraintError
    {
      super(Type.TOKEN_OUT, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenOut []");
      return builder.toString();
    }
  }

  public static final class TokenPackage extends Token
  {
    @SuppressWarnings("synthetic-access") public TokenPackage(
      final @Nonnull File file,
      final @Nonnull Position position)
      throws ConstraintError
    {
      super(Type.TOKEN_PACKAGE, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenPackage []");
      return builder.toString();
    }
  }

  public static final class TokenParameter extends Token
  {
    @SuppressWarnings("synthetic-access") public TokenParameter(
      final @Nonnull File file,
      final @Nonnull Position position)
      throws ConstraintError
    {
      super(Type.TOKEN_PARAMETER, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenParameter []");
      return builder.toString();
    }
  }

  public static final class TokenProgram extends Token
  {
    @SuppressWarnings("synthetic-access") public TokenProgram(
      final @Nonnull File file,
      final @Nonnull Position position)
      throws ConstraintError
    {
      super(Type.TOKEN_PROGRAM, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenProgram []");
      return builder.toString();
    }
  }

  public static final class TokenRecord extends Token
  {
    @SuppressWarnings("synthetic-access") public TokenRecord(
      final @Nonnull File file,
      final @Nonnull Position position)
      throws ConstraintError
    {
      super(Type.TOKEN_RECORD, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenRecord []");
      return builder.toString();
    }
  }

  public static final class TokenRoundLeft extends Token
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

  public static final class TokenRoundRight extends Token
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

  public static final class TokenSemicolon extends Token
  {
    @SuppressWarnings("synthetic-access") public TokenSemicolon(
      final @Nonnull File file,
      final @Nonnull Position position)
      throws ConstraintError
    {
      super(Type.TOKEN_SEMICOLON, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenSemicolon []");
      return builder.toString();
    }
  }

  public static final class TokenShader extends Token
  {
    @SuppressWarnings("synthetic-access") public TokenShader(
      final @Nonnull File file,
      final @Nonnull Position position)
      throws ConstraintError
    {
      super(Type.TOKEN_SHADER, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenShader []");
      return builder.toString();
    }
  }

  public static final class TokenSquareLeft extends Token
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

  public static final class TokenSquareRight extends Token
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

  public static final class TokenThen extends Token
  {
    @SuppressWarnings("synthetic-access") public TokenThen(
      final @Nonnull File file,
      final @Nonnull Position position)
      throws ConstraintError
    {
      super(Type.TOKEN_THEN, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenThen []");
      return builder.toString();
    }
  }

  public static final class TokenType extends Token
  {
    @SuppressWarnings("synthetic-access") public TokenType(
      final @Nonnull File file,
      final @Nonnull Position position)
      throws ConstraintError
    {
      super(Type.TOKEN_TYPE, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenType []");
      return builder.toString();
    }
  }

  public static final class TokenValue extends Token
  {
    @SuppressWarnings("synthetic-access") public TokenValue(
      final @Nonnull File file,
      final @Nonnull Position position)
      throws ConstraintError
    {
      super(Type.TOKEN_VALUE, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenValue []");
      return builder.toString();
    }
  }

  public static final class TokenVertex extends Token
  {
    @SuppressWarnings("synthetic-access") public TokenVertex(
      final @Nonnull File file,
      final @Nonnull Position position)
      throws ConstraintError
    {
      super(Type.TOKEN_VERTEX, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenVertex []");
      return builder.toString();
    }
  }

  public static final class TokenWith extends Token
  {
    @SuppressWarnings("synthetic-access") public TokenWith(
      final @Nonnull File file,
      final @Nonnull Position position)
      throws ConstraintError
    {
      super(Type.TOKEN_WITH, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenWith []");
      return builder.toString();
    }
  }

  public static enum Type
  {
    TOKEN_AS("keyword 'as'"),
    TOKEN_COLON("colon"),
    TOKEN_COMMA("comma"),
    TOKEN_CURLY_LEFT("'{'"),
    TOKEN_CURLY_RIGHT("'}'"),
    TOKEN_DISCARD("keyword 'discard'"),
    TOKEN_DOT("'.'"),
    TOKEN_ELSE("keyword 'else'"),
    TOKEN_END("keyword 'end'"),
    TOKEN_EOF("EOF"),
    TOKEN_EQUALS("'='"),
    TOKEN_EXTERNAL("keyword 'external'"),
    TOKEN_FRAGMENT("keyword 'fragment'"),
    TOKEN_FUNCTION("keyword 'function'"),
    TOKEN_IDENTIFIER_LOWER("lowercase identifier"),
    TOKEN_IDENTIFIER_UPPER("uppercase identifier"),
    TOKEN_IF("keyword 'if'"),
    TOKEN_IMPORT("keyword 'import'"),
    TOKEN_IN("keyword 'in'"),
    TOKEN_IS("keyword 'is'"),
    TOKEN_LET("keyword 'let'"),
    TOKEN_LITERAL_BOOLEAN("boolean literal ('true' | 'false')"),
    TOKEN_LITERAL_INTEGER_DECIMAL("integer literal (decimal)"),
    TOKEN_LITERAL_REAL("real literal (decimal)"),
    TOKEN_MODULE("keyword 'module'"),
    TOKEN_NEW("keyword 'new'"),
    TOKEN_OUT("keyword 'out'"),
    TOKEN_PACKAGE("keyword 'package'"),
    TOKEN_PARAMETER("keyword 'parameter'"),
    TOKEN_PROGRAM("keyword 'program'"),
    TOKEN_RECORD("keyword 'record'"),
    TOKEN_ROUND_LEFT("'('"),
    TOKEN_ROUND_RIGHT("')'"),
    TOKEN_SEMICOLON("';'"),
    TOKEN_SHADER("keyword 'shader'"),
    TOKEN_SQUARE_LEFT("'['"),
    TOKEN_SQUARE_RIGHT("']'"),
    TOKEN_THEN("keyword 'then'"),
    TOKEN_TYPE("keyword 'type'"),
    TOKEN_VALUE("keyword 'value'"),
    TOKEN_VERTEX("keyword 'vertex'"),
    TOKEN_WITH("keyword 'with'"),

    ;

    private final @Nonnull String description;

    private Type(
      final @Nonnull String description)
    {
      this.description = description;
    }

    public @Nonnull String getDescription()
    {
      return this.description;
    }
  }

  private final @Nonnull File     file;
  private final @Nonnull Position position;
  private final @Nonnull Type     type;

  private Token(
    final @Nonnull Type type,
    final @Nonnull File file,
    final @Nonnull Position position)
    throws ConstraintError
  {
    this.file = Constraints.constrainNotNull(file, "File");
    this.position = Constraints.constrainNotNull(position, "Position");
    this.type = Constraints.constrainNotNull(type, "Type");
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
    final Token other = (Token) obj;
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
