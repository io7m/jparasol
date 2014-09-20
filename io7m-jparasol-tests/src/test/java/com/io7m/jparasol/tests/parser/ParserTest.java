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

package com.io7m.jparasol.tests.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jfunctional.OptionType;
import com.io7m.jfunctional.Some;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.PackagePath;
import com.io7m.jparasol.PackagePathFlat;
import com.io7m.jparasol.lexer.Lexer;
import com.io7m.jparasol.lexer.LexerError;
import com.io7m.jparasol.lexer.Token.TokenIdentifierUpper;
import com.io7m.jparasol.parser.Parser;
import com.io7m.jparasol.parser.ParserError;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDExternal;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDFunctionArgument;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDFunctionDefined;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDFunctionExternal;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDImport;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDModule;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDPackage;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragment;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentInput;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentLocalDiscard;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentLocalValue;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentOutputAssignment;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentOutputData;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentOutputDepth;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentParameter;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderProgram;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertex;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertexInput;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertexLocalValue;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertexOutput;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertexOutputAssignment;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertexParameter;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDTypeRecord;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDTypeRecordField;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDValueDefined;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDValueLocal;
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
import com.io7m.jparasol.untyped.ast.initial.UASTITypePath;
import com.io7m.jparasol.untyped.ast.initial.UASTIUnit;
import com.io7m.jparasol.untyped.ast.initial.UASTIValuePath;

@SuppressWarnings("static-method") public class ParserTest
{
  @SuppressWarnings("resource") static Parser makeResourceInternalParser(
    final String name)
    throws IOException,
      LexerError
  {
    final InputStream is =
      ParserTest.class.getResourceAsStream("/com/io7m/jparasol/tests/parser/"
        + name);
    assert is != null;
    final Lexer lexer = new Lexer(is);
    return Parser.newInternalParser(lexer);
  }

  @SuppressWarnings("resource") static Parser makeResourceParser(
    final String name)
    throws IOException,
      LexerError
  {
    final InputStream is =
      ParserTest.class.getResourceAsStream("/com/io7m/jparasol/tests/parser/"
        + name);
    assert is != null;
    final Lexer lexer = new Lexer(is);
    return Parser.newParser(lexer);
  }

  static Parser makeStringInternalParser(
    final String text)
    throws IOException,
      LexerError

  {
    final ByteArrayInputStream bs = new ByteArrayInputStream(text.getBytes());
    final Lexer lexer = new Lexer(bs);
    return Parser.newInternalParser(lexer);
  }

  static Parser makeStringParser(
    final String text)
    throws IOException,
      LexerError

  {
    final ByteArrayInputStream bs = new ByteArrayInputStream(text.getBytes());
    final Lexer lexer = new Lexer(bs);
    return Parser.newParser(lexer);
  }

  @Test public void testDFragmentShader_0()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p = ParserTest.makeResourceParser("testDFragmentShader0.p");
    final UASTIDShaderFragment r = p.declarationFragmentShader();
    Assert.assertEquals("f", r.getName().getActual());

    Assert.assertEquals(1, r.getParameters().size());
    {
      final UASTIDShaderFragmentParameter rp = r.getParameters().get(0);
      Assert.assertEquals("x", rp.getName().getActual());
      Assert.assertTrue(rp.getType().getModule().isNone());
      Assert.assertEquals("integer", rp.getType().getName().getActual());
    }

    Assert.assertEquals(2, r.getInputs().size());
    {
      final UASTIDShaderFragmentInput ri0 = r.getInputs().get(0);
      Assert.assertEquals("mmv", ri0.getName().getActual());
      Assert.assertTrue(ri0.getType().getModule().isNone());
      Assert.assertEquals("matrix_4x4f", ri0.getType().getName().getActual());

      final UASTIDShaderFragmentInput ri1 = r.getInputs().get(1);
      Assert.assertEquals("in_pos", ri1.getName().getActual());
      Assert.assertTrue(ri1.getType().getModule().isNone());
      Assert.assertEquals("vector_4f", ri1.getType().getName().getActual());
    }

    Assert.assertEquals(2, r.getOutputs().size());
    {
      final UASTIDShaderFragmentOutputData ro0 =
        (UASTIDShaderFragmentOutputData) r.getOutputs().get(0);
      Assert.assertEquals("out_pos", ro0.getName().getActual());
      Assert.assertEquals(0, ro0.getIndex());
      Assert.assertEquals("vector_4f", ro0.getType().getName().getActual());

      final UASTIDShaderFragmentOutputData ro1 =
        (UASTIDShaderFragmentOutputData) r.getOutputs().get(1);
      Assert.assertEquals("out_pos2", ro1.getName().getActual());
      Assert.assertEquals(1, ro1.getIndex());
      Assert.assertEquals("vector_4f", ro1.getType().getName().getActual());
    }

    Assert.assertEquals(2, r.getLocals().size());
    {
      final UASTIDShaderFragmentLocalValue rl0 =
        (UASTIDShaderFragmentLocalValue) r.getLocals().get(0);
      Assert.assertEquals("pp", rl0.getValue().getName().getActual());
      Assert.assertTrue(rl0.getValue().getAscription().isNone());

      @SuppressWarnings("unused") final UASTIDShaderFragmentLocalDiscard rl1 =
        (UASTIDShaderFragmentLocalDiscard) r.getLocals().get(1);
    }

    Assert.assertEquals(2, r.getWrites().size());
    {
      final UASTIDShaderFragmentOutputAssignment w0 = r.getWrites().get(0);
      Assert.assertEquals("out_pos", w0.getName().getActual());
      Assert.assertEquals("pp", w0
        .getVariable()
        .getName()
        .getName()
        .getActual());

      final UASTIDShaderFragmentOutputAssignment w1 = r.getWrites().get(1);
      Assert.assertEquals("out_pos2", w1.getName().getActual());
      Assert.assertEquals("pp", w1
        .getVariable()
        .getName()
        .getName()
        .getActual());
    }
  }

  @Test public void testDFragmentShader_1()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p = ParserTest.makeResourceParser("testDFragmentShader1.p");
    final UASTIDShaderFragment r = p.declarationFragmentShader();
    Assert.assertEquals("f", r.getName().getActual());

    Assert.assertEquals(1, r.getParameters().size());
    {
      final UASTIDShaderFragmentParameter rp = r.getParameters().get(0);
      Assert.assertEquals("x", rp.getName().getActual());
      Assert.assertTrue(rp.getType().getModule().isNone());
      Assert.assertEquals("integer", rp.getType().getName().getActual());
    }

    Assert.assertEquals(2, r.getInputs().size());
    {
      final UASTIDShaderFragmentInput ri0 = r.getInputs().get(0);
      Assert.assertEquals("mmv", ri0.getName().getActual());
      Assert.assertTrue(ri0.getType().getModule().isNone());
      Assert.assertEquals("matrix_4x4f", ri0.getType().getName().getActual());

      final UASTIDShaderFragmentInput ri1 = r.getInputs().get(1);
      Assert.assertEquals("in_pos", ri1.getName().getActual());
      Assert.assertTrue(ri1.getType().getModule().isNone());
      Assert.assertEquals("vector_4f", ri1.getType().getName().getActual());
    }

    Assert.assertEquals(2, r.getOutputs().size());
    {
      final UASTIDShaderFragmentOutputData ro0 =
        (UASTIDShaderFragmentOutputData) r.getOutputs().get(0);
      Assert.assertEquals("out_pos", ro0.getName().getActual());
      Assert.assertEquals(0, ro0.getIndex());
      Assert.assertEquals("vector_4f", ro0.getType().getName().getActual());

      final UASTIDShaderFragmentOutputData ro1 =
        (UASTIDShaderFragmentOutputData) r.getOutputs().get(1);
      Assert.assertEquals("out_pos2", ro1.getName().getActual());
      Assert.assertEquals(1, ro1.getIndex());
      Assert.assertEquals("vector_4f", ro1.getType().getName().getActual());
    }

    Assert.assertEquals(2, r.getWrites().size());
    {
      final UASTIDShaderFragmentOutputAssignment w0 = r.getWrites().get(0);
      Assert.assertEquals("out_pos", w0.getName().getActual());
      Assert.assertEquals("in_pos", w0
        .getVariable()
        .getName()
        .getName()
        .getActual());

      final UASTIDShaderFragmentOutputAssignment w1 = r.getWrites().get(1);
      Assert.assertEquals("out_pos2", w1.getName().getActual());
      Assert.assertEquals("in_pos", w1
        .getVariable()
        .getName()
        .getName()
        .getActual());
    }
  }

  @Test public void testDFragmentShaderInput_0()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("in x : integer");
    final UASTIDShaderFragmentInput r = p.declarationFragmentShaderInput();
    Assert.assertEquals("x", r.getName().getActual());
    Assert.assertEquals("integer", r.getType().getName().getActual());
  }

  @Test public void testDFragmentShaderOutputData_0()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p =
      ParserTest.makeStringInternalParser("out x : integer as 23");
    final UASTIDShaderFragmentOutputData r =
      (UASTIDShaderFragmentOutputData) p.declarationFragmentShaderOutput();
    Assert.assertEquals("x", r.getName().getActual());
    Assert.assertEquals("integer", r.getType().getName().getActual());
    Assert.assertEquals(23, r.getIndex());
  }

  @Test public void testDFragmentShaderOutputDepth_0()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p =
      ParserTest.makeStringInternalParser("out depth x : float");
    final UASTIDShaderFragmentOutputDepth r =
      (UASTIDShaderFragmentOutputDepth) p.declarationFragmentShaderOutput();
    Assert.assertEquals("x", r.getName().getActual());
    Assert.assertEquals("float", r.getType().getName().getActual());
  }

  @Test public void testDFragmentShaderParameter_0()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p =
      ParserTest.makeStringInternalParser("parameter x : integer");
    final UASTIDShaderFragmentParameter r =
      p.declarationFragmentShaderParameter();
    Assert.assertEquals("x", r.getName().getActual());
    Assert.assertEquals("integer", r.getType().getName().getActual());
  }

  @Test public void testDFunctionExternal_1()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p =
      ParserTest
        .makeStringInternalParser("function f (x : integer, y : Example.t) : integer = external xyz is vertex true; fragment true; end");

    final UASTIDFunctionExternal r =
      (UASTIDFunctionExternal) p.declarationFunction();
    Assert.assertEquals("f", r.getName().getActual());
    Assert.assertEquals(2, r.getArguments().size());

    final UASTIDFunctionArgument arg0 = r.getArguments().get(0);
    final UASTIDFunctionArgument arg1 = r.getArguments().get(1);

    Assert.assertEquals("x", arg0.getName().getActual());
    Assert.assertEquals("integer", arg0.getType().getName().getActual());
    Assert.assertEquals("y", arg1.getName().getActual());
    Assert.assertEquals("Example", ((Some<TokenIdentifierUpper>) arg1
      .getType()
      .getModule()).get().getActual());
    Assert.assertEquals("t", arg1.getType().getName().getActual());

    final UASTIDExternal ext = r.getExternal();
    Assert.assertEquals("xyz", ext.getName().getActual());
  }

  @Test public void testDFunctionExternal_2()
    throws IOException,
      LexerError,

      ParserError
  {
    final StringBuilder text = new StringBuilder();
    text.append("function f (x : integer, y : Example.t) : integer =\n");
    text.append("  external xyz is\n");
    text.append("    vertex true;\n");
    text.append("    fragment true;\n");
    text.append("  with\n");
    text.append("    x\n");
    text.append("  end");

    final Parser p = ParserTest.makeStringInternalParser(text.toString());

    final UASTIDFunctionExternal r =
      (UASTIDFunctionExternal) p.declarationFunction();
    Assert.assertEquals("f", r.getName().getActual());
    Assert.assertEquals(2, r.getArguments().size());

    final UASTIDFunctionArgument arg0 = r.getArguments().get(0);
    final UASTIDFunctionArgument arg1 = r.getArguments().get(1);

    Assert.assertEquals("x", arg0.getName().getActual());
    Assert.assertEquals("integer", arg0.getType().getName().getActual());
    Assert.assertEquals("y", arg1.getName().getActual());
    Assert.assertEquals("Example", ((Some<TokenIdentifierUpper>) arg1
      .getType()
      .getModule()).get().getActual());
    Assert.assertEquals("t", arg1.getType().getName().getActual());

    final UASTIDExternal ext = r.getExternal();
    Assert.assertEquals("xyz", ext.getName().getActual());

    Assert.assertTrue(ext.getEmulation().isSome());
    final Some<UASTIExpression> exp =
      (Some<UASTIExpression>) ext.getEmulation();
    Assert.assertEquals("x", ((UASTIEVariable) exp.get())
      .getName()
      .getName()
      .getActual());
  }

  @Test(expected = ParserError.class) public
    void
    testDFunctionNotExternal_0()
      throws IOException,
        LexerError,

        ParserError
  {
    final Parser p =
      ParserTest
        .makeStringParser("function f (x : integer, y : Example.t) : integer = external xyz");
    p.declarationFunction();
  }

  @Test public void testDFunctionOK_0()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p =
      ParserTest
        .makeStringInternalParser("function f (x : integer, y : Example.t) : integer = 23");
    final UASTIDFunctionDefined r =
      (UASTIDFunctionDefined) p.declarationFunction();
    Assert.assertEquals("f", r.getName().getActual());
    Assert.assertEquals(2, r.getArguments().size());

    final UASTIDFunctionArgument arg0 = r.getArguments().get(0);
    final UASTIDFunctionArgument arg1 = r.getArguments().get(1);

    Assert.assertEquals("x", arg0.getName().getActual());
    Assert.assertEquals("integer", arg0.getType().getName().getActual());
    Assert.assertEquals("y", arg1.getName().getActual());
    Assert.assertEquals("Example", ((Some<TokenIdentifierUpper>) arg1
      .getType()
      .getModule()).get().getActual());
    Assert.assertEquals("t", arg1.getType().getName().getActual());
    Assert.assertEquals(23, ((UASTIEInteger) r.getBody())
      .getValue()
      .intValue());
  }

  @Test public void testDImportOK_0()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("import x.y.z.M");

    final UASTIDImport r = p.declarationImport();
    final ModulePathFlat flat = ModulePathFlat.fromModulePath(r.getPath());
    Assert.assertEquals("x.y.z.M", flat.getActual());
    Assert.assertTrue(r.getRename().isNone());
  }

  @Test public void testDImportOK_1()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p =
      ParserTest.makeStringInternalParser("import x.y.z.M as K");

    final UASTIDImport r = p.declarationImport();
    final ModulePathFlat flat = ModulePathFlat.fromModulePath(r.getPath());
    Assert.assertEquals("x.y.z.M", flat.getActual());
    Assert.assertTrue(r.getRename().isSome());
    final Some<TokenIdentifierUpper> some =
      (Some<TokenIdentifierUpper>) r.getRename();
    Assert.assertEquals("K", some.get().getActual());
  }

  @Test public void testDImportOK_2()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("import M");
    final UASTIDImport r = p.declarationImport();
    final ModulePathFlat flat = ModulePathFlat.fromModulePath(r.getPath());
    Assert.assertEquals("M", flat.getActual());
    Assert.assertTrue(r.getRename().isNone());
  }

  @Test public void testDImportOK_3()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("import M as K");
    final UASTIDImport r = p.declarationImport();
    final ModulePathFlat flat = ModulePathFlat.fromModulePath(r.getPath());
    Assert.assertEquals("M", flat.getActual());
    Assert.assertTrue(r.getRename().isSome());
    final Some<TokenIdentifierUpper> some =
      (Some<TokenIdentifierUpper>) r.getRename();
    Assert.assertEquals("K", some.get().getActual());
  }

  @Test public void testDModule_0()
    throws IOException,
      LexerError,

      ParserError
  {
    final PackagePath path = PackagePath.newBuilder().build();

    final Parser p = ParserTest.makeResourceParser("testDModule0.p");
    final UASTIDModule m = p.declarationModule(path);
    Assert.assertEquals("M", m.getPath().getName().getActual());

    Assert.assertEquals(2, m.getImports().size());
    Assert.assertEquals("K", m
      .getImports()
      .get(0)
      .getPath()
      .getName()
      .getActual());
    Assert.assertEquals("Q", m
      .getImports()
      .get(1)
      .getPath()
      .getName()
      .getActual());

    Assert.assertEquals(6, m.getDeclarations().size());
  }

  @Test public void testDPackageOK_0()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("package x.y.z");

    final UASTIDPackage r = p.declarationPackage();
    final PackagePathFlat flat = PackagePathFlat.fromPackagePath(r.getPath());
    Assert.assertEquals("x.y.z", flat.getActual());
  }

  @Test public void testDProgramShader_0()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p = ParserTest.makeResourceParser("testDProgramShader0.p");
    final UASTIDShaderProgram r = p.declarationProgramShader();

    Assert.assertEquals("p", r.getName().getActual());
    Assert.assertEquals("x", r.getVertexShader().getName().getActual());
    Assert.assertEquals("y", r.getFragmentShader().getName().getActual());
  }

  @Test(expected = ParserError.class) public void testDRecordNotOK_0()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p = ParserTest.makeStringParser("type t is record end");
    p.declarationType();
  }

  @Test public void testDRecordOK_0()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p =
      ParserTest
        .makeStringParser("type t is record x : integer, y : Z.integer end");

    final UASTIDTypeRecord r = (UASTIDTypeRecord) p.declarationType();
    Assert.assertEquals("t", r.getName().getActual());
    Assert.assertEquals(2, r.getFields().size());

    final UASTIDTypeRecordField arg0 = r.getFields().get(0);
    final UASTIDTypeRecordField arg1 = r.getFields().get(1);

    Assert.assertEquals("x", arg0.getName().getActual());
    Assert.assertEquals("integer", arg0.getType().getName().getActual());
    Assert.assertEquals("y", arg1.getName().getActual());
    Assert.assertEquals("Z", ((Some<TokenIdentifierUpper>) arg1
      .getType()
      .getModule()).get().getActual());
    Assert.assertEquals("integer", arg1.getType().getName().getActual());
  }

  @Test public void testDShader_0()
    throws IOException,
      LexerError,
      ParserError
  {
    final Parser p = ParserTest.makeResourceParser("testDShader0.p");

    final UASTIDShaderFragment r0 =
      (UASTIDShaderFragment) p.declarationShader();
    p.statementTerminate();

    final UASTIDShaderVertex r1 = (UASTIDShaderVertex) p.declarationShader();
    p.statementTerminate();

    final UASTIDShaderProgram r2 =
      (UASTIDShaderProgram) p.declarationShader();
    p.statementTerminate();

    Assert.assertEquals("f", r0.getName().getActual());
    Assert.assertEquals("v", r1.getName().getActual());
    Assert.assertEquals("p", r2.getName().getActual());
  }

  @Test public void testDTypePathOK_0()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("X.y");
    final UASTITypePath r = p.declarationTypePath();
    Assert.assertTrue(r.getModule().isSome());
    final Some<TokenIdentifierUpper> some =
      (Some<TokenIdentifierUpper>) r.getModule();
    Assert.assertEquals("X", some.get().getActual());
    Assert.assertEquals("y", r.getName().getActual());
  }

  @Test public void testDTypePathOK_1()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("y");
    final UASTITypePath r = p.declarationTypePath();
    Assert.assertTrue(r.getModule().isNone());
    Assert.assertEquals("y", r.getName().getActual());
  }

  @Test public void testDUnit_0()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p = ParserTest.makeResourceParser("testDUnit0.p");
    final UASTIUnit u = p.unit();
    Assert.assertEquals(
      "com.io7m.example",
      PackagePathFlat
        .fromPackagePath(u.getPackageName().getPath())
        .getActual());
    Assert.assertEquals(2, u.getModules().size());
    Assert.assertEquals("M", u
      .getModules()
      .get(0)
      .getPath()
      .getName()
      .getActual());
    Assert.assertEquals("N", u
      .getModules()
      .get(1)
      .getPath()
      .getName()
      .getActual());
  }

  @Test(expected = ParserError.class) public void testDValueNotOK_0()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("value K = 23");
    p.declarationValue();
  }

  @Test(expected = ParserError.class) public void testDValueNotOK_1()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("value k.K = 23");
    p.declarationValue();
  }

  @Test(expected = ParserError.class) public void testDValueNotOK_2()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p =
      ParserTest.makeStringInternalParser("value k : f.G = 23");
    p.declarationValue();
  }

  @Test(expected = ParserError.class) public void testDValueNotOK_3()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p =
      ParserTest.makeStringInternalParser("value k : F.G = 23");
    p.declarationValue();
  }

  @Test(expected = ParserError.class) public void testDValueNotOK_4()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("value k : G = 23");
    p.declarationValue();
  }

  @Test public void testDValueOK_0()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("value k = 23");
    final UASTIDValueDefined r = (UASTIDValueDefined) p.declarationValue();
    Assert.assertEquals("k", r.getName().getActual());
    Assert.assertTrue(r.getAscription().isNone());
    Assert.assertEquals(23, ((UASTIEInteger) r.getExpression())
      .getValue()
      .intValue());
  }

  @Test public void testDValueOK_1()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p =
      ParserTest.makeStringInternalParser("value k : integer = 23");
    final UASTIDValueDefined r = (UASTIDValueDefined) p.declarationValue();
    Assert.assertEquals("k", r.getName().getActual());
    Assert.assertTrue(r.getAscription().isSome());

    final UASTITypePath path =
      ((Some<UASTITypePath>) r.getAscription()).get();
    Assert.assertTrue(path.getModule().isNone());
    Assert.assertEquals("integer", path.getName().getActual());
    Assert.assertEquals(23, ((UASTIEInteger) r.getExpression())
      .getValue()
      .intValue());
  }

  @Test public void testDValueOK_2()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p =
      ParserTest.makeStringInternalParser("value k : P.integer = 23");
    final UASTIDValueDefined r = (UASTIDValueDefined) p.declarationValue();
    Assert.assertEquals("k", r.getName().getActual());
    Assert.assertTrue(r.getAscription().isSome());

    final UASTITypePath path =
      ((Some<UASTITypePath>) r.getAscription()).get();
    Assert.assertTrue(path.getModule().isSome());
    final Some<TokenIdentifierUpper> module =
      (Some<TokenIdentifierUpper>) path.getModule();
    Assert.assertEquals("P", module.get().getActual());

    Assert.assertEquals("integer", path.getName().getActual());
    Assert.assertEquals(23, ((UASTIEInteger) r.getExpression())
      .getValue()
      .intValue());
  }

  @Test public void testDValuePathOK_0()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("X.y");
    final UASTIValuePath r = p.declarationValuePath();
    Assert.assertTrue(r.getModule().isSome());
    final Some<TokenIdentifierUpper> some =
      (Some<TokenIdentifierUpper>) r.getModule();
    Assert.assertEquals("X", some.get().getActual());
    Assert.assertEquals("y", r.getName().getActual());
  }

  @Test public void testDValuePathOK_1()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("y");
    final UASTIValuePath r = p.declarationValuePath();
    Assert.assertTrue(r.getModule().isNone());
    Assert.assertEquals("y", r.getName().getActual());
  }

  @Test public void testDVertexShader_0()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p = ParserTest.makeResourceParser("testDVertexShader0.p");
    final UASTIDShaderVertex r = p.declarationVertexShader();
    Assert.assertEquals("v", r.getName().getActual());

    Assert.assertEquals(1, r.getParameters().size());
    {
      final UASTIDShaderVertexParameter rp = r.getParameters().get(0);
      Assert.assertEquals("x", rp.getName().getActual());
      Assert.assertTrue(rp.getType().getModule().isNone());
      Assert.assertEquals("integer", rp.getType().getName().getActual());
    }

    Assert.assertEquals(2, r.getInputs().size());
    {
      final UASTIDShaderVertexInput ri0 = r.getInputs().get(0);
      Assert.assertEquals("mmv", ri0.getName().getActual());
      Assert.assertTrue(ri0.getType().getModule().isNone());
      Assert.assertEquals("matrix_4x4f", ri0.getType().getName().getActual());

      final UASTIDShaderVertexInput ri1 = r.getInputs().get(1);
      Assert.assertEquals("in_pos", ri1.getName().getActual());
      Assert.assertTrue(ri1.getType().getModule().isNone());
      Assert.assertEquals("vector_4f", ri1.getType().getName().getActual());
    }

    Assert.assertEquals(2, r.getOutputs().size());
    {
      final UASTIDShaderVertexOutput ro0 = r.getOutputs().get(0);
      Assert.assertEquals("out_pos", ro0.getName().getActual());
      Assert.assertEquals("vector_4f", ro0.getType().getName().getActual());

      final UASTIDShaderVertexOutput ro1 = r.getOutputs().get(1);
      Assert.assertEquals("out_pos2", ro1.getName().getActual());
      Assert.assertEquals("vector_4f", ro1.getType().getName().getActual());
    }

    Assert.assertEquals(1, r.getLocals().size());
    {
      final UASTIDShaderVertexLocalValue rl0 = r.getLocals().get(0);
      Assert.assertEquals("pp", rl0.getValue().getName().getActual());
      Assert.assertTrue(rl0.getValue().getAscription().isNone());
    }

    Assert.assertEquals(2, r.getWrites().size());
    {
      final UASTIDShaderVertexOutputAssignment w0 = r.getWrites().get(0);
      Assert.assertEquals("out_pos", w0.getName().getActual());
      Assert.assertEquals("pp", w0
        .getVariable()
        .getName()
        .getName()
        .getActual());

      final UASTIDShaderVertexOutputAssignment w1 = r.getWrites().get(1);
      Assert.assertEquals("out_pos2", w1.getName().getActual());
      Assert.assertEquals("pp", w1
        .getVariable()
        .getName()
        .getName()
        .getActual());
    }
  }

  @Test public void testDVertexShader_1()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p = ParserTest.makeResourceParser("testDVertexShader1.p");
    final UASTIDShaderVertex r = p.declarationVertexShader();
    Assert.assertEquals("v", r.getName().getActual());

    Assert.assertEquals(1, r.getParameters().size());
    {
      final UASTIDShaderVertexParameter rp = r.getParameters().get(0);
      Assert.assertEquals("x", rp.getName().getActual());
      Assert.assertTrue(rp.getType().getModule().isNone());
      Assert.assertEquals("integer", rp.getType().getName().getActual());
    }

    Assert.assertEquals(2, r.getInputs().size());
    {
      final UASTIDShaderVertexInput ri0 = r.getInputs().get(0);
      Assert.assertEquals("mmv", ri0.getName().getActual());
      Assert.assertTrue(ri0.getType().getModule().isNone());
      Assert.assertEquals("matrix_4x4f", ri0.getType().getName().getActual());

      final UASTIDShaderVertexInput ri1 = r.getInputs().get(1);
      Assert.assertEquals("in_pos", ri1.getName().getActual());
      Assert.assertTrue(ri1.getType().getModule().isNone());
      Assert.assertEquals("vector_4f", ri1.getType().getName().getActual());
    }

    Assert.assertEquals(2, r.getOutputs().size());
    {
      final UASTIDShaderVertexOutput ro0 = r.getOutputs().get(0);
      Assert.assertEquals("out_pos", ro0.getName().getActual());
      Assert.assertEquals("vector_4f", ro0.getType().getName().getActual());

      final UASTIDShaderVertexOutput ro1 = r.getOutputs().get(1);
      Assert.assertEquals("out_pos2", ro1.getName().getActual());
      Assert.assertEquals("vector_4f", ro1.getType().getName().getActual());
    }

    Assert.assertEquals(0, r.getLocals().size());

    Assert.assertEquals(2, r.getWrites().size());
    {
      final UASTIDShaderVertexOutputAssignment w0 = r.getWrites().get(0);
      Assert.assertEquals("out_pos", w0.getName().getActual());
      Assert.assertEquals("in_pos", w0
        .getVariable()
        .getName()
        .getName()
        .getActual());

      final UASTIDShaderVertexOutputAssignment w1 = r.getWrites().get(1);
      Assert.assertEquals("out_pos2", w1.getName().getActual());
      Assert.assertEquals("in_pos", w1
        .getVariable()
        .getName()
        .getName()
        .getActual());
    }
  }

  @Test public void testDVertexShaderInput_0()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("in x : integer");
    final UASTIDShaderVertexInput r = p.declarationVertexShaderInput();
    Assert.assertEquals("x", r.getName().getActual());
    Assert.assertEquals("integer", r.getType().getName().getActual());
  }

  @Test public void testDVertexShaderOutput_0()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("out x : integer");
    final UASTIDShaderVertexOutput r = p.declarationVertexShaderOutput();
    Assert.assertEquals("x", r.getName().getActual());
    Assert.assertEquals("integer", r.getType().getName().getActual());
    Assert.assertFalse(r.isMain());
  }

  @Test public void testDVertexShaderOutput_1()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p =
      ParserTest.makeStringInternalParser("out vertex x : integer");
    final UASTIDShaderVertexOutput r = p.declarationVertexShaderOutput();
    Assert.assertEquals("x", r.getName().getActual());
    Assert.assertEquals("integer", r.getType().getName().getActual());
    Assert.assertTrue(r.isMain());
  }

  @Test public void testDVertexShaderParameter_0()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p =
      ParserTest.makeStringInternalParser("parameter x : integer");
    final UASTIDShaderVertexParameter r =
      p.declarationVertexShaderParameter();
    Assert.assertEquals("x", r.getName().getActual());
    Assert.assertEquals("integer", r.getType().getName().getActual());
  }

  @Test(expected = ParserError.class) public void testEApplicationNotOK_0()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("Y.X (1, 2, 3)");
    p.expressionVariableOrApplication();
  }

  @Test(expected = ParserError.class) public void testEApplicationNotOK_1()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("X (1, 2, 3)");
    p.expressionVariableOrApplication();
  }

  @Test(expected = ParserError.class) public void testEApplicationNotOK_2()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("f ()");
    p.expressionVariableOrApplication();
  }

  @Test public void testEApplicationOK_0()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("Y.x (1, 2, 3)");
    final UASTIEApplication r =
      (UASTIEApplication) p.expressionVariableOrApplication();
    Assert.assertTrue(r.getName().getModule().isSome());
    Assert.assertEquals("x", r.getName().getName().getActual());
    Assert.assertEquals(3, r.getArguments().size());

    Assert.assertEquals(1, ((UASTIEInteger) r.getArguments().get(0))
      .getValue()
      .intValue());
    Assert.assertEquals(2, ((UASTIEInteger) r.getArguments().get(1))
      .getValue()
      .intValue());
    Assert.assertEquals(3, ((UASTIEInteger) r.getArguments().get(2))
      .getValue()
      .intValue());
  }

  @Test public void testEApplicationOK_1()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("x (1, 2, 3)");
    final UASTIEApplication r =
      (UASTIEApplication) p.expressionVariableOrApplication();
    Assert.assertTrue(r.getName().getModule().isNone());
    Assert.assertEquals("x", r.getName().getName().getActual());
    Assert.assertEquals(3, r.getArguments().size());

    Assert.assertEquals(1, ((UASTIEInteger) r.getArguments().get(0))
      .getValue()
      .intValue());
    Assert.assertEquals(2, ((UASTIEInteger) r.getArguments().get(1))
      .getValue()
      .intValue());
    Assert.assertEquals(3, ((UASTIEInteger) r.getArguments().get(2))
      .getValue()
      .intValue());
  }

  @Test(expected = ParserError.class) public void testEBooleanNotOK()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("z");
    p.expressionBoolean();
  }

  @SuppressWarnings({ "boxing" }) @Test public void testEBooleanOK0()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("true");
    final UASTIEBoolean r = p.expressionBoolean();
    Assert.assertEquals(true, r.getValue());
  }

  @SuppressWarnings({ "boxing" }) @Test public void testEBooleanOK1()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("false");
    final UASTIEBoolean r = p.expressionBoolean();
    Assert.assertEquals(false, r.getValue());
  }

  @Test public void testEConditionalOK_0()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p =
      ParserTest.makeStringInternalParser("if true then 23 else 24 end");
    final UASTIEConditional r = (UASTIEConditional) p.expression();
    Assert.assertTrue(r.getCondition() instanceof UASTIEBoolean);
    Assert.assertTrue(r.getLeft() instanceof UASTIEInteger);
    Assert.assertTrue(r.getRight() instanceof UASTIEInteger);

    Assert.assertEquals(23, ((UASTIEInteger) r.getLeft())
      .getValue()
      .intValue());
    Assert.assertEquals(24, ((UASTIEInteger) r.getRight())
      .getValue()
      .intValue());
  }

  @Test(expected = ParserError.class) public void testEIntegerNotOK()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("z");
    p.expressionInteger();
  }

  @Test public void testEIntegerOK()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("23");
    final UASTIEInteger r = p.expressionInteger();
    Assert.assertEquals(BigInteger.valueOf(23), r.getValue());
  }

  @Test(expected = ParserError.class) public void testELetNotOK_0()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p =
      ParserTest.makeStringInternalParser("let value k.k = 23; in 24 end");
    p.expression();
  }

  @Test(expected = ParserError.class) public void testELetNotOK_1()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p =
      ParserTest.makeStringInternalParser("let value k.k = 23; in 24 end");
    p.expression();
  }

  @Test(expected = ParserError.class) public void testELetNotOK_2()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p =
      ParserTest.makeStringInternalParser("let value K = 23; in 24 end");
    p.expression();
  }

  @Test public void testELetOK_0()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p =
      ParserTest
        .makeStringInternalParser("let value k : integer = 23; in 24 end");
    final UASTIELet r = (UASTIELet) p.expression();

    Assert.assertEquals(1, r.getBindings().size());
    final UASTIDValueLocal first = r.getBindings().get(0);

    final OptionType<UASTITypePath> asc = first.getAscription();
    Assert.assertTrue(asc.isSome());
    final Some<UASTITypePath> some = (Some<UASTITypePath>) asc;
    Assert.assertEquals("integer", some.get().getName().getActual());
    Assert.assertEquals(23, ((UASTIEInteger) first.getExpression())
      .getValue()
      .intValue());
    Assert.assertEquals(24, ((UASTIEInteger) r.getBody())
      .getValue()
      .intValue());
  }

  @Test public void testELetOK_1()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p =
      ParserTest.makeStringInternalParser("let value k = 23; in 24 end");
    final UASTIELet r = (UASTIELet) p.expression();

    Assert.assertEquals(1, r.getBindings().size());
    final UASTIDValueLocal first = r.getBindings().get(0);
    final OptionType<UASTITypePath> asc = first.getAscription();
    Assert.assertTrue(asc.isNone());
    Assert.assertEquals(23, ((UASTIEInteger) first.getExpression())
      .getValue()
      .intValue());
    Assert.assertEquals(24, ((UASTIEInteger) r.getBody())
      .getValue()
      .intValue());
  }

  @Test public void testELetOK_2()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p =
      ParserTest
        .makeStringInternalParser("let value k = 23; value z = 25; in 24 end");
    final UASTIELet r = (UASTIELet) p.expression();

    Assert.assertEquals(2, r.getBindings().size());

    {
      final UASTIDValueLocal b = r.getBindings().get(0);
      final OptionType<UASTITypePath> asc = b.getAscription();
      Assert.assertTrue(asc.isNone());
      Assert.assertEquals(23, ((UASTIEInteger) b.getExpression())
        .getValue()
        .intValue());
    }

    {
      final UASTIDValueLocal b = r.getBindings().get(1);
      final OptionType<UASTITypePath> asc = b.getAscription();
      Assert.assertTrue(asc.isNone());
      Assert.assertEquals(25, ((UASTIEInteger) b.getExpression())
        .getValue()
        .intValue());
    }

    Assert.assertEquals(24, ((UASTIEInteger) r.getBody())
      .getValue()
      .intValue());
  }

  @Test(expected = ParserError.class) public
    void
    testEMatrixColumnAccessNotOK_0()
      throws IOException,
        LexerError,
        ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("column m x");
    p.expression();
  }

  @Test public void testEMatrixColumnAccessOK_0()
    throws IOException,
      LexerError,
      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("column m 0");
    final UASTIEMatrixColumnAccess r =
      (UASTIEMatrixColumnAccess) p.expression();

    Assert.assertEquals("m", ((UASTIEVariable) r.getExpression())
      .getName()
      .show());
    Assert.assertEquals(0, r.getColumn().getValue().intValue());
  }

  @Test(expected = ParserError.class) public void testENewNotOK_0()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("new Y.X (1, 2, 3)");
    p.expression();
  }

  @Test(expected = ParserError.class) public void testENewNotOK_1()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("new X (1, 2, 3)");
    p.expression();
  }

  @Test(expected = ParserError.class) public void testENewNotOK_2()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("new f ()");
    p.expression();
  }

  @Test public void testENewOK_0()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("new Y.x (1, 2, 3)");
    final UASTIENew r = (UASTIENew) p.expression();
    Assert.assertTrue(r.getName().getModule().isSome());
    Assert.assertEquals("x", r.getName().getName().getActual());
    Assert.assertEquals(3, r.getArguments().size());

    Assert.assertEquals(1, ((UASTIEInteger) r.getArguments().get(0))
      .getValue()
      .intValue());
    Assert.assertEquals(2, ((UASTIEInteger) r.getArguments().get(1))
      .getValue()
      .intValue());
    Assert.assertEquals(3, ((UASTIEInteger) r.getArguments().get(2))
      .getValue()
      .intValue());
  }

  @Test public void testENewOK_1()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("new x (1, 2, 3)");
    final UASTIENew r = (UASTIENew) p.expression();
    Assert.assertTrue(r.getName().getModule().isNone());
    Assert.assertEquals("x", r.getName().getName().getActual());
    Assert.assertEquals(3, r.getArguments().size());

    Assert.assertEquals(1, ((UASTIEInteger) r.getArguments().get(0))
      .getValue()
      .intValue());
    Assert.assertEquals(2, ((UASTIEInteger) r.getArguments().get(1))
      .getValue()
      .intValue());
    Assert.assertEquals(3, ((UASTIEInteger) r.getArguments().get(2))
      .getValue()
      .intValue());
  }

  @Test(expected = ParserError.class) public void testERealNotOK()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("z");
    p.expressionReal();
  }

  @Test public void testERealOK()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("23.0");
    final UASTIEReal r = (UASTIEReal) p.expression();
    Assert.assertEquals(BigDecimal.valueOf(23.0), r.getValue());
  }

  @Test public void testERecordOK_0()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p =
      ParserTest.makeStringInternalParser("record t { a = 23, b = 17 }");
    final UASTIERecord r = (UASTIERecord) p.expression();
    Assert.assertEquals("t", r.getTypePath().getName().getActual());
    Assert.assertEquals(2, r.getAssignments().size());
    final UASTIRecordFieldAssignment as0 = r.getAssignments().get(0);
    Assert.assertEquals("a", as0.getName().getActual());
    Assert.assertEquals(23, ((UASTIEInteger) as0.getExpression())
      .getValue()
      .intValue());
    final UASTIRecordFieldAssignment as1 = r.getAssignments().get(1);
    Assert.assertEquals("b", as1.getName().getActual());
    Assert.assertEquals(17, ((UASTIEInteger) as1.getExpression())
      .getValue()
      .intValue());
  }

  @Test public void testERecordProjectionOK_0()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("x.y");
    final UASTIERecordProjection r = (UASTIERecordProjection) p.expression();

    final UASTIEVariable var = (UASTIEVariable) r.getExpression();
    Assert.assertEquals("x", var.getName().getName().getActual());
    Assert.assertEquals("y", r.getField().getActual());
  }

  @Test public void testERecordProjectionOK_1()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("P.x.y");
    final UASTIERecordProjection r = (UASTIERecordProjection) p.expression();

    final UASTIEVariable var = (UASTIEVariable) r.getExpression();

    Assert.assertEquals("P", ((Some<TokenIdentifierUpper>) var
      .getName()
      .getModule()).get().getActual());
    Assert.assertEquals("x", var.getName().getName().getActual());
    Assert.assertEquals("y", r.getField().getActual());
  }

  @Test(expected = ParserError.class) public void testESwizzleNotOK_0()
    throws IOException,
      LexerError,
      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("23 [X]");
    p.expression();
  }

  @Test(expected = ParserError.class) public void testESwizzleNotOK_1()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("23 [x.y]");
    p.expression();
  }

  @Test(expected = ParserError.class) public void testESwizzleNotOK_2()
    throws IOException,
      LexerError,
      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("23 []");
    p.expression();
  }

  @Test public void testESwizzleOK_0()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("23 [x y z]");
    final UASTIESwizzle r = (UASTIESwizzle) p.expression();

    Assert.assertEquals(23, ((UASTIEInteger) r.getExpression())
      .getValue()
      .intValue());
    Assert.assertEquals(3, r.getFields().size());
    Assert.assertEquals("x", r.getFields().get(0).getActual());
    Assert.assertEquals("y", r.getFields().get(1).getActual());
    Assert.assertEquals("z", r.getFields().get(2).getActual());
  }

  @Test public void testESwizzleOK_1()
    throws IOException,
      LexerError,

      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("23 [x]");
    final UASTIESwizzle r = (UASTIESwizzle) p.expression();

    Assert.assertEquals(23, ((UASTIEInteger) r.getExpression())
      .getValue()
      .intValue());
    Assert.assertEquals(1, r.getFields().size());
    Assert.assertEquals("x", r.getFields().get(0).getActual());
  }

  @Test public void testESwizzleOK_2()
    throws IOException,
      LexerError,
      ParserError
  {
    final Parser p =
      ParserTest.makeStringInternalParser("23 [x y z] [z y x]");
    final UASTIESwizzle r = (UASTIESwizzle) p.expression();

    final UASTIESwizzle inner = (UASTIESwizzle) r.getExpression();

    Assert.assertEquals(23, ((UASTIEInteger) inner.getExpression())
      .getValue()
      .intValue());

    Assert.assertEquals(3, inner.getFields().size());
    Assert.assertEquals("x", inner.getFields().get(0).getActual());
    Assert.assertEquals("y", inner.getFields().get(1).getActual());
    Assert.assertEquals("z", inner.getFields().get(2).getActual());

    Assert.assertEquals(3, r.getFields().size());
    Assert.assertEquals("z", r.getFields().get(0).getActual());
    Assert.assertEquals("y", r.getFields().get(1).getActual());
    Assert.assertEquals("x", r.getFields().get(2).getActual());
  }

  @Test public void testEVariableOK_0()
    throws IOException,
      LexerError,
      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("x");
    final UASTIEVariable r = (UASTIEVariable) p.expression();
    Assert.assertTrue(r.getName().getModule().isNone());
    Assert.assertEquals("x", r.getName().getName().getActual());
  }

  @Test public void testEVariableOK_1()
    throws IOException,
      LexerError,
      ParserError
  {
    final Parser p = ParserTest.makeStringInternalParser("Y.x");
    final UASTIEVariable r = (UASTIEVariable) p.expression();
    Assert.assertTrue(r.getName().getModule().isSome());
    Assert.assertEquals("x", r.getName().getName().getActual());
  }
}
