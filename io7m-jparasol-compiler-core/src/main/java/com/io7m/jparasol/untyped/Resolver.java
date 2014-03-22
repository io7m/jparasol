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

package com.io7m.jparasol.untyped;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph.CycleFoundException;
import org.jgrapht.traverse.TopologicalOrderIterator;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jaux.functional.Option;
import com.io7m.jaux.functional.Option.Some;
import com.io7m.jaux.functional.PartialFunction;
import com.io7m.jaux.functional.Unit;
import com.io7m.jlog.Level;
import com.io7m.jlog.Log;
import com.io7m.jparasol.ModulePath;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.lexer.Token.TokenIdentifierUpper;
import com.io7m.jparasol.typed.TType;
import com.io7m.jparasol.typed.TTypeName.TTypeNameBuiltIn;
import com.io7m.jparasol.untyped.ast.resolved.UASTRCompilation;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDExternal;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDFunctionArgument;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDFunctionDefined;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDFunctionExternal;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDImport;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDModule;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDShader;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDShaderFragment;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDShaderFragmentInput;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDShaderFragmentLocal;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDShaderFragmentLocalDiscard;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDShaderFragmentLocalValue;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDShaderFragmentOutput;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDShaderFragmentOutputAssignment;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDShaderFragmentOutputData;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDShaderFragmentOutputDepth;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDShaderFragmentParameter;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDShaderProgram;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDShaderVertex;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDShaderVertexInput;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDShaderVertexLocalValue;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDShaderVertexOutput;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDShaderVertexOutputAssignment;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDShaderVertexParameter;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDTerm;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDType;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDTypeRecord;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDTypeRecordField;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDValueDefined;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDValueExternal;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDValueLocal;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDeclarationModuleLevel;
import com.io7m.jparasol.untyped.ast.resolved.UASTRExpression;
import com.io7m.jparasol.untyped.ast.resolved.UASTRExpression.UASTREApplication;
import com.io7m.jparasol.untyped.ast.resolved.UASTRExpression.UASTREBoolean;
import com.io7m.jparasol.untyped.ast.resolved.UASTRExpression.UASTREConditional;
import com.io7m.jparasol.untyped.ast.resolved.UASTRExpression.UASTREInteger;
import com.io7m.jparasol.untyped.ast.resolved.UASTRExpression.UASTRELet;
import com.io7m.jparasol.untyped.ast.resolved.UASTRExpression.UASTRENew;
import com.io7m.jparasol.untyped.ast.resolved.UASTRExpression.UASTREReal;
import com.io7m.jparasol.untyped.ast.resolved.UASTRExpression.UASTRERecord;
import com.io7m.jparasol.untyped.ast.resolved.UASTRExpression.UASTRERecordProjection;
import com.io7m.jparasol.untyped.ast.resolved.UASTRExpression.UASTRESwizzle;
import com.io7m.jparasol.untyped.ast.resolved.UASTRExpression.UASTREVariable;
import com.io7m.jparasol.untyped.ast.resolved.UASTRExpression.UASTRRecordFieldAssignment;
import com.io7m.jparasol.untyped.ast.resolved.UASTRShaderName;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTermName;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTermName.UASTRTermNameGlobal;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTermName.UASTRTermNameLocal;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTermNameVisitor;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTypeName;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTypeName.UASTRTypeNameBuiltIn;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTypeName.UASTRTypeNameGlobal;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTypeNameVisitor;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUCompilation;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDRecordVisitor;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDExternal;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDFunctionArgument;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDFunctionDefined;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDFunctionExternal;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDImport;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDModule;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderFragment;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderFragmentInput;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderFragmentLocalDiscard;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderFragmentLocalValue;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderFragmentOutputAssignment;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderFragmentOutputData;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderFragmentOutputDepth;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderFragmentParameter;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderProgram;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderVertex;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderVertexInput;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderVertexLocalValue;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderVertexOutput;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderVertexOutputAssignment;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderVertexParameter;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDTerm;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDTypeRecord;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDTypeRecordField;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDValueDefined;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDValueExternal;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDValueLocal;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUEApplication;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUEBoolean;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUEConditional;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUEInteger;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUELet;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUENew;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUEReal;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUERecord;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUERecordProjection;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUESwizzle;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUEVariable;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTURecordFieldAssignment;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpressionVisitor;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUFragmentShaderLocalVisitor;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUFragmentShaderOutputVisitor;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUFragmentShaderVisitor;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTULocalLevelVisitor;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUModuleVisitor;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUShaderPath;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUShaderVisitor;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUTermVisitor;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUTypePath;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUTypeVisitor;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUVertexShaderLocalVisitor;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUVertexShaderVisitor;
import com.io7m.jparasol.untyped.ast.unique_binders.UniqueName;
import com.io7m.jparasol.untyped.ast.unique_binders.UniqueName.UniqueNameLocal;
import com.io7m.jparasol.untyped.ast.unique_binders.UniqueName.UniqueNameNonLocal;
import com.io7m.jparasol.untyped.ast.unique_binders.UniqueNameVisitor;

public final class Resolver
{
  private static final class ExpressionResolver implements
    UASTUExpressionVisitor<UASTRExpression, UASTRDValueLocal, ResolverError>
  {
    private final @Nonnull UASTUDModule                      module;
    private final @Nonnull Map<ModulePathFlat, UASTUDModule> modules;
    private final @CheckForNull UASTUDTerm                   term;
    private final @CheckForNull TermGraph                    term_graph;

    public ExpressionResolver(
      final @CheckForNull UASTUDTerm term,
      final @Nonnull UASTUDModule module,
      final @Nonnull Map<ModulePathFlat, UASTUDModule> modules,
      final @CheckForNull TermGraph term_graph)
    {
      this.term = term;
      this.module = module;
      this.modules = modules;
      this.term_graph = term_graph;
    }

    /**
     * Add a reference from the current term to the given name, raising an
     * error on cyclic references.
     */

    private void checkTermReferenceRecursion(
      final @Nonnull UASTRTermName name)
      throws ConstraintError,
        ResolverError
    {
      if (this.term_graph != null) {
        name
          .termNameVisitableAccept(new UASTRTermNameVisitor<Unit, ResolverError>() {
            @SuppressWarnings("synthetic-access") @Override public
              Unit
              termNameVisitGlobal(
                final @Nonnull UASTRTermNameGlobal t)
                throws ConstraintError,
                  ResolverError
            {
              assert ExpressionResolver.this.term_graph != null;
              assert ExpressionResolver.this.term != null;

              ExpressionResolver.this.term_graph.addTermReference(
                ExpressionResolver.this.module.getPath(),
                ExpressionResolver.this.term.getName(),
                t.getPath(),
                t.getName());

              return Unit.unit();
            }

            @Override public Unit termNameVisitLocal(
              final @Nonnull UASTRTermNameLocal t)
              throws ConstraintError,
                ResolverError
            {
              return Unit.unit();
            }
          });
      }
    }

    @Override public UASTREApplication expressionVisitApplication(
      final @Nonnull List<UASTRExpression> arguments,
      final @Nonnull UASTUEApplication e)
      throws ResolverError,
        ConstraintError
    {
      final UniqueName en = e.getName();
      final UASTRTermName name =
        Resolver.lookupTerm(this.module, this.modules, en);

      this.checkTermReferenceRecursion(name);
      return new UASTREApplication(name, arguments);
    }

    @Override public void expressionVisitApplicationPre(
      final @Nonnull UASTUEApplication e)
      throws ResolverError,
        ConstraintError
    {
      // Nothing
    }

    @Override public UASTREBoolean expressionVisitBoolean(
      final @Nonnull UASTUEBoolean e)
      throws ResolverError,
        ConstraintError
    {
      return new UASTREBoolean(e.getToken());
    }

    @Override public UASTREConditional expressionVisitConditional(
      final @Nonnull UASTRExpression condition,
      final @Nonnull UASTRExpression left,
      final @Nonnull UASTRExpression right,
      final @Nonnull UASTUEConditional e)
      throws ResolverError,
        ConstraintError
    {
      return new UASTREConditional(e.getIf(), condition, left, right);
    }

    @Override public void expressionVisitConditionalPre(
      final @Nonnull UASTUEConditional e)
      throws ResolverError,
        ConstraintError
    {
      // Nothing
    }

    @Override public UASTREInteger expressionVisitInteger(
      final @Nonnull UASTUEInteger e)
      throws ResolverError,
        ConstraintError
    {
      return new UASTREInteger(e.getToken());
    }

    @Override public UASTRELet expressionVisitLet(
      final @Nonnull List<UASTRDValueLocal> bindings,
      final @Nonnull UASTRExpression body,
      final @Nonnull UASTUELet e)
      throws ResolverError,
        ConstraintError
    {
      return new UASTRELet(e.getToken(), bindings, body);
    }

    @Override public
      UASTULocalLevelVisitor<UASTRDValueLocal, ResolverError>
      expressionVisitLetPre(
        final @Nonnull UASTUELet e)
        throws ResolverError,
          ConstraintError
    {
      return new LocalResolver(
        this.term,
        this.module,
        this.modules,
        this.term_graph);
    }

    @SuppressWarnings("synthetic-access") @Override public
      UASTRENew
      expressionVisitNew(
        final @Nonnull List<UASTRExpression> arguments,
        final @Nonnull UASTUENew e)
        throws ResolverError,
          ConstraintError
    {
      final UASTUTypePath en = e.getName();
      final UASTRTypeName name =
        Resolver.lookupType(
          this.modules,
          this.module,
          en.getModule(),
          en.getName());

      return new UASTRENew(name, arguments);
    }

    @Override public void expressionVisitNewPre(
      final @Nonnull UASTUENew e)
      throws ResolverError,
        ConstraintError
    {
      // Nothing
    }

    @Override public UASTREReal expressionVisitReal(
      final @Nonnull UASTUEReal e)
      throws ResolverError,
        ConstraintError
    {
      return new UASTREReal(e.getToken());
    }

    @SuppressWarnings("synthetic-access") @Override public
      UASTRERecord
      expressionVisitRecord(
        final @Nonnull UASTUERecord e)
        throws ResolverError,
          ConstraintError
    {
      final UASTUTypePath etp = e.getTypePath();
      final UASTRTypeName type_path =
        Resolver.lookupType(
          this.modules,
          this.module,
          etp.getModule(),
          etp.getName());

      final List<UASTURecordFieldAssignment> ea = e.getAssignments();
      final List<UASTRRecordFieldAssignment> assignments =
        new ArrayList<UASTRRecordFieldAssignment>(ea.size());

      for (final UASTURecordFieldAssignment a : e.getAssignments()) {
        final ExpressionResolver er =
          new ExpressionResolver(
            this.term,
            this.module,
            this.modules,
            this.term_graph);
        final UASTRExpression ex =
          a.getExpression().expressionVisitableAccept(er);
        assignments.add(new UASTRRecordFieldAssignment(a.getName(), ex));
      }

      return new UASTRERecord(type_path, assignments);
    }

    @Override public UASTRERecordProjection expressionVisitRecordProjection(
      final @Nonnull UASTRExpression body,
      final @Nonnull UASTUERecordProjection e)
      throws ResolverError,
        ConstraintError
    {
      return new UASTRERecordProjection(body, e.getField());
    }

    @Override public void expressionVisitRecordProjectionPre(
      final @Nonnull UASTUERecordProjection e)
      throws ResolverError,
        ConstraintError
    {
      // Nothing
    }

    @Override public UASTRESwizzle expressionVisitSwizzle(
      final @Nonnull UASTRExpression body,
      final @Nonnull UASTUESwizzle e)
      throws ResolverError,
        ConstraintError
    {
      return new UASTRESwizzle(body, e.getFields());
    }

    @Override public void expressionVisitSwizzlePre(
      final @Nonnull UASTUESwizzle e)
      throws ResolverError,
        ConstraintError
    {
      // Nothing
    }

    @Override public UASTREVariable expressionVisitVariable(
      final @Nonnull UASTUEVariable e)
      throws ResolverError,
        ConstraintError
    {
      final UniqueName unique = e.getName();
      final UASTRTermName name =
        Resolver.lookupTerm(this.module, this.modules, unique);

      this.checkTermReferenceRecursion(name);
      return new UASTREVariable(name);
    }
  }

  private static final class FragmentShaderLocalResolver implements
    UASTUFragmentShaderLocalVisitor<UASTRDShaderFragmentLocal, ResolverError>
  {
    private final @Nonnull Log                               log;
    private final @Nonnull UASTUDModule                      module;
    private final @Nonnull Map<ModulePathFlat, UASTUDModule> modules;
    private final @Nonnull UASTUDShaderFragment              shader;

    public FragmentShaderLocalResolver(
      final @Nonnull Log log,
      final @Nonnull UASTUDModule module,
      final @Nonnull Map<ModulePathFlat, UASTUDModule> modules,
      final @Nonnull UASTUDShaderFragment shader)
    {
      this.module = module;
      this.modules = modules;
      this.shader = shader;
      this.log = log;
    }

    @Override public
      UASTRDShaderFragmentLocalDiscard
      fragmentShaderVisitLocalDiscard(
        final @Nonnull UASTUDShaderFragmentLocalDiscard d)
        throws ResolverError,
          ConstraintError
    {
      final UASTRExpression ex =
        d.getExpression().expressionVisitableAccept(
          new ExpressionResolver(null, this.module, this.modules, null));

      return new UASTRDShaderFragmentLocalDiscard(d.getDiscard(), ex);
    }

    @Override public
      UASTRDShaderFragmentLocalValue
      fragmentShaderVisitLocalValue(
        final @Nonnull UASTUDShaderFragmentLocalValue v)
        throws ResolverError,
          ConstraintError
    {
      final UASTUDValueLocal vv = v.getValue();
      final LocalResolver lr =
        new LocalResolver(null, this.module, this.modules, null);

      return new UASTRDShaderFragmentLocalValue(vv.localVisitableAccept(lr));
    }
  }

  private static final class FragmentShaderResolver implements
    UASTUFragmentShaderVisitor<UASTRDShaderFragment, UASTRDShaderFragmentInput, UASTRDShaderFragmentParameter, UASTRDShaderFragmentOutput, UASTRDShaderFragmentLocal, UASTRDShaderFragmentOutputAssignment, ResolverError>
  {
    private final @Nonnull Log                               log;
    private final @Nonnull UASTUDModule                      module;
    private final @Nonnull Map<ModulePathFlat, UASTUDModule> modules;
    private final @Nonnull Map<String, TokenIdentifierLower> outputs_declared;
    private final @Nonnull UASTUDShaderFragment              shader;

    public FragmentShaderResolver(
      final @Nonnull Log log,
      final @Nonnull UASTUDModule module,
      final @Nonnull Map<ModulePathFlat, UASTUDModule> modules,
      final @Nonnull UASTUDShaderFragment f)
    {
      this.module = module;
      this.modules = modules;
      this.shader = f;
      this.log = log;
      this.outputs_declared = new HashMap<String, TokenIdentifierLower>();
    }

    @Override public
      UASTRDShaderFragment
      fragmentShaderVisit(
        final @Nonnull List<UASTRDShaderFragmentInput> inputs,
        final @Nonnull List<UASTRDShaderFragmentParameter> parameters,
        final @Nonnull List<UASTRDShaderFragmentOutput> outputs,
        final @Nonnull List<UASTRDShaderFragmentLocal> locals,
        final @Nonnull List<UASTRDShaderFragmentOutputAssignment> output_assignments,
        final @Nonnull UASTUDShaderFragment f)
        throws ResolverError,
          ConstraintError
    {
      return new UASTRDShaderFragment(
        f.getName(),
        inputs,
        outputs,
        parameters,
        locals,
        output_assignments);
    }

    @SuppressWarnings("synthetic-access") @Override public
      UASTRDShaderFragmentInput
      fragmentShaderVisitInput(
        final @Nonnull UASTUDShaderFragmentInput i)
        throws ResolverError,
          ConstraintError
    {
      final UniqueNameLocal oname = i.getName();
      final UASTRTermNameLocal name =
        new UASTRTermNameLocal(oname.getOriginal(), oname.getCurrent());

      final UASTUTypePath otype = i.getType();
      final UASTRTypeName type =
        Resolver.lookupType(
          this.modules,
          this.module,
          otype.getModule(),
          otype.getName());
      return new UASTRDShaderFragmentInput(name, type);
    }

    @Override public
      UASTUFragmentShaderLocalVisitor<UASTRDShaderFragmentLocal, ResolverError>
      fragmentShaderVisitLocalsPre()
        throws ResolverError,
          ConstraintError
    {
      return new FragmentShaderLocalResolver(
        this.log,
        this.module,
        this.modules,
        this.shader);
    }

    @Override public
      UASTRDShaderFragmentOutputAssignment
      fragmentShaderVisitOutputAssignment(
        final @Nonnull UASTUDShaderFragmentOutputAssignment a)
        throws ResolverError,
          ConstraintError
    {
      final UASTRTermName name =
        Resolver.lookupTerm(this.module, this.modules, a
          .getVariable()
          .getName());

      if (this.outputs_declared.containsKey(a.getName().getActual()) == false) {
        throw ResolverError.shaderOutputNonexistent(this.shader, a.getName());
      }

      final UASTREVariable variable = new UASTREVariable(name);
      return new UASTRDShaderFragmentOutputAssignment(a.getName(), variable);
    }

    @SuppressWarnings("synthetic-access") @Override public
      UASTUFragmentShaderOutputVisitor<UASTRDShaderFragmentOutput, ResolverError>
      fragmentShaderVisitOutputsPre()
        throws ResolverError,
          ConstraintError
    {
      return new UASTUFragmentShaderOutputVisitor<UASTRDShaderFragmentOutput, ResolverError>() {
        @Override public
          UASTRDShaderFragmentOutput
          fragmentShaderVisitOutputData(
            final @Nonnull UASTUDShaderFragmentOutputData d)
            throws ResolverError,
              ConstraintError
        {
          final UniqueNameLocal oname = d.getName();
          final UASTUTypePath otype = d.getType();
          final UASTRTypeName type =
            Resolver.lookupType(
              FragmentShaderResolver.this.modules,
              FragmentShaderResolver.this.module,
              otype.getModule(),
              otype.getName());

          FragmentShaderResolver.this.outputs_declared.put(
            oname.getCurrent(),
            d.getName().getOriginal());

          return new UASTRDShaderFragmentOutputData(
            oname.getOriginal(),
            type,
            d.getIndex());
        }

        @Override public
          UASTRDShaderFragmentOutput
          fragmentShaderVisitOutputDepth(
            final @Nonnull UASTUDShaderFragmentOutputDepth d)
            throws ResolverError,
              ConstraintError
        {
          final UniqueNameLocal oname = d.getName();
          final UASTUTypePath otype = d.getType();
          final UASTRTypeName type =
            Resolver.lookupType(
              FragmentShaderResolver.this.modules,
              FragmentShaderResolver.this.module,
              otype.getModule(),
              otype.getName());

          FragmentShaderResolver.this.outputs_declared.put(
            oname.getCurrent(),
            d.getName().getOriginal());

          return new UASTRDShaderFragmentOutputDepth(
            oname.getOriginal(),
            type);
        }
      };
    }

    @SuppressWarnings("synthetic-access") @Override public
      UASTRDShaderFragmentParameter
      fragmentShaderVisitParameter(
        final @Nonnull UASTUDShaderFragmentParameter p)
        throws ResolverError,
          ConstraintError
    {
      final UniqueNameLocal oname = p.getName();
      final UASTUTypePath otype = p.getType();
      final UASTRTermNameLocal name =
        new UASTRTermNameLocal(oname.getOriginal(), oname.getCurrent());
      final UASTRTypeName type =
        Resolver.lookupType(
          this.modules,
          this.module,
          otype.getModule(),
          otype.getName());

      return new UASTRDShaderFragmentParameter(name, type);
    }
  }

  /**
   * Check that all imports reference existing modules, and ensure they form a
   * graph without cycles.
   */

  private static final class ImportResolver
  {
    private static final class Import
    {
      final @Nonnull UASTUDImport   actual;
      final @Nonnull ModulePathFlat importer;
      final @Nonnull ModulePathFlat target;

      public Import(
        final @Nonnull UASTUDImport actual,
        final @Nonnull ModulePathFlat importer,
        final @Nonnull ModulePathFlat target)
      {
        this.actual = actual;
        this.importer = importer;
        this.target = target;
      }

      @Override public boolean equals(
        final Object obj)
      {
        if (this == obj) {
          return true;
        }
        if (obj == null) {
          return false;
        }
        if (this.getClass() != obj.getClass()) {
          return false;
        }
        final Import other = (Import) obj;
        if (!this.actual.equals(other.actual)) {
          return false;
        }
        if (!this.importer.equals(other.importer)) {
          return false;
        }
        if (!this.target.equals(other.target)) {
          return false;
        }
        return true;
      }

      @Override public int hashCode()
      {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + this.actual.hashCode();
        result = (prime * result) + this.importer.hashCode();
        result = (prime * result) + this.target.hashCode();
        return result;
      }

      @Override public String toString()
      {
        final StringBuilder builder = new StringBuilder();
        builder.append("[Import ");
        builder.append(this.actual);
        builder.append(" ");
        builder.append(this.importer);
        builder.append(" -> ");
        builder.append(this.target);
        builder.append("]");
        return builder.toString();
      }
    }

    private final @Nonnull UASTUCompilation                           compilation;
    private final @Nonnull DirectedAcyclicGraph<UASTUDModule, Import> import_graph;
    private final @Nonnull Log                                        log;
    private final @Nonnull StringBuilder                              message;

    public ImportResolver(
      final @Nonnull UASTUCompilation compilation,
      final @Nonnull Log log)
      throws ResolverError,
        ConstraintError
    {
      this.log = new Log(log, "imports");
      this.message = new StringBuilder();
      this.compilation = compilation;

      this.import_graph =
        new DirectedAcyclicGraph<UASTUDModule, Import>(Import.class);

      final Map<ModulePathFlat, UASTUDModule> modules =
        compilation.getModules();

      for (final ModulePathFlat path : modules.keySet()) {
        final UASTUDModule module = modules.get(path);
        if (this.log.enabled(Level.LOG_DEBUG)) {
          this.message.setLength(0);
          this.message.append("Adding module ");
          this.message.append(path.getActual());
          this.log.debug(this.message.toString());
        }
        this.import_graph.addVertex(module);
      }

      for (final ModulePathFlat path : modules.keySet()) {
        final UASTUDModule module = modules.get(path);

        for (final UASTUDImport i : module.getImports()) {
          final ModulePathFlat target =
            ModulePathFlat.fromModulePath(i.getPath());

          if (this.log.enabled(Level.LOG_DEBUG)) {
            this.message.setLength(0);
            this.message.append("Adding module import ");
            this.message.append(path.getActual());
            this.message.append(" -> ");
            this.message.append(target.getActual());
            this.log.debug(this.message.toString());
          }

          if (modules.containsKey(target) == false) {
            throw ResolverError.moduleImportUnknown(i, target);
          }

          final UASTUDModule target_module = modules.get(target);

          final Import import_ = new Import(i, path, target);

          try {
            this.import_graph.addDagEdge(module, target_module, import_);
          } catch (final CycleFoundException e) {

            /**
             * Because a cycle as occurred on an insertion of edge A -> B,
             * then there must be some path B -> A already in the graph. Use a
             * shortest path algorithm to determine that path.
             */

            final DijkstraShortestPath<UASTUDModule, Import> dj =
              new DijkstraShortestPath<UASTUDModule, Import>(
                this.import_graph,
                target_module,
                module);

            final List<Import> imports = dj.getPathEdgeList();
            assert imports != null;

            final ArrayList<UASTUDImport> imports_ =
              new ArrayList<UASTUDImport>();
            for (final Import im : imports) {
              imports_.add(im.actual);
            }

            throw ResolverError.moduleImportCyclic(i, target, imports_);
          }
        }
      }
    }

    public @Nonnull List<ModulePathFlat> getTopology()
    {
      final TopologicalOrderIterator<UASTUDModule, Import> iter =
        new TopologicalOrderIterator<UASTUDModule, Import>(this.import_graph);

      final ArrayList<ModulePathFlat> ls = new ArrayList<ModulePathFlat>();
      while (iter.hasNext()) {
        final UASTUDModule m = iter.next();
        ls.add(ModulePathFlat.fromModulePath(m.getPath()));
      }

      return ls;
    }
  }

  private static final class LocalResolver implements
    UASTULocalLevelVisitor<UASTRDValueLocal, ResolverError>
  {
    private final @Nonnull UASTUDModule                      module;
    private final @Nonnull Map<ModulePathFlat, UASTUDModule> modules;
    private final @CheckForNull UASTUDTerm                   term;
    private final @CheckForNull TermGraph                    term_graph;

    public LocalResolver(
      final @CheckForNull UASTUDTerm term,
      final @Nonnull UASTUDModule module,
      final @Nonnull Map<ModulePathFlat, UASTUDModule> modules,
      final @CheckForNull TermGraph term_graph)
    {
      this.term = term;
      this.module = module;
      this.modules = modules;
      this.term_graph = term_graph;
    }

    @Override public UASTRDValueLocal localVisitValueLocal(
      final @Nonnull UASTUDValueLocal v)
      throws ResolverError,
        ConstraintError
    {
      final Option<UASTRTypeName> ascription =
        v.getAscription().mapPartial(
          new PartialFunction<UASTUTypePath, UASTRTypeName, ResolverError>() {
            @SuppressWarnings("synthetic-access") @Override public
              UASTRTypeName
              call(
                final @Nonnull UASTUTypePath t)
                throws ResolverError
            {
              try {
                return Resolver.lookupType(
                  LocalResolver.this.modules,
                  LocalResolver.this.module,
                  t.getModule(),
                  t.getName());
              } catch (final ConstraintError e) {
                throw new UnreachableCodeException(e);
              }
            }
          });

      final ExpressionResolver exr =
        new ExpressionResolver(
          this.term,
          this.module,
          this.modules,
          this.term_graph);

      final UASTRExpression expression =
        v.getExpression().expressionVisitableAccept(exr);

      final TokenIdentifierLower original = v.getName().getOriginal();
      final String current = v.getName().getCurrent();
      final UASTRTermNameLocal name =
        new UASTRTermNameLocal(original, current);
      return new UASTRDValueLocal(name, ascription, expression);
    }
  }

  private static class ModuleResolver implements
    UASTUModuleVisitor<UASTRDModule, UASTRDImport, UASTRDeclarationModuleLevel, UASTRDTerm, UASTRDType, UASTRDShader, ResolverError>,
    UASTUTermVisitor<UASTRDTerm, ResolverError>,
    UASTUTypeVisitor<UASTRDType, ResolverError>,
    UASTUShaderVisitor<UASTRDShader, ResolverError>
  {
    private final @Nonnull Log                               log;
    private final @Nonnull UASTUDModule                      module;
    private final @Nonnull Map<ModulePathFlat, UASTUDModule> modules;
    private final @Nonnull ShaderGraph                       shader_graph;
    private final @Nonnull TermGraph                         term_graph;
    private final @Nonnull TypeGraph                         type_graph;

    public ModuleResolver(
      final @Nonnull UASTUDModule module,
      final @Nonnull Map<ModulePathFlat, UASTUDModule> modules,
      final @Nonnull Log log)
    {
      this.module = module;
      this.modules = modules;
      this.term_graph = new TermGraph(log);
      this.type_graph = new TypeGraph(log);
      this.shader_graph = new ShaderGraph(log);
      this.log = new Log(log, "names");

      if (this.log.enabled(Level.LOG_DEBUG)) {
        final StringBuilder m = new StringBuilder();
        m.append("Resolving ");
        m.append(ModulePathFlat.fromModulePath(module.getPath()).getActual());
        this.log.debug(m.toString());
      }
    }

    @Override public
      UASTUShaderVisitor<UASTRDShader, ResolverError>
      moduleShadersPre(
        final @Nonnull UASTUDModule m)
        throws ResolverError,
          ConstraintError
    {
      return this;
    }

    @Override public
      UASTUTermVisitor<UASTRDTerm, ResolverError>
      moduleTermsPre(
        final @Nonnull UASTUDModule m)
        throws ResolverError,
          ConstraintError
    {
      return this;
    }

    @Override public
      UASTUTypeVisitor<UASTRDType, ResolverError>
      moduleTypesPre(
        final @Nonnull UASTUDModule m)
        throws ResolverError,
          ConstraintError
    {
      return this;
    }

    @Override public UASTRDModule moduleVisit(
      final @Nonnull List<UASTRDImport> imports,
      final @Nonnull List<UASTRDeclarationModuleLevel> declarations,
      final @Nonnull Map<String, UASTRDTerm> terms,
      final @Nonnull Map<String, UASTRDType> types,
      final @Nonnull Map<String, UASTRDShader> shaders,
      final @Nonnull UASTUDModule m)
      throws ResolverError,
        ConstraintError
    {
      final Map<String, UASTUDImport> import_names = m.getImportedNames();
      final Map<String, UASTRDImport> r_import_names =
        new HashMap<String, UASTRDImport>();
      for (final String in : import_names.keySet()) {
        final UASTUDImport i = import_names.get(in);
        r_import_names.put(in, new UASTRDImport(i.getPath(), i.getRename()));
      }

      final Map<String, UASTUDImport> import_renames = m.getImportedRenames();
      final Map<String, UASTRDImport> r_import_renames =
        new HashMap<String, UASTRDImport>();
      for (final String in : import_renames.keySet()) {
        final UASTUDImport i = import_renames.get(in);
        r_import_renames
          .put(in, new UASTRDImport(i.getPath(), i.getRename()));
      }

      final Map<ModulePathFlat, UASTUDImport> import_modules =
        m.getImportedModules();
      final Map<ModulePathFlat, UASTRDImport> r_import_modules =
        new HashMap<ModulePathFlat, UASTRDImport>();
      for (final ModulePathFlat in : import_modules.keySet()) {
        final UASTUDImport i = import_modules.get(in);
        r_import_modules
          .put(in, new UASTRDImport(i.getPath(), i.getRename()));
      }

      final ModulePathFlat current_flat =
        ModulePathFlat.fromModulePath(m.getPath());

      final List<String> term_topology =
        this.term_graph.getTopology(current_flat);
      final List<String> type_topology =
        this.type_graph.getTopology(current_flat);
      final List<String> shader_topology =
        this.shader_graph.getTopology(current_flat);

      return new UASTRDModule(
        m.getPath(),
        imports,
        r_import_modules,
        r_import_names,
        r_import_renames,
        declarations,
        terms,
        term_topology,
        types,
        type_topology,
        shaders,
        shader_topology);
    }

    @Override public @Nonnull UASTRDShader moduleVisitFragmentShader(
      final @Nonnull UASTUDShaderFragment f)
      throws ResolverError,
        ConstraintError
    {
      final UASTRDShaderFragment fs =
        f.fragmentShaderVisitableAccept(new FragmentShaderResolver(
          this.log,
          this.module,
          this.modules,
          f));

      this.shader_graph.addShader(this.module.getPath(), f.getName());
      return fs;
    }

    @Override public @Nonnull UASTRDImport moduleVisitImport(
      final @Nonnull UASTUDImport i)
      throws ResolverError,
        ConstraintError
    {
      return new UASTRDImport(i.getPath(), i.getRename());
    }

    @SuppressWarnings("synthetic-access") @Override public @Nonnull
      UASTRDShaderProgram
      moduleVisitProgramShader(
        final @Nonnull UASTUDShaderProgram p)
        throws ResolverError,
          ConstraintError
    {
      final UASTUShaderPath fp = p.getFragmentShader();
      final UASTUShaderPath vp = p.getVertexShader();

      final UASTRShaderName fragment_shader =
        Resolver.lookupShader(
          this.modules,
          this.module,
          fp.getModule(),
          fp.getName());

      final UASTRShaderName vertex_shader =
        Resolver.lookupShader(
          this.modules,
          this.module,
          vp.getModule(),
          vp.getName());

      this.shader_graph.addShaderReference(
        this.module.getPath(),
        p.getName(),
        vertex_shader.getPath(),
        vertex_shader.getName());

      this.shader_graph.addShaderReference(
        this.module.getPath(),
        p.getName(),
        fragment_shader.getPath(),
        fragment_shader.getName());

      return new UASTRDShaderProgram(
        p.getName(),
        vertex_shader,
        fragment_shader);
    }

    @Override public @Nonnull UASTRDShaderVertex moduleVisitVertexShader(
      final @Nonnull UASTUDShaderVertex v)
      throws ResolverError,
        ConstraintError
    {
      final UASTRDShaderVertex vs =
        v.vertexShaderVisitableAccept(new VertexShaderResolver(
          this.log,
          this.module,
          this.modules,
          v));

      this.shader_graph.addShader(this.module.getPath(), v.getName());
      return vs;
    }

    @Override public @Nonnull UASTRDTerm termVisitFunctionDefined(
      final @Nonnull UASTUDFunctionDefined f)
      throws ResolverError,
        ConstraintError
    {
      return f.termVisitableAccept(new TermResolver(
        this.log,
        this.module,
        this.modules,
        this.term_graph,
        f));
    }

    @Override public @Nonnull UASTRDTerm termVisitFunctionExternal(
      final @Nonnull UASTUDFunctionExternal f)
      throws ResolverError,
        ConstraintError
    {
      return f.termVisitableAccept(new TermResolver(
        this.log,
        this.module,
        this.modules,
        this.term_graph,
        f));
    }

    @Override public @Nonnull UASTRDTerm termVisitValueDefined(
      final @Nonnull UASTUDValueDefined v)
      throws ResolverError,
        ConstraintError
    {
      return v.termVisitableAccept(new TermResolver(
        this.log,
        this.module,
        this.modules,
        this.term_graph,
        v));
    }

    @Override public UASTRDTerm termVisitValueExternal(
      final @Nonnull UASTUDValueExternal v)
      throws ResolverError,
        ConstraintError
    {
      return v.termVisitableAccept(new TermResolver(
        this.log,
        this.module,
        this.modules,
        this.term_graph,
        v));
    }

    @Override public @Nonnull UASTRDType typeVisitTypeRecord(
      final @Nonnull UASTUDTypeRecord r)
      throws ResolverError,
        ConstraintError
    {
      return r.recordTypeVisitableAccept(new RecordTypeResolver(
        this.log,
        this.module,
        this.modules,
        this.type_graph,
        r));
    }
  }

  private static final class RecordTypeResolver implements
    UASTUDRecordVisitor<UASTRDTypeRecord, UASTRDTypeRecordField, ResolverError>
  {
    private final @Nonnull Log                               log;
    private final @Nonnull UASTUDModule                      module;
    private final @Nonnull Map<ModulePathFlat, UASTUDModule> modules;
    private final @Nonnull UASTUDTypeRecord                  type;
    private final @Nonnull TypeGraph                         type_graph;

    public RecordTypeResolver(
      final @Nonnull Log log,
      final @Nonnull UASTUDModule module,
      final @Nonnull Map<ModulePathFlat, UASTUDModule> modules,
      final @Nonnull TypeGraph type_graph,
      final @Nonnull UASTUDTypeRecord r)
    {
      this.log = log;
      this.module = module;
      this.modules = modules;
      this.type_graph = type_graph;
      this.type = r;
    }

    @Override public UASTRDTypeRecord recordTypeVisit(
      final @Nonnull List<UASTRDTypeRecordField> fields,
      final @Nonnull UASTUDTypeRecord e)
      throws ResolverError,
        ConstraintError
    {
      this.type_graph.addType(this.module.getPath(), e.getName());
      return new UASTRDTypeRecord(e.getName(), fields);
    }

    @SuppressWarnings("synthetic-access") @Override public
      UASTRDTypeRecordField
      recordTypeVisitField(
        final @Nonnull UASTUDTypeRecordField e)
        throws ResolverError,
          ConstraintError
    {
      final UASTUTypePath et = e.getType();
      final UASTRTypeName t =
        Resolver.lookupType(
          this.modules,
          this.module,
          et.getModule(),
          et.getName());

      t
        .typeNameVisitableAccept(new UASTRTypeNameVisitor<Unit, ResolverError>() {
          @Override public Unit typeNameVisitBuiltIn(
            final @Nonnull UASTRTypeNameBuiltIn name)
            throws ConstraintError,
              ResolverError
          {
            return Unit.unit();
          }

          @Override public Unit typeNameVisitGlobal(
            final UASTRTypeNameGlobal name)
            throws ConstraintError,
              ResolverError
          {
            RecordTypeResolver.this.type_graph.addTypeReference(
              RecordTypeResolver.this.module.getPath(),
              RecordTypeResolver.this.type.getName(),
              name.getPath(),
              name.getName());

            return Unit.unit();
          }
        });

      return new UASTRDTypeRecordField(e.getName(), t);
    }

    @Override public void recordTypeVisitPre(
      final @Nonnull UASTUDTypeRecord e)
      throws ResolverError,
        ConstraintError
    {
      // Nothing
    }
  }

  /**
   * A shader in the shader graph.
   */

  private static final class Shader
  {
    private final @Nonnull ModulePathFlat module;
    private final @Nonnull String         name;

    public Shader(
      final @Nonnull ModulePathFlat module,
      final @Nonnull String name)
    {
      this.module = module;
      this.name = name;
    }

    @Override public boolean equals(
      final Object obj)
    {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (this.getClass() != obj.getClass()) {
        return false;
      }
      final Shader other = (Shader) obj;
      if (!this.module.equals(other.module)) {
        return false;
      }
      if (!this.name.equals(other.name)) {
        return false;
      }
      return true;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.module.hashCode();
      result = (prime * result) + this.name.hashCode();
      return result;
    }
  }

  /**
   * Shader graph.
   */

  private static final class ShaderGraph
  {
    private final @Nonnull DirectedAcyclicGraph<Shader, ShaderReference> graph;
    private final @Nonnull Log                                           log;

    public ShaderGraph(
      final @Nonnull Log log)
    {
      this.graph =
        new DirectedAcyclicGraph<Shader, ShaderReference>(
          ShaderReference.class);
      this.log = new Log(log, "shader-graph");
    }

    public void addShader(
      final @Nonnull ModulePath source_module,
      final @Nonnull TokenIdentifierLower source_name)
    {
      final ModulePathFlat source_module_flat =
        ModulePathFlat.fromModulePath(source_module);
      final Shader source =
        new Shader(source_module_flat, source_name.getActual());

      if (this.log.enabled(Level.LOG_DEBUG)) {
        this.log.debug(String.format(
          "Adding shader: %s.%s",
          source_module_flat.getActual(),
          source_name.getActual()));
      }

      this.graph.addVertex(source);
    }

    public void addShaderReference(
      final @Nonnull ModulePath source_module,
      final @Nonnull TokenIdentifierLower source_name,
      final @Nonnull ModulePath target_module,
      final @Nonnull TokenIdentifierLower target_name)
      throws ResolverError,
        ConstraintError
    {
      final ModulePathFlat source_module_flat =
        ModulePathFlat.fromModulePath(source_module);
      final ModulePathFlat target_module_flat =
        ModulePathFlat.fromModulePath(target_module);

      final Shader source =
        new Shader(source_module_flat, source_name.getActual());

      final Shader target =
        new Shader(target_module_flat, target_name.getActual());

      final ShaderReference edge =
        new ShaderReference(
          source_module,
          source_name,
          target_module,
          target_name);

      try {
        this.addShader(source_module, source_name);
        this.addShader(target_module, target_name);

        if (this.log.enabled(Level.LOG_DEBUG)) {
          this.log.debug(String.format(
            "Adding shader reference: %s.%s -> %s.%s",
            source_module_flat.getActual(),
            source_name.getActual(),
            target_module_flat.getActual(),
            target_name.getActual()));
        }

        this.graph.addDagEdge(source, target, edge);
      } catch (final IllegalArgumentException x) {
        throw ResolverError.shaderRecursiveLocal(source_name, target_name);
      } catch (final CycleFoundException x) {

        /**
         * Because a cycle as occurred on an insertion of edge A -> B, then
         * there must be some path B -> A already in the graph. Use a shortest
         * path algorithm to determine that path.
         */

        final DijkstraShortestPath<Shader, ShaderReference> dj =
          new DijkstraShortestPath<Shader, ShaderReference>(
            this.graph,
            target,
            source);

        final List<ShaderReference> terms = dj.getPathEdgeList();
        assert terms != null;

        final ArrayList<TokenIdentifierLower> tokens =
          new ArrayList<TokenIdentifierLower>();
        tokens.add(target_name);

        for (final ShaderReference ref : terms) {
          tokens.add(ref.target_name);
        }

        throw ResolverError.shaderRecursiveMutual(source_name, tokens);
      }
    }

    @SuppressWarnings("synthetic-access") public @Nonnull
      List<String>
      getTopology(
        final @Nonnull ModulePathFlat current)
    {
      final TopologicalOrderIterator<Shader, ShaderReference> iter =
        new TopologicalOrderIterator<Shader, ShaderReference>(this.graph);

      final ArrayList<String> ls = new ArrayList<String>();
      while (iter.hasNext()) {
        final Shader t = iter.next();
        if (t.module.equals(current)) {
          ls.add(t.name);
        }
      }

      return ls;
    }
  }

  /**
   * A reference made to a shader named <code>name</code> via the module
   * <code>module</code>, in module <code>from</code>.
   */

  private static final class ShaderReference
  {
    final @Nonnull ModulePath           source_module;
    final @Nonnull TokenIdentifierLower source_name;
    final @Nonnull ModulePath           target_module;
    final @Nonnull TokenIdentifierLower target_name;

    public ShaderReference(
      final @Nonnull ModulePath source_module,
      final @Nonnull TokenIdentifierLower source_name,
      final @Nonnull ModulePath target_module,
      final @Nonnull TokenIdentifierLower target_name)
    {
      this.source_module = source_module;
      this.source_name = source_name;
      this.target_module = target_module;
      this.target_name = target_name;
    }

    @Override public boolean equals(
      final Object obj)
    {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (this.getClass() != obj.getClass()) {
        return false;
      }
      final ShaderReference other = (ShaderReference) obj;
      if (!this.source_name.equals(other.source_name)) {
        return false;
      }
      if (!this.source_module.equals(other.source_module)) {
        return false;
      }
      if (!this.target_module.equals(other.target_module)) {
        return false;
      }
      if (!this.target_name.equals(other.target_name)) {
        return false;
      }
      return true;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.source_name.hashCode();
      result = (prime * result) + this.source_module.hashCode();
      result = (prime * result) + this.target_module.hashCode();
      result = (prime * result) + this.target_name.hashCode();
      return result;
    }
  }

  /**
   * A term in the term graph.
   */

  private static final class Term
  {
    private final @Nonnull ModulePathFlat module;
    private final @Nonnull String         name;

    public Term(
      final @Nonnull ModulePathFlat module,
      final @Nonnull String name)
    {
      this.module = module;
      this.name = name;
    }

    @Override public boolean equals(
      final Object obj)
    {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (this.getClass() != obj.getClass()) {
        return false;
      }
      final Term other = (Term) obj;
      if (!this.module.equals(other.module)) {
        return false;
      }
      if (!this.name.equals(other.name)) {
        return false;
      }
      return true;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.module.hashCode();
      result = (prime * result) + this.name.hashCode();
      return result;
    }
  }

  /**
   * Term graph.
   */

  private static final class TermGraph
  {
    private final @Nonnull DirectedAcyclicGraph<Term, TermReference> graph;
    private final @Nonnull Log                                       log;

    public TermGraph(
      final @Nonnull Log log)
    {
      this.graph =
        new DirectedAcyclicGraph<Term, TermReference>(TermReference.class);
      this.log = new Log(log, "term-graph");
    }

    public void addTerm(
      final @Nonnull ModulePath source_module,
      final @Nonnull TokenIdentifierLower source_name)
    {
      final ModulePathFlat source_module_flat =
        ModulePathFlat.fromModulePath(source_module);
      final Term source =
        new Term(source_module_flat, source_name.getActual());

      if (this.log.enabled(Level.LOG_DEBUG)) {
        this.log.debug(String.format(
          "Adding term: %s.%s",
          source_module_flat.getActual(),
          source_name.getActual()));
      }

      this.graph.addVertex(source);
    }

    public void addTermReference(
      final @Nonnull ModulePath source_module,
      final @Nonnull TokenIdentifierLower source_name,
      final @Nonnull ModulePath target_module,
      final @Nonnull TokenIdentifierLower target_name)
      throws ResolverError,
        ConstraintError
    {
      final ModulePathFlat source_module_flat =
        ModulePathFlat.fromModulePath(source_module);
      final ModulePathFlat target_module_flat =
        ModulePathFlat.fromModulePath(target_module);

      final Term source =
        new Term(source_module_flat, source_name.getActual());
      final Term target =
        new Term(target_module_flat, target_name.getActual());

      final TermReference edge =
        new TermReference(
          source_module,
          source_name,
          target_module,
          target_name);

      try {
        this.addTerm(source_module, source_name);
        this.addTerm(target_module, target_name);

        if (this.log.enabled(Level.LOG_DEBUG)) {
          this.log.debug(String.format(
            "Adding term reference: %s.%s -> %s.%s",
            source_module_flat.getActual(),
            source_name.getActual(),
            target_module_flat.getActual(),
            target_name.getActual()));
        }

        this.graph.addDagEdge(source, target, edge);
      } catch (final IllegalArgumentException x) {
        throw ResolverError.termRecursiveLocal(source_name, target_name);
      } catch (final CycleFoundException x) {

        /**
         * Because a cycle as occurred on an insertion of edge A -> B, then
         * there must be some path B -> A already in the graph. Use a shortest
         * path algorithm to determine that path.
         */

        final DijkstraShortestPath<Term, TermReference> dj =
          new DijkstraShortestPath<Term, TermReference>(
            this.graph,
            target,
            source);

        final List<TermReference> terms = dj.getPathEdgeList();
        assert terms != null;

        final ArrayList<TokenIdentifierLower> tokens =
          new ArrayList<TokenIdentifierLower>();
        tokens.add(target_name);

        for (final TermReference ref : terms) {
          tokens.add(ref.target_name);
        }

        throw ResolverError.termRecursiveMutual(source_name, tokens);
      }
    }

    @SuppressWarnings("synthetic-access") public @Nonnull
      List<String>
      getTopology(
        final @Nonnull ModulePathFlat current)
    {
      final TopologicalOrderIterator<Term, TermReference> iter =
        new TopologicalOrderIterator<Term, TermReference>(this.graph);

      final ArrayList<String> ls = new ArrayList<String>();
      while (iter.hasNext()) {
        final Term t = iter.next();
        if (t.module.equals(current)) {
          ls.add(t.name);
        }
      }

      return ls;
    }
  }

  /**
   * A reference made to a term named <code>name</code> via the module
   * <code>module</code>, in module <code>from</code>.
   */

  private static final class TermReference
  {
    final @Nonnull ModulePath           source_module;
    final @Nonnull TokenIdentifierLower source_name;
    final @Nonnull ModulePath           target_module;
    final @Nonnull TokenIdentifierLower target_name;

    public TermReference(
      final @Nonnull ModulePath source_module,
      final @Nonnull TokenIdentifierLower source_name,
      final @Nonnull ModulePath target_module,
      final @Nonnull TokenIdentifierLower target_name)
    {
      this.source_module = source_module;
      this.source_name = source_name;
      this.target_module = target_module;
      this.target_name = target_name;
    }

    @Override public boolean equals(
      final Object obj)
    {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (this.getClass() != obj.getClass()) {
        return false;
      }
      final TermReference other = (TermReference) obj;
      if (!this.source_name.equals(other.source_name)) {
        return false;
      }
      if (!this.source_module.equals(other.source_module)) {
        return false;
      }
      if (!this.target_module.equals(other.target_module)) {
        return false;
      }
      if (!this.target_name.equals(other.target_name)) {
        return false;
      }
      return true;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.source_name.hashCode();
      result = (prime * result) + this.source_module.hashCode();
      result = (prime * result) + this.target_module.hashCode();
      result = (prime * result) + this.target_name.hashCode();
      return result;
    }
  }

  private static final class TermResolver implements
    UASTUTermVisitor<UASTRDTerm, ResolverError>
  {
    private final @Nonnull Log                               log;
    private final @Nonnull UASTUDModule                      module;
    private final @Nonnull Map<ModulePathFlat, UASTUDModule> modules;
    private final @Nonnull UASTUDTerm                        term;
    private final @Nonnull TermGraph                         term_graph;

    public TermResolver(
      final @Nonnull Log log,
      final @Nonnull UASTUDModule module,
      final @Nonnull Map<ModulePathFlat, UASTUDModule> modules,
      final @Nonnull TermGraph term_graph,
      final @Nonnull UASTUDTerm t)
    {
      this.log = log;
      this.module = module;
      this.modules = modules;
      this.term_graph = term_graph;
      this.term = t;
    }

    @SuppressWarnings("synthetic-access") @Override public
      UASTRDFunctionDefined
      termVisitFunctionDefined(
        final @Nonnull UASTUDFunctionDefined f)
        throws ResolverError,
          ConstraintError
    {
      final UASTRExpression ex =
        f.getBody().expressionVisitableAccept(
          new ExpressionResolver(
            this.term,
            this.module,
            this.modules,
            this.term_graph));

      final UASTUTypePath rt = f.getReturnType();
      final UASTRTypeName return_type =
        Resolver.lookupType(
          this.modules,
          this.module,
          rt.getModule(),
          rt.getName());

      final List<UASTRDFunctionArgument> arguments =
        new ArrayList<UASTRDFunctionArgument>();
      for (final UASTUDFunctionArgument arg : f.getArguments()) {
        final UniqueNameLocal an = arg.getName();
        final UASTUTypePath at = arg.getType();
        final UASTRTermNameLocal an_new =
          new UASTRTermNameLocal(an.getOriginal(), an.getCurrent());
        final UASTRTypeName at_new =
          Resolver.lookupType(
            this.modules,
            this.module,
            at.getModule(),
            at.getName());
        arguments.add(new UASTRDFunctionArgument(an_new, at_new));
      }

      this.term_graph.addTerm(this.module.getPath(), f.getName());
      return new UASTRDFunctionDefined(
        f.getName(),
        arguments,
        return_type,
        ex);
    }

    @SuppressWarnings("synthetic-access") @Override public
      UASTRDFunctionExternal
      termVisitFunctionExternal(
        final @Nonnull UASTUDFunctionExternal f)
        throws ResolverError,
          ConstraintError
    {

      final UASTUTypePath rt = f.getReturnType();
      final UASTRTypeName return_type =
        Resolver.lookupType(
          this.modules,
          this.module,
          rt.getModule(),
          rt.getName());

      final List<UASTRDFunctionArgument> arguments =
        new ArrayList<UASTRDFunctionArgument>();

      for (final UASTUDFunctionArgument arg : f.getArguments()) {
        final UniqueNameLocal an = arg.getName();
        final UASTUTypePath at = arg.getType();
        final UASTRTermNameLocal an_new =
          new UASTRTermNameLocal(an.getOriginal(), an.getCurrent());
        final UASTRTypeName at_new =
          Resolver.lookupType(
            this.modules,
            this.module,
            at.getModule(),
            at.getName());
        arguments.add(new UASTRDFunctionArgument(an_new, at_new));
      }

      this.term_graph.addTerm(this.module.getPath(), f.getName());

      final UASTUDExternal ext = f.getExternal();
      final Option<UASTUExpression> original_emulation = ext.getEmulation();
      final Option<UASTRExpression> emulation =
        original_emulation
          .mapPartial(new PartialFunction<UASTUExpression, UASTRExpression, ResolverError>() {
            @Override public UASTRExpression call(
              final @Nonnull UASTUExpression e)
              throws ResolverError
            {
              try {
                return e.expressionVisitableAccept(new ExpressionResolver(
                  f,
                  TermResolver.this.module,
                  TermResolver.this.modules,
                  TermResolver.this.term_graph));
              } catch (final ConstraintError x) {
                throw new UnreachableCodeException(x);
              }
            }
          });

      return new UASTRDFunctionExternal(
        f.getName(),
        arguments,
        return_type,
        new UASTRDExternal(
          ext.getName(),
          ext.isVertexShaderAllowed(),
          ext.isFragmentShaderAllowed(),
          emulation));
    }

    @Override public UASTRDValueDefined termVisitValueDefined(
      final @Nonnull UASTUDValueDefined v)
      throws ResolverError,
        ConstraintError
    {
      final Option<UASTRTypeName> ascription =
        v.getAscription().mapPartial(
          new PartialFunction<UASTUTypePath, UASTRTypeName, ResolverError>() {
            @SuppressWarnings("synthetic-access") @Override public
              UASTRTypeName
              call(
                final @Nonnull UASTUTypePath x)
                throws ResolverError
            {
              try {
                return Resolver.lookupType(
                  TermResolver.this.modules,
                  TermResolver.this.module,
                  x.getModule(),
                  x.getName());
              } catch (final ConstraintError e) {
                throw new UnreachableCodeException(e);
              }
            }
          });

      final UASTRExpression ex =
        v.getExpression().expressionVisitableAccept(
          new ExpressionResolver(
            this.term,
            this.module,
            this.modules,
            this.term_graph));

      this.term_graph.addTerm(this.module.getPath(), v.getName());
      return new UASTRDValueDefined(v.getName(), ascription, ex);
    }

    @SuppressWarnings("synthetic-access") @Override public
      UASTRDTerm
      termVisitValueExternal(
        final @Nonnull UASTUDValueExternal v)
        throws ResolverError,
          ConstraintError
    {
      final UASTUDExternal original_external = v.getExternal();

      final UASTUTypePath type_name = v.getAscription();
      final UASTRTypeName ascription =
        Resolver.lookupType(
          TermResolver.this.modules,
          TermResolver.this.module,
          type_name.getModule(),
          type_name.getName());

      this.term_graph.addTerm(this.module.getPath(), v.getName());
      final Option<UASTRExpression> none = Option.none();
      final UASTRDExternal external =
        new UASTRDExternal(
          original_external.getName(),
          original_external.isVertexShaderAllowed(),
          original_external.isFragmentShaderAllowed(),
          none);
      return new UASTRDValueExternal(v.getName(), ascription, external);
    }
  }

  /**
   * A type in the type graph.
   */

  private static final class Type
  {
    private final @Nonnull ModulePathFlat module;
    private final @Nonnull String         name;

    public Type(
      final @Nonnull ModulePathFlat module,
      final @Nonnull String name)
    {
      this.module = module;
      this.name = name;
    }

    @Override public boolean equals(
      final Object obj)
    {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (this.getClass() != obj.getClass()) {
        return false;
      }
      final Type other = (Type) obj;
      if (!this.module.equals(other.module)) {
        return false;
      }
      if (!this.name.equals(other.name)) {
        return false;
      }
      return true;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.module.hashCode();
      result = (prime * result) + this.name.hashCode();
      return result;
    }
  }

  /**
   * Type graph.
   */

  private static final class TypeGraph
  {
    private final @Nonnull DirectedAcyclicGraph<Type, TypeReference> graph;
    private final @Nonnull Log                                       log;

    public TypeGraph(
      final @Nonnull Log log)
    {
      this.graph =
        new DirectedAcyclicGraph<Type, TypeReference>(TypeReference.class);
      this.log = new Log(log, "type-graph");
    }

    public void addType(
      final @Nonnull ModulePath source_module,
      final @Nonnull TokenIdentifierLower source_name)
    {
      final ModulePathFlat source_module_flat =
        ModulePathFlat.fromModulePath(source_module);
      final Type source =
        new Type(source_module_flat, source_name.getActual());

      if (this.log.enabled(Level.LOG_DEBUG)) {
        this.log.debug(String.format(
          "Adding type: %s.%s",
          source_module_flat.getActual(),
          source_name.getActual()));
      }

      this.graph.addVertex(source);
    }

    public void addTypeReference(
      final @Nonnull ModulePath source_module,
      final @Nonnull TokenIdentifierLower source_name,
      final @Nonnull ModulePath target_module,
      final @Nonnull TokenIdentifierLower target_name)
      throws ResolverError,
        ConstraintError
    {
      final ModulePathFlat source_module_flat =
        ModulePathFlat.fromModulePath(source_module);
      final ModulePathFlat target_module_flat =
        ModulePathFlat.fromModulePath(target_module);

      final Type source =
        new Type(source_module_flat, source_name.getActual());

      final Type target =
        new Type(target_module_flat, target_name.getActual());

      final TypeReference edge =
        new TypeReference(
          source_module,
          source_name,
          target_module,
          target_name);

      try {
        this.addType(source_module, source_name);
        this.addType(target_module, target_name);

        if (this.log.enabled(Level.LOG_DEBUG)) {
          this.log.debug(String.format(
            "Adding type reference: %s.%s -> %s.%s",
            source_module_flat.getActual(),
            source_name.getActual(),
            target_module_flat.getActual(),
            target_name.getActual()));
        }

        this.graph.addDagEdge(source, target, edge);
      } catch (final IllegalArgumentException x) {
        throw ResolverError.typeRecursiveLocal(source_name, target_name);
      } catch (final CycleFoundException x) {

        /**
         * Because a cycle as occurred on an insertion of edge A -> B, then
         * there must be some path B -> A already in the graph. Use a shortest
         * path algorithm to determine that path.
         */

        final DijkstraShortestPath<Type, TypeReference> dj =
          new DijkstraShortestPath<Type, TypeReference>(
            this.graph,
            target,
            source);

        final List<TypeReference> terms = dj.getPathEdgeList();
        assert terms != null;

        final ArrayList<TokenIdentifierLower> tokens =
          new ArrayList<TokenIdentifierLower>();
        tokens.add(target_name);

        for (final TypeReference ref : terms) {
          tokens.add(ref.target_name);
        }

        throw ResolverError.typeRecursiveMutual(source_name, tokens);
      }
    }

    @SuppressWarnings("synthetic-access") public @Nonnull
      List<String>
      getTopology(
        final @Nonnull ModulePathFlat current)
    {
      final TopologicalOrderIterator<Type, TypeReference> iter =
        new TopologicalOrderIterator<Type, TypeReference>(this.graph);

      final ArrayList<String> ls = new ArrayList<String>();
      while (iter.hasNext()) {
        final Type t = iter.next();
        if (t.module.equals(current)) {
          ls.add(t.name);
        }
      }

      return ls;
    }
  }

  /**
   * A reference made to a type named <code>name</code> via the module
   * <code>module</code>, in module <code>from</code>.
   */

  private static final class TypeReference
  {
    final @Nonnull ModulePath           source_module;
    final @Nonnull TokenIdentifierLower source_name;
    final @Nonnull ModulePath           target_module;
    final @Nonnull TokenIdentifierLower target_name;

    public TypeReference(
      final @Nonnull ModulePath source_module,
      final @Nonnull TokenIdentifierLower source_name,
      final @Nonnull ModulePath target_module,
      final @Nonnull TokenIdentifierLower target_name)
    {
      this.source_module = source_module;
      this.source_name = source_name;
      this.target_module = target_module;
      this.target_name = target_name;
    }

    @Override public boolean equals(
      final Object obj)
    {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (this.getClass() != obj.getClass()) {
        return false;
      }
      final TypeReference other = (TypeReference) obj;
      if (!this.source_name.equals(other.source_name)) {
        return false;
      }
      if (!this.source_module.equals(other.source_module)) {
        return false;
      }
      if (!this.target_module.equals(other.target_module)) {
        return false;
      }
      if (!this.target_name.equals(other.target_name)) {
        return false;
      }
      return true;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.source_name.hashCode();
      result = (prime * result) + this.source_module.hashCode();
      result = (prime * result) + this.target_module.hashCode();
      result = (prime * result) + this.target_name.hashCode();
      return result;
    }
  }

  private static final class VertexShaderLocalResolver implements
    UASTUVertexShaderLocalVisitor<UASTRDShaderVertexLocalValue, ResolverError>
  {
    private final @Nonnull Log                               log;
    private final @Nonnull UASTUDModule                      module;
    private final @Nonnull Map<ModulePathFlat, UASTUDModule> modules;
    private final @Nonnull UASTUDShaderVertex                shader;

    public VertexShaderLocalResolver(
      final @Nonnull Log log,
      final @Nonnull UASTUDModule module,
      final @Nonnull Map<ModulePathFlat, UASTUDModule> modules,
      final @Nonnull UASTUDShaderVertex shader)
    {
      this.module = module;
      this.modules = modules;
      this.shader = shader;
      this.log = log;
    }

    @Override public
      UASTRDShaderVertexLocalValue
      vertexShaderVisitLocalValue(
        final @Nonnull UASTUDShaderVertexLocalValue v)
        throws ResolverError,
          ConstraintError
    {
      final UASTUDValueLocal vv = v.getValue();
      final LocalResolver lr =
        new LocalResolver(null, this.module, this.modules, null);

      return new UASTRDShaderVertexLocalValue(vv.localVisitableAccept(lr));
    }
  }

  private static final class VertexShaderResolver implements
    UASTUVertexShaderVisitor<UASTRDShaderVertex, UASTRDShaderVertexInput, UASTRDShaderVertexParameter, UASTRDShaderVertexOutput, UASTRDShaderVertexLocalValue, UASTRDShaderVertexOutputAssignment, ResolverError>
  {
    private final @Nonnull Log                               log;
    private final @Nonnull UASTUDModule                      module;
    private final @Nonnull Map<ModulePathFlat, UASTUDModule> modules;
    private final @Nonnull Map<String, TokenIdentifierLower> outputs_declared;
    private final @Nonnull UASTUDShaderVertex                shader;

    public VertexShaderResolver(
      final @Nonnull Log log,
      final @Nonnull UASTUDModule module,
      final @Nonnull Map<ModulePathFlat, UASTUDModule> modules,
      final @Nonnull UASTUDShaderVertex v)
    {
      this.module = module;
      this.modules = modules;
      this.shader = v;
      this.log = log;
      this.outputs_declared = new HashMap<String, TokenIdentifierLower>();
    }

    @Override public
      UASTRDShaderVertex
      vertexShaderVisit(
        final @Nonnull List<UASTRDShaderVertexInput> inputs,
        final @Nonnull List<UASTRDShaderVertexParameter> parameters,
        final @Nonnull List<UASTRDShaderVertexOutput> outputs,
        final @Nonnull List<UASTRDShaderVertexLocalValue> locals,
        final @Nonnull List<UASTRDShaderVertexOutputAssignment> output_assignments,
        final @Nonnull UASTUDShaderVertex v)
        throws ResolverError,
          ConstraintError
    {
      return new UASTRDShaderVertex(
        v.getName(),
        inputs,
        outputs,
        parameters,
        locals,
        output_assignments);
    }

    @SuppressWarnings("synthetic-access") @Override public
      UASTRDShaderVertexInput
      vertexShaderVisitInput(
        final @Nonnull UASTUDShaderVertexInput i)
        throws ResolverError,
          ConstraintError
    {
      final UniqueNameLocal oname = i.getName();
      final UASTRTermNameLocal name =
        new UASTRTermNameLocal(oname.getOriginal(), oname.getCurrent());

      final UASTUTypePath t = i.getType();
      final UASTRTypeName type =
        Resolver.lookupType(
          this.modules,
          this.module,
          t.getModule(),
          t.getName());
      return new UASTRDShaderVertexInput(name, type);
    }

    @Override public
      UASTUVertexShaderLocalVisitor<UASTRDShaderVertexLocalValue, ResolverError>
      vertexShaderVisitLocalsPre()
        throws ResolverError,
          ConstraintError
    {
      return new VertexShaderLocalResolver(
        this.log,
        this.module,
        this.modules,
        this.shader);
    }

    @SuppressWarnings("synthetic-access") @Override public
      UASTRDShaderVertexOutput
      vertexShaderVisitOutput(
        final @Nonnull UASTUDShaderVertexOutput o)
        throws ResolverError,
          ConstraintError
    {
      final UniqueNameLocal oname = o.getName();
      final UASTUTypePath otype = o.getType();
      final UASTRTypeName type =
        Resolver.lookupType(
          this.modules,
          this.module,
          otype.getModule(),
          otype.getName());

      this.outputs_declared
        .put(oname.getCurrent(), o.getName().getOriginal());

      return new UASTRDShaderVertexOutput(
        oname.getOriginal(),
        type,
        o.isMain());
    }

    @Override public
      UASTRDShaderVertexOutputAssignment
      vertexShaderVisitOutputAssignment(
        final @Nonnull UASTUDShaderVertexOutputAssignment a)
        throws ResolverError,
          ConstraintError
    {
      final UASTRTermName name =
        Resolver.lookupTerm(this.module, this.modules, a
          .getVariable()
          .getName());

      final UASTREVariable variable = new UASTREVariable(name);

      if (this.outputs_declared.containsKey(a.getName().getActual()) == false) {
        throw ResolverError.shaderOutputNonexistent(this.shader, a.getName());
      }

      return new UASTRDShaderVertexOutputAssignment(a.getName(), variable);
    }

    @SuppressWarnings("synthetic-access") @Override public
      UASTRDShaderVertexParameter
      vertexShaderVisitParameter(
        final @Nonnull UASTUDShaderVertexParameter p)
        throws ResolverError,
          ConstraintError
    {
      final UniqueNameLocal oname = p.getName();
      final UASTUTypePath otype = p.getType();
      final UASTRTermNameLocal name =
        new UASTRTermNameLocal(oname.getOriginal(), oname.getCurrent());
      final UASTRTypeName type =
        Resolver.lookupType(
          this.modules,
          this.module,
          otype.getModule(),
          otype.getName());

      return new UASTRDShaderVertexParameter(name, type);
    }
  }

  private static @Nonnull UASTUDModule lookupModuleFromQualification(
    final @Nonnull UASTUDModule current,
    final @Nonnull Map<ModulePathFlat, UASTUDModule> modules,
    final @Nonnull TokenIdentifierUpper qualification)
    throws ResolverError,
      ConstraintError
  {
    if (current.getImportedNames().containsKey(qualification.getActual())) {
      final UASTUDImport path =
        current.getImportedNames().get(qualification.getActual());
      final ModulePathFlat flat =
        ModulePathFlat.fromModulePath(path.getPath());
      assert modules.containsKey(flat);
      return modules.get(flat);
    }

    if (current.getImportedRenames().containsKey(qualification.getActual())) {
      final UASTUDImport path =
        current.getImportedRenames().get(qualification.getActual());
      final ModulePathFlat flat =
        ModulePathFlat.fromModulePath(path.getPath());
      assert modules.containsKey(flat);
      return modules.get(flat);
    }

    throw ResolverError.moduleReferenceUnknown(qualification);
  }

  private static @Nonnull UASTRShaderName lookupShader(
    final @Nonnull Map<ModulePathFlat, UASTUDModule> modules,
    final @Nonnull UASTUDModule current,
    final @Nonnull Option<TokenIdentifierUpper> qualification,
    final @Nonnull TokenIdentifierLower name)
    throws ResolverError,
      ConstraintError
  {
    final UASTUDModule m;

    if (qualification.isNone()) {
      m = current;
    } else {
      final Some<TokenIdentifierUpper> some =
        (Some<TokenIdentifierUpper>) qualification;
      m =
        Resolver.lookupModuleFromQualification(current, modules, some.value);
    }

    if (m.getShaders().containsKey(name.getActual()) == false) {
      throw ResolverError.shaderNonexistent(m, name);
    }

    return new UASTRShaderName(m.getPath(), name);
  }

  static @Nonnull UASTRTermName lookupTerm(
    final @Nonnull UASTUDModule module,
    final @Nonnull Map<ModulePathFlat, UASTUDModule> modules,
    final @Nonnull UniqueName name)
    throws ResolverError,
      ConstraintError
  {
    return name
      .uniqueNameVisitableAccept(new UniqueNameVisitor<UASTRTermName, ResolverError>() {
        @Override public @Nonnull UASTRTermName uniqueNameVisitLocal(
          final UniqueNameLocal name_actual)
          throws ResolverError,
            ConstraintError
        {
          return new UASTRTermNameLocal(
            name_actual.getOriginal(),
            name_actual.getCurrent());
        }

        @SuppressWarnings("synthetic-access") @Override public @Nonnull
          UASTRTermName
          uniqueNameVisitNonLocal(
            final UniqueNameNonLocal name_actual)
            throws ResolverError,
              ConstraintError
        {
          final UASTUDModule m;
          if (name_actual.getModule().isNone()) {
            m = module;
          } else {
            m =
              Resolver.lookupModuleFromQualification(
                module,
                modules,
                ((Some<TokenIdentifierUpper>) name_actual.getModule()).value);
          }

          final Map<String, UASTUDTerm> terms = m.getTerms();
          if (terms.containsKey(name_actual.getName().getActual())) {
            return new UASTRTermNameGlobal(m.getPath(), name_actual.getName());
          }

          throw ResolverError.termNonexistent(m, name_actual.getName());
        }
      });
  }

  private static @Nonnull UASTRTypeName lookupType(
    final @Nonnull Map<ModulePathFlat, UASTUDModule> modules,
    final @Nonnull UASTUDModule current,
    final @Nonnull Option<TokenIdentifierUpper> qualification,
    final @Nonnull TokenIdentifierLower name)
    throws ResolverError,
      ConstraintError
  {
    final UASTUDModule m;

    if (qualification.isNone()) {
      final Map<TTypeNameBuiltIn, TType> tbn = TType.getBaseTypesByName();
      final TTypeNameBuiltIn tb = new TTypeNameBuiltIn(name.getActual());
      if (tbn.containsKey(tb)) {
        return new UASTRTypeNameBuiltIn(name);
      }

      m = current;
    } else {
      final Some<TokenIdentifierUpper> some =
        (Some<TokenIdentifierUpper>) qualification;
      m =
        Resolver.lookupModuleFromQualification(current, modules, some.value);
    }

    if (m.getTypes().containsKey(name.getActual()) == false) {
      throw ResolverError.typeNonexistent(m, name);
    }

    return new UASTRTypeNameGlobal(m.getPath(), name);
  }

  public static @Nonnull Resolver newResolver(
    final @Nonnull UASTUCompilation compilation,
    final @Nonnull Log log)
    throws ConstraintError
  {
    return new Resolver(compilation, log);
  }

  private final @Nonnull UASTUCompilation compilation;
  private final @Nonnull Log              log;

  private Resolver(
    final @Nonnull UASTUCompilation compilation,
    final @Nonnull Log log)
    throws ConstraintError
  {
    this.compilation =
      Constraints.constrainNotNull(compilation, "Compilation");
    this.log = new Log(log, "resolver");
  }

  public final @Nonnull UASTRCompilation run()
    throws ResolverError,
      ConstraintError
  {
    final ImportResolver ri = new ImportResolver(this.compilation, this.log);

    final Map<ModulePathFlat, UASTUDModule> modules =
      this.compilation.getModules();

    final HashMap<ModulePathFlat, UASTRDModule> results =
      new HashMap<ModulePathFlat, UASTRDModule>();

    for (final ModulePathFlat path : modules.keySet()) {
      final UASTUDModule module = modules.get(path);
      final UASTRDModule mr =
        module.moduleVisitableAccept(new ModuleResolver(
          module,
          modules,
          this.log));
      results.put(path, mr);
    }

    return new UASTRCompilation(
      ri.getTopology(),
      results,
      this.compilation.getPaths());
  }
}
