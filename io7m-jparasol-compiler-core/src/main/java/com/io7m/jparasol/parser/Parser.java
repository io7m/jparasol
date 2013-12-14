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
import com.io7m.jparasol.ModulePath;
import com.io7m.jparasol.PackagePath;
import com.io7m.jparasol.lexer.Lexer;
import com.io7m.jparasol.lexer.LexerError;
import com.io7m.jparasol.lexer.Token;
import com.io7m.jparasol.lexer.Token.TokenDiscard;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.lexer.Token.TokenIdentifierUpper;
import com.io7m.jparasol.lexer.Token.TokenLet;
import com.io7m.jparasol.lexer.Token.TokenLiteralBoolean;
import com.io7m.jparasol.lexer.Token.TokenLiteralInteger;
import com.io7m.jparasol.lexer.Token.TokenLiteralIntegerDecimal;
import com.io7m.jparasol.lexer.Token.TokenLiteralReal;
import com.io7m.jparasol.lexer.Token.Type;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDFunction;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDFunctionArgument;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDFunctionDefined;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDFunctionExternal;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDImport;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDPackage;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShader;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragment;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentInput;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentLocal;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentLocalDiscard;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentLocalValue;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentOutput;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentOutputAssignment;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentParameter;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentParameters;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderProgram;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertex;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertexInput;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertexOutput;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertexOutputAssignment;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertexParameter;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertexParameters;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDType;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDTypeRecord;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDTypeRecordField;
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
import com.io7m.jparasol.untyped.ast.initial.UASTIShaderPath;
import com.io7m.jparasol.untyped.ast.initial.UASTITypePath;
import com.io7m.jparasol.untyped.ast.initial.UASTIUnchecked;
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

  public @Nonnull
    UASTIDShaderFragment<UASTIUnchecked>
    declarationFragmentShader()
      throws ParserError,
        IOException,
        LexerError,
        ConstraintError
  {
    this.parserConsumeExact(Type.TOKEN_FRAGMENT);
    this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
    final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
    this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
    this.parserConsumeExact(Type.TOKEN_IS);

    final List<UASTIDShaderFragmentParameters<UASTIUnchecked>> decls =
      this.declarationFragmentShaderParameterDeclarations();

    final List<UASTIDShaderFragmentInput<UASTIUnchecked>> inputs =
      new ArrayList<UASTIDShaderFragmentInput<UASTIUnchecked>>();
    final List<UASTIDShaderFragmentOutput<UASTIUnchecked>> outputs =
      new ArrayList<UASTIDShaderFragmentOutput<UASTIUnchecked>>();
    final List<UASTIDShaderFragmentParameter<UASTIUnchecked>> parameters =
      new ArrayList<UASTIDShaderFragmentParameter<UASTIUnchecked>>();

    for (int index = 0; index < decls.size(); ++index) {
      final UASTIDShaderFragmentParameters<UASTIUnchecked> d =
        decls.get(index);
      if (d instanceof UASTIDShaderFragmentInput<?>) {
        inputs.add((UASTIDShaderFragmentInput<UASTIUnchecked>) d);
        continue;
      }
      if (d instanceof UASTIDShaderFragmentOutput<?>) {
        outputs.add((UASTIDShaderFragmentOutput<UASTIUnchecked>) d);
        continue;
      }
      if (d instanceof UASTIDShaderFragmentParameter<?>) {
        parameters.add((UASTIDShaderFragmentParameter<UASTIUnchecked>) d);
        continue;
      }
    }

    this.parserExpectOneOf(new Type[] { Type.TOKEN_WITH, Type.TOKEN_AS });

    final List<UASTIDShaderFragmentLocal<UASTIUnchecked>> values;
    switch (this.token.getType()) {
      case TOKEN_WITH:
      {
        this.parserConsumeExact(Type.TOKEN_WITH);
        values = this.declarationFragmentShaderLocals();
        break;
      }
      // $CASES-OMITTED$
      default:
      {
        values = new ArrayList<UASTIDShaderFragmentLocal<UASTIUnchecked>>();
        break;
      }
    }

    this.parserConsumeExact(Type.TOKEN_AS);
    final List<UASTIDShaderFragmentOutputAssignment<UASTIUnchecked>> assigns =
      this.declarationFragmentShaderOutputAssignments();
    this.parserConsumeExact(Type.TOKEN_END);

    return new UASTIDShaderFragment<UASTIUnchecked>(
      name,
      inputs,
      outputs,
      parameters,
      values,
      assigns);
  }

  public
    UASTIDShaderFragmentInput<UASTIUnchecked>
    declarationFragmentShaderInput()
      throws ParserError,
        IOException,
        LexerError,
        ConstraintError
  {
    this.parserConsumeExact(Type.TOKEN_IN);
    this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
    final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
    this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
    this.parserConsumeExact(Type.TOKEN_COLON);
    return new UASTIDShaderFragmentInput<UASTIUnchecked>(
      name,
      this.declarationTypePath());
  }

  public @Nonnull
    UASTIDShaderFragmentLocalDiscard<UASTIUnchecked>
    declarationFragmentShaderLocalDiscard()
      throws ParserError,
        ConstraintError,
        IOException,
        LexerError
  {
    this.parserExpectExact(Type.TOKEN_DISCARD);
    final TokenDiscard discard = (TokenDiscard) this.token;
    this.parserConsumeExact(Type.TOKEN_DISCARD);
    this.parserConsumeExact(Type.TOKEN_ROUND_LEFT);
    final UASTIExpression<UASTIUnchecked> expr = this.expression();
    this.parserConsumeExact(Type.TOKEN_ROUND_RIGHT);
    return new UASTIDShaderFragmentLocalDiscard<UASTIUnchecked>(discard, expr);
  }

  public
    List<UASTIDShaderFragmentLocal<UASTIUnchecked>>
    declarationFragmentShaderLocals()
      throws ParserError,
        IOException,
        LexerError,
        ConstraintError
  {
    final ArrayList<UASTIDShaderFragmentLocal<UASTIUnchecked>> locals =
      new ArrayList<UASTIDShaderFragmentLocal<UASTIUnchecked>>();

    for (;;) {
      switch (this.token.getType()) {
        case TOKEN_VALUE:
          locals.add(new UASTIDShaderFragmentLocalValue<UASTIUnchecked>(this
            .declarationValueLocal()));
          this.parserConsumeExact(Type.TOKEN_SEMICOLON);
          break;
        case TOKEN_DISCARD:
          locals.add(this.declarationFragmentShaderLocalDiscard());
          this.parserConsumeExact(Type.TOKEN_SEMICOLON);
          break;
        // $CASES-OMITTED$
        default:
          return locals;
      }
    }
  }

  public
    UASTIDShaderFragmentOutput<UASTIUnchecked>
    declarationFragmentShaderOutput()
      throws ParserError,
        IOException,
        LexerError,
        ConstraintError
  {
    this.parserConsumeExact(Type.TOKEN_OUT);
    this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
    final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
    this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
    this.parserConsumeExact(Type.TOKEN_COLON);
    final UASTITypePath type = this.declarationTypePath();
    this.parserConsumeExact(Type.TOKEN_AS);
    this.parserExpectExact(Type.TOKEN_LITERAL_INTEGER_DECIMAL);
    final TokenLiteralIntegerDecimal index =
      (TokenLiteralIntegerDecimal) this.token;
    this.parserConsumeExact(Type.TOKEN_LITERAL_INTEGER_DECIMAL);

    return new UASTIDShaderFragmentOutput<UASTIUnchecked>(name, type, index
      .getValue()
      .intValue());
  }

  public @Nonnull
    UASTIDShaderFragmentOutputAssignment<UASTIUnchecked>
    declarationFragmentShaderOutputAssignment()
      throws ParserError,
        IOException,
        LexerError,
        ConstraintError
  {
    this.parserConsumeExact(Type.TOKEN_OUT);
    this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
    final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
    this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
    this.parserConsumeExact(Type.TOKEN_EQUALS);
    final UASTIEVariable<UASTIUnchecked> value =
      new UASTIEVariable<UASTIUnchecked>(this.declarationValuePath());
    return new UASTIDShaderFragmentOutputAssignment<UASTIUnchecked>(
      name,
      value);
  }

  public @Nonnull
    List<UASTIDShaderFragmentOutputAssignment<UASTIUnchecked>>
    declarationFragmentShaderOutputAssignments()
      throws ParserError,
        IOException,
        LexerError,
        ConstraintError
  {
    final List<UASTIDShaderFragmentOutputAssignment<UASTIUnchecked>> assigns =
      new ArrayList<UASTIDShaderFragmentOutputAssignment<UASTIUnchecked>>();

    for (;;) {
      switch (this.token.getType()) {
        case TOKEN_OUT:
          assigns.add(this.declarationFragmentShaderOutputAssignment());
          this.parserConsumeExact(Type.TOKEN_SEMICOLON);
          break;
        // $CASES-OMITTED$
        default:
          return assigns;
      }
    }
  }

  public @Nonnull
    UASTIDShaderFragmentParameter<UASTIUnchecked>
    declarationFragmentShaderParameter()
      throws ParserError,
        IOException,
        LexerError,
        ConstraintError
  {
    this.parserConsumeExact(Type.TOKEN_PARAMETER);
    this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
    final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
    this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
    this.parserConsumeExact(Type.TOKEN_COLON);
    return new UASTIDShaderFragmentParameter<UASTIUnchecked>(
      name,
      this.declarationTypePath());
  }

  public @Nonnull
    UASTIDShaderFragmentParameters<UASTIUnchecked>
    declarationFragmentShaderParameterDeclaration()
      throws ParserError,
        IOException,
        LexerError,
        ConstraintError
  {
    this.parserExpectOneOf(new Type[] {
      Type.TOKEN_IN,
      Type.TOKEN_OUT,
      Type.TOKEN_PARAMETER });

    switch (this.token.getType()) {
      case TOKEN_IN:
        return this.declarationFragmentShaderInput();
      case TOKEN_OUT:
        return this.declarationFragmentShaderOutput();
      case TOKEN_PARAMETER:
        return this.declarationFragmentShaderParameter();
        // $CASES-OMITTED$
      default:
        throw new UnreachableCodeException();
    }
  }

  public @Nonnull
    List<UASTIDShaderFragmentParameters<UASTIUnchecked>>
    declarationFragmentShaderParameterDeclarations()
      throws ParserError,
        IOException,
        LexerError,
        ConstraintError
  {
    final List<UASTIDShaderFragmentParameters<UASTIUnchecked>> declarations =
      new ArrayList<UASTIDShaderFragmentParameters<UASTIUnchecked>>();

    for (;;) {
      switch (this.token.getType()) {
        case TOKEN_IN:
        case TOKEN_OUT:
        case TOKEN_PARAMETER:
          declarations.add(this
            .declarationFragmentShaderParameterDeclaration());
          this.parserConsumeExact(Type.TOKEN_SEMICOLON);
          break;
        // $CASES-OMITTED$
        default:
          return declarations;
      }
    }
  }

  public @Nonnull UASTIDFunction<UASTIUnchecked> declarationFunction()
    throws ParserError,
      IOException,
      LexerError,
      ConstraintError
  {
    this.parserConsumeExact(Type.TOKEN_FUNCTION);
    this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
    final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
    this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);

    final List<UASTIDFunctionArgument<UASTIUnchecked>> args =
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
          return new UASTIDFunctionExternal<UASTIUnchecked>(
            name,
            args,
            type,
            ext);
        }

        /**
         * Otherwise, attempt to parse "external" as an expression, which will
         * result in an error.
         */

        return new UASTIDFunctionDefined<UASTIUnchecked>(
          name,
          args,
          type,
          this.expression());

        // $CASES-OMITTED$
      default:
        return new UASTIDFunctionDefined<UASTIUnchecked>(
          name,
          args,
          type,
          this.expression());
    }
  }

  public @Nonnull
    UASTIDFunctionArgument<UASTIUnchecked>
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
    return new UASTIDFunctionArgument<UASTIUnchecked>(
      name,
      this.declarationTypePath());
  }

  public @Nonnull
    List<UASTIDFunctionArgument<UASTIUnchecked>>
    declarationFunctionArguments()
      throws ParserError,
        IOException,
        LexerError,
        ConstraintError
  {
    this.parserConsumeExact(Type.TOKEN_ROUND_LEFT);

    final ArrayList<UASTIDFunctionArgument<UASTIUnchecked>> args =
      new ArrayList<UASTIDFunctionArgument<UASTIUnchecked>>();
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

  public @Nonnull UASTIDImport<UASTIUnchecked> declarationImport()
    throws ParserError,
      IOException,
      LexerError,
      ConstraintError
  {
    this.parserConsumeExact(Type.TOKEN_IMPORT);
    final PackagePath path = this.declarationPackagePath();
    this.parserExpectExact(Type.TOKEN_IDENTIFIER_UPPER);
    final TokenIdentifierUpper name = (TokenIdentifierUpper) this.token;
    this.parserConsumeExact(Type.TOKEN_IDENTIFIER_UPPER);

    switch (this.token.getType()) {
      case TOKEN_AS:
      {
        this.parserConsumeExact(Type.TOKEN_AS);
        this.parserExpectExact(Type.TOKEN_IDENTIFIER_UPPER);
        final TokenIdentifierUpper rename = (TokenIdentifierUpper) this.token;
        this.parserConsumeExact(Type.TOKEN_IDENTIFIER_UPPER);
        return new UASTIDImport<UASTIUnchecked>(
          new ModulePath(path, name),
          Option.some(rename));
      }
      // $CASES-OMITTED$
      default:
        final None<TokenIdentifierUpper> none = Option.none();
        return new UASTIDImport<UASTIUnchecked>(
          new ModulePath(path, name),
          none);
    }
  }

  public @Nonnull UASTIDPackage<UASTIUnchecked> declarationPackage()
    throws ParserError,
      IOException,
      LexerError,
      ConstraintError
  {
    this.parserConsumeExact(Type.TOKEN_PACKAGE);
    return new UASTIDPackage<UASTIUnchecked>(this.declarationPackagePath());
  }

  public @Nonnull PackagePath declarationPackagePath()
    throws ConstraintError,
      ParserError,
      IOException,
      LexerError
  {
    final ArrayList<TokenIdentifierLower> components =
      new ArrayList<TokenIdentifierLower>();

    boolean done = false;
    while (done == false) {
      switch (this.token.getType()) {
        case TOKEN_IDENTIFIER_LOWER:
        {
          components.add((TokenIdentifierLower) this.token);
          this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
          switch (this.token.getType()) {
            case TOKEN_DOT:
              this.parserConsumeExact(Type.TOKEN_DOT);
              break;
            // $CASES-OMITTED$
            default:
              done = true;
              break;
          }
          break;
        }
        // $CASES-OMITTED$
        default:
          done = true;
          break;
      }
    }

    return new PackagePath(components);
  }

  public @Nonnull
    UASTIDShaderProgram<UASTIUnchecked>
    declarationProgramShader()
      throws ParserError,
        IOException,
        LexerError,
        ConstraintError
  {
    this.parserConsumeExact(Type.TOKEN_PROGRAM);
    this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
    final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
    this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
    this.parserConsumeExact(Type.TOKEN_IS);

    this.parserConsumeExact(Type.TOKEN_VERTEX);
    final UASTIShaderPath vertex = this.declarationShaderPath();
    this.parserConsumeExact(Type.TOKEN_SEMICOLON);

    this.parserConsumeExact(Type.TOKEN_FRAGMENT);
    final UASTIShaderPath fragment = this.declarationShaderPath();
    this.parserConsumeExact(Type.TOKEN_SEMICOLON);

    this.parserConsumeExact(Type.TOKEN_END);
    return new UASTIDShaderProgram<UASTIUnchecked>(name, vertex, fragment);
  }

  public @Nonnull UASTIDShader<UASTIUnchecked> declarationShader()
    throws ParserError,
      IOException,
      LexerError,
      ConstraintError
  {
    this.parserConsumeExact(Type.TOKEN_SHADER);
    this.parserExpectOneOf(new Type[] {
      Type.TOKEN_VERTEX,
      Type.TOKEN_FRAGMENT,
      Type.TOKEN_PROGRAM });

    switch (this.token.getType()) {
      case TOKEN_VERTEX:
        return this.declarationVertexShader();
      case TOKEN_FRAGMENT:
        return this.declarationFragmentShader();
      case TOKEN_PROGRAM:
        return this.declarationProgramShader();
        // $CASES-OMITTED$
      default:
        throw new UnreachableCodeException();
    }
  }

  public @Nonnull UASTIShaderPath declarationShaderPath()
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
        return new UASTIShaderPath(none, name);
      }
      case TOKEN_IDENTIFIER_UPPER:
      {
        final TokenIdentifierUpper module = (TokenIdentifierUpper) this.token;
        this.parserConsumeExact(Type.TOKEN_IDENTIFIER_UPPER);
        this.parserConsumeExact(Type.TOKEN_DOT);
        this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
        final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
        this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
        return new UASTIShaderPath(Option.some(module), name);
      }

      // $CASES-OMITTED$
      default:
        throw new UnreachableCodeException();
    }
  }

  public @Nonnull UASTIDType<UASTIUnchecked> declarationType()
    throws ConstraintError,
      ParserError,
      IOException,
      LexerError
  {
    this.parserConsumeExact(Type.TOKEN_TYPE);
    this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
    final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
    this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
    this.parserConsumeExact(Type.TOKEN_IS);
    this.parserExpectOneOf(new Type[] { Type.TOKEN_RECORD });

    switch (this.token.getType()) {
      case TOKEN_RECORD:
        this.parserConsumeExact(Type.TOKEN_RECORD);
        final List<UASTIDTypeRecordField<UASTIUnchecked>> fields =
          this.declarationTypeRecordFields();
        this.parserExpectExact(Type.TOKEN_END);
        return new UASTIDTypeRecord<UASTIUnchecked>(name, fields);
        // $CASES-OMITTED$
      default:
        throw new UnreachableCodeException();
    }
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

  public @Nonnull
    UASTIDTypeRecordField<UASTIUnchecked>
    declarationTypeRecordField()
      throws ConstraintError,
        ParserError,
        IOException,
        LexerError
  {
    this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
    final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
    this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
    this.parserConsumeExact(Type.TOKEN_COLON);
    final UASTITypePath type = this.declarationTypePath();
    return new UASTIDTypeRecordField<UASTIUnchecked>(name, type);
  }

  public @Nonnull
    List<UASTIDTypeRecordField<UASTIUnchecked>>
    declarationTypeRecordFields()
      throws ParserError,
        IOException,
        LexerError,
        ConstraintError
  {
    final ArrayList<UASTIDTypeRecordField<UASTIUnchecked>> args =
      new ArrayList<UASTIDTypeRecordField<UASTIUnchecked>>();
    args.add(this.declarationTypeRecordField());

    boolean done = false;
    while (done == false) {
      switch (this.token.getType()) {
        case TOKEN_COMMA:
          this.parserConsumeExact(Type.TOKEN_COMMA);
          args.add(this.declarationTypeRecordField());
          break;
        // $CASES-OMITTED$
        default:
          done = true;
      }
    }

    return args;
  }

  public @Nonnull UASTIDValue<UASTIUnchecked> declarationValue()
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
        return new UASTIDValue<UASTIUnchecked>(
          name,
          Option.some(path),
          this.expression());
      }
      case TOKEN_EQUALS:
      {
        this.parserConsumeExact(Type.TOKEN_EQUALS);
        final Option<UASTITypePath> none = Option.none();
        return new UASTIDValue<UASTIUnchecked>(name, none, this.expression());
      }
      // $CASES-OMITTED$
      default:
        throw new UnreachableCodeException();
    }
  }

  public @Nonnull UASTIDValueLocal<UASTIUnchecked> declarationValueLocal()
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
        return new UASTIDValueLocal<UASTIUnchecked>(
          name,
          Option.some(path),
          this.expression());
      }
      case TOKEN_EQUALS:
      {
        this.parserConsumeExact(Type.TOKEN_EQUALS);
        final Option<UASTITypePath> none = Option.none();
        return new UASTIDValueLocal<UASTIUnchecked>(
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
    List<UASTIDValueLocal<UASTIUnchecked>>
    declarationValueLocals()
      throws ParserError,
        IOException,
        LexerError,
        ConstraintError
  {
    final List<UASTIDValueLocal<UASTIUnchecked>> values =
      new ArrayList<UASTIDValueLocal<UASTIUnchecked>>();
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

  public @Nonnull
    UASTIDShaderVertex<UASTIUnchecked>
    declarationVertexShader()
      throws ParserError,
        IOException,
        LexerError,
        ConstraintError
  {
    this.parserConsumeExact(Type.TOKEN_VERTEX);
    this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
    final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
    this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
    this.parserConsumeExact(Type.TOKEN_IS);

    final List<UASTIDShaderVertexParameters<UASTIUnchecked>> decls =
      this.declarationVertexShaderParameterDeclarations();

    final List<UASTIDShaderVertexInput<UASTIUnchecked>> inputs =
      new ArrayList<UASTIDShaderVertexInput<UASTIUnchecked>>();
    final List<UASTIDShaderVertexOutput<UASTIUnchecked>> outputs =
      new ArrayList<UASTIDShaderVertexOutput<UASTIUnchecked>>();
    final List<UASTIDShaderVertexParameter<UASTIUnchecked>> parameters =
      new ArrayList<UASTIDShaderVertexParameter<UASTIUnchecked>>();

    for (int index = 0; index < decls.size(); ++index) {
      final UASTIDShaderVertexParameters<UASTIUnchecked> d = decls.get(index);
      if (d instanceof UASTIDShaderVertexInput<?>) {
        inputs.add((UASTIDShaderVertexInput<UASTIUnchecked>) d);
        continue;
      }
      if (d instanceof UASTIDShaderVertexOutput<?>) {
        outputs.add((UASTIDShaderVertexOutput<UASTIUnchecked>) d);
        continue;
      }
      if (d instanceof UASTIDShaderVertexParameter<?>) {
        parameters.add((UASTIDShaderVertexParameter<UASTIUnchecked>) d);
        continue;
      }
    }

    this.parserExpectOneOf(new Type[] { Type.TOKEN_WITH, Type.TOKEN_AS });

    final List<UASTIDValueLocal<UASTIUnchecked>> values;
    switch (this.token.getType()) {
      case TOKEN_WITH:
      {
        this.parserConsumeExact(Type.TOKEN_WITH);
        values = this.declarationValueLocals();
        break;
      }
      // $CASES-OMITTED$
      default:
      {
        values = new ArrayList<UASTIDValueLocal<UASTIUnchecked>>();
        break;
      }
    }

    this.parserConsumeExact(Type.TOKEN_AS);
    final List<UASTIDShaderVertexOutputAssignment<UASTIUnchecked>> assigns =
      this.declarationVertexShaderOutputAssignments();
    this.parserConsumeExact(Type.TOKEN_END);

    return new UASTIDShaderVertex<UASTIUnchecked>(
      name,
      inputs,
      outputs,
      parameters,
      values,
      assigns);
  }

  public
    UASTIDShaderVertexInput<UASTIUnchecked>
    declarationVertexShaderInput()
      throws ParserError,
        IOException,
        LexerError,
        ConstraintError
  {
    this.parserConsumeExact(Type.TOKEN_IN);
    this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
    final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
    this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
    this.parserConsumeExact(Type.TOKEN_COLON);
    return new UASTIDShaderVertexInput<UASTIUnchecked>(
      name,
      this.declarationTypePath());
  }

  public
    UASTIDShaderVertexOutput<UASTIUnchecked>
    declarationVertexShaderOutput()
      throws ParserError,
        IOException,
        LexerError,
        ConstraintError
  {
    this.parserConsumeExact(Type.TOKEN_OUT);
    this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
    final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
    this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
    this.parserConsumeExact(Type.TOKEN_COLON);
    return new UASTIDShaderVertexOutput<UASTIUnchecked>(
      name,
      this.declarationTypePath());
  }

  public @Nonnull
    UASTIDShaderVertexOutputAssignment<UASTIUnchecked>
    declarationVertexShaderOutputAssignment()
      throws ParserError,
        IOException,
        LexerError,
        ConstraintError
  {
    this.parserConsumeExact(Type.TOKEN_OUT);
    this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
    final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
    this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
    this.parserConsumeExact(Type.TOKEN_EQUALS);
    final UASTIEVariable<UASTIUnchecked> value =
      new UASTIEVariable<UASTIUnchecked>(this.declarationValuePath());
    return new UASTIDShaderVertexOutputAssignment<UASTIUnchecked>(name, value);
  }

  public @Nonnull
    List<UASTIDShaderVertexOutputAssignment<UASTIUnchecked>>
    declarationVertexShaderOutputAssignments()
      throws ParserError,
        IOException,
        LexerError,
        ConstraintError
  {
    final List<UASTIDShaderVertexOutputAssignment<UASTIUnchecked>> assigns =
      new ArrayList<UASTIDShaderVertexOutputAssignment<UASTIUnchecked>>();

    for (;;) {
      switch (this.token.getType()) {
        case TOKEN_OUT:
          assigns.add(this.declarationVertexShaderOutputAssignment());
          this.parserConsumeExact(Type.TOKEN_SEMICOLON);
          break;
        // $CASES-OMITTED$
        default:
          return assigns;
      }
    }
  }

  public @Nonnull
    UASTIDShaderVertexParameter<UASTIUnchecked>
    declarationVertexShaderParameter()
      throws ParserError,
        IOException,
        LexerError,
        ConstraintError
  {
    this.parserConsumeExact(Type.TOKEN_PARAMETER);
    this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
    final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
    this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
    this.parserConsumeExact(Type.TOKEN_COLON);
    return new UASTIDShaderVertexParameter<UASTIUnchecked>(
      name,
      this.declarationTypePath());
  }

  public @Nonnull
    UASTIDShaderVertexParameters<UASTIUnchecked>
    declarationVertexShaderParameterDeclaration()
      throws ParserError,
        IOException,
        LexerError,
        ConstraintError
  {
    this.parserExpectOneOf(new Type[] {
      Type.TOKEN_IN,
      Type.TOKEN_OUT,
      Type.TOKEN_PARAMETER });

    switch (this.token.getType()) {
      case TOKEN_IN:
        return this.declarationVertexShaderInput();
      case TOKEN_OUT:
        return this.declarationVertexShaderOutput();
      case TOKEN_PARAMETER:
        return this.declarationVertexShaderParameter();
        // $CASES-OMITTED$
      default:
        throw new UnreachableCodeException();
    }
  }

  public @Nonnull
    List<UASTIDShaderVertexParameters<UASTIUnchecked>>
    declarationVertexShaderParameterDeclarations()
      throws ParserError,
        IOException,
        LexerError,
        ConstraintError
  {
    final List<UASTIDShaderVertexParameters<UASTIUnchecked>> declarations =
      new ArrayList<UASTIDShaderVertexParameters<UASTIUnchecked>>();

    for (;;) {
      switch (this.token.getType()) {
        case TOKEN_IN:
        case TOKEN_OUT:
        case TOKEN_PARAMETER:
          declarations
            .add(this.declarationVertexShaderParameterDeclaration());
          this.parserConsumeExact(Type.TOKEN_SEMICOLON);
          break;
        // $CASES-OMITTED$
        default:
          return declarations;
      }
    }
  }

  public @Nonnull UASTIExpression<UASTIUnchecked> expression()
    throws ParserError,
      IOException,
      LexerError,
      ConstraintError
  {
    return this.expressionPost(this.expressionPre());
  }

  public @Nonnull
    List<UASTIExpression<UASTIUnchecked>>
    expressionApplicationArguments()
      throws ParserError,
        IOException,
        LexerError,
        ConstraintError
  {
    this.parserConsumeExact(Type.TOKEN_ROUND_LEFT);
    final ArrayList<UASTIExpression<UASTIUnchecked>> arguments =
      new ArrayList<UASTIExpression<UASTIUnchecked>>();
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

  public @Nonnull UASTIEBoolean<UASTIUnchecked> expressionBoolean()
    throws ConstraintError,
      ParserError,
      IOException,
      LexerError
  {
    this.parserExpectExact(Type.TOKEN_LITERAL_BOOLEAN);
    final UASTIEBoolean<UASTIUnchecked> t =
      new UASTIEBoolean<UASTIUnchecked>((TokenLiteralBoolean) this.token);
    this.parserConsumeAny();
    return t;
  }

  public @Nonnull UASTIEConditional<UASTIUnchecked> expressionConditional()
    throws ParserError,
      ConstraintError,
      IOException,
      LexerError
  {
    this.parserConsumeExact(Type.TOKEN_IF);
    final UASTIExpression<UASTIUnchecked> econd = this.expression();
    this.parserConsumeExact(Type.TOKEN_THEN);
    final UASTIExpression<UASTIUnchecked> eleft = this.expression();
    this.parserConsumeExact(Type.TOKEN_ELSE);
    final UASTIExpression<UASTIUnchecked> eright = this.expression();
    this.parserConsumeExact(Type.TOKEN_END);
    return new UASTIEConditional<UASTIUnchecked>(econd, eleft, eright);
  }

  public @Nonnull UASTIEInteger<UASTIUnchecked> expressionInteger()
    throws ParserError,
      ConstraintError,
      IOException,
      LexerError
  {
    this.parserExpectExact(Type.TOKEN_LITERAL_INTEGER_DECIMAL);
    final UASTIEInteger<UASTIUnchecked> t =
      new UASTIEInteger<UASTIUnchecked>((TokenLiteralInteger) this.token);
    this.parserConsumeAny();
    return t;
  }

  public @Nonnull UASTIELet<UASTIUnchecked> expressionLet()
    throws ParserError,
      ConstraintError,
      IOException,
      LexerError
  {
    this.parserExpectExact(Type.TOKEN_LET);
    final TokenLet let = (TokenLet) this.token;
    this.parserConsumeExact(Type.TOKEN_LET);
    final List<UASTIDValueLocal<UASTIUnchecked>> bindings =
      this.declarationValueLocals();
    this.parserConsumeExact(Type.TOKEN_IN);
    final UASTIExpression<UASTIUnchecked> body = this.expression();
    this.parserConsumeExact(Type.TOKEN_END);
    return new UASTIELet<UASTIUnchecked>(let, bindings, body);
  }

  public @Nonnull UASTIENew<UASTIUnchecked> expressionNew()
    throws ParserError,
      IOException,
      LexerError,
      ConstraintError
  {
    this.parserConsumeExact(Type.TOKEN_NEW);
    final UASTITypePath path = this.declarationTypePath();
    return new UASTIENew<UASTIUnchecked>(
      path,
      this.expressionApplicationArguments());
  }

  private @Nonnull UASTIExpression<UASTIUnchecked> expressionPost(
    final @Nonnull UASTIExpression<UASTIUnchecked> e)
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

  private @Nonnull UASTIExpression<UASTIUnchecked> expressionPre()
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

  public @Nonnull UASTIEReal<UASTIUnchecked> expressionReal()
    throws ParserError,
      ConstraintError,
      IOException,
      LexerError
  {
    this.parserExpectExact(Type.TOKEN_LITERAL_REAL);
    final UASTIEReal<UASTIUnchecked> t =
      new UASTIEReal<UASTIUnchecked>((TokenLiteralReal) this.token);
    this.parserConsumeAny();
    return t;
  }

  public @Nonnull UASTIERecord<UASTIUnchecked> expressionRecord()
    throws ParserError,
      IOException,
      LexerError,
      ConstraintError
  {
    this.parserConsumeExact(Type.TOKEN_RECORD);
    final UASTITypePath path = this.declarationTypePath();
    this.parserConsumeExact(Type.TOKEN_CURLY_LEFT);
    final List<UASTIRecordFieldAssignment<UASTIUnchecked>> fields =
      new ArrayList<UASTIRecordFieldAssignment<UASTIUnchecked>>();
    fields.add(this.expressionRecordFieldAssignment());
    this.expressionRecordActual(fields);
    this.parserConsumeExact(Type.TOKEN_CURLY_RIGHT);
    return new UASTIERecord<UASTIUnchecked>(path, fields);
  }

  private void expressionRecordActual(
    final @Nonnull List<UASTIRecordFieldAssignment<UASTIUnchecked>> fields)
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

  public @Nonnull
    UASTIRecordFieldAssignment<UASTIUnchecked>
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
    return new UASTIRecordFieldAssignment<UASTIUnchecked>(
      name,
      this.expression());
  }

  public @Nonnull
    UASTIERecordProjection<UASTIUnchecked>
    expressionRecordProjection(
      final @Nonnull UASTIExpression<UASTIUnchecked> e)
      throws ConstraintError,
        ParserError,
        IOException,
        LexerError
  {
    this.parserConsumeExact(Type.TOKEN_DOT);
    this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
    final UASTIERecordProjection<UASTIUnchecked> r =
      new UASTIERecordProjection<UASTIUnchecked>(
        e,
        (TokenIdentifierLower) this.token);
    this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
    return r;
  }

  public UASTIESwizzle<UASTIUnchecked> expressionSwizzle(
    final @Nonnull UASTIExpression<UASTIUnchecked> e)
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
    return new UASTIESwizzle<UASTIUnchecked>(e, fields);
  }

  public @Nonnull TokenIdentifierLower expressionSwizzleField()
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
    UASTIExpression<UASTIUnchecked>
    expressionVariableOrApplication()
      throws ParserError,
        IOException,
        LexerError,
        ConstraintError
  {
    final UASTIValuePath path = this.declarationValuePath();
    switch (this.token.getType()) {
      case TOKEN_ROUND_LEFT:
        return new UASTIEApplication<UASTIUnchecked>(
          path,
          this.expressionApplicationArguments());
        // $CASES-OMITTED$
      default:
        return new UASTIEVariable<UASTIUnchecked>(path);
    }
  }

  protected void parserConsumeAny()
    throws IOException,
      LexerError,
      ConstraintError
  {
    this.token = this.lexer.token();
  }

  protected void parserConsumeExact(
    final @Nonnull Token.Type type)
    throws ParserError,
      ConstraintError,
      IOException,
      LexerError
  {
    this.parserExpectExact(type);
    this.parserConsumeAny();
  }

  protected void parserExpectExact(
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

  protected void parserExpectOneOf(
    final @Nonnull Token.Type types[])
    throws ParserError,
      ConstraintError
  {
    for (final Type want : types) {
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
