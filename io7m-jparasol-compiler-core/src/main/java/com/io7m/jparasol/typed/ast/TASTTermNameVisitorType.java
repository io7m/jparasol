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

import com.io7m.jparasol.typed.ast.TASTTermName.TASTTermNameExternal;
import com.io7m.jparasol.typed.ast.TASTTermName.TASTTermNameGlobal;
import com.io7m.jparasol.typed.ast.TASTTermName.TASTTermNameLocal;

/**
 * Visit a term name.
 * 
 * @param <A>
 *          The type of returned values
 * @param <E>
 *          The type of exceptions raised
 */

public interface TASTTermNameVisitorType<A, E extends Throwable>
{
  /**
   * Visit an external term name.
   * 
   * @param t
   *          The name
   * @return A value of type <code>A</code>
   * @throws E
   *           If required
   */

  A termNameVisitExternal(
    final TASTTermNameExternal t)
    throws E;

  /**
   * Visit a global term name.
   * 
   * @param t
   *          The name
   * @return A value of type <code>A</code>
   * @throws E
   *           If required
   */

  A termNameVisitGlobal(
    final TASTTermNameGlobal t)
    throws E;

  /**
   * Visit a local term name.
   * 
   * @param t
   *          The name
   * @return A value of type <code>A</code>
   * @throws E
   *           If required
   */

  A termNameVisitLocal(
    final TASTTermNameLocal t)
    throws E;
}
