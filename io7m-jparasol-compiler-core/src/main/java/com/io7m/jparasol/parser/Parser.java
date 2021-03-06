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

package com.io7m.jparasol.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jfunctional.Option;
import com.io7m.jfunctional.OptionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.ModulePath;
import com.io7m.jparasol.PackagePath;
import com.io7m.jparasol.PackagePath.BuilderType;
import com.io7m.jparasol.core.GVersionES;
import com.io7m.jparasol.core.GVersionFull;
import com.io7m.jparasol.lexer.Lexer;
import com.io7m.jparasol.lexer.LexerError;
import com.io7m.jparasol.lexer.Token;
import com.io7m.jparasol.lexer.Token.TokenDiscard;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.lexer.Token.TokenIdentifierUpper;
import com.io7m.jparasol.lexer.Token.TokenIf;
import com.io7m.jparasol.lexer.Token.TokenLet;
import com.io7m.jparasol.lexer.Token.TokenLiteralBoolean;
import com.io7m.jparasol.lexer.Token.TokenLiteralInteger;
import com.io7m.jparasol.lexer.Token.TokenLiteralIntegerDecimal;
import com.io7m.jparasol.lexer.Token.TokenLiteralReal;
import com.io7m.jparasol.lexer.Token.Type;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDExternal;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDFunction;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDFunctionArgument;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDFunctionDefined;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDFunctionExternal;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDImport;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDModule;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDPackage;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShader;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragment;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentInput;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentLocal;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentLocalDiscard;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentLocalValue;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentOutput;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentOutputAssignment;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentOutputData;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentOutputDepth;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentParameter;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentParameters;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderProgram;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertex;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertexInput;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertexLocalValue;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertexOutput;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertexOutputAssignment;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertexParameter;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertexParameters;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDType;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDTypeRecord;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDTypeRecordField;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDValue;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDValueDefined;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDValueExternal;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDValueLocal;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDeclarationModuleLevel;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpression;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpression.UASTIEApplication;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpression.UASTIEBoolean;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpression.UASTIEConditional;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpression.UASTIEInteger;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpression.UASTIELet;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpression.UASTIEMatrixColumnAccess;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpression.UASTIENew;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpression.UASTIEReal;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpression.UASTIERecord;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpression.UASTIERecordProjection;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpression.UASTIESwizzle;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpression.UASTIEVariable;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpression.UASTIRecordFieldAssignment;
import com.io7m.jparasol.untyped.ast.initial.UASTIShaderPath;
import com.io7m.jparasol.untyped.ast.initial.UASTITypePath;
import com.io7m.jparasol.untyped.ast.initial.UASTIUnit;
import com.io7m.jparasol.untyped.ast.initial.UASTIValuePath;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * The main parser.
 */

@EqualityReference public final class Parser
{
  /**
   * Construct a new parser capable of parsing "internal" (standard library)
   * units.
   *
   * @param lexer
   *          The lexer
   * @return A new parser
   * @throws IOException
   *           If an I/O error occurs
   * @throws LexerError
   *           If a lexical error occurs
   */

  public static Parser newInternalParser(
    final Lexer lexer)
    throws IOException,
      LexerError
  {
    return new Parser(true, lexer);
  }

  /**
   * Construct a new parser capable of parsing ordinary units.
   *
   * @param lexer
   *          The lexer
   * @return A new parser
   * @throws IOException
   *           If an I/O error occurs
   * @throws LexerError
   *           If a lexical error occurs
   */

  public static Parser newParser(
    final Lexer lexer)
    throws IOException,
      LexerError
  {
    return new Parser(false, lexer);
  }

  private final boolean       internal;
  private final Lexer         lexer;
  private final StringBuilder message;
  private Token               token;

  private Parser(
    final boolean in_internal,
    final Lexer in_lexer)
    throws IOException,
      LexerError
  {
    this.internal = in_internal;
    this.lexer = NullCheck.notNull(in_lexer, "Lexer");
    this.message = new StringBuilder();
    this.token = in_lexer.token();
  }

  // CHECKSTYLE_JAVADOC:OFF
  // CHECKSTYLE_SPACE:OFF

  public UASTIDExternal declarationExternal()
    throws ParserError,
      LexerError,
      IOException
  {
    this.parserConsumeExact(Type.TOKEN_EXTERNAL);
    this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
    final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
    this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
    this.parserConsumeExact(Type.TOKEN_IS);

    final SortedSet<GVersionES> supported_es =
      new TreeSet<GVersionES>(GVersionES.ALL);
    final SortedSet<GVersionFull> supported_full =
      new TreeSet<GVersionFull>(GVersionFull.ALL);

    VERTEX_AND_RESTRICT:
    while (true) {
      this.parserExpectOneOf(new Type[] {
        Type.TOKEN_VERTEX,
        Type.TOKEN_RESTRICT, });

      switch (this.token.getType()) {
        case TOKEN_VERTEX:
        {
          break VERTEX_AND_RESTRICT;
        }
        case TOKEN_RESTRICT:
        {
          this.parserConsumeExact(Type.TOKEN_RESTRICT);
          this.parserExpectExact(Type.TOKEN_LITERAL_INTEGER_DECIMAL);
          final TokenLiteralIntegerDecimal number =
            (TokenLiteralIntegerDecimal) this.token;
          this.parserConsumeExact(Type.TOKEN_LITERAL_INTEGER_DECIMAL);
          this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
          final TokenIdentifierLower profile =
            (TokenIdentifierLower) this.token;
          this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
          this.parserConsumeExact(Type.TOKEN_SEMICOLON);

          final String profile_name = profile.getActual();
          final int version = number.getValue().intValue();
          if ("es".equals(profile_name)) {
            supported_es.remove(new GVersionES(version));
          } else if ("full".equals(profile_name)) {
            supported_full.remove(new GVersionFull(version));
          } else {
            throw this.unknownProfile(profile_name);
          }

          break;
        }

        // $CASES-OMITTED$
        default:
          throw new UnreachableCodeException();
      }
    }

    this.parserConsumeExact(Type.TOKEN_VERTEX);
    this.parserExpectExact(Type.TOKEN_LITERAL_BOOLEAN);
    final TokenLiteralBoolean vallow = (TokenLiteralBoolean) this.token;
    this.parserConsumeExact(Type.TOKEN_LITERAL_BOOLEAN);
    this.parserConsumeExact(Type.TOKEN_SEMICOLON);

    this.parserConsumeExact(Type.TOKEN_FRAGMENT);
    this.parserExpectExact(Type.TOKEN_LITERAL_BOOLEAN);
    final TokenLiteralBoolean fallow = (TokenLiteralBoolean) this.token;
    this.parserConsumeExact(Type.TOKEN_LITERAL_BOOLEAN);
    this.parserConsumeExact(Type.TOKEN_SEMICOLON);

    this.parserExpectOneOf(new Type[] { Type.TOKEN_END, Type.TOKEN_WITH, });

    switch (this.token.getType()) {
      case TOKEN_END:
      {
        this.parserConsumeExact(Type.TOKEN_END);
        final OptionType<UASTIExpression> none = Option.none();
        return new UASTIDExternal(
          name,
          vallow.getValue(),
          fallow.getValue(),
          none,
          supported_es,
          supported_full);
      }
      case TOKEN_WITH:
      {
        this.parserConsumeExact(Type.TOKEN_WITH);
        final UASTIExpression e = this.expression();
        this.parserConsumeExact(Type.TOKEN_END);
        final OptionType<UASTIExpression> some = Option.some(e);
        return new UASTIDExternal(
          name,
          vallow.getValue(),
          fallow.getValue(),
          some,
          supported_es,
          supported_full);
      }

      // $CASES-OMITTED$
      default:
        throw new UnreachableCodeException();
    }
  }

  public UASTIDShaderFragment declarationFragmentShader()
    throws ParserError,
      IOException,
      LexerError
  {
    this.parserConsumeExact(Type.TOKEN_FRAGMENT);
    this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
    final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
    this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
    this.parserConsumeExact(Type.TOKEN_IS);

    final List<UASTIDShaderFragmentParameters> decls =
      this.declarationFragmentShaderParameterDeclarations();

    final List<UASTIDShaderFragmentInput> inputs =
      new ArrayList<UASTIDShaderFragmentInput>();
    final List<UASTIDShaderFragmentOutput> outputs =
      new ArrayList<UASTIDShaderFragmentOutput>();
    final List<UASTIDShaderFragmentParameter> parameters =
      new ArrayList<UASTIDShaderFragmentParameter>();

    for (int index = 0; index < decls.size(); ++index) {
      final UASTIDShaderFragmentParameters d = decls.get(index);
      if (d instanceof UASTIDShaderFragmentInput) {
        inputs.add((UASTIDShaderFragmentInput) d);
        continue;
      }
      if (d instanceof UASTIDShaderFragmentOutput) {
        outputs.add((UASTIDShaderFragmentOutput) d);
        continue;
      }
      if (d instanceof UASTIDShaderFragmentParameter) {
        parameters.add((UASTIDShaderFragmentParameter) d);
        continue;
      }
    }

    this.parserExpectOneOf(new Type[] { Type.TOKEN_WITH, Type.TOKEN_AS, });

    final List<UASTIDShaderFragmentLocal> values;
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
        values = new ArrayList<UASTIDShaderFragmentLocal>();
        break;
      }
    }

    this.parserConsumeExact(Type.TOKEN_AS);
    final List<UASTIDShaderFragmentOutputAssignment> assigns =
      this.declarationFragmentShaderOutputAssignments();
    this.parserConsumeExact(Type.TOKEN_END);

    return new UASTIDShaderFragment(
      name,
      inputs,
      outputs,
      parameters,
      values,
      assigns);
  }

  public UASTIDShaderFragmentInput declarationFragmentShaderInput()
    throws ParserError,
      IOException,
      LexerError
  {
    this.parserConsumeExact(Type.TOKEN_IN);
    this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
    final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
    this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
    this.parserConsumeExact(Type.TOKEN_COLON);
    return new UASTIDShaderFragmentInput(name, this.declarationTypePath());
  }

  public
    UASTIDShaderFragmentLocalDiscard
    declarationFragmentShaderLocalDiscard()
      throws ParserError,
        IOException,
        LexerError
  {
    this.parserExpectExact(Type.TOKEN_DISCARD);
    final TokenDiscard discard = (TokenDiscard) this.token;
    this.parserConsumeExact(Type.TOKEN_DISCARD);
    this.parserConsumeExact(Type.TOKEN_ROUND_LEFT);
    final UASTIExpression expr = this.expression();
    this.parserConsumeExact(Type.TOKEN_ROUND_RIGHT);
    return new UASTIDShaderFragmentLocalDiscard(discard, expr);
  }

  public List<UASTIDShaderFragmentLocal> declarationFragmentShaderLocals()
    throws ParserError,
      IOException,
      LexerError
  {
    final List<UASTIDShaderFragmentLocal> locals =
      new ArrayList<UASTIDShaderFragmentLocal>();

    for (;;) {
      switch (this.token.getType()) {
        case TOKEN_VALUE:
          locals.add(new UASTIDShaderFragmentLocalValue(this
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

  public UASTIDShaderFragmentOutput declarationFragmentShaderOutput()
    throws ParserError,
      IOException,
      LexerError
  {
    this.parserConsumeExact(Type.TOKEN_OUT);
    this.parserExpectOneOf(new Type[] {
      Type.TOKEN_IDENTIFIER_LOWER,
      Type.TOKEN_DEPTH, });

    switch (this.token.getType()) {
      case TOKEN_IDENTIFIER_LOWER:
      {
        final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
        this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
        this.parserConsumeExact(Type.TOKEN_COLON);
        final UASTITypePath type = this.declarationTypePath();
        this.parserConsumeExact(Type.TOKEN_AS);
        this.parserExpectExact(Type.TOKEN_LITERAL_INTEGER_DECIMAL);
        final TokenLiteralIntegerDecimal index =
          (TokenLiteralIntegerDecimal) this.token;
        this.parserConsumeExact(Type.TOKEN_LITERAL_INTEGER_DECIMAL);

        final int i = index.getValue().intValue();
        return new UASTIDShaderFragmentOutputData(name, type, i);
      }
      case TOKEN_DEPTH:
      {
        this.parserConsumeExact(Type.TOKEN_DEPTH);
        final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
        this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
        this.parserConsumeExact(Type.TOKEN_COLON);
        final UASTITypePath type = this.declarationTypePath();

        return new UASTIDShaderFragmentOutputDepth(name, type);
      }
      // $CASES-OMITTED$
      default:
      {
        throw new UnreachableCodeException();
      }
    }
  }

  public
    UASTIDShaderFragmentOutputAssignment
    declarationFragmentShaderOutputAssignment()
      throws ParserError,
        IOException,
        LexerError
  {
    this.parserConsumeExact(Type.TOKEN_OUT);
    this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
    final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
    this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
    this.parserConsumeExact(Type.TOKEN_EQUALS);
    final UASTIEVariable value =
      new UASTIEVariable(this.declarationValuePath());
    return new UASTIDShaderFragmentOutputAssignment(name, value);
  }

  public
    List<UASTIDShaderFragmentOutputAssignment>
    declarationFragmentShaderOutputAssignments()
      throws ParserError,
        IOException,
        LexerError
  {
    final List<UASTIDShaderFragmentOutputAssignment> assigns =
      new ArrayList<UASTIDShaderFragmentOutputAssignment>();

    assigns.add(this.declarationFragmentShaderOutputAssignment());
    this.parserConsumeExact(Type.TOKEN_SEMICOLON);

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

  public UASTIDShaderFragmentParameter declarationFragmentShaderParameter()
    throws ParserError,
      IOException,
      LexerError
  {
    this.parserConsumeExact(Type.TOKEN_PARAMETER);
    this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
    final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
    this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
    this.parserConsumeExact(Type.TOKEN_COLON);
    return new UASTIDShaderFragmentParameter(name, this.declarationTypePath());
  }

  public
    UASTIDShaderFragmentParameters
    declarationFragmentShaderParameterDeclaration()
      throws ParserError,
        IOException,
        LexerError
  {
    this.parserExpectOneOf(new Type[] {
      Type.TOKEN_IN,
      Type.TOKEN_OUT,
      Type.TOKEN_PARAMETER, });

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

  public
    List<UASTIDShaderFragmentParameters>
    declarationFragmentShaderParameterDeclarations()
      throws ParserError,
        IOException,
        LexerError
  {
    final List<UASTIDShaderFragmentParameters> declarations =
      new ArrayList<UASTIDShaderFragmentParameters>();

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

  public UASTIDFunction declarationFunction()
    throws ParserError,
      IOException,
      LexerError
  {
    this.parserConsumeExact(Type.TOKEN_FUNCTION);
    this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
    final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
    this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);

    final List<UASTIDFunctionArgument> args =
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
          final UASTIDExternal ext = this.declarationExternal();
          return new UASTIDFunctionExternal(name, args, type, ext);
        }

        /**
         * Otherwise, attempt to parse "external" as an expression, which will
         * result in an error.
         */

        return new UASTIDFunctionDefined(name, args, type, this.expression());

        // $CASES-OMITTED$
      default:
        return new UASTIDFunctionDefined(name, args, type, this.expression());
    }
  }

  public UASTIDFunctionArgument declarationFunctionArgument()
    throws ParserError,
      IOException,
      LexerError
  {
    this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
    final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
    this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
    this.parserConsumeExact(Type.TOKEN_COLON);
    return new UASTIDFunctionArgument(name, this.declarationTypePath());
  }

  public List<UASTIDFunctionArgument> declarationFunctionArguments()
    throws ParserError,
      IOException,
      LexerError
  {
    this.parserConsumeExact(Type.TOKEN_ROUND_LEFT);

    final List<UASTIDFunctionArgument> args =
      new ArrayList<UASTIDFunctionArgument>();
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

  public UASTIDImport declarationImport()
    throws ParserError,
      IOException,
      LexerError
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
        return new UASTIDImport(
          new ModulePath(path, name),
          Option.some(rename));
      }
      // $CASES-OMITTED$
      default:
        final OptionType<TokenIdentifierUpper> none = Option.none();
        return new UASTIDImport(new ModulePath(path, name), none);
    }
  }

  public List<UASTIDImport> declarationImports()
    throws ParserError,
      IOException,
      LexerError
  {
    final List<UASTIDImport> imports = new ArrayList<UASTIDImport>();

    for (;;) {
      switch (this.token.getType()) {
        case TOKEN_IMPORT:
          imports.add(this.declarationImport());
          this.parserConsumeExact(Type.TOKEN_SEMICOLON);
          break;
        // $CASES-OMITTED$
        default:
          return imports;
      }
    }
  }

  public UASTIDModule declarationModule(
    final PackagePath pack)
    throws ParserError,
      IOException,
      LexerError
  {
    this.parserConsumeExact(Type.TOKEN_MODULE);
    this.parserExpectExact(Type.TOKEN_IDENTIFIER_UPPER);
    final TokenIdentifierUpper name = (TokenIdentifierUpper) this.token;
    this.parserConsumeExact(Type.TOKEN_IDENTIFIER_UPPER);
    this.parserConsumeExact(Type.TOKEN_IS);

    final List<UASTIDImport> imports = this.declarationImports();
    final List<UASTIDeclarationModuleLevel> declarations =
      this.declarationModuleLevels();

    this.parserConsumeExact(Type.TOKEN_END);
    return new UASTIDModule(new ModulePath(pack, name), imports, declarations);
  }

  public UASTIDeclarationModuleLevel declarationModuleLevel()
    throws ParserError,
      IOException,
      LexerError
  {
    this.parserExpectOneOf(new Type[] {
      Type.TOKEN_VALUE,
      Type.TOKEN_FUNCTION,
      Type.TOKEN_TYPE,
      Type.TOKEN_SHADER, });

    switch (this.token.getType()) {
      case TOKEN_VALUE:
        return this.declarationValue();
      case TOKEN_FUNCTION:
        return this.declarationFunction();
      case TOKEN_TYPE:
        return this.declarationType();
      case TOKEN_SHADER:
        return this.declarationShader();
        // $CASES-OMITTED$
      default:
        throw new UnreachableCodeException();
    }
  }

  public List<UASTIDeclarationModuleLevel> declarationModuleLevels()
    throws ParserError,
      IOException,
      LexerError
  {
    final List<UASTIDeclarationModuleLevel> decls =
      new ArrayList<UASTIDeclarationModuleLevel>();

    for (;;) {
      switch (this.token.getType()) {
        case TOKEN_VALUE:
        case TOKEN_FUNCTION:
        case TOKEN_TYPE:
        case TOKEN_SHADER:
          decls.add(this.declarationModuleLevel());
          this.parserConsumeExact(Type.TOKEN_SEMICOLON);
          break;
        // $CASES-OMITTED$
        default:
          return decls;
      }
    }
  }

  public List<UASTIDModule> declarationModules(
    final PackagePath pack)
    throws ParserError,
      IOException,
      LexerError
  {
    final List<UASTIDModule> modules = new ArrayList<UASTIDModule>();
    for (;;) {
      switch (this.token.getType()) {
        case TOKEN_MODULE:
          modules.add(this.declarationModule(pack));
          this.parserConsumeExact(Type.TOKEN_SEMICOLON);
          break;
        // $CASES-OMITTED$
        default:
          return modules;
      }
    }
  }

  public UASTIDPackage declarationPackage()
    throws ParserError,
      IOException,
      LexerError
  {
    this.parserConsumeExact(Type.TOKEN_PACKAGE);
    return new UASTIDPackage(this.declarationPackagePath());
  }

  public PackagePath declarationPackagePath()
    throws ParserError,
      IOException,
      LexerError
  {
    final BuilderType builder = PackagePath.newBuilder();

    boolean done = false;
    while (done == false) {
      switch (this.token.getType()) {
        case TOKEN_IDENTIFIER_LOWER:
        {
          builder.addComponent((TokenIdentifierLower) this.token);
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

    return builder.build();
  }

  public UASTIDShaderProgram declarationProgramShader()
    throws ParserError,
      IOException,
      LexerError
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
    return new UASTIDShaderProgram(name, vertex, fragment);
  }

  public UASTIDShader declarationShader()
    throws ParserError,
      IOException,
      LexerError
  {
    this.parserConsumeExact(Type.TOKEN_SHADER);
    this.parserExpectOneOf(new Type[] {
      Type.TOKEN_VERTEX,
      Type.TOKEN_FRAGMENT,
      Type.TOKEN_PROGRAM, });

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

  public UASTIShaderPath declarationShaderPath()
    throws ParserError,
      IOException,
      LexerError
  {
    this.parserExpectOneOf(new Type[] {
      Type.TOKEN_IDENTIFIER_LOWER,
      Type.TOKEN_IDENTIFIER_UPPER, });

    switch (this.token.getType()) {
      case TOKEN_IDENTIFIER_LOWER:
      {
        final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
        this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
        final OptionType<TokenIdentifierUpper> none = Option.none();
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

  public UASTIDType declarationType()
    throws ParserError,
      IOException,
      LexerError
  {
    this.parserConsumeExact(Type.TOKEN_TYPE);
    this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
    final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
    this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
    this.parserConsumeExact(Type.TOKEN_IS);
    this.parserExpectOneOf(new Type[] { Type.TOKEN_RECORD, });

    switch (this.token.getType()) {
      case TOKEN_RECORD:
        this.parserConsumeExact(Type.TOKEN_RECORD);
        final List<UASTIDTypeRecordField> fields =
          this.declarationTypeRecordFields();
        this.parserConsumeExact(Type.TOKEN_END);
        return new UASTIDTypeRecord(name, fields);
        // $CASES-OMITTED$
      default:
        throw new UnreachableCodeException();
    }
  }

  public UASTITypePath declarationTypePath()
    throws ParserError,
      IOException,
      LexerError
  {
    this.parserExpectOneOf(new Type[] {
      Type.TOKEN_IDENTIFIER_LOWER,
      Type.TOKEN_IDENTIFIER_UPPER, });

    switch (this.token.getType()) {
      case TOKEN_IDENTIFIER_LOWER:
      {
        final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
        this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
        final OptionType<TokenIdentifierUpper> none = Option.none();
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

  public UASTIDTypeRecordField declarationTypeRecordField()
    throws ParserError,
      IOException,
      LexerError
  {
    this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
    final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
    this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
    this.parserConsumeExact(Type.TOKEN_COLON);
    final UASTITypePath type = this.declarationTypePath();
    return new UASTIDTypeRecordField(name, type);
  }

  public List<UASTIDTypeRecordField> declarationTypeRecordFields()
    throws ParserError,
      IOException,
      LexerError
  {
    final List<UASTIDTypeRecordField> args =
      new ArrayList<UASTIDTypeRecordField>();
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

  public UASTIDValue declarationValue()
    throws ParserError,
      IOException,
      LexerError
  {
    this.parserConsumeExact(Type.TOKEN_VALUE);
    this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
    final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
    this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
    this
      .parserExpectOneOf(new Type[] { Type.TOKEN_COLON, Type.TOKEN_EQUALS, });

    final OptionType<UASTITypePath> ascription;
    if (this.token.getType() == Type.TOKEN_COLON) {
      this.parserConsumeExact(Type.TOKEN_COLON);
      final UASTITypePath path = this.declarationTypePath();
      ascription = Option.some(path);
    } else {
      ascription = Option.none();
    }

    this.parserExpectExact(Type.TOKEN_EQUALS);
    this.parserConsumeExact(Type.TOKEN_EQUALS);

    if (this.token.getType() == Type.TOKEN_EXTERNAL) {

      /**
       * If parsing an "internal" unit, then allow "external" values.
       */

      if (this.internal) {
        final UASTIDExternal ext = this.declarationExternal();
        return new UASTIDValueExternal(name, ascription, ext);
      }

      /**
       * Otherwise, attempt to parse "external" as an expression, which will
       * result in an error.
       */

      return new UASTIDValueDefined(name, ascription, this.expression());
    }

    return new UASTIDValueDefined(name, ascription, this.expression());
  }

  public UASTIDValueLocal declarationValueLocal()
    throws ParserError,
      IOException,
      LexerError
  {
    this.parserConsumeExact(Type.TOKEN_VALUE);
    this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
    final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
    this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
    this
      .parserExpectOneOf(new Type[] { Type.TOKEN_COLON, Type.TOKEN_EQUALS, });

    switch (this.token.getType()) {
      case TOKEN_COLON:
      {
        this.parserConsumeExact(Type.TOKEN_COLON);
        final UASTITypePath path = this.declarationTypePath();
        this.parserConsumeExact(Type.TOKEN_EQUALS);
        return new UASTIDValueLocal(
          name,
          Option.some(path),
          this.expression());
      }
      case TOKEN_EQUALS:
      {
        this.parserConsumeExact(Type.TOKEN_EQUALS);
        final OptionType<UASTITypePath> none = Option.none();
        return new UASTIDValueLocal(name, none, this.expression());
      }
      // $CASES-OMITTED$
      default:
        throw new UnreachableCodeException();
    }
  }

  public List<UASTIDValueLocal> declarationValueLocals()
    throws ParserError,
      IOException,
      LexerError
  {
    final List<UASTIDValueLocal> values = new ArrayList<UASTIDValueLocal>();
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

  public UASTIValuePath declarationValuePath()
    throws ParserError,
      IOException,
      LexerError
  {
    this.parserExpectOneOf(new Type[] {
      Type.TOKEN_IDENTIFIER_LOWER,
      Type.TOKEN_IDENTIFIER_UPPER, });

    switch (this.token.getType()) {
      case TOKEN_IDENTIFIER_LOWER:
      {
        final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
        this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
        final OptionType<TokenIdentifierUpper> none = Option.none();
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

  public UASTIDShaderVertex declarationVertexShader()
    throws ParserError,
      IOException,
      LexerError
  {
    this.parserConsumeExact(Type.TOKEN_VERTEX);
    this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
    final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
    this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
    this.parserConsumeExact(Type.TOKEN_IS);

    final List<UASTIDShaderVertexParameters> decls =
      this.declarationVertexShaderParameterDeclarations();

    final List<UASTIDShaderVertexInput> inputs =
      new ArrayList<UASTIDShaderVertexInput>();
    final List<UASTIDShaderVertexOutput> outputs =
      new ArrayList<UASTIDShaderVertexOutput>();
    final List<UASTIDShaderVertexParameter> parameters =
      new ArrayList<UASTIDShaderVertexParameter>();

    for (int index = 0; index < decls.size(); ++index) {
      final UASTIDShaderVertexParameters d = decls.get(index);
      if (d instanceof UASTIDShaderVertexInput) {
        inputs.add((UASTIDShaderVertexInput) d);
        continue;
      }
      if (d instanceof UASTIDShaderVertexOutput) {
        outputs.add((UASTIDShaderVertexOutput) d);
        continue;
      }
      if (d instanceof UASTIDShaderVertexParameter) {
        parameters.add((UASTIDShaderVertexParameter) d);
        continue;
      }
    }

    this.parserExpectOneOf(new Type[] { Type.TOKEN_WITH, Type.TOKEN_AS, });

    final List<UASTIDValueLocal> values;
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
        values = new ArrayList<UASTIDValueLocal>();
        break;
      }
    }

    this.parserConsumeExact(Type.TOKEN_AS);
    final List<UASTIDShaderVertexOutputAssignment> assigns =
      this.declarationVertexShaderOutputAssignments();
    this.parserConsumeExact(Type.TOKEN_END);

    final List<UASTIDShaderVertexLocalValue> actual_locals =
      new ArrayList<UASTIDShaderVertexLocalValue>(values.size());
    for (int index = 0; index < values.size(); ++index) {
      final UASTIDValueLocal r = values.get(index);
      assert r != null;
      actual_locals.add(index, new UASTIDShaderVertexLocalValue(r));
    }

    return new UASTIDShaderVertex(
      name,
      inputs,
      outputs,
      parameters,
      actual_locals,
      assigns);
  }

  public UASTIDShaderVertexInput declarationVertexShaderInput()
    throws ParserError,
      IOException,
      LexerError
  {
    this.parserConsumeExact(Type.TOKEN_IN);
    this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
    final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
    this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
    this.parserConsumeExact(Type.TOKEN_COLON);
    return new UASTIDShaderVertexInput(name, this.declarationTypePath());
  }

  public UASTIDShaderVertexOutput declarationVertexShaderOutput()
    throws ParserError,
      IOException,
      LexerError
  {
    this.parserConsumeExact(Type.TOKEN_OUT);
    this.parserExpectOneOf(new Type[] {
      Type.TOKEN_IDENTIFIER_LOWER,
      Type.TOKEN_VERTEX, });

    switch (this.token.getType()) {
      case TOKEN_IDENTIFIER_LOWER:
      {
        final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
        this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
        this.parserConsumeExact(Type.TOKEN_COLON);
        return new UASTIDShaderVertexOutput(
          name,
          this.declarationTypePath(),
          false);
      }
      case TOKEN_VERTEX:
      {
        this.parserConsumeExact(Type.TOKEN_VERTEX);
        this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
        final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
        this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
        this.parserConsumeExact(Type.TOKEN_COLON);
        return new UASTIDShaderVertexOutput(
          name,
          this.declarationTypePath(),
          true);
      }
      // $CASES-OMITTED$
      default:
        throw new UnreachableCodeException();
    }
  }

  public
    UASTIDShaderVertexOutputAssignment
    declarationVertexShaderOutputAssignment()
      throws ParserError,
        IOException,
        LexerError
  {
    this.parserConsumeExact(Type.TOKEN_OUT);
    this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
    final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
    this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
    this.parserConsumeExact(Type.TOKEN_EQUALS);
    final UASTIEVariable value =
      new UASTIEVariable(this.declarationValuePath());
    return new UASTIDShaderVertexOutputAssignment(name, value);
  }

  public
    List<UASTIDShaderVertexOutputAssignment>
    declarationVertexShaderOutputAssignments()
      throws ParserError,
        IOException,
        LexerError
  {
    final List<UASTIDShaderVertexOutputAssignment> assigns =
      new ArrayList<UASTIDShaderVertexOutputAssignment>();

    assigns.add(this.declarationVertexShaderOutputAssignment());
    this.parserConsumeExact(Type.TOKEN_SEMICOLON);

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

  public UASTIDShaderVertexParameter declarationVertexShaderParameter()
    throws ParserError,
      IOException,
      LexerError
  {
    this.parserConsumeExact(Type.TOKEN_PARAMETER);
    this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
    final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
    this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
    this.parserConsumeExact(Type.TOKEN_COLON);
    return new UASTIDShaderVertexParameter(name, this.declarationTypePath());
  }

  public
    UASTIDShaderVertexParameters
    declarationVertexShaderParameterDeclaration()
      throws ParserError,
        IOException,
        LexerError
  {
    this.parserExpectOneOf(new Type[] {
      Type.TOKEN_IN,
      Type.TOKEN_OUT,
      Type.TOKEN_PARAMETER, });

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

  public
    List<UASTIDShaderVertexParameters>
    declarationVertexShaderParameterDeclarations()
      throws ParserError,
        IOException,
        LexerError
  {
    final List<UASTIDShaderVertexParameters> declarations =
      new ArrayList<UASTIDShaderVertexParameters>();

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

  public UASTIExpression expression()
    throws ParserError,
      IOException,
      LexerError
  {
    return this.expressionPost(this.expressionPre());
  }

  public List<UASTIExpression> expressionApplicationArguments()
    throws ParserError,
      IOException,
      LexerError
  {
    this.parserConsumeExact(Type.TOKEN_ROUND_LEFT);
    final List<UASTIExpression> arguments = new ArrayList<UASTIExpression>();
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

  public UASTIEBoolean expressionBoolean()
    throws ParserError,
      IOException,
      LexerError
  {
    this.parserExpectExact(Type.TOKEN_LITERAL_BOOLEAN);
    final UASTIEBoolean t =
      new UASTIEBoolean((TokenLiteralBoolean) this.token);
    this.parserConsumeAny();
    return t;
  }

  public UASTIEConditional expressionConditional()
    throws ParserError,
      IOException,
      LexerError
  {
    this.parserExpectExact(Type.TOKEN_IF);
    final TokenIf tif = (TokenIf) this.token;
    this.parserConsumeExact(Type.TOKEN_IF);
    final UASTIExpression econd = this.expression();
    this.parserConsumeExact(Type.TOKEN_THEN);
    final UASTIExpression eleft = this.expression();
    this.parserConsumeExact(Type.TOKEN_ELSE);
    final UASTIExpression eright = this.expression();
    this.parserConsumeExact(Type.TOKEN_END);
    return new UASTIEConditional(tif, econd, eleft, eright);
  }

  public UASTIEInteger expressionInteger()
    throws ParserError,
      IOException,
      LexerError
  {
    this.parserExpectExact(Type.TOKEN_LITERAL_INTEGER_DECIMAL);
    final UASTIEInteger t =
      new UASTIEInteger((TokenLiteralInteger) this.token);
    this.parserConsumeAny();
    return t;
  }

  public UASTIELet expressionLet()
    throws ParserError,
      IOException,
      LexerError
  {
    this.parserExpectExact(Type.TOKEN_LET);
    final TokenLet let = (TokenLet) this.token;
    this.parserConsumeExact(Type.TOKEN_LET);
    final List<UASTIDValueLocal> bindings = this.declarationValueLocals();
    this.parserConsumeExact(Type.TOKEN_IN);
    final UASTIExpression body = this.expression();
    this.parserConsumeExact(Type.TOKEN_END);
    return new UASTIELet(let, bindings, body);
  }

  public UASTIEMatrixColumnAccess expressionMatrixColumnAccess()
    throws ParserError,
      IOException,
      LexerError
  {
    this.parserConsumeExact(Type.TOKEN_COLUMN);
    final UASTIExpression e = this.expression();
    final UASTIEInteger column = this.expressionInteger();
    return new UASTIEMatrixColumnAccess(e, column.getToken());
  }

  public UASTIENew expressionNew()
    throws ParserError,
      IOException,
      LexerError
  {
    this.parserConsumeExact(Type.TOKEN_NEW);
    final UASTITypePath path = this.declarationTypePath();
    return new UASTIENew(path, this.expressionApplicationArguments());
  }

  private UASTIExpression expressionPost(
    final UASTIExpression e)
    throws ParserError,
      IOException,
      LexerError
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

  private UASTIExpression expressionPre()
    throws ParserError,
      IOException,
      LexerError
  {
    this.parserExpectOneOf(new Type[] {
      Type.TOKEN_LITERAL_INTEGER_DECIMAL,
      Type.TOKEN_LITERAL_BOOLEAN,
      Type.TOKEN_LITERAL_REAL,
      Type.TOKEN_IDENTIFIER_LOWER,
      Type.TOKEN_IDENTIFIER_UPPER,
      Type.TOKEN_COLUMN,
      Type.TOKEN_IF,
      Type.TOKEN_LET,
      Type.TOKEN_NEW,
      Type.TOKEN_RECORD, });

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
      case TOKEN_COLUMN:
        return this.expressionMatrixColumnAccess();
        // $CASES-OMITTED$
      default:
        throw new UnreachableCodeException();
    }
  }

  public UASTIEReal expressionReal()
    throws ParserError,
      IOException,
      LexerError
  {
    this.parserExpectExact(Type.TOKEN_LITERAL_REAL);
    final UASTIEReal t = new UASTIEReal((TokenLiteralReal) this.token);
    this.parserConsumeAny();
    return t;
  }

  public UASTIERecord expressionRecord()
    throws ParserError,
      IOException,
      LexerError
  {
    this.parserConsumeExact(Type.TOKEN_RECORD);
    final UASTITypePath path = this.declarationTypePath();
    this.parserConsumeExact(Type.TOKEN_CURLY_LEFT);
    final List<UASTIRecordFieldAssignment> fields =
      new ArrayList<UASTIRecordFieldAssignment>();
    fields.add(this.expressionRecordFieldAssignment());
    this.expressionRecordActual(fields);
    this.parserConsumeExact(Type.TOKEN_CURLY_RIGHT);
    return new UASTIERecord(path, fields);
  }

  private void expressionRecordActual(
    final List<UASTIRecordFieldAssignment> fields)
    throws ParserError,
      IOException,
      LexerError
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

  public UASTIRecordFieldAssignment expressionRecordFieldAssignment()
    throws ParserError,
      IOException,
      LexerError
  {
    this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
    final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
    this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
    this.parserConsumeExact(Type.TOKEN_EQUALS);
    return new UASTIRecordFieldAssignment(name, this.expression());
  }

  public UASTIERecordProjection expressionRecordProjection(
    final UASTIExpression e)
    throws ParserError,
      IOException,
      LexerError
  {
    this.parserConsumeExact(Type.TOKEN_DOT);
    this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
    final UASTIERecordProjection r =
      new UASTIERecordProjection(e, (TokenIdentifierLower) this.token);
    this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
    return r;
  }

  public UASTIESwizzle expressionSwizzle(
    final UASTIExpression e)
    throws ParserError,
      IOException,
      LexerError
  {
    this.parserConsumeExact(Type.TOKEN_SQUARE_LEFT);

    final List<TokenIdentifierLower> fields =
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
    return new UASTIESwizzle(e, fields);
  }

  public TokenIdentifierLower expressionSwizzleField()
    throws ParserError,
      IOException,
      LexerError
  {
    this.parserExpectExact(Type.TOKEN_IDENTIFIER_LOWER);
    final TokenIdentifierLower name = (TokenIdentifierLower) this.token;
    this.parserConsumeExact(Type.TOKEN_IDENTIFIER_LOWER);
    return name;
  }

  public UASTIExpression expressionVariableOrApplication()
    throws ParserError,
      IOException,
      LexerError
  {
    final UASTIValuePath path = this.declarationValuePath();
    switch (this.token.getType()) {
      case TOKEN_ROUND_LEFT:
        return new UASTIEApplication(
          path,
          this.expressionApplicationArguments());
        // $CASES-OMITTED$
      default:
        return new UASTIEVariable(path);
    }
  }

  protected void parserConsumeAny()
    throws IOException,
      LexerError
  {
    this.token = this.lexer.token();
  }

  protected void parserConsumeExact(
    final Token.Type type)
    throws ParserError,
      IOException,
      LexerError
  {
    this.parserExpectExact(type);
    this.parserConsumeAny();
  }

  protected void parserExpectExact(
    final Token.Type type)
    throws ParserError
  {
    if (this.token.getType() != type) {
      this.message.setLength(0);
      this.message.append("Expected ");
      this.message.append(type.getDescription());
      this.message.append(" but got ");
      this.parserShowToken();
      final String r = this.message.toString();
      assert r != null;
      throw new ParserError(r, this.lexer.getFile(), this.token.getPosition());
    }
  }

  protected void parserExpectOneOf(
    final Token.Type[] types)
    throws ParserError
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
    final String r = this.message.toString();
    assert r != null;
    throw new ParserError(r, this.lexer.getFile(), this.token.getPosition());
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

  public void statementTerminate()
    throws ParserError,
      LexerError,
      IOException
  {
    this.parserConsumeExact(Type.TOKEN_SEMICOLON);
  }

  public UASTIUnit unit()
    throws ParserError,
      IOException,
      LexerError
  {
    final UASTIDPackage pack = this.declarationPackage();
    this.parserConsumeExact(Type.TOKEN_SEMICOLON);
    final List<UASTIDModule> modules =
      this.declarationModules(pack.getPath());
    return new UASTIUnit(this.lexer.getFile(), pack, modules);
  }

  private ParserError unknownProfile(
    final String profile_name)
  {
    this.message.setLength(0);
    this.message.append("Unknown GLSL profile '");
    this.message.append(profile_name);
    this.message.append("'");
    this.parserShowToken();
    final String r = this.message.toString();
    assert r != null;
    return new ParserError(r, this.lexer.getFile(), this.token.getPosition());
  }
}
