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
import com.io7m.jnull.Nullable;

/**
 * <p>
 * Flattened module paths.
 * </p>
 * <p>
 * "Flattened" in this case is synonymous with "lacks lexical information".
 * </p>
 * 
 * @see ModulePath
 */

@EqualityStructural public final class ModulePathFlat implements NameShowType
{
  /**
   * Construct a flattened path from an existing module path.
   * 
   * @param path
   *          The path
   * @return A flattened path
   */

  public static ModulePathFlat fromModulePath(
    final ModulePath path)
  {
    final StringBuilder s = new StringBuilder();
    final String p =
      PackagePathFlat.fromPackagePath(path.getPackagePath()).getActual();

    if (p.isEmpty() == false) {
      s.append(p);
      s.append(".");
    }
    s.append(path.getName().getActual());
    final String r = s.toString();
    assert r != null;
    return new ModulePathFlat(r);
  }

  private final String actual;

  /**
   * Construct a flattened path from a string.
   * 
   * @param in_actual
   *          The string
   */

  public ModulePathFlat(
    final String in_actual)
  {
    this.actual = in_actual;
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
    final ModulePathFlat other = (ModulePathFlat) obj;
    if (!this.actual.equals(other.actual)) {
      return false;
    }
    return true;
  }

  /**
   * @return The path as a string
   */

  public String getActual()
  {
    return this.actual;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.actual.hashCode();
    return result;
  }

  @Override public String show()
  {
    return this.actual;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[ModulePathFlat ");
    builder.append(this.actual);
    builder.append("]");
    final String r = builder.toString();
    assert r != null;
    return r;
  }
}
