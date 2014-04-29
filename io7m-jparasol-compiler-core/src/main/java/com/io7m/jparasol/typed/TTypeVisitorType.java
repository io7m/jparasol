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

import com.io7m.jparasol.typed.TType.TBoolean;
import com.io7m.jparasol.typed.TType.TFloat;
import com.io7m.jparasol.typed.TType.TFunction;
import com.io7m.jparasol.typed.TType.TInteger;
import com.io7m.jparasol.typed.TType.TMatrix3x3F;
import com.io7m.jparasol.typed.TType.TMatrix4x4F;
import com.io7m.jparasol.typed.TType.TRecord;
import com.io7m.jparasol.typed.TType.TSampler2D;
import com.io7m.jparasol.typed.TType.TSamplerCube;
import com.io7m.jparasol.typed.TType.TVector2F;
import com.io7m.jparasol.typed.TType.TVector2I;
import com.io7m.jparasol.typed.TType.TVector3F;
import com.io7m.jparasol.typed.TType.TVector3I;
import com.io7m.jparasol.typed.TType.TVector4F;
import com.io7m.jparasol.typed.TType.TVector4I;

/**
 * The type of type visitors.
 * 
 * @param <A>
 *          The type of returned values
 * @param <E>
 *          The type of exceptions raised
 */

public interface TTypeVisitorType<A, E extends Throwable>
{
  /**
   * Visit a type.
   * 
   * @return A value of type <code>A</code>
   * @throws E
   *           If required
   */

  A typeVisitBoolean(
    final TBoolean t)
    throws E;

  /**
   * Visit a type.
   * 
   * @return A value of type <code>A</code>
   * @throws E
   *           If required
   */

  A typeVisitFloat(
    final TFloat t)
    throws E;

  /**
   * Visit a type.
   * 
   * @return A value of type <code>A</code>
   * @throws E
   *           If required
   */

  A typeVisitFunction(
    final TFunction t)
    throws E;

  /**
   * Visit a type.
   * 
   * @return A value of type <code>A</code>
   * @throws E
   *           If required
   */

  A typeVisitInteger(
    final TInteger t)
    throws E;

  /**
   * Visit a type.
   * 
   * @return A value of type <code>A</code>
   * @throws E
   *           If required
   */

  A typeVisitMatrix3x3F(
    final TMatrix3x3F t)
    throws E;

  /**
   * Visit a type.
   * 
   * @return A value of type <code>A</code>
   * @throws E
   *           If required
   */

  A typeVisitMatrix4x4F(
    final TMatrix4x4F t)
    throws E;

  /**
   * Visit a type.
   * 
   * @return A value of type <code>A</code>
   * @throws E
   *           If required
   */

  A typeVisitRecord(
    final TRecord t)
    throws E;

  /**
   * Visit a type.
   * 
   * @return A value of type <code>A</code>
   * @throws E
   *           If required
   */

  A typeVisitSampler2D(
    final TSampler2D t)
    throws E;

  /**
   * Visit a type.
   * 
   * @return A value of type <code>A</code>
   * @throws E
   *           If required
   */

  A typeVisitSamplerCube(
    final TSamplerCube t)
    throws E;

  /**
   * Visit a type.
   * 
   * @return A value of type <code>A</code>
   * @throws E
   *           If required
   */

  A typeVisitVector2F(
    final TVector2F t)
    throws E;

  /**
   * Visit a type.
   * 
   * @return A value of type <code>A</code>
   * @throws E
   *           If required
   */

  A typeVisitVector2I(
    final TVector2I t)
    throws E;

  /**
   * Visit a type.
   * 
   * @return A value of type <code>A</code>
   * @throws E
   *           If required
   */

  A typeVisitVector3F(
    final TVector3F t)
    throws E;

  /**
   * Visit a type.
   * 
   * @return A value of type <code>A</code>
   * @throws E
   *           If required
   */

  A typeVisitVector3I(
    final TVector3I t)
    throws E;

  /**
   * Visit a type.
   * 
   * @return A value of type <code>A</code>
   * @throws E
   *           If required
   */

  A typeVisitVector4F(
    final TVector4F t)
    throws E;

  /**
   * Visit a type.
   * 
   * @return A value of type <code>A</code>
   * @throws E
   *           If required
   */

  A typeVisitVector4I(
    final TVector4I t)
    throws E;
}
