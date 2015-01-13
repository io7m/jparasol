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
import java.io.InputStream;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jlog.LogUsableType;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.core.JPCompactedFragmentShaderMeta;
import com.io7m.jparasol.core.JPCompactedVertexShaderMeta;
import com.io7m.jparasol.core.JPCompiledShaderMetaType;
import com.io7m.jparasol.core.JPCompiledShaderMetaVisitorType;
import com.io7m.jparasol.core.JPFragmentShaderMetaType;
import com.io7m.jparasol.core.JPMissingHash;
import com.io7m.jparasol.core.JPUncompactedFragmentShaderMeta;
import com.io7m.jparasol.core.JPUncompactedProgramShaderMeta;
import com.io7m.jparasol.core.JPUncompactedVertexShaderMeta;
import com.io7m.jparasol.core.JPVertexShaderMetaType;
import com.io7m.jparasol.metaserializer.JPMetaDeserializerType;
import com.io7m.jparasol.metaserializer.JPSerializerException;

/**
 * The implementation of an XML metadata deserializer.
 */

@EqualityReference public final class JPXMLMetaDeserializer implements
  JPMetaDeserializerType
{
  /**
   * @return A new metadata deserializer
   */

  public static JPMetaDeserializerType newDeserializer(
    final LogUsableType in_log)
  {
    return new JPXMLMetaDeserializer(in_log);
  }

  private final LogUsableType log;

  private JPXMLMetaDeserializer(
    final LogUsableType in_log)
  {
    this.log = NullCheck.notNull(in_log);
  }

  @Override public JPFragmentShaderMetaType metaDeserializeFragmentShader(
    final InputStream in)
    throws IOException,
      JPSerializerException
  {
    return this
      .metaDeserializeShader(in)
      .matchMeta(
        new JPCompiledShaderMetaVisitorType<JPFragmentShaderMetaType, JPSerializerException>() {
          @Override public JPFragmentShaderMetaType compactedFragment(
            final JPCompactedFragmentShaderMeta m)
            throws JPSerializerException
          {
            return m;
          }

          @Override public JPFragmentShaderMetaType compactedVertex(
            final JPCompactedVertexShaderMeta m)
            throws JPSerializerException
          {
            throw new JPSerializerException(
              "Expected a fragment shader, but got an uncompacted vertex shader");
          }

          @Override public JPFragmentShaderMetaType uncompactedFragment(
            final JPUncompactedFragmentShaderMeta m)
            throws JPSerializerException
          {
            return m;
          }

          @Override public JPFragmentShaderMetaType uncompactedProgram(
            final JPUncompactedProgramShaderMeta m)
            throws JPSerializerException
          {
            throw new JPSerializerException(
              "Expected a fragment shader, but got an uncompacted program");
          }

          @Override public JPFragmentShaderMetaType uncompactedVertex(
            final JPUncompactedVertexShaderMeta m)
            throws JPSerializerException
          {
            throw new JPSerializerException(
              "Expected a fragment shader, but got an uncompacted vertex shader");
          }
        });
  }

  @Override public
    JPCompactedFragmentShaderMeta
    metaDeserializeFragmentShaderCompacted(
      final InputStream in)
      throws IOException,
        JPSerializerException
  {
    NullCheck.notNull(in);

    return this
      .metaDeserializeShader(in)
      .matchMeta(
        new JPCompiledShaderMetaVisitorType<JPCompactedFragmentShaderMeta, JPSerializerException>() {
          @Override public JPCompactedFragmentShaderMeta compactedFragment(
            final JPCompactedFragmentShaderMeta m)
            throws JPSerializerException
          {
            return m;
          }

          @Override public JPCompactedFragmentShaderMeta compactedVertex(
            final JPCompactedVertexShaderMeta m)
            throws JPSerializerException
          {
            throw new JPSerializerException(
              "Expected a compacted fragment shader, but got an uncompacted vertex shader");
          }

          @Override public JPCompactedFragmentShaderMeta uncompactedFragment(
            final JPUncompactedFragmentShaderMeta m)
            throws JPSerializerException
          {
            throw new JPSerializerException(
              "Expected a compacted fragment shader, but got an uncompacted fragment shader");
          }

          @Override public JPCompactedFragmentShaderMeta uncompactedProgram(
            final JPUncompactedProgramShaderMeta m)
            throws JPSerializerException
          {
            throw new JPSerializerException(
              "Expected a compacted fragment shader, but got an uncompacted program");
          }

          @Override public JPCompactedFragmentShaderMeta uncompactedVertex(
            final JPUncompactedVertexShaderMeta m)
            throws JPSerializerException
          {
            throw new JPSerializerException(
              "Expected a compacted fragment shader, but got an uncompacted program");
          }
        });
  }

  @Override public
    JPUncompactedFragmentShaderMeta
    metaDeserializeFragmentShaderUncompacted(
      final InputStream in)
      throws IOException,
        JPSerializerException
  {
    return this
      .metaDeserializeShader(in)
      .matchMeta(
        new JPCompiledShaderMetaVisitorType<JPUncompactedFragmentShaderMeta, JPSerializerException>() {
          @Override public JPUncompactedFragmentShaderMeta compactedFragment(
            final JPCompactedFragmentShaderMeta m)
            throws JPSerializerException
          {
            throw new JPSerializerException(
              "Expected an uncompacted fragment shader, but got a compacted fragment shader");
          }

          @Override public JPUncompactedFragmentShaderMeta compactedVertex(
            final JPCompactedVertexShaderMeta m)
            throws JPSerializerException
          {
            throw new JPSerializerException(
              "Expected an uncompacted fragment shader, but got a compacted vertex shader");
          }

          @Override public
            JPUncompactedFragmentShaderMeta
            uncompactedFragment(
              final JPUncompactedFragmentShaderMeta m)
              throws JPSerializerException
          {
            return m;
          }

          @Override public
            JPUncompactedFragmentShaderMeta
            uncompactedProgram(
              final JPUncompactedProgramShaderMeta m)
              throws JPSerializerException
          {
            throw new JPSerializerException(
              "Expected an uncompacted fragment shader, but got an uncompacted program");
          }

          @Override public JPUncompactedFragmentShaderMeta uncompactedVertex(
            final JPUncompactedVertexShaderMeta m)
            throws JPSerializerException
          {
            throw new JPSerializerException(
              "Expected an uncompacted fragment shader, but got an uncompacted vertex shader");
          }
        });
  }

  @Override public
    JPUncompactedProgramShaderMeta
    metaDeserializeProgramShaderUncompacted(
      final InputStream in)
      throws IOException,
        JPSerializerException
  {
    return this
      .metaDeserializeShader(in)
      .matchMeta(
        new JPCompiledShaderMetaVisitorType<JPUncompactedProgramShaderMeta, JPSerializerException>() {
          @Override public JPUncompactedProgramShaderMeta compactedFragment(
            final JPCompactedFragmentShaderMeta m)
            throws JPSerializerException
          {
            throw new JPSerializerException(
              "Expected an uncompacted program shader, but got a compacted fragment shader");
          }

          @Override public JPUncompactedProgramShaderMeta compactedVertex(
            final JPCompactedVertexShaderMeta m)
            throws JPSerializerException
          {
            throw new JPSerializerException(
              "Expected an uncompacted program shader, but got an uncompacted vertex shader");
          }

          @Override public
            JPUncompactedProgramShaderMeta
            uncompactedFragment(
              final JPUncompactedFragmentShaderMeta m)
              throws JPSerializerException
          {
            throw new JPSerializerException(
              "Expected an uncompacted program shader, but got an uncompacted fragment shader");
          }

          @Override public JPUncompactedProgramShaderMeta uncompactedProgram(
            final JPUncompactedProgramShaderMeta m)
            throws JPSerializerException
          {
            return m;
          }

          @Override public JPUncompactedProgramShaderMeta uncompactedVertex(
            final JPUncompactedVertexShaderMeta m)
            throws JPSerializerException
          {
            throw new JPSerializerException(
              "Expected an uncompacted program shader, but got an uncompacted program");
          }
        });
  }

  @Override public JPCompiledShaderMetaType metaDeserializeShader(
    final InputStream in)
    throws IOException,
      JPSerializerException
  {
    NullCheck.notNull(in);
    try {
      return XMLMeta.fromStream(in, this.log);
    } catch (final JPMissingHash e) {
      throw new JPSerializerException(e);
    } catch (final JPXMLException e) {
      throw new JPSerializerException(e);
    }
  }

  @Override public JPVertexShaderMetaType metaDeserializeVertexShader(
    final InputStream in)
    throws IOException,
      JPSerializerException
  {
    return this
      .metaDeserializeShader(in)
      .matchMeta(
        new JPCompiledShaderMetaVisitorType<JPVertexShaderMetaType, JPSerializerException>() {
          @Override public JPVertexShaderMetaType compactedFragment(
            final JPCompactedFragmentShaderMeta m)
            throws JPSerializerException
          {
            throw new JPSerializerException(
              "Expected a vertex shader, but got an compacted fragment shader");
          }

          @Override public JPVertexShaderMetaType compactedVertex(
            final JPCompactedVertexShaderMeta m)
            throws JPSerializerException
          {
            return m;
          }

          @Override public JPVertexShaderMetaType uncompactedFragment(
            final JPUncompactedFragmentShaderMeta m)
            throws JPSerializerException
          {
            throw new JPSerializerException(
              "Expected a vertex shader, but got an uncompacted fragment shader");
          }

          @Override public JPVertexShaderMetaType uncompactedProgram(
            final JPUncompactedProgramShaderMeta m)
            throws JPSerializerException
          {
            throw new JPSerializerException(
              "Expected a vertex shader, but got an uncompacted program");
          }

          @Override public JPVertexShaderMetaType uncompactedVertex(
            final JPUncompactedVertexShaderMeta m)
            throws JPSerializerException
          {
            return m;
          }
        });
  }

  @Override public
    JPCompactedVertexShaderMeta
    metaDeserializeVertexShaderCompacted(
      final InputStream in)
      throws IOException,
        JPSerializerException
  {
    return this
      .metaDeserializeShader(in)
      .matchMeta(
        new JPCompiledShaderMetaVisitorType<JPCompactedVertexShaderMeta, JPSerializerException>() {
          @Override public JPCompactedVertexShaderMeta compactedFragment(
            final JPCompactedFragmentShaderMeta m)
            throws JPSerializerException
          {
            throw new JPSerializerException(
              "Expected a compacted vertex shader, but got a compacted fragment shader");
          }

          @Override public JPCompactedVertexShaderMeta compactedVertex(
            final JPCompactedVertexShaderMeta m)
            throws JPSerializerException
          {
            return m;
          }

          @Override public JPCompactedVertexShaderMeta uncompactedFragment(
            final JPUncompactedFragmentShaderMeta m)
            throws JPSerializerException
          {
            throw new JPSerializerException(
              "Expected a compacted vertex shader, but got an uncompacted fragment shader");
          }

          @Override public JPCompactedVertexShaderMeta uncompactedProgram(
            final JPUncompactedProgramShaderMeta m)
            throws JPSerializerException
          {
            throw new JPSerializerException(
              "Expected a compacted vertex shader, but got an uncompacted program");
          }

          @Override public JPCompactedVertexShaderMeta uncompactedVertex(
            final JPUncompactedVertexShaderMeta m)
            throws JPSerializerException
          {
            throw new JPSerializerException(
              "Expected a compacted vertex shader, but got an uncompacted vertex shader");
          }
        });
  }

  @Override public
    JPUncompactedVertexShaderMeta
    metaDeserializeVertexShaderUncompacted(
      final InputStream in)
      throws IOException,
        JPSerializerException
  {
    return this
      .metaDeserializeShader(in)
      .matchMeta(
        new JPCompiledShaderMetaVisitorType<JPUncompactedVertexShaderMeta, JPSerializerException>() {
          @Override public JPUncompactedVertexShaderMeta compactedFragment(
            final JPCompactedFragmentShaderMeta m)
            throws JPSerializerException
          {
            throw new JPSerializerException(
              "Expected an uncompacted vertex shader, but got a compacted fragment shader");
          }

          @Override public JPUncompactedVertexShaderMeta compactedVertex(
            final JPCompactedVertexShaderMeta m)
            throws JPSerializerException
          {
            throw new JPSerializerException(
              "Expected an uncompacted vertex shader, but got an uncompacted vertex shader");
          }

          @Override public JPUncompactedVertexShaderMeta uncompactedFragment(
            final JPUncompactedFragmentShaderMeta m)
            throws JPSerializerException
          {
            throw new JPSerializerException(
              "Expected an uncompacted vertex shader, but got an uncompacted fragment shader");
          }

          @Override public JPUncompactedVertexShaderMeta uncompactedProgram(
            final JPUncompactedProgramShaderMeta m)
            throws JPSerializerException
          {
            throw new JPSerializerException(
              "Expected an uncompacted vertex shader, but got an uncompacted program");
          }

          @Override public JPUncompactedVertexShaderMeta uncompactedVertex(
            final JPUncompactedVertexShaderMeta m)
            throws JPSerializerException
          {
            return m;
          }
        });
  }
}
