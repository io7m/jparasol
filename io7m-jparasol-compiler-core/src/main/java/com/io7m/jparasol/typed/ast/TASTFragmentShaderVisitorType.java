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

import java.util.List;

import com.io7m.jnull.Nullable;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragment;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragmentInput;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragmentOutput;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragmentOutputAssignment;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragmentParameter;

/**
 * The type of vertex shader visitors.
 * 
 * @param <F>
 *          The type of transformed fragment shaders
 * @param <PI>
 *          The type of transformed shader inputs
 * @param <PP>
 *          The type of transformed shader parameters
 * @param <PO>
 *          The type of transformed shader outputs
 * @param <L>
 *          The type of transformed shader locals
 * @param <O>
 *          The type of transformed shader output assignments
 * @param <E>
 *          The type of exceptions raised
 */

public interface TASTFragmentShaderVisitorType<F, PI, PP, PO, L, O, E extends Throwable>
{
  /**
   * Visit a shader.
   * 
   * @param inputs
   *          The transformed inputs
   * @param parameters
   *          The transformed parameters
   * @param outputs
   *          The transformed outputs
   * @param locals
   *          The transformed locals
   * @param output_assignments
   *          The transformed assignments
   * @param f
   *          The original shader
   * @return A transformed shader
   * @throws E
   *           If required
   */

  F fragmentShaderVisit(
    final List<PI> inputs,
    final List<PP> parameters,
    final List<PO> outputs,
    final List<L> locals,
    final List<O> output_assignments,
    final TASTDShaderFragment f)
    throws E;

  /**
   * Visit a shader input.
   * 
   * @param i
   *          The input
   * @return A transformed input
   * @throws E
   *           If required
   */

  PI fragmentShaderVisitInput(
    final TASTDShaderFragmentInput i)
    throws E;

  /**
   * Prepare to visit shader locals.
   * 
   * @return A local visitor, or <code>null</code> if visiting is not desired.
   * @throws E
   *           If required
   */

  @Nullable
    TASTFragmentShaderLocalVisitorType<L, E>
    fragmentShaderVisitLocalsPre()
      throws E;

  /**
   * Visit a shader output.
   * 
   * @param o
   *          The output
   * @return A transformed output
   * @throws E
   *           If required
   */

  PO fragmentShaderVisitOutput(
    final TASTDShaderFragmentOutput o)
    throws E;

  /**
   * Visit a shader output assignment.
   * 
   * @param a
   *          The output assignment
   * @return A transformed output assignment
   * @throws E
   *           If required
   */

  O fragmentShaderVisitOutputAssignment(
    final TASTDShaderFragmentOutputAssignment a)
    throws E;

  /**
   * Visit a shader parameter.
   * 
   * @param p
   *          The parameter
   * @return A transformed parameter
   * @throws E
   *           If required
   */

  PP fragmentShaderVisitParameter(
    final TASTDShaderFragmentParameter p)
    throws E;
}
