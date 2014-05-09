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

package com.io7m.jparasol.untyped.ast.unique_binders;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jfunctional.FunctionType;
import com.io7m.jfunctional.OptionType;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.lexer.Token.TokenIdentifierUpper;

// CHECKSTYLE_JAVADOC:OFF

@EqualityReference public abstract class UniqueName implements
  UniqueNameVisitableType
{
  @EqualityReference public static final class UniqueNameLocal extends
    UniqueName
  {
    private final String               current;
    private final TokenIdentifierLower original;

    public UniqueNameLocal(
      final TokenIdentifierLower in_original,
      final String in_current)
    {
      this.original = NullCheck.notNull(in_original, "Original");
      this.current = NullCheck.notNull(in_current, "Current");
    }

    public String getCurrent()
    {
      return this.current;
    }

    public TokenIdentifierLower getOriginal()
    {
      return this.original;
    }

    @Override public String show()
    {
      return String.format("&%s", this.current);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UniqueNameLocal ");
      builder.append(this.current);
      builder.append(" ");
      builder.append(this.original);
      builder.append("]");
      return builder.toString();
    }

    @Override public
      <A, E extends Throwable, V extends UniqueNameVisitorType<A, E>>
      A
      uniqueNameVisitableAccept(
        final V v)
        throws E
    {
      return v.uniqueNameVisitLocal(this);
    }
  }

  @EqualityReference public static final class UniqueNameNonLocal extends
    UniqueName
  {
    private final OptionType<TokenIdentifierUpper> module;
    private final TokenIdentifierLower             name;

    public UniqueNameNonLocal(
      final OptionType<TokenIdentifierUpper> in_module,
      final TokenIdentifierLower in_name)
    {
      this.module = NullCheck.notNull(in_module, "Module");
      this.name = NullCheck.notNull(in_name, "Name");
    }

    public OptionType<TokenIdentifierUpper> getModule()
    {
      return this.module;
    }

    public TokenIdentifierLower getName()
    {
      return this.name;
    }

    @Override public String show()
    {
      final StringBuilder sb = new StringBuilder();
      sb.append("$");
      this.module.map(new FunctionType<TokenIdentifierUpper, Unit>() {
        @Override public Unit call(
          final TokenIdentifierUpper x)
        {
          sb.append(x.getActual());
          sb.append(".");
          return Unit.unit();
        }
      });
      sb.append(this.name.getActual());
      return sb.toString();
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UniqueNameNonLocal ");
      builder.append(this.module);
      builder.append(" ");
      builder.append(this.name);
      builder.append("]");
      return builder.toString();
    }

    @Override public
      <A, E extends Throwable, V extends UniqueNameVisitorType<A, E>>
      A
      uniqueNameVisitableAccept(
        final V v)
        throws E
    {
      return v.uniqueNameVisitNonLocal(this);
    }
  }

  public abstract String show();
}
