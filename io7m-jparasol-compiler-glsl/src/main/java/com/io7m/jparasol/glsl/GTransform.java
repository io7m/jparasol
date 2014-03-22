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

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jaux.functional.Function;
import com.io7m.jaux.functional.Option;
import com.io7m.jaux.functional.Pair;
import com.io7m.jaux.functional.Unit;
import com.io7m.jlog.Level;
import com.io7m.jlog.Log;
import com.io7m.jparasol.ModulePathFlat;
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
import com.io7m.jparasol.typed.ast.TASTExpressionVisitor;
import com.io7m.jparasol.typed.ast.TASTFragmentShaderLocalVisitor;
import com.io7m.jparasol.typed.ast.TASTFragmentShaderOutputVisitor;
import com.io7m.jparasol.typed.ast.TASTLocalLevelVisitor;
import com.io7m.jparasol.typed.ast.TASTShaderNameFlat;
import com.io7m.jparasol.typed.ast.TASTTermName;
import com.io7m.jparasol.typed.ast.TASTTermName.TASTTermNameExternal;
import com.io7m.jparasol.typed.ast.TASTTermName.TASTTermNameGlobal;
import com.io7m.jparasol.typed.ast.TASTTermName.TASTTermNameLocal;
import com.io7m.jparasol.typed.ast.TASTTermNameFlat;
import com.io7m.jparasol.typed.ast.TASTTermNameVisitor;
import com.io7m.jparasol.typed.ast.TASTTermVisitor;
import com.io7m.jparasol.typed.ast.TASTTypeVisitor;

public final class GTransform
{
  private static final class Binding
  {
    private final boolean             external;
    private final @Nonnull String     original;
    private final @Nonnull String     current;
    private final @Nonnull TValueType type;

    private Binding(
      final @Nonnull String original,
      final @Nonnull TValueType type,
      final @Nonnull String current,
      final boolean external)
    {
      this.original = original;
      this.current = current;
      this.type = type;
      this.external = external;

      assert (external == true ? original.equals(current) : true);
    }

    public static @Nonnull Binding newExternalBinding(
      final @Nonnull String name,
      final @Nonnull TValueType type)
    {
      return new Binding(name, type, name, true);
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
      final Binding other = (Binding) obj;
      if (this.current == null) {
        if (other.current != null) {
          return false;
        }
      } else if (!this.current.equals(other.current)) {
        return false;
      }
      if (this.external != other.external) {
        return false;
      }
      if (this.original == null) {
        if (other.original != null) {
          return false;
        }
      } else if (!this.original.equals(other.original)) {
        return false;
      }
      if (this.type == null) {
        if (other.type != null) {
          return false;
        }
      } else if (!this.type.equals(other.type)) {
        return false;
      }
      return true;
    }

    public @Nonnull String getOriginal()
    {
      return this.original;
    }

    public @Nonnull String getCurrent()
    {
      return this.current;
    }

    public @Nonnull TValueType getType()
    {
      return this.type;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.current.hashCode();
      result = (prime * result) + (this.external ? 1231 : 1237);
      result = (prime * result) + this.original.hashCode();
      result = (prime * result) + this.type.hashCode();
      return result;
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
      return builder.toString();
    }

    public static Binding newBinding(
      final @Nonnull String original,
      final @Nonnull TValueType type,
      final @Nonnull String current)
    {
      return new Binding(original, type, current, false);
    }
  }

  private static final class Context
  {
    private static final @Nonnull String                                   PREFIX_LOCAL;
    private static final @Nonnull String                                   PREFIX_TERM;
    private static final @Nonnull String                                   PREFIX_TYPE;

    static {
      PREFIX_TYPE = "pt_";
      PREFIX_TERM = "p_";
      PREFIX_LOCAL = "pl_";
    }

    private final @Nonnull TASTCompilation                                 compilation;
    private final @Nonnull GFFI                                            ffi;
    private final @Nonnull Log                                             log;
    private final @Nonnull TASTShaderNameFlat                              shader_name;
    private final @Nonnull AtomicInteger                                   temporary_prime;
    private final @Nonnull GNameContext<TASTTermNameFlat, GTermNameGlobal> terms_names;
    private final @Nonnull Topology                                        topology;
    private final @Nonnull GNameContext<TTypeNameFlat, GTypeName>          types_names;

    public Context(
      final @Nonnull TASTCompilation compilation,
      final @Nonnull Topology topology,
      final @Nonnull TASTShaderNameFlat shader_name,
      final @Nonnull Log log)
    {
      this.log = new Log(log, "context");

      this.ffi = GFFI.newFFI(this.log);
      this.compilation = compilation;
      this.topology = topology;
      this.shader_name = shader_name;
      this.temporary_prime = new AtomicInteger(0);

      this.types_names =
        GNameContext.newContext(this.log, new GNameConstructor<GTypeName>() {
          @Override public GTypeName newName(
            final @Nonnull String name)
          {
            return new GTypeName(name);
          }
        });

      this.terms_names =
        GNameContext.newContext(
          this.log,
          new GNameConstructor<GTermNameGlobal>() {
            @Override public GTermNameGlobal newName(
              final @Nonnull String name)
            {
              return new GTermNameGlobal(name);
            }
          });
    }

    public @Nonnull TASTCompilation getCompilation()
    {
      return this.compilation;
    }

    public @Nonnull GFFI getFFI()
    {
      return this.ffi;
    }

    public @Nonnull Binding getFreshBindingFromLocal(
      final @Nonnull TASTTermNameLocal name,
      final @Nonnull TValueType type,
      final boolean external)
    {
      final String original = name.getCurrent();
      if (external) {
        this.log.debug(String.format(
          "Fresh local (external) binding %s",
          original));
        return Binding.newExternalBinding(original, type);
      }

      final String current = Context.PREFIX_LOCAL + original;
      this.log.debug(String.format(
        "Fresh local binding %s -> %s",
        original,
        current));
      return Binding.newBinding(original, type, current);
    }

    private @Nonnull String getFreshTemporaryName()
    {
      final StringBuilder s = new StringBuilder();
      s.append("_tmp_");
      s.append(this.temporary_prime.incrementAndGet());
      final String name = s.toString();
      if (this.log.enabled(Level.LOG_DEBUG)) {
        this.log.debug(String.format("Fresh temporary %s", name));
      }
      return name;
    }

    public @Nonnull GTermNameGlobal getGlobalTermName(
      final @Nonnull TASTTermNameFlat term)
    {
      final String actual = this.terms_names.getName(term).show();
      return new GTermNameGlobal(Context.PREFIX_TERM + actual);
    }

    public @Nonnull GTermNameLocal getLocalFromBinding(
      final @Nonnull Binding b)
    {
      this.log.debug(String.format(
        "Local term name %s from %sbinding",
        b.getCurrent(),
        b.isExternal() ? "(external) " : ""));
      return new GTermNameLocal(b.getCurrent());
    }

    public @Nonnull Log getLog()
    {
      return this.log;
    }

    public @Nonnull TASTShaderNameFlat getShaderName()
    {
      return this.shader_name;
    }

    public @Nonnull Topology getTopology()
    {
      return this.topology;
    }

    public @Nonnull GTypeName getTypeName(
      final @Nonnull TType type)
      throws ConstraintError
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

    public @Nonnull GTermName lookupTermName(
      final @Nonnull Map<String, Binding> bindings,
      final @Nonnull TASTTermName name)
      throws ConstraintError
    {
      return name
        .termNameVisitableAccept(new TASTTermNameVisitor<GTermName, ConstraintError>() {
          @Override public GTermName termNameVisitGlobal(
            final TASTTermNameGlobal t)
            throws ConstraintError,
              ConstraintError
          {
            final TASTTermNameFlat flat =
              TASTTermNameFlat.fromTermNameGlobal(t);
            return Context.this.getGlobalTermName(flat);
          }

          @Override public GTermName termNameVisitLocal(
            final TASTTermNameLocal t)
            throws ConstraintError,
              ConstraintError
          {
            assert bindings.containsKey(t.getCurrent());
            final Binding binding = bindings.get(t.getCurrent());
            return new GTermNameLocal(binding.getCurrent());
          }

          @Override public GTermName termNameVisitExternal(
            final TASTTermNameExternal t)
            throws ConstraintError,
              ConstraintError
          {
            throw new UnreachableCodeException();
          }
        });
    }

    public @Nonnull Binding getFreshBindingTemporary(
      final @Nonnull TValueType type)
    {
      final String temp = this.getFreshTemporaryName();
      return Binding.newBinding(temp, type, temp);
    }

    public @Nonnull GTermNameGlobal getFreshTemporaryGlobal()
    {
      return new GTermNameGlobal(this.getFreshTemporaryName());
    }
  }

  private static final class ExpressionStatementTransformer implements
    TASTExpressionVisitor<GASTScope, TASTDValueLocal, GFFIError>
  {
    private final @Nonnull Map<String, Binding>                             bindings;
    private final @Nonnull Context                                          context;
    private final @Nonnull List<Pair<GTermNameGlobal, GASTTermDeclaration>> declarations;
    private final @Nonnull GVersion                                         version;

    public ExpressionStatementTransformer(
      final @Nonnull Context context,
      final @Nonnull Map<String, Binding> bindings,
      final @Nonnull List<Pair<GTermNameGlobal, GASTTermDeclaration>> declarations,
      final @Nonnull GVersion version)
    {
      this.context = context;
      this.bindings = bindings;
      this.declarations = declarations;
      this.version = version;
    }

    @Override public GASTScope expressionVisitApplication(
      final @Nonnull List<GASTScope> arguments,
      final @Nonnull TASTEApplication e)
      throws ConstraintError,
        GFFIError
    {
      return this.wrapReturn(e);
    }

    @Override public boolean expressionVisitApplicationPre(
      final @Nonnull TASTEApplication e)
      throws ConstraintError,
        GFFIError
    {
      return false;
    }

    @Override public GASTScope expressionVisitBoolean(
      final @Nonnull TASTEBoolean e)
      throws ConstraintError,
        GFFIError
    {
      return this.wrapReturn(e);
    }

    @Override public GASTScope expressionVisitConditional(
      final @Nonnull GASTScope condition,
      final @Nonnull GASTScope left,
      final @Nonnull GASTScope right,
      final @Nonnull TASTEConditional e)
      throws ConstraintError,
        GFFIError
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
      final @Nonnull TASTEConditional e)
      throws ConstraintError,
        GFFIError
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalConditionPre(
      final @Nonnull TASTEConditional e)
      throws ConstraintError,
        GFFIError
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalLeftPost(
      final @Nonnull TASTEConditional e)
      throws ConstraintError,
        GFFIError
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalLeftPre(
      final @Nonnull TASTEConditional e)
      throws ConstraintError,
        GFFIError
    {
      // Nothing
    }

    @Override public boolean expressionVisitConditionalPre(
      final @Nonnull TASTEConditional e)
      throws ConstraintError,
        GFFIError
    {
      return false;
    }

    @Override public void expressionVisitConditionalRightPost(
      final @Nonnull TASTEConditional e)
      throws ConstraintError,
        GFFIError
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalRightPre(
      final @Nonnull TASTEConditional e)
      throws ConstraintError,
        GFFIError
    {
      // Nothing
    }

    @Override public GASTScope expressionVisitInteger(
      final @Nonnull TASTEInteger e)
      throws ConstraintError,
        GFFIError
    {
      return this.wrapReturn(e);
    }

    @SuppressWarnings("synthetic-access") @Override public
      GASTScope
      expressionVisitLet(
        final @Nonnull List<TASTDValueLocal> k_bindings,
        final @Nonnull GASTScope body,
        final @Nonnull TASTELet e)
        throws ConstraintError,
          GFFIError
    {
      final ArrayList<GASTStatement> statements =
        new ArrayList<GASTStatement>();

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

    @Override public
      TASTLocalLevelVisitor<TASTDValueLocal, GFFIError>
      expressionVisitLetPre(
        final @Nonnull TASTELet e)
        throws ConstraintError,
          GFFIError
    {
      return null;
    }

    @Override public GASTScope expressionVisitNew(
      final @Nonnull List<GASTScope> arguments,
      final @Nonnull TASTENew e)
      throws ConstraintError,
        GFFIError
    {
      return this.wrapReturn(e);
    }

    @Override public boolean expressionVisitNewPre(
      final @Nonnull TASTENew e)
      throws ConstraintError,
        GFFIError
    {
      return false;
    }

    @Override public GASTScope expressionVisitReal(
      final @Nonnull TASTEReal e)
      throws ConstraintError,
        GFFIError
    {
      return this.wrapReturn(e);
    }

    @Override public GASTScope expressionVisitRecord(
      final @Nonnull TASTERecord e)
      throws ConstraintError,
        GFFIError
    {
      final ArrayList<GASTStatement> statements =
        new ArrayList<GASTStatement>();
      final HashMap<String, GTermNameLocal> field_temporaries =
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

      final ArrayList<GASTExpression> ordered_args =
        new ArrayList<GASTExpression>();
      final TRecord rt = (TRecord) e.getType();
      for (final TRecordField f : rt.getFields()) {
        assert field_temporaries.containsKey(f.getName());
        final GTermNameLocal v = field_temporaries.get(f.getName());
        ordered_args.add(new GASTEVariable(this.context.getTypeName(f
          .getType()), v));
      }

      final GASTEConstruction cons =
        new GASTEConstruction(this.context.getTypeName(rt), ordered_args);
      statements.add(new GASTStatement.GASTReturn(cons));
      return new GASTScope(statements);
    }

    @Override public GASTScope expressionVisitRecordProjection(
      final @Nonnull GASTScope body,
      final @Nonnull TASTERecordProjection e)
      throws ConstraintError,
        GFFIError
    {
      return this.wrapReturn(e);
    }

    @Override public boolean expressionVisitRecordProjectionPre(
      final @Nonnull TASTERecordProjection e)
      throws ConstraintError,
        GFFIError
    {
      return false;
    }

    @Override public GASTScope expressionVisitSwizzle(
      final @Nonnull GASTScope body,
      final @Nonnull TASTESwizzle e)
      throws ConstraintError,
        GFFIError
    {
      return this.wrapReturn(e);
    }

    @Override public boolean expressionVisitSwizzlePre(
      final @Nonnull TASTESwizzle e)
      throws ConstraintError,
        GFFIError
    {
      return false;
    }

    @Override public GASTScope expressionVisitVariable(
      final @Nonnull TASTEVariable e)
      throws ConstraintError,
        GFFIError
    {
      return this.wrapReturn(e);
    }

    private @Nonnull GASTScope wrapReturn(
      final @Nonnull TASTExpression e)
      throws ConstraintError,
        GFFIError
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

  private static final class ExpressionTransformer implements
    TASTExpressionVisitor<GASTExpression, TASTDValueLocal, GFFIError>
  {
    private final @Nonnull Map<String, Binding>                             bindings;
    private final @Nonnull Context                                          context;
    private final @Nonnull List<Pair<GTermNameGlobal, GASTTermDeclaration>> declarations;
    private final @Nonnull GVersion                                         version;

    public ExpressionTransformer(
      final @Nonnull Context context,
      final @Nonnull List<Pair<GTermNameGlobal, GASTTermDeclaration>> declarations,
      final @Nonnull Map<String, Binding> bindings,
      final @Nonnull GVersion version)
    {
      this.context = context;
      this.declarations = declarations;
      this.bindings = bindings;
      this.version = version;
    }

    @Override public GASTExpression expressionVisitApplication(
      final @Nonnull List<GASTExpression> arguments,
      final @Nonnull TASTEApplication e)
      throws ConstraintError,
        GFFIError
    {
      return e.getName().termNameVisitableAccept(
        new TASTTermNameVisitor<GASTExpression, GFFIError>() {
          @SuppressWarnings("synthetic-access") @Override public
            GASTExpression
            termNameVisitGlobal(
              final @Nonnull TASTTermNameGlobal t)
              throws ConstraintError,
                GFFIError
          {
            final TASTTermNameFlat term_name =
              TASTTermNameFlat.fromTermNameGlobal(t);

            final TASTDTerm term =
              ExpressionTransformer.this.context.getCompilation().lookupTerm(
                term_name);

            final GTermNameGlobal name =
              ExpressionTransformer.this.context.getGlobalTermName(term_name);

            return term
              .termVisitableAccept(new TASTTermVisitor<GASTExpression, GFFIError>() {

                /**
                 * The application of a defined function.
                 */

                @Override public GASTExpression termVisitFunctionDefined(
                  final @Nonnull TASTDFunctionDefined f)
                  throws ConstraintError,
                    GFFIError
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
                  final @Nonnull TASTDFunctionExternal f)
                  throws ConstraintError,
                    GFFIError
                {
                  final GFFI ffi =
                    ExpressionTransformer.this.context.getFFI();
                  final GFFIExpression g =
                    ffi.getExpression(
                      f,
                      arguments,
                      ExpressionTransformer.this.version);

                  return g
                    .ffiExpressionAccept(new GFFIExpressionVisitor<GASTExpression, ConstraintError>() {
                      @Override public
                        GASTExpression
                        ffiExpressionVisitBuiltIn(
                          final @Nonnull GFFIExpressionBuiltIn ge)
                          throws ConstraintError
                      {
                        return ge.getExpression();
                      }

                      @Override public
                        GASTExpression
                        ffiExpressionVisitDefined(
                          final @Nonnull GFFIExpressionDefined ge)
                          throws ConstraintError
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
                  final @Nonnull TASTDValueDefined v)
                  throws ConstraintError,
                    GFFIError
                {
                  throw new UnreachableCodeException();
                }

                /**
                 * The application of a value is forbidden by the type
                 * checker.
                 */

                @Override public GASTExpression termVisitValueExternal(
                  final @Nonnull TASTDValueExternal v)
                  throws GFFIError,
                    ConstraintError
                {
                  throw new UnreachableCodeException();
                }
              });
          }

          @Override public GASTExpression termNameVisitLocal(
            final @Nonnull TASTTermNameLocal t)
            throws ConstraintError,
              GFFIError
          {
            /**
             * There's no way to define a local function, so there's no way a
             * function application can occur on a local name.
             */
            throw new UnreachableCodeException();
          }

          @Override public GASTExpression termNameVisitExternal(
            final @Nonnull TASTTermNameExternal t)
            throws ConstraintError,
              GFFIError
          {
            throw new UnreachableCodeException();
          }
        });
    }

    @Override public boolean expressionVisitApplicationPre(
      final @Nonnull TASTEApplication e)
      throws ConstraintError,
        GFFIError
    {
      return true;
    }

    @Override public GASTExpression expressionVisitBoolean(
      final @Nonnull TASTEBoolean e)
      throws ConstraintError,
        GFFIError
    {
      return new GASTEBoolean(e.getValue());
    }

    @Override public GASTExpression expressionVisitConditional(
      final @Nonnull GASTExpression condition,
      final @Nonnull GASTExpression left,
      final @Nonnull GASTExpression right,
      final @Nonnull TASTEConditional e)
      throws ConstraintError,
        GFFIError
    {
      return this.wrapFunction(e);
    }

    @Override public void expressionVisitConditionalConditionPost(
      final @Nonnull TASTEConditional e)
      throws ConstraintError,
        GFFIError
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalConditionPre(
      final @Nonnull TASTEConditional e)
      throws ConstraintError,
        GFFIError
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalLeftPost(
      final @Nonnull TASTEConditional e)
      throws ConstraintError,
        GFFIError
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalLeftPre(
      final @Nonnull TASTEConditional e)
      throws ConstraintError,
        GFFIError
    {
      // Nothing
    }

    @Override public boolean expressionVisitConditionalPre(
      final @Nonnull TASTEConditional e)
      throws ConstraintError,
        GFFIError
    {
      return false;
    }

    @Override public void expressionVisitConditionalRightPost(
      final @Nonnull TASTEConditional e)
      throws ConstraintError,
        GFFIError
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalRightPre(
      final @Nonnull TASTEConditional e)
      throws ConstraintError,
        GFFIError
    {
      // Nothing
    }

    @Override public GASTExpression expressionVisitInteger(
      final @Nonnull TASTEInteger e)
      throws ConstraintError,
        GFFIError
    {
      return new GASTExpression.GASTEInteger(e.getValue());
    }

    @Override public GASTExpression expressionVisitLet(
      final @Nonnull List<TASTDValueLocal> let_bindings,
      final @Nonnull GASTExpression body,
      final @Nonnull TASTELet e)
      throws ConstraintError,
        GFFIError
    {
      return this.wrapFunction(e);
    }

    @Override public
      TASTLocalLevelVisitor<TASTDValueLocal, GFFIError>
      expressionVisitLetPre(
        final @Nonnull TASTELet e)
        throws ConstraintError,
          GFFIError
    {
      return null;
    }

    @Override public GASTExpression expressionVisitNew(
      final @Nonnull List<GASTExpression> arguments,
      final @Nonnull TASTENew e)
      throws ConstraintError,
        GFFIError
    {
      final GTypeName type = this.context.getTypeName(e.getType());
      return new GASTEConstruction(type, arguments);
    }

    @Override public boolean expressionVisitNewPre(
      final @Nonnull TASTENew e)
      throws ConstraintError,
        GFFIError
    {
      return true;
    }

    @Override public GASTExpression expressionVisitReal(
      final @Nonnull TASTEReal e)
      throws ConstraintError,
        GFFIError
    {
      return new GASTExpression.GASTEFloat(e.getValue());
    }

    @Override public GASTExpression expressionVisitRecord(
      final @Nonnull TASTERecord e)
      throws ConstraintError,
        GFFIError
    {
      return this.wrapFunction(e);
    }

    @Override public GASTExpression expressionVisitRecordProjection(
      final @Nonnull GASTExpression body,
      final @Nonnull TASTERecordProjection e)
      throws ConstraintError,
        GFFIError
    {
      final GFieldName field = new GFieldName(e.getField().getActual());
      final TType type = e.getType();
      return new GASTEProjection(body, field, type);
    }

    @Override public boolean expressionVisitRecordProjectionPre(
      final @Nonnull TASTERecordProjection e)
      throws ConstraintError,
        GFFIError
    {
      return true;
    }

    @Override public GASTExpression expressionVisitSwizzle(
      final @Nonnull GASTExpression body,
      final @Nonnull TASTESwizzle e)
      throws ConstraintError,
        GFFIError
    {
      final TType type = e.getType();
      final List<GFieldName> fields = new ArrayList<GFieldName>();
      for (final TokenIdentifierLower f : e.getFields()) {
        fields.add(new GFieldName(f.getActual()));
      }
      return new GASTESwizzle(body, fields, type);
    }

    @Override public boolean expressionVisitSwizzlePre(
      final @Nonnull TASTESwizzle e)
      throws ConstraintError,
        GFFIError
    {
      return true;
    }

    @Override public GASTExpression expressionVisitVariable(
      final @Nonnull TASTEVariable e)
      throws ConstraintError,
        GFFIError
    {
      final GTypeName type = this.context.getTypeName(e.getType());
      final GTermName term =
        e.getName().termNameVisitableAccept(
          new TASTTermNameVisitor<GTermName, ConstraintError>() {
            @SuppressWarnings("synthetic-access") @Override public
              GTermName
              termNameVisitGlobal(
                final TASTTermNameGlobal t)
                throws ConstraintError,
                  ConstraintError
            {
              final TASTTermNameFlat flat =
                TASTTermNameFlat.fromTermNameGlobal(t);
              return ExpressionTransformer.this.context
                .getGlobalTermName(flat);
            }

            @SuppressWarnings("synthetic-access") @Override public
              GTermName
              termNameVisitLocal(
                final TASTTermNameLocal t)
                throws ConstraintError,
                  ConstraintError
            {
              final String name = t.getCurrent();
              assert ExpressionTransformer.this.bindings.containsKey(name);
              final Binding binding =
                ExpressionTransformer.this.bindings.get(name);
              return ExpressionTransformer.this.context
                .getLocalFromBinding(binding);
            }

            @Override public GTermName termNameVisitExternal(
              final TASTTermNameExternal t)
              throws ConstraintError,
                ConstraintError
            {
              return new GTermNameGlobal(t.getCurrent());
            }
          });
      return new GASTEVariable(type, term);
    }

    private @Nonnull GASTExpression wrapFunction(
      final @Nonnull TASTExpression e)
      throws ConstraintError,
        GFFIError
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

      final Set<String> occurs =
        Occurences.occursIn(e, this.bindings.keySet());

      /**
       * Construct the new function and add it to the environment.
       */

      final GTermNameGlobal f_name = this.context.getFreshTemporaryGlobal();
      final GTypeName f_returns = this.context.getTypeName(e.getType());

      final List<Pair<GTermNameLocal, GTypeName>> f_parameters =
        new ArrayList<Pair<GTermNameLocal, GTypeName>>();

      for (final String b_name : this.bindings.keySet()) {
        final Binding binding = this.bindings.get(b_name);

        if (occurs.contains(b_name)) {
          final GTermNameLocal p_name =
            this.context.getLocalFromBinding(binding);
          final GTypeName p_type =
            this.context.getTypeName(binding.getType());
          final Pair<GTermNameLocal, GTypeName> p =
            new Pair<GTermNameLocal, GTypeName>(p_name, p_type);
          f_parameters.add(p);
        }
      }

      final GASTTermFunction function =
        new GASTTermFunction(f_name, f_returns, f_parameters, statement);
      this.declarations.add(new Pair<GTermNameGlobal, GASTTermDeclaration>(
        function.getName(),
        function));

      /**
       * Construct an application of the function.
       */

      final List<GASTExpression> application_args =
        new ArrayList<GASTExpression>();
      for (final Pair<GTermNameLocal, GTypeName> b : f_parameters) {
        application_args.add(new GASTExpression.GASTEVariable(
          b.second,
          b.first));
      }

      final GASTEApplication application =
        new GASTExpression.GASTEApplication(
          f_name,
          e.getType(),
          application_args);

      return application;
    }
  }

  private static final class TermTransformer implements
    TASTTermVisitor<GASTTermDeclaration, GFFIError>
  {
    private final @Nonnull Context                                          context;
    private final @Nonnull List<Pair<GTermNameGlobal, GASTTermDeclaration>> declarations;
    private final @Nonnull TASTTermNameFlat                                 name;
    private final @Nonnull GVersion                                         version;

    public TermTransformer(
      final @Nonnull Context context,
      final @Nonnull List<Pair<GTermNameGlobal, GASTTermDeclaration>> declarations,
      final @Nonnull TASTTermNameFlat name,
      final @Nonnull GVersion version)
    {
      this.context = context;
      this.declarations = declarations;
      this.name = name;
      this.version = version;
    }

    @Override public GASTTermFunction termVisitFunctionDefined(
      final @Nonnull TASTDFunctionDefined f)
      throws ConstraintError,
        GFFIError
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
        final GTermNameLocal term_name =
          this.context.getLocalFromBinding(binding);
        final GTypeName type_name = this.context.getTypeName(b.getType());
        final Pair<GTermNameLocal, GTypeName> p =
          new Pair<GTermNameLocal, GTypeName>(term_name, type_name);
        parameters.add(p);
      }

      final GTermNameGlobal function_name =
        this.context.getGlobalTermName(this.name);

      return new GASTTermFunction(
        function_name,
        returns,
        parameters,
        statement);
    }

    @Override public GASTTermFunction termVisitFunctionExternal(
      final @Nonnull TASTDFunctionExternal f)
      throws ConstraintError,
        GFFIError
    {
      final TASTDFunctionDefined td =
        this.context.getFFI().getFunctionDefinition(f, this.version);

      if (td != null) {
        return this.termVisitFunctionDefined(td);
      }
      return null;
    }

    @Override public GASTTermValue termVisitValueDefined(
      final @Nonnull TASTDValueDefined v)
      throws ConstraintError,
        GFFIError
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
      return new GASTTermValue(term_name, type_name, r);
    }

    @Override public GASTTermDeclaration termVisitValueExternal(
      final @Nonnull TASTDValueExternal v)
      throws GFFIError,
        ConstraintError
    {
      final TASTDValueDefined td =
        this.context.getFFI().getValueDefinition(v, this.version);

      if (td != null) {
        return this.termVisitValueDefined(td);
      }
      return null;
    }
  }

  private static @Nonnull TASTDModule checkConstraints(
    final @Nonnull TASTCompilation compilation,
    final @Nonnull Topology topology,
    final @Nonnull TASTShaderNameFlat shader_name,
    final @Nonnull GVersion version,
    final @Nonnull Log log)
    throws ConstraintError
  {
    Constraints.constrainNotNull(compilation, "Compilation");
    Constraints.constrainNotNull(shader_name, "Shader name");
    Constraints.constrainNotNull(version, "Version");
    Constraints.constrainNotNull(log, "Log");
    Constraints.constrainArbitrary(
      topology.getShaderName().equals(shader_name),
      "References produced for shader");

    final Map<ModulePathFlat, TASTDModule> modules = compilation.getModules();
    Constraints.constrainArbitrary(
      modules.containsKey(shader_name.getModulePath()),
      "Module exists");
    final TASTDModule m = modules.get(shader_name.getModulePath());
    Constraints.constrainArbitrary(
      m.getShaders().containsKey(shader_name.getName()),
      "Shader exists");

    return m;
  }

  private static @Nonnull TASTDShaderFragmentOutput findFragmentOutput(
    final @Nonnull TASTDShaderFragment shader,
    final @Nonnull String name)
  {
    for (final TASTDShaderFragmentOutput o : shader.getOutputs()) {
      if (o.getName().getActual().equals(name)) {
        return o;
      }
    }
    throw new UnreachableCodeException();
  }

  private static @Nonnull TASTDShaderVertexOutput findVertexOutput(
    final @Nonnull TASTDShaderVertex shader,
    final @Nonnull String name)
  {
    for (final TASTDShaderVertexOutput o : shader.getOutputs()) {
      if (o.getName().getActual().equals(name)) {
        return o;
      }
    }
    throw new UnreachableCodeException();
  }

  private static @Nonnull List<GASTShaderFragmentInput> makeFragmentInputs(
    final @Nonnull Context context,
    final @Nonnull List<TASTDShaderFragmentInput> inputs)
    throws ConstraintError
  {
    final ArrayList<GASTShaderFragmentInput> results =
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

  private static @Nonnull GASTShaderMainFragment makeFragmentMain(
    final @Nonnull Context context,
    final @Nonnull TASTDShaderFragment fragment,
    final @Nonnull List<Pair<GTermNameGlobal, GASTTermDeclaration>> terms,
    final @Nonnull GVersion version)
    throws ConstraintError,
      GFFIError
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
        .fragmentShaderLocalVisitableAccept(new TASTFragmentShaderLocalVisitor<Unit, GFFIError>() {
          @Override public Unit fragmentShaderVisitLocalDiscard(
            final TASTDShaderFragmentLocalDiscard d)
            throws ConstraintError,
              GFFIError
          {
            final GASTExpression expression =
              d.getExpression().expressionVisitableAccept(
                new ExpressionTransformer(context, terms, bindings, version));
            statements.add(new GASTFragmentConditionalDiscard(expression));
            return Unit.unit();
          }

          @SuppressWarnings("synthetic-access") @Override public
            Unit
            fragmentShaderVisitLocalValue(
              final TASTDShaderFragmentLocalValue v)
              throws ConstraintError,
                GFFIError
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
      final TASTDShaderFragmentOutput output =
        GTransform.findFragmentOutput(fragment, f_name.getActual());

      output
        .fragmentShaderOutputVisitableAccept(new TASTFragmentShaderOutputVisitor<Unit, GFFIError>() {
          @Override public Unit fragmentShaderVisitOutputData(
            final @Nonnull TASTDShaderFragmentOutputData od)
            throws GFFIError,
              ConstraintError
          {
            final GShaderOutputName name =
              new GShaderOutputName(f_name.getActual());
            final GTermName value =
              context.lookupTermName(bindings, f.getVariable().getName());
            writes.add(new GASTFragmentOutputDataAssignment(name, od
              .getIndex(), value));
            return Unit.unit();
          }

          @Override public Unit fragmentShaderVisitOutputDepth(
            final @Nonnull TASTDShaderFragmentOutputDepth v)
            throws GFFIError,
              ConstraintError
          {
            assert depth_write.get() == null;

            final GShaderOutputName name =
              new GShaderOutputName(f_name.getActual());
            final GTermName value =
              context.lookupTermName(bindings, f.getVariable().getName());
            depth_write
              .set(new GASTFragmentOutputDepthAssignment(name, value));
            return Unit.unit();
          }
        });
    }

    @SuppressWarnings("unchecked") final Option<GASTFragmentOutputDepthAssignment> depth_write_opt =
      (Option<GASTFragmentOutputDepthAssignment>) ((depth_write.get() != null)
        ? Option.some(depth_write.get())
        : Option.none());

    return new GASTShaderMainFragment(statements, writes, depth_write_opt);
  }

  private static @Nonnull List<GASTShaderFragmentOutput> makeFragmentOutputs(
    final @Nonnull Context context,
    final @Nonnull List<TASTDShaderFragmentOutput> outputs)
    throws ConstraintError
  {
    final ArrayList<GASTShaderFragmentOutput> results =
      new ArrayList<GASTShaderFragmentOutput>();

    for (final TASTDShaderFragmentOutput o : outputs) {
      o
        .fragmentShaderOutputVisitableAccept(new TASTFragmentShaderOutputVisitor<Unit, ConstraintError>() {
          @Override public Unit fragmentShaderVisitOutputData(
            final @Nonnull TASTDShaderFragmentOutputData od)
            throws ConstraintError,
              ConstraintError
          {
            final GShaderOutputName name =
              new GShaderOutputName(od.getName().getActual());
            final GTypeName type = context.getTypeName(od.getType());
            results.add(new GASTShaderFragmentOutput(
              name,
              od.getIndex(),
              type));
            return Unit.unit();
          }

          @Override public Unit fragmentShaderVisitOutputDepth(
            final @Nonnull TASTDShaderFragmentOutputDepth v)
            throws ConstraintError,
              ConstraintError
          {
            return Unit.unit();
          }
        });
    }

    return results;
  }

  private static @Nonnull
    List<GASTShaderFragmentParameter>
    makeFragmentParameters(
      final @Nonnull Context context,
      final @Nonnull List<TASTDShaderFragmentParameter> parameters)
      throws ConstraintError
  {
    final ArrayList<GASTShaderFragmentParameter> results =
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

  @SuppressWarnings("synthetic-access") private static @Nonnull
    List<Pair<GTermNameGlobal, GASTTermDeclaration>>
    makeTerms(
      final @Nonnull Context context,
      final @Nonnull GVersion version)
      throws ConstraintError,
        GFFIError
  {
    final TASTCompilation compilation = context.getCompilation();
    final Topology topology = context.getTopology();
    final Log log = context.log;

    final List<Pair<GTermNameGlobal, GASTTermDeclaration>> declarations =
      new GLoggingArrayList<Pair<GTermNameGlobal, GASTTermDeclaration>>(
        new Function<Pair<GTermNameGlobal, GASTTermDeclaration>, String>() {
          @Override public String call(
            final Pair<GTermNameGlobal, GASTTermDeclaration> x)
          {
            return x.first.show();
          }
        },
        new Log(log, "terms"));

    for (final TASTTermNameFlat name : topology.getTerms()) {
      final TASTDTerm term = compilation.lookupTerm(name);
      assert term != null;

      if (log.enabled(Level.LOG_DEBUG)) {
        log.debug(String.format("Transforming term %s", name.show()));
      }

      final GASTTermDeclaration result =
        term.termVisitableAccept(new TermTransformer(
          context,
          declarations,
          name,
          version));

      if (result != null) {
        final Pair<GTermNameGlobal, GASTTermDeclaration> p =
          new Pair<GTermNameGlobal, GASTTermDeclaration>(
            result.getName(),
            result);
        declarations.add(p);
      }
    }

    return declarations;
  }

  @SuppressWarnings("synthetic-access") private static @Nonnull
    List<Pair<GTypeName, GASTTypeDeclaration>>
    makeTypes(
      final @Nonnull Context context)
      throws ConstraintError
  {
    final TASTCompilation compilation = context.getCompilation();
    final Topology topology = context.getTopology();
    final Log log = context.log;

    final List<Pair<GTypeName, GASTTypeDeclaration>> declarations =
      new GLoggingArrayList<Pair<GTypeName, GASTTypeDeclaration>>(
        new Function<Pair<GTypeName, GASTTypeDeclaration>, String>() {
          @Override public String call(
            final Pair<GTypeName, GASTTypeDeclaration> p)
          {
            return p.first.show();
          }
        },
        new Log(log, "types"));

    for (final TTypeNameFlat name : topology.getTypes()) {
      final TASTDType type = compilation.lookupType(name);
      assert type != null;

      if (log.enabled(Level.LOG_DEBUG)) {
        log.debug(String.format("Transforming type %s", name.show()));
      }

      final GASTTypeDeclaration declaration =
        type
          .typeVisitableAccept(new TASTTypeVisitor<GASTTypeDeclaration, ConstraintError>() {
            @Override public GASTTypeDeclaration typeVisitTypeRecord(
              final @Nonnull TASTDTypeRecord r)
              throws ConstraintError,
                ConstraintError
            {
              final GTypeName new_name = context.getTypeName(r.getType());

              final List<Pair<GFieldName, GTypeName>> fields =
                new ArrayList<Pair<GFieldName, GTypeName>>();

              for (final TASTDTypeRecordField field : r.getFields()) {
                final GFieldName field_name =
                  new GFieldName(field.getName().getActual());
                final GTypeName field_type =
                  context.getTypeName(field.getType());
                fields.add(new Pair<GFieldName, GTypeName>(
                  field_name,
                  field_type));
              }

              return new GASTTypeDeclaration.GASTTypeRecord(new_name, fields);
            }
          });

      declarations.add(new Pair<GTypeName, GASTTypeDeclaration>(declaration
        .getName(), declaration));
    }

    return declarations;
  }

  private static @Nonnull List<GASTShaderVertexInput> makeVertexInputs(
    final @Nonnull Context context,
    final @Nonnull List<TASTDShaderVertexInput> inputs)
    throws ConstraintError
  {
    final ArrayList<GASTShaderVertexInput> results =
      new ArrayList<GASTShaderVertexInput>();

    for (final TASTDShaderVertexInput i : inputs) {
      final GShaderInputName name =
        new GShaderInputName(i.getName().getCurrent());
      final GTypeName type = context.getTypeName(i.getType());
      results.add(new GASTShaderVertexInput(name, type));
    }

    return results;
  }

  private static @Nonnull GASTShaderMainVertex makeVertexMain(
    final @Nonnull Context context,
    final @Nonnull TASTDShaderVertex vertex,
    final @Nonnull List<Pair<GTermNameGlobal, GASTTermDeclaration>> terms,
    final @Nonnull GVersion version)
    throws ConstraintError,
      GFFIError
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
      final TASTDShaderVertexOutput output =
        GTransform.findVertexOutput(vertex, written_name.getActual());

      if (output.isMain()) {
        writes.add(new GASTVertexOutputAssignment(new GShaderOutputName(
          "gl_Position"), value));
      }

      writes.add(new GASTVertexOutputAssignment(new GShaderOutputName(
        written_name.getActual()), value));
    }

    return new GASTShaderMainVertex(statements, writes);
  }

  private static @Nonnull List<GASTShaderVertexOutput> makeVertexOutputs(
    final @Nonnull Context context,
    final @Nonnull List<TASTDShaderVertexOutput> outputs)
    throws ConstraintError
  {
    final ArrayList<GASTShaderVertexOutput> results =
      new ArrayList<GASTShaderVertexOutput>();

    for (final TASTDShaderVertexOutput o : outputs) {
      final GShaderOutputName name =
        new GShaderOutputName(o.getName().getActual());
      final TValueType type = o.getType();
      final GTypeName type_name = context.getTypeName(type);
      results.add(new GASTShaderVertexOutput(name, type_name, type));
    }

    return results;
  }

  private static @Nonnull
    List<GASTShaderVertexParameter>
    makeVertexParameters(
      final @Nonnull Context context,
      final @Nonnull List<TASTDShaderVertexParameter> parameters)
      throws ConstraintError
  {
    final ArrayList<GASTShaderVertexParameter> results =
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

  private static
    void
    processFragmentLocal(
      final @Nonnull Context context,
      final @Nonnull List<Pair<GTermNameGlobal, GASTTermDeclaration>> declarations,
      final @Nonnull List<GASTFragmentShaderStatement> statements,
      final @Nonnull Map<String, Binding> bindings,
      final @Nonnull TASTDValueLocal b,
      final @Nonnull GVersion version)
      throws ConstraintError,
        GFFIError
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

  private static
    void
    processLocal(
      final @Nonnull Context context,
      final @Nonnull List<Pair<GTermNameGlobal, GASTTermDeclaration>> declarations,
      final @Nonnull List<GASTStatement> statements,
      final @Nonnull Map<String, Binding> bindings,
      final @Nonnull TASTDValueLocal b,
      final @Nonnull GVersion version)
      throws ConstraintError,
        GFFIError
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

  private static
    void
    processLocals(
      final @Nonnull Context context,
      final @Nonnull List<Pair<GTermNameGlobal, GASTTermDeclaration>> declarations,
      final @Nonnull List<GASTStatement> statements,
      final @Nonnull Map<String, Binding> bindings,
      final @Nonnull List<TASTDValueLocal> locals,
      final @Nonnull GVersion version)
      throws ConstraintError,
        GFFIError
  {
    for (final TASTDValueLocal b : locals) {
      GTransform.processLocal(
        context,
        declarations,
        statements,
        bindings,
        b,
        version);
    }
  }

  public static @Nonnull GASTShaderFragment transformFragment(
    final @Nonnull TASTCompilation compilation,
    final @Nonnull Topology topology,
    final @Nonnull TASTShaderNameFlat shader_name,
    final @Nonnull GVersion version,
    final @Nonnull Log log)
    throws ConstraintError,
      GFFIError
  {
    final TASTDModule m =
      GTransform.checkConstraints(
        compilation,
        topology,
        shader_name,
        version,
        log);

    final TASTDShader shader = m.getShaders().get(shader_name.getName());
    Constraints.constrainArbitrary(
      shader instanceof TASTDShaderFragment,
      "Shader is fragment shader");

    final TASTDShaderFragment fragment = (TASTDShaderFragment) shader;

    final Log logx = new Log(log, "fragment-transformer");
    if (logx.enabled(Level.LOG_DEBUG)) {
      logx.debug(String.format(
        "Transforming fragment shader %s for %s",
        shader_name.show(),
        version.getLongName()));
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

  public static @Nonnull GASTShaderVertex transformVertex(
    final @Nonnull TASTCompilation compilation,
    final @Nonnull Topology topology,
    final @Nonnull TASTShaderNameFlat shader_name,
    final @Nonnull GVersion version,
    final @Nonnull Log log)
    throws ConstraintError,
      GFFIError
  {
    final TASTDModule m =
      GTransform.checkConstraints(
        compilation,
        topology,
        shader_name,
        version,
        log);

    final TASTDShader shader = m.getShaders().get(shader_name.getName());
    Constraints.constrainArbitrary(
      shader instanceof TASTDShaderVertex,
      "Shader is vertex shader");

    final TASTDShaderVertex vertex = (TASTDShaderVertex) shader;

    final Log logx = new Log(log, "vertex-transformer");
    if (logx.enabled(Level.LOG_DEBUG)) {
      logx.debug(String.format(
        "Transforming vertex shader %s for %s",
        shader_name.show(),
        version.getLongName()));
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
}
