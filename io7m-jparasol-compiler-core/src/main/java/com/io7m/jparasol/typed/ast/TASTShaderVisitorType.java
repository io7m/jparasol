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

import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragment;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderProgram;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderVertex;

/**
 * The type of shader visitors.
 * 
 * @param <T>
 *          The type of transformed shaders
 * @param <E>
 *          The type of exceptions raised
 */

public interface TASTShaderVisitorType<T, E extends Throwable>
{
  /**
   * Visit a fragment shader.
   * 
   * @param f
   *          The shader
   * @return A transformed fragment shader
   * @throws E
   *           If required
   */

  T moduleVisitFragmentShader(
    final TASTDShaderFragment f)
    throws E;

  /**
   * Visit a program shader.
   * 
   * @param p
   *          The shader
   * @return A transformed program shader
   * @throws E
   *           If required
   */

  T moduleVisitProgramShader(
    final TASTDShaderProgram p)
    throws E;

  /**
   * Visit a vertex shader.
   * 
   * @param f
   *          The shader
   * @return A transformed vertex shader
   * @throws E
   *           If required
   */

  T moduleVisitVertexShader(
    final TASTDShaderVertex f)
    throws E;
}
