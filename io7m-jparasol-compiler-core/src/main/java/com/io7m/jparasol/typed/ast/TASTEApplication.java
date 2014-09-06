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
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.lexer.Position;
import com.io7m.jparasol.typed.TType;

// CHECKSTYLE_JAVADOC:OFF

@EqualityReference public final class TASTEApplication implements
  TASTExpressionType
{
  private final List<TASTExpressionType> arguments;
  private final TASTTermName             name;
  private final TType                    type;

  public TASTEApplication(
    final TASTTermName in_name,
    final List<TASTExpressionType> in_arguments,
    final TType in_type)
  {
    this.name = NullCheck.notNull(in_name, "Name");
    this.arguments = NullCheck.notNull(in_arguments, "Arguments");
    this.type = NullCheck.notNull(in_type, "Type");
  }

  @Override public
    <A, C, L, E extends Throwable, V extends TASTExpressionVisitorType<A, C, L, E>>
    A
    expressionVisitableAccept(
      final V v)
      throws E
  {
    final boolean traverse = v.expressionVisitApplicationPre(this);

    List<A> args = null;
    if (traverse) {
      args = new ArrayList<A>();
      for (final TASTExpressionType a : this.arguments) {
        final A x = a.expressionVisitableAccept(v);
        args.add(x);
      }
    }

    return v.expressionVisitApplication(args, this);
  }

  public List<TASTExpressionType> getArguments()
  {
    return this.arguments;
  }

  @Override public File getFile()
  {
    return this.name.getFile();
  }

  public TASTTermName getName()
  {
    return this.name;
  }

  @Override public Position getPosition()
  {
    return this.name.getPosition();
  }

  @Override public TType getType()
  {
    return this.type;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[TASTEApplication ");
    builder.append(this.name.show());
    builder.append(" ");
    builder.append(this.arguments);
    builder.append("]");
    final String r = builder.toString();
    assert r != null;
    return r;
  }
}
