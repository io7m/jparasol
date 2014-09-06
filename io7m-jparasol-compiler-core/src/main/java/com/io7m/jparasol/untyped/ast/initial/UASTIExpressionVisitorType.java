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

package com.io7m.jparasol.untyped.ast.initial;

import java.util.List;

import com.io7m.jfunctional.OptionType;
import com.io7m.jfunctional.Pair;
import com.io7m.jnull.Nullable;

// CHECKSTYLE_JAVADOC:OFF

public interface UASTIExpressionVisitorType<A, C, L, E extends Throwable>
{
  A expressionVisitApplication(
    final List<A> arguments,
    final UASTIEApplication e)
    throws E;

  void expressionVisitApplicationPre(
    final UASTIEApplication e)
    throws E;

  A expressionVisitBoolean(
    final UASTIEBoolean e)
    throws E;

  A expressionVisitConditional(
    final A condition,
    final A left,
    final A right,
    final UASTIEConditional e)
    throws E;

  void expressionVisitConditionalPre(
    final UASTIEConditional e)
    throws E;

  A expressionVisitInteger(
    final UASTIEInteger e)
    throws E;

  A expressionVisitLet(
    final List<L> bindings,
    final A body,
    final UASTIELet e)
    throws E;

  UASTILocalLevelVisitorType<L, E> expressionVisitLetPre(
    final UASTIELet e)
    throws E;

  A expressionVisitNew(
    final List<A> arguments,
    final UASTIENew e)
    throws E;

  void expressionVisitNewPre(
    final UASTIENew e)
    throws E;

  A expressionVisitReal(
    final UASTIEReal e)
    throws E;

  A expressionVisitRecord(
    final UASTIERecord e)
    throws E;

  A expressionVisitRecordProjection(
    final A body,
    final UASTIERecordProjection e)
    throws E;

  void expressionVisitRecordProjectionPre(
    final UASTIERecordProjection e)
    throws E;

  A expressionVisitSwizzle(
    final A body,
    final UASTIESwizzle e)
    throws E;

  void expressionVisitSwizzlePre(
    final UASTIESwizzle e)
    throws E;

  A expressionVisitVariable(
    final UASTIEVariable e)
    throws E;

  A expressionVisitMatch(
    final @Nullable A discriminee,
    final @Nullable List<Pair<C, A>> cases,
    final @Nullable OptionType<A> default_case,
    final UASTIEMatch m)
    throws E;

  void expressionVisitMatchDiscrimineePost()
    throws E;

  void expressionVisitMatchDiscrimineePre()
    throws E;

  @Nullable
    UASTIExpressionMatchConstantVisitorType<C, E>
    expressionVisitMatchPre(
      final UASTIEMatch m)
      throws E;

}
