/*
 * Copyright © 2014 <code@io7m.com> http://io7m.com
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

import java.util.Map;
import java.util.Set;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.traverse.DepthFirstIterator;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jfunctional.Unit;
import com.io7m.jlog.LogLevel;
import com.io7m.jlog.LogUsableType;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.ModulePath;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.typed.ast.TASTCompilation;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDExternal;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDFunctionDefined;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDFunctionExternal;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDModule;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShader;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragment;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderProgram;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderVertex;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDTerm;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDValueDefined;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDValueExternal;
import com.io7m.jparasol.typed.ast.TASTNameTermShaderFlatType;
import com.io7m.jparasol.typed.ast.TASTReference;
import com.io7m.jparasol.typed.ast.TASTShaderNameFlat;
import com.io7m.jparasol.typed.ast.TASTShaderVisitorType;
import com.io7m.jparasol.typed.ast.TASTTermNameFlat;
import com.io7m.jparasol.typed.ast.TASTTermVisitorType;

/**
 * A simple checker that determines if vertex-only or fragment-only external
 * functions can be reached from shaders of the wrong types.
 */

@EqualityReference public final class Externals
{
  private static enum Required
  {
    REQUIRE_FRAGMENT_SHADER,
    REQUIRE_VERTEX_SHADER
  }

  @EqualityReference private static final class ShaderChecker implements
    TASTShaderVisitorType<Unit, ExternalsError>
  {
    private final LogUsableType                                                   log;
    private final Map<ModulePathFlat, TASTDModule>                                modules;
    private final String                                                          name;
    private final ModulePathFlat                                                  path;
    private final DirectedAcyclicGraph<TASTNameTermShaderFlatType, TASTReference> term_shader;
    private final DirectedAcyclicGraph<TASTTermNameFlat, TASTReference>           term_term;

    public ShaderChecker(
      final Map<ModulePathFlat, TASTDModule> in_modules,
      final DirectedAcyclicGraph<TASTNameTermShaderFlatType, TASTReference> in_term_shader,
      final DirectedAcyclicGraph<TASTTermNameFlat, TASTReference> in_term_term,
      final ModulePathFlat in_path,
      final String in_name,
      final LogUsableType in_log)
    {
      this.modules = in_modules;
      this.term_shader = in_term_shader;
      this.term_term = in_term_term;
      this.path = in_path;
      this.name = in_name;
      this.log = in_log;
    }

    private void checkDependencies(
      final Required required)
      throws ExternalsError
    {
      final TASTShaderNameFlat shader_name =
        new TASTShaderNameFlat(this.path, this.name);

      if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
        final String r =
          String.format(
            "Checking dependencies for %s %s",
            shader_name.show(),
            required);
        assert r != null;
        this.log.debug(r);
      }

      final Set<TASTReference> direct_terms =
        this.term_shader.outgoingEdgesOf(shader_name);

      for (final TASTReference d : direct_terms) {
        final ModulePath target_module = d.getTargetModule();

        final String r = d.getTargetName().getActual();
        assert r != null;

        final TASTTermNameFlat start_term =
          new TASTTermNameFlat(
            ModulePathFlat.fromModulePath(target_module),
            r);

        final DepthFirstIterator<TASTTermNameFlat, TASTReference> di =
          new DepthFirstIterator<TASTTermNameFlat, TASTReference>(
            this.term_term,
            start_term);

        while (di.hasNext()) {
          final TASTTermNameFlat current = di.next();

          if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
            final String rr = String.format("Term %s", current.show());
            assert rr != null;
            this.log.debug(rr);
          }

          assert current != null;
          final TASTDTerm term = this.lookupTerm(current);
          term.termVisitableAccept(new TermDeclarationChecker(
            shader_name,
            start_term,
            d,
            current,
            this.term_shader,
            this.term_term,
            required));
        }
      }
    }

    private TASTDTerm lookupTerm(
      final TASTTermNameFlat current)
    {
      final TASTDModule m = this.modules.get(current.getModulePath());
      assert m.getTerms().containsKey(current.getName());
      final TASTDTerm r = m.getTerms().get(current.getName());
      assert r != null;
      return r;
    }

    @Override public Unit moduleVisitFragmentShader(
      final TASTDShaderFragment f)
      throws ExternalsError
    {
      this.checkDependencies(Required.REQUIRE_FRAGMENT_SHADER);
      return Unit.unit();
    }

    @Override public Unit moduleVisitProgramShader(
      final TASTDShaderProgram p)
      throws ExternalsError
    {
      return Unit.unit();
    }

    @Override public Unit moduleVisitVertexShader(
      final TASTDShaderVertex f)
      throws ExternalsError
    {
      this.checkDependencies(Required.REQUIRE_VERTEX_SHADER);
      return Unit.unit();
    }
  }

  @EqualityReference private static final class TermDeclarationChecker implements
    TASTTermVisitorType<Unit, ExternalsError>
  {
    private final TASTTermNameFlat                                                current_term;
    private final Required                                                        required;
    private final TASTShaderNameFlat                                              start_shader;
    private final TASTTermNameFlat                                                start_term;
    private final TASTReference                                                   start_term_reference;
    private final DirectedAcyclicGraph<TASTNameTermShaderFlatType, TASTReference> term_shader;
    private final DirectedAcyclicGraph<TASTTermNameFlat, TASTReference>           term_term;

    public TermDeclarationChecker(
      final TASTShaderNameFlat in_start_shader,
      final TASTTermNameFlat in_start_term,
      final TASTReference in_start_term_reference,
      final TASTTermNameFlat in_current_term,
      final DirectedAcyclicGraph<TASTNameTermShaderFlatType, TASTReference> in_term_shader,
      final DirectedAcyclicGraph<TASTTermNameFlat, TASTReference> in_term_term,
      final Required in_required)
    {
      this.start_shader = in_start_shader;
      this.start_term = in_start_term;
      this.start_term_reference = in_start_term_reference;
      this.current_term = in_current_term;
      this.term_shader = in_term_shader;
      this.term_term = in_term_term;
      this.required = in_required;
    }

    private void checkExternal(
      final TASTDExternal ext)
      throws ExternalsError
    {
      switch (this.required) {
        case REQUIRE_FRAGMENT_SHADER:
        {
          if (ext.isFragmentShaderAllowed() == false) {
            final DijkstraShortestPath<TASTTermNameFlat, TASTReference> dsp =
              new DijkstraShortestPath<TASTTermNameFlat, TASTReference>(
                this.term_term,
                this.start_term,
                this.current_term);
            final GraphPath<TASTTermNameFlat, TASTReference> r =
              dsp.getPath();
            assert r != null;
            throw ExternalsError.termDisallowedInFragmentShader(
              this.start_shader,
              this.start_term_reference,
              this.current_term,
              r);
          }
          break;
        }
        case REQUIRE_VERTEX_SHADER:
        {
          if (ext.isVertexShaderAllowed() == false) {
            final DijkstraShortestPath<TASTTermNameFlat, TASTReference> dsp =
              new DijkstraShortestPath<TASTTermNameFlat, TASTReference>(
                this.term_term,
                this.start_term,
                this.current_term);
            final GraphPath<TASTTermNameFlat, TASTReference> r =
              dsp.getPath();
            assert r != null;
            throw ExternalsError.termDisallowedInVertexShader(
              this.start_shader,
              this.start_term_reference,
              this.current_term,
              r);
          }
          break;
        }
      }
    }

    @Override public Unit termVisitFunctionDefined(
      final TASTDFunctionDefined f)
      throws ExternalsError
    {
      return Unit.unit();
    }

    @Override public Unit termVisitFunctionExternal(
      final TASTDFunctionExternal f)
      throws ExternalsError
    {
      this.checkExternal(f.getExternal());
      return Unit.unit();
    }

    @Override public Unit termVisitValueDefined(
      final TASTDValueDefined v)
      throws ExternalsError
    {
      return Unit.unit();
    }

    @Override public Unit termVisitValueExternal(
      final TASTDValueExternal v)
      throws ExternalsError
    {
      this.checkExternal(v.getExternal());
      return Unit.unit();
    }
  }

  /**
   * Construct a new externals checker.
   * 
   * @param log
   *          The log interface
   * @return A new checker
   */

  public static Externals newExternalsChecker(
    final LogUsableType log)
  {
    return new Externals(log);
  }

  private final LogUsableType log;

  private Externals(
    final LogUsableType in_log)
  {
    this.log = NullCheck.notNull(in_log, "Log").with("externals");
  }

  /**
   * Check the given typed AST.
   * 
   * @param compilation
   *          The AST
   * @throws ExternalsError
   *           If an error occurs
   */

  public void check(
    final TASTCompilation compilation)
    throws ExternalsError
  {
    NullCheck.notNull(compilation, "AST");

    final Map<ModulePathFlat, TASTDModule> modules = compilation.getModules();

    for (final ModulePathFlat p : modules.keySet()) {
      final TASTDModule m = modules.get(p);
      final Map<String, TASTDShader> shaders = m.getShaders();
      for (final String name : shaders.keySet()) {
        final TASTDShader s = shaders.get(name);

        assert p != null;
        assert name != null;

        final ShaderChecker c =
          new ShaderChecker(
            modules,
            compilation.getShaderTermGraph(),
            compilation.getTermGraph(),
            p,
            name,
            this.log);

        s.shaderVisitableAccept(c);
      }
    }
  }
}
