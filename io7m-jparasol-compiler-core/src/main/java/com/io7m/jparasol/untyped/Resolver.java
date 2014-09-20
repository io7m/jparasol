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

import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph.CycleFoundException;
import org.jgrapht.traverse.TopologicalOrderIterator;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jequality.annotations.EqualityStructural;
import com.io7m.jfunctional.Option;
import com.io7m.jfunctional.OptionType;
import com.io7m.jfunctional.PartialFunctionType;
import com.io7m.jfunctional.Some;
import com.io7m.jfunctional.Unit;
import com.io7m.jlog.LogLevel;
import com.io7m.jlog.LogUsableType;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
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
import com.io7m.jparasol.untyped.ast.resolved.UASTRExpression.UASTREMatrixColumnAccess;
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
import com.io7m.jparasol.untyped.ast.resolved.UASTRTermNameVisitorType;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTypeName;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTypeName.UASTRTypeNameBuiltIn;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTypeName.UASTRTypeNameGlobal;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTypeNameVisitorType;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUCompilation;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDRecordVisitorType;
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
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUEMatrixColumnAccess;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUENew;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUEReal;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUERecord;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUERecordProjection;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUESwizzle;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUEVariable;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTURecordFieldAssignment;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpressionVisitorType;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUFragmentShaderLocalVisitorType;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUFragmentShaderOutputVisitorType;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUFragmentShaderVisitorType;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTULocalLevelVisitorType;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUModuleVisitorType;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUShaderPath;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUShaderVisitorType;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUTermVisitorType;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUTypePath;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUTypeVisitorType;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUVertexShaderLocalVisitorType;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUVertexShaderVisitorType;
import com.io7m.jparasol.untyped.ast.unique_binders.UniqueName;
import com.io7m.jparasol.untyped.ast.unique_binders.UniqueName.UniqueNameLocal;
import com.io7m.jparasol.untyped.ast.unique_binders.UniqueName.UniqueNameNonLocal;
import com.io7m.jparasol.untyped.ast.unique_binders.UniqueNameVisitorType;

/**
 * The name resolver.
 */

@SuppressWarnings("synthetic-access") @EqualityReference public final class Resolver
{
  @EqualityReference private static final class ExpressionResolver implements
    UASTUExpressionVisitorType<UASTRExpression, UASTRDValueLocal, ResolverError>
  {
    private final UASTUDModule                      module;
    private final Map<ModulePathFlat, UASTUDModule> modules;
    private final @Nullable UASTUDTerm              term;
    private final @Nullable TermGraph               term_graph;

    public ExpressionResolver(
      final @Nullable UASTUDTerm in_term,
      final UASTUDModule in_module,
      final Map<ModulePathFlat, UASTUDModule> in_modules,
      final @Nullable TermGraph in_term_graph)
    {
      this.term = in_term;
      this.module = in_module;
      this.modules = in_modules;
      this.term_graph = in_term_graph;
    }

    /**
     * Add a reference from the current term to the given name, raising an
     * error on cyclic references.
     */

    private void checkTermReferenceRecursion(
      final UASTRTermName name)
      throws ResolverError
    {
      if (this.term_graph != null) {
        name
          .termNameVisitableAccept(new UASTRTermNameVisitorType<Unit, ResolverError>() {
            @Override public Unit termNameVisitGlobal(
              final UASTRTermNameGlobal t)
              throws ResolverError
            {
              final TermGraph tg = ExpressionResolver.this.term_graph;
              final UASTUDTerm tt = ExpressionResolver.this.term;
              assert tg != null;
              assert tt != null;

              tg.addTermReference(
                ExpressionResolver.this.module.getPath(),
                tt.getName(),
                t.getPath(),
                t.getName());

              return Unit.unit();
            }

            @Override public Unit termNameVisitLocal(
              final UASTRTermNameLocal t)
              throws ResolverError
            {
              return Unit.unit();
            }
          });
      }
    }

    @Override public UASTREApplication expressionVisitApplication(
      final List<UASTRExpression> arguments,
      final UASTUEApplication e)
      throws ResolverError
    {
      final UniqueName en = e.getName();
      final UASTRTermName name =
        Resolver.lookupTerm(this.module, this.modules, en);

      this.checkTermReferenceRecursion(name);
      return new UASTREApplication(name, arguments);
    }

    @Override public void expressionVisitApplicationPre(
      final UASTUEApplication e)
      throws ResolverError
    {
      // Nothing
    }

    @Override public UASTREBoolean expressionVisitBoolean(
      final UASTUEBoolean e)
      throws ResolverError
    {
      return new UASTREBoolean(e.getToken());
    }

    @Override public UASTREConditional expressionVisitConditional(
      final UASTRExpression condition,
      final UASTRExpression left,
      final UASTRExpression right,
      final UASTUEConditional e)
      throws ResolverError
    {
      return new UASTREConditional(e.getIf(), condition, left, right);
    }

    @Override public void expressionVisitConditionalPre(
      final UASTUEConditional e)
      throws ResolverError
    {
      // Nothing
    }

    @Override public UASTREInteger expressionVisitInteger(
      final UASTUEInteger e)
      throws ResolverError
    {
      return new UASTREInteger(e.getToken());
    }

    @Override public UASTRELet expressionVisitLet(
      final List<UASTRDValueLocal> bindings,
      final UASTRExpression body,
      final UASTUELet e)
      throws ResolverError
    {
      return new UASTRELet(e.getToken(), bindings, body);
    }

    @Override public
      UASTULocalLevelVisitorType<UASTRDValueLocal, ResolverError>
      expressionVisitLetPre(
        final UASTUELet e)
        throws ResolverError
    {
      return new LocalResolver(
        this.term,
        this.module,
        this.modules,
        this.term_graph);
    }

    @Override public UASTRExpression expressionVisitMatrixColumnAccess(
      final UASTRExpression body,
      final UASTUEMatrixColumnAccess e)
      throws ResolverError
    {
      return new UASTREMatrixColumnAccess(body, e.getColumn());
    }

    @Override public void expressionVisitMatrixColumnAccessPre(
      final UASTUEMatrixColumnAccess e)
      throws ResolverError
    {
      // Nothing
    }

    @Override public UASTRENew expressionVisitNew(
      final List<UASTRExpression> arguments,
      final UASTUENew e)
      throws ResolverError
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
      final UASTUENew e)
      throws ResolverError
    {
      // Nothing
    }

    @Override public UASTREReal expressionVisitReal(
      final UASTUEReal e)
      throws ResolverError
    {
      return new UASTREReal(e.getToken());
    }

    @Override public UASTRERecord expressionVisitRecord(
      final UASTUERecord e)
      throws ResolverError
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
      final UASTRExpression body,
      final UASTUERecordProjection e)
      throws ResolverError
    {
      return new UASTRERecordProjection(body, e.getField());
    }

    @Override public void expressionVisitRecordProjectionPre(
      final UASTUERecordProjection e)
      throws ResolverError
    {
      // Nothing
    }

    @Override public UASTRESwizzle expressionVisitSwizzle(
      final UASTRExpression body,
      final UASTUESwizzle e)
      throws ResolverError
    {
      return new UASTRESwizzle(body, e.getFields());
    }

    @Override public void expressionVisitSwizzlePre(
      final UASTUESwizzle e)
      throws ResolverError
    {
      // Nothing
    }

    @Override public UASTREVariable expressionVisitVariable(
      final UASTUEVariable e)
      throws ResolverError
    {
      final UniqueName unique = e.getName();
      final UASTRTermName name =
        Resolver.lookupTerm(this.module, this.modules, unique);

      this.checkTermReferenceRecursion(name);
      return new UASTREVariable(name);
    }
  }

  @EqualityReference private static final class FragmentShaderLocalResolver implements
    UASTUFragmentShaderLocalVisitorType<UASTRDShaderFragmentLocal, ResolverError>
  {
    private final LogUsableType                     log;
    private final UASTUDModule                      module;
    private final Map<ModulePathFlat, UASTUDModule> modules;
    private final UASTUDShaderFragment              shader;

    public FragmentShaderLocalResolver(
      final LogUsableType in_log,
      final UASTUDModule in_module,
      final Map<ModulePathFlat, UASTUDModule> in_modules,
      final UASTUDShaderFragment in_shader)
    {
      this.module = in_module;
      this.modules = in_modules;
      this.shader = in_shader;
      this.log = in_log;
    }

    @Override public
      UASTRDShaderFragmentLocalDiscard
      fragmentShaderVisitLocalDiscard(
        final UASTUDShaderFragmentLocalDiscard d)
        throws ResolverError
    {
      final UASTRExpression ex =
        d.getExpression().expressionVisitableAccept(
          new ExpressionResolver(null, this.module, this.modules, null));

      return new UASTRDShaderFragmentLocalDiscard(d.getDiscard(), ex);
    }

    @Override public
      UASTRDShaderFragmentLocalValue
      fragmentShaderVisitLocalValue(
        final UASTUDShaderFragmentLocalValue v)
        throws ResolverError
    {
      final UASTUDValueLocal vv = v.getValue();
      final LocalResolver lr =
        new LocalResolver(null, this.module, this.modules, null);

      return new UASTRDShaderFragmentLocalValue(vv.localVisitableAccept(lr));
    }
  }

  @EqualityReference private static final class FragmentShaderResolver implements
    UASTUFragmentShaderVisitorType<UASTRDShaderFragment, UASTRDShaderFragmentInput, UASTRDShaderFragmentParameter, UASTRDShaderFragmentOutput, UASTRDShaderFragmentLocal, UASTRDShaderFragmentOutputAssignment, ResolverError>
  {
    private final LogUsableType                     log;
    private final UASTUDModule                      module;
    private final Map<ModulePathFlat, UASTUDModule> modules;
    private final Map<String, TokenIdentifierLower> outputs_declared;
    private final UASTUDShaderFragment              shader;

    public FragmentShaderResolver(
      final LogUsableType in_log,
      final UASTUDModule in_module,
      final Map<ModulePathFlat, UASTUDModule> in_modules,
      final UASTUDShaderFragment f)
    {
      this.module = in_module;
      this.modules = in_modules;
      this.shader = f;
      this.log = in_log;
      this.outputs_declared = new HashMap<String, TokenIdentifierLower>();
    }

    @Override public UASTRDShaderFragment fragmentShaderVisit(
      final List<UASTRDShaderFragmentInput> inputs,
      final List<UASTRDShaderFragmentParameter> parameters,
      final List<UASTRDShaderFragmentOutput> outputs,
      final List<UASTRDShaderFragmentLocal> locals,
      final List<UASTRDShaderFragmentOutputAssignment> output_assignments,
      final UASTUDShaderFragment f)
      throws ResolverError
    {
      return new UASTRDShaderFragment(
        f.getName(),
        inputs,
        outputs,
        parameters,
        locals,
        output_assignments);
    }

    @Override public UASTRDShaderFragmentInput fragmentShaderVisitInput(
      final UASTUDShaderFragmentInput i)
      throws ResolverError
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
      UASTUFragmentShaderLocalVisitorType<UASTRDShaderFragmentLocal, ResolverError>
      fragmentShaderVisitLocalsPre()
        throws ResolverError
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
        final UASTUDShaderFragmentOutputAssignment a)
        throws ResolverError
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

    @Override public
      UASTUFragmentShaderOutputVisitorType<UASTRDShaderFragmentOutput, ResolverError>
      fragmentShaderVisitOutputsPre()
        throws ResolverError
    {
      return new UASTUFragmentShaderOutputVisitorType<UASTRDShaderFragmentOutput, ResolverError>() {
        @Override public
          UASTRDShaderFragmentOutput
          fragmentShaderVisitOutputData(
            final UASTUDShaderFragmentOutputData d)
            throws ResolverError
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
            final UASTUDShaderFragmentOutputDepth d)
            throws ResolverError
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

    @Override public
      UASTRDShaderFragmentParameter
      fragmentShaderVisitParameter(
        final UASTUDShaderFragmentParameter p)
        throws ResolverError
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

  @EqualityReference private static final class ImportResolver
  {
    @EqualityStructural private static final class Import
    {
      private final UASTUDImport   actual;
      private final ModulePathFlat importer;
      private final ModulePathFlat target;

      public Import(
        final UASTUDImport in_actual,
        final ModulePathFlat in_importer,
        final ModulePathFlat in_target)
      {
        this.actual = in_actual;
        this.importer = in_importer;
        this.target = in_target;
      }

      @Override public boolean equals(
        final @Nullable Object obj)
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
        builder.append(" → ");
        builder.append(this.target);
        builder.append("]");
        return builder.toString();
      }
    }

    private final UASTUCompilation                           compilation;
    private final DirectedAcyclicGraph<UASTUDModule, Import> import_graph;
    private final LogUsableType                              log;
    private final StringBuilder                              message;

    public ImportResolver(
      final UASTUCompilation in_compilation,
      final LogUsableType in_log)
      throws ResolverError
    {
      this.log = in_log.with("imports");
      this.message = new StringBuilder();
      this.compilation = in_compilation;

      this.import_graph =
        new DirectedAcyclicGraph<UASTUDModule, Import>(Import.class);

      final Map<ModulePathFlat, UASTUDModule> modules =
        in_compilation.getModules();

      for (final ModulePathFlat path : modules.keySet()) {
        final UASTUDModule module = modules.get(path);
        if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
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

          if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
            this.message.setLength(0);
            this.message.append("Adding module import ");
            this.message.append(path.getActual());
            this.message.append(" → ");
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
             * Because a cycle as occurred on an insertion of edge A → B, then
             * there must be some path B → A already in the graph. Use a
             * shortest path algorithm to determine that path.
             */

            final DijkstraShortestPath<UASTUDModule, Import> dj =
              new DijkstraShortestPath<UASTUDModule, Import>(
                this.import_graph,
                target_module,
                module);

            final List<Import> imports = dj.getPathEdgeList();
            assert imports != null;

            final List<UASTUDImport> imports_ = new ArrayList<UASTUDImport>();
            for (final Import im : imports) {
              imports_.add(im.actual);
            }

            throw ResolverError.moduleImportCyclic(i, target, imports_);
          }
        }
      }
    }

    public List<ModulePathFlat> getTopology()
    {
      final TopologicalOrderIterator<UASTUDModule, Import> iter =
        new TopologicalOrderIterator<UASTUDModule, Import>(this.import_graph);

      final List<ModulePathFlat> ls = new ArrayList<ModulePathFlat>();
      while (iter.hasNext()) {
        final UASTUDModule m = iter.next();
        ls.add(ModulePathFlat.fromModulePath(m.getPath()));
      }

      return ls;
    }
  }

  @EqualityReference private static final class LocalResolver implements
    UASTULocalLevelVisitorType<UASTRDValueLocal, ResolverError>
  {
    private final UASTUDModule                      module;
    private final Map<ModulePathFlat, UASTUDModule> modules;
    private final @Nullable UASTUDTerm              term;
    private final @Nullable TermGraph               term_graph;

    public LocalResolver(
      final @Nullable UASTUDTerm in_term,
      final UASTUDModule in_module,
      final Map<ModulePathFlat, UASTUDModule> in_modules,
      final @Nullable TermGraph in_term_graph)
    {
      this.term = in_term;
      this.module = in_module;
      this.modules = in_modules;
      this.term_graph = in_term_graph;
    }

    @Override public UASTRDValueLocal localVisitValueLocal(
      final UASTUDValueLocal v)
      throws ResolverError
    {
      final OptionType<UASTRTypeName> ascription =
        v
          .getAscription()
          .mapPartial(
            new PartialFunctionType<UASTUTypePath, UASTRTypeName, ResolverError>() {
              @Override public UASTRTypeName call(
                final UASTUTypePath t)
                throws ResolverError
              {
                return Resolver.lookupType(
                  LocalResolver.this.modules,
                  LocalResolver.this.module,
                  t.getModule(),
                  t.getName());
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

  @EqualityReference private static class ModuleResolver implements
    UASTUModuleVisitorType<UASTRDModule, UASTRDImport, UASTRDeclarationModuleLevel, UASTRDTerm, UASTRDType, UASTRDShader, ResolverError>,
    UASTUTermVisitorType<UASTRDTerm, ResolverError>,
    UASTUTypeVisitorType<UASTRDType, ResolverError>,
    UASTUShaderVisitorType<UASTRDShader, ResolverError>
  {
    private final LogUsableType                     log;
    private final UASTUDModule                      module;
    private final Map<ModulePathFlat, UASTUDModule> modules;
    private final ShaderGraph                       shader_graph;
    private final TermGraph                         term_graph;
    private final TypeGraph                         type_graph;

    public ModuleResolver(
      final UASTUDModule in_module,
      final Map<ModulePathFlat, UASTUDModule> in_modules,
      final LogUsableType in_log)
    {
      this.module = in_module;
      this.modules = in_modules;
      this.term_graph = new TermGraph(in_log);
      this.type_graph = new TypeGraph(in_log);
      this.shader_graph = new ShaderGraph(in_log);
      this.log = in_log.with("names");

      if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
        final StringBuilder m = new StringBuilder();
        m.append("Resolving ");
        m.append(ModulePathFlat
          .fromModulePath(in_module.getPath())
          .getActual());
        this.log.debug(m.toString());
      }
    }

    @Override public
      UASTUShaderVisitorType<UASTRDShader, ResolverError>
      moduleShadersPre(
        final UASTUDModule m)
        throws ResolverError
    {
      return this;
    }

    @Override public
      UASTUTermVisitorType<UASTRDTerm, ResolverError>
      moduleTermsPre(
        final UASTUDModule m)
        throws ResolverError
    {
      return this;
    }

    @Override public
      UASTUTypeVisitorType<UASTRDType, ResolverError>
      moduleTypesPre(
        final UASTUDModule m)
        throws ResolverError
    {
      return this;
    }

    @Override public UASTRDModule moduleVisit(
      final List<UASTRDImport> imports,
      final List<UASTRDeclarationModuleLevel> declarations,
      final Map<String, UASTRDTerm> terms,
      final Map<String, UASTRDType> types,
      final Map<String, UASTRDShader> shaders,
      final UASTUDModule m)
      throws ResolverError
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

    @Override public UASTRDShader moduleVisitFragmentShader(
      final UASTUDShaderFragment f)
      throws ResolverError
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

    @Override public UASTRDImport moduleVisitImport(
      final UASTUDImport i)
      throws ResolverError
    {
      return new UASTRDImport(i.getPath(), i.getRename());
    }

    @Override public UASTRDShaderProgram moduleVisitProgramShader(
      final UASTUDShaderProgram p)
      throws ResolverError
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

    @Override public UASTRDShaderVertex moduleVisitVertexShader(
      final UASTUDShaderVertex v)
      throws ResolverError
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

    @Override public UASTRDTerm termVisitFunctionDefined(
      final UASTUDFunctionDefined f)
      throws ResolverError
    {
      return f.termVisitableAccept(new TermResolver(
        this.log,
        this.module,
        this.modules,
        this.term_graph,
        f));
    }

    @Override public UASTRDTerm termVisitFunctionExternal(
      final UASTUDFunctionExternal f)
      throws ResolverError
    {
      return f.termVisitableAccept(new TermResolver(
        this.log,
        this.module,
        this.modules,
        this.term_graph,
        f));
    }

    @Override public UASTRDTerm termVisitValueDefined(
      final UASTUDValueDefined v)
      throws ResolverError
    {
      return v.termVisitableAccept(new TermResolver(
        this.log,
        this.module,
        this.modules,
        this.term_graph,
        v));
    }

    @Override public UASTRDTerm termVisitValueExternal(
      final UASTUDValueExternal v)
      throws ResolverError
    {
      return v.termVisitableAccept(new TermResolver(
        this.log,
        this.module,
        this.modules,
        this.term_graph,
        v));
    }

    @Override public UASTRDType typeVisitTypeRecord(
      final UASTUDTypeRecord r)
      throws ResolverError
    {
      return r.recordTypeVisitableAccept(new RecordTypeResolver(
        this.log,
        this.module,
        this.modules,
        this.type_graph,
        r));
    }
  }

  @EqualityReference private static final class RecordTypeResolver implements
    UASTUDRecordVisitorType<UASTRDTypeRecord, UASTRDTypeRecordField, ResolverError>
  {
    private final LogUsableType                     log;
    private final UASTUDModule                      module;
    private final Map<ModulePathFlat, UASTUDModule> modules;
    private final UASTUDTypeRecord                  type;
    private final TypeGraph                         type_graph;

    public RecordTypeResolver(
      final LogUsableType in_log,
      final UASTUDModule in_module,
      final Map<ModulePathFlat, UASTUDModule> in_modules,
      final TypeGraph in_type_graph,
      final UASTUDTypeRecord r)
    {
      this.log = in_log;
      this.module = in_module;
      this.modules = in_modules;
      this.type_graph = in_type_graph;
      this.type = r;
    }

    @Override public UASTRDTypeRecord recordTypeVisit(
      final List<UASTRDTypeRecordField> fields,
      final UASTUDTypeRecord e)
      throws ResolverError
    {
      this.type_graph.addType(this.module.getPath(), e.getName());
      return new UASTRDTypeRecord(e.getName(), fields);
    }

    @Override public UASTRDTypeRecordField recordTypeVisitField(
      final UASTUDTypeRecordField e)
      throws ResolverError
    {
      final UASTUTypePath et = e.getType();
      final UASTRTypeName t =
        Resolver.lookupType(
          this.modules,
          this.module,
          et.getModule(),
          et.getName());

      t
        .typeNameVisitableAccept(new UASTRTypeNameVisitorType<Unit, ResolverError>() {
          @Override public Unit typeNameVisitBuiltIn(
            final UASTRTypeNameBuiltIn name)
            throws ResolverError
          {
            return Unit.unit();
          }

          @Override public Unit typeNameVisitGlobal(
            final UASTRTypeNameGlobal name)
            throws ResolverError
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
      final UASTUDTypeRecord e)
      throws ResolverError
    {
      // Nothing
    }
  }

  /**
   * A shader in the shader graph.
   */

  @EqualityStructural private static final class Shader
  {
    private final ModulePathFlat module;
    private final String         name;

    public Shader(
      final ModulePathFlat in_module,
      final String in_name)
    {
      this.module = in_module;
      this.name = in_name;
    }

    @Override public boolean equals(
      final @Nullable Object obj)
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

  @EqualityReference private static final class ShaderGraph
  {
    private final DirectedAcyclicGraph<Shader, ShaderReference> graph;
    private final LogUsableType                                 log;

    public ShaderGraph(
      final LogUsableType in_log)
    {
      this.graph =
        new DirectedAcyclicGraph<Shader, ShaderReference>(
          ShaderReference.class);
      this.log = in_log.with("shader-graph");
    }

    public void addShader(
      final ModulePath source_module,
      final TokenIdentifierLower source_name)
    {
      final ModulePathFlat source_module_flat =
        ModulePathFlat.fromModulePath(source_module);
      final Shader source =
        new Shader(source_module_flat, source_name.getActual());

      if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
        this.log.debug(String.format(
          "Adding shader: %s.%s",
          source_module_flat.getActual(),
          source_name.getActual()));
      }

      this.graph.addVertex(source);
    }

    public void addShaderReference(
      final ModulePath source_module,
      final TokenIdentifierLower source_name,
      final ModulePath target_module,
      final TokenIdentifierLower target_name)
      throws ResolverError
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

        if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
          this.log.debug(String.format(
            "Adding shader reference: %s.%s → %s.%s",
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
         * Because a cycle as occurred on an insertion of edge A → B, then
         * there must be some path B → A already in the graph. Use a shortest
         * path algorithm to determine that path.
         */

        final DijkstraShortestPath<Shader, ShaderReference> dj =
          new DijkstraShortestPath<Shader, ShaderReference>(
            this.graph,
            target,
            source);

        final List<ShaderReference> terms = dj.getPathEdgeList();
        assert terms != null;

        final List<TokenIdentifierLower> tokens =
          new ArrayList<TokenIdentifierLower>();
        tokens.add(target_name);

        for (final ShaderReference ref : terms) {
          tokens.add(ref.target_name);
        }

        throw ResolverError.shaderRecursiveMutual(source_name, tokens);
      }
    }

    public List<String> getTopology(
      final ModulePathFlat current)
    {
      final TopologicalOrderIterator<Shader, ShaderReference> iter =
        new TopologicalOrderIterator<Shader, ShaderReference>(this.graph);

      final List<String> ls = new ArrayList<String>();
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

  @EqualityStructural private static final class ShaderReference
  {
    private final ModulePath           source_module;
    private final TokenIdentifierLower source_name;
    private final ModulePath           target_module;
    private final TokenIdentifierLower target_name;

    public ShaderReference(
      final ModulePath in_source_module,
      final TokenIdentifierLower in_source_name,
      final ModulePath in_target_module,
      final TokenIdentifierLower in_target_name)
    {
      this.source_module = in_source_module;
      this.source_name = in_source_name;
      this.target_module = in_target_module;
      this.target_name = in_target_name;
    }

    @Override public boolean equals(
      final @Nullable Object obj)
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

  @EqualityStructural private static final class Term
  {
    private final ModulePathFlat module;
    private final String         name;

    public Term(
      final ModulePathFlat in_module,
      final String in_name)
    {
      this.module = in_module;
      this.name = in_name;
    }

    @Override public boolean equals(
      final @Nullable Object obj)
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

  @EqualityReference private static final class TermGraph
  {
    private final DirectedAcyclicGraph<Term, TermReference> graph;
    private final LogUsableType                             log;

    public TermGraph(
      final LogUsableType in_log)
    {
      this.graph =
        new DirectedAcyclicGraph<Term, TermReference>(TermReference.class);
      this.log = in_log.with("term-graph");
    }

    public void addTerm(
      final ModulePath source_module,
      final TokenIdentifierLower source_name)
    {
      final ModulePathFlat source_module_flat =
        ModulePathFlat.fromModulePath(source_module);
      final Term source =
        new Term(source_module_flat, source_name.getActual());

      if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
        this.log.debug(String.format(
          "Adding term: %s.%s",
          source_module_flat.getActual(),
          source_name.getActual()));
      }

      this.graph.addVertex(source);
    }

    public void addTermReference(
      final ModulePath source_module,
      final TokenIdentifierLower source_name,
      final ModulePath target_module,
      final TokenIdentifierLower target_name)
      throws ResolverError
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

        if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
          this.log.debug(String.format(
            "Adding term reference: %s.%s → %s.%s",
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
         * Because a cycle as occurred on an insertion of edge A → B, then
         * there must be some path B → A already in the graph. Use a shortest
         * path algorithm to determine that path.
         */

        final DijkstraShortestPath<Term, TermReference> dj =
          new DijkstraShortestPath<Term, TermReference>(
            this.graph,
            target,
            source);

        final List<TermReference> terms = dj.getPathEdgeList();
        assert terms != null;

        final List<TokenIdentifierLower> tokens =
          new ArrayList<TokenIdentifierLower>();
        tokens.add(target_name);

        for (final TermReference ref : terms) {
          tokens.add(ref.target_name);
        }

        throw ResolverError.termRecursiveMutual(source_name, tokens);
      }
    }

    public List<String> getTopology(
      final ModulePathFlat current)
    {
      final TopologicalOrderIterator<Term, TermReference> iter =
        new TopologicalOrderIterator<Term, TermReference>(this.graph);

      final List<String> ls = new ArrayList<String>();
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

  @EqualityReference private static final class TermReference
  {
    private final ModulePath           source_module;
    private final TokenIdentifierLower source_name;
    private final ModulePath           target_module;
    private final TokenIdentifierLower target_name;

    public TermReference(
      final ModulePath in_source_module,
      final TokenIdentifierLower in_source_name,
      final ModulePath in_target_module,
      final TokenIdentifierLower in_target_name)
    {
      this.source_module = in_source_module;
      this.source_name = in_source_name;
      this.target_module = in_target_module;
      this.target_name = in_target_name;
    }
  }

  @EqualityReference private static final class TermResolver implements
    UASTUTermVisitorType<UASTRDTerm, ResolverError>
  {
    private final LogUsableType                     log;
    private final UASTUDModule                      module;
    private final Map<ModulePathFlat, UASTUDModule> modules;
    private final UASTUDTerm                        term;
    private final TermGraph                         term_graph;

    public TermResolver(
      final LogUsableType in_log,
      final UASTUDModule in_module,
      final Map<ModulePathFlat, UASTUDModule> in_modules,
      final TermGraph in_term_graph,
      final UASTUDTerm t)
    {
      this.log = in_log;
      this.module = in_module;
      this.modules = in_modules;
      this.term_graph = in_term_graph;
      this.term = t;
    }

    @Override public UASTRDFunctionDefined termVisitFunctionDefined(
      final UASTUDFunctionDefined f)
      throws ResolverError
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

    @Override public UASTRDFunctionExternal termVisitFunctionExternal(
      final UASTUDFunctionExternal f)
      throws ResolverError
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
      final OptionType<UASTUExpression> original_emulation =
        ext.getEmulation();
      final OptionType<UASTRExpression> emulation =
        original_emulation
          .mapPartial(new PartialFunctionType<UASTUExpression, UASTRExpression, ResolverError>() {
            @Override public UASTRExpression call(
              final UASTUExpression e)
              throws ResolverError
            {
              return e.expressionVisitableAccept(new ExpressionResolver(
                f,
                TermResolver.this.module,
                TermResolver.this.modules,
                TermResolver.this.term_graph));
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
      final UASTUDValueDefined v)
      throws ResolverError
    {
      final OptionType<UASTRTypeName> ascription =
        v
          .getAscription()
          .mapPartial(
            new PartialFunctionType<UASTUTypePath, UASTRTypeName, ResolverError>() {
              @Override public UASTRTypeName call(
                final UASTUTypePath x)
                throws ResolverError
              {
                return Resolver.lookupType(
                  TermResolver.this.modules,
                  TermResolver.this.module,
                  x.getModule(),
                  x.getName());
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

    @Override public UASTRDTerm termVisitValueExternal(
      final UASTUDValueExternal v)
      throws ResolverError
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
      final OptionType<UASTRExpression> none = Option.none();
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

  @EqualityStructural private static final class Type
  {
    private final ModulePathFlat module;
    private final String         name;

    public Type(
      final ModulePathFlat in_module,
      final String in_name)
    {
      this.module = in_module;
      this.name = in_name;
    }

    @Override public boolean equals(
      final @Nullable Object obj)
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

  @EqualityReference private static final class TypeGraph
  {
    private final DirectedAcyclicGraph<Type, TypeReference> graph;
    private final LogUsableType                             log;

    public TypeGraph(
      final LogUsableType in_log)
    {
      this.graph =
        new DirectedAcyclicGraph<Type, TypeReference>(TypeReference.class);
      this.log = in_log.with("type-graph");
    }

    public void addType(
      final ModulePath source_module,
      final TokenIdentifierLower source_name)
    {
      final ModulePathFlat source_module_flat =
        ModulePathFlat.fromModulePath(source_module);
      final Type source =
        new Type(source_module_flat, source_name.getActual());

      if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
        this.log.debug(String.format(
          "Adding type: %s.%s",
          source_module_flat.getActual(),
          source_name.getActual()));
      }

      this.graph.addVertex(source);
    }

    public void addTypeReference(
      final ModulePath source_module,
      final TokenIdentifierLower source_name,
      final ModulePath target_module,
      final TokenIdentifierLower target_name)
      throws ResolverError
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

        if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
          this.log.debug(String.format(
            "Adding type reference: %s.%s → %s.%s",
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
         * Because a cycle as occurred on an insertion of edge A → B, then
         * there must be some path B → A already in the graph. Use a shortest
         * path algorithm to determine that path.
         */

        final DijkstraShortestPath<Type, TypeReference> dj =
          new DijkstraShortestPath<Type, TypeReference>(
            this.graph,
            target,
            source);

        final List<TypeReference> terms = dj.getPathEdgeList();
        assert terms != null;

        final List<TokenIdentifierLower> tokens =
          new ArrayList<TokenIdentifierLower>();
        tokens.add(target_name);

        for (final TypeReference ref : terms) {
          tokens.add(ref.target_name);
        }

        throw ResolverError.typeRecursiveMutual(source_name, tokens);
      }
    }

    public List<String> getTopology(
      final ModulePathFlat current)
    {
      final TopologicalOrderIterator<Type, TypeReference> iter =
        new TopologicalOrderIterator<Type, TypeReference>(this.graph);

      final List<String> ls = new ArrayList<String>();
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

  @EqualityStructural private static final class TypeReference
  {
    private final ModulePath           source_module;
    private final TokenIdentifierLower source_name;
    private final ModulePath           target_module;
    private final TokenIdentifierLower target_name;

    public TypeReference(
      final ModulePath in_source_module,
      final TokenIdentifierLower in_source_name,
      final ModulePath in_target_module,
      final TokenIdentifierLower in_target_name)
    {
      this.source_module = in_source_module;
      this.source_name = in_source_name;
      this.target_module = in_target_module;
      this.target_name = in_target_name;
    }

    @Override public boolean equals(
      final @Nullable Object obj)
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

  @EqualityReference private static final class VertexShaderLocalResolver implements
    UASTUVertexShaderLocalVisitorType<UASTRDShaderVertexLocalValue, ResolverError>
  {
    private final LogUsableType                     log;
    private final UASTUDModule                      module;
    private final Map<ModulePathFlat, UASTUDModule> modules;
    private final UASTUDShaderVertex                shader;

    public VertexShaderLocalResolver(
      final LogUsableType in_log,
      final UASTUDModule in_module,
      final Map<ModulePathFlat, UASTUDModule> in_modules,
      final UASTUDShaderVertex in_shader)
    {
      this.module = in_module;
      this.modules = in_modules;
      this.shader = in_shader;
      this.log = in_log;
    }

    @Override public
      UASTRDShaderVertexLocalValue
      vertexShaderVisitLocalValue(
        final UASTUDShaderVertexLocalValue v)
        throws ResolverError
    {
      final UASTUDValueLocal vv = v.getValue();
      final LocalResolver lr =
        new LocalResolver(null, this.module, this.modules, null);

      return new UASTRDShaderVertexLocalValue(vv.localVisitableAccept(lr));
    }
  }

  @EqualityReference private static final class VertexShaderResolver implements
    UASTUVertexShaderVisitorType<UASTRDShaderVertex, UASTRDShaderVertexInput, UASTRDShaderVertexParameter, UASTRDShaderVertexOutput, UASTRDShaderVertexLocalValue, UASTRDShaderVertexOutputAssignment, ResolverError>
  {
    private final LogUsableType                     log;
    private final UASTUDModule                      module;
    private final Map<ModulePathFlat, UASTUDModule> modules;
    private final Map<String, TokenIdentifierLower> outputs_declared;
    private final UASTUDShaderVertex                shader;

    public VertexShaderResolver(
      final LogUsableType in_log,
      final UASTUDModule in_module,
      final Map<ModulePathFlat, UASTUDModule> in_modules,
      final UASTUDShaderVertex v)
    {
      this.module = in_module;
      this.modules = in_modules;
      this.shader = v;
      this.log = in_log;
      this.outputs_declared = new HashMap<String, TokenIdentifierLower>();
    }

    @Override public UASTRDShaderVertex vertexShaderVisit(
      final List<UASTRDShaderVertexInput> inputs,
      final List<UASTRDShaderVertexParameter> parameters,
      final List<UASTRDShaderVertexOutput> outputs,
      final List<UASTRDShaderVertexLocalValue> locals,
      final List<UASTRDShaderVertexOutputAssignment> output_assignments,
      final UASTUDShaderVertex v)
      throws ResolverError
    {
      return new UASTRDShaderVertex(
        v.getName(),
        inputs,
        outputs,
        parameters,
        locals,
        output_assignments);
    }

    @Override public UASTRDShaderVertexInput vertexShaderVisitInput(
      final UASTUDShaderVertexInput i)
      throws ResolverError
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
      UASTUVertexShaderLocalVisitorType<UASTRDShaderVertexLocalValue, ResolverError>
      vertexShaderVisitLocalsPre()
        throws ResolverError
    {
      return new VertexShaderLocalResolver(
        this.log,
        this.module,
        this.modules,
        this.shader);
    }

    @Override public UASTRDShaderVertexOutput vertexShaderVisitOutput(
      final UASTUDShaderVertexOutput o)
      throws ResolverError
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
        final UASTUDShaderVertexOutputAssignment a)
        throws ResolverError
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

    @Override public UASTRDShaderVertexParameter vertexShaderVisitParameter(
      final UASTUDShaderVertexParameter p)
      throws ResolverError
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

  private static UASTUDModule lookupModuleFromQualification(
    final UASTUDModule current,
    final Map<ModulePathFlat, UASTUDModule> modules,
    final TokenIdentifierUpper qualification)
    throws ResolverError
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

  private static UASTRShaderName lookupShader(
    final Map<ModulePathFlat, UASTUDModule> modules,
    final UASTUDModule current,
    final OptionType<TokenIdentifierUpper> qualification,
    final TokenIdentifierLower name)
    throws ResolverError
  {
    final UASTUDModule m;

    if (qualification.isNone()) {
      m = current;
    } else {
      final Some<TokenIdentifierUpper> some =
        (Some<TokenIdentifierUpper>) qualification;
      m =
        Resolver.lookupModuleFromQualification(current, modules, some.get());
    }

    if (m.getShaders().containsKey(name.getActual()) == false) {
      throw ResolverError.shaderNonexistent(m, name);
    }

    return new UASTRShaderName(m.getPath(), name);
  }

  static UASTRTermName lookupTerm(
    final UASTUDModule module,
    final Map<ModulePathFlat, UASTUDModule> modules,
    final UniqueName name)
    throws ResolverError
  {
    return name
      .uniqueNameVisitableAccept(new UniqueNameVisitorType<UASTRTermName, ResolverError>() {
        @Override public UASTRTermName uniqueNameVisitLocal(
          final UniqueNameLocal name_actual)
          throws ResolverError
        {
          return new UASTRTermNameLocal(
            name_actual.getOriginal(),
            name_actual.getCurrent());
        }

        @Override public UASTRTermName uniqueNameVisitNonLocal(
          final UniqueNameNonLocal name_actual)
          throws ResolverError
        {
          final UASTUDModule m;
          if (name_actual.getModule().isNone()) {
            m = module;
          } else {
            m =
              Resolver.lookupModuleFromQualification(
                module,
                modules,
                ((Some<TokenIdentifierUpper>) name_actual.getModule()).get());
          }

          final Map<String, UASTUDTerm> terms = m.getTerms();
          if (terms.containsKey(name_actual.getName().getActual())) {
            return new UASTRTermNameGlobal(m.getPath(), name_actual.getName());
          }

          throw ResolverError.termNonexistent(m, name_actual.getName());
        }
      });
  }

  private static UASTRTypeName lookupType(
    final Map<ModulePathFlat, UASTUDModule> modules,
    final UASTUDModule current,
    final OptionType<TokenIdentifierUpper> qualification,
    final TokenIdentifierLower name)
    throws ResolverError
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
        Resolver.lookupModuleFromQualification(current, modules, some.get());
    }

    if (m.getTypes().containsKey(name.getActual()) == false) {
      throw ResolverError.typeNonexistent(m, name);
    }

    return new UASTRTypeNameGlobal(m.getPath(), name);
  }

  /**
   * Construct a new name resolver for the given AST.
   *
   * @param compilation
   *          The AST
   * @param log
   *          A log interface
   * @return A name resolver
   */

  public static Resolver newResolver(
    final UASTUCompilation compilation,
    final LogUsableType log)
  {
    return new Resolver(compilation, log);
  }

  private final UASTUCompilation compilation;
  private final LogUsableType    log;

  private Resolver(
    final UASTUCompilation in_compilation,
    final LogUsableType in_log)
  {
    this.compilation = NullCheck.notNull(in_compilation, "Compilation");
    this.log = NullCheck.notNull(in_log, "Log").with("resolver");
  }

  /**
   * @return A resolved AST
   * @throws ResolverError
   *           If a name resolution error occurs
   */

  public UASTRCompilation run()
    throws ResolverError
  {
    final ImportResolver ri = new ImportResolver(this.compilation, this.log);

    final Map<ModulePathFlat, UASTUDModule> modules =
      this.compilation.getModules();

    final Map<ModulePathFlat, UASTRDModule> results =
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
