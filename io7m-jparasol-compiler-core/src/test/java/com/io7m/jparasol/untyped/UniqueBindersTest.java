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

package com.io7m.jparasol.untyped;

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.TestUtilities;
import com.io7m.jparasol.untyped.ast.initial.UASTIChecked;
import com.io7m.jparasol.untyped.ast.initial.UASTICompilation;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUCompilation;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDModule;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDValue;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDValueLocal;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUELet;

public final class UniqueBindersTest
{
  static UASTICompilation<UASTIChecked> checked(
    final String[] names)
  {
    try {
      return ModuleStructureTest.check(names);
    } catch (final ModuleStructureError e) {
      throw new UnreachableCodeException(e);
    } catch (final ConstraintError e) {
      throw new UnreachableCodeException(e);
    }
  }

  static UASTICompilation<UASTIChecked> checkedInternal(
    final String[] names)
  {
    try {
      return ModuleStructureTest.checkInternal(names);
    } catch (final ModuleStructureError e) {
      throw new UnreachableCodeException(e);
    } catch (final ConstraintError e) {
      throw new UnreachableCodeException(e);
    }
  }

  static UASTUCompilation unique(
    final String[] names)
    throws UniqueBindersError,
      ConstraintError
  {
    UniqueBinders u;
    try {
      u =
        UniqueBinders.newUniqueBinders(
          UniqueBindersTest.checked(names),
          TestUtilities.getLog());
    } catch (final ConstraintError e) {
      throw new UnreachableCodeException(e);
    }

    return u.run();
  }

  static UASTUCompilation uniqueInternal(
    final String[] names)
    throws UniqueBindersError,
      ConstraintError
  {
    UniqueBinders u;
    try {
      u =
        UniqueBinders.newUniqueBinders(
          UniqueBindersTest.checkedInternal(names),
          TestUtilities.getLog());
    } catch (final ConstraintError e) {
      throw new UnreachableCodeException(e);
    }

    return u.run();
  }

  @SuppressWarnings("static-method") @Test public void testPre0()
    throws UniqueBindersError,
      ConstraintError
  {
    final UASTUCompilation r =
      UniqueBindersTest
        .uniqueInternal(new String[] { "unique_binders/let-pre-0.p" });

    for (final UASTUDModule m : r.getModules().values()) {
      System.out.println(m);
    }
  }

  @SuppressWarnings("static-method") @Test public void testPre0_Simple()
    throws UniqueBindersError,
      ConstraintError
  {
    final UASTUCompilation r =
      UniqueBindersTest
        .uniqueInternal(new String[] { "unique_binders/let-pre-0-simple.p" });

    for (final UASTUDModule m : r.getModules().values()) {
      System.out.println(m);
    }
  }

  @SuppressWarnings("static-method") @Test public void testNotRestricted()
    throws UniqueBindersError,
      ConstraintError
  {
    final UASTUCompilation r =
      UniqueBindersTest
        .uniqueInternal(new String[] { "unique_binders/not-restricted-0.p" });

    final ModulePathFlat path = r.getModules().keySet().iterator().next();
    final UASTUDModule module = r.getModules().get(path);
    System.out.println(module);

    final UASTUDValue value = (UASTUDValue) module.getDeclarations().get(0);
    Assert.assertEquals("vec", value.getName().getActual());

    /**
     * The first binding of the let expression will have been modified to be
     * distinct from the module-level value.
     */

    final UASTUELet let0 = (UASTUELet) value.getExpression();
    final UASTUDValueLocal let0_v = let0.getBindings().get(0);
    Assert.assertEquals("&vec1", let0_v.getName().show());

    /**
     * The body of the let expression is another let expression. The first
     * binding of which will have been modified and will end up being called
     * "vec5", because the renaming algorithm will skip "vec2", "vec3", etc,
     * due to those being restricted names.
     */

    final UASTUELet let1 = (UASTUELet) let0.getBody();
    final UASTUDValueLocal let1_v = let1.getBindings().get(0);
    Assert.assertEquals("&vec5", let1_v.getName().show());
  }
}
