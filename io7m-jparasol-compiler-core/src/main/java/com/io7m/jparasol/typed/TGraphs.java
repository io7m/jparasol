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
import com.io7m.jparasol.typed.ast.TASTEitherTypeTerm;
import com.io7m.jparasol.typed.ast.TASTEitherTypeTerm.TASTEitherTerm;
import com.io7m.jparasol.typed.ast.TASTEitherTypeTerm.TASTEitherType;
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
import com.io7m.jparasol.typed.ast.TASTExpressionVisitor;
import com.io7m.jparasol.typed.ast.TASTLocalLevelVisitor;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDFunctionArgument;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDFunctionDefined;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDFunctionExternal;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDModule;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDTerm;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDType;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDTypeRecord;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDTypeRecordField;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDValue;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDValueLocal;
import com.io7m.jparasol.typed.ast.TASTReference;
import com.io7m.jparasol.typed.ast.TASTTermName;
import com.io7m.jparasol.typed.ast.TASTTermName.TASTTermNameBuiltIn;
import com.io7m.jparasol.typed.ast.TASTTermName.TASTTermNameGlobal;
import com.io7m.jparasol.typed.ast.TASTTermName.TASTTermNameLocal;
import com.io7m.jparasol.typed.ast.TASTTermNameFlat;
import com.io7m.jparasol.typed.ast.TASTTermNameVisitor;
import com.io7m.jparasol.typed.ast.TASTTermVisitor;
import com.io7m.jparasol.typed.ast.TASTTypeVisitor;

final class TGraphs
{
  /**
   * Graphs of term->term, term->type, and type->type dependencies.
   */

  static final class GlobalGraph
  {
    private final @Nonnull GlobalTermTermGraph term_term;
    private final @Nonnull GlobalTermTypeGraph term_type;
    private final @Nonnull GlobalTypeTypeGraph type_type;

    public GlobalGraph(
      final @Nonnull Log log)
    {
      this.term_term = new GlobalTermTermGraph(log);
      this.term_type = new GlobalTermTypeGraph(log);
      this.type_type = new GlobalTypeTypeGraph(log);
    }

    public void addTerm(
      final @Nonnull TASTTermNameGlobal term)
      throws ConstraintError
    {
      final TASTTermNameFlat flat = TASTTermNameFlat.fromTermNameGlobal(term);
      this.term_term.addTerm(flat);
      this.term_type.addTerm(flat);
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

    public @Nonnull GlobalTermTermGraph getTermTermGraph()
    {
      return this.term_term;
    }

    public @Nonnull GlobalTermTypeGraph getTermTypeGraph()
    {
      return this.term_type;
    }

    public @Nonnull GlobalTypeTypeGraph getTypeTypeGraph()
    {
      return this.type_type;
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
      final @Nonnull Log log)
    {
      this.log = new Log(log, "global-term-term-graph");
      this.graph =
        new DirectedAcyclicGraph<TASTTermNameFlat, TASTReference>(
          TASTReference.class);
    }

    public void addTerm(
      final @Nonnull TASTTermNameFlat term)
    {
      if (this.graph.containsVertex(term) == false) {
        if (this.log.enabled(Level.LOG_DEBUG)) {
          final String path = term.getPath().getActual();
          this.log
            .debug(String.format("Add term %s.%s", path, term.getName()));
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
    private final @Nonnull DirectedAcyclicGraph<TASTEitherTypeTerm, TASTReference> graph;
    private final @Nonnull Log                                                     log;

    public GlobalTermTypeGraph(
      final @Nonnull Log log)
    {
      this.log = new Log(log, "global-term-type-graph");
      this.graph =
        new DirectedAcyclicGraph<TASTEitherTypeTerm, TASTReference>(
          TASTReference.class);
    }

    public void addTerm(
      final @Nonnull TASTTermNameFlat flat)
      throws ConstraintError
    {
      final TASTEitherTerm e_term = new TASTEitherTerm(flat);
      if (this.graph.containsVertex(e_term) == false) {
        if (this.log.enabled(Level.LOG_DEBUG)) {
          this.log.debug(String.format("Add term %s.%s", flat
            .getPath()
            .getActual(), flat.getName()));
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

      final TASTEitherTerm e_source = new TASTEitherTerm(source_flat);
      final TASTEitherType e_target = new TASTEitherType(target_flat);
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
      final TASTEitherType e_type = new TASTEitherType(type);
      if (this.graph.containsVertex(e_type) == false) {
        if (this.log.enabled(Level.LOG_DEBUG)) {
          this.log.debug(String.format("Add type %s.%s", type
            .getPath()
            .getActual(), type.getName()));
        }
        this.graph.addVertex(e_type);
      }
    }

    public @Nonnull
      DirectedAcyclicGraph<TASTEitherTypeTerm, TASTReference>
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
      final @Nonnull Log log)
    {
      this.log = new Log(log, "global-type-type-graph");
      this.graph =
        new DirectedAcyclicGraph<TTypeNameFlat, TASTReference>(
          TASTReference.class);
    }

    public void addType(
      final TTypeNameFlat type)
    {
      if (this.graph.containsVertex(type) == false) {
        if (this.log.enabled(Level.LOG_DEBUG)) {
          this.log.debug(String.format("Add type %s.%s", type
            .getPath()
            .getActual(), type.getName()));
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
      final @Nonnull Map<ModulePathFlat, TASTDModule> checked_modules,
      final @Nonnull Map<String, TASTDTerm> checked_terms,
      final @Nonnull Map<String, TASTDType> checked_types,
      final @Nonnull Log log,
      final @Nonnull ModulePath module_path,
      final @Nonnull GlobalGraph graph)
    {
      this.checked_modules = checked_modules;
      this.checked_terms = checked_terms;
      this.checked_types = checked_types;
      this.log = log;
      this.module_path = module_path;
      this.graph = graph;
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

    @Override public TASTDTerm termVisitValue(
      final @Nonnull TASTDValue v)
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
  }

  /**
   * A visitor that walks over an expression that occurs in a well-typed term,
   * and adds references to other well-typed terms to the given graph.
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
      final @Nonnull TASTTermNameGlobal source,
      final @Nonnull Map<ModulePathFlat, TASTDModule> checked_modules,
      final @Nonnull Map<String, TASTDTerm> checked_terms,
      final @Nonnull Map<String, TASTDType> checked_types,
      final @Nonnull Log log,
      final @Nonnull ModulePath module_path,
      final @Nonnull GlobalGraph graph)
    {
      this.source = source;
      this.checked_modules = checked_modules;
      this.checked_terms = checked_terms;
      this.checked_types = checked_types;
      this.log = log;
      this.module_path = module_path;
      this.graph = graph;
    }

    @SuppressWarnings("synthetic-access") private void addTermReference(
      final @Nonnull TASTTermName name)
      throws ConstraintError
    {
      name
        .termNameVisitableAccept(new TASTTermNameVisitor<Unit, ConstraintError>() {
          @Override public Unit termNameVisitBuiltIn(
            final TASTTermNameBuiltIn t)
            throws ConstraintError,
              ConstraintError
          {
            return Unit.unit();
          }

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

    @Override public void expressionVisitApplicationPre(
      final @Nonnull TASTEApplication e)
      throws ConstraintError,
        ConstraintError
    {
      // Nothing
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
      return new GraphBuilderTermLocal(
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
      this.addTermTypeReference(e.getType());
      return e;
    }

    @Override public void expressionVisitNewPre(
      final @Nonnull TASTENew e)
      throws ConstraintError,
        ConstraintError
    {
      // Nothing
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

    @Override public void expressionVisitRecordProjectionPre(
      final @Nonnull TASTERecordProjection e)
      throws ConstraintError,
        ConstraintError
    {
      // Nothing
    }

    @Override public TASTExpression expressionVisitSwizzle(
      final @Nonnull TASTExpression body,
      final @Nonnull TASTESwizzle e)
      throws ConstraintError,
        ConstraintError
    {
      return e;
    }

    @Override public void expressionVisitSwizzlePre(
      final @Nonnull TASTESwizzle e)
      throws ConstraintError,
        ConstraintError
    {
      // Nothing
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

  private static final class GraphBuilderTermLocal implements
    TASTLocalLevelVisitor<TASTDValueLocal, ConstraintError>
  {
    private final @Nonnull Map<ModulePathFlat, TASTDModule> checked_modules;
    private final @Nonnull Map<String, TASTDTerm>           checked_terms;
    private final @Nonnull Map<String, TASTDType>           checked_types;
    private final @Nonnull GlobalGraph                      graph;
    private final @Nonnull Log                              log;
    private final @Nonnull ModulePath                       module_path;
    private final @Nonnull TASTTermNameGlobal               source;

    public GraphBuilderTermLocal(
      final @Nonnull TASTTermNameGlobal source,
      final @Nonnull Map<ModulePathFlat, TASTDModule> checked_modules,
      final @Nonnull Map<String, TASTDTerm> checked_terms,
      final @Nonnull Map<String, TASTDType> checked_types,
      final @Nonnull Log log,
      final @Nonnull ModulePath module_path,
      final @Nonnull GlobalGraph graph)
    {
      this.source = source;
      this.checked_modules = checked_modules;
      this.checked_terms = checked_terms;
      this.checked_types = checked_types;
      this.log = log;
      this.module_path = module_path;
      this.graph = graph;
    }

    @Override public TASTDValueLocal localVisitValueLocal(
      final @Nonnull TASTDValueLocal v)
      throws ConstraintError,
        ConstraintError
    {
      v.getExpression().expressionVisitableAccept(
        new GraphBuilderTermExpression(
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
      final @Nonnull Map<ModulePathFlat, TASTDModule> checked_modules,
      final @Nonnull Map<String, TASTDType> checked_types,
      final @Nonnull Log log,
      final @Nonnull ModulePath module_path,
      final @Nonnull GlobalGraph graph)
    {
      this.checked_modules = checked_modules;
      this.checked_types = checked_types;
      this.log = log;
      this.module_path = module_path;
      this.graph = graph;
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

  static @Nonnull TGraphs newGraphs(
    final @Nonnull Log log)
  {
    return new TGraphs(log);
  }

  private final @Nonnull Log log;

  private TGraphs(
    final @Nonnull Log log)
  {
    this.log = log;
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
    }

    return graph;
  }
}
