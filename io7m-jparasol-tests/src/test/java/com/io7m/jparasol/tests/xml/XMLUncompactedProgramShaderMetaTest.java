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

import com.io7m.jfunctional.Option;
import com.io7m.jparasol.core.GVersionES;
import com.io7m.jparasol.core.GVersionFull;
import com.io7m.jparasol.core.JPCompiledShaderMetaType;
import com.io7m.jparasol.core.JPMissingHash;
import com.io7m.jparasol.core.JPUncompactedProgramShaderMeta;
import com.io7m.jparasol.tests.TestUtilities;
import com.io7m.jparasol.xml.JPXMLException;
import com.io7m.jparasol.xml.XMLMeta;
import com.io7m.jparasol.xml.XMLUncompactedProgramShaderMeta;
import com.io7m.junreachable.UnreachableCodeException;

@SuppressWarnings({ "null", "resource", "static-method" }) public final class XMLUncompactedProgramShaderMetaTest
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
      XMLUncompactedProgramShaderMetaTest.class.getResourceAsStream(name);
    return XMLUncompactedProgramShaderMetaTest.fromStream(stream);
  }

  private static void serialize(
    final OutputStream bao,
    final JPUncompactedProgramShaderMeta meta)
  {
    try {
      final Serializer s = new Serializer(bao);
      s.setIndent(2);
      s.setMaxLength(160);
      s.write(new Document(XMLUncompactedProgramShaderMeta
        .serializeToXML(meta)));
      s.flush();
    } catch (final IOException e) {
      throw new UnreachableCodeException(e);
    }
  }

  @Test public void testRoundTrip_0()
    throws JPMissingHash
  {
    final JPUncompactedProgramShaderMeta meta0 =
      (JPUncompactedProgramShaderMeta) XMLUncompactedProgramShaderMetaTest
        .getXML("/com/io7m/jparasol/tests/xml/t-actual-program.xml");

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
        XMLUncompactedProgramShaderMetaTest.serialize(bao, meta);
        final ByteArrayInputStream bai =
          new ByteArrayInputStream(bao.toByteArray());
        meta_next =
          (JPUncompactedProgramShaderMeta) XMLUncompactedProgramShaderMetaTest
            .fromStream(bai);
      }

      Assert.assertNotSame(meta, meta_next);
      Assert.assertEquals(meta, meta_next);
      meta = meta_next;
    }
  }
}
