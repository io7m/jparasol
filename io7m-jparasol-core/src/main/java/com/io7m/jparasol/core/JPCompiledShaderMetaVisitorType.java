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

/**
 * The type of generic compiled shader meta visitors.
 * 
 * @param <A>
 *          The type of returned values.
 * @param <E>
 *          The type of raised exceptions.
 */

public interface JPCompiledShaderMetaVisitorType<A, E extends Exception>
{
  /**
   * Visit compacted fragment shader metadata.
   * 
   * @param m
   *          The metadata.
   * @return A value of <code>A</code>.
   * @throws E
   *           If required.
   */

  A compactedFragment(
    final JPCompactedFragmentShaderMeta m)
    throws E;

  /**
   * Visit compacted vertex shader metadata.
   * 
   * @param m
   *          The metadata.
   * @return A value of <code>A</code>.
   * @throws E
   *           If required.
   */

  A compactedVertex(
    final JPCompactedVertexShaderMeta m)
    throws E;

  /**
   * Visit uncompacted fragment shader metadata.
   * 
   * @param m
   *          The metadata.
   * @return A value of <code>A</code>.
   * @throws E
   *           If required.
   */

  A uncompactedFragment(
    final JPUncompactedFragmentShaderMeta m)
    throws E;

  /**
   * Visit uncompacted program shader metadata.
   * 
   * @param m
   *          The metadata.
   * @return A value of <code>A</code>.
   * @throws E
   *           If required.
   */

  A uncompactedProgram(
    final JPUncompactedProgramShaderMeta m)
    throws E;

  /**
   * Visit uncompacted vertex shader metadata.
   * 
   * @param m
   *          The metadata.
   * @return A value of <code>A</code>.
   * @throws E
   *           If required.
   */

  A uncompactedVertex(
    final JPUncompactedVertexShaderMeta m)
    throws E;
}
