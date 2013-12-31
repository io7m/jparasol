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

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Unit;
import com.io7m.jlog.Level;
import com.io7m.jlog.Log;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.typed.ast.TASTCompilation;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDModule;
import com.io7m.jparasol.typed.ast.TASTNameTermShaderFlat;
import com.io7m.jparasol.typed.ast.TASTNameTypeShaderFlat;
import com.io7m.jparasol.typed.ast.TASTNameTypeShaderFlatVisitor;
import com.io7m.jparasol.typed.ast.TASTNameTypeTermFlat;
import com.io7m.jparasol.typed.ast.TASTNameTypeTermFlat.Term;
import com.io7m.jparasol.typed.ast.TASTNameTypeTermFlat.Type;
import com.io7m.jparasol.typed.ast.TASTNameTypeTermFlatVisitor;
import com.io7m.jparasol.typed.ast.TASTReference;
import com.io7m.jparasol.typed.ast.TASTShaderNameFlat;
import com.io7m.jparasol.typed.ast.TASTTermNameFlat;

/**
 * Determine all of the referenced terms and types for a given shader.
 */

public final class Referenced
{
  private static void collectTerms(
    final @Nonnull TASTCompilation compilation,
    final @Nonnull TASTShaderNameFlat shader_name,
    final @Nonnull Log log_actual,
    final @Nonnull Set<TASTTermNameFlat> terms)
    throws ConstraintError
  {
    final TASTNameTermShaderFlat shader_term_name =
      new TASTNameTermShaderFlat.Shader(shader_name);

    final DirectedAcyclicGraph<TASTNameTermShaderFlat, TASTReference> shader_term_graph =
      compilation.getShaderTermGraph();
    final DirectedAcyclicGraph<TASTTermNameFlat, TASTReference> term_term_graph =
      compilation.getTermGraph();

    for (final TASTReference d : shader_term_graph
      .outgoingEdgesOf(shader_term_name)) {
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
        if (log_actual.enabled(Level.LOG_DEBUG)) {
          log_actual.debug(String.format("Adding term %s.%s", current
            .getPath()
            .getActual(), current.getName()));
        }
        terms.add(new TASTTermNameFlat(current.getPath(), current.getName()));
      }
    }
  }

  private static void collectTypes(
    final @Nonnull TASTCompilation compilation,
    final @Nonnull Set<TTypeNameFlat> types,
    final @Nonnull TASTShaderNameFlat shader_name,
    final @Nonnull Log log)
    throws ConstraintError
  {
    final TASTNameTypeShaderFlat shader_type_name =
      new TASTNameTypeShaderFlat.Shader(shader_name);

    final DirectedAcyclicGraph<TASTNameTypeShaderFlat, TASTReference> shader_type =
      compilation.getShaderTypeGraph();

    final BreadthFirstIterator<TASTNameTypeShaderFlat, TASTReference> bfi =
      new BreadthFirstIterator<TASTNameTypeShaderFlat, TASTReference>(
        shader_type,
        shader_type_name);

    while (bfi.hasNext()) {
      final TASTNameTypeShaderFlat current = bfi.next();
      current
        .nameTypeShaderVisitableAccept(new TASTNameTypeShaderFlatVisitor<Unit, ConstraintError>() {
          @Override public Unit nameTypeShaderVisitShader(
            final TASTNameTypeShaderFlat.Shader t)
            throws ConstraintError
          {
            return Unit.unit();
          }

          @Override public Unit nameTypeShaderVisitType(
            final TASTNameTypeShaderFlat.Type t)
            throws ConstraintError
          {
            if (log.enabled(Level.LOG_DEBUG)) {
              log.debug(String.format("Adding type %s", t.show()));
            }
            types.add(t.getName());
            return Unit.unit();
          }
        });
    }
  }

  private static void collectTypesForTerm(
    final @Nonnull TASTCompilation compilation,
    final @Nonnull Set<TTypeNameFlat> types,
    final @Nonnull TASTTermNameFlat t,
    final @Nonnull Log log)
    throws ConstraintError
  {
    final DirectedAcyclicGraph<TASTNameTypeTermFlat, TASTReference> term_type =
      compilation.getTermTypeGraph();

    final TASTNameTypeTermFlat.Term term = new TASTNameTypeTermFlat.Term(t);
    final BreadthFirstIterator<TASTNameTypeTermFlat, TASTReference> bfi =
      new BreadthFirstIterator<TASTNameTypeTermFlat, TASTReference>(
        term_type,
        term);

    while (bfi.hasNext()) {
      final TASTNameTypeTermFlat current = bfi.next();
      current
        .nameTypeTermVisitableAccept(new TASTNameTypeTermFlatVisitor<Unit, ConstraintError>() {
          @Override public Unit nameTypeTermVisitTerm(
            final @Nonnull Term _)
            throws ConstraintError
          {
            return Unit.unit();
          }

          @Override public Unit nameTypeTermVisitType(
            final @Nonnull Type type)
            throws ConstraintError
          {
            if (log.enabled(Level.LOG_DEBUG)) {
              log.debug(String.format("Adding type %s", type.show()));
            }
            types.add(type.getName());
            return Unit.unit();
          }
        });
    }
  }

  public static @Nonnull Referenced fromShader(
    final @Nonnull TASTCompilation compilation,
    final @Nonnull TASTShaderNameFlat shader_name,
    final @Nonnull Log log)
    throws ConstraintError
  {
    final Log log_actual = new Log(log, "referenced");

    final Map<ModulePathFlat, TASTDModule> modules = compilation.getModules();
    Constraints.constrainArbitrary(
      modules.containsKey(shader_name.getPath()),
      "Module exists");
    final TASTDModule m = modules.get(shader_name.getPath());
    Constraints.constrainArbitrary(
      m.getShaders().containsKey(shader_name.getName()),
      "Shader exists");

    final Set<TASTTermNameFlat> terms = new HashSet<TASTTermNameFlat>();
    Referenced.collectTerms(compilation, shader_name, log_actual, terms);

    final Set<TTypeNameFlat> types = new HashSet<TTypeNameFlat>();
    for (final TASTTermNameFlat t : terms) {
      Referenced.collectTypesForTerm(compilation, types, t, log_actual);
    }

    Referenced.collectTypes(compilation, types, shader_name, log_actual);
    return new Referenced(terms, types, log_actual);
  }

  private final @Nonnull Log                   log;
  private final @Nonnull Set<TASTTermNameFlat> terms;
  private final @Nonnull Set<TTypeNameFlat>    types;

  private Referenced(
    final @Nonnull Set<TASTTermNameFlat> terms,
    final @Nonnull Set<TTypeNameFlat> types,
    final @Nonnull Log log)
  {
    this.log = log;
    this.terms = terms;
    this.types = types;
  }

  public @Nonnull Set<TASTTermNameFlat> getTerms()
  {
    return Collections.unmodifiableSet(this.terms);
  }

  public @Nonnull Set<TTypeNameFlat> getTypes()
  {
    return Collections.unmodifiableSet(this.types);
  }
}
