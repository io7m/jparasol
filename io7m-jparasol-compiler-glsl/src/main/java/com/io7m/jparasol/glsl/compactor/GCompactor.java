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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jlog.LogLevel;
import com.io7m.jlog.LogUsableType;
import com.io7m.jparasol.core.GVersionType;
import com.io7m.jparasol.core.JPCompactedFragmentShader;
import com.io7m.jparasol.core.JPCompactedFragmentShaderMeta;
import com.io7m.jparasol.core.JPCompactedVertexShader;
import com.io7m.jparasol.core.JPCompactedVertexShaderMeta;
import com.io7m.jparasol.core.JPHashedLines;
import com.io7m.jparasol.core.JPMissingHash;
import com.io7m.jparasol.core.JPUncompactedFragmentShader;
import com.io7m.jparasol.core.JPUncompactedFragmentShaderMeta;
import com.io7m.jparasol.core.JPUncompactedVertexShader;
import com.io7m.jparasol.core.JPUncompactedVertexShaderMeta;
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

  public static JPCompactedFragmentShader compactSerializedFragmentShader(
    final JPUncompactedFragmentShader f,
    final LogUsableType in_log)
    throws GCompactorException
  {
    try {
      final JPUncompactedFragmentShaderMeta meta = f.getMeta();
      final Map<GVersionType, List<String>> sources = f.getSources();

      final Map<String, JPHashedLines> new_sources =
        new HashMap<String, JPHashedLines>();
      final Map<GVersionType, String> versions =
        new HashMap<GVersionType, String>();

      for (final GVersionType version : sources.keySet()) {
        final List<String> lines = sources.get(version);
        assert lines != null;

        final JPHashedLines new_lines =
          JPHashedLines.newSourceStripped(lines, in_log);

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

      final JPCompactedFragmentShaderMeta new_meta =
        JPCompactedFragmentShaderMeta.newMetadata(
          meta.getName(),
          meta.getSupportsES(),
          meta.getSupportsFull(),
          meta.getDeclaredFragmentInputs(),
          meta.getDeclaredFragmentOutputs(),
          meta.getDeclaredFragmentParameters(),
          versions);

      return JPCompactedFragmentShader.newShader(new_meta, new_sources);
    } catch (final IOException e) {
      throw new GCompactorException(e);
    } catch (final JPMissingHash e) {

      /**
       * The compactor should not lose versions! This would indicate a bug.
       */

      throw new UnreachableCodeException(e);
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

  public static JPCompactedVertexShader compactSerializedVertexShader(
    final JPUncompactedVertexShader v,
    final LogUsableType in_log)
    throws GCompactorException
  {
    try {
      final JPUncompactedVertexShaderMeta meta = v.getMeta();
      final Map<GVersionType, List<String>> sources = v.getSources();

      final Map<String, JPHashedLines> new_sources =
        new HashMap<String, JPHashedLines>();
      final Map<GVersionType, String> versions =
        new HashMap<GVersionType, String>();

      for (final GVersionType version : sources.keySet()) {
        final List<String> lines = sources.get(version);
        assert lines != null;

        final JPHashedLines new_lines =
          JPHashedLines.newSourceStripped(lines, in_log);

        final String hash = new_lines.getHash();
        new_sources.put(hash, new_lines);
        versions.put(version, hash);
      }

      final JPCompactedVertexShaderMeta new_meta =
        JPCompactedVertexShaderMeta.newMetadata(
          meta.getName(),
          meta.getSupportsES(),
          meta.getSupportsFull(),
          meta.getDeclaredVertexInputs(),
          meta.getDeclaredVertexOutputs(),
          meta.getDeclaredVertexParameters(),
          versions);

      return JPCompactedVertexShader.newShader(new_meta, new_sources);
    } catch (final IOException e) {
      throw new GCompactorException(e);
    } catch (final JPMissingHash e) {

      /**
       * The compactor should not lose versions! This would indicate a bug.
       */

      throw new UnreachableCodeException(e);
    }
  }

  private GCompactor()
  {
    throw new UnreachableCodeException();
  }
}
