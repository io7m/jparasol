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

import org.junit.Test;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jparasol.TestUtilities;
import com.io7m.jparasol.untyped.ast.initial.UASTIChecked;
import com.io7m.jparasol.untyped.ast.initial.UASTICompilation;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUCompilation;

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

  @Test public void testInitial()
    throws UniqueBindersError,
      ConstraintError
  {
    final UASTUCompilation r =
      UniqueBindersTest.uniqueInternal(new String[] { "all.p" });
  }
}
