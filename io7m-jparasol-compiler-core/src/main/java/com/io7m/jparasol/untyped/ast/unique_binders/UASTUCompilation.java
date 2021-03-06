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

package com.io7m.jparasol.untyped.ast.unique_binders;

import java.util.Collections;
import java.util.Map;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.ModulePath;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDModule;

// CHECKSTYLE_JAVADOC:OFF

@EqualityReference public final class UASTUCompilation
{
  private final Map<ModulePathFlat, UASTUDModule> modules;
  private final Map<ModulePathFlat, ModulePath>   paths;

  public UASTUCompilation(
    final Map<ModulePathFlat, UASTUDModule> in_modules,
    final Map<ModulePathFlat, ModulePath> in_paths)
  {
    this.modules = NullCheck.notNull(in_modules, "Modules");
    this.paths = NullCheck.notNull(in_paths, "Paths");
  }

  public Map<ModulePathFlat, UASTUDModule> getModules()
  {
    return Collections.unmodifiableMap(this.modules);
  }

  public Map<ModulePathFlat, ModulePath> getPaths()
  {
    return Collections.unmodifiableMap(this.paths);
  }
}
