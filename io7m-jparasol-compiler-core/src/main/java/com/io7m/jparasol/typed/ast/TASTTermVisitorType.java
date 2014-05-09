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

import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDFunctionDefined;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDFunctionExternal;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDValueDefined;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDValueExternal;

/**
 * The type of term visitors.
 * 
 * @param <T>
 *          The type of transformed terms
 * @param <E>
 *          The type of exceptions raised
 */

public interface TASTTermVisitorType<T, E extends Throwable>
{
  /**
   * Visit a defined function.
   * 
   * @param f
   *          The function
   * @return A transformed function
   * @throws E
   *           If required
   */

  T termVisitFunctionDefined(
    final TASTDFunctionDefined f)
    throws E;

  /**
   * Visit an external function.
   * 
   * @param f
   *          The function
   * @return A transformed function
   * @throws E
   *           If required
   */

  T termVisitFunctionExternal(
    final TASTDFunctionExternal f)
    throws E;

  /**
   * Visit a defined value.
   * 
   * @param v
   *          The value
   * @return A transformed value
   * @throws E
   *           If required
   */

  T termVisitValueDefined(
    final TASTDValueDefined v)
    throws E;

  /**
   * Visit an external value.
   * 
   * @param v
   *          The value
   * @return A transformed value
   * @throws E
   *           If required
   */

  T termVisitValueExternal(
    final TASTDValueExternal v)
    throws E;
}
