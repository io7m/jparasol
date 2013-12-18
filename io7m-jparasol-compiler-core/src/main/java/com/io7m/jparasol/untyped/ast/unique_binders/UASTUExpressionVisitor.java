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

package com.io7m.jparasol.untyped.ast.unique_binders;

import java.util.List;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUEApplication;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUEBoolean;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUEConditional;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUEInteger;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUELet;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUENew;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUEReal;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUERecord;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUERecordProjection;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUESwizzle;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUEVariable;

public interface UASTUExpressionVisitor<A, L, E extends Throwable>
{
  public A expressionVisitApplication(
    final @Nonnull List<A> arguments,
    final @Nonnull UASTUEApplication e)
    throws E,
      ConstraintError;

  public void expressionVisitApplicationPre(
    final @Nonnull UASTUEApplication e)
    throws E,
      ConstraintError;

  public A expressionVisitBoolean(
    final @Nonnull UASTUEBoolean e)
    throws E,
      ConstraintError;

  public A expressionVisitConditional(
    final @Nonnull A condition,
    final @Nonnull A left,
    final @Nonnull A right,
    final @Nonnull UASTUEConditional e)
    throws E,
      ConstraintError;

  public void expressionVisitConditionalConditionPre(
    final @Nonnull UASTUEConditional e)
    throws E,
      ConstraintError;

  public void expressionVisitConditionalConditionPost(
    final @Nonnull UASTUEConditional e)
    throws E,
      ConstraintError;

  public void expressionVisitConditionalLeftPre(
    final @Nonnull UASTUEConditional e)
    throws E,
      ConstraintError;

  public void expressionVisitConditionalLeftPost(
    final @Nonnull UASTUEConditional e)
    throws E,
      ConstraintError;

  public void expressionVisitConditionalRightPre(
    final @Nonnull UASTUEConditional e)
    throws E,
      ConstraintError;

  public void expressionVisitConditionalRightPost(
    final @Nonnull UASTUEConditional e)
    throws E,
      ConstraintError;

  public A expressionVisitInteger(
    final @Nonnull UASTUEInteger e)
    throws E,
      ConstraintError;

  public A expressionVisitLet(
    final @Nonnull List<L> bindings,
    final @Nonnull A body,
    final @Nonnull UASTUELet e)
    throws E,
      ConstraintError;

  public @Nonnull UASTULocalLevelVisitor<L, E> expressionVisitLetPre(
    final @Nonnull UASTUELet e)
    throws E,
      ConstraintError;

  public A expressionVisitNew(
    final @Nonnull List<A> arguments,
    final @Nonnull UASTUENew e)
    throws E,
      ConstraintError;

  public void expressionVisitNewPre(
    final @Nonnull UASTUENew e)
    throws E,
      ConstraintError;

  public A expressionVisitReal(
    final @Nonnull UASTUEReal e)
    throws E,
      ConstraintError;

  public A expressionVisitRecord(
    final @Nonnull UASTUERecord e)
    throws E,
      ConstraintError;

  public A expressionVisitRecordProjection(
    final @Nonnull A body,
    final @Nonnull UASTUERecordProjection e)
    throws E,
      ConstraintError;

  public void expressionVisitRecordProjectionPre(
    final @Nonnull UASTUERecordProjection e)
    throws E,
      ConstraintError;

  public A expressionVisitSwizzle(
    final @Nonnull A body,
    final @Nonnull UASTUESwizzle e)
    throws E,
      ConstraintError;

  public void expressionVisitSwizzlePre(
    final @Nonnull UASTUESwizzle e)
    throws E,
      ConstraintError;

  public A expressionVisitVariable(
    final @Nonnull UASTUEVariable e)
    throws E,
      ConstraintError;
}
