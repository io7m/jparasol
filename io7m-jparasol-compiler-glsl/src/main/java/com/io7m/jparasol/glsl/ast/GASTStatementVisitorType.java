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

import com.io7m.jfunctional.Pair;
import com.io7m.jnull.Nullable;

/**
 * The type of GLSL statement visitors.
 *
 * @param <A>
 *          The type of returned values
 * @param <C>
 *          The type of transformed switch cases
 * @param <E>
 *          The type of exceptions raised
 */

public interface GASTStatementVisitorType<A, C, E extends Throwable>
{
  /**
   * Visit a conditional statement.
   *
   * @param left
   *          The left branch
   * @param right
   *          The right branch
   * @param s
   *          The statement
   * @return A value of <code>A</code>
   * @throws E
   *           If required
   */

  A statementVisitConditional(
    final A left,
    final A right,
    final GASTStatementConditional s)
    throws E;

  /**
   * Finish visiting the left branch of a conditional.
   *
   * @param s
   *          The statement
   * @throws E
   *           If required
   */

  void statementVisitConditionalLeftPost(
    final GASTStatementConditional s)
    throws E;

  /**
   * Prepare to visit the left branch of a conditional.
   *
   * @param s
   *          The statement
   * @throws E
   *           If required
   */

  void statementVisitConditionalLeftPre(
    final GASTStatementConditional s)
    throws E;

  /**
   * Prepare to visit a conditional.
   *
   * @param s
   *          The statement
   * @throws E
   *           If required
   */

  void statementVisitConditionalPre(
    final GASTStatementConditional s)
    throws E;

  /**
   * Finish visiting the right branch of a conditional.
   *
   * @param s
   *          The statement
   * @throws E
   *           If required
   */

  void statementVisitConditionalRightPost(
    final GASTStatementConditional s)
    throws E;

  /**
   * Prepare to visit the right branch of a conditional.
   *
   * @param s
   *          The statement
   * @throws E
   *           If required
   */

  void statementVisitConditionalRightPre(
    final GASTStatementConditional s)
    throws E;

  /**
   * Visit a local variable.
   *
   * @param s
   *          The statement
   * @return A value of <code>A</code>
   * @throws E
   *           If required
   */

  A statementVisitLocalVariable(
    final GASTStatementLocalVariable s)
    throws E;

  /**
   * Visit a return statement.
   *
   * @param s
   *          The statement
   * @return A value of <code>A</code>
   * @throws E
   *           If required
   */

  A statementVisitReturn(
    final GASTStatementReturn s)
    throws E;

  /**
   * Finish visiting a scope.
   *
   * @param statements
   *          The list of transformed statements
   * @param s
   *          The scope
   * @return A value of <code>A</code>
   * @throws E
   *           If required
   */

  A statementVisitScope(
    final List<A> statements,
    final GASTStatementScope s)
    throws E;

  /**
   * Prepare to visit a scope.
   *
   * @param s
   *          The statement
   * @throws E
   *           If required
   */

  void statementVisitScopePre(
    final GASTStatementScope s)
    throws E;

  /**
   * <p>
   * Finish visiting a switch statement.
   * </p>
   * <p>
   * The @Nullable arguments will be <code>null</code> iff the last call to
   * {@link #statementVisitSwitchPre(GASTStatementSwitch)} returned
   * <code>false</code>.
   * </p>
   *
   * @param cases
   *          The transformed cases
   * @param default_case
   *          The default case
   * @param m
   *          The statement
   * @return A value of <code>A</code>
   * @throws E
   *           If required
   */

  A statementVisitSwitch(
    final @Nullable List<Pair<C, A>> cases,
    final @Nullable A default_case,
    final GASTStatementSwitch m)
    throws E;

  /**
   * Finish visiting a switch statement discriminee.
   *
   * @throws E
   *           If required
   */

  void statementVisitSwitchDiscrimineePost()
    throws E;

  /**
   * Prepare to visit a switch statement discriminee.
   *
   * @throws E
   *           If required
   */

  void statementVisitSwitchDiscrimineePre()
    throws E;

  /**
   * Prepare to visit a switch statement.
   *
   * @param m
   *          The statement
   * @return A non-<code>null</code> value if visiting is desired
   * @throws E
   *           If required
   */

  @Nullable
    GASTExpressionSwitchConstantVisitorType<C, E>
    statementVisitSwitchPre(
      final GASTStatementSwitch m)
      throws E;
}
