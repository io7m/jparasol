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
import java.util.Map;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Pair;
import com.io7m.jparasol.TestPipeline;
import com.io7m.jparasol.TestUtilities;
import com.io7m.jparasol.UIError;
import com.io7m.jparasol.glsl.GVersion.GVersionES;
import com.io7m.jparasol.glsl.GVersion.GVersionFull;
import com.io7m.jparasol.glsl.ast.GASTExpression;
import com.io7m.jparasol.glsl.ast.GASTShader.GASTShaderFragment;
import com.io7m.jparasol.glsl.ast.GASTShader.GASTShaderVertex;
import com.io7m.jparasol.glsl.ast.GASTTermDeclaration;
import com.io7m.jparasol.glsl.ast.GASTTermDeclaration.GASTTermFunction;
import com.io7m.jparasol.glsl.ast.GASTTermDeclaration.GASTTermValue;
import com.io7m.jparasol.glsl.ast.GTermName.GTermNameGlobal;
import com.io7m.jparasol.glsl.pipeline.GPipeline;
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

  @SuppressWarnings("static-method") @Test public void testFloatSign_0()
    throws UIError,
      GFFIError,
      GVersionCheckerError,
      ConstraintError
  {
    final GPipeline gpipe =
      TestPipeline.makeGPipeline(new String[] { "glsl/ffi/float-sign-0.p" });

    final Map<GVersion, Pair<GASTShaderVertex, GASTShaderFragment>> p =
      gpipe.makeProgram(
        TestPipeline.shaderName("x.y.M", "p"),
        new TreeSet<GVersionES>(),
        new TreeSet<GVersionFull>());

    /**
     * All versions of GLSL ES, and GLSL <= 120 must emit a replacement sign
     * function.
     */

    for (final GVersionES vn : GVersionES.ALL) {
      final Pair<GASTShaderVertex, GASTShaderFragment> program = p.get(vn);
      final Pair<GTermNameGlobal, GASTTermDeclaration> pf =
        program.first.getTerms().get(0);
      Assert.assertEquals("p_com_io7m_parasol_Float_sign", pf.first.show());
      GFFITest.dumpVertex(program);
    }

    for (final GVersionFull vn : GVersionFull.ALL) {
      final Pair<GASTShaderVertex, GASTShaderFragment> program = p.get(vn);
      if (vn.compareTo(GVersionFull.GLSL_120) <= 0) {
        final Pair<GTermNameGlobal, GASTTermDeclaration> pf =
          program.first.getTerms().get(0);
        Assert.assertEquals("p_com_io7m_parasol_Float_sign", pf.first.show());
        Assert.assertTrue(pf.second instanceof GASTTermFunction);
      } else {
        final Pair<GTermNameGlobal, GASTTermDeclaration> pf =
          program.first.getTerms().get(0);
        Assert.assertEquals("p_x_y_M_x", pf.first.show());
        Assert.assertTrue(pf.second instanceof GASTTermValue);
      }
      GFFITest.dumpVertex(program);
    }
  }

  @SuppressWarnings("static-method") @Test public
    void
    testFloatIsInfinite_0()
      throws UIError,
        GFFIError,
        GVersionCheckerError,
        ConstraintError
  {
    final GPipeline gpipe =
      TestPipeline
        .makeGPipeline(new String[] { "glsl/ffi/float-is_infinite-0.p" });

    final Map<GVersion, Pair<GASTShaderVertex, GASTShaderFragment>> p =
      gpipe.makeProgram(
        TestPipeline.shaderName("x.y.M", "p"),
        new TreeSet<GVersionES>(),
        new TreeSet<GVersionFull>());

    /**
     * All versions of GLSL ES, and GLSL <= 120 must emit a replacement isinf
     * function.
     */

    for (final GVersionES vn : GVersionES.ALL) {
      final Pair<GASTShaderVertex, GASTShaderFragment> program = p.get(vn);
      final Pair<GTermNameGlobal, GASTTermDeclaration> pf =
        program.first.getTerms().get(0);
      Assert.assertEquals(
        "p_com_io7m_parasol_Float_is_infinite",
        pf.first.show());
      GFFITest.dumpVertex(program);
    }

    for (final GVersionFull vn : GVersionFull.ALL) {
      final Pair<GASTShaderVertex, GASTShaderFragment> program = p.get(vn);
      if (vn.compareTo(GVersionFull.GLSL_120) <= 0) {
        final Pair<GTermNameGlobal, GASTTermDeclaration> pf =
          program.first.getTerms().get(0);
        Assert.assertEquals(
          "p_com_io7m_parasol_Float_is_infinite",
          pf.first.show());
        Assert.assertTrue(pf.second instanceof GASTTermFunction);
      } else {
        final Pair<GTermNameGlobal, GASTTermDeclaration> pf =
          program.first.getTerms().get(0);
        Assert.assertEquals("p_x_y_M_x", pf.first.show());
        Assert.assertTrue(pf.second instanceof GASTTermValue);
      }
      GFFITest.dumpVertex(program);
    }
  }

  @SuppressWarnings("static-method") @Test public void testFloatTruncate_0()
    throws UIError,
      GFFIError,
      GVersionCheckerError,
      ConstraintError
  {
    final GPipeline gpipe =
      TestPipeline
        .makeGPipeline(new String[] { "glsl/ffi/float-truncate-0.p" });

    final Map<GVersion, Pair<GASTShaderVertex, GASTShaderFragment>> p =
      gpipe.makeProgram(
        TestPipeline.shaderName("x.y.M", "p"),
        new TreeSet<GVersionES>(),
        new TreeSet<GVersionFull>());

    /**
     * All versions of GLSL ES, and GLSL <= 120 must emit a replacement trunc
     * function.
     */

    for (final GVersionES vn : GVersionES.ALL) {
      final Pair<GASTShaderVertex, GASTShaderFragment> program = p.get(vn);
      final Pair<GTermNameGlobal, GASTTermDeclaration> pf =
        program.first.getTerms().get(0);
      Assert.assertEquals(
        "p_com_io7m_parasol_Float_truncate",
        pf.first.show());
      GFFITest.dumpVertex(program);
    }

    for (final GVersionFull vn : GVersionFull.ALL) {
      final Pair<GASTShaderVertex, GASTShaderFragment> program = p.get(vn);
      if (vn.compareTo(GVersionFull.GLSL_120) <= 0) {
        final Pair<GTermNameGlobal, GASTTermDeclaration> pf =
          program.first.getTerms().get(0);
        Assert.assertEquals(
          "p_com_io7m_parasol_Float_truncate",
          pf.first.show());
        Assert.assertTrue(pf.second instanceof GASTTermFunction);
      } else {
        final Pair<GTermNameGlobal, GASTTermDeclaration> pf =
          program.first.getTerms().get(0);
        Assert.assertEquals("p_x_y_M_x", pf.first.show());
        Assert.assertTrue(pf.second instanceof GASTTermValue);
      }
      GFFITest.dumpVertex(program);
    }
  }

  @SuppressWarnings("static-method") @Test public void testFloatIsNan_0()
    throws UIError,
      GFFIError,
      GVersionCheckerError,
      ConstraintError
  {
    final GPipeline gpipe =
      TestPipeline
        .makeGPipeline(new String[] { "glsl/ffi/float-is_nan-0.p" });

    final Map<GVersion, Pair<GASTShaderVertex, GASTShaderFragment>> p =
      gpipe.makeProgram(
        TestPipeline.shaderName("x.y.M", "p"),
        new TreeSet<GVersionES>(),
        new TreeSet<GVersionFull>());

    /**
     * All versions of GLSL ES, and GLSL <= 120 must emit a replacement isnan
     * function.
     */

    for (final GVersionES vn : GVersionES.ALL) {
      final Pair<GASTShaderVertex, GASTShaderFragment> program = p.get(vn);
      final Pair<GTermNameGlobal, GASTTermDeclaration> pf =
        program.first.getTerms().get(0);
      Assert.assertEquals("p_com_io7m_parasol_Float_is_nan", pf.first.show());
      GFFITest.dumpVertex(program);
    }

    for (final GVersionFull vn : GVersionFull.ALL) {
      final Pair<GASTShaderVertex, GASTShaderFragment> program = p.get(vn);
      if (vn.compareTo(GVersionFull.GLSL_120) <= 0) {
        final Pair<GTermNameGlobal, GASTTermDeclaration> pf =
          program.first.getTerms().get(0);
        Assert.assertEquals(
          "p_com_io7m_parasol_Float_is_nan",
          pf.first.show());
        Assert.assertTrue(pf.second instanceof GASTTermFunction);
      } else {
        final Pair<GTermNameGlobal, GASTTermDeclaration> pf =
          program.first.getTerms().get(0);
        Assert.assertEquals("p_x_y_M_x", pf.first.show());
        Assert.assertTrue(pf.second instanceof GASTTermValue);
      }
      GFFITest.dumpVertex(program);
    }
  }

  private static void dumpVertex(
    final Pair<GASTShaderVertex, GASTShaderFragment> program)
    throws ConstraintError
  {
    System.err.println("Version: "
      + program.first.getGLSLVersion().getLongName());
    System.err
      .println("----------------------------------------------------------------------");
    GWriter.writeVertexShader(System.err, program.first);
    System.err
      .println("----------------------------------------------------------------------");
  }

  @SuppressWarnings("static-method") @Test public void testFloatLib()
    throws UIError,
      GFFIError,
      GVersionCheckerError,
      ConstraintError
  {
    final GPipeline gpipe =
      TestPipeline.makeGPipeline(new String[] { "glsl/ffi/float-lib.p" });

    final Map<GVersion, Pair<GASTShaderVertex, GASTShaderFragment>> p =
      gpipe.makeProgram(
        TestPipeline.shaderName("x.y.M", "p"),
        GVersionES.ALL,
        GVersionFull.ALL);
  }

  @SuppressWarnings("static-method") @Test public void testVectorLib()
    throws UIError,
      GFFIError,
      GVersionCheckerError,
      ConstraintError
  {
    final GPipeline gpipe =
      TestPipeline.makeGPipeline(new String[] { "glsl/ffi/vector-lib.p" });

    final Map<GVersion, Pair<GASTShaderVertex, GASTShaderFragment>> p =
      gpipe.makeProgram(
        TestPipeline.shaderName("x.y.M", "p"),
        GVersionES.ALL,
        GVersionFull.ALL);
  }

  @SuppressWarnings("static-method") @Test public void testSamplerLib()
    throws UIError,
      GFFIError,
      GVersionCheckerError,
      ConstraintError
  {
    final GPipeline gpipe =
      TestPipeline.makeGPipeline(new String[] { "glsl/ffi/sampler-lib.p" });

    final Map<GVersion, Pair<GASTShaderVertex, GASTShaderFragment>> p =
      gpipe.makeProgram(
        TestPipeline.shaderName("x.y.M", "p"),
        GVersionES.ALL,
        GVersionFull.ALL);
  }

}
