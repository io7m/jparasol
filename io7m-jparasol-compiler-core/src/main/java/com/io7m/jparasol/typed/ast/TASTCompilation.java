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

package com.io7m.jparasol.typed.ast;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.ModulePath;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDModule;

public final class TASTCompilation
{
  private final @Nonnull List<ModulePathFlat>             module_topology;
  private final @Nonnull Map<ModulePathFlat, TASTDModule> modules;
  private final @Nonnull Map<ModulePathFlat, ModulePath>  paths;

  public TASTCompilation(
    final @Nonnull List<ModulePathFlat> module_topology,
    final @Nonnull Map<ModulePathFlat, TASTDModule> modules,
    final @Nonnull Map<ModulePathFlat, ModulePath> paths)
    throws ConstraintError
  {
    this.module_topology =
      Constraints.constrainNotNull(module_topology, "Module topology");
    this.modules = Constraints.constrainNotNull(modules, "Modules");
    this.paths = Constraints.constrainNotNull(paths, "Paths");
  }

  public @Nonnull List<ModulePathFlat> getModuleTopology()
  {
    return this.module_topology;
  }

  public @Nonnull Map<ModulePathFlat, TASTDModule> getModules()
  {
    return Collections.unmodifiableMap(this.modules);
  }

  public @Nonnull Map<ModulePathFlat, ModulePath> getPaths()
  {
    return Collections.unmodifiableMap(this.paths);
  }
}
