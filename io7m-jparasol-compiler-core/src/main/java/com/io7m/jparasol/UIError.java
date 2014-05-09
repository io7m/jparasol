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

import com.io7m.jfunctional.Pair;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.Nullable;
import com.io7m.jparasol.lexer.Position;
import com.io7m.jparasol.typed.ast.TASTCompilation;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDModule;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShader;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragment;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderProgram;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderVertex;
import com.io7m.jparasol.typed.ast.TASTShaderNameFlat;
import com.io7m.jparasol.typed.ast.TASTShaderVisitorType;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * The type of UI errors (that is; mistakes made by the user on the command
 * line).
 */

public final class UIError extends CompilerError
{
  static enum Code
  {
    UI_ERROR_BAD_BATCH,
    UI_ERROR_COMMAND_LINE_INCORRECT,
    UI_ERROR_GLSL_VERSION_UNKNOWN,
    UI_ERROR_SHADER_NAME_UNPARSEABLE,
    UI_ERROR_SHADER_NONEXISTENT,
    UI_ERROR_SHADER_NOT_PROGRAM
  }

  private static final long serialVersionUID;

  static {
    serialVersionUID = 9123684816142549504L;
  }

  /**
   * @return A new error indicating a bad batch.
   */

  public static UIError badBatch(
    final int line_no,
    final File batch_file,
    final String message)
  {
    return new UIError(
      Code.UI_ERROR_BAD_BATCH,
      message,
      batch_file,
      new Position(line_no, 0));
  }

  /**
   * @return A new error indicating a generally incorrect command line.
   */

  public static UIError incorrectCommandLine(
    final String message)
  {
    return new UIError(
      Code.UI_ERROR_COMMAND_LINE_INCORRECT,
      message,
      new File("<none>"),
      new Position(0, 0));
  }

  /**
   * @return A new error indicating an unparseable shader name.
   */

  public static UIError shaderNameUnparseable(
    final String name,
    final Pair<File, Position> meta,
    final @Nullable String extra)
  {
    final StringBuilder m = new StringBuilder();
    m.append("Could not parse ");
    m.append(name);
    m.append(" as a shader name.\n");
    if (extra != null) {
      m.append("Problem: ");
      m.append(extra);
      m.append("\n");
    }
    m.append("Shader names must be in the format:\n");
    m.append("  package-path , \".\" , module-name , \".\" , shader-name\n");
    m.append("    Where:\n");
    m.append("      package-path := lowercase , (\".\" , lowercase )*\n");
    m.append("      module-name  := uppercase\n");
    m.append("      shader-name  := lowercase\n");
    m.append("\n");
    m.append("  Example: com.io7m.example.M.p\n");
    m.append("  Example: x.M.s\n");

    final String r = m.toString();
    assert r != null;
    return new UIError(
      Code.UI_ERROR_SHADER_NAME_UNPARSEABLE,
      r,
      meta.getLeft(),
      meta.getRight());
  }

  /**
   * @return A new error indicating a nonexistent shader program.
   */

  public static UIError shaderProgramNonexistent(
    final TASTShaderNameFlat name,
    final TASTCompilation typed)
  {
    final StringBuilder m = new StringBuilder();
    m.append("The named shader program ");
    m.append(name.show());
    m.append(" does not exist.\n");

    final Map<ModulePathFlat, TASTDModule> modules = typed.getModules();
    if (modules.containsKey(name.getModulePath())) {
      final TASTDModule module = modules.get(name.getModulePath());
      assert module != null;
      UIError.showProgramsInModule(name, m, module);
    } else {
      m.append("The module ");
      m.append(name.getModulePath().getActual());
      m.append(" does not exist");
    }

    final String r = m.toString();
    assert r != null;
    return new UIError(
      Code.UI_ERROR_SHADER_NONEXISTENT,
      r,
      new File("<none>"),
      new Position(0, 0));
  }

  /**
   * @return A new error indicating the the given name does not correspond to
   *         a program.
   */

  public static UIError shaderProgramNotProgram(
    final TASTShaderNameFlat name,
    final TASTDShader p,
    final TASTCompilation typed)
  {
    final StringBuilder m = new StringBuilder();
    m.append("The named shader program ");
    m.append(name.show());
    m.append(" is not a program, it is a ");

    p
      .shaderVisitableAccept(new TASTShaderVisitorType<Unit, UnreachableCodeException>() {
        @Override public Unit moduleVisitFragmentShader(
          final TASTDShaderFragment f)
        {
          m.append("fragment shader");
          return Unit.unit();
        }

        @Override public Unit moduleVisitProgramShader(
          final TASTDShaderProgram _)
        {
          throw new UnreachableCodeException();
        }

        @Override public Unit moduleVisitVertexShader(
          final TASTDShaderVertex f)
        {
          m.append("vertex shader");
          return Unit.unit();
        }
      });

    final Map<ModulePathFlat, TASTDModule> modules = typed.getModules();
    assert modules.containsKey(name.getModulePath());
    final TASTDModule module = modules.get(name.getModulePath());
    assert module != null;
    UIError.showProgramsInModule(name, m, module);

    final String r = m.toString();
    assert r != null;
    return new UIError(
      Code.UI_ERROR_SHADER_NOT_PROGRAM,
      r,
      new File("<none>"),
      new Position(0, 0));
  }

  private static void showProgramsInModule(
    final TASTShaderNameFlat name,
    final StringBuilder m,
    final TASTDModule module)
  {
    boolean found = false;
    for (final String s_name : module.getShaders().keySet()) {
      final TASTDShader shader = module.getShaders().get(s_name);
      if (shader instanceof TASTDShaderProgram) {
        if (found == false) {
          m.append("\nOther shader programs in the module include:\n");
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

  /**
   * @return A new error indicating an unknown GLSL version.
   */

  public static UIError versionUnknown(
    final int v)
  {
    final StringBuilder m = new StringBuilder();
    m.append("Unknown GLSL version ");
    m.append(v);

    final String r = m.toString();
    assert r != null;
    return new UIError(Code.UI_ERROR_GLSL_VERSION_UNKNOWN, r, new File(
      "<none>"), new Position(0, 0));
  }

  /**
   * @return A new error indicating an unknown GLSL ES version.
   */

  public static UIError versionUnknownES(
    final int v)
  {
    final StringBuilder m = new StringBuilder();
    m.append("Unknown GLSL ES version ");
    m.append(v);

    final String r = m.toString();
    assert r != null;
    return new UIError(Code.UI_ERROR_GLSL_VERSION_UNKNOWN, r, new File(
      "<none>"), new Position(0, 0));
  }

  private final Code code;

  private UIError(
    final Code in_code,
    final String message,
    final File file,
    final Position position)
  {
    super(message, file, position);
    this.code = in_code;
  }

  @Override public String getCategory()
  {
    return "ui-error";
  }

  /**
   * @return The error code.
   */

  Code getCode()
  {
    return this.code;
  }
}
