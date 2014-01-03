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

package com.io7m.jparasol.untyped.ast.resolved;

import java.util.List;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDShaderFragment;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDShaderFragmentInput;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDShaderFragmentOutput;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDShaderFragmentOutputAssignment;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDShaderFragmentParameter;

public interface UASTRFragmentShaderVisitor<F, PI, PP, PO, L, O, E extends Throwable>
{
  public @Nonnull F fragmentShaderVisit(
    final @Nonnull List<PI> inputs,
    final @Nonnull List<PP> parameters,
    final @Nonnull List<PO> outputs,
    final @Nonnull List<L> locals,
    final @Nonnull List<O> output_assignments,
    final @Nonnull UASTRDShaderFragment f)
    throws E,
      ConstraintError;

  public @Nonnull PI fragmentShaderVisitInput(
    final @Nonnull UASTRDShaderFragmentInput i)
    throws E,
      ConstraintError;

  public @Nonnull
    UASTRFragmentShaderLocalVisitor<L, E>
    fragmentShaderVisitLocalsPre()
      throws E,
        ConstraintError;

  public @Nonnull PO fragmentShaderVisitOutput(
    final @Nonnull UASTRDShaderFragmentOutput o)
    throws E,
      ConstraintError;

  public @Nonnull O fragmentShaderVisitOutputAssignment(
    final @Nonnull UASTRDShaderFragmentOutputAssignment a)
    throws E,
      ConstraintError;

  public @Nonnull PP fragmentShaderVisitParameter(
    final @Nonnull UASTRDShaderFragmentParameter p)
    throws E,
      ConstraintError;
}
