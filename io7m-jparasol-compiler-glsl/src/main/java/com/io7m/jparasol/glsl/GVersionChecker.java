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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jlog.Level;
import com.io7m.jlog.Log;
import com.io7m.jparasol.glsl.GVersion.GVersionES;
import com.io7m.jparasol.glsl.GVersion.GVersionFull;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragment;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderVertex;

public final class GVersionChecker
{
  private interface Check
  {
    public void checkFragmentShader(
      final @Nonnull GVersionsSupported supported,
      final @Nonnull TASTDShaderFragment fs)
      throws ConstraintError;

    public void checkVertexShader(
      final @Nonnull GVersionsSupported supported,
      final @Nonnull TASTDShaderVertex vs)
      throws ConstraintError;

    public @Nonnull String getName();
  }

  private static final class CheckFragmentOutputs implements Check
  {
    public CheckFragmentOutputs()
    {
      // Nothing
    }

    @Override public void checkFragmentShader(
      final @Nonnull GVersionsSupported supported,
      final @Nonnull TASTDShaderFragment fs)
      throws ConstraintError
    {
      if (fs.getOutputs().size() > 1) {
        final TokenIdentifierLower name = fs.getName();
        final GVersionCheckExclusionReason reason =
          new GVersionCheckExclusionReason(
            name.getFile(),
            name.getPosition(),
            "The fragment shader declares more than one output");
        supported.excludeES(GVersionES.GLSL_ES_100, reason);
      }
    }

    @Override public void checkVertexShader(
      final @Nonnull GVersionsSupported supported,
      final @Nonnull TASTDShaderVertex vs)
    {
      // Nothing
    }

    @Override public @Nonnull String getName()
    {
      return "fragment-outputs";
    }
  }

  private static final @Nonnull List<Check> CHECKS;

  static {
    CHECKS = GVersionChecker.makeChecks();
  }

  private static @Nonnull GVersionsSupported checkRequired(
    final @Nonnull GVersionsSupported s,
    final @Nonnull SortedSet<GVersionFull> required_full,
    final @Nonnull SortedSet<GVersionES> required_es)
    throws ConstraintError,
      GVersionCheckerError
  {
    final SortedSet<GVersionFull> supported_full = s.getFullVersions();
    final SortedSet<GVersionES> supported_es = s.getESVersions();

    final HashMap<GVersionFull, List<GVersionCheckExclusionReason>> exclusions_full =
      new HashMap<GVersionFull, List<GVersionCheckExclusionReason>>();
    final HashMap<GVersionES, List<GVersionCheckExclusionReason>> exclusions_es =
      new HashMap<GVersionES, List<GVersionCheckExclusionReason>>();

    for (final GVersionFull v : required_full) {
      if (supported_full.contains(v) == false) {
        final List<GVersionCheckExclusionReason> er =
          s.getExclusionReasonsFull(v);
        exclusions_full.put(v, er);
      }
    }

    for (final GVersionES v : required_es) {
      if (supported_es.contains(v) == false) {
        final List<GVersionCheckExclusionReason> er =
          s.getExclusionReasonsES(v);
        exclusions_es.put(v, er);
      }
    }

    if ((exclusions_es.size() > 0) || (exclusions_full.size() > 0)) {
      throw GVersionCheckerError.requiredVersionsExcluded(
        exclusions_full,
        exclusions_es);
    }

    return s;
  }

  private static @Nonnull List<Check> makeChecks()
  {
    final ArrayList<Check> checks = new ArrayList<Check>();
    checks.add(new CheckFragmentOutputs());
    return Collections.unmodifiableList(checks);
  }

  public static @Nonnull GVersionChecker newVersionChecker(
    final @Nonnull Log log)
  {
    return new GVersionChecker(log);
  }

  private final @Nonnull Log log;

  private GVersionChecker(
    final @Nonnull Log log)
  {
    this.log = new Log(log, "version-checker");
  }

  public @Nonnull GVersionsSupported checkFragmentShader(
    final @Nonnull TASTDShaderFragment f,
    final @Nonnull SortedSet<GVersionFull> required_full,
    final @Nonnull SortedSet<GVersionES> required_es)
    throws ConstraintError,
      GVersionCheckerError
  {
    final StringBuilder m = new StringBuilder();
    final GVersionsSupported s = GVersionsSupported.all();
    for (final Check c : GVersionChecker.CHECKS) {
      if (this.log.enabled(Level.LOG_DEBUG)) {
        m.setLength(0);
        m.append("Running check: ");
        m.append(c.getName());
        this.log.debug(m.toString());
      }
      c.checkFragmentShader(s, f);
    }

    return GVersionChecker.checkRequired(s, required_full, required_es);
  }

  public @Nonnull GVersionsSupported checkVertexShader(
    final @Nonnull TASTDShaderVertex v,
    final @Nonnull SortedSet<GVersionFull> required_full,
    final @Nonnull SortedSet<GVersionES> required_es)
    throws ConstraintError,
      GVersionCheckerError
  {
    final StringBuilder m = new StringBuilder();
    final GVersionsSupported s = GVersionsSupported.all();
    for (final Check c : GVersionChecker.CHECKS) {
      if (this.log.enabled(Level.LOG_DEBUG)) {
        m.setLength(0);
        m.append("Running check: ");
        m.append(c.getName());
        this.log.debug(m.toString());
      }
      c.checkVertexShader(s, v);
    }

    return GVersionChecker.checkRequired(s, required_full, required_es);
  }
}
