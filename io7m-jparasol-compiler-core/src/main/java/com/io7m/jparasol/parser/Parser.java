/*
 * Copyright © 2013 <code@io7m.com> http://io7m.com
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

package com.io7m.jparasol.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jaux.functional.Option;
import com.io7m.jaux.functional.Option.None;
import com.io7m.jparasol.lexer.Lexer;
import com.io7m.jparasol.lexer.LexerError;
import com.io7m.jparasol.lexer.Token;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.lexer.Token.TokenIdentifierUpper;
import com.io7m.jparasol.lexer.Token.TokenLet;
import com.io7m.jparasol.lexer.Token.TokenLiteralBoolean;
import com.io7m.jparasol.lexer.Token.TokenLiteralInteger;
import com.io7m.jparasol.lexer.Token.TokenLiteralReal;
import com.io7m.jparasol.lexer.Token.Type;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDFunction;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDFunctionArgument;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDFunctionDefined;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDFunctionExternal;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDValue;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDValueLocal;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpression;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpression.UASTIEApplication;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpression.UASTIEBoolean;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpression.UASTIEConditional;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpression.UASTIEInteger;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpression.UASTIELet;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpression.UASTIENew;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpression.UASTIEReal;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpression.UASTIERecord;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpression.UASTIERecordProjection;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpression.UASTIESwizzle;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpression.UASTIEVariable;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpression.UASTIRecordFieldAssignment;
import com.io7m.jparasol.untyped.ast.initial.UASTIStatusUnchecked;
import com.io7m.jparasol.untyped.ast.initial.UASTITypePath;
import com.io7m.jparasol.untyped.ast.initial.UASTIValuePath;

public final class Parser
{
  public static @Nonnull Parser newInternalParser(
    final @Nonnull Lexer lexer)
    throws ConstraintError,
      IOException,
      LexerError
  {
    return new Parser(true, lexer);
  }

  public static @Nonnull Parser newParser(
    final @Nonnull Lexer lexer)
    throws ConstraintError,
      IOException,
      LexerError
  {
    return new Parser(false, lexer);
  }

  private final boolean                internal;
  private final @Nonnull Lexer         lexer;
  private final @Nonnull StringBuilder message;
  private @Nonnull Token               token;

  private Parser(
    final boolean internal,
    final @Nonnull Lexer lexer)
    throws ConstraintError,
      IOException,
      LexerError
  {
    this.internal = internal;
    this.lexer = Constraints.constrainNotNull(lexer, "Lexer");
    this.message = new StringBuilder();
    this.token = lexer.token();
  }

  public @Nonnull UASTIDFunction<UASTIStatusUnchecked> declarationFunction()
    throws ParserError,
      IOException,
      LexerError,
      ConstraintError
  {
    this.parserConsumeExact(Type.TOKEN_FUNCTION);
    this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
    final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
    this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);

    final List<UASTIDFunctionArgument<UASTIStatusUnchecked>> args =
      this.declarationFunctionArguments();

    this.parserConsumeExact(Type.TOKEN_COLON);
    final UASTITypePath type = this.declarationTypePath();
    this.parserConsumeExact(Type.TOKEN_EQUALS);

    switch (this.token.getType()) {
      case TOKEN_EXTERNAL:

        /**
         * If parsing an "internal" unit, then allow "external" functions.
         */

        if (this.internal) {
          this.parserConsumeExact(Type.TOKEN_EXTERNAL);
          this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
          final TokenIdentifierLower ext = (TokenIdentifierLower) this.token;
          this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
          return new UASTIDFunctionExternal<UASTIStatusUnchecked>(
            name,
            args,
            type,
            ext);
        }

        /**
         * Otherwise, attempt to parse "external" as an expression, which will
         * result in an error.
         */

        return new UASTIDFunctionDefined<UASTIStatusUnchecked>(
          name,
          args,
          type,
          this.expression());

        // $CASES-OMITTED$
      default:
        return new UASTIDFunctionDefined<UASTIStatusUnchecked>(
          name,
          args,
          type,
          this.expression());
    }
  }

  private @Nonnull
    UASTIDFunctionArgument<UASTIStatusUnchecked>
    declarationFunctionArgument()
      throws ParserError,
        ConstraintError,
        IOException,
        LexerError
  {
    this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
    final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
    this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
    this.parserConsumeExact(Type.TOKEN_COLON);
    return new UASTIDFunctionArgument<UASTIStatusUnchecked>(
      name,
      this.declarationTypePath());
  }

  private @Nonnull
    List<UASTIDFunctionArgument<UASTIStatusUnchecked>>
    declarationFunctionArguments()
      throws ParserError,
        IOException,
        LexerError,
        ConstraintError
  {
    this.parserConsumeExact(Type.TOKEN_ROUND_LEFT);

    final ArrayList<UASTIDFunctionArgument<UASTIStatusUnchecked>> args =
      new ArrayList<UASTIDFunctionArgument<UASTIStatusUnchecked>>();
    args.add(this.declarationFunctionArgument());

    boolean done = false;
    while (done == false) {
      switch (this.token.getType()) {
        case TOKEN_COMMA:
          this.parserConsumeExact(Type.TOKEN_COMMA);
          args.add(this.declarationFunctionArgument());
          break;
        // $CASES-OMITTED$
        default:
          done = true;
      }
    }

    this.parserConsumeExact(Type.TOKEN_ROUND_RIGHT);
    return args;
  }

  public @Nonnull UASTITypePath declarationTypePath()
    throws ParserError,
      IOException,
      LexerError,
      ConstraintError
  {
    this.parserExpectOneOf(new Type[] {
      Type.TOKEN_IDENTIFIER_LOWER,
      Type.TOKEN_IDENTIFIER_UPPER });

    switch (this.token.getType()) {
      case TOKEN_IDENTIFIER_LOWER:
      {
        final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
        this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
        final None<TokenIdentifierUpper> none = Option.none();
        return new UASTITypePath(none, name);
      }
      case TOKEN_IDENTIFIER_UPPER:
      {
        final TokenIdentifierUpper module = (TokenIdentifierUpper) this.token;
        this.parserConsumeExact(Type.TOKEN_IDENTIFIER_UPPER);
        this.parserConsumeExact(Type.TOKEN_DOT);
        this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
        final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
        this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
        return new UASTITypePath(Option.some(module), name);
      }

      // $CASES-OMITTED$
      default:
        throw new UnreachableCodeException();
    }
  }

  public @Nonnull UASTIDValue<UASTIStatusUnchecked> declarationValue()
    throws ParserError,
      IOException,
      LexerError,
      ConstraintError
  {
    this.parserConsumeExact(Type.TOKEN_VALUE);
    this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
    final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
    this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
    this
      .parserExpectOneOf(new Type[] { Type.TOKEN_COLON, Type.TOKEN_EQUALS });

    switch (this.token.getType()) {
      case TOKEN_COLON:
      {
        this.parserConsumeExact(Type.TOKEN_COLON);
        final UASTITypePath path = this.declarationTypePath();
        this.parserConsumeExact(Type.TOKEN_EQUALS);
        return new UASTIDValue<UASTIStatusUnchecked>(
          name,
          Option.some(path),
          this.expression());
      }
      case TOKEN_EQUALS:
      {
        this.parserConsumeExact(Type.TOKEN_EQUALS);
        final Option<UASTITypePath> none = Option.none();
        return new UASTIDValue<UASTIStatusUnchecked>(
          name,
          none,
          this.expression());
      }
      // $CASES-OMITTED$
      default:
        throw new UnreachableCodeException();
    }
  }

  private @Nonnull
    UASTIDValueLocal<UASTIStatusUnchecked>
    declarationValueLocal()
      throws ParserError,
        IOException,
        LexerError,
        ConstraintError
  {
    this.parserConsumeExact(Type.TOKEN_VALUE);
    this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
    final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
    this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
    this
      .parserExpectOneOf(new Type[] { Type.TOKEN_COLON, Type.TOKEN_EQUALS });

    switch (this.token.getType()) {
      case TOKEN_COLON:
      {
        this.parserConsumeExact(Type.TOKEN_COLON);
        final UASTITypePath path = this.declarationTypePath();
        this.parserConsumeExact(Type.TOKEN_EQUALS);
        return new UASTIDValueLocal<UASTIStatusUnchecked>(
          name,
          Option.some(path),
          this.expression());
      }
      case TOKEN_EQUALS:
      {
        this.parserConsumeExact(Type.TOKEN_EQUALS);
        final Option<UASTITypePath> none = Option.none();
        return new UASTIDValueLocal<UASTIStatusUnchecked>(
          name,
          none,
          this.expression());
      }
      // $CASES-OMITTED$
      default:
        throw new UnreachableCodeException();
    }
  }

  public @Nonnull
    List<UASTIDValueLocal<UASTIStatusUnchecked>>
    declarationValueLocals()
      throws ParserError,
        IOException,
        LexerError,
        ConstraintError
  {
    final List<UASTIDValueLocal<UASTIStatusUnchecked>> values =
      new ArrayList<UASTIDValueLocal<UASTIStatusUnchecked>>();
    values.add(this.declarationValueLocal());
    this.parserConsumeExact(Type.TOKEN_SEMICOLON);

    boolean done = false;
    while (done == false) {
      switch (this.token.getType()) {
        case TOKEN_VALUE:
          values.add(this.declarationValueLocal());
          this.parserConsumeExact(Type.TOKEN_SEMICOLON);
          break;
        // $CASES-OMITTED$
        default:
          done = true;
      }
    }

    return values;
  }

  public @Nonnull UASTIValuePath declarationValuePath()
    throws ParserError,
      IOException,
      LexerError,
      ConstraintError
  {
    this.parserExpectOneOf(new Type[] {
      Type.TOKEN_IDENTIFIER_LOWER,
      Type.TOKEN_IDENTIFIER_UPPER });

    switch (this.token.getType()) {
      case TOKEN_IDENTIFIER_LOWER:
      {
        final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
        this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
        final None<TokenIdentifierUpper> none = Option.none();
        return new UASTIValuePath(none, name);
      }
      case TOKEN_IDENTIFIER_UPPER:
      {
        final TokenIdentifierUpper module = (TokenIdentifierUpper) this.token;
        this.parserConsumeExact(Type.TOKEN_IDENTIFIER_UPPER);
        this.parserConsumeExact(Type.TOKEN_DOT);
        this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
        final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
        this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
        return new UASTIValuePath(Option.some(module), name);
      }

      // $CASES-OMITTED$
      default:
        throw new UnreachableCodeException();
    }
  }

  public @Nonnull UASTIExpression<UASTIStatusUnchecked> expression()
    throws ParserError,
      IOException,
      LexerError,
      ConstraintError
  {
    return this.expressionPost(this.expressionPre());
  }

  public @Nonnull
    List<UASTIExpression<UASTIStatusUnchecked>>
    expressionApplicationArguments()
      throws ParserError,
        IOException,
        LexerError,
        ConstraintError
  {
    this.parserConsumeExact(Type.TOKEN_ROUND_LEFT);
    final ArrayList<UASTIExpression<UASTIStatusUnchecked>> arguments =
      new ArrayList<UASTIExpression<UASTIStatusUnchecked>>();
    arguments.add(this.expression());

    boolean done = false;
    while (done == false) {
      switch (this.token.getType()) {
        case TOKEN_COMMA:
          this.parserConsumeExact(Type.TOKEN_COMMA);
          arguments.add(this.expression());
          break;
        // $CASES-OMITTED$
        default:
          done = true;
          break;
      }
    }

    this.parserConsumeExact(Type.TOKEN_ROUND_RIGHT);
    return arguments;
  }

  public @Nonnull UASTIEBoolean<UASTIStatusUnchecked> expressionBoolean()
    throws ConstraintError,
      ParserError,
      IOException,
      LexerError
  {
    this.parserExpectExact(Type.TOKEN_LITERAL_BOOLEAN);
    final UASTIEBoolean<UASTIStatusUnchecked> t =
      new UASTIEBoolean<UASTIStatusUnchecked>(
        (TokenLiteralBoolean) this.token);
    this.parserConsumeAny();
    return t;
  }

  public @Nonnull
    UASTIEConditional<UASTIStatusUnchecked>
    expressionConditional()
      throws ParserError,
        ConstraintError,
        IOException,
        LexerError
  {
    this.parserConsumeExact(Type.TOKEN_IF);
    final UASTIExpression<UASTIStatusUnchecked> econd = this.expression();
    this.parserConsumeExact(Type.TOKEN_THEN);
    final UASTIExpression<UASTIStatusUnchecked> eleft = this.expression();
    this.parserConsumeExact(Type.TOKEN_ELSE);
    final UASTIExpression<UASTIStatusUnchecked> eright = this.expression();
    this.parserConsumeExact(Type.TOKEN_END);
    return new UASTIEConditional<UASTIStatusUnchecked>(econd, eleft, eright);
  }

  public @Nonnull UASTIEInteger<UASTIStatusUnchecked> expressionInteger()
    throws ParserError,
      ConstraintError,
      IOException,
      LexerError
  {
    this.parserExpectExact(Type.TOKEN_LITERAL_INTEGER_DECIMAL);
    final UASTIEInteger<UASTIStatusUnchecked> t =
      new UASTIEInteger<UASTIStatusUnchecked>(
        (TokenLiteralInteger) this.token);
    this.parserConsumeAny();
    return t;
  }

  public @Nonnull UASTIELet<UASTIStatusUnchecked> expressionLet()
    throws ParserError,
      ConstraintError,
      IOException,
      LexerError
  {
    this.parserExpectExact(Type.TOKEN_LET);
    final TokenLet let = (TokenLet) this.token;
    this.parserConsumeExact(Type.TOKEN_LET);
    final List<UASTIDValueLocal<UASTIStatusUnchecked>> bindings =
      this.declarationValueLocals();
    this.parserConsumeExact(Type.TOKEN_IN);
    final UASTIExpression<UASTIStatusUnchecked> body = this.expression();
    this.parserConsumeExact(Type.TOKEN_END);
    return new UASTIELet<UASTIStatusUnchecked>(let, bindings, body);
  }

  public @Nonnull UASTIENew<UASTIStatusUnchecked> expressionNew()
    throws ParserError,
      IOException,
      LexerError,
      ConstraintError
  {
    this.parserConsumeExact(Type.TOKEN_NEW);
    final UASTITypePath path = this.declarationTypePath();
    return new UASTIENew<UASTIStatusUnchecked>(
      path,
      this.expressionApplicationArguments());
  }

  private @Nonnull UASTIExpression<UASTIStatusUnchecked> expressionPost(
    final @Nonnull UASTIExpression<UASTIStatusUnchecked> e)
    throws ParserError,
      IOException,
      LexerError,
      ConstraintError
  {
    switch (this.token.getType()) {
      case TOKEN_DOT:
        return this.expressionPost(this.expressionRecordProjection(e));
      case TOKEN_SQUARE_LEFT:
        return this.expressionPost(this.expressionSwizzle(e));
        // $CASES-OMITTED$
      default:
        return e;
    }
  }

  private @Nonnull UASTIExpression<UASTIStatusUnchecked> expressionPre()
    throws ParserError,
      ConstraintError,
      IOException,
      LexerError
  {
    this.parserExpectOneOf(new Type[] {
      Type.TOKEN_LITERAL_INTEGER_DECIMAL,
      Type.TOKEN_LITERAL_BOOLEAN,
      Type.TOKEN_LITERAL_REAL,
      Type.TOKEN_IDENTIFIER_LOWER,
      Type.TOKEN_IDENTIFIER_UPPER,
      Type.TOKEN_IF,
      Type.TOKEN_LET,
      Type.TOKEN_NEW,
      Type.TOKEN_RECORD });

    switch (this.token.getType()) {
      case TOKEN_LITERAL_INTEGER_DECIMAL:
        return this.expressionInteger();
      case TOKEN_LITERAL_REAL:
        return this.expressionReal();
      case TOKEN_LITERAL_BOOLEAN:
        return this.expressionBoolean();
      case TOKEN_IDENTIFIER_LOWER:
        return this.expressionVariableOrApplication();
      case TOKEN_IDENTIFIER_UPPER:
        return this.expressionVariableOrApplication();
      case TOKEN_IF:
        return this.expressionConditional();
      case TOKEN_LET:
        return this.expressionLet();
      case TOKEN_NEW:
        return this.expressionNew();
      case TOKEN_RECORD:
        return this.expressionRecord();
        // $CASES-OMITTED$
      default:
        throw new UnreachableCodeException();
    }
  }

  public @Nonnull UASTIEReal<UASTIStatusUnchecked> expressionReal()
    throws ParserError,
      ConstraintError,
      IOException,
      LexerError
  {
    this.parserExpectExact(Type.TOKEN_LITERAL_REAL);
    final UASTIEReal<UASTIStatusUnchecked> t =
      new UASTIEReal<UASTIStatusUnchecked>((TokenLiteralReal) this.token);
    this.parserConsumeAny();
    return t;
  }

  public @Nonnull UASTIERecord<UASTIStatusUnchecked> expressionRecord()
    throws ParserError,
      IOException,
      LexerError,
      ConstraintError
  {
    this.parserConsumeExact(Type.TOKEN_RECORD);
    final UASTITypePath path = this.declarationTypePath();
    this.parserConsumeExact(Type.TOKEN_CURLY_LEFT);
    final List<UASTIRecordFieldAssignment<UASTIStatusUnchecked>> fields =
      new ArrayList<UASTIRecordFieldAssignment<UASTIStatusUnchecked>>();
    fields.add(this.expressionRecordFieldAssignment());
    this.expressionRecordActual(fields);
    this.parserConsumeExact(Type.TOKEN_CURLY_RIGHT);
    return new UASTIERecord<UASTIStatusUnchecked>(path, fields);
  }

  private
    void
    expressionRecordActual(
      final @Nonnull List<UASTIRecordFieldAssignment<UASTIStatusUnchecked>> fields)
      throws ParserError,
        IOException,
        LexerError,
        ConstraintError
  {
    switch (this.token.getType()) {
      case TOKEN_COMMA:
        this.parserConsumeExact(Type.TOKEN_COMMA);
        fields.add(this.expressionRecordFieldAssignment());
        this.expressionRecordActual(fields);
        break;
      // $CASES-OMITTED$
      default:
        return;
    }
  }

  private @Nonnull
    UASTIRecordFieldAssignment<UASTIStatusUnchecked>
    expressionRecordFieldAssignment()
      throws ConstraintError,
        ParserError,
        IOException,
        LexerError
  {
    this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
    final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
    this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
    this.parserConsumeExact(Type.TOKEN_EQUALS);
    return new UASTIRecordFieldAssignment<UASTIStatusUnchecked>(
      name,
      this.expression());
  }

  private @Nonnull
    UASTIERecordProjection<UASTIStatusUnchecked>
    expressionRecordProjection(
      final @Nonnull UASTIExpression<UASTIStatusUnchecked> e)
      throws ConstraintError,
        ParserError,
        IOException,
        LexerError
  {
    this.parserConsumeExact(Type.TOKEN_DOT);
    this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
    final UASTIERecordProjection<UASTIStatusUnchecked> r =
      new UASTIERecordProjection<UASTIStatusUnchecked>(
        e,
        (TokenIdentifierLower) this.token);
    this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
    return r;
  }

  private UASTIESwizzle<UASTIStatusUnchecked> expressionSwizzle(
    final @Nonnull UASTIExpression<UASTIStatusUnchecked> e)
    throws ParserError,
      IOException,
      LexerError,
      ConstraintError
  {
    this.parserConsumeExact(Type.TOKEN_SQUARE_LEFT);

    final ArrayList<TokenIdentifierLower> fields =
      new ArrayList<TokenIdentifierLower>();
    fields.add(this.expressionSwizzleField());

    boolean done = false;
    while (done == false) {
      switch (this.token.getType()) {
        case TOKEN_IDENTIFIER_LOWER:
          fields.add(this.expressionSwizzleField());
          break;
        // $CASES-OMITTED$
        default:
          done = true;
      }
    }

    this.parserConsumeExact(Type.TOKEN_SQUARE_RIGHT);
    return new UASTIESwizzle<UASTIStatusUnchecked>(e, fields);
  }

  private @Nonnull TokenIdentifierLower expressionSwizzleField()
    throws ParserError,
      ConstraintError,
      IOException,
      LexerError
  {
    this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
    final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
    this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
    return name;
  }

  public @Nonnull
    UASTIExpression<UASTIStatusUnchecked>
    expressionVariableOrApplication()
      throws ParserError,
        IOException,
        LexerError,
        ConstraintError
  {
    final UASTIValuePath path = this.declarationValuePath();
    switch (this.token.getType()) {
      case TOKEN_ROUND_LEFT:
        return new UASTIEApplication<UASTIStatusUnchecked>(
          path,
          this.expressionApplicationArguments());
        // $CASES-OMITTED$
      default:
        return new UASTIEVariable<UASTIStatusUnchecked>(path);
    }
  }

  private void parserConsumeAny()
    throws IOException,
      LexerError,
      ConstraintError
  {
    this.token = this.lexer.token();
  }

  private void parserConsumeExact(
    final @Nonnull Token.Type type)
    throws ParserError,
      ConstraintError,
      IOException,
      LexerError
  {
    this.parserExpectExact(type);
    this.parserConsumeAny();
  }

  private void parserExpectExact(
    final @Nonnull Token.Type type)
    throws ParserError,
      ConstraintError
  {
    if (this.token.getType() != type) {
      this.message.setLength(0);
      this.message.append("Expected ");
      this.message.append(type.getDescription());
      this.message.append(" but got ");
      this.parserShowToken();
      throw new ParserError(
        this.message.toString(),
        this.lexer.getFile(),
        this.token.getPosition());
    }
  }

  private void parserExpectOneOf(
    final @Nonnull Token.Type types[])
    throws ParserError,
      ConstraintError
  {
    for (int index = 0; index < types.length; ++index) {
      final Type want = types[index];
      if (this.token.getType() == want) {
        return;
      }
    }

    this.message.setLength(0);
    this.message.append("Expected one of {");
    for (int index = 0; index < types.length; ++index) {
      final Type t = types[index];
      this.message.append(t);
      if ((index + 1) != types.length) {
        this.message.append(", ");
      }
    }
    this.message.append("} but got ");
    this.parserShowToken();
    throw new ParserError(
      this.message.toString(),
      this.lexer.getFile(),
      this.token.getPosition());
  }

  private void parserShowToken()
  {
    this.message.append(this.token.getType().getDescription());
    switch (this.token.getType()) {
      case TOKEN_IDENTIFIER_LOWER:
      {
        final TokenIdentifierLower t =
          (Token.TokenIdentifierLower) this.token;
        this.message.append("('");
        this.message.append(t.getActual());
        this.message.append("')");
        break;
      }
      case TOKEN_IDENTIFIER_UPPER:
      {
        final TokenIdentifierUpper t =
          (Token.TokenIdentifierUpper) this.token;
        this.message.append("('");
        this.message.append(t.getActual());
        this.message.append("')");
        break;
      }
      // $CASES-OMITTED$
      default:
        break;
    }
  }
}
