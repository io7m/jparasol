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

package com.io7m.jparasol.glsl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.TestPipeline;
import com.io7m.jparasol.TestUtilities;
import com.io7m.jparasol.glsl.ast.GASTExpression;
import com.io7m.jparasol.typed.ast.TASTCompilation;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDFunctionExternal;

public final class GFFITest
{
  @SuppressWarnings("static-method") @Test(expected = GFFIError.class) public
    void
    testUnknownExpressionExternal()
      throws ConstraintError,
        GFFIError
  {
    final TASTCompilation r =
      TestPipeline
        .completeTypedInternal(new String[] { "glsl/ffi/unknown-0.p" });
    final TASTDFunctionExternal f =
      (TASTDFunctionExternal) r.lookupTerm(TestPipeline
        .termName("x.y.M", "f"));

    final List<GASTExpression> arguments = new ArrayList<GASTExpression>();

    final GFFI ffi = GFFI.newFFI(TestUtilities.getLog());
    ffi.getExpression(f, arguments, GVersion.GVersionFull.GLSL_110);
  }
}
