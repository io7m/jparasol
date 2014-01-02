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

package com.io7m.jparasol.glsl.ast;

import java.util.List;

import javax.annotation.Nonnull;

import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEApplication;
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

public interface GASTExpressionVisitor<A, E extends Throwable>
{
  public A expressionApplicationVisit(
    final @Nonnull List<A> arguments,
    final @Nonnull GASTEApplication e)
    throws E;

  public void expressionApplicationVisitPre(
    final @Nonnull GASTEApplication e)
    throws E;

  public A expressionBinaryOpDivideVisit(
    final @Nonnull A left,
    final @Nonnull A right,
    final @Nonnull GASTEBinaryOpDivide e)
    throws E;

  public void expressionBinaryOpDivideVisitPre(
    final @Nonnull GASTEBinaryOpDivide e)
    throws E;

  public A expressionBinaryOpEqualVisit(
    final @Nonnull A left,
    final @Nonnull A right,
    final @Nonnull GASTEBinaryOpEqual e)
    throws E;

  public void expressionBinaryOpEqualVisitPre(
    final @Nonnull GASTEBinaryOpEqual e)
    throws E;

  public A expressionBinaryOpGreaterThanOrEqualVisit(
    final @Nonnull A left,
    final @Nonnull A right,
    final @Nonnull GASTEBinaryOpGreaterThanOrEqual e)
    throws E;

  public void expressionBinaryOpGreaterThanOrEqualVisitPre(
    final @Nonnull GASTEBinaryOpGreaterThanOrEqual e)
    throws E;

  public A expressionBinaryOpGreaterThanVisit(
    final @Nonnull A left,
    final @Nonnull A right,
    final @Nonnull GASTEBinaryOpGreaterThan e)
    throws E;

  public void expressionBinaryOpGreaterThanVisitPre(
    final @Nonnull GASTEBinaryOpGreaterThan e)
    throws E;

  public A expressionBinaryOpLesserThanOrEqualVisit(
    final @Nonnull A left,
    final @Nonnull A right,
    final @Nonnull GASTEBinaryOpLesserThanOrEqual e)
    throws E;

  public void expressionBinaryOpLesserThanOrEqualVisitPre(
    final @Nonnull GASTEBinaryOpLesserThanOrEqual e)
    throws E;

  public A expressionBinaryOpLesserThanVisit(
    final @Nonnull A left,
    final @Nonnull A right,
    final @Nonnull GASTEBinaryOpLesserThan e)
    throws E;

  public void expressionBinaryOpLesserThanVisitPre(
    final @Nonnull GASTEBinaryOpLesserThan e)
    throws E;

  public A expressionBinaryOpMultiplyVisit(
    final @Nonnull A left,
    final @Nonnull A right,
    final @Nonnull GASTEBinaryOpMultiply e)
    throws E;

  public void expressionBinaryOpMultiplyVisitPre(
    final @Nonnull GASTEBinaryOpMultiply e)
    throws E;

  public A expressionBinaryOpPlusVisit(
    final @Nonnull A left,
    final @Nonnull A right,
    final @Nonnull GASTEBinaryOpPlus e)
    throws E;

  public void expressionBinaryOpPlusVisitPre(
    final @Nonnull GASTEBinaryOpPlus e)
    throws E;

  public A expressionBinaryOpSubtractVisit(
    final @Nonnull A left,
    final @Nonnull A right,
    final @Nonnull GASTEBinaryOpSubtract e)
    throws E;

  public void expressionBinaryOpSubtractVisitPre(
    final @Nonnull GASTEBinaryOpSubtract e)
    throws E;

  public A expressionBooleanVisit(
    final @Nonnull GASTEBoolean e)
    throws E;

  public A expressionConstructionVisit(
    final @Nonnull List<A> arguments,
    final @Nonnull GASTEConstruction e)
    throws E;

  public void expressionConstructionVisitPre(
    final @Nonnull GASTEConstruction e)
    throws E;

  public A expressionFloatVisit(
    final @Nonnull GASTEFloat e)
    throws E;

  public A expressionIntegerVisit(
    final @Nonnull GASTEInteger e)
    throws E;

  public A expressionProjectionVisit(
    final @Nonnull A body,
    final @Nonnull GASTEProjection e)
    throws E;

  public void expressionProjectionVisitPre(
    final @Nonnull GASTEProjection e)
    throws E;

  public A expressionSwizzleVisit(
    final @Nonnull A body,
    final @Nonnull GASTESwizzle e)
    throws E;

  public void expressionSwizzleVisitPre(
    final @Nonnull GASTESwizzle e)
    throws E;

  public A expressionUnaryOpNegateVisit(
    final @Nonnull A body,
    final @Nonnull GASTEUnaryOpNegate e)
    throws E;

  public void expressionUnaryOpNegateVisitPre(
    final @Nonnull GASTEUnaryOpNegate e)
    throws E;

  public A expressionVariableVisit(
    final @Nonnull GASTEVariable e)
    throws E;
}
