/*
 * Copyright © 2013 <code@io7m.com> http://io7m.com
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

import javax.annotation.Nonnull;

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

public interface TTypeVisitor<A, E extends Throwable>
{
  public A typeVisitBoolean(
    final @Nonnull TBoolean t)
    throws E;

  public A typeVisitFloat(
    final @Nonnull TFloat t)
    throws E;

  public A typeVisitFunction(
    final @Nonnull TFunction t)
    throws E;

  public A typeVisitInteger(
    final @Nonnull TInteger t)
    throws E;

  public A typeVisitMatrix3x3F(
    final @Nonnull TMatrix3x3F t)
    throws E;

  public A typeVisitMatrix4x4F(
    final @Nonnull TMatrix4x4F t)
    throws E;

  public A typeVisitRecord(
    final @Nonnull TRecord t)
    throws E;

  public A typeVisitSampler2D(
    final @Nonnull TSampler2D t)
    throws E;

  public A typeVisitSamplerCube(
    final @Nonnull TSamplerCube t)
    throws E;

  public A typeVisitVector2F(
    final @Nonnull TVector2F t)
    throws E;

  public A typeVisitVector2I(
    final @Nonnull TVector2I t)
    throws E;

  public A typeVisitVector3F(
    final @Nonnull TVector3F t)
    throws E;

  public A typeVisitVector3I(
    final @Nonnull TVector3I t)
    throws E;

  public A typeVisitVector4F(
    final @Nonnull TVector4F t)
    throws E;

  public A typeVisitVector4I(
    final @Nonnull TVector4I t)
    throws E;
}
