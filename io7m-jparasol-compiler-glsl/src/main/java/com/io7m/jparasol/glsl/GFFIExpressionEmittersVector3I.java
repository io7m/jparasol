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
import com.io7m.jparasol.typed.TType.TInteger;
import com.io7m.jparasol.typed.TType.TVector3I;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDFunctionExternal;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * Built-in FFI expression emitters.
 */

@EqualityReference public final class GFFIExpressionEmittersVector3I
{
  // CHECKSTYLE:OFF

  static GFFIExpression com_io7m_parasol_vector3i_add(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersionType version)
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
    final GVersionType version)
  {
    return GFFIExpressionEmitters.genericPlusScalar(
      f,
      arguments,
      version,
      TVector3I.get(),
      TInteger.get());
  }

  static GFFIExpression com_io7m_parasol_vector3i_divide(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersionType version)
  {
    return GFFIExpressionEmitters.genericDivide(
      f,
      arguments,
      version,
      TVector3I.get());
  }

  static GFFIExpression com_io7m_parasol_vector3i_divide_scalar(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersionType version)
  {
    return GFFIExpressionEmitters.genericDivideScalar(
      f,
      arguments,
      version,
      TVector3I.get(),
      TInteger.get());
  }

  static GFFIExpression com_io7m_parasol_vector3i_dot(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersionType version)
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
    final GVersionType version)
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
    final GVersionType version)
  {
    return GFFIExpressionEmitters.genericMagnitude(
      f,
      arguments,
      version,
      TVector3I.get());
  }

  static GFFIExpression com_io7m_parasol_vector3i_maximum(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersionType version)
  {
    return GFFIExpressionEmitters.genericMaximum(
      f,
      arguments,
      version,
      TVector3I.get());
  }

  static GFFIExpression com_io7m_parasol_vector3i_minimum(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersionType version)
  {
    return GFFIExpressionEmitters.genericMinimum(
      f,
      arguments,
      version,
      TVector3I.get());
  }

  static GFFIExpression com_io7m_parasol_vector3i_multiply(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersionType version)
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
    final GVersionType version)
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
    final GVersionType version)
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
    final GVersionType version)
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
    final GVersionType version)
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
    final GVersionType version)
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
    final GVersionType version)
  {
    return GFFIExpressionEmitters.genericSubtract(
      f,
      arguments,
      version,
      TVector3I.get());
  }

  static GFFIExpression com_io7m_parasol_vector3i_subtract_scalar(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersionType version)
  {
    return GFFIExpressionEmitters.genericSubtractScalar(
      f,
      arguments,
      version,
      TVector3I.get(),
      TInteger.get());
  }

  private GFFIExpressionEmittersVector3I()
  {
    throw new UnreachableCodeException();
  }

}
