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

import java.math.BigInteger;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jnull.NullCheck;

/**
 * The type of integer constants.
 */

@EqualityReference public final class GASTEInteger implements
  GASTExpressionSwitchConstantType
{
  private final BigInteger value;

  /**
   * Construct a constant.
   *
   * @param in_value
   *          The value
   */

  public GASTEInteger(
    final BigInteger in_value)
  {
    this.value = NullCheck.notNull(in_value, "Value");
  }

  @Override public
    <A, E extends Throwable, V extends GASTExpressionSwitchConstantVisitorType<A, E>>
    A
    expressionSwitchConstantVisitableAccept(
      final V v)
      throws E
  {
    return v.expressionIntegerVisit(this);
  }

  @Override public
    <A, E extends Throwable, V extends GASTExpressionVisitorType<A, E>>
    A
    expressionVisitableAccept(
      final V v)
      throws E
  {
    return v.expressionIntegerVisit(this);
  }

  /**
   * @return The value
   */

  public BigInteger getValue()
  {
    return this.value;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[GASTEInteger ");
    builder.append(this.value);
    builder.append("]");
    final String r = builder.toString();
    assert r != null;
    return r;
  }
}
