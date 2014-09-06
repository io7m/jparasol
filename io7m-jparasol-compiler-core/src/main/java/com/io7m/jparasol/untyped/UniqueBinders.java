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

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jfunctional.Option;
import com.io7m.jfunctional.OptionType;
import com.io7m.jfunctional.Pair;
import com.io7m.jfunctional.PartialFunctionType;
import com.io7m.jlog.LogLevel;
import com.io7m.jlog.LogUsableType;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jparasol.ModulePath;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.NameRestrictions;
import com.io7m.jparasol.NameRestrictions.NameRestricted;
import com.io7m.jparasol.lexer.TokenIdentifierLower;
import com.io7m.jparasol.lexer.TokenIdentifierUpper;
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
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDShaderFragmentOutputAssignment;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDShaderFragmentOutputData;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDShaderFragmentOutputDepth;
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
import com.io7m.jparasol.untyped.ast.checked.UASTCEApplication;
import com.io7m.jparasol.untyped.ast.checked.UASTCEBoolean;
import com.io7m.jparasol.untyped.ast.checked.UASTCEConditional;
import com.io7m.jparasol.untyped.ast.checked.UASTCEInteger;
import com.io7m.jparasol.untyped.ast.checked.UASTCELet;
import com.io7m.jparasol.untyped.ast.checked.UASTCEMatch;
import com.io7m.jparasol.untyped.ast.checked.UASTCENew;
import com.io7m.jparasol.untyped.ast.checked.UASTCEReal;
import com.io7m.jparasol.untyped.ast.checked.UASTCERecord;
import com.io7m.jparasol.untyped.ast.checked.UASTCERecordProjection;
import com.io7m.jparasol.untyped.ast.checked.UASTCESwizzle;
import com.io7m.jparasol.untyped.ast.checked.UASTCEVariable;
import com.io7m.jparasol.untyped.ast.checked.UASTCExpressionMatchConstantVisitorType;
import com.io7m.jparasol.untyped.ast.checked.UASTCExpressionType;
import com.io7m.jparasol.untyped.ast.checked.UASTCExpressionVisitorType;
import com.io7m.jparasol.untyped.ast.checked.UASTCFragmentShaderLocalVisitorType;
import com.io7m.jparasol.untyped.ast.checked.UASTCFragmentShaderOutputVisitorType;
import com.io7m.jparasol.untyped.ast.checked.UASTCFragmentShaderVisitorType;
import com.io7m.jparasol.untyped.ast.checked.UASTCFunctionVisitorType;
import com.io7m.jparasol.untyped.ast.checked.UASTCLocalLevelVisitorType;
import com.io7m.jparasol.untyped.ast.checked.UASTCModuleVisitorType;
import com.io7m.jparasol.untyped.ast.checked.UASTCRecordFieldAssignment;
import com.io7m.jparasol.untyped.ast.checked.UASTCShaderPath;
import com.io7m.jparasol.untyped.ast.checked.UASTCShaderVisitorType;
import com.io7m.jparasol.untyped.ast.checked.UASTCTermVisitorType;
import com.io7m.jparasol.untyped.ast.checked.UASTCTypePath;
import com.io7m.jparasol.untyped.ast.checked.UASTCTypeVisitorType;
import com.io7m.jparasol.untyped.ast.checked.UASTCValuePath;
import com.io7m.jparasol.untyped.ast.checked.UASTCValueVisitorType;
import com.io7m.jparasol.untyped.ast.checked.UASTCVertexShaderLocalVisitorType;
import com.io7m.jparasol.untyped.ast.checked.UASTCVertexShaderVisitorType;
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
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDType;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDTypeRecord;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDTypeRecordField;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDValue;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDValueDefined;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDValueExternal;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDValueLocal;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDeclarationModuleLevel;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUEApplication;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUEBoolean;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUEConditional;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUEInteger;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUELet;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUEMatch;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUENew;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUEReal;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUERecord;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUERecordProjection;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUESwizzle;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUEVariable;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpressionMatchConstantType;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpressionType;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTURecordFieldAssignment;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUShaderPath;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUTypePath;
import com.io7m.jparasol.untyped.ast.unique_binders.UniqueName;
import com.io7m.jparasol.untyped.ast.unique_binders.UniqueName.UniqueNameLocal;
import com.io7m.jparasol.untyped.ast.unique_binders.UniqueName.UniqueNameNonLocal;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * The unique binding phase.
 */

@EqualityReference public final class UniqueBinders
{
  @EqualityReference private static final class Context
  {
    public static Context initialContext(
      final ModuleContext module,
      final @Nullable UASTCDTerm term,
      final LogUsableType log)
    {
      return new Context(
        module,
        null,
        term,
        new HashMap<String, UniqueNameLocal>(),
        log);
    }

    private final int                          depth;
    private final LogUsableType                log;
    private final ModuleContext                module;
    private final Map<String, UniqueNameLocal> names;
    private final @Nullable Context            parent;
    private final @Nullable UASTCDTerm         root;

    public Context(
      final ModuleContext in_module,
      final @Nullable Context in_parent,
      final @Nullable UASTCDTerm in_root,
      final Map<String, UniqueNameLocal> in_names,
      final LogUsableType in_log)
    {
      this.module = in_module;
      this.parent = in_parent;
      this.names = in_names;
      this.log = in_log;
      this.root = in_root;

      final Context p = this.parent;
      if (p == null) {
        this.depth = 1;
      } else {
        this.depth = p.getDepth() + 1;
      }
    }

    public UniqueNameLocal addBinding(
      final TokenIdentifierLower name)
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

      if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
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

    public UniqueName getName(
      final TokenIdentifierLower name)
    {
      final UniqueName result = this.getNameInternal(name);

      if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
        final StringBuilder m = new StringBuilder();
        m.append("[");
        m.append(this.depth);
        m.append("] Retrieved name ");
        m.append(result.show());
        m.append(" for ");
        m.append(name.getActual());
        this.log.debug(m.toString());
      }

      return result;
    }

    public UniqueName getNameFromValuePath(
      final UASTCValuePath name)
    {
      final OptionType<TokenIdentifierUpper> m = name.getModule();
      if (m.isSome()) {
        return new UniqueNameNonLocal(m, name.getName());
      }
      return this.getName(name.getName());
    }

    private UniqueName getNameInternal(
      final TokenIdentifierLower name)
    {
      if (this.names.containsKey(name.getActual())) {
        final UniqueNameLocal n = this.names.get(name.getActual());
        assert n != null;
        return n;
      }
      final Context p = this.parent;
      if (p == null) {
        final OptionType<TokenIdentifierUpper> none = Option.none();
        return new UniqueNameNonLocal(none, name);
      }
      return p.getNameInternal(name);
    }

    public @Nullable Context getParent()
    {
      return this.parent;
    }

    private boolean nameExists(
      final String name)
    {
      if (this.names.containsKey(name)) {
        return true;
      }

      final Context p = this.parent;
      if (p == null) {
        if (this.root != null) {
          if (this.root.getName().getActual().equals(name)) {
            return true;
          }
        }
        return false;
      }
      return p.nameExists(name);
    }

    public Context withNew()
    {
      return new Context(
        this.module,
        this,
        this.root,
        new HashMap<String, UniqueName.UniqueNameLocal>(),
        this.log);
    }
  }

  @EqualityReference private static final class ExpressionTransformer implements
    UASTCExpressionVisitorType<UASTUExpressionType, UASTUExpressionMatchConstantType, UASTUDValueLocal, UniqueBindersError>
  {
    private Context context;

    public ExpressionTransformer(
      final Context in_context)
    {
      this.context = in_context;
    }

    @Override public UASTUEApplication expressionVisitApplication(
      final List<UASTUExpressionType> arguments,
      final UASTCEApplication e)
      throws UniqueBindersError
    {
      return new UASTUEApplication(this.context.getNameFromValuePath(e
        .getName()), arguments);
    }

    @Override public void expressionVisitApplicationPre(
      final UASTCEApplication e)
      throws UniqueBindersError
    {
      // Nothing
    }

    @Override public UASTUEBoolean expressionVisitBoolean(
      final UASTCEBoolean e)
      throws UniqueBindersError
    {
      return new UASTUEBoolean(e.getToken());
    }

    @Override public UASTUEConditional expressionVisitConditional(
      final UASTUExpressionType condition,
      final UASTUExpressionType left,
      final UASTUExpressionType right,
      final UASTCEConditional e)
      throws UniqueBindersError
    {
      return new UASTUEConditional(e.getIf(), condition, left, right);
    }

    @Override public void expressionVisitConditionalPre(
      final UASTCEConditional e)
      throws UniqueBindersError
    {
      // Nothing
    }

    @Override public UASTUEInteger expressionVisitInteger(
      final UASTCEInteger e)
      throws UniqueBindersError
    {
      return new UASTUEInteger(e.getToken());
    }

    @Override public UASTUELet expressionVisitLet(
      final List<UASTUDValueLocal> bindings,
      final UASTUExpressionType body,
      final UASTCELet e)
      throws UniqueBindersError
    {
      final Context p = this.context.getParent();
      assert p != null;
      this.context = p;
      return new UASTUELet(e.getToken(), bindings, body);
    }

    @Override public
      UASTCLocalLevelVisitorType<UASTUDValueLocal, UniqueBindersError>
      expressionVisitLetPre(
        final UASTCELet e)
        throws UniqueBindersError
    {
      this.context = this.context.withNew();
      return new LocalTransformer(this.context);
    }

    @Override public UASTUENew expressionVisitNew(
      final List<UASTUExpressionType> arguments,
      final UASTCENew e)
      throws UniqueBindersError
    {
      return new UASTUENew(UniqueBinders.mapTypePath(e.getName()), arguments);
    }

    @Override public void expressionVisitNewPre(
      final UASTCENew e)
      throws UniqueBindersError
    {
      // Nothing
    }

    @Override public UASTUEReal expressionVisitReal(
      final UASTCEReal e)
      throws UniqueBindersError
    {
      return new UASTUEReal(e.getToken());
    }

    @Override public UASTUERecord expressionVisitRecord(
      final UASTCERecord e)
      throws UniqueBindersError
    {
      final List<UASTURecordFieldAssignment> fields =
        new ArrayList<UASTURecordFieldAssignment>();

      for (final UASTCRecordFieldAssignment f : e.getAssignments()) {
        final UASTUExpressionType ep =
          f.getExpression().expressionVisitableAccept(
            new ExpressionTransformer(this.context));
        fields.add(new UASTURecordFieldAssignment(f.getName(), ep));
      }

      return new UASTUERecord(
        UniqueBinders.mapTypePath(e.getTypePath()),
        fields);
    }

    @Override public UASTUERecordProjection expressionVisitRecordProjection(
      final UASTUExpressionType body,
      final UASTCERecordProjection e)
      throws UniqueBindersError
    {
      return new UASTUERecordProjection(body, e.getField());
    }

    @Override public void expressionVisitRecordProjectionPre(
      final UASTCERecordProjection e)
      throws UniqueBindersError
    {
      // Nothing
    }

    @Override public UASTUESwizzle expressionVisitSwizzle(
      final UASTUExpressionType body,
      final UASTCESwizzle e)
      throws UniqueBindersError
    {
      return new UASTUESwizzle(body, e.getFields());
    }

    @Override public void expressionVisitSwizzlePre(
      final UASTCESwizzle e)
      throws UniqueBindersError
    {
      // Nothing
    }

    @Override public UASTUEVariable expressionVisitVariable(
      final UASTCEVariable e)
      throws UniqueBindersError
    {
      final UniqueName name = this.context.getNameFromValuePath(e.getName());
      return new UASTUEVariable(name);
    }

    @Override public
      UASTUExpressionType
      expressionVisitMatch(
        final @Nullable UASTUExpressionType discriminee,
        final @Nullable List<Pair<UASTUExpressionMatchConstantType, UASTUExpressionType>> cases,
        final @Nullable OptionType<UASTUExpressionType> default_case,
        final UASTCEMatch m)
        throws UniqueBindersError
    {
      assert discriminee != null;
      assert cases != null;
      assert default_case != null;

      return new UASTUEMatch(
        m.getTokenMatch(),
        discriminee,
        cases,
        default_case);
    }

    @Override public void expressionVisitMatchDiscrimineePost()
      throws UniqueBindersError
    {
      // Nothing
    }

    @Override public void expressionVisitMatchDiscrimineePre()
      throws UniqueBindersError
    {
      // Nothing
    }

    @Override public @Nullable
      UASTCExpressionMatchConstantVisitorType<UASTUExpressionMatchConstantType, UniqueBindersError>
      expressionVisitMatchPre(
        final UASTCEMatch m)
        throws UniqueBindersError
    {
      return new MatchConstantTransformer();
    }
  }

  @EqualityReference private static final class MatchConstantTransformer implements
    UASTCExpressionMatchConstantVisitorType<UASTUExpressionMatchConstantType, UniqueBindersError>
  {
    public MatchConstantTransformer()
    {
      // Nothing
    }

    @Override public UASTUExpressionMatchConstantType expressionVisitBoolean(
      final UASTCEBoolean e)
      throws UniqueBindersError
    {
      return new UASTUEBoolean(e.getToken());
    }

    @Override public UASTUExpressionMatchConstantType expressionVisitInteger(
      final UASTCEInteger e)
      throws UniqueBindersError
    {
      return new UASTUEInteger(e.getToken());
    }
  }

  @EqualityReference private static final class FragmentShaderTransformer implements
    UASTCFragmentShaderVisitorType<UASTUDShaderFragment, UASTUDShaderFragmentInput, UASTUDShaderFragmentParameter, UASTUDShaderFragmentOutput, UASTUDShaderFragmentLocal, UASTUDShaderFragmentOutputAssignment, UniqueBindersError>,
    UASTCFragmentShaderLocalVisitorType<UASTUDShaderFragmentLocal, UniqueBindersError>
  {
    private final Context context;

    public FragmentShaderTransformer(
      final Context in_context)
    {
      this.context = in_context.withNew();
    }

    @Override public UASTUDShaderFragment fragmentShaderVisit(
      final List<UASTUDShaderFragmentInput> inputs,
      final List<UASTUDShaderFragmentParameter> parameters,
      final List<UASTUDShaderFragmentOutput> outputs,
      final List<UASTUDShaderFragmentLocal> locals,
      final List<UASTUDShaderFragmentOutputAssignment> output_assignments,
      final UASTCDShaderFragment f)
      throws UniqueBindersError
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
      final UASTCDShaderFragmentInput i)
      throws UniqueBindersError
    {
      final UniqueNameLocal name = this.context.addBinding(i.getName());
      final UASTUTypePath type = UniqueBinders.mapTypePath(i.getType());
      return new UASTUDShaderFragmentInput(name, type);
    }

    @Override public
      UASTUDShaderFragmentLocalDiscard
      fragmentShaderVisitLocalDiscard(
        final UASTCDShaderFragmentLocalDiscard d)
        throws UniqueBindersError
    {
      final UASTUExpressionType ex =
        d.getExpression().expressionVisitableAccept(
          new ExpressionTransformer(this.context));
      return new UASTUDShaderFragmentLocalDiscard(d.getDiscard(), ex);
    }

    @Override public
      UASTCFragmentShaderLocalVisitorType<UASTUDShaderFragmentLocal, UniqueBindersError>
      fragmentShaderVisitLocalsPre()
        throws UniqueBindersError
    {
      return this;
    }

    @Override public
      UASTUDShaderFragmentLocalValue
      fragmentShaderVisitLocalValue(
        final UASTCDShaderFragmentLocalValue v)
        throws UniqueBindersError
    {
      final UASTCDValueLocal value = v.getValue();
      final UASTUDValueLocal value_new =
        value.localVisitableAccept(new LocalTransformer(this.context));
      return new UASTUDShaderFragmentLocalValue(value_new);
    }

    @Override public
      UASTUDShaderFragmentOutputAssignment
      fragmentShaderVisitOutputAssignment(
        final UASTCDShaderFragmentOutputAssignment a)
        throws UniqueBindersError
    {
      final UASTCValuePath path = a.getVariable().getName();
      final UASTUEVariable variable =
        new UASTUEVariable(this.context.getNameFromValuePath(path));
      return new UASTUDShaderFragmentOutputAssignment(a.getName(), variable);
    }

    @Override public
      UASTCFragmentShaderOutputVisitorType<UASTUDShaderFragmentOutput, UniqueBindersError>
      fragmentShaderVisitOutputsPre()
        throws UniqueBindersError
    {
      return new UASTCFragmentShaderOutputVisitorType<UASTUDShaderFragmentOutput, UniqueBindersError>() {
        @Override public
          UASTUDShaderFragmentOutput
          fragmentShaderVisitOutputData(
            final UASTCDShaderFragmentOutputData o)
            throws UniqueBindersError
        {
          final UniqueNameLocal name =
            new UniqueNameLocal(o.getName(), o.getName().getActual());
          final UASTUTypePath type = UniqueBinders.mapTypePath(o.getType());
          return new UASTUDShaderFragmentOutputData(name, type, o.getIndex());
        }

        @Override public
          UASTUDShaderFragmentOutput
          fragmentShaderVisitOutputDepth(
            final UASTCDShaderFragmentOutputDepth o)
            throws UniqueBindersError
        {
          final UniqueNameLocal name =
            new UniqueNameLocal(o.getName(), o.getName().getActual());
          final UASTUTypePath type = UniqueBinders.mapTypePath(o.getType());
          return new UASTUDShaderFragmentOutputDepth(name, type);
        }
      };
    }

    @Override public
      UASTUDShaderFragmentParameter
      fragmentShaderVisitParameter(
        final UASTCDShaderFragmentParameter p)
        throws UniqueBindersError
    {
      final UniqueNameLocal name = this.context.addBinding(p.getName());
      final UASTUTypePath type = UniqueBinders.mapTypePath(p.getType());
      return new UASTUDShaderFragmentParameter(name, type);
    }
  }

  @EqualityReference private static final class FunctionTransformer implements
    UASTCFunctionVisitorType<UASTUDFunction, UASTUDFunctionArgument, UniqueBindersError>
  {
    private final Context context;

    public FunctionTransformer(
      final Context c)
    {
      this.context = c;
    }

    @Override public UASTUDFunctionArgument functionVisitArgument(
      final UASTCDFunctionArgument f)
      throws UniqueBindersError
    {
      final UniqueNameLocal name = this.context.addBinding(f.getName());
      return new UASTUDFunctionArgument(name, UniqueBinders.mapTypePath(f
        .getType()));
    }

    @Override public UASTUDFunction functionVisitDefined(
      final List<UASTUDFunctionArgument> arguments,
      final UASTCDFunctionDefined f)
      throws UniqueBindersError
    {
      final TokenIdentifierLower name = f.getName();

      final UASTUExpressionType body =
        f.getBody().expressionVisitableAccept(
          new ExpressionTransformer(this.context));

      return new UASTUDFunctionDefined(
        name,
        arguments,
        UniqueBinders.mapTypePath(f.getReturnType()),
        body);
    }

    @Override public void functionVisitDefinedPre(
      final UASTCDFunctionDefined f)
      throws UniqueBindersError
    {
      // Nothing
    }

    @Override public UASTUDFunction functionVisitExternal(
      final List<UASTUDFunctionArgument> arguments,
      final UASTCDFunctionExternal f)
      throws UniqueBindersError
    {
      final UASTCDExternal ext = f.getExternal();
      final TokenIdentifierLower name = f.getName();

      final OptionType<UASTCExpressionType> original_emulation =
        ext.getEmulation();
      final OptionType<UASTUExpressionType> emulation =
        original_emulation
          .mapPartial(new PartialFunctionType<UASTCExpressionType, UASTUExpressionType, UniqueBindersError>() {
            @SuppressWarnings("synthetic-access") @Override public
              UASTUExpressionType
              call(
                final UASTCExpressionType x)
                throws UniqueBindersError
            {
              return x.expressionVisitableAccept(new ExpressionTransformer(
                FunctionTransformer.this.context));
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
      final UASTCDFunctionExternal f)
      throws UniqueBindersError
    {
      // Nothing
    }
  }

  @EqualityReference private static final class LocalTransformer implements
    UASTCLocalLevelVisitorType<UASTUDValueLocal, UniqueBindersError>
  {
    private final Context context;

    public LocalTransformer(
      final Context in_context)
    {
      this.context = in_context;
    }

    @Override public UASTUDValueLocal localVisitValueLocal(
      final UASTCDValueLocal v)
      throws UniqueBindersError
    {
      final OptionType<UASTUTypePath> ascription =
        UniqueBinders.mapAscription(v.getAscription());

      final UASTUExpressionType expression =
        v.getExpression().expressionVisitableAccept(
          new ExpressionTransformer(this.context));

      final UniqueNameLocal name = this.context.addBinding(v.getName());
      return new UASTUDValueLocal(name, ascription, expression);
    }
  }

  @EqualityReference private static final class ModuleContext
  {
    private final AtomicInteger next;

    public ModuleContext()
    {
      this.next = new AtomicInteger(0);
    }

    int getNext()
    {
      return this.next.incrementAndGet();
    }
  }

  @EqualityReference private static final class ModuleTransformer implements
    UASTCModuleVisitorType<UASTUDModule, UASTUDImport, UASTUDeclarationModuleLevel, UASTUDTerm, UASTUDType, UASTUDShader, UniqueBindersError>,
    UASTCTypeVisitorType<UASTUDType, UniqueBindersError>,
    UASTCTermVisitorType<UASTUDTerm, UniqueBindersError>,
    UASTCShaderVisitorType<UASTUDShader, UniqueBindersError>
  {
    private final ModuleContext context;
    private final LogUsableType log;

    public ModuleTransformer(
      final LogUsableType in_log)
    {
      this.context = new ModuleContext();
      this.log = in_log.with("module-transformer");
    }

    @Override public
      UASTCShaderVisitorType<UASTUDShader, UniqueBindersError>
      moduleShadersPre(
        final UASTCDModule m)
        throws UniqueBindersError
    {
      return this;
    }

    @Override public
      UASTCTermVisitorType<UASTUDTerm, UniqueBindersError>
      moduleTermsPre(
        final UASTCDModule m)
        throws UniqueBindersError
    {
      return this;
    }

    @Override public
      UASTCTypeVisitorType<UASTUDType, UniqueBindersError>
      moduleTypesPre(
        final UASTCDModule m)
        throws UniqueBindersError
    {
      return this;
    }

    @Override public UASTUDModule moduleVisit(
      final List<UASTUDImport> imports,
      final List<UASTUDeclarationModuleLevel> declarations,
      final Map<String, UASTUDTerm> terms,
      final Map<String, UASTUDType> types,
      final Map<String, UASTUDShader> shaders,
      final UASTCDModule m)
      throws UniqueBindersError
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
      final UASTCDShaderFragment f)
      throws UniqueBindersError
    {
      return f.fragmentShaderVisitableAccept(new FragmentShaderTransformer(
        Context.initialContext(this.context, null, this.log)));
    }

    @Override public UASTUDImport moduleVisitImport(
      final UASTCDImport i)
      throws UniqueBindersError
    {
      return new UASTUDImport(i.getPath(), i.getRename());
    }

    @Override public UASTUDShader moduleVisitProgramShader(
      final UASTCDShaderProgram p)
      throws UniqueBindersError
    {
      return new UASTUDShaderProgram(
        p.getName(),
        UniqueBinders.mapShaderPath(p.getVertexShader()),
        UniqueBinders.mapShaderPath(p.getFragmentShader()));
    }

    @Override public UASTUDShader moduleVisitVertexShader(
      final UASTCDShaderVertex v)
      throws UniqueBindersError
    {
      return v.vertexShaderVisitableAccept(new VertexShaderTransformer(
        Context.initialContext(this.context, null, this.log)));
    }

    @Override public UASTUDTerm termVisitFunctionDefined(
      final UASTCDFunctionDefined f)
      throws UniqueBindersError
    {
      final Context ctx = Context.initialContext(this.context, f, this.log);
      return f.functionVisitableAccept(new FunctionTransformer(ctx));
    }

    @Override public UASTUDTerm termVisitFunctionExternal(
      final UASTCDFunctionExternal f)
      throws UniqueBindersError
    {
      final Context ctx = Context.initialContext(this.context, f, this.log);
      return f.functionVisitableAccept(new FunctionTransformer(ctx));
    }

    @Override public UASTUDValue termVisitValue(
      final UASTCDValue v)
      throws UniqueBindersError
    {
      final Context ctx = Context.initialContext(this.context, v, this.log);
      return v.valueVisitableAccept(new ValueTransformer(ctx));
    }

    @Override public UASTUDTerm termVisitValueExternal(
      final UASTCDValueExternal v)
      throws UniqueBindersError
    {
      final Context ctx = Context.initialContext(this.context, v, this.log);
      return v.valueVisitableAccept(new ValueTransformer(ctx));
    }

    @Override public UASTUDTypeRecord typeVisitTypeRecord(
      final UASTCDTypeRecord r)
      throws UniqueBindersError
    {
      final List<UASTUDTypeRecordField> fields =
        new ArrayList<UASTUDTypeRecordField>();

      for (final UASTCDTypeRecordField f : r.getFields()) {
        fields.add(new UASTUDTypeRecordField(f.getName(), UniqueBinders
          .mapTypePath(f.getType())));
      }

      return new UASTUDTypeRecord(r.getName(), fields);
    }
  }

  @EqualityReference private static final class ValueTransformer implements
    UASTCValueVisitorType<UASTUDValue, UniqueBindersError>
  {
    private final Context context;

    public ValueTransformer(
      final Context c)
    {
      this.context = c;
    }

    @Override public UASTUDValueDefined valueVisitDefined(
      final UASTCDValueDefined v)
      throws UniqueBindersError
    {
      final OptionType<UASTUTypePath> ascription =
        UniqueBinders.mapAscription(v.getAscription());

      final UASTUExpressionType expression =
        v.getExpression().expressionVisitableAccept(
          new ExpressionTransformer(this.context));

      return new UASTUDValueDefined(v.getName(), ascription, expression);
    }

    @Override public UASTUDValueExternal valueVisitExternal(
      final UASTCDValueExternal v)
      throws UniqueBindersError
    {
      final UASTUTypePath ascription =
        UniqueBinders.mapTypePath(v.getAscription());

      final UASTCDExternal original_external = v.getExternal();
      final OptionType<UASTUExpressionType> none = Option.none();
      final UASTUDExternal external =
        new UASTUDExternal(
          original_external.getName(),
          original_external.isVertexShaderAllowed(),
          original_external.isFragmentShaderAllowed(),
          none);
      return new UASTUDValueExternal(v.getName(), ascription, external);
    }
  }

  @EqualityReference private static final class VertexShaderTransformer implements
    UASTCVertexShaderVisitorType<UASTUDShaderVertex, UASTUDShaderVertexInput, UASTUDShaderVertexParameter, UASTUDShaderVertexOutput, UASTUDShaderVertexLocalValue, UASTUDShaderVertexOutputAssignment, UniqueBindersError>,
    UASTCVertexShaderLocalVisitorType<UASTUDShaderVertexLocalValue, UniqueBindersError>
  {
    private final Context context;

    public VertexShaderTransformer(
      final Context in_context)
    {
      this.context = in_context;
    }

    @Override public UASTUDShaderVertex vertexShaderVisit(
      final List<UASTUDShaderVertexInput> inputs,
      final List<UASTUDShaderVertexParameter> parameters,
      final List<UASTUDShaderVertexOutput> outputs,
      final List<UASTUDShaderVertexLocalValue> locals,
      final List<UASTUDShaderVertexOutputAssignment> output_assignments,
      final UASTCDShaderVertex v)
      throws UniqueBindersError
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
      final UASTCDShaderVertexInput i)
      throws UniqueBindersError
    {
      final UniqueNameLocal name = this.context.addBinding(i.getName());
      final UASTUTypePath type = UniqueBinders.mapTypePath(i.getType());
      return new UASTUDShaderVertexInput(name, type);
    }

    @Override public
      UASTCVertexShaderLocalVisitorType<UASTUDShaderVertexLocalValue, UniqueBindersError>
      vertexShaderVisitLocalsPre()
        throws UniqueBindersError
    {
      return this;
    }

    @Override public
      UASTUDShaderVertexLocalValue
      vertexShaderVisitLocalValue(
        final UASTCDShaderVertexLocalValue v)
        throws UniqueBindersError
    {
      final UASTCDValueLocal value = v.getValue();
      final UASTUDValueLocal value_new =
        value.localVisitableAccept(new LocalTransformer(this.context));
      return new UASTUDShaderVertexLocalValue(value_new);
    }

    @Override public UASTUDShaderVertexOutput vertexShaderVisitOutput(
      final UASTCDShaderVertexOutput o)
      throws UniqueBindersError
    {
      final UniqueNameLocal name =
        new UniqueNameLocal(o.getName(), o.getName().getActual());
      final UASTUTypePath type = UniqueBinders.mapTypePath(o.getType());
      return new UASTUDShaderVertexOutput(name, type, o.isMain());
    }

    @Override public
      UASTUDShaderVertexOutputAssignment
      vertexShaderVisitOutputAssignment(
        final UASTCDShaderVertexOutputAssignment a)
        throws UniqueBindersError
    {
      final UASTCValuePath path = a.getVariable().getName();
      final UASTUEVariable variable =
        new UASTUEVariable(this.context.getNameFromValuePath(path));
      return new UASTUDShaderVertexOutputAssignment(a.getName(), variable);
    }

    @Override public UASTUDShaderVertexParameter vertexShaderVisitParameter(
      final UASTCDShaderVertexParameter p)
      throws UniqueBindersError
    {
      final UniqueNameLocal name = this.context.addBinding(p.getName());
      final UASTUTypePath type = UniqueBinders.mapTypePath(p.getType());
      return new UASTUDShaderVertexParameter(name, type);
    }
  }

  static OptionType<UASTUTypePath> mapAscription(
    final OptionType<UASTCTypePath> original)
  {
    return original
      .mapPartial(new PartialFunctionType<UASTCTypePath, UASTUTypePath, UnreachableCodeException>() {
        @Override public UASTUTypePath call(
          final UASTCTypePath x)
        {
          return UniqueBinders.mapTypePath(x);
        }
      });
  }

  static UASTUShaderPath mapShaderPath(
    final UASTCShaderPath path)
  {
    return new UASTUShaderPath(path.getModule(), path.getName());
  }

  static UASTUTypePath mapTypePath(
    final UASTCTypePath type)
  {
    return new UASTUTypePath(type.getModule(), type.getName());
  }

  /**
   * Construct a new unique binding processor.
   *
   * @param compilation
   *          The AST
   * @param log
   *          A log interface
   * @return A new unique binding processor
   */

  public static UniqueBinders newUniqueBinders(
    final UASTCCompilation compilation,
    final LogUsableType log)
  {
    return new UniqueBinders(compilation, log);
  }

  private final UASTCCompilation compilation;
  private final LogUsableType    log;

  private UniqueBinders(
    final UASTCCompilation in_compilation,
    final LogUsableType in_log)
  {
    this.log = NullCheck.notNull(in_log, "Log").with("unique-binders");
    this.compilation = NullCheck.notNull(in_compilation, "Compilation");
  }

  /**
   * Calculate new unique bindings for the current AST
   *
   * @return The AST
   * @throws UniqueBindersError
   *           If an error occurs
   */

  public UASTUCompilation run()
    throws UniqueBindersError
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
