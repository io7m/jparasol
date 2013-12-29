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

package com.io7m.jparasol.glsl;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Nonnull;

public abstract class GVersion implements GVersionVisitable
{
  public static final class GVersionES extends GVersion implements
    Comparable<GVersionES>
  {
    public static final @Nonnull SortedSet<GVersionES> ALL;
    public static final @Nonnull GVersionES            GLSL_ES_100;
    public static final @Nonnull GVersionES            GLSL_ES_300;
    public static final @Nonnull GVersionES            GLSL_ES_LOWER;
    public static final @Nonnull GVersionES            GLSL_ES_UPPER;

    static {
      GLSL_ES_100 = new GVersionES(100);
      GLSL_ES_300 = new GVersionES(300);
      GLSL_ES_LOWER = GVersionES.GLSL_ES_100;
      GLSL_ES_UPPER = GVersionES.GLSL_ES_300;
      ALL = GVersionES.makeES();
    }

    private static @Nonnull SortedSet<GVersionES> makeES()
    {
      final TreeSet<GVersionES> s = new TreeSet<GVersionES>();
      s.add(GVersionES.GLSL_ES_100);
      s.add(GVersionES.GLSL_ES_300);
      return Collections.unmodifiableSortedSet(s);
    }

    private final int number;

    public GVersionES(
      final int number)
    {
      this.number = number;
    }

    @Override public int compareTo(
      final GVersionES o)
    {
      return Integer.compare(this.number, o.number);
    }

    @SuppressWarnings("synthetic-access") @Override public boolean equals(
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
      final GVersionFull other = (GVersionFull) obj;
      if (this.number != other.number) {
        return false;
      }
      return true;
    }

    @Override public int getNumber()
    {
      return this.number;
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
      return builder.toString();
    }

    @Override public
      <A, E extends Throwable, V extends GVersionVisitor<A, E>>
      A
      versionAccept(
        final @Nonnull V v)
        throws E
    {
      return v.versionVisitES(this);
    }

    @Override public String getLongName()
    {
      return "GLSL ES " + this.number;
    }
  }

  public static final class GVersionFull extends GVersion implements
    Comparable<GVersionFull>
  {
    public static final @Nonnull SortedSet<GVersionFull> ALL;
    public static final @Nonnull GVersionFull            GLSL_110;
    public static final @Nonnull GVersionFull            GLSL_120;
    public static final @Nonnull GVersionFull            GLSL_130;
    public static final @Nonnull GVersionFull            GLSL_140;
    public static final @Nonnull GVersionFull            GLSL_150;
    public static final @Nonnull GVersionFull            GLSL_330;
    public static final @Nonnull GVersionFull            GLSL_400;
    public static final @Nonnull GVersionFull            GLSL_410;
    public static final @Nonnull GVersionFull            GLSL_420;
    public static final @Nonnull GVersionFull            GLSL_430;
    public static final @Nonnull GVersionFull            GLSL_440;
    public static final @Nonnull GVersionFull            GLSL_LOWER;
    public static final @Nonnull GVersionFull            GLSL_UPPER;

    static {
      GLSL_110 = new GVersionFull(110);
      GLSL_120 = new GVersionFull(120);
      GLSL_130 = new GVersionFull(130);
      GLSL_140 = new GVersionFull(140);
      GLSL_150 = new GVersionFull(150);
      GLSL_330 = new GVersionFull(330);
      GLSL_400 = new GVersionFull(400);
      GLSL_410 = new GVersionFull(410);
      GLSL_420 = new GVersionFull(420);
      GLSL_430 = new GVersionFull(430);
      GLSL_440 = new GVersionFull(440);
      GLSL_LOWER = GVersionFull.GLSL_110;
      GLSL_UPPER = GVersionFull.GLSL_440;
      ALL = GVersionFull.makeFull();
    }

    private static @Nonnull SortedSet<GVersionFull> makeFull()
    {
      final TreeSet<GVersionFull> s = new TreeSet<GVersionFull>();
      s.add(GVersionFull.GLSL_110);
      s.add(GVersionFull.GLSL_120);
      s.add(GVersionFull.GLSL_130);
      s.add(GVersionFull.GLSL_140);
      s.add(GVersionFull.GLSL_150);
      s.add(GVersionFull.GLSL_330);
      s.add(GVersionFull.GLSL_400);
      s.add(GVersionFull.GLSL_410);
      s.add(GVersionFull.GLSL_420);
      s.add(GVersionFull.GLSL_430);
      s.add(GVersionFull.GLSL_440);
      return Collections.unmodifiableSortedSet(s);
    }

    private final int number;

    public GVersionFull(
      final int number)
    {
      this.number = number;
    }

    @Override public int compareTo(
      final GVersionFull o)
    {
      return Integer.compare(this.number, o.number);
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
      final GVersionFull other = (GVersionFull) obj;
      if (this.number != other.number) {
        return false;
      }
      return true;
    }

    @Override public int getNumber()
    {
      return this.number;
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
      builder.append("[GVersionFull ");
      builder.append(this.number);
      builder.append("]");
      return builder.toString();
    }

    @Override public
      <A, E extends Throwable, V extends GVersionVisitor<A, E>>
      A
      versionAccept(
        final @Nonnull V v)
        throws E
    {
      return v.versionVisitFull(this);
    }

    @Override public String getLongName()
    {
      return "GLSL " + this.number;
    }
  }

  public abstract int getNumber();

  public abstract @Nonnull String getLongName();
}
