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

package com.io7m.jparasol.tests.glsl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jparasol.CompilerError;
import com.io7m.jparasol.glsl.GVersion;
import com.io7m.jparasol.glsl.GVersion.GVersionES;
import com.io7m.jparasol.glsl.GVersion.GVersionFull;
import com.io7m.jparasol.glsl.GWriter;
import com.io7m.jparasol.glsl.ast.GASTShader.GASTShaderFragment;
import com.io7m.jparasol.glsl.ast.GASTShader.GASTShaderVertex;
import com.io7m.jparasol.glsl.pipeline.GCompilation;
import com.io7m.jparasol.glsl.pipeline.GCompiledProgram;
import com.io7m.jparasol.glsl.pipeline.GPipeline;
import com.io7m.jparasol.tests.TestPipeline;
import com.io7m.jparasol.typed.ast.TASTShaderNameFlat;

/**
 * Not a particularly good way to do structural testing, but it should catch
 * simple errors.
 */

@SuppressWarnings("static-method") public final class GWriterTest
{
  @Test public void testInterpolate_0()
    throws IOException,
      CompilerError
  {
    final GPipeline gpipe =
      TestPipeline
        .makeGPipeline(new String[] { "glsl/writer/interpolate.p" });

    final Set<TASTShaderNameFlat> program_names =
      new HashSet<TASTShaderNameFlat>();
    final TASTShaderNameFlat program_name =
      TestPipeline.shaderName("x.y.M", "p");
    program_names.add(program_name);
    final GCompilation comp =
      gpipe
        .transformPrograms(program_names, GVersionES.ALL, GVersionFull.ALL);
    final GCompiledProgram p = comp.getShadersProgram().get(program_name);

    final Map<GVersion, GASTShaderVertex> vertex_shader =
      p.getShadersVertex().values().iterator().next();
    final Map<GVersion, GASTShaderFragment> fragment_shader =
      p.getShaderFragment();

    final GASTShaderVertex vs_110 = vertex_shader.get(GVersionFull.GLSL_110);
    final GASTShaderFragment fs_110 =
      fragment_shader.get(GVersionFull.GLSL_110);

    final ByteArrayOutputStream vertex_out = new ByteArrayOutputStream();
    final ByteArrayOutputStream fragment_out = new ByteArrayOutputStream();

    GWriter.writeVertexShader(System.out, vs_110);
    GWriter.writeVertexShader(vertex_out, vs_110);
    GWriter.writeFragmentShader(System.out, fs_110);
    GWriter.writeFragmentShader(fragment_out, fs_110);

    final String vertex_text = new String(vertex_out.toByteArray());
    final String fragment_text = new String(fragment_out.toByteArray());

    final String v_expected =
      TestPipeline.getFileText("glsl/writer/interpolate.v_exp");
    final String f_expected =
      TestPipeline.getFileText("glsl/writer/interpolate.f_exp");

    Assert.assertEquals(v_expected, vertex_text);
    Assert.assertEquals(f_expected, fragment_text);
  }

  @Test public void testOutput_0()
    throws IOException,
      CompilerError
  {
    final GPipeline gpipe =
      TestPipeline
        .makeGPipeline(new String[] { "glsl/writer/everything_opt.p" });

    final Set<TASTShaderNameFlat> program_names =
      new HashSet<TASTShaderNameFlat>();
    final TASTShaderNameFlat program_name =
      TestPipeline.shaderName("x.y.M", "everything_opt");
    program_names.add(program_name);
    final GCompilation comp =
      gpipe
        .transformPrograms(program_names, GVersionES.ALL, GVersionFull.ALL);
    final GCompiledProgram p = comp.getShadersProgram().get(program_name);

    final Map<GVersion, GASTShaderVertex> vertex_shader =
      p.getShadersVertex().values().iterator().next();
    final Map<GVersion, GASTShaderFragment> fragment_shader =
      p.getShaderFragment();

    final GASTShaderVertex vs_110 = vertex_shader.get(GVersionFull.GLSL_110);
    final GASTShaderFragment fs_110 =
      fragment_shader.get(GVersionFull.GLSL_110);

    final ByteArrayOutputStream vertex_out = new ByteArrayOutputStream();
    final ByteArrayOutputStream fragment_out = new ByteArrayOutputStream();

    GWriter.writeVertexShader(vertex_out, vs_110);
    GWriter.writeFragmentShader(fragment_out, fs_110);

    final String vertex_text = new String(vertex_out.toByteArray());
    final String fragment_text = new String(fragment_out.toByteArray());

    final String v_expected =
      TestPipeline.getFileText("glsl/writer/everything_opt.v_exp");
    final String f_expected =
      TestPipeline.getFileText("glsl/writer/everything_opt.f_exp");

    Assert.assertEquals(v_expected, vertex_text);
    Assert.assertEquals(f_expected, fragment_text);
  }
}
