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
import com.io7m.jfunctional.Pair;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.lexer.Position;
import com.io7m.jparasol.typed.TType;
import com.io7m.jparasol.typed.TType.TInteger;

// CHECKSTYLE_JAVADOC:OFF

@EqualityReference public final class TASTEMatchInteger implements
  TASTEMatchType
{
  private final TASTExpressionType                           case_default;
  private final List<Pair<TASTEInteger, TASTExpressionType>> cases;
  private final TASTExpressionType                           discriminee;
  private final TType                                        type;

  public TASTEMatchInteger(
    final TASTExpressionType in_expression,
    final List<Pair<TASTEInteger, TASTExpressionType>> in_cases,
    final TASTExpressionType in_case_default,
    final TType in_type)
  {
    this.discriminee = NullCheck.notNull(in_expression, "Expression");
    this.cases = NullCheck.notNull(in_cases, "Cases");
    this.case_default = NullCheck.notNull(in_case_default, "Default case");
    this.type = NullCheck.notNull(in_type, "Type");

    assert this.discriminee.getType().equals(TInteger.get());
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
    A default_case = null;
    A r_discriminee = null;

    if (traverse) {
      v.expressionVisitMatchDiscrimineePre();
      r_discriminee = this.discriminee.expressionVisitableAccept(v);
      v.expressionVisitMatchDiscrimineePost();

      new_cases = new ArrayList<Pair<C, A>>();
      for (final Pair<TASTEInteger, TASTExpressionType> c : this.cases) {
        final C rc = v.expressionVisitMatchCase(c.getLeft());
        final A e = c.getRight().expressionVisitableAccept(v);
        new_cases.add(Pair.pair(rc, e));
      }
      default_case = this.case_default.expressionVisitableAccept(v);
    }

    return v.expressionVisitMatch(
      r_discriminee,
      new_cases,
      default_case,
      this);
  }

  public List<Pair<TASTEInteger, TASTExpressionType>> getCases()
  {
    return this.cases;
  }

  public TASTExpressionType getDefaultCase()
  {
    return this.case_default;
  }

  @Override public TASTExpressionType getDiscriminee()
  {
    return this.discriminee;
  }

  public TASTExpressionType getExpression()
  {
    return this.discriminee;
  }

  @Override public File getFile()
  {
    return this.discriminee.getFile();
  }

  @Override public Position getPosition()
  {
    return this.discriminee.getPosition();
  }

  @Override public TType getType()
  {
    return this.type;
  }

  @Override public <A, E extends Throwable> A matchVisitableAccept(
    final TASTEMatchVisitorType<A, E> v)
    throws E
  {
    return v.visitInteger(this);
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[TASTEMatchInteger ");
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
