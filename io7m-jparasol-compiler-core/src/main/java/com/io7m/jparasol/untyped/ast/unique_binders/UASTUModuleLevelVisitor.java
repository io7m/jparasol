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

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDFunctionDefined;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDFunctionExternal;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDImport;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDModule;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderFragment;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderProgram;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderVertex;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDTypeRecord;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDValue;

public interface UASTUModuleLevelVisitor<E extends Throwable>
{
  public void moduleVisitFragmentShader(
    final @Nonnull UASTUDShaderFragment f)
    throws E,
      ConstraintError;

  public void moduleVisitFunctionDefined(
    final @Nonnull UASTUDFunctionDefined f)
    throws E,
      ConstraintError;

  public void moduleVisitFunctionExternal(
    final @Nonnull UASTUDFunctionExternal f)
    throws E,
      ConstraintError;

  public void moduleVisitImport(
    final @Nonnull UASTUDImport i)
    throws E,
      ConstraintError;

  public void moduleVisitModule(
    final @Nonnull UASTUDModule m)
    throws E,
      ConstraintError;

  public void moduleVisitProgramShader(
    final @Nonnull UASTUDShaderProgram p)
    throws E,
      ConstraintError;

  public void moduleVisitTypeRecord(
    final @Nonnull UASTUDTypeRecord r)
    throws E,
      ConstraintError;

  public void moduleVisitValue(
    final @Nonnull UASTUDValue v)
    throws E,
      ConstraintError;

  public void moduleVisitVertexShader(
    final @Nonnull UASTUDShaderVertex f)
    throws E,
      ConstraintError;
}
