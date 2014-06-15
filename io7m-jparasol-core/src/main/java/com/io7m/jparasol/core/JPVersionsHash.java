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

import java.util.Map;
import java.util.SortedSet;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.junreachable.UnreachableCodeException;

@EqualityReference final class JPVersionsHash
{
  static void checkComplete(
    final SortedSet<GVersionES> in_supports_es,
    final SortedSet<GVersionFull> in_supports_full,
    final Map<GVersionType, String> in_version_to_hash)
    throws JPMissingHash
  {
    boolean incomplete = false;
    final StringBuilder message =
      new StringBuilder("Versions hash is incomplete.\n");

    for (final GVersionES v : in_supports_es) {
      if (in_version_to_hash.containsKey(v) == false) {
        incomplete = true;
        message.append("  Missing hash for ");
        message.append(v.versionGetLongName());
        message.append("\n");
      }
    }

    for (final GVersionFull v : in_supports_full) {
      if (in_version_to_hash.containsKey(v) == false) {
        incomplete = true;
        message.append("  Missing hash for ");
        message.append(v.versionGetLongName());
        message.append("\n");
      }
    }

    if (incomplete) {
      final String r = message.toString();
      assert r != null;
      throw new JPMissingHash(r);
    }
  }

  private JPVersionsHash()
  {
    throw new UnreachableCodeException();
  }
}
