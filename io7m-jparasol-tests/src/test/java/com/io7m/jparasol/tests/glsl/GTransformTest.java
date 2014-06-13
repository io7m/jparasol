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

import java.io.IOException;
import java.util.List;

import javax.annotation.Nonnull;

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jfunctional.Pair;
import com.io7m.jlog.LogUsableType;
import com.io7m.jparasol.CompilerError;
import com.io7m.jparasol.core.GVersionFull;
import com.io7m.jparasol.glsl.GFFIError;
import com.io7m.jparasol.glsl.GTransform;
import com.io7m.jparasol.glsl.GWriter;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEApplicationExternal;
import com.io7m.jparasol.glsl.ast.GASTShader.GASTShaderFragment;
import com.io7m.jparasol.glsl.ast.GASTShader.GASTShaderVertex;
import com.io7m.jparasol.glsl.ast.GASTStatement;
import com.io7m.jparasol.glsl.ast.GASTStatement.GASTReturn;
import com.io7m.jparasol.glsl.ast.GASTStatement.GASTScope;
import com.io7m.jparasol.glsl.ast.GASTTermDeclaration;
import com.io7m.jparasol.glsl.ast.GASTTermDeclaration.GASTTermFunction;
import com.io7m.jparasol.glsl.ast.GTermName.GTermNameGlobal;
import com.io7m.jparasol.glsl.ast.GTermName.GTermNameLocal;
import com.io7m.jparasol.glsl.ast.GTypeName;
import com.io7m.jparasol.lexer.LexerError;
import com.io7m.jparasol.parser.ParserError;
import com.io7m.jparasol.pipeline.CorePipeline;
import com.io7m.jparasol.tests.TestPipeline;
import com.io7m.jparasol.tests.TestUtilities;
import com.io7m.jparasol.typed.Referenced;
import com.io7m.jparasol.typed.Topology;
import com.io7m.jparasol.typed.TypeCheckerError;
import com.io7m.jparasol.typed.ast.TASTCompilation;
import com.io7m.jparasol.typed.ast.TASTShaderNameFlat;
import com.io7m.jparasol.untyped.ModuleStructureError;
import com.io7m.jparasol.untyped.ResolverError;
import com.io7m.jparasol.untyped.UniqueBindersError;
import com.io7m.jparasol.untyped.UnitCombinerError;
import com.io7m.junreachable.UnreachableCodeException;

public final class GTransformTest
{
  static class Prepared
  {
    final @Nonnull LogUsableType   log;
    final @Nonnull CorePipeline    pipe;
    final @Nonnull Referenced      referenced;
    final @Nonnull Topology        topology;
    final @Nonnull TASTCompilation typed;

    Prepared(
      final TASTShaderNameFlat shader,
      final String names[])
    {
      try {
        this.log = TestUtilities.getLog();
        this.pipe = CorePipeline.newPipeline(this.log);
        this.pipe.pipeAddStandardLibrary();

        for (final String name : names) {
          this.pipe.pipeAddInput(TestPipeline.getInput(true, name));
        }

        this.typed = this.pipe.pipeCompile();
        this.referenced = Referenced.fromShader(this.typed, shader, this.log);
        this.topology =
          Topology.fromShader(this.typed, this.referenced, shader, this.log);
      } catch (final LexerError e) {
        e.printStackTrace();
        throw new UnreachableCodeException(e);
      } catch (final ParserError e) {
        e.printStackTrace();
        throw new UnreachableCodeException(e);
      } catch (final UnitCombinerError e) {
        e.printStackTrace();
        throw new UnreachableCodeException(e);
      } catch (final ModuleStructureError e) {
        e.printStackTrace();
        throw new UnreachableCodeException(e);
      } catch (final UniqueBindersError e) {
        e.printStackTrace();
        throw new UnreachableCodeException(e);
      } catch (final TypeCheckerError e) {
        e.printStackTrace();
        throw new UnreachableCodeException(e);
      } catch (final ResolverError e) {
        e.printStackTrace();
        throw new UnreachableCodeException(e);
      } catch (final IOException e) {
        e.printStackTrace();
        throw new UnreachableCodeException(e);
      } catch (final CompilerError e) {
        e.printStackTrace();
        throw new UnreachableCodeException(e);
      }
    }
  }

  @SuppressWarnings("static-method") @Test public
    void
    testBug6c29e7b125ecb_0()
      throws GFFIError
  {
    final TASTShaderNameFlat shader = TestPipeline.shaderName("x.y.M", "v");
    final Prepared p =
      new Prepared(
        shader,
        new String[] { "glsl/transform/bug-6c29e7b125ecb.p" });

    final GASTShaderVertex s =
      GTransform.transformVertex(
        p.typed,
        p.topology,
        shader,
        GVersionFull.GLSL_UPPER,
        p.log);

    final List<Pair<GTermNameGlobal, GASTTermDeclaration>> terms =
      s.getTerms();
    Assert.assertEquals(1, terms.size());

    {
      final Pair<GTermNameGlobal, GASTTermDeclaration> t = terms.get(0);
      final GASTTermDeclaration.GASTTermFunction f =
        (GASTTermFunction) t.getRight();
      Assert.assertEquals("p_x_y_M_f", t.getLeft().show());
      Assert.assertEquals(1, f.getParameters().size());
      final Pair<GTermNameLocal, GTypeName> p0 = f.getParameters().get(0);
      Assert.assertEquals("pl_x", p0.getLeft().show());
      Assert.assertEquals("float", p0.getRight().show());

      final GASTScope scope = f.getStatement();
      final List<GASTStatement> st = scope.getStatements();
      final GASTScope ret_scope = (GASTScope) (st.get(st.size() - 1));
      final GASTReturn ret = (GASTReturn) ret_scope.getStatements().get(0);

      final GASTEApplicationExternal app =
        (GASTEApplicationExternal) ret.getExpression();
      Assert.assertEquals("max", app.getName().show());
    }

    GWriter.writeVertexShader(System.out, s, true);
  }

  @SuppressWarnings("static-method") @Test public void testFragmentSimple_0()
    throws GFFIError
  {
    final TASTShaderNameFlat shader = TestPipeline.shaderName("x.y.M", "f");
    final Prepared p =
      new Prepared(
        shader,
        new String[] { "glsl/transform/fragment-simple-0.p" });

    final GASTShaderFragment s =
      GTransform.transformFragment(
        p.typed,
        p.topology,
        shader,
        GVersionFull.GLSL_UPPER,
        p.log);

    GWriter.writeFragmentShader(System.out, s, true);
  }

  @SuppressWarnings("static-method") @Test public void testFragmentSimple_1()
    throws GFFIError
  {
    final TASTShaderNameFlat shader = TestPipeline.shaderName("x.y.M", "f");
    final Prepared p =
      new Prepared(
        shader,
        new String[] { "glsl/transform/fragment-simple-1.p" });

    final GASTShaderFragment s =
      GTransform.transformFragment(
        p.typed,
        p.topology,
        shader,
        GVersionFull.GLSL_UPPER,
        p.log);

    GWriter.writeFragmentShader(System.out, s, true);
  }

  @SuppressWarnings("static-method") @Test public
    void
    testFragmentTemporaries_0()
      throws GFFIError
  {
    final TASTShaderNameFlat shader = TestPipeline.shaderName("x.y.M", "f");
    final Prepared p =
      new Prepared(
        shader,
        new String[] { "glsl/transform/fragment-temporaries-0.p" });

    final GASTShaderFragment s =
      GTransform.transformFragment(
        p.typed,
        p.topology,
        shader,
        GVersionFull.GLSL_UPPER,
        p.log);

    final List<Pair<GTermNameGlobal, GASTTermDeclaration>> terms =
      s.getTerms();
    Assert.assertEquals(1, terms.size());

    {
      final Pair<GTermNameGlobal, GASTTermDeclaration> t = terms.get(0);
      final GASTTermDeclaration.GASTTermFunction f =
        (GASTTermFunction) t.getRight();
      Assert.assertEquals("_tmp_2", t.getLeft().show());
      Assert.assertEquals(1, f.getParameters().size());
      final Pair<GTermNameLocal, GTypeName> p0 = f.getParameters().get(0);
      Assert.assertEquals("float", p0.getRight().show());
    }

    GWriter.writeFragmentShader(System.out, s, true);
  }

  @SuppressWarnings("static-method") @Test public void testVertexSimple_0()
    throws GFFIError
  {
    final TASTShaderNameFlat shader = TestPipeline.shaderName("x.y.M", "v");
    final Prepared p =
      new Prepared(
        shader,
        new String[] { "glsl/transform/vertex-simple-0.p" });

    final GASTShaderVertex s =
      GTransform.transformVertex(
        p.typed,
        p.topology,
        shader,
        GVersionFull.GLSL_UPPER,
        p.log);

    GWriter.writeVertexShader(System.out, s, true);
  }

  @SuppressWarnings("static-method") @Test public
    void
    testVertexTemporaries_0()
      throws GFFIError
  {
    final TASTShaderNameFlat shader = TestPipeline.shaderName("x.y.M", "v");
    final Prepared p =
      new Prepared(
        shader,
        new String[] { "glsl/transform/vertex-temporaries-0.p" });

    final GASTShaderVertex s =
      GTransform.transformVertex(
        p.typed,
        p.topology,
        shader,
        GVersionFull.GLSL_UPPER,
        p.log);

    final List<Pair<GTermNameGlobal, GASTTermDeclaration>> terms =
      s.getTerms();
    Assert.assertEquals(1, terms.size());

    {
      final Pair<GTermNameGlobal, GASTTermDeclaration> t = terms.get(0);
      final GASTTermDeclaration.GASTTermFunction f =
        (GASTTermFunction) t.getRight();
      Assert.assertEquals("_tmp_2", t.getLeft().show());
      Assert.assertEquals(1, f.getParameters().size());
      final Pair<GTermNameLocal, GTypeName> p0 = f.getParameters().get(0);
      Assert.assertEquals("float", p0.getRight().show());
    }

    GWriter.writeVertexShader(System.out, s, true);
  }
}
