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

package com.io7m.jparasol.xml;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;

/**
 * A GLSL version for a specific API.
 */

@Immutable public final class Version implements Comparable<Version>
{
  public static @Nonnull Version newVersion(
    final int version,
    final @Nonnull API api)
    throws ConstraintError
  {
    return new Version(version, api);
  }

  private final @Nonnull API api;
  private final int          version;

  Version(
    final int version,
    final @Nonnull API api)
    throws ConstraintError
  {
    this.version = version;
    this.api = Constraints.constrainNotNull(api, "API");
  }

  @Override public int compareTo(
    final @Nonnull Version o)
  {
    final Integer x = Integer.valueOf(this.version);
    final Integer y = Integer.valueOf(o.version);
    return x.compareTo(y);
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
    final Version other = (Version) obj;
    if (this.api != other.api) {
      return false;
    }
    if (this.version != other.version) {
      return false;
    }
    return true;
  }

  public @Nonnull API getAPI()
  {
    return this.api;
  }

  public int getVersion()
  {
    return this.version;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.api.hashCode();
    result = (prime * result) + this.version;
    return result;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[Version version=");
    builder.append(this.version);
    builder.append(" api=");
    builder.append(this.api);
    builder.append("]");
    return builder.toString();
  }
}
