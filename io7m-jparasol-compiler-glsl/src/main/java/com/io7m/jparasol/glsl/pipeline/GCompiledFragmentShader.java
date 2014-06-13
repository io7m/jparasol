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

package com.io7m.jparasol.glsl.pipeline;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.io7m.jequality.annotations.EqualityStructural;
import com.io7m.jfunctional.Pair;
import com.io7m.jlog.LogUsableType;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jparasol.core.FragmentInput;
import com.io7m.jparasol.core.FragmentOutput;
import com.io7m.jparasol.core.FragmentParameter;
import com.io7m.jparasol.core.GVersion;
import com.io7m.jparasol.core.GVersionES;
import com.io7m.jparasol.core.GVersionFull;
import com.io7m.jparasol.core.GVersionType;
import com.io7m.jparasol.core.SourceLines;
import com.io7m.jparasol.core.UncompactedFragmentShader;
import com.io7m.jparasol.core.UncompactedFragmentShaderMeta;
import com.io7m.jparasol.glsl.GLSLTypeNames;
import com.io7m.jparasol.glsl.GWriter;
import com.io7m.jparasol.glsl.ast.GASTShader.GASTShaderFragment;
import com.io7m.jparasol.glsl.ast.GASTShader.GASTShaderFragmentInput;
import com.io7m.jparasol.glsl.ast.GASTShader.GASTShaderFragmentOutput;
import com.io7m.jparasol.glsl.ast.GASTShader.GASTShaderFragmentParameter;
import com.io7m.jparasol.glsl.ast.GTypeName;
import com.io7m.jparasol.typed.TType;
import com.io7m.jparasol.typed.ast.TASTShaderNameFlat;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * A compiled fragment shader.
 */

@EqualityStructural public final class GCompiledFragmentShader
{
  /**
   * Construct a compiled fragment shader.
   * 
   * @param name
   *          The name.
   * @param sources
   *          The sources.
   * @return A compiled fragment shader.
   */

  public static GCompiledFragmentShader newShader(
    final TASTShaderNameFlat name,
    final Map<GVersionType, GASTShaderFragment> sources)
  {
    return new GCompiledFragmentShader(name, sources);
  }

  private final TASTShaderNameFlat                    name;
  private final Map<GVersionType, GASTShaderFragment> sources;

  private GCompiledFragmentShader(
    final TASTShaderNameFlat in_name,
    final Map<GVersionType, GASTShaderFragment> in_sources)
  {
    this.name = NullCheck.notNull(in_name, "Name");
    this.sources = NullCheck.notNull(in_sources, "Sources");
  }

  @Override public boolean equals(
    final @Nullable Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    final GCompiledFragmentShader other = (GCompiledFragmentShader) obj;
    return this.name.equals(other.name) && this.sources.equals(other.sources);
  }

  /**
   * Produce a flattened version of the given shader (a shader with the
   * majority of the semantic information stripped out, ready for
   * serialization).
   * 
   * @param log
   *          A log interface.
   * @return A flattened shader.
   * @throws NoSuchAlgorithmException
   *           If the current JVM lacks support for the SHA-256 hash function.
   */

  public UncompactedFragmentShader flatten(
    final LogUsableType log)
    throws NoSuchAlgorithmException
  {
    try {
      final SortedSet<GVersionES> out_supports_es = new TreeSet<GVersionES>();
      final SortedSet<GVersionFull> out_supports_full =
        new TreeSet<GVersionFull>();

      final Set<GVersionType> versions = this.sources.keySet();
      assert versions != null;
      GVersion.filterVersions(versions, out_supports_es, out_supports_full);

      final GASTShaderFragment shader =
        this.sources.values().iterator().next();

      final SortedSet<FragmentParameter> fragment_parameters =
        new TreeSet<FragmentParameter>();

      for (final GASTShaderFragmentParameter p : shader.getParameters()) {
        for (final Pair<String, TType> x : p.getExpanded()) {
          final String p_name = x.getLeft();
          final GTypeName p_type = GLSLTypeNames.getTypeName(x.getRight());
          fragment_parameters.add(FragmentParameter.newParameter(
            p_name,
            p_type.show()));
        }
      }

      final SortedSet<FragmentInput> fragment_inputs =
        new TreeSet<FragmentInput>();
      for (final GASTShaderFragmentInput i : shader.getInputs()) {
        final String i_name = i.getName().show();
        final String i_type = GLSLTypeNames.getTypeName(i.getType()).show();
        fragment_inputs.add(FragmentInput.newInput(i_name, i_type));
      }

      final SortedMap<Integer, FragmentOutput> fragment_outputs =
        new TreeMap<Integer, FragmentOutput>();
      for (final GASTShaderFragmentOutput o : shader.getOutputs()) {
        final String o_name = o.getName().show();
        final int o_index = o.getIndex();
        final String o_type = o.getType().show();
        final FragmentOutput ro =
          FragmentOutput.newOutput(o_name, o_index, o_type);
        fragment_outputs.put(o_index, ro);
      }

      final UncompactedFragmentShaderMeta in_meta =
        UncompactedFragmentShaderMeta.newMetadata(
          this.name.show(),
          out_supports_es,
          out_supports_full,
          fragment_inputs,
          fragment_outputs,
          fragment_parameters);

      final Map<GVersionType, SourceLines> in_sources =
        new HashMap<GVersionType, SourceLines>();

      for (final GVersionType v : versions) {
        final GASTShaderFragment source = this.sources.get(v);
        assert source != null;

        final ByteArrayOutputStream stream = new ByteArrayOutputStream(16384);
        GWriter.writeFragmentShader(stream, source, true);
        final ByteArrayInputStream input =
          new ByteArrayInputStream(stream.toByteArray());
        final SourceLines lines = SourceLines.fromStream(input, log);
        in_sources.put(v, lines);
      }

      return UncompactedFragmentShader.newShader(in_meta, in_sources);
    } catch (final IOException e) {
      throw new UnreachableCodeException(e);
    }
  }

  /**
   * @return The fully-qualified name of the fragment shader.
   */

  public TASTShaderNameFlat getName()
  {
    return this.name;
  }

  /**
   * @return The sources by version.
   */

  public Map<GVersionType, GASTShaderFragment> getSources()
  {
    final Map<GVersionType, GASTShaderFragment> r =
      Collections.unmodifiableMap(this.sources);
    assert r != null;
    return r;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.name.hashCode();
    result = (prime * result) + this.sources.hashCode();
    return result;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[GCompiledFragmentShader name=");
    builder.append(this.name);
    builder.append(" sources=");
    builder.append(this.sources);
    builder.append("]");
    final String r = builder.toString();
    assert r != null;
    return r;
  }
}
