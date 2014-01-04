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

package com.io7m.jparasol.untyped.ast.initial;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDFunctionDefined;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDFunctionExternal;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragment;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderProgram;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertex;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDTypeRecord;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDValue;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDValueExternal;

public interface UASTIModuleLevelDeclarationVisitor<D, E extends Throwable>
{
  public D moduleVisitFragmentShader(
    final @Nonnull UASTIDShaderFragment f)
    throws E,
      ConstraintError;

  public D moduleVisitFunctionDefined(
    final @Nonnull UASTIDFunctionDefined f)
    throws E,
      ConstraintError;

  public D moduleVisitFunctionExternal(
    final @Nonnull UASTIDFunctionExternal f)
    throws E,
      ConstraintError;

  public D moduleVisitProgramShader(
    final @Nonnull UASTIDShaderProgram p)
    throws E,
      ConstraintError;

  public D moduleVisitTypeRecord(
    final @Nonnull UASTIDTypeRecord r)
    throws E,
      ConstraintError;

  public D moduleVisitValue(
    final @Nonnull UASTIDValue v)
    throws E,
      ConstraintError;

  public D moduleVisitValueExternal(
    final @Nonnull UASTIDValueExternal v)
    throws E,
      ConstraintError;

  public D moduleVisitVertexShader(
    final @Nonnull UASTIDShaderVertex f)
    throws E,
      ConstraintError;
}
