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

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.TestPipeline;
import com.io7m.jparasol.typed.ast.TASTCompilation;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDFunctionDefined;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDValueDefined;

public final class OccurencesTest
{
  @SuppressWarnings("static-method") @Test public
    void
    testOccurencesConditional_0()
      throws ConstraintError
  {
    final TASTCompilation r =
      TestPipeline
        .completeTyped(new String[] { "typed/occurences/conditional-0.p" });
    final TASTDFunctionDefined f =
      (TASTDFunctionDefined) r
        .lookupTerm(TestPipeline.termName("x.y.M", "f"));

    final Set<String> check = new HashSet<String>();
    check.add("x");
    check.add("y");
    check.add("z");

    final Set<String> found = Occurences.occursIn(f.getBody(), check);
    Assert.assertEquals(1, found.size());
    Assert.assertTrue(found.contains("x"));
  }

  @SuppressWarnings("static-method") @Test public
    void
    testOccurencesFunction_0()
      throws ConstraintError
  {
    final TASTCompilation r =
      TestPipeline
        .completeTyped(new String[] { "typed/occurences/function-0.p" });
    final TASTDFunctionDefined f =
      (TASTDFunctionDefined) r
        .lookupTerm(TestPipeline.termName("x.y.M", "f"));

    final Set<String> check = new HashSet<String>();
    check.add("x");
    check.add("y");
    check.add("z");

    final Set<String> found = Occurences.occursIn(f.getBody(), check);
    Assert.assertEquals(1, found.size());
    Assert.assertTrue(found.contains("x"));
  }

  @SuppressWarnings("static-method") @Test public void testOccurencesNew_0()
    throws ConstraintError
  {
    final TASTCompilation r =
      TestPipeline.completeTyped(new String[] { "typed/occurences/new-0.p" });
    final TASTDValueDefined f =
      (TASTDValueDefined) r.lookupTerm(TestPipeline.termName("x.y.M", "x"));

    final Set<String> check = new HashSet<String>();
    check.add("x");
    check.add("y");
    check.add("z");

    final Set<String> found = Occurences.occursIn(f.getExpression(), check);
    Assert.assertEquals(0, found.size());
  }

  @SuppressWarnings("static-method") @Test public
    void
    testOccurencesProjection_0()
      throws ConstraintError
  {
    final TASTCompilation r =
      TestPipeline
        .completeTyped(new String[] { "typed/occurences/projection-0.p" });
    final TASTDFunctionDefined f =
      (TASTDFunctionDefined) r
        .lookupTerm(TestPipeline.termName("x.y.M", "f"));

    final Set<String> check = new HashSet<String>();
    check.add("x");
    check.add("y");
    check.add("z");

    final Set<String> found = Occurences.occursIn(f.getBody(), check);
    Assert.assertEquals(1, found.size());
    Assert.assertTrue(found.contains("x"));
  }

  @SuppressWarnings("static-method") @Test public
    void
    testOccurencesRecord_0()
      throws ConstraintError
  {
    final TASTCompilation r =
      TestPipeline
        .completeTyped(new String[] { "typed/occurences/record-0.p" });
    final TASTDFunctionDefined f =
      (TASTDFunctionDefined) r
        .lookupTerm(TestPipeline.termName("x.y.M", "f"));

    final Set<String> check = new HashSet<String>();
    check.add("x");
    check.add("y");
    check.add("z");

    final Set<String> found = Occurences.occursIn(f.getBody(), check);
    Assert.assertEquals(1, found.size());
    Assert.assertTrue(found.contains("x"));
  }

  @SuppressWarnings("static-method") @Test public
    void
    testOccurencesRecord_1()
      throws ConstraintError
  {
    final TASTCompilation r =
      TestPipeline
        .completeTyped(new String[] { "typed/occurences/record-1.p" });
    final TASTDValueDefined f =
      (TASTDValueDefined) r.lookupTerm(TestPipeline.termName("x.y.M", "f"));

    final Set<String> check = new HashSet<String>();
    check.add("x");
    check.add("y");
    check.add("z");

    final Set<String> found = Occurences.occursIn(f.getExpression(), check);
    Assert.assertEquals(0, found.size());
  }

  @SuppressWarnings("static-method") @Test public
    void
    testOccurencesRecord_2()
      throws ConstraintError
  {
    final TASTCompilation r =
      TestPipeline
        .completeTyped(new String[] { "typed/occurences/record-2.p" });
    final TASTDValueDefined f =
      (TASTDValueDefined) r.lookupTerm(TestPipeline.termName("x.y.M", "f"));

    final Set<String> check = new HashSet<String>();
    check.add("x");
    check.add("y");
    check.add("z");

    final Set<String> found = Occurences.occursIn(f.getExpression(), check);
    Assert.assertEquals(1, found.size());
    Assert.assertTrue(found.contains("x"));
  }

  @SuppressWarnings("static-method") @Test public
    void
    testOccurencesRecord_3()
      throws ConstraintError
  {
    final TASTCompilation r =
      TestPipeline
        .completeTyped(new String[] { "typed/occurences/record-3.p" });
    final TASTDValueDefined f =
      (TASTDValueDefined) r.lookupTerm(TestPipeline.termName("x.y.M", "f"));

    final Set<String> check = new HashSet<String>();
    check.add("x");
    check.add("y");
    check.add("z");

    final Set<String> found = Occurences.occursIn(f.getExpression(), check);
    Assert.assertEquals(0, found.size());
  }

  @SuppressWarnings("static-method") @Test public
    void
    testOccurencesSwizzle_0()
      throws ConstraintError
  {
    final TASTCompilation r =
      TestPipeline
        .completeTyped(new String[] { "typed/occurences/swizzle-0.p" });
    final TASTDFunctionDefined f =
      (TASTDFunctionDefined) r
        .lookupTerm(TestPipeline.termName("x.y.M", "f"));

    final Set<String> check = new HashSet<String>();
    check.add("x");
    check.add("y");
    check.add("z");

    final Set<String> found = Occurences.occursIn(f.getBody(), check);
    Assert.assertEquals(1, found.size());
    Assert.assertTrue(found.contains("x"));
  }
}
