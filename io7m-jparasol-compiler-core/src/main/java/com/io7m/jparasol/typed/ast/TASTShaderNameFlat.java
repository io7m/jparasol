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

import java.io.File;

import com.io7m.jequality.annotations.EqualityStructural;
import com.io7m.jfunctional.Pair;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.NameFlatType;
import com.io7m.jparasol.PackagePath;
import com.io7m.jparasol.PackagePath.BuilderType;
import com.io7m.jparasol.PackagePathFlat;
import com.io7m.jparasol.UIError;
import com.io7m.jparasol.lexer.Position;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;

/**
 * The type of flattened shader names.
 */

@EqualityStructural public final class TASTShaderNameFlat implements
  NameFlatType,
  TASTNameTypeShaderFlatType,
  TASTNameTermShaderFlatType,
  Comparable<TASTShaderNameFlat>
{
  /**
   * Flatten a shader name.
   *
   * @param name
   *          The name
   * @return A flattened name
   */

  public static TASTShaderNameFlat fromShaderName(
    final TASTShaderName name)
  {
    NullCheck.notNull(name, "Name");
    final TokenIdentifierLower n = name.getName();
    final String text = n.getActual();
    assert text != null;
    return new TASTShaderNameFlat(name.getFlat(), text);
  }

  /**
   * Parse a flattened shader name from a string, with associated metadata.
   *
   * @param name
   *          The string
   * @param meta
   *          The metadata
   * @return A flattened shader name
   * @throws UIError
   *           If the shader name cannot be parsed
   */

  public static TASTShaderNameFlat parse(
    final String name,
    final Pair<File, Position> meta)
    throws UIError
  {
    final String[] segments = name.split("\\.");
    if (segments.length < 3) {
      throw UIError.shaderNameUnparseable(name, meta, null);
    }

    final BuilderType pp = PackagePath.newBuilder();
    for (int index = 0; index < (segments.length - 2); ++index) {
      final String s = segments[index];
      assert s != null;
      pp.addFakeComponent(s);
    }

    final String module_name = segments[segments.length - 2];
    if (module_name.isEmpty()) {
      throw UIError.shaderNameUnparseable(name, meta, "Module name is empty");
    }
    if (Character.isUpperCase(module_name.charAt(0)) == false) {
      throw UIError.shaderNameUnparseable(
        name,
        meta,
        "Module name is not uppercase");
    }

    final String shader_name = segments[segments.length - 1];
    if (shader_name.isEmpty()) {
      throw UIError.shaderNameUnparseable(name, meta, "Shader name is empty");
    }
    if (Character.isLowerCase(shader_name.charAt(0)) == false) {
      throw UIError.shaderNameUnparseable(
        name,
        meta,
        "Shader name is not lowercase");
    }

    final PackagePathFlat ppf = PackagePathFlat.fromPackagePath(pp.build());
    final ModulePathFlat mpf =
      new ModulePathFlat(ppf.getActual() + "." + module_name);
    return new TASTShaderNameFlat(mpf, shader_name);
  }

  private final String         name;
  private final ModulePathFlat path;
  private final String         show_text;

  /**
   * Construct a flattened shader name.
   *
   * @param in_path
   *          The flattened module path
   * @param in_name
   *          The name
   */

  public TASTShaderNameFlat(
    final ModulePathFlat in_path,
    final String in_name)
  {
    this.path = NullCheck.notNull(in_path, "Path");
    this.name = NullCheck.notNull(in_name, "Name");

    {
      final StringBuilder builder = new StringBuilder();
      builder.append(this.path.getActual());
      builder.append(".");
      builder.append(this.name);
      final String r = builder.toString();
      assert r != null;
      this.show_text = r;
    }
  }

  @Override public int compareTo(
    final @Nullable TASTShaderNameFlat o)
  {
    return this.show_text.compareTo(NullCheck.notNull(o, "Other").show_text);
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
    final TASTShaderNameFlat other = (TASTShaderNameFlat) obj;
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
    return v.nameTypeShaderVisitShader(this);
  }

  @Override public
    <A, E extends Throwable, V extends TASTNameTypeShaderFlatVisitorType<A, E>>
    A
    nameTypeShaderVisitableAccept(
      final V v)
      throws E
  {
    return v.nameTypeShaderVisitShader(this);
  }

  @Override public String show()
  {
    return this.show_text;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[TASTShaderNameFlat ");
    builder.append(this.path);
    builder.append(" ");
    builder.append(this.name);
    builder.append("]");
    final String r = builder.toString();
    assert r != null;
    return r;
  }
}
