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
import java.util.List;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Option;
import com.io7m.jaux.functional.Option.Some;
import com.io7m.jaux.functional.PartialFunction;
import com.io7m.jlog.Log;
import com.io7m.jparasol.ModulePath;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.NameRestrictions;
import com.io7m.jparasol.NameRestrictions.NameRestrictionsException;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.lexer.Token.TokenIdentifierUpper;
import com.io7m.jparasol.untyped.ast.checked.UASTCCompilation;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDFunction;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDFunctionArgument;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDFunctionDefined;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDFunctionExternal;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDImport;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDModule;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDShader;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDShaderFragment;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDShaderFragmentInput;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDShaderFragmentLocal;
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
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDType;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDTypeRecord;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDTypeRecordField;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDValue;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDValueLocal;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDeclarationModuleLevel;
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
import com.io7m.jparasol.untyped.ast.checked.UASTCShaderPath;
import com.io7m.jparasol.untyped.ast.checked.UASTCTypePath;
import com.io7m.jparasol.untyped.ast.checked.UASTCValuePath;
import com.io7m.jparasol.untyped.ast.initial.UASTICompilation;
import com.io7m.jparasol.untyped.ast.initial.UASTIDRecordVisitor;
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
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderParameters;
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
import com.io7m.jparasol.untyped.ast.initial.UASTIShaderPath;
import com.io7m.jparasol.untyped.ast.initial.UASTITypePath;
import com.io7m.jparasol.untyped.ast.initial.UASTIValuePath;
import com.io7m.jparasol.untyped.ast.initial.UASTIVertexShaderLocalVisitor;
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
    UASTIExpressionVisitor<UASTCExpression, UASTCDValueLocal, ModuleStructureError>
  {
    public ExpressionChecker()
    {
      // Nothing
    }

    @Override public UASTCEApplication expressionVisitApplication(
      final @Nonnull List<UASTCExpression> arguments,
      final @Nonnull UASTIEApplication e)
      throws ModuleStructureError,
        ConstraintError
    {
      return new UASTCEApplication(
        ModuleStructure.valuePath(e.getName()),
        arguments);
    }

    @Override public void expressionVisitApplicationPre(
      final @Nonnull UASTIEApplication e)
      throws ModuleStructureError,
        ConstraintError
    {
      // Nothing
    }

    @Override public UASTCEBoolean expressionVisitBoolean(
      final @Nonnull UASTIEBoolean e)
      throws ModuleStructureError,
        ConstraintError
    {
      return new UASTCEBoolean(e.getToken());
    }

    @Override public UASTCEConditional expressionVisitConditional(
      final @Nonnull UASTCExpression condition,
      final @Nonnull UASTCExpression left,
      final @Nonnull UASTCExpression right,
      final @Nonnull UASTIEConditional e)
      throws ModuleStructureError,
        ConstraintError
    {
      return new UASTCEConditional(condition, left, right);
    }

    @Override public void expressionVisitConditionalPre(
      final @Nonnull UASTIEConditional e)
      throws ModuleStructureError,
        ConstraintError
    {
      // Nothing
    }

    @Override public UASTCEInteger expressionVisitInteger(
      final @Nonnull UASTIEInteger e)
      throws ModuleStructureError,
        ConstraintError
    {
      return new UASTCEInteger(e.getToken());
    }

    @Override public UASTCELet expressionVisitLet(
      final @Nonnull List<UASTCDValueLocal> bindings,
      final @Nonnull UASTCExpression body,
      final @Nonnull UASTIELet e)
      throws ModuleStructureError,
        ConstraintError
    {
      return new UASTCELet(e.getToken(), bindings, body);
    }

    @Override public LocalChecker expressionVisitLetPre(
      final @Nonnull UASTIELet e)
      throws ModuleStructureError,
        ConstraintError
    {
      return new LocalChecker();
    }

    @Override public UASTCENew expressionVisitNew(
      final @Nonnull List<UASTCExpression> args,
      final @Nonnull UASTIENew e)
      throws ModuleStructureError,
        ConstraintError
    {
      return new UASTCENew(ModuleStructure.typePath(e.getName()), args);
    }

    @Override public void expressionVisitNewPre(
      final @Nonnull UASTIENew e)
      throws ModuleStructureError,
        ConstraintError
    {
      // Nothing
    }

    @Override public UASTCEReal expressionVisitReal(
      final @Nonnull UASTIEReal e)
      throws ModuleStructureError,
        ConstraintError
    {
      return new UASTCEReal(e.getToken());
    }

    @Override public UASTCERecord expressionVisitRecord(
      final @Nonnull UASTIERecord e)
      throws ModuleStructureError,
        ConstraintError
    {
      try {
        final HashMap<String, UASTIRecordFieldAssignment> fields =
          new HashMap<String, UASTIRecordFieldAssignment>();
        final ArrayList<UASTCRecordFieldAssignment> assignments =
          new ArrayList<UASTCRecordFieldAssignment>();

        for (final UASTIRecordFieldAssignment f : e.getAssignments()) {
          final String name = f.getName().getActual();

          NameRestrictions.checkRestrictedExceptional(f.getName());

          if (fields.containsKey(name)) {
            throw ModuleStructureError.moduleRecordExpressionFieldDuplicate(
              f,
              fields.get(name));
          }

          final UASTIExpression r = f.getExpression();
          final ExpressionChecker ec = new ExpressionChecker();
          final UASTCExpression rx = r.expressionVisitableAccept(ec);

          assignments.add(new UASTCRecordFieldAssignment(f.getName(), rx));
          fields.put(name, f);
        }

        return new UASTCERecord(
          ModuleStructure.typePath(e.getTypePath()),
          assignments);
      } catch (final NameRestrictionsException x) {
        throw new ModuleStructureError(x);
      }
    }

    @Override public UASTCERecordProjection expressionVisitRecordProjection(
      final @Nonnull UASTCExpression body,
      final @Nonnull UASTIERecordProjection e)
      throws ModuleStructureError,
        ConstraintError
    {
      return new UASTCERecordProjection(body, e.getField());
    }

    @Override public void expressionVisitRecordProjectionPre(
      final @Nonnull UASTIERecordProjection e)
      throws ModuleStructureError,
        ConstraintError
    {
      // Nothing
    }

    @Override public UASTCESwizzle expressionVisitSwizzle(
      final @Nonnull UASTCExpression body,
      final @Nonnull UASTIESwizzle e)
      throws ModuleStructureError,
        ConstraintError
    {
      return new UASTCESwizzle(body, e.getFields());
    }

    @Override public void expressionVisitSwizzlePre(
      final @Nonnull UASTIESwizzle e)
      throws ModuleStructureError,
        ConstraintError
    {
      // Nothing
    }

    @Override public UASTCEVariable expressionVisitVariable(
      final @Nonnull UASTIEVariable e)
      throws ModuleStructureError,
        ConstraintError
    {
      try {
        NameRestrictions.checkRestrictedExceptional(e.getName().getName());
        return new UASTCEVariable(ModuleStructure.valuePath(e.getName()));
      } catch (final NameRestrictionsException x) {
        throw new ModuleStructureError(x);
      }
    }
  }

  private static class FragmentShaderChecker implements
    UASTIFragmentShaderVisitor<UASTCDShaderFragment, UASTCDShaderFragmentInput, UASTCDShaderFragmentParameter, UASTCDShaderFragmentOutput, UASTCDShaderFragmentLocal, UASTCDShaderFragmentOutputAssignment, ModuleStructureError>
  {
    private final @Nonnull HashMap<String, UASTIDShaderFragmentLocalValue>       locals;
    private final @Nonnull HashMap<String, UASTIDShaderFragmentOutputAssignment> output_assignments;
    private final @Nonnull HashMap<Integer, UASTIDShaderFragmentOutput>          output_indices;
    private int                                                                  output_max;
    private final @Nonnull HashMap<String, UASTIDShaderFragmentOutput>           outputs;
    private final @Nonnull HashMap<String, UASTIDShaderParameters>               parameters;
    private final @Nonnull UASTIDShaderFragment                                  shader;

    public FragmentShaderChecker(
      final @Nonnull UASTIDShaderFragment shader)
    {
      this.shader = shader;
      this.locals = new HashMap<String, UASTIDShaderFragmentLocalValue>();
      this.parameters = new HashMap<String, UASTIDShaderParameters>();
      this.outputs = new HashMap<String, UASTIDShaderFragmentOutput>();
      this.output_assignments =
        new HashMap<String, UASTIDShaderFragmentOutputAssignment>();
      this.output_max = -1;
      this.output_indices =
        new HashMap<Integer, UASTIDShaderFragmentOutput>();
    }

    private void addOutputAssignment(
      final UASTIDShaderFragmentOutputAssignment a)
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
      final UASTIDShaderParameters p)
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
          final UASTIDShaderFragmentOutput out =
            (UASTIDShaderFragmentOutput) p;

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
        final UASTIDShaderFragmentOutput output = this.outputs.get(p);
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

    @Override public
      UASTCDShaderFragment
      fragmentShaderVisit(
        final @Nonnull List<UASTCDShaderFragmentInput> in_inputs,
        final @Nonnull List<UASTCDShaderFragmentParameter> in_parameters,
        final @Nonnull List<UASTCDShaderFragmentOutput> in_outputs,
        final @Nonnull List<UASTCDShaderFragmentLocal> in_locals,
        final @Nonnull List<UASTCDShaderFragmentOutputAssignment> in_output_assignments,
        final UASTIDShaderFragment f)
        throws ModuleStructureError,
          ConstraintError
    {
      return new UASTCDShaderFragment(
        f.getName(),
        in_inputs,
        in_outputs,
        in_parameters,
        in_locals,
        in_output_assignments);
    }

    @Override public UASTCDShaderFragmentInput fragmentShaderVisitInput(
      final @Nonnull UASTIDShaderFragmentInput i)
      throws ModuleStructureError,
        ConstraintError
    {
      this.addShaderParameter(i);
      return new UASTCDShaderFragmentInput(
        i.getName(),
        ModuleStructure.typePath(i.getType()));
    }

    @Override public @Nonnull
      UASTIFragmentShaderLocalVisitor<UASTCDShaderFragmentLocal, ModuleStructureError>
      fragmentShaderVisitLocalsPre()
        throws ModuleStructureError,
          ConstraintError
    {
      return new FragmentShaderLocalChecker();
    }

    @Override public UASTCDShaderFragmentOutput fragmentShaderVisitOutput(
      final @Nonnull UASTIDShaderFragmentOutput o)
      throws ModuleStructureError,
        ConstraintError
    {
      this.addShaderParameter(o);
      return new UASTCDShaderFragmentOutput(
        o.getName(),
        ModuleStructure.typePath(o.getType()),
        o.getIndex());
    }

    @Override public
      UASTCDShaderFragmentOutputAssignment
      fragmentShaderVisitOutputAssignment(
        final @Nonnull UASTIDShaderFragmentOutputAssignment a)
        throws ModuleStructureError,
          ConstraintError
    {
      this.addOutputAssignment(a);
      final UASTCEVariable var =
        new UASTCEVariable(ModuleStructure.valuePath(a
          .getVariable()
          .getName()));
      return new UASTCDShaderFragmentOutputAssignment(a.getName(), var);
    }

    @Override public
      UASTCDShaderFragmentParameter
      fragmentShaderVisitParameter(
        final @Nonnull UASTIDShaderFragmentParameter p)
        throws ModuleStructureError,
          ConstraintError
    {
      this.addShaderParameter(p);
      return new UASTCDShaderFragmentParameter(
        p.getName(),
        ModuleStructure.typePath(p.getType()));
    }
  }

  private static class FragmentShaderLocalChecker implements
    UASTIFragmentShaderLocalVisitor<UASTCDShaderFragmentLocal, ModuleStructureError>
  {
    private final @Nonnull HashMap<String, UASTIDShaderFragmentLocalValue> locals;

    public FragmentShaderLocalChecker()
    {
      this.locals = new HashMap<String, UASTIDShaderFragmentLocalValue>();
    }

    @Override public
      UASTCDShaderFragmentLocalDiscard
      fragmentShaderVisitLocalDiscard(
        final @Nonnull UASTIDShaderFragmentLocalDiscard d)
        throws ModuleStructureError,
          ConstraintError
    {
      final UASTCExpression ex =
        d.getExpression().expressionVisitableAccept(new ExpressionChecker());
      return new UASTCDShaderFragmentLocalDiscard(d.getDiscard(), ex);
    }

    @Override public
      UASTCDShaderFragmentLocalValue
      fragmentShaderVisitLocalValue(
        final @Nonnull UASTIDShaderFragmentLocalValue v)
        throws ModuleStructureError,
          ConstraintError
    {
      try {
        final UASTIDValueLocal original = v.getValue();
        NameRestrictions.checkRestrictedExceptional(original.getName());

        final String name = original.getName().getActual();
        if (this.locals.containsKey(name)) {
          throw ModuleStructureError.moduleShaderLocalConflict(
            original,
            this.locals.get(name).getValue());
        }
        this.locals.put(name, v);

        final ExpressionChecker ec = new ExpressionChecker();
        final UASTCExpression ex =
          original.getExpression().expressionVisitableAccept(ec);
        final UASTCDValueLocal value =
          new UASTCDValueLocal(
            original.getName(),
            ModuleStructure.mapTypePath(original.getAscription()),
            ex);
        return new UASTCDShaderFragmentLocalValue(value);
      } catch (final NameRestrictionsException x) {
        throw new ModuleStructureError(x);
      }
    }
  }

  private static class FunctionChecker implements
    UASTIFunctionVisitor<UASTCDFunction, UASTCDFunctionArgument, ModuleStructureError>
  {
    private final HashMap<String, UASTIDFunctionArgument> args;

    public FunctionChecker()
    {
      this.args = new HashMap<String, UASTIDFunctionArgument>();
    }

    @Override public UASTCDFunctionArgument functionVisitArgument(
      final @Nonnull UASTIDFunctionArgument a)
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
        return new UASTCDFunctionArgument(
          a.getName(),
          ModuleStructure.typePath(a.getType()));
      } catch (final NameRestrictionsException x) {
        throw new ModuleStructureError(x);
      }
    }

    @Override public UASTCDFunctionDefined functionVisitDefined(
      final @Nonnull List<UASTCDFunctionArgument> arguments,
      final @Nonnull UASTIDFunctionDefined f)
      throws ModuleStructureError,
        ConstraintError
    {
      final ExpressionChecker ec = new ExpressionChecker();
      final UASTCExpression ex = f.getBody().expressionVisitableAccept(ec);
      return new UASTCDFunctionDefined(
        f.getName(),
        arguments,
        ModuleStructure.typePath(f.getReturnType()),
        ex);
    }

    @Override public void functionVisitDefinedPre(
      final @Nonnull UASTIDFunctionDefined f)
      throws ModuleStructureError,
        ConstraintError
    {
      // Nothing
    }

    @Override public UASTCDFunctionExternal functionVisitExternal(
      final @Nonnull List<UASTCDFunctionArgument> arguments,
      final @Nonnull UASTIDFunctionExternal f)
      throws ModuleStructureError,
        ConstraintError
    {
      return new UASTCDFunctionExternal(
        f.getName(),
        arguments,
        ModuleStructure.typePath(f.getReturnType()),
        f.getExternal());
    }

    @Override public void functionVisitExternalPre(
      final @Nonnull UASTIDFunctionExternal f)
      throws ModuleStructureError,
        ConstraintError
    {
      // Nothing
    }
  }

  private static class LocalChecker implements
    UASTILocalLevelVisitor<UASTCDValueLocal, ModuleStructureError>
  {
    private final @Nonnull HashMap<String, UASTIDValueLocal> values;

    public LocalChecker()
    {
      this.values = new HashMap<String, UASTIDValueLocal>();
    }

    @Override public UASTCDValueLocal localVisitValueLocal(
      final @Nonnull UASTIDValueLocal v)
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
        final UASTCExpression ex =
          v.getExpression().expressionVisitableAccept(ec);
        return new UASTCDValueLocal(
          v.getName(),
          ModuleStructure.mapTypePath(v.getAscription()),
          ex);
      } catch (final NameRestrictionsException x) {
        throw new ModuleStructureError(x);
      }
    }
  }

  private static class ModuleChecker implements
    UASTIModuleVisitor<UASTCDModule, UASTCDImport, UASTCDeclarationModuleLevel, ModuleStructureError>
  {
    private final @Nonnull ModulePath                            current_path;
    private final @Nonnull ModulePathFlat                        current_path_flat;
    private @CheckForNull ModuleDeclarationChecker               declaration_checker;
    private final @Nonnull HashMap<ModulePathFlat, UASTCDImport> imported_modules;
    private final @Nonnull HashMap<String, UASTCDImport>         imported_names;
    private final @Nonnull HashMap<String, UASTCDImport>         imported_renames;
    private final @Nonnull Log                                   log;

    public ModuleChecker(
      final @Nonnull ModulePath path,
      final @Nonnull Log log)
    {
      this.log = log;
      this.current_path = path;
      this.current_path_flat = ModulePathFlat.fromModulePath(path);

      this.imported_modules = new HashMap<ModulePathFlat, UASTCDImport>();
      this.imported_names = new HashMap<String, UASTCDImport>();
      this.imported_renames = new HashMap<String, UASTCDImport>();
    }

    private void addImport(
      final @Nonnull UASTCDImport i)
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
          final UASTCDImport original = this.imported_modules.get(flat);
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

    @SuppressWarnings("synthetic-access") @Override public
      UASTCDModule
      moduleVisit(
        final @Nonnull List<UASTCDImport> imports,
        final @Nonnull List<UASTCDeclarationModuleLevel> declarations,
        final @Nonnull UASTIDModule m)
        throws ModuleStructureError,
          ConstraintError
    {
      return new UASTCDModule(
        m.getPath(),
        imports,
        this.imported_modules,
        this.imported_names,
        this.imported_renames,
        declarations,
        this.declaration_checker.terms,
        this.declaration_checker.types,
        this.declaration_checker.shaders);
    }

    @Override public UASTCDImport moduleVisitImport(
      final @Nonnull UASTIDImport i)
      throws ModuleStructureError,
        ConstraintError
    {
      final UASTCDImport r = new UASTCDImport(i.getPath(), i.getRename());
      this.addImport(r);
      return r;
    }

    @Override public
      UASTIModuleLevelDeclarationVisitor<UASTCDeclarationModuleLevel, ModuleStructureError>
      moduleVisitPre(
        final @Nonnull UASTIDModule m)
        throws ModuleStructureError,
          ConstraintError
    {
      try {
        NameRestrictions.checkRestrictedExceptional(m.getPath().getName());

        final List<TokenIdentifierLower> c =
          this.current_path.getPackagePath().getComponents();
        for (final TokenIdentifierLower pc : c) {
          NameRestrictions.checkRestrictedExceptional(pc);
        }

        this.declaration_checker =
          new ModuleDeclarationChecker(this.current_path, this.log);

        return this.declaration_checker;
      } catch (final NameRestrictionsException e) {
        throw new ModuleStructureError(e);
      }
    }
  }

  private static class ModuleDeclarationChecker implements
    UASTIModuleLevelDeclarationVisitor<UASTCDeclarationModuleLevel, ModuleStructureError>
  {
    private final @Nonnull ModulePath                    current_path;
    private final @Nonnull ModulePathFlat                current_path_flat;
    private final @Nonnull Log                           log;
    private final @Nonnull HashMap<String, UASTCDShader> shaders;
    private final @Nonnull HashMap<String, UASTCDTerm>   terms;
    private final @Nonnull HashMap<String, UASTCDType>   types;

    public ModuleDeclarationChecker(
      final @Nonnull ModulePath path,
      final @Nonnull Log log)
    {
      this.log = log;
      this.current_path = path;
      this.current_path_flat = ModulePathFlat.fromModulePath(path);
      this.terms = new HashMap<String, UASTCDTerm>();
      this.types = new HashMap<String, UASTCDType>();
      this.shaders = new HashMap<String, UASTCDShader>();
    }

    private void addShader(
      final @Nonnull UASTCDShader shader)
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
          final UASTCDShader original = this.shaders.get(name);
          throw ModuleStructureError.moduleShaderConflict(shader, original);
        }

        this.shaders.put(name, shader);
      } catch (final NameRestrictionsException x) {
        throw new ModuleStructureError(x);
      }
    }

    private void addTerm(
      final @Nonnull UASTCDTerm term)
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
          final UASTCDTerm original = this.terms.get(name);
          throw ModuleStructureError.moduleTermConflict(term, original);
        }

        this.terms.put(name, term);
      } catch (final NameRestrictionsException x) {
        throw new ModuleStructureError(x);
      }
    }

    private void addType(
      final @Nonnull UASTCDType type)
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
          final UASTCDType original = this.types.get(name);
          throw ModuleStructureError.moduleTypeConflict(type, original);
        }

        this.types.put(name, type);
      } catch (final NameRestrictionsException x) {
        throw new ModuleStructureError(x);
      }
    }

    @Override public UASTCDShaderFragment moduleVisitFragmentShader(
      final @Nonnull UASTIDShaderFragment f)
      throws ModuleStructureError,
        ConstraintError
    {
      final FragmentShaderChecker c = new FragmentShaderChecker(f);
      final UASTCDShaderFragment r = f.fragmentShaderVisitableAccept(c);
      c.check();
      this.addShader(r);
      return r;
    }

    @Override public UASTCDFunction moduleVisitFunctionDefined(
      final @Nonnull UASTIDFunctionDefined f)
      throws ModuleStructureError,
        ConstraintError
    {
      final UASTCDFunction r =
        f.functionVisitableAccept(new FunctionChecker());
      this.addTerm(r);
      return r;
    }

    @Override public UASTCDFunction moduleVisitFunctionExternal(
      final @Nonnull UASTIDFunctionExternal f)
      throws ModuleStructureError,
        ConstraintError
    {
      final UASTCDFunction r =
        f.functionVisitableAccept(new FunctionChecker());
      this.addTerm(r);
      return r;
    }

    @Override public UASTCDShaderProgram moduleVisitProgramShader(
      final @Nonnull UASTIDShaderProgram p)
      throws ModuleStructureError,
        ConstraintError
    {
      final UASTCShaderPath vs =
        ModuleStructure.shaderPath(p.getVertexShader());
      final UASTCShaderPath fs =
        ModuleStructure.shaderPath(p.getFragmentShader());
      final UASTCDShaderProgram r =
        new UASTCDShaderProgram(p.getName(), vs, fs);
      this.addShader(r);
      return r;
    }

    @Override public UASTCDTypeRecord moduleVisitTypeRecord(
      final @Nonnull UASTIDTypeRecord r)
      throws ModuleStructureError,
        ConstraintError
    {
      final UASTCDTypeRecord rr =
        r.recordTypeVisitableAccept(new RecordTypeChecker());
      this.addType(rr);
      return rr;
    }

    @Override public UASTCDValue moduleVisitValue(
      final @Nonnull UASTIDValue v)
      throws ModuleStructureError,
        ConstraintError
    {
      final UASTCExpression e =
        v.getExpression().expressionVisitableAccept(new ExpressionChecker());
      final UASTCDValue rv =
        new UASTCDValue(v.getName(), ModuleStructure.mapTypePath(v
          .getAscription()), e);
      this.addTerm(rv);
      return rv;
    }

    @Override public UASTCDShaderVertex moduleVisitVertexShader(
      final @Nonnull UASTIDShaderVertex v)
      throws ModuleStructureError,
        ConstraintError
    {
      final VertexShaderChecker c = new VertexShaderChecker(v);
      final UASTCDShaderVertex r = v.vertexShaderVisitableAccept(c);
      c.check();
      this.addShader(r);
      return r;
    }
  }

  private static class RecordTypeChecker implements
    UASTIDRecordVisitor<UASTCDTypeRecord, UASTCDTypeRecordField, ModuleStructureError>
  {
    private final @Nonnull HashMap<String, UASTIDTypeRecordField> fields;

    public RecordTypeChecker()
    {
      this.fields = new HashMap<String, UASTIDTypeRecordField>();
    }

    @Override public UASTCDTypeRecord recordTypeVisit(
      final @Nonnull List<UASTCDTypeRecordField> u_fields,
      final @Nonnull UASTIDTypeRecord e)
      throws ModuleStructureError,
        ConstraintError
    {
      return new UASTCDTypeRecord(e.getName(), u_fields);
    }

    @Override public UASTCDTypeRecordField recordTypeVisitField(
      final @Nonnull UASTIDTypeRecordField e)
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
      return new UASTCDTypeRecordField(
        e.getName(),
        ModuleStructure.typePath(e.getType()));
    }

    @Override public void recordTypeVisitPre(
      final @Nonnull UASTIDTypeRecord e)
      throws ModuleStructureError,
        ConstraintError
    {
      // Nothing
    }
  }

  private static class VertexShaderChecker implements
    UASTIVertexShaderVisitor<UASTCDShaderVertex, UASTCDShaderVertexInput, UASTCDShaderVertexParameter, UASTCDShaderVertexOutput, UASTCDShaderVertexLocalValue, UASTCDShaderVertexOutputAssignment, ModuleStructureError>
  {
    private final @Nonnull HashMap<String, UASTIDShaderVertexLocalValue>       locals;
    private final @Nonnull HashMap<String, UASTIDShaderVertexOutputAssignment> output_assignments;
    private final @Nonnull HashMap<String, UASTIDShaderVertexOutput>           outputs;
    private final @Nonnull HashMap<String, UASTIDShaderParameters>             parameters;
    private final @Nonnull UASTIDShaderVertex                                  shader;

    public VertexShaderChecker(
      final @Nonnull UASTIDShaderVertex shader)
    {
      this.shader = shader;
      this.locals = new HashMap<String, UASTIDShaderVertexLocalValue>();
      this.parameters = new HashMap<String, UASTIDShaderParameters>();
      this.outputs = new HashMap<String, UASTIDShaderVertexOutput>();
      this.output_assignments =
        new HashMap<String, UASTIDShaderVertexOutputAssignment>();
    }

    private void addOutputAssignment(
      final UASTIDShaderVertexOutputAssignment a)
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
      final UASTIDShaderParameters p)
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
          final UASTIDShaderVertexOutput out = (UASTIDShaderVertexOutput) p;
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
        final UASTIDShaderVertexOutput output = this.outputs.get(p);
        if (this.output_assignments.containsKey(p) == false) {
          throw ModuleStructureError
            .moduleShaderOutputAssignmentMissing(output.getName());
        }
      }
    }

    @Override public
      UASTCDShaderVertex
      vertexShaderVisit(
        final @Nonnull List<UASTCDShaderVertexInput> r_inputs,
        final @Nonnull List<UASTCDShaderVertexParameter> r_parameters,
        final @Nonnull List<UASTCDShaderVertexOutput> r_outputs,
        final @Nonnull List<UASTCDShaderVertexLocalValue> r_locals,
        final @Nonnull List<UASTCDShaderVertexOutputAssignment> r_output_assignments,
        final @Nonnull UASTIDShaderVertex v)
        throws ModuleStructureError,
          ConstraintError
    {
      return new UASTCDShaderVertex(
        v.getName(),
        r_inputs,
        r_outputs,
        r_parameters,
        r_locals,
        r_output_assignments);
    }

    @Override public UASTCDShaderVertexInput vertexShaderVisitInput(
      final @Nonnull UASTIDShaderVertexInput i)
      throws ModuleStructureError,
        ConstraintError
    {
      this.addShaderParameter(i);
      return new UASTCDShaderVertexInput(
        i.getName(),
        ModuleStructure.typePath(i.getType()));
    }

    @Override public
      UASTIVertexShaderLocalVisitor<UASTCDShaderVertexLocalValue, ModuleStructureError>
      vertexShaderVisitLocalsPre()
        throws ModuleStructureError,
          ConstraintError
    {
      return new VertexShaderLocalChecker();
    }

    @Override public UASTCDShaderVertexOutput vertexShaderVisitOutput(
      final @Nonnull UASTIDShaderVertexOutput o)
      throws ModuleStructureError,
        ConstraintError
    {
      this.addShaderParameter(o);
      return new UASTCDShaderVertexOutput(
        o.getName(),
        ModuleStructure.typePath(o.getType()));
    }

    @Override public
      UASTCDShaderVertexOutputAssignment
      vertexShaderVisitOutputAssignment(
        final @Nonnull UASTIDShaderVertexOutputAssignment a)
        throws ModuleStructureError,
          ConstraintError
    {
      this.addOutputAssignment(a);
      final UASTCEVariable var =
        new UASTCEVariable(ModuleStructure.valuePath(a
          .getVariable()
          .getName()));
      return new UASTCDShaderVertexOutputAssignment(a.getName(), var);
    }

    @Override public UASTCDShaderVertexParameter vertexShaderVisitParameter(
      final @Nonnull UASTIDShaderVertexParameter p)
      throws ModuleStructureError,
        ConstraintError
    {
      this.addShaderParameter(p);
      return new UASTCDShaderVertexParameter(
        p.getName(),
        ModuleStructure.typePath(p.getType()));
    }
  }

  private static class VertexShaderLocalChecker implements
    UASTIVertexShaderLocalVisitor<UASTCDShaderVertexLocalValue, ModuleStructureError>
  {
    private final @Nonnull HashMap<String, UASTIDShaderVertexLocalValue> locals;

    public VertexShaderLocalChecker()
    {
      this.locals = new HashMap<String, UASTIDShaderVertexLocalValue>();
    }

    @Override public
      UASTCDShaderVertexLocalValue
      vertexShaderVisitLocalValue(
        final UASTIDShaderVertexLocalValue v)
        throws ModuleStructureError,
          ConstraintError
    {
      try {
        final UASTIDValueLocal original = v.getValue();
        NameRestrictions.checkRestrictedExceptional(original.getName());

        final String name = original.getName().getActual();
        if (this.locals.containsKey(name)) {
          throw ModuleStructureError.moduleShaderLocalConflict(
            original,
            this.locals.get(name).getValue());
        }
        this.locals.put(name, v);

        final ExpressionChecker ec = new ExpressionChecker();
        final UASTCExpression ex =
          original.getExpression().expressionVisitableAccept(ec);
        final UASTCDValueLocal value =
          new UASTCDValueLocal(
            original.getName(),
            ModuleStructure.mapTypePath(original.getAscription()),
            ex);

        return new UASTCDShaderVertexLocalValue(value);
      } catch (final NameRestrictionsException x) {
        throw new ModuleStructureError(x);
      }
    }
  }

  static @Nonnull Option<UASTCTypePath> mapTypePath(
    final @Nonnull Option<UASTITypePath> ascription)
    throws ConstraintError
  {
    return ascription
      .mapPartial(new PartialFunction<UASTITypePath, UASTCTypePath, ConstraintError>() {
        @Override public UASTCTypePath call(
          final @Nonnull UASTITypePath x)
          throws ConstraintError
        {
          return new UASTCTypePath(x.getModule(), x.getName());
        }
      });
  }

  public static @Nonnull ModuleStructure newModuleStructureChecker(
    final @Nonnull UASTICompilation compilation,
    final @Nonnull Log log)
    throws ConstraintError
  {
    return new ModuleStructure(compilation, log);
  }

  static @Nonnull UASTCShaderPath shaderPath(
    final @Nonnull UASTIShaderPath path)
    throws ConstraintError
  {
    return new UASTCShaderPath(path.getModule(), path.getName());
  }

  static @Nonnull UASTCTypePath typePath(
    final @Nonnull UASTITypePath name)
    throws ConstraintError
  {
    return new UASTCTypePath(name.getModule(), name.getName());
  }

  static @Nonnull UASTCValuePath valuePath(
    final @Nonnull UASTIValuePath name)
    throws ConstraintError
  {
    return new UASTCValuePath(name.getModule(), name.getName());
  }

  private final @Nonnull UASTICompilation compilation;
  private final @Nonnull Log              log;

  private ModuleStructure(
    final @Nonnull UASTICompilation compilation,
    final @Nonnull Log log)
    throws ConstraintError
  {
    this.log = new Log(log, "module-structure");
    this.compilation =
      Constraints.constrainNotNull(compilation, "Compilation");
  }

  public @Nonnull UASTCCompilation check()
    throws ConstraintError,
      ModuleStructureError
  {
    final Map<ModulePathFlat, UASTIDModule> modules =
      this.compilation.getModules();
    final Map<ModulePathFlat, UASTCDModule> r_modules =
      new HashMap<ModulePathFlat, UASTCDModule>();

    final Map<ModulePathFlat, ModulePath> paths = this.compilation.getPaths();

    for (final ModulePathFlat path : modules.keySet()) {
      this.log.debug(String.format("Checking module: %s", path.getActual()));

      final UASTIDModule module = modules.get(path);
      assert module != null;

      final UASTCDModule r_module =
        module.moduleVisitableAccept(new ModuleChecker(
          paths.get(path),
          this.log));

      r_modules.put(path, r_module);
    }

    return new UASTCCompilation(r_modules, paths);
  }
}
