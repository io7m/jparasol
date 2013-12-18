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

package com.io7m.jparasol.untyped;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Option;
import com.io7m.jaux.functional.PartialFunction;
import com.io7m.jlog.Log;
import com.io7m.jparasol.ModulePath;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.NameRestrictions;
import com.io7m.jparasol.NameRestrictions.NameRestricted;
import com.io7m.jparasol.NamesBuiltIn;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.lexer.Token.TokenIdentifierUpper;
import com.io7m.jparasol.untyped.ast.initial.UASTIChecked;
import com.io7m.jparasol.untyped.ast.initial.UASTICompilation;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDFunctionArgument;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDFunctionDefined;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDFunctionExternal;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDImport;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDModule;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragment;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentInput;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentLocalDiscard;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentLocalValue;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentOutput;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentOutputAssignment;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentParameter;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderProgram;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertex;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertexInput;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertexLocalValue;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertexOutput;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertexOutputAssignment;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertexParameter;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDTypeRecord;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDTypeRecordField;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDValue;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDValueLocal;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpression.UASTIEApplication;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpression.UASTIEBoolean;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpression.UASTIEConditional;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpression.UASTIEInteger;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpression.UASTIELet;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpression.UASTIENew;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpression.UASTIEReal;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpression.UASTIERecord;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpression.UASTIERecordProjection;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpression.UASTIESwizzle;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpression.UASTIEVariable;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpression.UASTIRecordFieldAssignment;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpressionVisitor;
import com.io7m.jparasol.untyped.ast.initial.UASTIFragmentShaderLocalVisitor;
import com.io7m.jparasol.untyped.ast.initial.UASTIFragmentShaderVisitor;
import com.io7m.jparasol.untyped.ast.initial.UASTIFunctionVisitor;
import com.io7m.jparasol.untyped.ast.initial.UASTILocalLevelVisitor;
import com.io7m.jparasol.untyped.ast.initial.UASTIModuleLevelDeclarationVisitor;
import com.io7m.jparasol.untyped.ast.initial.UASTIModuleVisitor;
import com.io7m.jparasol.untyped.ast.initial.UASTIShaderPath;
import com.io7m.jparasol.untyped.ast.initial.UASTITypePath;
import com.io7m.jparasol.untyped.ast.initial.UASTIValuePath;
import com.io7m.jparasol.untyped.ast.initial.UASTIVertexShaderLocalVisitor;
import com.io7m.jparasol.untyped.ast.initial.UASTIVertexShaderVisitor;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUCompilation;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDFunction;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDFunctionArgument;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDFunctionDefined;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDFunctionExternal;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDImport;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDModule;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderFragment;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderFragmentInput;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderFragmentLocal;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderFragmentLocalDiscard;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderFragmentLocalValue;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderFragmentOutput;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderFragmentOutputAssignment;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderFragmentParameter;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderProgram;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderVertex;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderVertexInput;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderVertexLocalValue;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderVertexOutput;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderVertexOutputAssignment;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShaderVertexParameter;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDTypeRecord;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDTypeRecordField;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDValue;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDValueLocal;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDeclarationModuleLevel;
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
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUName;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUName.UASTUNameGlobal;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUName.UASTUNameLocal;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUName.UASTUNameModuleLevel;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUShaderPath;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUTypePath;

public final class UniqueBinders
{
  private static class Context
  {
    public static @Nonnull Context initialContext(
      final @Nonnull ModuleContext module)
    {
      return new Context(
        module,
        null,
        new HashSet<String>(),
        new HashMap<String, UASTUNameLocal>());
    }

    private final @Nonnull Set<String>                 builtins;
    private final @Nonnull ModuleContext               module;
    private final @Nonnull Map<String, UASTUNameLocal> names;
    private final @CheckForNull Context                parent;

    public Context(
      final @Nonnull ModuleContext module,
      final @CheckForNull Context parent,
      final @Nonnull Set<String> builtins,
      final @Nonnull Map<String, UASTUNameLocal> names)
    {
      this.module = module;
      this.parent = parent;
      this.builtins = builtins;
      this.names = names;
    }

    public @Nonnull UASTUNameLocal addBinding(
      final @Nonnull TokenIdentifierLower name)
      throws ConstraintError
    {
      final StringBuilder s = new StringBuilder();
      s.append(name.getActual());

      for (;;) {
        final String name_s = s.toString();
        final boolean exists = this.nameExists(name_s);
        final boolean restricted =
          NameRestrictions.checkRestricted(name_s) != NameRestricted.NAME_OK;

        if (exists || restricted) {
          s.setLength(0);
          s.append(name.getActual());
          s.append(this.module.getNext());
        } else {
          break;
        }
      }

      final UASTUNameLocal u = new UASTUNameLocal(name, s.toString());
      this.names.put(s.toString(), u);
      return u;
    }

    public @Nonnull UASTUName getName(
      final @Nonnull TokenIdentifierLower name)
      throws ConstraintError
    {
      if (this.builtins.contains(name.getActual())) {
        return new UASTUName.UASTUNameBuiltIn(name);
      }
      if (this.names.containsKey(name.getActual())) {
        return this.names.get(name.getActual());
      }
      if (this.parent == null) {
        return new UASTUNameModuleLevel(name);
      }
      return this.parent.getName(name);
    }

    public @Nonnull UASTUName getNameFromValuePath(
      final @Nonnull UASTIValuePath name)
      throws ConstraintError
    {
      final Option<TokenIdentifierUpper> m = name.getModule();
      if (m.isSome()) {
        return new UASTUNameGlobal(
          ((Option.Some<TokenIdentifierUpper>) m).value,
          name.getName());
      }
      return this.getName(name.getName());
    }

    public @CheckForNull Context getParent()
    {
      return this.parent;
    }

    private boolean nameExists(
      final @Nonnull String name)
    {
      if (this.names.containsKey(name)) {
        return true;
      }
      if (this.parent == null) {
        return false;
      }
      return this.parent.nameExists(name);
    }

    public @Nonnull Context withNew()
    {
      return new Context(
        this.module,
        this,
        this.builtins,
        new HashMap<String, UASTUName.UASTUNameLocal>());
    }

    public @Nonnull Context withNewPlusBuiltins(
      final @Nonnull Set<String> more_builtins)
    {
      final HashSet<String> new_builtins = new HashSet<String>();
      new_builtins.addAll(this.builtins);
      new_builtins.addAll(more_builtins);

      return new Context(
        this.module,
        this,
        new_builtins,
        new HashMap<String, UASTUName.UASTUNameLocal>());
    }
  }

  private static class ExpressionTransformer implements
    UASTIExpressionVisitor<UASTUExpression, UASTUDValueLocal, UASTIChecked, UniqueBindersError>
  {
    private @Nonnull Context context;

    public ExpressionTransformer(
      final @Nonnull Context context)
    {
      this.context = context;
    }

    @Override public UASTUEApplication expressionVisitApplication(
      final @Nonnull List<UASTUExpression> arguments,
      final @Nonnull UASTIEApplication<UASTIChecked> e)
      throws UniqueBindersError,
        ConstraintError
    {
      return new UASTUEApplication(this.context.getNameFromValuePath(e
        .getName()), arguments);
    }

    @Override public void expressionVisitApplicationPre(
      final @Nonnull UASTIEApplication<UASTIChecked> e)
      throws UniqueBindersError,
        ConstraintError
    {
      // Nothing
    }

    @Override public UASTUEBoolean expressionVisitBoolean(
      final @Nonnull UASTIEBoolean<UASTIChecked> e)
      throws UniqueBindersError,
        ConstraintError
    {
      return new UASTUEBoolean(e.getToken());
    }

    @Override public @Nonnull UASTUEConditional expressionVisitConditional(
      final @Nonnull UASTUExpression condition,
      final @Nonnull UASTUExpression left,
      final @Nonnull UASTUExpression right,
      final @Nonnull UASTIEConditional<UASTIChecked> e)
      throws UniqueBindersError,
        ConstraintError
    {
      return new UASTUEConditional(condition, left, right);
    }

    @Override public void expressionVisitConditionalPre(
      final @Nonnull UASTIEConditional<UASTIChecked> e)
      throws UniqueBindersError,
        ConstraintError
    {
      // Nothing
    }

    @Override public @Nonnull UASTUEInteger expressionVisitInteger(
      final @Nonnull UASTIEInteger<UASTIChecked> e)
      throws UniqueBindersError,
        ConstraintError
    {
      return new UASTUEInteger(e.getToken());
    }

    @Override public @Nonnull UASTUELet expressionVisitLet(
      final @Nonnull List<UASTUDValueLocal> bindings,
      final @Nonnull UASTUExpression body,
      final @Nonnull UASTIELet<UASTIChecked> e)
      throws UniqueBindersError,
        ConstraintError
    {
      assert this.context.getParent() != null;
      this.context = this.context.getParent();
      return new UASTUELet(e.getToken(), bindings, body);
    }

    @Override public
      UASTILocalLevelVisitor<UASTUDValueLocal, UASTIChecked, UniqueBindersError>
      expressionVisitLetPre(
        final @Nonnull UASTIELet<UASTIChecked> e)
        throws UniqueBindersError,
          ConstraintError
    {
      this.context = this.context.withNew();
      return new LocalTransformer(this.context);
    }

    @Override public @Nonnull UASTUENew expressionVisitNew(
      final @Nonnull List<UASTUExpression> arguments,
      final @Nonnull UASTIENew<UASTIChecked> e)
      throws UniqueBindersError,
        ConstraintError
    {
      return new UASTUENew(UniqueBinders.mapTypePath(e.getName()), arguments);
    }

    @Override public void expressionVisitNewPre(
      final @Nonnull UASTIENew<UASTIChecked> e)
      throws UniqueBindersError,
        ConstraintError
    {
      // Nothing
    }

    @Override public @Nonnull UASTUEReal expressionVisitReal(
      final @Nonnull UASTIEReal<UASTIChecked> e)
      throws UniqueBindersError,
        ConstraintError
    {
      return new UASTUEReal(e.getToken());
    }

    @Override public @Nonnull UASTUERecord expressionVisitRecord(
      final @Nonnull UASTIERecord<UASTIChecked> e)
      throws UniqueBindersError,
        ConstraintError
    {
      final List<UASTURecordFieldAssignment> fields =
        new ArrayList<UASTURecordFieldAssignment>();

      for (final UASTIRecordFieldAssignment<UASTIChecked> f : e
        .getAssignments()) {
        final UASTUExpression ep =
          f.getExpression().expressionVisitableAccept(
            new ExpressionTransformer(this.context));
        fields.add(new UASTURecordFieldAssignment(f.getName(), ep));
      }

      return new UASTUERecord(
        UniqueBinders.mapTypePath(e.getTypePath()),
        fields);
    }

    @Override public @Nonnull
      UASTUERecordProjection
      expressionVisitRecordProjection(
        final @Nonnull UASTUExpression body,
        final @Nonnull UASTIERecordProjection<UASTIChecked> e)
        throws UniqueBindersError,
          ConstraintError
    {
      return new UASTUERecordProjection(body, e.getField());
    }

    @Override public void expressionVisitRecordProjectionPre(
      final @Nonnull UASTIERecordProjection<UASTIChecked> e)
      throws UniqueBindersError,
        ConstraintError
    {
      // Nothing
    }

    @Override public @Nonnull UASTUESwizzle expressionVisitSwizzle(
      final @Nonnull UASTUExpression body,
      final @Nonnull UASTIESwizzle<UASTIChecked> e)
      throws UniqueBindersError,
        ConstraintError
    {
      return new UASTUESwizzle(body, e.getFields());
    }

    @Override public void expressionVisitSwizzlePre(
      final @Nonnull UASTIESwizzle<UASTIChecked> e)
      throws UniqueBindersError,
        ConstraintError
    {
      // Nothing
    }

    @Override public @Nonnull UASTUEVariable expressionVisitVariable(
      final @Nonnull UASTIEVariable<UASTIChecked> e)
      throws UniqueBindersError,
        ConstraintError
    {
      return new UASTUEVariable(
        this.context.getNameFromValuePath(e.getName()));
    }
  }

  private static class FragmentShaderLocalTransformer implements
    UASTIFragmentShaderLocalVisitor<UASTUDShaderFragmentLocal, UASTIChecked, UniqueBindersError>
  {
    private final @Nonnull Context context;

    public FragmentShaderLocalTransformer(
      final @Nonnull Context c)
    {
      this.context = c;
    }

    @Override public @Nonnull
      UASTUDShaderFragmentLocalDiscard
      fragmentShaderVisitLocalDiscard(
        final @Nonnull UASTIDShaderFragmentLocalDiscard<UASTIChecked> d)
        throws UniqueBindersError,
          ConstraintError
    {
      final UASTUExpression expr =
        d.getExpression().expressionVisitableAccept(
          new ExpressionTransformer(this.context));
      return new UASTUDShaderFragmentLocalDiscard(d.getDiscard(), expr);
    }

    @Override public @Nonnull
      UASTUDShaderFragmentLocalValue
      fragmentShaderVisitLocalValue(
        final @Nonnull UASTIDShaderFragmentLocalValue<UASTIChecked> v)
        throws UniqueBindersError,
          ConstraintError
    {
      final UASTIDValueLocal<UASTIChecked> original = v.getValue();
      final UASTUNameLocal name = this.context.addBinding(original.getName());
      final Option<UASTUTypePath> ascription =
        UniqueBinders.mapAscription(original.getAscription());

      final UASTUExpression expression =
        original.getExpression().expressionVisitableAccept(
          new ExpressionTransformer(this.context));

      final UASTUDValueLocal value =
        new UASTUDValueLocal(name, ascription, expression);

      return new UASTUDShaderFragmentLocalValue(value);
    }
  }

  private static class VertexShaderLocalTransformer implements
    UASTIVertexShaderLocalVisitor<UASTUDShaderVertexLocalValue, UASTIChecked, UniqueBindersError>
  {
    private final @Nonnull Context context;

    public VertexShaderLocalTransformer(
      final @Nonnull Context c)
    {
      this.context = c;
    }

    @Override public
      UASTUDShaderVertexLocalValue
      vertexShaderVisitLocalValue(
        final @Nonnull UASTIDShaderVertexLocalValue<UASTIChecked> v)
        throws UniqueBindersError,
          ConstraintError
    {
      final UASTIDValueLocal<UASTIChecked> original = v.getValue();
      final UASTUNameLocal name = this.context.addBinding(original.getName());
      final Option<UASTUTypePath> ascription =
        UniqueBinders.mapAscription(original.getAscription());

      final UASTUExpression expression =
        original.getExpression().expressionVisitableAccept(
          new ExpressionTransformer(this.context));

      final UASTUDValueLocal value =
        new UASTUDValueLocal(name, ascription, expression);

      return new UASTUDShaderVertexLocalValue(value);
    }
  }

  private static class FragmentShaderTransformer implements
    UASTIFragmentShaderVisitor<UASTUDShaderFragment, UASTUDShaderFragmentInput, UASTUDShaderFragmentParameter, UASTUDShaderFragmentOutput, UASTUDShaderFragmentLocal, UASTUDShaderFragmentOutputAssignment, UASTIChecked, UniqueBindersError>
  {
    private @Nonnull Context context;

    public FragmentShaderTransformer(
      final Context c)
    {
      this.context = c;
    }

    @Override public @Nonnull
      UASTUDShaderFragmentInput
      fragmentShaderVisitInput(
        final @Nonnull UASTIDShaderFragmentInput<UASTIChecked> i)
        throws UniqueBindersError,
          ConstraintError
    {
      final UASTUNameLocal name = this.context.addBinding(i.getName());
      final UASTUTypePath type = UniqueBinders.mapTypePath(i.getType());
      return new UASTUDShaderFragmentInput(name, type);
    }

    @Override public @Nonnull
      UASTIFragmentShaderLocalVisitor<UASTUDShaderFragmentLocal, UASTIChecked, UniqueBindersError>
      fragmentShaderVisitLocalsPre()
        throws UniqueBindersError,
          ConstraintError
    {
      this.context =
        this.context.withNewPlusBuiltins(NamesBuiltIn.FRAGMENT_SHADER_INPUTS);
      return new FragmentShaderLocalTransformer(this.context);
    }

    @Override public @Nonnull
      UASTUDShaderFragmentOutput
      fragmentShaderVisitOutput(
        final @Nonnull UASTIDShaderFragmentOutput<UASTIChecked> o)
        throws UniqueBindersError,
          ConstraintError
    {
      return new UASTUDShaderFragmentOutput(
        o.getName(),
        UniqueBinders.mapTypePath(o.getType()),
        o.getIndex());
    }

    @Override public @Nonnull
      UASTUDShaderFragmentOutputAssignment
      fragmentShaderVisitOutputAssignment(
        final @Nonnull UASTIDShaderFragmentOutputAssignment<UASTIChecked> a)
        throws UniqueBindersError,
          ConstraintError
    {
      final UASTUExpression evar =
        a.getVariable().expressionVisitableAccept(
          new ExpressionTransformer(this.context));
      return new UASTUDShaderFragmentOutputAssignment(
        a.getName(),
        (UASTUEVariable) evar);
    }

    @Override public @Nonnull
      UASTUDShaderFragmentParameter
      fragmentShaderVisitParameter(
        final @Nonnull UASTIDShaderFragmentParameter<UASTIChecked> p)
        throws UniqueBindersError,
          ConstraintError
    {
      final UASTUNameLocal name = this.context.addBinding(p.getName());
      final UASTUTypePath type = UniqueBinders.mapTypePath(p.getType());
      return new UASTUDShaderFragmentParameter(name, type);
    }

    @Override public
      UASTUDShaderFragment
      fragmentShaderVisit(
        final @Nonnull List<UASTUDShaderFragmentInput> inputs,
        final @Nonnull List<UASTUDShaderFragmentParameter> parameters,
        final @Nonnull List<UASTUDShaderFragmentOutput> outputs,
        final @Nonnull List<UASTUDShaderFragmentLocal> locals,
        final @Nonnull List<UASTUDShaderFragmentOutputAssignment> output_assignments,
        final @Nonnull UASTIDShaderFragment<UASTIChecked> f)
        throws UniqueBindersError,
          ConstraintError
    {
      return new UASTUDShaderFragment(
        f.getName(),
        inputs,
        outputs,
        parameters,
        locals,
        output_assignments);
    }
  }

  private static class VertexShaderTransformer implements
    UASTIVertexShaderVisitor<UASTUDShaderVertex, UASTUDShaderVertexInput, UASTUDShaderVertexParameter, UASTUDShaderVertexOutput, UASTUDShaderVertexLocalValue, UASTUDShaderVertexOutputAssignment, UASTIChecked, UniqueBindersError>
  {
    private final @Nonnull Context context;

    public VertexShaderTransformer(
      final Context c)
    {
      this.context = c;
    }

    @Override public @Nonnull UASTUDShaderVertexInput vertexShaderVisitInput(
      final @Nonnull UASTIDShaderVertexInput<UASTIChecked> i)
      throws UniqueBindersError,
        ConstraintError
    {
      final UASTUNameLocal name = this.context.addBinding(i.getName());
      final UASTUTypePath type = UniqueBinders.mapTypePath(i.getType());
      return new UASTUDShaderVertexInput(name, type);
    }

    @Override public @Nonnull
      UASTUDShaderVertexOutput
      vertexShaderVisitOutput(
        final @Nonnull UASTIDShaderVertexOutput<UASTIChecked> o)
        throws UniqueBindersError,
          ConstraintError
    {
      return new UASTUDShaderVertexOutput(
        o.getName(),
        UniqueBinders.mapTypePath(o.getType()));
    }

    @Override public @Nonnull
      UASTUDShaderVertexOutputAssignment
      vertexShaderVisitOutputAssignment(
        final @Nonnull UASTIDShaderVertexOutputAssignment<UASTIChecked> a)
        throws UniqueBindersError,
          ConstraintError
    {
      final UASTUExpression evar =
        a.getVariable().expressionVisitableAccept(
          new ExpressionTransformer(this.context));
      return new UASTUDShaderVertexOutputAssignment(
        a.getName(),
        (UASTUEVariable) evar);
    }

    @Override public @Nonnull
      UASTUDShaderVertexParameter
      vertexShaderVisitParameter(
        final @Nonnull UASTIDShaderVertexParameter<UASTIChecked> p)
        throws UniqueBindersError,
          ConstraintError
    {
      final UASTUNameLocal name = this.context.addBinding(p.getName());
      final UASTUTypePath type = UniqueBinders.mapTypePath(p.getType());
      return new UASTUDShaderVertexParameter(name, type);
    }

    @Override public
      UASTUDShaderVertex
      vertexShaderVisit(
        final @Nonnull List<UASTUDShaderVertexInput> inputs,
        final @Nonnull List<UASTUDShaderVertexParameter> parameters,
        final @Nonnull List<UASTUDShaderVertexOutput> outputs,
        final @Nonnull List<UASTUDShaderVertexLocalValue> locals,
        final @Nonnull List<UASTUDShaderVertexOutputAssignment> output_assignments,
        final @Nonnull UASTIDShaderVertex<UASTIChecked> v)
        throws UniqueBindersError,
          ConstraintError
    {
      return new UASTUDShaderVertex(
        v.getName(),
        inputs,
        outputs,
        parameters,
        locals,
        output_assignments);
    }

    @Override public
      UASTIVertexShaderLocalVisitor<UASTUDShaderVertexLocalValue, UASTIChecked, UniqueBindersError>
      vertexShaderVisitLocalsPre()
        throws UniqueBindersError,
          ConstraintError
    {
      return new VertexShaderLocalTransformer(this.context);
    }
  }

  private static class FunctionTransformer implements
    UASTIFunctionVisitor<UASTUDFunction, UASTUDFunctionArgument, UASTIChecked, UniqueBindersError>
  {
    private final @Nonnull Context context;

    public FunctionTransformer(
      final @Nonnull Context c)
    {
      this.context = c;
    }

    @Override public UASTUDFunctionArgument functionVisitArgument(
      final @Nonnull UASTIDFunctionArgument<UASTIChecked> f)
      throws UniqueBindersError,
        ConstraintError
    {
      final UASTUNameLocal name = this.context.addBinding(f.getName());
      return new UASTUDFunctionArgument(name, UniqueBinders.mapTypePath(f
        .getType()));
    }

    @Override public UASTUDFunction functionVisitDefined(
      final @Nonnull List<UASTUDFunctionArgument> arguments,
      final @Nonnull UASTIDFunctionDefined<UASTIChecked> f)
      throws UniqueBindersError,
        ConstraintError
    {
      final TokenIdentifierLower name = f.getName();
      final UASTUExpression body =
        f.getBody().expressionVisitableAccept(
          new ExpressionTransformer(this.context));

      return new UASTUDFunctionDefined(
        name,
        arguments,
        UniqueBinders.mapTypePath(f.getReturnType()),
        body);
    }

    @Override public void functionVisitDefinedPre(
      final @Nonnull UASTIDFunctionDefined<UASTIChecked> f)
      throws UniqueBindersError,
        ConstraintError
    {
      // Nothing
    }

    @Override public UASTUDFunction functionVisitExternal(
      final @Nonnull List<UASTUDFunctionArgument> arguments,
      final @Nonnull UASTIDFunctionExternal<UASTIChecked> f)
      throws UniqueBindersError,
        ConstraintError
    {
      final TokenIdentifierLower name = f.getName();
      return new UASTUDFunctionExternal(
        name,
        arguments,
        UniqueBinders.mapTypePath(f.getReturnType()),
        f.getExternal());
    }

    @Override public void functionVisitExternalPre(
      final @Nonnull UASTIDFunctionExternal<UASTIChecked> f)
      throws UniqueBindersError,
        ConstraintError
    {
      // Nothing
    }
  }

  private static class LocalTransformer implements
    UASTILocalLevelVisitor<UASTUDValueLocal, UASTIChecked, UniqueBindersError>
  {
    private final @Nonnull Context context;

    public LocalTransformer(
      final @Nonnull Context context)
    {
      this.context = context;
    }

    @Override public UASTUDValueLocal localVisitValueLocal(
      final @Nonnull UASTIDValueLocal<UASTIChecked> v)
      throws UniqueBindersError,
        ConstraintError
    {
      final UASTUNameLocal name = this.context.addBinding(v.getName());
      final Option<UASTUTypePath> ascription =
        UniqueBinders.mapAscription(v.getAscription());

      final UASTUExpression expression =
        v.getExpression().expressionVisitableAccept(
          new ExpressionTransformer(this.context));

      return new UASTUDValueLocal(name, ascription, expression);
    }
  }

  private static class ModuleContext
  {
    private final @Nonnull AtomicInteger next;

    public ModuleContext()
    {
      this.next = new AtomicInteger(0);
    }

    int getNext()
    {
      return this.next.incrementAndGet();
    }
  }

  private static class ModuleLevelTransformer implements
    UASTIModuleLevelDeclarationVisitor<UASTUDeclarationModuleLevel, UASTIChecked, UniqueBindersError>
  {
    private final @Nonnull ModuleContext module;

    public ModuleLevelTransformer(
      final @Nonnull ModuleContext module)
    {
      this.module = module;
    }

    @Override public UASTUDShaderFragment moduleVisitFragmentShader(
      final @Nonnull UASTIDShaderFragment<UASTIChecked> f)
      throws UniqueBindersError,
        ConstraintError
    {
      final Context c = Context.initialContext(this.module);
      return f
        .fragmentShaderVisitableAccept(new FragmentShaderTransformer(c));
    }

    @Override public UASTUDFunction moduleVisitFunctionDefined(
      final @Nonnull UASTIDFunctionDefined<UASTIChecked> f)
      throws UniqueBindersError,
        ConstraintError
    {
      final Context c = Context.initialContext(this.module);
      return f.functionVisitableAccept(new FunctionTransformer(c));
    }

    @Override public UASTUDFunction moduleVisitFunctionExternal(
      final @Nonnull UASTIDFunctionExternal<UASTIChecked> f)
      throws UniqueBindersError,
        ConstraintError
    {
      final Context c = Context.initialContext(this.module);
      return f.functionVisitableAccept(new FunctionTransformer(c));
    }

    @Override public UASTUDShaderProgram moduleVisitProgramShader(
      final @Nonnull UASTIDShaderProgram<UASTIChecked> p)
      throws UniqueBindersError,
        ConstraintError
    {
      return new UASTUDShaderProgram(
        p.getName(),
        UniqueBinders.mapShaderPath(p.getVertexShader()),
        UniqueBinders.mapShaderPath(p.getFragmentShader()));
    }

    @Override public UASTUDTypeRecord moduleVisitTypeRecord(
      final @Nonnull UASTIDTypeRecord<UASTIChecked> r)
      throws UniqueBindersError,
        ConstraintError
    {
      final List<UASTUDTypeRecordField> fields =
        new ArrayList<UASTUDTypeRecordField>();
      for (final UASTIDTypeRecordField<UASTIChecked> f : r.getFields()) {
        fields.add(new UASTUDTypeRecordField(f.getName(), UniqueBinders
          .mapTypePath(f.getType())));
      }

      return new UASTUDTypeRecord(r.getName(), fields);
    }

    @Override public UASTUDValue moduleVisitValue(
      final @Nonnull UASTIDValue<UASTIChecked> v)
      throws UniqueBindersError,
        ConstraintError
    {
      final Context c = Context.initialContext(this.module);

      final UASTUExpression body =
        v.getExpression().expressionVisitableAccept(
          new ExpressionTransformer(c));

      final Option<UASTUTypePath> ascription =
        UniqueBinders.mapAscription(v.getAscription());

      return new UASTUDValue(v.getName(), ascription, body);
    }

    @Override public UASTUDShaderVertex moduleVisitVertexShader(
      final @Nonnull UASTIDShaderVertex<UASTIChecked> f)
      throws UniqueBindersError,
        ConstraintError
    {
      final Context c = Context.initialContext(this.module);
      return f.vertexShaderVisitableAccept(new VertexShaderTransformer(c));
    }
  }

  private static class ModuleTransformer implements
    UASTIModuleVisitor<UASTUDModule, UASTUDImport, UASTUDeclarationModuleLevel, UASTIChecked, UniqueBindersError>
  {
    public ModuleTransformer()
    {
      // Nothing
    }

    @Override public UASTUDModule moduleVisit(
      final @Nonnull List<UASTUDImport> imports,
      final @Nonnull List<UASTUDeclarationModuleLevel> declarations,
      final @Nonnull UASTIDModule<UASTIChecked> m)
      throws UniqueBindersError,
        ConstraintError
    {
      return new UASTUDModule(m.getName(), imports, declarations);
    }

    @Override public UASTUDImport moduleVisitImport(
      final @Nonnull UASTIDImport<UASTIChecked> i)
      throws UniqueBindersError,
        ConstraintError
    {
      return new UASTUDImport(i.getPath(), i.getRename());
    }

    @Override public
      UASTIModuleLevelDeclarationVisitor<UASTUDeclarationModuleLevel, UASTIChecked, UniqueBindersError>
      moduleVisitPre(
        final @Nonnull UASTIDModule<UASTIChecked> m)
        throws UniqueBindersError,
          ConstraintError
    {
      return new ModuleLevelTransformer(new ModuleContext());
    }
  }

  static @Nonnull Option<UASTUTypePath> mapAscription(
    final @Nonnull Option<UASTITypePath> original)
    throws ConstraintError
  {
    return original
      .mapPartial(new PartialFunction<UASTITypePath, UASTUTypePath, ConstraintError>() {
        @Override public UASTUTypePath call(
          final @Nonnull UASTITypePath x)
          throws ConstraintError
        {
          return UniqueBinders.mapTypePath(x);
        }
      });
  }

  static @Nonnull UASTUShaderPath mapShaderPath(
    final @Nonnull UASTIShaderPath path)
    throws ConstraintError
  {
    return new UASTUShaderPath(path.getModule(), path.getName());
  }

  static @Nonnull UASTUTypePath mapTypePath(
    final @Nonnull UASTITypePath type)
    throws ConstraintError
  {
    return new UASTUTypePath(type.getModule(), type.getName());
  }

  public static @Nonnull UniqueBinders newUniqueBinders(
    final @Nonnull UASTICompilation<UASTIChecked> compilation,
    final @Nonnull Log log)
    throws ConstraintError
  {
    return new UniqueBinders(compilation, log);
  }

  private final @Nonnull UASTICompilation<UASTIChecked> compilation;
  private final @Nonnull Log                            log;

  private UniqueBinders(
    final @Nonnull UASTICompilation<UASTIChecked> compilation,
    final @Nonnull Log log)
    throws ConstraintError
  {
    this.log = new Log(log, "unique-binders");
    this.compilation =
      Constraints.constrainNotNull(compilation, "Compilation");
  }

  public @Nonnull UASTUCompilation run()
    throws UniqueBindersError,
      ConstraintError
  {
    final Map<ModulePathFlat, UASTIDModule<UASTIChecked>> modules =
      this.compilation.getModules();
    final Map<ModulePathFlat, ModulePath> paths = this.compilation.getPaths();

    final Map<ModulePathFlat, UASTUDModule> modules_new =
      new HashMap<ModulePathFlat, UASTUDeclaration.UASTUDModule>();

    for (final ModulePathFlat path : modules.keySet()) {
      this.log.debug(String.format("module: %s", path.getActual()));

      final UASTIDModule<UASTIChecked> module = modules.get(path);
      assert module != null;

      final UASTUDModule u_module =
        module.moduleVisitableAccept(new ModuleTransformer());

      modules_new.put(path, u_module);
    }

    return new UASTUCompilation(modules_new, paths);
  }
}
