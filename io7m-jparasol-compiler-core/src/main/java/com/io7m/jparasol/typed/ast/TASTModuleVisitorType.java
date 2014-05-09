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
import java.util.Map;

import com.io7m.jnull.Nullable;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDImport;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDModule;

/**
 * The type of module visitors.
 * 
 * @param <M>
 *          The type of transformed modules.
 * @param <I>
 *          The type of transformed imports.
 * @param <D>
 *          The type of transformed declarations.
 * @param <DTE>
 *          The type of transformed term declarations.
 * @param <DTY>
 *          The type of transformed type declarations.
 * @param <DS>
 *          The type of transformed shader declarations.
 * @param <E>
 *          The type of raised exceptions.
 */

public interface TASTModuleVisitorType<M, I, D, DTE extends D, DTY extends D, DS extends D, E extends Throwable>
{
  /**
   * Prepare to visit shaders.
   * 
   * @param m
   *          The module
   * @return A visitor, or <code>null</code> if no visiting is desired.
   * @throws E
   *           If required
   */

  @Nullable TASTShaderVisitorType<DS, E> moduleShadersPre(
    final TASTDModule m)
    throws E;

  /**
   * Prepare to visit terms.
   * 
   * @param m
   *          The module
   * @return A visitor, or <code>null</code> if no visiting is desired.
   * @throws E
   *           If required
   */

  @Nullable TASTTermVisitorType<DTE, E> moduleTermsPre(
    final TASTDModule m)
    throws E;

  /**
   * Prepare to visit types.
   * 
   * @param m
   *          The module
   * @return A visitor, or <code>null</code> if no visiting is desired.
   * @throws E
   *           If required
   */

  @Nullable TASTTypeVisitorType<DTY, E> moduleTypesPre(
    final TASTDModule m)
    throws E;

  /**
   * Visit a module.
   * 
   * @param imports
   *          The transformed imports
   * @param declarations
   *          The transformed declarations
   * @param terms
   *          The transformed terms
   * @param types
   *          The transformed types
   * @param shaders
   *          The transformed shaders
   * @param m
   *          The original module
   * @return A transformed module
   * @throws E
   *           If required
   */

  M moduleVisit(
    final List<I> imports,
    final List<D> declarations,
    final Map<String, DTE> terms,
    final Map<String, DTY> types,
    final Map<String, DS> shaders,
    final TASTDModule m)
    throws E;

  /**
   * Visit an import.
   * 
   * @param i
   *          The original import
   * @return A transformed import
   * @throws E
   *           If required
   */

  I moduleVisitImport(
    final TASTDImport i)
    throws E;
}
