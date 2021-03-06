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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.io7m.jequality.annotations.EqualityStructural;
import com.io7m.jfunctional.Pair;
import com.io7m.jlog.LogUsableType;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jparasol.core.GVersion;
import com.io7m.jparasol.core.GVersionES;
import com.io7m.jparasol.core.GVersionFull;
import com.io7m.jparasol.core.GVersionType;
import com.io7m.jparasol.core.JPSourceLines;
import com.io7m.jparasol.core.JPUncompactedVertexShader;
import com.io7m.jparasol.core.JPUncompactedVertexShaderMeta;
import com.io7m.jparasol.core.JPVertexInput;
import com.io7m.jparasol.core.JPVertexOutput;
import com.io7m.jparasol.core.JPVertexParameter;
import com.io7m.jparasol.glsl.GLSLTypeNames;
import com.io7m.jparasol.glsl.GWriter;
import com.io7m.jparasol.glsl.ast.GASTShader.GASTShaderVertex;
import com.io7m.jparasol.glsl.ast.GASTShader.GASTShaderVertexInput;
import com.io7m.jparasol.glsl.ast.GASTShader.GASTShaderVertexOutput;
import com.io7m.jparasol.glsl.ast.GASTShader.GASTShaderVertexParameter;
import com.io7m.jparasol.glsl.ast.GTypeName;
import com.io7m.jparasol.typed.TType;
import com.io7m.jparasol.typed.ast.TASTShaderNameFlat;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * A compiled vertex shader.
 */

@EqualityStructural public final class GCompiledVertexShader
{
  /**
   * Construct a compiled vertex shader.
   * 
   * @param name
   *          The name.
   * @param sources
   *          The sources.
   * @return A compiled vertex shader.
   */

  public static GCompiledVertexShader newShader(
    final TASTShaderNameFlat name,
    final Map<GVersionType, GASTShaderVertex> sources)
  {
    return new GCompiledVertexShader(name, sources);
  }

  private final TASTShaderNameFlat                  name;
  private final Map<GVersionType, GASTShaderVertex> sources;

  private GCompiledVertexShader(
    final TASTShaderNameFlat in_name,
    final Map<GVersionType, GASTShaderVertex> in_sources)
  {
    this.name = NullCheck.notNull(in_name, "Name");
    this.sources = NullCheck.notNull(in_sources, "Sources");
  }

  @Override public boolean equals(
    @Nullable final Object obj)
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
    final GCompiledVertexShader other = (GCompiledVertexShader) obj;
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
   */

  public JPUncompactedVertexShader flatten(
    final LogUsableType log)
  {
    try {
      final SortedSet<GVersionES> out_supports_es = new TreeSet<GVersionES>();
      final SortedSet<GVersionFull> out_supports_full =
        new TreeSet<GVersionFull>();

      final Set<GVersionType> versions = this.sources.keySet();
      assert versions != null;
      GVersion.filterVersions(versions, out_supports_es, out_supports_full);

      final GASTShaderVertex shader = this.sources.values().iterator().next();

      final SortedSet<JPVertexParameter> vertex_parameters =
        new TreeSet<JPVertexParameter>();

      for (final GASTShaderVertexParameter p : shader.getParameters()) {
        for (final Pair<String, TType> x : p.getExpanded()) {
          final String p_name = x.getLeft();
          final GTypeName p_type = GLSLTypeNames.getTypeName(x.getRight());
          vertex_parameters.add(JPVertexParameter.newParameter(
            p_name,
            p_type.show()));
        }
      }

      final SortedSet<JPVertexInput> vertex_inputs =
        new TreeSet<JPVertexInput>();
      for (final GASTShaderVertexInput i : shader.getInputs()) {
        final String i_name = i.getName().show();
        final String i_type = i.getType().show();
        vertex_inputs.add(JPVertexInput.newInput(i_name, i_type));
      }

      final SortedSet<JPVertexOutput> vertex_outputs =
        new TreeSet<JPVertexOutput>();
      for (final GASTShaderVertexOutput o : shader.getOutputs()) {
        final String o_name = o.getName().show();
        final String o_type = GLSLTypeNames.getTypeName(o.getType()).show();
        vertex_outputs.add(JPVertexOutput.newOutput(o_name, o_type));
      }

      final JPUncompactedVertexShaderMeta in_meta =
        JPUncompactedVertexShaderMeta.newMetadata(
          this.name.show(),
          out_supports_es,
          out_supports_full,
          vertex_inputs,
          vertex_outputs,
          vertex_parameters);

      final Map<GVersionType, List<String>> in_sources =
        new HashMap<GVersionType, List<String>>();

      for (final GVersionType v : versions) {
        final GASTShaderVertex source = this.sources.get(v);
        assert source != null;

        final ByteArrayOutputStream stream = new ByteArrayOutputStream(16384);
        GWriter.writeVertexShader(stream, source, true);
        final ByteArrayInputStream input =
          new ByteArrayInputStream(stream.toByteArray());
        final List<String> lines = JPSourceLines.fromStream(input);
        in_sources.put(v, lines);
      }

      return JPUncompactedVertexShader.newShader(in_meta, in_sources);
    } catch (final IOException e) {
      throw new UnreachableCodeException(e);
    }
  }

  /**
   * @return The fully-qualified name of the vertex shader.
   */

  public TASTShaderNameFlat getName()
  {
    return this.name;
  }

  /**
   * @return The sources by version.
   */

  public Map<GVersionType, GASTShaderVertex> getSources()
  {
    final Map<GVersionType, GASTShaderVertex> r =
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
    builder.append("[GCompiledVertexShader name=");
    builder.append(this.name);
    builder.append(" sources=");
    builder.append(this.sources);
    builder.append("]");
    final String r = builder.toString();
    assert r != null;
    return r;
  }
}
