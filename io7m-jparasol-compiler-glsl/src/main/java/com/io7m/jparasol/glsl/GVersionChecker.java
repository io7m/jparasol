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
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jfunctional.Unit;
import com.io7m.jlog.LogLevel;
import com.io7m.jlog.LogUsableType;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jparasol.core.GVersionES;
import com.io7m.jparasol.core.GVersionFull;
import com.io7m.jparasol.core.GVersionType;
import com.io7m.jparasol.core.GVersionVisitorType;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.typed.Referenced;
import com.io7m.jparasol.typed.TType.TFloat;
import com.io7m.jparasol.typed.TType.TMatrix3x3F;
import com.io7m.jparasol.typed.TType.TMatrix4x4F;
import com.io7m.jparasol.typed.TType.TValueType;
import com.io7m.jparasol.typed.TType.TVector2F;
import com.io7m.jparasol.typed.TType.TVector3F;
import com.io7m.jparasol.typed.TType.TVector4F;
import com.io7m.jparasol.typed.ast.TASTCompilation;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDExternal;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDFunctionDefined;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDFunctionExternal;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragment;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragmentInput;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragmentLocalDiscard;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragmentLocalValue;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragmentOutput;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragmentOutputAssignment;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragmentOutputData;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragmentOutputDepth;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragmentParameter;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderVertex;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderVertexInput;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderVertexLocalValue;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderVertexOutput;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderVertexOutputAssignment;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderVertexParameter;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDTerm;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDValueDefined;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDValueExternal;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDValueLocal;
import com.io7m.jparasol.typed.ast.TASTExpression;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTEApplication;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTEBoolean;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTEConditional;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTEInteger;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTELet;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTEMatrixColumnAccess;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTENew;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTEReal;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTERecord;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTERecordProjection;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTESwizzle;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTEVariable;
import com.io7m.jparasol.typed.ast.TASTExpressionVisitorType;
import com.io7m.jparasol.typed.ast.TASTFragmentShaderLocalVisitorType;
import com.io7m.jparasol.typed.ast.TASTFragmentShaderOutputVisitorType;
import com.io7m.jparasol.typed.ast.TASTFragmentShaderVisitorType;
import com.io7m.jparasol.typed.ast.TASTLocalLevelVisitorType;
import com.io7m.jparasol.typed.ast.TASTTermName.TASTTermNameExternal;
import com.io7m.jparasol.typed.ast.TASTTermName.TASTTermNameGlobal;
import com.io7m.jparasol.typed.ast.TASTTermName.TASTTermNameLocal;
import com.io7m.jparasol.typed.ast.TASTTermNameFlat;
import com.io7m.jparasol.typed.ast.TASTTermNameVisitorType;
import com.io7m.jparasol.typed.ast.TASTTermVisitorType;
import com.io7m.jparasol.typed.ast.TASTVertexShaderLocalVisitorType;
import com.io7m.jparasol.typed.ast.TASTVertexShaderVisitorType;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * A version checker. Checks a GLSL program to see if it can execute on the
 * given GLSL versions.
 */

@EqualityReference public final class GVersionChecker
{
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

  @EqualityReference private static final class CheckAttributeTypes implements
    CheckType
  {
    private static final Set<TValueType>   RESTRICTED_TYPES;
    private static final Set<GVersionType> RESTRICTED_VERSIONS;

    static {
      RESTRICTED_TYPES = new HashSet<TValueType>();
      CheckAttributeTypes.RESTRICTED_TYPES.add(TFloat.get());
      CheckAttributeTypes.RESTRICTED_TYPES.add(TVector2F.get());
      CheckAttributeTypes.RESTRICTED_TYPES.add(TVector3F.get());
      CheckAttributeTypes.RESTRICTED_TYPES.add(TVector4F.get());
      CheckAttributeTypes.RESTRICTED_TYPES.add(TMatrix3x3F.get());
      CheckAttributeTypes.RESTRICTED_TYPES.add(TMatrix4x4F.get());

      RESTRICTED_VERSIONS = new HashSet<GVersionType>();
      CheckAttributeTypes.RESTRICTED_VERSIONS.add(GVersionES.GLSL_ES_100);
      CheckAttributeTypes.RESTRICTED_VERSIONS.add(GVersionFull.GLSL_110);
      CheckAttributeTypes.RESTRICTED_VERSIONS.add(GVersionFull.GLSL_120);
    }

    static GVersionCheckExclusionReason makeExclusion(
      final String attribute,
      final TokenIdentifierLower name,
      final TValueType type,
      final GVersionType version)

    {
      final StringBuilder m = new StringBuilder();
      m.append("The ");
      m.append(attribute);
      m.append(" ");
      m.append(name.getActual());
      m.append(" is of type ");
      m.append(type.getShowName());
      m.append(" but ");
      m.append(version.versionGetLongName());
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

    public CheckAttributeTypes()
    {
      // Nothing
    }

    @Override public void checkFragmentShader(
      final TASTCompilation typed,
      final Referenced in_referenced,
      final GVersionsSupported supported,
      final TASTDShaderFragment fs)

    {
      for (final TASTDShaderFragmentInput i : fs.getInputs()) {
        final TValueType type = i.getType();
        if (CheckAttributeTypes.RESTRICTED_TYPES.contains(type) == false) {
          for (final GVersionType v : CheckAttributeTypes.RESTRICTED_VERSIONS) {
            v
              .versionAccept(new GVersionVisitorType<Unit, UnreachableCodeException>() {
                @Override public Unit versionVisitES(
                  final GVersionES version)
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
                  final GVersionFull version)
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
          for (final GVersionType v : CheckAttributeTypes.RESTRICTED_VERSIONS) {
            v
              .versionAccept(new GVersionVisitorType<Unit, UnreachableCodeException>() {
                @Override public Unit versionVisitES(
                  final GVersionES version)
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
                  final GVersionFull version)
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
      final TASTCompilation in_typed,
      final Referenced in_referenced,
      final GVersionsSupported supported,
      final TASTDShaderVertex vs)

    {
      for (final TASTDShaderVertexInput i : vs.getInputs()) {
        final TValueType type = i.getType();
        if (CheckAttributeTypes.RESTRICTED_TYPES.contains(type) == false) {
          for (final GVersionType v : CheckAttributeTypes.RESTRICTED_VERSIONS) {
            v
              .versionAccept(new GVersionVisitorType<Unit, UnreachableCodeException>() {
                @Override public Unit versionVisitES(
                  final GVersionES version)
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
                  final GVersionFull version)
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
          for (final GVersionType v : CheckAttributeTypes.RESTRICTED_VERSIONS) {
            v
              .versionAccept(new GVersionVisitorType<Unit, UnreachableCodeException>() {
                @Override public Unit versionVisitES(
                  final GVersionES version)
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
                  final GVersionFull version)
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

  @EqualityReference private static final class CheckFragmentOutputCount implements
    CheckType
  {
    public CheckFragmentOutputCount()
    {
      // Nothing
    }

    @Override public void checkFragmentShader(
      final TASTCompilation typed,
      final Referenced in_referenced,
      final GVersionsSupported supported,
      final TASTDShaderFragment fs)
    {
      final TASTFragmentShaderOutputVisitorType<Boolean, UnreachableCodeException> counter =
        new TASTFragmentShaderOutputVisitorType<Boolean, UnreachableCodeException>() {
          private int count;

          @Override public Boolean fragmentShaderVisitOutputData(
            final TASTDShaderFragmentOutputData d)
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
            final TASTDShaderFragmentOutputDepth v)
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
      final TASTCompilation typed,
      final Referenced in_referenced,
      final GVersionsSupported supported,
      final TASTDShaderVertex vs)
    {
      // Nothing
    }

    @Override public String getName()
    {
      return "fragment-output-count";
    }
  }

  /**
   * Check known functions for version support.
   */

  @EqualityReference private static final class CheckFunctionVersionSupport implements
    CheckType
  {
    @EqualityReference private static final class ExpressionChecker implements
      TASTExpressionVisitorType<Unit, Unit, UnreachableCodeException>
    {
      private final GVersionsSupported supported;
      private final TASTCompilation    typed;

      public ExpressionChecker(
        final TASTCompilation in_typed,
        final GVersionsSupported in_supported)
      {
        this.typed = in_typed;
        this.supported = in_supported;
      }

      @Override public Unit expressionVisitApplication(
        final @Nullable List<Unit> arguments,
        final TASTEApplication e)
      {
        final TASTCompilation comp = this.typed;
        final GVersionsSupported exclusions = this.supported;

        return e.getName().termNameVisitableAccept(
          new TASTTermNameVisitorType<Unit, UnreachableCodeException>() {
            @Override public Unit termNameVisitExternal(
              final TASTTermNameExternal t)
            {
              return Unit.unit();
            }

            @Override public Unit termNameVisitGlobal(
              final TASTTermNameGlobal t)
            {
              final TASTTermNameFlat flat =
                TASTTermNameFlat.fromTermNameGlobal(t);
              final TASTDTerm ext = comp.lookupTerm(flat);
              assert ext != null;
              return ext.termVisitableAccept(new TermChecker(exclusions));
            }

            @Override public Unit termNameVisitLocal(
              final TASTTermNameLocal t)
            {
              return Unit.unit();
            }
          });
      }

      @Override public boolean expressionVisitApplicationPre(
        final TASTEApplication e)
      {
        return true;
      }

      @Override public Unit expressionVisitBoolean(
        final TASTEBoolean e)
      {
        return Unit.unit();
      }

      @Override public Unit expressionVisitConditional(
        final @Nullable Unit condition,
        final @Nullable Unit left,
        final @Nullable Unit right,
        final TASTEConditional e)
      {
        return Unit.unit();
      }

      @Override public void expressionVisitConditionalConditionPost(
        final TASTEConditional e)
      {
        // Nothing
      }

      @Override public void expressionVisitConditionalConditionPre(
        final TASTEConditional e)
      {
        // Nothing
      }

      @Override public void expressionVisitConditionalLeftPost(
        final TASTEConditional e)
      {
        // Nothing
      }

      @Override public void expressionVisitConditionalLeftPre(
        final TASTEConditional e)
      {
        // Nothing
      }

      @Override public boolean expressionVisitConditionalPre(
        final TASTEConditional e)
      {
        return true;
      }

      @Override public void expressionVisitConditionalRightPost(
        final TASTEConditional e)
      {
        // Nothing
      }

      @Override public void expressionVisitConditionalRightPre(
        final TASTEConditional e)
      {
        // Nothing
      }

      @Override public Unit expressionVisitInteger(
        final TASTEInteger e)
      {
        return Unit.unit();
      }

      @Override public Unit expressionVisitLet(
        final @Nullable List<Unit> bindings,
        final @Nullable Unit body,
        final TASTELet e)
      {
        return Unit.unit();
      }

      @Override public @Nullable
        TASTLocalLevelVisitorType<Unit, UnreachableCodeException>
        expressionVisitLetPre(
          final TASTELet e)
      {
        return new TASTLocalLevelVisitorType<Unit, UnreachableCodeException>() {
          @Override public Unit localVisitValueLocal(
            final TASTDValueLocal v)

          {
            final TASTExpression ex = v.getExpression();
            return ex.expressionVisitableAccept(new ExpressionChecker(
              ExpressionChecker.this.typed,
              ExpressionChecker.this.supported));
          }
        };
      }

      @Override public Unit expressionVisitMatrixColumnAccess(
        final @Nullable Unit body,
        final TASTEMatrixColumnAccess e)
      {
        return Unit.unit();
      }

      @Override public boolean expressionVisitMatrixColumnAccessPre(
        final TASTEMatrixColumnAccess e)
      {
        return true;
      }

      @Override public Unit expressionVisitNew(
        final @Nullable List<Unit> arguments,
        final TASTENew e)
      {
        return Unit.unit();
      }

      @Override public boolean expressionVisitNewPre(
        final TASTENew e)
      {
        return true;
      }

      @Override public Unit expressionVisitReal(
        final TASTEReal e)
      {
        return Unit.unit();
      }

      @Override public Unit expressionVisitRecord(
        final TASTERecord e)
      {
        return Unit.unit();
      }

      @Override public Unit expressionVisitRecordProjection(
        final @Nullable Unit body,
        final TASTERecordProjection e)
      {
        return Unit.unit();
      }

      @Override public boolean expressionVisitRecordProjectionPre(
        final @Nullable TASTERecordProjection e)
      {
        return true;
      }

      @Override public Unit expressionVisitSwizzle(
        final @Nullable Unit body,
        final TASTESwizzle e)
      {
        return Unit.unit();
      }

      @Override public boolean expressionVisitSwizzlePre(
        final TASTESwizzle e)
      {
        return true;
      }

      @Override public Unit expressionVisitVariable(
        final TASTEVariable e)
      {
        return Unit.unit();
      }
    }

    @EqualityReference private static final class FragmentShaderChecker implements
      TASTFragmentShaderVisitorType<Unit, Unit, Unit, Unit, Unit, Unit, UnreachableCodeException>
    {
      private final Referenced         referenced;
      private final GVersionsSupported supported;
      private final TASTCompilation    typed;

      private FragmentShaderChecker(
        final GVersionsSupported in_supported,
        final TASTCompilation in_typed,
        final Referenced in_referenced)
      {
        this.supported = in_supported;
        this.typed = in_typed;
        this.referenced = in_referenced;
      }

      @Override public Unit fragmentShaderVisit(
        final List<Unit> inputs,
        final List<Unit> parameters,
        final List<Unit> outputs,
        final List<Unit> locals,
        final List<Unit> output_assignments,
        final TASTDShaderFragment f)
      {
        for (final TASTTermNameFlat name : this.referenced.getTerms()) {
          final TASTDTerm term = this.typed.lookupTerm(name);
          assert term != null;
          term.termVisitableAccept(new TermChecker(this.supported));
        }
        return Unit.unit();
      }

      @Override public Unit fragmentShaderVisitInput(
        final TASTDShaderFragmentInput i)
      {
        return Unit.unit();
      }

      @Override public @Nullable
        TASTFragmentShaderLocalVisitorType<Unit, UnreachableCodeException>
        fragmentShaderVisitLocalsPre()
      {
        return new TASTFragmentShaderLocalVisitorType<Unit, UnreachableCodeException>() {
          @Override public Unit fragmentShaderVisitLocalDiscard(
            final TASTDShaderFragmentLocalDiscard d)
          {
            final TASTExpression e = d.getExpression();
            return e.expressionVisitableAccept(new ExpressionChecker(
              FragmentShaderChecker.this.typed,
              FragmentShaderChecker.this.supported));
          }

          @Override public Unit fragmentShaderVisitLocalValue(
            final TASTDShaderFragmentLocalValue v)
          {
            final TASTExpression e = v.getValue().getExpression();
            return e.expressionVisitableAccept(new ExpressionChecker(
              FragmentShaderChecker.this.typed,
              FragmentShaderChecker.this.supported));
          }
        };
      }

      @Override public Unit fragmentShaderVisitOutput(
        final TASTDShaderFragmentOutput o)
      {
        return Unit.unit();
      }

      @Override public Unit fragmentShaderVisitOutputAssignment(
        final TASTDShaderFragmentOutputAssignment a)
      {
        return Unit.unit();
      }

      @Override public Unit fragmentShaderVisitParameter(
        final TASTDShaderFragmentParameter p)
      {
        return Unit.unit();
      }
    }

    @EqualityReference private static final class TermChecker implements
      TASTTermVisitorType<Unit, UnreachableCodeException>
    {
      private final GVersionsSupported exclusions;

      private TermChecker(
        final GVersionsSupported in_exclusions)
      {
        this.exclusions = in_exclusions;
      }

      @Override public Unit termVisitFunctionDefined(
        final TASTDFunctionDefined f)
      {
        return Unit.unit();
      }

      @Override public Unit termVisitFunctionExternal(
        final TASTDFunctionExternal f)
      {
        final TASTDExternal ee = f.getExternal();

        final SortedSet<GVersionES> supported_es = ee.getSupportedES();
        final SortedSet<GVersionFull> supported_full = ee.getSupportedFull();

        final TokenIdentifierLower name = f.getName();
        for (final GVersionES v : GVersionES.ALL) {
          assert v != null;
          if (supported_es.contains(v) == false) {
            final StringBuilder m = new StringBuilder();
            m.append("Function '");
            m.append(name.getActual());
            m.append("' unavailable on this GLSL version");
            final String r = m.toString();
            assert r != null;
            this.exclusions.excludeES(v, new GVersionCheckExclusionReason(
              name.getFile(),
              name.getPosition(),
              r));
          }
        }

        for (final GVersionFull v : GVersionFull.ALL) {
          assert v != null;
          if (supported_full.contains(v) == false) {
            final StringBuilder m = new StringBuilder();
            m.append("Function '");
            m.append(name.getActual());
            m.append("' unavailable on this GLSL version");
            final String r = m.toString();
            assert r != null;
            this.exclusions.excludeFull(v, new GVersionCheckExclusionReason(
              name.getFile(),
              name.getPosition(),
              r));
          }
        }

        return Unit.unit();
      }

      @Override public Unit termVisitValueDefined(
        final TASTDValueDefined v)
      {
        return Unit.unit();
      }

      @Override public Unit termVisitValueExternal(
        final TASTDValueExternal v)
      {
        return Unit.unit();
      }
    }

    @EqualityReference private static final class VertexShaderChecker implements
      TASTVertexShaderVisitorType<Unit, Unit, Unit, Unit, Unit, Unit, UnreachableCodeException>
    {
      private final Referenced         referenced;
      private final GVersionsSupported supported;
      private final TASTCompilation    typed;

      private VertexShaderChecker(
        final GVersionsSupported in_supported,
        final TASTCompilation in_typed,
        final Referenced in_referenced)
      {
        this.supported = in_supported;
        this.typed = in_typed;
        this.referenced = in_referenced;
      }

      @Override public Unit vertexShaderVisit(
        final List<Unit> inputs,
        final List<Unit> parameters,
        final List<Unit> outputs,
        final List<Unit> locals,
        final List<Unit> output_assignments,
        final TASTDShaderVertex v)
      {
        for (final TASTTermNameFlat name : this.referenced.getTerms()) {
          final TASTDTerm term = this.typed.lookupTerm(name);
          assert term != null;
          term.termVisitableAccept(new TermChecker(this.supported));
        }

        return Unit.unit();
      }

      @Override public Unit vertexShaderVisitInput(
        final TASTDShaderVertexInput i)
      {
        return Unit.unit();
      }

      @Override public @Nullable
        TASTVertexShaderLocalVisitorType<Unit, UnreachableCodeException>
        vertexShaderVisitLocalsPre()
      {
        return new TASTVertexShaderLocalVisitorType<Unit, UnreachableCodeException>() {
          @Override public Unit vertexShaderVisitLocalValue(
            final TASTDShaderVertexLocalValue v)
            throws UnreachableCodeException
          {
            final TASTExpression e = v.getValue().getExpression();
            return e.expressionVisitableAccept(new ExpressionChecker(
              VertexShaderChecker.this.typed,
              VertexShaderChecker.this.supported));
          }
        };
      }

      @Override public Unit vertexShaderVisitOutput(
        final TASTDShaderVertexOutput o)
      {
        return Unit.unit();
      }

      @Override public Unit vertexShaderVisitOutputAssignment(
        final TASTDShaderVertexOutputAssignment a)
      {
        return Unit.unit();
      }

      @Override public Unit vertexShaderVisitParameter(
        final TASTDShaderVertexParameter p)
      {
        return Unit.unit();
      }
    }

    public CheckFunctionVersionSupport()
    {
      // Nothing
    }

    @Override public void checkFragmentShader(
      final TASTCompilation in_typed,
      final Referenced in_referenced,
      final GVersionsSupported supported,
      final TASTDShaderFragment fs)
    {
      fs.fragmentShaderVisitableAccept(new FragmentShaderChecker(
        supported,
        in_typed,
        in_referenced));
    }

    @Override public void checkVertexShader(
      final TASTCompilation in_typed,
      final Referenced in_referenced,
      final GVersionsSupported supported,
      final TASTDShaderVertex vs)
    {
      vs.vertexShaderVisitableAccept(new VertexShaderChecker(
        supported,
        in_typed,
        in_referenced));
    }

    @Override public String getName()
    {
      return "function-version-support";
    }
  }

  private interface CheckType
  {
    void checkFragmentShader(
      final TASTCompilation typed,
      final Referenced referenced,
      final GVersionsSupported supported,
      final TASTDShaderFragment fs);

    void checkVertexShader(
      final TASTCompilation typed,
      final Referenced referenced,
      final GVersionsSupported supported,
      final TASTDShaderVertex vs);

    String getName();
  }

  private static final List<CheckType> CHECKS;

  static {
    CHECKS = GVersionChecker.makeChecks();
  }

  private static GVersionsSupported checkRequired(
    final GVersionsSupported s,
    final SortedSet<GVersionFull> required_full,
    final SortedSet<GVersionES> required_es)
    throws GVersionCheckerError
  {
    final SortedSet<GVersionFull> supported_full = s.getFullVersions();
    final SortedSet<GVersionES> supported_es = s.getESVersions();

    final Map<GVersionFull, List<GVersionCheckExclusionReason>> exclusions_full =
      new HashMap<GVersionFull, List<GVersionCheckExclusionReason>>();
    final Map<GVersionES, List<GVersionCheckExclusionReason>> exclusions_es =
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

  private static List<CheckType> makeChecks()
  {
    final List<CheckType> checks = new ArrayList<CheckType>();
    checks.add(new CheckFragmentOutputCount());
    checks.add(new CheckAttributeTypes());
    checks.add(new CheckFunctionVersionSupport());
    return Collections.unmodifiableList(checks);
  }

  /**
   * Construct a new version checker.
   *
   * @param log
   *          A log interface
   * @return A new version checker
   */

  public static GVersionChecker newVersionChecker(
    final LogUsableType log)
  {
    return new GVersionChecker(log);
  }

  private final LogUsableType log;

  private GVersionChecker(
    final LogUsableType in_log)
  {
    this.log = NullCheck.notNull(in_log, "Log").with("version-checker");
  }

  /**
   * Check the given fragment shader.
   *
   * @param typed
   *          The compilation
   * @param referenced
   *          The referenced terms and types
   * @param f
   *          The shader
   * @param required_full
   *          The set of required GLSL versions
   * @param required_es
   *          The set of required GLSL ES versions
   * @return The set of supported versions
   *
   * @throws GVersionCheckerError
   *           If an error occurs
   */

  public GVersionsSupported checkFragmentShader(
    final TASTCompilation typed,
    final Referenced referenced,
    final TASTDShaderFragment f,
    final SortedSet<GVersionFull> required_full,
    final SortedSet<GVersionES> required_es)
    throws GVersionCheckerError
  {
    NullCheck.notNull(typed, "Compilation");
    NullCheck.notNull(f, "Shader");
    NullCheck.notNullAll(required_full, "GLSL versions");
    NullCheck.notNullAll(required_es, "GLSL ES versions");

    final StringBuilder m = new StringBuilder();
    final GVersionsSupported s = GVersionsSupported.all();
    for (final CheckType c : GVersionChecker.CHECKS) {
      if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
        m.setLength(0);
        m.append("Running check: ");
        m.append(c.getName());
        this.log.debug(m.toString());
      }
      c.checkFragmentShader(typed, referenced, s, f);
    }

    return GVersionChecker.checkRequired(s, required_full, required_es);
  }

  /**
   * Check the given vertex shader.
   *
   * @param typed
   *          The compilation
   * @param referenced
   *          The referenced terms and types
   * @param v
   *          The shader
   * @param required_full
   *          The set of required GLSL versions
   * @param required_es
   *          The set of required GLSL ES versions
   * @return The set of supported versions
   *
   * @throws GVersionCheckerError
   *           If an error occurs
   */

  public GVersionsSupported checkVertexShader(
    final TASTCompilation typed,
    final Referenced referenced,
    final TASTDShaderVertex v,
    final SortedSet<GVersionFull> required_full,
    final SortedSet<GVersionES> required_es)
    throws GVersionCheckerError
  {
    NullCheck.notNull(typed, "Compilation");
    NullCheck.notNull(v, "Shader");
    NullCheck.notNullAll(required_full, "GLSL versions");
    NullCheck.notNullAll(required_es, "GLSL ES versions");

    final StringBuilder m = new StringBuilder();
    final GVersionsSupported s = GVersionsSupported.all();
    for (final CheckType c : GVersionChecker.CHECKS) {
      if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
        m.setLength(0);
        m.append("Running check: ");
        m.append(c.getName());
        this.log.debug(m.toString());
      }
      c.checkVertexShader(typed, referenced, s, v);
    }

    return GVersionChecker.checkRequired(s, required_full, required_es);
  }
}
