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

package com.io7m.jparasol.untyped.ast.resolved;

// CHECKSTYLE_JAVADOC:OFF

import java.util.ArrayList;
import java.util.List;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.lexer.TokenLet;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDValueLocal;

@EqualityReference public final class UASTRELet implements
  UASTRExpressionType
{
  private final List<UASTRDValueLocal> bindings;
  private final UASTRExpressionType    body;
  private final TokenLet               token;

  public UASTRELet(
    final TokenLet in_token,
    final List<UASTRDValueLocal> in_bindings,
    final UASTRExpressionType in_body)
  {
    this.token = NullCheck.notNull(in_token, "Token");
    this.bindings = NullCheck.notNull(in_bindings, "Bindings");
    this.body = NullCheck.notNull(in_body, "Body");
  }

  @Override public
    <A, C, L, E extends Throwable, V extends UASTRExpressionVisitorType<A, C, L, E>>
    A
    expressionVisitableAccept(
      final V v)
      throws E
  {
    final UASTRLocalLevelVisitorType<L, E> bv = v.expressionVisitLetPre(this);

    final List<L> r_bindings = new ArrayList<L>();
    for (final UASTRDValueLocal b : this.bindings) {
      final L rb = bv.localVisitValueLocal(b);
      r_bindings.add(rb);
    }

    final A x = this.body.expressionVisitableAccept(v);
    return v.expressionVisitLet(r_bindings, x, this);
  }

  public List<UASTRDValueLocal> getBindings()
  {
    return this.bindings;
  }

  public UASTRExpressionType getBody()
  {
    return this.body;
  }

  public TokenLet getToken()
  {
    return this.token;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[UASTRELet ");
    builder.append(this.bindings);
    builder.append(" ");
    builder.append(this.body);
    builder.append("]");
    return builder.toString();
  }
}
