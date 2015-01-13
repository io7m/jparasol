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

package com.io7m.jparasol.tests.protobuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jparasol.core.GVersionES;
import com.io7m.jparasol.core.GVersionFull;
import com.io7m.jparasol.core.JPCompactedVertexShaderMeta;
import com.io7m.jparasol.core.JPCompiledShaderMetaType;
import com.io7m.jparasol.metaserializer.JPMetaDeserializerType;
import com.io7m.jparasol.metaserializer.JPMetaSerializerType;
import com.io7m.jparasol.metaserializer.JPSerializerException;
import com.io7m.jparasol.metaserializer.protobuf.JPProtobufMetaDeserializer;
import com.io7m.jparasol.metaserializer.protobuf.JPProtobufMetaSerializer;
import com.io7m.jparasol.tests.xml.XMLUncompactedFragmentShaderMetaTest;
import com.io7m.junreachable.UnreachableCodeException;

@SuppressWarnings({ "null", "resource", "static-method" }) public final class ProtobufCompactedVertexShaderMetaTest
{
  private static JPCompiledShaderMetaType fromStream(
    final InputStream stream)
    throws JPSerializerException,
      IOException
  {
    final JPMetaDeserializerType d =
      JPProtobufMetaDeserializer.newDeserializer();
    return d.metaDeserializeShader(stream);
  }

  public static JPCompiledShaderMetaType getData(
    final String name)
    throws JPSerializerException,
      IOException
  {
    final InputStream stream =
      ProtobufCompactedVertexShaderMetaTest.class.getResourceAsStream(name);
    return ProtobufCompactedVertexShaderMetaTest.fromStream(stream);
  }

  private static void serialize(
    final OutputStream bao,
    final JPCompactedVertexShaderMeta meta)
  {
    try {
      final JPMetaSerializerType s = JPProtobufMetaSerializer.newSerializer();
      s.metaSerializeCompactedVertexShader(meta, bao);
      bao.flush();
      bao.close();
    } catch (final IOException e) {
      throw new UnreachableCodeException(e);
    }
  }

  @Test public void testRoundTrip_0()
    throws Exception
  {
    final JPCompactedVertexShaderMeta meta0 =
      (JPCompactedVertexShaderMeta) ProtobufCompactedVertexShaderMetaTest
        .getData("/com/io7m/jparasol/tests/protobuf/t-actual-vertex-compacted.ppsm");

    Assert.assertTrue(meta0.isCompacted());
    Assert.assertEquals(2, meta0.getDeclaredVertexInputs().size());
    Assert.assertEquals(3, meta0.getDeclaredVertexOutputs().size());
    Assert.assertEquals(1, meta0.getDeclaredVertexParameters().size());

    Assert.assertEquals(
      "0cf3b92ad40b9e2a938bd7f2dd2207f067601575292325bdcb8653d6ae7481b4.v",
      XMLUncompactedFragmentShaderMetaTest.sourceCodeName(
        meta0,
        GVersionFull.GLSL_110));
    Assert.assertEquals(
      "0cf3b92ad40b9e2a938bd7f2dd2207f067601575292325bdcb8653d6ae7481b4.v",
      XMLUncompactedFragmentShaderMetaTest.sourceCodeName(
        meta0,
        GVersionFull.GLSL_120));

    Assert.assertEquals(
      "0f8f29aba4f434708f7b850052d5d423cbbcd59b07b5861a8d17278561e8639a.v",
      XMLUncompactedFragmentShaderMetaTest.sourceCodeName(
        meta0,
        GVersionFull.GLSL_130));
    Assert.assertEquals(
      "0f8f29aba4f434708f7b850052d5d423cbbcd59b07b5861a8d17278561e8639a.v",
      XMLUncompactedFragmentShaderMetaTest.sourceCodeName(
        meta0,
        GVersionFull.GLSL_140));
    Assert.assertEquals(
      "0f8f29aba4f434708f7b850052d5d423cbbcd59b07b5861a8d17278561e8639a.v",
      XMLUncompactedFragmentShaderMetaTest.sourceCodeName(
        meta0,
        GVersionFull.GLSL_150));
    Assert.assertEquals(
      "0f8f29aba4f434708f7b850052d5d423cbbcd59b07b5861a8d17278561e8639a.v",
      XMLUncompactedFragmentShaderMetaTest.sourceCodeName(
        meta0,
        GVersionFull.GLSL_330));
    Assert.assertEquals(
      "0f8f29aba4f434708f7b850052d5d423cbbcd59b07b5861a8d17278561e8639a.v",
      XMLUncompactedFragmentShaderMetaTest.sourceCodeName(
        meta0,
        GVersionFull.GLSL_400));
    Assert.assertEquals(
      "0f8f29aba4f434708f7b850052d5d423cbbcd59b07b5861a8d17278561e8639a.v",
      XMLUncompactedFragmentShaderMetaTest.sourceCodeName(
        meta0,
        GVersionFull.GLSL_410));
    Assert.assertEquals(
      "0f8f29aba4f434708f7b850052d5d423cbbcd59b07b5861a8d17278561e8639a.v",
      XMLUncompactedFragmentShaderMetaTest.sourceCodeName(
        meta0,
        GVersionFull.GLSL_420));
    Assert.assertEquals(
      "0f8f29aba4f434708f7b850052d5d423cbbcd59b07b5861a8d17278561e8639a.v",
      XMLUncompactedFragmentShaderMetaTest.sourceCodeName(
        meta0,
        GVersionFull.GLSL_430));
    Assert.assertEquals(
      "0f8f29aba4f434708f7b850052d5d423cbbcd59b07b5861a8d17278561e8639a.v",
      XMLUncompactedFragmentShaderMetaTest.sourceCodeName(
        meta0,
        GVersionFull.GLSL_440));

    Assert.assertEquals(
      "535cddb5c74a881501548a6fdf184ef0b8467aab1b603166745d65d6263ec08f.v",
      XMLUncompactedFragmentShaderMetaTest.sourceCodeName(
        meta0,
        GVersionES.GLSL_ES_100));
    Assert.assertEquals(
      "ec62defa1b0b3a48b2725bed74fd6e96fbc24a2a519705d664fa6edab6a42a4e.v",
      XMLUncompactedFragmentShaderMetaTest.sourceCodeName(
        meta0,
        GVersionES.GLSL_ES_300));

    JPCompactedVertexShaderMeta meta = meta0;
    for (int index = 0; index < 3; ++index) {
      final JPCompactedVertexShaderMeta meta_next;

      {
        final ByteArrayOutputStream bao = new ByteArrayOutputStream(1 << 14);
        ProtobufCompactedVertexShaderMetaTest.serialize(bao, meta);
        final ByteArrayInputStream bai =
          new ByteArrayInputStream(bao.toByteArray());
        meta_next =
          (JPCompactedVertexShaderMeta) ProtobufCompactedVertexShaderMetaTest
            .fromStream(bai);
      }

      Assert.assertNotSame(meta, meta_next);
      Assert.assertEquals(meta, meta_next);
      meta = meta_next;
    }
  }

  @Test(expected = JPSerializerException.class) public void testWrongType_0()
    throws Exception
  {
    final JPMetaDeserializerType d =
      JPProtobufMetaDeserializer.newDeserializer();
    final InputStream stream =
      ProtobufCompactedVertexShaderMetaTest.class
        .getResourceAsStream("/com/io7m/jparasol/tests/protobuf/t-actual-fragment.ppsm");
    d.metaDeserializeVertexShaderUncompacted(stream);
  }

  @Test(expected = JPSerializerException.class) public void testWrongType_1()
    throws Exception
  {
    final JPMetaDeserializerType d =
      JPProtobufMetaDeserializer.newDeserializer();
    final InputStream stream =
      ProtobufCompactedVertexShaderMetaTest.class
        .getResourceAsStream("/com/io7m/jparasol/tests/protobuf/t-actual-program.ppsm");
    d.metaDeserializeVertexShaderUncompacted(stream);
  }

  @Test(expected = JPSerializerException.class) public void testWrongType_2()
    throws Exception
  {
    final JPMetaDeserializerType d =
      JPProtobufMetaDeserializer.newDeserializer();
    final InputStream stream =
      ProtobufCompactedVertexShaderMetaTest.class
        .getResourceAsStream("/com/io7m/jparasol/tests/protobuf/t-actual-vertex-compacted.ppsm");
    d.metaDeserializeVertexShaderUncompacted(stream);
  }

  @Test(expected = JPSerializerException.class) public void testWrongType_3()
    throws Exception
  {
    final JPMetaDeserializerType d =
      JPProtobufMetaDeserializer.newDeserializer();
    final InputStream stream =
      ProtobufCompactedVertexShaderMetaTest.class
        .getResourceAsStream("/com/io7m/jparasol/tests/protobuf/t-actual-fragment-compacted.ppsm");
    d.metaDeserializeVertexShaderUncompacted(stream);
  }
}
