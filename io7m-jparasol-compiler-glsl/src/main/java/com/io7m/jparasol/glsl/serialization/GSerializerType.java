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

package com.io7m.jparasol.glsl.serialization;

import java.io.Closeable;
import java.io.IOException;

import com.io7m.jfunctional.OptionType;
import com.io7m.jparasol.core.CompactedFragmentShader;
import com.io7m.jparasol.core.CompactedVertexShader;
import com.io7m.jparasol.core.UncompactedFragmentShader;
import com.io7m.jparasol.core.UncompactedProgramShaderMeta;
import com.io7m.jparasol.core.UncompactedVertexShader;

/**
 * The type of serializers that can write shaders to serializer-specific
 * destinations.
 */

public interface GSerializerType extends Closeable
{
  /**
   * Serialize a compacted shader.
   * 
   * @param shader
   *          The shader.
   * 
   * @throws IOException
   *           If an I/O error occurs, or the shader already exists (and
   *           replacing is disallowed).
   */

  void serializeCompactedFragmentShader(
    final CompactedFragmentShader shader)
    throws IOException;

  /**
   * Serialize a compacted shader.
   * 
   * @param shader
   *          The shader.
   * 
   * @throws IOException
   *           If an I/O error occurs, or the shader already exists (and
   *           replacing is disallowed).
   */

  void serializeCompactedVertexShader(
    final CompactedVertexShader shader)
    throws IOException;

  /**
   * Serialize an uncompacted shader.
   * 
   * @param shader
   *          The shader.
   * 
   * @throws IOException
   *           If an I/O error occurs, or the shader already exists (and
   *           replacing is disallowed).
   */

  void serializeUncompactedFragmentShader(
    final UncompactedFragmentShader shader)
    throws IOException;

  /**
   * Serialize an uncompacted shader.
   * 
   * @param meta
   *          The metadata.
   * 
   * @throws IOException
   *           If an I/O error occurs, or the shader already exists (and
   *           replacing is disallowed).
   */

  void serializeUncompactedProgramShader(
    final UncompactedProgramShaderMeta meta,
    final OptionType<String> name)
    throws IOException;

  /**
   * Serialize an uncompacted shader.
   * 
   * @param shader
   *          The shader.
   * 
   * @throws IOException
   *           If an I/O error occurs, or the shader already exists (and
   *           replacing is disallowed).
   */

  void serializeUncompactedVertexShader(
    final UncompactedVertexShader shader)
    throws IOException;
}
