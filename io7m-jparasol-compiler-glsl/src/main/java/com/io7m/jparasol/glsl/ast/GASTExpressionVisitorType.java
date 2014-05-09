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

package com.io7m.jparasol.glsl.ast;

import java.util.List;

import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEApplication;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEApplicationExternal;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEBinaryOp.GASTEBinaryOpDivide;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEBinaryOp.GASTEBinaryOpEqual;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEBinaryOp.GASTEBinaryOpGreaterThan;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEBinaryOp.GASTEBinaryOpGreaterThanOrEqual;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEBinaryOp.GASTEBinaryOpLesserThan;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEBinaryOp.GASTEBinaryOpLesserThanOrEqual;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEBinaryOp.GASTEBinaryOpMultiply;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEBinaryOp.GASTEBinaryOpPlus;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEBinaryOp.GASTEBinaryOpSubtract;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEBoolean;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEConstruction;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEFloat;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEInteger;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEProjection;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTESwizzle;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEUnaryOp.GASTEUnaryOpNegate;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEVariable;

/**
 * The type of GLSL expression visitors.
 * 
 * @param <A>
 *          The type of returned values
 * @param <E>
 *          The type of exceptions raised
 */

public interface GASTExpressionVisitorType<A, E extends Throwable>
{
  /**
   * Visit an (external) function application.
   * 
   * @param arguments
   *          The transformed arguments
   * @param e
   *          The expression
   * @return A value of <code>A</code>
   * @throws E
   *           If required
   */

  A expressionApplicationExternalVisit(
    final List<A> arguments,
    final GASTEApplicationExternal e)
    throws E;

  /**
   * Prepare to visit an (external) function application.
   * 
   * @param e
   *          The expression
   * @throws E
   *           If required
   */

  void expressionApplicationExternalVisitPre(
    final GASTEApplicationExternal e)
    throws E;

  /**
   * Visit a function application.
   * 
   * @param arguments
   *          The transformed arguments
   * @param e
   *          The expression
   * @return A value of <code>A</code>
   * @throws E
   *           If required
   */

  A expressionApplicationVisit(
    final List<A> arguments,
    final GASTEApplication e)
    throws E;

  /**
   * Prepare to visit a function application.
   * 
   * @param e
   *          The expression
   * @throws E
   *           If required
   */

  void expressionApplicationVisitPre(
    final GASTEApplication e)
    throws E;

  /**
   * Visit an arithmetic expression.
   * 
   * @param left
   *          The left expression
   * @param right
   *          The right expression
   * @param e
   *          The expression
   * @return A value of <code>A</code>
   * @throws E
   *           If required
   */

  A expressionBinaryOpDivideVisit(
    final A left,
    final A right,
    final GASTEBinaryOpDivide e)
    throws E;

  /**
   * Prepare to visit an arithmetic expression.
   * 
   * @param e
   *          The expression
   * @throws E
   *           If required
   */

  void expressionBinaryOpDivideVisitPre(
    final GASTEBinaryOpDivide e)
    throws E;

  /**
   * Visit a comparison.
   * 
   * @param left
   *          The left expression
   * @param right
   *          The right expression
   * @param e
   *          The expression
   * @return A value of <code>A</code>
   * @throws E
   *           If required
   */

  A expressionBinaryOpEqualVisit(
    final A left,
    final A right,
    final GASTEBinaryOpEqual e)
    throws E;

  /**
   * Prepare to visit a comparison.
   * 
   * @param e
   *          The expression
   * @throws E
   *           If required
   */

  void expressionBinaryOpEqualVisitPre(
    final GASTEBinaryOpEqual e)
    throws E;

  /**
   * Visit a comparison.
   * 
   * @param left
   *          The left expression
   * @param right
   *          The right expression
   * @param e
   *          The expression
   * @return A value of <code>A</code>
   * @throws E
   *           If required
   */

  A expressionBinaryOpGreaterThanOrEqualVisit(
    final A left,
    final A right,
    final GASTEBinaryOpGreaterThanOrEqual e)
    throws E;

  /**
   * Prepare to visit a comparison.
   * 
   * @param e
   *          The expression
   * @throws E
   *           If required
   */

  void expressionBinaryOpGreaterThanOrEqualVisitPre(
    final GASTEBinaryOpGreaterThanOrEqual e)
    throws E;

  /**
   * Visit a comparison.
   * 
   * @param left
   *          The left expression
   * @param right
   *          The right expression
   * @param e
   *          The expression
   * @return A value of <code>A</code>
   * @throws E
   *           If required
   */

  A expressionBinaryOpGreaterThanVisit(
    final A left,
    final A right,
    final GASTEBinaryOpGreaterThan e)
    throws E;

  /**
   * Prepare to visit a comparison.
   * 
   * @param e
   *          The expression
   * @throws E
   *           If required
   */

  void expressionBinaryOpGreaterThanVisitPre(
    final GASTEBinaryOpGreaterThan e)
    throws E;

  /**
   * Visit a comparison.
   * 
   * @param left
   *          The left expression
   * @param right
   *          The right expression
   * @param e
   *          The expression
   * @return A value of <code>A</code>
   * @throws E
   *           If required
   */

  A expressionBinaryOpLesserThanOrEqualVisit(
    final A left,
    final A right,
    final GASTEBinaryOpLesserThanOrEqual e)
    throws E;

  /**
   * Prepare to visit a comparison.
   * 
   * @param e
   *          The expression
   * @throws E
   *           If required
   */

  void expressionBinaryOpLesserThanOrEqualVisitPre(
    final GASTEBinaryOpLesserThanOrEqual e)
    throws E;

  /**
   * Visit a comparison.
   * 
   * @param left
   *          The left expression
   * @param right
   *          The right expression
   * @param e
   *          The expression
   * @return A value of <code>A</code>
   * @throws E
   *           If required
   */

  A expressionBinaryOpLesserThanVisit(
    final A left,
    final A right,
    final GASTEBinaryOpLesserThan e)
    throws E;

  /**
   * Prepare to visit a comparison.
   * 
   * @param e
   *          The expression
   * @throws E
   *           If required
   */

  void expressionBinaryOpLesserThanVisitPre(
    final GASTEBinaryOpLesserThan e)
    throws E;

  /**
   * Visit an arithmetic expression.
   * 
   * @param left
   *          The left expression
   * @param right
   *          The right expression
   * @param e
   *          The expression
   * @return A value of <code>A</code>
   * @throws E
   *           If required
   */

  A expressionBinaryOpMultiplyVisit(
    final A left,
    final A right,
    final GASTEBinaryOpMultiply e)
    throws E;

  /**
   * Prepare to visit an arithmetic expression.
   * 
   * @param e
   *          The expression
   * @throws E
   *           If required
   */

  void expressionBinaryOpMultiplyVisitPre(
    final GASTEBinaryOpMultiply e)
    throws E;

  /**
   * Visit an arithmetic expression.
   * 
   * @param left
   *          The left expression
   * @param right
   *          The right expression
   * @param e
   *          The expression
   * @return A value of <code>A</code>
   * @throws E
   *           If required
   */

  A expressionBinaryOpPlusVisit(
    final A left,
    final A right,
    final GASTEBinaryOpPlus e)
    throws E;

  /**
   * Prepare to visit an arithmetic expression.
   * 
   * @param e
   *          The expression
   * @throws E
   *           If required
   */

  void expressionBinaryOpPlusVisitPre(
    final GASTEBinaryOpPlus e)
    throws E;

  /**
   * Visit an arithmetic expression.
   * 
   * @param left
   *          The left expression
   * @param right
   *          The right expression
   * @param e
   *          The expression
   * @return A value of <code>A</code>
   * @throws E
   *           If required
   */

  A expressionBinaryOpSubtractVisit(
    final A left,
    final A right,
    final GASTEBinaryOpSubtract e)
    throws E;

  /**
   * Prepare to visit an arithmetic expression.
   * 
   * @param e
   *          The expression
   * @throws E
   *           If required
   */

  void expressionBinaryOpSubtractVisitPre(
    final GASTEBinaryOpSubtract e)
    throws E;

  /**
   * Visit a constant.
   * 
   * @param e
   *          The constant
   * @return A value of <code>A</code>
   * @throws E
   *           If required
   */

  A expressionBooleanVisit(
    final GASTEBoolean e)
    throws E;

  /**
   * Visit a construction expression.
   * 
   * @param e
   *          The expression
   * @return A value of <code>A</code>
   * @throws E
   *           If required
   */

  A expressionConstructionVisit(
    final List<A> arguments,
    final GASTEConstruction e)
    throws E;

  /**
   * Prepare to visit a construction expression.
   * 
   * @param e
   *          The expression
   * @throws E
   *           If required
   */

  void expressionConstructionVisitPre(
    final GASTEConstruction e)
    throws E;

  /**
   * Visit a constant.
   * 
   * @param e
   *          The constant
   * @return A value of <code>A</code>
   * @throws E
   *           If required
   */

  A expressionFloatVisit(
    final GASTEFloat e)
    throws E;

  /**
   * Visit a constant.
   * 
   * @param e
   *          The constant
   * @return A value of <code>A</code>
   * @throws E
   *           If required
   */

  A expressionIntegerVisit(
    final GASTEInteger e)
    throws E;

  /**
   * Visit a record projection.
   * 
   * @param body
   *          The left-hand side of the expression
   * @param e
   *          The expression
   * @return A value of <code>A</code>
   * @throws E
   *           If required
   */

  A expressionProjectionVisit(
    final A body,
    final GASTEProjection e)
    throws E;

  /**
   * Prepare to visit a record projection.
   * 
   * @param e
   *          The expression
   * @throws E
   *           If required
   */

  void expressionProjectionVisitPre(
    final GASTEProjection e)
    throws E;

  /**
   * Visit a swizzle expression.
   * 
   * @param body
   *          The left-hand side of the expression
   * @param e
   *          The expression
   * @return A value of <code>A</code>
   * @throws E
   *           If required
   */

  A expressionSwizzleVisit(
    final A body,
    final GASTESwizzle e)
    throws E;

  /**
   * Prepare to visit a swizzle expression.
   * 
   * @param e
   *          The expression
   * @throws E
   *           If required
   */

  void expressionSwizzleVisitPre(
    final GASTESwizzle e)
    throws E;

  /**
   * Visit a unary negation.
   * 
   * @param body
   *          The right-hand side of the expression
   * @param e
   *          The expression
   * @return A value of <code>A</code>
   * @throws E
   *           If required
   */

  A expressionUnaryOpNegateVisit(
    final A body,
    final GASTEUnaryOpNegate e)
    throws E;

  /**
   * Prepare to visit a unary negation.
   * 
   * @param e
   *          The expression
   * @throws E
   *           If required
   */

  void expressionUnaryOpNegateVisitPre(
    final GASTEUnaryOpNegate e)
    throws E;

  /**
   * Visit a variable.
   * 
   * @param e
   *          The variable
   * @return A value of <code>A</code>
   * @throws E
   *           If required
   */

  A expressionVariableVisit(
    final GASTEVariable e)
    throws E;
}
