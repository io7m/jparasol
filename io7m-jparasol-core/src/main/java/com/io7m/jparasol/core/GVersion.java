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

import java.util.Collection;
import java.util.SortedSet;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jfunctional.Unit;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * Functions for dealing with versions.
 */

@EqualityReference public final class GVersion
{
  /**
   * Filter the given versions into GLSL ES and GLSL versions.
   * 
   * @param supports
   *          The versions.
   * @param out_supports_es
   *          The resulting GLSL ES versions.
   * @param out_supports_full
   *          The resulting GLSL versions.
   */

  public static void filterVersions(
    final Collection<GVersionType> supports,
    final SortedSet<GVersionES> out_supports_es,
    final SortedSet<GVersionFull> out_supports_full)
  {
    for (final GVersionType v : supports) {
      v
        .versionAccept(new GVersionVisitorType<Unit, UnreachableCodeException>() {
          @Override public Unit versionVisitES(
            final GVersionES ve)
            throws UnreachableCodeException
          {
            out_supports_es.add(ve);
            return Unit.unit();
          }

          @Override public Unit versionVisitFull(
            final GVersionFull vf)
            throws UnreachableCodeException
          {
            out_supports_full.add(vf);
            return Unit.unit();
          }
        });
    }
  }

  private GVersion()
  {
    throw new UnreachableCodeException();
  }
}
