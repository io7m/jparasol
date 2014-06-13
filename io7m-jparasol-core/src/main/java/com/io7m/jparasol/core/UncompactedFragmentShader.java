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

import java.util.Map;
import java.util.SortedSet;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jfunctional.OptionType;
import com.io7m.jnull.NullCheck;

/**
 * A compiled fragment shader.
 */

@EqualityReference public final class UncompactedFragmentShader implements
  UncompactedShaderType
{
  /**
   * Construct a shader.
   * 
   * @param in_meta
   *          The shader metadata.
   * @param in_sources
   *          The sources, by version.
   * @return A shader.
   */

  public static UncompactedFragmentShader newShader(
    final UncompactedFragmentShaderMeta in_meta,
    final Map<GVersionType, SourceLines> in_sources)
  {
    return new UncompactedFragmentShader(in_meta, in_sources);
  }

  private final UncompactedFragmentShaderMeta  meta;
  private final Map<GVersionType, SourceLines> sources;

  private UncompactedFragmentShader(
    final UncompactedFragmentShaderMeta in_meta,
    final Map<GVersionType, SourceLines> in_sources)
  {
    this.meta = NullCheck.notNull(in_meta, "Metadata");
    this.sources = NullCheck.notNull(in_sources, "Sources");
  }

  /**
   * @return The shader metadata.
   */

  public UncompactedFragmentShaderMeta getMeta()
  {
    return this.meta;
  }

  @Override public String getName()
  {
    return this.meta.getName();
  }

  /**
   * @return A read-only map of the shader sources, by version.
   */

  public Map<GVersionType, SourceLines> getSources()
  {
    return this.sources;
  }

  @Override public SortedSet<GVersionES> getSupportsES()
  {
    return this.meta.getSupportsES();
  }

  @Override public SortedSet<GVersionFull> getSupportsFull()
  {
    return this.meta.getSupportsFull();
  }

  @Override public <A, E extends Exception> A matchCompiledShader(
    final CompiledShaderVisitorType<A, E> v)
    throws E
  {
    return v.uncompacted(this);
  }

  @Override public <A, E extends Exception> A matchUncompactedShader(
    final UncompactedShaderVisitorType<A, E> v)
    throws E
  {
    return v.fragmentShader(this);
  }

  @Override public OptionType<String> getSourceCodeFilename(
    final GVersionType v)
  {
    return this.meta.getSourceCodeFilename(v);
  }
}
