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

import javax.annotation.Nonnull;

import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.traverse.DepthFirstIterator;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Unit;
import com.io7m.jlog.Level;
import com.io7m.jlog.Log;
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
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDValue;
import com.io7m.jparasol.typed.ast.TASTNameTermShaderFlat;
import com.io7m.jparasol.typed.ast.TASTNameTermShaderFlat.Shader;
import com.io7m.jparasol.typed.ast.TASTReference;
import com.io7m.jparasol.typed.ast.TASTShaderNameFlat;
import com.io7m.jparasol.typed.ast.TASTShaderVisitor;
import com.io7m.jparasol.typed.ast.TASTTermNameFlat;
import com.io7m.jparasol.typed.ast.TASTTermVisitor;

/**
 * A simple checker that determines if vertex-only or fragment-only external
 * functions can be reached from shaders of the wrong types.
 */

public final class Externals
{
  private static enum Required
  {
    REQUIRE_FRAGMENT_SHADER,
    REQUIRE_VERTEX_SHADER
  }

  private static class ShaderChecker implements
    TASTShaderVisitor<Unit, ExternalsError>
  {
    private final @Nonnull Log                                                         log;
    private final @Nonnull Map<ModulePathFlat, TASTDModule>                            modules;
    private final @Nonnull String                                                      name;
    private final @Nonnull ModulePathFlat                                              path;
    private final @Nonnull DirectedAcyclicGraph<TASTNameTermShaderFlat, TASTReference> term_shader;
    private final @Nonnull DirectedAcyclicGraph<TASTTermNameFlat, TASTReference>       term_term;

    public ShaderChecker(
      final @Nonnull Map<ModulePathFlat, TASTDModule> modules,
      final @Nonnull DirectedAcyclicGraph<TASTNameTermShaderFlat, TASTReference> term_shader,
      final @Nonnull DirectedAcyclicGraph<TASTTermNameFlat, TASTReference> term_term,
      final @Nonnull ModulePathFlat path,
      final @Nonnull String name,
      final @Nonnull Log log)
    {
      this.modules = modules;
      this.term_shader = term_shader;
      this.term_term = term_term;
      this.path = path;
      this.name = name;
      this.log = log;
    }

    private void checkDependencies(
      final @Nonnull Required required)
      throws ConstraintError,
        ExternalsError
    {
      final TASTShaderNameFlat shader_name =
        new TASTShaderNameFlat(this.path, this.name);
      final TASTNameTermShaderFlat.Shader start_shader =
        new TASTNameTermShaderFlat.Shader(shader_name);

      if (this.log.enabled(Level.LOG_DEBUG)) {
        this.log.debug(String.format(
          "Checking dependencies for %s %s",
          start_shader.show(),
          required));
      }

      final Set<TASTReference> direct_terms =
        this.term_shader.outgoingEdgesOf(start_shader);

      for (final TASTReference d : direct_terms) {
        final ModulePath target_module = d.getTargetModule();

        final TASTTermNameFlat start_term =
          new TASTTermNameFlat(
            ModulePathFlat.fromModulePath(target_module),
            d.getTargetName().getActual());

        final DepthFirstIterator<TASTTermNameFlat, TASTReference> di =
          new DepthFirstIterator<TASTTermNameFlat, TASTReference>(
            this.term_term,
            start_term);

        while (di.hasNext()) {
          final TASTTermNameFlat current = di.next();
          if (this.log.enabled(Level.LOG_DEBUG)) {
            this.log.debug(String.format("Term %s", current.show()));
          }

          final TASTDTerm term = this.lookupTerm(current);
          term.termVisitableAccept(new TermDeclarationChecker(
            start_shader,
            start_term,
            d,
            current,
            this.term_shader,
            this.term_term,
            required));
        }
      }
    }

    private @Nonnull TASTDTerm lookupTerm(
      final @Nonnull TASTTermNameFlat current)
    {
      final TASTDModule m = this.modules.get(current.getModulePath());
      assert m.getTerms().containsKey(current.getName());
      return m.getTerms().get(current.getName());
    }

    @Override public Unit moduleVisitFragmentShader(
      final @Nonnull TASTDShaderFragment f)
      throws ExternalsError,
        ConstraintError
    {
      this.checkDependencies(Required.REQUIRE_FRAGMENT_SHADER);
      return Unit.unit();
    }

    @Override public Unit moduleVisitProgramShader(
      final @Nonnull TASTDShaderProgram p)
      throws ExternalsError,
        ConstraintError
    {
      return Unit.unit();
    }

    @Override public Unit moduleVisitVertexShader(
      final @Nonnull TASTDShaderVertex f)
      throws ExternalsError,
        ConstraintError
    {
      this.checkDependencies(Required.REQUIRE_VERTEX_SHADER);
      return Unit.unit();
    }
  }

  private static final class TermDeclarationChecker implements
    TASTTermVisitor<Unit, ExternalsError>
  {
    private final @Nonnull TASTTermNameFlat                                            current_term;
    private final @Nonnull Required                                                    required;
    private final @Nonnull TASTNameTermShaderFlat.Shader                               start_shader;
    private final @Nonnull TASTTermNameFlat                                            start_term;
    private final @Nonnull TASTReference                                               start_term_reference;
    private final @Nonnull DirectedAcyclicGraph<TASTNameTermShaderFlat, TASTReference> term_shader;
    private final @Nonnull DirectedAcyclicGraph<TASTTermNameFlat, TASTReference>       term_term;

    public TermDeclarationChecker(
      final @Nonnull Shader start_shader,
      final @Nonnull TASTTermNameFlat start_term,
      final @Nonnull TASTReference start_term_reference,
      final @Nonnull TASTTermNameFlat current_term,
      final @Nonnull DirectedAcyclicGraph<TASTNameTermShaderFlat, TASTReference> term_shader,
      final @Nonnull DirectedAcyclicGraph<TASTTermNameFlat, TASTReference> term_term,
      final @Nonnull Required required)
    {
      this.start_shader = start_shader;
      this.start_term = start_term;
      this.start_term_reference = start_term_reference;
      this.current_term = current_term;
      this.term_shader = term_shader;
      this.term_term = term_term;
      this.required = required;
    }

    @Override public Unit termVisitFunctionDefined(
      final @Nonnull TASTDFunctionDefined f)
      throws ConstraintError,
        ExternalsError
    {
      return Unit.unit();
    }

    @Override public Unit termVisitFunctionExternal(
      final @Nonnull TASTDFunctionExternal f)
      throws ConstraintError,
        ExternalsError
    {
      final TASTDExternal ext = f.getExternal();

      switch (this.required) {
        case REQUIRE_FRAGMENT_SHADER:
        {
          if (ext.isFragmentShaderAllowed() == false) {
            final DijkstraShortestPath<TASTTermNameFlat, TASTReference> dsp =
              new DijkstraShortestPath<TASTTermNameFlat, TASTReference>(
                this.term_term,
                this.start_term,
                this.current_term);
            throw ExternalsError.termDisallowedInFragmentShader(
              this.start_shader,
              this.start_term_reference,
              this.current_term,
              dsp.getPath());
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
            throw ExternalsError.termDisallowedInVertexShader(
              this.start_shader,
              this.start_term_reference,
              this.current_term,
              dsp.getPath());
          }
          break;
        }
      }

      return Unit.unit();
    }

    @Override public Unit termVisitValue(
      final @Nonnull TASTDValue v)
      throws ConstraintError,
        ExternalsError
    {
      return Unit.unit();
    }
  }

  public static @Nonnull Externals newExternalsChecker(
    final @Nonnull Log log)
  {
    return new Externals(log);
  }

  private final @Nonnull Log log;

  private Externals(
    final @Nonnull Log log)
  {
    this.log = new Log(log, "externals");
  }

  public void check(
    final @Nonnull TASTCompilation compilation)
    throws ExternalsError,
      ConstraintError
  {
    final Map<ModulePathFlat, TASTDModule> modules = compilation.getModules();

    for (final ModulePathFlat p : modules.keySet()) {
      final TASTDModule m = modules.get(p);
      final Map<String, TASTDShader> shaders = m.getShaders();
      for (final String name : shaders.keySet()) {
        final TASTDShader s = shaders.get(name);

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
