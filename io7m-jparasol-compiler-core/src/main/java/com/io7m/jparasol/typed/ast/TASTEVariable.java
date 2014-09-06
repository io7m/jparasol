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

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.lexer.Position;
import com.io7m.jparasol.typed.TType;

// CHECKSTYLE_JAVADOC:OFF

@EqualityReference public final class TASTEVariable implements
  TASTExpressionType
{
  private final TASTTermName name;
  private final TType        type;

  public TASTEVariable(
    final TType in_type,
    final TASTTermName in_name)
  {
    this.type = NullCheck.notNull(in_type, "Type");
    this.name = NullCheck.notNull(in_name, "Name");
  }

  @Override public
    <A, C, L, E extends Throwable, V extends TASTExpressionVisitorType<A, C, L, E>>
    A
    expressionVisitableAccept(
      final V v)
      throws E
  {
    return v.expressionVisitVariable(this);
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
    builder.append("[TASTEVariable ");
    builder.append(this.name.show());
    builder.append("]");
    final String r = builder.toString();
    assert r != null;
    return r;
  }
}
