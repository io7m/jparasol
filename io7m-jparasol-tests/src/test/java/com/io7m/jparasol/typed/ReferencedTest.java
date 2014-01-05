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

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.TestPipeline;
import com.io7m.jparasol.TestUtilities;
import com.io7m.jparasol.typed.ast.TASTCompilation;
import com.io7m.jparasol.typed.ast.TASTShaderNameFlat;
import com.io7m.jparasol.typed.ast.TASTTermNameFlat;

public final class ReferencedTest
{
  @SuppressWarnings("static-method") @Test public void testBug926def871d_0()
    throws ConstraintError
  {
    final TASTCompilation r =
      TestPipeline
        .completeTyped(new String[] { "typed/referenced/bug-926def871d.p" });

    final TASTShaderNameFlat shader_name =
      new TASTShaderNameFlat(new ModulePathFlat("x.M"), "f");
    final Referenced ref =
      Referenced.fromShader(r, shader_name, TestUtilities.getLog());

    final Set<TTypeNameFlat> types = ref.getTypes();
    Assert.assertTrue(types.contains(TestPipeline.typeName("x.M", "v")));
    Assert.assertTrue(types.contains(TestPipeline.typeName("x.M", "u")));
    Assert.assertTrue(types.contains(TestPipeline.typeName("x.M", "t")));
  }

  @SuppressWarnings("static-method") @Test public
    void
    testVertexShaderTerm_0()
      throws ConstraintError
  {
    final TASTCompilation r =
      TestPipeline
        .completeTyped(new String[] { "typed/referenced/vertex-shader-term-0.p" });

    final TASTShaderNameFlat shader_name =
      new TASTShaderNameFlat(new ModulePathFlat("x.y.M"), "v");
    final Referenced ref =
      Referenced.fromShader(r, shader_name, TestUtilities.getLog());

    Assert.assertTrue(ref.getTerms().isEmpty());
    Assert.assertTrue(ref.getTypes().isEmpty());
  }

  @SuppressWarnings("static-method") @Test public
    void
    testVertexShaderTerm_1()
      throws ConstraintError
  {
    final TASTCompilation r =
      TestPipeline
        .completeTyped(new String[] { "typed/referenced/vertex-shader-term-1.p" });

    final TASTShaderNameFlat shader_name =
      new TASTShaderNameFlat(new ModulePathFlat("x.y.M"), "v");
    final Referenced ref =
      Referenced.fromShader(r, shader_name, TestUtilities.getLog());

    Assert.assertTrue(ref.getTypes().isEmpty());

    final Set<TASTTermNameFlat> terms = ref.getTerms();
    Assert.assertEquals(1, terms.size());
    Assert.assertTrue(terms.contains(TestPipeline.termName("x.y.M", "x")));
  }

  @SuppressWarnings("static-method") @Test public
    void
    testVertexShaderTerm_2()
      throws ConstraintError
  {
    final TASTCompilation r =
      TestPipeline
        .completeTyped(new String[] { "typed/referenced/vertex-shader-term-2.p" });

    final TASTShaderNameFlat shader_name =
      new TASTShaderNameFlat(new ModulePathFlat("x.y.M"), "v");
    final Referenced ref =
      Referenced.fromShader(r, shader_name, TestUtilities.getLog());

    Assert.assertTrue(ref.getTypes().isEmpty());

    final Set<TASTTermNameFlat> terms = ref.getTerms();
    Assert.assertEquals(2, terms.size());
    Assert.assertTrue(terms.contains(TestPipeline.termName("x.y.M", "x")));
    Assert.assertTrue(terms.contains(TestPipeline.termName("x.y.M", "y")));
  }

  @SuppressWarnings("static-method") @Test public
    void
    testVertexShaderTerm_3()
      throws ConstraintError
  {
    final TASTCompilation r =
      TestPipeline
        .completeTyped(new String[] { "typed/referenced/vertex-shader-term-3.p" });

    final TASTShaderNameFlat shader_name =
      new TASTShaderNameFlat(new ModulePathFlat("x.y.M"), "v");
    final Referenced ref =
      Referenced.fromShader(r, shader_name, TestUtilities.getLog());

    Assert.assertTrue(ref.getTypes().isEmpty());

    final Set<TASTTermNameFlat> terms = ref.getTerms();
    Assert.assertEquals(2, terms.size());
    Assert.assertTrue(terms.contains(TestPipeline.termName("x.y.M", "x")));
    Assert.assertTrue(terms.contains(TestPipeline.termName("x.y.M", "y")));
  }

  @SuppressWarnings("static-method") @Test public
    void
    testVertexShaderType_0()
      throws ConstraintError
  {
    final TASTCompilation r =
      TestPipeline
        .completeTyped(new String[] { "typed/referenced/vertex-shader-type-0.p" });

    final TASTShaderNameFlat shader_name =
      new TASTShaderNameFlat(new ModulePathFlat("x.y.M"), "v");
    final Referenced ref =
      Referenced.fromShader(r, shader_name, TestUtilities.getLog());

    Assert.assertTrue(ref.getTerms().isEmpty());

    final Set<TTypeNameFlat> types = ref.getTypes();
    Assert.assertEquals(1, types.size());
    Assert.assertTrue(types.contains(TestPipeline.typeName("x.y.M", "t")));
  }

  @SuppressWarnings("static-method") @Test public
    void
    testVertexShaderType_1()
      throws ConstraintError
  {
    final TASTCompilation r =
      TestPipeline
        .completeTyped(new String[] { "typed/referenced/vertex-shader-type-1.p" });

    final TASTShaderNameFlat shader_name =
      new TASTShaderNameFlat(new ModulePathFlat("x.y.M"), "v");
    final Referenced ref =
      Referenced.fromShader(r, shader_name, TestUtilities.getLog());

    final Set<TASTTermNameFlat> terms = ref.getTerms();
    Assert.assertEquals(1, terms.size());
    Assert.assertTrue(terms.contains(TestPipeline.termName("x.y.M", "x")));

    final Set<TTypeNameFlat> types = ref.getTypes();
    Assert.assertEquals(1, types.size());
    Assert.assertTrue(types.contains(TestPipeline.typeName("x.y.M", "t")));
  }
}
