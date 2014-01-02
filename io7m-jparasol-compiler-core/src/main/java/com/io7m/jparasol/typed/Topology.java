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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jlog.Level;
import com.io7m.jlog.Log;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.typed.ast.TASTCompilation;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDModule;
import com.io7m.jparasol.typed.ast.TASTReference;
import com.io7m.jparasol.typed.ast.TASTShaderNameFlat;
import com.io7m.jparasol.typed.ast.TASTTermNameFlat;

/**
 * <p>
 * Given a compilation, a shader, and the set of all referenced terms and
 * types for that shader, produce lists of terms and types in topological
 * order for translation to a flat order-dependent namespace like that of
 * GLSL.
 * </p>
 * <p>
 * Specifically, if a term <code>t₀</code> refers to a term <code>t₁</code>,
 * then <code>t₁</code> will appear before <code>t₀</code> in the resulting
 * list of terms.
 * </p>
 */

public final class Topology
{
  public static @Nonnull Topology fromShader(
    final @Nonnull TASTCompilation compilation,
    final @Nonnull Referenced referenced,
    final @Nonnull TASTShaderNameFlat shader_name,
    final @Nonnull Log log)
    throws ConstraintError
  {
    Constraints.constrainNotNull(compilation, "Compilation");
    Constraints.constrainNotNull(referenced, "Referenced");
    Constraints.constrainNotNull(shader_name, "Shader name");
    Constraints.constrainNotNull(log, "Log");
    Constraints.constrainArbitrary(
      referenced.getShaderName().equals(shader_name),
      "References produced for shader");

    final Map<ModulePathFlat, TASTDModule> modules = compilation.getModules();
    Constraints.constrainArbitrary(
      modules.containsKey(shader_name.getModulePath()),
      "Module exists");
    final TASTDModule m = modules.get(shader_name.getModulePath());
    Constraints.constrainArbitrary(
      m.getShaders().containsKey(shader_name.getName()),
      "Shader exists");

    final Log log_actual = new Log(log, "topology");

    final LinkedList<TTypeNameFlat> types = new LinkedList<TTypeNameFlat>();
    final Set<TTypeNameFlat> referenced_types = referenced.getTypes();

    {
      final DirectedAcyclicGraph<TTypeNameFlat, TASTReference> type_graph =
        compilation.getTypeGraph();
      final TopologicalOrderIterator<TTypeNameFlat, TASTReference> type_iter =
        new TopologicalOrderIterator<TTypeNameFlat, TASTReference>(type_graph);

      while (type_iter.hasNext()) {
        final TTypeNameFlat type_current = type_iter.next();
        if (referenced_types.contains(type_current)) {
          if (log_actual.enabled(Level.LOG_DEBUG)) {
            log_actual.debug(String.format(
              "Adding type %s",
              type_current.show()));
          }
          types.addFirst(type_current);
        }
      }
      assert types.size() == referenced_types.size();
    }

    final LinkedList<TASTTermNameFlat> terms =
      new LinkedList<TASTTermNameFlat>();
    final Set<TASTTermNameFlat> referenced_terms = referenced.getTerms();

    {
      final DirectedAcyclicGraph<TASTTermNameFlat, TASTReference> term_graph =
        compilation.getTermGraph();
      final TopologicalOrderIterator<TASTTermNameFlat, TASTReference> term_iter =
        new TopologicalOrderIterator<TASTTermNameFlat, TASTReference>(
          term_graph);

      while (term_iter.hasNext()) {
        final TASTTermNameFlat term_current = term_iter.next();
        if (referenced_terms.contains(term_current)) {
          if (log_actual.enabled(Level.LOG_DEBUG)) {
            log_actual.debug(String.format(
              "Adding term %s",
              term_current.show()));
          }
          terms.addFirst(term_current);
        }
      }

      assert terms.size() == referenced_terms.size();
    }

    return new Topology(shader_name, terms, types);
  }

  private final @Nonnull TASTShaderNameFlat     shader_name;
  private final @Nonnull List<TASTTermNameFlat> terms;
  private final @Nonnull List<TTypeNameFlat>    types;

  private Topology(
    final @Nonnull TASTShaderNameFlat shader_name,
    final @Nonnull List<TASTTermNameFlat> terms,
    final @Nonnull List<TTypeNameFlat> types)
  {
    this.shader_name = shader_name;
    this.terms = terms;
    this.types = types;
  }

  public @Nonnull TASTShaderNameFlat getShaderName()
  {
    return this.shader_name;
  }

  public @Nonnull List<TASTTermNameFlat> getTerms()
  {
    return Collections.unmodifiableList(this.terms);
  }

  public @Nonnull List<TTypeNameFlat> getTypes()
  {
    return Collections.unmodifiableList(this.types);
  }
}
