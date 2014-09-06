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

import com.io7m.jequality.annotations.EqualityStructural;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jparasol.lexer.TokenIdentifierUpper;

/**
 * Paths to specific modules.
 */

@EqualityStructural public final class ModulePath
{
  private final TokenIdentifierUpper name;
  private final PackagePath          package_path;

  /**
   * Construct a module path.
   * 
   * @param in_package_path
   *          The package path
   * @param in_name
   *          The module name
   */

  public ModulePath(
    final PackagePath in_package_path,
    final TokenIdentifierUpper in_name)
  {
    this.package_path = NullCheck.notNull(in_package_path, "Package path");
    this.name = NullCheck.notNull(in_name, "Module name");
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
    final ModulePath other = (ModulePath) obj;
    if (!this.name.equals(other.name)) {
      return false;
    }
    if (!this.package_path.equals(other.package_path)) {
      return false;
    }
    return true;
  }

  /**
   * @return The module name
   */

  public TokenIdentifierUpper getName()
  {
    return this.name;
  }

  /**
   * @return The package path
   */

  public PackagePath getPackagePath()
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
    final String r = builder.toString();
    assert r != null;
    return r;
  }
}
