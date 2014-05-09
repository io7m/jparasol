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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jlog.LogLevel;
import com.io7m.jlog.LogUsableType;
import com.io7m.jnull.NullCheck;
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

@EqualityReference public final class Topology
{
  /**
   * Calculate a topologically sorted set of terms and types for the given
   * shader.
   * 
   * @param compilation
   *          The AST
   * @param referenced
   *          The referenced terms and types
   * @param shader_name
   *          The name of the shader
   * @param log
   *          A log interface
   * @return A set of terms and types
   */

  public static Topology fromShader(
    final TASTCompilation compilation,
    final Referenced referenced,
    final TASTShaderNameFlat shader_name,
    final LogUsableType log)
  {
    NullCheck.notNull(compilation, "Compilation");
    NullCheck.notNull(referenced, "Referenced");
    NullCheck.notNull(shader_name, "Shader name");
    NullCheck.notNull(log, "Log");

    if (referenced.getShaderName().equals(shader_name) == false) {
      final StringBuilder m = new StringBuilder();
      m.append("References not produced for shader.\n");
      m.append("  References: ");
      m.append(referenced.getShaderName());
      m.append("\n");
      m.append("  Shader: ");
      m.append(shader_name);
      m.append("\n");
      throw new IllegalArgumentException(m.toString());
    }

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

    final LogUsableType log_actual = log.with("topology");
    // CHECKSTYLE:OFF
    final LinkedList<TTypeNameFlat> types = new LinkedList<TTypeNameFlat>();
    // CHECKSTYLE:ON
    final Set<TTypeNameFlat> referenced_types = referenced.getTypes();

    {
      final DirectedAcyclicGraph<TTypeNameFlat, TASTReference> type_graph =
        compilation.getTypeGraph();
      final TopologicalOrderIterator<TTypeNameFlat, TASTReference> type_iter =
        new TopologicalOrderIterator<TTypeNameFlat, TASTReference>(type_graph);

      while (type_iter.hasNext()) {
        final TTypeNameFlat type_current = type_iter.next();
        if (referenced_types.contains(type_current)) {
          if (log_actual.wouldLog(LogLevel.LOG_DEBUG)) {
            final String r =
              String.format("Adding type %s", type_current.show());
            assert r != null;
            log_actual.debug(r);
          }
          types.addFirst(type_current);
        }
      }
      assert types.size() == referenced_types.size();
    }

    // CHECKSTYLE:OFF
    final LinkedList<TASTTermNameFlat> terms =
      new LinkedList<TASTTermNameFlat>();
    // CHECKSTYLE:ON
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
          if (log_actual.wouldLog(LogLevel.LOG_DEBUG)) {
            final String r =
              String.format("Adding term %s", term_current.show());
            assert r != null;
            log_actual.debug(r);
          }
          terms.addFirst(term_current);
        }
      }

      assert terms.size() == referenced_terms.size();
    }

    return new Topology(shader_name, terms, types);
  }

  private final TASTShaderNameFlat     shader_name;
  private final List<TASTTermNameFlat> terms;
  private final List<TTypeNameFlat>    types;

  private Topology(
    final TASTShaderNameFlat in_shader_name,
    final List<TASTTermNameFlat> in_terms,
    final List<TTypeNameFlat> in_types)
  {
    this.shader_name = in_shader_name;
    this.terms = in_terms;
    this.types = in_types;
  }

  /**
   * @return The name of the shader
   */

  public TASTShaderNameFlat getShaderName()
  {
    return this.shader_name;
  }

  /**
   * @return The sorted list of terms
   */

  public List<TASTTermNameFlat> getTerms()
  {
    final List<TASTTermNameFlat> r = Collections.unmodifiableList(this.terms);
    assert r != null;
    return r;
  }

  /**
   * @return The sorted list of types
   */

  public List<TTypeNameFlat> getTypes()
  {
    final List<TTypeNameFlat> r = Collections.unmodifiableList(this.types);
    assert r != null;
    return r;
  }
}
