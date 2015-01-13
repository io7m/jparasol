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

import com.io7m.jfunctional.Option;
import com.io7m.jfunctional.Some;
import com.io7m.jparasol.core.GVersionES;
import com.io7m.jparasol.core.GVersionFull;
import com.io7m.jparasol.core.GVersionType;
import com.io7m.jparasol.core.JPCompiledShaderMetaType;
import com.io7m.jparasol.core.JPUncompactedProgramShaderMeta;
import com.io7m.jparasol.metaserializer.JPMetaDeserializerType;
import com.io7m.jparasol.metaserializer.JPMetaSerializerType;
import com.io7m.jparasol.metaserializer.JPSerializerException;
import com.io7m.jparasol.metaserializer.protobuf.JPProtobufMetaDeserializer;
import com.io7m.jparasol.metaserializer.protobuf.JPProtobufMetaSerializer;
import com.io7m.junreachable.UnreachableCodeException;

@SuppressWarnings({ "null", "resource", "static-method" }) public final class ProtobufUncompactedProgramShaderMetaTest
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
      ProtobufUncompactedVertexShaderMetaTest.class.getResourceAsStream(name);
    return ProtobufUncompactedProgramShaderMetaTest.fromStream(stream);
  }

  private static void serialize(
    final OutputStream bao,
    final JPUncompactedProgramShaderMeta meta)
  {
    try {
      final JPMetaSerializerType s = JPProtobufMetaSerializer.newSerializer();
      s.metaSerializeUncompactedProgram(meta, bao);
      bao.flush();
      bao.close();
    } catch (final IOException e) {
      throw new UnreachableCodeException(e);
    }
  }

  public static String sourceCodeName(
    final JPCompiledShaderMetaType meta,
    final GVersionType v)
  {
    final Some<String> r = (Some<String>) meta.getSourceCodeFilename(v);
    return r.get();
  }

  @Test public void testRoundTrip_0()
    throws Exception
  {
    final JPUncompactedProgramShaderMeta meta0 =
      (JPUncompactedProgramShaderMeta) ProtobufUncompactedProgramShaderMetaTest
        .getData("/com/io7m/jparasol/tests/protobuf/t-actual-program.ppsm");

    Assert.assertFalse(meta0.isCompacted());

    for (final GVersionFull v : GVersionFull.ALL) {
      Assert.assertEquals(Option.none(), meta0.getSourceCodeFilename(v));
    }

    for (final GVersionES v : GVersionES.ALL) {
      Assert.assertEquals(Option.none(), meta0.getSourceCodeFilename(v));
    }

    JPUncompactedProgramShaderMeta meta = meta0;
    for (int index = 0; index < 3; ++index) {
      final JPUncompactedProgramShaderMeta meta_next;
      {
        final ByteArrayOutputStream bao = new ByteArrayOutputStream(1 << 14);
        ProtobufUncompactedProgramShaderMetaTest.serialize(bao, meta);
        final ByteArrayInputStream bai =
          new ByteArrayInputStream(bao.toByteArray());
        meta_next =
          (JPUncompactedProgramShaderMeta) ProtobufUncompactedProgramShaderMetaTest
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
      ProtobufUncompactedFragmentShaderMetaTest.class
        .getResourceAsStream("/com/io7m/jparasol/tests/protobuf/t-actual-vertex.ppsm");
    d.metaDeserializeProgramShaderUncompacted(stream);
  }

  @Test(expected = JPSerializerException.class) public void testWrongType_1()
    throws Exception
  {
    final JPMetaDeserializerType d =
      JPProtobufMetaDeserializer.newDeserializer();
    final InputStream stream =
      ProtobufUncompactedFragmentShaderMetaTest.class
        .getResourceAsStream("/com/io7m/jparasol/tests/protobuf/t-actual-fragment.ppsm");
    d.metaDeserializeProgramShaderUncompacted(stream);
  }

  @Test(expected = JPSerializerException.class) public void testWrongType_2()
    throws Exception
  {
    final JPMetaDeserializerType d =
      JPProtobufMetaDeserializer.newDeserializer();
    final InputStream stream =
      ProtobufUncompactedFragmentShaderMetaTest.class
        .getResourceAsStream("/com/io7m/jparasol/tests/protobuf/t-actual-vertex-compacted.ppsm");
    d.metaDeserializeProgramShaderUncompacted(stream);
  }

  @Test(expected = JPSerializerException.class) public void testWrongType_3()
    throws Exception
  {
    final JPMetaDeserializerType d =
      JPProtobufMetaDeserializer.newDeserializer();
    final InputStream stream =
      ProtobufUncompactedFragmentShaderMetaTest.class
        .getResourceAsStream("/com/io7m/jparasol/tests/protobuf/t-actual-fragment-compacted.ppsm");
    d.metaDeserializeProgramShaderUncompacted(stream);
  }
}
