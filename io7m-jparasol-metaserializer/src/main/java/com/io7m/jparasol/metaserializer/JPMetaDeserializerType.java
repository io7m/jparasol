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
import java.io.InputStream;

import com.io7m.jparasol.core.JPCompactedFragmentShaderMeta;
import com.io7m.jparasol.core.JPCompactedVertexShaderMeta;
import com.io7m.jparasol.core.JPCompiledShaderMetaType;
import com.io7m.jparasol.core.JPUncompactedFragmentShaderMeta;
import com.io7m.jparasol.core.JPUncompactedProgramShaderMeta;
import com.io7m.jparasol.core.JPUncompactedVertexShaderMeta;

/**
 * The interface exposed by metadata deserializers.
 */

public interface JPMetaDeserializerType
{
  /**
   * Deserialize shader metadata from the given stream.
   *
   * @param in
   *          The input stream
   * @return Parsed metadata
   *
   * @throws IOException
   *           On I/O errors
   * @throws JPSerializerException
   *           On deserialization errors
   */

  JPCompactedFragmentShaderMeta metaDeserializeFragmentShaderCompacted(
    InputStream in)
    throws IOException,
      JPSerializerException;

  /**
   * Deserialize shader metadata from the given stream.
   *
   * @param in
   *          The input stream
   * @return Parsed metadata
   *
   * @throws IOException
   *           On I/O errors
   * @throws JPSerializerException
   *           On deserialization errors
   */

  JPUncompactedFragmentShaderMeta metaDeserializeFragmentShaderUncompacted(
    InputStream in)
    throws IOException,
      JPSerializerException;

  /**
   * Deserialize shader metadata from the given stream.
   *
   * @param in
   *          The input stream
   * @return Parsed metadata
   *
   * @throws IOException
   *           On I/O errors
   * @throws JPSerializerException
   *           On deserialization errors
   */

  JPUncompactedProgramShaderMeta metaDeserializeProgramShaderUncompacted(
    InputStream in)
    throws IOException,
      JPSerializerException;

  /**
   * Deserialize shader metadata from the given stream.
   *
   * @param in
   *          The input stream
   * @return Parsed metadata
   *
   * @throws IOException
   *           On I/O errors
   * @throws JPSerializerException
   *           On deserialization errors
   */

  JPCompiledShaderMetaType metaDeserializeShader(
    InputStream in)
    throws IOException,
      JPSerializerException;

  /**
   * Deserialize shader metadata from the given stream.
   *
   * @param in
   *          The input stream
   * @return Parsed metadata
   *
   * @throws IOException
   *           On I/O errors
   * @throws JPSerializerException
   *           On deserialization errors
   */

  JPCompactedVertexShaderMeta metaDeserializeVertexShaderCompacted(
    InputStream in)
    throws IOException,
      JPSerializerException;

  /**
   * Deserialize shader metadata from the given stream.
   *
   * @param in
   *          The input stream
   * @return Parsed metadata
   *
   * @throws IOException
   *           On I/O errors
   * @throws JPSerializerException
   *           On deserialization errors
   */

  JPUncompactedVertexShaderMeta metaDeserializeVertexShaderUncompacted(
    InputStream in)
    throws IOException,
      JPSerializerException;
}
