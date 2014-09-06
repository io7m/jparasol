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

package com.io7m.jparasol.typed.ast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jfunctional.None;
import com.io7m.jfunctional.OptionPartialVisitorType;
import com.io7m.jfunctional.OptionType;
import com.io7m.jfunctional.Pair;
import com.io7m.jfunctional.Some;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.lexer.Position;
import com.io7m.jparasol.typed.TType;
import com.io7m.jparasol.typed.TType.TBoolean;

// CHECKSTYLE_JAVADOC:OFF

@EqualityReference public final class TASTEMatchBoolean implements
  TASTEMatchType
{
  private final OptionType<Pair<TASTEBoolean, TASTExpressionType>> case_false;
  private final OptionType<Pair<TASTEBoolean, TASTExpressionType>> case_true;
  private final OptionType<TASTExpressionType>                     default_case;
  private final TASTExpressionType                                 discriminee;
  private final TType                                              type;

  public TASTEMatchBoolean(
    final TASTExpressionType in_expression,
    final OptionType<Pair<TASTEBoolean, TASTExpressionType>> in_case_true,
    final OptionType<Pair<TASTEBoolean, TASTExpressionType>> in_case_false,
    final OptionType<TASTExpressionType> in_default_case,
    final TType in_type)
  {
    this.discriminee = NullCheck.notNull(in_expression, "Discriminee");
    this.case_true = NullCheck.notNull(in_case_true, "True case");
    this.case_false = NullCheck.notNull(in_case_false, "False case");
    this.type = NullCheck.notNull(in_type, "Type");
    this.default_case = NullCheck.notNull(in_default_case, "Default");

    assert this.discriminee.getType().equals(TBoolean.get());
  }

  @Override public
    <A, C, L, E extends Throwable, V extends TASTExpressionVisitorType<A, C, L, E>>
    A
    expressionVisitableAccept(
      final V v)
      throws E
  {
    final boolean traverse = v.expressionVisitMatchPre(this);

    List<Pair<C, A>> new_cases = null;
    final A default_result = null;
    A r_discriminee = null;

    if (traverse) {
      v.expressionVisitMatchDiscrimineePre();
      r_discriminee = this.discriminee.expressionVisitableAccept(v);
      v.expressionVisitMatchDiscrimineePost();

      final List<Pair<C, A>> cases_current = new ArrayList<Pair<C, A>>();
      final OptionPartialVisitorType<Pair<TASTEBoolean, TASTExpressionType>, Unit, E> ch =
        new OptionPartialVisitorType<Pair<TASTEBoolean, TASTExpressionType>, Unit, E>() {
          @Override public Unit none(
            final None<Pair<TASTEBoolean, TASTExpressionType>> n)
            throws E
          {
            return Unit.unit();
          }

          @Override public Unit some(
            final Some<Pair<TASTEBoolean, TASTExpressionType>> s)
            throws E
          {
            final Pair<TASTEBoolean, TASTExpressionType> a = s.get();
            final C c = v.expressionVisitMatchCase(a.getLeft());
            final A e = a.getRight().expressionVisitableAccept(v);
            cases_current.add(Pair.pair(c, e));
            return Unit.unit();
          }
        };

      this.case_false.acceptPartial(ch);
      this.case_true.acceptPartial(ch);
      new_cases = cases_current;
    }

    return v.expressionVisitMatch(
      r_discriminee,
      new_cases,
      default_result,
      this);
  }

  public OptionType<TASTExpressionType> getDefaultCase()
  {
    return this.default_case;
  }

  @Override public TASTExpressionType getDiscriminee()
  {
    return this.discriminee;
  }

  public OptionType<Pair<TASTEBoolean, TASTExpressionType>> getFalseCase()
  {
    return this.case_false;
  }

  @Override public File getFile()
  {
    return this.discriminee.getFile();
  }

  @Override public Position getPosition()
  {
    return this.discriminee.getPosition();
  }

  public OptionType<Pair<TASTEBoolean, TASTExpressionType>> getTrueCase()
  {
    return this.case_true;
  }

  @Override public TType getType()
  {
    return this.type;
  }

  @Override public <A, E extends Throwable> A matchVisitableAccept(
    final TASTEMatchVisitorType<A, E> v)
    throws E
  {
    return v.visitBoolean(this);
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[TASTEMatchBoolean ");
    builder.append(this.discriminee);
    builder.append(" ");
    builder.append(this.case_true);
    builder.append(" ");
    builder.append(this.case_false);
    builder.append("]");
    final String r = builder.toString();
    assert r != null;
    return r;
  }
}
