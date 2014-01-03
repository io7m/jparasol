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

import java.util.List;

import javax.annotation.Nonnull;

import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;

public final class PackagePathFlat
{
  public static @Nonnull PackagePathFlat fromPackagePath(
    final @Nonnull PackagePath path)
  {
    final List<TokenIdentifierLower> c = path.getComponents();
    final StringBuilder s = new StringBuilder();
    for (int index = 0; index < c.size(); ++index) {
      s.append(c.get(index).getActual());
      if ((index + 1) < c.size()) {
        s.append(".");
      }
    }
    return new PackagePathFlat(s.toString());
  }

  private final @Nonnull String actual;

  private PackagePathFlat(
    final @Nonnull String actual)
  {
    this.actual = actual;
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
    final PackagePathFlat other = (PackagePathFlat) obj;
    if (!this.actual.equals(other.actual)) {
      return false;
    }
    return true;
  }

  public @Nonnull String getActual()
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

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[PackagePathFlat ");
    builder.append(this.actual);
    builder.append("]");
    return builder.toString();
  }
}
