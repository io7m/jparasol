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

package com.io7m.jparasol.untyped.ast.initial;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.ModulePath;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.PackagePath;
import com.io7m.jparasol.untyped.UnitCombinerError;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDModule;

public final class UASTICompilation<S extends UASTIStatus>
{
  public static @Nonnull UASTICompilation<UASTIUnchecked> fromUnits(
    final @Nonnull List<UASTIUnit<UASTIUnchecked>> units)
    throws ConstraintError,
      UnitCombinerError
  {
    final HashMap<ModulePathFlat, UASTIDModule<UASTIUnchecked>> m =
      new HashMap<ModulePathFlat, UASTIDModule<UASTIUnchecked>>();

    for (final UASTIUnit<UASTIUnchecked> u : units) {
      for (final UASTIDModule<UASTIUnchecked> um : u.getModules()) {
        final PackagePath pp = u.getPackageName().getPath();
        final ModulePathFlat flat =
          ModulePathFlat.fromModulePath(new ModulePath(pp, um.getName()));

        final UASTIDModule<UASTIUnchecked> original = m.get(flat);

        if (original != null) {
          throw UnitCombinerError.duplicateModule(original, um);
        }

        m.put(flat, um);
      }
    }

    return new UASTICompilation<UASTIUnchecked>(m);
  }

  private final @Nonnull Map<ModulePathFlat, UASTIDModule<S>> modules;

  private UASTICompilation(
    final @Nonnull Map<ModulePathFlat, UASTIDModule<S>> modules)
    throws ConstraintError
  {
    this.modules = Constraints.constrainNotNull(modules, "Modules");
  }

  public @Nonnull Map<ModulePathFlat, UASTIDModule<S>> getModules()
  {
    return Collections.unmodifiableMap(this.modules);
  }

}
