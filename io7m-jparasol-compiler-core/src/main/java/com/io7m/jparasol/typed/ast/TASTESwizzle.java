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
import java.util.List;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.lexer.Position;
import com.io7m.jparasol.lexer.TokenIdentifierLower;
import com.io7m.jparasol.typed.TType;
import com.io7m.jparasol.typed.TType.TValueType;
import com.io7m.jparasol.typed.TType.TVectorType;

// CHECKSTYLE_JAVADOC:OFF

@EqualityReference public final class TASTESwizzle implements
  TASTExpressionType
{
  private final TASTExpressionType         expression;
  private final List<TokenIdentifierLower> fields;
  private final TValueType                 type;

  public TASTESwizzle(
    final TValueType in_type,
    final TASTExpressionType in_expression,
    final List<TokenIdentifierLower> in_fields)
  {
    this.type = NullCheck.notNull(in_type, "Type");
    this.expression = NullCheck.notNull(in_expression, "Expression");
    this.fields = NullCheck.notNull(in_fields, "Fields");
    assert (this.expression.getType() instanceof TVectorType);
  }

  @Override public
    <A, C, L, E extends Throwable, V extends TASTExpressionVisitorType<A, C, L, E>>
    A
    expressionVisitableAccept(
      final V v)
      throws E
  {
    final boolean traverse = v.expressionVisitSwizzlePre(this);
    A x = null;
    if (traverse) {
      x = this.expression.expressionVisitableAccept(v);
    }
    return v.expressionVisitSwizzle(x, this);
  }

  public TASTExpressionType getExpression()
  {
    return this.expression;
  }

  public List<TokenIdentifierLower> getFields()
  {
    return this.fields;
  }

  @Override public File getFile()
  {
    return this.expression.getFile();
  }

  @Override public Position getPosition()
  {
    return this.expression.getPosition();
  }

  @Override public TType getType()
  {
    return this.type;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[TASTESwizzle ");
    builder.append(this.expression);
    for (final TokenIdentifierLower f : this.fields) {
      builder.append(" ");
      builder.append(f.getActual());
    }
    builder.append("]");
    final String r = builder.toString();
    assert r != null;
    return r;
  }
}
