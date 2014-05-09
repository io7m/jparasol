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

package com.io7m.jparasol.typed.ast;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jparasol.ModulePath;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.typed.TTypeNameFlat;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDModule;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShader;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDTerm;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDType;

/**
 * The final typed AST produced by the compiler pipeline.
 */

@EqualityReference public final class TASTCompilation
{
  private final List<ModulePathFlat>                                            module_topology;
  private final Map<ModulePathFlat, TASTDModule>                                modules;
  private final Map<ModulePathFlat, ModulePath>                                 paths;
  private final DirectedAcyclicGraph<TASTNameTermShaderFlatType, TASTReference> shader_term_graph;
  private final DirectedAcyclicGraph<TASTNameTypeShaderFlatType, TASTReference> shader_type_graph;
  private final DirectedAcyclicGraph<TASTTermNameFlat, TASTReference>           term_graph;
  private final DirectedAcyclicGraph<TASTNameTypeTermFlatType, TASTReference>   term_type_graph;
  private final DirectedAcyclicGraph<TTypeNameFlat, TASTReference>              type_graph;

  /**
   * Construct a new typed AST.
   * 
   * @param in_module_topology
   *          The module topology
   * @param in_modules
   *          The map of modules, by name
   * @param in_paths
   *          The map of flat module paths to module paths
   * @param in_term_graph
   *          The term graph
   * @param in_term_type_graph
   *          The term → type dependency graph
   * @param in_type_graph
   *          The type graph
   * @param in_shader_type_graph
   *          The shader → type dependency graph
   * @param in_shader_term_graph
   *          The shader → term dependency graph
   */

  public TASTCompilation(
    final List<ModulePathFlat> in_module_topology,
    final Map<ModulePathFlat, TASTDModule> in_modules,
    final Map<ModulePathFlat, ModulePath> in_paths,
    final DirectedAcyclicGraph<TASTTermNameFlat, TASTReference> in_term_graph,
    final DirectedAcyclicGraph<TASTNameTypeTermFlatType, TASTReference> in_term_type_graph,
    final DirectedAcyclicGraph<TTypeNameFlat, TASTReference> in_type_graph,
    final DirectedAcyclicGraph<TASTNameTypeShaderFlatType, TASTReference> in_shader_type_graph,
    final DirectedAcyclicGraph<TASTNameTermShaderFlatType, TASTReference> in_shader_term_graph)
  {
    this.module_topology =
      NullCheck.notNull(in_module_topology, "Module topology");
    this.modules = NullCheck.notNull(in_modules, "Modules");
    this.paths = NullCheck.notNull(in_paths, "Paths");

    this.term_graph = NullCheck.notNull(in_term_graph, "Term graph");
    this.type_graph = NullCheck.notNull(in_type_graph, "Type graph");
    this.term_type_graph =
      NullCheck.notNull(in_term_type_graph, "Term/type graph");
    this.shader_term_graph =
      NullCheck.notNull(in_shader_term_graph, "Shader/Term graph");
    this.shader_type_graph =
      NullCheck.notNull(in_shader_type_graph, "Shader/Type graph");
  }

  /**
   * @return The module map
   */

  public Map<ModulePathFlat, TASTDModule> getModules()
  {
    final Map<ModulePathFlat, TASTDModule> r =
      Collections.unmodifiableMap(this.modules);
    assert r != null;
    return r;
  }

  /**
   * @return The list of flat module paths
   */

  public List<ModulePathFlat> getModuleTopology()
  {
    return this.module_topology;
  }

  /**
   * @return The map of flat paths to module paths
   */

  public Map<ModulePathFlat, ModulePath> getPaths()
  {
    final Map<ModulePathFlat, ModulePath> r =
      Collections.unmodifiableMap(this.paths);
    assert r != null;
    return r;
  }

  /**
   * @return The graph of shader → term dependencies
   */

  public
    DirectedAcyclicGraph<TASTNameTermShaderFlatType, TASTReference>
    getShaderTermGraph()
  {
    return this.shader_term_graph;
  }

  /**
   * @return The graph of shader → type dependencies
   */

  public
    DirectedAcyclicGraph<TASTNameTypeShaderFlatType, TASTReference>
    getShaderTypeGraph()
  {
    return this.shader_type_graph;
  }

  /**
   * @return The graph of term → term dependencies
   */

  public DirectedAcyclicGraph<TASTTermNameFlat, TASTReference> getTermGraph()
  {
    return this.term_graph;
  }

  /**
   * @return The graph of term → type dependencies
   */

  public
    DirectedAcyclicGraph<TASTNameTypeTermFlatType, TASTReference>
    getTermTypeGraph()
  {
    return this.term_type_graph;
  }

  /**
   * @return The graph of type → type dependencies
   */

  public DirectedAcyclicGraph<TTypeNameFlat, TASTReference> getTypeGraph()
  {
    return this.type_graph;
  }

  /**
   * @return The shader associated with the given name, if any
   */

  public @Nullable TASTDShader lookupShader(
    final TASTShaderNameFlat name)
  {
    final TASTDModule m = this.modules.get(name.getModulePath());
    if (m != null) {
      final TASTDShader s = m.getShaders().get(name.getName());
      if (s != null) {
        return s;
      }
    }
    return null;
  }

  /**
   * @return The term associated with the given name, if any
   */

  public @Nullable TASTDTerm lookupTerm(
    final TASTTermNameFlat name)
  {
    final TASTDModule m = this.modules.get(name.getModulePath());
    if (m != null) {
      return m.getTerms().get(name.getName());
    }
    return null;
  }

  /**
   * @return The type associated with the given name, if any
   */

  public @Nullable TASTDType lookupType(
    final TTypeNameFlat name)
  {
    final TASTDModule m = this.modules.get(name.getModulePath());
    if (m != null) {
      return m.getTypes().get(name.getName());
    }
    return null;
  }
}
