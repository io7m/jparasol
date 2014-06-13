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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.io7m.jequality.annotations.EqualityStructural;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jparasol.core.GVersionES;
import com.io7m.jparasol.core.GVersionFull;

/**
 * Supported GLSL versions.
 */

@EqualityStructural public final class GVersionsSupported
{
  /**
   * @return A value indicating all possible GLSL versions
   */

  public static GVersionsSupported all()
  {
    return new GVersionsSupported();
  }

  private final Map<GVersionES, List<GVersionCheckExclusionReason>>   exclusions_es;
  private final Map<GVersionFull, List<GVersionCheckExclusionReason>> exclusions_full;

  private GVersionsSupported()
  {
    this.exclusions_es =
      new HashMap<GVersionES, List<GVersionCheckExclusionReason>>();
    this.exclusions_full =
      new HashMap<GVersionFull, List<GVersionCheckExclusionReason>>();
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
    final GVersionsSupported other = (GVersionsSupported) obj;
    if (!this.exclusions_es.equals(other.exclusions_es)) {
      return false;
    }
    if (!this.exclusions_full.equals(other.exclusions_full)) {
      return false;
    }
    return true;
  }

  /**
   * Exclude a specific version of GLSL ES for the given reason.
   * 
   * @param version
   *          The version
   * @param reason
   *          The reason
   */

  public void excludeES(
    final GVersionES version,
    final GVersionCheckExclusionReason reason)
  {
    final List<GVersionCheckExclusionReason> reasons;
    if (this.exclusions_es.containsKey(version)) {
      reasons = this.exclusions_es.get(version);
    } else {
      reasons = new ArrayList<GVersionCheckExclusionReason>();
    }

    reasons.add(reason);
    this.exclusions_es.put(version, reasons);
  }

  /**
   * Exclude a specific version of GLSL for the given reason.
   * 
   * @param version
   *          The version
   * @param reason
   *          The reason
   */

  public void excludeFull(
    final GVersionFull version,
    final GVersionCheckExclusionReason reason)
  {
    final List<GVersionCheckExclusionReason> reasons;
    if (this.exclusions_full.containsKey(version)) {
      reasons = this.exclusions_full.get(version);
    } else {
      reasons = new ArrayList<GVersionCheckExclusionReason>();
    }

    reasons.add(reason);
    this.exclusions_full.put(version, reasons);
  }

  /**
   * @return The set of supported GLSL ES versions
   */

  public SortedSet<GVersionES> getESVersions()
  {
    final SortedSet<GVersionES> all = new TreeSet<GVersionES>();
    all.addAll(GVersionES.ALL);
    for (final GVersionES v : this.exclusions_es.keySet()) {
      all.remove(v);
    }
    return all;
  }

  /**
   * @return The set of reasons that versions of GLSL ES are not supported
   */

  public List<GVersionCheckExclusionReason> getExclusionReasonsES(
    final GVersionES v)
  {
    NullCheck.notNull(v, "Version");
    if (this.exclusions_es.containsKey(v) == false) {
      throw new IllegalStateException("Version is not excluded");
    }
    final List<GVersionCheckExclusionReason> r = this.exclusions_es.get(v);
    assert r != null;
    return r;
  }

  /**
   * @return The set of reasons that versions of GLSL are not supported
   */

  public List<GVersionCheckExclusionReason> getExclusionReasonsFull(
    final GVersionFull v)
  {
    NullCheck.notNull(v, "Version");
    if (this.exclusions_full.containsKey(v) == false) {
      throw new IllegalStateException("Version is not excluded");
    }
    final List<GVersionCheckExclusionReason> r = this.exclusions_full.get(v);
    assert r != null;
    return r;
  }

  /**
   * @return The set of supported GLSL versions
   */

  public SortedSet<GVersionFull> getFullVersions()
  {
    final SortedSet<GVersionFull> all = new TreeSet<GVersionFull>();
    all.addAll(GVersionFull.ALL);
    for (final GVersionFull v : this.exclusions_full.keySet()) {
      all.remove(v);
    }
    return all;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.exclusions_es.hashCode();
    result = (prime * result) + this.exclusions_full.hashCode();
    return result;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[GVersionsSupported ");
    builder.append(this.exclusions_es);
    builder.append(" ");
    builder.append(this.exclusions_full);
    builder.append("]");
    final String r = builder.toString();
    assert r != null;
    return r;
  }
}
