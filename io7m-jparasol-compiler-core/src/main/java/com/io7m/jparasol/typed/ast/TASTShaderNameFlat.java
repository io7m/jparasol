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

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Pair;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.NameFlat;
import com.io7m.jparasol.PackagePath;
import com.io7m.jparasol.PackagePath.Builder;
import com.io7m.jparasol.PackagePathFlat;
import com.io7m.jparasol.UIError;
import com.io7m.jparasol.lexer.Position;

public final class TASTShaderNameFlat implements NameFlat
{
  public static @Nonnull TASTShaderNameFlat fromShaderName(
    final @Nonnull TASTShaderName name)
    throws ConstraintError
  {
    return new TASTShaderNameFlat(name.getFlat(), name.getName().getActual());
  }

  public static @Nonnull TASTShaderNameFlat parse(
    final @Nonnull String name,
    final @Nonnull Pair<File, Position> meta)
    throws UIError,
      ConstraintError
  {
    try {
      final String[] segments = name.split("\\.");
      if (segments.length < 3) {
        throw UIError.shaderNameUnparseable(name, meta, null);
      }

      final Builder pp = PackagePath.newBuilder();
      for (int index = 0; index < (segments.length - 2); ++index) {
        pp.addFakeComponent(segments[index]);
      }

      final String module_name = segments[segments.length - 2];
      Constraints.constrainArbitrary(
        module_name.isEmpty() == false,
        "Module name is non-empty");
      Constraints.constrainArbitrary(
        Character.isUpperCase(module_name.charAt(0)),
        "Module name is uppercase");

      final String shader_name = segments[segments.length - 1];
      Constraints.constrainArbitrary(
        shader_name.isEmpty() == false,
        "Shader name is non-empty");
      Constraints.constrainArbitrary(
        Character.isLowerCase(shader_name.charAt(0)),
        "Shader name is lowercase");

      final PackagePathFlat ppf = PackagePathFlat.fromPackagePath(pp.build());
      final ModulePathFlat mpf =
        new ModulePathFlat(ppf.getActual() + "." + module_name);
      return new TASTShaderNameFlat(mpf, shader_name);
    } catch (final ConstraintError x) {
      throw UIError.shaderNameUnparseable(name, meta, x.getMessage());
    }
  }

  private final @Nonnull String         name;
  private final @Nonnull ModulePathFlat path;

  public TASTShaderNameFlat(
    final @Nonnull ModulePathFlat in_path,
    final @Nonnull String in_name)
    throws ConstraintError
  {
    this.path = Constraints.constrainNotNull(in_path, "Path");
    this.name = Constraints.constrainNotNull(in_name, "Name");
  }

  @Override public boolean equals(
    final Object obj)
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

  @Override public @Nonnull String getName()
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

  @Override public @Nonnull String show()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append(this.path.getActual());
    builder.append(".");
    builder.append(this.name);
    return builder.toString();
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[TASTShaderNameFlat ");
    builder.append(this.path);
    builder.append(" ");
    builder.append(this.name);
    builder.append("]");
    return builder.toString();
  }
}
