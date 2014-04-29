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

import java.io.File;
import java.util.List;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDModule;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDPackage;

/**
 * A compilation unit.
 */

// CHECKSTYLE_JAVADOC:OFF

@EqualityReference public final class UASTIUnit
{
  private final File               file;
  private final List<UASTIDModule> modules;
  private final UASTIDPackage      package_name;

  public UASTIUnit(
    final File in_file,
    final UASTIDPackage in_package_name,
    final List<UASTIDModule> in_modules)
  {
    this.file = NullCheck.notNull(in_file, "File");
    this.package_name = NullCheck.notNull(in_package_name, "Package");
    this.modules = NullCheck.notNull(in_modules, "Modules");
  }

  public File getFile()
  {
    return this.file;
  }

  public List<UASTIDModule> getModules()
  {
    return this.modules;
  }

  public UASTIDPackage getPackageName()
  {
    return this.package_name;
  }
}
