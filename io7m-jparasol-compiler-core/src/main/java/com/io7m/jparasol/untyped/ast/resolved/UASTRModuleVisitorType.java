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
import java.util.Map;

import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDImport;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDModule;

// CHECKSTYLE_JAVADOC:OFF

public interface UASTRModuleVisitorType<M, I, D, DTE extends D, DTY extends D, DS extends D, E extends Throwable>
{
  UASTRShaderVisitorType<DS, E> moduleShadersPre(
    final UASTRDModule m)
    throws E;

  UASTRTermVisitorType<DTE, E> moduleTermsPre(
    final UASTRDModule m)
    throws E;

  UASTRTypeVisitorType<DTY, E> moduleTypesPre(
    final UASTRDModule m)
    throws E;

  M moduleVisit(
    final List<I> imports,
    final List<D> declarations,
    final Map<String, DTE> terms,
    final Map<String, DTY> types,
    final Map<String, DS> shaders,
    final UASTRDModule m)
    throws E;

  I moduleVisitImport(
    final UASTRDImport i)
    throws E;
}
