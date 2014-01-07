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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Pair;
import com.io7m.jparasol.TestPipeline;
import com.io7m.jparasol.UIError;
import com.io7m.jparasol.glsl.GVersion.GVersionES;
import com.io7m.jparasol.glsl.GVersion.GVersionFull;
import com.io7m.jparasol.glsl.ast.GASTShader.GASTShaderFragment;
import com.io7m.jparasol.glsl.ast.GASTShader.GASTShaderVertex;
import com.io7m.jparasol.glsl.pipeline.GPipeline;

/**
 * Not a particularly good way to do structural testing, but it should catch
 * simple errors.
 */

public final class GWriterTest
{
  @SuppressWarnings("static-method") @Test public void testOutput_0()
    throws UIError,
      GFFIError,
      GVersionCheckerError,
      ConstraintError,
      IOException
  {
    final GPipeline gpipe =
      TestPipeline
        .makeGPipeline(new String[] { "glsl/writer/everything_opt.p" });

    final Map<GVersion, Pair<GASTShaderVertex, GASTShaderFragment>> asts =
      gpipe.makeProgram(
        TestPipeline.shaderName("x.y.M", "everything_opt"),
        GVersionES.ALL,
        GVersionFull.ALL);

    final Pair<GASTShaderVertex, GASTShaderFragment> program =
      asts.get(GVersion.GVersionFull.GLSL_110);

    final ByteArrayOutputStream vertex_out = new ByteArrayOutputStream();
    final ByteArrayOutputStream fragment_out = new ByteArrayOutputStream();

    GWriter.writeVertexShader(vertex_out, program.first);
    GWriter.writeFragmentShader(fragment_out, program.second);

    final String vertex_text = new String(vertex_out.toByteArray());
    final String fragment_text = new String(fragment_out.toByteArray());

    final String v_expected =
      TestPipeline.getFileText("glsl/writer/everything_opt.v_exp");
    final String f_expected =
      TestPipeline.getFileText("glsl/writer/everything_opt.f_exp");

    Assert.assertEquals(v_expected, vertex_text);
    Assert.assertEquals(f_expected, fragment_text);
  }

  @SuppressWarnings("static-method") @Test public void testInterpolate_0()
    throws UIError,
      GFFIError,
      GVersionCheckerError,
      ConstraintError,
      IOException
  {
    final GPipeline gpipe =
      TestPipeline
        .makeGPipeline(new String[] { "glsl/writer/interpolate.p" });

    final Map<GVersion, Pair<GASTShaderVertex, GASTShaderFragment>> asts =
      gpipe.makeProgram(
        TestPipeline.shaderName("x.y.M", "p"),
        GVersionES.ALL,
        GVersionFull.ALL);

    final Pair<GASTShaderVertex, GASTShaderFragment> program =
      asts.get(GVersion.GVersionFull.GLSL_110);

    final ByteArrayOutputStream vertex_out = new ByteArrayOutputStream();
    final ByteArrayOutputStream fragment_out = new ByteArrayOutputStream();

    GWriter.writeVertexShader(System.out, program.first);
    GWriter.writeVertexShader(vertex_out, program.first);
    GWriter.writeFragmentShader(System.out, program.second);
    GWriter.writeFragmentShader(fragment_out, program.second);

    final String vertex_text = new String(vertex_out.toByteArray());
    final String fragment_text = new String(fragment_out.toByteArray());

    final String v_expected =
      TestPipeline.getFileText("glsl/writer/interpolate.v_exp");
    final String f_expected =
      TestPipeline.getFileText("glsl/writer/interpolate.f_exp");

    Assert.assertEquals(v_expected, vertex_text);
    Assert.assertEquals(f_expected, fragment_text);
  }
}
