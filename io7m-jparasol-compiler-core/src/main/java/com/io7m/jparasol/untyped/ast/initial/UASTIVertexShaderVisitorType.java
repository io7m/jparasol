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

import java.util.List;

import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertex;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertexInput;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertexOutput;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertexOutputAssignment;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertexParameter;

// CHECKSTYLE_JAVADOC:OFF

public interface UASTIVertexShaderVisitorType<VS, PI, PP, PO, L, O, E extends Throwable>
{
  VS vertexShaderVisit(
    final List<PI> inputs,
    final List<PP> parameters,
    final List<PO> outputs,
    final List<L> locals,
    final List<O> output_assignments,
    final UASTIDShaderVertex v)
    throws E;

  PI vertexShaderVisitInput(
    final UASTIDShaderVertexInput i)
    throws E;

  UASTIVertexShaderLocalVisitorType<L, E> vertexShaderVisitLocalsPre()
    throws E;

  PO vertexShaderVisitOutput(
    final UASTIDShaderVertexOutput o)
    throws E;

  O vertexShaderVisitOutputAssignment(
    final UASTIDShaderVertexOutputAssignment a)
    throws E;

  PP vertexShaderVisitParameter(
    final UASTIDShaderVertexParameter p)
    throws E;
}
