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

package com.io7m.jparasol.untyped.ast.checked;

import java.util.List;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.untyped.ast.checked.UASTCExpression.UASTCEApplication;
import com.io7m.jparasol.untyped.ast.checked.UASTCExpression.UASTCEBoolean;
import com.io7m.jparasol.untyped.ast.checked.UASTCExpression.UASTCEConditional;
import com.io7m.jparasol.untyped.ast.checked.UASTCExpression.UASTCEInteger;
import com.io7m.jparasol.untyped.ast.checked.UASTCExpression.UASTCELet;
import com.io7m.jparasol.untyped.ast.checked.UASTCExpression.UASTCENew;
import com.io7m.jparasol.untyped.ast.checked.UASTCExpression.UASTCEReal;
import com.io7m.jparasol.untyped.ast.checked.UASTCExpression.UASTCERecord;
import com.io7m.jparasol.untyped.ast.checked.UASTCExpression.UASTCERecordProjection;
import com.io7m.jparasol.untyped.ast.checked.UASTCExpression.UASTCESwizzle;
import com.io7m.jparasol.untyped.ast.checked.UASTCExpression.UASTCEVariable;

public interface UASTCExpressionVisitor<A, L, E extends Throwable>
{
  public A expressionVisitApplication(
    final @Nonnull List<A> arguments,
    final @Nonnull UASTCEApplication e)
    throws E,
      ConstraintError;

  public void expressionVisitApplicationPre(
    final @Nonnull UASTCEApplication e)
    throws E,
      ConstraintError;

  public A expressionVisitBoolean(
    final @Nonnull UASTCEBoolean e)
    throws E,
      ConstraintError;

  public A expressionVisitConditional(
    final @Nonnull A condition,
    final @Nonnull A left,
    final @Nonnull A right,
    final @Nonnull UASTCEConditional e)
    throws E,
      ConstraintError;

  public void expressionVisitConditionalPre(
    final @Nonnull UASTCEConditional e)
    throws E,
      ConstraintError;

  public A expressionVisitInteger(
    final @Nonnull UASTCEInteger e)
    throws E,
      ConstraintError;

  public A expressionVisitLet(
    final @Nonnull List<L> bindings,
    final @Nonnull A body,
    final @Nonnull UASTCELet e)
    throws E,
      ConstraintError;

  public @Nonnull UASTCLocalLevelVisitor<L, E> expressionVisitLetPre(
    final @Nonnull UASTCELet e)
    throws E,
      ConstraintError;

  public A expressionVisitNew(
    final @Nonnull List<A> arguments,
    final @Nonnull UASTCENew e)
    throws E,
      ConstraintError;

  public void expressionVisitNewPre(
    final @Nonnull UASTCENew e)
    throws E,
      ConstraintError;

  public A expressionVisitReal(
    final @Nonnull UASTCEReal e)
    throws E,
      ConstraintError;

  public A expressionVisitRecord(
    final @Nonnull UASTCERecord e)
    throws E,
      ConstraintError;

  public A expressionVisitRecordProjection(
    final @Nonnull A body,
    final @Nonnull UASTCERecordProjection e)
    throws E,
      ConstraintError;

  public void expressionVisitRecordProjectionPre(
    final @Nonnull UASTCERecordProjection e)
    throws E,
      ConstraintError;

  public A expressionVisitSwizzle(
    final @Nonnull A body,
    final @Nonnull UASTCESwizzle e)
    throws E,
      ConstraintError;

  public void expressionVisitSwizzlePre(
    final @Nonnull UASTCESwizzle e)
    throws E,
      ConstraintError;

  public A expressionVisitVariable(
    final @Nonnull UASTCEVariable e)
    throws E,
      ConstraintError;
}
