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

public interface UASTUExpressionVisitor<E extends Throwable>
{
  public void expressionVisitApplication(
    final @Nonnull UASTUEApplication e)
    throws E,
      ConstraintError;

  public void expressionVisitBoolean(
    final @Nonnull UASTUEBoolean e)
    throws E,
      ConstraintError;

  public void expressionVisitConditional(
    final @Nonnull UASTUEConditional e)
    throws E,
      ConstraintError;

  public void expressionVisitInteger(
    final @Nonnull UASTUEInteger e)
    throws E,
      ConstraintError;

  public void expressionVisitLet(
    final @Nonnull UASTUELet e)
    throws E,
      ConstraintError;

  public void expressionVisitNew(
    final @Nonnull UASTUENew e)
    throws E,
      ConstraintError;

  public void expressionVisitReal(
    final @Nonnull UASTUEReal e)
    throws E,
      ConstraintError;

  public void expressionVisitRecord(
    final @Nonnull UASTUERecord e)
    throws E,
      ConstraintError;

  public void expressionVisitRecordProjection(
    final @Nonnull UASTUERecordProjection e)
    throws E,
      ConstraintError;

  public void expressionVisitSwizzle(
    final @Nonnull UASTUESwizzle e)
    throws E,
      ConstraintError;

  public void expressionVisitVariable(
    final @Nonnull UASTUEVariable e)
    throws E,
      ConstraintError;
}
