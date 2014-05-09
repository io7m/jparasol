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
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;

/**
 * A reference from a term, type, or shader, to a term, type, or shader.
 */

@EqualityStructural public final class TASTReference
{
  private final ModulePath           source_module;
  private final ModulePathFlat       source_module_flat;
  private final TokenIdentifierLower source_name;
  private final ModulePath           target_module;
  private final ModulePathFlat       target_module_flat;
  private final TokenIdentifierLower target_name;

  /**
   * Construct a reference.
   * 
   * @param in_source_module
   *          The source module
   * @param in_source_name
   *          The source name
   * @param in_target_module
   *          The target module
   * @param in_target_name
   *          The target name
   */

  public TASTReference(
    final ModulePath in_source_module,
    final TokenIdentifierLower in_source_name,
    final ModulePath in_target_module,
    final TokenIdentifierLower in_target_name)
  {
    this.source_module = NullCheck.notNull(in_source_module, "Source module");
    this.source_name = NullCheck.notNull(in_source_name, "Source name");
    this.target_module = NullCheck.notNull(in_target_module, "Target module");
    this.target_name = NullCheck.notNull(in_target_name, "Target name");

    this.source_module_flat = ModulePathFlat.fromModulePath(in_source_module);
    this.target_module_flat = ModulePathFlat.fromModulePath(in_target_module);
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

  /**
   * @return The source module path
   */

  public ModulePath getSourceModule()
  {
    return this.source_module;
  }

  /**
   * @return The source module flattened path
   */

  public ModulePathFlat getSourceModuleFlat()
  {
    return this.source_module_flat;
  }

  /**
   * @return The source name
   */

  public TokenIdentifierLower getSourceName()
  {
    return this.source_name;
  }

  /**
   * @return The target module path
   */

  public ModulePath getTargetModule()
  {
    return this.target_module;
  }

  /**
   * @return The target module flattened path
   */

  public ModulePathFlat getTargetModuleFlat()
  {
    return this.target_module_flat;
  }

  /**
   * @return The target name
   */

  public TokenIdentifierLower getTargetName()
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
    builder.append(" → ");
    builder.append(this.target_module);
    builder.append(" ");
    builder.append(this.target_name);
    builder.append("]");
    final String r = builder.toString();
    assert r != null;
    return r;
  }
}
