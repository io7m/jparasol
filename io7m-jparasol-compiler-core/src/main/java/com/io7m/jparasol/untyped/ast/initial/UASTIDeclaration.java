/*
 * Copyright © 2013 <code@io7m.com> http://io7m.com
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

package com.io7m.jparasol.untyped.ast.initial;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Option;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;

public abstract class UASTIDeclaration<S extends UASTIStatus>
{
  public static enum Type
  {
    UASTID_VALUE_LOCAL
  }

  public static final class UASTIDValueLocal<S extends UASTIStatus> extends
    UASTIDeclaration<S>
  {
    private final @Nonnull Option<UASTITypePath> ascription;
    private final @Nonnull UASTIExpression<S>    expression;
    private final @Nonnull TokenIdentifierLower  name;

    @SuppressWarnings("synthetic-access") public UASTIDValueLocal(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull Option<UASTITypePath> ascription,
      final @Nonnull UASTIExpression<S> expression)
      throws ConstraintError
    {
      super(Type.UASTID_VALUE_LOCAL);
      this.name = Constraints.constrainNotNull(name, "Name");
      this.ascription =
        Constraints.constrainNotNull(ascription, "Ascription");
      this.expression =
        Constraints.constrainNotNull(expression, "Expression");
    }

    public @Nonnull Option<UASTITypePath> getAscription()
    {
      return this.ascription;
    }

    public @Nonnull UASTIExpression<S> getExpression()
    {
      return this.expression;
    }

    public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }
  }

  private final @Nonnull Type type;

  private UASTIDeclaration(
    final @Nonnull Type type)
    throws ConstraintError
  {
    this.type = Constraints.constrainNotNull(type, "Type");
  }

  public @Nonnull Type getType()
  {
    return this.type;
  }
}
