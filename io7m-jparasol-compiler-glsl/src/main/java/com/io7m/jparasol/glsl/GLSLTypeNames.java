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

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;
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
import com.io7m.jparasol.typed.TTypeVisitor;

public final class GLSLTypeNames
{
  public static @Nonnull GTypeName getTypeName(
    final @Nonnull TType type)
    throws ConstraintError
  {
    Constraints.constrainArbitrary(
      (type instanceof TRecord) == false,
      "Type is built-in GLSL type");

    return type
      .ttypeVisitableAccept(new TTypeVisitor<GTypeName, ConstraintError>() {
        @Override public GTypeName typeVisitBoolean(
          final @Nonnull TBoolean t)
          throws ConstraintError
        {
          return new GTypeName("bool");
        }

        @Override public GTypeName typeVisitFloat(
          final @Nonnull TFloat t)
          throws ConstraintError
        {
          return new GTypeName("float");
        }

        @Override public GTypeName typeVisitFunction(
          final @Nonnull TFunction t)
          throws ConstraintError
        {
          throw new UnreachableCodeException();
        }

        @Override public GTypeName typeVisitInteger(
          final @Nonnull TInteger t)
          throws ConstraintError
        {
          return new GTypeName("int");
        }

        @Override public GTypeName typeVisitMatrix3x3F(
          final @Nonnull TMatrix3x3F t)
          throws ConstraintError
        {
          return new GTypeName("mat3");
        }

        @Override public GTypeName typeVisitMatrix4x4F(
          final @Nonnull TMatrix4x4F t)
          throws ConstraintError
        {
          return new GTypeName("mat4");
        }

        @Override public GTypeName typeVisitRecord(
          final @Nonnull TRecord t)
          throws ConstraintError
        {
          throw new UnreachableCodeException();
        }

        @Override public GTypeName typeVisitSampler2D(
          final @Nonnull TSampler2D t)
          throws ConstraintError
        {
          return new GTypeName("sampler2D");
        }

        @Override public GTypeName typeVisitSamplerCube(
          final @Nonnull TSamplerCube t)
          throws ConstraintError
        {
          return new GTypeName("samplerCube");
        }

        @Override public GTypeName typeVisitVector2F(
          final @Nonnull TVector2F t)
          throws ConstraintError
        {
          return new GTypeName("vec2");
        }

        @Override public GTypeName typeVisitVector2I(
          final @Nonnull TVector2I t)
          throws ConstraintError
        {
          return new GTypeName("ivec2");
        }

        @Override public GTypeName typeVisitVector3F(
          final @Nonnull TVector3F t)
          throws ConstraintError
        {
          return new GTypeName("vec3");
        }

        @Override public GTypeName typeVisitVector3I(
          final @Nonnull TVector3I t)
          throws ConstraintError
        {
          return new GTypeName("ivec3");
        }

        @Override public GTypeName typeVisitVector4F(
          final @Nonnull TVector4F t)
          throws ConstraintError
        {
          return new GTypeName("vec4");
        }

        @Override public GTypeName typeVisitVector4I(
          final @Nonnull TVector4I t)
          throws ConstraintError
        {
          return new GTypeName("ivec4");
        }
      });
  }
}
