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

import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.TestPipeline;
import com.io7m.jparasol.TestUtilities;
import com.io7m.jparasol.glsl.GVersion.GVersionES;
import com.io7m.jparasol.glsl.GVersion.GVersionFull;
import com.io7m.jparasol.typed.ast.TASTCompilation;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDModule;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragment;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderVertex;

public final class GVersionCheckerTest
{
  @SuppressWarnings("static-method") @Test(
    expected = GVersionCheckerError.class) public
    void
    testFragmentOutputsBadType_ES100_0()
      throws ConstraintError,
        GVersionCheckerError
  {
    final TASTCompilation c =
      TestPipeline
        .completeTyped(new String[] { "glsl/version_checker/fragment-attributes-bad-type-es100-0.p" });
    final TASTDModule m = TestPipeline.getModule(c, "x.y", "M");

    final TASTDShaderFragment fs =
      (TASTDShaderFragment) m.getShaders().get("f");

    final GVersionChecker vc =
      GVersionChecker.newVersionChecker(TestUtilities.getLog());

    final SortedSet<GVersionFull> required_full =
      new TreeSet<GVersion.GVersionFull>();
    final SortedSet<GVersionES> required_es =
      new TreeSet<GVersion.GVersionES>();
    required_es.add(GVersionES.GLSL_ES_100);

    vc.checkFragmentShader(fs, required_full, required_es);
  }

  @SuppressWarnings("static-method") @Test(
    expected = GVersionCheckerError.class) public
    void
    testFragmentOutputsBadType_ES100_1()
      throws ConstraintError,
        GVersionCheckerError
  {
    final TASTCompilation c =
      TestPipeline
        .completeTyped(new String[] { "glsl/version_checker/fragment-attributes-bad-type-es100-1.p" });
    final TASTDModule m = TestPipeline.getModule(c, "x.y", "M");

    final TASTDShaderFragment fs =
      (TASTDShaderFragment) m.getShaders().get("f");

    final GVersionChecker vc =
      GVersionChecker.newVersionChecker(TestUtilities.getLog());
    final SortedSet<GVersionFull> required_full =
      new TreeSet<GVersion.GVersionFull>();
    final SortedSet<GVersionES> required_es =
      new TreeSet<GVersion.GVersionES>();
    required_es.add(GVersionES.GLSL_ES_100);

    vc.checkFragmentShader(fs, required_full, required_es);
  }

  @SuppressWarnings("static-method") @Test(
    expected = GVersionCheckerError.class) public
    void
    testVertexAttributesBadType_ES100_0()
      throws ConstraintError,
        GVersionCheckerError
  {
    final TASTCompilation c =
      TestPipeline
        .completeTyped(new String[] { "glsl/version_checker/vertex-attributes-bad-type-es100-0.p" });
    final TASTDModule m = TestPipeline.getModule(c, "x.y", "M");
    final TASTDShaderVertex fs = (TASTDShaderVertex) m.getShaders().get("v");

    final GVersionChecker vc =
      GVersionChecker.newVersionChecker(TestUtilities.getLog());
    final SortedSet<GVersionFull> required_full =
      new TreeSet<GVersion.GVersionFull>();
    final SortedSet<GVersionES> required_es =
      new TreeSet<GVersion.GVersionES>();
    required_es.add(GVersionES.GLSL_ES_100);

    vc.checkVertexShader(fs, required_full, required_es);
  }

  @SuppressWarnings("static-method") @Test(
    expected = GVersionCheckerError.class) public
    void
    testVertexAttributesBadType_ES100_1()
      throws ConstraintError,
        GVersionCheckerError
  {
    final TASTCompilation c =
      TestPipeline
        .completeTyped(new String[] { "glsl/version_checker/vertex-attributes-bad-type-es100-1.p" });
    final TASTDModule m = TestPipeline.getModule(c, "x.y", "M");
    final TASTDShaderVertex fs = (TASTDShaderVertex) m.getShaders().get("v");

    final GVersionChecker vc =
      GVersionChecker.newVersionChecker(TestUtilities.getLog());
    final SortedSet<GVersionFull> required_full =
      new TreeSet<GVersion.GVersionFull>();
    final SortedSet<GVersionES> required_es =
      new TreeSet<GVersion.GVersionES>();
    required_es.add(GVersionES.GLSL_ES_100);

    vc.checkVertexShader(fs, required_full, required_es);
  }

  @SuppressWarnings("static-method") @Test(
    expected = GVersionCheckerError.class) public
    void
    testFragmentOutputsBad_ES100_0()
      throws ConstraintError,
        GVersionCheckerError
  {
    final TASTCompilation c =
      TestPipeline
        .completeTyped(new String[] { "glsl/version_checker/fragment-outputs-bad-es100-0.p" });
    final TASTDModule m = TestPipeline.getModule(c, "x.y", "M");

    final TASTDShaderFragment fs =
      (TASTDShaderFragment) m.getShaders().get("f");

    final GVersionChecker vc =
      GVersionChecker.newVersionChecker(TestUtilities.getLog());
    final SortedSet<GVersionFull> required_full =
      new TreeSet<GVersion.GVersionFull>();
    final SortedSet<GVersionES> required_es =
      new TreeSet<GVersion.GVersionES>();
    required_es.add(GVersionES.GLSL_ES_100);

    vc.checkFragmentShader(fs, required_full, required_es);
  }

  @SuppressWarnings("static-method") @Test public
    void
    testFragmentOutputsOK_ES100_0()
      throws ConstraintError,
        GVersionCheckerError
  {
    final TASTCompilation c =
      TestPipeline
        .completeTyped(new String[] { "glsl/version_checker/fragment-outputs-ok-es100-0.p" });
    final TASTDModule m = TestPipeline.getModule(c, "x.y", "M");

    final TASTDShaderFragment fs =
      (TASTDShaderFragment) m.getShaders().get("f");

    final GVersionChecker vc =
      GVersionChecker.newVersionChecker(TestUtilities.getLog());
    final SortedSet<GVersionFull> required_full =
      new TreeSet<GVersion.GVersionFull>();
    final SortedSet<GVersionES> required_es =
      new TreeSet<GVersion.GVersionES>();
    required_es.add(GVersionES.GLSL_ES_100);

    final GVersionsSupported s =
      vc.checkFragmentShader(fs, required_full, required_es);
    Assert.assertTrue(s.getESVersions().contains(GVersionES.GLSL_ES_100));
  }
}
