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
import com.io7m.jparasol.ModulePath;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;

public abstract class TASTTypeName implements TASTTypeNameVisitable
{
  public static final class TASTTypeNameBuiltIn extends TASTTypeName
  {
    private final @Nonnull TokenIdentifierLower name;

    public TASTTypeNameBuiltIn(
      final @Nonnull TokenIdentifierLower name)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.name.hashCode();
      return result;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTTypeNameBuiltIn ");
      builder.append(this.name);
      builder.append("]");
      return builder.toString();
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
      final TASTTypeNameBuiltIn other = (TASTTypeNameBuiltIn) obj;
      if (!this.name.equals(other.name)) {
        return false;
      }
      return true;
    }

    public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    @Override public @Nonnull String show()
    {
      return this.name.getActual();
    }

    @Override public
      <A, E extends Throwable, V extends TASTTypeNameVisitor<A, E>>
      A
      typeNameVisitableAccept(
        final @Nonnull V v)
        throws ConstraintError,
          E
    {
      return v.typeNameVisitBuiltIn(this);
    }
  }

  public static final class TASTTypeNameGlobal extends TASTTypeName
  {
    private final @Nonnull ModulePathFlat       flat;
    private final @Nonnull TokenIdentifierLower name;
    private final @Nonnull ModulePath           path;

    public TASTTypeNameGlobal(
      final @Nonnull ModulePath path,
      final @Nonnull TokenIdentifierLower name)
      throws ConstraintError
    {
      this.path = Constraints.constrainNotNull(path, "Path");
      this.flat = ModulePathFlat.fromModulePath(path);
      this.name = Constraints.constrainNotNull(name, "Name");
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.flat.hashCode();
      result = (prime * result) + this.name.hashCode();
      result = (prime * result) + this.path.hashCode();
      return result;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTTypeNameGlobal ");
      builder.append(this.flat);
      builder.append(" ");
      builder.append(this.name);
      builder.append(" ");
      builder.append(this.path);
      builder.append("]");
      return builder.toString();
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
      final TASTTypeNameGlobal other = (TASTTypeNameGlobal) obj;
      if (!this.flat.equals(other.flat)) {
        return false;
      }
      if (!this.name.equals(other.name)) {
        return false;
      }
      if (!this.path.equals(other.path)) {
        return false;
      }
      return true;
    }

    public @Nonnull ModulePathFlat getFlat()
    {
      return this.flat;
    }

    public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    public @Nonnull ModulePath getPath()
    {
      return this.path;
    }

    @Override public @Nonnull String show()
    {
      final StringBuilder s = new StringBuilder();
      s.append(this.flat.getActual());
      s.append(".");
      s.append(this.name.getActual());
      return s.toString();
    }

    @Override public
      <A, E extends Throwable, V extends TASTTypeNameVisitor<A, E>>
      A
      typeNameVisitableAccept(
        final @Nonnull V v)
        throws ConstraintError,
          E
    {
      return v.typeNameVisitGlobal(this);
    }
  }

  public abstract @Nonnull String show();
}
