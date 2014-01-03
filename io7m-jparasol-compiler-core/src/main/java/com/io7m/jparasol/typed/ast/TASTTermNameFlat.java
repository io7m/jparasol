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

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.NameFlat;
import com.io7m.jparasol.typed.ast.TASTTermName.TASTTermNameGlobal;

public final class TASTTermNameFlat implements NameFlat
{
  public static @Nonnull TASTTermNameFlat fromTermNameGlobal(
    final @Nonnull TASTTermNameGlobal name)
    throws ConstraintError
  {
    return new TASTTermNameFlat(name.getFlat(), name.getName().getActual());
  }

  private final @Nonnull String         name;
  private final @Nonnull ModulePathFlat path;

  public TASTTermNameFlat(
    final @Nonnull ModulePathFlat path,
    final @Nonnull String name)
    throws ConstraintError
  {
    this.path = Constraints.constrainNotNull(path, "Path");
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
    final TASTTermNameFlat other = (TASTTermNameFlat) obj;
    if (!this.name.equals(other.name)) {
      return false;
    }
    if (!this.path.equals(other.path)) {
      return false;
    }
    return true;
  }

  @Override public ModulePathFlat getModulePath()
  {
    return this.path;
  }

  @Override public @Nonnull String getName()
  {
    return this.name;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.name.hashCode();
    result = (prime * result) + this.path.hashCode();
    return result;
  }

  @Override public @Nonnull String show()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append(this.path.getActual());
    builder.append(".");
    builder.append(this.name);
    return builder.toString();
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[TASTTermNameFlat ");
    builder.append(this.path);
    builder.append(" ");
    builder.append(this.name);
    builder.append("]");
    return builder.toString();
  }
}
