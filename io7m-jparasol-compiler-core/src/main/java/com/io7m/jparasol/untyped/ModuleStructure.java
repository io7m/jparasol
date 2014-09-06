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

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jfunctional.Option;
import com.io7m.jfunctional.OptionType;
import com.io7m.jfunctional.Pair;
import com.io7m.jfunctional.PartialFunctionType;
import com.io7m.jfunctional.Some;
import com.io7m.jfunctional.Unit;
import com.io7m.jlog.LogUsableType;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jparasol.ModulePath;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.NameRestrictions;
import com.io7m.jparasol.NameRestrictions.NameRestrictionsException;
import com.io7m.jparasol.lexer.TokenIdentifierLower;
import com.io7m.jparasol.lexer.TokenIdentifierUpper;
import com.io7m.jparasol.untyped.ast.checked.UASTCCompilation;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDExternal;
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
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDType;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDTypeRecord;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDTypeRecordField;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDValue;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDValueDefined;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDValueExternal;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDValueLocal;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDeclarationModuleLevel;
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
import com.io7m.jparasol.untyped.ast.checked.UASTCExpressionMatchConstantType;
import com.io7m.jparasol.untyped.ast.checked.UASTCExpressionType;
import com.io7m.jparasol.untyped.ast.checked.UASTCRecordFieldAssignment;
import com.io7m.jparasol.untyped.ast.checked.UASTCShaderPath;
import com.io7m.jparasol.untyped.ast.checked.UASTCTypePath;
import com.io7m.jparasol.untyped.ast.checked.UASTCValuePath;
import com.io7m.jparasol.untyped.ast.initial.UASTICompilation;
import com.io7m.jparasol.untyped.ast.initial.UASTIDRecordVisitorType;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDExternal;
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
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentOutputData;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentOutputDepth;
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
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDValueDefined;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDValueExternal;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDValueLocal;
import com.io7m.jparasol.untyped.ast.initial.UASTIEApplication;
import com.io7m.jparasol.untyped.ast.initial.UASTIEBoolean;
import com.io7m.jparasol.untyped.ast.initial.UASTIEConditional;
import com.io7m.jparasol.untyped.ast.initial.UASTIEInteger;
import com.io7m.jparasol.untyped.ast.initial.UASTIELet;
import com.io7m.jparasol.untyped.ast.initial.UASTIEMatch;
import com.io7m.jparasol.untyped.ast.initial.UASTIENew;
import com.io7m.jparasol.untyped.ast.initial.UASTIEReal;
import com.io7m.jparasol.untyped.ast.initial.UASTIERecord;
import com.io7m.jparasol.untyped.ast.initial.UASTIERecordProjection;
import com.io7m.jparasol.untyped.ast.initial.UASTIESwizzle;
import com.io7m.jparasol.untyped.ast.initial.UASTIEVariable;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpressionMatchConstantVisitorType;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpressionType;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpressionVisitorType;
import com.io7m.jparasol.untyped.ast.initial.UASTIFragmentShaderLocalVisitorType;
import com.io7m.jparasol.untyped.ast.initial.UASTIFragmentShaderOutputVisitorType;
import com.io7m.jparasol.untyped.ast.initial.UASTIFragmentShaderVisitorType;
import com.io7m.jparasol.untyped.ast.initial.UASTIFunctionVisitorType;
import com.io7m.jparasol.untyped.ast.initial.UASTILocalLevelVisitorType;
import com.io7m.jparasol.untyped.ast.initial.UASTIModuleLevelDeclarationVisitorType;
import com.io7m.jparasol.untyped.ast.initial.UASTIModuleVisitorType;
import com.io7m.jparasol.untyped.ast.initial.UASTIRecordFieldAssignment;
import com.io7m.jparasol.untyped.ast.initial.UASTIShaderPath;
import com.io7m.jparasol.untyped.ast.initial.UASTITypePath;
import com.io7m.jparasol.untyped.ast.initial.UASTIValuePath;
import com.io7m.jparasol.untyped.ast.initial.UASTIValueVisitorType;
import com.io7m.jparasol.untyped.ast.initial.UASTIVertexShaderLocalVisitorType;
import com.io7m.jparasol.untyped.ast.initial.UASTIVertexShaderVisitorType;
import com.io7m.junreachable.UnreachableCodeException;

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
 * <li>Require that exactly one "main" output exists</li>
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
 * <li>Multiple fragment shader depth outputs (
 * <code>out depth out0 : float; out depth out1 : float;</code>)</li>
 * </ul>
 *
 * For units:
 *
 * <ul>
 * <li>Modules that import themselves (
 * <code>package x.y; module K is import x.y.K; end</code>)</li>
 * </ul>
 */

@EqualityReference public final class ModuleStructure
{
  @EqualityReference private static final class ExpressionChecker implements
    UASTIExpressionVisitorType<UASTCExpressionType, UASTCExpressionMatchConstantType, UASTCDValueLocal, ModuleStructureError>
  {
    public ExpressionChecker()
    {
      // Nothing
    }

    @Override public UASTCEApplication expressionVisitApplication(
      final List<UASTCExpressionType> arguments,
      final UASTIEApplication e)
      throws ModuleStructureError
    {
      return new UASTCEApplication(
        ModuleStructure.valuePath(e.getName()),
        arguments);
    }

    @Override public void expressionVisitApplicationPre(
      final UASTIEApplication e)
      throws ModuleStructureError
    {
      // Nothing
    }

    @Override public UASTCEBoolean expressionVisitBoolean(
      final UASTIEBoolean e)
      throws ModuleStructureError
    {
      return new UASTCEBoolean(e.getToken());
    }

    @Override public UASTCEConditional expressionVisitConditional(
      final UASTCExpressionType condition,
      final UASTCExpressionType left,
      final UASTCExpressionType right,
      final UASTIEConditional e)
      throws ModuleStructureError
    {
      return new UASTCEConditional(e.getIf(), condition, left, right);
    }

    @Override public void expressionVisitConditionalPre(
      final UASTIEConditional e)
      throws ModuleStructureError
    {
      // Nothing
    }

    @Override public UASTCEInteger expressionVisitInteger(
      final UASTIEInteger e)
      throws ModuleStructureError
    {
      return new UASTCEInteger(e.getToken());
    }

    @Override public UASTCELet expressionVisitLet(
      final List<UASTCDValueLocal> bindings,
      final UASTCExpressionType body,
      final UASTIELet e)
      throws ModuleStructureError
    {
      return new UASTCELet(e.getToken(), bindings, body);
    }

    @Override public LocalChecker expressionVisitLetPre(
      final UASTIELet e)
      throws ModuleStructureError
    {
      return new LocalChecker();
    }

    @Override public
      UASTCExpressionType
      expressionVisitMatch(
        @Nullable final UASTCExpressionType discriminee,
        @Nullable final List<Pair<UASTCExpressionMatchConstantType, UASTCExpressionType>> cases,
        @Nullable final OptionType<UASTCExpressionType> default_case,
        final UASTIEMatch m)
        throws ModuleStructureError
    {
      assert discriminee != null;
      assert cases != null;
      assert default_case != null;

      return new UASTCEMatch(
        m.getTokenMatch(),
        discriminee,
        cases,
        default_case);
    }

    @Override public void expressionVisitMatchDiscrimineePost()
      throws ModuleStructureError
    {
      // Nothing
    }

    @Override public void expressionVisitMatchDiscrimineePre()
      throws ModuleStructureError
    {
      // Nothing
    }

    @Override public @Nullable
      UASTIExpressionMatchConstantVisitorType<UASTCExpressionMatchConstantType, ModuleStructureError>
      expressionVisitMatchPre(
        final UASTIEMatch m)
        throws ModuleStructureError
    {
      return new MatchConstantChecker();
    }

    @Override public UASTCENew expressionVisitNew(
      final List<UASTCExpressionType> args,
      final UASTIENew e)
      throws ModuleStructureError
    {
      return new UASTCENew(ModuleStructure.typePath(e.getName()), args);
    }

    @Override public void expressionVisitNewPre(
      final UASTIENew e)
      throws ModuleStructureError
    {
      // Nothing
    }

    @Override public UASTCEReal expressionVisitReal(
      final UASTIEReal e)
      throws ModuleStructureError
    {
      return new UASTCEReal(e.getToken());
    }

    @Override public UASTCERecord expressionVisitRecord(
      final UASTIERecord e)
      throws ModuleStructureError
    {
      try {
        final Map<String, UASTIRecordFieldAssignment> fields =
          new HashMap<String, UASTIRecordFieldAssignment>();
        final List<UASTCRecordFieldAssignment> assignments =
          new ArrayList<UASTCRecordFieldAssignment>();

        for (final UASTIRecordFieldAssignment f : e.getAssignments()) {
          final String name = f.getName().getActual();

          NameRestrictions.checkRestrictedExceptional(f.getName());

          if (fields.containsKey(name)) {
            throw ModuleStructureError.moduleRecordExpressionFieldDuplicate(
              f,
              fields.get(name));
          }

          final UASTIExpressionType r = f.getExpression();
          final ExpressionChecker ec = new ExpressionChecker();
          final UASTCExpressionType rx = r.expressionVisitableAccept(ec);

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
      final UASTCExpressionType body,
      final UASTIERecordProjection e)
      throws ModuleStructureError
    {
      return new UASTCERecordProjection(body, e.getField());
    }

    @Override public void expressionVisitRecordProjectionPre(
      final UASTIERecordProjection e)
      throws ModuleStructureError
    {
      // Nothing
    }

    @Override public UASTCESwizzle expressionVisitSwizzle(
      final UASTCExpressionType body,
      final UASTIESwizzle e)
      throws ModuleStructureError
    {
      return new UASTCESwizzle(body, e.getFields());
    }

    @Override public void expressionVisitSwizzlePre(
      final UASTIESwizzle e)
      throws ModuleStructureError
    {
      // Nothing
    }

    @Override public UASTCEVariable expressionVisitVariable(
      final UASTIEVariable e)
      throws ModuleStructureError
    {
      try {
        NameRestrictions.checkRestrictedExceptional(e.getName().getName());
        return new UASTCEVariable(ModuleStructure.valuePath(e.getName()));
      } catch (final NameRestrictionsException x) {
        throw new ModuleStructureError(x);
      }
    }
  }

  @EqualityReference private static final class FragmentShaderChecker implements
    UASTIFragmentShaderVisitorType<UASTCDShaderFragment, UASTCDShaderFragmentInput, UASTCDShaderFragmentParameter, UASTCDShaderFragmentOutput, UASTCDShaderFragmentLocal, UASTCDShaderFragmentOutputAssignment, ModuleStructureError>
  {
    private final Map<String, UASTIDShaderFragmentLocalValue>       locals;
    private final Map<String, UASTIDShaderFragmentOutputAssignment> output_assignments;
    private @Nullable UASTIDShaderFragmentOutputDepth               output_depth;
    private final Map<Integer, UASTIDShaderFragmentOutputData>      output_indices;
    private int                                                     output_max;
    private final Map<String, UASTIDShaderFragmentOutput>           outputs;
    private final Map<String, UASTIDShaderParameters>               parameters;
    private final UASTIDShaderFragment                              shader;

    public FragmentShaderChecker(
      final UASTIDShaderFragment in_shader)
    {
      this.shader = in_shader;
      this.locals = new HashMap<String, UASTIDShaderFragmentLocalValue>();
      this.parameters = new HashMap<String, UASTIDShaderParameters>();
      this.outputs = new HashMap<String, UASTIDShaderFragmentOutput>();
      this.output_assignments =
        new HashMap<String, UASTIDShaderFragmentOutputAssignment>();
      this.output_max = -1;
      this.output_indices =
        new HashMap<Integer, UASTIDShaderFragmentOutputData>();
      this.output_depth = null;
    }

    private void addOutputAssignment(
      final UASTIDShaderFragmentOutputAssignment a)
      throws ModuleStructureError
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

    @SuppressWarnings("synthetic-access") private void addShaderParameter(
      final UASTIDShaderParameters p)
      throws ModuleStructureError
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

          final Map<Integer, UASTIDShaderFragmentOutputData> out_ind =
            this.output_indices;

          out
            .fragmentShaderOutputVisitableAccept(new UASTIFragmentShaderOutputVisitorType<Unit, ModuleStructureError>() {
              @Override public Unit fragmentShaderVisitOutputData(
                final UASTIDShaderFragmentOutputData out_data)
                throws ModuleStructureError
              {
                if (out_data.getIndex() < 0) {
                  throw ModuleStructureError
                    .moduleShaderOutputIndexInvalid(out_data);
                }

                FragmentShaderChecker.this.output_max =
                  Math.max(
                    out_data.getIndex(),
                    FragmentShaderChecker.this.output_max);

                final Integer current_index =
                  Integer.valueOf(out_data.getIndex());
                if (out_ind.containsKey(current_index)) {
                  throw ModuleStructureError
                    .moduleShaderOutputIndexDuplicate(
                      out_data,
                      out_ind.get(current_index));
                }

                out_ind.put(current_index, out_data);
                return Unit.unit();
              }

              @Override public Unit fragmentShaderVisitOutputDepth(
                final UASTIDShaderFragmentOutputDepth v)
                throws ModuleStructureError
              {
                final UASTIDShaderFragmentOutputDepth d =
                  FragmentShaderChecker.this.output_depth;

                if (d != null) {
                  throw ModuleStructureError
                    .moduleShaderFragmentOutputDepthDuplicate(d, v);
                }

                FragmentShaderChecker.this.output_depth = v;
                return Unit.unit();
              }
            });

          this.outputs.put(name, out);
        }

      } catch (final NameRestrictionsException x) {
        throw new ModuleStructureError(x);
      }
    }

    public void check()
      throws ModuleStructureError
    {
      this.checkOutputAssignmentsComplete();
      this.checkOutputsContinuous();
    }

    public void checkOutputAssignmentsComplete()
      throws ModuleStructureError
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
      throws ModuleStructureError
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

    @Override public UASTCDShaderFragment fragmentShaderVisit(
      final List<UASTCDShaderFragmentInput> in_inputs,
      final List<UASTCDShaderFragmentParameter> in_parameters,
      final List<UASTCDShaderFragmentOutput> in_outputs,
      final List<UASTCDShaderFragmentLocal> in_locals,
      final List<UASTCDShaderFragmentOutputAssignment> in_output_assignments,
      final UASTIDShaderFragment f)
      throws ModuleStructureError
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
      final UASTIDShaderFragmentInput i)
      throws ModuleStructureError
    {
      this.addShaderParameter(i);
      return new UASTCDShaderFragmentInput(
        i.getName(),
        ModuleStructure.typePath(i.getType()));
    }

    @Override public
      UASTIFragmentShaderLocalVisitorType<UASTCDShaderFragmentLocal, ModuleStructureError>
      fragmentShaderVisitLocalsPre()
        throws ModuleStructureError
    {
      return new FragmentShaderLocalChecker();
    }

    @Override public
      UASTCDShaderFragmentOutputAssignment
      fragmentShaderVisitOutputAssignment(
        final UASTIDShaderFragmentOutputAssignment a)
        throws ModuleStructureError
    {
      this.addOutputAssignment(a);
      final UASTCEVariable var =
        new UASTCEVariable(ModuleStructure.valuePath(a
          .getVariable()
          .getName()));
      return new UASTCDShaderFragmentOutputAssignment(a.getName(), var);
    }

    @SuppressWarnings("synthetic-access") @Override public
      UASTIFragmentShaderOutputVisitorType<UASTCDShaderFragmentOutput, ModuleStructureError>
      fragmentShaderVisitOutputsPre()
        throws ModuleStructureError
    {
      return new UASTIFragmentShaderOutputVisitorType<UASTCDShaderFragmentOutput, ModuleStructureError>() {
        @Override public
          UASTCDShaderFragmentOutputData
          fragmentShaderVisitOutputData(
            final UASTIDShaderFragmentOutputData d)
            throws ModuleStructureError
        {
          FragmentShaderChecker.this.addShaderParameter(d);
          return new UASTCDShaderFragmentOutputData(
            d.getName(),
            ModuleStructure.typePath(d.getType()),
            d.getIndex());
        }

        @Override public
          UASTCDShaderFragmentOutputDepth
          fragmentShaderVisitOutputDepth(
            final UASTIDShaderFragmentOutputDepth d)
            throws ModuleStructureError
        {
          FragmentShaderChecker.this.addShaderParameter(d);
          return new UASTCDShaderFragmentOutputDepth(
            d.getName(),
            ModuleStructure.typePath(d.getType()));
        }
      };
    }

    @Override public
      UASTCDShaderFragmentParameter
      fragmentShaderVisitParameter(
        final UASTIDShaderFragmentParameter p)
        throws ModuleStructureError
    {
      this.addShaderParameter(p);
      return new UASTCDShaderFragmentParameter(
        p.getName(),
        ModuleStructure.typePath(p.getType()));
    }
  }

  @EqualityReference private static final class FragmentShaderLocalChecker implements
    UASTIFragmentShaderLocalVisitorType<UASTCDShaderFragmentLocal, ModuleStructureError>
  {
    private final Map<String, UASTIDShaderFragmentLocalValue> locals;

    public FragmentShaderLocalChecker()
    {
      this.locals = new HashMap<String, UASTIDShaderFragmentLocalValue>();
    }

    @Override public
      UASTCDShaderFragmentLocalDiscard
      fragmentShaderVisitLocalDiscard(
        final UASTIDShaderFragmentLocalDiscard d)
        throws ModuleStructureError
    {
      final UASTCExpressionType ex =
        d.getExpression().expressionVisitableAccept(new ExpressionChecker());

      return new UASTCDShaderFragmentLocalDiscard(d.getDiscard(), ex);
    }

    @Override public
      UASTCDShaderFragmentLocalValue
      fragmentShaderVisitLocalValue(
        final UASTIDShaderFragmentLocalValue v)
        throws ModuleStructureError
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
        final UASTCExpressionType ex =
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

  @EqualityReference private static final class FunctionChecker implements
    UASTIFunctionVisitorType<UASTCDFunction, UASTCDFunctionArgument, ModuleStructureError>
  {
    private final Map<String, UASTIDFunctionArgument> args;

    public FunctionChecker()
    {
      this.args = new HashMap<String, UASTIDFunctionArgument>();
    }

    @Override public UASTCDFunctionArgument functionVisitArgument(
      final UASTIDFunctionArgument a)
      throws ModuleStructureError
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
      final List<UASTCDFunctionArgument> arguments,
      final UASTIDFunctionDefined f)
      throws ModuleStructureError
    {
      final ExpressionChecker ec = new ExpressionChecker();
      final UASTCExpressionType ex =
        f.getBody().expressionVisitableAccept(ec);
      return new UASTCDFunctionDefined(
        f.getName(),
        arguments,
        ModuleStructure.typePath(f.getReturnType()),
        ex);
    }

    @Override public void functionVisitDefinedPre(
      final UASTIDFunctionDefined f)
      throws ModuleStructureError
    {
      // Nothing
    }

    @Override public UASTCDFunctionExternal functionVisitExternal(
      final List<UASTCDFunctionArgument> arguments,
      final UASTIDFunctionExternal f)
      throws ModuleStructureError
    {
      final UASTIDExternal ext = f.getExternal();

      final OptionType<UASTIExpressionType> original_emulation =
        ext.getEmulation();
      final OptionType<UASTCExpressionType> emulation =
        original_emulation
          .mapPartial(new PartialFunctionType<UASTIExpressionType, UASTCExpressionType, ModuleStructureError>() {
            @Override public UASTCExpressionType call(
              final UASTIExpressionType x)
              throws ModuleStructureError
            {
              return x.expressionVisitableAccept(new ExpressionChecker());
            }
          });

      return new UASTCDFunctionExternal(
        f.getName(),
        arguments,
        ModuleStructure.typePath(f.getReturnType()),
        new UASTCDExternal(
          ext.getName(),
          ext.isVertexShaderAllowed(),
          ext.isFragmentShaderAllowed(),
          emulation));
    }

    @Override public void functionVisitExternalPre(
      final UASTIDFunctionExternal f)
      throws ModuleStructureError
    {
      // Nothing
    }
  }

  @EqualityReference private static final class LocalChecker implements
    UASTILocalLevelVisitorType<UASTCDValueLocal, ModuleStructureError>
  {
    public LocalChecker()
    {

    }

    @Override public UASTCDValueLocal localVisitValueLocal(
      final UASTIDValueLocal v)
      throws ModuleStructureError
    {
      try {
        final TokenIdentifierLower name = v.getName();
        NameRestrictions.checkRestrictedExceptional(name);

        final ExpressionChecker ec = new ExpressionChecker();
        final UASTCExpressionType ex =
          v.getExpression().expressionVisitableAccept(ec);
        return new UASTCDValueLocal(name, ModuleStructure.mapTypePath(v
          .getAscription()), ex);
      } catch (final NameRestrictionsException x) {
        throw new ModuleStructureError(x);
      }
    }
  }

  @EqualityReference private static final class MatchConstantChecker implements
    UASTIExpressionMatchConstantVisitorType<UASTCExpressionMatchConstantType, ModuleStructureError>
  {
    public MatchConstantChecker()
    {
      // Nothing
    }

    @Override public UASTCExpressionMatchConstantType expressionVisitBoolean(
      final UASTIEBoolean e)
      throws ModuleStructureError
    {
      return new UASTCEBoolean(e.getToken());
    }

    @Override public UASTCExpressionMatchConstantType expressionVisitInteger(
      final UASTIEInteger e)
      throws ModuleStructureError
    {
      return new UASTCEInteger(e.getToken());
    }
  }

  @EqualityReference private static final class ModuleChecker implements
    UASTIModuleVisitorType<UASTCDModule, UASTCDImport, UASTCDeclarationModuleLevel, ModuleStructureError>
  {
    private final ModulePath                        current_path;
    private final ModulePathFlat                    current_path_flat;
    private @Nullable ModuleDeclarationChecker      declaration_checker;
    private final Map<ModulePathFlat, UASTCDImport> imported_modules;
    private final Map<String, UASTCDImport>         imported_names;
    private final Map<String, UASTCDImport>         imported_renames;
    private final LogUsableType                     log;

    public ModuleChecker(
      final ModulePath path,
      final LogUsableType in_log)
    {
      this.log = in_log;
      this.current_path = path;
      this.current_path_flat = ModulePathFlat.fromModulePath(path);

      this.imported_modules = new HashMap<ModulePathFlat, UASTCDImport>();
      this.imported_names = new HashMap<String, UASTCDImport>();
      this.imported_renames = new HashMap<String, UASTCDImport>();
    }

    private void addImport(
      final UASTCDImport i)
      throws ModuleStructureError
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

        final OptionType<TokenIdentifierUpper> rename_opt = i.getRename();
        final TokenIdentifierUpper import_name = path.getName();

        if (rename_opt.isSome()) {
          final TokenIdentifierUpper rename =
            ((Some<TokenIdentifierUpper>) rename_opt).get();

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
        final List<UASTCDImport> imports,
        final List<UASTCDeclarationModuleLevel> declarations,
        final UASTIDModule m)
        throws ModuleStructureError
    {
      final ModuleDeclarationChecker d = this.declaration_checker;
      assert d != null;

      return new UASTCDModule(
        m.getPath(),
        imports,
        this.imported_modules,
        this.imported_names,
        this.imported_renames,
        declarations,
        d.terms,
        d.types,
        d.shaders);
    }

    @Override public UASTCDImport moduleVisitImport(
      final UASTIDImport i)
      throws ModuleStructureError
    {
      final UASTCDImport r = new UASTCDImport(i.getPath(), i.getRename());
      this.addImport(r);
      return r;
    }

    @Override public
      UASTIModuleLevelDeclarationVisitorType<UASTCDeclarationModuleLevel, ModuleStructureError>
      moduleVisitPre(
        final UASTIDModule m)
        throws ModuleStructureError
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

  @EqualityReference private static final class ModuleDeclarationChecker implements
    UASTIModuleLevelDeclarationVisitorType<UASTCDeclarationModuleLevel, ModuleStructureError>
  {
    private final ModulePath                current_path;
    private final ModulePathFlat            current_path_flat;
    private final LogUsableType             log;
    private final Map<String, UASTCDShader> shaders;
    private final Map<String, UASTCDTerm>   terms;
    private final Map<String, UASTCDType>   types;

    public ModuleDeclarationChecker(
      final ModulePath path,
      final LogUsableType in_log)
    {
      this.log = in_log;
      this.current_path = path;
      this.current_path_flat = ModulePathFlat.fromModulePath(path);
      this.terms = new HashMap<String, UASTCDTerm>();
      this.types = new HashMap<String, UASTCDType>();
      this.shaders = new HashMap<String, UASTCDShader>();
    }

    private void addShader(
      final UASTCDShader shader)
      throws ModuleStructureError
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
      final UASTCDTerm term)
      throws ModuleStructureError
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
      final UASTCDType type)
      throws ModuleStructureError
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
      final UASTIDShaderFragment f)
      throws ModuleStructureError
    {
      final FragmentShaderChecker c = new FragmentShaderChecker(f);
      final UASTCDShaderFragment r = f.fragmentShaderVisitableAccept(c);
      c.check();
      this.addShader(r);
      return r;
    }

    @Override public UASTCDFunction moduleVisitFunctionDefined(
      final UASTIDFunctionDefined f)
      throws ModuleStructureError
    {
      final UASTCDFunction r =
        f.functionVisitableAccept(new FunctionChecker());
      this.addTerm(r);
      return r;
    }

    @Override public UASTCDFunction moduleVisitFunctionExternal(
      final UASTIDFunctionExternal f)
      throws ModuleStructureError
    {
      final UASTCDFunction r =
        f.functionVisitableAccept(new FunctionChecker());
      this.addTerm(r);
      return r;
    }

    @Override public UASTCDShaderProgram moduleVisitProgramShader(
      final UASTIDShaderProgram p)
      throws ModuleStructureError
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
      final UASTIDTypeRecord r)
      throws ModuleStructureError
    {
      final UASTCDTypeRecord rr =
        r.recordTypeVisitableAccept(new RecordTypeChecker());
      this.addType(rr);
      return rr;
    }

    @Override public UASTCDValue moduleVisitValue(
      final UASTIDValue v)
      throws ModuleStructureError
    {
      final UASTCDValue r = v.valueVisitableAccept(new ValueChecker());
      this.addTerm(r);
      return r;
    }

    @Override public UASTCDValue moduleVisitValueExternal(
      final UASTIDValueExternal v)
      throws ModuleStructureError
    {
      final UASTCDValue r = v.valueVisitableAccept(new ValueChecker());
      this.addTerm(r);
      return r;
    }

    @Override public UASTCDShaderVertex moduleVisitVertexShader(
      final UASTIDShaderVertex v)
      throws ModuleStructureError
    {
      final VertexShaderChecker c = new VertexShaderChecker(v);
      final UASTCDShaderVertex r = v.vertexShaderVisitableAccept(c);
      c.check();
      this.addShader(r);
      return r;
    }
  }

  @EqualityReference private static final class RecordTypeChecker implements
    UASTIDRecordVisitorType<UASTCDTypeRecord, UASTCDTypeRecordField, ModuleStructureError>
  {
    private final Map<String, UASTIDTypeRecordField> fields;

    public RecordTypeChecker()
    {
      this.fields = new HashMap<String, UASTIDTypeRecordField>();
    }

    @Override public UASTCDTypeRecord recordTypeVisit(
      final List<UASTCDTypeRecordField> u_fields,
      final UASTIDTypeRecord e)
      throws ModuleStructureError
    {
      return new UASTCDTypeRecord(e.getName(), u_fields);
    }

    @Override public UASTCDTypeRecordField recordTypeVisitField(
      final UASTIDTypeRecordField e)
      throws ModuleStructureError
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
      final UASTIDTypeRecord e)
      throws ModuleStructureError
    {
      // Nothing
    }
  }

  @EqualityReference private static final class ValueChecker implements
    UASTIValueVisitorType<UASTCDValue, ModuleStructureError>
  {
    public ValueChecker()
    {
      // Nothing
    }

    @Override public UASTCDValueDefined valueVisitDefined(
      final UASTIDValueDefined v)
      throws ModuleStructureError
    {
      final UASTCExpressionType e =
        v.getExpression().expressionVisitableAccept(new ExpressionChecker());
      final UASTCDValueDefined rv =
        new UASTCDValueDefined(v.getName(), ModuleStructure.mapTypePath(v
          .getAscription()), e);
      return rv;
    }

    @Override public UASTCDValue valueVisitExternal(
      final UASTIDValueExternal v)
      throws ModuleStructureError
    {
      final UASTIDExternal original_external = v.getExternal();

      if (original_external.getEmulation().isSome()) {
        throw ModuleStructureError.moduleValueExternalHasExpression(v);
      }

      final OptionType<UASTITypePath> ascription = v.getAscription();
      if (ascription.isNone()) {
        throw ModuleStructureError.moduleValueExternalLacksAscription(v);
      }

      final OptionType<UASTCExpressionType> none = Option.none();
      final UASTCDExternal external =
        new UASTCDExternal(
          original_external.getName(),
          original_external.isVertexShaderAllowed(),
          original_external.isFragmentShaderAllowed(),
          none);

      final Some<UASTITypePath> some = (Some<UASTITypePath>) ascription;
      final UASTCTypePath type = ModuleStructure.typePath(some.get());
      final UASTCDValueExternal ve =
        new UASTCDValueExternal(v.getName(), type, external);
      return ve;
    }
  }

  @EqualityReference private static final class VertexShaderChecker implements
    UASTIVertexShaderVisitorType<UASTCDShaderVertex, UASTCDShaderVertexInput, UASTCDShaderVertexParameter, UASTCDShaderVertexOutput, UASTCDShaderVertexLocalValue, UASTCDShaderVertexOutputAssignment, ModuleStructureError>
  {
    private final Map<String, UASTIDShaderVertexLocalValue>       locals;
    private @Nullable UASTIDShaderVertexOutput                    main;
    private final Map<String, UASTIDShaderVertexOutputAssignment> output_assignments;
    private final Map<String, UASTIDShaderVertexOutput>           outputs;
    private final Map<String, UASTIDShaderParameters>             parameters;
    private final UASTIDShaderVertex                              shader;

    public VertexShaderChecker(
      final UASTIDShaderVertex in_shader)
    {
      this.shader = in_shader;
      this.locals = new HashMap<String, UASTIDShaderVertexLocalValue>();
      this.parameters = new HashMap<String, UASTIDShaderParameters>();
      this.outputs = new HashMap<String, UASTIDShaderVertexOutput>();
      this.output_assignments =
        new HashMap<String, UASTIDShaderVertexOutputAssignment>();
    }

    private void addOutputAssignment(
      final UASTIDShaderVertexOutputAssignment a)
      throws ModuleStructureError
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
      throws ModuleStructureError
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
      throws ModuleStructureError
    {
      this.checkOutputAssignmentsComplete();
    }

    public void checkOutputAssignmentsComplete()
      throws ModuleStructureError
    {
      for (final String p : this.outputs.keySet()) {
        final UASTIDShaderVertexOutput output = this.outputs.get(p);
        if (this.output_assignments.containsKey(p) == false) {
          throw ModuleStructureError
            .moduleShaderOutputAssignmentMissing(output.getName());
        }
      }
    }

    @Override public UASTCDShaderVertex vertexShaderVisit(
      final List<UASTCDShaderVertexInput> r_inputs,
      final List<UASTCDShaderVertexParameter> r_parameters,
      final List<UASTCDShaderVertexOutput> r_outputs,
      final List<UASTCDShaderVertexLocalValue> r_locals,
      final List<UASTCDShaderVertexOutputAssignment> r_output_assignments,
      final UASTIDShaderVertex v)
      throws ModuleStructureError
    {
      if (this.main == null) {
        throw ModuleStructureError.moduleShaderVertexOutputMissingMain(v
          .getName());
      }

      return new UASTCDShaderVertex(
        v.getName(),
        r_inputs,
        r_outputs,
        r_parameters,
        r_locals,
        r_output_assignments);
    }

    @Override public UASTCDShaderVertexInput vertexShaderVisitInput(
      final UASTIDShaderVertexInput i)
      throws ModuleStructureError
    {
      this.addShaderParameter(i);
      return new UASTCDShaderVertexInput(
        i.getName(),
        ModuleStructure.typePath(i.getType()));
    }

    @Override public
      UASTIVertexShaderLocalVisitorType<UASTCDShaderVertexLocalValue, ModuleStructureError>
      vertexShaderVisitLocalsPre()
        throws ModuleStructureError
    {
      return new VertexShaderLocalChecker();
    }

    @Override public UASTCDShaderVertexOutput vertexShaderVisitOutput(
      final UASTIDShaderVertexOutput o)
      throws ModuleStructureError
    {
      if (o.isMain()) {
        if (this.main != null) {
          throw ModuleStructureError.moduleShaderVertexOutputMultipleMains(
            this.main,
            o);
        }
        this.main = o;
      }

      this.addShaderParameter(o);
      return new UASTCDShaderVertexOutput(
        o.getName(),
        ModuleStructure.typePath(o.getType()),
        o.isMain());
    }

    @Override public
      UASTCDShaderVertexOutputAssignment
      vertexShaderVisitOutputAssignment(
        final UASTIDShaderVertexOutputAssignment a)
        throws ModuleStructureError
    {
      this.addOutputAssignment(a);
      final UASTCEVariable var =
        new UASTCEVariable(ModuleStructure.valuePath(a
          .getVariable()
          .getName()));
      return new UASTCDShaderVertexOutputAssignment(a.getName(), var);
    }

    @Override public UASTCDShaderVertexParameter vertexShaderVisitParameter(
      final UASTIDShaderVertexParameter p)
      throws ModuleStructureError
    {
      this.addShaderParameter(p);
      return new UASTCDShaderVertexParameter(
        p.getName(),
        ModuleStructure.typePath(p.getType()));
    }
  }

  @EqualityReference private static final class VertexShaderLocalChecker implements
    UASTIVertexShaderLocalVisitorType<UASTCDShaderVertexLocalValue, ModuleStructureError>
  {
    private final Map<String, UASTIDShaderVertexLocalValue> locals;

    public VertexShaderLocalChecker()
    {
      this.locals = new HashMap<String, UASTIDShaderVertexLocalValue>();
    }

    @Override public
      UASTCDShaderVertexLocalValue
      vertexShaderVisitLocalValue(
        final UASTIDShaderVertexLocalValue v)
        throws ModuleStructureError
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
        final UASTCExpressionType ex =
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

  static OptionType<UASTCTypePath> mapTypePath(
    final OptionType<UASTITypePath> ascription)
  {
    return ascription
      .mapPartial(new PartialFunctionType<UASTITypePath, UASTCTypePath, UnreachableCodeException>() {
        @Override public UASTCTypePath call(
          final UASTITypePath x)
        {
          return new UASTCTypePath(x.getModule(), x.getName());
        }
      });
  }

  /**
   * Construct a new module structure checker.
   *
   * @param compilation
   *          The AST
   * @param log
   *          A log interface
   * @return A new module structure checker
   */

  public static ModuleStructure newModuleStructureChecker(
    final UASTICompilation compilation,
    final LogUsableType log)
  {
    return new ModuleStructure(compilation, log);
  }

  static UASTCShaderPath shaderPath(
    final UASTIShaderPath path)
  {
    return new UASTCShaderPath(path.getModule(), path.getName());
  }

  static UASTCTypePath typePath(
    final UASTITypePath name)
  {
    return new UASTCTypePath(name.getModule(), name.getName());
  }

  static UASTCValuePath valuePath(
    final UASTIValuePath name)
  {
    return new UASTCValuePath(name.getModule(), name.getName());
  }

  private final UASTICompilation compilation;
  private final LogUsableType    log;

  private ModuleStructure(
    final UASTICompilation in_compilation,
    final LogUsableType in_log)
  {
    this.log = NullCheck.notNull(in_log, "Log").with("module-structure");
    this.compilation = NullCheck.notNull(in_compilation, "Compilation");
  }

  /**
   * Check the current AST
   *
   * @return A checked AST
   * @throws ModuleStructureError
   *           If an error occurs
   */

  public UASTCCompilation check()
    throws ModuleStructureError
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
