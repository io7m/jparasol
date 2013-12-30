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

import java.util.List;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderVertex;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderVertexInput;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderVertexOutput;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderVertexOutputAssignment;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderVertexParameter;

public interface TASTVertexShaderVisitor<V, PI, PP, PO, L, O, E extends Throwable>
{
  public @Nonnull V vertexShaderVisit(
    final @Nonnull List<PI> inputs,
    final @Nonnull List<PP> parameters,
    final @Nonnull List<PO> outputs,
    final @Nonnull List<L> locals,
    final @Nonnull List<O> output_assignments,
    final @Nonnull TASTDShaderVertex v)
    throws E,
      ConstraintError;

  public @Nonnull PI vertexShaderVisitInput(
    final @Nonnull TASTDShaderVertexInput i)
    throws E,
      ConstraintError;

  public @Nonnull
    TASTVertexShaderLocalVisitor<L, E>
    vertexShaderVisitLocalsPre()
      throws E,
        ConstraintError;

  public @Nonnull PO vertexShaderVisitOutput(
    final @Nonnull TASTDShaderVertexOutput o)
    throws E,
      ConstraintError;

  public @Nonnull O vertexShaderVisitOutputAssignment(
    final @Nonnull TASTDShaderVertexOutputAssignment a)
    throws E,
      ConstraintError;

  public @Nonnull PP vertexShaderVisitParameter(
    final @Nonnull TASTDShaderVertexParameter p)
    throws E,
      ConstraintError;
}
