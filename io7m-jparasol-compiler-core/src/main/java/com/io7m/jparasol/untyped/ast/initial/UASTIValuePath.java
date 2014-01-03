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

package com.io7m.jparasol.untyped.ast.initial;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Function;
import com.io7m.jaux.functional.Option;
import com.io7m.jaux.functional.Unit;
import com.io7m.jparasol.lexer.Token;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.lexer.Token.TokenIdentifierUpper;

/**
 * A path to a value.
 */

public final class UASTIValuePath
{
  private final @Nonnull Option<Token.TokenIdentifierUpper> module;
  private final @Nonnull Token.TokenIdentifierLower         name;

  public UASTIValuePath(
    final @Nonnull Option<TokenIdentifierUpper> module,
    final @Nonnull TokenIdentifierLower name)
    throws ConstraintError
  {
    this.module = Constraints.constrainNotNull(module, "Module");
    this.name = Constraints.constrainNotNull(name, "Name");
  }

  @Override public boolean equals(
    final Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    final UASTIValuePath other = (UASTIValuePath) obj;
    if (!this.module.equals(other.module)) {
      return false;
    }
    if (!this.name.equals(other.name)) {
      return false;
    }
    return true;
  }

  public @Nonnull Option<Token.TokenIdentifierUpper> getModule()
  {
    return this.module;
  }

  public @Nonnull Token.TokenIdentifierLower getName()
  {
    return this.name;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.module.hashCode();
    result = (prime * result) + this.name.hashCode();
    return result;
  }

  public @Nonnull String show()
  {
    final StringBuilder s = new StringBuilder();
    this.module.map(new Function<TokenIdentifierUpper, Unit>() {
      @Override public Unit call(
        final TokenIdentifierUpper x)
      {
        s.append(x.getActual());
        s.append(".");
        return Unit.unit();
      }
    });
    s.append(this.name.getActual());
    return s.toString();
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[UASTIValuePath ");
    builder.append(this.module);
    builder.append(" ");
    builder.append(this.name);
    builder.append("]");
    return builder.toString();
  }
}
