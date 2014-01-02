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

import java.util.List;

import javax.annotation.Nonnull;

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Pair;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.TestPipeline;
import com.io7m.jparasol.TestUtilities;
import com.io7m.jparasol.typed.ast.TASTCompilation;
import com.io7m.jparasol.typed.ast.TASTShaderNameFlat;
import com.io7m.jparasol.typed.ast.TASTTermNameFlat;

public final class TopologyTest
{
  private static @Nonnull Pair<TASTCompilation, Referenced> referenced(
    final @Nonnull String[] names,
    final @Nonnull TASTShaderNameFlat shader)
    throws ConstraintError
  {
    final TASTCompilation r = TestPipeline.completeTyped(names);
    final Referenced ref =
      Referenced.fromShader(r, shader, TestUtilities.getLog());
    return new Pair<TASTCompilation, Referenced>(r, ref);
  }

  private static @Nonnull TASTShaderNameFlat shaderName(
    final @Nonnull String module,
    final @Nonnull String name)
    throws ConstraintError
  {
    return new TASTShaderNameFlat(new ModulePathFlat(module), name);
  }

  @SuppressWarnings("static-method") @Test public
    void
    testVertexShaderTerm_3()
      throws ConstraintError
  {
    final TASTShaderNameFlat shader_name =
      TopologyTest.shaderName("x.y.M", "v");

    final Pair<TASTCompilation, Referenced> r =
      TopologyTest.referenced(
        new String[] { "typed/referenced/vertex-shader-term-3.p" },
        shader_name);

    final Topology topo =
      Topology.fromShader(
        r.first,
        r.second,
        shader_name,
        TestUtilities.getLog());

    final List<TASTTermNameFlat> terms = topo.getTerms();
    Assert.assertEquals(2, terms.size());
    Assert.assertEquals(terms.get(0), TestPipeline.termName("x.y.M", "x"));
    Assert.assertEquals(terms.get(1), TestPipeline.termName("x.y.M", "y"));
  }
}
