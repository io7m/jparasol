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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTEApplication;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTEBoolean;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTEConditional;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTEInteger;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTELet;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTENew;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTEReal;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTERecord;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTERecordProjection;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTESwizzle;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTEVariable;

public interface TASTExpressionVisitor<A, L, E extends Throwable>
{
  public A expressionVisitApplication(
    final @CheckForNull List<A> arguments,
    final @Nonnull TASTEApplication e)
    throws E,
      ConstraintError;

  public boolean expressionVisitApplicationPre(
    final @Nonnull TASTEApplication e)
    throws E,
      ConstraintError;

  public A expressionVisitBoolean(
    final @Nonnull TASTEBoolean e)
    throws E,
      ConstraintError;

  public A expressionVisitConditional(
    final @CheckForNull A condition,
    final @CheckForNull A left,
    final @CheckForNull A right,
    final @Nonnull TASTEConditional e)
    throws E,
      ConstraintError;

  public void expressionVisitConditionalConditionPost(
    final @Nonnull TASTEConditional e)
    throws E,
      ConstraintError;

  public void expressionVisitConditionalConditionPre(
    final @Nonnull TASTEConditional e)
    throws E,
      ConstraintError;

  public void expressionVisitConditionalLeftPost(
    final @Nonnull TASTEConditional e)
    throws E,
      ConstraintError;

  public void expressionVisitConditionalLeftPre(
    final @Nonnull TASTEConditional e)
    throws E,
      ConstraintError;

  public boolean expressionVisitConditionalPre(
    final @Nonnull TASTEConditional e)
    throws E,
      ConstraintError;

  public void expressionVisitConditionalRightPost(
    final @Nonnull TASTEConditional e)
    throws E,
      ConstraintError;

  public void expressionVisitConditionalRightPre(
    final @Nonnull TASTEConditional e)
    throws E,
      ConstraintError;

  public A expressionVisitInteger(
    final @Nonnull TASTEInteger e)
    throws E,
      ConstraintError;

  public A expressionVisitLet(
    final @CheckForNull List<L> bindings,
    final @CheckForNull A body,
    final @Nonnull TASTELet e)
    throws E,
      ConstraintError;

  public @CheckForNull TASTLocalLevelVisitor<L, E> expressionVisitLetPre(
    final @Nonnull TASTELet e)
    throws E,
      ConstraintError;

  public A expressionVisitNew(
    final @CheckForNull List<A> arguments,
    final @Nonnull TASTENew e)
    throws E,
      ConstraintError;

  public boolean expressionVisitNewPre(
    final @Nonnull TASTENew e)
    throws E,
      ConstraintError;

  public A expressionVisitReal(
    final @Nonnull TASTEReal e)
    throws E,
      ConstraintError;

  public A expressionVisitRecord(
    final @Nonnull TASTERecord e)
    throws E,
      ConstraintError;

  public A expressionVisitRecordProjection(
    final @CheckForNull A body,
    final @Nonnull TASTERecordProjection e)
    throws E,
      ConstraintError;

  public boolean expressionVisitRecordProjectionPre(
    final @CheckForNull TASTERecordProjection e)
    throws E,
      ConstraintError;

  public A expressionVisitSwizzle(
    final @CheckForNull A body,
    final @Nonnull TASTESwizzle e)
    throws E,
      ConstraintError;

  public boolean expressionVisitSwizzlePre(
    final @Nonnull TASTESwizzle e)
    throws E,
      ConstraintError;

  public A expressionVisitVariable(
    final @Nonnull TASTEVariable e)
    throws E,
      ConstraintError;
}
