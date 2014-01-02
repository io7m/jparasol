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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Pair;
import com.io7m.jparasol.typed.TType;
import com.io7m.jparasol.typed.TType.TBoolean;
import com.io7m.jparasol.typed.TType.TFloat;
import com.io7m.jparasol.typed.TType.TFunction;
import com.io7m.jparasol.typed.TType.TInteger;
import com.io7m.jparasol.typed.TType.TManifestType;
import com.io7m.jparasol.typed.TType.TMatrix3x3F;
import com.io7m.jparasol.typed.TType.TMatrix4x4F;
import com.io7m.jparasol.typed.TType.TRecord;
import com.io7m.jparasol.typed.TType.TRecordField;
import com.io7m.jparasol.typed.TType.TSampler2D;
import com.io7m.jparasol.typed.TType.TSamplerCube;
import com.io7m.jparasol.typed.TType.TVector2F;
import com.io7m.jparasol.typed.TType.TVector2I;
import com.io7m.jparasol.typed.TType.TVector3F;
import com.io7m.jparasol.typed.TType.TVector3I;
import com.io7m.jparasol.typed.TType.TVector4F;
import com.io7m.jparasol.typed.TType.TVector4I;
import com.io7m.jparasol.typed.TTypeVisitor;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragmentParameter;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderVertexParameter;

public final class GUniform
{
  private static abstract class Expander implements
    TTypeVisitor<List<Pair<String, TType>>, ConstraintError>
  {
    private static @Nonnull List<Pair<String, TType>> one(
      final @Nonnull String name,
      final @Nonnull TType type)
    {
      final ArrayList<Pair<String, TType>> ls =
        new ArrayList<Pair<String, TType>>();
      final Pair<String, TType> r = new Pair<String, TType>(name, type);
      ls.add(r);
      return ls;
    }

    private final @Nonnull String name;

    public Expander(
      final @Nonnull String name)
    {
      this.name = name;
    }

    @Override public List<Pair<String, TType>> typeVisitBoolean(
      final TBoolean t)
      throws ConstraintError
    {
      return Expander.one(this.name, t);
    }

    @Override public List<Pair<String, TType>> typeVisitFloat(
      final TFloat t)
      throws ConstraintError
    {
      return Expander.one(this.name, t);
    }

    @Override public List<Pair<String, TType>> typeVisitFunction(
      final TFunction t)
      throws ConstraintError
    {
      return Expander.one(this.name, t);
    }

    @Override public List<Pair<String, TType>> typeVisitInteger(
      final TInteger t)
      throws ConstraintError
    {
      return Expander.one(this.name, t);
    }

    @Override public List<Pair<String, TType>> typeVisitMatrix3x3F(
      final TMatrix3x3F t)
      throws ConstraintError
    {
      return Expander.one(this.name, t);
    }

    @Override public List<Pair<String, TType>> typeVisitMatrix4x4F(
      final TMatrix4x4F t)
      throws ConstraintError
    {
      return Expander.one(this.name, t);
    }

    @Override public List<Pair<String, TType>> typeVisitSampler2D(
      final TSampler2D t)
      throws ConstraintError
    {
      return Expander.one(this.name, t);
    }

    @Override public List<Pair<String, TType>> typeVisitSamplerCube(
      final TSamplerCube t)
      throws ConstraintError
    {
      return Expander.one(this.name, t);
    }

    @Override public List<Pair<String, TType>> typeVisitVector2F(
      final TVector2F t)
      throws ConstraintError
    {
      return Expander.one(this.name, t);
    }

    @Override public List<Pair<String, TType>> typeVisitVector2I(
      final TVector2I t)
      throws ConstraintError
    {
      return Expander.one(this.name, t);
    }

    @Override public List<Pair<String, TType>> typeVisitVector3F(
      final TVector3F t)
      throws ConstraintError
    {
      return Expander.one(this.name, t);
    }

    @Override public List<Pair<String, TType>> typeVisitVector3I(
      final TVector3I t)
      throws ConstraintError
    {
      return Expander.one(this.name, t);
    }

    @Override public List<Pair<String, TType>> typeVisitVector4F(
      final TVector4F t)
      throws ConstraintError
    {
      return Expander.one(this.name, t);
    }

    @Override public List<Pair<String, TType>> typeVisitVector4I(
      final TVector4I t)
      throws ConstraintError
    {
      return Expander.one(this.name, t);
    }
  }

  private static @Nonnull List<Pair<String, TType>> expandRecordUniforms(
    final @Nonnull List<String> s,
    final @Nonnull TRecord f)
  {
    final List<Pair<String, TType>> lb = new ArrayList<Pair<String, TType>>();

    for (final TRecordField field : f.getFields()) {
      if (field.getType() instanceof TRecord) {
        s.add(0, field.getName());
        lb
          .addAll(GUniform.expandRecordUniforms(s, (TRecord) field.getType()));
        s.remove(0);
      } else {
        lb.add(GUniform.expandValue(s, field.getName(), field.getType()));
      }
    }

    return lb;
  }

  public static @Nonnull List<Pair<String, TType>> expandUniformFragment(
    final @Nonnull TASTDShaderFragmentParameter p)
    throws ConstraintError
  {
    final String name = p.getName().getCurrent();
    return p.getType().ttypeVisitableAccept(new Expander(name) {
      @Override public List<Pair<String, TType>> typeVisitRecord(
        final @Nonnull TRecord t)
        throws ConstraintError
      {
        final ArrayList<String> ls = new ArrayList<String>();
        ls.add(name);
        return GUniform.expandRecordUniforms(ls, t);
      }
    });
  }

  public static @Nonnull List<Pair<String, TType>> expandUniformVertex(
    final @Nonnull TASTDShaderVertexParameter p)
    throws ConstraintError
  {
    final String name = p.getName().getCurrent();
    return p.getType().ttypeVisitableAccept(new Expander(name) {
      @Override public List<Pair<String, TType>> typeVisitRecord(
        final @Nonnull TRecord t)
        throws ConstraintError
      {
        final ArrayList<String> ls = new ArrayList<String>();
        ls.add(name);
        return GUniform.expandRecordUniforms(ls, t);
      }
    });
  }

  private static @Nonnull Pair<String, TType> expandValue(
    final @Nonnull List<String> s,
    final @Nonnull String name,
    final @Nonnull TManifestType type)
  {
    final StringBuilder b = new StringBuilder();
    final int max = s.size() - 1;

    for (int index = max; index >= 0; --index) {
      b.append(s.get(index));
      b.append(".");
    }

    b.append(name);
    return new Pair<String, TType>(b.toString(), type);
  }
}
