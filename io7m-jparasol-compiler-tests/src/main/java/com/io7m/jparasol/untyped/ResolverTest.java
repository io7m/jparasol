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

package com.io7m.jparasol.untyped;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jaux.functional.Option.Some;
import com.io7m.jparasol.ModulePath;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.PackagePath;
import com.io7m.jparasol.TestUtilities;
import com.io7m.jparasol.lexer.Position;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.lexer.Token.TokenIdentifierUpper;
import com.io7m.jparasol.untyped.ast.checked.UASTCCompilation;
import com.io7m.jparasol.untyped.ast.resolved.UASTRCompilation;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDModule;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDShaderFragment;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDShaderFragmentLocal;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDShaderFragmentLocalDiscard;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDShaderFragmentLocalValue;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDShaderProgram;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDShaderVertex;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDTypeRecord;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDValue;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDValueLocal;
import com.io7m.jparasol.untyped.ast.resolved.UASTRExpression.UASTREApplication;
import com.io7m.jparasol.untyped.ast.resolved.UASTRExpression.UASTREInteger;
import com.io7m.jparasol.untyped.ast.resolved.UASTRExpression.UASTRELet;
import com.io7m.jparasol.untyped.ast.resolved.UASTRExpression.UASTRENew;
import com.io7m.jparasol.untyped.ast.resolved.UASTRExpression.UASTRERecord;
import com.io7m.jparasol.untyped.ast.resolved.UASTRExpression.UASTREVariable;
import com.io7m.jparasol.untyped.ast.resolved.UASTRExpression.UASTRRecordFieldAssignment;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTermName.UASTRTermNameGlobal;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTypeName;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTypeName.UASTRTypeNameBuiltIn;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTypeName.UASTRTypeNameGlobal;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUCompilation;

public final class ResolverTest
{
  static UASTCCompilation checked(
    final String[] names)
  {
    try {
      return ModuleStructureTest.check(names);
    } catch (final ModuleStructureError e) {
      throw new UnreachableCodeException(e);
    } catch (final ConstraintError e) {
      throw new UnreachableCodeException(e);
    }
  }

  static UASTCCompilation checkedInternal(
    final String[] names)
  {
    try {
      return ModuleStructureTest.checkInternal(names);
    } catch (final ModuleStructureError e) {
      throw new UnreachableCodeException(e);
    } catch (final ConstraintError e) {
      throw new UnreachableCodeException(e);
    }
  }

  static void checkMustFailWithCode(
    final @Nonnull String[] names,
    final @Nonnull ResolverError.Code code)
    throws ConstraintError,
      ResolverError
  {
    boolean caught = false;

    try {
      ResolverTest.resolved(names);
    } catch (final ResolverError e) {
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
    final @Nonnull ResolverError.Code code)
    throws ConstraintError,
      ResolverError
  {
    boolean caught = false;

    try {
      ResolverTest.resolvedInternal(names);
    } catch (final ResolverError e) {
      caught = true;
      Assert.assertEquals(code, e.getCode());
      throw e;
    } catch (final ConstraintError e) {
      e.printStackTrace();
      throw e;
    }

    Assert.assertTrue("Caught exception", caught);
  }

  private static UASTRDModule firstModule(
    final UASTRCompilation r)
  {
    return r.getModules().entrySet().iterator().next().getValue();
  }

  private static UASTRDModule getModule(
    final @Nonnull UASTRCompilation comp,
    final @Nonnull String pp,
    final @Nonnull String name)
    throws ConstraintError
  {
    final ModulePath path = ResolverTest.getModuleMakePath(pp, name);
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

  static UASTRCompilation resolved(
    final String[] names)
    throws ResolverError,
      ConstraintError
  {
    Resolver r;
    try {
      r =
        Resolver.newResolver(
          ResolverTest.unique(names),
          TestUtilities.getLog());
    } catch (final ConstraintError e) {
      throw new UnreachableCodeException(e);
    } catch (final UniqueBindersError e) {
      throw new UnreachableCodeException(e);
    }

    return r.run();
  }

  static UASTRCompilation resolvedInternal(
    final String[] names)
    throws ResolverError,
      ConstraintError
  {
    Resolver r;
    try {
      r =
        Resolver.newResolver(
          ResolverTest.uniqueInternal(names),
          TestUtilities.getLog());
    } catch (final ConstraintError e) {
      throw new UnreachableCodeException(e);
    } catch (final UniqueBindersError e) {
      throw new UnreachableCodeException(e);
    }

    return r.run();
  }

  static UASTUCompilation unique(
    final String[] names)
    throws UniqueBindersError,
      ConstraintError
  {
    UniqueBinders u;
    try {
      u =
        UniqueBinders.newUniqueBinders(
          UniqueBindersTest.checked(names),
          TestUtilities.getLog());
    } catch (final ConstraintError e) {
      throw new UnreachableCodeException(e);
    }

    return u.run();
  }

  static UASTUCompilation uniqueInternal(
    final String[] names)
    throws UniqueBindersError,
      ConstraintError
  {
    UniqueBinders u;
    try {
      u =
        UniqueBinders.newUniqueBinders(
          UniqueBindersTest.checkedInternal(names),
          TestUtilities.getLog());
    } catch (final ConstraintError e) {
      throw new UnreachableCodeException(e);
    }

    return u.run();
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testExpressionApplicationNonexistent0()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest.checkMustFailWithCode(
      new String[] { "resolver/expression-app-term-nonexistent-0.p" },
      ResolverError.Code.RESOLVER_TERM_NONEXISTENT);
  }

  @SuppressWarnings("static-method") @Test public
    void
    testExpressionApplicationOK0()
      throws ResolverError,
        ConstraintError
  {
    final UASTRCompilation r =
      ResolverTest
        .resolved(new String[] { "resolver/expression-app-term-ok-0.p" });

    final UASTRDModule m = ResolverTest.firstModule(r);

    final UASTRDValue t = (UASTRDValue) m.getTerms().get("x");
    Assert.assertEquals("x", t.getName().getActual());
    final UASTREApplication app = (UASTREApplication) t.getExpression();
    final UASTRTermNameGlobal name = (UASTRTermNameGlobal) app.getName();
    Assert.assertEquals("f", name.getName().getActual());
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testExpressionApplicationRecursive0()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest.checkMustFailWithCode(
      new String[] { "resolver/expression-app-term-recursive-0.p" },
      ResolverError.Code.RESOLVER_TERM_RECURSIVE_MUTUAL);
  }

  @SuppressWarnings("static-method") @Test public
    void
    testExpressionLetTypeOK0()
      throws ResolverError,
        ConstraintError
  {
    final UASTRCompilation r =
      ResolverTest
        .resolved(new String[] { "resolver/expression-let-type-ok-0.p" });

    final UASTRDModule m = ResolverTest.firstModule(r);

    final UASTRDValue t = (UASTRDValue) m.getTerms().get("x");
    Assert.assertEquals("x", t.getName().getActual());
    final UASTRELet elet = (UASTRELet) t.getExpression();

    {
      final UASTRDValueLocal b0 = elet.getBindings().get(0);
      final UASTRTypeNameBuiltIn b0_type =
        (UASTRTypeNameBuiltIn) ((Some<UASTRTypeName>) b0.getAscription()).value;
      Assert.assertEquals("integer", b0_type.getName().getActual());
    }
  }

  @SuppressWarnings("static-method") @Test public void testExpressionNewOK0()
    throws ResolverError,
      ConstraintError
  {
    final UASTRCompilation r =
      ResolverTest
        .resolved(new String[] { "resolver/expression-new-term-ok-0.p" });

    final UASTRDModule m = ResolverTest.firstModule(r);

    final UASTRDValue t = (UASTRDValue) m.getTerms().get("x");
    Assert.assertEquals("x", t.getName().getActual());
    final UASTRENew enew = (UASTRENew) t.getExpression();
    final UASTRTypeNameBuiltIn name = (UASTRTypeNameBuiltIn) enew.getName();
    Assert.assertEquals("integer", name.getName().getActual());
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testExpressionNewTermNonexistent0()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest.checkMustFailWithCode(
      new String[] { "resolver/expression-new-term-nonexistent-0.p" },
      ResolverError.Code.RESOLVER_TERM_NONEXISTENT);
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testExpressionNewTermRecursive0()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest.checkMustFailWithCode(
      new String[] { "resolver/expression-new-term-recursive-0.p" },
      ResolverError.Code.RESOLVER_TERM_RECURSIVE_MUTUAL);
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testExpressionNewTypeNonexistent0()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest.checkMustFailWithCode(
      new String[] { "resolver/expression-new-type-nonexistent-0.p" },
      ResolverError.Code.RESOLVER_TYPE_NONEXISTENT);
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testExpressionRecordTermNonexistent0()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest.checkMustFailWithCode(
      new String[] { "resolver/expression-record-term-nonexistent-0.p" },
      ResolverError.Code.RESOLVER_TERM_NONEXISTENT);
  }

  @SuppressWarnings("static-method") @Test public
    void
    testExpressionRecordTermOK0()
      throws ResolverError,
        ConstraintError
  {
    final UASTRCompilation r =
      ResolverTest
        .resolved(new String[] { "resolver/expression-record-term-ok-0.p" });

    final UASTRDModule m = ResolverTest.firstModule(r);

    final UASTRDValue x = (UASTRDValue) m.getTerms().get("x");
    final UASTRERecord body = (UASTRERecord) x.getExpression();
    final UASTRTypeNameGlobal type = (UASTRTypeNameGlobal) body.getTypePath();

    Assert.assertEquals("t", type.getName().getActual());
    final UASTRRecordFieldAssignment ass0 = body.getAssignments().get(0);
    Assert.assertEquals("z", ass0.getName().getActual());
    final UASTREInteger val = (UASTREInteger) ass0.getExpression();
    Assert.assertEquals(23, val.getValue().intValue());
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testExpressionRecordTypeNonexistent0()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest.checkMustFailWithCode(
      new String[] { "resolver/expression-record-type-nonexistent-0.p" },
      ResolverError.Code.RESOLVER_TYPE_NONEXISTENT);
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testFragmentShaderDiscardTermExists0()
      throws ResolverError,
        ConstraintError
  {
    final UASTRCompilation r =
      ResolverTest
        .resolved(new String[] { "resolver/fragment-shader-discard-term-exists-0.p" });

    final UASTRDModule m = ResolverTest.firstModule(r);

    final UASTRDShaderFragment f =
      (UASTRDShaderFragment) m.getShaders().get("f");
    final List<UASTRDShaderFragmentLocal> locals = f.getLocals();

    final UASTRDShaderFragmentLocalValue v =
      (UASTRDShaderFragmentLocalValue) locals.get(0);
    final UASTRDShaderFragmentLocalDiscard d =
      (UASTRDShaderFragmentLocalDiscard) locals.get(1);

    final UASTREVariable var = (UASTREVariable) d.getExpression();
    Assert.assertEquals("earlier", v.getValue().getName().getCurrent());

    final UASTRTermNameGlobal var_name = (UASTRTermNameGlobal) var.getName();
    Assert.assertEquals("earlier", var_name.getName().getActual());
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testFragmentShaderDiscardTermNonexistent0()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest
      .checkMustFailWithCode(
        new String[] { "resolver/fragment-shader-discard-term-nonexistent-0.p" },
        ResolverError.Code.RESOLVER_TERM_NONEXISTENT);
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testFragmentShaderDiscardTermNonexistent1()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest
      .checkMustFailWithCode(
        new String[] { "resolver/fragment-shader-discard-term-nonexistent-1.p" },
        ResolverError.Code.RESOLVER_TERM_NONEXISTENT);
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testFragmentShaderLocalTermNonexistent0()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest.checkMustFailWithCode(
      new String[] { "resolver/fragment-shader-local-term-nonexistent-0.p" },
      ResolverError.Code.RESOLVER_TERM_NONEXISTENT);
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testFragmentShaderLocalTermNotRecursive0()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest
      .checkMustFailWithCode(
        new String[] { "resolver/fragment-shader-local-term-not-recursive-0.p" },
        ResolverError.Code.RESOLVER_TERM_NONEXISTENT);
  }

  @SuppressWarnings("static-method") @Test public
    void
    testFragmentShaderOK0()
      throws ResolverError,
        ConstraintError
  {
    final UASTRCompilation r =
      ResolverTest
        .resolved(new String[] { "resolver/fragment-shader-ok-0.p" });

    final UASTRDModule m = ResolverTest.firstModule(r);
    final UASTRDShaderFragment fs =
      (UASTRDShaderFragment) m.getShaders().get("f");

    Assert.assertEquals(1, fs.getInputs().size());
    Assert.assertEquals(1, fs.getParameters().size());
    Assert.assertEquals(1, fs.getOutputs().size());

    Assert.assertEquals("in_0", fs.getInputs().get(0).getName().getCurrent());
    Assert.assertEquals("p_0", fs
      .getParameters()
      .get(0)
      .getName()
      .getCurrent());
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testFragmentShaderTypeNonexistentInput0()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest.checkMustFailWithCode(
      new String[] { "resolver/fragment-shader-type-nonexistent-input-0.p" },
      ResolverError.Code.RESOLVER_TYPE_NONEXISTENT);
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testFragmentShaderTypeNonexistentParameter0()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest
      .checkMustFailWithCode(
        new String[] { "resolver/fragment-shader-type-nonexistent-parameter-0.p" },
        ResolverError.Code.RESOLVER_TYPE_NONEXISTENT);
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testImportCyclic0()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest.checkMustFailWithCode(
      new String[] { "resolver/import-cyclic-0.p" },
      ResolverError.Code.RESOLVER_IMPORT_CYCLIC);
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testImportCyclic1()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest.checkMustFailWithCode(
      new String[] { "resolver/import-cyclic-1.p" },
      ResolverError.Code.RESOLVER_IMPORT_CYCLIC);
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testImportUnknown0()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest.checkMustFailWithCode(
      new String[] { "resolver/import-unknown-0.p" },
      ResolverError.Code.RESOLVER_IMPORT_UNKNOWN);
  }

  @SuppressWarnings("static-method") @Test public
    void
    testModuleTermTopology0()
      throws ResolverError,
        ConstraintError
  {
    final UASTRCompilation r =
      ResolverTest
        .resolved(new String[] { "resolver/module-term-topology-0.p" });

    final UASTRDModule m = ResolverTest.firstModule(r);

    final List<String> topo = m.getTermTopology();
    Assert.assertEquals(3, topo.size());

    System.out.println(topo);

    Assert.assertEquals("z", topo.get(0));
    Assert.assertEquals("y", topo.get(1));
    Assert.assertEquals("x", topo.get(2));
  }

  @SuppressWarnings("static-method") @Test public void testModuleTopology0()
    throws ResolverError,
      ConstraintError
  {
    final UASTRCompilation r =
      ResolverTest.resolved(new String[] { "resolver/module-topology-0.p" });

    final List<ModulePathFlat> topo = r.getModuleTopology();
    Assert.assertEquals(3, topo.size());

    System.out.println(topo);

    Assert.assertEquals("x.y.P", topo.get(0).getActual());
    Assert.assertEquals("x.y.N", topo.get(1).getActual());
    Assert.assertEquals("x.y.M", topo.get(2).getActual());
  }

  @SuppressWarnings("static-method") @Test public void testModuleTopology1()
    throws ResolverError,
      ConstraintError
  {
    final UASTRCompilation r =
      ResolverTest.resolved(new String[] { "resolver/module-topology-1.p" });

    final List<ModulePathFlat> topo = r.getModuleTopology();
    Assert.assertEquals(4, topo.size());

    System.out.println(topo);

    Assert.assertEquals("x.y.Q", topo.get(0).getActual());
    Assert.assertEquals("x.y.N", topo.get(1).getActual());
    Assert.assertEquals("x.y.P", topo.get(2).getActual());
    Assert.assertEquals("x.y.M", topo.get(3).getActual());
  }

  @SuppressWarnings("static-method") @Test public
    void
    testModuleTypeTopology0()
      throws ResolverError,
        ConstraintError
  {
    final UASTRCompilation r =
      ResolverTest
        .resolved(new String[] { "resolver/module-type-topology-0.p" });

    final UASTRDModule m = ResolverTest.firstModule(r);

    final List<String> topo = m.getTypeTopology();
    Assert.assertEquals(3, topo.size());

    System.out.println(topo);

    Assert.assertEquals("z", topo.get(0));
    Assert.assertEquals("y", topo.get(1));
    Assert.assertEquals("x", topo.get(2));
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testProgramShaderNonexistent0()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest.checkMustFailWithCode(
      new String[] { "resolver/program-shader-nonexistent-0.p" },
      ResolverError.Code.RESOLVER_SHADER_NONEXISTENT);
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testProgramShaderNonexistent1()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest.checkMustFailWithCode(
      new String[] { "resolver/program-shader-nonexistent-1.p" },
      ResolverError.Code.RESOLVER_SHADER_NONEXISTENT);
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testProgramShaderNonexistent2()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest.checkMustFailWithCode(
      new String[] { "resolver/program-shader-nonexistent-2.p" },
      ResolverError.Code.RESOLVER_MODULE_REFERENCE_UNKNOWN);
  }

  @SuppressWarnings("static-method") @Test public void testProgramShaderOk0()
    throws ResolverError,
      ConstraintError
  {
    final UASTRCompilation r =
      ResolverTest
        .resolved(new String[] { "resolver/program-shader-ok-0.p" });

    final UASTRDModule m = ResolverTest.firstModule(r);
    final UASTRDShaderProgram p =
      (UASTRDShaderProgram) m.getShaders().get("p");

    Assert.assertEquals("v", p.getVertexShader().getName().getActual());
    Assert.assertEquals("f", p.getFragmentShader().getName().getActual());
  }

  @SuppressWarnings("static-method") @Test public void testProgramShaderOk1()
    throws ResolverError,
      ConstraintError
  {
    final UASTRCompilation r =
      ResolverTest
        .resolved(new String[] { "resolver/program-shader-ok-1.p" });

    final UASTRDModule m = ResolverTest.getModule(r, "x.y", "M");
    final UASTRDShaderProgram p =
      (UASTRDShaderProgram) m.getShaders().get("p");

    Assert.assertEquals("x.y.N", p.getVertexShader().getFlat().getActual());
    Assert.assertEquals("v", p.getVertexShader().getName().getActual());
    Assert.assertEquals("x.y.N", p.getFragmentShader().getFlat().getActual());
    Assert.assertEquals("f", p.getFragmentShader().getName().getActual());
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testProgramShaderRecursive0()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest.checkMustFailWithCode(
      new String[] { "resolver/program-shader-recursive-0.p" },
      ResolverError.Code.RESOLVER_SHADER_RECURSIVE_LOCAL);
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testProgramShaderRecursive1()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest.checkMustFailWithCode(
      new String[] { "resolver/program-shader-recursive-1.p" },
      ResolverError.Code.RESOLVER_SHADER_RECURSIVE_MUTUAL);
  }

  @SuppressWarnings("static-method") @Test public
    void
    testTypeRecordBuiltIn0()
      throws ResolverError,
        ConstraintError
  {
    final UASTRCompilation r =
      ResolverTest
        .resolved(new String[] { "resolver/type-record-built-in-0.p" });

    final UASTRDModule m = ResolverTest.firstModule(r);

    final UASTRDTypeRecord t = (UASTRDTypeRecord) m.getTypes().get("t");
    Assert.assertEquals("x", t.getFields().get(0).getName().getActual());
    final UASTRTypeNameBuiltIn tname =
      (UASTRTypeNameBuiltIn) t.getFields().get(0).getType();
    Assert.assertEquals("integer", tname.getName().getActual());
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testTypeRecordNonexistent0()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest.checkMustFailWithCode(
      new String[] { "resolver/type-record-nonexistent-0.p" },
      ResolverError.Code.RESOLVER_TYPE_NONEXISTENT);
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testTypeRecordRecursive0()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest.checkMustFailWithCode(
      new String[] { "resolver/type-record-recursive-0.p" },
      ResolverError.Code.RESOLVER_TYPE_RECURSIVE_LOCAL);
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testTypeRecordRecursive1()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest.checkMustFailWithCode(
      new String[] { "resolver/type-record-recursive-1.p" },
      ResolverError.Code.RESOLVER_TYPE_RECURSIVE_MUTUAL);
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testValueNonexistent0()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest.checkMustFailWithCode(
      new String[] { "resolver/value-nonexistent-0.p" },
      ResolverError.Code.RESOLVER_TERM_NONEXISTENT);
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testValueNonexistent1()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest.checkMustFailWithCode(
      new String[] { "resolver/value-nonexistent-1.p" },
      ResolverError.Code.RESOLVER_TERM_NONEXISTENT);
  }

  @SuppressWarnings("static-method") @Test public
    void
    testValueNotRecursive0()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest
      .resolved(new String[] { "resolver/value-not-recursive-0.p" });
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testValueRecursiveLocal0()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest.checkMustFailWithCode(
      new String[] { "resolver/value-recursive-local-0.p" },
      ResolverError.Code.RESOLVER_TERM_RECURSIVE_LOCAL);
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testValueRecursiveLocal1()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest.checkMustFailWithCode(
      new String[] { "resolver/value-recursive-local-1.p" },
      ResolverError.Code.RESOLVER_TERM_RECURSIVE_LOCAL);
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testValueRecursiveLocal2()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest.checkMustFailWithCode(
      new String[] { "resolver/value-recursive-local-2.p" },
      ResolverError.Code.RESOLVER_TERM_RECURSIVE_LOCAL);
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testValueRecursiveLocal3()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest.checkMustFailWithCode(
      new String[] { "resolver/value-recursive-local-3.p" },
      ResolverError.Code.RESOLVER_TERM_RECURSIVE_LOCAL);
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testValueRecursiveMutual0()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest.checkMustFailWithCode(
      new String[] { "resolver/value-recursive-mutual-0.p" },
      ResolverError.Code.RESOLVER_TERM_RECURSIVE_MUTUAL);
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testValueRecursiveMutual1()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest.checkMustFailWithCode(
      new String[] { "resolver/value-recursive-mutual-1.p" },
      ResolverError.Code.RESOLVER_TERM_RECURSIVE_MUTUAL);
  }

  @SuppressWarnings("static-method") @Test public void testValueRenameOK0()
    throws ResolverError,
      ConstraintError
  {
    final UASTRCompilation r =
      ResolverTest.resolved(new String[] { "resolver/value-rename-ok-0.p" });

    final UASTRDModule m = ResolverTest.getModule(r, "x.y", "M");
    final UASTRDValue v = (UASTRDValue) m.getTerms().get("x");
    final UASTREVariable vvar = (UASTREVariable) v.getExpression();
    final UASTRTermNameGlobal vvn = (UASTRTermNameGlobal) vvar.getName();

    Assert.assertEquals("x.y.N", vvn.getFlat().getActual());
    Assert.assertEquals("x", vvn.getName().getActual());
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testValueTypeNonexistent0()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest.checkMustFailWithCode(
      new String[] { "resolver/value-type-nonexistent-0.p" },
      ResolverError.Code.RESOLVER_TYPE_NONEXISTENT);
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testValueTypeNonexistent1()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest.checkMustFailWithCode(
      new String[] { "resolver/value-type-nonexistent-1.p" },
      ResolverError.Code.RESOLVER_TYPE_NONEXISTENT);
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testValueTypeNonexistent2()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest.checkMustFailWithCode(
      new String[] { "resolver/value-type-nonexistent-2.p" },
      ResolverError.Code.RESOLVER_TYPE_NONEXISTENT);
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testValueTypeNonexistent3()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest.checkMustFailWithCode(
      new String[] { "resolver/value-type-nonexistent-3.p" },
      ResolverError.Code.RESOLVER_TYPE_NONEXISTENT);
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testValueTypeNonexistent4()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest.checkMustFailWithCode(
      new String[] { "resolver/value-type-nonexistent-4.p" },
      ResolverError.Code.RESOLVER_TYPE_NONEXISTENT);
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testValueTypeNonexistent5()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest.checkMustFailWithCode(
      new String[] { "resolver/value-type-nonexistent-5.p" },
      ResolverError.Code.RESOLVER_TYPE_NONEXISTENT);
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testValueTypeNonexistent6()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest.checkMustFailWithCode(
      new String[] { "resolver/value-type-nonexistent-6.p" },
      ResolverError.Code.RESOLVER_TYPE_NONEXISTENT);
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testValueTypeNonexistent7()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest.checkMustFailWithCode(
      new String[] { "resolver/value-type-nonexistent-7.p" },
      ResolverError.Code.RESOLVER_TYPE_NONEXISTENT);
  }

  @SuppressWarnings("static-method") @Test public void testValueTypeOK0()
    throws ResolverError,
      ConstraintError
  {
    final UASTRCompilation r =
      ResolverTest.resolved(new String[] { "resolver/value-type-ok-0.p" });

    final UASTRDModule m = ResolverTest.firstModule(r);

    final UASTRDValue v = (UASTRDValue) m.getTerms().get("x");
    final UASTRTypeName t = ((Some<UASTRTypeName>) v.getAscription()).value;
    final UASTRTypeNameBuiltIn tb = (UASTRTypeNameBuiltIn) t;

    Assert.assertEquals("integer", tb.getName().getActual());
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testVertexShaderLocalTermNonexistent0()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest.checkMustFailWithCode(
      new String[] { "resolver/vertex-shader-local-term-nonexistent-0.p" },
      ResolverError.Code.RESOLVER_TERM_NONEXISTENT);
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testVertexShaderLocalTermNotRecursive0()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest.checkMustFailWithCode(
      new String[] { "resolver/vertex-shader-local-term-not-recursive-0.p" },
      ResolverError.Code.RESOLVER_TERM_NONEXISTENT);
  }

  @SuppressWarnings("static-method") @Test public void testVertexShaderOK0()
    throws ResolverError,
      ConstraintError
  {
    final UASTRCompilation r =
      ResolverTest.resolved(new String[] { "resolver/vertex-shader-ok-0.p" });

    final UASTRDModule m = ResolverTest.firstModule(r);
    final UASTRDShaderVertex vs =
      (UASTRDShaderVertex) m.getShaders().get("v");

    Assert.assertEquals(1, vs.getInputs().size());
    Assert.assertEquals(1, vs.getParameters().size());
    Assert.assertEquals(1, vs.getOutputs().size());

    Assert.assertEquals("in_0", vs.getInputs().get(0).getName().getCurrent());
    Assert.assertEquals("p_0", vs
      .getParameters()
      .get(0)
      .getName()
      .getCurrent());
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testVertexShaderTypeNonexistentInput0()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest.checkMustFailWithCode(
      new String[] { "resolver/vertex-shader-type-nonexistent-input-0.p" },
      ResolverError.Code.RESOLVER_TYPE_NONEXISTENT);
  }

  @SuppressWarnings("static-method") @Test(expected = ResolverError.class) public
    void
    testVertexShaderTypeNonexistentParameter0()
      throws ResolverError,
        ConstraintError
  {
    ResolverTest
      .checkMustFailWithCode(
        new String[] { "resolver/vertex-shader-type-nonexistent-parameter-0.p" },
        ResolverError.Code.RESOLVER_TYPE_NONEXISTENT);
  }
}
