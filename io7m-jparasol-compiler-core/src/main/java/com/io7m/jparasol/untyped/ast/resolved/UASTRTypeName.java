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

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.ModulePath;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;

public abstract class UASTRTypeName implements UASTRTypeNameVisitable
{
  public static final class UASTRTypeNameBuiltIn extends UASTRTypeName
  {
    private final @Nonnull TokenIdentifierLower name;

    public UASTRTypeNameBuiltIn(
      final @Nonnull TokenIdentifierLower name)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
    }

    @Override public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    @Override public @Nonnull String show()
    {
      return this.name.getActual();
    }

    @Override public
      <A, E extends Throwable, V extends UASTRTypeNameVisitor<A, E>>
      A
      typeNameVisitableAccept(
        final @Nonnull V v)
        throws ConstraintError,
          E
    {
      return v.typeNameVisitBuiltIn(this);
    }
  }

  public static final class UASTRTypeNameGlobal extends UASTRTypeName
  {
    private final @Nonnull ModulePathFlat       flat;
    private final @Nonnull TokenIdentifierLower name;
    private final @Nonnull ModulePath           path;

    public UASTRTypeNameGlobal(
      final @Nonnull ModulePath path,
      final @Nonnull TokenIdentifierLower name)
      throws ConstraintError
    {
      this.path = Constraints.constrainNotNull(path, "Path");
      this.flat = ModulePathFlat.fromModulePath(path);
      this.name = Constraints.constrainNotNull(name, "Name");
    }

    public @Nonnull ModulePathFlat getFlat()
    {
      return this.flat;
    }

    @Override public @Nonnull TokenIdentifierLower getName()
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
      <A, E extends Throwable, V extends UASTRTypeNameVisitor<A, E>>
      A
      typeNameVisitableAccept(
        final @Nonnull V v)
        throws ConstraintError,
          E
    {
      return v.typeNameVisitGlobal(this);
    }
  }

  public abstract @Nonnull TokenIdentifierLower getName();

  public abstract @Nonnull String show();
}
