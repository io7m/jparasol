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
import com.io7m.jparasol.glsl.ast.GTermName.GTermNameExternal;
import com.io7m.jparasol.typed.TType.TFloat;
import com.io7m.jparasol.typed.TType.TFunction;
import com.io7m.jparasol.typed.TType.TFunctionArgument;
import com.io7m.jparasol.typed.TType.TSampler2D;
import com.io7m.jparasol.typed.TType.TValueType;
import com.io7m.jparasol.typed.TType.TVector2F;
import com.io7m.jparasol.typed.TType.TVector2I;
import com.io7m.jparasol.typed.TType.TVector3F;
import com.io7m.jparasol.typed.TType.TVector4F;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDFunctionExternal;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * Built-in FFI expression emitters.
 */

@EqualityReference public final class GFFIExpressionEmittersSampler2D
{
  // CHECKSTYLE:OFF

  static GFFIExpression com_io7m_parasol_sampler2d_texture(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersionType version)
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
    final GVersionType version)
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
    final GVersionType version)
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

  static GFFIExpression com_io7m_parasol_sampler2d_texture_with_offset(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersionType version)
    throws UnreachableCodeException,
      GFFIError
  {
    final TFunction ft = f.getType();
    final List<TFunctionArgument> fta = ft.getArguments();

    assert arguments.size() == 3;
    assert fta.size() == 3;
    assert fta.get(0).getType().equals(TSampler2D.get());
    assert fta.get(1).getType().equals(TVector2F.get());
    assert fta.get(2).getType().equals(TVector2I.get());
    assert ft.getReturnType().equals(TVector4F.get());

    final TValueType type = ft.getReturnType();
    return version
      .versionAccept(new GVersionVisitorType<GFFIExpression, GFFIError>() {
        @Override public GFFIExpression versionVisitES(
          final GVersionES v)
          throws GFFIError
        {
          if (v.compareTo(GVersionES.GLSL_ES_300) < 0) {
            throw GFFIError.unsupportedExternal(f.getExternal(), v);
          }
          final GTermNameExternal name =
            new GTermNameExternal("textureOffset");
          return new GFFIExpression.GFFIExpressionBuiltIn(
            new GASTExpression.GASTEApplicationExternal(name, type, arguments));
        }

        @Override public GFFIExpression versionVisitFull(
          final GVersionFull v)
          throws GFFIError
        {
          if (v.compareTo(GVersionFull.GLSL_120) <= 0) {
            throw GFFIError.unsupportedExternal(f.getExternal(), v);
          }
          final GTermNameExternal name =
            new GTermNameExternal("textureOffset");
          return new GFFIExpression.GFFIExpressionBuiltIn(
            new GASTExpression.GASTEApplicationExternal(name, type, arguments));
        }
      });
  }

  static GFFIExpression com_io7m_parasol_sampler2d_texture_with_lod(
    final TASTDFunctionExternal f,
    final List<GASTExpression> arguments,
    final GVersionType version)
    throws UnreachableCodeException,
      GFFIError
  {
    final TFunction ft = f.getType();
    final List<TFunctionArgument> fta = ft.getArguments();

    assert arguments.size() == 3;
    assert fta.size() == 3;
    assert fta.get(0).getType().equals(TSampler2D.get());
    assert fta.get(1).getType().equals(TVector2F.get());
    assert fta.get(2).getType().equals(TFloat.get());
    assert ft.getReturnType().equals(TVector4F.get());

    final TValueType type = ft.getReturnType();
    return version
      .versionAccept(new GVersionVisitorType<GFFIExpression, GFFIError>() {
        @Override public GFFIExpression versionVisitES(
          final GVersionES v)
          throws GFFIError
        {
          if (v.compareTo(GVersionES.GLSL_ES_300) < 0) {
            throw GFFIError.unsupportedExternal(f.getExternal(), v);
          }
          final GTermNameExternal name = new GTermNameExternal("textureLod");
          return new GFFIExpression.GFFIExpressionBuiltIn(
            new GASTExpression.GASTEApplicationExternal(name, type, arguments));
        }

        @Override public GFFIExpression versionVisitFull(
          final GVersionFull v)
          throws GFFIError
        {
          if (v.compareTo(GVersionFull.GLSL_120) <= 0) {
            throw GFFIError.unsupportedExternal(f.getExternal(), v);
          }
          final GTermNameExternal name = new GTermNameExternal("textureLod");
          return new GFFIExpression.GFFIExpressionBuiltIn(
            new GASTExpression.GASTEApplicationExternal(name, type, arguments));
        }
      });
  }

  private GFFIExpressionEmittersSampler2D()
  {
    throw new UnreachableCodeException();
  }

}
