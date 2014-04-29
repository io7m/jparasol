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

package com.io7m.jparasol.xml;

import com.io7m.jequality.annotations.EqualityStructural;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;

/**
 * A GLSL version for a specific API.
 */

@EqualityStructural public final class Version implements Comparable<Version>
{
  /**
   * Construct a new version.
   * 
   * @param version
   *          The version number
   * @param api
   *          The API
   * @return A new version
   */

  public static Version newVersion(
    final int version,
    final API api)
  {
    return new Version(version, api);
  }

  private final API api;
  private final int version;

  Version(
    final int in_version,
    final API in_api)
  {
    this.version = in_version;
    this.api = NullCheck.notNull(in_api, "API");
  }

  @Override public int compareTo(
    final Version o)
  {
    final Integer x = Integer.valueOf(this.version);
    final Integer y = Integer.valueOf(o.version);
    return x.compareTo(y);
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
    final Version other = (Version) obj;
    if (this.api != other.api) {
      return false;
    }
    if (this.version != other.version) {
      return false;
    }
    return true;
  }

  /**
   * @return The API
   */

  public API getAPI()
  {
    return this.api;
  }

  /**
   * @return The version number
   */

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
    final String r = builder.toString();
    assert r != null;
    return r;
  }
}
