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

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Function;
import com.io7m.jaux.functional.Option;
import com.io7m.jaux.functional.Unit;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.lexer.Token.TokenIdentifierUpper;

public abstract class UniqueName implements UniqueNameVisitable
{
  public static final class UniqueNameLocal extends UniqueName
  {
    private final @Nonnull String               current;
    private final @Nonnull TokenIdentifierLower original;

    public UniqueNameLocal(
      final @Nonnull TokenIdentifierLower original,
      final @Nonnull String current)
      throws ConstraintError
    {
      this.original = Constraints.constrainNotNull(original, "Original");
      this.current = Constraints.constrainNotNull(current, "Current");
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
      final UniqueNameLocal other = (UniqueNameLocal) obj;
      if (!this.current.equals(other.current)) {
        return false;
      }
      if (!this.original.equals(other.original)) {
        return false;
      }
      return true;
    }

    public @Nonnull String getCurrent()
    {
      return this.current;
    }

    public @Nonnull TokenIdentifierLower getOriginal()
    {
      return this.original;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.current.hashCode();
      result = (prime * result) + this.original.hashCode();
      return result;
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
      <A, E extends Throwable, V extends UniqueNameVisitor<A, E>>
      A
      uniqueNameVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.uniqueNameVisitLocal(this);
    }
  }

  public static final class UniqueNameNonLocal extends UniqueName
  {
    private final @Nonnull Option<TokenIdentifierUpper> module;
    private final @Nonnull TokenIdentifierLower         name;

    public UniqueNameNonLocal(
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
      final UniqueNameNonLocal other = (UniqueNameNonLocal) obj;
      if (!this.module.equals(other.module)) {
        return false;
      }
      if (!this.name.equals(other.name)) {
        return false;
      }
      return true;
    }

    public @Nonnull Option<TokenIdentifierUpper> getModule()
    {
      return this.module;
    }

    public @Nonnull TokenIdentifierLower getName()
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

    @Override public String show()
    {
      final StringBuilder sb = new StringBuilder();
      sb.append("$");
      this.module.map(new Function<TokenIdentifierUpper, Unit>() {
        @Override public Unit call(
          final @Nonnull TokenIdentifierUpper x)
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
      <A, E extends Throwable, V extends UniqueNameVisitor<A, E>>
      A
      uniqueNameVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.uniqueNameVisitNonLocal(this);
    }
  }

  public abstract @Nonnull String show();
}
