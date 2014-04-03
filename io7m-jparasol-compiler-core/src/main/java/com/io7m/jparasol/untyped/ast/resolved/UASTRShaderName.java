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

package com.io7m.jparasol.untyped.ast.resolved;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.ModulePath;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;

public final class UASTRShaderName
{
  private final @Nonnull ModulePathFlat       flat;
  private final @Nonnull TokenIdentifierLower name;
  private final @Nonnull ModulePath           path;

  public UASTRShaderName(
    final @Nonnull ModulePath in_path,
    final @Nonnull TokenIdentifierLower in_name)
    throws ConstraintError
  {
    this.path = Constraints.constrainNotNull(in_path, "Path");
    this.flat = ModulePathFlat.fromModulePath(in_path);
    this.name = Constraints.constrainNotNull(in_name, "Name");
  }

  public @Nonnull ModulePathFlat getFlat()
  {
    return this.flat;
  }

  public @Nonnull TokenIdentifierLower getName()
  {
    return this.name;
  }

  public @Nonnull ModulePath getPath()
  {
    return this.path;
  }

  public @Nonnull String show()
  {
    final StringBuilder s = new StringBuilder();
    s.append(this.flat.getActual());
    s.append(".");
    s.append(this.name.getActual());
    return s.toString();
  }
}
