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

import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragmentLocalDiscard;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragmentLocalValue;

/**
 * The type of fragment shader local visitors.
 * 
 * @param <L>
 *          The type of returned values
 * @param <E>
 *          The type of exceptions raised
 */

public interface TASTFragmentShaderLocalVisitorType<L, E extends Throwable>
{
  /**
   * Visit a discard statement.
   * 
   * @param d
   *          The statement
   * @return A value of <code>L</code>
   * @throws E
   *           If required
   */

  L fragmentShaderVisitLocalDiscard(
    final TASTDShaderFragmentLocalDiscard d)
    throws E;

  /**
   * Visit a value statement.
   * 
   * @param v
   *          The statement
   * @return A value of <code>L</code>
   * @throws E
   *           If required
   */

  L fragmentShaderVisitLocalValue(
    final TASTDShaderFragmentLocalValue v)
    throws E;
}
