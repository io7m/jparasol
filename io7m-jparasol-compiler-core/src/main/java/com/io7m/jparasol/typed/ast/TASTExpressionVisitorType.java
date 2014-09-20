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

package com.io7m.jparasol.typed.ast;

import java.util.List;

import com.io7m.jnull.Nullable;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTEApplication;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTEBoolean;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTEConditional;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTEInteger;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTELet;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTEMatrixColumnAccess;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTENew;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTEReal;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTERecord;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTERecordProjection;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTESwizzle;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTEVariable;

/**
 * Visit a typed expression.
 *
 * @param <A>
 *          The type of returned values
 * @param <L>
 *          The type of expression local declarations
 * @param <E>
 *          The type of raised exceptions
 */

public interface TASTExpressionVisitorType<A, L, E extends Throwable>
{
  /**
   * <p>
   * Visit a function application.
   * <p>
   * <p>
   * The @Nullable arguments will be <code>null</code> iff the last call to
   * {@link TASTExpressionVisitorType#expressionVisitApplicationPre(TASTEApplication)}
   * returned <code>false</code>.
   * </p>
   *
   * @param arguments
   *          The arguments
   * @param e
   *          The application
   * @return A value of <code>A</code>
   * @throws E
   *           If required
   */

  A expressionVisitApplication(
    final @Nullable List<A> arguments,
    final TASTEApplication e)
    throws E;

  /**
   * Prepare to visit a function application.
   *
   * @param e
   *          The application
   * @return <code>true</code> if visiting should proceed
   * @throws E
   *           If required
   */

  boolean expressionVisitApplicationPre(
    final TASTEApplication e)
    throws E;

  /**
   * Visit a boolean constant.
   *
   * @param e
   *          The expression
   * @return A value of <code>A</code>
   * @throws E
   *           If required
   */

  A expressionVisitBoolean(
    final TASTEBoolean e)
    throws E;

  /**
   * <p>
   * Visit a function application.
   * <p>
   * <p>
   * The @Nullable arguments will be <code>null</code> iff the last call to
   * {@link #expressionVisitConditionalPre(TASTEConditional)} returned
   * <code>false</code>.
   * </p>
   *
   * @param condition
   *          The condition
   * @param left
   *          The left branch
   * @param right
   *          The right branch
   * @param e
   *          The expression
   * @return A value of <code>A</code>
   * @throws E
   *           If required
   */

  A expressionVisitConditional(
    final @Nullable A condition,
    final @Nullable A left,
    final @Nullable A right,
    final TASTEConditional e)
    throws E;

  /**
   * Finish visiting the condition of a conditional expression.
   *
   * @param e
   *          The expression
   * @throws E
   *           If required
   */

  void expressionVisitConditionalConditionPost(
    final TASTEConditional e)
    throws E;

  /**
   * Prepare to visit the condition of a conditional expression.
   *
   * @param e
   *          The expression
   * @throws E
   *           If required
   */

  void expressionVisitConditionalConditionPre(
    final TASTEConditional e)
    throws E;

  /**
   * Finish visiting the left branch of a conditional expression.
   *
   * @param e
   *          The expression
   * @throws E
   *           If required
   */

  void expressionVisitConditionalLeftPost(
    final TASTEConditional e)
    throws E;

  /**
   * Prepare to visit the left branch of a conditional expression.
   *
   * @param e
   *          The expression
   * @throws E
   *           If required
   */

  void expressionVisitConditionalLeftPre(
    final TASTEConditional e)
    throws E;

  /**
   * Prepare to a conditional expression.
   *
   * @param e
   *          The expression
   * @return <code>true</code> if visiting is desired
   * @throws E
   *           If required
   */

  boolean expressionVisitConditionalPre(
    final TASTEConditional e)
    throws E;

  /**
   * Finish visiting the right branch of a conditional expression.
   *
   * @param e
   *          The expression
   * @throws E
   *           If required
   */

  void expressionVisitConditionalRightPost(
    final TASTEConditional e)
    throws E;

  /**
   * Prepare to visit the right branch of a conditional expression.
   *
   * @param e
   *          The expression
   * @throws E
   *           If required
   */

  void expressionVisitConditionalRightPre(
    final TASTEConditional e)
    throws E;

  /**
   * Visit an integer constant.
   *
   * @param e
   *          The expression
   * @return A value of <code>A</code>
   * @throws E
   *           If required
   */

  A expressionVisitInteger(
    final TASTEInteger e)
    throws E;

  /**
   * <p>
   * Visit a let expression.
   * <p>
   * <p>
   * The @Nullable arguments will be <code>null</code> iff the last call to
   * {@link #expressionVisitLetPre(TASTELet)} returned <code>null</code>.
   * </p>
   *
   * @param bindings
   *          The transformed bindings
   * @param body
   *          The transformed body of the expression
   * @param e
   *          The original expression
   * @return A value of <code>A</code>
   * @throws E
   *           If required
   */

  A expressionVisitLet(
    final @Nullable List<L> bindings,
    final @Nullable A body,
    final TASTELet e)
    throws E;

  /**
   * Prepare to visit a let expression.
   *
   * @param e
   *          The expression
   * @return A visitor, or <code>null</code> if visiting is not desired
   * @throws E
   *           If required
   */

  @Nullable TASTLocalLevelVisitorType<L, E> expressionVisitLetPre(
    final TASTELet e)
    throws E;

  /**
   * Visit a matrix column access expression.
   *
   * @param body
   *          The transformed left-hand side
   * @param e
   *          The expression
   * @return A value of <code>A</code>
   * @throws E
   *           If required
   */

  A expressionVisitMatrixColumnAccess(
    final @Nullable A body,
    final TASTEMatrixColumnAccess e)
    throws E;

  /**
   * Prepare to visit a matrix column access expression.
   *
   * @param e
   *          The expression
   * @return <code>true</code> if visiting should proceed.
   * @throws E
   *           If required
   */

  boolean expressionVisitMatrixColumnAccessPre(
    final TASTEMatrixColumnAccess e)
    throws E;

  /**
   * <p>
   * Visit a new expression.
   * <p>
   * <p>
   * The @Nullable arguments will be <code>null</code> iff the last call to
   * {@link #expressionVisitNewPre(TASTENew)} returned <code>false</code>.
   * </p>
   *
   * @param arguments
   *          The list of transformed arguments
   * @param e
   *          The expression
   * @return <code>true</code> if visiting should proceed.
   * @throws E
   *           If required
   */

  A expressionVisitNew(
    final @Nullable List<A> arguments,
    final TASTENew e)
    throws E;

  /**
   * Prepare to visit a new expression.
   *
   * @param e
   *          The projection
   * @return <code>true</code> if visiting should proceed.
   * @throws E
   *           If required
   */

  boolean expressionVisitNewPre(
    final TASTENew e)
    throws E;

  /**
   * Visit a floating point constant.
   *
   * @param e
   *          The expression
   * @return A value of <code>A</code>
   * @throws E
   *           If required
   */

  A expressionVisitReal(
    final TASTEReal e)
    throws E;

  /**
   * Visit a record expression.
   *
   * @param e
   *          The expression
   * @return A value of <code>A</code>
   * @throws E
   *           If required
   */

  A expressionVisitRecord(
    final TASTERecord e)
    throws E;

  /**
   * <p>
   * Visit a record expression.
   * <p>
   * <p>
   * The @Nullable arguments will be <code>null</code> iff the last call to
   * {@link #expressionVisitRecordProjectionPre(TASTERecordProjection)}
   * returned <code>false</code>.
   * </p>
   *
   * @param body
   *          The transformed left-hand side
   * @param e
   *          The projection
   * @return A value of <code>A</code>
   * @throws E
   *           If required
   */

  A expressionVisitRecordProjection(
    final @Nullable A body,
    final TASTERecordProjection e)
    throws E;

  /**
   * Prepare to visit a record projection.
   *
   * @param e
   *          The projection
   * @return <code>true</code> if visiting should proceed.
   * @throws E
   *           If required
   */

  boolean expressionVisitRecordProjectionPre(
    final @Nullable TASTERecordProjection e)
    throws E;

  /**
   * Visit a swizzle expression.
   *
   * @param body
   *          The transformed left-hand side
   * @param e
   *          The expression
   * @return A value of <code>A</code>
   * @throws E
   *           If required
   */

  A expressionVisitSwizzle(
    final @Nullable A body,
    final TASTESwizzle e)
    throws E;

  /**
   * Prepare to visit a swizzle expression.
   *
   * @param e
   *          The expression
   * @return <code>true</code> if visiting should proceed.
   * @throws E
   *           If required
   */

  boolean expressionVisitSwizzlePre(
    final TASTESwizzle e)
    throws E;

  /**
   * Visit a variable.
   *
   * @param e
   *          The expression
   * @return A value of <code>A</code>
   * @throws E
   *           If required
   */

  A expressionVisitVariable(
    final TASTEVariable e)
    throws E;
}
