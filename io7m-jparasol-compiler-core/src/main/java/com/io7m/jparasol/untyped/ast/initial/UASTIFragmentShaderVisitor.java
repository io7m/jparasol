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

package com.io7m.jparasol.untyped.ast.initial;

import java.util.List;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragment;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentInput;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentOutput;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentOutputAssignment;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentParameter;

public interface UASTIFragmentShaderVisitor<F, P, L, O, S extends UASTIStatus, E extends Throwable>
{
  public @Nonnull
    UASTIFragmentShaderLocalVisitor<L, S, E>
    fragmentShaderVisitPre(
      final @Nonnull UASTIDShaderFragment<S> f)
      throws E,
        ConstraintError;

  public F fragmentShaderVisit(
    final @Nonnull List<P> parameters,
    final @Nonnull List<L> locals,
    final @Nonnull List<O> output_assignments,
    final @Nonnull UASTIDShaderFragment<S> f)
    throws E,
      ConstraintError;

  public P fragmentShaderVisitInput(
    final @Nonnull UASTIDShaderFragmentInput<S> i)
    throws E,
      ConstraintError;

  public P fragmentShaderVisitOutput(
    final @Nonnull UASTIDShaderFragmentOutput<S> o)
    throws E,
      ConstraintError;

  public O fragmentShaderVisitOutputAssignment(
    final @Nonnull UASTIDShaderFragmentOutputAssignment<S> a)
    throws E,
      ConstraintError;

  public P fragmentShaderVisitParameter(
    final @Nonnull UASTIDShaderFragmentParameter<S> p)
    throws E,
      ConstraintError;
}
