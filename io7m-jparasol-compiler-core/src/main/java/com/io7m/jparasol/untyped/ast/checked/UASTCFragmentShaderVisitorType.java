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

package com.io7m.jparasol.untyped.ast.checked;

import java.util.List;

import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDShaderFragment;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDShaderFragmentInput;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDShaderFragmentOutputAssignment;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDShaderFragmentParameter;

// CHECKSTYLE_JAVADOC:OFF

public interface UASTCFragmentShaderVisitorType<F, PI, PP, PO, L, OA, E extends Throwable>
{
  F fragmentShaderVisit(
    final List<PI> inputs,
    final List<PP> parameters,
    final List<PO> outputs,
    final List<L> locals,
    final List<OA> output_assignments,
    final UASTCDShaderFragment f)
    throws E;

  PI fragmentShaderVisitInput(
    final UASTCDShaderFragmentInput i)
    throws E;

  UASTCFragmentShaderLocalVisitorType<L, E> fragmentShaderVisitLocalsPre()
    throws E;

  OA fragmentShaderVisitOutputAssignment(
    final UASTCDShaderFragmentOutputAssignment a)
    throws E;

  UASTCFragmentShaderOutputVisitorType<PO, E> fragmentShaderVisitOutputsPre()
    throws E;

  PP fragmentShaderVisitParameter(
    final UASTCDShaderFragmentParameter p)
    throws E;
}
