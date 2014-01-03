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

package com.io7m.jparasol.untyped.ast.resolved;

import java.util.List;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.untyped.ast.resolved.UASTRExpression.UASTREApplication;
import com.io7m.jparasol.untyped.ast.resolved.UASTRExpression.UASTREBoolean;
import com.io7m.jparasol.untyped.ast.resolved.UASTRExpression.UASTREConditional;
import com.io7m.jparasol.untyped.ast.resolved.UASTRExpression.UASTREInteger;
import com.io7m.jparasol.untyped.ast.resolved.UASTRExpression.UASTRELet;
import com.io7m.jparasol.untyped.ast.resolved.UASTRExpression.UASTRENew;
import com.io7m.jparasol.untyped.ast.resolved.UASTRExpression.UASTREReal;
import com.io7m.jparasol.untyped.ast.resolved.UASTRExpression.UASTRERecord;
import com.io7m.jparasol.untyped.ast.resolved.UASTRExpression.UASTRERecordProjection;
import com.io7m.jparasol.untyped.ast.resolved.UASTRExpression.UASTRESwizzle;
import com.io7m.jparasol.untyped.ast.resolved.UASTRExpression.UASTREVariable;

public interface UASTRExpressionVisitor<A, L, E extends Throwable>
{
  public A expressionVisitApplication(
    final @Nonnull List<A> arguments,
    final @Nonnull UASTREApplication e)
    throws E,
      ConstraintError;

  public void expressionVisitApplicationPre(
    final @Nonnull UASTREApplication e)
    throws E,
      ConstraintError;

  public A expressionVisitBoolean(
    final @Nonnull UASTREBoolean e)
    throws E,
      ConstraintError;

  public A expressionVisitConditional(
    final @Nonnull A condition,
    final @Nonnull A left,
    final @Nonnull A right,
    final @Nonnull UASTREConditional e)
    throws E,
      ConstraintError;

  public void expressionVisitConditionalConditionPost(
    final @Nonnull UASTREConditional e)
    throws E,
      ConstraintError;

  public void expressionVisitConditionalConditionPre(
    final @Nonnull UASTREConditional e)
    throws E,
      ConstraintError;

  public void expressionVisitConditionalLeftPost(
    final @Nonnull UASTREConditional e)
    throws E,
      ConstraintError;

  public void expressionVisitConditionalLeftPre(
    final @Nonnull UASTREConditional e)
    throws E,
      ConstraintError;

  public void expressionVisitConditionalRightPost(
    final @Nonnull UASTREConditional e)
    throws E,
      ConstraintError;

  public void expressionVisitConditionalRightPre(
    final @Nonnull UASTREConditional e)
    throws E,
      ConstraintError;

  public A expressionVisitInteger(
    final @Nonnull UASTREInteger e)
    throws E,
      ConstraintError;

  public A expressionVisitLet(
    final @Nonnull List<L> bindings,
    final @Nonnull A body,
    final @Nonnull UASTRELet e)
    throws E,
      ConstraintError;

  public @Nonnull UASTRLocalLevelVisitor<L, E> expressionVisitLetPre(
    final @Nonnull UASTRELet e)
    throws E,
      ConstraintError;

  public A expressionVisitNew(
    final @Nonnull List<A> arguments,
    final @Nonnull UASTRENew e)
    throws E,
      ConstraintError;

  public void expressionVisitNewPre(
    final @Nonnull UASTRENew e)
    throws E,
      ConstraintError;

  public A expressionVisitReal(
    final @Nonnull UASTREReal e)
    throws E,
      ConstraintError;

  public A expressionVisitRecord(
    final @Nonnull UASTRERecord e)
    throws E,
      ConstraintError;

  public A expressionVisitRecordProjection(
    final @Nonnull A body,
    final @Nonnull UASTRERecordProjection e)
    throws E,
      ConstraintError;

  public void expressionVisitRecordProjectionPre(
    final @Nonnull UASTRERecordProjection e)
    throws E,
      ConstraintError;

  public A expressionVisitSwizzle(
    final @Nonnull A body,
    final @Nonnull UASTRESwizzle e)
    throws E,
      ConstraintError;

  public void expressionVisitSwizzlePre(
    final @Nonnull UASTRESwizzle e)
    throws E,
      ConstraintError;

  public A expressionVisitVariable(
    final @Nonnull UASTREVariable e)
    throws E,
      ConstraintError;
}
