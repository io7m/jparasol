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

package com.io7m.jparasol.typed.ast;

import com.io7m.jequality.annotations.EqualityStructural;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jparasol.ModulePath;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.NameShowType;
import com.io7m.jparasol.lexer.TokenIdentifierLower;

/**
 * The type of shader names.
 */

@EqualityStructural public final class TASTShaderName implements NameShowType
{
  private final ModulePathFlat       flat;
  private final TokenIdentifierLower name;
  private final ModulePath           path;

  /**
   * Construct a shader name.
   * 
   * @param in_path
   *          The module path
   * @param in_name
   *          The name
   */

  public TASTShaderName(
    final ModulePath in_path,
    final TokenIdentifierLower in_name)
  {
    this.path = NullCheck.notNull(in_path, "Path");
    this.flat = ModulePathFlat.fromModulePath(in_path);
    this.name = NullCheck.notNull(in_name, "Name");
  }

  @Override public boolean equals(
    final @Nullable Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    final TASTShaderName other = (TASTShaderName) obj;
    if (!this.flat.equals(other.flat)) {
      return false;
    }
    if (!this.name.equals(other.name)) {
      return false;
    }
    if (!this.path.equals(other.path)) {
      return false;
    }
    return true;
  }

  /**
   * @return The flattened module path
   */

  public ModulePathFlat getFlat()
  {
    return this.flat;
  }

  /**
   * @return The name of the shader
   */

  public TokenIdentifierLower getName()
  {
    return this.name;
  }

  /**
   * @return The module path
   */

  public ModulePath getPath()
  {
    return this.path;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.flat.hashCode();
    result = (prime * result) + this.name.hashCode();
    result = (prime * result) + this.path.hashCode();
    return result;
  }

  @Override public String show()
  {
    final StringBuilder s = new StringBuilder();
    s.append(this.flat.getActual());
    s.append(".");
    s.append(this.name.getActual());
    final String r = s.toString();
    assert r != null;
    return r;
  }
}
