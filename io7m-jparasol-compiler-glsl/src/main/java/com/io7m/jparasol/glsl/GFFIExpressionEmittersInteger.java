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
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDFunctionExternal;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * Built-in FFI expression emitters.
 */

@EqualityReference public final class GFFIExpressionEmittersInteger
{
  // CHECKSTYLE:OFF

  static GFFIExpression com_io7m_parasol_integer_add(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersionType version)
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
    final GVersionType version)
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
    final GVersionType version)
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
    final GVersionType version)
  {
    return GFFIExpressionEmitters.genericSubtract(
      f,
      arguments,
      version,
      TInteger.get());
  }

  private GFFIExpressionEmittersInteger()
  {
    throw new UnreachableCodeException();
  }

}
