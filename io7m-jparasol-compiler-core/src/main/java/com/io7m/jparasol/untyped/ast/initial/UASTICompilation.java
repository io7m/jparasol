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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.ModulePath;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.NameRestrictions;
import com.io7m.jparasol.NameRestrictions.NameRestrictionsException;
import com.io7m.jparasol.PackagePath;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.untyped.UnitCombinerError;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDModule;

public final class UASTICompilation
{
  static final @Nonnull Set<String> EMPTY;

  static {
    EMPTY = Collections.unmodifiableSet(new HashSet<String>());
  }

  public static @Nonnull UASTICompilation fromUnits(
    final @Nonnull List<UASTIUnit> units)
    throws ConstraintError,
      UnitCombinerError
  {
    try {
      final HashMap<ModulePathFlat, UASTIDModule> m =
        new HashMap<ModulePathFlat, UASTIDModule>();
      final HashMap<ModulePathFlat, ModulePath> paths =
        new HashMap<ModulePathFlat, ModulePath>();

      for (final UASTIUnit u : units) {
        final PackagePath pp = u.getPackageName().getPath();

        for (final TokenIdentifierLower pc : pp.getComponents()) {
          NameRestrictions.checkRestrictedExceptional(pc);
        }

        for (final UASTIDModule um : u.getModules()) {

          final ModulePath mp = um.getPath();
          final ModulePathFlat flat = ModulePathFlat.fromModulePath(mp);

          final UASTIDModule original = m.get(flat);

          if (original != null) {
            throw UnitCombinerError.duplicateModule(original, um);
          }

          m.put(flat, um);
          paths.put(flat, mp);
        }
      }

      return new UASTICompilation(m, paths);
    } catch (final NameRestrictionsException x) {
      throw new UnitCombinerError(x);
    }
  }

  private final @Nonnull Map<ModulePathFlat, UASTIDModule> modules;
  private final @Nonnull Map<ModulePathFlat, ModulePath>   paths;

  private UASTICompilation(
    final @Nonnull Map<ModulePathFlat, UASTIDModule> modules,
    final @Nonnull Map<ModulePathFlat, ModulePath> paths)
    throws ConstraintError
  {
    this.modules = Constraints.constrainNotNull(modules, "Modules");
    this.paths = Constraints.constrainNotNull(paths, "Paths");
  }

  public @Nonnull Map<ModulePathFlat, UASTIDModule> getModules()
  {
    return Collections.unmodifiableMap(this.modules);
  }

  public @Nonnull Map<ModulePathFlat, ModulePath> getPaths()
  {
    return Collections.unmodifiableMap(this.paths);
  }
}
