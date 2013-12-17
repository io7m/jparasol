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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Option;
import com.io7m.jaux.functional.Option.Some;
import com.io7m.jaux.functional.Unit;
import com.io7m.jlog.Log;
import com.io7m.jparasol.ModulePath;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.NameRestrictions;
import com.io7m.jparasol.NameRestrictions.NameRestrictionsException;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.lexer.Token.TokenIdentifierUpper;
import com.io7m.jparasol.untyped.ast.initial.UASTIChecked;
import com.io7m.jparasol.untyped.ast.initial.UASTICompilation;
import com.io7m.jparasol.untyped.ast.initial.UASTIDRecordVisitor;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDFunctionArgument;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDFunctionDefined;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDFunctionExternal;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDImport;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDModule;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShader;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragment;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentInput;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentLocalDiscard;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentLocalValue;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentOutput;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentOutputAssignment;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentParameter;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderParameters;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderProgram;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertex;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertexInput;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertexLocalValue;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertexOutput;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertexOutputAssignment;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertexParameter;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDTerm;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDType;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDTypeRecord;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDTypeRecordField;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDValue;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDValueLocal;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpression;
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
import com.io7m.jparasol.untyped.ast.initial.UASTIUnchecked;
import com.io7m.jparasol.untyped.ast.initial.UASTIVertexShaderVisitor;

/**
 * Reject insane modules. Specifically, reject:
 * 
 * For modules:
 * 
 * <ul>
 * <li>Module names that are restricted, according to {@link NameRestrictions}
 * .</li>
 * </ul>
 * 
 * For imports:
 * 
 * <ul>
 * <li>Duplicate imports (<code>import x.Y; import x.Y;</code>)</li>
 * <li>Duplicate renames for imports (
 * <code>import x.Y as A; import x.Z as A;</code>)</li>
 * <li>Imports that conflict with renames (
 * <code>import x.Y as A; import x.A;</code>)</li>
 * <li>Redundant import renames (<code>import x.Y as Y;</code>)</li>
 * </ul>
 * 
 * For terms/types in modules:
 * 
 * <ul>
 * <li>Terms that have names that are restricted, according to
 * {@link NameRestrictions}.</li>
 * <li>Multiple function declarations with the same name (
 * <code>function f ...; function f ...;</code>)</li>
 * <li>Multiple value declarations with the same name in the same scope (
 * <code>value x = ...; value x = ...;</code>)</li>
 * <li>Multiple function arguments with the same name (
 * <code>function f (x : int, x : int)</code>)</li>
 * <li>Record expressions with duplicate field names (
 * <code>{ x = 23, x = 23 }</code>)</li>
 * </ul>
 * 
 * For types in modules:
 * 
 * <ul>
 * <li>Multiple type declarations with the same name (
 * <code>type t ...; type t ...;</code>)</li>
 * <li>Type declarations with names matching any of those of the built-in
 * types</li>
 * <li>Record type declarations with duplicate field names (
 * <code>type t is record x : int, x : int end</code>)</li>
 * </ul>
 * 
 * For shaders in modules:
 * 
 * <ul>
 * <li>Shaders with names that are restricted, according to
 * <code>NameRestrictions</code>.</li>
 * <li>Shaders with inputs, outputs, or parameters with names that are
 * restricted, according to {@link NameRestrictions}.</li>
 * <li>Multiple shader declarations with the same name (
 * <code>shader vertex v is ...; shader fragment v is ...;</code>)</li>
 * <li>Multiple input, output, or parameters with the same name</li>
 * <li>Multiple local values with the same name</li>
 * <li>Multiple output assignments to the same name</li>
 * <li>Missing output assignments</li>
 * </ul>
 * 
 * For vertex shaders:
 * 
 * <ul>
 * <li>Require that exactly one output assignment exists to the built-in
 * output <code>gl_Position</code></li>
 * </ul>
 * 
 * For fragment shaders:
 * 
 * <ul>
 * <li>Multiple outputs with the same index (
 * <code>out out0 : vector4f as 0; out out1 : vector4f as 0;</code>)</li>
 * <li>Outputs with discontinuous indices (
 * <code>out out0 : vector4f as 2; out out1 : vector4f as 0;</code>)</li>
 * <li>Outputs with negative indices</li>
 * </ul>
 * 
 * For units:
 * 
 * <ul>
 * <li>Modules that import themselves (
 * <code>package x.y; module K is import x.y.K; end</code>)</li>
 * </ul>
 */

public final class ModuleStructure
{
  private static class ExpressionChecker implements
    UASTIExpressionVisitor<Unit, Unit, UASTIUnchecked, ModuleStructureError>
  {
    public ExpressionChecker()
    {
      // Nothing
    }

    @Override public Unit expressionVisitApplication(
      final @Nonnull List<Unit> arguments,
      final @Nonnull UASTIEApplication<UASTIUnchecked> e)
      throws ModuleStructureError,
        ConstraintError
    {
      return Unit.unit();
    }

    @Override public void expressionVisitApplicationPre(
      final @Nonnull UASTIEApplication<UASTIUnchecked> e)
      throws ModuleStructureError,
        ConstraintError
    {
      // Nothing
    }

    @Override public Unit expressionVisitBoolean(
      final @Nonnull UASTIEBoolean<UASTIUnchecked> e)
      throws ModuleStructureError,
        ConstraintError
    {
      return Unit.unit();
    }

    @Override public Unit expressionVisitConditional(
      final @Nonnull Unit condition,
      final @Nonnull Unit left,
      final @Nonnull Unit right,
      final @Nonnull UASTIEConditional<UASTIUnchecked> e)
      throws ModuleStructureError,
        ConstraintError
    {
      return Unit.unit();
    }

    @Override public void expressionVisitConditionalPre(
      final @Nonnull UASTIEConditional<UASTIUnchecked> e)
      throws ModuleStructureError,
        ConstraintError
    {
      // Nothing
    }

    @Override public Unit expressionVisitInteger(
      final @Nonnull UASTIEInteger<UASTIUnchecked> e)
      throws ModuleStructureError,
        ConstraintError
    {
      return Unit.unit();
    }

    @Override public Unit expressionVisitLet(
      final @Nonnull List<Unit> bindings,
      final @Nonnull Unit body,
      final @Nonnull UASTIELet<UASTIUnchecked> e)
      throws ModuleStructureError,
        ConstraintError
    {
      return Unit.unit();
    }

    @Override public LocalChecker expressionVisitLetPre(
      final @Nonnull UASTIELet<UASTIUnchecked> e)
      throws ModuleStructureError,
        ConstraintError
    {
      return new LocalChecker();
    }

    @Override public Unit expressionVisitNew(
      final @Nonnull List<Unit> args,
      final @Nonnull UASTIENew<UASTIUnchecked> e)
      throws ModuleStructureError,
        ConstraintError
    {
      return Unit.unit();
    }

    @Override public void expressionVisitNewPre(
      final @Nonnull UASTIENew<UASTIUnchecked> e)
      throws ModuleStructureError,
        ConstraintError
    {
      // Nothing
    }

    @Override public Unit expressionVisitReal(
      final @Nonnull UASTIEReal<UASTIUnchecked> e)
      throws ModuleStructureError,
        ConstraintError
    {
      return Unit.unit();
    }

    @Override public Unit expressionVisitRecord(
      final @Nonnull UASTIERecord<UASTIUnchecked> e)
      throws ModuleStructureError,
        ConstraintError
    {
      try {
        final HashMap<String, UASTIRecordFieldAssignment<UASTIUnchecked>> fields =
          new HashMap<String, UASTIRecordFieldAssignment<UASTIUnchecked>>();

        for (final UASTIRecordFieldAssignment<UASTIUnchecked> f : e
          .getAssignments()) {
          final String name = f.getName().getActual();

          NameRestrictions.checkRestrictedExceptional(f.getName());

          if (fields.containsKey(name)) {
            throw ModuleStructureError.moduleRecordExpressionFieldDuplicate(
              f,
              fields.get(name));
          }

          final UASTIExpression<UASTIUnchecked> r = f.getExpression();
          final ExpressionChecker ec = new ExpressionChecker();
          @SuppressWarnings("unused") final Unit rx =
            r.expressionVisitableAccept(ec);

          fields.put(name, f);
        }

        return Unit.unit();
      } catch (final NameRestrictionsException x) {
        throw new ModuleStructureError(x);
      }
    }

    @Override public Unit expressionVisitRecordProjection(
      final @Nonnull Unit body,
      final @Nonnull UASTIERecordProjection<UASTIUnchecked> e)
      throws ModuleStructureError,
        ConstraintError
    {
      return Unit.unit();
    }

    @Override public void expressionVisitRecordProjectionPre(
      final @Nonnull UASTIERecordProjection<UASTIUnchecked> e)
      throws ModuleStructureError,
        ConstraintError
    {
      // Nothing
    }

    @Override public Unit expressionVisitSwizzle(
      final @Nonnull Unit body,
      final @Nonnull UASTIESwizzle<UASTIUnchecked> e)
      throws ModuleStructureError,
        ConstraintError
    {
      return Unit.unit();
    }

    @Override public void expressionVisitSwizzlePre(
      final @Nonnull UASTIESwizzle<UASTIUnchecked> e)
      throws ModuleStructureError,
        ConstraintError
    {
      // Nothing
    }

    @Override public Unit expressionVisitVariable(
      final @Nonnull UASTIEVariable<UASTIUnchecked> e)
      throws ModuleStructureError,
        ConstraintError
    {
      try {
        NameRestrictions.checkRestrictedExceptional(e.getName().getName());
        return Unit.unit();
      } catch (final NameRestrictionsException x) {
        throw new ModuleStructureError(x);
      }
    }
  }

  private static class FragmentShaderChecker implements
    UASTIFragmentShaderVisitor<Unit, Unit, Unit, Unit, UASTIUnchecked, ModuleStructureError>
  {
    private final @Nonnull HashMap<String, UASTIDShaderFragmentLocalValue<UASTIUnchecked>>       locals;
    private final @Nonnull HashMap<String, UASTIDShaderFragmentOutputAssignment<UASTIUnchecked>> output_assignments;
    private final @Nonnull HashMap<Integer, UASTIDShaderFragmentOutput<UASTIUnchecked>>          output_indices;
    private int                                                                                  output_max;
    private final @Nonnull HashMap<String, UASTIDShaderFragmentOutput<UASTIUnchecked>>           outputs;
    private final @Nonnull HashMap<String, UASTIDShaderParameters<UASTIUnchecked>>               parameters;
    private final @Nonnull UASTIDShaderFragment<UASTIUnchecked>                                  shader;

    public FragmentShaderChecker(
      final @Nonnull UASTIDShaderFragment<UASTIUnchecked> shader)
    {
      this.shader = shader;
      this.locals =
        new HashMap<String, UASTIDShaderFragmentLocalValue<UASTIUnchecked>>();
      this.parameters =
        new HashMap<String, UASTIDShaderParameters<UASTIUnchecked>>();
      this.outputs =
        new HashMap<String, UASTIDShaderFragmentOutput<UASTIUnchecked>>();
      this.output_assignments =
        new HashMap<String, UASTIDShaderFragmentOutputAssignment<UASTIUnchecked>>();
      this.output_max = -1;
      this.output_indices =
        new HashMap<Integer, UASTIDShaderFragmentOutput<UASTIUnchecked>>();
    }

    private void addOutputAssignment(
      final UASTIDShaderFragmentOutputAssignment<UASTIUnchecked> a)
      throws ModuleStructureError,
        ConstraintError
    {
      final String name = a.getName().getActual();
      if (this.output_assignments.containsKey(name)) {
        throw ModuleStructureError.moduleShaderOutputAssignmentDuplicate(
          a.getName(),
          this.output_assignments.get(name).getName());
      }

      final ExpressionChecker ec = new ExpressionChecker();
      a.getVariable().expressionVisitableAccept(ec);
      this.output_assignments.put(name, a);
    }

    private void addShaderParameter(
      final UASTIDShaderParameters<UASTIUnchecked> p)
      throws ModuleStructureError,
        ConstraintError
    {
      try {
        NameRestrictions.checkRestrictedExceptional(p.getName());

        final String name = p.getName().getActual();
        if (this.parameters.containsKey(name)) {
          throw ModuleStructureError.moduleShaderParameterDuplicate(
            p,
            this.parameters.get(name));
        }
        this.parameters.put(name, p);

        if (p instanceof UASTIDShaderFragmentOutput) {
          final UASTIDShaderFragmentOutput<UASTIUnchecked> out =
            (UASTIDShaderFragmentOutput<UASTIUnchecked>) p;

          if (out.getIndex() < 0) {
            throw ModuleStructureError.moduleShaderOutputIndexInvalid(out);
          }

          this.output_max = Math.max(out.getIndex(), this.output_max);

          final Integer current_index = Integer.valueOf(out.getIndex());
          if (this.output_indices.containsKey(current_index)) {
            throw ModuleStructureError.moduleShaderOutputIndexDuplicate(
              out,
              this.output_indices.get(current_index));
          }

          this.output_indices.put(current_index, out);
          this.outputs.put(name, out);
        }

      } catch (final NameRestrictionsException x) {
        throw new ModuleStructureError(x);
      }
    }

    public void check()
      throws ModuleStructureError,
        ConstraintError
    {
      this.checkOutputAssignmentsComplete();
      this.checkOutputsContinuous();
    }

    public void checkOutputAssignmentsComplete()
      throws ModuleStructureError,
        ConstraintError
    {
      for (final String p : this.outputs.keySet()) {
        final UASTIDShaderFragmentOutput<UASTIUnchecked> output =
          this.outputs.get(p);
        if (this.output_assignments.containsKey(p) == false) {
          throw ModuleStructureError
            .moduleShaderOutputAssignmentMissing(output.getName());
        }
      }
    }

    public void checkOutputsContinuous()
      throws ModuleStructureError,
        ConstraintError
    {
      for (int index = 0; index <= this.output_max; ++index) {
        if (this.output_indices.containsKey(Integer.valueOf(index)) == false) {
          throw ModuleStructureError.moduleShaderOutputIndexMissing(
            this.shader,
            this.output_indices,
            index);
        }
      }
    }

    @Override public Unit fragmentShaderVisit(
      final @Nonnull List<Unit> in_parameters,
      final @Nonnull List<Unit> in_locals,
      final @Nonnull List<Unit> in_output_assignments,
      final UASTIDShaderFragment<UASTIUnchecked> f)
      throws ModuleStructureError,
        ConstraintError
    {
      return Unit.unit();
    }

    @Override public Unit fragmentShaderVisitInput(
      final @Nonnull UASTIDShaderFragmentInput<UASTIUnchecked> i)
      throws ModuleStructureError,
        ConstraintError
    {
      this.addShaderParameter(i);
      return Unit.unit();
    }

    @Override public Unit fragmentShaderVisitOutput(
      final @Nonnull UASTIDShaderFragmentOutput<UASTIUnchecked> o)
      throws ModuleStructureError,
        ConstraintError
    {
      this.addShaderParameter(o);
      return Unit.unit();
    }

    @Override public Unit fragmentShaderVisitOutputAssignment(
      final @Nonnull UASTIDShaderFragmentOutputAssignment<UASTIUnchecked> a)
      throws ModuleStructureError,
        ConstraintError
    {
      this.addOutputAssignment(a);
      return Unit.unit();
    }

    @Override public Unit fragmentShaderVisitParameter(
      final @Nonnull UASTIDShaderFragmentParameter<UASTIUnchecked> p)
      throws ModuleStructureError,
        ConstraintError
    {
      this.addShaderParameter(p);
      return Unit.unit();
    }

    @Override public
      UASTIFragmentShaderLocalVisitor<Unit, UASTIUnchecked, ModuleStructureError>
      fragmentShaderVisitPre(
        final UASTIDShaderFragment<UASTIUnchecked> f)
        throws ModuleStructureError,
          ConstraintError
    {
      return new FragmentShaderLocalChecker();
    }
  }

  private static class FragmentShaderLocalChecker implements
    UASTIFragmentShaderLocalVisitor<Unit, UASTIUnchecked, ModuleStructureError>
  {
    private final @Nonnull HashMap<String, UASTIDShaderFragmentLocalValue<UASTIUnchecked>> locals;

    public FragmentShaderLocalChecker()
    {
      this.locals =
        new HashMap<String, UASTIDShaderFragmentLocalValue<UASTIUnchecked>>();
    }

    @Override public Unit fragmentShaderVisitLocalDiscard(
      final @Nonnull UASTIDShaderFragmentLocalDiscard<UASTIUnchecked> d)
      throws ModuleStructureError,
        ConstraintError
    {
      d.getExpression().expressionVisitableAccept(new ExpressionChecker());
      return Unit.unit();
    }

    @Override public Unit fragmentShaderVisitLocalValue(
      final @Nonnull UASTIDShaderFragmentLocalValue<UASTIUnchecked> v)
      throws ModuleStructureError,
        ConstraintError
    {
      try {
        NameRestrictions.checkRestrictedExceptional(v.getValue().getName());

        final String name = v.getValue().getName().getActual();
        if (this.locals.containsKey(name)) {
          throw ModuleStructureError.moduleShaderLocalConflict(
            v.getValue(),
            this.locals.get(name).getValue());
        }
        this.locals.put(name, v);

        final ExpressionChecker ec = new ExpressionChecker();
        v.getValue().getExpression().expressionVisitableAccept(ec);
        return Unit.unit();
      } catch (final NameRestrictionsException x) {
        throw new ModuleStructureError(x);
      }
    }
  }

  private static class FunctionChecker implements
    UASTIFunctionVisitor<Unit, Unit, UASTIUnchecked, ModuleStructureError>
  {
    private final HashMap<String, UASTIDFunctionArgument<UASTIUnchecked>> args;

    public FunctionChecker()
    {
      this.args =
        new HashMap<String, UASTIDFunctionArgument<UASTIUnchecked>>();
    }

    @Override public Unit functionVisitArgument(
      final @Nonnull UASTIDFunctionArgument<UASTIUnchecked> a)
      throws ModuleStructureError,
        ConstraintError
    {
      try {
        final String name = a.getName().getActual();

        NameRestrictions.checkRestrictedExceptional(a.getName());
        if (this.args.containsKey(name)) {
          throw ModuleStructureError.moduleFunctionArgumentDuplicate(
            a,
            this.args.get(name));
        }
        this.args.put(name, a);
        return Unit.unit();
      } catch (final NameRestrictionsException x) {
        throw new ModuleStructureError(x);
      }
    }

    @Override public Unit functionVisitDefined(
      final @Nonnull List<Unit> arguments,
      final @Nonnull UASTIDFunctionDefined<UASTIUnchecked> f)
      throws ModuleStructureError,
        ConstraintError
    {
      final ExpressionChecker ec = new ExpressionChecker();
      return f.getBody().expressionVisitableAccept(ec);
    }

    @Override public void functionVisitDefinedPre(
      final @Nonnull UASTIDFunctionDefined<UASTIUnchecked> f)
      throws ModuleStructureError,
        ConstraintError
    {
      // Nothing
    }

    @Override public Unit functionVisitExternal(
      final @Nonnull List<Unit> arguments,
      final @Nonnull UASTIDFunctionExternal<UASTIUnchecked> f)
      throws ModuleStructureError,
        ConstraintError
    {
      return Unit.unit();
    }

    @Override public void functionVisitExternalPre(
      final @Nonnull UASTIDFunctionExternal<UASTIUnchecked> f)
      throws ModuleStructureError,
        ConstraintError
    {
      // Nothing
    }
  }

  private static class LocalChecker implements
    UASTILocalLevelVisitor<Unit, UASTIUnchecked, ModuleStructureError>
  {
    private final @Nonnull HashMap<String, UASTIDValueLocal<UASTIUnchecked>> values;

    public LocalChecker()
    {
      this.values = new HashMap<String, UASTIDValueLocal<UASTIUnchecked>>();
    }

    @Override public Unit localVisitValueLocal(
      final @Nonnull UASTIDValueLocal<UASTIUnchecked> v)
      throws ModuleStructureError,
        ConstraintError
    {
      try {
        final String name = v.getName().getActual();
        NameRestrictions.checkRestrictedExceptional(v.getName());

        if (this.values.containsKey(name)) {
          throw ModuleStructureError.moduleShaderLocalConflict(
            v,
            this.values.get(name));
        }
        this.values.put(name, v);

        final ExpressionChecker ec = new ExpressionChecker();
        v.getExpression().expressionVisitableAccept(ec);
        return Unit.unit();
      } catch (final NameRestrictionsException x) {
        throw new ModuleStructureError(x);
      }
    }
  }

  private static class ModuleChecker implements
    UASTIModuleVisitor<Unit, Unit, Unit, UASTIUnchecked, ModuleStructureError>
  {
    private final @Nonnull ModulePath                                            current_path;
    private final @Nonnull ModulePathFlat                                        current_path_flat;
    private final @Nonnull HashMap<ModulePathFlat, UASTIDImport<UASTIUnchecked>> imported_modules;
    private final @Nonnull HashMap<String, UASTIDImport<UASTIUnchecked>>         imported_names;
    private final @Nonnull HashMap<String, UASTIDImport<UASTIUnchecked>>         imported_renames;
    private final @Nonnull Log                                                   log;
    private final @Nonnull HashMap<String, UASTIDShader<UASTIUnchecked>>         shaders;
    private final @Nonnull HashMap<String, UASTIDTerm<UASTIUnchecked>>           terms;
    private final @Nonnull HashMap<String, UASTIDType<UASTIUnchecked>>           types;

    public ModuleChecker(
      final @Nonnull ModulePath path,
      final @Nonnull Log log)
    {
      this.log = log;
      this.current_path = path;
      this.current_path_flat = ModulePathFlat.fromModulePath(path);

      this.imported_modules =
        new HashMap<ModulePathFlat, UASTIDImport<UASTIUnchecked>>();
      this.imported_names =
        new HashMap<String, UASTIDImport<UASTIUnchecked>>();
      this.imported_renames =
        new HashMap<String, UASTIDImport<UASTIUnchecked>>();

      this.terms = new HashMap<String, UASTIDTerm<UASTIUnchecked>>();
      this.types = new HashMap<String, UASTIDType<UASTIUnchecked>>();
      this.shaders = new HashMap<String, UASTIDShader<UASTIUnchecked>>();
    }

    private void addImport(
      final @Nonnull UASTIDImport<UASTIUnchecked> i)
      throws ModuleStructureError,
        ConstraintError
    {
      try {
        final ModulePath path = i.getPath();
        final ModulePathFlat flat = ModulePathFlat.fromModulePath(path);

        if (flat.equals(this.current_path_flat)) {
          throw ModuleStructureError.moduleImportsSelf(i);
        }

        if (this.imported_modules.containsKey(flat)) {
          final UASTIDImport<UASTIUnchecked> original =
            this.imported_modules.get(flat);
          throw ModuleStructureError.moduleImportDuplicate(i, original);
        }

        final Option<TokenIdentifierUpper> rename_opt = i.getRename();
        final TokenIdentifierUpper import_name = path.getName();

        if (rename_opt.isSome()) {
          final TokenIdentifierUpper rename =
            ((Some<TokenIdentifierUpper>) rename_opt).value;

          NameRestrictions.checkRestrictedExceptional(rename);
          if (import_name.getActual().equals(rename.getActual())) {
            throw ModuleStructureError.moduleImportRedundantRename(i);
          }

          if (this.imported_names.containsKey(rename.getActual())) {
            if (this.imported_renames.containsKey(rename.getActual())) {
              throw ModuleStructureError.moduleRenameRenameConflict(
                i,
                this.imported_renames.get(rename.getActual()));
            }
            throw ModuleStructureError.moduleRenameImportConflict(
              i,
              this.imported_names.get(rename.getActual()));
          }

          this.imported_modules.put(flat, i);
          this.imported_names.put(rename.getActual(), i);
          this.imported_renames.put(rename.getActual(), i);

        } else {

          final String name = i.getPath().getName().getActual();
          if (this.imported_names.containsKey(name)) {
            if (this.imported_renames.containsKey(name)) {
              throw ModuleStructureError.moduleImportRenameConflict(
                i,
                this.imported_renames.get(name));
            }
            throw ModuleStructureError.moduleImportImportConflict(
              i,
              this.imported_names.get(name));
          }

          this.imported_modules.put(flat, i);
          this.imported_names.put(name, i);
        }
      } catch (final NameRestrictionsException e) {
        throw new ModuleStructureError(e);
      }
    }

    @Override public Unit moduleVisit(
      final @Nonnull List<Unit> imports,
      final @Nonnull List<Unit> declarations,
      final @Nonnull UASTIDModule<UASTIUnchecked> m)
      throws ModuleStructureError,
        ConstraintError
    {
      return Unit.unit();
    }

    @Override public Unit moduleVisitImport(
      final @Nonnull UASTIDImport<UASTIUnchecked> i)
      throws ModuleStructureError,
        ConstraintError
    {
      this.addImport(i);
      return Unit.unit();
    }

    @Override public
      UASTIModuleLevelDeclarationVisitor<Unit, UASTIUnchecked, ModuleStructureError>
      moduleVisitPre(
        final @Nonnull UASTIDModule<UASTIUnchecked> m)
        throws ModuleStructureError,
          ConstraintError
    {
      try {
        NameRestrictions.checkRestrictedExceptional(m.getName());

        final List<TokenIdentifierLower> c =
          this.current_path.getPackagePath().getComponents();
        for (final TokenIdentifierLower pc : c) {
          NameRestrictions.checkRestrictedExceptional(pc);
        }

        return new ModuleDeclarationChecker(this.current_path, this.log);
      } catch (final NameRestrictionsException e) {
        throw new ModuleStructureError(e);
      }
    }
  }

  private static class ModuleDeclarationChecker implements
    UASTIModuleLevelDeclarationVisitor<Unit, UASTIUnchecked, ModuleStructureError>
  {
    private final @Nonnull ModulePath                                    current_path;
    private final @Nonnull ModulePathFlat                                current_path_flat;
    private final @Nonnull Log                                           log;
    private final @Nonnull HashMap<String, UASTIDShader<UASTIUnchecked>> shaders;
    private final @Nonnull HashMap<String, UASTIDTerm<UASTIUnchecked>>   terms;
    private final @Nonnull HashMap<String, UASTIDType<UASTIUnchecked>>   types;

    public ModuleDeclarationChecker(
      final @Nonnull ModulePath path,
      final @Nonnull Log log)
    {
      this.log = log;
      this.current_path = path;
      this.current_path_flat = ModulePathFlat.fromModulePath(path);
      this.terms = new HashMap<String, UASTIDTerm<UASTIUnchecked>>();
      this.types = new HashMap<String, UASTIDType<UASTIUnchecked>>();
      this.shaders = new HashMap<String, UASTIDShader<UASTIUnchecked>>();
    }

    private void addShader(
      final @Nonnull UASTIDShader<UASTIUnchecked> shader)
      throws ConstraintError,
        ModuleStructureError
    {
      try {
        final TokenIdentifierLower token = shader.getName();

        this.log.debug(String.format(
          "Adding shader %s at %s",
          token.getActual(),
          token.getPosition()));

        final String name = token.getActual();
        NameRestrictions.checkRestrictedExceptional(token);

        if (this.shaders.containsKey(name)) {
          final UASTIDShader<UASTIUnchecked> original =
            this.shaders.get(name);
          throw ModuleStructureError.moduleShaderConflict(shader, original);
        }

        this.shaders.put(name, shader);
      } catch (final NameRestrictionsException x) {
        throw new ModuleStructureError(x);
      }
    }

    private void addTerm(
      final @Nonnull UASTIDTerm<UASTIUnchecked> term)
      throws ModuleStructureError,
        ConstraintError
    {
      try {
        final TokenIdentifierLower token = term.getName();

        this.log.debug(String.format(
          "Adding term %s at %s",
          token.getActual(),
          token.getPosition()));

        final String name = token.getActual();
        NameRestrictions.checkRestrictedExceptional(token);

        if (this.terms.containsKey(name)) {
          final UASTIDTerm<UASTIUnchecked> original = this.terms.get(name);
          throw ModuleStructureError.moduleTermConflict(term, original);
        }

        this.terms.put(name, term);
      } catch (final NameRestrictionsException x) {
        throw new ModuleStructureError(x);
      }
    }

    private void addType(
      final @Nonnull UASTIDType<UASTIUnchecked> type)
      throws ModuleStructureError,
        ConstraintError
    {
      try {
        final TokenIdentifierLower token = type.getName();

        this.log.debug(String.format(
          "Adding type %s at %s",
          token.getActual(),
          token.getPosition()));

        NameRestrictions.checkRestrictedExceptional(token);

        final String name = token.getActual();
        if (this.types.containsKey(name)) {
          final UASTIDType<UASTIUnchecked> original = this.types.get(name);
          throw ModuleStructureError.moduleTypeConflict(type, original);
        }

        this.types.put(name, type);
      } catch (final NameRestrictionsException x) {
        throw new ModuleStructureError(x);
      }
    }

    @Override public Unit moduleVisitFragmentShader(
      final @Nonnull UASTIDShaderFragment<UASTIUnchecked> f)
      throws ModuleStructureError,
        ConstraintError
    {
      this.addShader(f);
      final FragmentShaderChecker c = new FragmentShaderChecker(f);
      f.fragmentShaderVisitableAccept(c);
      c.check();
      return Unit.unit();
    }

    @Override public Unit moduleVisitFunctionDefined(
      final @Nonnull UASTIDFunctionDefined<UASTIUnchecked> f)
      throws ModuleStructureError,
        ConstraintError
    {
      this.addTerm(f);
      f.functionVisitableAccept(new FunctionChecker());
      return Unit.unit();
    }

    @Override public Unit moduleVisitFunctionExternal(
      final @Nonnull UASTIDFunctionExternal<UASTIUnchecked> f)
      throws ModuleStructureError,
        ConstraintError
    {
      this.addTerm(f);
      f.functionVisitableAccept(new FunctionChecker());
      return Unit.unit();
    }

    @Override public Unit moduleVisitProgramShader(
      final @Nonnull UASTIDShaderProgram<UASTIUnchecked> p)
      throws ModuleStructureError,
        ConstraintError
    {
      this.addShader(p);
      return Unit.unit();
    }

    @Override public Unit moduleVisitTypeRecord(
      final @Nonnull UASTIDTypeRecord<UASTIUnchecked> r)
      throws ModuleStructureError,
        ConstraintError
    {
      this.addType(r);
      r.recordTypeVisitableAccept(new RecordTypeChecker());
      return Unit.unit();
    }

    @Override public Unit moduleVisitValue(
      final @Nonnull UASTIDValue<UASTIUnchecked> v)
      throws ModuleStructureError,
        ConstraintError
    {
      this.addTerm(v);
      v.getExpression().expressionVisitableAccept(new ExpressionChecker());
      return Unit.unit();
    }

    @Override public Unit moduleVisitVertexShader(
      final @Nonnull UASTIDShaderVertex<UASTIUnchecked> v)
      throws ModuleStructureError,
        ConstraintError
    {
      this.addShader(v);
      final VertexShaderChecker c = new VertexShaderChecker(v);
      v.vertexShaderVisitableAccept(c);
      c.check();
      return Unit.unit();
    }
  }

  private static class RecordTypeChecker implements
    UASTIDRecordVisitor<Unit, Unit, UASTIUnchecked, ModuleStructureError>
  {
    private final @Nonnull HashMap<String, UASTIDTypeRecordField<UASTIUnchecked>> fields;

    public RecordTypeChecker()
    {
      this.fields =
        new HashMap<String, UASTIDTypeRecordField<UASTIUnchecked>>();
    }

    @Override public Unit recordTypeVisit(
      final @Nonnull List<Unit> u_fields,
      final @Nonnull UASTIDTypeRecord<UASTIUnchecked> e)
      throws ModuleStructureError,
        ConstraintError
    {
      return Unit.unit();
    }

    @Override public Unit recordTypeVisitField(
      final @Nonnull UASTIDTypeRecordField<UASTIUnchecked> e)
      throws ModuleStructureError,
        ConstraintError
    {
      final String name = e.getName().getActual();
      if (this.fields.containsKey(name)) {
        throw ModuleStructureError.moduleRecordFieldDuplicate(
          e,
          this.fields.get(name));
      }
      this.fields.put(name, e);
      return Unit.unit();
    }

    @Override public void recordTypeVisitPre(
      final @Nonnull UASTIDTypeRecord<UASTIUnchecked> e)
      throws ModuleStructureError,
        ConstraintError
    {
      // Nothing
    }
  }

  private static class VertexShaderChecker implements
    UASTIVertexShaderVisitor<Unit, Unit, Unit, Unit, UASTIUnchecked, ModuleStructureError>
  {
    private final @Nonnull HashMap<String, UASTIDShaderVertexLocalValue<UASTIUnchecked>>       locals;
    private final @Nonnull HashMap<String, UASTIDShaderVertexOutputAssignment<UASTIUnchecked>> output_assignments;
    private final @Nonnull HashMap<String, UASTIDShaderVertexOutput<UASTIUnchecked>>           outputs;
    private final @Nonnull HashMap<String, UASTIDShaderParameters<UASTIUnchecked>>             parameters;
    private final @Nonnull UASTIDShaderVertex<UASTIUnchecked>                                  shader;

    public VertexShaderChecker(
      final @Nonnull UASTIDShaderVertex<UASTIUnchecked> shader)
    {
      this.shader = shader;
      this.locals =
        new HashMap<String, UASTIDShaderVertexLocalValue<UASTIUnchecked>>();
      this.parameters =
        new HashMap<String, UASTIDShaderParameters<UASTIUnchecked>>();
      this.outputs =
        new HashMap<String, UASTIDShaderVertexOutput<UASTIUnchecked>>();
      this.output_assignments =
        new HashMap<String, UASTIDShaderVertexOutputAssignment<UASTIUnchecked>>();
    }

    private void addOutputAssignment(
      final UASTIDShaderVertexOutputAssignment<UASTIUnchecked> a)
      throws ModuleStructureError,
        ConstraintError
    {
      final String name = a.getName().getActual();
      if (this.output_assignments.containsKey(name)) {
        throw ModuleStructureError.moduleShaderOutputAssignmentDuplicate(
          a.getName(),
          this.output_assignments.get(name).getName());
      }
      a.getVariable().expressionVisitableAccept(new ExpressionChecker());
      this.output_assignments.put(name, a);
    }

    private void addShaderParameter(
      final UASTIDShaderParameters<UASTIUnchecked> p)
      throws ModuleStructureError,
        ConstraintError
    {
      try {
        NameRestrictions.checkRestrictedExceptional(p.getName());

        final String name = p.getName().getActual();
        if (this.parameters.containsKey(name)) {
          throw ModuleStructureError.moduleShaderParameterDuplicate(
            p,
            this.parameters.get(name));
        }
        this.parameters.put(name, p);

        if (p instanceof UASTIDShaderVertexOutput) {
          final UASTIDShaderVertexOutput<UASTIUnchecked> out =
            (UASTIDShaderVertexOutput<UASTIUnchecked>) p;
          this.outputs.put(out.getName().getActual(), out);
        }

      } catch (final NameRestrictionsException x) {
        throw new ModuleStructureError(x);
      }
    }

    public void check()
      throws ModuleStructureError,
        ConstraintError
    {
      this.checkOutputAssignmentsComplete();
    }

    public void checkOutputAssignmentsComplete()
      throws ModuleStructureError,
        ConstraintError
    {
      for (final String p : this.outputs.keySet()) {
        final UASTIDShaderVertexOutput<UASTIUnchecked> output =
          this.outputs.get(p);
        if (this.output_assignments.containsKey(p) == false) {
          throw ModuleStructureError
            .moduleShaderOutputAssignmentMissing(output.getName());
        }
      }
    }

    @Override public Unit vertexShaderVisit(
      final @Nonnull List<Unit> r_parameters,
      final @Nonnull List<Unit> r_locals,
      final @Nonnull List<Unit> r_output_assignments,
      final @Nonnull UASTIDShaderVertex<UASTIUnchecked> v)
      throws ModuleStructureError,
        ConstraintError
    {
      return Unit.unit();
    }

    @Override public Unit vertexShaderVisitInput(
      final @Nonnull UASTIDShaderVertexInput<UASTIUnchecked> i)
      throws ModuleStructureError,
        ConstraintError
    {
      this.addShaderParameter(i);
      return Unit.unit();
    }

    @Override public Unit vertexShaderVisitLocal(
      final @Nonnull UASTIDShaderVertexLocalValue<UASTIUnchecked> l)
      throws ModuleStructureError,
        ConstraintError
    {
      try {
        NameRestrictions.checkRestrictedExceptional(l.getValue().getName());

        final String name = l.getValue().getName().getActual();
        if (this.locals.containsKey(name)) {
          throw ModuleStructureError.moduleShaderLocalConflict(
            l.getValue(),
            this.locals.get(name).getValue());
        }
        this.locals.put(name, l);

        final ExpressionChecker ec = new ExpressionChecker();
        l.getValue().getExpression().expressionVisitableAccept(ec);
        return Unit.unit();
      } catch (final NameRestrictionsException x) {
        throw new ModuleStructureError(x);
      }
    }

    @Override public Unit vertexShaderVisitOutput(
      final @Nonnull UASTIDShaderVertexOutput<UASTIUnchecked> o)
      throws ModuleStructureError,
        ConstraintError
    {
      this.addShaderParameter(o);
      return Unit.unit();
    }

    @Override public Unit vertexShaderVisitOutputAssignment(
      final @Nonnull UASTIDShaderVertexOutputAssignment<UASTIUnchecked> a)
      throws ModuleStructureError,
        ConstraintError
    {
      this.addOutputAssignment(a);
      return Unit.unit();
    }

    @Override public Unit vertexShaderVisitParameter(
      final @Nonnull UASTIDShaderVertexParameter<UASTIUnchecked> p)
      throws ModuleStructureError,
        ConstraintError
    {
      this.addShaderParameter(p);
      return Unit.unit();
    }

    @Override public void vertexShaderVisitPre(
      final @Nonnull UASTIDShaderVertex<UASTIUnchecked> v)
      throws ModuleStructureError,
        ConstraintError
    {
      // Nothing
    }
  }

  public static @Nonnull ModuleStructure newModuleStructureChecker(
    final @Nonnull UASTICompilation<UASTIUnchecked> compilation,
    final @Nonnull Log log)
    throws ConstraintError
  {
    return new ModuleStructure(compilation, log);
  }

  @SuppressWarnings("unchecked") private static
    UASTICompilation<UASTIChecked>
    unsafeBrandChecked(
      final UASTICompilation<UASTIUnchecked> c)
  {
    final Object x = c;
    return (UASTICompilation<UASTIChecked>) x;
  }

  private final @Nonnull UASTICompilation<UASTIUnchecked> compilation;
  private final @Nonnull Log                              log;

  private ModuleStructure(
    final @Nonnull UASTICompilation<UASTIUnchecked> compilation,
    final @Nonnull Log log)
    throws ConstraintError
  {
    this.log = new Log(log, "module-structure");
    this.compilation =
      Constraints.constrainNotNull(compilation, "Compilation");
  }

  public @Nonnull UASTICompilation<UASTIChecked> check()
    throws ConstraintError,
      ModuleStructureError
  {
    final Map<ModulePathFlat, UASTIDModule<UASTIUnchecked>> modules =
      this.compilation.getModules();
    final Map<ModulePathFlat, ModulePath> paths = this.compilation.getPaths();

    for (final ModulePathFlat path : modules.keySet()) {
      this.log.debug(String.format("Checking module: %s", path.getActual()));

      final UASTIDModule<UASTIUnchecked> module = modules.get(path);
      assert module != null;

      module.moduleVisitableAccept(new ModuleChecker(
        paths.get(path),
        this.log));
    }

    return ModuleStructure.unsafeBrandChecked(this.compilation);
  }
}
