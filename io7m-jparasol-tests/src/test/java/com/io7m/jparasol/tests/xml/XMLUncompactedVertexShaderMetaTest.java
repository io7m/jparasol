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

package com.io7m.jparasol.tests.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import nu.xom.Document;
import nu.xom.Serializer;

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jparasol.core.GVersionES;
import com.io7m.jparasol.core.GVersionFull;
import com.io7m.jparasol.core.JPCompiledShaderMetaType;
import com.io7m.jparasol.core.JPMissingHash;
import com.io7m.jparasol.core.JPUncompactedVertexShaderMeta;
import com.io7m.jparasol.metaserializer.JPMetaDeserializerType;
import com.io7m.jparasol.metaserializer.JPSerializerException;
import com.io7m.jparasol.metaserializer.xml.JPXMLException;
import com.io7m.jparasol.metaserializer.xml.JPXMLMetaDeserializer;
import com.io7m.jparasol.metaserializer.xml.XMLMeta;
import com.io7m.jparasol.metaserializer.xml.XMLUncompactedVertexShaderMeta;
import com.io7m.jparasol.tests.TestUtilities;
import com.io7m.junreachable.UnreachableCodeException;

@SuppressWarnings({ "null", "resource", "static-method" }) public final class XMLUncompactedVertexShaderMetaTest
{
  private static JPCompiledShaderMetaType fromStream(
    final InputStream stream)
    throws JPMissingHash
  {
    try {
      return XMLMeta.fromStream(stream, TestUtilities.getLog());
    } catch (final JPXMLException e) {
      throw new UnreachableCodeException(e);
    }
  }

  private static JPCompiledShaderMetaType getXML(
    final String name)
    throws JPMissingHash
  {
    final InputStream stream =
      XMLUncompactedVertexShaderMetaTest.class.getResourceAsStream(name);
    return XMLUncompactedVertexShaderMetaTest.fromStream(stream);
  }

  private static void serialize(
    final OutputStream bao,
    final JPUncompactedVertexShaderMeta meta)
  {
    try {
      final Serializer s = new Serializer(bao);
      s.setIndent(2);
      s.setMaxLength(160);
      s.write(new Document(XMLUncompactedVertexShaderMeta
        .serializeToXML(meta)));
      s.flush();
    } catch (final IOException e) {
      throw new UnreachableCodeException(e);
    }
  }

  @Test public void testRoundTrip_0()
    throws JPMissingHash
  {
    final JPUncompactedVertexShaderMeta meta0 =
      (JPUncompactedVertexShaderMeta) XMLUncompactedVertexShaderMetaTest
        .getXML("/com/io7m/jparasol/tests/xml/t-actual-vertex.xml");

    Assert.assertFalse(meta0.isCompacted());
    Assert.assertEquals(2, meta0.getDeclaredVertexInputs().size());
    Assert.assertEquals(3, meta0.getDeclaredVertexOutputs().size());
    Assert.assertEquals(1, meta0.getDeclaredVertexParameters().size());

    for (final GVersionFull v : GVersionFull.ALL) {
      final String expected = "glsl-" + v.versionGetNumber() + ".v";
      System.err.println("Expected " + expected);
      Assert.assertEquals(
        expected,
        XMLUncompactedFragmentShaderMetaTest.sourceCodeName(meta0, v));
    }

    for (final GVersionES v : GVersionES.ALL) {
      final String expected = "glsl-es-" + v.versionGetNumber() + ".v";
      System.err.println("Expected " + expected);
      Assert.assertEquals(
        expected,
        XMLUncompactedFragmentShaderMetaTest.sourceCodeName(meta0, v));
    }

    JPUncompactedVertexShaderMeta meta = meta0;
    for (int index = 0; index < 3; ++index) {
      final JPUncompactedVertexShaderMeta meta_next;
      {
        final ByteArrayOutputStream bao = new ByteArrayOutputStream(1 << 14);
        XMLUncompactedVertexShaderMetaTest.serialize(bao, meta);
        final ByteArrayInputStream bai =
          new ByteArrayInputStream(bao.toByteArray());
        meta_next =
          (JPUncompactedVertexShaderMeta) XMLUncompactedVertexShaderMetaTest
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
      JPXMLMetaDeserializer.newDeserializer(TestUtilities.getLog());
    final InputStream stream =
      XMLUncompactedVertexShaderMetaTest.class
        .getResourceAsStream("/com/io7m/jparasol/tests/xml/t-actual-fragment.xml");
    d.metaDeserializeVertexShaderUncompacted(stream);
  }

  @Test(expected = JPSerializerException.class) public void testWrongType_1()
    throws Exception
  {
    final JPMetaDeserializerType d =
      JPXMLMetaDeserializer.newDeserializer(TestUtilities.getLog());
    final InputStream stream =
      XMLUncompactedVertexShaderMetaTest.class
        .getResourceAsStream("/com/io7m/jparasol/tests/xml/t-actual-program.xml");
    d.metaDeserializeVertexShaderUncompacted(stream);
  }

  @Test(expected = JPSerializerException.class) public void testWrongType_2()
    throws Exception
  {
    final JPMetaDeserializerType d =
      JPXMLMetaDeserializer.newDeserializer(TestUtilities.getLog());
    final InputStream stream =
      XMLUncompactedVertexShaderMetaTest.class
        .getResourceAsStream("/com/io7m/jparasol/tests/xml/t-actual-vertex-compacted.xml");
    d.metaDeserializeVertexShaderUncompacted(stream);
  }

  @Test(expected = JPSerializerException.class) public void testWrongType_3()
    throws Exception
  {
    final JPMetaDeserializerType d =
      JPXMLMetaDeserializer.newDeserializer(TestUtilities.getLog());
    final InputStream stream =
      XMLUncompactedVertexShaderMetaTest.class
        .getResourceAsStream("/com/io7m/jparasol/tests/xml/t-actual-fragment-compacted.xml");
    d.metaDeserializeVertexShaderUncompacted(stream);
  }
}
