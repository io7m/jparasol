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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jfunctional.Pair;
import com.io7m.jparasol.CompilerError;
import com.io7m.jparasol.core.GVersionES;
import com.io7m.jparasol.core.GVersionFull;
import com.io7m.jparasol.glsl.GFFI;
import com.io7m.jparasol.glsl.GFFIError;
import com.io7m.jparasol.glsl.GWriter;
import com.io7m.jparasol.glsl.ast.GASTExpressionType;
import com.io7m.jparasol.glsl.ast.GASTShader.GASTShaderFragment;
import com.io7m.jparasol.glsl.ast.GASTShader.GASTShaderVertex;
import com.io7m.jparasol.glsl.ast.GASTTermDeclarationType;
import com.io7m.jparasol.glsl.ast.GASTTermFunction;
import com.io7m.jparasol.glsl.ast.GASTTermValue;
import com.io7m.jparasol.glsl.ast.GTermName.GTermNameGlobal;
import com.io7m.jparasol.glsl.pipeline.GCompilation;
import com.io7m.jparasol.glsl.pipeline.GCompiledProgram;
import com.io7m.jparasol.glsl.pipeline.GCompiledVertexShader;
import com.io7m.jparasol.glsl.pipeline.GPipeline;
import com.io7m.jparasol.tests.TestPipeline;
import com.io7m.jparasol.tests.TestUtilities;
import com.io7m.jparasol.typed.ast.TASTCompilation;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDFunctionExternal;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDValueExternal;
import com.io7m.jparasol.typed.ast.TASTShaderNameFlat;

@SuppressWarnings("static-method") public final class GFFITest
{
  private static void dumpFragment(
    final GASTShaderFragment program)
  {
    System.err.println("Version: "
      + program.getGLSLVersion().versionGetLongName());
    System.err
      .println("----------------------------------------------------------------------");
    GWriter.writeFragmentShader(System.err, program, true);
    System.err
      .println("----------------------------------------------------------------------");
  }

  private static void dumpVertex(
    final GASTShaderVertex program)
  {
    System.err.println("Version: "
      + program.getGLSLVersion().versionGetLongName());
    System.err
      .println("----------------------------------------------------------------------");
    GWriter.writeVertexShader(System.err, program, true);
    System.err
      .println("----------------------------------------------------------------------");
  }

  @Test public void testFloatIsInfinite_0()
    throws CompilerError
  {
    final GPipeline gpipe =
      TestPipeline
        .makeGPipeline(new String[] { "glsl/ffi/float-is_infinite-0.p" });

    final Set<TASTShaderNameFlat> program_names =
      new HashSet<TASTShaderNameFlat>();
    final TASTShaderNameFlat program_name =
      TestPipeline.shaderName("x.y.M", "p");
    program_names.add(program_name);
    final GCompilation comp =
      gpipe.transformPrograms(
        program_names,
        new TreeSet<GVersionES>(),
        new TreeSet<GVersionFull>());
    final GCompiledProgram p = comp.getShadersProgram().get(program_name);

    final GCompiledVertexShader vertex_shader =
      p.getShadersVertex().values().iterator().next();

    /**
     * All versions of GLSL ES, and GLSL <= 120 must emit a replacement isinf
     * function.
     */

    for (final GVersionES vn : GVersionES.ALL) {
      final GASTShaderVertex program = vertex_shader.getSources().get(vn);
      final Pair<GTermNameGlobal, GASTTermDeclarationType> pf =
        program.getTerms().get(0);
      Assert.assertEquals("p_com_io7m_parasol_Float_is_infinite", pf
        .getLeft()
        .show());
      GFFITest.dumpVertex(program);
    }

    for (final GVersionFull vn : GVersionFull.ALL) {
      final GASTShaderVertex program = vertex_shader.getSources().get(vn);
      if (vn.compareTo(GVersionFull.GLSL_120) <= 0) {
        final Pair<GTermNameGlobal, GASTTermDeclarationType> pf =
          program.getTerms().get(0);
        Assert.assertEquals("p_com_io7m_parasol_Float_is_infinite", pf
          .getLeft()
          .show());
        Assert.assertTrue(pf.getRight() instanceof GASTTermFunction);
      } else {
        final Pair<GTermNameGlobal, GASTTermDeclarationType> pf =
          program.getTerms().get(0);
        Assert.assertEquals("p_x_y_M_x", pf.getLeft().show());
        Assert.assertTrue(pf.getRight() instanceof GASTTermValue);
      }
      GFFITest.dumpVertex(program);
    }
  }

  @Test public void testFloatIsNan_0()
    throws CompilerError
  {
    final GPipeline gpipe =
      TestPipeline
        .makeGPipeline(new String[] { "glsl/ffi/float-is_nan-0.p" });

    final Set<TASTShaderNameFlat> program_names =
      new HashSet<TASTShaderNameFlat>();
    final TASTShaderNameFlat program_name =
      TestPipeline.shaderName("x.y.M", "p");
    program_names.add(program_name);
    final GCompilation comp =
      gpipe.transformPrograms(
        program_names,
        new TreeSet<GVersionES>(),
        new TreeSet<GVersionFull>());
    final GCompiledProgram p = comp.getShadersProgram().get(program_name);

    final GCompiledVertexShader vertex_shader =
      p.getShadersVertex().values().iterator().next();

    /**
     * All versions of GLSL ES, and GLSL <= 120 must emit a replacement isnan
     * function.
     */

    for (final GVersionES vn : GVersionES.ALL) {
      final GASTShaderVertex vs = vertex_shader.getSources().get(vn);
      final Pair<GTermNameGlobal, GASTTermDeclarationType> terms =
        vs.getTerms().get(0);
      Assert.assertEquals("p_com_io7m_parasol_Float_is_nan", terms
        .getLeft()
        .show());
      GFFITest.dumpVertex(vs);
    }

    for (final GVersionFull vn : GVersionFull.ALL) {
      final GASTShaderVertex vs = vertex_shader.getSources().get(vn);
      if (vn.compareTo(GVersionFull.GLSL_120) <= 0) {
        final Pair<GTermNameGlobal, GASTTermDeclarationType> pf =
          vs.getTerms().get(0);
        Assert.assertEquals("p_com_io7m_parasol_Float_is_nan", pf
          .getLeft()
          .show());
        Assert.assertTrue(pf.getRight() instanceof GASTTermFunction);
      } else {
        final Pair<GTermNameGlobal, GASTTermDeclarationType> pf =
          vs.getTerms().get(0);
        Assert.assertEquals("p_x_y_M_x", pf.getLeft().show());
        Assert.assertTrue(pf.getRight() instanceof GASTTermValue);
      }
      GFFITest.dumpVertex(vs);
    }
  }

  @Test public void testFloatLib()
    throws CompilerError
  {
    final GPipeline gpipe =
      TestPipeline.makeGPipeline(new String[] { "glsl/ffi/float-lib.p" });

    final Set<TASTShaderNameFlat> program_names =
      new HashSet<TASTShaderNameFlat>();
    program_names.add(TestPipeline.shaderName("x.y.M", "p"));
    final GCompilation comp =
      gpipe
        .transformPrograms(program_names, GVersionES.ALL, GVersionFull.ALL);
  }

  @Test public void testFloatSign_0()
    throws CompilerError
  {
    final GPipeline gpipe =
      TestPipeline.makeGPipeline(new String[] { "glsl/ffi/float-sign-0.p" });

    final Set<TASTShaderNameFlat> program_names =
      new HashSet<TASTShaderNameFlat>();
    final TASTShaderNameFlat program_name =
      TestPipeline.shaderName("x.y.M", "p");
    program_names.add(program_name);
    final GCompilation comp =
      gpipe.transformPrograms(
        program_names,
        new TreeSet<GVersionES>(),
        new TreeSet<GVersionFull>());
    final GCompiledProgram p = comp.getShadersProgram().get(program_name);

    final GCompiledVertexShader vertex_shader =
      p.getShadersVertex().values().iterator().next();

    /**
     * All versions of GLSL ES, and GLSL <= 120 must emit a replacement sign
     * function.
     */

    for (final GVersionES vn : GVersionES.ALL) {
      final GASTShaderVertex program = vertex_shader.getSources().get(vn);
      final Pair<GTermNameGlobal, GASTTermDeclarationType> pf =
        program.getTerms().get(0);
      Assert.assertEquals("p_com_io7m_parasol_Float_sign", pf
        .getLeft()
        .show());
      GFFITest.dumpVertex(program);
    }

    for (final GVersionFull vn : GVersionFull.ALL) {
      final GASTShaderVertex program = vertex_shader.getSources().get(vn);
      if (vn.compareTo(GVersionFull.GLSL_120) <= 0) {
        final Pair<GTermNameGlobal, GASTTermDeclarationType> pf =
          program.getTerms().get(0);
        Assert.assertEquals("p_com_io7m_parasol_Float_sign", pf
          .getLeft()
          .show());
        Assert.assertTrue(pf.getRight() instanceof GASTTermFunction);
      } else {
        final Pair<GTermNameGlobal, GASTTermDeclarationType> pf =
          program.getTerms().get(0);
        Assert.assertEquals("p_x_y_M_x", pf.getLeft().show());
        Assert.assertTrue(pf.getRight() instanceof GASTTermValue);
      }
      GFFITest.dumpVertex(program);
    }
  }

  @Test public void testFloatTruncate_0()
    throws CompilerError
  {
    final GPipeline gpipe =
      TestPipeline
        .makeGPipeline(new String[] { "glsl/ffi/float-truncate-0.p" });

    final Set<TASTShaderNameFlat> program_names =
      new HashSet<TASTShaderNameFlat>();
    final TASTShaderNameFlat program_name =
      TestPipeline.shaderName("x.y.M", "p");
    program_names.add(program_name);
    final GCompilation comp =
      gpipe.transformPrograms(
        program_names,
        new TreeSet<GVersionES>(),
        new TreeSet<GVersionFull>());
    final GCompiledProgram p = comp.getShadersProgram().get(program_name);

    final GCompiledVertexShader vertex_shader =
      p.getShadersVertex().values().iterator().next();

    /**
     * All versions of GLSL ES, and GLSL <= 120 must emit a replacement trunc
     * function.
     */

    for (final GVersionES vn : GVersionES.ALL) {
      final GASTShaderVertex program = vertex_shader.getSources().get(vn);
      final Pair<GTermNameGlobal, GASTTermDeclarationType> pf =
        program.getTerms().get(0);
      Assert.assertEquals("p_com_io7m_parasol_Float_truncate", pf
        .getLeft()
        .show());
      GFFITest.dumpVertex(program);
    }

    for (final GVersionFull vn : GVersionFull.ALL) {
      final GASTShaderVertex program = vertex_shader.getSources().get(vn);
      if (vn.compareTo(GVersionFull.GLSL_120) <= 0) {
        final Pair<GTermNameGlobal, GASTTermDeclarationType> pf =
          program.getTerms().get(0);
        Assert.assertEquals("p_com_io7m_parasol_Float_truncate", pf
          .getLeft()
          .show());
        Assert.assertTrue(pf.getRight() instanceof GASTTermFunction);
      } else {
        final Pair<GTermNameGlobal, GASTTermDeclarationType> pf =
          program.getTerms().get(0);
        Assert.assertEquals("p_x_y_M_x", pf.getLeft().show());
        Assert.assertTrue(pf.getRight() instanceof GASTTermValue);
      }
      GFFITest.dumpVertex(program);
    }
  }

  @Test public void testFragmentCoordinate_0()
    throws CompilerError
  {
    final GPipeline gpipe =
      TestPipeline
        .makeGPipeline(new String[] { "glsl/ffi/fragment-coordinate-0.p" });

    final Set<TASTShaderNameFlat> program_names =
      new HashSet<TASTShaderNameFlat>();
    final TASTShaderNameFlat program_name =
      TestPipeline.shaderName("x.y.M", "p");
    program_names.add(program_name);
    final GCompilation comp =
      gpipe.transformPrograms(
        program_names,
        new TreeSet<GVersionES>(),
        new TreeSet<GVersionFull>());
    final GCompiledProgram p = comp.getShadersProgram().get(program_name);

    for (final GVersionES vn : GVersionES.ALL) {
      final GASTShaderFragment program =
        p.getShaderFragment().getSources().get(vn);
      GFFITest.dumpFragment(program);
    }

    for (final GVersionFull vn : GVersionFull.ALL) {
      final GASTShaderFragment program =
        p.getShaderFragment().getSources().get(vn);
      GFFITest.dumpFragment(program);
    }
  }

  @Test public void testSamplerLib()
    throws CompilerError
  {
    final GPipeline gpipe =
      TestPipeline.makeGPipeline(new String[] { "glsl/ffi/sampler-lib.p" });

    final Set<TASTShaderNameFlat> program_names =
      new HashSet<TASTShaderNameFlat>();
    program_names.add(TestPipeline.shaderName("x.y.M", "p"));
    final GCompilation comp =
      gpipe
        .transformPrograms(program_names, GVersionES.ALL, GVersionFull.ALL);
  }

  @Test(expected = GFFIError.class) public
    void
    testUnknownExpressionExternal_0()
      throws GFFIError
  {
    final TASTCompilation r =
      TestPipeline
        .completeTypedInternal(new String[] { "glsl/ffi/unknown-0.p" });
    final TASTDFunctionExternal f =
      (TASTDFunctionExternal) r.lookupTerm(TestPipeline
        .termName("x.y.M", "f"));
    assert f != null;

    final List<GASTExpressionType> arguments =
      new ArrayList<GASTExpressionType>();

    final GFFI ffi = GFFI.newFFI(TestUtilities.getLog());
    ffi.getExpression(f, arguments, GVersionFull.GLSL_110);
  }

  @Test(expected = GFFIError.class) public void testUnknownValueExternal_0()
    throws GFFIError
  {
    final TASTCompilation r =
      TestPipeline
        .completeTypedInternal(new String[] { "glsl/ffi/unknown-1.p" });
    final TASTDValueExternal v =
      (TASTDValueExternal) r.lookupTerm(TestPipeline.termName("x.y.M", "x"));
    assert v != null;

    final GFFI ffi = GFFI.newFFI(TestUtilities.getLog());
    ffi.getValueDefinition(v, GVersionFull.GLSL_110);
  }

  @Test public void testVectorLib()
    throws CompilerError
  {
    final GPipeline gpipe =
      TestPipeline.makeGPipeline(new String[] { "glsl/ffi/vector-lib.p" });

    final Set<TASTShaderNameFlat> program_names =
      new HashSet<TASTShaderNameFlat>();
    program_names.add(TestPipeline.shaderName("x.y.M", "p"));
    final GCompilation comp =
      gpipe
        .transformPrograms(program_names, GVersionES.ALL, GVersionFull.ALL);
  }

}
