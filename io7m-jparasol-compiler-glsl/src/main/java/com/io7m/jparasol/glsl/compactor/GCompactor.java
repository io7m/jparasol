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

package com.io7m.jparasol.glsl.compactor;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jlog.LogLevel;
import com.io7m.jlog.LogUsableType;
import com.io7m.jparasol.core.CompactedFragmentShader;
import com.io7m.jparasol.core.CompactedFragmentShaderMeta;
import com.io7m.jparasol.core.CompactedVertexShader;
import com.io7m.jparasol.core.CompactedVertexShaderMeta;
import com.io7m.jparasol.core.GVersionType;
import com.io7m.jparasol.core.SourceLines;
import com.io7m.jparasol.core.UncompactedFragmentShader;
import com.io7m.jparasol.core.UncompactedFragmentShaderMeta;
import com.io7m.jparasol.core.UncompactedVertexShader;
import com.io7m.jparasol.core.UncompactedVertexShaderMeta;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * <p>
 * A compactor that eliminates duplicate GLSL shader files.
 * </p>
 */

@EqualityReference public final class GCompactor
{
  /**
   * Compact a serialized fragment shader.
   * 
   * @param f
   *          The shader.
   * @return A compacted shader.
   * @throws GCompactorException
   *           If an error occurs during compaction.
   */

  public static CompactedFragmentShader compactSerializedFragmentShader(
    final UncompactedFragmentShader f,
    final LogUsableType in_log)
    throws GCompactorException
  {
    try {
      final UncompactedFragmentShaderMeta meta = f.getMeta();
      final Map<GVersionType, SourceLines> sources = f.getSources();

      final Map<String, SourceLines> new_sources =
        new HashMap<String, SourceLines>();
      final Map<GVersionType, String> versions =
        new HashMap<GVersionType, String>();

      for (final GVersionType version : sources.keySet()) {
        final SourceLines lines = sources.get(version);
        final SourceLines new_lines =
          SourceLines.newSourceStripped(lines.getLines(), in_log);

        final String hash = new_lines.getHash();
        new_sources.put(hash, new_lines);
        versions.put(version, hash);
      }

      if (in_log.wouldLog(LogLevel.LOG_DEBUG)) {
        @SuppressWarnings("boxing") final String m =
          String.format(
            "Got %d unique sources from %d",
            new_sources.size(),
            sources.size());
        assert m != null;
        in_log.debug(m);
      }

      final CompactedFragmentShaderMeta new_meta =
        CompactedFragmentShaderMeta.newMetadata(
          meta.getName(),
          meta.getSupportsES(),
          meta.getSupportsFull(),
          meta.getDeclaredFragmentInputs(),
          meta.getDeclaredFragmentOutputs(),
          meta.getDeclaredFragmentParameters(),
          versions);

      return CompactedFragmentShader.newShader(new_meta, new_sources);
    } catch (final NoSuchAlgorithmException e) {
      throw new GCompactorException(e);
    } catch (final IOException e) {
      throw new GCompactorException(e);
    }
  }

  /**
   * Compact a serialized vertex shader.
   * 
   * @param v
   *          The shader.
   * @return A compacted shader.
   * @throws GCompactorException
   *           If an error occurs during compaction.
   */

  public static CompactedVertexShader compactSerializedVertexShader(
    final UncompactedVertexShader v,
    final LogUsableType in_log)
    throws GCompactorException
  {
    try {
      final UncompactedVertexShaderMeta meta = v.getMeta();
      final Map<GVersionType, SourceLines> sources = v.getSources();

      final Map<String, SourceLines> new_sources =
        new HashMap<String, SourceLines>();
      final Map<GVersionType, String> versions =
        new HashMap<GVersionType, String>();

      for (final GVersionType version : sources.keySet()) {
        final SourceLines lines = sources.get(version);
        final SourceLines new_lines =
          SourceLines.newSourceStripped(lines.getLines(), in_log);

        final String hash = new_lines.getHash();
        new_sources.put(hash, new_lines);
        versions.put(version, hash);
      }

      final CompactedVertexShaderMeta new_meta =
        CompactedVertexShaderMeta.newMetadata(
          meta.getName(),
          meta.getSupportsES(),
          meta.getSupportsFull(),
          meta.getDeclaredVertexInputs(),
          meta.getDeclaredVertexOutputs(),
          meta.getDeclaredVertexParameters(),
          versions);

      return CompactedVertexShader.newShader(new_meta, new_sources);
    } catch (final NoSuchAlgorithmException e) {
      throw new GCompactorException(e);
    } catch (final IOException e) {
      throw new GCompactorException(e);
    }
  }

  private GCompactor()
  {
    throw new UnreachableCodeException();
  }
}
