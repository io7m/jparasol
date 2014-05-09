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

import com.io7m.jequality.annotations.EqualityStructural;
import com.io7m.jfunctional.FunctionType;
import com.io7m.jfunctional.OptionType;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jparasol.lexer.Token;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.lexer.Token.TokenIdentifierUpper;

/**
 * A path to a value.
 */

// CHECKSTYLE_JAVADOC:OFF

@EqualityStructural public final class UASTIValuePath
{
  private final OptionType<Token.TokenIdentifierUpper> module;
  private final Token.TokenIdentifierLower             name;

  public UASTIValuePath(
    final OptionType<TokenIdentifierUpper> in_module,
    final TokenIdentifierLower in_name)
  {
    this.module = NullCheck.notNull(in_module, "Module");
    this.name = NullCheck.notNull(in_name, "Name");
  }

  @Override public boolean equals(
    final @Nullable Object obj)
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

  public OptionType<Token.TokenIdentifierUpper> getModule()
  {
    return this.module;
  }

  public Token.TokenIdentifierLower getName()
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

  public String show()
  {
    final StringBuilder s = new StringBuilder();
    this.module.map(new FunctionType<TokenIdentifierUpper, Unit>() {
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
