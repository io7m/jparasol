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
import com.io7m.jparasol.core.GVersionType;
import com.io7m.jparasol.glsl.ast.GASTExpression;
import com.io7m.jparasol.glsl.ast.GTermName.GTermNameExternal;
import com.io7m.jparasol.typed.TType.TFloat;
import com.io7m.jparasol.typed.TType.TFunction;
import com.io7m.jparasol.typed.TType.TVector3F;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDFunctionExternal;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * Built-in FFI expression emitters.
 */

@EqualityReference public final class GFFIExpressionEmittersVector3F
{
  // CHECKSTYLE:OFF

  static GFFIExpression com_io7m_parasol_vector3f_add(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersionType version)
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
    final GVersionType version)
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
    final GVersionType version)
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

  static GFFIExpression com_io7m_parasol_vector3f_divide(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersionType version)
  {
    return GFFIExpressionEmitters.genericDivide(
      f,
      arguments,
      version,
      TVector3F.get());
  }

  static GFFIExpression com_io7m_parasol_vector3f_divide_scalar(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersionType version)
  {
    return GFFIExpressionEmitters.genericDivideScalar(
      f,
      arguments,
      version,
      TVector3F.get(),
      TFloat.get());
  }

  static GFFIExpression com_io7m_parasol_vector3f_dot(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersionType version)
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
    final GVersionType version)
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
    final GVersionType version)
  {
    return GFFIExpressionEmitters.genericMagnitude(
      f,
      arguments,
      version,
      TVector3F.get());
  }

  static GFFIExpression com_io7m_parasol_vector3f_maximum(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersionType version)
  {
    return GFFIExpressionEmitters.genericMaximum(
      f,
      arguments,
      version,
      TVector3F.get());
  }

  static GFFIExpression com_io7m_parasol_vector3f_minimum(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersionType version)
  {
    return GFFIExpressionEmitters.genericMinimum(
      f,
      arguments,
      version,
      TVector3F.get());
  }

  static GFFIExpression com_io7m_parasol_vector3f_multiply(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersionType version)
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
    final GVersionType version)
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
    final GVersionType version)
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
    final GVersionType version)
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
    final GVersionType version)
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
    final GVersionType version)
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
    final GVersionType version)
  {
    return GFFIExpressionEmitters.genericSubtract(
      f,
      arguments,
      version,
      TVector3F.get());
  }

  static GFFIExpression com_io7m_parasol_vector3f_subtract_scalar(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersionType version)
  {
    return GFFIExpressionEmitters.genericSubtractScalar(
      f,
      arguments,
      version,
      TVector3F.get(),
      TFloat.get());
  }

  private GFFIExpressionEmittersVector3F()
  {
    throw new UnreachableCodeException();
  }

}
