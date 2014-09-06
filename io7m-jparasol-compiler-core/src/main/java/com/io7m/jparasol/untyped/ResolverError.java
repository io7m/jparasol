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

package com.io7m.jparasol.untyped;

import java.io.File;
import java.util.List;

import com.io7m.jparasol.CompilerError;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.lexer.Position;
import com.io7m.jparasol.lexer.TokenIdentifierLower;
import com.io7m.jparasol.lexer.TokenIdentifierUpper;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDImport;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDModule;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShader;

/**
 * Errors raised during the name resolution phase.
 * 
 * @see ResolverError
 */

public final class ResolverError extends CompilerError
{
  /**
   * Name resolution error codes.
   */

  public static enum Code
  {
    /**
     * A cyclic import was detected.
     */

    RESOLVER_IMPORT_CYCLIC,

    /**
     * Attempted to import an unknown module.
     */

    RESOLVER_IMPORT_UNKNOWN,

    /**
     * Attempted to reference an unknown module.
     */

    RESOLVER_MODULE_REFERENCE_UNKNOWN,

    /**
     * Attempted to reference a nonexistent shader.
     */

    RESOLVER_SHADER_NONEXISTENT,

    /**
     * Attempted to reference a nonexistent shader output.
     */

    RESOLVER_SHADER_OUTPUT_NONEXISTENT,

    /**
     * Attempted to write a locally recursive shader.
     */

    RESOLVER_SHADER_RECURSIVE_LOCAL,

    /**
     * Attempted to write mutually-recursive shader.
     */

    RESOLVER_SHADER_RECURSIVE_MUTUAL,

    /**
     * Attempted to reference a nonexistent term.
     */

    RESOLVER_TERM_NONEXISTENT,

    /**
     * Attempted to write a locally recursive term.
     */

    RESOLVER_TERM_RECURSIVE_LOCAL,

    /**
     * Attempted to write mutually-recursive term.
     */

    RESOLVER_TERM_RECURSIVE_MUTUAL,

    /**
     * Attempted to reference a nonexistent type.
     */

    RESOLVER_TYPE_NONEXISTENT,

    /**
     * Attempted to write a locally recursive type.
     */

    RESOLVER_TYPE_RECURSIVE_LOCAL,

    /**
     * Attempted to write a mutually recursive type.
     */

    RESOLVER_TYPE_RECURSIVE_MUTUAL,
  }

  private static final long serialVersionUID = 2073538624493476012L;

  /**
   * @return A name resolution error
   */

  public static ResolverError moduleImportCyclic(
    final UASTUDImport first,
    final ModulePathFlat target,
    final List<UASTUDImport> imports_)
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
      m.append("  → Import of ");
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

  /**
   * @return A name resolution error
   */

  public static ResolverError moduleImportUnknown(
    final UASTUDImport i,
    final ModulePathFlat target)
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

  /**
   * @return A name resolution error
   */

  public static ResolverError moduleReferenceUnknown(
    final TokenIdentifierUpper name)
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

  /**
   * @return A name resolution error
   */

  public static ResolverError shaderNonexistent(
    final UASTUDModule module,
    final TokenIdentifierLower name)
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

  /**
   * @return A name resolution error
   */

  public static ResolverError shaderOutputNonexistent(
    final UASTUDShader shader,
    final TokenIdentifierLower name)
  {
    final StringBuilder m = new StringBuilder();
    m.append("The shader ");
    m.append(shader.getName().getActual());
    m.append(" does not contain an output named ");
    m.append(name.getActual());

    return new ResolverError(
      Code.RESOLVER_SHADER_OUTPUT_NONEXISTENT,
      name.getFile(),
      name.getPosition(),
      m.toString());
  }

  /**
   * @return A name resolution error
   */

  public static ResolverError shaderRecursiveLocal(
    final TokenIdentifierLower source_name,
    final TokenIdentifierLower target_name)
  {
    final StringBuilder m = new StringBuilder();
    m.append("Referring to the shader ");
    m.append(target_name.getActual());
    m.append(" defined at ");
    m.append(target_name.getFile());
    m.append(":");
    m.append(target_name.getPosition());
    m.append(" would result in a recursive shader");
    return new ResolverError(
      Code.RESOLVER_SHADER_RECURSIVE_LOCAL,
      source_name.getFile(),
      source_name.getPosition(),
      m.toString());
  }

  /**
   * @return A name resolution error
   */

  public static ResolverError shaderRecursiveMutual(
    final TokenIdentifierLower name,
    final List<TokenIdentifierLower> tokens)
  {
    final StringBuilder m = new StringBuilder();
    m.append("The reference to ");
    m.append(name.getActual());
    m.append(" results in a mutually recursive shader\n");
    m.append("The sequence of shaders that led to a cycle is:\n");

    m.append("  Shader ");
    m.append(name.getActual());
    m.append(" at ");
    m.append(name.getFile());
    m.append(":");
    m.append(name.getPosition());
    m.append("\n");

    for (final TokenIdentifierLower t : tokens) {
      m.append("  → Shader ");
      m.append(t.getActual());
      m.append(" at ");
      m.append(t.getFile());
      m.append(":");
      m.append(t.getPosition());
      m.append("\n");
    }

    return new ResolverError(
      Code.RESOLVER_SHADER_RECURSIVE_MUTUAL,
      name.getFile(),
      name.getPosition(),
      m.toString());
  }

  /**
   * @return A name resolution error
   */

  public static ResolverError termNonexistent(
    final UASTUDModule module,
    final TokenIdentifierLower name)
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

  /**
   * @return A name resolution error
   */

  public static ResolverError termRecursiveLocal(
    final TokenIdentifierLower source_name,
    final TokenIdentifierLower target_name)
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

  /**
   * @return A name resolution error
   */

  public static ResolverError termRecursiveMutual(
    final TokenIdentifierLower name,
    final List<TokenIdentifierLower> tokens)
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
      m.append("  → Term ");
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

  /**
   * @return A name resolution error
   */

  public static ResolverError typeNonexistent(
    final UASTUDModule module,
    final TokenIdentifierLower name)
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

  /**
   * @return A name resolution error
   */

  public static ResolverError typeRecursiveLocal(
    final TokenIdentifierLower source_name,
    final TokenIdentifierLower target_name)
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

  /**
   * @return A name resolution error
   */

  public static ResolverError typeRecursiveMutual(
    final TokenIdentifierLower name,
    final List<TokenIdentifierLower> tokens)
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
      m.append("  → Type ");
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

  private final Code code;

  private ResolverError(
    final Code in_code,
    final File file,
    final Position position,
    final String message)
  {
    super(message, file, position);
    this.code = in_code;
  }

  @Override public String getCategory()
  {
    return "name-resolution";
  }

  /**
   * @return The error code
   */

  public Code getCode()
  {
    return this.code;
  }
}
