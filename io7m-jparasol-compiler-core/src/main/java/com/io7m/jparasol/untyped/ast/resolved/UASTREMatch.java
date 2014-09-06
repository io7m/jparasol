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
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITWHETHER IN AN ACTION
 * OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN
 * CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.jparasol.untyped.ast.resolved;

import java.util.ArrayList;
import java.util.List;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jfunctional.OptionType;
import com.io7m.jfunctional.Pair;
import com.io7m.jfunctional.PartialFunctionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.lexer.TokenMatch;

// CHECKSTYLE_JAVADOC:OFF

@EqualityReference public final class UASTREMatch implements
  UASTRExpressionType
{
  private final OptionType<UASTRExpressionType>                                   case_default;
  private final List<Pair<UASTRExpressionMatchConstantType, UASTRExpressionType>> cases;
  private final UASTRExpressionType                                               discriminee;
  private final TokenMatch                                                        token;

  public UASTREMatch(
    final TokenMatch in_token,
    final UASTRExpressionType in_discriminee,
    final List<Pair<UASTRExpressionMatchConstantType, UASTRExpressionType>> in_cases,
    final OptionType<UASTRExpressionType> in_case_default)
  {
    this.token = NullCheck.notNull(in_token, "Token");
    this.discriminee = NullCheck.notNull(in_discriminee, "Discriminee");
    this.cases = NullCheck.notNullAll(in_cases, "Cases");
    this.case_default = NullCheck.notNull(in_case_default, "Default case");
  }

  @Override public
    <A, C, L, E extends Throwable, V extends UASTRExpressionVisitorType<A, C, L, E>>
    A
    expressionVisitableAccept(
      final V v)
      throws E
  {
    final UASTRExpressionMatchConstantVisitorType<C, E> mv =
      v.expressionVisitMatchPre(this);

    A r_discriminee = null;
    List<Pair<C, A>> r_cases = null;
    OptionType<A> r_default_case = null;

    if (mv != null) {
      v.expressionVisitMatchDiscrimineePre();
      r_discriminee = this.discriminee.expressionVisitableAccept(v);
      v.expressionVisitMatchDiscrimineePost();

      r_cases = new ArrayList<Pair<C, A>>();
      for (final Pair<UASTRExpressionMatchConstantType, UASTRExpressionType> c : this.cases) {
        final C rmc = c.getLeft().matchConstantVisitableAccept(mv);
        final A rme = c.getRight().expressionVisitableAccept(v);
        r_cases.add(Pair.pair(rmc, rme));
      }

      r_default_case =
        this.case_default
          .mapPartial(new PartialFunctionType<UASTRExpressionType, A, E>() {
            @Override public A call(
              final UASTRExpressionType de)
              throws E
            {
              return de.expressionVisitableAccept(v);
            }
          });
    }

    return v.expressionVisitMatch(
      r_discriminee,
      r_cases,
      r_default_case,
      this);
  }

  public
    List<Pair<UASTRExpressionMatchConstantType, UASTRExpressionType>>
    getCases()
  {
    return this.cases;
  }

  public OptionType<UASTRExpressionType> getDefaultCase()
  {
    return this.case_default;
  }

  public UASTRExpressionType getDiscriminee()
  {
    return this.discriminee;
  }

  public TokenMatch getTokenMatch()
  {
    return this.token;
  }
}
