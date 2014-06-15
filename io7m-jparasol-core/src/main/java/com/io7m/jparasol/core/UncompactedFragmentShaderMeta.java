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

import java.util.SortedMap;
import java.util.SortedSet;

import com.io7m.jequality.annotations.EqualityStructural;
import com.io7m.jfunctional.Option;
import com.io7m.jfunctional.OptionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * Metadata for a compiled fragment shader.
 */

@EqualityStructural public final class UncompactedFragmentShaderMeta implements
  CompiledShaderMetaType,
  FragmentShaderMetaType
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
   * @return A shader.
   */

  public static UncompactedFragmentShaderMeta newMetadata(
    final String in_name,
    final SortedSet<GVersionES> in_supports_es,
    final SortedSet<GVersionFull> in_supports_full,
    final SortedSet<FragmentInput> in_fragment_inputs,
    final SortedMap<Integer, FragmentOutput> in_fragment_outputs,
    final SortedSet<FragmentParameter> in_fragment_parameters)
  {
    return new UncompactedFragmentShaderMeta(
      in_name,
      in_supports_es,
      in_supports_full,
      in_fragment_inputs,
      in_fragment_outputs,
      in_fragment_parameters);
  }

  private final SortedSet<FragmentInput>           fragment_inputs;

  private final SortedMap<Integer, FragmentOutput> fragment_outputs;

  private final SortedSet<FragmentParameter>       fragment_parameters;
  private final String                             name;
  private final SortedSet<GVersionES>              supports_es;
  private final SortedSet<GVersionFull>            supports_full;

  private UncompactedFragmentShaderMeta(
    final String in_name,
    final SortedSet<GVersionES> in_supports_es,
    final SortedSet<GVersionFull> in_supports_full,
    final SortedSet<FragmentInput> in_fragment_inputs,
    final SortedMap<Integer, FragmentOutput> in_fragment_outputs,
    final SortedSet<FragmentParameter> in_fragment_parameters)
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
    final UncompactedFragmentShaderMeta other =
      (UncompactedFragmentShaderMeta) obj;
    return this.fragment_inputs.equals(other.fragment_inputs)
      && this.fragment_outputs.equals(other.fragment_outputs)
      && this.fragment_parameters.equals(other.fragment_parameters)
      && this.name.equals(other.name)
      && this.supports_es.equals(other.supports_es)
      && this.supports_full.equals(other.supports_full);
  }

  /**
   * @return The set of fragment shader inputs that were declared in the given
   *         program.
   */

  public SortedSet<FragmentInput> getDeclaredFragmentInputs()
  {
    return this.fragment_inputs;
  }

  /**
   * @return The set of fragment shader outputs that were declared in the
   *         given program.
   */

  public SortedMap<Integer, FragmentOutput> getDeclaredFragmentOutputs()
  {
    return this.fragment_outputs;
  }

  /**
   * @return The set of fragment shader parameters that were declared in the
   *         given program.
   */

  public SortedSet<FragmentParameter> getDeclaredFragmentParameters()
  {
    return this.fragment_parameters;
  }

  @Override public String getName()
  {
    return this.name;
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
          if (UncompactedFragmentShaderMeta.this.supports_es.contains(ve)) {
            final String r =
              String.format(
                "%s-%s.f",
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
          if (UncompactedFragmentShaderMeta.this.supports_full.contains(vf)) {
            final String r =
              String.format(
                "%s-%s.f",
                v.versionGetAPIName(),
                v.versionGetNumber());
            assert r != null;
            return Option.some(r);
          }
          return Option.none();
        }
      });
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
    result = (prime * result) + this.fragment_inputs.hashCode();
    result = (prime * result) + this.fragment_outputs.hashCode();
    result = (prime * result) + this.fragment_parameters.hashCode();
    result = (prime * result) + this.name.hashCode();
    result = (prime * result) + this.supports_es.hashCode();
    result = (prime * result) + this.supports_full.hashCode();
    return result;
  }

  @Override public <A, E extends Exception> A matchMeta(
    final CompiledShaderMetaVisitorType<A, E> v)
    throws E
  {
    return v.uncompactedFragment(this);
  }

  @Override public <A, E extends Exception> A matchFragmentMeta(
    final FragmentShaderMetaVisitorType<A, E> v)
    throws E
  {
    return v.uncompacted(this);
  }
}
