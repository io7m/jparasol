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

import java.io.File;
import java.util.List;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDModule;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDPackage;

/**
 * A compilation unit.
 */

public final class UASTIUnit
{
  private final @Nonnull File               file;
  private final @Nonnull List<UASTIDModule> modules;
  private final @Nonnull UASTIDPackage      package_name;

  public UASTIUnit(
    final @Nonnull File file,
    final @Nonnull UASTIDPackage package_name,
    final @Nonnull List<UASTIDModule> modules)
    throws ConstraintError
  {
    this.file = Constraints.constrainNotNull(file, "File");
    this.package_name = Constraints.constrainNotNull(package_name, "Package");
    this.modules = Constraints.constrainNotNull(modules, "Modules");
  }

  public @Nonnull File getFile()
  {
    return this.file;
  }

  public @Nonnull List<UASTIDModule> getModules()
  {
    return this.modules;
  }

  public @Nonnull UASTIDPackage getPackageName()
  {
    return this.package_name;
  }
}
