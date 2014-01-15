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

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jparasol.typed.TType.TBoolean;
import com.io7m.jparasol.typed.TType.TConstructor;
import com.io7m.jparasol.typed.TType.TFloat;
import com.io7m.jparasol.typed.TType.TInteger;
import com.io7m.jparasol.typed.TType.TMatrix3x3F;
import com.io7m.jparasol.typed.TType.TMatrix4x4F;
import com.io7m.jparasol.typed.TType.TSampler2D;
import com.io7m.jparasol.typed.TType.TSamplerCube;
import com.io7m.jparasol.typed.TType.TValueType;
import com.io7m.jparasol.typed.TType.TVector2F;
import com.io7m.jparasol.typed.TType.TVector2I;
import com.io7m.jparasol.typed.TType.TVector3F;
import com.io7m.jparasol.typed.TType.TVector3I;
import com.io7m.jparasol.typed.TType.TVector4F;
import com.io7m.jparasol.typed.TType.TVector4I;

public final class TTypeTest
{
  @SuppressWarnings({ "static-method", "boxing" }) boolean hasConstructor(
    final TType t,
    final TValueType[] parameters)
  {
    final TConstructor cr = TConstructor.newConstructor(parameters);

    for (final TConstructor c : t.getConstructors()) {
      final boolean r = cr.equals(c);
      System.err.println(String.format(
        "%s = %s? -> %s",
        c.show(),
        cr.show(),
        r));
      if (r) {
        return true;
      }
    }

    return false;
  }

  @Test public void testBooleanConstructors()
  {
    Assert.assertEquals(3, TBoolean.get().getConstructors().size());
    Assert.assertTrue(this.hasConstructor(
      TBoolean.get(),
      new TValueType[] { TBoolean.get() }));
    Assert.assertTrue(this.hasConstructor(
      TBoolean.get(),
      new TValueType[] { TFloat.get() }));
    Assert.assertTrue(this.hasConstructor(
      TBoolean.get(),
      new TValueType[] { TInteger.get() }));

    for (final TConstructor c : TBoolean.get().getConstructors()) {
      Assert.assertEquals(1, c.getComponentCount());
    }
  }

  @SuppressWarnings("static-method") @Test public void testBooleanEquals()
  {
    Assert.assertEquals(TBoolean.get(), TBoolean.get());
    Assert.assertNotEquals(TBoolean.get(), TFloat.get());
    Assert.assertNotEquals(TBoolean.get(), null);
  }

  @SuppressWarnings("static-method") @Test public void testBooleanName()
  {
    Assert.assertEquals("boolean", TBoolean.get().getName().getName());
  }

  @Test public void testFloatConstructors()
  {
    Assert.assertEquals(3, TFloat.get().getConstructors().size());
    Assert.assertTrue(this.hasConstructor(
      TFloat.get(),
      new TValueType[] { TFloat.get() }));
    Assert.assertTrue(this.hasConstructor(
      TFloat.get(),
      new TValueType[] { TInteger.get() }));
    Assert.assertTrue(this.hasConstructor(
      TFloat.get(),
      new TValueType[] { TBoolean.get() }));

    for (final TConstructor c : TFloat.get().getConstructors()) {
      Assert.assertEquals(1, c.getComponentCount());
    }
  }

  @SuppressWarnings("static-method") @Test public void testFloatEquals()
  {
    Assert.assertEquals(TFloat.get(), TFloat.get());
    Assert.assertNotEquals(TFloat.get(), TInteger.get());
    Assert.assertNotEquals(TFloat.get(), null);
  }

  @SuppressWarnings("static-method") @Test public void testFloatName()
  {
    Assert.assertEquals("float", TFloat.get().getName().getName());
  }

  @Test public void testIntegerConstructors()
  {
    Assert.assertEquals(3, TInteger.get().getConstructors().size());
    Assert.assertTrue(this.hasConstructor(
      TInteger.get(),
      new TValueType[] { TInteger.get() }));
    Assert.assertTrue(this.hasConstructor(
      TInteger.get(),
      new TValueType[] { TFloat.get() }));
    Assert.assertTrue(this.hasConstructor(
      TInteger.get(),
      new TValueType[] { TBoolean.get() }));

    for (final TConstructor c : TInteger.get().getConstructors()) {
      Assert.assertEquals(1, c.getComponentCount());
    }
  }

  @SuppressWarnings("static-method") @Test public void testIntegerEquals()
  {
    Assert.assertEquals(TInteger.get(), TInteger.get());
    Assert.assertNotEquals(TInteger.get(), TFloat.get());
    Assert.assertNotEquals(TInteger.get(), null);
  }

  @SuppressWarnings("static-method") @Test public void testIntegerName()
  {
    Assert.assertEquals("integer", TInteger.get().getName().getName());
  }

  @Test public void testMatrix3x3FConstructors()
  {
    Assert.assertEquals(1, TMatrix3x3F.get().getConstructors().size());

    Assert
      .assertTrue(this.hasConstructor(TMatrix3x3F.get(), new TValueType[] {
        TVector3F.get(),
        TVector3F.get(),
        TVector3F.get() }));

    for (final TConstructor c : TMatrix3x3F.get().getConstructors()) {
      Assert.assertEquals(9, c.getComponentCount());
    }
  }

  @SuppressWarnings("static-method") @Test public void testMatrix3x3FEquals()
  {
    Assert.assertEquals(TMatrix3x3F.get(), TMatrix3x3F.get());
    Assert.assertNotEquals(TMatrix3x3F.get(), TInteger.get());
    Assert.assertNotEquals(TMatrix3x3F.get(), null);
  }

  @SuppressWarnings("static-method") @Test public void testMatrix3x3FName()
  {
    Assert.assertEquals("matrix_3x3f", TMatrix3x3F.get().getName().getName());
  }

  @Test public void testMatrix4x4FConstructors()
  {
    Assert.assertEquals(1, TMatrix4x4F.get().getConstructors().size());

    Assert.assertTrue(this.hasConstructor(
      TMatrix4x4F.get(),
      new TValueType[] {
        TVector4F.get(),
        TVector4F.get(),
        TVector4F.get(),
        TVector4F.get() }));

    for (final TConstructor c : TMatrix4x4F.get().getConstructors()) {
      Assert.assertEquals(16, c.getComponentCount());
    }
  }

  @SuppressWarnings("static-method") @Test public void testMatrix4x4FEquals()
  {
    Assert.assertEquals(TMatrix4x4F.get(), TMatrix4x4F.get());
    Assert.assertNotEquals(TMatrix4x4F.get(), TInteger.get());
    Assert.assertNotEquals(TMatrix4x4F.get(), null);
  }

  @SuppressWarnings("static-method") @Test public void testMatrix4x4FName()
  {
    Assert.assertEquals("matrix_4x4f", TMatrix4x4F.get().getName().getName());
  }

  @SuppressWarnings("static-method") @Test public
    void
    testSampler2DConstructors()
  {
    Assert.assertEquals(0, TSampler2D.get().getConstructors().size());
  }

  @SuppressWarnings("static-method") @Test public void testSampler2DEquals()
  {
    Assert.assertEquals(TSampler2D.get(), TSampler2D.get());
    Assert.assertNotEquals(TSampler2D.get(), TInteger.get());
    Assert.assertNotEquals(TSampler2D.get(), null);
  }

  @SuppressWarnings("static-method") @Test public void testSampler2DName()
  {
    Assert.assertEquals("sampler_2d", TSampler2D.get().getName().getName());
  }

  @SuppressWarnings("static-method") @Test public
    void
    testSamplerCubeConstructors()
  {
    Assert.assertEquals(0, TSamplerCube.get().getConstructors().size());
  }

  @SuppressWarnings("static-method") @Test public
    void
    testSamplerCubeEquals()
  {
    Assert.assertEquals(TSamplerCube.get(), TSamplerCube.get());
    Assert.assertNotEquals(TSamplerCube.get(), TInteger.get());
    Assert.assertNotEquals(TSamplerCube.get(), null);
  }

  @SuppressWarnings("static-method") @Test public void testSamplerCubeName()
  {
    Assert.assertEquals("sampler_cube", TSamplerCube
      .get()
      .getName()
      .getName());
  }

  @SuppressWarnings("static-method") @Test public
    void
    testVector2FComponents()
  {
    Assert.assertEquals(TFloat.get(), TVector2F.get().getComponentType());
    Assert.assertEquals(2, TVector2F.get().getComponentNames().size());
    Assert.assertEquals("x", TVector2F.get().getComponentNames().get(0));
    Assert.assertEquals("y", TVector2F.get().getComponentNames().get(1));
  }

  @Test public void testVector2FConstructors()
  {
    Assert.assertEquals(2, TVector2F.get().getConstructors().size());
    Assert.assertTrue(this.hasConstructor(
      TVector2F.get(),
      new TValueType[] { TVector2F.get() }));
    Assert.assertTrue(this.hasConstructor(TVector2F.get(), new TValueType[] {
      TFloat.get(),
      TFloat.get() }));

    for (final TConstructor c : TVector2F.get().getConstructors()) {
      Assert.assertEquals(2, c.getComponentCount());
    }
  }

  @SuppressWarnings("static-method") @Test public void testVector2FEquals()
  {
    Assert.assertEquals(TVector2F.get(), TVector2F.get());
    Assert.assertNotEquals(TVector2F.get(), TFloat.get());
    Assert.assertNotEquals(TVector2F.get(), null);
  }

  @SuppressWarnings("static-method") @Test public void testVector2FName()
  {
    Assert.assertEquals("vector_2f", TVector2F.get().getName().getName());
  }

  @SuppressWarnings("static-method") @Test public
    void
    testVector2IComponents()
  {
    Assert.assertEquals(TInteger.get(), TVector2I.get().getComponentType());
    Assert.assertEquals(2, TVector2I.get().getComponentNames().size());
    Assert.assertEquals("x", TVector2I.get().getComponentNames().get(0));
    Assert.assertEquals("y", TVector2I.get().getComponentNames().get(1));
  }

  @Test public void testVector2IConstructors()
  {
    Assert.assertEquals(2, TVector2I.get().getConstructors().size());
    Assert.assertTrue(this.hasConstructor(
      TVector2I.get(),
      new TValueType[] { TVector2I.get() }));
    Assert.assertTrue(this.hasConstructor(TVector2I.get(), new TValueType[] {
      TInteger.get(),
      TInteger.get() }));

    for (final TConstructor c : TVector2I.get().getConstructors()) {
      Assert.assertEquals(2, c.getComponentCount());
    }
  }

  @SuppressWarnings("static-method") @Test public void testVector2IEquals()
  {
    Assert.assertEquals(TVector2I.get(), TVector2I.get());
    Assert.assertNotEquals(TVector2I.get(), TInteger.get());
    Assert.assertNotEquals(TVector2I.get(), null);
  }

  @SuppressWarnings("static-method") @Test public void testVector2IName()
  {
    Assert.assertEquals("vector_2i", TVector2I.get().getName().getName());
  }

  @SuppressWarnings("static-method") @Test public
    void
    testVector3FComponents()
  {
    Assert.assertEquals(TFloat.get(), TVector3F.get().getComponentType());
    Assert.assertEquals(3, TVector3F.get().getComponentNames().size());
    Assert.assertEquals("x", TVector3F.get().getComponentNames().get(0));
    Assert.assertEquals("y", TVector3F.get().getComponentNames().get(1));
    Assert.assertEquals("z", TVector3F.get().getComponentNames().get(2));
  }

  @Test public void testVector3FConstructors()
  {
    Assert.assertEquals(4, TVector3F.get().getConstructors().size());

    Assert.assertTrue(this.hasConstructor(
      TVector3F.get(),
      new TValueType[] { TVector3F.get() }));

    Assert.assertTrue(this.hasConstructor(TVector3F.get(), new TValueType[] {
      TFloat.get(),
      TFloat.get(),
      TFloat.get() }));

    Assert.assertTrue(this.hasConstructor(TVector3F.get(), new TValueType[] {
      TFloat.get(),
      TVector2F.get() }));

    Assert.assertTrue(this.hasConstructor(TVector3F.get(), new TValueType[] {
      TVector2F.get(),
      TFloat.get() }));

    for (final TConstructor c : TVector3F.get().getConstructors()) {
      Assert.assertEquals(3, c.getComponentCount());
    }
  }

  @SuppressWarnings("static-method") @Test public void testVector3FEquals()
  {
    Assert.assertEquals(TVector3F.get(), TVector3F.get());
    Assert.assertNotEquals(TVector3F.get(), TFloat.get());
    Assert.assertNotEquals(TVector3F.get(), null);
  }

  @SuppressWarnings("static-method") @Test public void testVector3FName()
  {
    Assert.assertEquals("vector_3f", TVector3F.get().getName().getName());
  }

  @SuppressWarnings("static-method") @Test public
    void
    testVector3IComponents()
  {
    Assert.assertEquals(TInteger.get(), TVector3I.get().getComponentType());
    Assert.assertEquals(3, TVector3I.get().getComponentNames().size());
    Assert.assertEquals("x", TVector3I.get().getComponentNames().get(0));
    Assert.assertEquals("y", TVector3I.get().getComponentNames().get(1));
    Assert.assertEquals("z", TVector3I.get().getComponentNames().get(2));
  }

  @Test public void testVector3IConstructors()
  {
    Assert.assertEquals(4, TVector3I.get().getConstructors().size());

    Assert.assertTrue(this.hasConstructor(
      TVector3I.get(),
      new TValueType[] { TVector3I.get() }));

    Assert.assertTrue(this.hasConstructor(TVector3I.get(), new TValueType[] {
      TInteger.get(),
      TInteger.get(),
      TInteger.get() }));

    Assert.assertTrue(this.hasConstructor(TVector3I.get(), new TValueType[] {
      TInteger.get(),
      TVector2I.get() }));

    Assert.assertTrue(this.hasConstructor(TVector3I.get(), new TValueType[] {
      TVector2I.get(),
      TInteger.get() }));

    for (final TConstructor c : TVector3I.get().getConstructors()) {
      Assert.assertEquals(3, c.getComponentCount());
    }
  }

  @SuppressWarnings("static-method") @Test public void testVector3IEquals()
  {
    Assert.assertEquals(TVector3I.get(), TVector3I.get());
    Assert.assertNotEquals(TVector3I.get(), TInteger.get());
    Assert.assertNotEquals(TVector3I.get(), null);
  }

  @SuppressWarnings("static-method") @Test public void testVector3IName()
  {
    Assert.assertEquals("vector_3i", TVector3I.get().getName().getName());
  }

  @SuppressWarnings("static-method") @Test public
    void
    testVector4FComponents()
  {
    Assert.assertEquals(TFloat.get(), TVector4F.get().getComponentType());
    Assert.assertEquals(4, TVector4F.get().getComponentNames().size());
    Assert.assertEquals("x", TVector4F.get().getComponentNames().get(0));
    Assert.assertEquals("y", TVector4F.get().getComponentNames().get(1));
    Assert.assertEquals("z", TVector4F.get().getComponentNames().get(2));
    Assert.assertEquals("w", TVector4F.get().getComponentNames().get(3));
  }

  @Test public void testVector4FConstructors()
  {
    Assert.assertEquals(8, TVector4F.get().getConstructors().size());

    Assert.assertTrue(this.hasConstructor(TVector4F.get(), new TValueType[] {
      TFloat.get(),
      TFloat.get(),
      TFloat.get(),
      TFloat.get() }));

    Assert.assertTrue(this.hasConstructor(TVector4F.get(), new TValueType[] {
      TVector2F.get(),
      TFloat.get(),
      TFloat.get() }));

    Assert.assertTrue(this.hasConstructor(TVector4F.get(), new TValueType[] {
      TFloat.get(),
      TFloat.get(),
      TVector2F.get() }));

    Assert.assertTrue(this.hasConstructor(TVector4F.get(), new TValueType[] {
      TFloat.get(),
      TVector2F.get(),
      TFloat.get() }));

    Assert.assertTrue(this.hasConstructor(TVector4F.get(), new TValueType[] {
      TVector2F.get(),
      TVector2F.get() }));

    Assert.assertTrue(this.hasConstructor(TVector4F.get(), new TValueType[] {
      TVector3F.get(),
      TFloat.get() }));

    Assert.assertTrue(this.hasConstructor(TVector4F.get(), new TValueType[] {
      TFloat.get(),
      TVector3F.get() }));

    Assert.assertTrue(this.hasConstructor(
      TVector4F.get(),
      new TValueType[] { TVector4F.get() }));

    for (final TConstructor c : TVector4F.get().getConstructors()) {
      Assert.assertEquals(4, c.getComponentCount());
    }
  }

  @SuppressWarnings("static-method") @Test public void testVector4FEquals()
  {
    Assert.assertEquals(TVector4F.get(), TVector4F.get());
    Assert.assertNotEquals(TVector4F.get(), TFloat.get());
    Assert.assertNotEquals(TVector4F.get(), null);
  }

  @SuppressWarnings("static-method") @Test public void testVector4FName()
  {
    Assert.assertEquals("vector_4f", TVector4F.get().getName().getName());
  }

  @SuppressWarnings("static-method") @Test public
    void
    testVector4IComponents()
  {
    Assert.assertEquals(TInteger.get(), TVector4I.get().getComponentType());
    Assert.assertEquals(4, TVector4I.get().getComponentNames().size());
    Assert.assertEquals("x", TVector4I.get().getComponentNames().get(0));
    Assert.assertEquals("y", TVector4I.get().getComponentNames().get(1));
    Assert.assertEquals("z", TVector4I.get().getComponentNames().get(2));
    Assert.assertEquals("w", TVector4I.get().getComponentNames().get(3));
  }

  @Test public void testVector4IConstructors()
  {
    Assert.assertEquals(8, TVector4I.get().getConstructors().size());

    Assert.assertTrue(this.hasConstructor(TVector4I.get(), new TValueType[] {
      TInteger.get(),
      TInteger.get(),
      TInteger.get(),
      TInteger.get() }));

    Assert.assertTrue(this.hasConstructor(TVector4I.get(), new TValueType[] {
      TVector2I.get(),
      TInteger.get(),
      TInteger.get() }));

    Assert.assertTrue(this.hasConstructor(TVector4I.get(), new TValueType[] {
      TInteger.get(),
      TInteger.get(),
      TVector2I.get() }));

    Assert.assertTrue(this.hasConstructor(TVector4I.get(), new TValueType[] {
      TInteger.get(),
      TVector2I.get(),
      TInteger.get() }));

    Assert.assertTrue(this.hasConstructor(TVector4I.get(), new TValueType[] {
      TVector2I.get(),
      TVector2I.get() }));

    Assert.assertTrue(this.hasConstructor(TVector4I.get(), new TValueType[] {
      TVector3I.get(),
      TInteger.get() }));

    Assert.assertTrue(this.hasConstructor(TVector4I.get(), new TValueType[] {
      TInteger.get(),
      TVector3I.get() }));

    Assert.assertTrue(this.hasConstructor(
      TVector4I.get(),
      new TValueType[] { TVector4I.get() }));

    for (final TConstructor c : TVector4I.get().getConstructors()) {
      Assert.assertEquals(4, c.getComponentCount());
    }
  }

  @SuppressWarnings("static-method") @Test public void testVector4IEquals()
  {
    Assert.assertEquals(TVector4I.get(), TVector4I.get());
    Assert.assertNotEquals(TVector4I.get(), TInteger.get());
    Assert.assertNotEquals(TVector4I.get(), null);
  }

  @SuppressWarnings("static-method") @Test public void testVector4IName()
  {
    Assert.assertEquals("vector_4i", TVector4I.get().getName().getName());
  }
}
