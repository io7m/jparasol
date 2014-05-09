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
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.NameFlatType;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.typed.ast.TASTTermName.TASTTermNameGlobal;

/**
 * A flattened term name.
 */

@EqualityStructural public final class TASTTermNameFlat implements
  NameFlatType,
  TASTNameTypeTermFlatType,
  TASTNameTermShaderFlatType
{
  /**
   * @param name
   *          The name
   * @return A flattened version of the given name
   */

  public static TASTTermNameFlat fromTermNameGlobal(
    final TASTTermNameGlobal name)
  {
    NullCheck.notNull(name, "Name");

    final TokenIdentifierLower n = name.getName();
    final String text = n.getActual();
    assert text != null;
    return new TASTTermNameFlat(name.getFlat(), text);
  }

  private final String         name;
  private final ModulePathFlat path;

  /**
   * Construct a flattened name.
   * 
   * @param in_path
   *          The flattened module path
   * @param in_name
   *          The name
   */

  public TASTTermNameFlat(
    final ModulePathFlat in_path,
    final String in_name)
  {
    this.path = NullCheck.notNull(in_path, "Path");
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
    final TASTTermNameFlat other = (TASTTermNameFlat) obj;
    if (!this.name.equals(other.name)) {
      return false;
    }
    if (!this.path.equals(other.path)) {
      return false;
    }
    return true;
  }

  @Override public ModulePathFlat getModulePath()
  {
    return this.path;
  }

  @Override public String getName()
  {
    return this.name;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.name.hashCode();
    result = (prime * result) + this.path.hashCode();
    return result;
  }

  @Override public
    <A, E extends Throwable, V extends TASTNameTermShaderFlatVisitorType<A, E>>
    A
    nameTermShaderVisitableAccept(
      final V v)
      throws E
  {
    return v.nameTypeShaderVisitTerm(this);
  }

  @Override public
    <A, E extends Throwable, V extends TASTNameTypeTermFlatVisitorType<A, E>>
    A
    nameTypeTermVisitableAccept(
      final V v)
      throws E
  {
    return v.nameTypeTermVisitTerm(this);
  }

  @Override public String show()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append(this.path.getActual());
    builder.append(".");
    builder.append(this.name);
    final String r = builder.toString();
    assert r != null;
    return r;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[TASTTermNameFlat ");
    builder.append(this.path);
    builder.append(" ");
    builder.append(this.name);
    builder.append("]");
    final String r = builder.toString();
    assert r != null;
    return r;
  }
}
