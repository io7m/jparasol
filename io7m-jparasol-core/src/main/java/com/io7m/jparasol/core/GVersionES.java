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

package com.io7m.jparasol.core;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import com.io7m.jequality.annotations.EqualityStructural;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;

/**
 * OpenGL ES version numbers.
 */

@EqualityStructural public final class GVersionES implements GVersionType
{
  /**
   * All possible GLSL ES versions.
   */

  public static final SortedSet<GVersionES> ALL;

  /**
   * The short API name ("glsl-es").
   */

  public static final String                API_NAME = "glsl-es";

  /**
   * GLSL ES 1.00
   */

  public static final GVersionES            GLSL_ES_100;

  /**
   * GLSL ES 3.00
   */

  public static final GVersionES            GLSL_ES_300;

  /**
   * The lowest GLSL ES version.
   */

  public static final GVersionES            GLSL_ES_LOWER;

  /**
   * The highest GLSL ES version.
   */

  public static final GVersionES            GLSL_ES_UPPER;

  static {
    GLSL_ES_100 = new GVersionES(100);
    GLSL_ES_300 = new GVersionES(300);
    GLSL_ES_LOWER = GVersionES.GLSL_ES_100;
    GLSL_ES_UPPER = GVersionES.GLSL_ES_300;
    ALL = GVersionES.makeES();
  }

  private static SortedSet<GVersionES> makeES()
  {
    final SortedSet<GVersionES> s = new TreeSet<GVersionES>();
    s.add(GVersionES.GLSL_ES_100);
    s.add(GVersionES.GLSL_ES_300);
    final SortedSet<GVersionES> r = Collections.unmodifiableSortedSet(s);
    assert r != null;
    return r;
  }

  private final int number;

  /**
   * Construct a GLSL ES version number.
   *
   * @param in_number
   *          The number
   */

  public GVersionES(
    final int in_number)
  {
    this.number = in_number;
  }

  @Override public int compareTo(
    final @Nullable GVersionType o)
  {
    return Integer.compare(this.number, NullCheck
      .notNull(o, "Other")
      .versionGetNumber());
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
    final GVersionES other = (GVersionES) obj;
    if (this.number != other.number) {
      return false;
    }
    return true;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.number;
    return result;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[GVersionES ");
    builder.append(this.number);
    builder.append("]");
    final String r = builder.toString();
    assert r != null;
    return r;
  }

  @Override public
    <A, E extends Throwable, V extends GVersionVisitorType<A, E>>
    A
    versionAccept(
      final V v)
      throws E
  {
    return v.versionVisitES(this);
  }

  @Override public String versionGetAPIName()
  {
    return GVersionES.API_NAME;
  }

  @Override public String versionGetLongName()
  {
    return "GLSL ES " + this.number;
  }

  @Override public int versionGetNumber()
  {
    return this.number;
  }
}
