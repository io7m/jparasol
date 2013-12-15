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

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertex;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertexInput;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertexLocalValue;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertexOutput;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertexOutputAssignment;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertexParameter;

public interface UASTIVertexShaderVisitor<S extends UASTIStatus, E extends Throwable>
{
  public void vertexShaderVisit(
    final @Nonnull UASTIDShaderVertex<S> v)
    throws E,
      ConstraintError;

  public void vertexShaderVisitInput(
    final @Nonnull UASTIDShaderVertexInput<S> i)
    throws E,
      ConstraintError;

  public void vertexShaderVisitLocalValue(
    final @Nonnull UASTIDShaderVertexLocalValue<S> v)
    throws E,
      ConstraintError;

  public void vertexShaderVisitOutput(
    final @Nonnull UASTIDShaderVertexOutput<S> o)
    throws E,
      ConstraintError;

  public void vertexShaderVisitOutputAssignment(
    final @Nonnull UASTIDShaderVertexOutputAssignment<S> a)
    throws E,
      ConstraintError;

  public void vertexShaderVisitParameter(
    final @Nonnull UASTIDShaderVertexParameter<S> p)
    throws E,
      ConstraintError;
}
