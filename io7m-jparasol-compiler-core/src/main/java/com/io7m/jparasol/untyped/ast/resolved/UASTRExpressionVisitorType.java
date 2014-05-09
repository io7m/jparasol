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

// CHECKSTYLE_JAVADOC:OFF

public interface UASTRExpressionVisitorType<A, L, E extends Throwable>
{
  A expressionVisitApplication(
    final List<A> arguments,
    final UASTREApplication e)
    throws E;

  void expressionVisitApplicationPre(
    final UASTREApplication e)
    throws E;

  A expressionVisitBoolean(
    final UASTREBoolean e)
    throws E;

  A expressionVisitConditional(
    final A condition,
    final A left,
    final A right,
    final UASTREConditional e)
    throws E;

  void expressionVisitConditionalConditionPost(
    final UASTREConditional e)
    throws E;

  void expressionVisitConditionalConditionPre(
    final UASTREConditional e)
    throws E;

  void expressionVisitConditionalLeftPost(
    final UASTREConditional e)
    throws E;

  void expressionVisitConditionalLeftPre(
    final UASTREConditional e)
    throws E;

  void expressionVisitConditionalRightPost(
    final UASTREConditional e)
    throws E;

  void expressionVisitConditionalRightPre(
    final UASTREConditional e)
    throws E;

  A expressionVisitInteger(
    final UASTREInteger e)
    throws E;

  A expressionVisitLet(
    final List<L> bindings,
    final A body,
    final UASTRELet e)
    throws E;

  UASTRLocalLevelVisitorType<L, E> expressionVisitLetPre(
    final UASTRELet e)
    throws E;

  A expressionVisitNew(
    final List<A> arguments,
    final UASTRENew e)
    throws E;

  void expressionVisitNewPre(
    final UASTRENew e)
    throws E;

  A expressionVisitReal(
    final UASTREReal e)
    throws E;

  A expressionVisitRecord(
    final UASTRERecord e)
    throws E;

  A expressionVisitRecordProjection(
    final A body,
    final UASTRERecordProjection e)
    throws E;

  void expressionVisitRecordProjectionPre(
    final UASTRERecordProjection e)
    throws E;

  A expressionVisitSwizzle(
    final A body,
    final UASTRESwizzle e)
    throws E;

  void expressionVisitSwizzlePre(
    final UASTRESwizzle e)
    throws E;

  A expressionVisitVariable(
    final UASTREVariable e)
    throws E;
}
