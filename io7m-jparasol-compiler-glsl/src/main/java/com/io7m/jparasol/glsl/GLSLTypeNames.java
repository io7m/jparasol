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

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jparasol.glsl.ast.GTypeName;
import com.io7m.jparasol.typed.TType;
import com.io7m.jparasol.typed.TType.TBoolean;
import com.io7m.jparasol.typed.TType.TFloat;
import com.io7m.jparasol.typed.TType.TFunction;
import com.io7m.jparasol.typed.TType.TInteger;
import com.io7m.jparasol.typed.TType.TMatrix3x3F;
import com.io7m.jparasol.typed.TType.TMatrix4x4F;
import com.io7m.jparasol.typed.TType.TRecord;
import com.io7m.jparasol.typed.TType.TSampler2D;
import com.io7m.jparasol.typed.TType.TSamplerCube;
import com.io7m.jparasol.typed.TType.TVector2F;
import com.io7m.jparasol.typed.TType.TVector2I;
import com.io7m.jparasol.typed.TType.TVector3F;
import com.io7m.jparasol.typed.TType.TVector3I;
import com.io7m.jparasol.typed.TType.TVector4F;
import com.io7m.jparasol.typed.TType.TVector4I;
import com.io7m.jparasol.typed.TTypeVisitorType;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * The names of GLSL types.
 */

@EqualityReference public final class GLSLTypeNames
{
  /**
   * @param type
   *          The type
   * @return The GLSL name of the given type
   */

  public static GTypeName getTypeName(
    final TType type)
  {
    if (type instanceof TRecord) {
      throw new IllegalArgumentException("Expected a built-in type");
    }

    return type
      .ttypeVisitableAccept(new TTypeVisitorType<GTypeName, UnreachableCodeException>() {
        @Override public GTypeName typeVisitBoolean(
          final TBoolean t)
          throws UnreachableCodeException
        {
          return new GTypeName("bool");
        }

        @Override public GTypeName typeVisitFloat(
          final TFloat t)
          throws UnreachableCodeException
        {
          return new GTypeName("float");
        }

        @Override public GTypeName typeVisitFunction(
          final TFunction t)
          throws UnreachableCodeException
        {
          throw new UnreachableCodeException();
        }

        @Override public GTypeName typeVisitInteger(
          final TInteger t)
          throws UnreachableCodeException
        {
          return new GTypeName("int");
        }

        @Override public GTypeName typeVisitMatrix3x3F(
          final TMatrix3x3F t)
          throws UnreachableCodeException
        {
          return new GTypeName("mat3");
        }

        @Override public GTypeName typeVisitMatrix4x4F(
          final TMatrix4x4F t)
          throws UnreachableCodeException
        {
          return new GTypeName("mat4");
        }

        @Override public GTypeName typeVisitRecord(
          final TRecord t)
          throws UnreachableCodeException
        {
          throw new UnreachableCodeException();
        }

        @Override public GTypeName typeVisitSampler2D(
          final TSampler2D t)
          throws UnreachableCodeException
        {
          return new GTypeName("sampler2D");
        }

        @Override public GTypeName typeVisitSamplerCube(
          final TSamplerCube t)
          throws UnreachableCodeException
        {
          return new GTypeName("samplerCube");
        }

        @Override public GTypeName typeVisitVector2F(
          final TVector2F t)
          throws UnreachableCodeException
        {
          return new GTypeName("vec2");
        }

        @Override public GTypeName typeVisitVector2I(
          final TVector2I t)
          throws UnreachableCodeException
        {
          return new GTypeName("ivec2");
        }

        @Override public GTypeName typeVisitVector3F(
          final TVector3F t)
          throws UnreachableCodeException
        {
          return new GTypeName("vec3");
        }

        @Override public GTypeName typeVisitVector3I(
          final TVector3I t)
          throws UnreachableCodeException
        {
          return new GTypeName("ivec3");
        }

        @Override public GTypeName typeVisitVector4F(
          final TVector4F t)
          throws UnreachableCodeException
        {
          return new GTypeName("vec4");
        }

        @Override public GTypeName typeVisitVector4I(
          final TVector4I t)
          throws UnreachableCodeException
        {
          return new GTypeName("ivec4");
        }
      });
  }

  private GLSLTypeNames()
  {
    throw new UnreachableCodeException();
  }
}
