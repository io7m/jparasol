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

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jfunctional.Unit;
import com.io7m.jlog.LogLevel;
import com.io7m.jlog.LogUsableType;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.lexer.TokenIdentifierLower;
import com.io7m.jparasol.typed.ast.TASTCompilation;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDModule;
import com.io7m.jparasol.typed.ast.TASTNameTermShaderFlatType;
import com.io7m.jparasol.typed.ast.TASTNameTypeShaderFlatType;
import com.io7m.jparasol.typed.ast.TASTNameTypeShaderFlatVisitorType;
import com.io7m.jparasol.typed.ast.TASTNameTypeTermFlatType;
import com.io7m.jparasol.typed.ast.TASTNameTypeTermFlatVisitorType;
import com.io7m.jparasol.typed.ast.TASTReference;
import com.io7m.jparasol.typed.ast.TASTShaderNameFlat;
import com.io7m.jparasol.typed.ast.TASTTermNameFlat;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * Determine all of the referenced terms and types for a given shader.
 */

@EqualityReference public final class Referenced
{
  private static void collectTermsFromShader(
    final TASTCompilation compilation,
    final TASTShaderNameFlat shader_name,
    final LogUsableType log_actual,
    final Set<TASTTermNameFlat> terms)
  {
    final DirectedAcyclicGraph<TASTNameTermShaderFlatType, TASTReference> shader_term_graph =
      compilation.getShaderTermGraph();
    final DirectedAcyclicGraph<TASTTermNameFlat, TASTReference> term_term_graph =
      compilation.getTermGraph();

    for (final TASTReference d : shader_term_graph
      .outgoingEdgesOf(shader_name)) {
      final ModulePathFlat term_module = d.getTargetModuleFlat();
      final TokenIdentifierLower term_name = d.getTargetName();
      final TASTTermNameFlat term =
        new TASTTermNameFlat(term_module, term_name.getActual());

      final BreadthFirstIterator<TASTTermNameFlat, TASTReference> bfi =
        new BreadthFirstIterator<TASTTermNameFlat, TASTReference>(
          term_term_graph,
          term);

      while (bfi.hasNext()) {
        final TASTTermNameFlat current = bfi.next();
        if (log_actual.wouldLog(LogLevel.LOG_DEBUG)) {
          final String r = String.format("Adding term %s", current.show());
          assert r != null;
          log_actual.debug(r);
        }
        terms.add(new TASTTermNameFlat(current.getModulePath(), current
          .getName()));
      }
    }
  }

  private static void collectTypesForTerm(
    final TASTCompilation compilation,
    final Set<TTypeNameFlat> types,
    final TASTTermNameFlat term,
    final LogUsableType log)
  {
    final DirectedAcyclicGraph<TASTNameTypeTermFlatType, TASTReference> term_type =
      compilation.getTermTypeGraph();

    final BreadthFirstIterator<TASTNameTypeTermFlatType, TASTReference> bfi =
      new BreadthFirstIterator<TASTNameTypeTermFlatType, TASTReference>(
        term_type,
        term);

    while (bfi.hasNext()) {
      final TASTNameTypeTermFlatType current = bfi.next();
      current
        .nameTypeTermVisitableAccept(new TASTNameTypeTermFlatVisitorType<Unit, UnreachableCodeException>() {
          @Override public Unit nameTypeTermVisitTerm(
            final TASTTermNameFlat _)
          {
            return Unit.unit();
          }

          @Override public Unit nameTypeTermVisitType(
            final TTypeNameFlat type)
          {
            if (log.wouldLog(LogLevel.LOG_DEBUG)) {
              final String r = String.format("Adding type %s", type.show());
              assert r != null;
              log.debug(r);
            }
            types.add(type);
            return Unit.unit();
          }
        });
    }
  }

  private static void collectTypesForType(
    final TASTCompilation compilation,
    final Set<TTypeNameFlat> types,
    final TTypeNameFlat type_name,
    final LogUsableType log)
  {
    final DirectedAcyclicGraph<TTypeNameFlat, TASTReference> type_graph =
      compilation.getTypeGraph();

    final BreadthFirstIterator<TTypeNameFlat, TASTReference> bfi =
      new BreadthFirstIterator<TTypeNameFlat, TASTReference>(
        type_graph,
        type_name);

    while (bfi.hasNext()) {
      final TTypeNameFlat current = bfi.next();
      if (log.wouldLog(LogLevel.LOG_DEBUG)) {
        final String r = String.format("Adding type %s", current.show());
        assert r != null;
        log.debug(r);
      }
      types.add(current);
    }
  }

  private static void collectTypesFromShader(
    final TASTCompilation compilation,
    final Set<TTypeNameFlat> types,
    final TASTShaderNameFlat shader_name,
    final LogUsableType log)
  {
    final DirectedAcyclicGraph<TASTNameTypeShaderFlatType, TASTReference> shader_type =
      compilation.getShaderTypeGraph();

    final BreadthFirstIterator<TASTNameTypeShaderFlatType, TASTReference> bfi =
      new BreadthFirstIterator<TASTNameTypeShaderFlatType, TASTReference>(
        shader_type,
        shader_name);

    while (bfi.hasNext()) {
      final TASTNameTypeShaderFlatType current = bfi.next();
      current
        .nameTypeShaderVisitableAccept(new TASTNameTypeShaderFlatVisitorType<Unit, UnreachableCodeException>() {
          @Override public Unit nameTypeShaderVisitShader(
            final TASTShaderNameFlat t)
          {
            return Unit.unit();
          }

          @Override public Unit nameTypeShaderVisitType(
            final TTypeNameFlat t)
          {
            if (log.wouldLog(LogLevel.LOG_DEBUG)) {
              final String r = String.format("Adding type %s", t.show());
              assert r != null;
              log.debug(r);
            }
            types.add(t);
            return Unit.unit();
          }
        });
    }
  }

  /**
   * @param compilation
   *          The typed AST
   * @param shader_name
   *          The name of a shader
   * @param log
   *          A log interface
   * @return The terms and types referenced by the shader
   */

  public static Referenced fromShader(
    final TASTCompilation compilation,
    final TASTShaderNameFlat shader_name,
    final LogUsableType log)
  {
    NullCheck.notNull(compilation, "Compilation");
    NullCheck.notNull(shader_name, "Shader name");
    NullCheck.notNull(log, "Log");

    final Map<ModulePathFlat, TASTDModule> modules = compilation.getModules();

    if (modules.containsKey(shader_name.getModulePath()) == false) {
      throw new IllegalStateException(String.format(
        "Module %s does not exist",
        shader_name.getModulePath()));
    }
    final TASTDModule m = modules.get(shader_name.getModulePath());
    if (m.getShaders().containsKey(shader_name.getName()) == false) {
      throw new IllegalStateException(String.format(
        "Shader %s does not exist",
        shader_name.getName()));
    }

    final LogUsableType log_actual =
      NullCheck.notNull(log, "Log").with("referenced");

    /**
     * Collect all terms referenced by the shader.
     */

    final Set<TASTTermNameFlat> terms = new HashSet<TASTTermNameFlat>();
    Referenced.collectTermsFromShader(
      compilation,
      shader_name,
      log_actual,
      terms);

    /**
     * Collect all types referenced by those terms.
     */

    final Set<TTypeNameFlat> types = new HashSet<TTypeNameFlat>();
    for (final TASTTermNameFlat t : terms) {
      assert t != null;
      Referenced.collectTypesForTerm(compilation, types, t, log_actual);
    }

    /**
     * Collect all types referenced by the shader.
     */

    Referenced.collectTypesFromShader(
      compilation,
      types,
      shader_name,
      log_actual);

    /**
     * Collect all types referenced by those types.
     */

    final Set<TTypeNameFlat> extra_types = new HashSet<TTypeNameFlat>();
    for (final TTypeNameFlat t : types) {
      assert t != null;
      Referenced.collectTypesForType(compilation, extra_types, t, log_actual);
    }
    types.addAll(extra_types);
    return new Referenced(shader_name, terms, types);
  }

  private final TASTShaderNameFlat    shader_name;
  private final Set<TASTTermNameFlat> terms;
  private final Set<TTypeNameFlat>    types;

  private Referenced(
    final TASTShaderNameFlat in_shader_name,
    final Set<TASTTermNameFlat> in_terms,
    final Set<TTypeNameFlat> in_types)
  {
    this.shader_name = in_shader_name;
    this.terms = in_terms;
    this.types = in_types;
  }

  /**
   * @return The name of the shader referenced
   */

  public TASTShaderNameFlat getShaderName()
  {
    return this.shader_name;
  }

  /**
   * @return The set of terms referenced by the shader
   */

  public Set<TASTTermNameFlat> getTerms()
  {
    final Set<TASTTermNameFlat> r = Collections.unmodifiableSet(this.terms);
    assert r != null;
    return r;
  }

  /**
   * @return The set of types referenced by the shader
   */

  public Set<TTypeNameFlat> getTypes()
  {
    final Set<TTypeNameFlat> r = Collections.unmodifiableSet(this.types);
    assert r != null;
    return r;
  }
}
