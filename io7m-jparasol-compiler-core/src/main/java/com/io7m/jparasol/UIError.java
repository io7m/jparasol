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

package com.io7m.jparasol;

import java.io.File;
import java.util.Map;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jaux.functional.Unit;
import com.io7m.jparasol.lexer.Position;
import com.io7m.jparasol.typed.ast.TASTCompilation;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDModule;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShader;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragment;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderProgram;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderVertex;
import com.io7m.jparasol.typed.ast.TASTShaderNameFlat;
import com.io7m.jparasol.typed.ast.TASTShaderVisitor;

public final class UIError extends CompilerError
{
  private static final long serialVersionUID;

  static {
    serialVersionUID = 9123684816142549504L;
  }

  static enum Code
  {
    UI_ERROR_SHADER_NONEXISTENT,
    UI_ERROR_SHADER_NOT_PROGRAM,
    UI_ERROR_GLSL_VERSION_UNKNOWN
  }

  private final @Nonnull Code code;

  private UIError(
    final @Nonnull Code code,
    final @Nonnull String message,
    final @Nonnull File file,
    final @Nonnull Position position)
    throws ConstraintError
  {
    super(message, file, position);
    this.code = code;
  }

  public static @Nonnull UIError shaderProgramNonexistent(
    final @Nonnull TASTShaderNameFlat name,
    final @Nonnull TASTCompilation typed)
    throws ConstraintError
  {
    final StringBuilder m = new StringBuilder();
    m.append("The named shader program ");
    m.append(name.show());
    m.append(" does not exist.\n");

    final Map<ModulePathFlat, TASTDModule> modules = typed.getModules();
    if (modules.containsKey(name.getModulePath())) {
      final TASTDModule module = modules.get(name.getModulePath());
      UIError.showProgramsInModule(name, m, module);
    } else {
      m.append("The module ");
      m.append(name.getModulePath().getActual());
      m.append(" does not exist");
    }

    return new UIError(
      Code.UI_ERROR_SHADER_NONEXISTENT,
      m.toString(),
      new File("<none>"),
      new Position(0, 0));
  }

  private static void showProgramsInModule(
    final @Nonnull TASTShaderNameFlat name,
    final @Nonnull StringBuilder m,
    final @Nonnull TASTDModule module)
  {
    boolean found = false;
    for (final String s_name : module.getShaders().keySet()) {
      final TASTDShader shader = module.getShaders().get(s_name);
      if (shader instanceof TASTDShaderProgram) {
        if (found == false) {
          m.append("Other shader programs in the module include:\n");
        }
        found = true;
        m.append("  ");
        m.append(name.getModulePath().show());
        m.append(".");
        m.append(s_name);
        m.append("\n");
      }
    }
  }

  public static @Nonnull UIError shaderProgramNotProgram(
    final @Nonnull TASTShaderNameFlat name,
    final @Nonnull TASTDShader p,
    final @Nonnull TASTCompilation typed)
    throws ConstraintError
  {
    final StringBuilder m = new StringBuilder();
    m.append("The named shader program ");
    m.append(name.show());
    m.append(" is not a program, it is a ");

    p.shaderVisitableAccept(new TASTShaderVisitor<Unit, ConstraintError>() {
      @Override public Unit moduleVisitFragmentShader(
        final @Nonnull TASTDShaderFragment f)
        throws ConstraintError,
          ConstraintError
      {
        m.append("fragment shader");
        return Unit.unit();
      }

      @Override public Unit moduleVisitProgramShader(
        final @Nonnull TASTDShaderProgram _)
        throws ConstraintError,
          ConstraintError
      {
        throw new UnreachableCodeException();
      }

      @Override public Unit moduleVisitVertexShader(
        final @Nonnull TASTDShaderVertex f)
        throws ConstraintError,
          ConstraintError
      {
        m.append("vertex shader");
        return Unit.unit();
      }
    });

    final Map<ModulePathFlat, TASTDModule> modules = typed.getModules();
    assert modules.containsKey(name.getModulePath());
    final TASTDModule module = modules.get(name.getModulePath());
    UIError.showProgramsInModule(name, m, module);

    return new UIError(
      Code.UI_ERROR_SHADER_NOT_PROGRAM,
      m.toString(),
      new File("<none>"),
      new Position(0, 0));
  }

  public static @Nonnull UIError versionUnknownES(
    final int v)
    throws ConstraintError
  {
    final StringBuilder m = new StringBuilder();
    m.append("Unknown GLSL ES version ");
    m.append(v);

    return new UIError(
      Code.UI_ERROR_GLSL_VERSION_UNKNOWN,
      m.toString(),
      new File("<none>"),
      new Position(0, 0));
  }

  public static @Nonnull UIError versionUnknown(
    final int v)
    throws ConstraintError
  {
    final StringBuilder m = new StringBuilder();
    m.append("Unknown GLSL version ");
    m.append(v);

    return new UIError(
      Code.UI_ERROR_GLSL_VERSION_UNKNOWN,
      m.toString(),
      new File("<none>"),
      new Position(0, 0));
  }
}
