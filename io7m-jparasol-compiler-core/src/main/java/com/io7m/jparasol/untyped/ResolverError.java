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

package com.io7m.jparasol.untyped;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.CompilerError;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.lexer.Position;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.lexer.Token.TokenIdentifierUpper;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDImport;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDModule;

public final class ResolverError extends CompilerError
{
  public static enum Code
  {
    RESOLVER_IMPORT_CYCLIC,
    RESOLVER_IMPORT_UNKNOWN,
    RESOLVER_MODULE_REFERENCE_UNKNOWN,
    RESOLVER_SHADER_NONEXISTENT,
    RESOLVER_TERM_NONEXISTENT,
    RESOLVER_TERM_RECURSIVE_LOCAL,
    RESOLVER_TERM_RECURSIVE_MUTUAL,
    RESOLVER_TYPE_NONEXISTENT,
    RESOLVER_TYPE_RECURSIVE_LOCAL,
    RESOLVER_TYPE_RECURSIVE_MUTUAL,
  }

  private static final long serialVersionUID = 2073538624493476012L;

  public static @Nonnull ResolverError moduleImportCyclic(
    final @Nonnull UASTUDImport first,
    final @Nonnull ModulePathFlat target,
    final @Nonnull ArrayList<UASTUDImport> imports_)
    throws ConstraintError
  {
    final StringBuilder m = new StringBuilder();
    m.append("Cyclic import of module ");
    m.append(target.getActual());
    m.append("\n");
    m.append("The sequence of imports that led to a cycle is:\n");

    m.append("  Import of ");
    m.append(target.getActual());
    m.append(" at ");
    m.append(first.getPath().getName().getFile());
    m.append(":");
    m.append(first.getPath().getName().getPosition());
    m.append("\n");

    for (final UASTUDImport i : imports_) {
      m.append("  -> Import of ");
      m.append(ModulePathFlat.fromModulePath(i.getPath()).getActual());
      m.append(" at ");
      m.append(i.getPath().getName().getFile());
      m.append(":");
      m.append(i.getPath().getName().getPosition());
      m.append("\n");
    }

    final TokenIdentifierUpper token = first.getPath().getName();
    return new ResolverError(
      Code.RESOLVER_IMPORT_CYCLIC,
      token.getFile(),
      token.getPosition(),
      m.toString());
  }

  public static @Nonnull ResolverError moduleImportUnknown(
    final @Nonnull UASTUDImport i,
    final @Nonnull ModulePathFlat target)
    throws ConstraintError
  {
    final StringBuilder m = new StringBuilder();
    m.append("Nonexistent module ");
    m.append(target.getActual());

    final TokenIdentifierUpper token = i.getPath().getName();
    return new ResolverError(
      Code.RESOLVER_IMPORT_UNKNOWN,
      token.getFile(),
      token.getPosition(),
      m.toString());
  }

  public static @Nonnull ResolverError moduleReferenceUnknown(
    final @Nonnull TokenIdentifierUpper name)
    throws ConstraintError
  {
    final StringBuilder m = new StringBuilder();
    m.append("There is no module imported with the name ");
    m.append(name.getActual());

    return new ResolverError(
      Code.RESOLVER_MODULE_REFERENCE_UNKNOWN,
      name.getFile(),
      name.getPosition(),
      m.toString());
  }

  public static @Nonnull ResolverError shaderNonexistent(
    final @Nonnull UASTUDModule module,
    final @Nonnull TokenIdentifierLower name)
    throws ConstraintError
  {
    final StringBuilder m = new StringBuilder();
    m.append("The module ");
    m.append(ModulePathFlat.fromModulePath(module.getPath()).getActual());
    m.append(" does not contain a shader named ");
    m.append(name.getActual());

    return new ResolverError(
      Code.RESOLVER_SHADER_NONEXISTENT,
      name.getFile(),
      name.getPosition(),
      m.toString());
  }

  public static @Nonnull ResolverError termNonexistent(
    final @Nonnull UASTUDModule module,
    final @Nonnull TokenIdentifierLower name)
    throws ConstraintError
  {
    final StringBuilder m = new StringBuilder();
    m.append("The module ");
    m.append(ModulePathFlat.fromModulePath(module.getPath()).getActual());
    m.append(" does not contain a term named ");
    m.append(name.getActual());

    return new ResolverError(
      Code.RESOLVER_TERM_NONEXISTENT,
      name.getFile(),
      name.getPosition(),
      m.toString());
  }

  public static @Nonnull ResolverError termRecursiveLocal(
    final @Nonnull TokenIdentifierLower source_name,
    final @Nonnull TokenIdentifierLower target_name)
    throws ConstraintError
  {
    final StringBuilder m = new StringBuilder();
    m.append("Referring to the term ");
    m.append(target_name.getActual());
    m.append(" defined at ");
    m.append(target_name.getFile());
    m.append(":");
    m.append(target_name.getPosition());
    m.append(" would result in a recursive term");
    return new ResolverError(
      Code.RESOLVER_TERM_RECURSIVE_LOCAL,
      source_name.getFile(),
      source_name.getPosition(),
      m.toString());
  }

  public static @Nonnull ResolverError termRecursiveMutual(
    final @Nonnull TokenIdentifierLower name,
    final @Nonnull List<TokenIdentifierLower> tokens)
    throws ConstraintError
  {
    final StringBuilder m = new StringBuilder();
    m.append("The reference to ");
    m.append(name.getActual());
    m.append(" results in a mutually recursive term\n");
    m.append("The sequence of terms that led to a cycle is:\n");

    m.append("  Term ");
    m.append(name.getActual());
    m.append(" at ");
    m.append(name.getFile());
    m.append(":");
    m.append(name.getPosition());
    m.append("\n");

    for (final TokenIdentifierLower t : tokens) {
      m.append("  -> Term ");
      m.append(t.getActual());
      m.append(" at ");
      m.append(t.getFile());
      m.append(":");
      m.append(t.getPosition());
      m.append("\n");
    }

    return new ResolverError(
      Code.RESOLVER_TERM_RECURSIVE_MUTUAL,
      name.getFile(),
      name.getPosition(),
      m.toString());
  }

  public static @Nonnull ResolverError typeNonexistent(
    final @Nonnull UASTUDModule module,
    final @Nonnull TokenIdentifierLower name)
    throws ConstraintError
  {
    final StringBuilder m = new StringBuilder();
    m.append("The module ");
    m.append(ModulePathFlat.fromModulePath(module.getPath()).getActual());
    m.append(" does not contain a type named ");
    m.append(name.getActual());

    return new ResolverError(
      Code.RESOLVER_TYPE_NONEXISTENT,
      name.getFile(),
      name.getPosition(),
      m.toString());
  }

  public static @Nonnull ResolverError typeRecursiveLocal(
    final @Nonnull TokenIdentifierLower source_name,
    final @Nonnull TokenIdentifierLower target_name)
    throws ConstraintError
  {
    final StringBuilder m = new StringBuilder();
    m.append("Referring to the type ");
    m.append(target_name.getActual());
    m.append(" defined at ");
    m.append(target_name.getFile());
    m.append(":");
    m.append(target_name.getPosition());
    m.append(" would result in a recursive type");
    return new ResolverError(
      Code.RESOLVER_TYPE_RECURSIVE_LOCAL,
      source_name.getFile(),
      source_name.getPosition(),
      m.toString());
  }

  public static @Nonnull ResolverError typeRecursiveMutual(
    final @Nonnull TokenIdentifierLower name,
    final @Nonnull List<TokenIdentifierLower> tokens)
    throws ConstraintError
  {
    final StringBuilder m = new StringBuilder();
    m.append("The reference to ");
    m.append(name.getActual());
    m.append(" results in a mutually recursive type\n");
    m.append("The sequence of types that led to a cycle is:\n");

    m.append("  Type ");
    m.append(name.getActual());
    m.append(" at ");
    m.append(name.getFile());
    m.append(":");
    m.append(name.getPosition());
    m.append("\n");

    for (final TokenIdentifierLower t : tokens) {
      m.append("  -> Type ");
      m.append(t.getActual());
      m.append(" at ");
      m.append(t.getFile());
      m.append(":");
      m.append(t.getPosition());
      m.append("\n");
    }

    return new ResolverError(
      Code.RESOLVER_TYPE_RECURSIVE_MUTUAL,
      name.getFile(),
      name.getPosition(),
      m.toString());
  }

  private final @Nonnull Code code;

  private ResolverError(
    final @Nonnull Code code,
    final @Nonnull File file,
    final @Nonnull Position position,
    final @Nonnull String message)
    throws ConstraintError
  {
    super(message, file, position);
    this.code = code;
  }

  public @Nonnull Code getCode()
  {
    return this.code;
  }
}
