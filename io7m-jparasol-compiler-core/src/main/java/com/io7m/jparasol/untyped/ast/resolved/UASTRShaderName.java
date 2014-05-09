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

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.ModulePath;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;

// CHECKSTYLE_JAVADOC:OFF

@EqualityReference public final class UASTRShaderName
{
  private final ModulePathFlat       flat;
  private final TokenIdentifierLower name;
  private final ModulePath           path;

  public UASTRShaderName(
    final ModulePath in_path,
    final TokenIdentifierLower in_name)
  {
    this.path = NullCheck.notNull(in_path, "Path");
    this.flat = ModulePathFlat.fromModulePath(in_path);
    this.name = NullCheck.notNull(in_name, "Name");
  }

  public ModulePathFlat getFlat()
  {
    return this.flat;
  }

  public TokenIdentifierLower getName()
  {
    return this.name;
  }

  public ModulePath getPath()
  {
    return this.path;
  }

  public String show()
  {
    final StringBuilder s = new StringBuilder();
    s.append(this.flat.getActual());
    s.append(".");
    s.append(this.name.getActual());
    return s.toString();
  }
}
