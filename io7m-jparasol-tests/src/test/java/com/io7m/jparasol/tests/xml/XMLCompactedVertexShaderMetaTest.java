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

import javax.xml.parsers.ParserConfigurationException;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;
import nu.xom.Serializer;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.io7m.jparasol.core.CompactedVertexShaderMeta;
import com.io7m.jparasol.core.CompiledShaderMetaType;
import com.io7m.jparasol.tests.TestUtilities;
import com.io7m.jparasol.xml.XMLCompactedVertexShaderMeta;
import com.io7m.jparasol.xml.XMLMeta;
import com.io7m.junreachable.UnreachableCodeException;

public final class XMLCompactedVertexShaderMetaTest
{
  private static CompiledShaderMetaType fromStream(
    final InputStream stream)
  {
    try {
      return XMLMeta.fromStream(stream, TestUtilities.getLog());
    } catch (final ParsingException e) {
      throw new UnreachableCodeException(e);
    } catch (final IOException e) {
      throw new UnreachableCodeException(e);
    } catch (final SAXException e) {
      throw new UnreachableCodeException(e);
    } catch (final ParserConfigurationException e) {
      throw new UnreachableCodeException(e);
    }
  }

  private static CompiledShaderMetaType getXML(
    final String name)
  {
    final InputStream stream =
      XMLCompactedVertexShaderMetaTest.class.getResourceAsStream(name);
    return XMLCompactedVertexShaderMetaTest.fromStream(stream);
  }

  private static void serialize(
    final OutputStream bao,
    final CompactedVertexShaderMeta meta)
  {
    final Element root = XMLCompactedVertexShaderMeta.serializeToXML(meta);
    try {
      final Serializer s = new Serializer(bao);
      s.setIndent(2);
      s.setMaxLength(160);
      s.write(new Document(root));
      s.flush();
    } catch (final IOException e) {
      throw new UnreachableCodeException(e);
    }
  }

  @Test public void testRoundTrip_0()
  {
    final CompactedVertexShaderMeta meta0 =
      (CompactedVertexShaderMeta) XMLCompactedVertexShaderMetaTest
        .getXML("/com/io7m/jparasol/tests/xml/t-actual-vertex-compacted.xml");

    Assert.assertEquals(2, meta0.getDeclaredVertexInputs().size());
    Assert.assertEquals(3, meta0.getDeclaredVertexOutputs().size());
    Assert.assertEquals(1, meta0.getDeclaredVertexParameters().size());

    CompactedVertexShaderMeta meta = meta0;
    for (int index = 0; index < 3; ++index) {
      final CompactedVertexShaderMeta meta_next;
      {
        XMLCompactedVertexShaderMetaTest.serialize(System.out, meta);

        final ByteArrayOutputStream bao = new ByteArrayOutputStream(1 << 14);
        XMLCompactedVertexShaderMetaTest.serialize(bao, meta);
        final ByteArrayInputStream bai =
          new ByteArrayInputStream(bao.toByteArray());
        meta_next =
          (CompactedVertexShaderMeta) XMLCompactedVertexShaderMetaTest
            .fromStream(bai);
      }

      Assert.assertNotSame(meta, meta_next);
      Assert.assertEquals(meta, meta_next);
      meta = meta_next;
    }
  }
}
