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

import java.io.File;
import java.util.List;
import java.util.Map;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jparasol.CompilerError;
import com.io7m.jparasol.core.GVersionES;
import com.io7m.jparasol.core.GVersionFull;
import com.io7m.jparasol.lexer.Position;

/**
 * The type of version checker errors.
 */

@EqualityReference public final class GVersionCheckerError extends
  CompilerError
{
  private static final long serialVersionUID;

  static {
    serialVersionUID = -5916767962958997985L;
  }

  /**
   * Construct an error indicating that some versions were excluded.
   * 
   * @param exclusions_full
   *          The GLSL versions excluded, and why
   * @param exclusions_es
   *          The GLSL ES versions excluded, and why
   * @return An error
   */

  public static
    GVersionCheckerError
    requiredVersionsExcluded(
      final Map<GVersionFull, List<GVersionCheckExclusionReason>> exclusions_full,
      final Map<GVersionES, List<GVersionCheckExclusionReason>> exclusions_es)
  {
    final StringBuilder m = new StringBuilder();
    m.append("The given program cannot be used on the required versions.\n");

    for (final GVersionFull v : exclusions_full.keySet()) {
      final List<GVersionCheckExclusionReason> reasons =
        exclusions_full.get(v);
      m.append("Cannot run on ");
      m.append(v.versionGetLongName());
      m.append(" because:\n");
      for (final GVersionCheckExclusionReason r : reasons) {
        m.append("  ");
        m.append(r.getMessage());
        m.append(" (");
        m.append(r.getFile());
        m.append(":");
        m.append(r.getPosition());
        m.append(")\n");
      }
    }

    for (final GVersionES v : exclusions_es.keySet()) {
      final List<GVersionCheckExclusionReason> reasons = exclusions_es.get(v);
      m.append("Cannot run on ");
      m.append(v.versionGetLongName());
      m.append(" because:\n");
      for (final GVersionCheckExclusionReason r : reasons) {
        m.append("  ");
        m.append(r.getMessage());
        m.append(" (");
        m.append(r.getFile());
        m.append(":");
        m.append(r.getPosition());
        m.append(")\n");
      }
    }

    final String r = m.toString();
    assert r != null;
    return new GVersionCheckerError(
      new File("<multiple>"),
      new Position(0, 0),
      r);
  }

  private GVersionCheckerError(
    final File file,
    final Position position,
    final String message)
  {
    super(message, file, position);
  }

  @Override public String getCategory()
  {
    return "version-checker";
  }
}
