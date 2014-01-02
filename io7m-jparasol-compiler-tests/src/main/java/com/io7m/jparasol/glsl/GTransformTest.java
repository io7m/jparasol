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

import javax.annotation.Nonnull;

import org.junit.Test;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jlog.Log;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.TestPipeline;
import com.io7m.jparasol.TestUtilities;
import com.io7m.jparasol.glsl.GVersion.GVersionFull;
import com.io7m.jparasol.glsl.ast.GASTShader.GASTShaderFragment;
import com.io7m.jparasol.glsl.ast.GASTShader.GASTShaderVertex;
import com.io7m.jparasol.typed.Referenced;
import com.io7m.jparasol.typed.Topology;
import com.io7m.jparasol.typed.ast.TASTCompilation;
import com.io7m.jparasol.typed.ast.TASTShaderNameFlat;

public final class GTransformTest
{
  static class Prepared
  {
    final @Nonnull Log             log;
    final @Nonnull Referenced      referenced;
    final @Nonnull Topology        topology;
    final @Nonnull TASTCompilation typed;

    Prepared(
      final TASTShaderNameFlat shader,
      final String names[])
    {
      try {
        this.typed = TestPipeline.completeTypedInternal(names);
        this.log = TestUtilities.getLog();
        this.referenced = Referenced.fromShader(this.typed, shader, this.log);
        this.topology =
          Topology.fromShader(this.typed, this.referenced, shader, this.log);
      } catch (final ConstraintError x) {
        throw new UnreachableCodeException(x);
      }
    }
  }

  private static @Nonnull TASTShaderNameFlat shaderName(
    final @Nonnull String module,
    final @Nonnull String name)
  {
    try {
      return new TASTShaderNameFlat(new ModulePathFlat(module), name);
    } catch (final ConstraintError e) {
      throw new UnreachableCodeException(e);
    }
  }

  @SuppressWarnings("static-method") @Test public void testFragmentSimple_0()
    throws ConstraintError
  {
    final TASTShaderNameFlat shader = GTransformTest.shaderName("x.y.M", "f");
    final Prepared p =
      new Prepared(
        shader,
        new String[] { "glsl/transform/fragment-simple-0.p" });

    final GASTShaderFragment s =
      GTransform.transformFragment(
        p.typed,
        p.topology,
        shader,
        GVersionFull.GLSL_UPPER,
        p.log);

    GWriter
      .writeFragmentShader(System.out, s, GVersion.GVersionFull.GLSL_110);
  }

  @SuppressWarnings("static-method") @Test public void testVertexSimple_0()
    throws ConstraintError
  {
    final TASTShaderNameFlat shader = GTransformTest.shaderName("x.y.M", "v");
    final Prepared p =
      new Prepared(
        shader,
        new String[] { "glsl/transform/vertex-simple-0.p" });

    final GASTShaderVertex s =
      GTransform.transformVertex(
        p.typed,
        p.topology,
        shader,
        GVersionFull.GLSL_UPPER,
        p.log);

    GWriter.writeVertexShader(System.out, s, GVersion.GVersionFull.GLSL_110);
  }
}
