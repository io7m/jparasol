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

package com.io7m.jparasol.typed;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * <p>
 * Determine whether or not two types are equal, for the purposes of type
 * checking.
 * </p>
 * <p>
 * Type equality in Parasol is nominal equality; two types are equal iff they
 * have the same name.
 * </p>
 */

@EqualityReference public final class TTypeEquality
{
  /**
   * @param ty0
   *          The first type
   * @param ty1
   *          The second type
   * @return <code>true</code> if types <code>t0</code> and <code>t1</code>
   *         are equal.
   */

  public static boolean typesAreEqual(
    final TType ty0,
    final TType ty1)
  {
    return ty0.getName().equals(ty1.getName());
  }

  private TTypeEquality()
  {
    throw new UnreachableCodeException();
  }
}
