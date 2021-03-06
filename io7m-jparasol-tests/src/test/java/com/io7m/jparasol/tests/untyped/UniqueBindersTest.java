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

package com.io7m.jparasol.tests.untyped;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jfunctional.Some;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.tests.TestUtilities;
import com.io7m.jparasol.untyped.ModuleStructureError;
import com.io7m.jparasol.untyped.UniqueBinders;
import com.io7m.jparasol.untyped.UniqueBindersError;
import com.io7m.jparasol.untyped.ast.checked.UASTCCompilation;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUCompilation;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDExternal;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDFunctionDefined;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDFunctionExternal;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDModule;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderFragment;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderFragmentLocalDiscard;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderFragmentLocalValue;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderFragmentOutputAssignment;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderVertex;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderVertexLocalValue;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderVertexOutputAssignment;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDValueDefined;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDValueLocal;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUEInteger;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUELet;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUEVariable;
import com.io7m.jparasol.untyped.ast.unique_binders.UniqueName.UniqueNameLocal;
import com.io7m.jparasol.untyped.ast.unique_binders.UniqueName.UniqueNameNonLocal;
import com.io7m.junreachable.UnreachableCodeException;

@SuppressWarnings("static-method") public final class UniqueBindersTest
{
  static UASTCCompilation checked(
    final String[] names)
  {
    try {
      return ModuleStructureTest.check(names);
    } catch (final ModuleStructureError e) {
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
    }
  }

  private static UASTUDModule firstModule(
    final UASTUCompilation r)
  {
    return r.getModules().get(r.getModules().keySet().iterator().next());
  }

  static UASTUCompilation unique(
    final String[] names)
    throws UniqueBindersError
  {
    UniqueBinders u;

    u =
      UniqueBinders.newUniqueBinders(
        UniqueBindersTest.checked(names),
        TestUtilities.getLog());

    return u.run();
  }

  static UASTUCompilation uniqueInternal(
    final String[] names)
    throws UniqueBindersError
  {
    UniqueBinders u;

    u =
      UniqueBinders.newUniqueBinders(
        UniqueBindersTest.checkedInternal(names),
        TestUtilities.getLog());

    return u.run();
  }

  @Test public void testFragmentShader0()
    throws UniqueBindersError
  {
    final UASTUCompilation r =
      UniqueBindersTest
        .uniqueInternal(new String[] { "unique_binders/fragment-shader-0.p" });

    final UASTUDModule first = UniqueBindersTest.firstModule(r);
    System.out.println(first);

    final UASTUDShaderFragment fs =
      (UASTUDShaderFragment) first.getShaders().get("f");

    {
      final UASTUDShaderFragmentLocalValue v0 =
        (UASTUDShaderFragmentLocalValue) fs.getLocals().get(0);
      Assert.assertEquals("inv41", v0.getValue().getName().getCurrent());
      final UASTUEVariable v0_body =
        (UASTUEVariable) v0.getValue().getExpression();
      final UniqueNameLocal v0_body_name =
        (UniqueNameLocal) v0_body.getName();
      Assert.assertEquals("inv4", v0_body_name.getCurrent());
    }

    {
      final UASTUDShaderFragmentOutputAssignment out0 = fs.getWrites().get(0);
      final UniqueNameLocal name =
        (UniqueNameLocal) out0.getVariable().getName();
      Assert.assertEquals("inv41", name.getCurrent());
    }
  }

  @Test public void testFragmentShader1()
    throws UniqueBindersError
  {
    final UASTUCompilation r =
      UniqueBindersTest
        .uniqueInternal(new String[] { "unique_binders/fragment-shader-1.p" });

    final UASTUDModule first = UniqueBindersTest.firstModule(r);
    System.out.println(first);

    final UASTUDShaderFragment fs =
      (UASTUDShaderFragment) first.getShaders().get("f");

    {
      final UASTUDShaderFragmentLocalValue v0 =
        (UASTUDShaderFragmentLocalValue) fs.getLocals().get(0);
      Assert.assertEquals("inv41", v0.getValue().getName().getCurrent());
      final UASTUEVariable v0_body =
        (UASTUEVariable) v0.getValue().getExpression();
      final UniqueNameLocal v0_body_name =
        (UniqueNameLocal) v0_body.getName();
      Assert.assertEquals("inv4", v0_body_name.getCurrent());
    }

    {
      final UASTUDShaderFragmentLocalDiscard v1 =
        (UASTUDShaderFragmentLocalDiscard) fs.getLocals().get(1);
      final UASTUEVariable v1v = (UASTUEVariable) v1.getExpression();
      final UniqueNameLocal v1v_name = (UniqueNameLocal) v1v.getName();
      Assert.assertEquals("inv41", v1v_name.getCurrent());
    }

    {
      final UASTUDShaderFragmentOutputAssignment out0 = fs.getWrites().get(0);
      final UniqueNameLocal name =
        (UniqueNameLocal) out0.getVariable().getName();
      Assert.assertEquals("inv41", name.getCurrent());
    }
  }

  @Test public void testFunction0()
    throws UniqueBindersError
  {
    final UASTUCompilation r =
      UniqueBindersTest
        .uniqueInternal(new String[] { "unique_binders/function-0.p" });

    final UASTUDModule first = UniqueBindersTest.firstModule(r);
    System.out.println(first);

    final UASTUDFunctionDefined f =
      (UASTUDFunctionDefined) first.getTerms().get("f");

    final UASTUELet f_let = (UASTUELet) f.getBody();

    {
      final UASTUDValueLocal b0 = f_let.getBindings().get(0);
      final UniqueNameLocal name = b0.getName();
      Assert.assertEquals("f1", name.getCurrent());

      final UASTUEVariable b0_body = (UASTUEVariable) b0.getExpression();
      final UniqueNameLocal body_name = (UniqueNameLocal) b0_body.getName();
      Assert.assertEquals("x", body_name.getCurrent());
    }

    {
      final UASTUEVariable f_let_body = (UASTUEVariable) f_let.getBody();
      final UniqueNameLocal name = (UniqueNameLocal) f_let_body.getName();
      Assert.assertEquals("f1", name.getCurrent());
    }
  }

  @Test public void testFunction1()
    throws UniqueBindersError
  {
    final UASTUCompilation r =
      UniqueBindersTest
        .uniqueInternal(new String[] { "unique_binders/function-1.p" });

    final UASTUDModule first = UniqueBindersTest.firstModule(r);
    System.out.println(first);

    final UASTUDFunctionDefined f =
      (UASTUDFunctionDefined) first.getTerms().get("f");

    final UASTUELet f_let = (UASTUELet) f.getBody();

    {
      final UASTUDValueLocal b0 = f_let.getBindings().get(0);
      final UniqueNameLocal name = b0.getName();
      Assert.assertEquals("x1", name.getCurrent());

      final UASTUEVariable b0_body = (UASTUEVariable) b0.getExpression();
      final UniqueNameLocal body_name = (UniqueNameLocal) b0_body.getName();
      Assert.assertEquals("x", body_name.getCurrent());
    }

    {
      final UASTUEVariable f_let_body = (UASTUEVariable) f_let.getBody();
      final UniqueNameLocal name = (UniqueNameLocal) f_let_body.getName();
      Assert.assertEquals("x1", name.getCurrent());
    }
  }

  @Test public void testFunction2()
    throws UniqueBindersError
  {
    final UASTUCompilation r =
      UniqueBindersTest
        .uniqueInternal(new String[] { "unique_binders/function-2.p" });

    final UASTUDModule first = UniqueBindersTest.firstModule(r);
    System.out.println(first);

    final UASTUDFunctionDefined f =
      (UASTUDFunctionDefined) first.getTerms().get("f");
    Assert.assertEquals("f", f.getName().getActual());
    final UASTUEVariable var = (UASTUEVariable) f.getBody();
    Assert.assertEquals("f", ((UniqueNameNonLocal) var.getName())
      .getName()
      .getActual());
  }

  @Test public void testFunctionExternal0()
    throws UniqueBindersError
  {
    final UASTUCompilation r =
      UniqueBindersTest
        .uniqueInternal(new String[] { "unique_binders/function-external-0.p" });

    final UASTUDModule first = UniqueBindersTest.firstModule(r);
    System.out.println(first);

    final UASTUDFunctionExternal f =
      (UASTUDFunctionExternal) first.getTerms().get("f");
    Assert.assertEquals("f", f.getName().getActual());
    final UASTUDExternal ext = f.getExternal();
    final Some<UASTUExpression> emu =
      (Some<UASTUExpression>) ext.getEmulation();

    final UASTUEVariable var = (UASTUEVariable) emu.get();
    Assert.assertEquals("x", ((UniqueNameLocal) var.getName()).getCurrent());
  }

  @Test public void testFunctionExternal1()
    throws UniqueBindersError
  {
    final UASTUCompilation r =
      UniqueBindersTest
        .uniqueInternal(new String[] { "unique_binders/function-external-1.p" });

    final UASTUDModule first = UniqueBindersTest.firstModule(r);
    System.out.println(first);

    final UASTUDFunctionExternal f =
      (UASTUDFunctionExternal) first.getTerms().get("f");
    Assert.assertEquals("f", f.getName().getActual());
    final UASTUDExternal ext = f.getExternal();
    final Some<UASTUExpression> emu =
      (Some<UASTUExpression>) ext.getEmulation();

    final UASTUEVariable var = (UASTUEVariable) emu.get();
    Assert.assertEquals("y", ((UniqueNameNonLocal) var.getName())
      .getName()
      .getActual());
  }

  /**
   * Successive let bindings shadow previous bindings in the same scope.
   */

  @Test public void testLetShadow0()
    throws UniqueBindersError
  {
    final UASTUCompilation r =
      UniqueBindersTest
        .uniqueInternal(new String[] { "unique_binders/let-shadow-0.p" });

    final UASTUDModule first = UniqueBindersTest.firstModule(r);
    System.out.println(first);

    final UASTUDValueDefined z =
      (UASTUDValueDefined) first.getTerms().get("z");
    final UASTUELet z_let = (UASTUELet) z.getExpression();

    final List<UASTUDValueLocal> bindings = z_let.getBindings();
    final UASTUDValueLocal x = bindings.get(0);
    Assert.assertEquals("x", x.getName().getCurrent());
    Assert.assertEquals(0, ((UASTUEInteger) x.getExpression())
      .getValue()
      .intValue());

    final UASTUDValueLocal x1 = bindings.get(1);
    Assert.assertEquals("x1", x1.getName().getCurrent());
    Assert.assertEquals("&x", ((UASTUEVariable) x1.getExpression())
      .getName()
      .show());

    final UASTUDValueLocal x2 = bindings.get(2);
    Assert.assertEquals("x2", x2.getName().getCurrent());
    Assert.assertEquals("&x1", ((UASTUEVariable) x2.getExpression())
      .getName()
      .show());
  }

  @Test public void testNotRestricted()
    throws UniqueBindersError
  {
    final UASTUCompilation r =
      UniqueBindersTest
        .uniqueInternal(new String[] { "unique_binders/not-restricted-0.p" });

    final ModulePathFlat path = r.getModules().keySet().iterator().next();
    final UASTUDModule module = r.getModules().get(path);
    System.out.println(module);

    final UASTUDValueDefined value =
      (UASTUDValueDefined) module.getDeclarations().get(0);
    Assert.assertEquals("vec", value.getName().getActual());

    /**
     * The first binding of the let expression will have been modified to be
     * distinct from the module-level value.
     */

    final UASTUELet let0 = (UASTUELet) value.getExpression();
    final UASTUDValueLocal let0_v = let0.getBindings().get(0);
    Assert.assertEquals("&vec1", let0_v.getName().show());

    /**
     * The body of the let expression is another let expression. The first
     * binding of which will have been modified and will end up being called
     * "vec5", because the renaming algorithm will skip "vec2", "vec3", etc,
     * due to those being restricted names.
     */

    final UASTUELet let1 = (UASTUELet) let0.getBody();
    final UASTUDValueLocal let1_v = let1.getBindings().get(0);
    Assert.assertEquals("&vec5", let1_v.getName().show());
  }

  @Test public void testPreSimple0()
    throws UniqueBindersError
  {
    final UASTUCompilation r =
      UniqueBindersTest
        .uniqueInternal(new String[] { "unique_binders/let-pre-simple-0.p" });

    final UASTUDModule first = UniqueBindersTest.firstModule(r);
    System.out.println(first);

    final UASTUDValueDefined y =
      (UASTUDValueDefined) first.getTerms().get("y");
    final UASTUELet y_let = (UASTUELet) y.getExpression();
    final UASTUDValueLocal y_let_0 = y_let.getBindings().get(0);

    {
      final UniqueNameLocal name = y_let_0.getName();
      Assert.assertEquals("y1", name.getCurrent());
    }

    {
      final UASTUEVariable y_let_body = (UASTUEVariable) y_let.getBody();
      final UniqueNameLocal name = (UniqueNameLocal) y_let_body.getName();
      Assert.assertEquals("y1", name.getCurrent());
    }
  }

  @Test public void testPreSimple1()
    throws UniqueBindersError
  {
    final UASTUCompilation r =
      UniqueBindersTest
        .uniqueInternal(new String[] { "unique_binders/let-pre-simple-1.p" });

    final UASTUDModule first = UniqueBindersTest.firstModule(r);
    System.out.println(first);

    final UASTUDValueDefined y =
      (UASTUDValueDefined) first.getTerms().get("y");
    final UASTUELet y_let = (UASTUELet) y.getExpression();

    {
      final UASTUDValueLocal b0 = y_let.getBindings().get(0);
      final UniqueNameLocal name = b0.getName();
      Assert.assertEquals("y1", name.getCurrent());
    }

    final UASTUELet y_let_let = (UASTUELet) y_let.getBody();

    {
      final UASTUDValueLocal b0 = y_let_let.getBindings().get(0);
      final UniqueNameLocal name = b0.getName();
      Assert.assertEquals("y2", name.getCurrent());
    }

    {
      final UASTUEVariable y_let_body = (UASTUEVariable) y_let_let.getBody();
      final UniqueNameLocal name = (UniqueNameLocal) y_let_body.getName();
      Assert.assertEquals("y2", name.getCurrent());
    }
  }

  @Test public void testPreSimple2()
    throws UniqueBindersError
  {
    final UASTUCompilation r =
      UniqueBindersTest
        .uniqueInternal(new String[] { "unique_binders/let-pre-simple-2.p" });

    final UASTUDModule first = UniqueBindersTest.firstModule(r);
    System.out.println(first);

    final UASTUDValueDefined y =
      (UASTUDValueDefined) first.getTerms().get("y");
    final UASTUELet y_let = (UASTUELet) y.getExpression();

    {
      final UASTUDValueLocal b0 = y_let.getBindings().get(0);
      final UniqueNameLocal name = b0.getName();
      Assert.assertEquals("y1", name.getCurrent());

      final UASTUEVariable b0e = (UASTUEVariable) b0.getExpression();
      final UniqueNameNonLocal b0e_name = (UniqueNameNonLocal) b0e.getName();
      Assert.assertEquals("y", b0e_name.getName().getActual());
    }

    final UASTUEVariable y_let_body = (UASTUEVariable) y_let.getBody();
    final UniqueNameLocal name = (UniqueNameLocal) y_let_body.getName();
    Assert.assertEquals("y1", name.getCurrent());
  }

  @Test public void testValue0()
    throws UniqueBindersError
  {
    final UASTUCompilation r =
      UniqueBindersTest
        .uniqueInternal(new String[] { "unique_binders/value-0.p" });

    final UASTUDModule first = UniqueBindersTest.firstModule(r);
    System.out.println(first);

    final UASTUDValueDefined v =
      (UASTUDValueDefined) first.getTerms().get("x");
    Assert.assertEquals("x", v.getName().getActual());
    final UASTUEVariable var = (UASTUEVariable) v.getExpression();
    Assert.assertEquals("x", ((UniqueNameNonLocal) var.getName())
      .getName()
      .getActual());
  }

  @Test public void testVertexShader0()
    throws UniqueBindersError
  {
    final UASTUCompilation r =
      UniqueBindersTest
        .uniqueInternal(new String[] { "unique_binders/vertex-shader-0.p" });

    final UASTUDModule first = UniqueBindersTest.firstModule(r);
    System.out.println(first);

    final UASTUDShaderVertex vs =
      (UASTUDShaderVertex) first.getShaders().get("v");

    {
      final UASTUDShaderVertexLocalValue v0 = vs.getLocals().get(0);
      Assert.assertEquals("inv41", v0.getValue().getName().getCurrent());
      final UASTUEVariable v0_body =
        (UASTUEVariable) v0.getValue().getExpression();
      final UniqueNameLocal v0_body_name =
        (UniqueNameLocal) v0_body.getName();
      Assert.assertEquals("inv4", v0_body_name.getCurrent());
    }

    {
      final UASTUDShaderVertexOutputAssignment out0 = vs.getWrites().get(0);
      final UniqueNameLocal name =
        (UniqueNameLocal) out0.getVariable().getName();
      Assert.assertEquals("inv41", name.getCurrent());
    }
  }
}
