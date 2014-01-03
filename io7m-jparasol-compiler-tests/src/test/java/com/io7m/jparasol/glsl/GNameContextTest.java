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

package com.io7m.jparasol.glsl;

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.TestUtilities;
import com.io7m.jparasol.glsl.ast.GTypeName;
import com.io7m.jparasol.typed.TTypeNameFlat;

public final class GNameContextTest
{
  @SuppressWarnings("static-method") @Test public void testNames()
    throws ConstraintError
  {
    final GNameContext<TTypeNameFlat, GTypeName> c =
      GNameContext.newContext(
        TestUtilities.getLog(),
        new GNameConstructor<GTypeName>() {
          @Override public GTypeName newName(
            final String name)
          {
            return new GTypeName(name);
          }
        });

    final GTypeName t0 =
      c.getName(new TTypeNameFlat(new ModulePathFlat("x.y.M"), "t"));
    final GTypeName t1 =
      c.getName(new TTypeNameFlat(new ModulePathFlat("x.y.M"), "t"));

    Assert.assertEquals(t0, t1);
    Assert.assertEquals("x_y_M_t", t0.show());
    Assert.assertEquals("x_y_M_t", t1.show());

    final GTypeName t2 =
      c.freshName(new TTypeNameFlat(new ModulePathFlat("x.y.M"), "t"));

    Assert.assertNotEquals(t2, t0);
    Assert.assertNotEquals(t2, t1);
    Assert.assertEquals("x_y_M_t1", t2.show());
  }
}
