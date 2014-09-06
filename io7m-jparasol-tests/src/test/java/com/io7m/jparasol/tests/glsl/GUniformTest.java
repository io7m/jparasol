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

package com.io7m.jparasol.tests.glsl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jfunctional.Pair;
import com.io7m.jparasol.glsl.GUniform;
import com.io7m.jparasol.lexer.Position;
import com.io7m.jparasol.lexer.TokenIdentifierLower;
import com.io7m.jparasol.tests.TestPipeline;
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

@SuppressWarnings("static-method") public final class GUniformTest
{
  static TASTTermNameLocal nameLocal(
    final String name)
  {
    return new TASTTermNameLocal(GUniformTest.token(name), name);
  }

  private static TokenIdentifierLower token(
    final String name)

  {
    return new TokenIdentifierLower(
      new File("<stdin>"),
      new Position(0, 0),
      name);
  }

  @Test public void testFragmentUniforms()

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
      Assert.assertEquals("xyz", expanded.get(0).getLeft());
      Assert.assertEquals(type, expanded.get(0).getRight());
    }
  }

  @Test public void testUniformsFragmentRecord_0()

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
    Assert.assertEquals("xyz.x", expanded.get(0).getLeft());
    Assert.assertEquals(TInteger.get(), expanded.get(0).getRight());
  }

  @Test public void testUniformsFragmentRecord_1()

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
    Assert.assertEquals("xyz.x", expanded.get(0).getLeft());
    Assert.assertEquals(TInteger.get(), expanded.get(0).getRight());
    Assert.assertEquals("xyz.y", expanded.get(1).getLeft());
    Assert.assertEquals(TFloat.get(), expanded.get(1).getRight());
    Assert.assertEquals("xyz.z", expanded.get(2).getLeft());
    Assert.assertEquals(TBoolean.get(), expanded.get(2).getRight());
  }

  @Test public void testUniformsFragmentRecord_2()

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
    Assert.assertEquals("xyz.t0.x", expanded.get(0).getLeft());
    Assert.assertEquals(TInteger.get(), expanded.get(0).getRight());
    Assert.assertEquals("xyz.t0.y", expanded.get(1).getLeft());
    Assert.assertEquals(TFloat.get(), expanded.get(1).getRight());
    Assert.assertEquals("xyz.t0.z", expanded.get(2).getLeft());
    Assert.assertEquals(TBoolean.get(), expanded.get(2).getRight());

    Assert.assertEquals("xyz.t1.x", expanded.get(3).getLeft());
    Assert.assertEquals(TInteger.get(), expanded.get(3).getRight());
    Assert.assertEquals("xyz.t1.y", expanded.get(4).getLeft());
    Assert.assertEquals(TFloat.get(), expanded.get(4).getRight());
    Assert.assertEquals("xyz.t1.z", expanded.get(5).getLeft());
    Assert.assertEquals(TBoolean.get(), expanded.get(5).getRight());

    Assert.assertEquals("xyz.t2.x", expanded.get(6).getLeft());
    Assert.assertEquals(TInteger.get(), expanded.get(6).getRight());
    Assert.assertEquals("xyz.t2.y", expanded.get(7).getLeft());
    Assert.assertEquals(TFloat.get(), expanded.get(7).getRight());
    Assert.assertEquals("xyz.t2.z", expanded.get(8).getLeft());
    Assert.assertEquals(TBoolean.get(), expanded.get(8).getRight());
  }

  @Test public void testUniformsVertexRecord_0()

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
    Assert.assertEquals("xyz.x", expanded.get(0).getLeft());
    Assert.assertEquals(TInteger.get(), expanded.get(0).getRight());
  }

  @Test public void testUniformsVertexRecord_1()

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
    Assert.assertEquals("xyz.x", expanded.get(0).getLeft());
    Assert.assertEquals(TInteger.get(), expanded.get(0).getRight());
    Assert.assertEquals("xyz.y", expanded.get(1).getLeft());
    Assert.assertEquals(TFloat.get(), expanded.get(1).getRight());
    Assert.assertEquals("xyz.z", expanded.get(2).getLeft());
    Assert.assertEquals(TBoolean.get(), expanded.get(2).getRight());
  }

  @Test public void testUniformsVertexRecord_2()

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
    Assert.assertEquals("xyz.t0.x", expanded.get(0).getLeft());
    Assert.assertEquals(TInteger.get(), expanded.get(0).getRight());
    Assert.assertEquals("xyz.t0.y", expanded.get(1).getLeft());
    Assert.assertEquals(TFloat.get(), expanded.get(1).getRight());
    Assert.assertEquals("xyz.t0.z", expanded.get(2).getLeft());
    Assert.assertEquals(TBoolean.get(), expanded.get(2).getRight());

    Assert.assertEquals("xyz.t1.x", expanded.get(3).getLeft());
    Assert.assertEquals(TInteger.get(), expanded.get(3).getRight());
    Assert.assertEquals("xyz.t1.y", expanded.get(4).getLeft());
    Assert.assertEquals(TFloat.get(), expanded.get(4).getRight());
    Assert.assertEquals("xyz.t1.z", expanded.get(5).getLeft());
    Assert.assertEquals(TBoolean.get(), expanded.get(5).getRight());

    Assert.assertEquals("xyz.t2.x", expanded.get(6).getLeft());
    Assert.assertEquals(TInteger.get(), expanded.get(6).getRight());
    Assert.assertEquals("xyz.t2.y", expanded.get(7).getLeft());
    Assert.assertEquals(TFloat.get(), expanded.get(7).getRight());
    Assert.assertEquals("xyz.t2.z", expanded.get(8).getLeft());
    Assert.assertEquals(TBoolean.get(), expanded.get(8).getRight());
  }

  @Test public void testVertexUniforms()

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
      Assert.assertEquals("xyz", expanded.get(0).getLeft());
      Assert.assertEquals(type, expanded.get(0).getRight());
    }
  }
}
