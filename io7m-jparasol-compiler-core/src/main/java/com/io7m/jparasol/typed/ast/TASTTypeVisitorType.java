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

import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDTypeRecord;

/**
 * The type of type visitors.
 * 
 * @param <T>
 *          The type of transformed types
 * @param <E>
 *          The type of exceptions raised
 */

public interface TASTTypeVisitorType<T, E extends Throwable>
{
  /**
   * Visit a record type.
   * 
   * @param r
   *          The record type
   * @return A transformed record type
   * @throws E
   *           If required
   */

  T typeVisitTypeRecord(
    final TASTDTypeRecord r)
    throws E;
}
