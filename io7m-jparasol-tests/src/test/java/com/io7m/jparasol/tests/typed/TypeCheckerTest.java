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

package com.io7m.jparasol.tests.typed;

import java.util.List;

import javax.annotation.Nonnull;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.junit.Assert;
import org.junit.Test;

import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.tests.TestPipeline;
import com.io7m.jparasol.typed.TType.TBoolean;
import com.io7m.jparasol.typed.TType.TFloat;
import com.io7m.jparasol.typed.TType.TInteger;
import com.io7m.jparasol.typed.TType.TRecord;
import com.io7m.jparasol.typed.TType.TRecordField;
import com.io7m.jparasol.typed.TType.TVector2F;
import com.io7m.jparasol.typed.TType.TVector2I;
import com.io7m.jparasol.typed.TType.TVector3F;
import com.io7m.jparasol.typed.TType.TVector3I;
import com.io7m.jparasol.typed.TType.TVector4F;
import com.io7m.jparasol.typed.TType.TVector4I;
import com.io7m.jparasol.typed.TTypeNameFlat;
import com.io7m.jparasol.typed.TypeCheckerError;
import com.io7m.jparasol.typed.TypeCheckerError.Code;
import com.io7m.jparasol.typed.ast.TASTCompilation;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDFunctionDefined;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDFunctionExternal;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDModule;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragment;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragmentInput;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderVertex;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderVertexOutput;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDTypeRecord;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDValueDefined;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTEApplication;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTENew;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTERecord;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTESwizzle;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTRecordFieldAssignment;
import com.io7m.jparasol.typed.ast.TASTNameTermShaderFlatType;
import com.io7m.jparasol.typed.ast.TASTNameTypeShaderFlatType;
import com.io7m.jparasol.typed.ast.TASTNameTypeTermFlatType;
import com.io7m.jparasol.typed.ast.TASTReference;
import com.io7m.jparasol.typed.ast.TASTShaderNameFlat;
import com.io7m.jparasol.typed.ast.TASTTermNameFlat;

@SuppressWarnings("static-method") public final class TypeCheckerTest
{
  static void checkMustFailWithCode(
    final @Nonnull String[] names,
    final @Nonnull TypeCheckerError.Code code)
    throws TypeCheckerError
  {
    boolean caught = false;

    try {
      TestPipeline.typed(names);
    } catch (final TypeCheckerError e) {
      caught = true;
      Assert.assertEquals(code, e.getCode());
      System.err.println(e);
      throw e;
    }

    Assert.assertTrue("Caught exception", caught);
  }

  static void checkMustFailWithCodeInternal(
    final @Nonnull String[] names,
    final @Nonnull TypeCheckerError.Code code)
    throws TypeCheckerError
  {
    boolean caught = false;

    try {
      TestPipeline.typedInternal(names);
    } catch (final TypeCheckerError e) {
      caught = true;
      Assert.assertEquals(code, e.getCode());
      System.err.println(e);
      throw e;
    }

    Assert.assertTrue("Caught exception", caught);
  }

  @Test public void testAllOK_0()
    throws TypeCheckerError
  {
    TestPipeline.typedInternal(new String[] { "typed/all.p" });
  }

  @Test public void testBugOld_b77370072()
    throws TypeCheckerError
  {
    TestPipeline.typed(new String[] { "typed/bug-old-b77370072.p" });
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testFragmentShaderDepthBadType_0()
      throws TypeCheckerError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "typed/fragment-shader-depth-bad-type-0.p" },
      Code.TYPE_ERROR_SHADER_DEPTH_NOT_FLOAT);
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testFragmentShaderAssignmentBadType_0()
      throws TypeCheckerError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "typed/fragment-shader-assignment-bad-type-0.p" },
      Code.TYPE_ERROR_SHADER_ASSIGNMENT_BAD_TYPE);
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testFragmentShaderAssignmentBadType_1()
      throws TypeCheckerError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "typed/fragment-shader-assignment-bad-type-1.p" },
      Code.TYPE_ERROR_SHADER_ASSIGNMENT_BAD_TYPE);
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testFragmentShaderAssignmentBadType_2()
      throws TypeCheckerError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "typed/fragment-shader-assignment-bad-type-2.p" },
      Code.TYPE_ERROR_SHADER_ASSIGNMENT_BAD_TYPE);
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testFragmentShaderAssignmentBadType_3()
      throws TypeCheckerError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "typed/fragment-shader-assignment-bad-type-3.p" },
      Code.TYPE_ERROR_SHADER_ASSIGNMENT_BAD_TYPE);
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testFragmentShaderAssignmentBadType_4()
      throws TypeCheckerError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "typed/fragment-shader-assignment-bad-type-4.p" },
      Code.TYPE_ERROR_SHADER_ASSIGNMENT_BAD_TYPE);
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testFragmentShaderAssignmentBadType_5()
      throws TypeCheckerError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "typed/fragment-shader-assignment-bad-type-5.p" },
      Code.TYPE_ERROR_SHADER_ASSIGNMENT_BAD_TYPE);
  }

  @Test public void testFragmentShaderAssignmentOK_0()
    throws TypeCheckerError
  {
    final TASTCompilation r =
      TestPipeline.typed(new String[] { "typed/fragment-shader-ok-0.p" });

    final TASTDModule m = TestPipeline.getModule(r, "x.y", "M");
    final TASTDShaderFragment v =
      (TASTDShaderFragment) m.getShaders().get("f");

    Assert.assertEquals("f", v.getName().getActual());

    Assert.assertEquals("in_0", v.getInputs().get(0).getName().getCurrent());
    Assert.assertEquals(TVector4F.get(), v.getInputs().get(0).getType());

    Assert.assertEquals("p_0", v
      .getParameters()
      .get(0)
      .getName()
      .getCurrent());
    Assert.assertEquals(TVector4F.get(), v.getParameters().get(0).getType());

    Assert.assertEquals("out_0", v.getOutputs().get(0).getName().getActual());
    Assert.assertEquals(TVector4F.get(), v.getOutputs().get(0).getType());
    Assert.assertEquals("out_1", v.getOutputs().get(1).getName().getActual());
    Assert.assertEquals(TVector4F.get(), v.getOutputs().get(1).getType());
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testFragmentShaderDiscardNotBoolean_0()
      throws TypeCheckerError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "typed/fragment-shader-discard-not-boolean-0.p" },
      Code.TYPE_ERROR_SHADER_DISCARD_NOT_BOOLEAN);
  }

  @Test public void testGraphShader_0()
    throws TypeCheckerError
  {
    final TASTCompilation r =
      TestPipeline.typed(new String[] { "typed/graph-shader-0.p" });

    {
      final DirectedAcyclicGraph<TASTNameTermShaderFlatType, TASTReference> g =
        r.getShaderTermGraph();

      for (final ModulePathFlat k : r.getModules().keySet()) {
        final TASTDModule m = r.getModules().get(k);
        for (final String tn : m.getShaders().keySet()) {
          final TASTShaderNameFlat p = new TASTShaderNameFlat(k, tn);
          System.out.println("Check " + p);
          Assert.assertTrue(g.containsVertex(p));
        }
      }

      Assert.assertEquals(9, g.vertexSet().size());
    }

    {
      final DirectedAcyclicGraph<TASTNameTypeShaderFlatType, TASTReference> g =
        r.getShaderTypeGraph();

      for (final ModulePathFlat k : r.getModules().keySet()) {
        final TASTDModule m = r.getModules().get(k);
        for (final String tn : m.getShaders().keySet()) {
          final TASTShaderNameFlat p = new TASTShaderNameFlat(k, tn);
          System.out.println("Check " + p);
          Assert.assertTrue(g.containsVertex(p));
        }
      }

      Assert.assertEquals(9, g.vertexSet().size());
    }
  }

  @Test public void testGraphTermTerm_0()
    throws TypeCheckerError
  {
    final TASTCompilation r =
      TestPipeline.typed(new String[] { "typed/graph-term-term-0.p" });

    {
      final DirectedAcyclicGraph<TASTTermNameFlat, TASTReference> g =
        r.getTermGraph();

      for (final ModulePathFlat k : r.getModules().keySet()) {
        final TASTDModule m = r.getModules().get(k);
        for (final String tn : m.getTerms().keySet()) {
          final TASTTermNameFlat p = new TASTTermNameFlat(k, tn);
          System.out.println("Check " + p);
          Assert.assertTrue(g.containsVertex(p));
        }
      }

      Assert.assertEquals(9, g.vertexSet().size());
    }

    {
      final DirectedAcyclicGraph<TASTNameTypeTermFlatType, TASTReference> g =
        r.getTermTypeGraph();

      for (final ModulePathFlat k : r.getModules().keySet()) {
        final TASTDModule m = r.getModules().get(k);
        for (final String tn : m.getTerms().keySet()) {
          final TASTTermNameFlat p = new TASTTermNameFlat(k, tn);
          System.out.println("Check " + p);
          Assert.assertTrue(g.containsVertex(p));
        }
      }

      Assert.assertEquals(9, g.vertexSet().size());
    }

    {
      final DirectedAcyclicGraph<TASTNameTermShaderFlatType, TASTReference> g =
        r.getShaderTermGraph();

      for (final ModulePathFlat k : r.getModules().keySet()) {
        final TASTDModule m = r.getModules().get(k);
        for (final String tn : m.getTerms().keySet()) {
          final TASTTermNameFlat p = new TASTTermNameFlat(k, tn);
          System.out.println("Check " + p);
          Assert.assertTrue(g.containsVertex(p));
        }
      }

      Assert.assertEquals(9, g.vertexSet().size());
    }
  }

  @Test public void testGraphTypeType_0()
    throws TypeCheckerError
  {
    final TASTCompilation r =
      TestPipeline.typed(new String[] { "typed/graph-type-type-0.p" });

    {
      final DirectedAcyclicGraph<TTypeNameFlat, TASTReference> g =
        r.getTypeGraph();

      for (final ModulePathFlat k : r.getModules().keySet()) {
        final TASTDModule m = r.getModules().get(k);
        for (final String tn : m.getTypes().keySet()) {
          final TTypeNameFlat p = new TTypeNameFlat(k, tn);
          System.out.println("Check " + p);
          Assert.assertTrue(g.containsVertex(p));
        }
      }

      Assert.assertEquals(9, g.vertexSet().size());
    }

    {
      final DirectedAcyclicGraph<TASTNameTypeTermFlatType, TASTReference> g =
        r.getTermTypeGraph();

      for (final ModulePathFlat k : r.getModules().keySet()) {
        final TASTDModule m = r.getModules().get(k);
        for (final String tn : m.getTypes().keySet()) {
          final TTypeNameFlat p = new TTypeNameFlat(k, tn);
          System.out.println("Check " + p);
          Assert.assertTrue(g.containsVertex(p));
        }
      }

      Assert.assertEquals(9, g.vertexSet().size());
    }

    {
      final DirectedAcyclicGraph<TASTNameTypeShaderFlatType, TASTReference> g =
        r.getShaderTypeGraph();

      for (final ModulePathFlat k : r.getModules().keySet()) {
        final TASTDModule m = r.getModules().get(k);
        for (final String tn : m.getTypes().keySet()) {
          final TTypeNameFlat p = new TTypeNameFlat(k, tn);
          System.out.println("Check " + p);
          Assert.assertTrue(g.containsVertex(p));
        }
      }

      Assert.assertEquals(9, g.vertexSet().size());
    }
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testProgramShaderNotCompatible_0()
      throws TypeCheckerError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "typed/program-shader-not-compatible-0.p" },
      Code.TYPE_ERROR_SHADERS_INCOMPATIBLE);
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testProgramShaderNotCompatible_1()
      throws TypeCheckerError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "typed/program-shader-not-compatible-1.p" },
      Code.TYPE_ERROR_SHADERS_INCOMPATIBLE);
  }

  @Test public void testProgramShaderOK_0()
    throws TypeCheckerError
  {
    final TASTCompilation r =
      TestPipeline.typed(new String[] { "typed/program-shader-ok-0.p" });

    final TASTDModule m = TestPipeline.getModule(r, "x.y", "M");
    final TASTDShaderVertex v = (TASTDShaderVertex) m.getShaders().get("v");
    final TASTDShaderFragment f =
      (TASTDShaderFragment) m.getShaders().get("f");

    for (final TASTDShaderFragmentInput i : f.getInputs()) {
      for (final TASTDShaderVertexOutput o : v.getOutputs()) {
        if (i.getName().getCurrent().equals(o.getName().getActual())) {
          Assert.assertEquals(i.getType(), o.getType());
        }
      }
    }
  }

  @Test public void testProgramShaderOK_1()
    throws TypeCheckerError
  {
    final TASTCompilation r =
      TestPipeline.typed(new String[] { "typed/program-shader-ok-1.p" });

    final TASTDModule mm = TestPipeline.getModule(r, "x.y", "M");
    final TASTDModule mn = TestPipeline.getModule(r, "x.y", "N");

    final TASTDShaderVertex v = (TASTDShaderVertex) mm.getShaders().get("v");
    final TASTDShaderFragment f =
      (TASTDShaderFragment) mn.getShaders().get("f");

    for (final TASTDShaderFragmentInput i : f.getInputs()) {
      for (final TASTDShaderVertexOutput o : v.getOutputs()) {
        if (i.getName().getCurrent().equals(o.getName().getActual())) {
          Assert.assertEquals(i.getType(), o.getType());
        }
      }
    }
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testProgramShaderWrongShaderType_0()
      throws TypeCheckerError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "typed/program-shader-wrong-shader-type-0.p" },
      Code.TYPE_ERROR_SHADER_WRONG_SHADER_TYPE);
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testProgramShaderWrongShaderType_1()
      throws TypeCheckerError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "typed/program-shader-wrong-shader-type-1.p" },
      Code.TYPE_ERROR_SHADER_WRONG_SHADER_TYPE);
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testTermExpressionApplicationBadCount_0()
      throws TypeCheckerError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "typed/term-expression-app-bad-count-0.p" },
      Code.TYPE_ERROR_EXPRESSION_APPLICATION_BAD_TYPES);
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testTermExpressionApplicationBadTypes_0()
      throws TypeCheckerError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "typed/term-expression-app-bad-types-0.p" },
      Code.TYPE_ERROR_EXPRESSION_APPLICATION_BAD_TYPES);
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testTermExpressionApplicationNotFunction_0()
      throws TypeCheckerError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "typed/term-expression-app-not-function-0.p" },
      Code.TYPE_ERROR_EXPRESSION_APPLICATION_NOT_FUNCTION_TYPE);
  }

  @Test public void testTermExpressionApplicationOK_0()
    throws TypeCheckerError
  {
    final TASTCompilation r =
      TestPipeline.typed(new String[] { "typed/term-expression-app-ok-0.p" });

    final TASTDModule m = TestPipeline.getModule(r, "x.y", "M");
    final TASTDValueDefined z = (TASTDValueDefined) m.getTerms().get("z");

    final TASTEApplication app = (TASTEApplication) z.getExpression();
    Assert.assertEquals("$x.y.M.f", app.getName().show());
    Assert.assertEquals(TInteger.get(), app.getType());
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testTermExpressionConditionalNotBoolean_0()
      throws TypeCheckerError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "typed/term-expression-conditional-not-boolean-0.p" },
      Code.TYPE_ERROR_EXPRESSION_CONDITION_NOT_BOOLEAN);
  }

  @Test public void testTermExpressionConditionalOK_0()
    throws TypeCheckerError
  {
    final TASTCompilation r =
      TestPipeline
        .typed(new String[] { "typed/term-expression-conditional-ok-0.p" });
    final TASTDModule m = TestPipeline.getModule(r, "x.y", "M");

    final TASTDValueDefined v = (TASTDValueDefined) m.getTerms().get("z");
    Assert.assertEquals("z", v.getName().getActual());
    Assert.assertEquals(TInteger.get(), v.getExpression().getType());
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testTermExpressionLetBadAscription_0()
      throws TypeCheckerError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "typed/term-expression-let-bad-ascription-0.p" },
      Code.TYPE_ERROR_VALUE_ASCRIPTION_MISMATCH);
  }

  @Test public void testTermExpressionLetOK_0()
    throws TypeCheckerError
  {
    final TASTCompilation r =
      TestPipeline.typed(new String[] { "typed/term-expression-let-ok-0.p" });
    final TASTDModule m = TestPipeline.getModule(r, "x.y", "M");

    final TASTDValueDefined v = (TASTDValueDefined) m.getTerms().get("z");
    Assert.assertEquals("z", v.getName().getActual());
    Assert.assertEquals(TVector3F.get(), v.getExpression().getType());
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testTermExpressionNewNoConstructors_0()
      throws TypeCheckerError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "typed/term-expression-new-no-constructors-0.p" },
      Code.TYPE_ERROR_EXPRESSION_NEW_NO_APPROPRIATE_CONSTRUCTORS);
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testTermExpressionNewNoConstructors_1()
      throws TypeCheckerError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "typed/term-expression-new-no-constructors-1.p" },
      Code.TYPE_ERROR_EXPRESSION_NEW_NO_APPROPRIATE_CONSTRUCTORS);
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testTermExpressionNewNotConstructable_0()
      throws TypeCheckerError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "typed/term-expression-new-not-constructable-0.p" },
      Code.TYPE_ERROR_EXPRESSION_NEW_TYPE_NOT_CONSTRUCTABLE);
  }

  @Test public void testTermExpressionNewOK_0()
    throws TypeCheckerError
  {
    final TASTCompilation r =
      TestPipeline.typed(new String[] { "typed/term-expression-new-ok-0.p" });
    final TASTDModule m = TestPipeline.getModule(r, "x.y", "M");

    for (int index = 0; index < TInteger.get().getConstructors().size(); ++index) {
      final TASTDValueDefined v =
        (TASTDValueDefined) m.getTerms().get("z" + index);
      Assert.assertEquals("z" + index, v.getName().getActual());
      final TASTENew e = (TASTENew) v.getExpression();
      Assert.assertEquals(TInteger.get(), e.getType());
    }

    for (int index = 0; index < TFloat.get().getConstructors().size(); ++index) {
      final TASTDValueDefined v =
        (TASTDValueDefined) m.getTerms().get("r" + index);
      Assert.assertEquals("r" + index, v.getName().getActual());
      final TASTENew e = (TASTENew) v.getExpression();
      Assert.assertEquals(TFloat.get(), e.getType());
    }

    for (int index = 0; index < TBoolean.get().getConstructors().size(); ++index) {
      final TASTDValueDefined v =
        (TASTDValueDefined) m.getTerms().get("b" + index);
      Assert.assertEquals("b" + index, v.getName().getActual());
      final TASTENew e = (TASTENew) v.getExpression();
      Assert.assertEquals(TBoolean.get(), e.getType());
    }

    for (int index = 0; index < TVector2F.get().getConstructors().size(); ++index) {
      final TASTDValueDefined v =
        (TASTDValueDefined) m.getTerms().get("v2f" + index);
      Assert.assertEquals("v2f" + index, v.getName().getActual());
      final TASTENew e = (TASTENew) v.getExpression();
      Assert.assertEquals(TVector2F.get(), e.getType());
    }

    for (int index = 0; index < TVector3F.get().getConstructors().size(); ++index) {
      final TASTDValueDefined v =
        (TASTDValueDefined) m.getTerms().get("v3f" + index);
      Assert.assertEquals("v3f" + index, v.getName().getActual());
      final TASTENew e = (TASTENew) v.getExpression();
      Assert.assertEquals(TVector3F.get(), e.getType());
    }

    for (int index = 0; index < TVector4F.get().getConstructors().size(); ++index) {
      final TASTDValueDefined v =
        (TASTDValueDefined) m.getTerms().get("v4f" + index);
      Assert.assertEquals("v4f" + index, v.getName().getActual());
      final TASTENew e = (TASTENew) v.getExpression();
      Assert.assertEquals(TVector4F.get(), e.getType());
    }
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testTermExpressionRecordFieldBadType_0()
      throws TypeCheckerError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "typed/term-expression-record-field-bad-type-0.p" },
      Code.TYPE_ERROR_EXPRESSION_RECORD_FIELD_BAD_TYPE);
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testTermExpressionRecordFieldNotAssigned_0()
      throws TypeCheckerError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "typed/term-expression-record-field-not-assigned-0.p" },
      Code.TYPE_ERROR_EXPRESSION_RECORD_FIELDS_UNASSIGNED);
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testTermExpressionRecordNotRecordType_0()
      throws TypeCheckerError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "typed/term-expression-record-not-record-0.p" },
      Code.TYPE_ERROR_EXPRESSION_RECORD_NOT_RECORD_TYPE);
  }

  @Test public void testTermExpressionRecordOK_0()
    throws TypeCheckerError
  {
    final TASTCompilation r =
      TestPipeline
        .typed(new String[] { "typed/term-expression-record-ok-0.p" });
    final TASTDModule m = TestPipeline.getModule(r, "x.y", "M");
    final TASTDValueDefined x = (TASTDValueDefined) m.getTerms().get("x");
    final TASTERecord e = (TASTERecord) x.getExpression();

    final TASTRecordFieldAssignment ea0 = e.getAssignments().get(0);
    final TASTRecordFieldAssignment ea1 = e.getAssignments().get(1);
    final TASTRecordFieldAssignment ea2 = e.getAssignments().get(2);

    Assert.assertEquals("z", ea0.getName().getActual());
    Assert.assertEquals("b", ea1.getName().getActual());
    Assert.assertEquals("r", ea2.getName().getActual());
    Assert.assertEquals(TInteger.get(), ea0.getExpression().getType());
    Assert.assertEquals(TBoolean.get(), ea1.getExpression().getType());
    Assert.assertEquals(TFloat.get(), ea2.getExpression().getType());
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testTermExpressionRecordProjectionNoSuchField_0()
      throws TypeCheckerError
  {
    TypeCheckerTest
      .checkMustFailWithCode(
        new String[] { "typed/term-expression-record-projection-no-such-field-0.p" },
        Code.TYPE_ERROR_EXPRESSION_RECORD_PROJECTION_NO_SUCH_FIELD);
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testTermExpressionRecordProjectionNotRecord_0()
      throws TypeCheckerError
  {
    TypeCheckerTest
      .checkMustFailWithCode(
        new String[] { "typed/term-expression-record-projection-not-record-0.p" },
        Code.TYPE_ERROR_EXPRESSION_RECORD_PROJECTION_NOT_RECORD);
  }

  @Test public void testTermExpressionRecordProjectionOK_0()
    throws TypeCheckerError
  {
    final TASTCompilation r =
      TestPipeline
        .typed(new String[] { "typed/term-expression-record-projection-ok-0.p" });
    final TASTDModule m = TestPipeline.getModule(r, "x.y", "M");

    final TASTDValueDefined v = (TASTDValueDefined) m.getTerms().get("x");
    Assert.assertEquals("x", v.getName().getActual());
    Assert.assertEquals(TFloat.get(), v.getExpression().getType());
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testTermExpressionRecordUnknownField_0()
      throws TypeCheckerError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "typed/term-expression-record-unknown-field-0.p" },
      Code.TYPE_ERROR_EXPRESSION_RECORD_FIELD_UNKNOWN);
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testTermExpressionSwizzleNotVector_0()
      throws TypeCheckerError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "typed/term-expression-swizzle-not-vector-0.p" },
      Code.TYPE_ERROR_EXPRESSION_SWIZZLE_NOT_VECTOR);
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testTermExpressionSwizzleTooMany_0()
      throws TypeCheckerError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "typed/term-expression-swizzle-too-many-0.p" },
      Code.TYPE_ERROR_EXPRESSION_SWIZZLE_TOO_MANY_COMPONENTS);
  }

  @Test public void testTermExpressionSwizzleTypesOK_0()
    throws TypeCheckerError
  {
    final TASTCompilation r =
      TestPipeline
        .typed(new String[] { "typed/term-expression-swizzle-types-ok-0.p" });
    final TASTDModule m = TestPipeline.getModule(r, "x.y", "M");

    for (int index = 1; index <= 4; ++index) {
      final TASTDValueDefined v =
        (TASTDValueDefined) m.getTerms().get("v2f" + index);
      final TASTESwizzle e = (TASTESwizzle) v.getExpression();
      switch (index) {
        case 1:
          Assert.assertEquals(TFloat.get(), e.getType());
          break;
        case 2:
          Assert.assertEquals(TVector2F.get(), e.getType());
          break;
        case 3:
          Assert.assertEquals(TVector3F.get(), e.getType());
          break;
        case 4:
          Assert.assertEquals(TVector4F.get(), e.getType());
          break;
      }
    }

    for (int index = 1; index <= 4; ++index) {
      final TASTDValueDefined v =
        (TASTDValueDefined) m.getTerms().get("v3f" + index);
      final TASTESwizzle e = (TASTESwizzle) v.getExpression();
      switch (index) {
        case 1:
          Assert.assertEquals(TFloat.get(), e.getType());
          break;
        case 2:
          Assert.assertEquals(TVector2F.get(), e.getType());
          break;
        case 3:
          Assert.assertEquals(TVector3F.get(), e.getType());
          break;
        case 4:
          Assert.assertEquals(TVector4F.get(), e.getType());
          break;
      }
    }

    for (int index = 1; index <= 4; ++index) {
      final TASTDValueDefined v =
        (TASTDValueDefined) m.getTerms().get("v4f" + index);
      final TASTESwizzle e = (TASTESwizzle) v.getExpression();
      switch (index) {
        case 1:
          Assert.assertEquals(TFloat.get(), e.getType());
          break;
        case 2:
          Assert.assertEquals(TVector2F.get(), e.getType());
          break;
        case 3:
          Assert.assertEquals(TVector3F.get(), e.getType());
          break;
        case 4:
          Assert.assertEquals(TVector4F.get(), e.getType());
          break;
      }
    }
  }

  @Test public void testTermExpressionSwizzleTypesOK_1()
    throws TypeCheckerError
  {
    final TASTCompilation r =
      TestPipeline
        .typed(new String[] { "typed/term-expression-swizzle-types-ok-1.p" });
    final TASTDModule m = TestPipeline.getModule(r, "x.y", "M");

    for (int index = 1; index <= 4; ++index) {
      final TASTDValueDefined v =
        (TASTDValueDefined) m.getTerms().get("v2i" + index);
      final TASTESwizzle e = (TASTESwizzle) v.getExpression();
      switch (index) {
        case 1:
          Assert.assertEquals(TInteger.get(), e.getType());
          break;
        case 2:
          Assert.assertEquals(TVector2I.get(), e.getType());
          break;
        case 3:
          Assert.assertEquals(TVector3I.get(), e.getType());
          break;
        case 4:
          Assert.assertEquals(TVector4I.get(), e.getType());
          break;
      }
    }

    for (int index = 1; index <= 4; ++index) {
      final TASTDValueDefined v =
        (TASTDValueDefined) m.getTerms().get("v3i" + index);
      final TASTESwizzle e = (TASTESwizzle) v.getExpression();
      switch (index) {
        case 1:
          Assert.assertEquals(TInteger.get(), e.getType());
          break;
        case 2:
          Assert.assertEquals(TVector2I.get(), e.getType());
          break;
        case 3:
          Assert.assertEquals(TVector3I.get(), e.getType());
          break;
        case 4:
          Assert.assertEquals(TVector4I.get(), e.getType());
          break;
      }
    }

    for (int index = 1; index <= 4; ++index) {
      final TASTDValueDefined v =
        (TASTDValueDefined) m.getTerms().get("v4i" + index);
      final TASTESwizzle e = (TASTESwizzle) v.getExpression();
      switch (index) {
        case 1:
          Assert.assertEquals(TInteger.get(), e.getType());
          break;
        case 2:
          Assert.assertEquals(TVector2I.get(), e.getType());
          break;
        case 3:
          Assert.assertEquals(TVector3I.get(), e.getType());
          break;
        case 4:
          Assert.assertEquals(TVector4I.get(), e.getType());
          break;
      }
    }
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testTermExpressionSwizzleUnknownComponent_0()
      throws TypeCheckerError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "typed/term-expression-swizzle-unknown-component-0.p" },
      Code.TYPE_ERROR_EXPRESSION_SWIZZLE_UNKNOWN_COMPONENT);
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testTermFunctionBadType_0()
      throws TypeCheckerError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "typed/term-function-bad-type-0.p" },
      Code.TYPE_ERROR_FUNCTION_BODY_RETURN_INCOMPATIBLE);
  }

  @Test public void testTermFunctionExternalTypeOK_0()
    throws TypeCheckerError
  {
    final TASTCompilation r =
      TestPipeline
        .typedInternal(new String[] { "typed/term-function-external-type-ok-0.p" });

    final TASTDModule m = TestPipeline.getModule(r, "x.y", "M");
    final TASTDFunctionExternal f =
      (TASTDFunctionExternal) m.getTerms().get("f");

    Assert.assertEquals(TInteger.get(), f.getArguments().get(0).getType());
    Assert.assertEquals(TInteger.get(), f.getType().getReturnType());

    System.out.println(f.getType().getName());
  }

  @Test public void testTermFunctionExternalTypeOK_1()
    throws TypeCheckerError
  {
    final TASTCompilation r =
      TestPipeline
        .typedInternal(new String[] { "typed/term-function-external-type-ok-1.p" });

    final TASTDModule m = TestPipeline.getModule(r, "x.y", "M");
    final TASTDFunctionExternal f =
      (TASTDFunctionExternal) m.getTerms().get("f");

    Assert.assertEquals(TInteger.get(), f.getArguments().get(0).getType());
    Assert.assertEquals(TInteger.get(), f.getType().getReturnType());

    System.out.println(f.getType().getName());
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testTermFunctionExternalTypeNotOK_0()
      throws TypeCheckerError
  {
    TypeCheckerTest.checkMustFailWithCodeInternal(
      new String[] { "typed/term-function-external-type-not-ok-0.p" },
      Code.TYPE_ERROR_FUNCTION_BODY_RETURN_INCOMPATIBLE);
  }

  @Test public void testTermFunctionTypeOK_0()
    throws TypeCheckerError
  {
    final TASTCompilation r =
      TestPipeline.typed(new String[] { "typed/term-function-type-ok-0.p" });

    final TASTDModule m = TestPipeline.getModule(r, "x.y", "M");
    final TASTDFunctionDefined f =
      (TASTDFunctionDefined) m.getTerms().get("f");

    Assert.assertEquals(TInteger.get(), f.getArguments().get(0).getType());
    Assert.assertEquals(TInteger.get(), f.getReturnType());
    Assert.assertEquals(TInteger.get(), f.getBody().getType());

    System.out.println(f.getType().getName());
  }

  @Test public void testTermFunctionTypeOK_1()
    throws TypeCheckerError
  {
    final TASTCompilation r =
      TestPipeline.typed(new String[] { "typed/term-function-type-ok-1.p" });

    final TASTDModule m = TestPipeline.getModule(r, "x.y", "M");
    final TASTDFunctionDefined f =
      (TASTDFunctionDefined) m.getTerms().get("f");

    Assert.assertEquals(TInteger.get(), f.getArguments().get(0).getType());
    Assert.assertEquals(TInteger.get(), f.getReturnType());
    Assert.assertEquals(TInteger.get(), f.getBody().getType());

    System.out.println(f.getType().getName());
  }

  @Test public void testTermFunctionTypeOK_2()
    throws TypeCheckerError
  {
    final TASTCompilation r =
      TestPipeline.typed(new String[] { "typed/term-function-type-ok-2.p" });

    final TASTDModule m = TestPipeline.getModule(r, "x.y", "M");
    final TASTDFunctionDefined f =
      (TASTDFunctionDefined) m.getTerms().get("f");

    Assert.assertEquals(TInteger.get(), f.getArguments().get(0).getType());
    Assert.assertEquals(TInteger.get(), f.getReturnType());
    Assert.assertEquals(TInteger.get(), f.getBody().getType());

    System.out.println(f.getType().getName());
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testTermValueBadAscription_0()
      throws TypeCheckerError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "typed/term-value-bad-ascription-0.p" },
      Code.TYPE_ERROR_VALUE_ASCRIPTION_MISMATCH);
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testTermValueTypeFunction_0()
      throws TypeCheckerError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "typed/term-value-type-function-0.p" },
      Code.TYPE_ERROR_VALUE_NON_VALUE_TYPE);
  }

  @Test public void testTermValueTypeOK_0()
    throws TypeCheckerError
  {
    final TASTCompilation r =
      TestPipeline.typed(new String[] { "typed/term-value-type-ok-0.p" });
    final TASTDModule m = TestPipeline.getModule(r, "x.y", "M");
    final TASTDValueDefined x = (TASTDValueDefined) m.getTerms().get("z");
    Assert.assertEquals(TInteger.get(), x.getType());
  }

  @Test public void testTermValueTypeOK_1()
    throws TypeCheckerError
  {
    final TASTCompilation r =
      TestPipeline.typed(new String[] { "typed/term-value-type-ok-1.p" });
    final TASTDModule m = TestPipeline.getModule(r, "x.y", "M");
    final TASTDValueDefined x = (TASTDValueDefined) m.getTerms().get("z");
    Assert.assertEquals(TInteger.get(), x.getType());
  }

  @Test public void testTrivialOK_0()
    throws TypeCheckerError
  {
    TestPipeline.typed(new String[] { "typed/trivial-0.p" });
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testTypeRecordNotManifest_0()
      throws TypeCheckerError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "typed/type-record-not-manifest-0.p" },
      Code.TYPE_ERROR_RECORD_FIELD_NOT_MANIFEST);
  }

  @Test public void testTypeRecordOK_0()
    throws TypeCheckerError
  {
    final TASTCompilation r =
      TestPipeline.typed(new String[] { "typed/type-record-0.p" });
    final TASTDModule m = TestPipeline.getModule(r, "x.y", "M");
    final TASTDTypeRecord t = (TASTDTypeRecord) m.getTypes().get("t");

    Assert.assertEquals("t", t.getName().getActual());
    final TRecord tr = t.getType();
    Assert.assertEquals("x.y.M", tr.getName().getFlat().getActual());
    Assert.assertEquals("t", tr.getName().getName().getActual());
    final List<TRecordField> tr_fields = tr.getFields();
    Assert.assertEquals(3, tr_fields.size());
    Assert.assertEquals("x", tr_fields.get(0).getName());
    Assert.assertEquals(TInteger.get(), tr_fields.get(0).getType());
    Assert.assertEquals("y", tr_fields.get(1).getName());
    Assert.assertEquals(TBoolean.get(), tr_fields.get(1).getType());
    Assert.assertEquals("z", tr_fields.get(2).getName());
    Assert.assertEquals(TVector4F.get(), tr_fields.get(2).getType());
  }

  @Test public void testTypeRecordOK_1()
    throws TypeCheckerError
  {
    final TASTCompilation r =
      TestPipeline.typed(new String[] { "typed/type-record-1.p" });
    final TASTDModule m = TestPipeline.getModule(r, "x.y", "M");
    final TASTDTypeRecord t = (TASTDTypeRecord) m.getTypes().get("t");
    final TASTDTypeRecord u = (TASTDTypeRecord) m.getTypes().get("u");

    Assert.assertEquals("u", u.getName().getActual());
    final TRecord ur = u.getType();
    Assert.assertEquals("u", ur.getName().getName().getActual());
    final List<TRecordField> ur_fields = ur.getFields();
    Assert.assertEquals(1, ur_fields.size());
    Assert.assertEquals("x", ur_fields.get(0).getName());
    Assert.assertEquals(TInteger.get(), ur_fields.get(0).getType());

    Assert.assertEquals("t", t.getName().getActual());
    final TRecord tr = t.getType();
    Assert.assertEquals("t", tr.getName().getName().getActual());
    final List<TRecordField> tr_fields = tr.getFields();
    Assert.assertEquals(1, tr_fields.size());
    Assert.assertEquals("x", tr_fields.get(0).getName());
    Assert.assertEquals(ur, tr_fields.get(0).getType());
  }

  @Test public void testTypeRecordOK_2()
    throws TypeCheckerError
  {
    final TASTCompilation r =
      TestPipeline.typed(new String[] { "typed/type-record-2.p" });

    final TASTDModule m = TestPipeline.getModule(r, "x.y", "M");
    final TASTDModule n = TestPipeline.getModule(r, "x.y", "N");

    final TASTDTypeRecord t = (TASTDTypeRecord) m.getTypes().get("t");
    final TASTDTypeRecord u = (TASTDTypeRecord) n.getTypes().get("u");

    Assert.assertEquals("u", u.getName().getActual());
    final TRecord ur = u.getType();
    Assert.assertEquals("u", ur.getName().getName().getActual());
    final List<TRecordField> ur_fields = ur.getFields();
    Assert.assertEquals(1, ur_fields.size());
    Assert.assertEquals("x", ur_fields.get(0).getName());
    Assert.assertEquals(TInteger.get(), ur_fields.get(0).getType());

    Assert.assertEquals("t", t.getName().getActual());
    final TRecord tr = t.getType();
    Assert.assertEquals("t", tr.getName().getName().getActual());
    final List<TRecordField> tr_fields = tr.getFields();
    Assert.assertEquals(1, tr_fields.size());
    Assert.assertEquals("x", tr_fields.get(0).getName());
    Assert.assertEquals(ur, tr_fields.get(0).getType());
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testVertexShaderAssignmentBadType_0()
      throws TypeCheckerError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "typed/vertex-shader-assignment-bad-type-0.p" },
      Code.TYPE_ERROR_SHADER_ASSIGNMENT_BAD_TYPE);
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testVertexShaderAssignmentBadType_1()
      throws TypeCheckerError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "typed/vertex-shader-assignment-bad-type-1.p" },
      Code.TYPE_ERROR_SHADER_ASSIGNMENT_BAD_TYPE);
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testVertexShaderAssignmentBadType_2()
      throws TypeCheckerError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "typed/vertex-shader-assignment-bad-type-2.p" },
      Code.TYPE_ERROR_SHADER_ASSIGNMENT_BAD_TYPE);
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testVertexShaderAssignmentBadType_3()
      throws TypeCheckerError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "typed/vertex-shader-assignment-bad-type-3.p" },
      Code.TYPE_ERROR_SHADER_ASSIGNMENT_BAD_TYPE);
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testVertexShaderAssignmentBadType_4()
      throws TypeCheckerError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "typed/vertex-shader-assignment-bad-type-4.p" },
      Code.TYPE_ERROR_SHADER_ASSIGNMENT_BAD_TYPE);
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testVertexShaderAssignmentBadType_5()
      throws TypeCheckerError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "typed/vertex-shader-assignment-bad-type-5.p" },
      Code.TYPE_ERROR_SHADER_ASSIGNMENT_BAD_TYPE);
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testVertexShaderBadAttributeType_0()
      throws TypeCheckerError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "typed/vertex-shader-bad-attribute-type-0.p" },
      Code.TYPE_ERROR_SHADER_BAD_ATTRIBUTE_TYPE);
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testVertexShaderBadAttributeType_1()
      throws TypeCheckerError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "typed/vertex-shader-bad-attribute-type-1.p" },
      Code.TYPE_ERROR_SHADER_BAD_ATTRIBUTE_TYPE);
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testFragmentShaderBadAttributeType_0()
      throws TypeCheckerError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "typed/fragment-shader-bad-attribute-type-0.p" },
      Code.TYPE_ERROR_SHADER_BAD_ATTRIBUTE_TYPE);
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testFragmentShaderBadAttributeType_1()
      throws TypeCheckerError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "typed/fragment-shader-bad-attribute-type-1.p" },
      Code.TYPE_ERROR_SHADER_BAD_ATTRIBUTE_TYPE);
  }

  @Test public void testVertexShaderAssignmentOK_0()
    throws TypeCheckerError
  {
    final TASTCompilation r =
      TestPipeline.typed(new String[] { "typed/vertex-shader-ok-0.p" });

    final TASTDModule m = TestPipeline.getModule(r, "x.y", "M");
    final TASTDShaderVertex v = (TASTDShaderVertex) m.getShaders().get("v");

    Assert.assertEquals("v", v.getName().getActual());
    Assert.assertEquals("in_0", v.getInputs().get(0).getName().getCurrent());
    Assert.assertEquals(TVector4F.get(), v.getInputs().get(0).getType());
    Assert.assertEquals("p_0", v
      .getParameters()
      .get(0)
      .getName()
      .getCurrent());
    Assert.assertEquals(TVector4F.get(), v.getParameters().get(0).getType());
    Assert.assertEquals("out_0", v.getOutputs().get(0).getName().getActual());
    Assert.assertEquals(TVector4F.get(), v.getOutputs().get(0).getType());
  }

  @Test(expected = TypeCheckerError.class) public
    void
    testVertexShaderOutputMainWrongType_0()
      throws TypeCheckerError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "typed/vertex-shader-output-main-bad-type-0.p" },
      Code.TYPE_ERROR_SHADER_OUTPUT_MAIN_BAD_TYPE);
  }
}
