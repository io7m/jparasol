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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Option;
import com.io7m.jaux.functional.Option.Some;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.PackagePathFlat;
import com.io7m.jparasol.lexer.Lexer;
import com.io7m.jparasol.lexer.LexerError;
import com.io7m.jparasol.lexer.Token.TokenIdentifierUpper;
import com.io7m.jparasol.lexer.Token.Type;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDFunctionArgument;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDFunctionDefined;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDFunctionExternal;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDImport;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDPackage;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragment;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentInput;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentLocalDiscard;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentLocalValue;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentOutput;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentOutputAssignment;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentParameter;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderProgram;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertex;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertexInput;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertexOutput;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertexOutputAssignment;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertexParameter;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDTypeRecord;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDTypeRecordField;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDValue;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDValueLocal;
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
import com.io7m.jparasol.untyped.ast.initial.UASTITypePath;
import com.io7m.jparasol.untyped.ast.initial.UASTIUnchecked;
import com.io7m.jparasol.untyped.ast.initial.UASTIValuePath;

public class ParserTest
{
  @SuppressWarnings("resource") static Parser makeResourceInternalParser(
    final String name)
    throws IOException,
      LexerError,
      ConstraintError
  {
    final InputStream is =
      ParserTest.class.getResourceAsStream("/com/io7m/jparasol/parser/"
        + name);
    final Lexer lexer = new Lexer(is);
    return Parser.newInternalParser(lexer);
  }

  @SuppressWarnings("resource") static Parser makeResourceParser(
    final String name)
    throws IOException,
      LexerError,
      ConstraintError
  {
    final InputStream is =
      ParserTest.class.getResourceAsStream("/com/io7m/jparasol/parser/"
        + name);
    final Lexer lexer = new Lexer(is);
    return Parser.newParser(lexer);
  }

  static Parser makeStringInternalParser(
    final String text)
    throws IOException,
      LexerError,
      ConstraintError
  {
    final ByteArrayInputStream bs = new ByteArrayInputStream(text.getBytes());
    final Lexer lexer = new Lexer(bs);
    return Parser.newInternalParser(lexer);
  }

  static Parser makeStringParser(
    final String text)
    throws IOException,
      LexerError,
      ConstraintError
  {
    final ByteArrayInputStream bs = new ByteArrayInputStream(text.getBytes());
    final Lexer lexer = new Lexer(bs);
    return Parser.newParser(lexer);
  }

  @SuppressWarnings("static-method") @Test public
    void
    testDFragmentShader_0()
      throws IOException,
        LexerError,
        ConstraintError,
        ParserError
  {
    final Parser p = ParserTest.makeResourceParser("testDFragmentShader0.p");
    final UASTIDShaderFragment<UASTIUnchecked> r =
      p.declarationFragmentShader();
    Assert.assertEquals("f", r.getName().getActual());

    Assert.assertEquals(1, r.getParameters().size());
    {
      final UASTIDShaderFragmentParameter<UASTIUnchecked> rp =
        r.getParameters().get(0);
      Assert.assertEquals("x", rp.getName().getActual());
      Assert.assertTrue(rp.getType().getModule().isNone());
      Assert.assertEquals("integer", rp.getType().getName().getActual());
    }

    Assert.assertEquals(2, r.getInputs().size());
    {
      final UASTIDShaderFragmentInput<UASTIUnchecked> ri0 =
        r.getInputs().get(0);
      Assert.assertEquals("mmv", ri0.getName().getActual());
      Assert.assertTrue(ri0.getType().getModule().isNone());
      Assert.assertEquals("matrix_4x4f", ri0.getType().getName().getActual());

      final UASTIDShaderFragmentInput<UASTIUnchecked> ri1 =
        r.getInputs().get(1);
      Assert.assertEquals("in_pos", ri1.getName().getActual());
      Assert.assertTrue(ri1.getType().getModule().isNone());
      Assert.assertEquals("vector_4f", ri1.getType().getName().getActual());
    }

    Assert.assertEquals(2, r.getOutputs().size());
    {
      final UASTIDShaderFragmentOutput<UASTIUnchecked> ro0 =
        r.getOutputs().get(0);
      Assert.assertEquals("out_pos", ro0.getName().getActual());
      Assert.assertEquals(0, ro0.getIndex());
      Assert.assertEquals("vector_4f", ro0.getType().getName().getActual());

      final UASTIDShaderFragmentOutput<UASTIUnchecked> ro1 =
        r.getOutputs().get(1);
      Assert.assertEquals("out_pos2", ro1.getName().getActual());
      Assert.assertEquals(1, ro1.getIndex());
      Assert.assertEquals("vector_4f", ro1.getType().getName().getActual());
    }

    Assert.assertEquals(2, r.getLocals().size());
    {
      final UASTIDShaderFragmentLocalValue<UASTIUnchecked> rl0 =
        (UASTIDShaderFragmentLocalValue<UASTIUnchecked>) r.getLocals().get(0);
      Assert.assertEquals("pp", rl0.getValue().getName().getActual());
      Assert.assertTrue(rl0.getValue().getAscription().isNone());

      @SuppressWarnings("unused") final UASTIDShaderFragmentLocalDiscard<UASTIUnchecked> rl1 =
        (UASTIDShaderFragmentLocalDiscard<UASTIUnchecked>) r.getLocals().get(
          1);
    }

    Assert.assertEquals(2, r.getWrites().size());
    {
      final UASTIDShaderFragmentOutputAssignment<UASTIUnchecked> w0 =
        r.getWrites().get(0);
      Assert.assertEquals("out_pos", w0.getName().getActual());
      Assert.assertEquals("pp", w0
        .getVariable()
        .getName()
        .getName()
        .getActual());

      final UASTIDShaderFragmentOutputAssignment<UASTIUnchecked> w1 =
        r.getWrites().get(1);
      Assert.assertEquals("out_pos2", w1.getName().getActual());
      Assert.assertEquals("pp", w1
        .getVariable()
        .getName()
        .getName()
        .getActual());
    }
  }

  @SuppressWarnings("static-method") @Test public
    void
    testDFragmentShader_1()
      throws IOException,
        LexerError,
        ConstraintError,
        ParserError
  {
    final Parser p = ParserTest.makeResourceParser("testDFragmentShader1.p");
    final UASTIDShaderFragment<UASTIUnchecked> r =
      p.declarationFragmentShader();
    Assert.assertEquals("f", r.getName().getActual());

    Assert.assertEquals(1, r.getParameters().size());
    {
      final UASTIDShaderFragmentParameter<UASTIUnchecked> rp =
        r.getParameters().get(0);
      Assert.assertEquals("x", rp.getName().getActual());
      Assert.assertTrue(rp.getType().getModule().isNone());
      Assert.assertEquals("integer", rp.getType().getName().getActual());
    }

    Assert.assertEquals(2, r.getInputs().size());
    {
      final UASTIDShaderFragmentInput<UASTIUnchecked> ri0 =
        r.getInputs().get(0);
      Assert.assertEquals("mmv", ri0.getName().getActual());
      Assert.assertTrue(ri0.getType().getModule().isNone());
      Assert.assertEquals("matrix_4x4f", ri0.getType().getName().getActual());

      final UASTIDShaderFragmentInput<UASTIUnchecked> ri1 =
        r.getInputs().get(1);
      Assert.assertEquals("in_pos", ri1.getName().getActual());
      Assert.assertTrue(ri1.getType().getModule().isNone());
      Assert.assertEquals("vector_4f", ri1.getType().getName().getActual());
    }

    Assert.assertEquals(2, r.getOutputs().size());
    {
      final UASTIDShaderFragmentOutput<UASTIUnchecked> ro0 =
        r.getOutputs().get(0);
      Assert.assertEquals("out_pos", ro0.getName().getActual());
      Assert.assertEquals(0, ro0.getIndex());
      Assert.assertEquals("vector_4f", ro0.getType().getName().getActual());

      final UASTIDShaderFragmentOutput<UASTIUnchecked> ro1 =
        r.getOutputs().get(1);
      Assert.assertEquals("out_pos2", ro1.getName().getActual());
      Assert.assertEquals(1, ro1.getIndex());
      Assert.assertEquals("vector_4f", ro1.getType().getName().getActual());
    }

    Assert.assertEquals(2, r.getWrites().size());
    {
      final UASTIDShaderFragmentOutputAssignment<UASTIUnchecked> w0 =
        r.getWrites().get(0);
      Assert.assertEquals("out_pos", w0.getName().getActual());
      Assert.assertEquals("in_pos", w0
        .getVariable()
        .getName()
        .getName()
        .getActual());

      final UASTIDShaderFragmentOutputAssignment<UASTIUnchecked> w1 =
        r.getWrites().get(1);
      Assert.assertEquals("out_pos2", w1.getName().getActual());
      Assert.assertEquals("in_pos", w1
        .getVariable()
        .getName()
        .getName()
        .getActual());
    }
  }

  @SuppressWarnings("static-method") @Test public
    void
    testDFragmentShaderInput_0()
      throws IOException,
        LexerError,
        ConstraintError,
        ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("in x : integer");
    final UASTIDShaderFragmentInput<UASTIUnchecked> r =
      p.declarationFragmentShaderInput();
    Assert.assertEquals("x", r.getName().getActual());
    Assert.assertEquals("integer", r.getType().getName().getActual());
  }

  @SuppressWarnings("static-method") @Test public
    void
    testDFragmentShaderOutput_0()
      throws IOException,
        LexerError,
        ConstraintError,
        ParserError
  {
    final Parser p =
      ParserTest.makeStringInternalParser("out x : integer as 23");
    final UASTIDShaderFragmentOutput<UASTIUnchecked> r =
      p.declarationFragmentShaderOutput();
    Assert.assertEquals("x", r.getName().getActual());
    Assert.assertEquals("integer", r.getType().getName().getActual());
    Assert.assertEquals(23, r.getIndex());
  }

  @SuppressWarnings("static-method") @Test public
    void
    testDFragmentShaderParameter_0()
      throws IOException,
        LexerError,
        ConstraintError,
        ParserError
  {
    final Parser p =
      ParserTest.makeStringInternalParser("parameter x : integer");
    final UASTIDShaderFragmentParameter<UASTIUnchecked> r =
      p.declarationFragmentShaderParameter();
    Assert.assertEquals("x", r.getName().getActual());
    Assert.assertEquals("integer", r.getType().getName().getActual());
  }

  @SuppressWarnings({ "static-method" }) @Test public
    void
    testDFunctionExternal_1()
      throws IOException,
        LexerError,
        ConstraintError,
        ParserError
  {
    final Parser p =
      ParserTest
        .makeStringInternalParser("function f (x : integer, y : Example.t) : integer = external xyz");

    final UASTIDFunctionExternal<UASTIUnchecked> r =
      (UASTIDFunctionExternal<UASTIUnchecked>) p.declarationFunction();
    Assert.assertEquals("f", r.getName().getActual());
    Assert.assertEquals(2, r.getArguments().size());

    final UASTIDFunctionArgument<UASTIUnchecked> arg0 =
      r.getArguments().get(0);
    final UASTIDFunctionArgument<UASTIUnchecked> arg1 =
      r.getArguments().get(1);

    Assert.assertEquals("x", arg0.getName().getActual());
    Assert.assertEquals("integer", arg0.getType().getName().getActual());
    Assert.assertEquals("y", arg1.getName().getActual());
    Assert.assertEquals("Example", ((Option.Some<TokenIdentifierUpper>) arg1
      .getType()
      .getModule()).value.getActual());
    Assert.assertEquals("t", arg1.getType().getName().getActual());
    Assert.assertEquals("xyz", r.getExternal().getActual());
  }

  @SuppressWarnings({ "static-method" }) @Test(expected = ParserError.class) public
    void
    testDFunctionNotExternal_0()
      throws IOException,
        LexerError,
        ConstraintError,
        ParserError
  {
    final Parser p =
      ParserTest
        .makeStringParser("function f (x : integer, y : Example.t) : integer = external xyz");
    p.declarationFunction();
  }

  @SuppressWarnings({ "static-method" }) @Test public
    void
    testDFunctionOK_0()
      throws IOException,
        LexerError,
        ConstraintError,
        ParserError
  {
    final Parser p =
      ParserTest
        .makeStringInternalParser("function f (x : integer, y : Example.t) : integer = 23");
    final UASTIDFunctionDefined<UASTIUnchecked> r =
      (UASTIDFunctionDefined<UASTIUnchecked>) p.declarationFunction();
    Assert.assertEquals("f", r.getName().getActual());
    Assert.assertEquals(2, r.getArguments().size());

    final UASTIDFunctionArgument<UASTIUnchecked> arg0 =
      r.getArguments().get(0);
    final UASTIDFunctionArgument<UASTIUnchecked> arg1 =
      r.getArguments().get(1);

    Assert.assertEquals("x", arg0.getName().getActual());
    Assert.assertEquals("integer", arg0.getType().getName().getActual());
    Assert.assertEquals("y", arg1.getName().getActual());
    Assert.assertEquals("Example", ((Option.Some<TokenIdentifierUpper>) arg1
      .getType()
      .getModule()).value.getActual());
    Assert.assertEquals("t", arg1.getType().getName().getActual());
    Assert.assertEquals(23, ((UASTIEInteger<UASTIUnchecked>) r.getBody())
      .getValue()
      .intValue());
  }

  @SuppressWarnings({ "static-method" }) @Test public void testDImportOK_0()
    throws IOException,
      LexerError,
      ConstraintError,
      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("import x.y.z.M");

    final UASTIDImport<UASTIUnchecked> r = p.declarationImport();
    final ModulePathFlat flat = ModulePathFlat.fromModulePath(r.getPath());
    Assert.assertEquals("x.y.z.M", flat.getActual());
    Assert.assertTrue(r.getRename().isNone());
  }

  @SuppressWarnings({ "static-method" }) @Test public void testDImportOK_1()
    throws IOException,
      LexerError,
      ConstraintError,
      ParserError
  {
    final Parser p =
      ParserTest.makeStringInternalParser("import x.y.z.M as K");

    final UASTIDImport<UASTIUnchecked> r = p.declarationImport();
    final ModulePathFlat flat = ModulePathFlat.fromModulePath(r.getPath());
    Assert.assertEquals("x.y.z.M", flat.getActual());
    Assert.assertTrue(r.getRename().isSome());
    final Some<TokenIdentifierUpper> some =
      (Option.Some<TokenIdentifierUpper>) r.getRename();
    Assert.assertEquals("K", some.value.getActual());
  }

  @SuppressWarnings({ "static-method" }) @Test public void testDImportOK_2()
    throws IOException,
      LexerError,
      ConstraintError,
      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("import M");
    final UASTIDImport<UASTIUnchecked> r = p.declarationImport();
    final ModulePathFlat flat = ModulePathFlat.fromModulePath(r.getPath());
    Assert.assertEquals("M", flat.getActual());
    Assert.assertTrue(r.getRename().isNone());
  }

  @SuppressWarnings({ "static-method" }) @Test public void testDImportOK_3()
    throws IOException,
      LexerError,
      ConstraintError,
      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("import M as K");
    final UASTIDImport<UASTIUnchecked> r = p.declarationImport();
    final ModulePathFlat flat = ModulePathFlat.fromModulePath(r.getPath());
    Assert.assertEquals("M", flat.getActual());
    Assert.assertTrue(r.getRename().isSome());
    final Some<TokenIdentifierUpper> some =
      (Option.Some<TokenIdentifierUpper>) r.getRename();
    Assert.assertEquals("K", some.value.getActual());
  }

  @SuppressWarnings({ "static-method" }) @Test public void testDPackageOK_0()
    throws IOException,
      LexerError,
      ConstraintError,
      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("package x.y.z");

    final UASTIDPackage<UASTIUnchecked> r = p.declarationPackage();
    final PackagePathFlat flat = PackagePathFlat.fromPackagePath(r.getPath());
    Assert.assertEquals("x.y.z", flat.getActual());
  }

  @SuppressWarnings("static-method") @Test public void testDProgramShader_0()
    throws IOException,
      LexerError,
      ConstraintError,
      ParserError
  {
    final Parser p = ParserTest.makeResourceParser("testDProgramShader0.p");
    final UASTIDShaderProgram<UASTIUnchecked> r =
      p.declarationProgramShader();

    Assert.assertEquals("p", r.getName().getActual());
    Assert.assertEquals("x", r.getVertexShader().getName().getActual());
    Assert.assertEquals("y", r.getFragmentShader().getName().getActual());
  }

  @SuppressWarnings({ "static-method" }) @Test(expected = ParserError.class) public
    void
    testDRecordNotOK_0()
      throws IOException,
        LexerError,
        ConstraintError,
        ParserError
  {
    final Parser p = ParserTest.makeStringParser("type t is record end");
    p.declarationType();
  }

  @SuppressWarnings({ "static-method" }) @Test public void testDRecordOK_0()
    throws IOException,
      LexerError,
      ConstraintError,
      ParserError
  {
    final Parser p =
      ParserTest
        .makeStringParser("type t is record x : integer, y : Z.integer end");

    final UASTIDTypeRecord<UASTIUnchecked> r =
      (UASTIDTypeRecord<UASTIUnchecked>) p.declarationType();
    Assert.assertEquals("t", r.getName().getActual());
    Assert.assertEquals(2, r.getFields().size());

    final UASTIDTypeRecordField<UASTIUnchecked> arg0 = r.getFields().get(0);
    final UASTIDTypeRecordField<UASTIUnchecked> arg1 = r.getFields().get(1);

    Assert.assertEquals("x", arg0.getName().getActual());
    Assert.assertEquals("integer", arg0.getType().getName().getActual());
    Assert.assertEquals("y", arg1.getName().getActual());
    Assert.assertEquals("Z", ((Option.Some<TokenIdentifierUpper>) arg1
      .getType()
      .getModule()).value.getActual());
    Assert.assertEquals("integer", arg1.getType().getName().getActual());
  }

  @SuppressWarnings("static-method") @Test public void testDShader_0()
    throws IOException,
      LexerError,
      ConstraintError,
      ParserError
  {
    final Parser p = ParserTest.makeResourceParser("testDShader0.p");

    final UASTIDShaderFragment<UASTIUnchecked> r0 =
      (UASTIDShaderFragment<UASTIUnchecked>) p.declarationShader();
    p.parserConsumeExact(Type.TOKEN_SEMICOLON);

    final UASTIDShaderVertex<UASTIUnchecked> r1 =
      (UASTIDShaderVertex<UASTIUnchecked>) p.declarationShader();
    p.parserConsumeExact(Type.TOKEN_SEMICOLON);

    final UASTIDShaderProgram<UASTIUnchecked> r2 =
      (UASTIDShaderProgram<UASTIUnchecked>) p.declarationShader();
    p.parserConsumeExact(Type.TOKEN_SEMICOLON);

    Assert.assertEquals("f", r0.getName().getActual());
    Assert.assertEquals("v", r1.getName().getActual());
    Assert.assertEquals("p", r2.getName().getActual());
  }

  @SuppressWarnings({ "static-method" }) @Test public
    void
    testDTypePathOK_0()
      throws IOException,
        LexerError,
        ConstraintError,
        ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("X.y");
    final UASTITypePath r = p.declarationTypePath();
    Assert.assertTrue(r.getModule().isSome());
    final Option.Some<TokenIdentifierUpper> some =
      (Option.Some<TokenIdentifierUpper>) r.getModule();
    Assert.assertEquals("X", some.value.getActual());
    Assert.assertEquals("y", r.getName().getActual());
  }

  @SuppressWarnings({ "static-method" }) @Test public
    void
    testDTypePathOK_1()
      throws IOException,
        LexerError,
        ConstraintError,
        ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("y");
    final UASTITypePath r = p.declarationTypePath();
    Assert.assertTrue(r.getModule().isNone());
    Assert.assertEquals("y", r.getName().getActual());
  }

  @SuppressWarnings({ "static-method" }) @Test(expected = ParserError.class) public
    void
    testDValueNotOK_0()
      throws IOException,
        LexerError,
        ConstraintError,
        ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("value K = 23");
    p.declarationValue();
  }

  @SuppressWarnings({ "static-method" }) @Test(expected = ParserError.class) public
    void
    testDValueNotOK_1()
      throws IOException,
        LexerError,
        ConstraintError,
        ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("value k.K = 23");
    p.declarationValue();
  }

  @SuppressWarnings({ "static-method" }) @Test(expected = ParserError.class) public
    void
    testDValueNotOK_2()
      throws IOException,
        LexerError,
        ConstraintError,
        ParserError
  {
    final Parser p =
      ParserTest.makeStringInternalParser("value k : f.G = 23");
    p.declarationValue();
  }

  @SuppressWarnings({ "static-method" }) @Test(expected = ParserError.class) public
    void
    testDValueNotOK_3()
      throws IOException,
        LexerError,
        ConstraintError,
        ParserError
  {
    final Parser p =
      ParserTest.makeStringInternalParser("value k : F.G = 23");
    p.declarationValue();
  }

  @SuppressWarnings({ "static-method" }) @Test(expected = ParserError.class) public
    void
    testDValueNotOK_4()
      throws IOException,
        LexerError,
        ConstraintError,
        ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("value k : G = 23");
    p.declarationValue();
  }

  @SuppressWarnings({ "static-method" }) @Test public void testDValueOK_0()
    throws IOException,
      LexerError,
      ConstraintError,
      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("value k = 23");
    final UASTIDValue<UASTIUnchecked> r = p.declarationValue();
    Assert.assertEquals("k", r.getName().getActual());
    Assert.assertTrue(r.getAscription().isNone());
    Assert.assertEquals(23, ((UASTIEInteger<UASTIUnchecked>) r
      .getExpression()).getValue().intValue());
  }

  @SuppressWarnings({ "static-method" }) @Test public void testDValueOK_1()
    throws IOException,
      LexerError,
      ConstraintError,
      ParserError
  {
    final Parser p =
      ParserTest.makeStringInternalParser("value k : integer = 23");
    final UASTIDValue<UASTIUnchecked> r = p.declarationValue();
    Assert.assertEquals("k", r.getName().getActual());
    Assert.assertTrue(r.getAscription().isSome());

    final UASTITypePath path =
      ((Option.Some<UASTITypePath>) r.getAscription()).value;
    Assert.assertTrue(path.getModule().isNone());
    Assert.assertEquals("integer", path.getName().getActual());
    Assert.assertEquals(23, ((UASTIEInteger<UASTIUnchecked>) r
      .getExpression()).getValue().intValue());
  }

  @SuppressWarnings({ "static-method" }) @Test public void testDValueOK_2()
    throws IOException,
      LexerError,
      ConstraintError,
      ParserError
  {
    final Parser p =
      ParserTest.makeStringInternalParser("value k : P.integer = 23");
    final UASTIDValue<UASTIUnchecked> r = p.declarationValue();
    Assert.assertEquals("k", r.getName().getActual());
    Assert.assertTrue(r.getAscription().isSome());

    final UASTITypePath path =
      ((Option.Some<UASTITypePath>) r.getAscription()).value;
    Assert.assertTrue(path.getModule().isSome());
    final Some<TokenIdentifierUpper> module =
      (Option.Some<TokenIdentifierUpper>) path.getModule();
    Assert.assertEquals("P", module.value.getActual());

    Assert.assertEquals("integer", path.getName().getActual());
    Assert.assertEquals(23, ((UASTIEInteger<UASTIUnchecked>) r
      .getExpression()).getValue().intValue());
  }

  @SuppressWarnings({ "static-method" }) @Test public
    void
    testDValuePathOK_0()
      throws IOException,
        LexerError,
        ConstraintError,
        ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("X.y");
    final UASTIValuePath r = p.declarationValuePath();
    Assert.assertTrue(r.getModule().isSome());
    final Option.Some<TokenIdentifierUpper> some =
      (Option.Some<TokenIdentifierUpper>) r.getModule();
    Assert.assertEquals("X", some.value.getActual());
    Assert.assertEquals("y", r.getName().getActual());
  }

  @SuppressWarnings({ "static-method" }) @Test public
    void
    testDValuePathOK_1()
      throws IOException,
        LexerError,
        ConstraintError,
        ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("y");
    final UASTIValuePath r = p.declarationValuePath();
    Assert.assertTrue(r.getModule().isNone());
    Assert.assertEquals("y", r.getName().getActual());
  }

  @SuppressWarnings("static-method") @Test public void testDVertexShader_0()
    throws IOException,
      LexerError,
      ConstraintError,
      ParserError
  {
    final Parser p = ParserTest.makeResourceParser("testDVertexShader0.p");
    final UASTIDShaderVertex<UASTIUnchecked> r = p.declarationVertexShader();
    Assert.assertEquals("v", r.getName().getActual());

    Assert.assertEquals(1, r.getParameters().size());
    {
      final UASTIDShaderVertexParameter<UASTIUnchecked> rp =
        r.getParameters().get(0);
      Assert.assertEquals("x", rp.getName().getActual());
      Assert.assertTrue(rp.getType().getModule().isNone());
      Assert.assertEquals("integer", rp.getType().getName().getActual());
    }

    Assert.assertEquals(2, r.getInputs().size());
    {
      final UASTIDShaderVertexInput<UASTIUnchecked> ri0 =
        r.getInputs().get(0);
      Assert.assertEquals("mmv", ri0.getName().getActual());
      Assert.assertTrue(ri0.getType().getModule().isNone());
      Assert.assertEquals("matrix_4x4f", ri0.getType().getName().getActual());

      final UASTIDShaderVertexInput<UASTIUnchecked> ri1 =
        r.getInputs().get(1);
      Assert.assertEquals("in_pos", ri1.getName().getActual());
      Assert.assertTrue(ri1.getType().getModule().isNone());
      Assert.assertEquals("vector_4f", ri1.getType().getName().getActual());
    }

    Assert.assertEquals(2, r.getOutputs().size());
    {
      final UASTIDShaderVertexOutput<UASTIUnchecked> ro0 =
        r.getOutputs().get(0);
      Assert.assertEquals("out_pos", ro0.getName().getActual());
      Assert.assertEquals("vector_4f", ro0.getType().getName().getActual());

      final UASTIDShaderVertexOutput<UASTIUnchecked> ro1 =
        r.getOutputs().get(1);
      Assert.assertEquals("out_pos2", ro1.getName().getActual());
      Assert.assertEquals("vector_4f", ro1.getType().getName().getActual());
    }

    Assert.assertEquals(1, r.getValues().size());
    {
      final UASTIDValueLocal<UASTIUnchecked> rl0 = r.getValues().get(0);
      Assert.assertEquals("pp", rl0.getName().getActual());
      Assert.assertTrue(rl0.getAscription().isNone());
    }

    Assert.assertEquals(2, r.getWrites().size());
    {
      final UASTIDShaderVertexOutputAssignment<UASTIUnchecked> w0 =
        r.getWrites().get(0);
      Assert.assertEquals("out_pos", w0.getName().getActual());
      Assert.assertEquals("pp", w0
        .getVariable()
        .getName()
        .getName()
        .getActual());

      final UASTIDShaderVertexOutputAssignment<UASTIUnchecked> w1 =
        r.getWrites().get(1);
      Assert.assertEquals("out_pos2", w1.getName().getActual());
      Assert.assertEquals("pp", w1
        .getVariable()
        .getName()
        .getName()
        .getActual());
    }
  }

  @SuppressWarnings("static-method") @Test public void testDVertexShader_1()
    throws IOException,
      LexerError,
      ConstraintError,
      ParserError
  {
    final Parser p = ParserTest.makeResourceParser("testDVertexShader1.p");
    final UASTIDShaderVertex<UASTIUnchecked> r = p.declarationVertexShader();
    Assert.assertEquals("v", r.getName().getActual());

    Assert.assertEquals(1, r.getParameters().size());
    {
      final UASTIDShaderVertexParameter<UASTIUnchecked> rp =
        r.getParameters().get(0);
      Assert.assertEquals("x", rp.getName().getActual());
      Assert.assertTrue(rp.getType().getModule().isNone());
      Assert.assertEquals("integer", rp.getType().getName().getActual());
    }

    Assert.assertEquals(2, r.getInputs().size());
    {
      final UASTIDShaderVertexInput<UASTIUnchecked> ri0 =
        r.getInputs().get(0);
      Assert.assertEquals("mmv", ri0.getName().getActual());
      Assert.assertTrue(ri0.getType().getModule().isNone());
      Assert.assertEquals("matrix_4x4f", ri0.getType().getName().getActual());

      final UASTIDShaderVertexInput<UASTIUnchecked> ri1 =
        r.getInputs().get(1);
      Assert.assertEquals("in_pos", ri1.getName().getActual());
      Assert.assertTrue(ri1.getType().getModule().isNone());
      Assert.assertEquals("vector_4f", ri1.getType().getName().getActual());
    }

    Assert.assertEquals(2, r.getOutputs().size());
    {
      final UASTIDShaderVertexOutput<UASTIUnchecked> ro0 =
        r.getOutputs().get(0);
      Assert.assertEquals("out_pos", ro0.getName().getActual());
      Assert.assertEquals("vector_4f", ro0.getType().getName().getActual());

      final UASTIDShaderVertexOutput<UASTIUnchecked> ro1 =
        r.getOutputs().get(1);
      Assert.assertEquals("out_pos2", ro1.getName().getActual());
      Assert.assertEquals("vector_4f", ro1.getType().getName().getActual());
    }

    Assert.assertEquals(0, r.getValues().size());

    Assert.assertEquals(2, r.getWrites().size());
    {
      final UASTIDShaderVertexOutputAssignment<UASTIUnchecked> w0 =
        r.getWrites().get(0);
      Assert.assertEquals("out_pos", w0.getName().getActual());
      Assert.assertEquals("in_pos", w0
        .getVariable()
        .getName()
        .getName()
        .getActual());

      final UASTIDShaderVertexOutputAssignment<UASTIUnchecked> w1 =
        r.getWrites().get(1);
      Assert.assertEquals("out_pos2", w1.getName().getActual());
      Assert.assertEquals("in_pos", w1
        .getVariable()
        .getName()
        .getName()
        .getActual());
    }
  }

  @SuppressWarnings("static-method") @Test public
    void
    testDVertexShaderInput_0()
      throws IOException,
        LexerError,
        ConstraintError,
        ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("in x : integer");
    final UASTIDShaderVertexInput<UASTIUnchecked> r =
      p.declarationVertexShaderInput();
    Assert.assertEquals("x", r.getName().getActual());
    Assert.assertEquals("integer", r.getType().getName().getActual());
  }

  @SuppressWarnings("static-method") @Test public
    void
    testDVertexShaderOutput_0()
      throws IOException,
        LexerError,
        ConstraintError,
        ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("out x : integer");
    final UASTIDShaderVertexOutput<UASTIUnchecked> r =
      p.declarationVertexShaderOutput();
    Assert.assertEquals("x", r.getName().getActual());
    Assert.assertEquals("integer", r.getType().getName().getActual());
  }

  @SuppressWarnings("static-method") @Test public
    void
    testDVertexShaderParameter_0()
      throws IOException,
        LexerError,
        ConstraintError,
        ParserError
  {
    final Parser p =
      ParserTest.makeStringInternalParser("parameter x : integer");
    final UASTIDShaderVertexParameter<UASTIUnchecked> r =
      p.declarationVertexShaderParameter();
    Assert.assertEquals("x", r.getName().getActual());
    Assert.assertEquals("integer", r.getType().getName().getActual());
  }

  @SuppressWarnings("static-method") @Test(expected = ParserError.class) public
    void
    testEApplicationNotOK_0()
      throws IOException,
        LexerError,
        ConstraintError,
        ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("Y.X (1, 2, 3)");
    p.expressionVariableOrApplication();
  }

  @SuppressWarnings("static-method") @Test(expected = ParserError.class) public
    void
    testEApplicationNotOK_1()
      throws IOException,
        LexerError,
        ConstraintError,
        ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("X (1, 2, 3)");
    p.expressionVariableOrApplication();
  }

  @SuppressWarnings("static-method") @Test(expected = ParserError.class) public
    void
    testEApplicationNotOK_2()
      throws IOException,
        LexerError,
        ConstraintError,
        ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("f ()");
    p.expressionVariableOrApplication();
  }

  @SuppressWarnings("static-method") @Test public void testEApplicationOK_0()
    throws IOException,
      LexerError,
      ConstraintError,
      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("Y.x (1, 2, 3)");
    final UASTIEApplication<UASTIUnchecked> r =
      (UASTIEApplication<UASTIUnchecked>) p.expressionVariableOrApplication();
    Assert.assertTrue(r.getName().getModule().isSome());
    Assert.assertEquals("x", r.getName().getName().getActual());
    Assert.assertEquals(3, r.getArguments().size());

    Assert.assertEquals(1, ((UASTIEInteger<UASTIUnchecked>) r
      .getArguments()
      .get(0)).getValue().intValue());
    Assert.assertEquals(2, ((UASTIEInteger<UASTIUnchecked>) r
      .getArguments()
      .get(1)).getValue().intValue());
    Assert.assertEquals(3, ((UASTIEInteger<UASTIUnchecked>) r
      .getArguments()
      .get(2)).getValue().intValue());
  }

  @SuppressWarnings("static-method") @Test public void testEApplicationOK_1()
    throws IOException,
      LexerError,
      ConstraintError,
      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("x (1, 2, 3)");
    final UASTIEApplication<UASTIUnchecked> r =
      (UASTIEApplication<UASTIUnchecked>) p.expressionVariableOrApplication();
    Assert.assertTrue(r.getName().getModule().isNone());
    Assert.assertEquals("x", r.getName().getName().getActual());
    Assert.assertEquals(3, r.getArguments().size());

    Assert.assertEquals(1, ((UASTIEInteger<UASTIUnchecked>) r
      .getArguments()
      .get(0)).getValue().intValue());
    Assert.assertEquals(2, ((UASTIEInteger<UASTIUnchecked>) r
      .getArguments()
      .get(1)).getValue().intValue());
    Assert.assertEquals(3, ((UASTIEInteger<UASTIUnchecked>) r
      .getArguments()
      .get(2)).getValue().intValue());
  }

  @SuppressWarnings("static-method") @Test(expected = ParserError.class) public
    void
    testEBooleanNotOK()
      throws IOException,
        LexerError,
        ConstraintError,
        ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("z");
    p.expressionBoolean();
  }

  @SuppressWarnings({ "static-method", "boxing" }) @Test public
    void
    testEBooleanOK0()
      throws IOException,
        LexerError,
        ConstraintError,
        ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("true");
    final UASTIEBoolean<UASTIUnchecked> r = p.expressionBoolean();
    Assert.assertEquals(true, r.getValue());
  }

  @SuppressWarnings({ "static-method", "boxing" }) @Test public
    void
    testEBooleanOK1()
      throws IOException,
        LexerError,
        ConstraintError,
        ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("false");
    final UASTIEBoolean<UASTIUnchecked> r = p.expressionBoolean();
    Assert.assertEquals(false, r.getValue());
  }

  @SuppressWarnings("static-method") @Test public void testEConditionalOK_0()
    throws IOException,
      LexerError,
      ConstraintError,
      ParserError
  {
    final Parser p =
      ParserTest.makeStringInternalParser("if true then 23 else 24 end");
    final UASTIEConditional<UASTIUnchecked> r =
      (UASTIEConditional<UASTIUnchecked>) p.expression();
    Assert.assertTrue(r.getCondition() instanceof UASTIEBoolean<?>);
    Assert.assertTrue(r.getLeft() instanceof UASTIEInteger<?>);
    Assert.assertTrue(r.getRight() instanceof UASTIEInteger<?>);

    Assert.assertEquals(23, ((UASTIEInteger<?>) r.getLeft())
      .getValue()
      .intValue());
    Assert.assertEquals(24, ((UASTIEInteger<?>) r.getRight())
      .getValue()
      .intValue());
  }

  @SuppressWarnings("static-method") @Test(expected = ParserError.class) public
    void
    testEIntegerNotOK()
      throws IOException,
        LexerError,
        ConstraintError,
        ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("z");
    p.expressionInteger();
  }

  @SuppressWarnings("static-method") @Test public void testEIntegerOK()
    throws IOException,
      LexerError,
      ConstraintError,
      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("23");
    final UASTIEInteger<UASTIUnchecked> r = p.expressionInteger();
    Assert.assertEquals(BigDecimal.valueOf(23), r.getValue());
  }

  @SuppressWarnings("static-method") @Test(expected = ParserError.class) public
    void
    testELetNotOK_0()
      throws IOException,
        LexerError,
        ConstraintError,
        ParserError
  {
    final Parser p =
      ParserTest.makeStringInternalParser("let value k.k = 23; in 24 end");
    p.expression();
  }

  @SuppressWarnings("static-method") @Test(expected = ParserError.class) public
    void
    testELetNotOK_1()
      throws IOException,
        LexerError,
        ConstraintError,
        ParserError
  {
    final Parser p =
      ParserTest.makeStringInternalParser("let value k.k = 23; in 24 end");
    p.expression();
  }

  @SuppressWarnings("static-method") @Test(expected = ParserError.class) public
    void
    testELetNotOK_2()
      throws IOException,
        LexerError,
        ConstraintError,
        ParserError
  {
    final Parser p =
      ParserTest.makeStringInternalParser("let value K = 23; in 24 end");
    p.expression();
  }

  @SuppressWarnings("static-method") @Test public void testELetOK_0()
    throws IOException,
      LexerError,
      ConstraintError,
      ParserError
  {
    final Parser p =
      ParserTest
        .makeStringInternalParser("let value k : integer = 23; in 24 end");
    final UASTIELet<UASTIUnchecked> r =
      (UASTIELet<UASTIUnchecked>) p.expression();

    Assert.assertEquals(1, r.getBindings().size());
    final UASTIDValueLocal<UASTIUnchecked> first = r.getBindings().get(0);

    final Option<UASTITypePath> asc = first.getAscription();
    Assert.assertTrue(asc.isSome());
    final Some<UASTITypePath> some = (Option.Some<UASTITypePath>) asc;
    Assert.assertEquals("integer", some.value.getName().getActual());
    Assert.assertEquals(23, ((UASTIEInteger<UASTIUnchecked>) first
      .getExpression()).getValue().intValue());
    Assert.assertEquals(24, ((UASTIEInteger<UASTIUnchecked>) r.getBody())
      .getValue()
      .intValue());
  }

  @SuppressWarnings("static-method") @Test public void testELetOK_1()
    throws IOException,
      LexerError,
      ConstraintError,
      ParserError
  {
    final Parser p =
      ParserTest.makeStringInternalParser("let value k = 23; in 24 end");
    final UASTIELet<UASTIUnchecked> r =
      (UASTIELet<UASTIUnchecked>) p.expression();

    Assert.assertEquals(1, r.getBindings().size());
    final UASTIDValueLocal<UASTIUnchecked> first = r.getBindings().get(0);
    final Option<UASTITypePath> asc = first.getAscription();
    Assert.assertTrue(asc.isNone());
    Assert.assertEquals(23, ((UASTIEInteger<UASTIUnchecked>) first
      .getExpression()).getValue().intValue());
    Assert.assertEquals(24, ((UASTIEInteger<UASTIUnchecked>) r.getBody())
      .getValue()
      .intValue());
  }

  @SuppressWarnings("static-method") @Test public void testELetOK_2()
    throws IOException,
      LexerError,
      ConstraintError,
      ParserError
  {
    final Parser p =
      ParserTest
        .makeStringInternalParser("let value k = 23; value z = 25; in 24 end");
    final UASTIELet<UASTIUnchecked> r =
      (UASTIELet<UASTIUnchecked>) p.expression();

    Assert.assertEquals(2, r.getBindings().size());

    {
      final UASTIDValueLocal<UASTIUnchecked> b = r.getBindings().get(0);
      final Option<UASTITypePath> asc = b.getAscription();
      Assert.assertTrue(asc.isNone());
      Assert.assertEquals(23, ((UASTIEInteger<UASTIUnchecked>) b
        .getExpression()).getValue().intValue());
    }

    {
      final UASTIDValueLocal<UASTIUnchecked> b = r.getBindings().get(1);
      final Option<UASTITypePath> asc = b.getAscription();
      Assert.assertTrue(asc.isNone());
      Assert.assertEquals(25, ((UASTIEInteger<UASTIUnchecked>) b
        .getExpression()).getValue().intValue());
    }

    Assert.assertEquals(24, ((UASTIEInteger<UASTIUnchecked>) r.getBody())
      .getValue()
      .intValue());
  }

  @SuppressWarnings("static-method") @Test(expected = ParserError.class) public
    void
    testENewNotOK_0()
      throws IOException,
        LexerError,
        ConstraintError,
        ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("new Y.X (1, 2, 3)");
    p.expression();
  }

  @SuppressWarnings("static-method") @Test(expected = ParserError.class) public
    void
    testENewNotOK_1()
      throws IOException,
        LexerError,
        ConstraintError,
        ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("new X (1, 2, 3)");
    p.expression();
  }

  @SuppressWarnings("static-method") @Test(expected = ParserError.class) public
    void
    testENewNotOK_2()
      throws IOException,
        LexerError,
        ConstraintError,
        ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("new f ()");
    p.expression();
  }

  @SuppressWarnings("static-method") @Test public void testENewOK_0()
    throws IOException,
      LexerError,
      ConstraintError,
      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("new Y.x (1, 2, 3)");
    final UASTIENew<UASTIUnchecked> r =
      (UASTIENew<UASTIUnchecked>) p.expression();
    Assert.assertTrue(r.getName().getModule().isSome());
    Assert.assertEquals("x", r.getName().getName().getActual());
    Assert.assertEquals(3, r.getArguments().size());

    Assert.assertEquals(1, ((UASTIEInteger<UASTIUnchecked>) r
      .getArguments()
      .get(0)).getValue().intValue());
    Assert.assertEquals(2, ((UASTIEInteger<UASTIUnchecked>) r
      .getArguments()
      .get(1)).getValue().intValue());
    Assert.assertEquals(3, ((UASTIEInteger<UASTIUnchecked>) r
      .getArguments()
      .get(2)).getValue().intValue());
  }

  @SuppressWarnings("static-method") @Test public void testENewOK_1()
    throws IOException,
      LexerError,
      ConstraintError,
      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("new x (1, 2, 3)");
    final UASTIENew<UASTIUnchecked> r =
      (UASTIENew<UASTIUnchecked>) p.expression();
    Assert.assertTrue(r.getName().getModule().isNone());
    Assert.assertEquals("x", r.getName().getName().getActual());
    Assert.assertEquals(3, r.getArguments().size());

    Assert.assertEquals(1, ((UASTIEInteger<UASTIUnchecked>) r
      .getArguments()
      .get(0)).getValue().intValue());
    Assert.assertEquals(2, ((UASTIEInteger<UASTIUnchecked>) r
      .getArguments()
      .get(1)).getValue().intValue());
    Assert.assertEquals(3, ((UASTIEInteger<UASTIUnchecked>) r
      .getArguments()
      .get(2)).getValue().intValue());
  }

  @SuppressWarnings("static-method") @Test(expected = ParserError.class) public
    void
    testERealNotOK()
      throws IOException,
        LexerError,
        ConstraintError,
        ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("z");
    p.expressionReal();
  }

  @SuppressWarnings("static-method") @Test public void testERealOK()
    throws IOException,
      LexerError,
      ConstraintError,
      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("23.0");
    final UASTIEReal<UASTIUnchecked> r =
      (UASTIEReal<UASTIUnchecked>) p.expression();
    Assert.assertEquals(BigDecimal.valueOf(23.0), r.getValue());
  }

  @SuppressWarnings("static-method") @Test public void testERecordOK_0()
    throws IOException,
      LexerError,
      ConstraintError,
      ParserError
  {
    final Parser p =
      ParserTest.makeStringInternalParser("record t { a = 23, b = 17 }");
    final UASTIERecord<UASTIUnchecked> r =
      (UASTIERecord<UASTIUnchecked>) p.expression();
    Assert.assertEquals("t", r.getTypePath().getName().getActual());
    Assert.assertEquals(2, r.getAssignments().size());
    final UASTIRecordFieldAssignment<UASTIUnchecked> as0 =
      r.getAssignments().get(0);
    Assert.assertEquals("a", as0.getName().getActual());
    Assert.assertEquals(23, ((UASTIEInteger<UASTIUnchecked>) as0
      .getExpression()).getValue().intValue());
    final UASTIRecordFieldAssignment<UASTIUnchecked> as1 =
      r.getAssignments().get(1);
    Assert.assertEquals("b", as1.getName().getActual());
    Assert.assertEquals(17, ((UASTIEInteger<UASTIUnchecked>) as1
      .getExpression()).getValue().intValue());
  }

  @SuppressWarnings("static-method") @Test public
    void
    testERecordProjectionOK_0()
      throws IOException,
        LexerError,
        ConstraintError,
        ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("x.y");
    final UASTIERecordProjection<UASTIUnchecked> r =
      (UASTIERecordProjection<UASTIUnchecked>) p.expression();

    final UASTIEVariable<UASTIUnchecked> var =
      (UASTIEVariable<UASTIUnchecked>) r.getExpression();
    Assert.assertEquals("x", var.getName().getName().getActual());
    Assert.assertEquals("y", r.getField().getActual());
  }

  @SuppressWarnings("static-method") @Test public
    void
    testERecordProjectionOK_1()
      throws IOException,
        LexerError,
        ConstraintError,
        ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("P.x.y");
    final UASTIERecordProjection<UASTIUnchecked> r =
      (UASTIERecordProjection<UASTIUnchecked>) p.expression();

    final UASTIEVariable<UASTIUnchecked> var =
      (UASTIEVariable<UASTIUnchecked>) r.getExpression();

    Assert.assertEquals("P", ((Option.Some<TokenIdentifierUpper>) var
      .getName()
      .getModule()).value.getActual());
    Assert.assertEquals("x", var.getName().getName().getActual());
    Assert.assertEquals("y", r.getField().getActual());
  }

  @SuppressWarnings("static-method") @Test(expected = ParserError.class) public
    void
    testESwizzleNotOK_0()
      throws IOException,
        LexerError,
        ConstraintError,
        ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("23 [X]");
    p.expression();
  }

  @SuppressWarnings("static-method") @Test(expected = ParserError.class) public
    void
    testESwizzleNotOK_1()
      throws IOException,
        LexerError,
        ConstraintError,
        ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("23 [x.y]");
    p.expression();
  }

  @SuppressWarnings("static-method") @Test(expected = ParserError.class) public
    void
    testESwizzleNotOK_2()
      throws IOException,
        LexerError,
        ConstraintError,
        ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("23 []");
    p.expression();
  }

  @SuppressWarnings("static-method") @Test public void testESwizzleOK_0()
    throws IOException,
      LexerError,
      ConstraintError,
      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("23 [x y z]");
    final UASTIESwizzle<UASTIUnchecked> r =
      (UASTIESwizzle<UASTIUnchecked>) p.expression();

    Assert.assertEquals(23, ((UASTIEInteger<UASTIUnchecked>) r
      .getExpression()).getValue().intValue());
    Assert.assertEquals(3, r.getFields().size());
    Assert.assertEquals("x", r.getFields().get(0).getActual());
    Assert.assertEquals("y", r.getFields().get(1).getActual());
    Assert.assertEquals("z", r.getFields().get(2).getActual());
  }

  @SuppressWarnings("static-method") @Test public void testESwizzleOK_1()
    throws IOException,
      LexerError,
      ConstraintError,
      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("23 [x]");
    final UASTIESwizzle<UASTIUnchecked> r =
      (UASTIESwizzle<UASTIUnchecked>) p.expression();

    Assert.assertEquals(23, ((UASTIEInteger<UASTIUnchecked>) r
      .getExpression()).getValue().intValue());
    Assert.assertEquals(1, r.getFields().size());
    Assert.assertEquals("x", r.getFields().get(0).getActual());
  }

  @SuppressWarnings("static-method") @Test public void testESwizzleOK_2()
    throws IOException,
      LexerError,
      ConstraintError,
      ParserError
  {
    final Parser p =
      ParserTest.makeStringInternalParser("23 [x y z] [z y x]");
    final UASTIESwizzle<UASTIUnchecked> r =
      (UASTIESwizzle<UASTIUnchecked>) p.expression();

    final UASTIESwizzle<UASTIUnchecked> inner =
      (UASTIESwizzle<UASTIUnchecked>) r.getExpression();

    Assert.assertEquals(23, ((UASTIEInteger<UASTIUnchecked>) inner
      .getExpression()).getValue().intValue());

    Assert.assertEquals(3, inner.getFields().size());
    Assert.assertEquals("x", inner.getFields().get(0).getActual());
    Assert.assertEquals("y", inner.getFields().get(1).getActual());
    Assert.assertEquals("z", inner.getFields().get(2).getActual());

    Assert.assertEquals(3, r.getFields().size());
    Assert.assertEquals("z", r.getFields().get(0).getActual());
    Assert.assertEquals("y", r.getFields().get(1).getActual());
    Assert.assertEquals("x", r.getFields().get(2).getActual());
  }

  @SuppressWarnings("static-method") @Test public void testEVariableOK_0()
    throws IOException,
      LexerError,
      ConstraintError,
      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("x");
    final UASTIEVariable<UASTIUnchecked> r =
      (UASTIEVariable<UASTIUnchecked>) p.expression();
    Assert.assertTrue(r.getName().getModule().isNone());
    Assert.assertEquals("x", r.getName().getName().getActual());
  }

  @SuppressWarnings("static-method") @Test public void testEVariableOK_1()
    throws IOException,
      LexerError,
      ConstraintError,
      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("Y.x");
    final UASTIEVariable<UASTIUnchecked> r =
      (UASTIEVariable<UASTIUnchecked>) p.expression();
    Assert.assertTrue(r.getName().getModule().isSome());
    Assert.assertEquals("x", r.getName().getName().getActual());
  }
}