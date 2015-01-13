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

package com.io7m.jparasol.metaserializer;

import java.io.IOException;
import java.io.OutputStream;

import com.io7m.jparasol.core.JPCompactedFragmentShaderMeta;
import com.io7m.jparasol.core.JPCompactedVertexShaderMeta;
import com.io7m.jparasol.core.JPCompiledShaderMetaType;
import com.io7m.jparasol.core.JPFragmentShaderMetaType;
import com.io7m.jparasol.core.JPUncompactedFragmentShaderMeta;
import com.io7m.jparasol.core.JPUncompactedProgramShaderMeta;
import com.io7m.jparasol.core.JPUncompactedVertexShaderMeta;
import com.io7m.jparasol.core.JPVertexShaderMetaType;

/**
 * The interface exposed by metadata serializers.
 */

public interface JPMetaSerializerType
{
  /**
   * @return The suggested filename to use for data produced by this
   *         serializer.
   */

  String metaGetSuggestedFilename();

  /**
   * @return The suggested filename suffix to use for data produced by this
   *         serializer.
   */

  String metaGetSuggestedFilenameSuffix();

  /**
   * Serialize compacted fragment shader metadata to the given stream.
   *
   * @param meta
   *          The metadata
   * @param out
   *          The output stream
   * @throws IOException
   *           On I/O errors
   */

  void metaSerializeCompactedFragmentShader(
    final JPCompactedFragmentShaderMeta meta,
    final OutputStream out)
    throws IOException;

  /**
   * Serialize compacted vertex shader metadata to the given stream.
   *
   * @param meta
   *          The metadata
   * @param out
   *          The output stream
   * @throws IOException
   *           On I/O errors
   */

  void metaSerializeCompactedVertexShader(
    final JPCompactedVertexShaderMeta meta,
    final OutputStream out)
    throws IOException;

  /**
   * Serialize vertex shader metadata to the given stream.
   *
   * @param meta
   *          The metadata
   * @param out
   *          The output stream
   * @throws IOException
   *           On I/O errors
   */

  void metaSerializeFragmentShader(
    final JPFragmentShaderMetaType meta,
    final OutputStream out)
    throws IOException;

  /**
   * Serialize shader metadata to the given stream.
   *
   * @param meta
   *          The metadata
   * @param out
   *          The output stream
   * @throws IOException
   *           On I/O errors
   */

  void metaSerializeShader(
    final JPCompiledShaderMetaType meta,
    final OutputStream out)
    throws IOException;

  /**
   * Serialize uncompacted fragment shader metadata to the given stream.
   *
   * @param meta
   *          The metadata
   * @param out
   *          The output stream
   * @throws IOException
   *           On I/O errors
   */

  void metaSerializeUncompactedFragmentShader(
    final JPUncompactedFragmentShaderMeta meta,
    final OutputStream out)
    throws IOException;

  /**
   * Serialize uncompacted program shader metadata to the given stream.
   *
   * @param meta
   *          The metadata
   * @param out
   *          The output stream
   * @throws IOException
   *           On I/O errors
   */

  void metaSerializeUncompactedProgram(
    final JPUncompactedProgramShaderMeta meta,
    final OutputStream out)
    throws IOException;

  /**
   * Serialize uncompacted vertex shader metadata to the given stream.
   *
   * @param meta
   *          The metadata
   * @param out
   *          The output stream
   * @throws IOException
   *           On I/O errors
   */

  void metaSerializeUncompactedVertexShader(
    final JPUncompactedVertexShaderMeta meta,
    final OutputStream out)
    throws IOException;

  /**
   * Serialize vertex shader metadata to the given stream.
   *
   * @param meta
   *          The metadata
   * @param out
   *          The output stream
   * @throws IOException
   *           On I/O errors
   */

  void metaSerializeVertexShader(
    final JPVertexShaderMetaType meta,
    final OutputStream out)
    throws IOException;
}
