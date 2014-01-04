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

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.CompilerError;
import com.io7m.jparasol.glsl.GVersion.GVersionES;
import com.io7m.jparasol.glsl.GVersion.GVersionFull;
import com.io7m.jparasol.lexer.Position;

public final class GVersionCheckerError extends CompilerError
{
  private static final long serialVersionUID;

  static {
    serialVersionUID = -5916767962958997985L;
  }

  public static @Nonnull
    GVersionCheckerError
    requiredVersionsExcluded(
      final @Nonnull Map<GVersionFull, List<GVersionCheckExclusionReason>> exclusions_full,
      final @Nonnull Map<GVersionES, List<GVersionCheckExclusionReason>> exclusions_es)
      throws ConstraintError
  {
    final StringBuilder m = new StringBuilder();
    m.append("The given program cannot be used on the required versions.\n");

    for (final GVersionFull v : exclusions_full.keySet()) {
      final List<GVersionCheckExclusionReason> reasons =
        exclusions_full.get(v);
      m.append("Cannot run on ");
      m.append(v.getLongName());
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
      m.append(v.getLongName());
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

    return new GVersionCheckerError(
      new File("<multiple>"),
      new Position(0, 0),
      m.toString());
  }

  private GVersionCheckerError(
    final @Nonnull File file,
    final @Nonnull Position position,
    final @Nonnull String message)
    throws ConstraintError
  {
    super(message, file, position);
  }

  @Override public String getCategory()
  {
    return "version-checker";
  }
}
