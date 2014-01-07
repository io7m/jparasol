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

import java.util.List;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.glsl.GFFIExpression.GFFIExpressionBuiltIn;
import com.io7m.jparasol.glsl.GVersion.GVersionES;
import com.io7m.jparasol.glsl.GVersion.GVersionFull;
import com.io7m.jparasol.glsl.ast.GASTExpression;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEBinaryOp.GASTEBinaryOpEqual;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEBinaryOp.GASTEBinaryOpGreaterThan;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEBinaryOp.GASTEBinaryOpGreaterThanOrEqual;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEBinaryOp.GASTEBinaryOpLesserThan;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEBinaryOp.GASTEBinaryOpLesserThanOrEqual;
import com.io7m.jparasol.glsl.ast.GTermName.GTermNameGlobal;
import com.io7m.jparasol.typed.TType.TBoolean;
import com.io7m.jparasol.typed.TType.TFloat;
import com.io7m.jparasol.typed.TType.TFunction;
import com.io7m.jparasol.typed.TType.TFunctionArgument;
import com.io7m.jparasol.typed.TType.TInteger;
import com.io7m.jparasol.typed.TType.TMatrix3x3F;
import com.io7m.jparasol.typed.TType.TMatrix4x4F;
import com.io7m.jparasol.typed.TType.TSampler2D;
import com.io7m.jparasol.typed.TType.TSamplerCube;
import com.io7m.jparasol.typed.TType.TValueType;
import com.io7m.jparasol.typed.TType.TVector2F;
import com.io7m.jparasol.typed.TType.TVector2I;
import com.io7m.jparasol.typed.TType.TVector3F;
import com.io7m.jparasol.typed.TType.TVector3I;
import com.io7m.jparasol.typed.TType.TVector4F;
import com.io7m.jparasol.typed.TType.TVector4I;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDFunctionExternal;

public final class GFFIExpressionEmitters
{
  static @Nonnull GFFIExpression com_io7m_parasol_float_absolute(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericUnary(
      "abs",
      f,
      arguments,
      version,
      TFloat.get(),
      TFloat.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_float_add(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericPlus(
      f,
      arguments,
      version,
      TFloat.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_float_arc_cosine(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericUnary(
      "acos",
      f,
      arguments,
      version,
      TFloat.get(),
      TFloat.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_float_arc_sine(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericUnary(
      "asin",
      f,
      arguments,
      version,
      TFloat.get(),
      TFloat.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_float_arc_tangent(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericUnary(
      "atan",
      f,
      arguments,
      version,
      TFloat.get(),
      TFloat.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_float_ceiling(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericUnary(
      "ceil",
      f,
      arguments,
      version,
      TFloat.get(),
      TFloat.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_float_clamp(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericClamp(
      f,
      arguments,
      version,
      TFloat.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_float_cosine(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericUnary(
      "cosine",
      f,
      arguments,
      version,
      TFloat.get(),
      TFloat.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_float_divide(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericDivide(
      f,
      arguments,
      version,
      TFloat.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_float_equals(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericEquals(
      f,
      arguments,
      version,
      TFloat.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_float_floor(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericUnary(
      "floor",
      f,
      arguments,
      version,
      TFloat.get(),
      TFloat.get());
  }

  @SuppressWarnings("unused") static @Nonnull
    GFFIExpression
    com_io7m_parasol_float_greater(
      final @Nonnull TASTDFunctionExternal f,
      final @Nonnull List<GASTExpression> arguments,
      final @Nonnull GVersion version)
  {
    assert arguments.size() == 2;
    assert f.getType().getArguments().size() == 2;
    assert f.getType().getArguments().get(0).getType().equals(TFloat.get());
    assert f.getType().getArguments().get(1).getType().equals(TFloat.get());
    assert f.getType().getReturnType().equals(TBoolean.get());

    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTEBinaryOpGreaterThan(arguments.get(0), arguments.get(1)));
  }

  @SuppressWarnings("unused") static @Nonnull
    GFFIExpression
    com_io7m_parasol_float_greater_or_equal(
      final @Nonnull TASTDFunctionExternal f,
      final @Nonnull List<GASTExpression> arguments,
      final @Nonnull GVersion version)
  {
    assert arguments.size() == 2;
    assert f.getType().getArguments().size() == 2;
    assert f.getType().getArguments().get(0).getType().equals(TFloat.get());
    assert f.getType().getArguments().get(1).getType().equals(TFloat.get());
    assert f.getType().getReturnType().equals(TBoolean.get());

    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTEBinaryOpGreaterThanOrEqual(arguments.get(0), arguments.get(1)));
  }

  static @Nonnull GFFIExpression com_io7m_parasol_float_interpolate(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericInterpolate(
      f,
      arguments,
      version,
      TFloat.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_float_is_infinite(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
    throws ConstraintError
  {
    return version
      .versionAccept(new GVersionVisitor<GFFIExpression, ConstraintError>() {
        @Override public GFFIExpression versionVisitES(
          final GVersionES v)
          throws ConstraintError
        {
          return new GFFIExpression.GFFIExpressionDefined();
        }

        @SuppressWarnings("synthetic-access") @Override public
          GFFIExpression
          versionVisitFull(
            final GVersionFull v)
            throws ConstraintError
        {
          if (v.compareTo(GVersionFull.GLSL_120) <= 0) {
            return new GFFIExpression.GFFIExpressionDefined();
          }
          return GFFIExpressionEmitters.genericUnary(
            "isinf",
            f,
            arguments,
            version,
            TFloat.get(),
            TBoolean.get());
        }
      });
  }

  static @Nonnull GFFIExpression com_io7m_parasol_float_is_nan(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
    throws ConstraintError
  {
    return version
      .versionAccept(new GVersionVisitor<GFFIExpression, ConstraintError>() {
        @Override public GFFIExpression versionVisitES(
          final GVersionES v)
          throws ConstraintError
        {
          return new GFFIExpression.GFFIExpressionDefined();
        }

        @SuppressWarnings("synthetic-access") @Override public
          GFFIExpression
          versionVisitFull(
            final GVersionFull v)
            throws ConstraintError
        {
          if (v.compareTo(GVersionFull.GLSL_120) <= 0) {
            return new GFFIExpression.GFFIExpressionDefined();
          }
          return GFFIExpressionEmitters.genericUnary(
            "isnan",
            f,
            arguments,
            version,
            TFloat.get(),
            TBoolean.get());
        }
      });
  }

  @SuppressWarnings("unused") static @Nonnull
    GFFIExpression
    com_io7m_parasol_float_lesser(
      final @Nonnull TASTDFunctionExternal f,
      final @Nonnull List<GASTExpression> arguments,
      final @Nonnull GVersion version)
  {
    assert arguments.size() == 2;
    assert f.getType().getArguments().size() == 2;
    assert f.getType().getArguments().get(0).getType().equals(TFloat.get());
    assert f.getType().getArguments().get(1).getType().equals(TFloat.get());
    assert f.getType().getReturnType().equals(TBoolean.get());

    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTEBinaryOpLesserThan(arguments.get(0), arguments.get(1)));
  }

  @SuppressWarnings("unused") static @Nonnull
    GFFIExpression
    com_io7m_parasol_float_lesser_or_equal(
      final @Nonnull TASTDFunctionExternal f,
      final @Nonnull List<GASTExpression> arguments,
      final @Nonnull GVersion version)
  {
    assert arguments.size() == 2;
    assert f.getType().getArguments().size() == 2;
    assert f.getType().getArguments().get(0).getType().equals(TFloat.get());
    assert f.getType().getArguments().get(1).getType().equals(TFloat.get());
    assert f.getType().getReturnType().equals(TBoolean.get());

    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTEBinaryOpLesserThanOrEqual(arguments.get(0), arguments.get(1)));
  }

  @SuppressWarnings("unused") static @Nonnull
    GFFIExpression
    com_io7m_parasol_float_maximum(
      final @Nonnull TASTDFunctionExternal f,
      final @Nonnull List<GASTExpression> arguments,
      final @Nonnull GVersion version)
  {
    assert arguments.size() == 2;
    assert f.getType().getArguments().size() == 2;
    assert f.getType().getArguments().get(0).getType().equals(TFloat.get());
    assert f.getType().getArguments().get(1).getType().equals(TFloat.get());
    assert f.getType().getReturnType().equals(TFloat.get());

    final GTermNameGlobal tname = new GTermNameGlobal("max");
    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTExpression.GASTEApplication(tname, TFloat.get(), arguments));
  }

  @SuppressWarnings("unused") static @Nonnull
    GFFIExpression
    com_io7m_parasol_float_minimum(
      final @Nonnull TASTDFunctionExternal f,
      final @Nonnull List<GASTExpression> arguments,
      final @Nonnull GVersion version)
  {
    assert arguments.size() == 2;
    assert f.getType().getArguments().size() == 2;
    assert f.getType().getArguments().get(0).getType().equals(TFloat.get());
    assert f.getType().getArguments().get(1).getType().equals(TFloat.get());
    assert f.getType().getReturnType().equals(TFloat.get());

    final GTermNameGlobal tname = new GTermNameGlobal("min");
    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTExpression.GASTEApplication(tname, TFloat.get(), arguments));
  }

  @SuppressWarnings("unused") static @Nonnull
    GFFIExpression
    com_io7m_parasol_float_modulo(
      final @Nonnull TASTDFunctionExternal f,
      final @Nonnull List<GASTExpression> arguments,
      final @Nonnull GVersion version)
  {
    assert arguments.size() == 2;
    assert f.getType().getArguments().size() == 2;
    assert f.getType().getArguments().get(0).getType().equals(TFloat.get());
    assert f.getType().getArguments().get(1).getType().equals(TFloat.get());
    assert f.getType().getReturnType().equals(TFloat.get());

    final GTermNameGlobal tname = new GTermNameGlobal("mod");
    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTExpression.GASTEApplication(tname, TFloat.get(), arguments));
  }

  static @Nonnull GFFIExpression com_io7m_parasol_float_multiply(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericMultiply(
      f,
      arguments,
      version,
      TFloat.get());
  }

  @SuppressWarnings("unused") static @Nonnull
    GFFIExpression
    com_io7m_parasol_float_power(
      final @Nonnull TASTDFunctionExternal f,
      final @Nonnull List<GASTExpression> arguments,
      final @Nonnull GVersion version)
  {
    assert arguments.size() == 2;
    assert f.getType().getArguments().size() == 2;
    assert f.getType().getArguments().get(0).getType().equals(TFloat.get());
    assert f.getType().getArguments().get(1).getType().equals(TFloat.get());
    assert f.getType().getReturnType().equals(TFloat.get());

    final GTermNameGlobal tname = new GTermNameGlobal("pow");
    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTExpression.GASTEApplication(tname, TFloat.get(), arguments));
  }

  /**
   * "round" isn't supported universally, so it's necessary to fall back to
   * "floor" occasionally.
   */

  static @Nonnull GFFIExpression com_io7m_parasol_float_round(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
    throws ConstraintError
  {
    return version
      .versionAccept(new GVersionVisitor<GFFIExpression, ConstraintError>() {
        @SuppressWarnings("synthetic-access") @Override public
          GFFIExpression
          versionVisitES(
            final GVersionES v)
            throws ConstraintError
        {
          return GFFIExpressionEmitters.genericUnary(
            "floor",
            f,
            arguments,
            version,
            TFloat.get(),
            TFloat.get());
        }

        @SuppressWarnings("synthetic-access") @Override public
          GFFIExpression
          versionVisitFull(
            final GVersionFull v)
            throws ConstraintError
        {
          if (v.compareTo(GVersionFull.GLSL_120) <= 0) {
            return GFFIExpressionEmitters.genericUnary(
              "floor",
              f,
              arguments,
              version,
              TFloat.get(),
              TFloat.get());
          }

          return GFFIExpressionEmitters.genericUnary(
            "round",
            f,
            arguments,
            version,
            TFloat.get(),
            TFloat.get());
        }
      });
  }

  static @Nonnull GFFIExpression com_io7m_parasol_float_sign(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
    throws ConstraintError
  {
    return version
      .versionAccept(new GVersionVisitor<GFFIExpression, ConstraintError>() {
        @Override public GFFIExpression versionVisitES(
          final GVersionES v)
          throws ConstraintError
        {
          return new GFFIExpression.GFFIExpressionDefined();
        }

        @SuppressWarnings("synthetic-access") @Override public
          GFFIExpression
          versionVisitFull(
            final GVersionFull v)
            throws ConstraintError
        {
          if (v.compareTo(GVersionFull.GLSL_120) <= 0) {
            return new GFFIExpression.GFFIExpressionDefined();
          }
          return GFFIExpressionEmitters.genericUnary(
            "sign",
            f,
            arguments,
            version,
            TFloat.get(),
            TFloat.get());
        }
      });
  }

  static @Nonnull GFFIExpression com_io7m_parasol_float_sine(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericUnary(
      "sin",
      f,
      arguments,
      version,
      TFloat.get(),
      TFloat.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_float_square_root(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericUnary(
      "sqrt",
      f,
      arguments,
      version,
      TFloat.get(),
      TFloat.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_float_subtract(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericSubtract(
      f,
      arguments,
      version,
      TFloat.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_float_tangent(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericUnary(
      "tan",
      f,
      arguments,
      version,
      TFloat.get(),
      TFloat.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_float_truncate(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
    throws ConstraintError
  {
    return version
      .versionAccept(new GVersionVisitor<GFFIExpression, ConstraintError>() {
        @Override public GFFIExpression versionVisitES(
          final GVersionES v)
          throws ConstraintError
        {
          return new GFFIExpression.GFFIExpressionDefined();
        }

        @SuppressWarnings("synthetic-access") @Override public
          GFFIExpression
          versionVisitFull(
            final GVersionFull v)
            throws ConstraintError
        {
          if (v.compareTo(GVersionFull.GLSL_120) <= 0) {
            return new GFFIExpression.GFFIExpressionDefined();
          }
          return GFFIExpressionEmitters.genericUnary(
            "trunc",
            f,
            arguments,
            version,
            TFloat.get(),
            TFloat.get());
        }
      });
  }

  static @Nonnull GFFIExpression com_io7m_parasol_integer_add(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericPlus(
      f,
      arguments,
      version,
      TInteger.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_integer_divide(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericDivide(
      f,
      arguments,
      version,
      TInteger.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_integer_multiply(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericMultiply(
      f,
      arguments,
      version,
      TInteger.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_integer_subtract(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericSubtract(
      f,
      arguments,
      version,
      TInteger.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_matrix3x3f_multiply(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericMultiply(
      f,
      arguments,
      version,
      TMatrix3x3F.get());
  }

  @SuppressWarnings("unused") static @Nonnull
    GFFIExpression
    com_io7m_parasol_matrix3x3f_multiply_vector(
      final @Nonnull TASTDFunctionExternal f,
      final @Nonnull List<GASTExpression> arguments,
      final @Nonnull GVersion version)
  {
    final TFunction ft = f.getType();

    assert arguments.size() == 2;
    assert ft.getArguments().size() == 2;
    assert ft.getArguments().get(0).getType().equals(TMatrix3x3F.get());
    assert ft.getArguments().get(1).getType().equals(TVector3F.get());
    assert ft.getReturnType().equals(TVector3F.get());

    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTExpression.GASTEBinaryOp.GASTEBinaryOpMultiply(
        arguments.get(0),
        arguments.get(1)));
  }

  static @Nonnull GFFIExpression com_io7m_parasol_matrix4x4f_multiply(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericMultiply(
      f,
      arguments,
      version,
      TMatrix4x4F.get());
  }

  @SuppressWarnings("unused") static @Nonnull
    GFFIExpression
    com_io7m_parasol_matrix4x4f_multiply_vector(
      final @Nonnull TASTDFunctionExternal f,
      final @Nonnull List<GASTExpression> arguments,
      final @Nonnull GVersion version)
  {
    final TFunction ft = f.getType();
    final List<TFunctionArgument> fta = ft.getArguments();

    assert arguments.size() == 2;
    assert fta.size() == 2;
    assert fta.get(0).getType().equals(TMatrix4x4F.get());
    assert fta.get(1).getType().equals(TVector4F.get());
    assert ft.getReturnType().equals(TVector4F.get());

    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTExpression.GASTEBinaryOp.GASTEBinaryOpMultiply(
        arguments.get(0),
        arguments.get(1)));
  }

  static @Nonnull GFFIExpression com_io7m_parasol_sampler_cube_texture(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
    throws ConstraintError
  {
    final TFunction ft = f.getType();
    final List<TFunctionArgument> fta = ft.getArguments();

    assert arguments.size() == 2;
    assert fta.size() == 2;
    assert fta.get(0).getType().equals(TSamplerCube.get());
    assert fta.get(1).getType().equals(TVector3F.get());
    assert ft.getReturnType().equals(TVector4F.get());

    final TValueType type = ft.getReturnType();
    return version
      .versionAccept(new GVersionVisitor<GFFIExpression, ConstraintError>() {
        @Override public GFFIExpression versionVisitES(
          final GVersionES v)
          throws ConstraintError
        {
          if (v.compareTo(GVersionES.GLSL_ES_300) < 0) {
            final GTermNameGlobal name = new GTermNameGlobal("textureCube");
            return new GFFIExpression.GFFIExpressionBuiltIn(
              new GASTExpression.GASTEApplication(name, type, arguments));
          }
          final GTermNameGlobal name = new GTermNameGlobal("texture");
          return new GFFIExpression.GFFIExpressionBuiltIn(
            new GASTExpression.GASTEApplication(name, type, arguments));
        }

        @Override public GFFIExpression versionVisitFull(
          final GVersionFull v)
          throws ConstraintError
        {
          if (v.compareTo(GVersionFull.GLSL_120) <= 0) {
            final GTermNameGlobal name = new GTermNameGlobal("textureCube");
            return new GFFIExpression.GFFIExpressionBuiltIn(
              new GASTExpression.GASTEApplication(name, type, arguments));
          }
          final GTermNameGlobal name = new GTermNameGlobal("texture");
          return new GFFIExpression.GFFIExpressionBuiltIn(
            new GASTExpression.GASTEApplication(name, type, arguments));
        }
      });
  }

  static @Nonnull GFFIExpression com_io7m_parasol_sampler2d_texture(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
    throws ConstraintError
  {
    final TFunction ft = f.getType();
    final List<TFunctionArgument> fta = ft.getArguments();

    assert arguments.size() == 2;
    assert fta.size() == 2;
    assert fta.get(0).getType().equals(TSampler2D.get());
    assert fta.get(1).getType().equals(TVector2F.get());
    assert ft.getReturnType().equals(TVector4F.get());

    final TValueType type = ft.getReturnType();
    return version
      .versionAccept(new GVersionVisitor<GFFIExpression, ConstraintError>() {
        @Override public GFFIExpression versionVisitES(
          final GVersionES v)
          throws ConstraintError
        {
          if (v.compareTo(GVersionES.GLSL_ES_300) < 0) {
            final GTermNameGlobal name = new GTermNameGlobal("texture2D");
            return new GFFIExpression.GFFIExpressionBuiltIn(
              new GASTExpression.GASTEApplication(name, type, arguments));
          }
          final GTermNameGlobal name = new GTermNameGlobal("texture");
          return new GFFIExpression.GFFIExpressionBuiltIn(
            new GASTExpression.GASTEApplication(name, type, arguments));
        }

        @Override public GFFIExpression versionVisitFull(
          final GVersionFull v)
          throws ConstraintError
        {
          if (v.compareTo(GVersionFull.GLSL_120) <= 0) {
            final GTermNameGlobal name = new GTermNameGlobal("texture2D");
            return new GFFIExpression.GFFIExpressionBuiltIn(
              new GASTExpression.GASTEApplication(name, type, arguments));
          }
          final GTermNameGlobal name = new GTermNameGlobal("texture");
          return new GFFIExpression.GFFIExpressionBuiltIn(
            new GASTExpression.GASTEApplication(name, type, arguments));
        }
      });
  }

  static @Nonnull
    GFFIExpression
    com_io7m_parasol_sampler2d_texture_projective_3f(
      final @Nonnull TASTDFunctionExternal f,
      final @Nonnull List<GASTExpression> arguments,
      final @Nonnull GVersion version)
      throws ConstraintError
  {
    final TFunction ft = f.getType();
    final List<TFunctionArgument> fta = ft.getArguments();

    assert arguments.size() == 2;
    assert fta.size() == 2;
    assert fta.get(0).getType().equals(TSampler2D.get());
    assert fta.get(1).getType().equals(TVector3F.get());
    assert ft.getReturnType().equals(TVector4F.get());

    final TValueType type = ft.getReturnType();
    return version
      .versionAccept(new GVersionVisitor<GFFIExpression, ConstraintError>() {
        @Override public GFFIExpression versionVisitES(
          final GVersionES v)
          throws ConstraintError
        {
          if (v.compareTo(GVersionES.GLSL_ES_300) < 0) {
            final GTermNameGlobal name = new GTermNameGlobal("texture2DProj");
            return new GFFIExpression.GFFIExpressionBuiltIn(
              new GASTExpression.GASTEApplication(name, type, arguments));
          }
          final GTermNameGlobal name = new GTermNameGlobal("textureProj");
          return new GFFIExpression.GFFIExpressionBuiltIn(
            new GASTExpression.GASTEApplication(name, type, arguments));
        }

        @Override public GFFIExpression versionVisitFull(
          final GVersionFull v)
          throws ConstraintError
        {
          if (v.compareTo(GVersionFull.GLSL_120) <= 0) {
            final GTermNameGlobal name = new GTermNameGlobal("texture2DProj");
            return new GFFIExpression.GFFIExpressionBuiltIn(
              new GASTExpression.GASTEApplication(name, type, arguments));
          }
          final GTermNameGlobal name = new GTermNameGlobal("textureProj");
          return new GFFIExpression.GFFIExpressionBuiltIn(
            new GASTExpression.GASTEApplication(name, type, arguments));
        }
      });
  }

  static @Nonnull
    GFFIExpression
    com_io7m_parasol_sampler2d_texture_projective_4f(
      final @Nonnull TASTDFunctionExternal f,
      final @Nonnull List<GASTExpression> arguments,
      final @Nonnull GVersion version)
      throws ConstraintError
  {
    final TFunction ft = f.getType();
    final List<TFunctionArgument> fta = ft.getArguments();

    assert arguments.size() == 2;
    assert fta.size() == 2;
    assert fta.get(0).getType().equals(TSampler2D.get());
    assert fta.get(1).getType().equals(TVector4F.get());
    assert ft.getReturnType().equals(TVector4F.get());

    final TValueType type = ft.getReturnType();
    return version
      .versionAccept(new GVersionVisitor<GFFIExpression, ConstraintError>() {
        @Override public GFFIExpression versionVisitES(
          final GVersionES v)
          throws ConstraintError
        {
          if (v.compareTo(GVersionES.GLSL_ES_300) < 0) {
            final GTermNameGlobal name = new GTermNameGlobal("texture2DProj");
            return new GFFIExpression.GFFIExpressionBuiltIn(
              new GASTExpression.GASTEApplication(name, type, arguments));
          }
          final GTermNameGlobal name = new GTermNameGlobal("textureProj");
          return new GFFIExpression.GFFIExpressionBuiltIn(
            new GASTExpression.GASTEApplication(name, type, arguments));
        }

        @Override public GFFIExpression versionVisitFull(
          final GVersionFull v)
          throws ConstraintError
        {
          if (v.compareTo(GVersionFull.GLSL_120) <= 0) {
            final GTermNameGlobal name = new GTermNameGlobal("texture2DProj");
            return new GFFIExpression.GFFIExpressionBuiltIn(
              new GASTExpression.GASTEApplication(name, type, arguments));
          }
          final GTermNameGlobal name = new GTermNameGlobal("textureProj");
          return new GFFIExpression.GFFIExpressionBuiltIn(
            new GASTExpression.GASTEApplication(name, type, arguments));
        }
      });
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2f_add(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericPlus(
      f,
      arguments,
      version,
      TVector2F.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2f_add_scalar(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericPlusScalar(
      f,
      arguments,
      version,
      TVector2F.get(),
      TFloat.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2f_dot(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericDot(
      f,
      arguments,
      version,
      TVector2F.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2f_interpolate(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericInterpolate(
      f,
      arguments,
      version,
      TVector2F.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2f_magnitude(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericMagnitude(
      f,
      arguments,
      version,
      TVector2F.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2f_multiply(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericMultiply(
      f,
      arguments,
      version,
      TVector2F.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2f_multiply_scalar(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericMultiplyScalar(
      f,
      arguments,
      version,
      TVector2F.get(),
      TFloat.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2f_negate(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericNegate(
      f,
      arguments,
      version,
      TVector2F.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2f_normalize(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericNormalize(
      f,
      arguments,
      version,
      TVector2F.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2f_reflect(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericReflect(
      f,
      arguments,
      version,
      TVector2F.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2f_refract(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericRefract(
      f,
      arguments,
      version,
      TVector2F.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2f_subtract(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericSubtract(
      f,
      arguments,
      version,
      TVector2F.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2i_add(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericPlus(
      f,
      arguments,
      version,
      TVector2I.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2i_add_scalar(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericPlusScalar(
      f,
      arguments,
      version,
      TVector2I.get(),
      TInteger.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2i_dot(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericDot(
      f,
      arguments,
      version,
      TVector2I.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2i_interpolate(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericInterpolate(
      f,
      arguments,
      version,
      TVector2I.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2i_magnitude(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericMagnitude(
      f,
      arguments,
      version,
      TVector2I.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2i_multiply(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericMultiply(
      f,
      arguments,
      version,
      TVector2I.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2i_multiply_scalar(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericMultiplyScalar(
      f,
      arguments,
      version,
      TVector2I.get(),
      TInteger.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2i_negate(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericNegate(
      f,
      arguments,
      version,
      TVector2I.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2i_normalize(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericNormalize(
      f,
      arguments,
      version,
      TVector2I.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2i_reflect(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericReflect(
      f,
      arguments,
      version,
      TVector2I.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2i_refract(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericRefract(
      f,
      arguments,
      version,
      TVector2I.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2i_subtract(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericSubtract(
      f,
      arguments,
      version,
      TVector2I.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3f_add(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericPlus(
      f,
      arguments,
      version,
      TVector3F.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3f_add_scalar(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericPlusScalar(
      f,
      arguments,
      version,
      TVector3F.get(),
      TFloat.get());
  }

  @SuppressWarnings("unused") static @Nonnull
    GFFIExpression
    com_io7m_parasol_vector3f_cross(
      final @Nonnull TASTDFunctionExternal f,
      final @Nonnull List<GASTExpression> arguments,
      final @Nonnull GVersion version)
  {
    final TFunction ft = f.getType();

    assert arguments.size() == 2;
    assert ft.getArguments().size() == 2;
    assert ft.getArguments().get(0).getType().equals(TVector3F.get());
    assert ft.getArguments().get(1).getType().equals(TVector3F.get());
    assert ft.getReturnType().equals(TVector3F.get());

    final GTermNameGlobal tname = new GTermNameGlobal("cross");
    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTExpression.GASTEApplication(tname, TFloat.get(), arguments));
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3f_dot(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericDot(
      f,
      arguments,
      version,
      TVector3F.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3f_interpolate(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericInterpolate(
      f,
      arguments,
      version,
      TVector3F.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3f_magnitude(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericMagnitude(
      f,
      arguments,
      version,
      TVector3F.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3f_multiply(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericMultiply(
      f,
      arguments,
      version,
      TVector3F.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3f_multiply_scalar(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericMultiplyScalar(
      f,
      arguments,
      version,
      TVector3F.get(),
      TFloat.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3f_negate(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericNegate(
      f,
      arguments,
      version,
      TVector3F.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3f_normalize(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericNormalize(
      f,
      arguments,
      version,
      TVector3F.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3f_reflect(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericReflect(
      f,
      arguments,
      version,
      TVector3F.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3f_refract(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericRefract(
      f,
      arguments,
      version,
      TVector3F.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3f_subtract(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericSubtract(
      f,
      arguments,
      version,
      TVector3F.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3i_add(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericPlus(
      f,
      arguments,
      version,
      TVector3I.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3i_add_scalar(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericPlusScalar(
      f,
      arguments,
      version,
      TVector3I.get(),
      TInteger.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3i_dot(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericDot(
      f,
      arguments,
      version,
      TVector3I.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3i_interpolate(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericInterpolate(
      f,
      arguments,
      version,
      TVector3I.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3i_magnitude(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericMagnitude(
      f,
      arguments,
      version,
      TVector3I.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3i_multiply(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericMultiply(
      f,
      arguments,
      version,
      TVector3I.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3i_multiply_scalar(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericMultiplyScalar(
      f,
      arguments,
      version,
      TVector3I.get(),
      TInteger.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3i_negate(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericNegate(
      f,
      arguments,
      version,
      TVector3I.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3i_normalize(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericNormalize(
      f,
      arguments,
      version,
      TVector3I.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3i_reflect(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericReflect(
      f,
      arguments,
      version,
      TVector3I.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3i_refract(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericRefract(
      f,
      arguments,
      version,
      TVector3I.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3i_subtract(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericSubtract(
      f,
      arguments,
      version,
      TVector3I.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4f_add(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericPlus(
      f,
      arguments,
      version,
      TVector4F.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4f_add_scalar(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericPlusScalar(
      f,
      arguments,
      version,
      TVector4F.get(),
      TFloat.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4f_dot(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericDot(
      f,
      arguments,
      version,
      TVector4F.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4f_interpolate(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericInterpolate(
      f,
      arguments,
      version,
      TVector4F.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4f_magnitude(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericMagnitude(
      f,
      arguments,
      version,
      TVector4F.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4f_multiply(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericMultiply(
      f,
      arguments,
      version,
      TVector4F.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4f_multiply_scalar(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericMultiplyScalar(
      f,
      arguments,
      version,
      TVector4F.get(),
      TFloat.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4f_negate(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericNegate(
      f,
      arguments,
      version,
      TVector4F.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4f_normalize(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericNormalize(
      f,
      arguments,
      version,
      TVector4F.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4f_reflect(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericReflect(
      f,
      arguments,
      version,
      TVector4F.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4f_refract(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericRefract(
      f,
      arguments,
      version,
      TVector4F.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4f_subtract(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericSubtract(
      f,
      arguments,
      version,
      TVector4F.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4i_add(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericPlus(
      f,
      arguments,
      version,
      TVector4I.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4i_add_scalar(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericPlusScalar(
      f,
      arguments,
      version,
      TVector4I.get(),
      TInteger.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4i_dot(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericDot(
      f,
      arguments,
      version,
      TVector4I.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4i_interpolate(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericInterpolate(
      f,
      arguments,
      version,
      TVector4I.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4i_magnitude(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericMagnitude(
      f,
      arguments,
      version,
      TVector4I.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4i_multiply(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericMultiply(
      f,
      arguments,
      version,
      TVector4I.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4i_multiply_scalar(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericMultiplyScalar(
      f,
      arguments,
      version,
      TVector4I.get(),
      TInteger.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4i_negate(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericNegate(
      f,
      arguments,
      version,
      TVector4I.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4i_normalize(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericNormalize(
      f,
      arguments,
      version,
      TVector4I.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4i_reflect(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericReflect(
      f,
      arguments,
      version,
      TVector4I.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4i_refract(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericRefract(
      f,
      arguments,
      version,
      TVector4I.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4i_subtract(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.genericSubtract(
      f,
      arguments,
      version,
      TVector4I.get());
  }

  @SuppressWarnings("unused") private static @Nonnull
    GFFIExpressionBuiltIn
    genericClamp(
      final @Nonnull TASTDFunctionExternal f,
      final @Nonnull List<GASTExpression> arguments,
      final @Nonnull GVersion version,
      final @Nonnull TValueType type)
  {
    assert arguments.size() == 3;
    assert f.getType().getArguments().size() == 3;
    assert f.getType().getArguments().get(0).getType().equals(type);
    assert f.getType().getArguments().get(1).getType().equals(type);
    assert f.getType().getArguments().get(2).getType().equals(type);
    assert f.getType().getReturnType().equals(type);

    final GTermNameGlobal tname = new GTermNameGlobal("clamp");
    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTExpression.GASTEApplication(tname, type, arguments));
  }

  @SuppressWarnings("unused") private static @Nonnull
    GFFIExpressionBuiltIn
    genericNormalize(
      final @Nonnull TASTDFunctionExternal f,
      final @Nonnull List<GASTExpression> arguments,
      final @Nonnull GVersion version,
      final @Nonnull TValueType type)
  {
    assert arguments.size() == 1;
    assert f.getType().getArguments().size() == 1;
    assert f.getType().getArguments().get(0).getType().equals(type);
    assert f.getType().getReturnType().equals(type);

    final GTermNameGlobal tname = new GTermNameGlobal("normalize");
    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTExpression.GASTEApplication(tname, type, arguments));
  }

  @SuppressWarnings("unused") private static @Nonnull
    GFFIExpressionBuiltIn
    genericReflect(
      final @Nonnull TASTDFunctionExternal f,
      final @Nonnull List<GASTExpression> arguments,
      final @Nonnull GVersion version,
      final @Nonnull TValueType type)
  {
    assert arguments.size() == 2;
    assert f.getType().getArguments().size() == 2;
    assert f.getType().getArguments().get(0).getType().equals(type);
    assert f.getType().getArguments().get(1).getType().equals(type);
    assert f.getType().getReturnType().equals(type);

    final GTermNameGlobal tname = new GTermNameGlobal("reflect");
    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTExpression.GASTEApplication(tname, type, arguments));
  }

  @SuppressWarnings("unused") private static @Nonnull
    GFFIExpressionBuiltIn
    genericRefract(
      final @Nonnull TASTDFunctionExternal f,
      final @Nonnull List<GASTExpression> arguments,
      final @Nonnull GVersion version,
      final @Nonnull TValueType type)
  {
    assert arguments.size() == 3;
    assert f.getType().getArguments().size() == 3;
    assert f.getType().getArguments().get(0).getType().equals(type);
    assert f.getType().getArguments().get(1).getType().equals(type);
    assert f.getType().getArguments().get(2).getType().equals(TFloat.get());
    assert f.getType().getReturnType().equals(type);

    final GTermNameGlobal tname = new GTermNameGlobal("refract");
    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTExpression.GASTEApplication(tname, type, arguments));
  }

  @SuppressWarnings("unused") private static @Nonnull
    GFFIExpressionBuiltIn
    genericInterpolate(
      final @Nonnull TASTDFunctionExternal f,
      final @Nonnull List<GASTExpression> arguments,
      final @Nonnull GVersion version,
      final @Nonnull TValueType type)
  {
    final TFunction ft = f.getType();
    final List<TFunctionArgument> fta = ft.getArguments();

    assert arguments.size() == 3;
    assert fta.size() == 3;
    assert fta.get(0).getType().equals(type);
    assert fta.get(1).getType().equals(type);
    assert fta.get(2).getType().equals(TFloat.get());
    assert ft.getReturnType().equals(type);

    final GTermNameGlobal tname = new GTermNameGlobal("mix");
    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTExpression.GASTEApplication(tname, type, arguments));
  }

  @SuppressWarnings("unused") private static @Nonnull
    GFFIExpressionBuiltIn
    genericDot(
      final @Nonnull TASTDFunctionExternal f,
      final @Nonnull List<GASTExpression> arguments,
      final @Nonnull GVersion version,
      final @Nonnull TValueType type)
  {
    assert arguments.size() == 2;
    assert f.getType().getArguments().size() == 2;
    assert f.getType().getArguments().get(0).getType().equals(type);
    assert f.getType().getArguments().get(1).getType().equals(type);
    assert f.getType().getReturnType().equals(TFloat.get());

    final GTermNameGlobal tname = new GTermNameGlobal("dot");
    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTExpression.GASTEApplication(tname, TFloat.get(), arguments));
  }

  @SuppressWarnings("unused") private static @Nonnull
    GFFIExpressionBuiltIn
    genericUnary(
      final @Nonnull String name,
      final @Nonnull TASTDFunctionExternal f,
      final @Nonnull List<GASTExpression> arguments,
      final @Nonnull GVersion version,
      final @Nonnull TValueType from,
      final @Nonnull TValueType to)
  {
    assert arguments.size() == 1;
    assert f.getType().getArguments().size() == 1;
    assert f.getType().getArguments().get(0).getType().equals(from);
    assert f.getType().getReturnType().equals(to);

    final GTermNameGlobal tname = new GTermNameGlobal(name);
    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTExpression.GASTEApplication(tname, to, arguments));
  }

  @SuppressWarnings("unused") private static @Nonnull
    GFFIExpressionBuiltIn
    genericEquals(
      final @Nonnull TASTDFunctionExternal f,
      final @Nonnull List<GASTExpression> arguments,
      final @Nonnull GVersion version,
      final @Nonnull TValueType type)
  {
    assert arguments.size() == 2;
    assert f.getType().getArguments().size() == 2;
    assert f.getType().getArguments().get(0).getType().equals(type);
    assert f.getType().getArguments().get(1).getType().equals(type);
    assert f.getType().getReturnType().equals(TBoolean.get());

    return new GFFIExpression.GFFIExpressionBuiltIn(new GASTEBinaryOpEqual(
      arguments.get(0),
      arguments.get(1)));
  }

  @SuppressWarnings("unused") private static @Nonnull
    GFFIExpressionBuiltIn
    genericPlus(
      final @Nonnull TASTDFunctionExternal f,
      final @Nonnull List<GASTExpression> arguments,
      final @Nonnull GVersion version,
      final @Nonnull TValueType type)
  {
    assert arguments.size() == 2;
    assert f.getType().getArguments().size() == 2;
    assert f.getType().getArguments().get(0).getType().equals(type);
    assert f.getType().getArguments().get(1).getType().equals(type);
    assert f.getType().getReturnType().equals(type);

    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTExpression.GASTEBinaryOp.GASTEBinaryOpPlus(
        arguments.get(0),
        arguments.get(1)));
  }

  @SuppressWarnings("unused") private static @Nonnull
    GFFIExpressionBuiltIn
    genericSubtract(
      final @Nonnull TASTDFunctionExternal f,
      final @Nonnull List<GASTExpression> arguments,
      final @Nonnull GVersion version,
      final @Nonnull TValueType type)
  {
    assert arguments.size() == 2;
    assert f.getType().getArguments().size() == 2;
    assert f.getType().getArguments().get(0).getType().equals(type);
    assert f.getType().getArguments().get(1).getType().equals(type);
    assert f.getType().getReturnType().equals(type);

    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTExpression.GASTEBinaryOp.GASTEBinaryOpSubtract(
        arguments.get(0),
        arguments.get(1)));
  }

  @SuppressWarnings("unused") private static @Nonnull
    GFFIExpressionBuiltIn
    genericPlusScalar(
      final @Nonnull TASTDFunctionExternal f,
      final @Nonnull List<GASTExpression> arguments,
      final @Nonnull GVersion version,
      final @Nonnull TValueType v_type,
      final @Nonnull TValueType s_type)
  {
    assert arguments.size() == 2;
    assert f.getType().getArguments().size() == 2;
    assert f.getType().getArguments().get(0).getType().equals(v_type);
    assert f.getType().getArguments().get(1).getType().equals(s_type);
    assert f.getType().getReturnType().equals(v_type);

    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTExpression.GASTEBinaryOp.GASTEBinaryOpPlus(
        arguments.get(0),
        arguments.get(1)));
  }

  @SuppressWarnings("unused") private static @Nonnull
    GFFIExpressionBuiltIn
    genericMultiplyScalar(
      final @Nonnull TASTDFunctionExternal f,
      final @Nonnull List<GASTExpression> arguments,
      final @Nonnull GVersion version,
      final @Nonnull TValueType v_type,
      final @Nonnull TValueType s_type)
  {
    assert arguments.size() == 2;
    assert f.getType().getArguments().size() == 2;
    assert f.getType().getArguments().get(0).getType().equals(v_type);
    assert f.getType().getArguments().get(1).getType().equals(s_type);
    assert f.getType().getReturnType().equals(v_type);

    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTExpression.GASTEBinaryOp.GASTEBinaryOpMultiply(
        arguments.get(0),
        arguments.get(1)));
  }

  @SuppressWarnings("unused") private static @Nonnull
    GFFIExpressionBuiltIn
    genericMultiply(
      final @Nonnull TASTDFunctionExternal f,
      final @Nonnull List<GASTExpression> arguments,
      final @Nonnull GVersion version,
      final @Nonnull TValueType type)
  {
    assert arguments.size() == 2;
    assert f.getType().getArguments().size() == 2;
    assert f.getType().getArguments().get(0).getType().equals(type);
    assert f.getType().getArguments().get(1).getType().equals(type);
    assert f.getType().getReturnType().equals(type);

    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTExpression.GASTEBinaryOp.GASTEBinaryOpMultiply(
        arguments.get(0),
        arguments.get(1)));
  }

  @SuppressWarnings("unused") private static @Nonnull
    GFFIExpressionBuiltIn
    genericMagnitude(
      final @Nonnull TASTDFunctionExternal f,
      final @Nonnull List<GASTExpression> arguments,
      final @Nonnull GVersion version,
      final @Nonnull TValueType type)
  {
    assert arguments.size() == 1;
    assert f.getType().getArguments().size() == 1;
    assert f.getType().getArguments().get(0).getType().equals(type);
    assert f.getType().getReturnType().equals(TFloat.get());

    final GTermNameGlobal tname = new GTermNameGlobal("length");
    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTExpression.GASTEApplication(tname, type, arguments));
  }

  @SuppressWarnings("unused") private static @Nonnull
    GFFIExpressionBuiltIn
    genericDivide(
      final @Nonnull TASTDFunctionExternal f,
      final @Nonnull List<GASTExpression> arguments,
      final @Nonnull GVersion version,
      final @Nonnull TValueType type)
  {
    assert arguments.size() == 2;
    assert f.getType().getArguments().size() == 2;
    assert f.getType().getArguments().get(0).getType().equals(type);
    assert f.getType().getArguments().get(1).getType().equals(type);
    assert f.getType().getReturnType().equals(type);

    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTExpression.GASTEBinaryOp.GASTEBinaryOpDivide(
        arguments.get(0),
        arguments.get(1)));
  }

  @SuppressWarnings("unused") private static @Nonnull
    GFFIExpressionBuiltIn
    genericNegate(
      final @Nonnull TASTDFunctionExternal f,
      final @Nonnull List<GASTExpression> arguments,
      final @Nonnull GVersion version,
      final @Nonnull TValueType type)
  {
    assert arguments.size() == 1;
    assert f.getType().getArguments().size() == 1;
    assert f.getType().getArguments().get(0).getType().equals(type);
    assert f.getType().getReturnType().equals(type);

    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTExpression.GASTEUnaryOp.GASTEUnaryOpNegate(arguments.get(0)));
  }
}
