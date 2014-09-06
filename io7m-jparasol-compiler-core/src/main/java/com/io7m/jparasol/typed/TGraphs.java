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

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph.CycleFoundException;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jfunctional.Pair;
import com.io7m.jfunctional.Unit;
import com.io7m.jlog.LogLevel;
import com.io7m.jlog.LogUsableType;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
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
import com.io7m.jparasol.typed.ast.TASTEApplication;
import com.io7m.jparasol.typed.ast.TASTEBoolean;
import com.io7m.jparasol.typed.ast.TASTEConditional;
import com.io7m.jparasol.typed.ast.TASTEInteger;
import com.io7m.jparasol.typed.ast.TASTELet;
import com.io7m.jparasol.typed.ast.TASTEMatchType;
import com.io7m.jparasol.typed.ast.TASTENew;
import com.io7m.jparasol.typed.ast.TASTEReal;
import com.io7m.jparasol.typed.ast.TASTERecord;
import com.io7m.jparasol.typed.ast.TASTERecordProjection;
import com.io7m.jparasol.typed.ast.TASTESwizzle;
import com.io7m.jparasol.typed.ast.TASTEVariable;
import com.io7m.jparasol.typed.ast.TASTExpressionMatchConstantType;
import com.io7m.jparasol.typed.ast.TASTExpressionType;
import com.io7m.jparasol.typed.ast.TASTExpressionVisitorType;
import com.io7m.jparasol.typed.ast.TASTFragmentShaderLocalVisitorType;
import com.io7m.jparasol.typed.ast.TASTFragmentShaderVisitorType;
import com.io7m.jparasol.typed.ast.TASTLocalLevelVisitorType;
import com.io7m.jparasol.typed.ast.TASTNameTermShaderFlatType;
import com.io7m.jparasol.typed.ast.TASTNameTypeShaderFlatType;
import com.io7m.jparasol.typed.ast.TASTNameTypeTermFlatType;
import com.io7m.jparasol.typed.ast.TASTRecordFieldAssignment;
import com.io7m.jparasol.typed.ast.TASTReference;
import com.io7m.jparasol.typed.ast.TASTShaderName;
import com.io7m.jparasol.typed.ast.TASTShaderNameFlat;
import com.io7m.jparasol.typed.ast.TASTShaderVisitorType;
import com.io7m.jparasol.typed.ast.TASTTermName;
import com.io7m.jparasol.typed.ast.TASTTermName.TASTTermNameExternal;
import com.io7m.jparasol.typed.ast.TASTTermName.TASTTermNameGlobal;
import com.io7m.jparasol.typed.ast.TASTTermName.TASTTermNameLocal;
import com.io7m.jparasol.typed.ast.TASTTermNameFlat;
import com.io7m.jparasol.typed.ast.TASTTermNameVisitorType;
import com.io7m.jparasol.typed.ast.TASTTermVisitorType;
import com.io7m.jparasol.typed.ast.TASTTypeVisitorType;
import com.io7m.jparasol.typed.ast.TASTVertexShaderLocalVisitorType;
import com.io7m.jparasol.typed.ast.TASTVertexShaderVisitorType;
import com.io7m.junreachable.UnreachableCodeException;

@EqualityReference final class TGraphs
{
  /**
   * Graphs of term→term, term→type, and type→type dependencies.
   */

  @EqualityReference static final class GlobalGraph
  {
    private final GlobalTermShaderGraph term_shader;
    private final GlobalTermTermGraph   term_term;
    private final GlobalTermTypeGraph   term_type;
    private final GlobalTypeShaderGraph type_shader;
    private final GlobalTypeTypeGraph   type_type;

    public GlobalGraph(
      final LogUsableType log)
    {
      this.term_term = new GlobalTermTermGraph(log);
      this.term_type = new GlobalTermTypeGraph(log);
      this.type_type = new GlobalTypeTypeGraph(log);
      this.term_shader = new GlobalTermShaderGraph(log);
      this.type_shader = new GlobalTypeShaderGraph(log);
    }

    public void addShader(
      final TASTShaderName shader)
    {
      final TASTShaderNameFlat flat =
        TASTShaderNameFlat.fromShaderName(shader);
      this.type_shader.addShader(flat);
      this.term_shader.addShader(flat);
    }

    public void addShaderTermReference(
      final TASTShaderName shader,
      final TASTTermNameGlobal term)
    {
      this.addTerm(term);
      this.addShader(shader);
      this.term_shader.addTermShaderReference(shader, term);
    }

    public void addShaderTypeReference(
      final TASTShaderName shader,
      final TTypeNameGlobal type)
    {
      this.addType(type);
      this.addShader(shader);
      this.type_shader.addTypeShaderReference(shader, type);
    }

    public void addTerm(
      final TASTTermNameGlobal term)
    {
      final TASTTermNameFlat flat = TASTTermNameFlat.fromTermNameGlobal(term);
      this.term_term.addTerm(flat);
      this.term_type.addTerm(flat);
      this.term_shader.addTerm(flat);
    }

    public void addTermTermReference(
      final TASTTermNameGlobal source,
      final TASTTermNameGlobal target)
    {
      this.addTerm(source);
      this.addTerm(target);
      this.term_term.addTermReference(source, target);
    }

    public void addTermTypeReference(
      final TASTTermNameGlobal source,
      final TTypeNameGlobal target)
    {
      this.addTerm(source);
      this.term_type.addTermTypeReference(source, target);
    }

    public void addType(
      final TTypeNameGlobal type)
    {
      final TTypeNameFlat flat = TTypeNameFlat.fromTypeNameGlobal(type);
      this.term_type.addType(flat);
      this.type_type.addType(flat);
      this.type_shader.addType(flat);
    }

    public void addTypeTypeReference(
      final TTypeNameGlobal source,
      final TTypeNameGlobal target)
    {
      this.addType(source);
      this.addType(target);
      this.type_type.addTypeReference(source, target);
    }

    public GlobalTermShaderGraph getTermShader()
    {
      return this.term_shader;
    }

    public GlobalTermTermGraph getTermTermGraph()
    {
      return this.term_term;
    }

    public GlobalTermTypeGraph getTermTypeGraph()
    {
      return this.term_type;
    }

    public GlobalTypeShaderGraph getTypeShader()
    {
      return this.type_shader;
    }

    public GlobalTypeTypeGraph getTypeTypeGraph()
    {
      return this.type_type;
    }
  }

  /**
   * A directed acyclic graph of references to terms by shaders over the
   * entire compilation.
   */

  @EqualityReference static final class GlobalTermShaderGraph
  {
    private final DirectedAcyclicGraph<TASTNameTermShaderFlatType, TASTReference> graph;
    private final LogUsableType                                                   log;

    public GlobalTermShaderGraph(
      final LogUsableType in_log)
    {
      this.log = in_log.with("global-shader-term-graph");
      this.graph =
        new DirectedAcyclicGraph<TASTNameTermShaderFlatType, TASTReference>(
          TASTReference.class);
    }

    public void addShader(
      final TASTShaderNameFlat flat)
    {
      if (this.graph.containsVertex(flat) == false) {
        if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
          final String r = String.format("Add shader %s", flat.show());
          assert r != null;
          this.log.debug(r);
        }
        this.graph.addVertex(flat);
      }
    }

    public void addTerm(
      final TASTTermNameFlat term)
    {
      if (this.graph.containsVertex(term) == false) {
        if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
          final String r = String.format("Add term %s", term.show());
          assert r != null;
          this.log.debug(r);
        }
        this.graph.addVertex(term);
      }
    }

    public void addTermShaderReference(
      final TASTShaderName source,
      final TASTTermNameGlobal target)
    {
      final TASTShaderNameFlat source_flat =
        TASTShaderNameFlat.fromShaderName(source);
      final TASTTermNameFlat target_flat =
        TASTTermNameFlat.fromTermNameGlobal(target);

      final TASTReference reference =
        new TASTReference(
          source.getPath(),
          source.getName(),
          target.getPath(),
          target.getName());

      if (this.graph.containsEdge(reference) == false) {
        if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
          final String r =
            String.format("Add shader term reference %s.%s → %s.%s", source
              .getFlat()
              .getActual(), source.getName().getActual(), target
              .getFlat()
              .getActual(), target.getName().getActual());
          assert r != null;
          this.log.debug(r);
        }

        this.addShader(source_flat);
        this.addTerm(target_flat);

        try {
          this.graph.addDagEdge(source_flat, target_flat, reference);
        } catch (final CycleFoundException e) {
          throw new UnreachableCodeException(e);
        }
      }
    }

    public
      DirectedAcyclicGraph<TASTNameTermShaderFlatType, TASTReference>
      getGraph()
    {
      return this.graph;
    }
  }

  /**
   * A directed acyclic graph of references to terms by terms over the entire
   * compilation.
   */

  @EqualityReference static final class GlobalTermTermGraph
  {
    private final DirectedAcyclicGraph<TASTTermNameFlat, TASTReference> graph;
    private final LogUsableType                                         log;

    public GlobalTermTermGraph(
      final LogUsableType in_log)
    {
      this.log = in_log.with("global-term-term-graph");
      this.graph =
        new DirectedAcyclicGraph<TASTTermNameFlat, TASTReference>(
          TASTReference.class);
    }

    public void addTerm(
      final TASTTermNameFlat term)
    {
      if (this.graph.containsVertex(term) == false) {
        if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
          final String r = String.format("Add term %s", term.show());
          assert r != null;
          this.log.debug(r);
        }
        this.graph.addVertex(term);
      }
    }

    public void addTermReference(
      final TASTTermNameGlobal source,
      final TASTTermNameGlobal target)
    {
      final TASTReference reference =
        new TASTReference(
          source.getPath(),
          source.getName(),
          target.getPath(),
          target.getName());

      if (this.graph.containsEdge(reference) == false) {
        if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
          final String r =
            String.format("Add term reference %s.%s → %s.%s", source
              .getFlat()
              .getActual(), source.getName().getActual(), target
              .getFlat()
              .getActual(), target.getName().getActual());
          assert r != null;
          this.log.debug(r);
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

    public DirectedAcyclicGraph<TASTTermNameFlat, TASTReference> getGraph()
    {
      return this.graph;
    }
  }

  /**
   * A directed acyclic graph of references to types by terms over the entire
   * compilation.
   */

  @EqualityReference static final class GlobalTermTypeGraph
  {
    private final DirectedAcyclicGraph<TASTNameTypeTermFlatType, TASTReference> graph;
    private final LogUsableType                                                 log;

    public GlobalTermTypeGraph(
      final LogUsableType in_log)
    {
      this.log = in_log.with("global-term-type-graph");
      this.graph =
        new DirectedAcyclicGraph<TASTNameTypeTermFlatType, TASTReference>(
          TASTReference.class);
    }

    public void addTerm(
      final TASTTermNameFlat flat)
    {
      if (this.graph.containsVertex(flat) == false) {
        if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
          final String r = String.format("Add term %s", flat.show());
          assert r != null;
          this.log.debug(r);
        }
        this.graph.addVertex(flat);
      }
    }

    public void addTermTypeReference(
      final TASTTermNameGlobal source,
      final TTypeNameGlobal target)
    {
      final TASTTermNameFlat source_flat =
        TASTTermNameFlat.fromTermNameGlobal(source);
      final TTypeNameFlat target_flat =
        TTypeNameFlat.fromTypeNameGlobal(target);

      final TASTReference reference =
        new TASTReference(
          source.getPath(),
          source.getName(),
          target.getPath(),
          target.getName());

      if (this.graph.containsEdge(reference) == false) {
        if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
          final String r =
            String.format("Add term type reference %s.%s → %s.%s", source
              .getFlat()
              .getActual(), source.getName().getActual(), target
              .getFlat()
              .getActual(), target.getName().getActual());
          assert r != null;
          this.log.debug(r);
        }

        this.addTerm(source_flat);
        this.addType(target_flat);

        try {
          this.graph.addDagEdge(source_flat, target_flat, reference);
        } catch (final CycleFoundException e) {
          throw new UnreachableCodeException(e);
        }
      }
    }

    public void addType(
      final TTypeNameFlat type)
    {
      if (this.graph.containsVertex(type) == false) {
        if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
          final String r = String.format("Add type %s", type.show());
          assert r != null;
          this.log.debug(r);
        }
        this.graph.addVertex(type);
      }
    }

    public
      DirectedAcyclicGraph<TASTNameTypeTermFlatType, TASTReference>
      getGraph()
    {
      return this.graph;
    }
  }

  /**
   * A directed acyclic graph of references to types by shaders over the
   * entire compilation.
   */

  @EqualityReference static final class GlobalTypeShaderGraph
  {
    private final DirectedAcyclicGraph<TASTNameTypeShaderFlatType, TASTReference> graph;
    private final LogUsableType                                                   log;

    public GlobalTypeShaderGraph(
      final LogUsableType in_log)
    {
      this.log = in_log.with("global-shader-type-graph");
      this.graph =
        new DirectedAcyclicGraph<TASTNameTypeShaderFlatType, TASTReference>(
          TASTReference.class);
    }

    public void addShader(
      final TASTShaderNameFlat flat)
    {
      if (this.graph.containsVertex(flat) == false) {
        if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
          final String r = String.format("Add shader %s", flat.show());
          assert r != null;
          this.log.debug(r);
        }
        this.graph.addVertex(flat);
      }
    }

    public void addType(
      final TTypeNameFlat type)
    {
      if (this.graph.containsVertex(type) == false) {
        if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
          final String r = String.format("Add type %s", type.show());
          assert r != null;
          this.log.debug(r);
        }
        this.graph.addVertex(type);
      }
    }

    public void addTypeShaderReference(
      final TASTShaderName source,
      final TTypeNameGlobal target)
    {
      final TASTShaderNameFlat source_flat =
        TASTShaderNameFlat.fromShaderName(source);
      final TTypeNameFlat target_flat =
        TTypeNameFlat.fromTypeNameGlobal(target);

      final TASTReference reference =
        new TASTReference(
          source.getPath(),
          source.getName(),
          target.getPath(),
          target.getName());

      if (this.graph.containsEdge(reference) == false) {
        if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
          final String r =
            String.format("Add shader type reference %s.%s → %s.%s", source
              .getFlat()
              .getActual(), source.getName().getActual(), target
              .getFlat()
              .getActual(), target.getName().getActual());
          assert r != null;
          this.log.debug(r);
        }

        this.addShader(source_flat);
        this.addType(target_flat);

        try {
          this.graph.addDagEdge(source_flat, target_flat, reference);
        } catch (final CycleFoundException e) {
          throw new UnreachableCodeException(e);
        }
      }
    }

    public
      DirectedAcyclicGraph<TASTNameTypeShaderFlatType, TASTReference>
      getGraph()
    {
      return this.graph;
    }
  }

  /**
   * A directed acyclic graph of references to types by types over the entire
   * compilation.
   */

  @EqualityReference static final class GlobalTypeTypeGraph
  {
    private final DirectedAcyclicGraph<TTypeNameFlat, TASTReference> graph;
    private final LogUsableType                                      log;

    public GlobalTypeTypeGraph(
      final LogUsableType in_log)
    {
      this.log = in_log.with("global-type-type-graph");
      this.graph =
        new DirectedAcyclicGraph<TTypeNameFlat, TASTReference>(
          TASTReference.class);
    }

    public void addType(
      final TTypeNameFlat type)
    {
      if (this.graph.containsVertex(type) == false) {
        if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
          final String r = String.format("Add type %s", type.show());
          assert r != null;
          this.log.debug(r);
        }
        this.graph.addVertex(type);
      }
    }

    public void addTypeReference(
      final TTypeNameGlobal source,
      final TTypeNameGlobal target)
    {
      final TASTReference reference =
        new TASTReference(
          source.getPath(),
          source.getName(),
          target.getPath(),
          target.getName());

      if (this.graph.containsEdge(reference) == false) {
        if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
          final String r =
            String.format("Add type reference %s.%s → %s.%s", source
              .getFlat()
              .getActual(), source.getName().getActual(), target
              .getFlat()
              .getActual(), target.getName().getActual());
          assert r != null;
          this.log.debug(r);
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

    public DirectedAcyclicGraph<TTypeNameFlat, TASTReference> getGraph()
    {
      return this.graph;
    }
  }

  @EqualityReference private static final class GraphBuilderFragmentShader implements
    TASTFragmentShaderVisitorType<TASTDShaderFragment, TASTDShaderFragmentInput, TASTDShaderFragmentParameter, TASTDShaderFragmentOutput, TASTDShaderFragmentLocal, TASTDShaderFragmentOutputAssignment, UnreachableCodeException>
  {
    private final Map<ModulePathFlat, TASTDModule> checked_modules;
    private final Map<String, TASTDTerm>           checked_terms;
    private final Map<String, TASTDType>           checked_types;
    private final GlobalGraph                      graph;
    private final LogUsableType                    log;
    private final ModulePath                       module_path;
    private final TASTShaderName                   source;

    public GraphBuilderFragmentShader(
      final TASTShaderName in_source,
      final Map<ModulePathFlat, TASTDModule> in_checked_modules,
      final Map<String, TASTDTerm> in_checked_terms,
      final Map<String, TASTDType> in_checked_types,
      final LogUsableType in_log,
      final ModulePath in_module_path,
      final GlobalGraph in_graph)
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
      final TType type)
    {
      type.getName().typeNameVisitableAccept(
        new TTypeNameVisitorType<Unit, UnreachableCodeException>() {
          @Override public Unit typeNameVisitBuiltIn(
            final TTypeNameBuiltIn t)
          {
            return Unit.unit();
          }

          @SuppressWarnings("synthetic-access") @Override public
            Unit
            typeNameVisitGlobal(
              final TTypeNameGlobal t)
          {
            GraphBuilderFragmentShader.this.graph.addShaderTypeReference(
              GraphBuilderFragmentShader.this.source,
              t);
            return Unit.unit();
          }
        });
    }

    @Override public TASTDShaderFragment fragmentShaderVisit(
      final List<TASTDShaderFragmentInput> inputs,
      final List<TASTDShaderFragmentParameter> parameters,
      final List<TASTDShaderFragmentOutput> outputs,
      final List<TASTDShaderFragmentLocal> locals,
      final List<TASTDShaderFragmentOutputAssignment> output_assignments,
      final TASTDShaderFragment f)
    {
      return f;
    }

    @Override public TASTDShaderFragmentInput fragmentShaderVisitInput(
      final TASTDShaderFragmentInput i)
    {
      this.addTypeReference(i.getType());
      return i;
    }

    @Override public @Nullable
      TASTFragmentShaderLocalVisitorType<TASTDShaderFragmentLocal, UnreachableCodeException>
      fragmentShaderVisitLocalsPre()
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
      final TASTDShaderFragmentOutput o)
    {
      this.addTypeReference(o.getType());
      return o;
    }

    @Override public
      TASTDShaderFragmentOutputAssignment
      fragmentShaderVisitOutputAssignment(
        final TASTDShaderFragmentOutputAssignment a)
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
        final TASTDShaderFragmentParameter p)
    {
      this.addTypeReference(p.getType());
      return p;
    }
  }

  @EqualityReference private static final class GraphBuilderFragmentShaderLocal implements
    TASTFragmentShaderLocalVisitorType<TASTDShaderFragmentLocal, UnreachableCodeException>
  {
    private final Map<ModulePathFlat, TASTDModule> checked_modules;
    private final Map<String, TASTDTerm>           checked_terms;
    private final Map<String, TASTDType>           checked_types;
    private final GlobalGraph                      graph;
    private final LogUsableType                    log;
    private final ModulePath                       module_path;
    private final TASTShaderName                   source;

    public GraphBuilderFragmentShaderLocal(
      final TASTShaderName in_source,
      final Map<ModulePathFlat, TASTDModule> in_checked_modules,
      final Map<String, TASTDTerm> in_checked_terms,
      final Map<String, TASTDType> in_checked_types,
      final LogUsableType in_log,
      final ModulePath in_module_path,
      final GlobalGraph in_graph)
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
        final TASTDShaderFragmentLocalDiscard d)
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
      final TASTDShaderFragmentLocalValue v)
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

  @EqualityReference private static final class GraphBuilderShader implements
    TASTShaderVisitorType<TASTDShader, UnreachableCodeException>
  {
    private final Map<ModulePathFlat, TASTDModule> checked_modules;
    private final Map<String, TASTDTerm>           checked_terms;
    private final Map<String, TASTDType>           checked_types;
    private final GlobalGraph                      graph;
    private final LogUsableType                    log;
    private final ModulePath                       module_path;

    public GraphBuilderShader(
      final Map<ModulePathFlat, TASTDModule> in_checked_modules,
      final Map<String, TASTDTerm> in_checked_terms,
      final Map<String, TASTDType> in_checked_types,
      final LogUsableType in_log,
      final ModulePath in_module_path,
      final GlobalGraph in_graph)
    {
      this.checked_modules = in_checked_modules;
      this.checked_terms = in_checked_terms;
      this.checked_types = in_checked_types;
      this.log = in_log;
      this.module_path = in_module_path;
      this.graph = in_graph;
    }

    @Override public TASTDShader moduleVisitFragmentShader(
      final TASTDShaderFragment f)
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
      final TASTDShaderProgram p)
    {
      final TASTShaderName source =
        new TASTShaderName(this.module_path, p.getName());
      this.graph.addShader(source);
      this.graph.addShader(p.getFragmentShader());
      this.graph.addShader(p.getVertexShader());
      return p;
    }

    @Override public TASTDShader moduleVisitVertexShader(
      final TASTDShaderVertex v)
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

  @EqualityReference private static final class GraphBuilderShaderExpression implements
    TASTExpressionVisitorType<TASTExpressionType, TASTExpressionMatchConstantType, TASTDValueLocal, UnreachableCodeException>
  {
    private final Map<ModulePathFlat, TASTDModule> checked_modules;
    private final Map<String, TASTDTerm>           checked_terms;
    private final Map<String, TASTDType>           checked_types;
    private final GlobalGraph                      graph;
    private final LogUsableType                    log;
    private final ModulePath                       module_path;
    private final TASTShaderName                   source;

    public GraphBuilderShaderExpression(
      final TASTShaderName in_source,
      final Map<ModulePathFlat, TASTDModule> in_checked_modules,
      final Map<String, TASTDTerm> in_checked_terms,
      final Map<String, TASTDType> in_checked_types,
      final LogUsableType in_log,
      final ModulePath in_module_path,
      final GlobalGraph in_graph)
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
      final TASTTermName name)
    {
      name
        .termNameVisitableAccept(new TASTTermNameVisitorType<Unit, UnreachableCodeException>() {
          @Override public Unit termNameVisitExternal(
            final TASTTermNameExternal t)
          {
            return Unit.unit();
          }

          @Override public Unit termNameVisitGlobal(
            final TASTTermNameGlobal t)
          {
            GraphBuilderShaderExpression.this.graph.addShaderTermReference(
              GraphBuilderShaderExpression.this.source,
              t);
            return Unit.unit();
          }

          @Override public Unit termNameVisitLocal(
            final TASTTermNameLocal t)
          {
            return Unit.unit();
          }
        });
    }

    private void addTypeReference(
      final TType type)
    {
      type.getName().typeNameVisitableAccept(
        new TTypeNameVisitorType<Unit, UnreachableCodeException>() {
          @Override public Unit typeNameVisitBuiltIn(
            final TTypeNameBuiltIn t)
          {
            return Unit.unit();
          }

          @SuppressWarnings("synthetic-access") @Override public
            Unit
            typeNameVisitGlobal(
              final TTypeNameGlobal t)
          {
            GraphBuilderShaderExpression.this.graph.addShaderTypeReference(
              GraphBuilderShaderExpression.this.source,
              t);
            return Unit.unit();
          }
        });
    }

    @Override public TASTExpressionType expressionVisitApplication(
      final @Nullable List<TASTExpressionType> arguments,
      final TASTEApplication e)
    {
      this.addTermReference(e.getName());
      return e;
    }

    @Override public boolean expressionVisitApplicationPre(
      final TASTEApplication e)
    {
      return true;
    }

    @Override public TASTExpressionType expressionVisitBoolean(
      final TASTEBoolean e)
    {
      return e;
    }

    @Override public TASTExpressionType expressionVisitConditional(
      final @Nullable TASTExpressionType condition,
      final @Nullable TASTExpressionType left,
      final @Nullable TASTExpressionType right,
      final TASTEConditional e)
    {
      return e;
    }

    @Override public void expressionVisitConditionalConditionPost(
      final TASTEConditional e)
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalConditionPre(
      final TASTEConditional e)
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalLeftPost(
      final TASTEConditional e)
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalLeftPre(
      final TASTEConditional e)
    {
      // Nothing
    }

    @Override public boolean expressionVisitConditionalPre(
      final TASTEConditional e)
    {
      return true;
    }

    @Override public void expressionVisitConditionalRightPost(
      final TASTEConditional e)
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalRightPre(
      final TASTEConditional e)
    {
      // Nothing
    }

    @Override public TASTExpressionType expressionVisitInteger(
      final TASTEInteger e)
    {
      return e;
    }

    @Override public TASTExpressionType expressionVisitLet(
      final @Nullable List<TASTDValueLocal> bindings,
      final @Nullable TASTExpressionType body,
      final TASTELet e)
    {
      return e;
    }

    @Override public @Nullable
      TASTLocalLevelVisitorType<TASTDValueLocal, UnreachableCodeException>
      expressionVisitLetPre(
        final TASTELet e)
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

    @Override public
      TASTExpressionType
      expressionVisitMatch(
        final @Nullable TASTExpressionType discriminee,
        final @Nullable List<Pair<TASTExpressionMatchConstantType, TASTExpressionType>> cases,
        final @Nullable TASTExpressionType default_case,
        final TASTEMatchType m)
    {
      return m;
    }

    @Override public
      TASTExpressionMatchConstantType
      expressionVisitMatchCase(
        final TASTExpressionMatchConstantType c)
    {
      return c;
    }

    @Override public void expressionVisitMatchDiscrimineePost()
    {
      // Nothing
    }

    @Override public void expressionVisitMatchDiscrimineePre()
    {
      // Nothing
    }

    @Override public boolean expressionVisitMatchPre(
      final TASTEMatchType m)
    {
      return true;
    }

    @Override public TASTExpressionType expressionVisitNew(
      final @Nullable List<TASTExpressionType> arguments,
      final TASTENew e)
    {
      this.addTypeReference(e.getType());
      return e;
    }

    @Override public boolean expressionVisitNewPre(
      final TASTENew e)
    {
      return true;
    }

    @Override public TASTExpressionType expressionVisitReal(
      final TASTEReal e)
    {
      return e;
    }

    @Override public TASTExpressionType expressionVisitRecord(
      final TASTERecord e)
    {
      this.addTypeReference(e.getType());
      return e;
    }

    @Override public TASTExpressionType expressionVisitRecordProjection(
      final @Nullable TASTExpressionType body,
      final TASTERecordProjection e)
    {
      this.addTypeReference(NullCheck.notNull(body, "Body").getType());
      return e;
    }

    @Override public boolean expressionVisitRecordProjectionPre(
      final @Nullable TASTERecordProjection e)
    {
      return true;
    }

    @Override public TASTExpressionType expressionVisitSwizzle(
      final @Nullable TASTExpressionType body,
      final TASTESwizzle e)
    {
      return e;
    }

    @Override public boolean expressionVisitSwizzlePre(
      final TASTESwizzle e)
    {
      return true;
    }

    @Override public TASTExpressionType expressionVisitVariable(
      final TASTEVariable e)
    {
      this.addTermReference(e.getName());
      this.addTypeReference(e.getType());
      return e;
    }
  }

  @EqualityReference private static final class GraphBuilderShaderLocal implements
    TASTLocalLevelVisitorType<TASTDValueLocal, UnreachableCodeException>
  {
    private final Map<ModulePathFlat, TASTDModule> checked_modules;
    private final Map<String, TASTDTerm>           checked_terms;
    private final Map<String, TASTDType>           checked_types;
    private final GlobalGraph                      graph;
    private final LogUsableType                    log;
    private final ModulePath                       module_path;
    private final TASTShaderName                   source;

    public GraphBuilderShaderLocal(
      final TASTShaderName in_source,
      final Map<ModulePathFlat, TASTDModule> in_checked_modules,
      final Map<String, TASTDTerm> in_checked_terms,
      final Map<String, TASTDType> in_checked_types,
      final LogUsableType in_log,
      final ModulePath in_module_path,
      final GlobalGraph in_graph)
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
      final TASTDValueLocal v)
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

  @EqualityReference private static final class GraphBuilderTerm implements
    TASTTermVisitorType<TASTDTerm, UnreachableCodeException>
  {
    private final Map<ModulePathFlat, TASTDModule> checked_modules;
    private final Map<String, TASTDTerm>           checked_terms;
    private final Map<String, TASTDType>           checked_types;
    private final GlobalGraph                      graph;
    private final LogUsableType                    log;
    private final ModulePath                       module_path;

    public GraphBuilderTerm(
      final Map<ModulePathFlat, TASTDModule> in_checked_modules,
      final Map<String, TASTDTerm> in_checked_terms,
      final Map<String, TASTDType> in_checked_types,
      final LogUsableType in_log,
      final ModulePath in_module_path,
      final GlobalGraph in_graph)
    {
      this.checked_modules = in_checked_modules;
      this.checked_terms = in_checked_terms;
      this.checked_types = in_checked_types;
      this.log = in_log;
      this.module_path = in_module_path;
      this.graph = in_graph;
    }

    private void addTermTypeReference(
      final TASTTermNameGlobal source,
      final TType type)
    {
      type.getName().typeNameVisitableAccept(
        new TTypeNameVisitorType<Unit, UnreachableCodeException>() {
          @Override public Unit typeNameVisitBuiltIn(
            final TTypeNameBuiltIn t)
          {
            return Unit.unit();
          }

          @SuppressWarnings("synthetic-access") @Override public
            Unit
            typeNameVisitGlobal(
              final TTypeNameGlobal t)
          {
            GraphBuilderTerm.this.graph.addTermTypeReference(source, t);
            return Unit.unit();
          }
        });
    }

    @Override public TASTDTerm termVisitFunctionDefined(
      final TASTDFunctionDefined f)
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
      final TASTDFunctionExternal f)
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
      final TASTDValueDefined v)
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
      final TASTDValueExternal v)
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

  @EqualityReference private static final class GraphBuilderTermExpression implements
    TASTExpressionVisitorType<TASTExpressionType, TASTExpressionMatchConstantType, TASTDValueLocal, UnreachableCodeException>
  {
    private final Map<ModulePathFlat, TASTDModule> checked_modules;
    private final Map<String, TASTDTerm>           checked_terms;
    private final Map<String, TASTDType>           checked_types;
    private final GlobalGraph                      graph;
    private final LogUsableType                    log;
    private final ModulePath                       module_path;
    private final TASTTermNameGlobal               source;

    public GraphBuilderTermExpression(
      final TASTTermNameGlobal in_source,
      final Map<ModulePathFlat, TASTDModule> in_checked_modules,
      final Map<String, TASTDTerm> in_checked_terms,
      final Map<String, TASTDType> in_checked_types,
      final LogUsableType in_log,
      final ModulePath in_module_path,
      final GlobalGraph in_graph)
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
      final TASTTermName name)
    {
      name
        .termNameVisitableAccept(new TASTTermNameVisitorType<Unit, UnreachableCodeException>() {
          @Override public Unit termNameVisitExternal(
            final TASTTermNameExternal t)
          {
            return Unit.unit();
          }

          @Override public Unit termNameVisitGlobal(
            final TASTTermNameGlobal t)
          {
            GraphBuilderTermExpression.this.graph.addTermTermReference(
              GraphBuilderTermExpression.this.source,
              t);
            return Unit.unit();
          }

          @Override public Unit termNameVisitLocal(
            final TASTTermNameLocal t)
          {
            return Unit.unit();
          }
        });
    }

    private void addTermTypeReference(
      final TType type)
    {
      type.getName().typeNameVisitableAccept(
        new TTypeNameVisitorType<Unit, UnreachableCodeException>() {
          @Override public Unit typeNameVisitBuiltIn(
            final TTypeNameBuiltIn t)
          {
            return Unit.unit();
          }

          @SuppressWarnings("synthetic-access") @Override public
            Unit
            typeNameVisitGlobal(
              final TTypeNameGlobal t)
          {
            GraphBuilderTermExpression.this.graph.addTermTypeReference(
              GraphBuilderTermExpression.this.source,
              t);
            return Unit.unit();
          }
        });
    }

    @Override public TASTExpressionType expressionVisitApplication(
      final @Nullable List<TASTExpressionType> arguments,
      final TASTEApplication e)
    {
      this.addTermReference(e.getName());
      return e;
    }

    @Override public boolean expressionVisitApplicationPre(
      final TASTEApplication e)
    {
      return true;
    }

    @Override public TASTExpressionType expressionVisitBoolean(
      final TASTEBoolean e)
    {
      return e;
    }

    @Override public TASTExpressionType expressionVisitConditional(
      final @Nullable TASTExpressionType condition,
      final @Nullable TASTExpressionType left,
      final @Nullable TASTExpressionType right,
      final TASTEConditional e)
    {
      return e;
    }

    @Override public void expressionVisitConditionalConditionPost(
      final TASTEConditional e)
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalConditionPre(
      final TASTEConditional e)
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalLeftPost(
      final TASTEConditional e)
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalLeftPre(
      final TASTEConditional e)
    {
      // Nothing
    }

    @Override public boolean expressionVisitConditionalPre(
      final TASTEConditional e)
    {
      return true;
    }

    @Override public void expressionVisitConditionalRightPost(
      final TASTEConditional e)
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalRightPre(
      final TASTEConditional e)
    {
      // Nothing
    }

    @Override public TASTExpressionType expressionVisitInteger(
      final TASTEInteger e)
    {
      return e;
    }

    @Override public TASTExpressionType expressionVisitLet(
      final @Nullable List<TASTDValueLocal> bindings,
      final @Nullable TASTExpressionType body,
      final TASTELet e)
    {
      return e;
    }

    @Override public @Nullable
      TASTLocalLevelVisitorType<TASTDValueLocal, UnreachableCodeException>
      expressionVisitLetPre(
        final TASTELet e)
    {
      return new TASTLocalLevelVisitorType<TASTDValueLocal, UnreachableCodeException>() {
        @SuppressWarnings("synthetic-access") @Override public
          TASTDValueLocal
          localVisitValueLocal(
            final TASTDValueLocal v)
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

    @Override public
      TASTExpressionType
      expressionVisitMatch(
        final @Nullable TASTExpressionType discriminee,
        final @Nullable List<Pair<TASTExpressionMatchConstantType, TASTExpressionType>> cases,
        final @Nullable TASTExpressionType default_case,
        final TASTEMatchType m)
    {
      return m;
    }

    @Override public
      TASTExpressionMatchConstantType
      expressionVisitMatchCase(
        final TASTExpressionMatchConstantType c)
    {
      return c;
    }

    @Override public void expressionVisitMatchDiscrimineePost()
    {
      // Nothing.
    }

    @Override public void expressionVisitMatchDiscrimineePre()
    {
      // Nothing.
    }

    @Override public boolean expressionVisitMatchPre(
      final TASTEMatchType m)
    {
      return true;
    }

    @Override public TASTExpressionType expressionVisitNew(
      final @Nullable List<TASTExpressionType> arguments,
      final TASTENew e)
    {
      this.addTermTypeReference(e.getType());
      return e;
    }

    @Override public boolean expressionVisitNewPre(
      final TASTENew e)
    {
      return true;
    }

    @Override public TASTExpressionType expressionVisitReal(
      final TASTEReal e)
    {
      return e;
    }

    @Override public TASTExpressionType expressionVisitRecord(
      final TASTERecord e)
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

    @Override public TASTExpressionType expressionVisitRecordProjection(
      final @Nullable TASTExpressionType body,
      final TASTERecordProjection e)
    {
      this.addTermTypeReference(NullCheck.notNull(body, "Body").getType());
      return e;
    }

    @Override public boolean expressionVisitRecordProjectionPre(
      final @Nullable TASTERecordProjection e)
    {
      return true;
    }

    @Override public TASTExpressionType expressionVisitSwizzle(
      final @Nullable TASTExpressionType body,
      final TASTESwizzle e)
    {
      return e;
    }

    @Override public boolean expressionVisitSwizzlePre(
      final TASTESwizzle e)
    {
      return true;
    }

    @Override public TASTExpressionType expressionVisitVariable(
      final TASTEVariable e)
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

  @EqualityReference private static final class GraphBuilderType implements
    TASTTypeVisitorType<TASTDType, UnreachableCodeException>
  {
    private final Map<ModulePathFlat, TASTDModule> checked_modules;
    private final Map<String, TASTDType>           checked_types;
    private final GlobalGraph                      graph;
    private final LogUsableType                    log;
    private final ModulePath                       module_path;

    public GraphBuilderType(
      final Map<ModulePathFlat, TASTDModule> in_checked_modules,
      final Map<String, TASTDType> in_checked_types,
      final LogUsableType in_log,
      final ModulePath in_module_path,
      final GlobalGraph in_graph)
    {
      this.checked_modules = NullCheck.notNull(in_checked_modules, "Modules");
      this.checked_types = NullCheck.notNull(in_checked_types, "Types");
      this.log = in_log;
      this.module_path = NullCheck.notNull(in_module_path, "Path");
      this.graph = in_graph;
    }

    @Override public TASTDType typeVisitTypeRecord(
      final TASTDTypeRecord r)
    {
      final TTypeNameGlobal source =
        new TTypeNameGlobal(this.module_path, r.getName());

      this.graph.addType(source);

      for (final TASTDTypeRecordField f : r.getFields()) {
        final TManifestType ft = f.getType();
        ft.getName().typeNameVisitableAccept(
          new TTypeNameVisitorType<Unit, UnreachableCodeException>() {
            @Override public Unit typeNameVisitBuiltIn(
              final TTypeNameBuiltIn t)
            {
              return Unit.unit();
            }

            @SuppressWarnings("synthetic-access") @Override public
              Unit
              typeNameVisitGlobal(
                final TTypeNameGlobal t)
            {
              GraphBuilderType.this.graph.addTypeTypeReference(source, t);
              return Unit.unit();
            }
          });
      }

      return r;
    }
  }

  @EqualityReference private static final class GraphBuilderVertexShader implements
    TASTVertexShaderVisitorType<TASTDShaderVertex, TASTDShaderVertexInput, TASTDShaderVertexParameter, TASTDShaderVertexOutput, TASTDShaderVertexLocalValue, TASTDShaderVertexOutputAssignment, UnreachableCodeException>
  {
    private final Map<ModulePathFlat, TASTDModule> checked_modules;
    private final Map<String, TASTDTerm>           checked_terms;
    private final Map<String, TASTDType>           checked_types;
    private final GlobalGraph                      graph;
    private final LogUsableType                    log;
    private final ModulePath                       module_path;
    private final TASTShaderName                   source;

    public GraphBuilderVertexShader(
      final TASTShaderName in_source,
      final Map<ModulePathFlat, TASTDModule> in_checked_modules,
      final Map<String, TASTDTerm> in_checked_terms,
      final Map<String, TASTDType> in_checked_types,
      final LogUsableType in_log,
      final ModulePath in_module_path,
      final GlobalGraph in_graph)
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
      final TType type)
      throws UnreachableCodeException
    {
      type.getName().typeNameVisitableAccept(
        new TTypeNameVisitorType<Unit, UnreachableCodeException>() {
          @Override public Unit typeNameVisitBuiltIn(
            final TTypeNameBuiltIn t)
          {
            return Unit.unit();
          }

          @SuppressWarnings("synthetic-access") @Override public
            Unit
            typeNameVisitGlobal(
              final TTypeNameGlobal t)
          {
            GraphBuilderVertexShader.this.graph.addShaderTypeReference(
              GraphBuilderVertexShader.this.source,
              t);
            return Unit.unit();
          }
        });
    }

    @Override public TASTDShaderVertex vertexShaderVisit(
      final List<TASTDShaderVertexInput> inputs,
      final List<TASTDShaderVertexParameter> parameters,
      final List<TASTDShaderVertexOutput> outputs,
      final List<TASTDShaderVertexLocalValue> locals,
      final List<TASTDShaderVertexOutputAssignment> output_assignments,
      final TASTDShaderVertex v)
    {
      return v;
    }

    @Override public TASTDShaderVertexInput vertexShaderVisitInput(
      final TASTDShaderVertexInput i)
    {
      this.addTypeReference(i.getType());
      return i;
    }

    @Override public @Nullable
      TASTVertexShaderLocalVisitorType<TASTDShaderVertexLocalValue, UnreachableCodeException>
      vertexShaderVisitLocalsPre()
    {
      return new TASTVertexShaderLocalVisitorType<TASTDShaderVertexLocalValue, UnreachableCodeException>() {
        @SuppressWarnings("synthetic-access") @Override public
          TASTDShaderVertexLocalValue
          vertexShaderVisitLocalValue(
            final TASTDShaderVertexLocalValue v)
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
      final TASTDShaderVertexOutput o)
    {
      this.addTypeReference(o.getType());
      return o;
    }

    @Override public
      TASTDShaderVertexOutputAssignment
      vertexShaderVisitOutputAssignment(
        final TASTDShaderVertexOutputAssignment a)
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
      final TASTDShaderVertexParameter p)
    {
      this.addTypeReference(p.getType());
      return p;
    }
  }

  static TGraphs newGraphs(
    final LogUsableType log)
  {
    return new TGraphs(log);
  }

  private final LogUsableType log;

  private TGraphs(
    final LogUsableType in_log)
  {
    this.log = in_log;
  }

  public GlobalGraph check(
    final Map<ModulePathFlat, TASTDModule> checked_modules)
  {
    NullCheck.notNull(checked_modules, "Modules");

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
