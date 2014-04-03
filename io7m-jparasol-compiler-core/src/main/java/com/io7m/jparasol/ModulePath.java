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

package com.io7m.jparasol;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.lexer.Token.TokenIdentifierUpper;

public final class ModulePath
{
  private final @Nonnull TokenIdentifierUpper name;
  private final @Nonnull PackagePath          package_path;

  public ModulePath(
    final @Nonnull PackagePath in_package_path,
    final @Nonnull TokenIdentifierUpper in_name)
    throws ConstraintError
  {
    this.package_path =
      Constraints.constrainNotNull(in_package_path, "Package path");
    this.name = Constraints.constrainNotNull(in_name, "Module name");
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
    final ModulePath other = (ModulePath) obj;
    if (!this.name.equals(other.name)) {
      return false;
    }
    if (!this.package_path.equals(other.package_path)) {
      return false;
    }
    return true;
  }

  public @Nonnull TokenIdentifierUpper getName()
  {
    return this.name;
  }

  public @Nonnull PackagePath getPackagePath()
  {
    return this.package_path;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.name.hashCode();
    result = (prime * result) + this.package_path.hashCode();
    return result;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[ModulePath ");
    builder.append(this.package_path);
    builder.append(" ");
    builder.append(this.name);
    builder.append("]");
    return builder.toString();
  }
}
