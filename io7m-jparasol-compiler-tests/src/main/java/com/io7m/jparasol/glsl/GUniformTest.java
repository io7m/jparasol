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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jaux.functional.Pair;
import com.io7m.jparasol.TestPipeline;
import com.io7m.jparasol.lexer.Position;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.typed.TType;
import com.io7m.jparasol.typed.TType.TBoolean;
import com.io7m.jparasol.typed.TType.TFloat;
import com.io7m.jparasol.typed.TType.TInteger;
import com.io7m.jparasol.typed.TType.TRecord;
import com.io7m.jparasol.typed.TType.TRecordField;
import com.io7m.jparasol.typed.TType.TValueType;
import com.io7m.jparasol.typed.TTypeName.TTypeNameBuiltIn;
import com.io7m.jparasol.typed.TTypeName.TTypeNameGlobal;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragmentParameter;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderVertexParameter;
import com.io7m.jparasol.typed.ast.TASTTermName.TASTTermNameLocal;

public final class GUniformTest
{
  static @Nonnull TASTTermNameLocal nameLocal(
    final @Nonnull String name)
  {
    try {
      return new TASTTermNameLocal(GUniformTest.token(name), name);
    } catch (final ConstraintError e) {
      throw new UnreachableCodeException(e);
    }
  }

  private static @Nonnull TokenIdentifierLower token(
    final @Nonnull String name)
    throws ConstraintError
  {
    return new TokenIdentifierLower(
      new File("<stdin>"),
      new Position(0, 0),
      name);
  }

  @SuppressWarnings("static-method") @Test public void testVertexUniforms()
    throws ConstraintError
  {
    final Map<TTypeNameBuiltIn, TType> types = TType.getBaseTypesByName();
    for (final TTypeNameBuiltIn name : types.keySet()) {
      final TType type = types.get(name);
      final TASTTermNameLocal p_name = GUniformTest.nameLocal("xyz");
      final List<Pair<String, TType>> expanded =
        GUniform.expandUniformVertex(new TASTDShaderVertexParameter(
          p_name,
          (TValueType) type));
      Assert.assertEquals(1, expanded.size());
      Assert.assertEquals("xyz", expanded.get(0).first);
      Assert.assertEquals(type, expanded.get(0).second);
    }
  }

  @SuppressWarnings("static-method") @Test public
    void
    testUniformsVertexRecord_0()
      throws ConstraintError
  {
    final TTypeNameGlobal name =
      new TTypeNameGlobal(
        TestPipeline.getModuleMakePath("x.y", "M"),
        GUniformTest.token("t"));
    final List<TRecordField> fields = new ArrayList<TType.TRecordField>();
    fields.add(new TRecordField("x", TInteger.get()));
    final TRecord tr = new TRecord(name, fields);
    final TASTTermNameLocal p_name = GUniformTest.nameLocal("xyz");
    final TASTDShaderVertexParameter p =
      new TASTDShaderVertexParameter(p_name, tr);

    final List<Pair<String, TType>> expanded =
      GUniform.expandUniformVertex(p);
    Assert.assertEquals(1, expanded.size());
    Assert.assertEquals("xyz.x", expanded.get(0).first);
    Assert.assertEquals(TInteger.get(), expanded.get(0).second);
  }

  @SuppressWarnings("static-method") @Test public
    void
    testUniformsVertexRecord_1()
      throws ConstraintError
  {
    final TTypeNameGlobal name =
      new TTypeNameGlobal(
        TestPipeline.getModuleMakePath("x.y", "M"),
        GUniformTest.token("t"));

    final List<TRecordField> fields = new ArrayList<TType.TRecordField>();
    fields.add(new TRecordField("x", TInteger.get()));
    fields.add(new TRecordField("y", TFloat.get()));
    fields.add(new TRecordField("z", TBoolean.get()));
    final TRecord tr = new TRecord(name, fields);

    final TASTTermNameLocal p_name = GUniformTest.nameLocal("xyz");
    final TASTDShaderVertexParameter p =
      new TASTDShaderVertexParameter(p_name, tr);

    final List<Pair<String, TType>> expanded =
      GUniform.expandUniformVertex(p);
    Assert.assertEquals(3, expanded.size());
    Assert.assertEquals("xyz.x", expanded.get(0).first);
    Assert.assertEquals(TInteger.get(), expanded.get(0).second);
    Assert.assertEquals("xyz.y", expanded.get(1).first);
    Assert.assertEquals(TFloat.get(), expanded.get(1).second);
    Assert.assertEquals("xyz.z", expanded.get(2).first);
    Assert.assertEquals(TBoolean.get(), expanded.get(2).second);
  }

  @SuppressWarnings("static-method") @Test public
    void
    testUniformsVertexRecord_2()
      throws ConstraintError
  {
    final TTypeNameGlobal name =
      new TTypeNameGlobal(
        TestPipeline.getModuleMakePath("x.y", "M"),
        GUniformTest.token("t"));

    final List<TRecordField> fields = new ArrayList<TType.TRecordField>();
    fields.add(new TRecordField("x", TInteger.get()));
    fields.add(new TRecordField("y", TFloat.get()));
    fields.add(new TRecordField("z", TBoolean.get()));
    final TRecord tr = new TRecord(name, fields);

    final List<TRecordField> outer_fields =
      new ArrayList<TType.TRecordField>();
    outer_fields.add(new TRecordField("t0", tr));
    outer_fields.add(new TRecordField("t1", tr));
    outer_fields.add(new TRecordField("t2", tr));
    final TRecord outer = new TRecord(name, outer_fields);

    final TASTTermNameLocal p_name = GUniformTest.nameLocal("xyz");
    final TASTDShaderVertexParameter p =
      new TASTDShaderVertexParameter(p_name, outer);

    final List<Pair<String, TType>> expanded =
      GUniform.expandUniformVertex(p);
    Assert.assertEquals(9, expanded.size());
    Assert.assertEquals("xyz.t0.x", expanded.get(0).first);
    Assert.assertEquals(TInteger.get(), expanded.get(0).second);
    Assert.assertEquals("xyz.t0.y", expanded.get(1).first);
    Assert.assertEquals(TFloat.get(), expanded.get(1).second);
    Assert.assertEquals("xyz.t0.z", expanded.get(2).first);
    Assert.assertEquals(TBoolean.get(), expanded.get(2).second);

    Assert.assertEquals("xyz.t1.x", expanded.get(3).first);
    Assert.assertEquals(TInteger.get(), expanded.get(3).second);
    Assert.assertEquals("xyz.t1.y", expanded.get(4).first);
    Assert.assertEquals(TFloat.get(), expanded.get(4).second);
    Assert.assertEquals("xyz.t1.z", expanded.get(5).first);
    Assert.assertEquals(TBoolean.get(), expanded.get(5).second);

    Assert.assertEquals("xyz.t2.x", expanded.get(6).first);
    Assert.assertEquals(TInteger.get(), expanded.get(6).second);
    Assert.assertEquals("xyz.t2.y", expanded.get(7).first);
    Assert.assertEquals(TFloat.get(), expanded.get(7).second);
    Assert.assertEquals("xyz.t2.z", expanded.get(8).first);
    Assert.assertEquals(TBoolean.get(), expanded.get(8).second);
  }

  @SuppressWarnings("static-method") @Test public void testFragmentUniforms()
    throws ConstraintError
  {
    final Map<TTypeNameBuiltIn, TType> types = TType.getBaseTypesByName();
    for (final TTypeNameBuiltIn name : types.keySet()) {
      final TType type = types.get(name);
      final TASTTermNameLocal p_name = GUniformTest.nameLocal("xyz");
      final List<Pair<String, TType>> expanded =
        GUniform.expandUniformFragment(new TASTDShaderFragmentParameter(
          p_name,
          (TValueType) type));
      Assert.assertEquals(1, expanded.size());
      Assert.assertEquals("xyz", expanded.get(0).first);
      Assert.assertEquals(type, expanded.get(0).second);
    }
  }

  @SuppressWarnings("static-method") @Test public
    void
    testUniformsFragmentRecord_0()
      throws ConstraintError
  {
    final TTypeNameGlobal name =
      new TTypeNameGlobal(
        TestPipeline.getModuleMakePath("x.y", "M"),
        GUniformTest.token("t"));
    final List<TRecordField> fields = new ArrayList<TType.TRecordField>();
    fields.add(new TRecordField("x", TInteger.get()));
    final TRecord tr = new TRecord(name, fields);
    final TASTTermNameLocal p_name = GUniformTest.nameLocal("xyz");
    final TASTDShaderFragmentParameter p =
      new TASTDShaderFragmentParameter(p_name, tr);

    final List<Pair<String, TType>> expanded =
      GUniform.expandUniformFragment(p);
    Assert.assertEquals(1, expanded.size());
    Assert.assertEquals("xyz.x", expanded.get(0).first);
    Assert.assertEquals(TInteger.get(), expanded.get(0).second);
  }

  @SuppressWarnings("static-method") @Test public
    void
    testUniformsFragmentRecord_1()
      throws ConstraintError
  {
    final TTypeNameGlobal name =
      new TTypeNameGlobal(
        TestPipeline.getModuleMakePath("x.y", "M"),
        GUniformTest.token("t"));

    final List<TRecordField> fields = new ArrayList<TType.TRecordField>();
    fields.add(new TRecordField("x", TInteger.get()));
    fields.add(new TRecordField("y", TFloat.get()));
    fields.add(new TRecordField("z", TBoolean.get()));
    final TRecord tr = new TRecord(name, fields);

    final TASTTermNameLocal p_name = GUniformTest.nameLocal("xyz");
    final TASTDShaderFragmentParameter p =
      new TASTDShaderFragmentParameter(p_name, tr);

    final List<Pair<String, TType>> expanded =
      GUniform.expandUniformFragment(p);
    Assert.assertEquals(3, expanded.size());
    Assert.assertEquals("xyz.x", expanded.get(0).first);
    Assert.assertEquals(TInteger.get(), expanded.get(0).second);
    Assert.assertEquals("xyz.y", expanded.get(1).first);
    Assert.assertEquals(TFloat.get(), expanded.get(1).second);
    Assert.assertEquals("xyz.z", expanded.get(2).first);
    Assert.assertEquals(TBoolean.get(), expanded.get(2).second);
  }

  @SuppressWarnings("static-method") @Test public
    void
    testUniformsFragmentRecord_2()
      throws ConstraintError
  {
    final TTypeNameGlobal name =
      new TTypeNameGlobal(
        TestPipeline.getModuleMakePath("x.y", "M"),
        GUniformTest.token("t"));

    final List<TRecordField> fields = new ArrayList<TType.TRecordField>();
    fields.add(new TRecordField("x", TInteger.get()));
    fields.add(new TRecordField("y", TFloat.get()));
    fields.add(new TRecordField("z", TBoolean.get()));
    final TRecord tr = new TRecord(name, fields);

    final List<TRecordField> outer_fields =
      new ArrayList<TType.TRecordField>();
    outer_fields.add(new TRecordField("t0", tr));
    outer_fields.add(new TRecordField("t1", tr));
    outer_fields.add(new TRecordField("t2", tr));
    final TRecord outer = new TRecord(name, outer_fields);

    final TASTTermNameLocal p_name = GUniformTest.nameLocal("xyz");
    final TASTDShaderFragmentParameter p =
      new TASTDShaderFragmentParameter(p_name, outer);

    final List<Pair<String, TType>> expanded =
      GUniform.expandUniformFragment(p);
    Assert.assertEquals(9, expanded.size());
    Assert.assertEquals("xyz.t0.x", expanded.get(0).first);
    Assert.assertEquals(TInteger.get(), expanded.get(0).second);
    Assert.assertEquals("xyz.t0.y", expanded.get(1).first);
    Assert.assertEquals(TFloat.get(), expanded.get(1).second);
    Assert.assertEquals("xyz.t0.z", expanded.get(2).first);
    Assert.assertEquals(TBoolean.get(), expanded.get(2).second);

    Assert.assertEquals("xyz.t1.x", expanded.get(3).first);
    Assert.assertEquals(TInteger.get(), expanded.get(3).second);
    Assert.assertEquals("xyz.t1.y", expanded.get(4).first);
    Assert.assertEquals(TFloat.get(), expanded.get(4).second);
    Assert.assertEquals("xyz.t1.z", expanded.get(5).first);
    Assert.assertEquals(TBoolean.get(), expanded.get(5).second);

    Assert.assertEquals("xyz.t2.x", expanded.get(6).first);
    Assert.assertEquals(TInteger.get(), expanded.get(6).second);
    Assert.assertEquals("xyz.t2.y", expanded.get(7).first);
    Assert.assertEquals(TFloat.get(), expanded.get(7).second);
    Assert.assertEquals("xyz.t2.z", expanded.get(8).first);
    Assert.assertEquals(TBoolean.get(), expanded.get(8).second);
  }
}
