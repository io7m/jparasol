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

package com.io7m.jparasol.untyped.ast.initial;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.ModulePath;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.NameRestrictions;
import com.io7m.jparasol.NameRestrictions.NameRestrictionsException;
import com.io7m.jparasol.PackagePath;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.untyped.UnitCombinerError;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDModule;

// CHECKSTYLE_JAVADOC:OFF

@EqualityReference public final class UASTICompilation
{
  static final Set<String> EMPTY;

  static {
    EMPTY = Collections.unmodifiableSet(new HashSet<String>());
  }

  public static UASTICompilation fromUnits(
    final List<UASTIUnit> units)
    throws UnitCombinerError
  {
    try {
      final Map<ModulePathFlat, UASTIDModule> m =
        new HashMap<ModulePathFlat, UASTIDModule>();
      final Map<ModulePathFlat, ModulePath> paths =
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

  private final Map<ModulePathFlat, UASTIDModule> modules;
  private final Map<ModulePathFlat, ModulePath>   paths;

  private UASTICompilation(
    final Map<ModulePathFlat, UASTIDModule> in_modules,
    final Map<ModulePathFlat, ModulePath> in_paths)
  {
    this.modules = NullCheck.notNull(in_modules, "Modules");
    this.paths = NullCheck.notNull(in_paths, "Paths");
  }

  public Map<ModulePathFlat, UASTIDModule> getModules()
  {
    return Collections.unmodifiableMap(this.modules);
  }

  public Map<ModulePathFlat, ModulePath> getPaths()
  {
    return Collections.unmodifiableMap(this.paths);
  }
}
