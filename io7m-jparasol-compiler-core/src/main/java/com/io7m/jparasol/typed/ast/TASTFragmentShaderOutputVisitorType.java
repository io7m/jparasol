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

package com.io7m.jparasol.typed.ast;

import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragmentOutputData;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragmentOutputDepth;

/**
 * The type of fragment shader output visitors.
 * 
 * @param <A>
 *          The type of returned values
 * @param <E>
 *          The type of exceptions raised
 */

public interface TASTFragmentShaderOutputVisitorType<A, E extends Throwable>
{
  /**
   * Visit a data output.
   * 
   * @param d
   *          The output
   * @return A value of <code>A</code>
   * @throws E
   *           If required
   */

  A fragmentShaderVisitOutputData(
    final TASTDShaderFragmentOutputData d)
    throws E;

  /**
   * Visit a depth output.
   * 
   * @param v
   *          The output
   * @return A value of <code>A</code>
   * @throws E
   *           If required
   */

  A fragmentShaderVisitOutputDepth(
    final TASTDShaderFragmentOutputDepth v)
    throws E;
}
