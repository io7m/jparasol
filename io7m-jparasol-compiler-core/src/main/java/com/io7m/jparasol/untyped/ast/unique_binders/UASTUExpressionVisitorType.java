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

package com.io7m.jparasol.untyped.ast.unique_binders;

import java.util.List;

import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUEApplication;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUEBoolean;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUEConditional;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUEInteger;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUELet;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUEMatrixColumnAccess;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUENew;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUEReal;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUERecord;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUERecordProjection;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUESwizzle;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUEVariable;

// CHECKSTYLE_JAVADOC:OFF

public interface UASTUExpressionVisitorType<A, L, E extends Throwable>
{
  A expressionVisitApplication(
    final List<A> arguments,
    final UASTUEApplication e)
    throws E;

  void expressionVisitApplicationPre(
    final UASTUEApplication e)
    throws E;

  A expressionVisitBoolean(
    final UASTUEBoolean e)
    throws E;

  A expressionVisitConditional(
    final A condition,
    final A left,
    final A right,
    final UASTUEConditional e)
    throws E;

  void expressionVisitConditionalPre(
    final UASTUEConditional e)
    throws E;

  A expressionVisitInteger(
    final UASTUEInteger e)
    throws E;

  A expressionVisitLet(
    final List<L> bindings,
    final A body,
    final UASTUELet e)
    throws E;

  UASTULocalLevelVisitorType<L, E> expressionVisitLetPre(
    final UASTUELet e)
    throws E;

  A expressionVisitMatrixColumnAccess(
    final A body,
    final UASTUEMatrixColumnAccess e)
    throws E;

  void expressionVisitMatrixColumnAccessPre(
    final UASTUEMatrixColumnAccess e)
    throws E;

  A expressionVisitNew(
    final List<A> arguments,
    final UASTUENew e)
    throws E;

  void expressionVisitNewPre(
    final UASTUENew e)
    throws E;

  A expressionVisitReal(
    final UASTUEReal e)
    throws E;

  A expressionVisitRecord(
    final UASTUERecord e)
    throws E;

  A expressionVisitRecordProjection(
    final A body,
    final UASTUERecordProjection e)
    throws E;

  void expressionVisitRecordProjectionPre(
    final UASTUERecordProjection e)
    throws E;

  A expressionVisitSwizzle(
    final A body,
    final UASTUESwizzle e)
    throws E;

  void expressionVisitSwizzlePre(
    final UASTUESwizzle e)
    throws E;

  A expressionVisitVariable(
    final UASTUEVariable e)
    throws E;
}
