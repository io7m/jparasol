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

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jfunctional.OptionType;
import com.io7m.jnull.NullCheck;

/**
 * A compacted vertex shader.
 */

@EqualityReference public final class JPCompactedVertexShader implements
  JPCompactedShaderType,
  JPVertexShaderType
{
  /**
   * Construct a shader.
   * 
   * @param in_meta
   *          The metadata.
   * @param in_sources
   *          The sources, by hash.
   * @return A shader.
   */

  public static JPCompactedVertexShader newShader(
    final JPCompactedVertexShaderMeta in_meta,
    final Map<String, JPHashedLines> in_sources)
  {
    return new JPCompactedVertexShader(in_meta, in_sources);
  }

  private final JPCompactedVertexShaderMeta meta;
  private final Map<String, JPHashedLines>  sources_hash;

  private JPCompactedVertexShader(
    final JPCompactedVertexShaderMeta in_meta,
    final Map<String, JPHashedLines> in_sources_hash)
  {
    this.meta = NullCheck.notNull(in_meta, "Metadata");
    this.sources_hash = NullCheck.notNull(in_sources_hash, "Sources by hash");
  }

  /**
   * @return The metadata.
   */

  public JPCompactedVertexShaderMeta getMeta()
  {
    return this.meta;
  }

  @Override public String getName()
  {
    return this.meta.getName();
  }

  @Override public OptionType<String> getSourceCodeFilename(
    final GVersionType v)
  {
    return this.meta.getSourceCodeFilename(v);
  }

  @Override public Map<String, JPHashedLines> getSourcesByHash()
  {
    final Map<String, JPHashedLines> r =
      Collections.unmodifiableMap(this.sources_hash);
    assert r != null;
    return r;
  }

  @Override public SortedSet<GVersionES> getSupportsES()
  {
    return this.meta.getSupportsES();
  }

  @Override public SortedSet<GVersionFull> getSupportsFull()
  {
    return this.meta.getSupportsFull();
  }

  @Override public boolean isCompacted()
  {
    return this.meta.isCompacted();
  }

  @Override public <A, E extends Exception> A matchCompiledShader(
    final JPCompiledShaderVisitorType<A, E> v)
    throws E
  {
    return v.compacted(this);
  }

  @Override public <A, E extends Exception> A matchMeta(
    final JPCompiledShaderMetaVisitorType<A, E> v)
    throws E
  {
    return v.compactedVertex(this.meta);
  }

  @Override public <A, E extends Exception> A matchVertex(
    final JPVertexShaderVisitorType<A, E> v)
    throws E
  {
    return v.compacted(this);
  }
}
