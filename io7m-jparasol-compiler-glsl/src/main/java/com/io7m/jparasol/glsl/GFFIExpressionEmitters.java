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
import com.io7m.jparasol.glsl.GFFIExpression.GFFIExpressionBuiltIn;
import com.io7m.jparasol.glsl.ast.GASTExpression;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEBinaryOp.GASTEBinaryOpEqual;
import com.io7m.jparasol.glsl.ast.GTermName.GTermNameExternal;
import com.io7m.jparasol.typed.TType.TBoolean;
import com.io7m.jparasol.typed.TType.TFloat;
import com.io7m.jparasol.typed.TType.TFunction;
import com.io7m.jparasol.typed.TType.TFunctionArgument;
import com.io7m.jparasol.typed.TType.TValueType;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDFunctionExternal;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * Built-in FFI expression emitters.
 */

@EqualityReference public final class GFFIExpressionEmitters
{
  // CHECKSTYLE:OFF

  static GFFIExpressionBuiltIn genericClamp(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersionType version,
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

  static GFFIExpressionBuiltIn genericDivide(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersionType version,
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

  static GFFIExpressionBuiltIn genericDivideScalar(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersionType version,
    final TValueType v_type,
    final TValueType s_type)
  {
    assert arguments.size() == 2;
    assert f.getType().getArguments().size() == 2;
    assert f.getType().getArguments().get(0).getType().equals(v_type);
    assert f.getType().getArguments().get(1).getType().equals(s_type);
    assert f.getType().getReturnType().equals(v_type);

    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTExpression.GASTEBinaryOp.GASTEBinaryOpDivide(
        arguments.get(0),
        arguments.get(1)));
  }

  static GFFIExpressionBuiltIn genericDot(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersionType version,
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

  static GFFIExpressionBuiltIn genericEquals(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersionType version,
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

  static GFFIExpressionBuiltIn genericInterpolate(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersionType version,
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

  static GFFIExpressionBuiltIn genericMagnitude(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersionType version,
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

  static GFFIExpressionBuiltIn genericMaximum(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersionType version,
    final TValueType type)
  {
    assert arguments.size() == 2;
    assert f.getType().getArguments().size() == 2;
    assert f.getType().getArguments().get(0).getType().equals(type);
    assert f.getType().getArguments().get(1).getType().equals(type);
    assert f.getType().getReturnType().equals(type);

    final GTermNameExternal tname = new GTermNameExternal("max");
    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTExpression.GASTEApplicationExternal(tname, type, arguments));
  }

  static GFFIExpressionBuiltIn genericMinimum(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersionType version,
    final TValueType type)
  {
    assert arguments.size() == 2;
    assert f.getType().getArguments().size() == 2;
    assert f.getType().getArguments().get(0).getType().equals(type);
    assert f.getType().getArguments().get(1).getType().equals(type);
    assert f.getType().getReturnType().equals(type);

    final GTermNameExternal tname = new GTermNameExternal("min");
    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTExpression.GASTEApplicationExternal(tname, type, arguments));
  }

  static GFFIExpressionBuiltIn genericMultiply(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersionType version,
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

  static GFFIExpressionBuiltIn genericMultiplyScalar(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersionType version,
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

  static GFFIExpressionBuiltIn genericNegate(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersionType version,
    final TValueType type)
  {
    assert arguments.size() == 1;
    assert f.getType().getArguments().size() == 1;
    assert f.getType().getArguments().get(0).getType().equals(type);
    assert f.getType().getReturnType().equals(type);

    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTExpression.GASTEUnaryOp.GASTEUnaryOpNegate(arguments.get(0)));
  }

  static GFFIExpressionBuiltIn genericNormalize(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersionType version,
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

  static GFFIExpressionBuiltIn genericPlus(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersionType version,
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

  static GFFIExpressionBuiltIn genericPlusScalar(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersionType version,
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

  static GFFIExpressionBuiltIn genericReflect(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersionType version,
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

  static GFFIExpressionBuiltIn genericRefract(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersionType version,
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

  static GFFIExpressionBuiltIn genericSubtract(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersionType version,
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

  static GFFIExpressionBuiltIn genericSubtractScalar(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersionType version,
    final TValueType v_type,
    final TValueType s_type)
  {
    assert arguments.size() == 2;
    assert f.getType().getArguments().size() == 2;
    assert f.getType().getArguments().get(0).getType().equals(v_type);
    assert f.getType().getArguments().get(1).getType().equals(s_type);
    assert f.getType().getReturnType().equals(v_type);

    return new GFFIExpression.GFFIExpressionBuiltIn(
      new GASTExpression.GASTEBinaryOp.GASTEBinaryOpSubtract(
        arguments.get(0),
        arguments.get(1)));
  }

  static GFFIExpressionBuiltIn genericUnary(
    final String name,
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersionType version,
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

  private GFFIExpressionEmitters()
  {
    throw new UnreachableCodeException();
  }
}
