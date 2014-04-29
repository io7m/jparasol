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

import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderVertexLocalValue;

/**
 * The type of local visitors.
 * 
 * @param <L>
 *          The type of transformed shader locals
 * @param <E>
 *          The type of exceptions raised
 */

public interface TASTVertexShaderLocalVisitorType<L, E extends Throwable>
{
  /**
   * Visit a local.
   * 
   * @param v
   *          The local
   * @return A transformed local
   * @throws E
   *           If required
   */

  L vertexShaderVisitLocalValue(
    final TASTDShaderVertexLocalValue v)
    throws E;
}
