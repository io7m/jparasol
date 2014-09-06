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

package com.io7m.jparasol.untyped.ast.checked;

import java.util.List;

import com.io7m.jfunctional.OptionType;
import com.io7m.jfunctional.Pair;
import com.io7m.jnull.Nullable;

// CHECKSTYLE_JAVADOC:OFF

public interface UASTCExpressionVisitorType<A, C, L, E extends Throwable>
{
  A expressionVisitApplication(
    final List<A> arguments,
    final UASTCEApplication e)
    throws E;

  void expressionVisitApplicationPre(
    final UASTCEApplication e)
    throws E;

  A expressionVisitBoolean(
    final UASTCEBoolean e)
    throws E;

  A expressionVisitConditional(
    final A condition,
    final A left,
    final A right,
    final UASTCEConditional e)
    throws E;

  void expressionVisitConditionalPre(
    final UASTCEConditional e)
    throws E;

  A expressionVisitInteger(
    final UASTCEInteger e)
    throws E;

  A expressionVisitLet(
    final List<L> bindings,
    final A body,
    final UASTCELet e)
    throws E;

  UASTCLocalLevelVisitorType<L, E> expressionVisitLetPre(
    final UASTCELet e)
    throws E;

  A expressionVisitNew(
    final List<A> arguments,
    final UASTCENew e)
    throws E;

  void expressionVisitNewPre(
    final UASTCENew e)
    throws E;

  A expressionVisitReal(
    final UASTCEReal e)
    throws E;

  A expressionVisitRecord(
    final UASTCERecord e)
    throws E;

  A expressionVisitRecordProjection(
    final A body,
    final UASTCERecordProjection e)
    throws E;

  void expressionVisitRecordProjectionPre(
    final UASTCERecordProjection e)
    throws E;

  A expressionVisitSwizzle(
    final A body,
    final UASTCESwizzle e)
    throws E;

  void expressionVisitSwizzlePre(
    final UASTCESwizzle e)
    throws E;

  A expressionVisitVariable(
    final UASTCEVariable e)
    throws E;

  A expressionVisitMatch(
    final @Nullable A discriminee,
    final @Nullable List<Pair<C, A>> cases,
    final @Nullable OptionType<A> default_case,
    final UASTCEMatch m)
    throws E;

  void expressionVisitMatchDiscrimineePost()
    throws E;

  void expressionVisitMatchDiscrimineePre()
    throws E;

  @Nullable
    UASTCExpressionMatchConstantVisitorType<C, E>
    expressionVisitMatchPre(
      final UASTCEMatch m)
      throws E;
}
