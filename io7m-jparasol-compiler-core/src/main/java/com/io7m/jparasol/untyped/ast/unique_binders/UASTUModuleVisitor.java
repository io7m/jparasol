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
import java.util.Map;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDImport;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDModule;

public interface UASTUModuleVisitor<M, I, D, DTE extends D, DTY extends D, DS extends D, E extends Throwable>
{
  public UASTUShaderVisitor<DS, E> moduleShadersPre(
    final @Nonnull UASTUDModule m)
    throws E,
      ConstraintError;

  public UASTUTermVisitor<DTE, E> moduleTermsPre(
    final @Nonnull UASTUDModule m)
    throws E,
      ConstraintError;

  public UASTUTypeVisitor<DTY, E> moduleTypesPre(
    final @Nonnull UASTUDModule m)
    throws E,
      ConstraintError;

  public M moduleVisit(
    final @Nonnull List<I> imports,
    final @Nonnull List<D> declarations,
    final @Nonnull Map<String, DTE> terms,
    final @Nonnull Map<String, DTY> types,
    final @Nonnull Map<String, DS> shaders,
    final @Nonnull UASTUDModule m)
    throws E,
      ConstraintError;

  public I moduleVisitImport(
    final @Nonnull UASTUDImport i)
    throws E,
      ConstraintError;
}