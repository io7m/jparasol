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

package com.io7m.jparasol.typed;

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jparasol.TestPipeline;
import com.io7m.jparasol.typed.ExternalsError.Code;

public final class ExternalsTest
{
  static void failWithCodeInternal(
    final String names[],
    final ExternalsError.Code code)
    throws ExternalsError
  {
    try {
      TestPipeline.externalCheckedInternal(names);
    } catch (final ExternalsError e) {
      System.err.println(e.getMessage());
      Assert.assertEquals(code, e.getCode());
      throw e;
    }
  }

  @SuppressWarnings("static-method") @Test public
    void
    testFragmentShaderAllowed_0()
      throws ExternalsError
  {
    TestPipeline
      .externalCheckedInternal(new String[] { "typed/externals/fragment-shader-allowed-0.p" });
  }

  @SuppressWarnings("static-method") @Test(expected = ExternalsError.class) public
    void
    testFragmentShaderDisallowed_0()
      throws ExternalsError
  {
    ExternalsTest.failWithCodeInternal(
      new String[] { "typed/externals/fragment-shader-disallowed-0.p" },
      Code.EXTERNALS_DISALLOWED_IN_FRAGMENT_SHADER);
  }

  @SuppressWarnings("static-method") @Test(expected = ExternalsError.class) public
    void
    testFragmentShaderDisallowed_1()
      throws ExternalsError
  {
    ExternalsTest.failWithCodeInternal(
      new String[] { "typed/externals/fragment-shader-disallowed-1.p" },
      Code.EXTERNALS_DISALLOWED_IN_FRAGMENT_SHADER);
  }

  @SuppressWarnings("static-method") @Test public
    void
    testVertexShaderAllowed_0()
      throws ExternalsError
  {
    TestPipeline
      .externalCheckedInternal(new String[] { "typed/externals/vertex-shader-allowed-0.p" });
  }

  @SuppressWarnings("static-method") @Test(expected = ExternalsError.class) public
    void
    testVertexShaderDisallowed_0()
      throws ExternalsError
  {
    ExternalsTest.failWithCodeInternal(
      new String[] { "typed/externals/vertex-shader-disallowed-0.p" },
      Code.EXTERNALS_DISALLOWED_IN_VERTEX_SHADER);
  }

  @SuppressWarnings("static-method") @Test(expected = ExternalsError.class) public
    void
    testVertexShaderDisallowed_1()
      throws ExternalsError
  {
    ExternalsTest.failWithCodeInternal(
      new String[] { "typed/externals/vertex-shader-disallowed-1.p" },
      Code.EXTERNALS_DISALLOWED_IN_VERTEX_SHADER);
  }
}
