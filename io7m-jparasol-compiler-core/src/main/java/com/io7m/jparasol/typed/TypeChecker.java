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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jaux.functional.PartialFunction;
import com.io7m.jaux.functional.Unit;
import com.io7m.jlog.Level;
import com.io7m.jlog.Log;
import com.io7m.jparasol.ModulePath;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.NamesBuiltIn;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.typed.TType.TBoolean;
import com.io7m.jparasol.typed.TType.TConstructor;
import com.io7m.jparasol.typed.TType.TFunction;
import com.io7m.jparasol.typed.TType.TFunctionArgument;
import com.io7m.jparasol.typed.TType.TManifestType;
import com.io7m.jparasol.typed.TType.TRecord;
import com.io7m.jparasol.typed.TType.TRecordField;
import com.io7m.jparasol.typed.TType.TValueType;
import com.io7m.jparasol.typed.TType.TVectorType;
import com.io7m.jparasol.typed.ast.TASTCompilation;
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
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDExternal;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDFunctionArgument;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDFunctionDefined;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDFunctionExternal;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDImport;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDModule;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDShader;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDShaderFragment;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDShaderFragmentInput;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDShaderFragmentLocal;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDShaderFragmentLocalDiscard;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDShaderFragmentLocalValue;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDShaderFragmentOutput;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDShaderFragmentOutputAssignment;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDShaderFragmentParameter;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDShaderProgram;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDShaderVertex;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDShaderVertexInput;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDShaderVertexLocalValue;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDShaderVertexOutput;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDShaderVertexOutputAssignment;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDShaderVertexParameter;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDTerm;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDType;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDTypeRecord;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDTypeRecordField;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDValue;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDValueLocal;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDeclarationModuleLevel;
import com.io7m.jparasol.typed.ast.TASTShaderName;
import com.io7m.jparasol.typed.ast.TASTTermName;
import com.io7m.jparasol.typed.ast.TASTTermName.TASTTermNameBuiltIn;
import com.io7m.jparasol.typed.ast.TASTTermName.TASTTermNameGlobal;
import com.io7m.jparasol.typed.ast.TASTTermName.TASTTermNameLocal;
import com.io7m.jparasol.untyped.ast.resolved.UASTRCompilation;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDExternal;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDFunctionArgument;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDFunctionDefined;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDFunctionExternal;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDModule;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDShader;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDShaderFragment;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDShaderFragmentInput;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDShaderFragmentLocalDiscard;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDShaderFragmentLocalValue;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDShaderFragmentOutput;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDShaderFragmentOutputAssignment;
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
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDValue;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDValueLocal;
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
import com.io7m.jparasol.untyped.ast.resolved.UASTRExpressionVisitor;
import com.io7m.jparasol.untyped.ast.resolved.UASTRFragmentShaderLocalVisitor;
import com.io7m.jparasol.untyped.ast.resolved.UASTRFragmentShaderVisitor;
import com.io7m.jparasol.untyped.ast.resolved.UASTRLocalLevelVisitor;
import com.io7m.jparasol.untyped.ast.resolved.UASTRShaderName;
import com.io7m.jparasol.untyped.ast.resolved.UASTRShaderVisitor;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTermName;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTermName.UASTRTermNameBuiltIn;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTermName.UASTRTermNameGlobal;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTermName.UASTRTermNameLocal;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTermNameVisitor;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTermVisitor;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTypeName;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTypeName.UASTRTypeNameBuiltIn;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTypeName.UASTRTypeNameGlobal;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTypeNameVisitor;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTypeVisitor;
import com.io7m.jparasol.untyped.ast.resolved.UASTRVertexShaderLocalVisitor;
import com.io7m.jparasol.untyped.ast.resolved.UASTRVertexShaderVisitor;

public final class TypeChecker
{
  private static final class ExpressionChecker implements
    UASTRExpressionVisitor<TASTExpression, TASTDValueLocal, TypeCheckerError>
  {
    private final @Nonnull Map<String, TValueType>          builtins;
    private final @Nonnull Map<ModulePathFlat, TASTDModule> checked_modules;
    private final @Nonnull Map<String, TASTDTerm>           checked_terms;
    private final @Nonnull Map<String, TASTDType>           checked_types;
    private @Nonnull LocalTypes                             locals;
    private final @Nonnull Log                              log;
    private final @Nonnull UASTRDModule                     module;

    public ExpressionChecker(
      final @Nonnull UASTRDModule module,
      final @Nonnull Map<ModulePathFlat, TASTDModule> checked_modules,
      final @Nonnull Map<String, TASTDType> checked_types,
      final @Nonnull Map<String, TASTDTerm> checked_terms,
      final @Nonnull LocalTypes locals,
      final @Nonnull Map<String, TValueType> builtins,
      final @Nonnull Log log)
    {
      this.module = module;
      this.checked_modules = checked_modules;
      this.checked_types = checked_types;
      this.checked_terms = checked_terms;
      this.locals = locals;
      this.builtins = builtins;
      this.log = log;
    }

    @SuppressWarnings("synthetic-access") @Override public
      TASTEApplication
      expressionVisitApplication(
        final @Nonnull List<TASTExpression> arguments,
        final @Nonnull UASTREApplication e)
        throws TypeCheckerError,
          ConstraintError
    {
      final TType t =
        TypeChecker.lookupTermType(
          e.getName(),
          this.module,
          this.checked_modules,
          this.checked_terms,
          this.locals,
          this.builtins);

      if (t instanceof TFunction) {
        final TFunction ft = (TFunction) t;
        final List<TFunctionArgument> f_args = ft.getArguments();
        if (arguments.size() == f_args.size()) {
          for (int index = 0; index < arguments.size(); ++index) {
            final TFunctionArgument exp = f_args.get(index);
            final TASTExpression got = arguments.get(index);

            if (got.getType().equals(exp.getType()) == false) {
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
      final @Nonnull UASTREApplication e)
      throws TypeCheckerError,
        ConstraintError
    {
      // Nothing
    }

    @Override public TASTEBoolean expressionVisitBoolean(
      final @Nonnull UASTREBoolean e)
      throws TypeCheckerError,
        ConstraintError
    {
      return new TASTEBoolean(e.getToken());
    }

    @Override public TASTEConditional expressionVisitConditional(
      final @Nonnull TASTExpression condition,
      final @Nonnull TASTExpression left,
      final @Nonnull TASTExpression right,
      final @Nonnull UASTREConditional e)
      throws TypeCheckerError,
        ConstraintError
    {
      if (condition.getType().equals(TBoolean.get()) == false) {
        throw TypeCheckerError.typeConditionNotBoolean(e.getIf(), condition);
      }

      return new TASTEConditional(condition, left, right);
    }

    @Override public void expressionVisitConditionalConditionPost(
      final @Nonnull UASTREConditional e)
      throws TypeCheckerError,
        ConstraintError
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalConditionPre(
      final @Nonnull UASTREConditional e)
      throws TypeCheckerError,
        ConstraintError
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalLeftPost(
      final @Nonnull UASTREConditional e)
      throws TypeCheckerError,
        ConstraintError
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalLeftPre(
      final @Nonnull UASTREConditional e)
      throws TypeCheckerError,
        ConstraintError
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalRightPost(
      final @Nonnull UASTREConditional e)
      throws TypeCheckerError,
        ConstraintError
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalRightPre(
      final @Nonnull UASTREConditional e)
      throws TypeCheckerError,
        ConstraintError
    {
      // Nothing
    }

    @Override public TASTEInteger expressionVisitInteger(
      final @Nonnull UASTREInteger e)
      throws TypeCheckerError,
        ConstraintError
    {
      return new TASTEInteger(e.getToken());
    }

    @Override public TASTELet expressionVisitLet(
      final @Nonnull List<TASTDValueLocal> bindings,
      final @Nonnull TASTExpression body,
      final @Nonnull UASTRELet e)
      throws TypeCheckerError,
        ConstraintError
    {
      assert this.locals.parent != null;
      this.locals = this.locals.parent;

      return new TASTELet(e.getToken(), bindings, body);
    }

    @Override public
      UASTRLocalLevelVisitor<TASTDValueLocal, TypeCheckerError>
      expressionVisitLetPre(
        final @Nonnull UASTRELet e)
        throws TypeCheckerError,
          ConstraintError
    {
      this.locals = this.locals.withNew();

      return new LocalChecker(
        this.checked_modules,
        this.checked_terms,
        this.checked_types,
        this.locals,
        this.builtins,
        this.log,
        this.module);
    }

    @SuppressWarnings("synthetic-access") @Override public
      TASTENew
      expressionVisitNew(
        final @Nonnull List<TASTExpression> arguments,
        final @Nonnull UASTRENew e)
        throws TypeCheckerError,
          ConstraintError
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
            final TType at = arguments.get(index).getType();
            if (ct.equals(at) == false) {
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
      final @Nonnull UASTRENew e)
      throws TypeCheckerError,
        ConstraintError
    {
      // Nothing
    }

    @Override public TASTEReal expressionVisitReal(
      final @Nonnull UASTREReal e)
      throws TypeCheckerError,
        ConstraintError
    {
      return new TASTEReal(e.getToken());
    }

    @SuppressWarnings("synthetic-access") @Override public
      TASTERecord
      expressionVisitRecord(
        final @Nonnull UASTRERecord e)
        throws TypeCheckerError,
          ConstraintError
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
      final HashMap<String, TRecordField> t_fields_map =
        new HashMap<String, TRecordField>();

      for (final TRecordField tf : t_fields) {
        assert t_fields_map.containsKey(tf.getName()) == false;
        t_fields_map.put(tf.getName(), tf);
      }

      final HashMap<String, TokenIdentifierLower> assigned =
        new HashMap<String, TokenIdentifierLower>();
      final ArrayList<TASTRecordFieldAssignment> typed_assigns =
        new ArrayList<TASTRecordFieldAssignment>();

      for (final UASTRRecordFieldAssignment f : e.getAssignments()) {
        final TokenIdentifierLower f_name = f.getName();
        if (t_fields_map.containsKey(f_name.getActual()) == false) {
          throw TypeCheckerError.termExpressionRecordUnknownField(f_name, tr);
        }

        final TASTExpression et =
          f.getExpression().expressionVisitableAccept(
            new ExpressionChecker(
              this.module,
              this.checked_modules,
              this.checked_types,
              this.checked_terms,
              this.locals,
              this.builtins,
              this.log));

        assert t_fields_map.containsKey(f_name.getActual());
        final TManifestType expected_type =
          t_fields_map.get(f_name.getActual()).getType();

        if (expected_type.equals(et.getType()) == false) {
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
      final @Nonnull TASTExpression body,
      final @Nonnull UASTRERecordProjection e)
      throws TypeCheckerError,
        ConstraintError
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
      final @Nonnull UASTRERecordProjection e)
      throws TypeCheckerError,
        ConstraintError
    {
      // Nothing
    }

    @Override public TASTESwizzle expressionVisitSwizzle(
      final @Nonnull TASTExpression body,
      final @Nonnull UASTRESwizzle e)
      throws TypeCheckerError,
        ConstraintError
    {
      final List<TokenIdentifierLower> ef = e.getFields();
      if (body.getType() instanceof TVectorType) {
        final TVectorType tv = (TVectorType) body.getType();
        final List<String> components = tv.getComponentNames();

        if (ef.size() > 4) {
          throw TypeCheckerError.termExpressionSwizzleTooManyFields(
            ef.get(0),
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

      throw TypeCheckerError.termExpressionSwizzleNotVector(body, ef.get(0));
    }

    @Override public void expressionVisitSwizzlePre(
      final @Nonnull UASTRESwizzle e)
      throws TypeCheckerError,
        ConstraintError
    {
      // Nothing
    }

    @SuppressWarnings("synthetic-access") @Override public @Nonnull
      TASTEVariable
      expressionVisitVariable(
        final @Nonnull UASTREVariable e)
        throws TypeCheckerError,
          ConstraintError
    {
      final TType t =
        TypeChecker.lookupTermType(
          e.getName(),
          this.module,
          this.checked_modules,
          this.checked_terms,
          this.locals,
          this.builtins);

      return new TASTEVariable(t, TypeChecker.mapTermName(e.getName()));
    }
  }

  private static final class FragmentShaderChecker implements
    UASTRFragmentShaderVisitor<TASTDShaderFragment, TASTDShaderFragmentInput, TASTDShaderFragmentParameter, TASTDShaderFragmentOutput, TASTDShaderFragmentLocal, TASTDShaderFragmentOutputAssignment, TypeCheckerError>
  {
    private final @Nonnull Map<String, TValueType>          builtin_inputs;
    private final @Nonnull Map<String, TValueType>          builtin_outputs;
    private final @Nonnull Map<ModulePathFlat, TASTDModule> checked_modules;
    private final @Nonnull Map<String, TASTDTerm>           checked_terms;
    private final @Nonnull Map<String, TASTDType>           checked_types;
    private final @Nonnull LocalTypes                       locals;
    private final @Nonnull Log                              log;
    private final @Nonnull UASTRDModule                     module;
    private final @Nonnull Map<String, TValueType>          outputs;

    public FragmentShaderChecker(
      final @Nonnull UASTRDModule module,
      final @Nonnull Map<ModulePathFlat, TASTDModule> checked_modules,
      final @Nonnull Map<String, TASTDType> checked_types,
      final @Nonnull Map<String, TASTDTerm> checked_terms,
      final @Nonnull Log log)
    {
      this.module = module;
      this.checked_modules = checked_modules;
      this.checked_types = checked_types;
      this.checked_terms = checked_terms;
      this.log = log;
      this.builtin_inputs = NamesBuiltIn.FRAGMENT_SHADER_INPUTS;
      this.builtin_outputs = new HashMap<String, TValueType>();
      this.locals = LocalTypes.initial();
      this.outputs = new HashMap<String, TValueType>();
    }

    @Override public
      TASTDShaderFragment
      fragmentShaderVisit(
        final @Nonnull List<TASTDShaderFragmentInput> r_inputs,
        final @Nonnull List<TASTDShaderFragmentParameter> r_parameters,
        final @Nonnull List<TASTDShaderFragmentOutput> r_outputs,
        final @Nonnull List<TASTDShaderFragmentLocal> r_locals,
        final @Nonnull List<TASTDShaderFragmentOutputAssignment> r_output_assignments,
        final @Nonnull UASTRDShaderFragment f)
        throws TypeCheckerError,
          ConstraintError
    {
      return new TASTDShaderFragment(
        f.getName(),
        r_inputs,
        r_outputs,
        r_parameters,
        r_locals,
        r_output_assignments);
    }

    @SuppressWarnings("synthetic-access") @Override public
      TASTDShaderFragmentInput
      fragmentShaderVisitInput(
        final @Nonnull UASTRDShaderFragmentInput i)
        throws TypeCheckerError,
          ConstraintError
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

      this.locals.addTerm(in.getCurrent(), (TValueType) t);
      return new TASTDShaderFragmentInput(name, (TValueType) t);
    }

    @Override public
      UASTRFragmentShaderLocalVisitor<TASTDShaderFragmentLocal, TypeCheckerError>
      fragmentShaderVisitLocalsPre()
        throws TypeCheckerError,
          ConstraintError
    {
      return new FragmentShaderLocalChecker(
        this.module,
        this.checked_modules,
        this.checked_types,
        this.checked_terms,
        this.locals,
        this.builtin_inputs,
        this.log);
    }

    @SuppressWarnings("synthetic-access") @Override public
      TASTDShaderFragmentOutput
      fragmentShaderVisitOutput(
        final @Nonnull UASTRDShaderFragmentOutput o)
        throws TypeCheckerError,
          ConstraintError
    {
      final TType t =
        TypeChecker.lookupType(
          this.module,
          this.checked_modules,
          this.checked_types,
          o.getType());

      this.outputs.put(o.getName().getActual(), (TValueType) t);
      return new TASTDShaderFragmentOutput(
        o.getName(),
        (TValueType) t,
        o.getIndex());
    }

    @SuppressWarnings("synthetic-access") @Override public
      TASTDShaderFragmentOutputAssignment
      fragmentShaderVisitOutputAssignment(
        final @Nonnull UASTRDShaderFragmentOutputAssignment a)
        throws TypeCheckerError,
          ConstraintError
    {
      final TType type =
        TypeChecker.lookupTermType(
          a.getVariable().getName(),
          this.module,
          this.checked_modules,
          this.checked_terms,
          this.locals,
          this.builtin_inputs);

      final TokenIdentifierLower a_name = a.getName();
      final TValueType out_type;
      if (this.builtin_outputs.containsKey(a_name.getActual())) {
        out_type = this.builtin_outputs.get(a_name.getActual());
      } else {
        assert this.outputs.containsKey(a_name.getActual());
        out_type = this.outputs.get(a_name.getActual());
      }

      if (out_type.equals(type) == false) {
        throw TypeCheckerError
          .shaderAssignmentBadType(a_name, out_type, type);
      }

      final TASTTermName name =
        TypeChecker.mapTermName(a.getVariable().getName());
      final TASTEVariable variable = new TASTEVariable(type, name);
      return new TASTDShaderFragmentOutputAssignment(a_name, variable);
    }

    @SuppressWarnings("synthetic-access") @Override public
      TASTDShaderFragmentParameter
      fragmentShaderVisitParameter(
        final @Nonnull UASTRDShaderFragmentParameter p)
        throws TypeCheckerError,
          ConstraintError
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

  private static final class FragmentShaderLocalChecker implements
    UASTRFragmentShaderLocalVisitor<TASTDShaderFragmentLocal, TypeCheckerError>
  {
    private final @Nonnull Map<String, TValueType>          builtins;
    private final @Nonnull Map<ModulePathFlat, TASTDModule> checked_modules;
    private final @Nonnull Map<String, TASTDTerm>           checked_terms;
    private final @Nonnull Map<String, TASTDType>           checked_types;
    private final @Nonnull LocalTypes                       locals;
    private final @Nonnull Log                              log;
    private final @Nonnull UASTRDModule                     module;

    public FragmentShaderLocalChecker(
      final @Nonnull UASTRDModule module,
      final @Nonnull Map<ModulePathFlat, TASTDModule> checked_modules,
      final @Nonnull Map<String, TASTDType> checked_types,
      final @Nonnull Map<String, TASTDTerm> checked_terms,
      final @Nonnull LocalTypes locals,
      final @Nonnull Map<String, TValueType> builtins,
      final @Nonnull Log log)
    {
      this.module = module;
      this.checked_modules = checked_modules;
      this.checked_types = checked_types;
      this.checked_terms = checked_terms;
      this.locals = locals;
      this.builtins = builtins;
      this.log = log;
    }

    @Override public
      TASTDShaderFragmentLocalDiscard
      fragmentShaderVisitLocalDiscard(
        final @Nonnull UASTRDShaderFragmentLocalDiscard d)
        throws TypeCheckerError,
          ConstraintError
    {
      final TASTExpression e =
        d.getExpression().expressionVisitableAccept(
          new ExpressionChecker(
            this.module,
            this.checked_modules,
            this.checked_types,
            this.checked_terms,
            this.locals,
            this.builtins,
            this.log));

      return new TASTDShaderFragmentLocalDiscard(d.getDiscard(), e);
    }

    @Override public
      TASTDShaderFragmentLocalValue
      fragmentShaderVisitLocalValue(
        final @Nonnull UASTRDShaderFragmentLocalValue v)
        throws TypeCheckerError,
          ConstraintError
    {
      final LocalChecker lc =
        new LocalChecker(
          this.checked_modules,
          this.checked_terms,
          this.checked_types,
          this.locals,
          this.builtins,
          this.log,
          this.module);
      final TASTDValueLocal vr = lc.localVisitValueLocal(v.getValue());
      return new TASTDShaderFragmentLocalValue(vr);
    }
  }

  private static final class LocalChecker implements
    UASTRLocalLevelVisitor<TASTDValueLocal, TypeCheckerError>
  {
    private final @Nonnull Map<String, TValueType>          builtins;
    private final @Nonnull Map<ModulePathFlat, TASTDModule> checked_modules;
    private final @Nonnull Map<String, TASTDTerm>           checked_terms;
    private final @Nonnull Map<String, TASTDType>           checked_types;
    private final @Nonnull LocalTypes                       locals;
    private final @Nonnull Log                              log;
    private final @Nonnull UASTRDModule                     module;

    public LocalChecker(
      final @Nonnull Map<ModulePathFlat, TASTDModule> checked_modules,
      final @Nonnull Map<String, TASTDTerm> checked_terms,
      final @Nonnull Map<String, TASTDType> checked_types,
      final @Nonnull LocalTypes locals,
      final @Nonnull Map<String, TValueType> builtins,
      final @Nonnull Log log,
      final @Nonnull UASTRDModule module)
    {
      this.checked_modules = checked_modules;
      this.checked_terms = checked_terms;
      this.checked_types = checked_types;
      this.locals = locals;
      this.builtins = builtins;
      this.log = log;
      this.module = module;
    }

    @Override public TASTDValueLocal localVisitValueLocal(
      final @Nonnull UASTRDValueLocal v)
      throws TypeCheckerError,
        ConstraintError
    {
      final TASTExpression e =
        v.getExpression().expressionVisitableAccept(
          new ExpressionChecker(
            this.module,
            this.checked_modules,
            this.checked_types,
            this.checked_terms,
            this.locals,
            this.builtins,
            this.log));

      final UASTRTermNameLocal vn = v.getName();

      v.getAscription().mapPartial(
        new PartialFunction<UASTRTypeName, Unit, TypeCheckerError>() {
          @SuppressWarnings("synthetic-access") @Override public Unit call(
            final @Nonnull UASTRTypeName t)
            throws TypeCheckerError
          {
            try {
              final TType r =
                TypeChecker.lookupType(
                  LocalChecker.this.module,
                  LocalChecker.this.checked_modules,
                  LocalChecker.this.checked_types,
                  t);
              if (r.equals(e.getType()) == false) {
                throw TypeCheckerError.termValueExpressionAscriptionMismatch(
                  vn.getOriginal(),
                  r,
                  e.getType());
              }
              return Unit.unit();
            } catch (final ConstraintError x) {
              throw new UnreachableCodeException(x);
            }
          }
        });

      this.locals.addTerm(v.getName().getCurrent(), (TValueType) e.getType());

      final TASTTermNameLocal new_name =
        new TASTTermNameLocal(vn.getOriginal(), vn.getCurrent());
      return new TASTDValueLocal(new_name, e);
    }
  }

  private static class LocalTypes
  {
    public static @Nonnull LocalTypes initial()
    {
      return new LocalTypes(null);
    }

    private final @CheckForNull LocalTypes         parent;
    private final @Nonnull Map<String, TValueType> terms;

    private LocalTypes(
      final @CheckForNull LocalTypes parent)
    {
      this.terms = new HashMap<String, TValueType>();
      this.parent = parent;
    }

    public void addTerm(
      final @Nonnull String name,
      final @Nonnull TValueType type)
    {
      assert this.terms.containsKey(name) == false;
      this.terms.put(name, type);
    }

    public @Nonnull TValueType getName(
      final @Nonnull String name)
    {
      if (this.parent == null) {
        assert this.terms.containsKey(name);
        return this.terms.get(name);
      }

      if (this.terms.containsKey(name)) {
        return this.terms.get(name);
      }

      return this.parent.getName(name);
    }

    public @Nonnull LocalTypes withNew()
    {
      return new LocalTypes(this);
    }
  }

  private static final class ModuleChecker
  {
    private final @Nonnull Map<ModulePathFlat, TASTDModule> checked_modules;
    private final @Nonnull Log                              log;
    private final @Nonnull UASTRDModule                     module;

    public ModuleChecker(
      final @Nonnull UASTRDModule module,
      final @Nonnull Map<ModulePathFlat, TASTDModule> checked_modules,
      final @Nonnull Log log)
    {
      this.module = module;
      this.checked_modules = checked_modules;
      this.log = new Log(log, "module-checker");

      if (log.enabled(Level.LOG_DEBUG)) {
        final StringBuilder m = new StringBuilder();
        m.append("Checking ");
        m.append(module.getFlat().getActual());
        log.debug(m.toString());
      }
    }

    public @Nonnull TASTDModule check()
      throws TypeCheckerError,
        ConstraintError
    {
      final Map<String, TASTDType> checked_types =
        new HashMap<String, TASTDType>();
      final Map<String, TASTDTerm> checked_terms =
        new HashMap<String, TASTDTerm>();
      final Map<String, TASTDShader> checked_shaders =
        new HashMap<String, TASTDShader>();

      /**
       * Check type declarations.
       */

      final Map<String, UASTRDType> types = this.module.getTypes();
      final List<String> types_topo = this.module.getTypeTopology();

      if (this.log.enabled(Level.LOG_DEBUG)) {
        final StringBuilder m = new StringBuilder();
        m.append(types_topo.size());
        m.append(" types in topology");
        this.log.debug(m.toString());
      }

      for (int index = types_topo.size() - 1; index >= 0; --index) {
        final String name = types_topo.get(index);
        final UASTRDType type = types.get(name);
        final TASTDType checked =
          type.typeVisitableAccept(new TypeDeclarationChecker(
            this.module,
            this.checked_modules,
            checked_types,
            this.log));

        assert checked_types.containsKey(name) == false;
        checked_types.put(name, checked);
      }

      /**
       * Check term declarations.
       */

      final Map<String, UASTRDTerm> terms = this.module.getTerms();
      final List<String> terms_topo = this.module.getTermTopology();

      if (this.log.enabled(Level.LOG_DEBUG)) {
        final StringBuilder m = new StringBuilder();
        m.append(terms_topo.size());
        m.append(" terms in topology");
        this.log.debug(m.toString());
      }

      for (int index = terms_topo.size() - 1; index >= 0; --index) {
        final String name = terms_topo.get(index);
        final UASTRDTerm term = terms.get(name);
        final TASTDTerm checked =
          term.termVisitableAccept(new TermDeclarationChecker(
            this.module,
            this.checked_modules,
            checked_types,
            checked_terms,
            this.log));

        assert checked_terms.containsKey(name) == false;
        checked_terms.put(name, checked);
      }

      /**
       * Check shader declarations.
       */

      final Map<String, UASTRDShader> shaders = this.module.getShaders();
      final List<String> shaders_topo = this.module.getShaderTopology();

      if (this.log.enabled(Level.LOG_DEBUG)) {
        final StringBuilder m = new StringBuilder();
        m.append(shaders_topo.size());
        m.append(" shaders in topology");
        this.log.debug(m.toString());
      }

      for (int index = shaders_topo.size() - 1; index >= 0; --index) {
        final String name = shaders_topo.get(index);
        final UASTRDShader shader = shaders.get(name);
        final TASTDShader checked =
          shader.shaderVisitableAccept(new ShaderDeclarationChecker(
            this.module,
            this.checked_modules,
            checked_types,
            checked_terms,
            checked_shaders,
            this.log));

        assert checked_shaders.containsKey(name) == false;
        checked_shaders.put(name, checked);
      }

      /**
       * Assemble the rest of the module metadata.
       */

      final List<TASTDeclarationModuleLevel> checked_declarations =
        new ArrayList<TASTDeclarationModuleLevel>();

      final List<TASTDImport> imports = new ArrayList<TASTDImport>();
      final Map<ModulePathFlat, TASTDImport> imported_modules =
        new HashMap<ModulePathFlat, TASTDImport>();
      final Map<String, TASTDImport> imported_names =
        new HashMap<String, TASTDImport>();
      final Map<String, TASTDImport> imported_renames =
        new HashMap<String, TASTDImport>();

      return new TASTDModule(
        this.module.getPath(),
        imports,
        imported_modules,
        imported_names,
        imported_renames,
        checked_declarations,
        checked_terms,
        terms_topo,
        checked_types,
        types_topo,
        checked_shaders,
        shaders_topo);
    }
  }

  private static final class ShaderDeclarationChecker implements
    UASTRShaderVisitor<TASTDShader, TypeCheckerError>
  {
    private static void checkShaderTypeCompatibility(
      final @Nonnull TokenIdentifierLower program,
      final @Nonnull TASTDShaderVertex vs,
      final @Nonnull TASTDShaderFragment fs)
      throws TypeCheckerError,
        ConstraintError
    {
      boolean compatible = true;
      final HashSet<String> assigned = new HashSet<String>();
      final HashMap<String, TValueType> wrong_types =
        new HashMap<String, TValueType>();

      for (final TASTDShaderFragmentInput fi : fs.getInputs()) {
        for (final TASTDShaderVertexOutput vo : vs.getOutputs()) {
          final String fi_name = fi.getName().getCurrent();
          final String vo_name = vo.getName().getActual();
          if (fi_name.equals(vo_name)) {
            final TValueType fi_type = fi.getType();
            final TValueType vo_type = vo.getType();
            if (fi_type.equals(vo_type) == false) {
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
          vs,
          fs,
          assigned,
          wrong_types);
      }
    }

    private final @Nonnull Map<ModulePathFlat, TASTDModule> checked_modules;
    private final @Nonnull Map<String, TASTDShader>         checked_shaders;
    private final @Nonnull Map<String, TASTDTerm>           checked_terms;
    private final @Nonnull Map<String, TASTDType>           checked_types;
    private final @Nonnull Log                              log;

    private final @Nonnull UASTRDModule                     module;

    public ShaderDeclarationChecker(
      final @Nonnull UASTRDModule module,
      final @Nonnull Map<ModulePathFlat, TASTDModule> checked_modules,
      final @Nonnull Map<String, TASTDType> checked_types,
      final @Nonnull Map<String, TASTDTerm> checked_terms,
      final @Nonnull Map<String, TASTDShader> checked_shaders,
      final @Nonnull Log log)
    {
      this.module = module;
      this.checked_modules = checked_modules;
      this.checked_types = checked_types;
      this.checked_terms = checked_terms;
      this.checked_shaders = checked_shaders;
      this.log = log;
    }

    @Override public TASTDShaderFragment moduleVisitFragmentShader(
      final @Nonnull UASTRDShaderFragment f)
      throws TypeCheckerError,
        ConstraintError
    {
      return f.fragmentShaderVisitableAccept(new FragmentShaderChecker(
        this.module,
        this.checked_modules,
        this.checked_types,
        this.checked_terms,
        this.log));
    }

    @SuppressWarnings("synthetic-access") @Override public
      TASTDShaderProgram
      moduleVisitProgramShader(
        final @Nonnull UASTRDShaderProgram p)
        throws TypeCheckerError,
          ConstraintError
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
          ShaderDeclarationChecker.checkShaderTypeCompatibility(
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
      final @Nonnull UASTRDShaderVertex v)
      throws TypeCheckerError,
        ConstraintError
    {
      return v.vertexShaderVisitableAccept(new VertexShaderChecker(
        this.module,
        this.checked_modules,
        this.checked_types,
        this.checked_terms,
        this.log));
    }
  }

  private static final class TermDeclarationChecker implements
    UASTRTermVisitor<TASTDTerm, TypeCheckerError>
  {
    private final @Nonnull Map<ModulePathFlat, TASTDModule> checked_modules;
    private final @Nonnull Map<String, TASTDTerm>           checked_terms;
    private final @Nonnull Map<String, TASTDType>           checked_types;
    private final @Nonnull Log                              log;
    private final @Nonnull UASTRDModule                     module;

    public TermDeclarationChecker(
      final @Nonnull UASTRDModule module,
      final @Nonnull Map<ModulePathFlat, TASTDModule> checked_modules,
      final @Nonnull Map<String, TASTDType> checked_types,
      final @Nonnull Map<String, TASTDTerm> checked_terms,
      final @Nonnull Log log)
    {
      this.module = module;
      this.checked_modules = checked_modules;
      this.checked_types = checked_types;
      this.checked_terms = checked_terms;
      this.log = log;
    }

    @SuppressWarnings("synthetic-access") @Override public @Nonnull
      TASTDFunctionDefined
      termVisitFunctionDefined(
        final @Nonnull UASTRDFunctionDefined f)
        throws TypeCheckerError,
          ConstraintError
    {
      final LocalTypes locals = LocalTypes.initial();

      final List<TASTDFunctionArgument> arguments =
        new ArrayList<TASTDFunctionArgument>();
      final ArrayList<TFunctionArgument> f_arguments =
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
          new ExpressionChecker(
            this.module,
            this.checked_modules,
            this.checked_types,
            this.checked_terms,
            locals,
            new HashMap<String, TType.TValueType>(),
            this.log));

      if (body.getType().equals(treturn)) {
        return new TASTDFunctionDefined(
          f.getName(),
          arguments,
          body,
          new TFunction(f_arguments, (TValueType) treturn));
      }

      throw TypeCheckerError.termFunctionBodyReturnMismatch(
        f.getName(),
        treturn,
        body.getType());
    }

    @SuppressWarnings("synthetic-access") @Override public @Nonnull
      TASTDFunctionExternal
      termVisitFunctionExternal(
        final @Nonnull UASTRDFunctionExternal f)
        throws TypeCheckerError,
          ConstraintError
    {
      final LocalTypes locals = LocalTypes.initial();

      final List<TASTDFunctionArgument> arguments =
        new ArrayList<TASTDFunctionArgument>();
      final ArrayList<TFunctionArgument> f_arguments =
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
      final TASTDExternal ext =
        new TASTDExternal(
          orig_ext.getName(),
          orig_ext.isVertexShaderAllowed(),
          orig_ext.isFragmentShaderAllowed());
      return new TASTDFunctionExternal(f.getName(), arguments, tf, ext);
    }

    @Override public @Nonnull TASTDValue termVisitValue(
      final @Nonnull UASTRDValue v)
      throws TypeCheckerError,
        ConstraintError
    {
      final LocalTypes locals = LocalTypes.initial();

      final TASTExpression e =
        v.getExpression().expressionVisitableAccept(
          new ExpressionChecker(
            this.module,
            this.checked_modules,
            this.checked_types,
            this.checked_terms,
            locals,
            new HashMap<String, TType.TValueType>(),
            this.log));

      v.getAscription().mapPartial(
        new PartialFunction<UASTRTypeName, Unit, TypeCheckerError>() {
          @SuppressWarnings("synthetic-access") @Override public Unit call(
            final @Nonnull UASTRTypeName n)
            throws TypeCheckerError
          {
            try {
              final TType t =
                TypeChecker.lookupType(
                  TermDeclarationChecker.this.module,
                  TermDeclarationChecker.this.checked_modules,
                  TermDeclarationChecker.this.checked_types,
                  n);
              if (e.getType().equals(t) == false) {
                throw TypeCheckerError.termValueExpressionAscriptionMismatch(
                  v.getName(),
                  t,
                  e.getType());
              }
              return Unit.unit();
            } catch (final ConstraintError x) {
              throw new UnreachableCodeException(x);
            }
          }
        });

      if (e.getType() instanceof TValueType) {
        return new TASTDValue(v.getName(), e);
      }

      throw TypeCheckerError.termValueNotValueType(v.getName(), e.getType());
    }
  }

  private static final class TypeDeclarationChecker implements
    UASTRTypeVisitor<TASTDType, TypeCheckerError>
  {
    private final @Nonnull Map<ModulePathFlat, TASTDModule> checked_modules;
    private final @Nonnull Map<String, TASTDType>           checked_types;
    private final @Nonnull Log                              log;
    private final @Nonnull UASTRDModule                     module;

    public TypeDeclarationChecker(
      final @Nonnull UASTRDModule module,
      final @Nonnull Map<ModulePathFlat, TASTDModule> checked_modules,
      final @Nonnull Map<String, TASTDType> checked_types,
      final @Nonnull Log log)
    {
      this.module = module;
      this.checked_modules = checked_modules;
      this.checked_types = checked_types;
      this.log = log;
    }

    @SuppressWarnings("synthetic-access") @Override public @Nonnull
      TASTDTypeRecord
      typeVisitTypeRecord(
        final @Nonnull UASTRDTypeRecord r)
        throws TypeCheckerError,
          ConstraintError
    {
      if (this.log.enabled(Level.LOG_DEBUG)) {
        final StringBuilder m = new StringBuilder();
        m.append("Checking ");
        m.append(this.module.getFlat().getActual());
        m.append(".");
        m.append(r.getName().getActual());
        this.log.debug(m.toString());
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
        t_fields
          .add(new TRecordField(f_name.getActual(), (TManifestType) rt));
        fields.add(new TASTDTypeRecordField(f_name, (TManifestType) rt));
      }

      final TokenIdentifierLower name = r.getName();
      final TRecord trecord = new TRecord(name.getActual(), t_fields);
      final TASTDTypeRecord trecord_d =
        new TASTDTypeRecord(name, fields, trecord);
      return trecord_d;
    }
  }

  private static final class VertexShaderChecker implements
    UASTRVertexShaderVisitor<TASTDShaderVertex, TASTDShaderVertexInput, TASTDShaderVertexParameter, TASTDShaderVertexOutput, TASTDShaderVertexLocalValue, TASTDShaderVertexOutputAssignment, TypeCheckerError>
  {
    private final @Nonnull Map<String, TValueType>          builtin_inputs;
    private final @Nonnull Map<String, TValueType>          builtin_outputs;
    private final @Nonnull Map<ModulePathFlat, TASTDModule> checked_modules;
    private final @Nonnull Map<String, TASTDTerm>           checked_terms;
    private final @Nonnull Map<String, TASTDType>           checked_types;
    private final @Nonnull LocalTypes                       locals;
    private final @Nonnull Log                              log;
    private final @Nonnull UASTRDModule                     module;
    private final @Nonnull Map<String, TValueType>          outputs;

    public VertexShaderChecker(
      final @Nonnull UASTRDModule module,
      final @Nonnull Map<ModulePathFlat, TASTDModule> checked_modules,
      final @Nonnull Map<String, TASTDType> checked_types,
      final @Nonnull Map<String, TASTDTerm> checked_terms,
      final @Nonnull Log log)
    {
      this.module = module;
      this.checked_modules = checked_modules;
      this.checked_types = checked_types;
      this.checked_terms = checked_terms;
      this.log = log;
      this.builtin_inputs = new HashMap<String, TValueType>();
      this.builtin_outputs = NamesBuiltIn.VERTEX_SHADER_OUTPUTS;
      this.locals = LocalTypes.initial();
      this.outputs = new HashMap<String, TValueType>();
    }

    @Override public
      TASTDShaderVertex
      vertexShaderVisit(
        final @Nonnull List<TASTDShaderVertexInput> r_inputs,
        final @Nonnull List<TASTDShaderVertexParameter> r_parameters,
        final @Nonnull List<TASTDShaderVertexOutput> r_outputs,
        final @Nonnull List<TASTDShaderVertexLocalValue> r_values,
        final @Nonnull List<TASTDShaderVertexOutputAssignment> r_output_assignments,
        final @Nonnull UASTRDShaderVertex v)
        throws TypeCheckerError,
          ConstraintError
    {
      return new TASTDShaderVertex(
        v.getName(),
        r_inputs,
        r_outputs,
        r_parameters,
        r_values,
        r_output_assignments);
    }

    @SuppressWarnings("synthetic-access") @Override public
      TASTDShaderVertexInput
      vertexShaderVisitInput(
        final @Nonnull UASTRDShaderVertexInput i)
        throws TypeCheckerError,
          ConstraintError
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

      this.locals.addTerm(in.getCurrent(), (TValueType) t);
      return new TASTDShaderVertexInput(name, (TValueType) t);
    }

    @Override public
      UASTRVertexShaderLocalVisitor<TASTDShaderVertexLocalValue, TypeCheckerError>
      vertexShaderVisitLocalsPre()
        throws TypeCheckerError,
          ConstraintError
    {
      return new VertexShaderLocalChecker(
        this.module,
        this.checked_modules,
        this.checked_types,
        this.checked_terms,
        this.locals,
        this.builtin_inputs,
        this.log);
    }

    @SuppressWarnings("synthetic-access") @Override public
      TASTDShaderVertexOutput
      vertexShaderVisitOutput(
        final @Nonnull UASTRDShaderVertexOutput o)
        throws TypeCheckerError,
          ConstraintError
    {
      final TType t =
        TypeChecker.lookupType(
          this.module,
          this.checked_modules,
          this.checked_types,
          o.getType());

      this.outputs.put(o.getName().getActual(), (TValueType) t);
      return new TASTDShaderVertexOutput(o.getName(), (TValueType) t);
    }

    @SuppressWarnings("synthetic-access") @Override public
      TASTDShaderVertexOutputAssignment
      vertexShaderVisitOutputAssignment(
        final @Nonnull UASTRDShaderVertexOutputAssignment a)
        throws TypeCheckerError,
          ConstraintError
    {
      final TType type =
        TypeChecker.lookupTermType(
          a.getVariable().getName(),
          this.module,
          this.checked_modules,
          this.checked_terms,
          this.locals,
          this.builtin_inputs);

      final TokenIdentifierLower a_name = a.getName();
      final TValueType out_type;
      if (this.builtin_outputs.containsKey(a_name.getActual())) {
        out_type = this.builtin_outputs.get(a_name.getActual());
      } else {
        assert this.outputs.containsKey(a_name.getActual());
        out_type = this.outputs.get(a_name.getActual());
      }

      if (out_type.equals(type) == false) {
        throw TypeCheckerError
          .shaderAssignmentBadType(a_name, out_type, type);
      }

      final TASTTermName name =
        TypeChecker.mapTermName(a.getVariable().getName());
      final TASTEVariable variable = new TASTEVariable(type, name);
      return new TASTDShaderVertexOutputAssignment(a_name, variable);
    }

    @SuppressWarnings("synthetic-access") @Override public
      TASTDShaderVertexParameter
      vertexShaderVisitParameter(
        final @Nonnull UASTRDShaderVertexParameter p)
        throws TypeCheckerError,
          ConstraintError
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

  private static final class VertexShaderLocalChecker implements
    UASTRVertexShaderLocalVisitor<TASTDShaderVertexLocalValue, TypeCheckerError>
  {
    private final @Nonnull Map<String, TValueType>          builtins;
    private final @Nonnull Map<ModulePathFlat, TASTDModule> checked_modules;
    private final @Nonnull Map<String, TASTDTerm>           checked_terms;
    private final @Nonnull Map<String, TASTDType>           checked_types;
    private final @Nonnull LocalTypes                       locals;
    private final @Nonnull Log                              log;
    private final @Nonnull UASTRDModule                     module;

    public VertexShaderLocalChecker(
      final @Nonnull UASTRDModule module,
      final @Nonnull Map<ModulePathFlat, TASTDModule> checked_modules,
      final @Nonnull Map<String, TASTDType> checked_types,
      final @Nonnull Map<String, TASTDTerm> checked_terms,
      final @Nonnull LocalTypes locals,
      final @Nonnull Map<String, TValueType> builtins,
      final @Nonnull Log log)
    {
      this.module = module;
      this.checked_modules = checked_modules;
      this.checked_types = checked_types;
      this.checked_terms = checked_terms;
      this.locals = locals;
      this.builtins = builtins;
      this.log = log;
    }

    @Override public TASTDShaderVertexLocalValue vertexShaderVisitLocalValue(
      final @Nonnull UASTRDShaderVertexLocalValue v)
      throws TypeCheckerError,
        ConstraintError
    {
      final LocalChecker lc =
        new LocalChecker(
          this.checked_modules,
          this.checked_terms,
          this.checked_types,
          this.locals,
          this.builtins,
          this.log,
          this.module);
      final TASTDValueLocal vr = lc.localVisitValueLocal(v.getValue());
      return new TASTDShaderVertexLocalValue(vr);
    }
  }

  private static @Nonnull TASTDShader lookupShader(
    final @Nonnull UASTRDModule module,
    final @Nonnull Map<ModulePathFlat, TASTDModule> checked_modules,
    final @Nonnull Map<String, TASTDShader> checked_shaders,
    final @Nonnull UASTRShaderName name)
  {
    final TokenIdentifierLower sname = name.getName();
    if (name.getFlat().equals(module.getFlat())) {
      assert checked_shaders.containsKey(sname.getActual());
      return checked_shaders.get(sname.getActual());
    }

    final TASTDModule m = checked_modules.get(name.getFlat());
    assert m.getShaders().containsKey(sname.getActual());
    return m.getShaders().get(sname.getActual());
  }

  private static @Nonnull TType lookupTermType(
    final @Nonnull UASTRTermName name,
    final @Nonnull UASTRDModule module,
    final @Nonnull Map<ModulePathFlat, TASTDModule> checked_modules,
    final @Nonnull Map<String, TASTDTerm> checked_terms,
    final @Nonnull LocalTypes locals,
    final @Nonnull Map<String, TValueType> builtins)
    throws ConstraintError
  {
    return name
      .termNameVisitableAccept(new UASTRTermNameVisitor<TType, ConstraintError>() {
        @Override public TType termNameVisitBuiltIn(
          final @Nonnull UASTRTermNameBuiltIn t)
          throws ConstraintError
        {
          assert builtins.containsKey(t.getActual().getActual());
          return builtins.get(t.getActual().getActual());
        }

        @Override public TType termNameVisitGlobal(
          final @Nonnull UASTRTermNameGlobal t)
          throws ConstraintError
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
          final @Nonnull UASTRTermNameLocal t)
          throws ConstraintError
        {
          return locals.getName(t.getCurrent());
        }
      });
  }

  private static @Nonnull TType lookupType(
    final @Nonnull UASTRDModule module,
    final @Nonnull Map<ModulePathFlat, TASTDModule> checked_modules,
    final @Nonnull Map<String, TASTDType> checked_types,
    final @Nonnull UASTRTypeName type)
    throws ConstraintError
  {
    return type
      .typeNameVisitableAccept(new UASTRTypeNameVisitor<TType, ConstraintError>() {
        @Override public TType typeNameVisitBuiltIn(
          final @Nonnull UASTRTypeNameBuiltIn t)
          throws ConstraintError
        {
          final String actual = t.getName().getActual();
          return TType.getBaseTypes().get(actual);
        }

        @Override public TType typeNameVisitGlobal(
          final @Nonnull UASTRTypeNameGlobal t)
          throws ConstraintError
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

  private static @Nonnull TASTShaderName mapShaderName(
    final @Nonnull UASTRShaderName name)
    throws ConstraintError
  {
    return new TASTShaderName(name.getPath(), name.getName());
  }

  private static @Nonnull TASTTermName mapTermName(
    final @Nonnull UASTRTermName n)
    throws ConstraintError
  {
    return n
      .termNameVisitableAccept(new UASTRTermNameVisitor<TASTTermName, ConstraintError>() {
        @Override public @Nonnull TASTTermNameBuiltIn termNameVisitBuiltIn(
          final @Nonnull UASTRTermNameBuiltIn t_name)
          throws ConstraintError,
            ConstraintError
        {
          return new TASTTermNameBuiltIn(t_name.getActual());
        }

        @Override public @Nonnull TASTTermNameGlobal termNameVisitGlobal(
          final @Nonnull UASTRTermNameGlobal t_name)
          throws ConstraintError,
            ConstraintError
        {
          return new TASTTermNameGlobal(t_name.getPath(), t_name.getName());
        }

        @Override public @Nonnull TASTTermNameLocal termNameVisitLocal(
          final @Nonnull UASTRTermNameLocal t_name)
          throws ConstraintError,
            ConstraintError
        {
          return new TASTTermNameLocal(t_name.getOriginal(), t_name
            .getCurrent());
        }
      });
  }

  public static @Nonnull TypeChecker newTypeChecker(
    final @Nonnull UASTRCompilation compilation,
    final @Nonnull Log log)
    throws ConstraintError
  {
    return new TypeChecker(compilation, log);
  }

  private final @Nonnull UASTRCompilation compilation;
  private final @Nonnull Log              log;

  private TypeChecker(
    final @Nonnull UASTRCompilation compilation,
    final @Nonnull Log log)
    throws ConstraintError
  {
    this.log = new Log(log, "type-checker");
    this.compilation =
      Constraints.constrainNotNull(compilation, "Compilation");
  }

  public @Nonnull TASTCompilation check()
    throws ConstraintError,
      TypeCheckerError
  {
    final Map<ModulePathFlat, UASTRDModule> modules =
      this.compilation.getModules();
    final Map<ModulePathFlat, TASTDModule> checked_modules =
      new HashMap<ModulePathFlat, TASTDModule>();

    final List<ModulePathFlat> topology = this.compilation.getTopology();
    if (this.log.enabled(Level.LOG_DEBUG)) {
      final StringBuilder m = new StringBuilder();
      m.append(topology.size());
      m.append(" modules in topology");
      this.log.debug(m.toString());
    }

    for (int index = topology.size() - 1; index >= 0; --index) {
      final ModulePathFlat path = topology.get(index);
      final UASTRDModule module = modules.get(path);
      final ModuleChecker mr =
        new ModuleChecker(module, checked_modules, this.log);
      final TASTDModule checked = mr.check();
      checked_modules.put(path, checked);
    }

    return new TASTCompilation(
      this.compilation.getTopology(),
      checked_modules,
      this.compilation.getPaths());
  }
}
