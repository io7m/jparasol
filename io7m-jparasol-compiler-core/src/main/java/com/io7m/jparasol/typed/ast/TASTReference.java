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

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.ModulePath;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;

/**
 * A reference from a term, type, or shader to a term, type, or shader.
 */

public final class TASTReference
{
  private final @Nonnull ModulePath           source_module;
  private final @Nonnull ModulePathFlat       source_module_flat;
  private final @Nonnull TokenIdentifierLower source_name;
  private final @Nonnull ModulePath           target_module;
  private final @Nonnull ModulePathFlat       target_module_flat;
  private final @Nonnull TokenIdentifierLower target_name;

  public TASTReference(
    final @Nonnull ModulePath source_module,
    final @Nonnull TokenIdentifierLower source_name,
    final @Nonnull ModulePath target_module,
    final @Nonnull TokenIdentifierLower target_name)
    throws ConstraintError
  {
    this.source_module =
      Constraints.constrainNotNull(source_module, "Source module");
    this.source_name =
      Constraints.constrainNotNull(source_name, "Source name");
    this.target_module =
      Constraints.constrainNotNull(target_module, "Target module");
    this.target_name =
      Constraints.constrainNotNull(target_name, "Target name");

    this.source_module_flat = ModulePathFlat.fromModulePath(source_module);
    this.target_module_flat = ModulePathFlat.fromModulePath(target_module);
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
    final TASTReference other = (TASTReference) obj;
    if (!this.source_module.equals(other.source_module)) {
      return false;
    }
    if (!this.source_name.equals(other.source_name)) {
      return false;
    }
    if (!this.target_module.equals(other.target_module)) {
      return false;
    }
    if (!this.target_name.equals(other.target_name)) {
      return false;
    }
    return true;
  }

  public @Nonnull ModulePath getSourceModule()
  {
    return this.source_module;
  }

  public @Nonnull ModulePathFlat getSourceModuleFlat()
  {
    return this.source_module_flat;
  }

  public @Nonnull TokenIdentifierLower getSourceName()
  {
    return this.source_name;
  }

  public @Nonnull ModulePath getTargetModule()
  {
    return this.target_module;
  }

  public @Nonnull ModulePathFlat getTargetModuleFlat()
  {
    return this.target_module_flat;
  }

  public @Nonnull TokenIdentifierLower getTargetName()
  {
    return this.target_name;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.source_module.hashCode();
    result = (prime * result) + this.source_name.hashCode();
    result = (prime * result) + this.target_module.hashCode();
    result = (prime * result) + this.target_name.hashCode();
    return result;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[TASTReference ");
    builder.append(this.source_module);
    builder.append(" ");
    builder.append(this.source_name);
    builder.append(" -> ");
    builder.append(this.target_module);
    builder.append(" ");
    builder.append(this.target_name);
    builder.append("]");
    return builder.toString();
  }
}