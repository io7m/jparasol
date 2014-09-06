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

package com.io7m.jparasol.glsl.ast;

import java.util.ArrayList;
import java.util.List;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jfunctional.OptionType;
import com.io7m.jfunctional.Pair;
import com.io7m.jfunctional.PartialFunctionType;
import com.io7m.jfunctional.Some;
import com.io7m.jnull.NullCheck;

/**
 * A switch statement.
 */

@EqualityReference public final class GASTStatementSwitch implements
  GASTStatementType
{
  private final OptionType<GASTStatementReturn>                                   case_default;
  private final List<Pair<GASTExpressionSwitchConstantType, GASTStatementReturn>> cases;
  private final GASTExpressionType                                                discriminee;

  /**
   * Construct a switch statement.
   *
   * @param in_discriminee
   *          The discriminee expression.
   * @param in_cases
   *          The cases.
   * @param in_case_default
   *          The default case, if any.
   */

  public GASTStatementSwitch(
    final GASTExpressionType in_discriminee,
    final List<Pair<GASTExpressionSwitchConstantType, GASTStatementReturn>> in_cases,
    final OptionType<GASTStatementReturn> in_case_default)
  {
    this.discriminee = NullCheck.notNull(in_discriminee, "Discriminee");
    this.cases = NullCheck.notNullAll(in_cases, "Cases");
    this.case_default = NullCheck.notNull(in_case_default, "Default case");
  }

  /**
   * @return The list of non-default cases, in declaration order.
   */

  public
    List<Pair<GASTExpressionSwitchConstantType, GASTStatementReturn>>
    getCases()
  {
    return this.cases;
  }

  /**
   * @return The default case, if any.
   */

  public OptionType<GASTStatementReturn> getDefaultCase()
  {
    return this.case_default;
  }

  /**
   * @return The discriminee expression.
   */

  public GASTExpressionType getDiscriminee()
  {
    return this.discriminee;
  }

  @Override public
    <A, C, E extends Throwable, V extends GASTStatementVisitorType<A, C, E>>
    A
    statementVisitableAccept(
      final V v)
      throws E
  {
    final GASTExpressionSwitchConstantVisitorType<C, E> cv =
      v.statementVisitSwitchPre(this);
    List<Pair<C, A>> r_cases = null;
    A r_default_case = null;

    if (cv != null) {
      r_cases = new ArrayList<Pair<C, A>>();
      for (final Pair<GASTExpressionSwitchConstantType, GASTStatementReturn> c : this.cases) {
        final C cc = c.getLeft().expressionSwitchConstantVisitableAccept(cv);
        final A cs = c.getRight().statementVisitableAccept(v);
        r_cases.add(Pair.pair(cc, cs));
      }

      final OptionType<A> rdc_opt =
        this.case_default
          .mapPartial(new PartialFunctionType<GASTStatementReturn, A, E>() {
            @Override public A call(
              final GASTStatementReturn r)
              throws E
            {
              return r.statementVisitableAccept(v);
            }
          });

      r_default_case = rdc_opt.isSome() ? ((Some<A>) rdc_opt).get() : null;
    }

    return v.statementVisitSwitch(r_cases, r_default_case, this);
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[GASTStatementSwitch ");
    builder.append(this.discriminee);
    builder.append(" ");
    builder.append(this.cases);
    builder.append(" ");
    builder.append(this.case_default);
    builder.append("]");
    final String r = builder.toString();
    assert r != null;
    return r;
  }
}
