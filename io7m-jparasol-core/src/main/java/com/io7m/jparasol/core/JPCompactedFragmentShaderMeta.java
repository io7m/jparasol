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
import java.util.SortedMap;
import java.util.SortedSet;

import com.io7m.jequality.annotations.EqualityStructural;
import com.io7m.jfunctional.Option;
import com.io7m.jfunctional.OptionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;

/**
 * Metadata for a compiled fragment shader.
 */

@EqualityStructural public final class JPCompactedFragmentShaderMeta implements
  JPCompiledShaderMetaType,
  JPFragmentShaderMetaType
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
   * @param in_fragment_inputs
   *          The declared inputs.
   * @param in_fragment_outputs
   *          The declared outputs.
   * @param in_fragment_parameters
   *          The declared parameters.
   * @param in_version_to_hash
   *          A map from GLSL versions to file hashes.
   * 
   * @return Metadata.
   * @throws JPMissingHash
   *           If one or more versions are missing an associated hash value.
   */

  public static JPCompactedFragmentShaderMeta newMetadata(
    final String in_name,
    final SortedSet<GVersionES> in_supports_es,
    final SortedSet<GVersionFull> in_supports_full,
    final SortedSet<JPFragmentInput> in_fragment_inputs,
    final SortedMap<Integer, JPFragmentOutput> in_fragment_outputs,
    final SortedSet<JPFragmentParameter> in_fragment_parameters,
    final Map<GVersionType, String> in_version_to_hash)
    throws JPMissingHash
  {
    return new JPCompactedFragmentShaderMeta(
      in_name,
      in_supports_es,
      in_supports_full,
      in_fragment_inputs,
      in_fragment_outputs,
      in_fragment_parameters,
      in_version_to_hash);
  }

  private final SortedSet<JPFragmentInput>           fragment_inputs;
  private final SortedMap<Integer, JPFragmentOutput> fragment_outputs;
  private final SortedSet<JPFragmentParameter>       fragment_parameters;
  private final String                               name;
  private final SortedSet<GVersionES>                supports_es;
  private final SortedSet<GVersionFull>              supports_full;
  private final Map<GVersionType, String>            version_to_hash;

  private JPCompactedFragmentShaderMeta(
    final String in_name,
    final SortedSet<GVersionES> in_supports_es,
    final SortedSet<GVersionFull> in_supports_full,
    final SortedSet<JPFragmentInput> in_fragment_inputs,
    final SortedMap<Integer, JPFragmentOutput> in_fragment_outputs,
    final SortedSet<JPFragmentParameter> in_fragment_parameters,
    final Map<GVersionType, String> in_version_to_hash)
    throws JPMissingHash
  {
    this.name = NullCheck.notNull(in_name, "Name");
    this.supports_es =
      NullCheck.notNullAll(in_supports_es, "GLSL ES versions");
    this.supports_full =
      NullCheck.notNullAll(in_supports_full, "GLSL versions");
    this.fragment_inputs =
      NullCheck.notNullAll(in_fragment_inputs, "Fragment inputs");
    this.fragment_outputs =
      NullCheck.notNull(in_fragment_outputs, "Fragment outputs");
    this.fragment_parameters =
      NullCheck.notNullAll(in_fragment_parameters, "Fragment parameters");
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
    final JPCompactedFragmentShaderMeta other =
      (JPCompactedFragmentShaderMeta) obj;
    return this.fragment_inputs.equals(other.fragment_inputs)
      && this.fragment_outputs.equals(other.fragment_outputs)
      && this.fragment_parameters.equals(other.fragment_parameters)
      && this.name.equals(other.name)
      && this.supports_es.equals(other.supports_es)
      && this.supports_full.equals(other.supports_full)
      && this.version_to_hash.equals(other.version_to_hash);
  }

  /**
   * @return The set of fragment shader inputs that were declared in the given
   *         program.
   */

  public SortedSet<JPFragmentInput> getDeclaredFragmentInputs()
  {
    return this.fragment_inputs;
  }

  /**
   * @return The set of fragment shader outputs that were declared in the
   *         given program.
   */

  public SortedMap<Integer, JPFragmentOutput> getDeclaredFragmentOutputs()
  {
    return this.fragment_outputs;
  }

  /**
   * @return The set of fragment shader parameters that were declared in the
   *         given program.
   */

  public SortedSet<JPFragmentParameter> getDeclaredFragmentParameters()
  {
    return this.fragment_parameters;
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
    final String r = String.format("%s.f", h);
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
    result = (prime * result) + this.fragment_inputs.hashCode();
    result = (prime * result) + this.fragment_outputs.hashCode();
    result = (prime * result) + this.fragment_parameters.hashCode();
    result = (prime * result) + this.name.hashCode();
    result = (prime * result) + this.supports_es.hashCode();
    result = (prime * result) + this.supports_full.hashCode();
    result = (prime * result) + this.version_to_hash.hashCode();
    return result;
  }

  @Override public <A, E extends Exception> A matchFragmentMeta(
    final JPFragmentShaderMetaVisitorType<A, E> v)
    throws E
  {
    return v.compacted(this);
  }

  @Override public <A, E extends Exception> A matchMeta(
    final JPCompiledShaderMetaVisitorType<A, E> v)
    throws E
  {
    return v.compactedFragment(this);
  }
}
