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

package com.io7m.jparasol.untyped.ast.unique_binders;

import java.util.List;

import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderVertex;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderVertexInput;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderVertexOutput;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderVertexOutputAssignment;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderVertexParameter;

// CHECKSTYLE_JAVADOC:OFF

public interface UASTUVertexShaderVisitorType<VS, PI, PP, PO, L, O, E extends Throwable>
{
  VS vertexShaderVisit(
    final List<PI> inputs,
    final List<PP> parameters,
    final List<PO> outputs,
    final List<L> locals,
    final List<O> output_assignments,
    final UASTUDShaderVertex v)
    throws E;

  PI vertexShaderVisitInput(
    final UASTUDShaderVertexInput i)
    throws E;

  UASTUVertexShaderLocalVisitorType<L, E> vertexShaderVisitLocalsPre()
    throws E;

  PO vertexShaderVisitOutput(
    final UASTUDShaderVertexOutput o)
    throws E;

  O vertexShaderVisitOutputAssignment(
    final UASTUDShaderVertexOutputAssignment a)
    throws E;

  PP vertexShaderVisitParameter(
    final UASTUDShaderVertexParameter p)
    throws E;
}
