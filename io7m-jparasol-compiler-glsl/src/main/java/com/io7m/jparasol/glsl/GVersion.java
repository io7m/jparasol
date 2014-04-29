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

package com.io7m.jparasol.glsl;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import com.io7m.jequality.annotations.EqualityStructural;
import com.io7m.jnull.Nullable;

/**
 * The type of GLSL versions.
 */

@SuppressWarnings("synthetic-access") @EqualityStructural public abstract class GVersion
{
  /**
   * The type of GLSL ES versions.
   */

  @EqualityStructural public static final class GVersionES extends GVersion implements
    Comparable<GVersionES>
  {
    /**
     * All possible GLSL ES versions.
     */

    public static final SortedSet<GVersionES> ALL;

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
      final GVersionES o)
    {
      return Integer.compare(this.number, o.number);
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
      final GVersionFull other = (GVersionFull) obj;
      if (this.number != other.number) {
        return false;
      }
      return true;
    }

    @Override public String getLongName()
    {
      return "GLSL ES " + this.number;
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
  }

  /**
   * The type of GLSL versions.
   */

  @EqualityStructural public static final class GVersionFull extends GVersion implements
    Comparable<GVersionFull>
  {
    /**
     * All possible GLSL versions.
     */

    public static final SortedSet<GVersionFull> ALL;

    /**
     * GLSL 1.10
     */

    public static final GVersionFull            GLSL_110;

    /**
     * GLSL 1.20
     */

    public static final GVersionFull            GLSL_120;

    /**
     * GLSL 1.30
     */
    public static final GVersionFull            GLSL_130;

    /**
     * GLSL 1.40
     */
    public static final GVersionFull            GLSL_140;

    /**
     * GLSL 1.50
     */
    public static final GVersionFull            GLSL_150;

    /**
     * GLSL 3.30
     */
    public static final GVersionFull            GLSL_330;

    /**
     * GLSL 4.00
     */
    public static final GVersionFull            GLSL_400;

    /**
     * GLSL 4.10
     */
    public static final GVersionFull            GLSL_410;
    /**
     * GLSL 4.20
     */
    public static final GVersionFull            GLSL_420;
    /**
     * GLSL 4.30
     */
    public static final GVersionFull            GLSL_430;
    /**
     * GLSL 4.40
     */
    public static final GVersionFull            GLSL_440;
    /**
     * The lowest GLSL version.
     */
    public static final GVersionFull            GLSL_LOWER;
    /**
     * The highest GLSL version.
     */
    public static final GVersionFull            GLSL_UPPER;

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

    private static SortedSet<GVersionFull> makeFull()
    {
      final SortedSet<GVersionFull> s = new TreeSet<GVersionFull>();
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
      final SortedSet<GVersionFull> r = Collections.unmodifiableSortedSet(s);
      assert r != null;
      return r;
    }

    private final int number;

    /**
     * Construct a GLSL version.
     * 
     * @param in_number
     *          The version number
     */

    public GVersionFull(
      final int in_number)
    {
      this.number = in_number;
    }

    @Override public int compareTo(
      final GVersionFull o)
    {
      return Integer.compare(this.number, o.number);
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
      final GVersionFull other = (GVersionFull) obj;
      if (this.number != other.number) {
        return false;
      }
      return true;
    }

    @Override public String getLongName()
    {
      return "GLSL " + this.number;
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
      return v.versionVisitFull(this);
    }
  }

  @Override public abstract boolean equals(
    @Nullable Object obj);

  /**
   * @return The humanly-readable long form of the version number.
   */

  public abstract String getLongName();

  /**
   * @return The version number.
   */

  public abstract int getNumber();

  @Override public abstract int hashCode();

  /**
   * Accept a generic visitor.
   * 
   * @param v
   *          The visitor
   * @return A value of <code>A</code>
   * @throws E
   *           If the visitor raises <code>E</code>
   */

  public abstract
    <A, E extends Throwable, V extends GVersionVisitorType<A, E>>
    A
    versionAccept(
      final V v)
      throws E;
}
