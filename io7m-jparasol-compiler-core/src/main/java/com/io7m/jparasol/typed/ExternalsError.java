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

import javax.annotation.Nonnull;

import org.jgrapht.GraphPath;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.CompilerError;
import com.io7m.jparasol.lexer.Position;
import com.io7m.jparasol.typed.ast.TASTNameTermShaderFlat;
import com.io7m.jparasol.typed.ast.TASTReference;
import com.io7m.jparasol.typed.ast.TASTTermNameFlat;

public final class ExternalsError extends CompilerError
{
  static enum Code
  {
    EXTERNALS_DISALLOWED_IN_FRAGMENT_SHADER,
    EXTERNALS_DISALLOWED_IN_VERTEX_SHADER
  }

  private static final long serialVersionUID = -4604639514273970019L;

  public static @Nonnull ExternalsError termDisallowedInFragmentShader(
    final @Nonnull TASTNameTermShaderFlat.Shader start_shader,
    final @Nonnull TASTReference start_term_reference,
    final @Nonnull TASTTermNameFlat final_term,
    final @Nonnull GraphPath<TASTTermNameFlat, TASTReference> path)
    throws ConstraintError
  {
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
      m.append("      -> Term ");
      m.append(e.getSourceModuleFlat().getActual());
      m.append(".");
      m.append(e.getSourceName().getActual());
      m.append(" (");
      m.append(e.getSourceName().getFile());
      m.append(":");
      m.append(e.getSourceName().getPosition());
      m.append(")\n");
    }

    m.append("      -> Term ");
    m.append(final_term.show());

    return new ExternalsError(
      Code.EXTERNALS_DISALLOWED_IN_FRAGMENT_SHADER,
      m.toString(),
      start_term_reference.getSourceName().getFile(),
      start_term_reference.getSourceName().getPosition());
  }

  public static @Nonnull ExternalsError termDisallowedInVertexShader(
    final @Nonnull TASTNameTermShaderFlat.Shader start_shader,
    final @Nonnull TASTReference start_term_reference,
    final @Nonnull TASTTermNameFlat final_term,
    final @Nonnull GraphPath<TASTTermNameFlat, TASTReference> path)
    throws ConstraintError
  {
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
      m.append("      -> Term ");
      m.append(e.getSourceModuleFlat().getActual());
      m.append(".");
      m.append(e.getSourceName().getActual());
      m.append(" (");
      m.append(e.getSourceName().getFile());
      m.append(":");
      m.append(e.getSourceName().getPosition());
      m.append(")\n");
    }

    m.append("      -> Term ");
    m.append(final_term.show());

    return new ExternalsError(
      Code.EXTERNALS_DISALLOWED_IN_VERTEX_SHADER,
      m.toString(),
      start_term_reference.getSourceName().getFile(),
      start_term_reference.getSourceName().getPosition());
  }

  private final @Nonnull Code code;

  private ExternalsError(
    final @Nonnull Code in_code,
    final @Nonnull String message,
    final @Nonnull File file,
    final @Nonnull Position position)
    throws ConstraintError
  {
    super(message, file, position);
    this.code = Constraints.constrainNotNull(in_code, "Code");
  }

  public @Nonnull Code getCode()
  {
    return this.code;
  }

  @Override public String getCategory()
  {
    return "externals-access";
  }
}
