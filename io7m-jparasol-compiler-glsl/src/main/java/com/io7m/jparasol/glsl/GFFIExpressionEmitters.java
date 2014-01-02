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

import java.util.List;

import javax.annotation.Nonnull;

import com.io7m.jaux.UnimplementedCodeException;
import com.io7m.jparasol.glsl.GFFIExpression.GFFIBuiltIn;
import com.io7m.jparasol.glsl.ast.GASTExpression;
import com.io7m.jparasol.glsl.ast.GTermName.GTermNameGlobal;
import com.io7m.jparasol.typed.TType.TFloat;
import com.io7m.jparasol.typed.TType.TValueType;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDFunctionExternal;

public final class GFFIExpressionEmitters
{
  private static @Nonnull GFFIBuiltIn unary(
    final @Nonnull String name,
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version,
    final @Nonnull TValueType from,
    final @Nonnull TValueType to)
  {
    assert arguments.size() == 1;
    assert f.getType().getArguments().size() == 1;
    assert f.getType().getArguments().get(0).equals(from);
    assert f.getType().getReturnType().equals(to);

    final GTermNameGlobal tname = new GTermNameGlobal(name);
    return new GFFIExpression.GFFIBuiltIn(
      new GASTExpression.GASTEApplication(tname, to, arguments));
  }

  static @Nonnull GFFIExpression com_io7m_parasol_float_absolute(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.unary(
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
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_float_arc_cosine(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.unary(
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
    return GFFIExpressionEmitters.unary(
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
    return GFFIExpressionEmitters.unary(
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
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_float_clamp(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_float_cosine(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.unary(
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
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_float_equals(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_float_floor(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.unary(
      "floor",
      f,
      arguments,
      version,
      TFloat.get(),
      TFloat.get());
  }

  static @Nonnull GFFIExpression com_io7m_parasol_float_greater(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_float_greater_or_equal(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_float_interpolate(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_float_is_infinite(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_float_is_nan(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_float_lesser(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_float_lesser_or_equal(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_float_maximum(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_float_minimum(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_float_modulo(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_float_multiply(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_float_power(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_float_round(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_float_sign(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_float_sine(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.unary(
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
    return GFFIExpressionEmitters.unary(
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
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_float_tangent(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    return GFFIExpressionEmitters.unary(
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
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_integer_add(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_integer_divide(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_integer_multiply(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_integer_subtract(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_matrix3x3f_multiply(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_matrix3x3f_multiply_vector(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_matrix4x4f_multiply(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_matrix4x4f_multiply_vector(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_sampler2d_texture(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull
    GFFIExpression
    com_io7m_parasol_sampler2d_texture_projective_3f(
      final @Nonnull TASTDFunctionExternal f,
      final @Nonnull List<GASTExpression> arguments,
      final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull
    GFFIExpression
    com_io7m_parasol_sampler2d_texture_projective_4f(
      final @Nonnull TASTDFunctionExternal f,
      final @Nonnull List<GASTExpression> arguments,
      final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_sampler_cube_texture(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2f_add(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2f_add_scalar(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2f_dot(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2f_interpolate(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2f_magnitude(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2f_multiply(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2f_multiply_scalar(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2f_negate(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2f_normalize(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2f_reflect(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2f_refract(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2f_subtract(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2i_add(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2i_add_scalar(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2i_dot(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2i_interpolate(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2i_magnitude(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2i_multiply(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2i_multiply_scalar(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2i_negate(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2i_normalize(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2i_reflect(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2i_refract(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector2i_subtract(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3f_add(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3f_add_scalar(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3f_cross(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3f_dot(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3f_interpolate(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3f_magnitude(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3f_multiply(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3f_multiply_scalar(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3f_negate(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3f_normalize(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3f_reflect(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3f_refract(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3f_subtract(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3i_add(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3i_add_scalar(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3i_dot(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3i_interpolate(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3i_magnitude(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3i_multiply(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3i_multiply_scalar(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3i_negate(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3i_normalize(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3i_reflect(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3i_refract(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector3i_subtract(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4f_add(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4f_add_scalar(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4f_dot(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4f_interpolate(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4f_magnitude(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4f_multiply(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4f_multiply_scalar(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4f_negate(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4f_normalize(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4f_reflect(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4f_refract(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4f_subtract(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4i_add(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4i_add_scalar(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4i_dot(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4i_interpolate(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4i_magnitude(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4i_multiply(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4i_multiply_scalar(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4i_negate(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4i_normalize(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4i_reflect(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4i_refract(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

  static @Nonnull GFFIExpression com_io7m_parasol_vector4i_subtract(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }

}
