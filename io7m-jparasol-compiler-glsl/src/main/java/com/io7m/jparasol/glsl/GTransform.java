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

package com.io7m.jparasol.glsl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jfunctional.FunctionType;
import com.io7m.jfunctional.Option;
import com.io7m.jfunctional.OptionType;
import com.io7m.jfunctional.Pair;
import com.io7m.jfunctional.Unit;
import com.io7m.jlog.LogLevel;
import com.io7m.jlog.LogUsableType;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.core.GVersionType;
import com.io7m.jparasol.glsl.GFFIExpression.GFFIExpressionBuiltIn;
import com.io7m.jparasol.glsl.GFFIExpression.GFFIExpressionDefined;
import com.io7m.jparasol.glsl.ast.GASTExpression;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEApplication;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEBoolean;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEConstruction;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEProjection;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTESwizzle;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEVariable;
import com.io7m.jparasol.glsl.ast.GASTFragmentShaderStatement;
import com.io7m.jparasol.glsl.ast.GASTFragmentShaderStatement.GASTFragmentConditionalDiscard;
import com.io7m.jparasol.glsl.ast.GASTFragmentShaderStatement.GASTFragmentLocalVariable;
import com.io7m.jparasol.glsl.ast.GASTFragmentShaderStatement.GASTFragmentOutputDataAssignment;
import com.io7m.jparasol.glsl.ast.GASTFragmentShaderStatement.GASTFragmentOutputDepthAssignment;
import com.io7m.jparasol.glsl.ast.GASTShader.GASTShaderFragment;
import com.io7m.jparasol.glsl.ast.GASTShader.GASTShaderFragmentInput;
import com.io7m.jparasol.glsl.ast.GASTShader.GASTShaderFragmentOutput;
import com.io7m.jparasol.glsl.ast.GASTShader.GASTShaderFragmentParameter;
import com.io7m.jparasol.glsl.ast.GASTShader.GASTShaderVertex;
import com.io7m.jparasol.glsl.ast.GASTShader.GASTShaderVertexInput;
import com.io7m.jparasol.glsl.ast.GASTShader.GASTShaderVertexOutput;
import com.io7m.jparasol.glsl.ast.GASTShader.GASTShaderVertexParameter;
import com.io7m.jparasol.glsl.ast.GASTShaderMain.GASTShaderMainFragment;
import com.io7m.jparasol.glsl.ast.GASTShaderMain.GASTShaderMainVertex;
import com.io7m.jparasol.glsl.ast.GASTStatement;
import com.io7m.jparasol.glsl.ast.GASTStatement.GASTConditional;
import com.io7m.jparasol.glsl.ast.GASTStatement.GASTLocalVariable;
import com.io7m.jparasol.glsl.ast.GASTStatement.GASTReturn;
import com.io7m.jparasol.glsl.ast.GASTStatement.GASTScope;
import com.io7m.jparasol.glsl.ast.GASTStatement.GASTVertexShaderStatement.GASTVertexOutputAssignment;
import com.io7m.jparasol.glsl.ast.GASTTermDeclaration;
import com.io7m.jparasol.glsl.ast.GASTTermDeclaration.GASTTermFunction;
import com.io7m.jparasol.glsl.ast.GASTTermDeclaration.GASTTermValue;
import com.io7m.jparasol.glsl.ast.GASTTypeDeclaration;
import com.io7m.jparasol.glsl.ast.GFieldName;
import com.io7m.jparasol.glsl.ast.GShaderInputName;
import com.io7m.jparasol.glsl.ast.GShaderOutputName;
import com.io7m.jparasol.glsl.ast.GShaderParameterName;
import com.io7m.jparasol.glsl.ast.GTermName;
import com.io7m.jparasol.glsl.ast.GTermName.GTermNameGlobal;
import com.io7m.jparasol.glsl.ast.GTermName.GTermNameLocal;
import com.io7m.jparasol.glsl.ast.GTypeName;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.typed.Occurences;
import com.io7m.jparasol.typed.TType;
import com.io7m.jparasol.typed.TType.TRecord;
import com.io7m.jparasol.typed.TType.TRecordField;
import com.io7m.jparasol.typed.TType.TValueType;
import com.io7m.jparasol.typed.TTypeNameFlat;
import com.io7m.jparasol.typed.Topology;
import com.io7m.jparasol.typed.ast.TASTCompilation;
import com.io7m.jparasol.typed.ast.TASTDeclaration;
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
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragmentOutputData;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragmentOutputDepth;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragmentParameter;
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
import com.io7m.jparasol.typed.ast.TASTExpressionVisitorType;
import com.io7m.jparasol.typed.ast.TASTFragmentShaderLocalVisitorType;
import com.io7m.jparasol.typed.ast.TASTFragmentShaderOutputVisitorType;
import com.io7m.jparasol.typed.ast.TASTLocalLevelVisitorType;
import com.io7m.jparasol.typed.ast.TASTShaderNameFlat;
import com.io7m.jparasol.typed.ast.TASTTermName;
import com.io7m.jparasol.typed.ast.TASTTermName.TASTTermNameExternal;
import com.io7m.jparasol.typed.ast.TASTTermName.TASTTermNameGlobal;
import com.io7m.jparasol.typed.ast.TASTTermName.TASTTermNameLocal;
import com.io7m.jparasol.typed.ast.TASTTermNameFlat;
import com.io7m.jparasol.typed.ast.TASTTermNameVisitorType;
import com.io7m.jparasol.typed.ast.TASTTermVisitorType;
import com.io7m.jparasol.typed.ast.TASTTypeVisitorType;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * Functions to transform Parasol shaders to GLSL.
 */

@SuppressWarnings("synthetic-access") @EqualityReference public final class GTransform
{
  @EqualityReference private static final class Binding
  {
    public static Binding newBinding(
      final String original,
      final TValueType type,
      final String current)
    {
      return new Binding(original, type, current, false);
    }

    public static Binding newExternalBinding(
      final String name,
      final TValueType type)
    {
      return new Binding(name, type, name, true);
    }

    private final String     current;
    private final boolean    external;
    private final String     original;
    private final TValueType type;

    private Binding(
      final String in_original,
      final TValueType in_type,
      final String in_current,
      final boolean in_external)
    {
      this.original = in_original;
      this.current = in_current;
      this.type = in_type;
      this.external = in_external;

      assert (this.external == true
        ? this.original.equals(this.current)
        : true);
    }

    public String getCurrent()
    {
      return this.current;
    }

    public String getOriginal()
    {
      return this.original;
    }

    public TValueType getType()
    {
      return this.type;
    }

    public boolean isExternal()
    {
      return this.external;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[Binding external=");
      builder.append(this.external);
      builder.append(", original=");
      builder.append(this.original);
      builder.append(", current=");
      builder.append(this.current);
      builder.append(", type=");
      builder.append(this.type);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference private static final class Context
  {
    private static final String                                   PREFIX_LOCAL;
    private static final String                                   PREFIX_TERM;
    private static final String                                   PREFIX_TYPE;

    static {
      PREFIX_TYPE = "pt_";
      PREFIX_TERM = "p_";
      PREFIX_LOCAL = "pl_";
    }

    private final TASTCompilation                                 compilation;
    private final GFFI                                            ffi;
    private final LogUsableType                                   log;
    private final TASTShaderNameFlat                              shader_name;
    private final AtomicInteger                                   temporary_prime;
    private final GNameContext<TASTTermNameFlat, GTermNameGlobal> terms_names;
    private final Topology                                        topology;
    private final GNameContext<TTypeNameFlat, GTypeName>          types_names;

    public Context(
      final TASTCompilation in_compilation,
      final Topology in_topology,
      final TASTShaderNameFlat in_shader_name,
      final LogUsableType in_log)
    {
      this.log = NullCheck.notNull(in_log, "Log").with("context");

      this.ffi = GFFI.newFFI(this.log);
      this.compilation = in_compilation;
      this.topology = in_topology;
      this.shader_name = in_shader_name;
      this.temporary_prime = new AtomicInteger(0);

      this.types_names =
        GNameContext.newContext(
          this.log,
          new GNameConstructorType<GTypeName>() {
            @Override public GTypeName newName(
              final String name)
            {
              return new GTypeName(name);
            }
          });

      this.terms_names =
        GNameContext.newContext(
          this.log,
          new GNameConstructorType<GTermNameGlobal>() {
            @Override public GTermNameGlobal newName(
              final String name)
            {
              return new GTermNameGlobal(name);
            }
          });
    }

    public TASTCompilation getCompilation()
    {
      return this.compilation;
    }

    public GFFI getFFI()
    {
      return this.ffi;
    }

    public Binding getFreshBindingFromLocal(
      final TASTTermNameLocal name,
      final TValueType type,
      final boolean external)
    {
      final String original = name.getCurrent();
      if (external) {
        final String s =
          String.format("Fresh local (external) binding %s", original);
        assert s != null;
        this.log.debug(s);
        return Binding.newExternalBinding(original, type);
      }

      final String current = Context.PREFIX_LOCAL + original;
      final String s =
        String.format("Fresh local binding %s -> %s", original, current);
      assert s != null;
      this.log.debug(s);
      return Binding.newBinding(original, type, current);
    }

    public Binding getFreshBindingTemporary(
      final TValueType type)
    {
      final String temp = this.getFreshTemporaryName();
      return Binding.newBinding(temp, type, temp);
    }

    public GTermNameGlobal getFreshTemporaryGlobal()
    {
      return new GTermNameGlobal(this.getFreshTemporaryName());
    }

    private String getFreshTemporaryName()
    {
      final StringBuilder s = new StringBuilder();
      s.append("_tmp_");
      s.append(this.temporary_prime.incrementAndGet());
      final String name = s.toString();
      if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
        final String r = String.format("Fresh temporary %s", name);
        assert r != null;
        this.log.debug(r);
      }

      assert name != null;
      return name;
    }

    public GTermNameGlobal getGlobalTermName(
      final TASTTermNameFlat term)
    {
      final String actual = this.terms_names.getName(term).show();
      return new GTermNameGlobal(Context.PREFIX_TERM + actual);
    }

    public GTermNameLocal getLocalFromBinding(
      final Binding b)
    {
      final String s =
        String.format(
          "Local term name %s from %sbinding",
          b.getCurrent(),
          b.isExternal() ? "(external) " : "");
      assert s != null;
      this.log.debug(s);
      return new GTermNameLocal(b.getCurrent());
    }

    public LogUsableType getLog()
    {
      return this.log;
    }

    public TASTShaderNameFlat getShaderName()
    {
      return this.shader_name;
    }

    public Topology getTopology()
    {
      return this.topology;
    }

    public GTypeName getTypeName(
      final TType type)
    {
      if (type instanceof TRecord) {
        final TRecord tr = (TRecord) type;
        final GTypeName name =
          Context.this.types_names.getName(TTypeNameFlat
            .fromTypeNameGlobal(tr.getName()));
        return new GTypeName(Context.PREFIX_TYPE + name.show());
      }

      return GLSLTypeNames.getTypeName(type);
    }

    public GTermName lookupTermName(
      final Map<String, Binding> bindings,
      final TASTTermName name)
    {
      return name
        .termNameVisitableAccept(new TASTTermNameVisitorType<GTermName, UnreachableCodeException>() {
          @Override public GTermName termNameVisitExternal(
            final TASTTermNameExternal t)
          {
            throw new UnreachableCodeException();
          }

          @Override public GTermName termNameVisitGlobal(
            final TASTTermNameGlobal t)
          {
            final TASTTermNameFlat flat =
              TASTTermNameFlat.fromTermNameGlobal(t);
            return Context.this.getGlobalTermName(flat);
          }

          @Override public GTermName termNameVisitLocal(
            final TASTTermNameLocal t)
          {
            assert bindings.containsKey(t.getCurrent());
            final Binding binding = bindings.get(t.getCurrent());
            return new GTermNameLocal(binding.getCurrent());
          }
        });
    }
  }

  @EqualityReference private static final class ExpressionStatementTransformer implements
    TASTExpressionVisitorType<GASTScope, TASTDValueLocal, GFFIError>
  {
    private final Map<String, Binding>                             bindings;
    private final Context                                          context;
    private final List<Pair<GTermNameGlobal, GASTTermDeclaration>> declarations;
    private final GVersionType                                     version;

    public ExpressionStatementTransformer(
      final Context in_context,
      final Map<String, Binding> in_bindings,
      final List<Pair<GTermNameGlobal, GASTTermDeclaration>> in_declarations,
      final GVersionType in_version)
    {
      this.context = in_context;
      this.bindings = in_bindings;
      this.declarations = in_declarations;
      this.version = in_version;
    }

    @Override public GASTScope expressionVisitApplication(
      final List<GASTScope> arguments,
      final TASTEApplication e)
      throws GFFIError
    {
      return this.wrapReturn(e);
    }

    @Override public boolean expressionVisitApplicationPre(
      final TASTEApplication e)
      throws GFFIError
    {
      return false;
    }

    @Override public GASTScope expressionVisitBoolean(
      final TASTEBoolean e)
      throws GFFIError
    {
      return this.wrapReturn(e);
    }

    @Override public GASTScope expressionVisitConditional(
      final GASTScope condition,
      final GASTScope left,
      final GASTScope right,
      final TASTEConditional e)
      throws GFFIError
    {
      final GASTExpression ec =
        e.getCondition().expressionVisitableAccept(
          new ExpressionTransformer(
            this.context,
            this.declarations,
            this.bindings,
            this.version));

      final GASTScope sl =
        e.getLeft().expressionVisitableAccept(
          new ExpressionStatementTransformer(
            this.context,
            this.bindings,
            this.declarations,
            this.version));

      final GASTScope sr =
        e.getRight().expressionVisitableAccept(
          new ExpressionStatementTransformer(
            this.context,
            this.bindings,
            this.declarations,
            this.version));

      final GASTConditional gc =
        new GASTStatement.GASTConditional(ec, sl, sr);

      final List<GASTStatement> statements = new ArrayList<GASTStatement>();
      statements.add(gc);
      return new GASTScope(statements);
    }

    @Override public void expressionVisitConditionalConditionPost(
      final TASTEConditional e)
      throws GFFIError
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalConditionPre(
      final TASTEConditional e)
      throws GFFIError
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalLeftPost(
      final TASTEConditional e)
      throws GFFIError
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalLeftPre(
      final TASTEConditional e)
      throws GFFIError
    {
      // Nothing
    }

    @Override public boolean expressionVisitConditionalPre(
      final TASTEConditional e)
      throws GFFIError
    {
      return false;
    }

    @Override public void expressionVisitConditionalRightPost(
      final TASTEConditional e)
      throws GFFIError
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalRightPre(
      final TASTEConditional e)
      throws GFFIError
    {
      // Nothing
    }

    @Override public GASTScope expressionVisitInteger(
      final TASTEInteger e)
      throws GFFIError
    {
      return this.wrapReturn(e);
    }

    @Override public GASTScope expressionVisitLet(
      final List<TASTDValueLocal> k_bindings,
      final GASTScope body,
      final TASTELet e)
      throws GFFIError
    {
      final List<GASTStatement> statements = new ArrayList<GASTStatement>();

      final Map<String, Binding> bindings_new =
        new HashMap<String, Binding>(this.bindings);

      final List<TASTDValueLocal> locals = e.getBindings();

      GTransform.processLocals(
        this.context,
        this.declarations,
        statements,
        bindings_new,
        locals,
        this.version);

      /**
       * Wrap the body in a scope statement, taking into account the bindings
       * added by the previous locals.
       */

      final GASTScope sbody =
        e.getBody().expressionVisitableAccept(
          new ExpressionStatementTransformer(
            this.context,
            bindings_new,
            this.declarations,
            this.version));

      statements.add(sbody);
      return new GASTScope(statements);
    }

    @Override public @Nullable
      TASTLocalLevelVisitorType<TASTDValueLocal, GFFIError>
      expressionVisitLetPre(
        final TASTELet e)
        throws GFFIError
    {
      return null;
    }

    @Override public GASTScope expressionVisitNew(
      final List<GASTScope> arguments,
      final TASTENew e)
      throws GFFIError
    {
      return this.wrapReturn(e);
    }

    @Override public boolean expressionVisitNewPre(
      final TASTENew e)
      throws GFFIError
    {
      return false;
    }

    @Override public GASTScope expressionVisitReal(
      final TASTEReal e)
      throws GFFIError
    {
      return this.wrapReturn(e);
    }

    @Override public GASTScope expressionVisitRecord(
      final TASTERecord e)
      throws GFFIError
    {
      final List<GASTStatement> statements = new ArrayList<GASTStatement>();
      final Map<String, GTermNameLocal> field_temporaries =
        new HashMap<String, GTermNameLocal>();

      /**
       * Generate assignments to local variables for each assignment to a
       * record field, in the order that they appeared in the source.
       */

      for (final TASTRecordFieldAssignment a : e.getAssignments()) {
        final TValueType type = (TValueType) a.getExpression().getType();
        final Binding binding = this.context.getFreshBindingTemporary(type);
        final GTermNameLocal t = new GTermNameLocal(binding.getCurrent());

        final GASTExpression ae =
          a.getExpression().expressionVisitableAccept(
            new ExpressionTransformer(
              this.context,
              this.declarations,
              this.bindings,
              this.version));

        assert field_temporaries.containsKey(a.getName().getActual()) == false;
        field_temporaries.put(a.getName().getActual(), t);

        final GASTStatement.GASTLocalVariable local_assign =
          new GASTStatement.GASTLocalVariable(t, this.context.getTypeName(a
            .getExpression()
            .getType()), ae);
        statements.add(local_assign);
      }

      /**
       * Create a 'new' expression that passes the above local variables in
       * the order required to construct a new value of a record type.
       */

      final List<GASTExpression> ordered_args =
        new ArrayList<GASTExpression>();
      final TRecord rt = (TRecord) e.getType();
      for (final TRecordField f : rt.getFields()) {
        assert field_temporaries.containsKey(f.getName());
        final GTermNameLocal v = field_temporaries.get(f.getName());
        assert v != null;
        ordered_args.add(new GASTEVariable(this.context.getTypeName(f
          .getType()), v));
      }

      final GASTEConstruction cons =
        new GASTEConstruction(this.context.getTypeName(rt), ordered_args);
      statements.add(new GASTStatement.GASTReturn(cons));
      return new GASTScope(statements);
    }

    @Override public GASTScope expressionVisitRecordProjection(
      final GASTScope body,
      final TASTERecordProjection e)
      throws GFFIError
    {
      return this.wrapReturn(e);
    }

    @Override public boolean expressionVisitRecordProjectionPre(
      final @Nullable TASTERecordProjection e)
      throws GFFIError
    {
      return false;
    }

    @Override public GASTScope expressionVisitSwizzle(
      final GASTScope body,
      final TASTESwizzle e)
      throws GFFIError
    {
      return this.wrapReturn(e);
    }

    @Override public boolean expressionVisitSwizzlePre(
      final TASTESwizzle e)
      throws GFFIError
    {
      return false;
    }

    @Override public GASTScope expressionVisitVariable(
      final TASTEVariable e)
      throws GFFIError
    {
      return this.wrapReturn(e);
    }

    private GASTScope wrapReturn(
      final TASTExpression e)
      throws GFFIError
    {
      final GASTExpression x =
        e.expressionVisitableAccept(new ExpressionTransformer(
          this.context,
          this.declarations,
          this.bindings,
          this.version));
      final List<GASTStatement> r = new ArrayList<GASTStatement>();
      r.add(new GASTReturn(x));
      return new GASTScope(r);
    }
  }

  /**
   * A transformer that takes a parasol expression and produces a GLSL
   * expression, lifting any expressions that cannot be represented as a GLSL
   * expressions into temporary functions.
   */

  @EqualityReference private static final class ExpressionTransformer implements
    TASTExpressionVisitorType<GASTExpression, TASTDValueLocal, GFFIError>
  {
    private final Map<String, Binding>                             bindings;
    private final Context                                          context;
    private final List<Pair<GTermNameGlobal, GASTTermDeclaration>> declarations;
    private final GVersionType                                     version;

    public ExpressionTransformer(
      final Context in_context,
      final List<Pair<GTermNameGlobal, GASTTermDeclaration>> in_declarations,
      final Map<String, Binding> in_bindings,
      final GVersionType in_version)
    {
      this.context = in_context;
      this.declarations = in_declarations;
      this.bindings = in_bindings;
      this.version = in_version;
    }

    @Override public GASTExpression expressionVisitApplication(
      final List<GASTExpression> arguments,
      final TASTEApplication e)
      throws GFFIError
    {
      return e.getName().termNameVisitableAccept(
        new TASTTermNameVisitorType<GASTExpression, GFFIError>() {
          @Override public GASTExpression termNameVisitExternal(
            final TASTTermNameExternal t)
            throws GFFIError
          {
            throw new UnreachableCodeException();
          }

          @Override public GASTExpression termNameVisitGlobal(
            final TASTTermNameGlobal t)
            throws GFFIError
          {
            final TASTTermNameFlat term_name =
              TASTTermNameFlat.fromTermNameGlobal(t);

            final TASTDTerm term =
              ExpressionTransformer.this.context.getCompilation().lookupTerm(
                term_name);
            assert term != null;

            final GTermNameGlobal name =
              ExpressionTransformer.this.context.getGlobalTermName(term_name);

            return term
              .termVisitableAccept(new TASTTermVisitorType<GASTExpression, GFFIError>() {

                /**
                 * The application of a defined function.
                 */

                @Override public GASTExpression termVisitFunctionDefined(
                  final TASTDFunctionDefined f)
                  throws GFFIError
                {
                  return new GASTExpression.GASTEApplication(name, e
                    .getType(), arguments);
                }

                /**
                 * The application of an external function: Either a direct
                 * application of a defined function, or a replacement
                 * expression produced by the FFI.
                 */

                @Override public GASTExpression termVisitFunctionExternal(
                  final TASTDFunctionExternal f)
                  throws GFFIError
                {
                  final GFFI ffi =
                    ExpressionTransformer.this.context.getFFI();
                  final GFFIExpression g =
                    ffi.getExpression(
                      f,
                      arguments,
                      ExpressionTransformer.this.version);

                  return g
                    .ffiExpressionAccept(new GFFIExpressionVisitorType<GASTExpression, UnreachableCodeException>() {
                      @Override public
                        GASTExpression
                        ffiExpressionVisitBuiltIn(
                          final GFFIExpressionBuiltIn ge)

                      {
                        return ge.getExpression();
                      }

                      @Override public
                        GASTExpression
                        ffiExpressionVisitDefined(
                          final GFFIExpressionDefined ge)

                      {
                        final TValueType returns =
                          f.getType().getReturnType();
                        return new GASTEApplication(name, returns, arguments);
                      }
                    });
                }

                /**
                 * The application of a value is forbidden by the type
                 * checker.
                 */

                @Override public GASTExpression termVisitValueDefined(
                  final TASTDValueDefined v)
                  throws GFFIError
                {
                  throw new UnreachableCodeException();
                }

                /**
                 * The application of a value is forbidden by the type
                 * checker.
                 */

                @Override public GASTExpression termVisitValueExternal(
                  final TASTDValueExternal v)
                  throws GFFIError,
                    UnreachableCodeException
                {
                  throw new UnreachableCodeException();
                }
              });
          }

          @Override public GASTExpression termNameVisitLocal(
            final TASTTermNameLocal t)
            throws GFFIError
          {
            /**
             * There's no way to define a local function, so there's no way a
             * function application can occur on a local name.
             */
            throw new UnreachableCodeException();
          }
        });
    }

    @Override public boolean expressionVisitApplicationPre(
      final TASTEApplication e)
      throws GFFIError
    {
      return true;
    }

    @Override public GASTExpression expressionVisitBoolean(
      final TASTEBoolean e)
      throws GFFIError
    {
      return new GASTEBoolean(e.getValue());
    }

    @Override public GASTExpression expressionVisitConditional(
      final GASTExpression condition,
      final GASTExpression left,
      final GASTExpression right,
      final TASTEConditional e)
      throws GFFIError
    {
      return this.wrapFunction(e);
    }

    @Override public void expressionVisitConditionalConditionPost(
      final TASTEConditional e)
      throws GFFIError
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalConditionPre(
      final TASTEConditional e)
      throws GFFIError
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalLeftPost(
      final TASTEConditional e)
      throws GFFIError
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalLeftPre(
      final TASTEConditional e)
      throws GFFIError
    {
      // Nothing
    }

    @Override public boolean expressionVisitConditionalPre(
      final TASTEConditional e)
      throws GFFIError
    {
      return false;
    }

    @Override public void expressionVisitConditionalRightPost(
      final TASTEConditional e)
      throws GFFIError
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalRightPre(
      final TASTEConditional e)
      throws GFFIError
    {
      // Nothing
    }

    @Override public GASTExpression expressionVisitInteger(
      final TASTEInteger e)
      throws GFFIError
    {
      return new GASTExpression.GASTEInteger(e.getValue());
    }

    @Override public GASTExpression expressionVisitLet(
      final List<TASTDValueLocal> let_bindings,
      final GASTExpression body,
      final TASTELet e)
      throws GFFIError
    {
      return this.wrapFunction(e);
    }

    @Override public @Nullable
      TASTLocalLevelVisitorType<TASTDValueLocal, GFFIError>
      expressionVisitLetPre(
        final TASTELet e)
        throws GFFIError
    {
      return null;
    }

    @Override public GASTExpression expressionVisitNew(
      final List<GASTExpression> arguments,
      final TASTENew e)
      throws GFFIError
    {
      final GTypeName type = this.context.getTypeName(e.getType());
      return new GASTEConstruction(type, arguments);
    }

    @Override public boolean expressionVisitNewPre(
      final TASTENew e)
      throws GFFIError
    {
      return true;
    }

    @Override public GASTExpression expressionVisitReal(
      final TASTEReal e)
      throws GFFIError
    {
      return new GASTExpression.GASTEFloat(e.getValue());
    }

    @Override public GASTExpression expressionVisitRecord(
      final TASTERecord e)
      throws GFFIError
    {
      return this.wrapFunction(e);
    }

    @Override public GASTExpression expressionVisitRecordProjection(
      final GASTExpression body,
      final TASTERecordProjection e)
      throws GFFIError
    {
      final String a = e.getField().getActual();
      assert a != null;
      final GFieldName field = new GFieldName(a);
      final TType type = e.getType();
      return new GASTEProjection(body, field, type);
    }

    @Override public boolean expressionVisitRecordProjectionPre(
      final @Nullable TASTERecordProjection e)
      throws GFFIError
    {
      return true;
    }

    @Override public GASTExpression expressionVisitSwizzle(
      final GASTExpression body,
      final TASTESwizzle e)
      throws GFFIError
    {
      final TType type = e.getType();
      final List<GFieldName> fields = new ArrayList<GFieldName>();
      for (final TokenIdentifierLower f : e.getFields()) {
        final String a = f.getActual();
        assert a != null;
        fields.add(new GFieldName(a));
      }
      return new GASTESwizzle(body, fields, type);
    }

    @Override public boolean expressionVisitSwizzlePre(
      final TASTESwizzle e)
      throws GFFIError
    {
      return true;
    }

    @Override public GASTExpression expressionVisitVariable(
      final TASTEVariable e)
      throws GFFIError
    {
      final GTypeName type = this.context.getTypeName(e.getType());
      final GTermName term =
        e.getName().termNameVisitableAccept(
          new TASTTermNameVisitorType<GTermName, UnreachableCodeException>() {
            @Override public GTermName termNameVisitExternal(
              final TASTTermNameExternal t)

            {
              return new GTermNameGlobal(t.getCurrent());
            }

            @Override public GTermName termNameVisitGlobal(
              final TASTTermNameGlobal t)

            {
              final TASTTermNameFlat flat =
                TASTTermNameFlat.fromTermNameGlobal(t);
              return ExpressionTransformer.this.context
                .getGlobalTermName(flat);
            }

            @Override public GTermName termNameVisitLocal(
              final TASTTermNameLocal t)

            {
              final String name = t.getCurrent();
              assert ExpressionTransformer.this.bindings.containsKey(name);
              final Binding binding =
                ExpressionTransformer.this.bindings.get(name);
              assert binding != null;
              return ExpressionTransformer.this.context
                .getLocalFromBinding(binding);
            }
          });
      return new GASTEVariable(type, term);
    }

    private GASTExpression wrapFunction(
      final TASTExpression e)
      throws GFFIError
    {
      /**
       * Produce the body for the target function.
       */

      final GASTScope statement =
        e.expressionVisitableAccept(new ExpressionStatementTransformer(
          this.context,
          this.bindings,
          this.declarations,
          this.version));

      /**
       * Determine which of the current local variables actually occur in the
       * given expression, in order to pass them to the wrapper function.
       */

      final Set<String> names = this.bindings.keySet();
      assert names != null;
      final Set<String> occurs = Occurences.occursIn(e, names);

      /**
       * Construct the new function and add it to the environment.
       */

      final GTermNameGlobal f_name = this.context.getFreshTemporaryGlobal();
      final GTypeName f_returns = this.context.getTypeName(e.getType());

      final List<Pair<GTermNameLocal, GTypeName>> f_parameters =
        new ArrayList<Pair<GTermNameLocal, GTypeName>>();

      for (final String b_name : names) {
        final Binding binding = this.bindings.get(b_name);
        assert binding != null;

        if (occurs.contains(b_name)) {
          final GTermNameLocal p_name =
            this.context.getLocalFromBinding(binding);
          final GTypeName p_type =
            this.context.getTypeName(binding.getType());
          final Pair<GTermNameLocal, GTypeName> p = Pair.pair(p_name, p_type);
          f_parameters.add(p);
        }
      }

      final GASTTermFunction function =
        new GASTTermFunction(f_name, f_returns, f_parameters, statement);
      final Pair<GTermNameGlobal, GASTTermDeclaration> p =
        Pair.pair(function.getName(), (GASTTermDeclaration) function);
      this.declarations.add(p);

      /**
       * Construct an application of the function.
       */

      final List<GASTExpression> application_args =
        new ArrayList<GASTExpression>();
      for (final Pair<GTermNameLocal, GTypeName> b : f_parameters) {
        application_args.add(new GASTExpression.GASTEVariable(b.getRight(), b
          .getLeft()));
      }

      final GASTEApplication application =
        new GASTExpression.GASTEApplication(
          f_name,
          e.getType(),
          application_args);

      return application;
    }
  }

  @EqualityReference private static final class TermTransformer implements
    TASTTermVisitorType<OptionType<GASTTermDeclaration>, GFFIError>
  {
    private final Context                                          context;
    private final List<Pair<GTermNameGlobal, GASTTermDeclaration>> declarations;
    private final TASTTermNameFlat                                 name;
    private final GVersionType                                     version;

    public TermTransformer(
      final Context in_context,
      final List<Pair<GTermNameGlobal, GASTTermDeclaration>> in_declarations,
      final TASTTermNameFlat in_name,
      final GVersionType in_version)
    {
      this.context = in_context;
      this.declarations = in_declarations;
      this.name = in_name;
      this.version = in_version;
    }

    @Override public
      OptionType<GASTTermDeclaration>
      termVisitFunctionDefined(
        final TASTDFunctionDefined f)
        throws GFFIError
    {
      final Map<String, Binding> bindings = new HashMap<String, Binding>();

      for (final TASTDFunctionArgument b : f.getArguments()) {
        final Binding binding =
          this.context.getFreshBindingFromLocal(
            b.getName(),
            b.getType(),
            false);
        assert bindings.containsKey(binding.getOriginal()) == false;
        bindings.put(binding.getOriginal(), binding);
      }

      final GASTScope statement =
        f.getBody().expressionVisitableAccept(
          new ExpressionStatementTransformer(
            this.context,
            bindings,
            this.declarations,
            this.version));

      final GTypeName returns =
        this.context.getTypeName(f.getType().getReturnType());

      final List<Pair<GTermNameLocal, GTypeName>> parameters =
        new ArrayList<Pair<GTermNameLocal, GTypeName>>();

      for (final TASTDFunctionArgument b : f.getArguments()) {
        final String b_name = b.getName().getCurrent();
        assert bindings.containsKey(b_name);
        final Binding binding = bindings.get(b_name);
        assert binding != null;
        final GTermNameLocal term_name =
          this.context.getLocalFromBinding(binding);
        final GTypeName type_name = this.context.getTypeName(b.getType());
        final Pair<GTermNameLocal, GTypeName> p =
          Pair.pair(term_name, type_name);
        parameters.add(p);
      }

      final GTermNameGlobal function_name =
        this.context.getGlobalTermName(this.name);

      final GASTTermDeclaration result =
        new GASTTermFunction(function_name, returns, parameters, statement);
      return Option.some(result);
    }

    @Override public
      OptionType<GASTTermDeclaration>
      termVisitFunctionExternal(
        final TASTDFunctionExternal f)
        throws GFFIError
    {
      final TASTDFunctionDefined td =
        this.context.getFFI().getFunctionDefinition(f, this.version);

      if (td != null) {
        return this.termVisitFunctionDefined(td);
      }
      return Option.none();
    }

    @Override public OptionType<GASTTermDeclaration> termVisitValueDefined(
      final TASTDValueDefined v)
      throws GFFIError
    {
      final GASTExpression r =
        v.getExpression().expressionVisitableAccept(
          new ExpressionTransformer(
            this.context,
            this.declarations,
            new HashMap<String, Binding>(),
            this.version));

      final GTermNameGlobal term_name =
        this.context.getGlobalTermName(this.name);
      final GTypeName type_name = this.context.getTypeName(v.getType());
      final GASTTermDeclaration result =
        new GASTTermValue(term_name, type_name, r);
      return Option.some(result);
    }

    @Override public OptionType<GASTTermDeclaration> termVisitValueExternal(
      final TASTDValueExternal v)
      throws GFFIError,
        UnreachableCodeException
    {
      final TASTDValueDefined td =
        this.context.getFFI().getValueDefinition(v, this.version);

      if (td != null) {
        return this.termVisitValueDefined(td);
      }
      return Option.none();
    }
  }

  private static TASTDModule checkConstraints(
    final TASTCompilation compilation,
    final Topology topology,
    final TASTShaderNameFlat shader_name,
    final GVersionType version,
    final LogUsableType log)
  {
    NullCheck.notNull(compilation, "Compilation");
    NullCheck.notNull(shader_name, "Shader name");
    NullCheck.notNull(version, "Version");
    NullCheck.notNull(log, "LogUsableType");

    if (topology.getShaderName().equals(shader_name) == false) {
      throw new IllegalArgumentException("References not produced for shader");
    }

    final Map<ModulePathFlat, TASTDModule> modules = compilation.getModules();
    if (modules.containsKey(shader_name.getModulePath()) == false) {
      throw new IllegalArgumentException("Module does not exist");
    }

    final TASTDModule m = modules.get(shader_name.getModulePath());
    if (m.getShaders().containsKey(shader_name.getName()) == false) {
      throw new IllegalArgumentException("Shader does not exist");
    }

    return m;
  }

  private static TASTDShaderFragmentOutput findFragmentOutput(
    final TASTDShaderFragment shader,
    final String name)
  {
    for (final TASTDShaderFragmentOutput o : shader.getOutputs()) {
      if (o.getName().getActual().equals(name)) {
        return o;
      }
    }
    throw new UnreachableCodeException();
  }

  private static TASTDShaderVertexOutput findVertexOutput(
    final TASTDShaderVertex shader,
    final String name)
  {
    for (final TASTDShaderVertexOutput o : shader.getOutputs()) {
      if (o.getName().getActual().equals(name)) {
        return o;
      }
    }
    throw new UnreachableCodeException();
  }

  private static List<GASTShaderFragmentInput> makeFragmentInputs(
    final Context context,
    final List<TASTDShaderFragmentInput> inputs)

  {
    final List<GASTShaderFragmentInput> results =
      new ArrayList<GASTShaderFragmentInput>();

    for (final TASTDShaderFragmentInput i : inputs) {
      final GShaderInputName name =
        new GShaderInputName(i.getName().getCurrent());
      final TValueType type = i.getType();
      final GTypeName type_name = context.getTypeName(type);
      results.add(new GASTShaderFragmentInput(name, type_name, type));
    }

    return results;
  }

  private static GASTShaderMainFragment makeFragmentMain(
    final Context context,
    final TASTDShaderFragment fragment,
    final List<Pair<GTermNameGlobal, GASTTermDeclaration>> terms,
    final GVersionType version)
    throws GFFIError
  {
    final List<GASTFragmentShaderStatement> statements =
      new ArrayList<GASTFragmentShaderStatement>();
    final Map<String, Binding> bindings = new HashMap<String, Binding>();

    for (final TASTDShaderFragmentInput i : fragment.getInputs()) {
      final Binding binding =
        context.getFreshBindingFromLocal(i.getName(), i.getType(), true);
      assert bindings.containsKey(binding.getOriginal()) == false;
      bindings.put(binding.getOriginal(), binding);
    }

    for (final TASTDShaderFragmentParameter p : fragment.getParameters()) {
      final Binding binding =
        context.getFreshBindingFromLocal(p.getName(), p.getType(), true);
      assert bindings.containsKey(binding.getOriginal()) == false;
      bindings.put(binding.getOriginal(), binding);
    }

    /**
     * Translate locals and conditional discards to statements.
     */

    for (final TASTDShaderFragmentLocal l : fragment.getLocals()) {
      l
        .fragmentShaderLocalVisitableAccept(new TASTFragmentShaderLocalVisitorType<Unit, GFFIError>() {
          @Override public Unit fragmentShaderVisitLocalDiscard(
            final TASTDShaderFragmentLocalDiscard d)
            throws GFFIError
          {
            final GASTExpression expression =
              d.getExpression().expressionVisitableAccept(
                new ExpressionTransformer(context, terms, bindings, version));
            statements.add(new GASTFragmentConditionalDiscard(expression));
            return Unit.unit();
          }

          @Override public Unit fragmentShaderVisitLocalValue(
            final TASTDShaderFragmentLocalValue v)
            throws GFFIError
          {
            GTransform.processFragmentLocal(
              context,
              terms,
              statements,
              bindings,
              v.getValue(),
              version);
            return Unit.unit();
          }
        });
    }

    /**
     * Translate writes to named and numbered outputs.
     */

    final List<GASTFragmentOutputDataAssignment> writes =
      new ArrayList<GASTFragmentOutputDataAssignment>();
    final AtomicReference<GASTFragmentOutputDepthAssignment> depth_write =
      new AtomicReference<GASTFragmentOutputDepthAssignment>();

    for (final TASTDShaderFragmentOutputAssignment f : fragment.getWrites()) {
      final TokenIdentifierLower f_name = f.getName();
      final String a = f_name.getActual();
      assert a != null;
      final TASTDShaderFragmentOutput output =
        GTransform.findFragmentOutput(fragment, a);

      output
        .fragmentShaderOutputVisitableAccept(new TASTFragmentShaderOutputVisitorType<Unit, GFFIError>() {
          @Override public Unit fragmentShaderVisitOutputData(
            final TASTDShaderFragmentOutputData od)
            throws GFFIError,
              UnreachableCodeException
          {
            final GShaderOutputName name = new GShaderOutputName(a);
            final GTermName value =
              context.lookupTermName(bindings, f.getVariable().getName());
            writes.add(new GASTFragmentOutputDataAssignment(name, od
              .getIndex(), value));
            return Unit.unit();
          }

          @Override public Unit fragmentShaderVisitOutputDepth(
            final TASTDShaderFragmentOutputDepth v)
            throws GFFIError,
              UnreachableCodeException
          {
            assert depth_write.get() == null;

            final GShaderOutputName name = new GShaderOutputName(a);
            final GTermName value =
              context.lookupTermName(bindings, f.getVariable().getName());
            depth_write
              .set(new GASTFragmentOutputDepthAssignment(name, value));
            return Unit.unit();
          }
        });
    }

    final OptionType<GASTFragmentOutputDepthAssignment> depth_write_opt =
      Option.of(depth_write.get());

    return new GASTShaderMainFragment(statements, writes, depth_write_opt);
  }

  private static List<GASTShaderFragmentOutput> makeFragmentOutputs(
    final Context context,
    final List<TASTDShaderFragmentOutput> outputs)

  {
    final List<GASTShaderFragmentOutput> results =
      new ArrayList<GASTShaderFragmentOutput>();

    for (final TASTDShaderFragmentOutput o : outputs) {
      o
        .fragmentShaderOutputVisitableAccept(new TASTFragmentShaderOutputVisitorType<Unit, UnreachableCodeException>() {
          @Override public Unit fragmentShaderVisitOutputData(
            final TASTDShaderFragmentOutputData od)
          {
            final String a = od.getName().getActual();
            assert a != null;

            final GShaderOutputName name = new GShaderOutputName(a);
            final GTypeName type = context.getTypeName(od.getType());
            results.add(new GASTShaderFragmentOutput(
              name,
              od.getIndex(),
              type));
            return Unit.unit();
          }

          @Override public Unit fragmentShaderVisitOutputDepth(
            final TASTDShaderFragmentOutputDepth v)

          {
            return Unit.unit();
          }
        });
    }

    return results;
  }

  private static List<GASTShaderFragmentParameter> makeFragmentParameters(
    final Context context,
    final List<TASTDShaderFragmentParameter> parameters)

  {
    final List<GASTShaderFragmentParameter> results =
      new ArrayList<GASTShaderFragmentParameter>();

    for (final TASTDShaderFragmentParameter p : parameters) {
      final GShaderParameterName name =
        new GShaderParameterName(p.getName().getCurrent());
      final GTypeName type = context.getTypeName(p.getType());
      final List<Pair<String, TType>> expanded =
        GUniform.expandUniformFragment(p);
      results.add(new GASTShaderFragmentParameter(name, type, expanded));
    }

    return results;
  }

  private static List<Pair<GTermNameGlobal, GASTTermDeclaration>> makeTerms(
    final Context context,
    final GVersionType version)
    throws GFFIError
  {
    final TASTCompilation compilation = context.getCompilation();
    final Topology topology = context.getTopology();
    final LogUsableType log = context.log;

    final List<Pair<GTermNameGlobal, GASTTermDeclaration>> declarations =
      new GLoggingArrayList<Pair<GTermNameGlobal, GASTTermDeclaration>>(
        new FunctionType<Pair<GTermNameGlobal, GASTTermDeclaration>, String>() {
          @Override public String call(
            final Pair<GTermNameGlobal, GASTTermDeclaration> x)
          {
            return x.getLeft().show();
          }
        },
        NullCheck.notNull(log, "Log").with("terms"));

    for (final TASTTermNameFlat name : topology.getTerms()) {
      assert name != null;

      final TASTDTerm term = compilation.lookupTerm(name);
      assert term != null;

      if (log.wouldLog(LogLevel.LOG_DEBUG)) {
        final String s = String.format("Transforming term %s", name.show());
        assert s != null;
        log.debug(s);
      }

      final OptionType<GASTTermDeclaration> result =
        term.termVisitableAccept(new TermTransformer(
          context,
          declarations,
          name,
          version));

      result.map(new FunctionType<GASTTermDeclaration, Unit>() {
        @Override public Unit call(
          final GASTTermDeclaration rx)
        {
          final Pair<GTermNameGlobal, GASTTermDeclaration> p =
            Pair.pair(rx.getName(), rx);
          declarations.add(p);
          return Unit.unit();
        }
      });
    }

    return declarations;
  }

  private static List<Pair<GTypeName, GASTTypeDeclaration>> makeTypes(
    final Context context)

  {
    final TASTCompilation compilation = context.getCompilation();
    final Topology topology = context.getTopology();
    final LogUsableType log = context.log;

    final FunctionType<Pair<GTypeName, GASTTypeDeclaration>, String> f =
      new FunctionType<Pair<GTypeName, GASTTypeDeclaration>, String>() {
        @Override public String call(
          final Pair<GTypeName, GASTTypeDeclaration> p)
        {
          return p.getLeft().show();
        }
      };

    final List<Pair<GTypeName, GASTTypeDeclaration>> declarations =
      new GLoggingArrayList<Pair<GTypeName, GASTTypeDeclaration>>(
        f,
        NullCheck.notNull(log, "Log").with("types"));

    for (final TTypeNameFlat name : topology.getTypes()) {
      assert name != null;
      final TASTDType type = compilation.lookupType(name);
      assert type != null;

      if (log.wouldLog(LogLevel.LOG_DEBUG)) {
        final String s = String.format("Transforming type %s", name.show());
        assert s != null;
        log.debug(s);
      }

      final GASTTypeDeclaration declaration =
        type
          .typeVisitableAccept(new TASTTypeVisitorType<GASTTypeDeclaration, UnreachableCodeException>() {
            @Override public GASTTypeDeclaration typeVisitTypeRecord(
              final TASTDTypeRecord r)
            {
              final GTypeName new_name = context.getTypeName(r.getType());

              final List<Pair<GFieldName, GTypeName>> fields =
                new ArrayList<Pair<GFieldName, GTypeName>>();

              for (final TASTDTypeRecordField field : r.getFields()) {
                final String a = field.getName().getActual();
                assert a != null;

                final GFieldName field_name = new GFieldName(a);
                final GTypeName field_type =
                  context.getTypeName(field.getType());
                fields.add(Pair.pair(field_name, field_type));
              }

              return new GASTTypeDeclaration.GASTTypeRecord(new_name, fields);
            }
          });

      declarations.add(Pair.pair(declaration.getName(), declaration));
    }

    return declarations;
  }

  private static List<GASTShaderVertexInput> makeVertexInputs(
    final Context context,
    final List<TASTDShaderVertexInput> inputs)
  {
    final List<GASTShaderVertexInput> results =
      new ArrayList<GASTShaderVertexInput>();

    for (final TASTDShaderVertexInput i : inputs) {
      final GShaderInputName name =
        new GShaderInputName(i.getName().getCurrent());
      final GTypeName type = context.getTypeName(i.getType());
      results.add(new GASTShaderVertexInput(name, type));
    }

    return results;
  }

  private static GASTShaderMainVertex makeVertexMain(
    final Context context,
    final TASTDShaderVertex vertex,
    final List<Pair<GTermNameGlobal, GASTTermDeclaration>> terms,
    final GVersionType version)
    throws GFFIError
  {
    final List<GASTStatement> statements = new ArrayList<GASTStatement>();
    final Map<String, Binding> bindings = new HashMap<String, Binding>();
    final List<TASTDValueLocal> locals =
      new ArrayList<TASTDeclaration.TASTDValueLocal>();

    for (final TASTDShaderVertexInput i : vertex.getInputs()) {
      final Binding binding =
        context.getFreshBindingFromLocal(i.getName(), i.getType(), true);
      assert bindings.containsKey(binding.getOriginal()) == false;
      bindings.put(binding.getOriginal(), binding);
    }

    for (final TASTDShaderVertexParameter p : vertex.getParameters()) {
      final Binding binding =
        context.getFreshBindingFromLocal(p.getName(), p.getType(), true);
      assert bindings.containsKey(binding.getOriginal()) == false;
      bindings.put(binding.getOriginal(), binding);
    }

    for (final TASTDShaderVertexLocalValue v : vertex.getValues()) {
      locals.add(v.getValue());
    }

    GTransform.processLocals(
      context,
      terms,
      statements,
      bindings,
      locals,
      version);

    final List<GASTVertexOutputAssignment> writes =
      new ArrayList<GASTVertexOutputAssignment>();

    for (final TASTDShaderVertexOutputAssignment w : vertex.getWrites()) {

      /**
       * Look up the name of the variable used.
       */

      final GTermName value =
        context.lookupTermName(bindings, w.getVariable().getName());

      /**
       * If the shader output in question is a "main" output, then it assigns
       * to "gl_Position", not the named output.
       */

      final TokenIdentifierLower written_name = w.getName();
      final String written_actual = written_name.getActual();
      assert written_actual != null;

      final TASTDShaderVertexOutput output =
        GTransform.findVertexOutput(vertex, written_actual);

      if (output.isMain()) {
        writes.add(new GASTVertexOutputAssignment(new GShaderOutputName(
          "gl_Position"), value));
      }

      writes.add(new GASTVertexOutputAssignment(new GShaderOutputName(
        written_actual), value));
    }

    return new GASTShaderMainVertex(statements, writes);
  }

  private static List<GASTShaderVertexOutput> makeVertexOutputs(
    final Context context,
    final List<TASTDShaderVertexOutput> outputs)
  {
    final List<GASTShaderVertexOutput> results =
      new ArrayList<GASTShaderVertexOutput>();

    for (final TASTDShaderVertexOutput o : outputs) {
      final String a = o.getName().getActual();
      assert a != null;

      final GShaderOutputName name = new GShaderOutputName(a);
      final TValueType type = o.getType();
      final GTypeName type_name = context.getTypeName(type);
      results.add(new GASTShaderVertexOutput(name, type_name, type));
    }

    return results;
  }

  private static List<GASTShaderVertexParameter> makeVertexParameters(
    final Context context,
    final List<TASTDShaderVertexParameter> parameters)
  {
    final List<GASTShaderVertexParameter> results =
      new ArrayList<GASTShaderVertexParameter>();

    for (final TASTDShaderVertexParameter p : parameters) {
      final GShaderParameterName name =
        new GShaderParameterName(p.getName().getCurrent());
      final GTypeName type = context.getTypeName(p.getType());
      final List<Pair<String, TType>> expanded =
        GUniform.expandUniformVertex(p);
      results.add(new GASTShaderVertexParameter(name, type, expanded));
    }

    return results;
  }

  private static void processFragmentLocal(
    final Context context,
    final List<Pair<GTermNameGlobal, GASTTermDeclaration>> declarations,
    final List<GASTFragmentShaderStatement> statements,
    final Map<String, Binding> bindings,
    final TASTDValueLocal b,
    final GVersionType version)
    throws GFFIError
  {
    final GASTExpression ex =
      b.getExpression().expressionVisitableAccept(
        new ExpressionTransformer(context, declarations, bindings, version));

    final TValueType b_type = (TValueType) b.getExpression().getType();
    final Binding binding =
      context.getFreshBindingFromLocal(b.getName(), b_type, false);
    assert bindings.containsKey(binding.getOriginal()) == false;
    bindings.put(binding.getOriginal(), binding);

    final GTermNameLocal name = context.getLocalFromBinding(binding);
    final TValueType type = b_type;
    final GASTFragmentLocalVariable l =
      new GASTFragmentLocalVariable(name, context.getTypeName(type), ex);

    statements.add(l);
  }

  private static void processLocal(
    final Context context,
    final List<Pair<GTermNameGlobal, GASTTermDeclaration>> declarations,
    final List<GASTStatement> statements,
    final Map<String, Binding> bindings,
    final TASTDValueLocal b,
    final GVersionType version)
    throws GFFIError
  {
    final GASTExpression ex =
      b.getExpression().expressionVisitableAccept(
        new ExpressionTransformer(context, declarations, bindings, version));

    final TValueType b_type = (TValueType) b.getExpression().getType();
    final Binding binding =
      context.getFreshBindingFromLocal(b.getName(), b_type, false);
    assert bindings.containsKey(binding.getOriginal()) == false;
    bindings.put(binding.getOriginal(), binding);

    final TValueType type = b_type;
    final GASTLocalVariable l =
      new GASTStatement.GASTLocalVariable(
        context.getLocalFromBinding(binding),
        context.getTypeName(type),
        ex);

    statements.add(l);
  }

  /**
   * Create new local variables for each local value and add each successive
   * variable to <code>bindings</code>. Any temporary functions generated will
   * be added to <code>declarations</code> and local variable statements will
   * be added to <code>statements</code>.
   */

  private static void processLocals(
    final Context context,
    final List<Pair<GTermNameGlobal, GASTTermDeclaration>> declarations,
    final List<GASTStatement> statements,
    final Map<String, Binding> bindings,
    final List<TASTDValueLocal> locals,
    final GVersionType version)
    throws GFFIError
  {
    for (final TASTDValueLocal b : locals) {
      assert b != null;

      GTransform.processLocal(
        context,
        declarations,
        statements,
        bindings,
        b,
        version);
    }
  }

  /**
   * Transform a Parasol fragment shader to a GLSL fragment shader.
   * 
   * @param compilation
   *          The AST
   * @param topology
   *          The topologically sorted types and terms
   * @param shader_name
   *          The shader name
   * @param version
   *          The GLSL version
   * @param log
   *          A log interface
   * @return A transformed GLSL shader
   * @throws GFFIError
   *           If an FFI error occurs
   */

  public static GASTShaderFragment transformFragment(
    final TASTCompilation compilation,
    final Topology topology,
    final TASTShaderNameFlat shader_name,
    final GVersionType version,
    final LogUsableType log)
    throws GFFIError
  {
    final TASTDModule m =
      GTransform.checkConstraints(
        compilation,
        topology,
        shader_name,
        version,
        log);

    final TASTDShader shader = m.getShaders().get(shader_name.getName());
    if ((shader instanceof TASTDShaderFragment) == false) {
      throw new IllegalArgumentException("Expected a fragment shader");
    }

    final TASTDShaderFragment fragment = (TASTDShaderFragment) shader;

    final LogUsableType logx =
      NullCheck.notNull(log, "Log").with("fragment-transformer");
    if (logx.wouldLog(LogLevel.LOG_DEBUG)) {
      final String s =
        String.format(
          "Transforming fragment shader %s for %s",
          shader_name.show(),
          version.versionGetLongName());
      assert s != null;
      logx.debug(s);
    }

    final Context context =
      new Context(compilation, topology, shader_name, logx);

    final List<Pair<GTypeName, GASTTypeDeclaration>> types =
      GTransform.makeTypes(context);
    final List<Pair<GTermNameGlobal, GASTTermDeclaration>> terms =
      GTransform.makeTerms(context, version);

    final List<GASTShaderFragmentInput> inputs =
      GTransform.makeFragmentInputs(context, fragment.getInputs());
    final List<GASTShaderFragmentOutput> outputs =
      GTransform.makeFragmentOutputs(context, fragment.getOutputs());
    final List<GASTShaderFragmentParameter> parameters =
      GTransform.makeFragmentParameters(context, fragment.getParameters());

    final GASTShaderMainFragment main =
      GTransform.makeFragmentMain(context, fragment, terms, version);

    return new GASTShaderFragment(
      inputs,
      main,
      outputs,
      parameters,
      terms,
      types,
      version);
  }

  /**
   * Transform a Parasol vertex shader to a GLSL vertex shader.
   * 
   * @param compilation
   *          The AST
   * @param topology
   *          The topologically sorted types and terms
   * @param shader_name
   *          The shader name
   * @param version
   *          The GLSL version
   * @param log
   *          A log interface
   * @return A transformed GLSL shader
   * @throws GFFIError
   *           If an FFI error occurs
   */

  public static GASTShaderVertex transformVertex(
    final TASTCompilation compilation,
    final Topology topology,
    final TASTShaderNameFlat shader_name,
    final GVersionType version,
    final LogUsableType log)
    throws GFFIError
  {
    final TASTDModule m =
      GTransform.checkConstraints(
        compilation,
        topology,
        shader_name,
        version,
        log);

    final TASTDShader shader = m.getShaders().get(shader_name.getName());

    if ((shader instanceof TASTDShaderVertex) == false) {
      throw new IllegalArgumentException("Expected a vertex shader");
    }

    final TASTDShaderVertex vertex = (TASTDShaderVertex) shader;

    final LogUsableType logx =
      NullCheck.notNull(log, "Log").with("vertex-transformer");

    if (logx.wouldLog(LogLevel.LOG_DEBUG)) {
      final String s =
        String.format(
          "Transforming vertex shader %s for %s",
          shader_name.show(),
          version.versionGetLongName());
      assert s != null;
      logx.debug(s);
    }

    final Context context =
      new Context(compilation, topology, shader_name, logx);

    final List<Pair<GTypeName, GASTTypeDeclaration>> types =
      GTransform.makeTypes(context);
    final List<Pair<GTermNameGlobal, GASTTermDeclaration>> terms =
      GTransform.makeTerms(context, version);

    final List<GASTShaderVertexInput> inputs =
      GTransform.makeVertexInputs(context, vertex.getInputs());
    final List<GASTShaderVertexOutput> outputs =
      GTransform.makeVertexOutputs(context, vertex.getOutputs());
    final List<GASTShaderVertexParameter> parameters =
      GTransform.makeVertexParameters(context, vertex.getParameters());
    final GASTShaderMainVertex main =
      GTransform.makeVertexMain(context, vertex, terms, version);

    return new GASTShaderVertex(
      inputs,
      main,
      outputs,
      parameters,
      terms,
      types,
      version);
  }

  private GTransform()
  {
    throw new UnreachableCodeException();
  }
}
