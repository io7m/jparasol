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

import java.util.Collections;
import java.util.Map;
import java.util.SortedSet;

import com.io7m.jequality.annotations.EqualityStructural;
import com.io7m.jfunctional.Option;
import com.io7m.jfunctional.OptionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;

/**
 * Metadata for a compiled vertex shader.
 */

@EqualityStructural public final class JPCompactedVertexShaderMeta implements
  JPVertexShaderMetaType
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
   * @param in_version_to_hash
   *          A map from GLSL versions to file hashes.
   * 
   * @return Metadata.
   * @throws JPMissingHash
   *           If one or more versions are missing an associated hash value.
   */

  public static JPCompactedVertexShaderMeta newMetadata(
    final String in_name,
    final SortedSet<GVersionES> in_supports_es,
    final SortedSet<GVersionFull> in_supports_full,
    final SortedSet<JPVertexInput> in_vertex_inputs,
    final SortedSet<JPVertexOutput> in_vertex_outputs,
    final SortedSet<JPVertexParameter> in_vertex_parameters,
    final Map<GVersionType, String> in_version_to_hash)
    throws JPMissingHash
  {
    return new JPCompactedVertexShaderMeta(
      in_name,
      in_supports_es,
      in_supports_full,
      in_vertex_inputs,
      in_vertex_outputs,
      in_vertex_parameters,
      in_version_to_hash);
  }

  private final String                       name;
  private final SortedSet<GVersionES>        supports_es;
  private final SortedSet<GVersionFull>      supports_full;
  private final Map<GVersionType, String>    version_to_hash;
  private final SortedSet<JPVertexInput>     vertex_inputs;
  private final SortedSet<JPVertexOutput>    vertex_outputs;
  private final SortedSet<JPVertexParameter> vertex_parameters;

  private JPCompactedVertexShaderMeta(
    final String in_name,
    final SortedSet<GVersionES> in_supports_es,
    final SortedSet<GVersionFull> in_supports_full,
    final SortedSet<JPVertexInput> in_vertex_inputs,
    final SortedSet<JPVertexOutput> in_vertex_outputs,
    final SortedSet<JPVertexParameter> in_vertex_parameters,
    final Map<GVersionType, String> in_version_to_hash)
    throws JPMissingHash
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
    this.version_to_hash =
      NullCheck.notNull(in_version_to_hash, "Version to hash");

    JPVersionsHash.checkComplete(
      in_supports_es,
      in_supports_full,
      in_version_to_hash);
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
    final JPCompactedVertexShaderMeta other =
      (JPCompactedVertexShaderMeta) obj;
    return this.vertex_inputs.equals(other.vertex_inputs)
      && this.vertex_outputs.equals(other.vertex_outputs)
      && this.vertex_parameters.equals(other.vertex_parameters)
      && this.name.equals(other.name)
      && this.supports_es.equals(other.supports_es)
      && this.supports_full.equals(other.supports_full)
      && this.version_to_hash.equals(other.version_to_hash);
  }

  /**
   * @return The set of vertex shader inputs that were declared in the given
   *         program.
   */

  public SortedSet<JPVertexInput> getDeclaredVertexInputs()
  {
    return this.vertex_inputs;
  }

  /**
   * @return The set of vertex shader outputs that were declared in the given
   *         program.
   */

  public SortedSet<JPVertexOutput> getDeclaredVertexOutputs()
  {
    return this.vertex_outputs;
  }

  /**
   * @return The set of vertex shader parameters that were declared in the
   *         given program.
   */

  public SortedSet<JPVertexParameter> getDeclaredVertexParameters()
  {
    return this.vertex_parameters;
  }

  @Override public String getName()
  {
    return this.name;
  }

  @Override public OptionType<String> getSourceCodeFilename(
    final GVersionType v)
  {
    if (this.version_to_hash.containsKey(v) == false) {
      return Option.none();
    }

    final String h = this.version_to_hash.get(v);
    assert h != null;
    final String r = String.format("%s.v", h);
    assert r != null;
    return Option.some(r);
  }

  @Override public SortedSet<GVersionES> getSupportsES()
  {
    return this.supports_es;
  }

  @Override public SortedSet<GVersionFull> getSupportsFull()
  {
    return this.supports_full;
  }

  /**
   * @return A read-only view of the mapping from versions to hashes.
   */

  public Map<GVersionType, String> getVersionToHash()
  {
    final Map<GVersionType, String> r =
      Collections.unmodifiableMap(this.version_to_hash);
    assert r != null;
    return r;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.vertex_inputs.hashCode();
    result = (prime * result) + this.vertex_outputs.hashCode();
    result = (prime * result) + this.vertex_parameters.hashCode();
    result = (prime * result) + this.name.hashCode();
    result = (prime * result) + this.supports_es.hashCode();
    result = (prime * result) + this.supports_full.hashCode();
    result = (prime * result) + this.version_to_hash.hashCode();
    return result;
  }

  @Override public <A, E extends Exception> A matchMeta(
    final JPCompiledShaderMetaVisitorType<A, E> v)
    throws E
  {
    return v.compactedVertex(this);
  }

  @Override public <A, E extends Exception> A matchVertexMeta(
    final JPVertexShaderMetaVisitorType<A, E> v)
    throws E
  {
    return v.compacted(this);
  }

  @Override public boolean isCompacted()
  {
    return true;
  }
}
