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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.io7m.jequality.annotations.EqualityReference;
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
import com.io7m.jparasol.typed.TGraphs.GlobalGraph;
import com.io7m.jparasol.typed.TType.TBoolean;
import com.io7m.jparasol.typed.TType.TConstructor;
import com.io7m.jparasol.typed.TType.TFloat;
import com.io7m.jparasol.typed.TType.TFunction;
import com.io7m.jparasol.typed.TType.TFunctionArgument;
import com.io7m.jparasol.typed.TType.TManifestType;
import com.io7m.jparasol.typed.TType.TMatrixType;
import com.io7m.jparasol.typed.TType.TRecord;
import com.io7m.jparasol.typed.TType.TRecordField;
import com.io7m.jparasol.typed.TType.TValueType;
import com.io7m.jparasol.typed.TType.TVector4F;
import com.io7m.jparasol.typed.TType.TVectorType;
import com.io7m.jparasol.typed.TTypeName.TTypeNameBuiltIn;
import com.io7m.jparasol.typed.TTypeName.TTypeNameGlobal;
import com.io7m.jparasol.typed.ast.TASTCompilation;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDExternal;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDFunctionArgument;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDFunctionDefined;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDFunctionExternal;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDImport;
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
import com.io7m.jparasol.typed.ast.TASTShaderName;
import com.io7m.jparasol.typed.ast.TASTTermName;
import com.io7m.jparasol.typed.ast.TASTTermName.TASTTermNameGlobal;
import com.io7m.jparasol.typed.ast.TASTTermName.TASTTermNameLocal;
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
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDShaderFragmentLocalDiscard;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDShaderFragmentLocalValue;
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
import com.io7m.jparasol.untyped.ast.resolved.UASTRExpressionVisitorType;
import com.io7m.jparasol.untyped.ast.resolved.UASTRFragmentShaderLocalVisitorType;
import com.io7m.jparasol.untyped.ast.resolved.UASTRFragmentShaderOutputVisitorType;
import com.io7m.jparasol.untyped.ast.resolved.UASTRFragmentShaderVisitorType;
import com.io7m.jparasol.untyped.ast.resolved.UASTRLocalLevelVisitorType;
import com.io7m.jparasol.untyped.ast.resolved.UASTRShaderName;
import com.io7m.jparasol.untyped.ast.resolved.UASTRShaderVisitorType;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTermName;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTermName.UASTRTermNameGlobal;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTermName.UASTRTermNameLocal;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTermNameVisitorType;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTermVisitorType;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTypeName;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTypeName.UASTRTypeNameBuiltIn;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTypeName.UASTRTypeNameGlobal;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTypeNameVisitorType;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTypeVisitorType;
import com.io7m.jparasol.untyped.ast.resolved.UASTRVertexShaderLocalVisitorType;
import com.io7m.jparasol.untyped.ast.resolved.UASTRVertexShaderVisitorType;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * The type checker.
 */

@SuppressWarnings("synthetic-access") @EqualityReference public final class TypeChecker
{
  /**
   * The types of local terms.
   */

  @EqualityReference private static final class LocalTypes
  {
    public static LocalTypes initial()
    {
      return new LocalTypes(null);
    }

    private final @Nullable LocalTypes    parent;
    private final Map<String, TValueType> terms;

    private LocalTypes(
      final @Nullable LocalTypes in_parent)
    {
      this.terms = new HashMap<String, TValueType>();
      this.parent = in_parent;
    }

    public void addTerm(
      final String name,
      final TValueType type)
    {
      assert this.terms.containsKey(name) == false;
      this.terms.put(name, type);
    }

    public TValueType getName(
      final String name)
    {
      if (this.parent == null) {
        assert this.terms.containsKey(name);
        final TValueType r = this.terms.get(name);
        assert r != null;
        return r;
      }

      if (this.terms.containsKey(name)) {
        final TValueType r = this.terms.get(name);
        assert r != null;
        return r;
      }

      assert this.parent != null;
      return this.parent.getName(name);
    }

    public LocalTypes withNew()
    {
      return new LocalTypes(this);
    }
  }

  /**
   * Expression type checker.
   */

  @EqualityReference private static final class TypeCheckerExpression implements
    UASTRExpressionVisitorType<TASTExpression, TASTDValueLocal, TypeCheckerError>
  {
    private final Map<ModulePathFlat, TASTDModule> checked_modules;
    private final Map<String, TASTDTerm>           checked_terms;
    private final Map<String, TASTDType>           checked_types;
    private @Nullable LocalTypes                   locals;
    private final LogUsableType                    log;
    private final UASTRDModule                     module;

    public TypeCheckerExpression(
      final UASTRDModule in_module,
      final Map<ModulePathFlat, TASTDModule> in_checked_modules,
      final Map<String, TASTDType> in_checked_types,
      final Map<String, TASTDTerm> in_checked_terms,
      final @Nullable LocalTypes in_locals,
      final LogUsableType in_log)
    {
      this.module = in_module;
      this.checked_modules = in_checked_modules;
      this.checked_types = in_checked_types;
      this.checked_terms = in_checked_terms;
      this.locals = in_locals;
      this.log = in_log;
    }

    @Override public TASTEApplication expressionVisitApplication(
      final List<TASTExpression> arguments,
      final UASTREApplication e)
      throws TypeCheckerError
    {
      final TType t =
        TypeChecker.lookupTermType(
          e.getName(),
          this.module,
          this.checked_modules,
          this.checked_terms,
          this.locals);

      if (t instanceof TFunction) {
        final TFunction ft = (TFunction) t;
        final List<TFunctionArgument> f_args = ft.getArguments();
        if (arguments.size() == f_args.size()) {
          for (int index = 0; index < arguments.size(); ++index) {
            final TFunctionArgument exp = f_args.get(index);
            final TASTExpression got = arguments.get(index);

            if (TTypeEquality.typesAreEqual(got.getType(), exp.getType()) == false) {
              throw TypeCheckerError.termExpressionApplicationBadTypes(
                e.getName(),
                f_args,
                arguments);
            }
          }

          return new TASTEApplication(
            TypeChecker.mapTermName(e.getName()),
            arguments,
            ft.getReturnType());
        }

        throw TypeCheckerError.termExpressionApplicationBadTypes(
          e.getName(),
          f_args,
          arguments);
      }

      throw TypeCheckerError.termExpressionApplicationNotFunctionType(
        e.getName(),
        t);
    }

    @Override public void expressionVisitApplicationPre(
      final UASTREApplication e)
      throws TypeCheckerError
    {
      // Nothing
    }

    @Override public TASTEBoolean expressionVisitBoolean(
      final UASTREBoolean e)
      throws TypeCheckerError
    {
      return new TASTEBoolean(e.getToken());
    }

    @Override public TASTEConditional expressionVisitConditional(
      final TASTExpression condition,
      final TASTExpression left,
      final TASTExpression right,
      final UASTREConditional e)
      throws TypeCheckerError
    {
      if (TTypeEquality.typesAreEqual(condition.getType(), TBoolean.get()) == false) {
        throw TypeCheckerError.typeConditionNotBoolean(e.getIf(), condition);
      }

      return new TASTEConditional(condition, left, right);
    }

    @Override public void expressionVisitConditionalConditionPost(
      final UASTREConditional e)
      throws TypeCheckerError
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalConditionPre(
      final UASTREConditional e)
      throws TypeCheckerError
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalLeftPost(
      final UASTREConditional e)
      throws TypeCheckerError
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalLeftPre(
      final UASTREConditional e)
      throws TypeCheckerError
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalRightPost(
      final UASTREConditional e)
      throws TypeCheckerError
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalRightPre(
      final UASTREConditional e)
      throws TypeCheckerError
    {
      // Nothing
    }

    @Override public TASTEInteger expressionVisitInteger(
      final UASTREInteger e)
      throws TypeCheckerError
    {
      return new TASTEInteger(e.getToken());
    }

    @Override public TASTELet expressionVisitLet(
      final List<TASTDValueLocal> bindings,
      final TASTExpression body,
      final UASTRELet e)
      throws TypeCheckerError
    {
      final LocalTypes l = this.locals;
      assert l != null;
      this.locals = l.parent;
      return new TASTELet(e.getToken(), bindings, body);
    }

    @Override public
      UASTRLocalLevelVisitorType<TASTDValueLocal, TypeCheckerError>
      expressionVisitLetPre(
        final UASTRELet e)
        throws TypeCheckerError
    {
      final LocalTypes l = this.locals;
      assert l != null;
      this.locals = l.withNew();

      return new TypeCheckerLocal(
        this.checked_modules,
        this.checked_terms,
        this.checked_types,
        this.locals,
        this.log,
        this.module);
    }

    @Override public TASTExpression expressionVisitMatrixColumnAccess(
      final @Nullable TASTExpression body,
      final UASTREMatrixColumnAccess e)
      throws TypeCheckerError
    {
      final TASTExpression b = NullCheck.notNull(body, "Body");

      if (b.getType() instanceof TMatrixType) {
        final TMatrixType mt = (TMatrixType) b.getType();
        final BigInteger index = e.getColumn().getValue();

        final BigInteger upper = BigInteger.valueOf(mt.getColumns());
        if ((index.compareTo(upper) < 0)
          && (index.compareTo(BigInteger.ZERO) >= 0)) {
          return new TASTExpression.TASTEMatrixColumnAccess(
            mt.getColumnType(),
            b,
            e.getColumn());
        }

        throw TypeCheckerError.termExpressionMatrixColumnAccessOutOfBounds(
          e.getColumn(),
          mt);
      }

      throw TypeCheckerError.termExpressionMatrixColumnAccessNotMatrix(
        b,
        e.getColumn());
    }

    @Override public void expressionVisitMatrixColumnAccessPre(
      final UASTREMatrixColumnAccess e)
      throws TypeCheckerError
    {
      // Nothing
    }

    @Override public TASTENew expressionVisitNew(
      final List<TASTExpression> arguments,
      final UASTRENew e)
      throws TypeCheckerError
    {
      final TType t =
        TypeChecker.lookupType(
          this.module,
          this.checked_modules,
          this.checked_types,
          e.getName());

      final List<TConstructor> constructors = t.getConstructors();
      if (constructors.isEmpty()) {
        throw TypeCheckerError.termExpressionNewNotConstructable(e.getName());
      }

      check_constructors:
      for (final TConstructor c : constructors) {
        final List<TValueType> c_args = c.getParameters();
        if (c_args.size() == arguments.size()) {
          for (int index = 0; index < c_args.size(); ++index) {
            final TValueType ct = c_args.get(index);
            assert ct != null;

            final TType at = arguments.get(index).getType();
            if (TTypeEquality.typesAreEqual(at, ct) == false) {
              continue check_constructors;
            }
          }
          return new TASTENew((TValueType) t, arguments);
        }
      }

      throw TypeCheckerError.termExpressionNewNoAppropriateConstructors(
        e.getName(),
        arguments,
        constructors);
    }

    @Override public void expressionVisitNewPre(
      final UASTRENew e)
      throws TypeCheckerError
    {
      // Nothing
    }

    @Override public TASTEReal expressionVisitReal(
      final UASTREReal e)
      throws TypeCheckerError
    {
      return new TASTEReal(e.getToken());
    }

    @Override public TASTERecord expressionVisitRecord(
      final UASTRERecord e)
      throws TypeCheckerError
    {
      final TType t =
        TypeChecker.lookupType(
          this.module,
          this.checked_modules,
          this.checked_types,
          e.getTypePath());

      if ((t instanceof TRecord) == false) {
        throw TypeCheckerError.termExpressionRecordNotRecordType(e
          .getTypePath());
      }

      final TRecord tr = (TRecord) t;
      final List<TRecordField> t_fields = tr.getFields();
      final Map<String, TRecordField> t_fields_map =
        new HashMap<String, TRecordField>();

      for (final TRecordField tf : t_fields) {
        assert t_fields_map.containsKey(tf.getName()) == false;
        t_fields_map.put(tf.getName(), tf);
      }

      final Map<String, TokenIdentifierLower> assigned =
        new HashMap<String, TokenIdentifierLower>();
      final List<TASTRecordFieldAssignment> typed_assigns =
        new ArrayList<TASTRecordFieldAssignment>();

      for (final UASTRRecordFieldAssignment f : e.getAssignments()) {
        final TokenIdentifierLower f_name = f.getName();
        if (t_fields_map.containsKey(f_name.getActual()) == false) {
          throw TypeCheckerError.termExpressionRecordUnknownField(f_name, tr);
        }

        final TASTExpression et =
          f.getExpression().expressionVisitableAccept(
            new TypeCheckerExpression(
              this.module,
              this.checked_modules,
              this.checked_types,
              this.checked_terms,
              this.locals,
              this.log));

        assert t_fields_map.containsKey(f_name.getActual());
        final TManifestType expected_type =
          t_fields_map.get(f_name.getActual()).getType();

        if (TTypeEquality.typesAreEqual(et.getType(), expected_type) == false) {
          throw TypeCheckerError.termExpressionRecordBadFieldType(
            f_name,
            expected_type,
            et.getType());
        }

        typed_assigns.add(new TASTRecordFieldAssignment(f_name, et));
        assigned.put(f_name.getActual(), f_name);
      }

      final List<TRecordField> unassigned = new ArrayList<TRecordField>();
      for (final String name : t_fields_map.keySet()) {
        if (assigned.containsKey(name) == false) {
          unassigned.add(t_fields_map.get(name));
        }
      }

      if (unassigned.size() > 0) {
        throw TypeCheckerError.termExpressionRecordFieldsUnassigned(
          e.getTypePath(),
          unassigned);
      }

      return new TASTERecord(tr, typed_assigns);
    }

    @Override public TASTERecordProjection expressionVisitRecordProjection(
      final TASTExpression body,
      final UASTRERecordProjection e)
      throws TypeCheckerError
    {
      if (body.getType() instanceof TRecord) {
        final TRecord tr = (TRecord) body.getType();

        for (final TRecordField f : tr.getFields()) {
          if (f.getName().equals(e.getField().getActual())) {
            return new TASTERecordProjection(f.getType(), body, e.getField());
          }
        }

        throw TypeCheckerError.termExpressionRecordProjectionNoSuchField(
          tr,
          e.getField());
      }

      throw TypeCheckerError.termExpressionRecordProjectionNotRecord(
        body,
        e.getField());
    }

    @Override public void expressionVisitRecordProjectionPre(
      final UASTRERecordProjection e)
      throws TypeCheckerError
    {
      // Nothing
    }

    @Override public TASTESwizzle expressionVisitSwizzle(
      final TASTExpression body,
      final UASTRESwizzle e)
      throws TypeCheckerError
    {
      final List<TokenIdentifierLower> ef = e.getFields();
      final TokenIdentifierLower r = ef.get(0);
      assert r != null;

      if (body.getType() instanceof TVectorType) {
        final TVectorType tv = (TVectorType) body.getType();
        final List<String> components = tv.getComponentNames();

        if (ef.size() > 4) {
          throw TypeCheckerError.termExpressionSwizzleTooManyFields(
            r,
            ef.size());
        }

        for (final TokenIdentifierLower f : ef) {
          if (components.contains(f.getActual()) == false) {
            throw TypeCheckerError.termExpressionSwizzleUnknownField(tv, f);
          }
        }

        final TManifestType actual = tv.getSwizzleType(ef.size());
        return new TASTESwizzle(actual, body, ef);
      }

      throw TypeCheckerError.termExpressionSwizzleNotVector(body, r);
    }

    @Override public void expressionVisitSwizzlePre(
      final UASTRESwizzle e)
      throws TypeCheckerError
    {
      // Nothing
    }

    @Override public TASTEVariable expressionVisitVariable(
      final UASTREVariable e)
      throws TypeCheckerError
    {
      final TType t =
        TypeChecker.lookupTermType(
          e.getName(),
          this.module,
          this.checked_modules,
          this.checked_terms,
          this.locals);

      return new TASTEVariable(t, TypeChecker.mapTermName(e.getName()));
    }
  }

  /**
   * Type checking of fragment shaders.
   */

  @EqualityReference private static final class TypeCheckerFragmentShader implements
    UASTRFragmentShaderVisitorType<TASTDShaderFragment, TASTDShaderFragmentInput, TASTDShaderFragmentParameter, TASTDShaderFragmentOutput, TASTDShaderFragmentLocal, TASTDShaderFragmentOutputAssignment, TypeCheckerError>
  {
    private final Map<ModulePathFlat, TASTDModule> checked_modules;
    private final Map<String, TASTDTerm>           checked_terms;
    private final Map<String, TASTDType>           checked_types;
    private final LocalTypes                       locals;
    private final LogUsableType                    log;
    private final UASTRDModule                     module;
    private final Map<String, TValueType>          outputs;

    public TypeCheckerFragmentShader(
      final UASTRDModule in_module,
      final Map<ModulePathFlat, TASTDModule> in_checked_modules,
      final Map<String, TASTDType> in_checked_types,
      final Map<String, TASTDTerm> in_checked_terms,
      final LogUsableType in_log)
    {
      this.module = in_module;
      this.checked_modules = in_checked_modules;
      this.checked_types = in_checked_types;
      this.checked_terms = in_checked_terms;
      this.log = in_log;
      this.locals = LocalTypes.initial();
      this.outputs = new HashMap<String, TValueType>();
    }

    @Override public TASTDShaderFragment fragmentShaderVisit(
      final List<TASTDShaderFragmentInput> r_inputs,
      final List<TASTDShaderFragmentParameter> r_parameters,
      final List<TASTDShaderFragmentOutput> r_outputs,
      final List<TASTDShaderFragmentLocal> r_locals,
      final List<TASTDShaderFragmentOutputAssignment> r_output_assignments,
      final UASTRDShaderFragment f)
      throws TypeCheckerError
    {
      return new TASTDShaderFragment(
        f.getName(),
        r_inputs,
        r_outputs,
        r_parameters,
        r_locals,
        r_output_assignments);
    }

    @Override public TASTDShaderFragmentInput fragmentShaderVisitInput(
      final UASTRDShaderFragmentInput i)
      throws TypeCheckerError
    {
      final UASTRTermNameLocal in = i.getName();
      final TASTTermNameLocal name =
        new TASTTermNameLocal(in.getOriginal(), in.getCurrent());

      final TType t =
        TypeChecker.lookupType(
          this.module,
          this.checked_modules,
          this.checked_types,
          i.getType());

      if (t instanceof TRecord) {
        throw TypeCheckerError.shaderFragmentInputBadType(i);
      }

      this.locals.addTerm(in.getCurrent(), (TValueType) t);
      return new TASTDShaderFragmentInput(name, (TValueType) t);
    }

    @Override public
      UASTRFragmentShaderLocalVisitorType<TASTDShaderFragmentLocal, TypeCheckerError>
      fragmentShaderVisitLocalsPre()
        throws TypeCheckerError
    {
      return new TypeCheckerFragmentShaderLocal(
        this.module,
        this.checked_modules,
        this.checked_types,
        this.checked_terms,
        this.locals,
        this.log);
    }

    @Override public
      TASTDShaderFragmentOutputAssignment
      fragmentShaderVisitOutputAssignment(
        final UASTRDShaderFragmentOutputAssignment a)
        throws TypeCheckerError
    {
      final TType type =
        TypeChecker.lookupTermType(
          a.getVariable().getName(),
          this.module,
          this.checked_modules,
          this.checked_terms,
          this.locals);

      final TokenIdentifierLower a_name = a.getName();
      assert this.outputs.containsKey(a_name.getActual());
      final TValueType out_type = this.outputs.get(a_name.getActual());
      assert out_type != null;

      if (TTypeEquality.typesAreEqual(out_type, type) == false) {
        throw TypeCheckerError
          .shaderAssignmentBadType(a_name, out_type, type);
      }

      final TASTTermName name =
        TypeChecker.mapTermName(a.getVariable().getName());
      final TASTEVariable variable = new TASTEVariable(type, name);
      return new TASTDShaderFragmentOutputAssignment(a_name, variable);
    }

    @Override public
      UASTRFragmentShaderOutputVisitorType<TASTDShaderFragmentOutput, TypeCheckerError>
      fragmentShaderVisitOutputsPre()
        throws TypeCheckerError
    {
      return new UASTRFragmentShaderOutputVisitorType<TASTDShaderFragmentOutput, TypeCheckerError>() {
        @Override public
          TASTDShaderFragmentOutput
          fragmentShaderVisitOutputData(
            final UASTRDShaderFragmentOutputData d)
            throws TypeCheckerError
        {
          final TType t =
            TypeChecker.lookupType(
              TypeCheckerFragmentShader.this.module,
              TypeCheckerFragmentShader.this.checked_modules,
              TypeCheckerFragmentShader.this.checked_types,
              d.getType());

          if (t instanceof TRecord) {
            throw TypeCheckerError.shaderFragmentOutputBadType(d);
          }

          TypeCheckerFragmentShader.this.outputs.put(
            d.getName().getActual(),
            (TValueType) t);
          return new TASTDShaderFragmentOutputData(
            d.getName(),
            (TValueType) t,
            d.getIndex());
        }

        @Override public
          TASTDShaderFragmentOutput
          fragmentShaderVisitOutputDepth(
            final UASTRDShaderFragmentOutputDepth d)
            throws TypeCheckerError
        {
          final TType t =
            TypeChecker.lookupType(
              TypeCheckerFragmentShader.this.module,
              TypeCheckerFragmentShader.this.checked_modules,
              TypeCheckerFragmentShader.this.checked_types,
              d.getType());

          if ((t instanceof TFloat) == false) {
            throw TypeCheckerError.shaderFragmentOutputDepthWrongType(d);
          }

          TypeCheckerFragmentShader.this.outputs.put(
            d.getName().getActual(),
            (TValueType) t);
          return new TASTDShaderFragmentOutputDepth(d.getName());
        }
      };
    }

    @Override public
      TASTDShaderFragmentParameter
      fragmentShaderVisitParameter(
        final UASTRDShaderFragmentParameter p)
        throws TypeCheckerError
    {
      final UASTRTermNameLocal in = p.getName();
      final TASTTermNameLocal name =
        new TASTTermNameLocal(in.getOriginal(), in.getCurrent());

      final TType t =
        TypeChecker.lookupType(
          this.module,
          this.checked_modules,
          this.checked_types,
          p.getType());

      this.locals.addTerm(in.getCurrent(), (TValueType) t);
      return new TASTDShaderFragmentParameter(name, (TValueType) t);
    }
  }

  /**
   * Type checking of fragment shader locals.
   */

  @EqualityReference private static final class TypeCheckerFragmentShaderLocal implements
    UASTRFragmentShaderLocalVisitorType<TASTDShaderFragmentLocal, TypeCheckerError>
  {
    private final Map<ModulePathFlat, TASTDModule> checked_modules;
    private final Map<String, TASTDTerm>           checked_terms;
    private final Map<String, TASTDType>           checked_types;
    private final LocalTypes                       locals;
    private final LogUsableType                    log;
    private final UASTRDModule                     module;

    public TypeCheckerFragmentShaderLocal(
      final UASTRDModule in_module,
      final Map<ModulePathFlat, TASTDModule> in_checked_modules,
      final Map<String, TASTDType> in_checked_types,
      final Map<String, TASTDTerm> in_checked_terms,
      final LocalTypes in_locals,
      final LogUsableType in_log)
    {
      this.module = in_module;
      this.checked_modules = in_checked_modules;
      this.checked_types = in_checked_types;
      this.checked_terms = in_checked_terms;
      this.locals = in_locals;
      this.log = in_log;
    }

    @Override public
      TASTDShaderFragmentLocalDiscard
      fragmentShaderVisitLocalDiscard(
        final UASTRDShaderFragmentLocalDiscard d)
        throws TypeCheckerError
    {
      final TASTExpression e =
        d.getExpression().expressionVisitableAccept(
          new TypeCheckerExpression(
            this.module,
            this.checked_modules,
            this.checked_types,
            this.checked_terms,
            this.locals,
            this.log));

      if (TTypeEquality.typesAreEqual(e.getType(), TBoolean.get())) {
        return new TASTDShaderFragmentLocalDiscard(d.getDiscard(), e);
      }

      throw TypeCheckerError.shaderDiscardNotBoolean(
        d.getDiscard(),
        e.getType());
    }

    @Override public
      TASTDShaderFragmentLocalValue
      fragmentShaderVisitLocalValue(
        final UASTRDShaderFragmentLocalValue v)
        throws TypeCheckerError
    {
      final TypeCheckerLocal lc =
        new TypeCheckerLocal(
          this.checked_modules,
          this.checked_terms,
          this.checked_types,
          this.locals,
          this.log,
          this.module);
      final TASTDValueLocal vr = lc.localVisitValueLocal(v.getValue());
      return new TASTDShaderFragmentLocalValue(vr);
    }
  }

  @EqualityReference private static final class TypeCheckerLocal implements
    UASTRLocalLevelVisitorType<TASTDValueLocal, TypeCheckerError>
  {
    private final Map<ModulePathFlat, TASTDModule> checked_modules;
    private final Map<String, TASTDTerm>           checked_terms;
    private final Map<String, TASTDType>           checked_types;
    private final LocalTypes                       locals;
    private final LogUsableType                    log;
    private final UASTRDModule                     module;

    public TypeCheckerLocal(
      final Map<ModulePathFlat, TASTDModule> in_checked_modules,
      final Map<String, TASTDTerm> in_checked_terms,
      final Map<String, TASTDType> in_checked_types,
      final LocalTypes in_locals,
      final LogUsableType in_log,
      final UASTRDModule in_module)
    {
      this.checked_modules = in_checked_modules;
      this.checked_terms = in_checked_terms;
      this.checked_types = in_checked_types;
      this.locals = in_locals;
      this.log = in_log;
      this.module = in_module;
    }

    @Override public TASTDValueLocal localVisitValueLocal(
      final UASTRDValueLocal v)
      throws TypeCheckerError
    {
      final TASTExpression e =
        v.getExpression().expressionVisitableAccept(
          new TypeCheckerExpression(
            this.module,
            this.checked_modules,
            this.checked_types,
            this.checked_terms,
            this.locals,
            this.log));

      final UASTRTermNameLocal vn = v.getName();

      v.getAscription().mapPartial(
        new PartialFunctionType<UASTRTypeName, Unit, TypeCheckerError>() {
          @Override public Unit call(
            final UASTRTypeName t)
            throws TypeCheckerError
          {
            final TType r =
              TypeChecker.lookupType(
                TypeCheckerLocal.this.module,
                TypeCheckerLocal.this.checked_modules,
                TypeCheckerLocal.this.checked_types,
                t);

            if (TTypeEquality.typesAreEqual(e.getType(), r) == false) {
              throw TypeCheckerError.termValueExpressionAscriptionMismatch(
                vn.getOriginal(),
                r,
                e.getType());
            }
            return Unit.unit();
          }
        });

      this.locals.addTerm(v.getName().getCurrent(), (TValueType) e.getType());

      final TASTTermNameLocal new_name =
        new TASTTermNameLocal(vn.getOriginal(), vn.getCurrent());
      return new TASTDValueLocal(new_name, e);
    }
  }

  @EqualityReference private static final class TypeCheckerModule
  {
    private final Map<ModulePathFlat, TASTDModule> checked_modules;
    private final LogUsableType                    log;
    private final UASTRDModule                     module;

    public TypeCheckerModule(
      final UASTRDModule in_module,
      final Map<ModulePathFlat, TASTDModule> in_checked_modules,
      final LogUsableType in_log)
    {
      this.module = in_module;
      this.checked_modules = in_checked_modules;
      this.log = in_log.with("module-checker");

      if (in_log.wouldLog(LogLevel.LOG_DEBUG)) {
        final StringBuilder m = new StringBuilder();
        m.append("Checking ");
        m.append(in_module.getFlat().getActual());
        final String r = m.toString();
        assert r != null;
        in_log.debug(r);
      }
    }

    /**
     * Type-check the given module
     *
     * @return A typed module
     * @throws TypeCheckerError
     *           If a type error occurs
     */

    public TASTDModule check()
      throws TypeCheckerError
    {
      final Map<String, TASTDType> checked_types =
        new HashMap<String, TASTDType>();
      final Map<String, TASTDTerm> checked_terms =
        new HashMap<String, TASTDTerm>();
      final Map<String, TASTDShader> checked_shaders =
        new HashMap<String, TASTDShader>();

      /**
       * Terms, types, and shaders are checked in (reverse) topological order.
       * That is, for each term/type/shader t checked, all of the dependencies
       * of t have already been checked and are available during checking of
       * t.
       */

      /**
       * Check type declarations.
       */

      final Map<String, UASTRDType> types = this.module.getTypes();
      final List<String> types_topo = this.module.getTypeTopology();
      this.checkTypeDeclarations(checked_types, types, types_topo);
      assert checked_types.size() == types_topo.size();
      assert checked_types.size() == types.size();

      /**
       * Check term declarations.
       */

      final Map<String, UASTRDTerm> terms = this.module.getTerms();
      final List<String> terms_topo = this.module.getTermTopology();
      this.checkTermDeclarations(
        checked_types,
        checked_terms,
        terms,
        terms_topo);
      assert checked_terms.size() == terms_topo.size();
      assert checked_terms.size() == terms.size();

      /**
       * Check shader declarations.
       */

      final Map<String, UASTRDShader> shaders = this.module.getShaders();
      final List<String> shaders_topo = this.module.getShaderTopology();
      this.checkShaderDeclarations(
        checked_types,
        checked_terms,
        checked_shaders,
        shaders,
        shaders_topo);
      assert checked_shaders.size() == shaders_topo.size();
      assert checked_shaders.size() == shaders.size();

      /**
       * Assemble the rest of the module metadata.
       */

      final List<TASTDImport> imports = new ArrayList<TASTDImport>();
      for (final UASTRDImport i : this.module.getImports()) {
        imports.add(new TASTDImport(i.getPath(), i.getRename()));
      }

      final Map<ModulePathFlat, TASTDImport> imported_modules =
        new HashMap<ModulePathFlat, TASTDImport>();

      final Map<ModulePathFlat, UASTRDImport> oim =
        this.module.getImportedModules();
      for (final ModulePathFlat p : oim.keySet()) {
        final UASTRDImport i = oim.get(p);
        imported_modules.put(p, new TASTDImport(i.getPath(), i.getRename()));
      }

      final Map<String, TASTDImport> imported_names =
        new HashMap<String, TASTDImport>();

      final Map<String, UASTRDImport> oin = this.module.getImportedNames();
      for (final String p : oin.keySet()) {
        final UASTRDImport i = oin.get(p);
        imported_names.put(p, new TASTDImport(i.getPath(), i.getRename()));
      }

      final Map<String, TASTDImport> imported_renames =
        new HashMap<String, TASTDImport>();

      final Map<String, UASTRDImport> oir = this.module.getImportedRenames();
      for (final String p : oir.keySet()) {
        final UASTRDImport i = oir.get(p);
        imported_renames.put(p, new TASTDImport(i.getPath(), i.getRename()));
      }

      assert imports.size() == this.module.getImports().size();
      assert imported_modules.size() == oim.size();
      assert imported_names.size() == oin.size();
      assert imported_renames.size() == oir.size();

      return new TASTDModule(
        this.module.getPath(),
        imports,
        imported_modules,
        imported_names,
        imported_renames,
        checked_terms,
        terms_topo,
        checked_types,
        types_topo,
        checked_shaders,
        shaders_topo);
    }

    private void checkShaderDeclarations(
      final Map<String, TASTDType> checked_types,
      final Map<String, TASTDTerm> checked_terms,
      final Map<String, TASTDShader> checked_shaders,
      final Map<String, UASTRDShader> shaders,
      final List<String> shaders_topo)
      throws TypeCheckerError
    {
      if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
        final StringBuilder m = new StringBuilder();
        m.append(shaders_topo.size());
        m.append(" shaders in topology");
        final String r = m.toString();
        assert r != null;
        this.log.debug(r);
      }

      for (int index = shaders_topo.size() - 1; index >= 0; --index) {
        final String name = shaders_topo.get(index);
        final UASTRDShader shader = shaders.get(name);
        final TASTDShader checked =
          shader.shaderVisitableAccept(new TypeCheckerShaderDeclaration(
            this.module,
            this.checked_modules,
            checked_types,
            checked_terms,
            checked_shaders,
            this.log));

        assert checked_shaders.containsKey(name) == false;
        checked_shaders.put(name, checked);
      }
    }

    private void checkTermDeclarations(
      final Map<String, TASTDType> checked_types,
      final Map<String, TASTDTerm> checked_terms,
      final Map<String, UASTRDTerm> terms,
      final List<String> terms_topo)
      throws TypeCheckerError
    {
      if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
        final StringBuilder m = new StringBuilder();
        m.append(terms_topo.size());
        m.append(" terms in topology");
        final String r = m.toString();
        assert r != null;
        this.log.debug(r);
      }

      for (int index = terms_topo.size() - 1; index >= 0; --index) {
        final String name = terms_topo.get(index);
        final UASTRDTerm term = terms.get(name);
        final TASTDTerm checked =
          term.termVisitableAccept(new TypeCheckerTermDeclaration(
            this.module,
            this.checked_modules,
            checked_types,
            checked_terms,
            this.log));

        assert checked_terms.containsKey(name) == false;
        checked_terms.put(name, checked);
      }
    }

    private void checkTypeDeclarations(
      final Map<String, TASTDType> checked_types,
      final Map<String, UASTRDType> types,
      final List<String> types_topo)
      throws TypeCheckerError
    {
      if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
        final StringBuilder m = new StringBuilder();
        m.append(types_topo.size());
        m.append(" types in topology");
        final String r = m.toString();
        assert r != null;
        this.log.debug(r);
      }

      for (int index = types_topo.size() - 1; index >= 0; --index) {
        final String name = types_topo.get(index);
        final UASTRDType type = types.get(name);
        final TASTDType checked =
          type.typeVisitableAccept(new TypeCheckerTypeDeclaration(
            this.module,
            this.checked_modules,
            checked_types,
            this.log));

        assert checked_types.containsKey(name) == false;
        checked_types.put(name, checked);
      }
    }
  }

  @EqualityReference private static final class TypeCheckerShaderDeclaration implements
    UASTRShaderVisitorType<TASTDShader, TypeCheckerError>
  {
    private static void checkShaderTypeCompatibility(
      final TokenIdentifierLower program,
      final TASTDShaderVertex vs,
      final TASTDShaderFragment fs)
      throws TypeCheckerError
    {
      boolean compatible = true;
      final Set<String> assigned = new HashSet<String>();
      final Map<String, TValueType> wrong_types =
        new HashMap<String, TValueType>();

      for (final TASTDShaderFragmentInput fi : fs.getInputs()) {
        for (final TASTDShaderVertexOutput vo : vs.getOutputs()) {
          final String fi_name = fi.getName().getCurrent();
          final String vo_name = vo.getName().getActual();
          if (fi_name.equals(vo_name)) {
            final TValueType fi_type = fi.getType();
            final TValueType vo_type = vo.getType();

            if (TTypeEquality.typesAreEqual(fi_type, vo_type) == false) {
              wrong_types.put(fi_name, vo.getType());
              compatible = false;
              break;
            }
            assigned.add(fi_name);
            break;
          }
        }
      }

      for (final TASTDShaderFragmentInput fi : fs.getInputs()) {
        final String fi_name = fi.getName().getCurrent();
        if (assigned.contains(fi_name) == false) {
          compatible = false;
        }
      }

      if (compatible == false) {
        throw TypeCheckerError.shadersNotCompatible(
          program,
          fs,
          assigned,
          wrong_types);
      }
    }

    private final Map<ModulePathFlat, TASTDModule> checked_modules;
    private final Map<String, TASTDShader>         checked_shaders;
    private final Map<String, TASTDTerm>           checked_terms;
    private final Map<String, TASTDType>           checked_types;
    private final LogUsableType                    log;
    private final UASTRDModule                     module;

    public TypeCheckerShaderDeclaration(
      final UASTRDModule in_module,
      final Map<ModulePathFlat, TASTDModule> in_checked_modules,
      final Map<String, TASTDType> in_checked_types,
      final Map<String, TASTDTerm> in_checked_terms,
      final Map<String, TASTDShader> in_checked_shaders,
      final LogUsableType in_log)
    {
      this.module = in_module;
      this.checked_modules = in_checked_modules;
      this.checked_types = in_checked_types;
      this.checked_terms = in_checked_terms;
      this.checked_shaders = in_checked_shaders;
      this.log = in_log;
    }

    @Override public TASTDShaderFragment moduleVisitFragmentShader(
      final UASTRDShaderFragment f)
      throws TypeCheckerError
    {
      return f.fragmentShaderVisitableAccept(new TypeCheckerFragmentShader(
        this.module,
        this.checked_modules,
        this.checked_types,
        this.checked_terms,
        this.log));
    }

    @Override public TASTDShaderProgram moduleVisitProgramShader(
      final UASTRDShaderProgram p)
      throws TypeCheckerError
    {
      final TASTDShader vs =
        TypeChecker.lookupShader(
          this.module,
          this.checked_modules,
          this.checked_shaders,
          p.getVertexShader());

      if (vs instanceof TASTDShaderVertex) {
        final TASTDShader fs =
          TypeChecker.lookupShader(
            this.module,
            this.checked_modules,
            this.checked_shaders,
            p.getFragmentShader());
        if (fs instanceof TASTDShaderFragment) {
          TypeCheckerShaderDeclaration.checkShaderTypeCompatibility(
            p.getName(),
            (TASTDShaderVertex) vs,
            (TASTDShaderFragment) fs);
          return new TASTDShaderProgram(
            p.getName(),
            TypeChecker.mapShaderName(p.getVertexShader()),
            TypeChecker.mapShaderName(p.getFragmentShader()));
        }
        throw TypeCheckerError.shaderNotFragment(p.getFragmentShader(), fs);
      }
      throw TypeCheckerError.shaderNotVertex(p.getVertexShader(), vs);
    }

    @Override public TASTDShaderVertex moduleVisitVertexShader(
      final UASTRDShaderVertex v)
      throws TypeCheckerError
    {
      return v.vertexShaderVisitableAccept(new TypeCheckerVertexShader(
        this.module,
        this.checked_modules,
        this.checked_types,
        this.checked_terms,
        this.log));
    }
  }

  @EqualityReference private static final class TypeCheckerTermDeclaration implements
    UASTRTermVisitorType<TASTDTerm, TypeCheckerError>
  {
    private final Map<ModulePathFlat, TASTDModule> checked_modules;
    private final Map<String, TASTDTerm>           checked_terms;
    private final Map<String, TASTDType>           checked_types;
    private final LogUsableType                    log;
    private final UASTRDModule                     module;

    public TypeCheckerTermDeclaration(
      final UASTRDModule in_module,
      final Map<ModulePathFlat, TASTDModule> in_checked_modules,
      final Map<String, TASTDType> in_checked_types,
      final Map<String, TASTDTerm> in_checked_terms,
      final LogUsableType in_log)
    {
      this.module = in_module;
      this.checked_modules = in_checked_modules;
      this.checked_types = in_checked_types;
      this.checked_terms = in_checked_terms;
      this.log = in_log;
    }

    @Override public TASTDFunctionDefined termVisitFunctionDefined(
      final UASTRDFunctionDefined f)
      throws TypeCheckerError
    {
      final LocalTypes locals = LocalTypes.initial();

      final List<TASTDFunctionArgument> arguments =
        new ArrayList<TASTDFunctionArgument>();
      final List<TFunctionArgument> f_arguments =
        new ArrayList<TFunctionArgument>();

      for (final UASTRDFunctionArgument a : f.getArguments()) {
        final UASTRTermNameLocal name = a.getName();
        final TType type =
          TypeChecker.lookupType(
            this.module,
            this.checked_modules,
            this.checked_types,
            a.getType());

        assert type instanceof TValueType;
        final TASTTermNameLocal tname =
          new TASTTermNameLocal(name.getOriginal(), name.getCurrent());
        arguments.add(new TASTDFunctionArgument(tname, (TValueType) type));

        locals.addTerm(name.getCurrent(), (TValueType) type);

        f_arguments.add(new TFunctionArgument(
          name.getCurrent(),
          (TValueType) type));
      }

      final TType treturn =
        TypeChecker.lookupType(
          this.module,
          this.checked_modules,
          this.checked_types,
          f.getReturnType());

      final TASTExpression body =
        f.getBody().expressionVisitableAccept(
          new TypeCheckerExpression(
            this.module,
            this.checked_modules,
            this.checked_types,
            this.checked_terms,
            locals,
            this.log));

      if (TTypeEquality.typesAreEqual(body.getType(), treturn)) {
        final TFunction ft = new TFunction(f_arguments, (TValueType) treturn);
        final TASTDFunctionDefined fd =
          new TASTDFunctionDefined(f.getName(), arguments, body, ft);
        return fd;
      }

      throw TypeCheckerError.termFunctionBodyReturnMismatch(
        f.getName(),
        treturn,
        body.getType());
    }

    @Override public TASTDFunctionExternal termVisitFunctionExternal(
      final UASTRDFunctionExternal f)
      throws TypeCheckerError
    {
      final LocalTypes locals = LocalTypes.initial();

      final List<TASTDFunctionArgument> arguments =
        new ArrayList<TASTDFunctionArgument>();
      final List<TFunctionArgument> f_arguments =
        new ArrayList<TFunctionArgument>();

      for (final UASTRDFunctionArgument a : f.getArguments()) {
        final UASTRTermNameLocal name = a.getName();
        final TType type =
          TypeChecker.lookupType(
            this.module,
            this.checked_modules,
            this.checked_types,
            a.getType());

        assert type instanceof TValueType;
        final TASTTermNameLocal tname =
          new TASTTermNameLocal(name.getOriginal(), name.getCurrent());
        arguments.add(new TASTDFunctionArgument(tname, (TValueType) type));

        locals.addTerm(name.getCurrent(), (TValueType) type);

        f_arguments.add(new TFunctionArgument(
          name.getCurrent(),
          (TValueType) type));
      }

      final TType treturn =
        TypeChecker.lookupType(
          this.module,
          this.checked_modules,
          this.checked_types,
          f.getReturnType());

      final TFunction tf = new TFunction(f_arguments, (TValueType) treturn);
      final UASTRDExternal orig_ext = f.getExternal();

      final OptionType<UASTRExpression> orig_emulation =
        orig_ext.getEmulation();
      final OptionType<TASTExpression> emulation =
        orig_emulation
          .mapPartial(new PartialFunctionType<UASTRExpression, TASTExpression, TypeCheckerError>() {
            @Override public TASTExpression call(
              final UASTRExpression e)
              throws TypeCheckerError
            {
              return e.expressionVisitableAccept(new TypeCheckerExpression(
                TypeCheckerTermDeclaration.this.module,
                TypeCheckerTermDeclaration.this.checked_modules,
                TypeCheckerTermDeclaration.this.checked_types,
                TypeCheckerTermDeclaration.this.checked_terms,
                locals,
                TypeCheckerTermDeclaration.this.log));
            }
          });

      final TASTDExternal ext =
        new TASTDExternal(
          orig_ext.getName(),
          orig_ext.isVertexShaderAllowed(),
          orig_ext.isFragmentShaderAllowed(),
          emulation,
          orig_ext.getSupportedES(),
          orig_ext.getSupportedFull());

      if (emulation.isSome()) {
        final Some<TASTExpression> some = (Some<TASTExpression>) emulation;
        final TType emu_type = some.get().getType();

        if (TTypeEquality.typesAreEqual(emu_type, tf.getReturnType())) {
          return new TASTDFunctionExternal(f.getName(), arguments, tf, ext);
        }

        throw TypeCheckerError.termFunctionBodyReturnMismatch(
          f.getName(),
          treturn,
          emu_type);
      }

      return new TASTDFunctionExternal(f.getName(), arguments, tf, ext);
    }

    @Override public TASTDValueDefined termVisitValueDefined(
      final UASTRDValueDefined v)
      throws TypeCheckerError
    {
      final LocalTypes locals = LocalTypes.initial();

      final TASTExpression e =
        v.getExpression().expressionVisitableAccept(
          new TypeCheckerExpression(
            this.module,
            this.checked_modules,
            this.checked_types,
            this.checked_terms,
            locals,
            this.log));

      v.getAscription().mapPartial(
        new PartialFunctionType<UASTRTypeName, Unit, TypeCheckerError>() {
          @Override public Unit call(
            final UASTRTypeName n)
            throws TypeCheckerError
          {
            final TType t =
              TypeChecker.lookupType(
                TypeCheckerTermDeclaration.this.module,
                TypeCheckerTermDeclaration.this.checked_modules,
                TypeCheckerTermDeclaration.this.checked_types,
                n);

            if (TTypeEquality.typesAreEqual(e.getType(), t) == false) {
              throw TypeCheckerError.termValueExpressionAscriptionMismatch(
                v.getName(),
                t,
                e.getType());
            }
            return Unit.unit();
          }
        });

      if (e.getType() instanceof TValueType) {
        return new TASTDValueDefined(v.getName(), e);
      }

      throw TypeCheckerError.termValueNotValueType(v.getName(), e.getType());
    }

    @Override public TASTDTerm termVisitValueExternal(
      final UASTRDValueExternal v)
      throws TypeCheckerError
    {
      final UASTRDExternal original_external = v.getExternal();
      final OptionType<TASTExpression> none = Option.none();
      final TASTDExternal external =
        new TASTDExternal(
          original_external.getName(),
          original_external.isVertexShaderAllowed(),
          original_external.isFragmentShaderAllowed(),
          none,
          original_external.getSupportedES(),
          original_external.getSupportedFull());

      final TValueType type =
        (TValueType) TypeChecker.lookupType(
          this.module,
          this.checked_modules,
          this.checked_types,
          v.getAscription());

      return new TASTDValueExternal(v.getName(), type, external);
    }
  }

  @EqualityReference private static final class TypeCheckerTypeDeclaration implements
    UASTRTypeVisitorType<TASTDType, TypeCheckerError>
  {
    private final Map<ModulePathFlat, TASTDModule> checked_modules;
    private final Map<String, TASTDType>           checked_types;
    private final LogUsableType                    log;
    private final UASTRDModule                     module;

    public TypeCheckerTypeDeclaration(
      final UASTRDModule in_module,
      final Map<ModulePathFlat, TASTDModule> in_checked_modules,
      final Map<String, TASTDType> in_checked_types,
      final LogUsableType in_log)
    {
      this.module = in_module;
      this.checked_modules = in_checked_modules;
      this.checked_types = in_checked_types;
      this.log = in_log;
    }

    @Override public TASTDTypeRecord typeVisitTypeRecord(
      final UASTRDTypeRecord r)
      throws TypeCheckerError
    {
      if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
        final StringBuilder m = new StringBuilder();
        m.append("Checking ");
        m.append(this.module.getFlat().getActual());
        m.append(".");
        m.append(r.getName().getActual());
        final String rs = m.toString();
        assert rs != null;
        this.log.debug(rs);
      }

      final List<TASTDTypeRecordField> fields =
        new ArrayList<TASTDTypeRecordField>();
      final List<TRecordField> t_fields = new ArrayList<TRecordField>();

      for (final UASTRDTypeRecordField f : r.getFields()) {
        final TType rt =
          TypeChecker.lookupType(
            this.module,
            this.checked_modules,
            this.checked_types,
            f.getType());

        if ((rt instanceof TManifestType) == false) {
          throw TypeCheckerError.typeRecordFieldNotManifest(f.getType(), rt);
        }

        final TokenIdentifierLower f_name = f.getName();
        final String fn = f_name.getActual();
        assert fn != null;
        t_fields.add(new TRecordField(fn, (TManifestType) rt));
        fields.add(new TASTDTypeRecordField(f_name, (TManifestType) rt));
      }

      final TokenIdentifierLower name = r.getName();

      final TTypeNameGlobal t_name =
        new TTypeNameGlobal(this.module.getPath(), name);
      final TRecord trecord = new TRecord(t_name, t_fields);
      final TASTDTypeRecord trecord_d =
        new TASTDTypeRecord(name, fields, trecord);
      return trecord_d;
    }
  }

  @EqualityReference private static final class TypeCheckerVertexShader implements
    UASTRVertexShaderVisitorType<TASTDShaderVertex, TASTDShaderVertexInput, TASTDShaderVertexParameter, TASTDShaderVertexOutput, TASTDShaderVertexLocalValue, TASTDShaderVertexOutputAssignment, TypeCheckerError>
  {
    private final Map<ModulePathFlat, TASTDModule> checked_modules;
    private final Map<String, TASTDTerm>           checked_terms;
    private final Map<String, TASTDType>           checked_types;
    private final LocalTypes                       locals;
    private final LogUsableType                    log;
    private final UASTRDModule                     module;
    private final Map<String, TValueType>          outputs;

    public TypeCheckerVertexShader(
      final UASTRDModule in_module,
      final Map<ModulePathFlat, TASTDModule> in_checked_modules,
      final Map<String, TASTDType> in_checked_types,
      final Map<String, TASTDTerm> in_checked_terms,
      final LogUsableType in_log)
    {
      this.module = in_module;
      this.checked_modules = in_checked_modules;
      this.checked_types = in_checked_types;
      this.checked_terms = in_checked_terms;
      this.log = in_log;
      this.locals = LocalTypes.initial();
      this.outputs = new HashMap<String, TValueType>();
    }

    @Override public TASTDShaderVertex vertexShaderVisit(
      final List<TASTDShaderVertexInput> r_inputs,
      final List<TASTDShaderVertexParameter> r_parameters,
      final List<TASTDShaderVertexOutput> r_outputs,
      final List<TASTDShaderVertexLocalValue> r_values,
      final List<TASTDShaderVertexOutputAssignment> r_output_assignments,
      final UASTRDShaderVertex v)
      throws TypeCheckerError
    {
      return new TASTDShaderVertex(
        v.getName(),
        r_inputs,
        r_outputs,
        r_parameters,
        r_values,
        r_output_assignments);
    }

    @Override public TASTDShaderVertexInput vertexShaderVisitInput(
      final UASTRDShaderVertexInput i)
      throws TypeCheckerError
    {
      final UASTRTermNameLocal in = i.getName();
      final TASTTermNameLocal name =
        new TASTTermNameLocal(in.getOriginal(), in.getCurrent());

      final TType t =
        TypeChecker.lookupType(
          this.module,
          this.checked_modules,
          this.checked_types,
          i.getType());

      if (t instanceof TRecord) {
        throw TypeCheckerError.shaderVertexInputBadType(i);
      }

      this.locals.addTerm(in.getCurrent(), (TValueType) t);
      return new TASTDShaderVertexInput(name, (TValueType) t);
    }

    @Override public
      UASTRVertexShaderLocalVisitorType<TASTDShaderVertexLocalValue, TypeCheckerError>
      vertexShaderVisitLocalsPre()
        throws TypeCheckerError
    {
      return new TypeCheckerVertexShaderLocal(
        this.module,
        this.checked_modules,
        this.checked_types,
        this.checked_terms,
        this.locals,
        this.log);
    }

    @Override public TASTDShaderVertexOutput vertexShaderVisitOutput(
      final UASTRDShaderVertexOutput o)
      throws TypeCheckerError
    {
      final TType t =
        TypeChecker.lookupType(
          this.module,
          this.checked_modules,
          this.checked_types,
          o.getType());

      if (o.isMain()) {
        if (TTypeEquality.typesAreEqual(t, TVector4F.get()) == false) {
          throw TypeCheckerError.shaderVertexMainOutputBadType(o, t);
        }
      }

      if (t instanceof TRecord) {
        throw TypeCheckerError.shaderVertexOutputBadType(o);
      }

      this.outputs.put(o.getName().getActual(), (TValueType) t);
      return new TASTDShaderVertexOutput(
        o.getName(),
        (TValueType) t,
        o.isMain());
    }

    @Override public
      TASTDShaderVertexOutputAssignment
      vertexShaderVisitOutputAssignment(
        final UASTRDShaderVertexOutputAssignment a)
        throws TypeCheckerError
    {
      final TType type =
        TypeChecker.lookupTermType(
          a.getVariable().getName(),
          this.module,
          this.checked_modules,
          this.checked_terms,
          this.locals);

      final TokenIdentifierLower a_name = a.getName();
      assert this.outputs.containsKey(a_name.getActual());
      final TValueType out_type = this.outputs.get(a_name.getActual());
      assert out_type != null;

      if (TTypeEquality.typesAreEqual(out_type, type) == false) {
        throw TypeCheckerError
          .shaderAssignmentBadType(a_name, out_type, type);
      }

      final TASTTermName name =
        TypeChecker.mapTermName(a.getVariable().getName());
      final TASTEVariable variable = new TASTEVariable(type, name);
      return new TASTDShaderVertexOutputAssignment(a_name, variable);
    }

    @Override public TASTDShaderVertexParameter vertexShaderVisitParameter(
      final UASTRDShaderVertexParameter p)
      throws TypeCheckerError
    {
      final UASTRTermNameLocal in = p.getName();
      final TASTTermNameLocal name =
        new TASTTermNameLocal(in.getOriginal(), in.getCurrent());

      final TType t =
        TypeChecker.lookupType(
          this.module,
          this.checked_modules,
          this.checked_types,
          p.getType());

      this.locals.addTerm(in.getCurrent(), (TValueType) t);
      return new TASTDShaderVertexParameter(name, (TValueType) t);
    }
  }

  @EqualityReference private static final class TypeCheckerVertexShaderLocal implements
    UASTRVertexShaderLocalVisitorType<TASTDShaderVertexLocalValue, TypeCheckerError>
  {
    private final Map<ModulePathFlat, TASTDModule> checked_modules;
    private final Map<String, TASTDTerm>           checked_terms;
    private final Map<String, TASTDType>           checked_types;
    private final LocalTypes                       locals;
    private final LogUsableType                    log;
    private final UASTRDModule                     module;

    public TypeCheckerVertexShaderLocal(
      final UASTRDModule in_module,
      final Map<ModulePathFlat, TASTDModule> in_checked_modules,
      final Map<String, TASTDType> in_checked_types,
      final Map<String, TASTDTerm> in_checked_terms,
      final LocalTypes in_locals,
      final LogUsableType in_log)
    {
      this.module = in_module;
      this.checked_modules = in_checked_modules;
      this.checked_types = in_checked_types;
      this.checked_terms = in_checked_terms;
      this.locals = in_locals;
      this.log = in_log;
    }

    @Override public TASTDShaderVertexLocalValue vertexShaderVisitLocalValue(
      final UASTRDShaderVertexLocalValue v)
      throws TypeCheckerError
    {
      final TypeCheckerLocal lc =
        new TypeCheckerLocal(
          this.checked_modules,
          this.checked_terms,
          this.checked_types,
          this.locals,
          this.log,
          this.module);
      final TASTDValueLocal vr = lc.localVisitValueLocal(v.getValue());
      return new TASTDShaderVertexLocalValue(vr);
    }
  }

  private static TASTDShader lookupShader(
    final UASTRDModule module,
    final Map<ModulePathFlat, TASTDModule> checked_modules,
    final Map<String, TASTDShader> checked_shaders,
    final UASTRShaderName name)
  {
    final TokenIdentifierLower sname = name.getName();
    if (name.getFlat().equals(module.getFlat())) {
      assert checked_shaders.containsKey(sname.getActual());
      final TASTDShader r = checked_shaders.get(sname.getActual());
      assert r != null;
      return r;
    }

    final TASTDModule m = checked_modules.get(name.getFlat());
    assert m.getShaders().containsKey(sname.getActual());
    final TASTDShader r = m.getShaders().get(sname.getActual());
    assert r != null;
    return r;
  }

  private static TType lookupTermType(
    final UASTRTermName name,
    final UASTRDModule module,
    final Map<ModulePathFlat, TASTDModule> checked_modules,
    final Map<String, TASTDTerm> checked_terms,
    final @Nullable LocalTypes locals)
  {
    return name
      .termNameVisitableAccept(new UASTRTermNameVisitorType<TType, UnreachableCodeException>() {
        @Override public TType termNameVisitGlobal(
          final UASTRTermNameGlobal t)
        {
          final TokenIdentifierLower t_name = t.getName();

          final ModulePathFlat flat = t.getFlat();
          if (flat.equals(module.getFlat())) {
            assert checked_terms.containsKey(t_name.getActual());
            final TASTDTerm tt = checked_terms.get(t_name.getActual());
            return tt.getType();
          }

          assert checked_modules.containsKey(flat);
          final TASTDModule m = checked_modules.get(flat);
          assert m.getTerms().containsKey(t_name.getActual());
          final TASTDTerm tt = m.getTerms().get(t_name.getActual());
          return tt.getType();
        }

        @Override public TType termNameVisitLocal(
          final UASTRTermNameLocal t)
        {
          assert locals != null;
          return locals.getName(t.getCurrent());
        }
      });
  }

  private static TType lookupType(
    final UASTRDModule module,
    final Map<ModulePathFlat, TASTDModule> checked_modules,
    final Map<String, TASTDType> checked_types,
    final UASTRTypeName type)
  {
    return type
      .typeNameVisitableAccept(new UASTRTypeNameVisitorType<TType, UnreachableCodeException>() {
        @Override public TType typeNameVisitBuiltIn(
          final UASTRTypeNameBuiltIn t)
        {
          final String actual = t.getName().getActual();
          final Map<TTypeNameBuiltIn, TType> bt = TType.getBaseTypesByName();
          final TTypeNameBuiltIn tbn = new TTypeNameBuiltIn(actual);
          final TType r = bt.get(tbn);
          assert r != null;
          return r;
        }

        @Override public TType typeNameVisitGlobal(
          final UASTRTypeNameGlobal t)
        {
          final ModulePath current_path = module.getPath();
          final ModulePathFlat current_flat =
            ModulePathFlat.fromModulePath(current_path);

          final ModulePathFlat target_path = t.getFlat();
          final String t_name = t.getName().getActual();

          if (target_path.equals(current_flat)) {
            assert checked_types.containsKey(t_name);
            final TASTDType ty = checked_types.get(t_name);
            return ty.getType();
          }

          assert checked_modules.containsKey(target_path);
          final TASTDModule m = checked_modules.get(target_path);

          final Map<String, TASTDType> m_types = m.getTypes();
          assert m_types.containsKey(t_name);
          return m_types.get(t_name).getType();
        }
      });
  }

  private static TASTShaderName mapShaderName(
    final UASTRShaderName name)
  {
    return new TASTShaderName(name.getPath(), name.getName());
  }

  private static TASTTermName mapTermName(
    final UASTRTermName n)
  {
    return n
      .termNameVisitableAccept(new UASTRTermNameVisitorType<TASTTermName, UnreachableCodeException>() {
        @Override public TASTTermNameGlobal termNameVisitGlobal(
          final UASTRTermNameGlobal t_name)
        {
          return new TASTTermNameGlobal(t_name.getPath(), t_name.getName());
        }

        @Override public TASTTermNameLocal termNameVisitLocal(
          final UASTRTermNameLocal t_name)
        {
          return new TASTTermNameLocal(t_name.getOriginal(), t_name
            .getCurrent());
        }
      });
  }

  /**
   * Construct a new type checker for the given AST.
   *
   * @param compilation
   *          The AST
   * @param log
   *          A log interface
   * @return A type checker
   */

  public static TypeChecker newTypeChecker(
    final UASTRCompilation compilation,
    final LogUsableType log)
  {
    return new TypeChecker(compilation, log);
  }

  private final UASTRCompilation compilation;
  private final LogUsableType    log;

  private TypeChecker(
    final UASTRCompilation in_compilation,
    final LogUsableType in_log)
  {
    this.log = in_log.with("type-checker");
    this.compilation = NullCheck.notNull(in_compilation, "Compilation");
  }

  /**
   * Check the given AST.
   *
   * @return A typed AST
   * @throws TypeCheckerError
   *           If a type error occurs
   */

  public TASTCompilation check()
    throws TypeCheckerError
  {
    final Map<ModulePathFlat, UASTRDModule> modules =
      this.compilation.getModules();

    final List<ModulePathFlat> topology =
      this.compilation.getModuleTopology();
    if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
      final StringBuilder m = new StringBuilder();
      m.append(topology.size());
      m.append(" modules in topology");
      final String r = m.toString();
      assert r != null;
      this.log.debug(r);
    }

    /**
     * Type check modules in (reverse) topological order.
     */

    final Map<ModulePathFlat, TASTDModule> checked_modules =
      new HashMap<ModulePathFlat, TASTDModule>();

    for (int index = topology.size() - 1; index >= 0; --index) {
      final ModulePathFlat path = topology.get(index);
      final UASTRDModule module = modules.get(path);
      assert module != null;
      final TypeCheckerModule mr =
        new TypeCheckerModule(module, checked_modules, this.log);
      final TASTDModule checked = mr.check();
      checked_modules.put(path, checked);
    }

    final TGraphs graphs = TGraphs.newGraphs(this.log);
    final GlobalGraph gg = graphs.check(checked_modules);

    return new TASTCompilation(
      this.compilation.getModuleTopology(),
      checked_modules,
      this.compilation.getPaths(),
      gg.getTermTermGraph().getGraph(),
      gg.getTermTypeGraph().getGraph(),
      gg.getTypeTypeGraph().getGraph(),
      gg.getTypeShader().getGraph(),
      gg.getTermShader().getGraph());
  }
}
