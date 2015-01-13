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

package com.io7m.jparasol.metaserializer.xml;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.core.JPCompactedFragmentShaderMeta;
import com.io7m.jparasol.core.JPCompactedVertexShaderMeta;
import com.io7m.jparasol.core.JPCompiledShaderMetaType;
import com.io7m.jparasol.core.JPCompiledShaderMetaVisitorType;
import com.io7m.jparasol.core.JPFragmentShaderMetaType;
import com.io7m.jparasol.core.JPFragmentShaderMetaVisitorType;
import com.io7m.jparasol.core.JPUncompactedFragmentShaderMeta;
import com.io7m.jparasol.core.JPUncompactedProgramShaderMeta;
import com.io7m.jparasol.core.JPUncompactedVertexShaderMeta;
import com.io7m.jparasol.core.JPVertexShaderMetaType;
import com.io7m.jparasol.core.JPVertexShaderMetaVisitorType;
import com.io7m.jparasol.metaserializer.JPMetaSerializerType;

/**
 * The implementation of an XML metadata serializer.
 */

@EqualityReference public final class JPXMLMetaSerializer implements
  JPMetaSerializerType
{
  /**
   * The suggested metadata filename.
   */

  public static final String SUGGESTED_FILENAME;

  /**
   * The suggested metadata filename suffix.
   */

  public static final String SUGGESTED_FILENAME_SUFFIX;

  static {
    SUGGESTED_FILENAME_SUFFIX = "xml";
    SUGGESTED_FILENAME =
      "meta." + JPXMLMetaSerializer.SUGGESTED_FILENAME_SUFFIX;
  }

  /**
   * @return A new metadata serializer
   */

  public static JPMetaSerializerType newSerializer()
  {
    return new JPXMLMetaSerializer();
  }

  private static void serializeDocument(
    final OutputStream out_meta_stream,
    final Element root)
    throws UnsupportedEncodingException,
      IOException
  {
    final Document doc = new Document(root);
    final Serializer s = new Serializer(out_meta_stream, "UTF-8");
    s.setIndent(2);
    s.setMaxLength(160);
    s.write(doc);
    s.flush();
  }

  private JPXMLMetaSerializer()
  {

  }

  @Override public String metaGetSuggestedFilename()
  {
    return JPXMLMetaSerializer.SUGGESTED_FILENAME;
  }

  @Override public String metaGetSuggestedFilenameSuffix()
  {
    return JPXMLMetaSerializer.SUGGESTED_FILENAME_SUFFIX;
  }

  @Override public void metaSerializeCompactedFragmentShader(
    final JPCompactedFragmentShaderMeta meta,
    final OutputStream out)
    throws IOException
  {
    NullCheck.notNull(meta);
    NullCheck.notNull(out);

    final Element root = XMLCompactedFragmentShaderMeta.serializeToXML(meta);
    JPXMLMetaSerializer.serializeDocument(out, root);
  }

  @Override public void metaSerializeCompactedVertexShader(
    final JPCompactedVertexShaderMeta meta,
    final OutputStream out)
    throws IOException
  {
    NullCheck.notNull(meta);
    NullCheck.notNull(out);

    final Element root = XMLCompactedVertexShaderMeta.serializeToXML(meta);
    JPXMLMetaSerializer.serializeDocument(out, root);
  }

  @Override public void metaSerializeFragmentShader(
    final JPFragmentShaderMetaType meta,
    final OutputStream out)
    throws IOException
  {
    NullCheck.notNull(meta);
    NullCheck.notNull(out);

    final JPXMLMetaSerializer s = this;
    meta
      .matchFragmentMeta(new JPFragmentShaderMetaVisitorType<Unit, IOException>() {
        @Override public Unit compacted(
          final JPCompactedFragmentShaderMeta m)
          throws IOException
        {
          s.metaSerializeCompactedFragmentShader(m, out);
          return Unit.unit();
        }

        @Override public Unit uncompacted(
          final JPUncompactedFragmentShaderMeta m)
          throws IOException
        {
          s.metaSerializeUncompactedFragmentShader(m, out);
          return Unit.unit();
        }
      });
  }

  @Override public void metaSerializeShader(
    final JPCompiledShaderMetaType meta,
    final OutputStream out)
    throws IOException
  {
    NullCheck.notNull(meta);
    NullCheck.notNull(out);

    final JPXMLMetaSerializer s = this;
    meta.matchMeta(new JPCompiledShaderMetaVisitorType<Unit, IOException>() {
      @Override public Unit compactedFragment(
        final JPCompactedFragmentShaderMeta m)
        throws IOException
      {
        s.metaSerializeCompactedFragmentShader(m, out);
        return Unit.unit();
      }

      @Override public Unit compactedVertex(
        final JPCompactedVertexShaderMeta m)
        throws IOException
      {
        s.metaSerializeCompactedVertexShader(m, out);
        return Unit.unit();
      }

      @Override public Unit uncompactedFragment(
        final JPUncompactedFragmentShaderMeta m)
        throws IOException
      {
        s.metaSerializeUncompactedFragmentShader(m, out);
        return Unit.unit();
      }

      @Override public Unit uncompactedProgram(
        final JPUncompactedProgramShaderMeta m)
        throws IOException
      {
        s.metaSerializeUncompactedProgram(m, out);
        return Unit.unit();
      }

      @Override public Unit uncompactedVertex(
        final JPUncompactedVertexShaderMeta m)
        throws IOException
      {
        s.metaSerializeUncompactedVertexShader(m, out);
        return Unit.unit();
      }
    });
  }

  @Override public void metaSerializeUncompactedFragmentShader(
    final JPUncompactedFragmentShaderMeta meta,
    final OutputStream out)
    throws IOException
  {
    NullCheck.notNull(meta);
    NullCheck.notNull(out);

    final Element root =
      XMLUncompactedFragmentShaderMeta.serializeToXML(meta);
    JPXMLMetaSerializer.serializeDocument(out, root);
  }

  @Override public void metaSerializeUncompactedProgram(
    final JPUncompactedProgramShaderMeta meta,
    final OutputStream out)
    throws IOException
  {
    NullCheck.notNull(meta);
    NullCheck.notNull(out);

    final Element root = XMLUncompactedProgramShaderMeta.serializeToXML(meta);
    JPXMLMetaSerializer.serializeDocument(out, root);
  }

  @Override public void metaSerializeUncompactedVertexShader(
    final JPUncompactedVertexShaderMeta meta,
    final OutputStream out)
    throws IOException
  {
    NullCheck.notNull(meta);
    NullCheck.notNull(out);

    final Element root = XMLUncompactedVertexShaderMeta.serializeToXML(meta);
    JPXMLMetaSerializer.serializeDocument(out, root);
  }

  @Override public void metaSerializeVertexShader(
    final JPVertexShaderMetaType meta,
    final OutputStream out)
    throws IOException
  {
    NullCheck.notNull(meta);
    NullCheck.notNull(out);

    final JPXMLMetaSerializer s = this;
    meta
      .matchVertexMeta(new JPVertexShaderMetaVisitorType<Unit, IOException>() {
        @Override public Unit compacted(
          final JPCompactedVertexShaderMeta m)
          throws IOException
        {
          s.metaSerializeCompactedVertexShader(m, out);
          return Unit.unit();
        }

        @Override public Unit uncompacted(
          final JPUncompactedVertexShaderMeta m)
          throws IOException
        {
          s.metaSerializeUncompactedVertexShader(m, out);
          return Unit.unit();
        }
      });
  }
}
