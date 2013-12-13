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
import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Option;
import com.io7m.jaux.functional.Option.Some;
import com.io7m.jparasol.lexer.Lexer;
import com.io7m.jparasol.lexer.LexerError;
import com.io7m.jparasol.lexer.Token.TokenIdentifierUpper;
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
import com.io7m.jparasol.untyped.ast.initial.UASTIStatusUnchecked;
import com.io7m.jparasol.untyped.ast.initial.UASTITypePath;
import com.io7m.jparasol.untyped.ast.initial.UASTIValuePath;

public class ParserTest
{
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
    final UASTIEApplication<UASTIStatusUnchecked> r =
      (UASTIEApplication<UASTIStatusUnchecked>) p
        .expressionVariableOrApplication();
    Assert.assertTrue(r.getName().getModule().isSome());
    Assert.assertEquals("x", r.getName().getName().getActual());
    Assert.assertEquals(3, r.getArguments().size());

    Assert.assertEquals(1, ((UASTIEInteger<UASTIStatusUnchecked>) r
      .getArguments()
      .get(0)).getValue().intValue());
    Assert.assertEquals(2, ((UASTIEInteger<UASTIStatusUnchecked>) r
      .getArguments()
      .get(1)).getValue().intValue());
    Assert.assertEquals(3, ((UASTIEInteger<UASTIStatusUnchecked>) r
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
    final UASTIEApplication<UASTIStatusUnchecked> r =
      (UASTIEApplication<UASTIStatusUnchecked>) p
        .expressionVariableOrApplication();
    Assert.assertTrue(r.getName().getModule().isNone());
    Assert.assertEquals("x", r.getName().getName().getActual());
    Assert.assertEquals(3, r.getArguments().size());

    Assert.assertEquals(1, ((UASTIEInteger<UASTIStatusUnchecked>) r
      .getArguments()
      .get(0)).getValue().intValue());
    Assert.assertEquals(2, ((UASTIEInteger<UASTIStatusUnchecked>) r
      .getArguments()
      .get(1)).getValue().intValue());
    Assert.assertEquals(3, ((UASTIEInteger<UASTIStatusUnchecked>) r
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
    final UASTIEBoolean<UASTIStatusUnchecked> r = p.expressionBoolean();
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
    final UASTIEBoolean<UASTIStatusUnchecked> r = p.expressionBoolean();
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
    final UASTIEConditional<UASTIStatusUnchecked> r =
      (UASTIEConditional<UASTIStatusUnchecked>) p.expression();
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
    final UASTIEInteger<UASTIStatusUnchecked> r = p.expressionInteger();
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
    final UASTIELet<UASTIStatusUnchecked> r =
      (UASTIELet<UASTIStatusUnchecked>) p.expression();

    Assert.assertEquals(1, r.getBindings().size());
    final UASTIDValueLocal<UASTIStatusUnchecked> first =
      r.getBindings().get(0);

    final Option<UASTITypePath> asc = first.getAscription();
    Assert.assertTrue(asc.isSome());
    final Some<UASTITypePath> some = (Option.Some<UASTITypePath>) asc;
    Assert.assertEquals("integer", some.value.getName().getActual());
    Assert.assertEquals(23, ((UASTIEInteger<UASTIStatusUnchecked>) first
      .getExpression()).getValue().intValue());
    Assert.assertEquals(24, ((UASTIEInteger<UASTIStatusUnchecked>) r
      .getBody()).getValue().intValue());
  }

  @SuppressWarnings("static-method") @Test public void testELetOK_1()
    throws IOException,
      LexerError,
      ConstraintError,
      ParserError
  {
    final Parser p =
      ParserTest.makeStringInternalParser("let value k = 23; in 24 end");
    final UASTIELet<UASTIStatusUnchecked> r =
      (UASTIELet<UASTIStatusUnchecked>) p.expression();

    Assert.assertEquals(1, r.getBindings().size());
    final UASTIDValueLocal<UASTIStatusUnchecked> first =
      r.getBindings().get(0);
    final Option<UASTITypePath> asc = first.getAscription();
    Assert.assertTrue(asc.isNone());
    Assert.assertEquals(23, ((UASTIEInteger<UASTIStatusUnchecked>) first
      .getExpression()).getValue().intValue());
    Assert.assertEquals(24, ((UASTIEInteger<UASTIStatusUnchecked>) r
      .getBody()).getValue().intValue());
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
    final UASTIELet<UASTIStatusUnchecked> r =
      (UASTIELet<UASTIStatusUnchecked>) p.expression();

    Assert.assertEquals(2, r.getBindings().size());

    {
      final UASTIDValueLocal<UASTIStatusUnchecked> b = r.getBindings().get(0);
      final Option<UASTITypePath> asc = b.getAscription();
      Assert.assertTrue(asc.isNone());
      Assert.assertEquals(23, ((UASTIEInteger<UASTIStatusUnchecked>) b
        .getExpression()).getValue().intValue());
    }

    {
      final UASTIDValueLocal<UASTIStatusUnchecked> b = r.getBindings().get(1);
      final Option<UASTITypePath> asc = b.getAscription();
      Assert.assertTrue(asc.isNone());
      Assert.assertEquals(25, ((UASTIEInteger<UASTIStatusUnchecked>) b
        .getExpression()).getValue().intValue());
    }

    Assert.assertEquals(24, ((UASTIEInteger<UASTIStatusUnchecked>) r
      .getBody()).getValue().intValue());
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
    final UASTIENew<UASTIStatusUnchecked> r =
      (UASTIENew<UASTIStatusUnchecked>) p.expression();
    Assert.assertTrue(r.getName().getModule().isSome());
    Assert.assertEquals("x", r.getName().getName().getActual());
    Assert.assertEquals(3, r.getArguments().size());

    Assert.assertEquals(1, ((UASTIEInteger<UASTIStatusUnchecked>) r
      .getArguments()
      .get(0)).getValue().intValue());
    Assert.assertEquals(2, ((UASTIEInteger<UASTIStatusUnchecked>) r
      .getArguments()
      .get(1)).getValue().intValue());
    Assert.assertEquals(3, ((UASTIEInteger<UASTIStatusUnchecked>) r
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
    final UASTIENew<UASTIStatusUnchecked> r =
      (UASTIENew<UASTIStatusUnchecked>) p.expression();
    Assert.assertTrue(r.getName().getModule().isNone());
    Assert.assertEquals("x", r.getName().getName().getActual());
    Assert.assertEquals(3, r.getArguments().size());

    Assert.assertEquals(1, ((UASTIEInteger<UASTIStatusUnchecked>) r
      .getArguments()
      .get(0)).getValue().intValue());
    Assert.assertEquals(2, ((UASTIEInteger<UASTIStatusUnchecked>) r
      .getArguments()
      .get(1)).getValue().intValue());
    Assert.assertEquals(3, ((UASTIEInteger<UASTIStatusUnchecked>) r
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
    final UASTIEReal<UASTIStatusUnchecked> r =
      (UASTIEReal<UASTIStatusUnchecked>) p.expression();
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
    final UASTIERecord<UASTIStatusUnchecked> r =
      (UASTIERecord<UASTIStatusUnchecked>) p.expression();
    Assert.assertEquals("t", r.getTypePath().getName().getActual());
    Assert.assertEquals(2, r.getAssignments().size());
    final UASTIRecordFieldAssignment<UASTIStatusUnchecked> as0 =
      r.getAssignments().get(0);
    Assert.assertEquals("a", as0.getName().getActual());
    Assert.assertEquals(23, ((UASTIEInteger<UASTIStatusUnchecked>) as0
      .getExpression()).getValue().intValue());
    final UASTIRecordFieldAssignment<UASTIStatusUnchecked> as1 =
      r.getAssignments().get(1);
    Assert.assertEquals("b", as1.getName().getActual());
    Assert.assertEquals(17, ((UASTIEInteger<UASTIStatusUnchecked>) as1
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
    final UASTIERecordProjection<UASTIStatusUnchecked> r =
      (UASTIERecordProjection<UASTIStatusUnchecked>) p.expression();

    final UASTIEVariable<UASTIStatusUnchecked> var =
      (UASTIEVariable<UASTIStatusUnchecked>) r.getExpression();
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
    final UASTIERecordProjection<UASTIStatusUnchecked> r =
      (UASTIERecordProjection<UASTIStatusUnchecked>) p.expression();

    final UASTIEVariable<UASTIStatusUnchecked> var =
      (UASTIEVariable<UASTIStatusUnchecked>) r.getExpression();

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
    final UASTIESwizzle<UASTIStatusUnchecked> r =
      (UASTIESwizzle<UASTIStatusUnchecked>) p.expression();

    Assert.assertEquals(23, ((UASTIEInteger<UASTIStatusUnchecked>) r
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
    final UASTIESwizzle<UASTIStatusUnchecked> r =
      (UASTIESwizzle<UASTIStatusUnchecked>) p.expression();

    Assert.assertEquals(23, ((UASTIEInteger<UASTIStatusUnchecked>) r
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
    final UASTIESwizzle<UASTIStatusUnchecked> r =
      (UASTIESwizzle<UASTIStatusUnchecked>) p.expression();

    final UASTIESwizzle<UASTIStatusUnchecked> inner =
      (UASTIESwizzle<UASTIStatusUnchecked>) r.getExpression();

    Assert.assertEquals(23, ((UASTIEInteger<UASTIStatusUnchecked>) inner
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
    final UASTIEVariable<UASTIStatusUnchecked> r =
      (UASTIEVariable<UASTIStatusUnchecked>) p.expression();
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
    final UASTIEVariable<UASTIStatusUnchecked> r =
      (UASTIEVariable<UASTIStatusUnchecked>) p.expression();
    Assert.assertTrue(r.getName().getModule().isSome());
    Assert.assertEquals("x", r.getName().getName().getActual());
  }
}
