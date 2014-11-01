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

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jnull.NullCheck;

/**
 * The type of tokens.
 */

// CHECKSTYLE_JAVADOC:OFF

@SuppressWarnings("synthetic-access") @EqualityReference public abstract class Token
{
  @EqualityReference public static final class TokenAs extends Token
  {
    public TokenAs(
      final File file,
      final Position position)

    {
      super(Type.TOKEN_AS, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenAs []");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TokenColumn extends Token
  {
    public TokenColumn(
      final File file,
      final Position position)

    {
      super(Type.TOKEN_COLUMN, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenColumn []");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TokenColon extends Token
  {
    public TokenColon(
      final File file,
      final Position position)
    {
      super(Type.TOKEN_COLON, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenColon []");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TokenComma extends Token
  {
    public TokenComma(
      final File file,
      final Position position)

    {
      super(Type.TOKEN_COMMA, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenComma []");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TokenCurlyLeft extends Token
  {
    public TokenCurlyLeft(
      final File file,
      final Position position)

    {
      super(Type.TOKEN_CURLY_LEFT, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenCurlyLeft []");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TokenCurlyRight extends Token
  {
    public TokenCurlyRight(
      final File file,
      final Position position)

    {
      super(Type.TOKEN_CURLY_RIGHT, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenCurlyRight []");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TokenDepth extends Token
  {
    public TokenDepth(
      final File file,
      final Position position)

    {
      super(Type.TOKEN_DEPTH, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenDepth []");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TokenDiscard extends Token
  {
    public TokenDiscard(
      final File file,
      final Position position)

    {
      super(Type.TOKEN_DISCARD, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenDiscard []");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TokenDot extends Token
  {
    public TokenDot(
      final File file,
      final Position position)

    {
      super(Type.TOKEN_DOT, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenDot []");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TokenElse extends Token
  {
    public TokenElse(
      final File file,
      final Position position)

    {
      super(Type.TOKEN_ELSE, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenElse []");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TokenEnd extends Token
  {
    public TokenEnd(
      final File file,
      final Position position)

    {
      super(Type.TOKEN_END, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenEnd []");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TokenEOF extends Token
  {
    public TokenEOF(
      final File file,
      final Position position)

    {
      super(Type.TOKEN_EOF, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenEOF []");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TokenEquals extends Token
  {
    public TokenEquals(
      final File file,
      final Position position)

    {
      super(Type.TOKEN_EQUALS, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenEquals []");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TokenExternal extends Token
  {
    public TokenExternal(
      final File file,
      final Position position)

    {
      super(Type.TOKEN_EXTERNAL, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenExternal []");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TokenFragment extends Token
  {
    public TokenFragment(
      final File file,
      final Position position)

    {
      super(Type.TOKEN_FRAGMENT, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenFragment []");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TokenFunction extends Token
  {
    public TokenFunction(
      final File file,
      final Position position)

    {
      super(Type.TOKEN_FUNCTION, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenFunction []");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static abstract class TokenIdentifier extends
    Token
  {
    protected TokenIdentifier(
      final Type type,
      final File file,
      final Position position)

    {
      super(type, file, position);
    }

    public abstract String getActual();
  }

  @EqualityReference public static final class TokenIdentifierLower extends
    TokenIdentifier
  {
    private final String text;

    public TokenIdentifierLower(
      final File file,
      final Position position,
      final String in_text)

    {
      super(Type.TOKEN_IDENTIFIER_LOWER, file, position);
      this.text = NullCheck.notNull(in_text, "Text");
    }

    @Override public String getActual()
    {
      return this.text;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenIdentifierLower [text=");
      builder.append(this.text);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TokenIdentifierUpper extends
    TokenIdentifier
  {
    private final String text;

    public TokenIdentifierUpper(
      final File file,
      final Position position,
      final String in_text)

    {
      super(Type.TOKEN_IDENTIFIER_UPPER, file, position);
      this.text = NullCheck.notNull(in_text, "Text");
    }

    @Override public String getActual()
    {
      return this.text;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenIdentifierUpper [text=");
      builder.append(this.text);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TokenIf extends Token
  {
    public TokenIf(
      final File file,
      final Position position)

    {
      super(Type.TOKEN_IF, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenIf []");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TokenImport extends Token
  {
    public TokenImport(
      final File file,
      final Position position)

    {
      super(Type.TOKEN_IMPORT, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenImport []");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TokenIn extends Token
  {
    public TokenIn(
      final File file,
      final Position position)

    {
      super(Type.TOKEN_IN, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenIn []");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TokenIs extends Token
  {
    public TokenIs(
      final File file,
      final Position position)

    {
      super(Type.TOKEN_IS, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenIs []");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TokenLet extends Token
  {
    public TokenLet(
      final File file,
      final Position position)

    {
      super(Type.TOKEN_LET, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenLet []");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TokenLiteralBoolean extends
    Token
  {
    private final boolean value;

    public TokenLiteralBoolean(
      final File file,
      final Position position,
      final boolean in_value)

    {
      super(Type.TOKEN_LITERAL_BOOLEAN, file, position);
      this.value = in_value;
    }

    public boolean getValue()
    {
      return this.value;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenLiteralBoolean [value=");
      builder.append(this.value);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static abstract class TokenLiteralInteger extends
    Token
  {
    protected TokenLiteralInteger(
      final Type in_type,
      final File in_file,
      final Position in_position)
    {
      super(in_type, in_file, in_position);
    }

    public abstract BigInteger getValue();
  }

  @EqualityReference public static final class TokenLiteralIntegerDecimal extends
    TokenLiteralInteger
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

    private TokenLiteralIntegerDecimal(
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
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TokenLiteralReal extends Token
  {
    public static TokenLiteralReal newReal(
      final File file,
      final Position position,
      final String text)
    {
      NullCheck.notNull(text, "Text");
      return new TokenLiteralReal(file, position, new BigDecimal(text));
    }

    private final BigDecimal value;

    private TokenLiteralReal(
      final File file,
      final Position position,
      final BigDecimal in_value)
    {
      super(Type.TOKEN_LITERAL_REAL, file, position);
      this.value = NullCheck.notNull(in_value, "Value");
    }

    public BigDecimal getValue()
    {
      return this.value;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenLiteralReal [value=");
      builder.append(this.value);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TokenModule extends Token
  {
    public TokenModule(
      final File file,
      final Position position)
    {
      super(Type.TOKEN_MODULE, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenModule []");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TokenNew extends Token
  {
    public TokenNew(
      final File file,
      final Position position)
    {
      super(Type.TOKEN_NEW, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenNew []");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TokenOut extends Token
  {
    public TokenOut(
      final File file,
      final Position position)
    {
      super(Type.TOKEN_OUT, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenOut []");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TokenPackage extends Token
  {
    public TokenPackage(
      final File file,
      final Position position)
    {
      super(Type.TOKEN_PACKAGE, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenPackage []");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TokenParameter extends Token
  {
    public TokenParameter(
      final File file,
      final Position position)
    {
      super(Type.TOKEN_PARAMETER, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenParameter []");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TokenProgram extends Token
  {
    public TokenProgram(
      final File file,
      final Position position)
    {
      super(Type.TOKEN_PROGRAM, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenProgram []");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TokenRecord extends Token
  {
    public TokenRecord(
      final File file,
      final Position position)
    {
      super(Type.TOKEN_RECORD, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenRecord []");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TokenRestrict extends Token
  {
    public TokenRestrict(
      final File file,
      final Position position)
    {
      super(Type.TOKEN_RESTRICT, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenRestrict []");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TokenRoundLeft extends Token
  {
    public TokenRoundLeft(
      final File file,
      final Position position)
    {
      super(Type.TOKEN_ROUND_LEFT, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenRoundLeft []");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TokenRoundRight extends Token
  {
    public TokenRoundRight(
      final File file,
      final Position position)
    {
      super(Type.TOKEN_ROUND_RIGHT, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenRoundRight []");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TokenSemicolon extends Token
  {
    public TokenSemicolon(
      final File file,
      final Position position)
    {
      super(Type.TOKEN_SEMICOLON, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenSemicolon []");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TokenShader extends Token
  {
    public TokenShader(
      final File file,
      final Position position)
    {
      super(Type.TOKEN_SHADER, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenShader []");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TokenSquareLeft extends Token
  {
    public TokenSquareLeft(
      final File file,
      final Position position)
    {
      super(Type.TOKEN_SQUARE_LEFT, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenSquareLeft []");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TokenSquareRight extends Token
  {
    public TokenSquareRight(
      final File file,
      final Position position)
    {
      super(Type.TOKEN_SQUARE_RIGHT, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenSquareRight []");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TokenThen extends Token
  {
    public TokenThen(
      final File file,
      final Position position)
    {
      super(Type.TOKEN_THEN, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenThen []");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TokenType extends Token
  {
    public TokenType(
      final File file,
      final Position position)
    {
      super(Type.TOKEN_TYPE, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenType []");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TokenValue extends Token
  {
    public TokenValue(
      final File file,
      final Position position)
    {
      super(Type.TOKEN_VALUE, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenValue []");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TokenVertex extends Token
  {
    public TokenVertex(
      final File file,
      final Position position)
    {
      super(Type.TOKEN_VERTEX, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenVertex []");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TokenWith extends Token
  {
    public TokenWith(
      final File file,
      final Position position)
    {
      super(Type.TOKEN_WITH, file, position);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("TokenWith []");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  public static enum Type
  {
    TOKEN_AS("keyword 'as'"),
    TOKEN_COLON("colon"),
    TOKEN_COLUMN("keyword 'column'"),
    TOKEN_COMMA("comma"),
    TOKEN_CURLY_LEFT("'{'"),
    TOKEN_CURLY_RIGHT("'}'"),
    TOKEN_DEPTH("keyword 'depth'"),
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
    TOKEN_RESTRICT("keyword 'restrict'");

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

  private Token(
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
