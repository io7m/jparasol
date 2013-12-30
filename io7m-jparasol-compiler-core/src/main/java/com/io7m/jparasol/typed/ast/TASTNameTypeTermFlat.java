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

package com.io7m.jparasol.typed.ast;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.typed.TTypeNameFlat;

/**
 * Either a type name or a term name.
 */

public abstract class TASTNameTypeTermFlat implements
  TASTNameTypeTermFlatVisitable
{
  public static final class Term extends TASTNameTypeTermFlat
  {
    private final @Nonnull TASTTermNameFlat name;

    public Term(
      final @Nonnull TASTTermNameFlat name)
      throws ConstraintError
    {
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
      final Term other = (Term) obj;
      if (!this.name.equals(other.name)) {
        return false;
      }
      return true;
    }

    @Override public int hashCode()
    {
      return this.name.hashCode();
    }

    @Override public
      <A, E extends Throwable, V extends TASTNameTypeTermFlatVisitor<A, E>>
      A
      nameTypeTermVisitableAccept(
        final V v)
        throws E
    {
      return v.nameTypeTermVisitTerm(this);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[Term ");
      builder.append(this.name);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class Type extends TASTNameTypeTermFlat
  {
    private final @Nonnull TTypeNameFlat name;

    public Type(
      final @Nonnull TTypeNameFlat name)
      throws ConstraintError
    {
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
      final Type other = (Type) obj;
      if (!this.name.equals(other.name)) {
        return false;
      }
      return true;
    }

    @Override public int hashCode()
    {
      return this.name.hashCode();
    }

    @Override public
      <A, E extends Throwable, V extends TASTNameTypeTermFlatVisitor<A, E>>
      A
      nameTypeTermVisitableAccept(
        final V v)
        throws E
    {
      return v.nameTypeTermVisitType(this);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[Type ");
      builder.append(this.name);
      builder.append("]");
      return builder.toString();
    }
  }
}
