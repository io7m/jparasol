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

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph.CycleFoundException;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jaux.functional.Unit;
import com.io7m.jlog.Level;
import com.io7m.jlog.Log;
import com.io7m.jparasol.ModulePath;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.typed.TType.TManifestType;
import com.io7m.jparasol.typed.TTypeName.TTypeNameBuiltIn;
import com.io7m.jparasol.typed.TTypeName.TTypeNameGlobal;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDFunctionArgument;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDFunctionDefined;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDFunctionExternal;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDModule;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShader;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragment;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragmentInput;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragmentLocal;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragmentLocalDiscard;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragmentLocalValue;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragmentOutput;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragmentOutputAssignment;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragmentParameter;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderProgram;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderVertex;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderVertexInput;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderVertexLocalValue;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderVertexOutput;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderVertexOutputAssignment;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderVertexParameter;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDTerm;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDType;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDTypeRecord;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDTypeRecordField;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDValueDefined;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDValueExternal;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDValueLocal;
import com.io7m.jparasol.typed.ast.TASTExpression;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTEApplication;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTEBoolean;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTEConditional;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTEInteger;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTELet;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTENew;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTEReal;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTERecord;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTERecordProjection;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTESwizzle;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTEVariable;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTRecordFieldAssignment;
import com.io7m.jparasol.typed.ast.TASTExpressionVisitor;
import com.io7m.jparasol.typed.ast.TASTFragmentShaderLocalVisitor;
import com.io7m.jparasol.typed.ast.TASTFragmentShaderVisitor;
import com.io7m.jparasol.typed.ast.TASTLocalLevelVisitor;
import com.io7m.jparasol.typed.ast.TASTNameTermShaderFlat;
import com.io7m.jparasol.typed.ast.TASTNameTypeShaderFlat;
import com.io7m.jparasol.typed.ast.TASTNameTypeTermFlat;
import com.io7m.jparasol.typed.ast.TASTReference;
import com.io7m.jparasol.typed.ast.TASTShaderName;
import com.io7m.jparasol.typed.ast.TASTShaderNameFlat;
import com.io7m.jparasol.typed.ast.TASTShaderVisitor;
import com.io7m.jparasol.typed.ast.TASTTermName;
import com.io7m.jparasol.typed.ast.TASTTermName.TASTTermNameExternal;
import com.io7m.jparasol.typed.ast.TASTTermName.TASTTermNameGlobal;
import com.io7m.jparasol.typed.ast.TASTTermName.TASTTermNameLocal;
import com.io7m.jparasol.typed.ast.TASTTermNameFlat;
import com.io7m.jparasol.typed.ast.TASTTermNameVisitor;
import com.io7m.jparasol.typed.ast.TASTTermVisitor;
import com.io7m.jparasol.typed.ast.TASTTypeVisitor;
import com.io7m.jparasol.typed.ast.TASTVertexShaderLocalVisitor;
import com.io7m.jparasol.typed.ast.TASTVertexShaderVisitor;

final class TGraphs
{
  /**
   * Graphs of term->term, term->type, and type->type dependencies.
   */

  static final class GlobalGraph
  {
    private final @Nonnull GlobalTermShaderGraph term_shader;
    private final @Nonnull GlobalTermTermGraph   term_term;
    private final @Nonnull GlobalTermTypeGraph   term_type;
    private final @Nonnull GlobalTypeShaderGraph type_shader;
    private final @Nonnull GlobalTypeTypeGraph   type_type;

    public GlobalGraph(
      final @Nonnull Log log)
    {
      this.term_term = new GlobalTermTermGraph(log);
      this.term_type = new GlobalTermTypeGraph(log);
      this.type_type = new GlobalTypeTypeGraph(log);
      this.term_shader = new GlobalTermShaderGraph(log);
      this.type_shader = new GlobalTypeShaderGraph(log);
    }

    public void addShader(
      final @Nonnull TASTShaderName shader)
      throws ConstraintError
    {
      final TASTShaderNameFlat flat =
        TASTShaderNameFlat.fromShaderName(shader);
      this.type_shader.addShader(flat);
      this.term_shader.addShader(flat);
    }

    public void addShaderTermReference(
      final @Nonnull TASTShaderName shader,
      final @Nonnull TASTTermNameGlobal term)
      throws ConstraintError
    {
      this.addTerm(term);
      this.addShader(shader);
      this.term_shader.addTermShaderReference(shader, term);
    }

    public void addShaderTypeReference(
      final @Nonnull TASTShaderName shader,
      final @Nonnull TTypeNameGlobal type)
      throws ConstraintError
    {
      this.addType(type);
      this.addShader(shader);
      this.type_shader.addTypeShaderReference(shader, type);
    }

    public void addTerm(
      final @Nonnull TASTTermNameGlobal term)
      throws ConstraintError
    {
      final TASTTermNameFlat flat = TASTTermNameFlat.fromTermNameGlobal(term);
      this.term_term.addTerm(flat);
      this.term_type.addTerm(flat);
      this.term_shader.addTerm(flat);
    }

    public void addTermTermReference(
      final @Nonnull TASTTermNameGlobal source,
      final @Nonnull TASTTermNameGlobal target)
      throws ConstraintError
    {
      this.addTerm(source);
      this.addTerm(target);
      this.term_term.addTermReference(source, target);
    }

    public void addTermTypeReference(
      final @Nonnull TASTTermNameGlobal source,
      final @Nonnull TTypeNameGlobal target)
      throws ConstraintError
    {
      this.addTerm(source);
      this.term_type.addTermTypeReference(source, target);
    }

    public void addType(
      final @Nonnull TTypeNameGlobal type)
      throws ConstraintError
    {
      final TTypeNameFlat flat = TTypeNameFlat.fromTypeNameGlobal(type);
      this.term_type.addType(flat);
      this.type_type.addType(flat);
      this.type_shader.addType(flat);
    }

    public void addTypeTypeReference(
      final @Nonnull TTypeNameGlobal source,
      final @Nonnull TTypeNameGlobal target)
      throws ConstraintError
    {
      this.addType(source);
      this.addType(target);
      this.type_type.addTypeReference(source, target);
    }

    public @Nonnull GlobalTermShaderGraph getTermShader()
    {
      return this.term_shader;
    }

    public @Nonnull GlobalTermTermGraph getTermTermGraph()
    {
      return this.term_term;
    }

    public @Nonnull GlobalTermTypeGraph getTermTypeGraph()
    {
      return this.term_type;
    }

    public @Nonnull GlobalTypeShaderGraph getTypeShader()
    {
      return this.type_shader;
    }

    public @Nonnull GlobalTypeTypeGraph getTypeTypeGraph()
    {
      return this.type_type;
    }
  }

  /**
   * A directed acyclic graph of references to terms by shaders over the
   * entire compilation.
   */

  static final class GlobalTermShaderGraph
  {
    private final @Nonnull DirectedAcyclicGraph<TASTNameTermShaderFlat, TASTReference> graph;
    private final @Nonnull Log                                                         log;

    public GlobalTermShaderGraph(
      final @Nonnull Log in_log)
    {
      this.log = new Log(in_log, "global-shader-term-graph");
      this.graph =
        new DirectedAcyclicGraph<TASTNameTermShaderFlat, TASTReference>(
          TASTReference.class);
    }

    public void addShader(
      final @Nonnull TASTShaderNameFlat flat)
      throws ConstraintError
    {
      final TASTNameTermShaderFlat.Shader e_term =
        new TASTNameTermShaderFlat.Shader(flat);
      if (this.graph.containsVertex(e_term) == false) {
        if (this.log.enabled(Level.LOG_DEBUG)) {
          this.log.debug(String.format("Add shader %s", flat.show()));
        }
        this.graph.addVertex(e_term);
      }
    }

    public void addTerm(
      final @Nonnull TASTTermNameFlat term)
      throws ConstraintError
    {
      final TASTNameTermShaderFlat.Term e_term =
        new TASTNameTermShaderFlat.Term(term);
      if (this.graph.containsVertex(e_term) == false) {
        if (this.log.enabled(Level.LOG_DEBUG)) {
          this.log.debug(String.format("Add term %s", term.show()));
        }
        this.graph.addVertex(e_term);
      }
    }

    public void addTermShaderReference(
      final @Nonnull TASTShaderName source,
      final @Nonnull TASTTermNameGlobal target)
      throws ConstraintError
    {
      final TASTShaderNameFlat source_flat =
        TASTShaderNameFlat.fromShaderName(source);
      final TASTTermNameFlat target_flat =
        TASTTermNameFlat.fromTermNameGlobal(target);

      final TASTNameTermShaderFlat.Shader e_source =
        new TASTNameTermShaderFlat.Shader(source_flat);
      final TASTNameTermShaderFlat.Term e_target =
        new TASTNameTermShaderFlat.Term(target_flat);
      final TASTReference reference =
        new TASTReference(
          source.getPath(),
          source.getName(),
          target.getPath(),
          target.getName());

      if (this.graph.containsEdge(reference) == false) {
        if (this.log.enabled(Level.LOG_DEBUG)) {
          this.log.debug(String.format(
            "Add shader term reference %s.%s -> %s.%s",
            source.getFlat().getActual(),
            source.getName().getActual(),
            target.getFlat().getActual(),
            target.getName().getActual()));
        }

        this.addShader(source_flat);
        this.addTerm(target_flat);

        try {
          this.graph.addDagEdge(e_source, e_target, reference);
        } catch (final CycleFoundException e) {
          throw new UnreachableCodeException(e);
        }
      }
    }

    public @Nonnull
      DirectedAcyclicGraph<TASTNameTermShaderFlat, TASTReference>
      getGraph()
    {
      return this.graph;
    }
  }

  /**
   * A directed acyclic graph of references to terms by terms over the entire
   * compilation.
   */

  static final class GlobalTermTermGraph
  {
    private final @Nonnull DirectedAcyclicGraph<TASTTermNameFlat, TASTReference> graph;
    private final @Nonnull Log                                                   log;

    public GlobalTermTermGraph(
      final @Nonnull Log in_log)
    {
      this.log = new Log(in_log, "global-term-term-graph");
      this.graph =
        new DirectedAcyclicGraph<TASTTermNameFlat, TASTReference>(
          TASTReference.class);
    }

    public void addTerm(
      final @Nonnull TASTTermNameFlat term)
    {
      if (this.graph.containsVertex(term) == false) {
        if (this.log.enabled(Level.LOG_DEBUG)) {
          this.log.debug(String.format("Add term %s", term.show()));
        }
        this.graph.addVertex(term);
      }
    }

    public void addTermReference(
      final @Nonnull TASTTermNameGlobal source,
      final @Nonnull TASTTermNameGlobal target)
      throws ConstraintError
    {
      final TASTReference reference =
        new TASTReference(
          source.getPath(),
          source.getName(),
          target.getPath(),
          target.getName());

      if (this.graph.containsEdge(reference) == false) {
        if (this.log.enabled(Level.LOG_DEBUG)) {
          this.log.debug(String.format(
            "Add term reference %s.%s -> %s.%s",
            source.getFlat().getActual(),
            source.getName().getActual(),
            target.getFlat().getActual(),
            target.getName().getActual()));
        }

        final TASTTermNameFlat source_flat =
          TASTTermNameFlat.fromTermNameGlobal(source);
        final TASTTermNameFlat target_flat =
          TASTTermNameFlat.fromTermNameGlobal(target);

        this.addTerm(source_flat);
        this.addTerm(target_flat);

        try {
          this.graph.addDagEdge(source_flat, target_flat, reference);
        } catch (final CycleFoundException e) {
          throw new UnreachableCodeException(e);
        }
      }
    }

    public @Nonnull
      DirectedAcyclicGraph<TASTTermNameFlat, TASTReference>
      getGraph()
    {
      return this.graph;
    }
  }

  /**
   * A directed acyclic graph of references to types by terms over the entire
   * compilation.
   */

  static final class GlobalTermTypeGraph
  {
    private final @Nonnull DirectedAcyclicGraph<TASTNameTypeTermFlat, TASTReference> graph;
    private final @Nonnull Log                                                       log;

    public GlobalTermTypeGraph(
      final @Nonnull Log in_log)
    {
      this.log = new Log(in_log, "global-term-type-graph");
      this.graph =
        new DirectedAcyclicGraph<TASTNameTypeTermFlat, TASTReference>(
          TASTReference.class);
    }

    public void addTerm(
      final @Nonnull TASTTermNameFlat flat)
      throws ConstraintError
    {
      final TASTNameTypeTermFlat.Term e_term =
        new TASTNameTypeTermFlat.Term(flat);
      if (this.graph.containsVertex(e_term) == false) {
        if (this.log.enabled(Level.LOG_DEBUG)) {
          this.log.debug(String.format("Add term %s", flat.show()));
        }
        this.graph.addVertex(e_term);
      }
    }

    public void addTermTypeReference(
      final @Nonnull TASTTermNameGlobal source,
      final @Nonnull TTypeNameGlobal target)
      throws ConstraintError
    {
      final TASTTermNameFlat source_flat =
        TASTTermNameFlat.fromTermNameGlobal(source);
      final TTypeNameFlat target_flat =
        TTypeNameFlat.fromTypeNameGlobal(target);

      final TASTNameTypeTermFlat.Term e_source =
        new TASTNameTypeTermFlat.Term(source_flat);
      final TASTNameTypeTermFlat.Type e_target =
        new TASTNameTypeTermFlat.Type(target_flat);
      final TASTReference reference =
        new TASTReference(
          source.getPath(),
          source.getName(),
          target.getPath(),
          target.getName());

      if (this.graph.containsEdge(reference) == false) {
        if (this.log.enabled(Level.LOG_DEBUG)) {
          this.log.debug(String.format(
            "Add term type reference %s.%s -> %s.%s",
            source.getFlat().getActual(),
            source.getName().getActual(),
            target.getFlat().getActual(),
            target.getName().getActual()));
        }

        this.addTerm(source_flat);
        this.addType(target_flat);

        try {
          this.graph.addDagEdge(e_source, e_target, reference);
        } catch (final CycleFoundException e) {
          throw new UnreachableCodeException(e);
        }
      }
    }

    public void addType(
      final @Nonnull TTypeNameFlat type)
      throws ConstraintError
    {
      final TASTNameTypeTermFlat.Type e_type =
        new TASTNameTypeTermFlat.Type(type);
      if (this.graph.containsVertex(e_type) == false) {
        if (this.log.enabled(Level.LOG_DEBUG)) {
          this.log.debug(String.format("Add type %s", type.show()));
        }
        this.graph.addVertex(e_type);
      }
    }

    public @Nonnull
      DirectedAcyclicGraph<TASTNameTypeTermFlat, TASTReference>
      getGraph()
    {
      return this.graph;
    }
  }

  /**
   * A directed acyclic graph of references to types by shaders over the
   * entire compilation.
   */

  static final class GlobalTypeShaderGraph
  {
    private final @Nonnull DirectedAcyclicGraph<TASTNameTypeShaderFlat, TASTReference> graph;
    private final @Nonnull Log                                                         log;

    public GlobalTypeShaderGraph(
      final @Nonnull Log in_log)
    {
      this.log = new Log(in_log, "global-shader-type-graph");
      this.graph =
        new DirectedAcyclicGraph<TASTNameTypeShaderFlat, TASTReference>(
          TASTReference.class);
    }

    public void addShader(
      final @Nonnull TASTShaderNameFlat flat)
      throws ConstraintError
    {
      final TASTNameTypeShaderFlat.Shader e_term =
        new TASTNameTypeShaderFlat.Shader(flat);
      if (this.graph.containsVertex(e_term) == false) {
        if (this.log.enabled(Level.LOG_DEBUG)) {
          this.log.debug(String.format("Add shader %s", flat.show()));
        }
        this.graph.addVertex(e_term);
      }
    }

    public void addType(
      final @Nonnull TTypeNameFlat type)
      throws ConstraintError
    {
      final TASTNameTypeShaderFlat.Type e_type =
        new TASTNameTypeShaderFlat.Type(type);
      if (this.graph.containsVertex(e_type) == false) {
        if (this.log.enabled(Level.LOG_DEBUG)) {
          this.log.debug(String.format("Add type %s", type.show()));
        }
        this.graph.addVertex(e_type);
      }
    }

    public void addTypeShaderReference(
      final @Nonnull TASTShaderName source,
      final @Nonnull TTypeNameGlobal target)
      throws ConstraintError
    {
      final TASTShaderNameFlat source_flat =
        TASTShaderNameFlat.fromShaderName(source);
      final TTypeNameFlat target_flat =
        TTypeNameFlat.fromTypeNameGlobal(target);

      final TASTNameTypeShaderFlat.Shader e_source =
        new TASTNameTypeShaderFlat.Shader(source_flat);
      final TASTNameTypeShaderFlat.Type e_target =
        new TASTNameTypeShaderFlat.Type(target_flat);
      final TASTReference reference =
        new TASTReference(
          source.getPath(),
          source.getName(),
          target.getPath(),
          target.getName());

      if (this.graph.containsEdge(reference) == false) {
        if (this.log.enabled(Level.LOG_DEBUG)) {
          this.log.debug(String.format(
            "Add shader type reference %s.%s -> %s.%s",
            source.getFlat().getActual(),
            source.getName().getActual(),
            target.getFlat().getActual(),
            target.getName().getActual()));
        }

        this.addShader(source_flat);
        this.addType(target_flat);

        try {
          this.graph.addDagEdge(e_source, e_target, reference);
        } catch (final CycleFoundException e) {
          throw new UnreachableCodeException(e);
        }
      }
    }

    public @Nonnull
      DirectedAcyclicGraph<TASTNameTypeShaderFlat, TASTReference>
      getGraph()
    {
      return this.graph;
    }
  }

  /**
   * A directed acyclic graph of references to types by types over the entire
   * compilation.
   */

  static final class GlobalTypeTypeGraph
  {
    private final @Nonnull DirectedAcyclicGraph<TTypeNameFlat, TASTReference> graph;
    private final @Nonnull Log                                                log;

    public GlobalTypeTypeGraph(
      final @Nonnull Log in_log)
    {
      this.log = new Log(in_log, "global-type-type-graph");
      this.graph =
        new DirectedAcyclicGraph<TTypeNameFlat, TASTReference>(
          TASTReference.class);
    }

    public void addType(
      final TTypeNameFlat type)
    {
      if (this.graph.containsVertex(type) == false) {
        if (this.log.enabled(Level.LOG_DEBUG)) {
          this.log.debug(String.format("Add type %s", type.show()));
        }
        this.graph.addVertex(type);
      }
    }

    public void addTypeReference(
      final @Nonnull TTypeNameGlobal source,
      final @Nonnull TTypeNameGlobal target)
      throws ConstraintError
    {
      final TASTReference reference =
        new TASTReference(
          source.getPath(),
          source.getName(),
          target.getPath(),
          target.getName());

      if (this.graph.containsEdge(reference) == false) {
        if (this.log.enabled(Level.LOG_DEBUG)) {
          this.log.debug(String.format(
            "Add type reference %s.%s -> %s.%s",
            source.getFlat().getActual(),
            source.getName().getActual(),
            target.getFlat().getActual(),
            target.getName().getActual()));
        }

        final TTypeNameFlat source_flat =
          TTypeNameFlat.fromTypeNameGlobal(source);
        final TTypeNameFlat target_flat =
          TTypeNameFlat.fromTypeNameGlobal(target);

        this.addType(source_flat);
        this.addType(target_flat);

        try {
          this.graph.addDagEdge(source_flat, target_flat, reference);
        } catch (final CycleFoundException e) {
          throw new UnreachableCodeException(e);
        }
      }
    }

    public @Nonnull
      DirectedAcyclicGraph<TTypeNameFlat, TASTReference>
      getGraph()
    {
      return this.graph;
    }
  }

  private static final class GraphBuilderFragmentShader implements
    TASTFragmentShaderVisitor<TASTDShaderFragment, TASTDShaderFragmentInput, TASTDShaderFragmentParameter, TASTDShaderFragmentOutput, TASTDShaderFragmentLocal, TASTDShaderFragmentOutputAssignment, ConstraintError>
  {
    private final @Nonnull Map<ModulePathFlat, TASTDModule> checked_modules;
    private final @Nonnull Map<String, TASTDTerm>           checked_terms;
    private final @Nonnull Map<String, TASTDType>           checked_types;
    private final @Nonnull GlobalGraph                      graph;
    private final @Nonnull Log                              log;
    private final @Nonnull ModulePath                       module_path;
    private final @Nonnull TASTShaderName                   source;

    public GraphBuilderFragmentShader(
      final @Nonnull TASTShaderName in_source,
      final @Nonnull Map<ModulePathFlat, TASTDModule> in_checked_modules,
      final @Nonnull Map<String, TASTDTerm> in_checked_terms,
      final @Nonnull Map<String, TASTDType> in_checked_types,
      final @Nonnull Log in_log,
      final @Nonnull ModulePath in_module_path,
      final @Nonnull GlobalGraph in_graph)
    {
      this.source = in_source;
      this.checked_modules = in_checked_modules;
      this.checked_terms = in_checked_terms;
      this.checked_types = in_checked_types;
      this.log = in_log;
      this.module_path = in_module_path;
      this.graph = in_graph;
    }

    private void addTypeReference(
      final @Nonnull TType type)
      throws ConstraintError
    {
      type.getName().typeNameVisitableAccept(
        new TTypeNameVisitor<Unit, ConstraintError>() {
          @Override public Unit typeNameVisitBuiltIn(
            final @Nonnull TTypeNameBuiltIn t)
            throws ConstraintError,
              ConstraintError
          {
            return Unit.unit();
          }

          @SuppressWarnings("synthetic-access") @Override public
            Unit
            typeNameVisitGlobal(
              final @Nonnull TTypeNameGlobal t)
              throws ConstraintError,
                ConstraintError
          {
            GraphBuilderFragmentShader.this.graph.addShaderTypeReference(
              GraphBuilderFragmentShader.this.source,
              t);
            return Unit.unit();
          }
        });
    }

    @Override public
      TASTDShaderFragment
      fragmentShaderVisit(
        final @Nonnull List<TASTDShaderFragmentInput> inputs,
        final @Nonnull List<TASTDShaderFragmentParameter> parameters,
        final @Nonnull List<TASTDShaderFragmentOutput> outputs,
        final @Nonnull List<TASTDShaderFragmentLocal> locals,
        final @Nonnull List<TASTDShaderFragmentOutputAssignment> output_assignments,
        final @Nonnull TASTDShaderFragment f)
        throws ConstraintError,
          ConstraintError
    {
      return f;
    }

    @Override public TASTDShaderFragmentInput fragmentShaderVisitInput(
      final @Nonnull TASTDShaderFragmentInput i)
      throws ConstraintError,
        ConstraintError
    {
      this.addTypeReference(i.getType());
      return i;
    }

    @Override public
      TASTFragmentShaderLocalVisitor<TASTDShaderFragmentLocal, ConstraintError>
      fragmentShaderVisitLocalsPre()
        throws ConstraintError,
          ConstraintError
    {
      return new GraphBuilderFragmentShaderLocal(
        this.source,
        this.checked_modules,
        this.checked_terms,
        this.checked_types,
        this.log,
        this.module_path,
        this.graph);
    }

    @Override public TASTDShaderFragmentOutput fragmentShaderVisitOutput(
      final @Nonnull TASTDShaderFragmentOutput o)
      throws ConstraintError,
        ConstraintError
    {
      this.addTypeReference(o.getType());
      return o;
    }

    @Override public
      TASTDShaderFragmentOutputAssignment
      fragmentShaderVisitOutputAssignment(
        final @Nonnull TASTDShaderFragmentOutputAssignment a)
        throws ConstraintError,
          ConstraintError
    {
      a.getVariable().expressionVisitableAccept(
        new GraphBuilderShaderExpression(
          this.source,
          this.checked_modules,
          this.checked_terms,
          this.checked_types,
          this.log,
          this.module_path,
          this.graph));
      return a;
    }

    @Override public
      TASTDShaderFragmentParameter
      fragmentShaderVisitParameter(
        final @Nonnull TASTDShaderFragmentParameter p)
        throws ConstraintError,
          ConstraintError
    {
      this.addTypeReference(p.getType());
      return p;
    }
  }

  private static final class GraphBuilderFragmentShaderLocal implements
    TASTFragmentShaderLocalVisitor<TASTDShaderFragmentLocal, ConstraintError>
  {
    private final @Nonnull Map<ModulePathFlat, TASTDModule> checked_modules;
    private final @Nonnull Map<String, TASTDTerm>           checked_terms;
    private final @Nonnull Map<String, TASTDType>           checked_types;
    private final @Nonnull GlobalGraph                      graph;
    private final @Nonnull Log                              log;
    private final @Nonnull ModulePath                       module_path;
    private final @Nonnull TASTShaderName                   source;

    public GraphBuilderFragmentShaderLocal(
      final @Nonnull TASTShaderName in_source,
      final @Nonnull Map<ModulePathFlat, TASTDModule> in_checked_modules,
      final @Nonnull Map<String, TASTDTerm> in_checked_terms,
      final @Nonnull Map<String, TASTDType> in_checked_types,
      final @Nonnull Log in_log,
      final @Nonnull ModulePath in_module_path,
      final @Nonnull GlobalGraph in_graph)
    {
      this.source = in_source;
      this.checked_modules = in_checked_modules;
      this.checked_terms = in_checked_terms;
      this.checked_types = in_checked_types;
      this.log = in_log;
      this.module_path = in_module_path;
      this.graph = in_graph;
    }

    @Override public
      TASTDShaderFragmentLocal
      fragmentShaderVisitLocalDiscard(
        final @Nonnull TASTDShaderFragmentLocalDiscard d)
        throws ConstraintError,
          ConstraintError
    {
      d.getExpression().expressionVisitableAccept(
        new GraphBuilderShaderExpression(
          this.source,
          this.checked_modules,
          this.checked_terms,
          this.checked_types,
          this.log,
          this.module_path,
          this.graph));
      return d;
    }

    @Override public TASTDShaderFragmentLocal fragmentShaderVisitLocalValue(
      final @Nonnull TASTDShaderFragmentLocalValue v)
      throws ConstraintError,
        ConstraintError
    {
      v
        .getValue()
        .getExpression()
        .expressionVisitableAccept(
          new GraphBuilderShaderExpression(
            this.source,
            this.checked_modules,
            this.checked_terms,
            this.checked_types,
            this.log,
            this.module_path,
            this.graph));
      return v;
    }
  }

  /**
   * A visitor that produces dependency information from shader declarations.
   */

  private static final class GraphBuilderShader implements
    TASTShaderVisitor<TASTDShader, ConstraintError>
  {
    private final @Nonnull Map<ModulePathFlat, TASTDModule> checked_modules;
    private final @Nonnull Map<String, TASTDTerm>           checked_terms;
    private final @Nonnull Map<String, TASTDType>           checked_types;
    private final @Nonnull GlobalGraph                      graph;
    private final @Nonnull Log                              log;
    private final @Nonnull ModulePath                       module_path;

    public GraphBuilderShader(
      final @Nonnull Map<ModulePathFlat, TASTDModule> in_checked_modules,
      final @Nonnull Map<String, TASTDTerm> in_checked_terms,
      final @Nonnull Map<String, TASTDType> in_checked_types,
      final @Nonnull Log in_log,
      final @Nonnull ModulePath in_module_path,
      final @Nonnull GlobalGraph in_graph)
    {
      this.checked_modules = in_checked_modules;
      this.checked_terms = in_checked_terms;
      this.checked_types = in_checked_types;
      this.log = in_log;
      this.module_path = in_module_path;
      this.graph = in_graph;
    }

    @Override public TASTDShader moduleVisitFragmentShader(
      final @Nonnull TASTDShaderFragment f)
      throws ConstraintError,
        ConstraintError
    {
      final TASTShaderName source =
        new TASTShaderName(this.module_path, f.getName());
      this.graph.addShader(source);

      f.fragmentShaderVisitableAccept(new GraphBuilderFragmentShader(
        source,
        this.checked_modules,
        this.checked_terms,
        this.checked_types,
        this.log,
        this.module_path,
        this.graph));

      return f;
    }

    @Override public TASTDShader moduleVisitProgramShader(
      final @Nonnull TASTDShaderProgram p)
      throws ConstraintError,
        ConstraintError
    {
      final TASTShaderName source =
        new TASTShaderName(this.module_path, p.getName());
      this.graph.addShader(source);
      this.graph.addShader(p.getFragmentShader());
      this.graph.addShader(p.getVertexShader());
      return p;
    }

    @Override public TASTDShader moduleVisitVertexShader(
      final @Nonnull TASTDShaderVertex v)
      throws ConstraintError,
        ConstraintError
    {
      final TASTShaderName source =
        new TASTShaderName(this.module_path, v.getName());
      this.graph.addShader(source);

      v.vertexShaderVisitableAccept(new GraphBuilderVertexShader(
        source,
        this.checked_modules,
        this.checked_terms,
        this.checked_types,
        this.log,
        this.module_path,
        this.graph));

      return v;
    }
  }

  /**
   * A visitor that walks over an expression that occurs in a shader, and adds
   * references to other terms and types to the given graph.
   */

  private static final class GraphBuilderShaderExpression implements
    TASTExpressionVisitor<TASTExpression, TASTDValueLocal, ConstraintError>
  {
    private final @Nonnull Map<ModulePathFlat, TASTDModule> checked_modules;
    private final @Nonnull Map<String, TASTDTerm>           checked_terms;
    private final @Nonnull Map<String, TASTDType>           checked_types;
    private final @Nonnull GlobalGraph                      graph;
    private final @Nonnull Log                              log;
    private final @Nonnull ModulePath                       module_path;
    private final @Nonnull TASTShaderName                   source;

    public GraphBuilderShaderExpression(
      final @Nonnull TASTShaderName in_source,
      final @Nonnull Map<ModulePathFlat, TASTDModule> in_checked_modules,
      final @Nonnull Map<String, TASTDTerm> in_checked_terms,
      final @Nonnull Map<String, TASTDType> in_checked_types,
      final @Nonnull Log in_log,
      final @Nonnull ModulePath in_module_path,
      final @Nonnull GlobalGraph in_graph)
    {
      this.source = in_source;
      this.checked_modules = in_checked_modules;
      this.checked_terms = in_checked_terms;
      this.checked_types = in_checked_types;
      this.log = in_log;
      this.module_path = in_module_path;
      this.graph = in_graph;
    }

    @SuppressWarnings("synthetic-access") private void addTermReference(
      final @Nonnull TASTTermName name)
      throws ConstraintError
    {
      name
        .termNameVisitableAccept(new TASTTermNameVisitor<Unit, ConstraintError>() {
          @Override public Unit termNameVisitGlobal(
            final TASTTermNameGlobal t)
            throws ConstraintError,
              ConstraintError
          {
            GraphBuilderShaderExpression.this.graph.addShaderTermReference(
              GraphBuilderShaderExpression.this.source,
              t);
            return Unit.unit();
          }

          @Override public Unit termNameVisitLocal(
            final TASTTermNameLocal t)
            throws ConstraintError,
              ConstraintError
          {
            return Unit.unit();
          }

          @Override public Unit termNameVisitExternal(
            final @Nonnull TASTTermNameExternal t)
            throws ConstraintError,
              ConstraintError
          {
            return Unit.unit();
          }
        });
    }

    private void addTypeReference(
      final @Nonnull TType type)
      throws ConstraintError
    {
      type.getName().typeNameVisitableAccept(
        new TTypeNameVisitor<Unit, ConstraintError>() {
          @Override public Unit typeNameVisitBuiltIn(
            final TTypeNameBuiltIn t)
            throws ConstraintError,
              ConstraintError
          {
            return Unit.unit();
          }

          @SuppressWarnings("synthetic-access") @Override public
            Unit
            typeNameVisitGlobal(
              final TTypeNameGlobal t)
              throws ConstraintError,
                ConstraintError
          {
            GraphBuilderShaderExpression.this.graph.addShaderTypeReference(
              GraphBuilderShaderExpression.this.source,
              t);
            return Unit.unit();
          }
        });
    }

    @Override public TASTExpression expressionVisitApplication(
      final @Nonnull List<TASTExpression> arguments,
      final @Nonnull TASTEApplication e)
      throws ConstraintError,
        ConstraintError
    {
      this.addTermReference(e.getName());
      return e;
    }

    @Override public boolean expressionVisitApplicationPre(
      final @Nonnull TASTEApplication e)
      throws ConstraintError,
        ConstraintError
    {
      return true;
    }

    @Override public TASTExpression expressionVisitBoolean(
      final @Nonnull TASTEBoolean e)
      throws ConstraintError,
        ConstraintError
    {
      return e;
    }

    @Override public TASTExpression expressionVisitConditional(
      final @Nonnull TASTExpression condition,
      final @Nonnull TASTExpression left,
      final @Nonnull TASTExpression right,
      final @Nonnull TASTEConditional e)
      throws ConstraintError,
        ConstraintError
    {
      return e;
    }

    @Override public void expressionVisitConditionalConditionPost(
      final @Nonnull TASTEConditional e)
      throws ConstraintError,
        ConstraintError
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalConditionPre(
      final @Nonnull TASTEConditional e)
      throws ConstraintError,
        ConstraintError
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalLeftPost(
      final @Nonnull TASTEConditional e)
      throws ConstraintError,
        ConstraintError
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalLeftPre(
      final @Nonnull TASTEConditional e)
      throws ConstraintError,
        ConstraintError
    {
      // Nothing
    }

    @Override public boolean expressionVisitConditionalPre(
      final @Nonnull TASTEConditional e)
      throws ConstraintError,
        ConstraintError
    {
      return true;
    }

    @Override public void expressionVisitConditionalRightPost(
      final @Nonnull TASTEConditional e)
      throws ConstraintError,
        ConstraintError
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalRightPre(
      final @Nonnull TASTEConditional e)
      throws ConstraintError,
        ConstraintError
    {
      // Nothing
    }

    @Override public TASTExpression expressionVisitInteger(
      final @Nonnull TASTEInteger e)
      throws ConstraintError,
        ConstraintError
    {
      return e;
    }

    @Override public TASTExpression expressionVisitLet(
      final @Nonnull List<TASTDValueLocal> bindings,
      final @Nonnull TASTExpression body,
      final @Nonnull TASTELet e)
      throws ConstraintError,
        ConstraintError
    {
      return e;
    }

    @Override public
      TASTLocalLevelVisitor<TASTDValueLocal, ConstraintError>
      expressionVisitLetPre(
        final @Nonnull TASTELet e)
        throws ConstraintError,
          ConstraintError
    {
      return new GraphBuilderShaderLocal(
        this.source,
        this.checked_modules,
        this.checked_terms,
        this.checked_types,
        this.log,
        this.module_path,
        this.graph);
    }

    @Override public TASTExpression expressionVisitNew(
      final @Nonnull List<TASTExpression> arguments,
      final @Nonnull TASTENew e)
      throws ConstraintError,
        ConstraintError
    {
      this.addTypeReference(e.getType());
      return e;
    }

    @Override public boolean expressionVisitNewPre(
      final @Nonnull TASTENew e)
      throws ConstraintError,
        ConstraintError
    {
      return true;
    }

    @Override public TASTExpression expressionVisitReal(
      final @Nonnull TASTEReal e)
      throws ConstraintError,
        ConstraintError
    {
      return e;
    }

    @Override public TASTExpression expressionVisitRecord(
      final @Nonnull TASTERecord e)
      throws ConstraintError,
        ConstraintError
    {
      this.addTypeReference(e.getType());
      return e;
    }

    @Override public TASTExpression expressionVisitRecordProjection(
      final @Nonnull TASTExpression body,
      final @Nonnull TASTERecordProjection e)
      throws ConstraintError,
        ConstraintError
    {
      this.addTypeReference(body.getType());
      return e;
    }

    @Override public boolean expressionVisitRecordProjectionPre(
      final @Nonnull TASTERecordProjection e)
      throws ConstraintError,
        ConstraintError
    {
      return true;
    }

    @Override public TASTExpression expressionVisitSwizzle(
      final @Nonnull TASTExpression body,
      final @Nonnull TASTESwizzle e)
      throws ConstraintError,
        ConstraintError
    {
      return e;
    }

    @Override public boolean expressionVisitSwizzlePre(
      final @Nonnull TASTESwizzle e)
      throws ConstraintError,
        ConstraintError
    {
      return true;
    }

    @Override public TASTExpression expressionVisitVariable(
      final @Nonnull TASTEVariable e)
      throws ConstraintError,
        ConstraintError
    {
      this.addTermReference(e.getName());
      this.addTypeReference(e.getType());
      return e;
    }
  }

  private static final class GraphBuilderShaderLocal implements
    TASTLocalLevelVisitor<TASTDValueLocal, ConstraintError>
  {
    private final @Nonnull Map<ModulePathFlat, TASTDModule> checked_modules;
    private final @Nonnull Map<String, TASTDTerm>           checked_terms;
    private final @Nonnull Map<String, TASTDType>           checked_types;
    private final @Nonnull GlobalGraph                      graph;
    private final @Nonnull Log                              log;
    private final @Nonnull ModulePath                       module_path;
    private final @Nonnull TASTShaderName                   source;

    public GraphBuilderShaderLocal(
      final @Nonnull TASTShaderName in_source,
      final @Nonnull Map<ModulePathFlat, TASTDModule> in_checked_modules,
      final @Nonnull Map<String, TASTDTerm> in_checked_terms,
      final @Nonnull Map<String, TASTDType> in_checked_types,
      final @Nonnull Log in_log,
      final @Nonnull ModulePath in_module_path,
      final @Nonnull GlobalGraph in_graph)
    {
      this.checked_modules = in_checked_modules;
      this.checked_terms = in_checked_terms;
      this.checked_types = in_checked_types;
      this.graph = in_graph;
      this.log = in_log;
      this.module_path = in_module_path;
      this.source = in_source;
    }

    @Override public TASTDValueLocal localVisitValueLocal(
      final @Nonnull TASTDValueLocal v)
      throws ConstraintError,
        ConstraintError
    {
      v.getExpression().expressionVisitableAccept(
        new GraphBuilderShaderExpression(
          this.source,
          this.checked_modules,
          this.checked_terms,
          this.checked_types,
          this.log,
          this.module_path,
          this.graph));
      return v;
    }
  }

  /**
   * A visitor that produces dependency information from term declarations.
   */

  private static final class GraphBuilderTerm implements
    TASTTermVisitor<TASTDTerm, ConstraintError>
  {
    private final @Nonnull Map<ModulePathFlat, TASTDModule> checked_modules;
    private final @Nonnull Map<String, TASTDTerm>           checked_terms;
    private final @Nonnull Map<String, TASTDType>           checked_types;
    private final @Nonnull GlobalGraph                      graph;
    private final @Nonnull Log                              log;
    private final @Nonnull ModulePath                       module_path;

    public GraphBuilderTerm(
      final @Nonnull Map<ModulePathFlat, TASTDModule> in_checked_modules,
      final @Nonnull Map<String, TASTDTerm> in_checked_terms,
      final @Nonnull Map<String, TASTDType> in_checked_types,
      final @Nonnull Log in_log,
      final @Nonnull ModulePath in_module_path,
      final @Nonnull GlobalGraph in_graph)
    {
      this.checked_modules = in_checked_modules;
      this.checked_terms = in_checked_terms;
      this.checked_types = in_checked_types;
      this.log = in_log;
      this.module_path = in_module_path;
      this.graph = in_graph;
    }

    private void addTermTypeReference(
      final @Nonnull TASTTermNameGlobal source,
      final @Nonnull TType type)
      throws ConstraintError
    {
      type.getName().typeNameVisitableAccept(
        new TTypeNameVisitor<Unit, ConstraintError>() {
          @Override public Unit typeNameVisitBuiltIn(
            final @Nonnull TTypeNameBuiltIn t)
            throws ConstraintError,
              ConstraintError
          {
            return Unit.unit();
          }

          @SuppressWarnings("synthetic-access") @Override public
            Unit
            typeNameVisitGlobal(
              final @Nonnull TTypeNameGlobal t)
              throws ConstraintError,
                ConstraintError
          {
            GraphBuilderTerm.this.graph.addTermTypeReference(source, t);
            return Unit.unit();
          }
        });
    }

    @Override public TASTDTerm termVisitFunctionDefined(
      final @Nonnull TASTDFunctionDefined f)
      throws ConstraintError,
        ConstraintError
    {
      final TASTTermNameGlobal source =
        new TASTTermNameGlobal(this.module_path, f.getName());
      this.graph.addTerm(source);

      for (final TASTDFunctionArgument a : f.getArguments()) {
        this.addTermTypeReference(source, a.getType());
      }

      f.getBody().expressionVisitableAccept(
        new GraphBuilderTermExpression(
          source,
          this.checked_modules,
          this.checked_terms,
          this.checked_types,
          this.log,
          this.module_path,
          this.graph));

      this.addTermTypeReference(source, f.getBody().getType());
      return f;
    }

    @Override public TASTDTerm termVisitFunctionExternal(
      final @Nonnull TASTDFunctionExternal f)
      throws ConstraintError,
        ConstraintError
    {
      final TASTTermNameGlobal source =
        new TASTTermNameGlobal(this.module_path, f.getName());
      this.graph.addTerm(source);

      for (final TASTDFunctionArgument a : f.getArguments()) {
        this.addTermTypeReference(source, a.getType());
      }

      return f;
    }

    @Override public TASTDTerm termVisitValueDefined(
      final @Nonnull TASTDValueDefined v)
      throws ConstraintError,
        ConstraintError
    {
      final TASTTermNameGlobal source =
        new TASTTermNameGlobal(this.module_path, v.getName());
      this.graph.addTerm(source);

      v.getExpression().expressionVisitableAccept(
        new GraphBuilderTermExpression(
          source,
          this.checked_modules,
          this.checked_terms,
          this.checked_types,
          this.log,
          this.module_path,
          this.graph));
      return v;
    }

    @Override public TASTDTerm termVisitValueExternal(
      final @Nonnull TASTDValueExternal v)
      throws ConstraintError,
        ConstraintError
    {
      final TASTTermNameGlobal source =
        new TASTTermNameGlobal(this.module_path, v.getName());
      this.graph.addTerm(source);
      this.addTermTypeReference(source, v.getType());
      return v;
    }
  }

  /**
   * A visitor that walks over an expression that occurs in a well-typed term,
   * and adds references to other terms and types to the given graph.
   */

  private static final class GraphBuilderTermExpression implements
    TASTExpressionVisitor<TASTExpression, TASTDValueLocal, ConstraintError>
  {
    private final @Nonnull Map<ModulePathFlat, TASTDModule> checked_modules;
    private final @Nonnull Map<String, TASTDTerm>           checked_terms;
    private final @Nonnull Map<String, TASTDType>           checked_types;
    private final @Nonnull GlobalGraph                      graph;
    private final @Nonnull Log                              log;
    private final @Nonnull ModulePath                       module_path;
    private final @Nonnull TASTTermNameGlobal               source;

    public GraphBuilderTermExpression(
      final @Nonnull TASTTermNameGlobal in_source,
      final @Nonnull Map<ModulePathFlat, TASTDModule> in_checked_modules,
      final @Nonnull Map<String, TASTDTerm> in_checked_terms,
      final @Nonnull Map<String, TASTDType> in_checked_types,
      final @Nonnull Log in_log,
      final @Nonnull ModulePath in_module_path,
      final @Nonnull GlobalGraph in_graph)
    {
      this.source = in_source;
      this.checked_modules = in_checked_modules;
      this.checked_terms = in_checked_terms;
      this.checked_types = in_checked_types;
      this.log = in_log;
      this.module_path = in_module_path;
      this.graph = in_graph;
    }

    @SuppressWarnings("synthetic-access") private void addTermReference(
      final @Nonnull TASTTermName name)
      throws ConstraintError
    {
      name
        .termNameVisitableAccept(new TASTTermNameVisitor<Unit, ConstraintError>() {
          @Override public Unit termNameVisitGlobal(
            final TASTTermNameGlobal t)
            throws ConstraintError,
              ConstraintError
          {
            GraphBuilderTermExpression.this.graph.addTermTermReference(
              GraphBuilderTermExpression.this.source,
              t);
            return Unit.unit();
          }

          @Override public Unit termNameVisitLocal(
            final TASTTermNameLocal t)
            throws ConstraintError,
              ConstraintError
          {
            return Unit.unit();
          }

          @Override public Unit termNameVisitExternal(
            final TASTTermNameExternal t)
            throws ConstraintError,
              ConstraintError
          {
            return Unit.unit();
          }
        });
    }

    private void addTermTypeReference(
      final @Nonnull TType type)
      throws ConstraintError
    {
      type.getName().typeNameVisitableAccept(
        new TTypeNameVisitor<Unit, ConstraintError>() {
          @Override public Unit typeNameVisitBuiltIn(
            final @Nonnull TTypeNameBuiltIn t)
            throws ConstraintError,
              ConstraintError
          {
            return Unit.unit();
          }

          @SuppressWarnings("synthetic-access") @Override public
            Unit
            typeNameVisitGlobal(
              final @Nonnull TTypeNameGlobal t)
              throws ConstraintError,
                ConstraintError
          {
            GraphBuilderTermExpression.this.graph.addTermTypeReference(
              GraphBuilderTermExpression.this.source,
              t);
            return Unit.unit();
          }
        });
    }

    @Override public TASTExpression expressionVisitApplication(
      final @Nonnull List<TASTExpression> arguments,
      final @Nonnull TASTEApplication e)
      throws ConstraintError,
        ConstraintError
    {
      this.addTermReference(e.getName());
      return e;
    }

    @Override public boolean expressionVisitApplicationPre(
      final @Nonnull TASTEApplication e)
      throws ConstraintError,
        ConstraintError
    {
      return true;
    }

    @Override public TASTExpression expressionVisitBoolean(
      final @Nonnull TASTEBoolean e)
      throws ConstraintError,
        ConstraintError
    {
      return e;
    }

    @Override public TASTExpression expressionVisitConditional(
      final @Nonnull TASTExpression condition,
      final @Nonnull TASTExpression left,
      final @Nonnull TASTExpression right,
      final @Nonnull TASTEConditional e)
      throws ConstraintError,
        ConstraintError
    {
      return e;
    }

    @Override public void expressionVisitConditionalConditionPost(
      final @Nonnull TASTEConditional e)
      throws ConstraintError,
        ConstraintError
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalConditionPre(
      final @Nonnull TASTEConditional e)
      throws ConstraintError,
        ConstraintError
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalLeftPost(
      final @Nonnull TASTEConditional e)
      throws ConstraintError,
        ConstraintError
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalLeftPre(
      final @Nonnull TASTEConditional e)
      throws ConstraintError,
        ConstraintError
    {
      // Nothing
    }

    @Override public boolean expressionVisitConditionalPre(
      final @Nonnull TASTEConditional e)
      throws ConstraintError,
        ConstraintError
    {
      return true;
    }

    @Override public void expressionVisitConditionalRightPost(
      final @Nonnull TASTEConditional e)
      throws ConstraintError,
        ConstraintError
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalRightPre(
      final @Nonnull TASTEConditional e)
      throws ConstraintError,
        ConstraintError
    {
      // Nothing
    }

    @Override public TASTExpression expressionVisitInteger(
      final @Nonnull TASTEInteger e)
      throws ConstraintError,
        ConstraintError
    {
      return e;
    }

    @Override public TASTExpression expressionVisitLet(
      final @Nonnull List<TASTDValueLocal> bindings,
      final @Nonnull TASTExpression body,
      final @Nonnull TASTELet e)
      throws ConstraintError,
        ConstraintError
    {
      return e;
    }

    @Override public
      TASTLocalLevelVisitor<TASTDValueLocal, ConstraintError>
      expressionVisitLetPre(
        final @Nonnull TASTELet e)
        throws ConstraintError,
          ConstraintError
    {
      return new TASTLocalLevelVisitor<TASTDValueLocal, ConstraintError>() {
        @SuppressWarnings("synthetic-access") @Override public
          TASTDValueLocal
          localVisitValueLocal(
            final TASTDValueLocal v)
            throws ConstraintError,
              ConstraintError
        {
          v.getExpression().expressionVisitableAccept(
            new GraphBuilderTermExpression(
              GraphBuilderTermExpression.this.source,
              GraphBuilderTermExpression.this.checked_modules,
              GraphBuilderTermExpression.this.checked_terms,
              GraphBuilderTermExpression.this.checked_types,
              GraphBuilderTermExpression.this.log,
              GraphBuilderTermExpression.this.module_path,
              GraphBuilderTermExpression.this.graph));
          return v;
        }
      };
    }

    @Override public TASTExpression expressionVisitNew(
      final @Nonnull List<TASTExpression> arguments,
      final @Nonnull TASTENew e)
      throws ConstraintError,
        ConstraintError
    {
      this.addTermTypeReference(e.getType());
      return e;
    }

    @Override public boolean expressionVisitNewPre(
      final @Nonnull TASTENew e)
      throws ConstraintError,
        ConstraintError
    {
      return true;
    }

    @Override public TASTExpression expressionVisitReal(
      final @Nonnull TASTEReal e)
      throws ConstraintError,
        ConstraintError
    {
      return e;
    }

    @Override public TASTExpression expressionVisitRecord(
      final @Nonnull TASTERecord e)
      throws ConstraintError,
        ConstraintError
    {
      this.addTermTypeReference(e.getType());

      for (final TASTRecordFieldAssignment a : e.getAssignments()) {
        a.getExpression().expressionVisitableAccept(
          new GraphBuilderTermExpression(
            GraphBuilderTermExpression.this.source,
            GraphBuilderTermExpression.this.checked_modules,
            GraphBuilderTermExpression.this.checked_terms,
            GraphBuilderTermExpression.this.checked_types,
            GraphBuilderTermExpression.this.log,
            GraphBuilderTermExpression.this.module_path,
            GraphBuilderTermExpression.this.graph));
      }
      return e;
    }

    @Override public TASTExpression expressionVisitRecordProjection(
      final @Nonnull TASTExpression body,
      final @Nonnull TASTERecordProjection e)
      throws ConstraintError,
        ConstraintError
    {
      this.addTermTypeReference(body.getType());
      return e;
    }

    @Override public boolean expressionVisitRecordProjectionPre(
      final @Nonnull TASTERecordProjection e)
      throws ConstraintError,
        ConstraintError
    {
      return true;
    }

    @Override public TASTExpression expressionVisitSwizzle(
      final @Nonnull TASTExpression body,
      final @Nonnull TASTESwizzle e)
      throws ConstraintError,
        ConstraintError
    {
      return e;
    }

    @Override public boolean expressionVisitSwizzlePre(
      final @Nonnull TASTESwizzle e)
      throws ConstraintError,
        ConstraintError
    {
      return true;
    }

    @Override public TASTExpression expressionVisitVariable(
      final @Nonnull TASTEVariable e)
      throws ConstraintError,
        ConstraintError
    {
      this.addTermReference(e.getName());
      this.addTermTypeReference(e.getType());
      return e;
    }
  }

  /**
   * A visitor that produces dependency information from local term
   * declarations.
   */

  /**
   * A visitor that produces dependency information from type declarations.
   */

  private static final class GraphBuilderType implements
    TASTTypeVisitor<TASTDType, ConstraintError>
  {
    private final @Nonnull Map<ModulePathFlat, TASTDModule> checked_modules;
    private final @Nonnull Map<String, TASTDType>           checked_types;
    private final @Nonnull GlobalGraph                      graph;
    private final @Nonnull Log                              log;
    private final @Nonnull ModulePath                       module_path;

    public GraphBuilderType(
      final @Nonnull Map<ModulePathFlat, TASTDModule> in_checked_modules,
      final @Nonnull Map<String, TASTDType> in_checked_types,
      final @Nonnull Log in_log,
      final @Nonnull ModulePath in_module_path,
      final @Nonnull GlobalGraph in_graph)
    {
      this.checked_modules = in_checked_modules;
      this.checked_types = in_checked_types;
      this.log = in_log;
      this.module_path = in_module_path;
      this.graph = in_graph;
    }

    @Override public TASTDType typeVisitTypeRecord(
      final TASTDTypeRecord r)
      throws ConstraintError
    {
      final TTypeNameGlobal source =
        new TTypeNameGlobal(this.module_path, r.getName());

      this.graph.addType(source);

      for (final TASTDTypeRecordField f : r.getFields()) {
        final TManifestType ft = f.getType();
        ft.getName().typeNameVisitableAccept(
          new TTypeNameVisitor<Unit, ConstraintError>() {
            @Override public Unit typeNameVisitBuiltIn(
              final @Nonnull TTypeNameBuiltIn t)
              throws ConstraintError,
                ConstraintError
            {
              return Unit.unit();
            }

            @SuppressWarnings("synthetic-access") @Override public
              Unit
              typeNameVisitGlobal(
                final @Nonnull TTypeNameGlobal t)
                throws ConstraintError,
                  ConstraintError
            {
              GraphBuilderType.this.graph.addTypeTypeReference(source, t);
              return Unit.unit();
            }
          });
      }

      return r;
    }
  }

  private static final class GraphBuilderVertexShader implements
    TASTVertexShaderVisitor<TASTDShaderVertex, TASTDShaderVertexInput, TASTDShaderVertexParameter, TASTDShaderVertexOutput, TASTDShaderVertexLocalValue, TASTDShaderVertexOutputAssignment, ConstraintError>
  {
    private final @Nonnull Map<ModulePathFlat, TASTDModule> checked_modules;
    private final @Nonnull Map<String, TASTDTerm>           checked_terms;
    private final @Nonnull Map<String, TASTDType>           checked_types;
    private final @Nonnull GlobalGraph                      graph;
    private final @Nonnull Log                              log;
    private final @Nonnull ModulePath                       module_path;
    private final @Nonnull TASTShaderName                   source;

    public GraphBuilderVertexShader(
      final @Nonnull TASTShaderName in_source,
      final @Nonnull Map<ModulePathFlat, TASTDModule> in_checked_modules,
      final @Nonnull Map<String, TASTDTerm> in_checked_terms,
      final @Nonnull Map<String, TASTDType> in_checked_types,
      final @Nonnull Log in_log,
      final @Nonnull ModulePath in_module_path,
      final @Nonnull GlobalGraph in_graph)
    {
      this.source = in_source;
      this.checked_modules = in_checked_modules;
      this.checked_terms = in_checked_terms;
      this.checked_types = in_checked_types;
      this.log = in_log;
      this.module_path = in_module_path;
      this.graph = in_graph;
    }

    private void addTypeReference(
      final @Nonnull TType type)
      throws ConstraintError
    {
      type.getName().typeNameVisitableAccept(
        new TTypeNameVisitor<Unit, ConstraintError>() {
          @Override public Unit typeNameVisitBuiltIn(
            final @Nonnull TTypeNameBuiltIn t)
            throws ConstraintError,
              ConstraintError
          {
            return Unit.unit();
          }

          @SuppressWarnings("synthetic-access") @Override public
            Unit
            typeNameVisitGlobal(
              final @Nonnull TTypeNameGlobal t)
              throws ConstraintError,
                ConstraintError
          {
            GraphBuilderVertexShader.this.graph.addShaderTypeReference(
              GraphBuilderVertexShader.this.source,
              t);
            return Unit.unit();
          }
        });
    }

    @Override public
      TASTDShaderVertex
      vertexShaderVisit(
        final @Nonnull List<TASTDShaderVertexInput> inputs,
        final @Nonnull List<TASTDShaderVertexParameter> parameters,
        final @Nonnull List<TASTDShaderVertexOutput> outputs,
        final @Nonnull List<TASTDShaderVertexLocalValue> locals,
        final @Nonnull List<TASTDShaderVertexOutputAssignment> output_assignments,
        final @Nonnull TASTDShaderVertex v)
        throws ConstraintError,
          ConstraintError
    {
      return v;
    }

    @Override public TASTDShaderVertexInput vertexShaderVisitInput(
      final @Nonnull TASTDShaderVertexInput i)
      throws ConstraintError,
        ConstraintError
    {
      this.addTypeReference(i.getType());
      return i;
    }

    @Override public
      TASTVertexShaderLocalVisitor<TASTDShaderVertexLocalValue, ConstraintError>
      vertexShaderVisitLocalsPre()
        throws ConstraintError,
          ConstraintError
    {
      return new TASTVertexShaderLocalVisitor<TASTDShaderVertexLocalValue, ConstraintError>() {
        @SuppressWarnings("synthetic-access") @Override public
          TASTDShaderVertexLocalValue
          vertexShaderVisitLocalValue(
            final TASTDShaderVertexLocalValue v)
            throws ConstraintError,
              ConstraintError
        {
          v
            .getValue()
            .getExpression()
            .expressionVisitableAccept(
              new GraphBuilderShaderExpression(
                GraphBuilderVertexShader.this.source,
                GraphBuilderVertexShader.this.checked_modules,
                GraphBuilderVertexShader.this.checked_terms,
                GraphBuilderVertexShader.this.checked_types,
                GraphBuilderVertexShader.this.log,
                GraphBuilderVertexShader.this.module_path,
                GraphBuilderVertexShader.this.graph));
          return v;
        }
      };
    }

    @Override public TASTDShaderVertexOutput vertexShaderVisitOutput(
      final @Nonnull TASTDShaderVertexOutput o)
      throws ConstraintError,
        ConstraintError
    {
      this.addTypeReference(o.getType());
      return o;
    }

    @Override public
      TASTDShaderVertexOutputAssignment
      vertexShaderVisitOutputAssignment(
        final @Nonnull TASTDShaderVertexOutputAssignment a)
        throws ConstraintError,
          ConstraintError
    {
      a.getVariable().expressionVisitableAccept(
        new GraphBuilderShaderExpression(
          this.source,
          this.checked_modules,
          this.checked_terms,
          this.checked_types,
          this.log,
          this.module_path,
          this.graph));
      return a;
    }

    @Override public TASTDShaderVertexParameter vertexShaderVisitParameter(
      final @Nonnull TASTDShaderVertexParameter p)
      throws ConstraintError,
        ConstraintError
    {
      this.addTypeReference(p.getType());
      return p;
    }
  }

  static @Nonnull TGraphs newGraphs(
    final @Nonnull Log log)
  {
    return new TGraphs(log);
  }

  private final @Nonnull Log log;

  private TGraphs(
    final @Nonnull Log in_log)
  {
    this.log = in_log;
  }

  public @Nonnull GlobalGraph check(
    final @Nonnull Map<ModulePathFlat, TASTDModule> checked_modules)
    throws ConstraintError
  {
    final GlobalGraph graph = new GlobalGraph(this.log);

    for (final ModulePathFlat p : checked_modules.keySet()) {
      final TASTDModule m = checked_modules.get(p);

      final GraphBuilderType type_tb =
        new GraphBuilderType(
          checked_modules,
          m.getTypes(),
          this.log,
          m.getPath(),
          graph);

      for (final String name : m.getTypes().keySet()) {
        final TASTDType type = m.getTypes().get(name);
        type.typeVisitableAccept(type_tb);
      }

      final GraphBuilderTerm term_tb =
        new GraphBuilderTerm(
          checked_modules,
          m.getTerms(),
          m.getTypes(),
          this.log,
          m.getPath(),
          graph);

      for (final String name : m.getTerms().keySet()) {
        final TASTDTerm term = m.getTerms().get(name);
        term.termVisitableAccept(term_tb);
      }

      final GraphBuilderShader shader_tb =
        new GraphBuilderShader(
          checked_modules,
          m.getTerms(),
          m.getTypes(),
          this.log,
          m.getPath(),
          graph);

      for (final String name : m.getShaders().keySet()) {
        final TASTDShader shader = m.getShaders().get(name);
        shader.shaderVisitableAccept(shader_tb);
      }
    }

    return graph;
  }
}
