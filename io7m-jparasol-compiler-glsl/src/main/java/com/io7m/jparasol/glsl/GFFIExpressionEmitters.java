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

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jparasol.glsl.GFFIExpression.GFFIExpressionBuiltIn;
import com.io7m.jparasol.glsl.GVersion.GVersionES;
import com.io7m.jparasol.glsl.GVersion.GVersionFull;
import com.io7m.jparasol.glsl.ast.GASTExpression;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEBinaryOp.GASTEBinaryOpEqual;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEBinaryOp.GASTEBinaryOpGreaterThan;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEBinaryOp.GASTEBinaryOpGreaterThanOrEqual;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEBinaryOp.GASTEBinaryOpLesserThan;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEBinaryOp.GASTEBinaryOpLesserThanOrEqual;
import com.io7m.jparasol.glsl.ast.GTermName.GTermNameExternal;
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
import com.io7m.junreachable.UnreachableCodeException;

/**
 * Built-in FFI expression emitters.
 */

@EqualityReference public final class GFFIExpressionEmitters
{
  private GFFIExpressionEmitters()
  {
    throw new UnreachableCodeException();
  }

  // CHECKSTYLE:OFF

  static GFFIExpression com_io7m_parasol_float_absolute(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericUnary(
      "abs",
      f,
      arguments,
      version,
      TFloat.get(),
      TFloat.get());
  }

  static GFFIExpression com_io7m_parasol_float_add(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericPlus(
      f,
      arguments,
      version,
      TFloat.get());
  }

  static GFFIExpression com_io7m_parasol_float_arc_cosine(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericUnary(
      "acos",
      f,
      arguments,
      version,
      TFloat.get(),
      TFloat.get());
  }

  static GFFIExpression com_io7m_parasol_float_arc_sine(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericUnary(
      "asin",
      f,
      arguments,
      version,
      TFloat.get(),
      TFloat.get());
  }

  static GFFIExpression com_io7m_parasol_float_arc_tangent(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericUnary(
      "atan",
      f,
      arguments,
      version,
      TFloat.get(),
      TFloat.get());
  }

  static GFFIExpression com_io7m_parasol_float_ceiling(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericUnary(
      "ceil",
      f,
      arguments,
      version,
      TFloat.get(),
      TFloat.get());
  }

  static GFFIExpression com_io7m_parasol_float_clamp(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericClamp(
      f,
      arguments,
      version,
      TFloat.get());
  }

  static GFFIExpression com_io7m_parasol_float_cosine(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericUnary(
      "cosine",
      f,
      arguments,
      version,
      TFloat.get(),
      TFloat.get());
  }

  static GFFIExpression com_io7m_parasol_float_divide(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericDivide(
      f,
      arguments,
      version,
      TFloat.get());
  }

  static GFFIExpression com_io7m_parasol_float_equals(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericEquals(
      f,
      arguments,
      version,
      TFloat.get());
  }

  static GFFIExpression com_io7m_parasol_float_floor(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericUnary(
      "floor",
      f,
      arguments,
      version,
      TFloat.get(),
      TFloat.get());
  }

  static GFFIExpression com_io7m_parasol_float_greater(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    assert arguments.size() == 2;
    assert f.getType().getArguments().size() == 2;
    assert f.getType().getArguments().get(0).getType().equals(TFloat.get());
    assert f.getType().getArguments().get(1).getType().equals(TFloat.get());
    assert f.getType().getReturnType().equals(TBoolean.get());

    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTEBinaryOpGreaterThan(arguments.get(0), arguments.get(1)));
  }

  static GFFIExpression com_io7m_parasol_float_greater_or_equal(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    assert arguments.size() == 2;
    assert f.getType().getArguments().size() == 2;
    assert f.getType().getArguments().get(0).getType().equals(TFloat.get());
    assert f.getType().getArguments().get(1).getType().equals(TFloat.get());
    assert f.getType().getReturnType().equals(TBoolean.get());

    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTEBinaryOpGreaterThanOrEqual(arguments.get(0), arguments.get(1)));
  }

  static GFFIExpression com_io7m_parasol_float_interpolate(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericInterpolate(
      f,
      arguments,
      version,
      TFloat.get());
  }

  static GFFIExpression com_io7m_parasol_float_is_infinite(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return version
      .versionAccept(new GVersionVisitorType<GFFIExpression, UnreachableCodeException>() {
        @Override public GFFIExpression versionVisitES(
          final GVersionES v)
        {
          return new GFFIExpression.GFFIExpressionDefined();
        }

        @SuppressWarnings("synthetic-access") @Override public
          GFFIExpression
          versionVisitFull(
            final GVersionFull v)
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

  static GFFIExpression com_io7m_parasol_float_is_nan(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
    throws UnreachableCodeException
  {
    return version
      .versionAccept(new GVersionVisitorType<GFFIExpression, UnreachableCodeException>() {
        @Override public GFFIExpression versionVisitES(
          final GVersionES v)
          throws UnreachableCodeException
        {
          return new GFFIExpression.GFFIExpressionDefined();
        }

        @SuppressWarnings("synthetic-access") @Override public
          GFFIExpression
          versionVisitFull(
            final GVersionFull v)
            throws UnreachableCodeException
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

  static GFFIExpression com_io7m_parasol_float_lesser(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    assert arguments.size() == 2;
    assert f.getType().getArguments().size() == 2;
    assert f.getType().getArguments().get(0).getType().equals(TFloat.get());
    assert f.getType().getArguments().get(1).getType().equals(TFloat.get());
    assert f.getType().getReturnType().equals(TBoolean.get());

    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTEBinaryOpLesserThan(arguments.get(0), arguments.get(1)));
  }

  static GFFIExpression com_io7m_parasol_float_lesser_or_equal(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    assert arguments.size() == 2;
    assert f.getType().getArguments().size() == 2;
    assert f.getType().getArguments().get(0).getType().equals(TFloat.get());
    assert f.getType().getArguments().get(1).getType().equals(TFloat.get());
    assert f.getType().getReturnType().equals(TBoolean.get());

    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTEBinaryOpLesserThanOrEqual(arguments.get(0), arguments.get(1)));
  }

  static GFFIExpression com_io7m_parasol_float_maximum(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    assert arguments.size() == 2;
    assert f.getType().getArguments().size() == 2;
    assert f.getType().getArguments().get(0).getType().equals(TFloat.get());
    assert f.getType().getArguments().get(1).getType().equals(TFloat.get());
    assert f.getType().getReturnType().equals(TFloat.get());

    final GTermNameExternal tname = new GTermNameExternal("max");
    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTExpression.GASTEApplicationExternal(
        tname,
        TFloat.get(),
        arguments));
  }

  static GFFIExpression com_io7m_parasol_float_minimum(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    assert arguments.size() == 2;
    assert f.getType().getArguments().size() == 2;
    assert f.getType().getArguments().get(0).getType().equals(TFloat.get());
    assert f.getType().getArguments().get(1).getType().equals(TFloat.get());
    assert f.getType().getReturnType().equals(TFloat.get());

    final GTermNameExternal tname = new GTermNameExternal("min");
    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTExpression.GASTEApplicationExternal(
        tname,
        TFloat.get(),
        arguments));
  }

  static GFFIExpression com_io7m_parasol_float_modulo(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    assert arguments.size() == 2;
    assert f.getType().getArguments().size() == 2;
    assert f.getType().getArguments().get(0).getType().equals(TFloat.get());
    assert f.getType().getArguments().get(1).getType().equals(TFloat.get());
    assert f.getType().getReturnType().equals(TFloat.get());

    final GTermNameExternal tname = new GTermNameExternal("mod");
    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTExpression.GASTEApplicationExternal(
        tname,
        TFloat.get(),
        arguments));
  }

  static GFFIExpression com_io7m_parasol_float_multiply(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericMultiply(
      f,
      arguments,
      version,
      TFloat.get());
  }

  static GFFIExpression com_io7m_parasol_float_power(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    assert arguments.size() == 2;
    assert f.getType().getArguments().size() == 2;
    assert f.getType().getArguments().get(0).getType().equals(TFloat.get());
    assert f.getType().getArguments().get(1).getType().equals(TFloat.get());
    assert f.getType().getReturnType().equals(TFloat.get());

    final GTermNameExternal tname = new GTermNameExternal("pow");
    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTExpression.GASTEApplicationExternal(
        tname,
        TFloat.get(),
        arguments));
  }

  /**
   * "round" isn't supported universally, so it's necessary to fall back to
   * "floor" occasionally.
   */

  static GFFIExpression com_io7m_parasol_float_round(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
    throws UnreachableCodeException
  {
    return version
      .versionAccept(new GVersionVisitorType<GFFIExpression, UnreachableCodeException>() {
        @SuppressWarnings("synthetic-access") @Override public
          GFFIExpression
          versionVisitES(
            final GVersionES v)
            throws UnreachableCodeException
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
            throws UnreachableCodeException
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

  static GFFIExpression com_io7m_parasol_float_sign(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
    throws UnreachableCodeException
  {
    return version
      .versionAccept(new GVersionVisitorType<GFFIExpression, UnreachableCodeException>() {
        @Override public GFFIExpression versionVisitES(
          final GVersionES v)
          throws UnreachableCodeException
        {
          return new GFFIExpression.GFFIExpressionDefined();
        }

        @SuppressWarnings("synthetic-access") @Override public
          GFFIExpression
          versionVisitFull(
            final GVersionFull v)
            throws UnreachableCodeException
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

  static GFFIExpression com_io7m_parasol_float_sine(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericUnary(
      "sin",
      f,
      arguments,
      version,
      TFloat.get(),
      TFloat.get());
  }

  static GFFIExpression com_io7m_parasol_float_square_root(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericUnary(
      "sqrt",
      f,
      arguments,
      version,
      TFloat.get(),
      TFloat.get());
  }

  static GFFIExpression com_io7m_parasol_float_subtract(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericSubtract(
      f,
      arguments,
      version,
      TFloat.get());
  }

  static GFFIExpression com_io7m_parasol_float_tangent(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericUnary(
      "tan",
      f,
      arguments,
      version,
      TFloat.get(),
      TFloat.get());
  }

  static GFFIExpression com_io7m_parasol_float_truncate(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
    throws UnreachableCodeException
  {
    return version
      .versionAccept(new GVersionVisitorType<GFFIExpression, UnreachableCodeException>() {
        @Override public GFFIExpression versionVisitES(
          final GVersionES v)
          throws UnreachableCodeException
        {
          return new GFFIExpression.GFFIExpressionDefined();
        }

        @SuppressWarnings("synthetic-access") @Override public
          GFFIExpression
          versionVisitFull(
            final GVersionFull v)
            throws UnreachableCodeException
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

  static GFFIExpression com_io7m_parasol_integer_add(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericPlus(
      f,
      arguments,
      version,
      TInteger.get());
  }

  static GFFIExpression com_io7m_parasol_integer_divide(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericDivide(
      f,
      arguments,
      version,
      TInteger.get());
  }

  static GFFIExpression com_io7m_parasol_integer_multiply(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericMultiply(
      f,
      arguments,
      version,
      TInteger.get());
  }

  static GFFIExpression com_io7m_parasol_integer_subtract(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericSubtract(
      f,
      arguments,
      version,
      TInteger.get());
  }

  static GFFIExpression com_io7m_parasol_matrix3x3f_multiply(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericMultiply(
      f,
      arguments,
      version,
      TMatrix3x3F.get());
  }

  static GFFIExpression com_io7m_parasol_matrix3x3f_multiply_vector(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
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

  static GFFIExpression com_io7m_parasol_matrix4x4f_multiply(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericMultiply(
      f,
      arguments,
      version,
      TMatrix4x4F.get());
  }

  static GFFIExpression com_io7m_parasol_matrix4x4f_multiply_vector(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
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

  static GFFIExpression com_io7m_parasol_sampler_cube_texture(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
    throws UnreachableCodeException
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
      .versionAccept(new GVersionVisitorType<GFFIExpression, UnreachableCodeException>() {
        @Override public GFFIExpression versionVisitES(
          final GVersionES v)
          throws UnreachableCodeException
        {
          if (v.compareTo(GVersionES.GLSL_ES_300) < 0) {
            final GTermNameExternal name =
              new GTermNameExternal("textureCube");
            return new GFFIExpression.GFFIExpressionBuiltIn(
              new GASTExpression.GASTEApplicationExternal(
                name,
                type,
                arguments));
          }
          final GTermNameExternal name = new GTermNameExternal("texture");
          return new GFFIExpression.GFFIExpressionBuiltIn(
            new GASTExpression.GASTEApplicationExternal(name, type, arguments));
        }

        @Override public GFFIExpression versionVisitFull(
          final GVersionFull v)
          throws UnreachableCodeException
        {
          if (v.compareTo(GVersionFull.GLSL_120) <= 0) {
            final GTermNameExternal name =
              new GTermNameExternal("textureCube");
            return new GFFIExpression.GFFIExpressionBuiltIn(
              new GASTExpression.GASTEApplicationExternal(
                name,
                type,
                arguments));
          }
          final GTermNameExternal name = new GTermNameExternal("texture");
          return new GFFIExpression.GFFIExpressionBuiltIn(
            new GASTExpression.GASTEApplicationExternal(name, type, arguments));
        }
      });
  }

  static GFFIExpression com_io7m_parasol_sampler2d_texture(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
    throws UnreachableCodeException
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
      .versionAccept(new GVersionVisitorType<GFFIExpression, UnreachableCodeException>() {
        @Override public GFFIExpression versionVisitES(
          final GVersionES v)
          throws UnreachableCodeException
        {
          if (v.compareTo(GVersionES.GLSL_ES_300) < 0) {
            final GTermNameExternal name = new GTermNameExternal("texture2D");
            return new GFFIExpression.GFFIExpressionBuiltIn(
              new GASTExpression.GASTEApplicationExternal(
                name,
                type,
                arguments));
          }
          final GTermNameExternal name = new GTermNameExternal("texture");
          return new GFFIExpression.GFFIExpressionBuiltIn(
            new GASTExpression.GASTEApplicationExternal(name, type, arguments));
        }

        @Override public GFFIExpression versionVisitFull(
          final GVersionFull v)
          throws UnreachableCodeException
        {
          if (v.compareTo(GVersionFull.GLSL_120) <= 0) {
            final GTermNameExternal name = new GTermNameExternal("texture2D");
            return new GFFIExpression.GFFIExpressionBuiltIn(
              new GASTExpression.GASTEApplicationExternal(
                name,
                type,
                arguments));
          }
          final GTermNameExternal name = new GTermNameExternal("texture");
          return new GFFIExpression.GFFIExpressionBuiltIn(
            new GASTExpression.GASTEApplicationExternal(name, type, arguments));
        }
      });
  }

  static GFFIExpression com_io7m_parasol_sampler2d_texture_projective_3f(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
    throws UnreachableCodeException
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
      .versionAccept(new GVersionVisitorType<GFFIExpression, UnreachableCodeException>() {
        @Override public GFFIExpression versionVisitES(
          final GVersionES v)
          throws UnreachableCodeException
        {
          if (v.compareTo(GVersionES.GLSL_ES_300) < 0) {
            final GTermNameExternal name =
              new GTermNameExternal("texture2DProj");
            return new GFFIExpression.GFFIExpressionBuiltIn(
              new GASTExpression.GASTEApplicationExternal(
                name,
                type,
                arguments));
          }
          final GTermNameExternal name = new GTermNameExternal("textureProj");
          return new GFFIExpression.GFFIExpressionBuiltIn(
            new GASTExpression.GASTEApplicationExternal(name, type, arguments));
        }

        @Override public GFFIExpression versionVisitFull(
          final GVersionFull v)
          throws UnreachableCodeException
        {
          if (v.compareTo(GVersionFull.GLSL_120) <= 0) {
            final GTermNameExternal name =
              new GTermNameExternal("texture2DProj");
            return new GFFIExpression.GFFIExpressionBuiltIn(
              new GASTExpression.GASTEApplicationExternal(
                name,
                type,
                arguments));
          }
          final GTermNameExternal name = new GTermNameExternal("textureProj");
          return new GFFIExpression.GFFIExpressionBuiltIn(
            new GASTExpression.GASTEApplicationExternal(name, type, arguments));
        }
      });
  }

  static GFFIExpression com_io7m_parasol_sampler2d_texture_projective_4f(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
    throws UnreachableCodeException
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
      .versionAccept(new GVersionVisitorType<GFFIExpression, UnreachableCodeException>() {
        @Override public GFFIExpression versionVisitES(
          final GVersionES v)
          throws UnreachableCodeException
        {
          if (v.compareTo(GVersionES.GLSL_ES_300) < 0) {
            final GTermNameExternal name =
              new GTermNameExternal("texture2DProj");
            return new GFFIExpression.GFFIExpressionBuiltIn(
              new GASTExpression.GASTEApplicationExternal(
                name,
                type,
                arguments));
          }
          final GTermNameExternal name = new GTermNameExternal("textureProj");
          return new GFFIExpression.GFFIExpressionBuiltIn(
            new GASTExpression.GASTEApplicationExternal(name, type, arguments));
        }

        @Override public GFFIExpression versionVisitFull(
          final GVersionFull v)
          throws UnreachableCodeException
        {
          if (v.compareTo(GVersionFull.GLSL_120) <= 0) {
            final GTermNameExternal name =
              new GTermNameExternal("texture2DProj");
            return new GFFIExpression.GFFIExpressionBuiltIn(
              new GASTExpression.GASTEApplicationExternal(
                name,
                type,
                arguments));
          }
          final GTermNameExternal name = new GTermNameExternal("textureProj");
          return new GFFIExpression.GFFIExpressionBuiltIn(
            new GASTExpression.GASTEApplicationExternal(name, type, arguments));
        }
      });
  }

  static GFFIExpression com_io7m_parasol_vector2f_add(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericPlus(
      f,
      arguments,
      version,
      TVector2F.get());
  }

  static GFFIExpression com_io7m_parasol_vector2f_add_scalar(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericPlusScalar(
      f,
      arguments,
      version,
      TVector2F.get(),
      TFloat.get());
  }

  static GFFIExpression com_io7m_parasol_vector2f_dot(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericDot(
      f,
      arguments,
      version,
      TVector2F.get());
  }

  static GFFIExpression com_io7m_parasol_vector2f_interpolate(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericInterpolate(
      f,
      arguments,
      version,
      TVector2F.get());
  }

  static GFFIExpression com_io7m_parasol_vector2f_magnitude(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericMagnitude(
      f,
      arguments,
      version,
      TVector2F.get());
  }

  static GFFIExpression com_io7m_parasol_vector2f_multiply(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericMultiply(
      f,
      arguments,
      version,
      TVector2F.get());
  }

  static GFFIExpression com_io7m_parasol_vector2f_multiply_scalar(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericMultiplyScalar(
      f,
      arguments,
      version,
      TVector2F.get(),
      TFloat.get());
  }

  static GFFIExpression com_io7m_parasol_vector2f_negate(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericNegate(
      f,
      arguments,
      version,
      TVector2F.get());
  }

  static GFFIExpression com_io7m_parasol_vector2f_normalize(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericNormalize(
      f,
      arguments,
      version,
      TVector2F.get());
  }

  static GFFIExpression com_io7m_parasol_vector2f_reflect(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericReflect(
      f,
      arguments,
      version,
      TVector2F.get());
  }

  static GFFIExpression com_io7m_parasol_vector2f_refract(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericRefract(
      f,
      arguments,
      version,
      TVector2F.get());
  }

  static GFFIExpression com_io7m_parasol_vector2f_subtract(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericSubtract(
      f,
      arguments,
      version,
      TVector2F.get());
  }

  static GFFIExpression com_io7m_parasol_vector2i_add(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericPlus(
      f,
      arguments,
      version,
      TVector2I.get());
  }

  static GFFIExpression com_io7m_parasol_vector2i_add_scalar(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericPlusScalar(
      f,
      arguments,
      version,
      TVector2I.get(),
      TInteger.get());
  }

  static GFFIExpression com_io7m_parasol_vector2i_dot(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericDot(
      f,
      arguments,
      version,
      TVector2I.get());
  }

  static GFFIExpression com_io7m_parasol_vector2i_interpolate(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericInterpolate(
      f,
      arguments,
      version,
      TVector2I.get());
  }

  static GFFIExpression com_io7m_parasol_vector2i_magnitude(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericMagnitude(
      f,
      arguments,
      version,
      TVector2I.get());
  }

  static GFFIExpression com_io7m_parasol_vector2i_multiply(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericMultiply(
      f,
      arguments,
      version,
      TVector2I.get());
  }

  static GFFIExpression com_io7m_parasol_vector2i_multiply_scalar(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericMultiplyScalar(
      f,
      arguments,
      version,
      TVector2I.get(),
      TInteger.get());
  }

  static GFFIExpression com_io7m_parasol_vector2i_negate(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericNegate(
      f,
      arguments,
      version,
      TVector2I.get());
  }

  static GFFIExpression com_io7m_parasol_vector2i_normalize(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericNormalize(
      f,
      arguments,
      version,
      TVector2I.get());
  }

  static GFFIExpression com_io7m_parasol_vector2i_reflect(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericReflect(
      f,
      arguments,
      version,
      TVector2I.get());
  }

  static GFFIExpression com_io7m_parasol_vector2i_refract(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericRefract(
      f,
      arguments,
      version,
      TVector2I.get());
  }

  static GFFIExpression com_io7m_parasol_vector2i_subtract(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericSubtract(
      f,
      arguments,
      version,
      TVector2I.get());
  }

  static GFFIExpression com_io7m_parasol_vector3f_add(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericPlus(
      f,
      arguments,
      version,
      TVector3F.get());
  }

  static GFFIExpression com_io7m_parasol_vector3f_add_scalar(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericPlusScalar(
      f,
      arguments,
      version,
      TVector3F.get(),
      TFloat.get());
  }

  static GFFIExpression com_io7m_parasol_vector3f_cross(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    final TFunction ft = f.getType();

    assert arguments.size() == 2;
    assert ft.getArguments().size() == 2;
    assert ft.getArguments().get(0).getType().equals(TVector3F.get());
    assert ft.getArguments().get(1).getType().equals(TVector3F.get());
    assert ft.getReturnType().equals(TVector3F.get());

    final GTermNameExternal tname = new GTermNameExternal("cross");
    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTExpression.GASTEApplicationExternal(
        tname,
        TFloat.get(),
        arguments));
  }

  static GFFIExpression com_io7m_parasol_vector3f_dot(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericDot(
      f,
      arguments,
      version,
      TVector3F.get());
  }

  static GFFIExpression com_io7m_parasol_vector3f_interpolate(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericInterpolate(
      f,
      arguments,
      version,
      TVector3F.get());
  }

  static GFFIExpression com_io7m_parasol_vector3f_magnitude(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericMagnitude(
      f,
      arguments,
      version,
      TVector3F.get());
  }

  static GFFIExpression com_io7m_parasol_vector3f_multiply(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericMultiply(
      f,
      arguments,
      version,
      TVector3F.get());
  }

  static GFFIExpression com_io7m_parasol_vector3f_multiply_scalar(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericMultiplyScalar(
      f,
      arguments,
      version,
      TVector3F.get(),
      TFloat.get());
  }

  static GFFIExpression com_io7m_parasol_vector3f_negate(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericNegate(
      f,
      arguments,
      version,
      TVector3F.get());
  }

  static GFFIExpression com_io7m_parasol_vector3f_normalize(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericNormalize(
      f,
      arguments,
      version,
      TVector3F.get());
  }

  static GFFIExpression com_io7m_parasol_vector3f_reflect(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericReflect(
      f,
      arguments,
      version,
      TVector3F.get());
  }

  static GFFIExpression com_io7m_parasol_vector3f_refract(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericRefract(
      f,
      arguments,
      version,
      TVector3F.get());
  }

  static GFFIExpression com_io7m_parasol_vector3f_subtract(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericSubtract(
      f,
      arguments,
      version,
      TVector3F.get());
  }

  static GFFIExpression com_io7m_parasol_vector3i_add(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericPlus(
      f,
      arguments,
      version,
      TVector3I.get());
  }

  static GFFIExpression com_io7m_parasol_vector3i_add_scalar(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericPlusScalar(
      f,
      arguments,
      version,
      TVector3I.get(),
      TInteger.get());
  }

  static GFFIExpression com_io7m_parasol_vector3i_dot(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericDot(
      f,
      arguments,
      version,
      TVector3I.get());
  }

  static GFFIExpression com_io7m_parasol_vector3i_interpolate(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericInterpolate(
      f,
      arguments,
      version,
      TVector3I.get());
  }

  static GFFIExpression com_io7m_parasol_vector3i_magnitude(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericMagnitude(
      f,
      arguments,
      version,
      TVector3I.get());
  }

  static GFFIExpression com_io7m_parasol_vector3i_multiply(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericMultiply(
      f,
      arguments,
      version,
      TVector3I.get());
  }

  static GFFIExpression com_io7m_parasol_vector3i_multiply_scalar(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericMultiplyScalar(
      f,
      arguments,
      version,
      TVector3I.get(),
      TInteger.get());
  }

  static GFFIExpression com_io7m_parasol_vector3i_negate(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericNegate(
      f,
      arguments,
      version,
      TVector3I.get());
  }

  static GFFIExpression com_io7m_parasol_vector3i_normalize(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericNormalize(
      f,
      arguments,
      version,
      TVector3I.get());
  }

  static GFFIExpression com_io7m_parasol_vector3i_reflect(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericReflect(
      f,
      arguments,
      version,
      TVector3I.get());
  }

  static GFFIExpression com_io7m_parasol_vector3i_refract(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericRefract(
      f,
      arguments,
      version,
      TVector3I.get());
  }

  static GFFIExpression com_io7m_parasol_vector3i_subtract(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericSubtract(
      f,
      arguments,
      version,
      TVector3I.get());
  }

  static GFFIExpression com_io7m_parasol_vector4f_add(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericPlus(
      f,
      arguments,
      version,
      TVector4F.get());
  }

  static GFFIExpression com_io7m_parasol_vector4f_add_scalar(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericPlusScalar(
      f,
      arguments,
      version,
      TVector4F.get(),
      TFloat.get());
  }

  static GFFIExpression com_io7m_parasol_vector4f_dot(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericDot(
      f,
      arguments,
      version,
      TVector4F.get());
  }

  static GFFIExpression com_io7m_parasol_vector4f_interpolate(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericInterpolate(
      f,
      arguments,
      version,
      TVector4F.get());
  }

  static GFFIExpression com_io7m_parasol_vector4f_magnitude(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericMagnitude(
      f,
      arguments,
      version,
      TVector4F.get());
  }

  static GFFIExpression com_io7m_parasol_vector4f_multiply(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericMultiply(
      f,
      arguments,
      version,
      TVector4F.get());
  }

  static GFFIExpression com_io7m_parasol_vector4f_multiply_scalar(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericMultiplyScalar(
      f,
      arguments,
      version,
      TVector4F.get(),
      TFloat.get());
  }

  static GFFIExpression com_io7m_parasol_vector4f_negate(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericNegate(
      f,
      arguments,
      version,
      TVector4F.get());
  }

  static GFFIExpression com_io7m_parasol_vector4f_normalize(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericNormalize(
      f,
      arguments,
      version,
      TVector4F.get());
  }

  static GFFIExpression com_io7m_parasol_vector4f_reflect(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericReflect(
      f,
      arguments,
      version,
      TVector4F.get());
  }

  static GFFIExpression com_io7m_parasol_vector4f_refract(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericRefract(
      f,
      arguments,
      version,
      TVector4F.get());
  }

  static GFFIExpression com_io7m_parasol_vector4f_subtract(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericSubtract(
      f,
      arguments,
      version,
      TVector4F.get());
  }

  static GFFIExpression com_io7m_parasol_vector4i_add(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericPlus(
      f,
      arguments,
      version,
      TVector4I.get());
  }

  static GFFIExpression com_io7m_parasol_vector4i_add_scalar(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericPlusScalar(
      f,
      arguments,
      version,
      TVector4I.get(),
      TInteger.get());
  }

  static GFFIExpression com_io7m_parasol_vector4i_dot(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericDot(
      f,
      arguments,
      version,
      TVector4I.get());
  }

  static GFFIExpression com_io7m_parasol_vector4i_interpolate(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericInterpolate(
      f,
      arguments,
      version,
      TVector4I.get());
  }

  static GFFIExpression com_io7m_parasol_vector4i_magnitude(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericMagnitude(
      f,
      arguments,
      version,
      TVector4I.get());
  }

  static GFFIExpression com_io7m_parasol_vector4i_multiply(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericMultiply(
      f,
      arguments,
      version,
      TVector4I.get());
  }

  static GFFIExpression com_io7m_parasol_vector4i_multiply_scalar(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericMultiplyScalar(
      f,
      arguments,
      version,
      TVector4I.get(),
      TInteger.get());
  }

  static GFFIExpression com_io7m_parasol_vector4i_negate(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericNegate(
      f,
      arguments,
      version,
      TVector4I.get());
  }

  static GFFIExpression com_io7m_parasol_vector4i_normalize(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericNormalize(
      f,
      arguments,
      version,
      TVector4I.get());
  }

  static GFFIExpression com_io7m_parasol_vector4i_reflect(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericReflect(
      f,
      arguments,
      version,
      TVector4I.get());
  }

  static GFFIExpression com_io7m_parasol_vector4i_refract(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericRefract(
      f,
      arguments,
      version,
      TVector4I.get());
  }

  static GFFIExpression com_io7m_parasol_vector4i_subtract(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version)
  {
    return GFFIExpressionEmitters.genericSubtract(
      f,
      arguments,
      version,
      TVector4I.get());
  }

  private static GFFIExpressionBuiltIn genericClamp(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version,
    final TValueType type)
  {
    assert arguments.size() == 3;
    assert f.getType().getArguments().size() == 3;
    assert f.getType().getArguments().get(0).getType().equals(type);
    assert f.getType().getArguments().get(1).getType().equals(type);
    assert f.getType().getArguments().get(2).getType().equals(type);
    assert f.getType().getReturnType().equals(type);

    final GTermNameExternal tname = new GTermNameExternal("clamp");
    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTExpression.GASTEApplicationExternal(tname, type, arguments));
  }

  private static GFFIExpressionBuiltIn genericNormalize(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version,
    final TValueType type)
  {
    assert arguments.size() == 1;
    assert f.getType().getArguments().size() == 1;
    assert f.getType().getArguments().get(0).getType().equals(type);
    assert f.getType().getReturnType().equals(type);

    final GTermNameExternal tname = new GTermNameExternal("normalize");
    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTExpression.GASTEApplicationExternal(tname, type, arguments));
  }

  private static GFFIExpressionBuiltIn genericReflect(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version,
    final TValueType type)
  {
    assert arguments.size() == 2;
    assert f.getType().getArguments().size() == 2;
    assert f.getType().getArguments().get(0).getType().equals(type);
    assert f.getType().getArguments().get(1).getType().equals(type);
    assert f.getType().getReturnType().equals(type);

    final GTermNameExternal tname = new GTermNameExternal("reflect");
    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTExpression.GASTEApplicationExternal(tname, type, arguments));
  }

  private static GFFIExpressionBuiltIn genericRefract(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version,
    final TValueType type)
  {
    assert arguments.size() == 3;
    assert f.getType().getArguments().size() == 3;
    assert f.getType().getArguments().get(0).getType().equals(type);
    assert f.getType().getArguments().get(1).getType().equals(type);
    assert f.getType().getArguments().get(2).getType().equals(TFloat.get());
    assert f.getType().getReturnType().equals(type);

    final GTermNameExternal tname = new GTermNameExternal("refract");
    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTExpression.GASTEApplicationExternal(tname, type, arguments));
  }

  private static GFFIExpressionBuiltIn genericInterpolate(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version,
    final TValueType type)
  {
    final TFunction ft = f.getType();
    final List<TFunctionArgument> fta = ft.getArguments();

    assert arguments.size() == 3;
    assert fta.size() == 3;
    assert fta.get(0).getType().equals(type);
    assert fta.get(1).getType().equals(type);
    assert fta.get(2).getType().equals(TFloat.get());
    assert ft.getReturnType().equals(type);

    final GTermNameExternal tname = new GTermNameExternal("mix");
    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTExpression.GASTEApplicationExternal(tname, type, arguments));
  }

  private static GFFIExpressionBuiltIn genericDot(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version,
    final TValueType type)
  {
    assert arguments.size() == 2;
    assert f.getType().getArguments().size() == 2;
    assert f.getType().getArguments().get(0).getType().equals(type);
    assert f.getType().getArguments().get(1).getType().equals(type);
    assert f.getType().getReturnType().equals(TFloat.get());

    final GTermNameExternal tname = new GTermNameExternal("dot");
    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTExpression.GASTEApplicationExternal(
        tname,
        TFloat.get(),
        arguments));
  }

  private static GFFIExpressionBuiltIn genericUnary(
    final String name,
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version,
    final TValueType from,
    final TValueType to)
  {
    assert arguments.size() == 1;
    assert f.getType().getArguments().size() == 1;
    assert f.getType().getArguments().get(0).getType().equals(from);
    assert f.getType().getReturnType().equals(to);

    final GTermNameExternal tname = new GTermNameExternal(name);
    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTExpression.GASTEApplicationExternal(tname, to, arguments));
  }

  private static GFFIExpressionBuiltIn genericEquals(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version,
    final TValueType type)
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

  private static GFFIExpressionBuiltIn genericPlus(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version,
    final TValueType type)
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

  private static GFFIExpressionBuiltIn genericSubtract(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version,
    final TValueType type)
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

  private static GFFIExpressionBuiltIn genericPlusScalar(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version,
    final TValueType v_type,
    final TValueType s_type)
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

  private static GFFIExpressionBuiltIn genericMultiplyScalar(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version,
    final TValueType v_type,
    final TValueType s_type)
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

  private static GFFIExpressionBuiltIn genericMultiply(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version,
    final TValueType type)
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

  private static GFFIExpressionBuiltIn genericMagnitude(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version,
    final TValueType type)
  {
    assert arguments.size() == 1;
    assert f.getType().getArguments().size() == 1;
    assert f.getType().getArguments().get(0).getType().equals(type);
    assert f.getType().getReturnType().equals(TFloat.get());

    final GTermNameExternal tname = new GTermNameExternal("length");
    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTExpression.GASTEApplicationExternal(tname, type, arguments));
  }

  private static GFFIExpressionBuiltIn genericDivide(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version,
    final TValueType type)
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

  private static GFFIExpressionBuiltIn genericNegate(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersion version,
    final TValueType type)
  {
    assert arguments.size() == 1;
    assert f.getType().getArguments().size() == 1;
    assert f.getType().getArguments().get(0).getType().equals(type);
    assert f.getType().getReturnType().equals(type);

    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTExpression.GASTEUnaryOp.GASTEUnaryOpNegate(arguments.get(0)));
  }
}
