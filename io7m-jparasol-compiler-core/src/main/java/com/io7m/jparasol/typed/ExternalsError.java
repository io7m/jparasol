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

package com.io7m.jparasol.typed;

import java.io.File;

import org.jgrapht.GraphPath;

import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.CompilerError;
import com.io7m.jparasol.lexer.Position;
import com.io7m.jparasol.typed.ast.TASTReference;
import com.io7m.jparasol.typed.ast.TASTShaderNameFlat;
import com.io7m.jparasol.typed.ast.TASTTermNameFlat;

/**
 * The type of errors raised by the externals checker.
 * 
 * @see Externals
 */

public final class ExternalsError extends CompilerError
{
  /**
   * Error codes for the externals checker.
   */

  public static enum Code
  {
    /**
     * External not allowed in fragment shader.
     */

    EXTERNALS_DISALLOWED_IN_FRAGMENT_SHADER,

    /**
     * External not allowed in vertex shader.
     */

    EXTERNALS_DISALLOWED_IN_VERTEX_SHADER
  }

  private static final long serialVersionUID = -4604639514273970019L;

  /**
   * An error indicating that a term cannot be used in a fragment shader.
   * 
   * @param start_shader
   *          The shader
   * @param start_term_reference
   *          The term reference
   * @param final_term
   *          The resulting term
   * @param path
   *          The path to the term
   * @return An error
   */

  public static ExternalsError termDisallowedInFragmentShader(
    final TASTShaderNameFlat start_shader,
    final TASTReference start_term_reference,
    final TASTTermNameFlat final_term,
    final GraphPath<TASTTermNameFlat, TASTReference> path)
  {
    NullCheck.notNull(start_shader, "Shader");
    NullCheck.notNull(start_term_reference, "Initial term");
    NullCheck.notNull(final_term, "Final term");
    NullCheck.notNull(path, "Graph path");

    final StringBuilder m = new StringBuilder();
    m.append("The external function ");
    m.append(final_term.show());
    m.append(" cannot be used from fragment shaders.\n");
    m.append("The sequence of terms that led to this function is:\n");

    m.append("  Shader ");
    m.append(start_shader.show());
    m.append("\n");
    m.append("    Term ");
    m.append(start_term_reference.getTargetName().getActual());
    m.append(" (");
    m.append(start_term_reference.getTargetName().getFile());
    m.append(":");
    m.append(start_term_reference.getTargetName().getPosition());
    m.append(")\n");

    for (final TASTReference e : path.getEdgeList()) {
      m.append("      → Term ");
      m.append(e.getSourceModuleFlat().getActual());
      m.append(".");
      m.append(e.getSourceName().getActual());
      m.append(" (");
      m.append(e.getSourceName().getFile());
      m.append(":");
      m.append(e.getSourceName().getPosition());
      m.append(")\n");
    }

    m.append("      → Term ");
    m.append(final_term.show());

    final String r = m.toString();
    assert r != null;
    return new ExternalsError(
      Code.EXTERNALS_DISALLOWED_IN_FRAGMENT_SHADER,
      r,
      start_term_reference.getSourceName().getFile(),
      start_term_reference.getSourceName().getPosition());
  }

  /**
   * An error indicating that a term cannot be used in a vertex shader.
   * 
   * @param start_shader
   *          The shader
   * @param start_term_reference
   *          The term reference
   * @param final_term
   *          The resulting term
   * @param path
   *          The path to the term
   * @return An error
   */

  public static ExternalsError termDisallowedInVertexShader(
    final TASTShaderNameFlat start_shader,
    final TASTReference start_term_reference,
    final TASTTermNameFlat final_term,
    final GraphPath<TASTTermNameFlat, TASTReference> path)
  {
    NullCheck.notNull(start_shader, "Shader");
    NullCheck.notNull(start_term_reference, "Initial term");
    NullCheck.notNull(final_term, "Final term");
    NullCheck.notNull(path, "Graph path");

    final StringBuilder m = new StringBuilder();
    m.append("The external function ");
    m.append(final_term.show());
    m.append(" cannot be used from vertex shaders.\n");
    m.append("The sequence of terms that led to this function is:\n");

    m.append("  Shader ");
    m.append(start_shader.show());
    m.append("\n");
    m.append("    Term ");
    m.append(start_term_reference.getTargetName().getActual());
    m.append(" (");
    m.append(start_term_reference.getTargetName().getFile());
    m.append(":");
    m.append(start_term_reference.getTargetName().getPosition());
    m.append(")\n");

    for (final TASTReference e : path.getEdgeList()) {
      m.append("      → Term ");
      m.append(e.getSourceModuleFlat().getActual());
      m.append(".");
      m.append(e.getSourceName().getActual());
      m.append(" (");
      m.append(e.getSourceName().getFile());
      m.append(":");
      m.append(e.getSourceName().getPosition());
      m.append(")\n");
    }

    m.append("      → Term ");
    m.append(final_term.show());

    final String r = m.toString();
    assert r != null;
    return new ExternalsError(
      Code.EXTERNALS_DISALLOWED_IN_VERTEX_SHADER,
      r,
      start_term_reference.getSourceName().getFile(),
      start_term_reference.getSourceName().getPosition());
  }

  private final Code code;

  private ExternalsError(
    final Code in_code,
    final String message,
    final File file,
    final Position position)
  {
    super(message, file, position);
    this.code = NullCheck.notNull(in_code, "Code");
  }

  /**
   * @return The error code
   */

  public Code getCode()
  {
    return this.code;
  }

  @Override public String getCategory()
  {
    return "externals-access";
  }
}
