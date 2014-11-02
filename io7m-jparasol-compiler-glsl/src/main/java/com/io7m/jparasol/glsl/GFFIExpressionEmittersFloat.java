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
import com.io7m.jparasol.core.GVersionES;
import com.io7m.jparasol.core.GVersionFull;
import com.io7m.jparasol.core.GVersionType;
import com.io7m.jparasol.core.GVersionVisitorType;
import com.io7m.jparasol.glsl.ast.GASTExpression;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEBinaryOp.GASTEBinaryOpGreaterThan;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEBinaryOp.GASTEBinaryOpGreaterThanOrEqual;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEBinaryOp.GASTEBinaryOpLesserThan;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEBinaryOp.GASTEBinaryOpLesserThanOrEqual;
import com.io7m.jparasol.glsl.ast.GTermName.GTermNameExternal;
import com.io7m.jparasol.typed.TType.TBoolean;
import com.io7m.jparasol.typed.TType.TFloat;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDFunctionExternal;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * Built-in FFI expression emitters.
 */

@EqualityReference public final class GFFIExpressionEmittersFloat
{
  // CHECKSTYLE:OFF

  static GFFIExpression com_io7m_parasol_float_absolute(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersionType version)
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
    final GVersionType version)
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
    final GVersionType version)
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
    final GVersionType version)
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
    final GVersionType version)
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
    final GVersionType version)
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
    final GVersionType version)
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
    final GVersionType version)
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
    final GVersionType version)
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
    final GVersionType version)
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
    final GVersionType version)
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
    final GVersionType version)
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
    final GVersionType version)
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
    final GVersionType version)
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
    final GVersionType version)
  {
    return version
      .versionAccept(new GVersionVisitorType<GFFIExpression, UnreachableCodeException>() {
        @Override public GFFIExpression versionVisitES(
          final GVersionES v)
        {
          return new GFFIExpression.GFFIExpressionDefined();
        }

        @Override public GFFIExpression versionVisitFull(
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
    final GVersionType version)
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

        @Override public GFFIExpression versionVisitFull(
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
    final GVersionType version)
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
    final GVersionType version)
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
    final GVersionType version)
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
    final GVersionType version)
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
    final GVersionType version)
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
    final GVersionType version)
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
    final GVersionType version)
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
    final GVersionType version)
    throws UnreachableCodeException
  {
    return version
      .versionAccept(new GVersionVisitorType<GFFIExpression, UnreachableCodeException>() {
        @Override public GFFIExpression versionVisitES(
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

        @Override public GFFIExpression versionVisitFull(
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
    final GVersionType version)
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

        @Override public GFFIExpression versionVisitFull(
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
    final GVersionType version)
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
    final GVersionType version)
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
    final GVersionType version)
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
    final GVersionType version)
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
    final GVersionType version)
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

        @Override public GFFIExpression versionVisitFull(
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

  private GFFIExpressionEmittersFloat()
  {
    throw new UnreachableCodeException();
  }

}
