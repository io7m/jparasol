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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Unit;
import com.io7m.jlog.Level;
import com.io7m.jlog.Log;
import com.io7m.jparasol.glsl.GVersion.GVersionES;
import com.io7m.jparasol.glsl.GVersion.GVersionFull;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.typed.TType.TFloat;
import com.io7m.jparasol.typed.TType.TMatrix3x3F;
import com.io7m.jparasol.typed.TType.TMatrix4x4F;
import com.io7m.jparasol.typed.TType.TValueType;
import com.io7m.jparasol.typed.TType.TVector2F;
import com.io7m.jparasol.typed.TType.TVector3F;
import com.io7m.jparasol.typed.TType.TVector4F;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragment;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragmentInput;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragmentOutput;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragmentOutputData;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragmentOutputDepth;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderVertex;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderVertexInput;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderVertexOutput;
import com.io7m.jparasol.typed.ast.TASTFragmentShaderOutputVisitor;

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

  /**
   * ES2 specification states:
   * 
   * "The attribute qualifier can be used only with the data types float,
   * vec2, vec3, vec4, mat2, mat3, and mat4. Attribute variables cannot be
   * declared as arrays or structures."
   * 
   * GLSL <= 120 specification states the same.
   * 
   * ES3 specification states:
   * 
   * "Vertex shader inputs can only be float, floating-point vectors,
   * matrices, signed and unsigned integers and integer vectors. Vertex shader
   * inputs cannot be arrays or structures."
   * 
   * "Fragment inputs can only be signed and unsigned integers and integer
   * vectors, float, floating-point vectors, matrices, or arrays or structures
   * of these. Fragment shader inputs that are, or contain, signed or unsigned
   * integers or integer vectors must be qualified with the interpolation
   * qualifier flat."
   */

  private static final class CheckAttributeTypes implements Check
  {
    private static final @Nonnull Set<TValueType> RESTRICTED_TYPES;
    private static final @Nonnull Set<GVersion>   RESTRICTED_VERSIONS;

    static {
      RESTRICTED_TYPES = new HashSet<TValueType>();
      CheckAttributeTypes.RESTRICTED_TYPES.add(TFloat.get());
      CheckAttributeTypes.RESTRICTED_TYPES.add(TVector2F.get());
      CheckAttributeTypes.RESTRICTED_TYPES.add(TVector3F.get());
      CheckAttributeTypes.RESTRICTED_TYPES.add(TVector4F.get());
      CheckAttributeTypes.RESTRICTED_TYPES.add(TMatrix3x3F.get());
      CheckAttributeTypes.RESTRICTED_TYPES.add(TMatrix4x4F.get());

      RESTRICTED_VERSIONS = new HashSet<GVersion>();
      CheckAttributeTypes.RESTRICTED_VERSIONS.add(GVersionES.GLSL_ES_100);
      CheckAttributeTypes.RESTRICTED_VERSIONS.add(GVersionFull.GLSL_110);
      CheckAttributeTypes.RESTRICTED_VERSIONS.add(GVersionFull.GLSL_120);
    }

    public CheckAttributeTypes()
    {
      // Nothing
    }

    static @Nonnull GVersionCheckExclusionReason makeExclusion(
      final @Nonnull String attribute,
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull TValueType type,
      final @Nonnull GVersion version)
      throws ConstraintError
    {
      final StringBuilder m = new StringBuilder();
      m.append("The ");
      m.append(attribute);
      m.append(" ");
      m.append(name.getActual());
      m.append(" is of type ");
      m.append(type.getShowName());
      m.append(" but ");
      m.append(version.getLongName());
      m.append(" only permits the following types: ");

      for (final TValueType t : CheckAttributeTypes.RESTRICTED_TYPES) {
        m.append(t.getShowName());
        m.append(" ");
      }

      return new GVersionCheckExclusionReason(
        name.getFile(),
        name.getPosition(),
        m.toString());
    }

    @Override public void checkFragmentShader(
      final @Nonnull GVersionsSupported supported,
      final @Nonnull TASTDShaderFragment fs)
      throws ConstraintError
    {
      for (final TASTDShaderFragmentInput i : fs.getInputs()) {
        final TValueType type = i.getType();
        if (CheckAttributeTypes.RESTRICTED_TYPES.contains(type) == false) {
          for (final GVersion v : CheckAttributeTypes.RESTRICTED_VERSIONS) {
            v.versionAccept(new GVersionVisitor<Unit, ConstraintError>() {
              @Override public Unit versionVisitES(
                final @Nonnull GVersionES version)
                throws ConstraintError
              {
                final TokenIdentifierLower name = fs.getName();
                final GVersionCheckExclusionReason reason =
                  CheckAttributeTypes.makeExclusion(
                    "fragment shader input",
                    name,
                    type,
                    v);
                supported.excludeES(version, reason);
                return Unit.unit();
              }

              @Override public Unit versionVisitFull(
                final @Nonnull GVersionFull version)
                throws ConstraintError
              {
                final TokenIdentifierLower name = fs.getName();
                final GVersionCheckExclusionReason reason =
                  CheckAttributeTypes.makeExclusion(
                    "fragment shader input",
                    name,
                    type,
                    v);
                supported.excludeFull(version, reason);
                return Unit.unit();
              }
            });
          }
        }
      }

      for (final TASTDShaderFragmentOutput o : fs.getOutputs()) {
        final TValueType type = o.getType();
        if (CheckAttributeTypes.RESTRICTED_TYPES.contains(type) == false) {
          for (final GVersion v : CheckAttributeTypes.RESTRICTED_VERSIONS) {
            v.versionAccept(new GVersionVisitor<Unit, ConstraintError>() {
              @Override public Unit versionVisitES(
                final @Nonnull GVersionES version)
                throws ConstraintError
              {
                final TokenIdentifierLower name = fs.getName();
                final GVersionCheckExclusionReason reason =
                  CheckAttributeTypes.makeExclusion(
                    "fragment shader output",
                    name,
                    type,
                    v);
                supported.excludeES(version, reason);
                return Unit.unit();
              }

              @Override public Unit versionVisitFull(
                final @Nonnull GVersionFull version)
                throws ConstraintError
              {
                final TokenIdentifierLower name = fs.getName();
                final GVersionCheckExclusionReason reason =
                  CheckAttributeTypes.makeExclusion(
                    "fragment shader output",
                    name,
                    type,
                    v);
                supported.excludeFull(version, reason);
                return Unit.unit();
              }
            });
          }
        }
      }
    }

    @Override public void checkVertexShader(
      final @Nonnull GVersionsSupported supported,
      final @Nonnull TASTDShaderVertex vs)
      throws ConstraintError
    {
      for (final TASTDShaderVertexInput i : vs.getInputs()) {
        final TValueType type = i.getType();
        if (CheckAttributeTypes.RESTRICTED_TYPES.contains(type) == false) {
          for (final GVersion v : CheckAttributeTypes.RESTRICTED_VERSIONS) {
            v.versionAccept(new GVersionVisitor<Unit, ConstraintError>() {
              @Override public Unit versionVisitES(
                final @Nonnull GVersionES version)
                throws ConstraintError
              {
                final TokenIdentifierLower name = vs.getName();
                final GVersionCheckExclusionReason reason =
                  CheckAttributeTypes.makeExclusion(
                    "vertex shader input",
                    name,
                    type,
                    v);
                supported.excludeES(version, reason);
                return Unit.unit();
              }

              @Override public Unit versionVisitFull(
                final @Nonnull GVersionFull version)
                throws ConstraintError
              {
                final TokenIdentifierLower name = vs.getName();
                final GVersionCheckExclusionReason reason =
                  CheckAttributeTypes.makeExclusion(
                    "vertex shader input",
                    name,
                    type,
                    v);
                supported.excludeFull(version, reason);
                return Unit.unit();
              }
            });
          }
        }
      }

      for (final TASTDShaderVertexOutput o : vs.getOutputs()) {
        final TValueType type = o.getType();
        if (CheckAttributeTypes.RESTRICTED_TYPES.contains(type) == false) {
          for (final GVersion v : CheckAttributeTypes.RESTRICTED_VERSIONS) {
            v.versionAccept(new GVersionVisitor<Unit, ConstraintError>() {
              @Override public Unit versionVisitES(
                final @Nonnull GVersionES version)
                throws ConstraintError
              {
                final TokenIdentifierLower name = vs.getName();
                final GVersionCheckExclusionReason reason =
                  CheckAttributeTypes.makeExclusion(
                    "vertex shader output",
                    name,
                    type,
                    v);
                supported.excludeES(version, reason);
                return Unit.unit();
              }

              @Override public Unit versionVisitFull(
                final @Nonnull GVersionFull version)
                throws ConstraintError
              {
                final TokenIdentifierLower name = vs.getName();
                final GVersionCheckExclusionReason reason =
                  CheckAttributeTypes.makeExclusion(
                    "vertex shader output",
                    name,
                    type,
                    v);
                supported.excludeFull(version, reason);
                return Unit.unit();
              }
            });
          }
        }
      }
    }

    @Override public String getName()
    {
      return "attribute-types";
    }
  }

  private static final class CheckFragmentOutputCount implements Check
  {
    public CheckFragmentOutputCount()
    {
      // Nothing
    }

    @Override public void checkFragmentShader(
      final @Nonnull GVersionsSupported supported,
      final @Nonnull TASTDShaderFragment fs)
      throws ConstraintError
    {
      final TASTFragmentShaderOutputVisitor<Boolean, ConstraintError> counter =
        new TASTFragmentShaderOutputVisitor<Boolean, ConstraintError>() {
          private int count = 0;

          @Override public Boolean fragmentShaderVisitOutputData(
            final @Nonnull TASTDShaderFragmentOutputData d)
            throws ConstraintError,
              ConstraintError
          {
            ++this.count;

            if (this.count > 1) {
              final TokenIdentifierLower name = fs.getName();
              final GVersionCheckExclusionReason reason =
                new GVersionCheckExclusionReason(
                  name.getFile(),
                  name.getPosition(),
                  "The fragment shader declares more than one output");
              supported.excludeES(GVersionES.GLSL_ES_100, reason);
              return Boolean.FALSE;
            }

            return Boolean.TRUE;
          }

          @Override public Boolean fragmentShaderVisitOutputDepth(
            final @Nonnull TASTDShaderFragmentOutputDepth v)
            throws ConstraintError,
              ConstraintError
          {
            return Boolean.TRUE;
          }
        };

      for (final TASTDShaderFragmentOutput o : fs.getOutputs()) {
        final Boolean r = o.fragmentShaderOutputVisitableAccept(counter);
        if (r.booleanValue() == false) {
          return;
        }
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
      return "fragment-output-count";
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
    checks.add(new CheckFragmentOutputCount());
    checks.add(new CheckAttributeTypes());
    return Collections.unmodifiableList(checks);
  }

  public static @Nonnull GVersionChecker newVersionChecker(
    final @Nonnull Log log)
  {
    return new GVersionChecker(log);
  }

  private final @Nonnull Log log;

  private GVersionChecker(
    final @Nonnull Log in_log)
  {
    this.log = new Log(in_log, "version-checker");
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
