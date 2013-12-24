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

package com.io7m.jparasol.untyped.ast.unique_binders;

import java.util.List;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderFragment;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderFragmentInput;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderFragmentOutput;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderFragmentOutputAssignment;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderFragmentParameter;

public interface UASTUFragmentShaderVisitor<F, PI, PP, PO, L, O, E extends Throwable>
{
  public @Nonnull F fragmentShaderVisit(
    final @Nonnull List<PI> inputs,
    final @Nonnull List<PP> parameters,
    final @Nonnull List<PO> outputs,
    final @Nonnull List<L> locals,
    final @Nonnull List<O> output_assignments,
    final @Nonnull UASTUDShaderFragment f)
    throws E,
      ConstraintError;

  public @Nonnull PI fragmentShaderVisitInput(
    final @Nonnull UASTUDShaderFragmentInput i)
    throws E,
      ConstraintError;

  public @Nonnull
    UASTUFragmentShaderLocalVisitor<L, E>
    fragmentShaderVisitLocalsPre()
      throws E,
        ConstraintError;

  public @Nonnull PO fragmentShaderVisitOutput(
    final @Nonnull UASTUDShaderFragmentOutput o)
    throws E,
      ConstraintError;

  public @Nonnull O fragmentShaderVisitOutputAssignment(
    final @Nonnull UASTUDShaderFragmentOutputAssignment a)
    throws E,
      ConstraintError;

  public @Nonnull PP fragmentShaderVisitParameter(
    final @Nonnull UASTUDShaderFragmentParameter p)
    throws E,
      ConstraintError;
}
