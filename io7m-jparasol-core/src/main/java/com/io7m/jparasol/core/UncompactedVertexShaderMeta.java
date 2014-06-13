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

package com.io7m.jparasol.core;

import java.util.SortedSet;

import com.io7m.jequality.annotations.EqualityStructural;
import com.io7m.jfunctional.Option;
import com.io7m.jfunctional.OptionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * Metadata for a compiled vertex shader.
 */

@EqualityStructural public final class UncompactedVertexShaderMeta implements
  CompiledShaderMetaType
{
  /**
   * Construct a shader.
   * 
   * @param in_name
   *          The fully-qualified shader name.
   * @param in_supports_es
   *          The supported GLSL ES versions.
   * @param in_supports_full
   *          The supported GLSL versions.
   * @param in_vertex_inputs
   *          The declared inputs.
   * @param in_vertex_outputs
   *          The declared outputs.
   * @param in_vertex_parameters
   *          The declared parameters.
   * @return A shader.
   */

  public static UncompactedVertexShaderMeta newMetadata(
    final String in_name,
    final SortedSet<GVersionES> in_supports_es,
    final SortedSet<GVersionFull> in_supports_full,
    final SortedSet<VertexInput> in_vertex_inputs,
    final SortedSet<VertexOutput> in_vertex_outputs,
    final SortedSet<VertexParameter> in_vertex_parameters)
  {
    return new UncompactedVertexShaderMeta(
      in_name,
      in_supports_es,
      in_supports_full,
      in_vertex_inputs,
      in_vertex_outputs,
      in_vertex_parameters);
  }

  private final String                     name;
  private final SortedSet<GVersionES>      supports_es;
  private final SortedSet<GVersionFull>    supports_full;
  private final SortedSet<VertexInput>     vertex_inputs;
  private final SortedSet<VertexOutput>    vertex_outputs;
  private final SortedSet<VertexParameter> vertex_parameters;

  private UncompactedVertexShaderMeta(
    final String in_name,
    final SortedSet<GVersionES> in_supports_es,
    final SortedSet<GVersionFull> in_supports_full,
    final SortedSet<VertexInput> in_vertex_inputs,
    final SortedSet<VertexOutput> in_vertex_outputs,
    final SortedSet<VertexParameter> in_vertex_parameters)
  {
    this.name = NullCheck.notNull(in_name, "Name");
    this.supports_es =
      NullCheck.notNullAll(in_supports_es, "GLSL ES versions");
    this.supports_full =
      NullCheck.notNullAll(in_supports_full, "GLSL versions");
    this.vertex_inputs =
      NullCheck.notNullAll(in_vertex_inputs, "Vertex inputs");
    this.vertex_outputs =
      NullCheck.notNullAll(in_vertex_outputs, "Vertex outputs");
    this.vertex_parameters =
      NullCheck.notNullAll(in_vertex_parameters, "Vertex parameters");
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
    final UncompactedVertexShaderMeta other =
      (UncompactedVertexShaderMeta) obj;
    return this.supports_es.equals(other.supports_es)
      && this.supports_full.equals(other.supports_full)
      && this.vertex_inputs.equals(other.vertex_inputs)
      && this.vertex_outputs.equals(other.vertex_outputs)
      && this.vertex_parameters.equals(other.vertex_parameters);
  }

  /**
   * @return The set of vertex shader inputs that were declared in the given
   *         program.
   */

  public SortedSet<VertexInput> getDeclaredVertexInputs()
  {
    return this.vertex_inputs;
  }

  /**
   * @return The set of vertex shader outputs that were declared in the given
   *         program.
   */

  public SortedSet<VertexOutput> getDeclaredVertexOutputs()
  {
    return this.vertex_outputs;
  }

  /**
   * @return The set of vertex shader parameters that were declared in the
   *         given program.
   */

  public SortedSet<VertexParameter> getDeclaredVertexParameters()
  {
    return this.vertex_parameters;
  }

  @Override public String getName()
  {
    return this.name;
  }

  @Override public SortedSet<GVersionES> getSupportsES()
  {
    return this.supports_es;
  }

  @Override public SortedSet<GVersionFull> getSupportsFull()
  {
    return this.supports_full;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.name.hashCode();
    result = (prime * result) + this.supports_es.hashCode();
    result = (prime * result) + this.supports_full.hashCode();
    result = (prime * result) + this.vertex_inputs.hashCode();
    result = (prime * result) + this.vertex_outputs.hashCode();
    result = (prime * result) + this.vertex_parameters.hashCode();
    return result;
  }

  @SuppressWarnings({ "boxing", "synthetic-access" }) @Override public
    OptionType<String>
    getSourceCodeFilename(
      final GVersionType v)
  {
    return v
      .versionAccept(new GVersionVisitorType<OptionType<String>, UnreachableCodeException>() {
        @Override public OptionType<String> versionVisitES(
          final GVersionES ve)
        {
          if (UncompactedVertexShaderMeta.this.supports_es.contains(ve)) {
            final String r =
              String.format(
                "%s-%s.v",
                v.versionGetAPIName(),
                v.versionGetNumber());
            assert r != null;
            return Option.some(r);
          }
          return Option.none();
        }

        @Override public OptionType<String> versionVisitFull(
          final GVersionFull vf)
        {
          if (UncompactedVertexShaderMeta.this.supports_full.contains(vf)) {
            final String r =
              String.format(
                "%s-%s.v",
                v.versionGetAPIName(),
                v.versionGetNumber());
            assert r != null;
            return Option.some(r);
          }
          return Option.none();
        }
      });
  }
}
