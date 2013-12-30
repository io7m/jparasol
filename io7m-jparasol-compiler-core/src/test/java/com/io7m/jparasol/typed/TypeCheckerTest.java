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

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.junit.Assert;
import org.junit.Test;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jlog.Log;
import com.io7m.jparasol.ModulePath;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.PackagePath;
import com.io7m.jparasol.TestUtilities;
import com.io7m.jparasol.lexer.Lexer;
import com.io7m.jparasol.lexer.Position;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.lexer.Token.TokenIdentifierUpper;
import com.io7m.jparasol.parser.Parser;
import com.io7m.jparasol.parser.ParserTest;
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
import com.io7m.jparasol.typed.TypeCheckerError.Code;
import com.io7m.jparasol.typed.ast.TASTCompilation;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTEApplication;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTENew;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTERecord;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTESwizzle;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTRecordFieldAssignment;
import com.io7m.jparasol.typed.ast.TASTNameTermShaderFlat;
import com.io7m.jparasol.typed.ast.TASTNameTypeShaderFlat;
import com.io7m.jparasol.typed.ast.TASTNameTypeTermFlat;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDFunctionDefined;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDFunctionExternal;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDModule;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragment;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderVertex;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDTypeRecord;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDValue;
import com.io7m.jparasol.typed.ast.TASTReference;
import com.io7m.jparasol.typed.ast.TASTTermNameFlat;
import com.io7m.jparasol.untyped.ModuleStructure;
import com.io7m.jparasol.untyped.ModuleStructureError;
import com.io7m.jparasol.untyped.Resolver;
import com.io7m.jparasol.untyped.ResolverError;
import com.io7m.jparasol.untyped.UniqueBinders;
import com.io7m.jparasol.untyped.UniqueBindersError;
import com.io7m.jparasol.untyped.UnitCombinerError;
import com.io7m.jparasol.untyped.ast.checked.UASTCCompilation;
import com.io7m.jparasol.untyped.ast.initial.UASTICompilation;
import com.io7m.jparasol.untyped.ast.initial.UASTIUnit;
import com.io7m.jparasol.untyped.ast.resolved.UASTRCompilation;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUCompilation;

public final class TypeCheckerTest
{
  static TASTCompilation checked(
    final String[] names)
    throws ConstraintError,
      TypeCheckerError
  {
    TypeChecker tc;

    try {
      final Log log = TestUtilities.getLog();
      final List<UASTIUnit> units = TypeCheckerTest.parseUnits(names);
      final UASTICompilation initial = UASTICompilation.fromUnits(units);
      final ModuleStructure mc =
        ModuleStructure.newModuleStructureChecker(initial, log);
      final UASTCCompilation checked = mc.check();
      final UniqueBinders ub = UniqueBinders.newUniqueBinders(checked, log);
      final UASTUCompilation unique = ub.run();
      final Resolver nr = Resolver.newResolver(unique, log);
      final UASTRCompilation resolved = nr.run();
      tc = TypeChecker.newTypeChecker(resolved, log);
    } catch (final ConstraintError e) {
      e.printStackTrace();
      throw new UnreachableCodeException(e);
    } catch (final UniqueBindersError e) {
      e.printStackTrace();
      throw new UnreachableCodeException(e);
    } catch (final UnitCombinerError e) {
      e.printStackTrace();
      throw new UnreachableCodeException(e);
    } catch (final ModuleStructureError e) {
      e.printStackTrace();
      throw new UnreachableCodeException(e);
    } catch (final ResolverError e) {
      e.printStackTrace();
      throw new UnreachableCodeException(e);
    }

    return tc.check();
  }

  static TASTCompilation checkedInternal(
    final String[] names)
    throws ConstraintError,
      TypeCheckerError
  {
    TypeChecker tc;

    try {
      final Log log = TestUtilities.getLog();
      final List<UASTIUnit> units = TypeCheckerTest.parseUnitsInternal(names);
      final UASTICompilation initial = UASTICompilation.fromUnits(units);
      final ModuleStructure mc =
        ModuleStructure.newModuleStructureChecker(initial, log);
      final UASTCCompilation checked = mc.check();
      final UniqueBinders ub = UniqueBinders.newUniqueBinders(checked, log);
      final UASTUCompilation unique = ub.run();
      final Resolver nr = Resolver.newResolver(unique, log);
      final UASTRCompilation resolved = nr.run();
      tc = TypeChecker.newTypeChecker(resolved, log);
    } catch (final ConstraintError e) {
      throw new UnreachableCodeException(e);
    } catch (final UniqueBindersError e) {
      throw new UnreachableCodeException(e);
    } catch (final UnitCombinerError e) {
      throw new UnreachableCodeException(e);
    } catch (final ModuleStructureError e) {
      throw new UnreachableCodeException(e);
    } catch (final ResolverError e) {
      throw new UnreachableCodeException(e);
    }

    return tc.check();
  }

  static void checkMustFailWithCode(
    final @Nonnull String[] names,
    final @Nonnull TypeCheckerError.Code code)
    throws ConstraintError,
      TypeCheckerError
  {
    boolean caught = false;

    try {
      TypeCheckerTest.checked(names);
    } catch (final TypeCheckerError e) {
      caught = true;
      Assert.assertEquals(code, e.getCode());
      System.err.println(e);
      throw e;
    } catch (final ConstraintError e) {
      e.printStackTrace();
      throw e;
    }

    Assert.assertTrue("Caught exception", caught);
  }

  static void checkMustFailWithCodeInternal(
    final @Nonnull String[] names,
    final @Nonnull TypeCheckerError.Code code)
    throws ConstraintError,
      TypeCheckerError
  {
    boolean caught = false;

    try {
      TypeCheckerTest.checkedInternal(names);
    } catch (final TypeCheckerError e) {
      caught = true;
      Assert.assertEquals(code, e.getCode());
      throw e;
    } catch (final ConstraintError e) {
      e.printStackTrace();
      throw e;
    }

    Assert.assertTrue("Caught exception", caught);
  }

  private static TASTDModule firstModule(
    final TASTCompilation r)
  {
    return r.getModules().entrySet().iterator().next().getValue();
  }

  private static TASTDModule getModule(
    final @Nonnull TASTCompilation comp,
    final @Nonnull String pp,
    final @Nonnull String name)
    throws ConstraintError
  {
    final ModulePath path = TypeCheckerTest.getModuleMakePath(pp, name);
    final ModulePathFlat flat = ModulePathFlat.fromModulePath(path);
    return comp.getModules().get(flat);
  }

  private static @Nonnull ModulePath getModuleMakePath(
    final @Nonnull String pp,
    final @Nonnull String name)
    throws ConstraintError
  {
    final String[] segments = pp.split("\\.");
    final ArrayList<TokenIdentifierLower> tokens =
      new ArrayList<TokenIdentifierLower>();

    final File file = new File("<stdin>");
    final Position pos = new Position(0, 0);
    for (final String segment : segments) {
      tokens.add(new TokenIdentifierLower(file, pos, segment));
    }

    final TokenIdentifierUpper tname =
      new TokenIdentifierUpper(file, pos, name);
    return new ModulePath(new PackagePath(tokens), tname);
  }

  @SuppressWarnings("resource") private static UASTIUnit parseUnit(
    final String name,
    final boolean internal)
  {
    try {
      final InputStream is =
        ParserTest.class.getResourceAsStream("/com/io7m/jparasol/typed/"
          + name);
      final Lexer lexer = new Lexer(is);
      if (internal) {
        final Parser p = Parser.newInternalParser(lexer);
        return p.unit();
      }
      final Parser p = Parser.newParser(lexer);
      return p.unit();
    } catch (final Throwable x) {
      x.printStackTrace();
      System.err.println("UNREACHABLE: " + x);
      throw new UnreachableCodeException(x);
    }
  }

  public static List<UASTIUnit> parseUnits(
    final String[] names)
  {
    final List<UASTIUnit> units = new ArrayList<UASTIUnit>();

    for (final String name : names) {
      units.add(TypeCheckerTest.parseUnit(name, false));
    }

    return units;
  }

  public static List<UASTIUnit> parseUnitsInternal(
    final String[] names)
  {
    final List<UASTIUnit> units = new ArrayList<UASTIUnit>();

    for (final String name : names) {
      units.add(TypeCheckerTest.parseUnit(name, true));
    }

    return units;
  }

  @SuppressWarnings("static-method") @Test public void testAllOK_0()
    throws TypeCheckerError,
      ConstraintError
  {
    TypeCheckerTest.checkedInternal(new String[] { "all.p" });
  }

  @SuppressWarnings("static-method") @Test public void testBugOld_b77370072()
    throws TypeCheckerError,
      ConstraintError
  {
    TypeCheckerTest.checked(new String[] { "bug-old-b77370072.p" });
  }

  @SuppressWarnings("static-method") @Test(expected = TypeCheckerError.class) public
    void
    testFragmentShaderAssignmentBadType_0()
      throws TypeCheckerError,
        ConstraintError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "fragment-shader-assignment-bad-type-0.p" },
      Code.TYPE_ERROR_SHADER_ASSIGNMENT_BAD_TYPE);
  }

  @SuppressWarnings("static-method") @Test(expected = TypeCheckerError.class) public
    void
    testFragmentShaderAssignmentBadType_1()
      throws TypeCheckerError,
        ConstraintError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "fragment-shader-assignment-bad-type-1.p" },
      Code.TYPE_ERROR_SHADER_ASSIGNMENT_BAD_TYPE);
  }

  @SuppressWarnings("static-method") @Test(expected = TypeCheckerError.class) public
    void
    testFragmentShaderAssignmentBadType_2()
      throws TypeCheckerError,
        ConstraintError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "fragment-shader-assignment-bad-type-2.p" },
      Code.TYPE_ERROR_SHADER_ASSIGNMENT_BAD_TYPE);
  }

  @SuppressWarnings("static-method") @Test(expected = TypeCheckerError.class) public
    void
    testFragmentShaderAssignmentBadType_3()
      throws TypeCheckerError,
        ConstraintError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "fragment-shader-assignment-bad-type-3.p" },
      Code.TYPE_ERROR_SHADER_ASSIGNMENT_BAD_TYPE);
  }

  @SuppressWarnings("static-method") @Test(expected = TypeCheckerError.class) public
    void
    testFragmentShaderAssignmentBadType_4()
      throws TypeCheckerError,
        ConstraintError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "fragment-shader-assignment-bad-type-4.p" },
      Code.TYPE_ERROR_SHADER_ASSIGNMENT_BAD_TYPE);
  }

  @SuppressWarnings("static-method") @Test(expected = TypeCheckerError.class) public
    void
    testFragmentShaderAssignmentBadType_5()
      throws TypeCheckerError,
        ConstraintError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "fragment-shader-assignment-bad-type-5.p" },
      Code.TYPE_ERROR_SHADER_ASSIGNMENT_BAD_TYPE);
  }

  @SuppressWarnings("static-method") @Test(expected = TypeCheckerError.class) public
    void
    testFragmentShaderAssignmentBadType_6()
      throws TypeCheckerError,
        ConstraintError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "fragment-shader-assignment-bad-type-6.p" },
      Code.TYPE_ERROR_SHADER_ASSIGNMENT_BAD_TYPE);
  }

  @SuppressWarnings("static-method") @Test public
    void
    testFragmentShaderAssignmentOK_0()
      throws TypeCheckerError,
        ConstraintError
  {
    final TASTCompilation r =
      TypeCheckerTest.checked(new String[] { "fragment-shader-ok-0.p" });

    final TASTDModule m = TypeCheckerTest.getModule(r, "x.y", "M");
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

  @SuppressWarnings("static-method") @Test(expected = TypeCheckerError.class) public
    void
    testFragmentShaderDiscardNotBoolean_0()
      throws TypeCheckerError,
        ConstraintError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "fragment-shader-discard-not-boolean-0.p" },
      Code.TYPE_ERROR_SHADER_DISCARD_NOT_BOOLEAN);
  }

  @SuppressWarnings("static-method") @Test public void testGraphTermTerm_0()
    throws TypeCheckerError,
      ConstraintError
  {
    final TASTCompilation r =
      TypeCheckerTest.checked(new String[] { "graph-term-term-0.p" });

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
      final DirectedAcyclicGraph<TASTNameTypeTermFlat, TASTReference> g =
        r.getTermTypeGraph();

      for (final ModulePathFlat k : r.getModules().keySet()) {
        final TASTDModule m = r.getModules().get(k);
        for (final String tn : m.getTerms().keySet()) {
          final TASTTermNameFlat p = new TASTTermNameFlat(k, tn);
          final TASTNameTypeTermFlat.Term q =
            new TASTNameTypeTermFlat.Term(p);
          System.out.println("Check " + q);
          Assert.assertTrue(g.containsVertex(q));
        }
      }

      Assert.assertEquals(9, g.vertexSet().size());
    }

    {
      final DirectedAcyclicGraph<TASTNameTermShaderFlat, TASTReference> g =
        r.getShaderTermGraph();

      for (final ModulePathFlat k : r.getModules().keySet()) {
        final TASTDModule m = r.getModules().get(k);
        for (final String tn : m.getTerms().keySet()) {
          final TASTTermNameFlat p = new TASTTermNameFlat(k, tn);
          final TASTNameTermShaderFlat.Term q =
            new TASTNameTermShaderFlat.Term(p);
          System.out.println("Check " + q);
          Assert.assertTrue(g.containsVertex(q));
        }
      }

      Assert.assertEquals(9, g.vertexSet().size());
    }
  }

  @SuppressWarnings("static-method") @Test public void testGraphTypeType_0()
    throws TypeCheckerError,
      ConstraintError
  {
    final TASTCompilation r =
      TypeCheckerTest.checked(new String[] { "graph-type-type-0.p" });

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
      final DirectedAcyclicGraph<TASTNameTypeTermFlat, TASTReference> g =
        r.getTermTypeGraph();

      for (final ModulePathFlat k : r.getModules().keySet()) {
        final TASTDModule m = r.getModules().get(k);
        for (final String tn : m.getTypes().keySet()) {
          final TTypeNameFlat p = new TTypeNameFlat(k, tn);
          final TASTNameTypeTermFlat.Type q =
            new TASTNameTypeTermFlat.Type(p);
          System.out.println("Check " + q);
          Assert.assertTrue(g.containsVertex(q));
        }
      }

      Assert.assertEquals(9, g.vertexSet().size());
    }

    {
      final DirectedAcyclicGraph<TASTNameTypeShaderFlat, TASTReference> g =
        r.getShaderTypeGraph();

      for (final ModulePathFlat k : r.getModules().keySet()) {
        final TASTDModule m = r.getModules().get(k);
        for (final String tn : m.getTypes().keySet()) {
          final TTypeNameFlat p = new TTypeNameFlat(k, tn);
          final TASTNameTypeShaderFlat.Type q =
            new TASTNameTypeShaderFlat.Type(p);
          System.out.println("Check " + q);
          Assert.assertTrue(g.containsVertex(q));
        }
      }

      Assert.assertEquals(9, g.vertexSet().size());
    }
  }

  @SuppressWarnings("static-method") @Test(expected = TypeCheckerError.class) public
    void
    testProgramShaderWrongShaderType_0()
      throws TypeCheckerError,
        ConstraintError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "program-shader-wrong-shader-type-0.p" },
      Code.TYPE_ERROR_SHADER_WRONG_SHADER_TYPE);
  }

  @SuppressWarnings("static-method") @Test(expected = TypeCheckerError.class) public
    void
    testProgramShaderWrongShaderType_1()
      throws TypeCheckerError,
        ConstraintError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "program-shader-wrong-shader-type-1.p" },
      Code.TYPE_ERROR_SHADER_WRONG_SHADER_TYPE);
  }

  @SuppressWarnings("static-method") @Test(expected = TypeCheckerError.class) public
    void
    testTermExpressionApplicationBadCount_0()
      throws TypeCheckerError,
        ConstraintError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "term-expression-app-bad-count-0.p" },
      Code.TYPE_ERROR_EXPRESSION_APPLICATION_BAD_TYPES);
  }

  @SuppressWarnings("static-method") @Test(expected = TypeCheckerError.class) public
    void
    testTermExpressionApplicationBadTypes_0()
      throws TypeCheckerError,
        ConstraintError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "term-expression-app-bad-types-0.p" },
      Code.TYPE_ERROR_EXPRESSION_APPLICATION_BAD_TYPES);
  }

  @SuppressWarnings("static-method") @Test(expected = TypeCheckerError.class) public
    void
    testTermExpressionApplicationNotFunction_0()
      throws TypeCheckerError,
        ConstraintError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "term-expression-app-not-function-0.p" },
      Code.TYPE_ERROR_EXPRESSION_APPLICATION_NOT_FUNCTION_TYPE);
  }

  @SuppressWarnings("static-method") @Test public
    void
    testTermExpressionApplicationOK_0()
      throws TypeCheckerError,
        ConstraintError
  {
    final TASTCompilation r =
      TypeCheckerTest.checked(new String[] { "term-expression-app-ok-0.p" });

    final TASTDModule m = TypeCheckerTest.getModule(r, "x.y", "M");
    final TASTDValue z = (TASTDValue) m.getTerms().get("z");

    final TASTEApplication app = (TASTEApplication) z.getExpression();
    Assert.assertEquals("$x.y.M.f", app.getName().show());
    Assert.assertEquals(TInteger.get(), app.getType());
  }

  @SuppressWarnings("static-method") @Test(expected = TypeCheckerError.class) public
    void
    testTermExpressionConditionalNotBoolean_0()
      throws TypeCheckerError,
        ConstraintError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "term-expression-conditional-not-boolean-0.p" },
      Code.TYPE_ERROR_EXPRESSION_CONDITION_NOT_BOOLEAN);
  }

  @SuppressWarnings("static-method") @Test public
    void
    testTermExpressionConditionalOK_0()
      throws TypeCheckerError,
        ConstraintError
  {
    final TASTCompilation r =
      TypeCheckerTest
        .checked(new String[] { "term-expression-conditional-ok-0.p" });
    final TASTDModule m = TypeCheckerTest.getModule(r, "x.y", "M");

    final TASTDValue v = (TASTDValue) m.getTerms().get("z");
    Assert.assertEquals("z", v.getName().getActual());
    Assert.assertEquals(TInteger.get(), v.getExpression().getType());
  }

  @SuppressWarnings("static-method") @Test(expected = TypeCheckerError.class) public
    void
    testTermExpressionLetBadAscription_0()
      throws TypeCheckerError,
        ConstraintError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "term-expression-let-bad-ascription-0.p" },
      Code.TYPE_ERROR_VALUE_ASCRIPTION_MISMATCH);
  }

  @SuppressWarnings("static-method") @Test public
    void
    testTermExpressionLetOK_0()
      throws TypeCheckerError,
        ConstraintError
  {
    final TASTCompilation r =
      TypeCheckerTest.checked(new String[] { "term-expression-let-ok-0.p" });
    final TASTDModule m = TypeCheckerTest.getModule(r, "x.y", "M");

    final TASTDValue v = (TASTDValue) m.getTerms().get("z");
    Assert.assertEquals("z", v.getName().getActual());
    Assert.assertEquals(TVector3F.get(), v.getExpression().getType());
  }

  @SuppressWarnings("static-method") @Test(expected = TypeCheckerError.class) public
    void
    testTermExpressionNewNoConstructors_0()
      throws TypeCheckerError,
        ConstraintError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "term-expression-new-no-constructors-0.p" },
      Code.TYPE_ERROR_EXPRESSION_NEW_NO_APPROPRIATE_CONSTRUCTORS);
  }

  @SuppressWarnings("static-method") @Test(expected = TypeCheckerError.class) public
    void
    testTermExpressionNewNoConstructors_1()
      throws TypeCheckerError,
        ConstraintError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "term-expression-new-no-constructors-1.p" },
      Code.TYPE_ERROR_EXPRESSION_NEW_NO_APPROPRIATE_CONSTRUCTORS);
  }

  @SuppressWarnings("static-method") @Test(expected = TypeCheckerError.class) public
    void
    testTermExpressionNewNotConstructable_0()
      throws TypeCheckerError,
        ConstraintError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "term-expression-new-not-constructable-0.p" },
      Code.TYPE_ERROR_EXPRESSION_NEW_TYPE_NOT_CONSTRUCTABLE);
  }

  @SuppressWarnings("static-method") @Test public
    void
    testTermExpressionNewOK_0()
      throws TypeCheckerError,
        ConstraintError
  {
    final TASTCompilation r =
      TypeCheckerTest.checked(new String[] { "term-expression-new-ok-0.p" });
    final TASTDModule m = TypeCheckerTest.firstModule(r);

    for (int index = 0; index < TInteger.get().getConstructors().size(); ++index) {
      final TASTDValue v = (TASTDValue) m.getTerms().get("z" + index);
      Assert.assertEquals("z" + index, v.getName().getActual());
      final TASTENew e = (TASTENew) v.getExpression();
      Assert.assertEquals(TInteger.get(), e.getType());
    }

    for (int index = 0; index < TFloat.get().getConstructors().size(); ++index) {
      final TASTDValue v = (TASTDValue) m.getTerms().get("r" + index);
      Assert.assertEquals("r" + index, v.getName().getActual());
      final TASTENew e = (TASTENew) v.getExpression();
      Assert.assertEquals(TFloat.get(), e.getType());
    }

    for (int index = 0; index < TBoolean.get().getConstructors().size(); ++index) {
      final TASTDValue v = (TASTDValue) m.getTerms().get("b" + index);
      Assert.assertEquals("b" + index, v.getName().getActual());
      final TASTENew e = (TASTENew) v.getExpression();
      Assert.assertEquals(TBoolean.get(), e.getType());
    }

    for (int index = 0; index < TVector2F.get().getConstructors().size(); ++index) {
      final TASTDValue v = (TASTDValue) m.getTerms().get("v2f" + index);
      Assert.assertEquals("v2f" + index, v.getName().getActual());
      final TASTENew e = (TASTENew) v.getExpression();
      Assert.assertEquals(TVector2F.get(), e.getType());
    }

    for (int index = 0; index < TVector3F.get().getConstructors().size(); ++index) {
      final TASTDValue v = (TASTDValue) m.getTerms().get("v3f" + index);
      Assert.assertEquals("v3f" + index, v.getName().getActual());
      final TASTENew e = (TASTENew) v.getExpression();
      Assert.assertEquals(TVector3F.get(), e.getType());
    }

    for (int index = 0; index < TVector4F.get().getConstructors().size(); ++index) {
      final TASTDValue v = (TASTDValue) m.getTerms().get("v4f" + index);
      Assert.assertEquals("v4f" + index, v.getName().getActual());
      final TASTENew e = (TASTENew) v.getExpression();
      Assert.assertEquals(TVector4F.get(), e.getType());
    }
  }

  @SuppressWarnings("static-method") @Test(expected = TypeCheckerError.class) public
    void
    testTermExpressionRecordFieldBadType_0()
      throws TypeCheckerError,
        ConstraintError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "term-expression-record-field-bad-type-0.p" },
      Code.TYPE_ERROR_EXPRESSION_RECORD_FIELD_BAD_TYPE);
  }

  @SuppressWarnings("static-method") @Test(expected = TypeCheckerError.class) public
    void
    testTermExpressionRecordFieldNotAssigned_0()
      throws TypeCheckerError,
        ConstraintError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "term-expression-record-field-not-assigned-0.p" },
      Code.TYPE_ERROR_EXPRESSION_RECORD_FIELDS_UNASSIGNED);
  }

  @SuppressWarnings("static-method") @Test(expected = TypeCheckerError.class) public
    void
    testTermExpressionRecordNotRecordType_0()
      throws TypeCheckerError,
        ConstraintError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "term-expression-record-not-record-0.p" },
      Code.TYPE_ERROR_EXPRESSION_RECORD_NOT_RECORD_TYPE);
  }

  @SuppressWarnings("static-method") @Test public
    void
    testTermExpressionRecordOK_0()
      throws TypeCheckerError,
        ConstraintError
  {
    final TASTCompilation r =
      TypeCheckerTest
        .checked(new String[] { "term-expression-record-ok-0.p" });
    final TASTDModule m = TypeCheckerTest.getModule(r, "x.y", "M");
    final TASTDValue x = (TASTDValue) m.getTerms().get("x");
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

  @SuppressWarnings("static-method") @Test(expected = TypeCheckerError.class) public
    void
    testTermExpressionRecordProjectionNoSuchField_0()
      throws TypeCheckerError,
        ConstraintError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "term-expression-record-projection-no-such-field-0.p" },
      Code.TYPE_ERROR_EXPRESSION_RECORD_PROJECTION_NO_SUCH_FIELD);
  }

  @SuppressWarnings("static-method") @Test(expected = TypeCheckerError.class) public
    void
    testTermExpressionRecordProjectionNotRecord_0()
      throws TypeCheckerError,
        ConstraintError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "term-expression-record-projection-not-record-0.p" },
      Code.TYPE_ERROR_EXPRESSION_RECORD_PROJECTION_NOT_RECORD);
  }

  @SuppressWarnings("static-method") @Test public
    void
    testTermExpressionRecordProjectionOK_0()
      throws TypeCheckerError,
        ConstraintError
  {
    final TASTCompilation r =
      TypeCheckerTest
        .checked(new String[] { "term-expression-record-projection-ok-0.p" });
    final TASTDModule m = TypeCheckerTest.getModule(r, "x.y", "M");

    final TASTDValue v = (TASTDValue) m.getTerms().get("x");
    Assert.assertEquals("x", v.getName().getActual());
    Assert.assertEquals(TFloat.get(), v.getExpression().getType());
  }

  @SuppressWarnings("static-method") @Test(expected = TypeCheckerError.class) public
    void
    testTermExpressionRecordUnknownField_0()
      throws TypeCheckerError,
        ConstraintError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "term-expression-record-unknown-field-0.p" },
      Code.TYPE_ERROR_EXPRESSION_RECORD_FIELD_UNKNOWN);
  }

  @SuppressWarnings("static-method") @Test(expected = TypeCheckerError.class) public
    void
    testTermExpressionSwizzleNotVector_0()
      throws TypeCheckerError,
        ConstraintError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "term-expression-swizzle-not-vector-0.p" },
      Code.TYPE_ERROR_EXPRESSION_SWIZZLE_NOT_VECTOR);
  }

  @SuppressWarnings("static-method") @Test(expected = TypeCheckerError.class) public
    void
    testTermExpressionSwizzleTooMany_0()
      throws TypeCheckerError,
        ConstraintError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "term-expression-swizzle-too-many-0.p" },
      Code.TYPE_ERROR_EXPRESSION_SWIZZLE_TOO_MANY_COMPONENTS);
  }

  @SuppressWarnings("static-method") @Test public
    void
    testTermExpressionSwizzleTypesOK_0()
      throws TypeCheckerError,
        ConstraintError
  {
    final TASTCompilation r =
      TypeCheckerTest
        .checked(new String[] { "term-expression-swizzle-types-ok-0.p" });
    final TASTDModule m = TypeCheckerTest.getModule(r, "x.y", "M");

    for (int index = 1; index <= 4; ++index) {
      final TASTDValue v = (TASTDValue) m.getTerms().get("v2f" + index);
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
      final TASTDValue v = (TASTDValue) m.getTerms().get("v3f" + index);
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
      final TASTDValue v = (TASTDValue) m.getTerms().get("v4f" + index);
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

  @SuppressWarnings("static-method") @Test public
    void
    testTermExpressionSwizzleTypesOK_1()
      throws TypeCheckerError,
        ConstraintError
  {
    final TASTCompilation r =
      TypeCheckerTest
        .checked(new String[] { "term-expression-swizzle-types-ok-1.p" });
    final TASTDModule m = TypeCheckerTest.getModule(r, "x.y", "M");

    for (int index = 1; index <= 4; ++index) {
      final TASTDValue v = (TASTDValue) m.getTerms().get("v2i" + index);
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
      final TASTDValue v = (TASTDValue) m.getTerms().get("v3i" + index);
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
      final TASTDValue v = (TASTDValue) m.getTerms().get("v4i" + index);
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

  @SuppressWarnings("static-method") @Test(expected = TypeCheckerError.class) public
    void
    testTermExpressionSwizzleUnknownComponent_0()
      throws TypeCheckerError,
        ConstraintError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "term-expression-swizzle-unknown-component-0.p" },
      Code.TYPE_ERROR_EXPRESSION_SWIZZLE_UNKNOWN_COMPONENT);
  }

  @SuppressWarnings("static-method") @Test(expected = TypeCheckerError.class) public
    void
    testTermFunctionBadType_0()
      throws TypeCheckerError,
        ConstraintError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "term-function-bad-type-0.p" },
      Code.TYPE_ERROR_FUNCTION_BODY_RETURN_INCOMPATIBLE);
  }

  @SuppressWarnings("static-method") @Test public
    void
    testTermFunctionExternalTypeOK_0()
      throws TypeCheckerError,
        ConstraintError
  {
    final TASTCompilation r =
      TypeCheckerTest
        .checkedInternal(new String[] { "term-function-external-type-ok-0.p" });

    final TASTDModule m = TypeCheckerTest.firstModule(r);
    final TASTDFunctionExternal f =
      (TASTDFunctionExternal) m.getTerms().get("f");

    Assert.assertEquals(TInteger.get(), f.getArguments().get(0).getType());
    Assert.assertEquals(TInteger.get(), f.getType().getReturnType());

    System.out.println(f.getType().getName());
  }

  @SuppressWarnings("static-method") @Test public
    void
    testTermFunctionTypeOK_0()
      throws TypeCheckerError,
        ConstraintError
  {
    final TASTCompilation r =
      TypeCheckerTest.checked(new String[] { "term-function-type-ok-0.p" });

    final TASTDModule m = TypeCheckerTest.firstModule(r);
    final TASTDFunctionDefined f =
      (TASTDFunctionDefined) m.getTerms().get("f");

    Assert.assertEquals(TInteger.get(), f.getArguments().get(0).getType());
    Assert.assertEquals(TInteger.get(), f.getReturnType());
    Assert.assertEquals(TInteger.get(), f.getBody().getType());

    System.out.println(f.getType().getName());
  }

  @SuppressWarnings("static-method") @Test public
    void
    testTermFunctionTypeOK_1()
      throws TypeCheckerError,
        ConstraintError
  {
    final TASTCompilation r =
      TypeCheckerTest.checked(new String[] { "term-function-type-ok-1.p" });

    final TASTDModule m = TypeCheckerTest.firstModule(r);
    final TASTDFunctionDefined f =
      (TASTDFunctionDefined) m.getTerms().get("f");

    Assert.assertEquals(TInteger.get(), f.getArguments().get(0).getType());
    Assert.assertEquals(TInteger.get(), f.getReturnType());
    Assert.assertEquals(TInteger.get(), f.getBody().getType());

    System.out.println(f.getType().getName());
  }

  @SuppressWarnings("static-method") @Test public
    void
    testTermFunctionTypeOK_2()
      throws TypeCheckerError,
        ConstraintError
  {
    final TASTCompilation r =
      TypeCheckerTest.checked(new String[] { "term-function-type-ok-2.p" });

    final TASTDModule m = TypeCheckerTest.getModule(r, "x.y", "M");
    final TASTDModule n = TypeCheckerTest.getModule(r, "x.y", "N");

    final TASTDFunctionDefined f =
      (TASTDFunctionDefined) m.getTerms().get("f");

    Assert.assertEquals(TInteger.get(), f.getArguments().get(0).getType());
    Assert.assertEquals(TInteger.get(), f.getReturnType());
    Assert.assertEquals(TInteger.get(), f.getBody().getType());

    System.out.println(f.getType().getName());
  }

  @SuppressWarnings("static-method") @Test(expected = TypeCheckerError.class) public
    void
    testTermValueBadAscription_0()
      throws TypeCheckerError,
        ConstraintError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "term-value-bad-ascription-0.p" },
      Code.TYPE_ERROR_VALUE_ASCRIPTION_MISMATCH);
  }

  @SuppressWarnings("static-method") @Test(expected = TypeCheckerError.class) public
    void
    testTermValueTypeFunction_0()
      throws TypeCheckerError,
        ConstraintError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "term-value-type-function-0.p" },
      Code.TYPE_ERROR_VALUE_NON_VALUE_TYPE);
  }

  @SuppressWarnings("static-method") @Test public
    void
    testTermValueTypeOK_0()
      throws TypeCheckerError,
        ConstraintError
  {
    final TASTCompilation r =
      TypeCheckerTest.checked(new String[] { "term-value-type-ok-0.p" });
    final TASTDModule m = TypeCheckerTest.getModule(r, "x.y", "M");
    final TASTDValue x = (TASTDValue) m.getTerms().get("z");
    Assert.assertEquals(TInteger.get(), x.getType());
  }

  @SuppressWarnings("static-method") @Test public
    void
    testTermValueTypeOK_1()
      throws TypeCheckerError,
        ConstraintError
  {
    final TASTCompilation r =
      TypeCheckerTest.checked(new String[] { "term-value-type-ok-1.p" });
    final TASTDModule m = TypeCheckerTest.getModule(r, "x.y", "M");
    final TASTDValue x = (TASTDValue) m.getTerms().get("z");
    Assert.assertEquals(TInteger.get(), x.getType());
  }

  @SuppressWarnings("static-method") @Test public void testTrivialOK_0()
    throws TypeCheckerError,
      ConstraintError
  {
    TypeCheckerTest.checked(new String[] { "trivial-0.p" });
  }

  @SuppressWarnings("static-method") @Test(expected = TypeCheckerError.class) public
    void
    testTypeRecordNotManifest_0()
      throws TypeCheckerError,
        ConstraintError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "type-record-not-manifest-0.p" },
      Code.TYPE_ERROR_RECORD_FIELD_NOT_MANIFEST);
  }

  @SuppressWarnings("static-method") @Test public void testTypeRecordOK_0()
    throws TypeCheckerError,
      ConstraintError
  {
    final TASTCompilation r =
      TypeCheckerTest.checked(new String[] { "type-record-0.p" });
    final TASTDModule m = TypeCheckerTest.firstModule(r);
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

  @SuppressWarnings("static-method") @Test public void testTypeRecordOK_1()
    throws TypeCheckerError,
      ConstraintError
  {
    final TASTCompilation r =
      TypeCheckerTest.checked(new String[] { "type-record-1.p" });
    final TASTDModule m = TypeCheckerTest.firstModule(r);
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

  @SuppressWarnings("static-method") @Test public void testTypeRecordOK_2()
    throws TypeCheckerError,
      ConstraintError
  {
    final TASTCompilation r =
      TypeCheckerTest.checked(new String[] { "type-record-2.p" });

    final TASTDModule m = TypeCheckerTest.getModule(r, "x.y", "M");
    final TASTDModule n = TypeCheckerTest.getModule(r, "x.y", "N");

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

  @SuppressWarnings("static-method") @Test(expected = TypeCheckerError.class) public
    void
    testVertexShaderAssignmentBadType_0()
      throws TypeCheckerError,
        ConstraintError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "vertex-shader-assignment-bad-type-0.p" },
      Code.TYPE_ERROR_SHADER_ASSIGNMENT_BAD_TYPE);
  }

  @SuppressWarnings("static-method") @Test(expected = TypeCheckerError.class) public
    void
    testVertexShaderAssignmentBadType_1()
      throws TypeCheckerError,
        ConstraintError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "vertex-shader-assignment-bad-type-1.p" },
      Code.TYPE_ERROR_SHADER_ASSIGNMENT_BAD_TYPE);
  }

  @SuppressWarnings("static-method") @Test(expected = TypeCheckerError.class) public
    void
    testVertexShaderAssignmentBadType_2()
      throws TypeCheckerError,
        ConstraintError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "vertex-shader-assignment-bad-type-2.p" },
      Code.TYPE_ERROR_SHADER_ASSIGNMENT_BAD_TYPE);
  }

  @SuppressWarnings("static-method") @Test(expected = TypeCheckerError.class) public
    void
    testVertexShaderAssignmentBadType_3()
      throws TypeCheckerError,
        ConstraintError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "vertex-shader-assignment-bad-type-3.p" },
      Code.TYPE_ERROR_SHADER_ASSIGNMENT_BAD_TYPE);
  }

  @SuppressWarnings("static-method") @Test(expected = TypeCheckerError.class) public
    void
    testVertexShaderAssignmentBadType_4()
      throws TypeCheckerError,
        ConstraintError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "vertex-shader-assignment-bad-type-4.p" },
      Code.TYPE_ERROR_SHADER_ASSIGNMENT_BAD_TYPE);
  }

  @SuppressWarnings("static-method") @Test(expected = TypeCheckerError.class) public
    void
    testVertexShaderAssignmentBadType_5()
      throws TypeCheckerError,
        ConstraintError
  {
    TypeCheckerTest.checkMustFailWithCode(
      new String[] { "vertex-shader-assignment-bad-type-5.p" },
      Code.TYPE_ERROR_SHADER_ASSIGNMENT_BAD_TYPE);
  }

  @SuppressWarnings("static-method") @Test public
    void
    testVertexShaderAssignmentOK_0()
      throws TypeCheckerError,
        ConstraintError
  {
    final TASTCompilation r =
      TypeCheckerTest.checked(new String[] { "vertex-shader-ok-0.p" });

    final TASTDModule m = TypeCheckerTest.getModule(r, "x.y", "M");
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
}
