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
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jaux.functional.Option;
import com.io7m.jaux.functional.Option.None;
import com.io7m.jaux.functional.PartialFunction;
import com.io7m.jlog.Level;
import com.io7m.jlog.Log;
import com.io7m.jparasol.ModulePath;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.NameRestrictions;
import com.io7m.jparasol.NameRestrictions.NameRestricted;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.lexer.Token.TokenIdentifierUpper;
import com.io7m.jparasol.untyped.ast.checked.UASTCCompilation;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDExternal;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDFunctionArgument;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDFunctionDefined;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDFunctionExternal;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDImport;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDModule;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDShaderFragment;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDShaderFragmentInput;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDShaderFragmentLocalDiscard;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDShaderFragmentLocalValue;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDShaderFragmentOutput;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDShaderFragmentOutputAssignment;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDShaderFragmentParameter;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDShaderProgram;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDShaderVertex;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDShaderVertexInput;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDShaderVertexLocalValue;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDShaderVertexOutput;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDShaderVertexOutputAssignment;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDShaderVertexParameter;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDTerm;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDTypeRecord;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDTypeRecordField;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDValue;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDValueDefined;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDValueExternal;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDValueLocal;
import com.io7m.jparasol.untyped.ast.checked.UASTCExpression;
import com.io7m.jparasol.untyped.ast.checked.UASTCExpression.UASTCEApplication;
import com.io7m.jparasol.untyped.ast.checked.UASTCExpression.UASTCEBoolean;
import com.io7m.jparasol.untyped.ast.checked.UASTCExpression.UASTCEConditional;
import com.io7m.jparasol.untyped.ast.checked.UASTCExpression.UASTCEInteger;
import com.io7m.jparasol.untyped.ast.checked.UASTCExpression.UASTCELet;
import com.io7m.jparasol.untyped.ast.checked.UASTCExpression.UASTCENew;
import com.io7m.jparasol.untyped.ast.checked.UASTCExpression.UASTCEReal;
import com.io7m.jparasol.untyped.ast.checked.UASTCExpression.UASTCERecord;
import com.io7m.jparasol.untyped.ast.checked.UASTCExpression.UASTCERecordProjection;
import com.io7m.jparasol.untyped.ast.checked.UASTCExpression.UASTCESwizzle;
import com.io7m.jparasol.untyped.ast.checked.UASTCExpression.UASTCEVariable;
import com.io7m.jparasol.untyped.ast.checked.UASTCExpression.UASTCRecordFieldAssignment;
import com.io7m.jparasol.untyped.ast.checked.UASTCExpressionVisitor;
import com.io7m.jparasol.untyped.ast.checked.UASTCFragmentShaderLocalVisitor;
import com.io7m.jparasol.untyped.ast.checked.UASTCFragmentShaderVisitor;
import com.io7m.jparasol.untyped.ast.checked.UASTCFunctionVisitor;
import com.io7m.jparasol.untyped.ast.checked.UASTCLocalLevelVisitor;
import com.io7m.jparasol.untyped.ast.checked.UASTCModuleVisitor;
import com.io7m.jparasol.untyped.ast.checked.UASTCShaderPath;
import com.io7m.jparasol.untyped.ast.checked.UASTCShaderVisitor;
import com.io7m.jparasol.untyped.ast.checked.UASTCTermVisitor;
import com.io7m.jparasol.untyped.ast.checked.UASTCTypePath;
import com.io7m.jparasol.untyped.ast.checked.UASTCTypeVisitor;
import com.io7m.jparasol.untyped.ast.checked.UASTCValuePath;
import com.io7m.jparasol.untyped.ast.checked.UASTCValueVisitor;
import com.io7m.jparasol.untyped.ast.checked.UASTCVertexShaderLocalVisitor;
import com.io7m.jparasol.untyped.ast.checked.UASTCVertexShaderVisitor;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUCompilation;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDExternal;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDFunction;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDFunctionArgument;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDFunctionDefined;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDFunctionExternal;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDImport;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDModule;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDShader;
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
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDTerm;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDType;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDTypeRecord;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDTypeRecordField;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDValue;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDValueDefined;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDValueExternal;
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
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUShaderPath;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUTypePath;
import com.io7m.jparasol.untyped.ast.unique_binders.UniqueName;
import com.io7m.jparasol.untyped.ast.unique_binders.UniqueName.UniqueNameLocal;
import com.io7m.jparasol.untyped.ast.unique_binders.UniqueName.UniqueNameNonLocal;

public final class UniqueBinders
{
  private static final class Context
  {
    public static @Nonnull Context initialContext(
      final @Nonnull ModuleContext module,
      final @CheckForNull UASTCDTerm term,
      final @Nonnull Log log)
    {
      return new Context(
        module,
        null,
        term,
        new HashMap<String, UniqueNameLocal>(),
        log);
    }

    private final int                                   depth;
    private final @Nonnull Log                          log;
    private final @Nonnull ModuleContext                module;
    private final @Nonnull Map<String, UniqueNameLocal> names;
    private final @CheckForNull Context                 parent;
    private final @CheckForNull UASTCDTerm              root;

    public Context(
      final @Nonnull ModuleContext module,
      final @CheckForNull Context parent,
      final @CheckForNull UASTCDTerm root,
      final @Nonnull Map<String, UniqueNameLocal> names,
      final @Nonnull Log log)
    {
      this.module = module;
      this.parent = parent;
      this.names = names;
      this.log = log;
      this.root = root;
      this.depth = this.parent == null ? 1 : this.parent.getDepth() + 1;
    }

    public @Nonnull UniqueNameLocal addBinding(
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

      final UniqueNameLocal u = new UniqueNameLocal(name, s.toString());
      this.names.put(name.getActual(), u);

      if (this.log.enabled(Level.LOG_DEBUG)) {
        final StringBuilder m = new StringBuilder();
        m.append("[");
        m.append(this.depth);
        m.append("] Added binding ");
        m.append(u.show());
        m.append(" for ");
        m.append(name.getActual());
        this.log.debug(m.toString());
      }
      return u;
    }

    private int getDepth()
    {
      return this.depth;
    }

    public @Nonnull UniqueName getName(
      final @Nonnull TokenIdentifierLower name)
      throws ConstraintError
    {
      final UniqueName result = this.getNameInternal(name);

      if (this.log.enabled(Level.LOG_DEBUG)) {
        final StringBuilder m = new StringBuilder();
        m.append("[");
        m.append(this.depth);
        m.append("] Retrieved name ");
        m.append(result != null ? result.show() : "<null>");
        m.append(" for ");
        m.append(name.getActual());
        this.log.debug(m.toString());
      }

      return result;
    }

    public @Nonnull UniqueName getNameFromValuePath(
      final @Nonnull UASTCValuePath name)
      throws ConstraintError
    {
      final Option<TokenIdentifierUpper> m = name.getModule();
      if (m.isSome()) {
        return new UniqueNameNonLocal(m, name.getName());
      }
      return this.getName(name.getName());
    }

    private @Nonnull UniqueName getNameInternal(
      final @Nonnull TokenIdentifierLower name)
      throws ConstraintError
    {
      if (this.names.containsKey(name.getActual())) {
        return this.names.get(name.getActual());
      }
      if (this.parent == null) {
        final None<TokenIdentifierUpper> none = Option.none();
        return new UniqueNameNonLocal(none, name);
      }
      return this.parent.getNameInternal(name);
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
        if (this.root != null) {
          if (this.root.getName().getActual().equals(name)) {
            return true;
          }
        }
        return false;
      }
      return this.parent.nameExists(name);
    }

    public @Nonnull Context withNew()
    {
      return new Context(
        this.module,
        this,
        this.root,
        new HashMap<String, UniqueName.UniqueNameLocal>(),
        this.log);
    }
  }

  private static final class ExpressionTransformer implements
    UASTCExpressionVisitor<UASTUExpression, UASTUDValueLocal, UniqueBindersError>
  {
    private @Nonnull Context context;

    public ExpressionTransformer(
      final @Nonnull Context context)
    {
      this.context = context;
    }

    @Override public UASTUEApplication expressionVisitApplication(
      final @Nonnull List<UASTUExpression> arguments,
      final @Nonnull UASTCEApplication e)
      throws UniqueBindersError,
        ConstraintError
    {
      return new UASTUEApplication(this.context.getNameFromValuePath(e
        .getName()), arguments);
    }

    @Override public void expressionVisitApplicationPre(
      final @Nonnull UASTCEApplication e)
      throws UniqueBindersError,
        ConstraintError
    {
      // Nothing
    }

    @Override public UASTUEBoolean expressionVisitBoolean(
      final @Nonnull UASTCEBoolean e)
      throws UniqueBindersError,
        ConstraintError
    {
      return new UASTUEBoolean(e.getToken());
    }

    @Override public @Nonnull UASTUEConditional expressionVisitConditional(
      final @Nonnull UASTUExpression condition,
      final @Nonnull UASTUExpression left,
      final @Nonnull UASTUExpression right,
      final @Nonnull UASTCEConditional e)
      throws UniqueBindersError,
        ConstraintError
    {
      return new UASTUEConditional(e.getIf(), condition, left, right);
    }

    @Override public void expressionVisitConditionalPre(
      final @Nonnull UASTCEConditional e)
      throws UniqueBindersError,
        ConstraintError
    {
      // Nothing
    }

    @Override public @Nonnull UASTUEInteger expressionVisitInteger(
      final @Nonnull UASTCEInteger e)
      throws UniqueBindersError,
        ConstraintError
    {
      return new UASTUEInteger(e.getToken());
    }

    @Override public @Nonnull UASTUELet expressionVisitLet(
      final @Nonnull List<UASTUDValueLocal> bindings,
      final @Nonnull UASTUExpression body,
      final @Nonnull UASTCELet e)
      throws UniqueBindersError,
        ConstraintError
    {
      final Context p = this.context.getParent();
      assert p != null;
      this.context = p;
      return new UASTUELet(e.getToken(), bindings, body);
    }

    @Override public
      UASTCLocalLevelVisitor<UASTUDValueLocal, UniqueBindersError>
      expressionVisitLetPre(
        final @Nonnull UASTCELet e)
        throws UniqueBindersError,
          ConstraintError
    {
      this.context = this.context.withNew();
      return new LocalTransformer(this.context);
    }

    @Override public @Nonnull UASTUENew expressionVisitNew(
      final @Nonnull List<UASTUExpression> arguments,
      final @Nonnull UASTCENew e)
      throws UniqueBindersError,
        ConstraintError
    {
      return new UASTUENew(UniqueBinders.mapTypePath(e.getName()), arguments);
    }

    @Override public void expressionVisitNewPre(
      final @Nonnull UASTCENew e)
      throws UniqueBindersError,
        ConstraintError
    {
      // Nothing
    }

    @Override public @Nonnull UASTUEReal expressionVisitReal(
      final @Nonnull UASTCEReal e)
      throws UniqueBindersError,
        ConstraintError
    {
      return new UASTUEReal(e.getToken());
    }

    @Override public @Nonnull UASTUERecord expressionVisitRecord(
      final @Nonnull UASTCERecord e)
      throws UniqueBindersError,
        ConstraintError
    {
      final List<UASTURecordFieldAssignment> fields =
        new ArrayList<UASTURecordFieldAssignment>();

      for (final UASTCRecordFieldAssignment f : e.getAssignments()) {
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
        final @Nonnull UASTCERecordProjection e)
        throws UniqueBindersError,
          ConstraintError
    {
      return new UASTUERecordProjection(body, e.getField());
    }

    @Override public void expressionVisitRecordProjectionPre(
      final @Nonnull UASTCERecordProjection e)
      throws UniqueBindersError,
        ConstraintError
    {
      // Nothing
    }

    @Override public @Nonnull UASTUESwizzle expressionVisitSwizzle(
      final @Nonnull UASTUExpression body,
      final @Nonnull UASTCESwizzle e)
      throws UniqueBindersError,
        ConstraintError
    {
      return new UASTUESwizzle(body, e.getFields());
    }

    @Override public void expressionVisitSwizzlePre(
      final @Nonnull UASTCESwizzle e)
      throws UniqueBindersError,
        ConstraintError
    {
      // Nothing
    }

    @Override public @Nonnull UASTUEVariable expressionVisitVariable(
      final @Nonnull UASTCEVariable e)
      throws UniqueBindersError,
        ConstraintError
    {
      final UniqueName name = this.context.getNameFromValuePath(e.getName());
      return new UASTUEVariable(name);
    }
  }

  private static final class FragmentShaderTransformer implements
    UASTCFragmentShaderVisitor<UASTUDShaderFragment, UASTUDShaderFragmentInput, UASTUDShaderFragmentParameter, UASTUDShaderFragmentOutput, UASTUDShaderFragmentLocal, UASTUDShaderFragmentOutputAssignment, UniqueBindersError>,
    UASTCFragmentShaderLocalVisitor<UASTUDShaderFragmentLocal, UniqueBindersError>
  {
    private final @Nonnull Context context;

    public FragmentShaderTransformer(
      final @Nonnull Context context)
    {
      this.context = context.withNew();
    }

    @Override public
      UASTUDShaderFragment
      fragmentShaderVisit(
        final @Nonnull List<UASTUDShaderFragmentInput> inputs,
        final @Nonnull List<UASTUDShaderFragmentParameter> parameters,
        final @Nonnull List<UASTUDShaderFragmentOutput> outputs,
        final @Nonnull List<UASTUDShaderFragmentLocal> locals,
        final @Nonnull List<UASTUDShaderFragmentOutputAssignment> output_assignments,
        final @Nonnull UASTCDShaderFragment f)
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

    @Override public UASTUDShaderFragmentInput fragmentShaderVisitInput(
      final @Nonnull UASTCDShaderFragmentInput i)
      throws UniqueBindersError,
        ConstraintError
    {
      final UniqueNameLocal name = this.context.addBinding(i.getName());
      final UASTUTypePath type = UniqueBinders.mapTypePath(i.getType());
      return new UASTUDShaderFragmentInput(name, type);
    }

    @Override public
      UASTUDShaderFragmentLocalDiscard
      fragmentShaderVisitLocalDiscard(
        final @Nonnull UASTCDShaderFragmentLocalDiscard d)
        throws UniqueBindersError,
          ConstraintError
    {
      final UASTUExpression ex =
        d.getExpression().expressionVisitableAccept(
          new ExpressionTransformer(this.context));
      return new UASTUDShaderFragmentLocalDiscard(d.getDiscard(), ex);
    }

    @Override public
      UASTCFragmentShaderLocalVisitor<UASTUDShaderFragmentLocal, UniqueBindersError>
      fragmentShaderVisitLocalsPre()
        throws UniqueBindersError,
          ConstraintError
    {
      return this;
    }

    @Override public
      UASTUDShaderFragmentLocalValue
      fragmentShaderVisitLocalValue(
        final UASTCDShaderFragmentLocalValue v)
        throws UniqueBindersError,
          ConstraintError
    {
      final UASTCDValueLocal value = v.getValue();
      final UASTUDValueLocal value_new =
        value.localVisitableAccept(new LocalTransformer(this.context));
      return new UASTUDShaderFragmentLocalValue(value_new);
    }

    @Override public UASTUDShaderFragmentOutput fragmentShaderVisitOutput(
      final @Nonnull UASTCDShaderFragmentOutput o)
      throws UniqueBindersError,
        ConstraintError
    {
      final UniqueNameLocal name =
        new UniqueNameLocal(o.getName(), o.getName().getActual());
      final UASTUTypePath type = UniqueBinders.mapTypePath(o.getType());
      return new UASTUDShaderFragmentOutput(name, type, o.getIndex());
    }

    @Override public
      UASTUDShaderFragmentOutputAssignment
      fragmentShaderVisitOutputAssignment(
        final @Nonnull UASTCDShaderFragmentOutputAssignment a)
        throws UniqueBindersError,
          ConstraintError
    {
      final UASTCValuePath path = a.getVariable().getName();
      final UASTUEVariable variable =
        new UASTUEVariable(this.context.getNameFromValuePath(path));
      return new UASTUDShaderFragmentOutputAssignment(a.getName(), variable);
    }

    @Override public
      UASTUDShaderFragmentParameter
      fragmentShaderVisitParameter(
        final @Nonnull UASTCDShaderFragmentParameter p)
        throws UniqueBindersError,
          ConstraintError
    {
      final UniqueNameLocal name = this.context.addBinding(p.getName());
      final UASTUTypePath type = UniqueBinders.mapTypePath(p.getType());
      return new UASTUDShaderFragmentParameter(name, type);
    }
  }

  private static final class ValueTransformer implements
    UASTCValueVisitor<UASTUDValue, UniqueBindersError>
  {
    private final @Nonnull Context context;

    public ValueTransformer(
      final @Nonnull Context c)
    {
      this.context = c;
    }

    @Override public UASTUDValueDefined valueVisitDefined(
      final @Nonnull UASTCDValueDefined v)
      throws UniqueBindersError,
        ConstraintError
    {
      final Option<UASTUTypePath> ascription =
        UniqueBinders.mapAscription(v.getAscription());

      final UASTUExpression expression =
        v.getExpression().expressionVisitableAccept(
          new ExpressionTransformer(this.context));

      return new UASTUDValueDefined(v.getName(), ascription, expression);
    }

    @Override public UASTUDValueExternal valueVisitExternal(
      final @Nonnull UASTCDValueExternal v)
      throws UniqueBindersError,
        ConstraintError
    {
      final UASTUTypePath ascription =
        UniqueBinders.mapTypePath(v.getAscription());

      final UASTCDExternal original_external = v.getExternal();
      final Option<UASTUExpression> none = Option.none();
      final UASTUDExternal external =
        new UASTUDExternal(
          original_external.getName(),
          original_external.isVertexShaderAllowed(),
          original_external.isFragmentShaderAllowed(),
          none);
      return new UASTUDValueExternal(v.getName(), ascription, external);
    }
  }

  private static final class FunctionTransformer implements
    UASTCFunctionVisitor<UASTUDFunction, UASTUDFunctionArgument, UniqueBindersError>
  {
    private final @Nonnull Context context;

    public FunctionTransformer(
      final @Nonnull Context c)
    {
      this.context = c;
    }

    @Override public UASTUDFunctionArgument functionVisitArgument(
      final @Nonnull UASTCDFunctionArgument f)
      throws UniqueBindersError,
        ConstraintError
    {
      final UniqueNameLocal name = this.context.addBinding(f.getName());
      return new UASTUDFunctionArgument(name, UniqueBinders.mapTypePath(f
        .getType()));
    }

    @Override public UASTUDFunction functionVisitDefined(
      final @Nonnull List<UASTUDFunctionArgument> arguments,
      final @Nonnull UASTCDFunctionDefined f)
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
      final @Nonnull UASTCDFunctionDefined f)
      throws UniqueBindersError,
        ConstraintError
    {
      // Nothing
    }

    @Override public UASTUDFunction functionVisitExternal(
      final @Nonnull List<UASTUDFunctionArgument> arguments,
      final @Nonnull UASTCDFunctionExternal f)
      throws UniqueBindersError,
        ConstraintError
    {
      final UASTCDExternal ext = f.getExternal();
      final TokenIdentifierLower name = f.getName();

      final Option<UASTCExpression> original_emulation = ext.getEmulation();
      final Option<UASTUExpression> emulation =
        original_emulation
          .mapPartial(new PartialFunction<UASTCExpression, UASTUExpression, UniqueBindersError>() {
            @SuppressWarnings("synthetic-access") @Override public
              UASTUExpression
              call(
                final @Nonnull UASTCExpression x)
                throws UniqueBindersError
            {
              try {
                return x.expressionVisitableAccept(new ExpressionTransformer(
                  FunctionTransformer.this.context));
              } catch (final ConstraintError e) {
                throw new UnreachableCodeException(e);
              }
            }
          });

      return new UASTUDFunctionExternal(
        name,
        arguments,
        UniqueBinders.mapTypePath(f.getReturnType()),
        new UASTUDExternal(
          ext.getName(),
          ext.isVertexShaderAllowed(),
          ext.isFragmentShaderAllowed(),
          emulation));
    }

    @Override public void functionVisitExternalPre(
      final @Nonnull UASTCDFunctionExternal f)
      throws UniqueBindersError,
        ConstraintError
    {
      // Nothing
    }
  }

  private static final class LocalTransformer implements
    UASTCLocalLevelVisitor<UASTUDValueLocal, UniqueBindersError>
  {
    private final @Nonnull Context context;

    public LocalTransformer(
      final @Nonnull Context context)
    {
      this.context = context;
    }

    @Override public UASTUDValueLocal localVisitValueLocal(
      final @Nonnull UASTCDValueLocal v)
      throws UniqueBindersError,
        ConstraintError
    {
      final Option<UASTUTypePath> ascription =
        UniqueBinders.mapAscription(v.getAscription());

      final UASTUExpression expression =
        v.getExpression().expressionVisitableAccept(
          new ExpressionTransformer(this.context));

      final UniqueNameLocal name = this.context.addBinding(v.getName());
      return new UASTUDValueLocal(name, ascription, expression);
    }
  }

  private static final class ModuleContext
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

  private static final class ModuleTransformer implements
    UASTCModuleVisitor<UASTUDModule, UASTUDImport, UASTUDeclarationModuleLevel, UASTUDTerm, UASTUDType, UASTUDShader, UniqueBindersError>,
    UASTCTypeVisitor<UASTUDType, UniqueBindersError>,
    UASTCTermVisitor<UASTUDTerm, UniqueBindersError>,
    UASTCShaderVisitor<UASTUDShader, UniqueBindersError>
  {
    private final @Nonnull ModuleContext context;
    private final @Nonnull Log           log;

    public ModuleTransformer(
      final @Nonnull Log log)
    {
      this.context = new ModuleContext();
      this.log = new Log(log, "module-transformer");
    }

    @Override public
      UASTCShaderVisitor<UASTUDShader, UniqueBindersError>
      moduleShadersPre(
        final @Nonnull UASTCDModule m)
        throws UniqueBindersError,
          ConstraintError
    {
      return this;
    }

    @Override public
      UASTCTermVisitor<UASTUDTerm, UniqueBindersError>
      moduleTermsPre(
        final @Nonnull UASTCDModule m)
        throws UniqueBindersError,
          ConstraintError
    {
      return this;
    }

    @Override public
      UASTCTypeVisitor<UASTUDType, UniqueBindersError>
      moduleTypesPre(
        final @Nonnull UASTCDModule m)
        throws UniqueBindersError,
          ConstraintError
    {
      return this;
    }

    @Override public UASTUDModule moduleVisit(
      final @Nonnull List<UASTUDImport> imports,
      final @Nonnull List<UASTUDeclarationModuleLevel> declarations,
      final @Nonnull Map<String, UASTUDTerm> terms,
      final @Nonnull Map<String, UASTUDType> types,
      final @Nonnull Map<String, UASTUDShader> shaders,
      final @Nonnull UASTCDModule m)
      throws UniqueBindersError,
        ConstraintError
    {
      final Map<String, UASTCDImport> import_names = m.getImportedNames();
      final Map<String, UASTUDImport> r_import_names =
        new HashMap<String, UASTUDImport>();
      for (final String in : import_names.keySet()) {
        final UASTCDImport i = import_names.get(in);
        r_import_names.put(in, new UASTUDImport(i.getPath(), i.getRename()));
      }

      final Map<String, UASTCDImport> import_renames = m.getImportedRenames();
      final Map<String, UASTUDImport> r_import_renames =
        new HashMap<String, UASTUDImport>();
      for (final String in : import_renames.keySet()) {
        final UASTCDImport i = import_renames.get(in);
        final UASTUDImport ri = new UASTUDImport(i.getPath(), i.getRename());
        r_import_renames.put(in, ri);
      }

      final Map<ModulePathFlat, UASTCDImport> import_modules =
        m.getImportedModules();
      final Map<ModulePathFlat, UASTUDImport> r_import_modules =
        new HashMap<ModulePathFlat, UASTUDImport>();
      for (final ModulePathFlat in : import_modules.keySet()) {
        final UASTCDImport i = import_modules.get(in);
        r_import_modules
          .put(in, new UASTUDImport(i.getPath(), i.getRename()));
      }

      return new UASTUDModule(
        m.getPath(),
        imports,
        r_import_modules,
        r_import_names,
        r_import_renames,
        declarations,
        terms,
        types,
        shaders);
    }

    @Override public UASTUDShaderFragment moduleVisitFragmentShader(
      final @Nonnull UASTCDShaderFragment f)
      throws UniqueBindersError,
        ConstraintError
    {
      return f.fragmentShaderVisitableAccept(new FragmentShaderTransformer(
        Context.initialContext(this.context, null, this.log)));
    }

    @Override public UASTUDImport moduleVisitImport(
      final @Nonnull UASTCDImport i)
      throws UniqueBindersError,
        ConstraintError
    {
      return new UASTUDImport(i.getPath(), i.getRename());
    }

    @Override public UASTUDShader moduleVisitProgramShader(
      final @Nonnull UASTCDShaderProgram p)
      throws UniqueBindersError,
        ConstraintError
    {
      return new UASTUDShaderProgram(
        p.getName(),
        UniqueBinders.mapShaderPath(p.getVertexShader()),
        UniqueBinders.mapShaderPath(p.getFragmentShader()));
    }

    @Override public UASTUDShader moduleVisitVertexShader(
      final @Nonnull UASTCDShaderVertex v)
      throws UniqueBindersError,
        ConstraintError
    {
      return v.vertexShaderVisitableAccept(new VertexShaderTransformer(
        Context.initialContext(this.context, null, this.log)));
    }

    @Override public UASTUDTerm termVisitFunctionDefined(
      final @Nonnull UASTCDFunctionDefined f)
      throws UniqueBindersError,
        ConstraintError
    {
      final Context ctx = Context.initialContext(this.context, f, this.log);
      return f.functionVisitableAccept(new FunctionTransformer(ctx));
    }

    @Override public UASTUDTerm termVisitFunctionExternal(
      final @Nonnull UASTCDFunctionExternal f)
      throws UniqueBindersError,
        ConstraintError
    {
      final Context ctx = Context.initialContext(this.context, f, this.log);
      return f.functionVisitableAccept(new FunctionTransformer(ctx));
    }

    @Override public UASTUDValue termVisitValue(
      final @Nonnull UASTCDValue v)
      throws UniqueBindersError,
        ConstraintError
    {
      final Context ctx = Context.initialContext(this.context, v, this.log);
      return v.valueVisitableAccept(new ValueTransformer(ctx));
    }

    @Override public UASTUDTypeRecord typeVisitTypeRecord(
      final @Nonnull UASTCDTypeRecord r)
      throws UniqueBindersError,
        ConstraintError
    {
      final List<UASTUDTypeRecordField> fields =
        new ArrayList<UASTUDTypeRecordField>();

      for (final UASTCDTypeRecordField f : r.getFields()) {
        fields.add(new UASTUDTypeRecordField(f.getName(), UniqueBinders
          .mapTypePath(f.getType())));
      }

      return new UASTUDTypeRecord(r.getName(), fields);
    }

    @Override public UASTUDTerm termVisitValueExternal(
      final @Nonnull UASTCDValueExternal v)
      throws UniqueBindersError,
        ConstraintError
    {
      final Context ctx = Context.initialContext(this.context, v, this.log);
      return v.valueVisitableAccept(new ValueTransformer(ctx));
    }
  }

  private static final class VertexShaderTransformer implements
    UASTCVertexShaderVisitor<UASTUDShaderVertex, UASTUDShaderVertexInput, UASTUDShaderVertexParameter, UASTUDShaderVertexOutput, UASTUDShaderVertexLocalValue, UASTUDShaderVertexOutputAssignment, UniqueBindersError>,
    UASTCVertexShaderLocalVisitor<UASTUDShaderVertexLocalValue, UniqueBindersError>
  {
    private final @Nonnull Context context;

    public VertexShaderTransformer(
      final @Nonnull Context context)
    {
      this.context = context;
    }

    @Override public
      UASTUDShaderVertex
      vertexShaderVisit(
        final @Nonnull List<UASTUDShaderVertexInput> inputs,
        final @Nonnull List<UASTUDShaderVertexParameter> parameters,
        final @Nonnull List<UASTUDShaderVertexOutput> outputs,
        final @Nonnull List<UASTUDShaderVertexLocalValue> locals,
        final @Nonnull List<UASTUDShaderVertexOutputAssignment> output_assignments,
        final @Nonnull UASTCDShaderVertex v)
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

    @Override public UASTUDShaderVertexInput vertexShaderVisitInput(
      final @Nonnull UASTCDShaderVertexInput i)
      throws UniqueBindersError,
        ConstraintError
    {
      final UniqueNameLocal name = this.context.addBinding(i.getName());
      final UASTUTypePath type = UniqueBinders.mapTypePath(i.getType());
      return new UASTUDShaderVertexInput(name, type);
    }

    @Override public
      UASTCVertexShaderLocalVisitor<UASTUDShaderVertexLocalValue, UniqueBindersError>
      vertexShaderVisitLocalsPre()
        throws UniqueBindersError,
          ConstraintError
    {
      return this;
    }

    @Override public
      UASTUDShaderVertexLocalValue
      vertexShaderVisitLocalValue(
        final UASTCDShaderVertexLocalValue v)
        throws UniqueBindersError,
          ConstraintError
    {
      final UASTCDValueLocal value = v.getValue();
      final UASTUDValueLocal value_new =
        value.localVisitableAccept(new LocalTransformer(this.context));
      return new UASTUDShaderVertexLocalValue(value_new);
    }

    @Override public UASTUDShaderVertexOutput vertexShaderVisitOutput(
      final @Nonnull UASTCDShaderVertexOutput o)
      throws UniqueBindersError,
        ConstraintError
    {
      final UniqueNameLocal name =
        new UniqueNameLocal(o.getName(), o.getName().getActual());
      final UASTUTypePath type = UniqueBinders.mapTypePath(o.getType());
      return new UASTUDShaderVertexOutput(name, type, o.isMain());
    }

    @Override public
      UASTUDShaderVertexOutputAssignment
      vertexShaderVisitOutputAssignment(
        final @Nonnull UASTCDShaderVertexOutputAssignment a)
        throws UniqueBindersError,
          ConstraintError
    {
      final UASTCValuePath path = a.getVariable().getName();
      final UASTUEVariable variable =
        new UASTUEVariable(this.context.getNameFromValuePath(path));
      return new UASTUDShaderVertexOutputAssignment(a.getName(), variable);
    }

    @Override public UASTUDShaderVertexParameter vertexShaderVisitParameter(
      final @Nonnull UASTCDShaderVertexParameter p)
      throws UniqueBindersError,
        ConstraintError
    {
      final UniqueNameLocal name = this.context.addBinding(p.getName());
      final UASTUTypePath type = UniqueBinders.mapTypePath(p.getType());
      return new UASTUDShaderVertexParameter(name, type);
    }
  }

  static @Nonnull Option<UASTUTypePath> mapAscription(
    final @Nonnull Option<UASTCTypePath> original)
    throws ConstraintError
  {
    return original
      .mapPartial(new PartialFunction<UASTCTypePath, UASTUTypePath, ConstraintError>() {
        @Override public UASTUTypePath call(
          final @Nonnull UASTCTypePath x)
          throws ConstraintError
        {
          return UniqueBinders.mapTypePath(x);
        }
      });
  }

  static @Nonnull UASTUShaderPath mapShaderPath(
    final @Nonnull UASTCShaderPath path)
    throws ConstraintError
  {
    return new UASTUShaderPath(path.getModule(), path.getName());
  }

  static @Nonnull UASTUTypePath mapTypePath(
    final @Nonnull UASTCTypePath type)
    throws ConstraintError
  {
    return new UASTUTypePath(type.getModule(), type.getName());
  }

  public static @Nonnull UniqueBinders newUniqueBinders(
    final @Nonnull UASTCCompilation compilation,
    final @Nonnull Log log)
    throws ConstraintError
  {
    return new UniqueBinders(compilation, log);
  }

  private final @Nonnull UASTCCompilation compilation;
  private final @Nonnull Log              log;

  private UniqueBinders(
    final @Nonnull UASTCCompilation compilation,
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
    final Map<ModulePathFlat, UASTCDModule> modules =
      this.compilation.getModules();
    final Map<ModulePathFlat, ModulePath> paths = this.compilation.getPaths();

    final Map<ModulePathFlat, UASTUDModule> modules_new =
      new HashMap<ModulePathFlat, UASTUDeclaration.UASTUDModule>();

    for (final ModulePathFlat path : modules.keySet()) {
      this.log.debug(String.format("module: %s", path.getActual()));

      final UASTCDModule module = modules.get(path);
      assert module != null;

      final UASTUDModule u_module =
        module.moduleVisitableAccept(new ModuleTransformer(this.log));

      modules_new.put(path, u_module);
    }

    return new UASTUCompilation(modules_new, paths);
  }
}
