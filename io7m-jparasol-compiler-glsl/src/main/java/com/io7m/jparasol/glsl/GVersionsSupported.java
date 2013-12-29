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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.glsl.GVersion.GVersionES;
import com.io7m.jparasol.glsl.GVersion.GVersionFull;

public final class GVersionsSupported
{
  public static @Nonnull GVersionsSupported all()
  {
    return new GVersionsSupported();
  }

  private final @Nonnull Map<GVersionES, List<GVersionCheckExclusionReason>>   exclusions_es;
  private final @Nonnull Map<GVersionFull, List<GVersionCheckExclusionReason>> exclusions_full;

  private GVersionsSupported()
  {
    this.exclusions_es =
      new HashMap<GVersionES, List<GVersionCheckExclusionReason>>();
    this.exclusions_full =
      new HashMap<GVersionFull, List<GVersionCheckExclusionReason>>();
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
    final GVersionsSupported other = (GVersionsSupported) obj;
    if (!this.exclusions_es.equals(other.exclusions_es)) {
      return false;
    }
    if (!this.exclusions_full.equals(other.exclusions_full)) {
      return false;
    }
    return true;
  }

  public void excludeES(
    final @Nonnull GVersionES version,
    final @Nonnull GVersionCheckExclusionReason reason)
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

  public void excludeFull(
    final @Nonnull GVersionFull version,
    final @Nonnull GVersionCheckExclusionReason reason)
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

  public @Nonnull SortedSet<GVersionES> getESVersions()
  {
    final SortedSet<GVersionES> all = new TreeSet<GVersionES>();
    all.addAll(GVersionES.ALL);
    for (final GVersionES v : this.exclusions_es.keySet()) {
      all.remove(v);
    }
    return all;
  }

  public @Nonnull List<GVersionCheckExclusionReason> getExclusionReasonsES(
    final @Nonnull GVersionES v)
    throws ConstraintError
  {
    Constraints.constrainArbitrary(
      this.exclusions_es.containsKey(v),
      "Version is excluded");
    return this.exclusions_es.get(v);
  }

  public @Nonnull List<GVersionCheckExclusionReason> getExclusionReasonsFull(
    final @Nonnull GVersionFull v)
    throws ConstraintError
  {
    Constraints.constrainArbitrary(
      this.exclusions_full.containsKey(v),
      "Version is excluded");
    return this.exclusions_full.get(v);
  }

  public @Nonnull SortedSet<GVersionFull> getFullVersions()
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
    return builder.toString();
  }
}
