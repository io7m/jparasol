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

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Pair;
import com.io7m.jaux.functional.Unit;
import com.io7m.jparasol.glsl.GVersion.GVersionES;
import com.io7m.jparasol.glsl.GVersion.GVersionFull;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEApplication;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEBinaryOp.GASTEBinaryOpDivide;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEBinaryOp.GASTEBinaryOpEqual;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEBinaryOp.GASTEBinaryOpGreaterThan;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEBinaryOp.GASTEBinaryOpGreaterThanOrEqual;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEBinaryOp.GASTEBinaryOpLesserThan;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEBinaryOp.GASTEBinaryOpLesserThanOrEqual;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEBinaryOp.GASTEBinaryOpMultiply;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEBinaryOp.GASTEBinaryOpPlus;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEBinaryOp.GASTEBinaryOpSubtract;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEBoolean;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEConstruction;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEFloat;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEInteger;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEProjection;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTESwizzle;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEUnaryOp.GASTEUnaryOpNegate;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEVariable;
import com.io7m.jparasol.glsl.ast.GASTExpressionVisitor;
import com.io7m.jparasol.glsl.ast.GASTFragmentShaderStatement;
import com.io7m.jparasol.glsl.ast.GASTFragmentShaderStatement.GASTFragmentConditionalDiscard;
import com.io7m.jparasol.glsl.ast.GASTFragmentShaderStatement.GASTFragmentLocalVariable;
import com.io7m.jparasol.glsl.ast.GASTFragmentShaderStatement.GASTFragmentOutputAssignment;
import com.io7m.jparasol.glsl.ast.GASTFragmentShaderStatementVisitor;
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
import com.io7m.jparasol.glsl.ast.GASTStatementVisitor;
import com.io7m.jparasol.glsl.ast.GASTTermDeclaration;
import com.io7m.jparasol.glsl.ast.GASTTermDeclaration.GASTTermFunction;
import com.io7m.jparasol.glsl.ast.GASTTermDeclaration.GASTTermValue;
import com.io7m.jparasol.glsl.ast.GASTTermDeclarationVisitor;
import com.io7m.jparasol.glsl.ast.GASTTypeDeclaration;
import com.io7m.jparasol.glsl.ast.GASTTypeDeclaration.GASTTypeRecord;
import com.io7m.jparasol.glsl.ast.GASTTypeDeclarationVisitor;
import com.io7m.jparasol.glsl.ast.GFieldName;
import com.io7m.jparasol.glsl.ast.GShaderInputName;
import com.io7m.jparasol.glsl.ast.GShaderOutputName;
import com.io7m.jparasol.glsl.ast.GShaderParameterName;
import com.io7m.jparasol.glsl.ast.GTermName.GTermNameGlobal;
import com.io7m.jparasol.glsl.ast.GTermName.GTermNameLocal;
import com.io7m.jparasol.glsl.ast.GTermNameVisitor;
import com.io7m.jparasol.glsl.ast.GTypeName;
import com.io7m.jparasol.typed.TType;
import com.io7m.jparasol.typed.TType.TInteger;
import com.io7m.jparasol.typed.TType.TVectorIType;

public final class GWriter
{
  static final class ExpressionWriter implements
    GASTExpressionVisitor<String, ConstraintError>
  {
    @Override public String expressionApplicationVisit(
      final @Nonnull List<String> arguments,
      final @Nonnull GASTEApplication e)
      throws ConstraintError
    {
      final String arg_text = GWriter.formatCommaSeparatedList(arguments);
      return String.format("%s (%s)", e.getName().show(), arg_text);
    }

    @Override public void expressionApplicationVisitPre(
      final GASTEApplication e)
      throws ConstraintError
    {
      // Nothing
    }

    @Override public String expressionBinaryOpDivideVisit(
      final String left,
      final String right,
      final GASTEBinaryOpDivide e)
      throws ConstraintError
    {
      return String.format("(%s / %s)", left, right);
    }

    @Override public void expressionBinaryOpDivideVisitPre(
      final GASTEBinaryOpDivide e)
      throws ConstraintError
    {
      // Nothing
    }

    @Override public String expressionBinaryOpEqualVisit(
      final String left,
      final String right,
      final GASTEBinaryOpEqual e)
      throws ConstraintError
    {
      return String.format("(%s == %s)", left, right);
    }

    @Override public void expressionBinaryOpEqualVisitPre(
      final GASTEBinaryOpEqual e)
      throws ConstraintError
    {
      // Nothing
    }

    @Override public String expressionBinaryOpGreaterThanOrEqualVisit(
      final String left,
      final String right,
      final GASTEBinaryOpGreaterThanOrEqual e)
      throws ConstraintError
    {
      return String.format("(%s >= %s)", left, right);
    }

    @Override public void expressionBinaryOpGreaterThanOrEqualVisitPre(
      final GASTEBinaryOpGreaterThanOrEqual e)
      throws ConstraintError
    {
      // Nothing
    }

    @Override public String expressionBinaryOpGreaterThanVisit(
      final String left,
      final String right,
      final GASTEBinaryOpGreaterThan e)
      throws ConstraintError
    {
      return String.format("(%s > %s)", left, right);
    }

    @Override public void expressionBinaryOpGreaterThanVisitPre(
      final GASTEBinaryOpGreaterThan e)
      throws ConstraintError
    {
      // Nothing
    }

    @Override public String expressionBinaryOpLesserThanOrEqualVisit(
      final String left,
      final String right,
      final GASTEBinaryOpLesserThanOrEqual e)
      throws ConstraintError
    {
      return String.format("(%s <= %s)", left, right);
    }

    @Override public void expressionBinaryOpLesserThanOrEqualVisitPre(
      final GASTEBinaryOpLesserThanOrEqual e)
      throws ConstraintError
    {
      // Nothing
    }

    @Override public String expressionBinaryOpLesserThanVisit(
      final String left,
      final String right,
      final GASTEBinaryOpLesserThan e)
      throws ConstraintError
    {
      return String.format("(%s < %s)", left, right);
    }

    @Override public void expressionBinaryOpLesserThanVisitPre(
      final GASTEBinaryOpLesserThan e)
      throws ConstraintError
    {
      // Nothing
    }

    @Override public String expressionBinaryOpMultiplyVisit(
      final String left,
      final String right,
      final GASTEBinaryOpMultiply e)
      throws ConstraintError
    {
      return String.format("(%s * %s)", left, right);
    }

    @Override public void expressionBinaryOpMultiplyVisitPre(
      final GASTEBinaryOpMultiply e)
      throws ConstraintError
    {
      // Nothing
    }

    @Override public String expressionBinaryOpPlusVisit(
      final String left,
      final String right,
      final GASTEBinaryOpPlus e)
      throws ConstraintError
    {
      return String.format("(%s + %s)", left, right);
    }

    @Override public void expressionBinaryOpPlusVisitPre(
      final GASTEBinaryOpPlus e)
      throws ConstraintError
    {
      // Nothing
    }

    @Override public String expressionBinaryOpSubtractVisit(
      final @Nonnull String left,
      final @Nonnull String right,
      final @Nonnull GASTEBinaryOpSubtract e)
      throws ConstraintError
    {
      return String.format("(%s - %s)", left, right);
    }

    @Override public void expressionBinaryOpSubtractVisitPre(
      final GASTEBinaryOpSubtract e)
      throws ConstraintError
    {
      // Nothing
    }

    @Override public String expressionBooleanVisit(
      final @Nonnull GASTEBoolean e)
      throws ConstraintError
    {
      return Boolean.toString(e.getValue());
    }

    @Override public String expressionConstructionVisit(
      final @Nonnull List<String> arguments,
      final GASTEConstruction e)
      throws ConstraintError
    {
      final String arg_text = GWriter.formatCommaSeparatedList(arguments);
      return String.format("%s (%s)", e.getType().show(), arg_text);
    }

    @Override public void expressionConstructionVisitPre(
      final GASTEConstruction e)
      throws ConstraintError
    {
      // Nothing
    }

    @Override public String expressionFloatVisit(
      final GASTEFloat e)
      throws ConstraintError
    {
      return e.getValue().toPlainString();
    }

    @Override public String expressionIntegerVisit(
      final GASTEInteger e)
      throws ConstraintError
    {
      return e.getValue().toString();
    }

    @Override public String expressionProjectionVisit(
      final String body,
      final GASTEProjection e)
      throws ConstraintError
    {
      return String.format("%s.%s", body, e.getField().show());
    }

    @Override public void expressionProjectionVisitPre(
      final GASTEProjection e)
      throws ConstraintError
    {
      // Nothing
    }

    @Override public String expressionSwizzleVisit(
      final String body,
      final GASTESwizzle e)
      throws ConstraintError
    {
      final StringBuilder b = new StringBuilder();
      b.append(body);
      b.append(".");
      for (final GFieldName f : e.getFields()) {
        b.append(f.show());
      }
      return b.toString();
    }

    @Override public void expressionSwizzleVisitPre(
      final GASTESwizzle e)
      throws ConstraintError
    {
      // Nothing
    }

    @Override public String expressionUnaryOpNegateVisit(
      final String body,
      final GASTEUnaryOpNegate e)
      throws ConstraintError
    {
      return String.format("(-%s)", body);
    }

    @Override public void expressionUnaryOpNegateVisitPre(
      final GASTEUnaryOpNegate e)
      throws ConstraintError
    {
      // Nothing
    }

    @Override public String expressionVariableVisit(
      final GASTEVariable e)
      throws ConstraintError
    {
      return e.getTerm().show();
    }
  }

  static final class FragmentStatementWriter implements
    GASTFragmentShaderStatementVisitor<Unit, ConstraintError>
  {
    private final @Nonnull PrintWriter writer;

    public FragmentStatementWriter(
      final @Nonnull PrintWriter writer)
    {
      this.writer = writer;
    }

    @Override public Unit fragmentShaderConditionalDiscardVisit(
      final @Nonnull GASTFragmentConditionalDiscard s)
      throws ConstraintError
    {
      final String etext =
        s.getCondition().expressionVisitableAccept(new ExpressionWriter());
      this.writer.println(String.format("  if (%s) {", etext));
      this.writer.println("    discard;");
      this.writer.println("  }");
      return Unit.unit();
    }

    @Override public Unit fragmentShaderLocalVariableVisit(
      final @Nonnull GASTFragmentLocalVariable s)
      throws ConstraintError
    {
      final String etext =
        s.getExpression().expressionVisitableAccept(new ExpressionWriter());
      this.writer.println(String.format(
        "  %s %s = %s;",
        s.getType().show(),
        s.getName().show(),
        etext));
      return Unit.unit();
    }
  }

  static final class StatementWriter implements
    GASTStatementVisitor<Unit, ConstraintError>
  {
    private static @Nonnull String makeIndentText(
      final int m)
    {
      final StringBuilder s = new StringBuilder();
      for (int i = 0; i < m; ++i) {
        s.append("  ");
      }
      return s.toString();
    }

    private int                        indent;
    private String                     indent_text;
    private final @Nonnull PrintWriter w;

    public StatementWriter(
      final @Nonnull PrintWriter w,
      final int indent)
    {
      this.w = w;
      this.indent = indent;
      this.indent_text = StatementWriter.makeIndentText(indent);
    }

    void indentDecrease()
    {
      assert this.indent > 0;
      this.indent--;
      this.indent_text = StatementWriter.makeIndentText(this.indent);
    }

    void indentIncrease()
    {
      this.indent++;
      this.indent_text = StatementWriter.makeIndentText(this.indent);
    }

    @Override public Unit statementVisitConditional(
      final @Nonnull Unit left,
      final @Nonnull Unit right,
      final @Nonnull GASTConditional s)
      throws ConstraintError
    {
      return Unit.unit();
    }

    @Override public void statementVisitConditionalLeftPost(
      final @Nonnull GASTConditional s)
      throws ConstraintError
    {
      this.indentDecrease();
      this.w.println(String.format("%s} else {", this.indent_text));
    }

    @Override public void statementVisitConditionalLeftPre(
      final @Nonnull GASTConditional s)
      throws ConstraintError
    {
      this.indentIncrease();
    }

    @Override public void statementVisitConditionalPre(
      final @Nonnull GASTConditional s)
      throws ConstraintError
    {
      final String etext =
        s.getCondition().expressionVisitableAccept(new ExpressionWriter());
      this.w.println(String.format("%sif (%s) {", this.indent_text, etext));
    }

    @Override public void statementVisitConditionalRightPost(
      final @Nonnull GASTConditional s)
      throws ConstraintError
    {
      this.indentDecrease();
      this.w.println(String.format("%s}", this.indent_text));
    }

    @Override public void statementVisitConditionalRightPre(
      final @Nonnull GASTConditional s)
      throws ConstraintError
    {
      this.indentIncrease();
    }

    @Override public Unit statementVisitLocalVariable(
      final @Nonnull GASTLocalVariable s)
      throws ConstraintError
    {
      final String etext =
        s.getExpression().expressionVisitableAccept(new ExpressionWriter());
      this.w.println(String.format("%s%s %s = %s;", this.indent_text, s
        .getType()
        .show(), s.getName().show(), etext));

      return Unit.unit();
    }

    @Override public Unit statementVisitReturn(
      final GASTReturn s)
      throws ConstraintError
    {
      final String text =
        s.getExpression().expressionVisitableAccept(new ExpressionWriter());
      this.w.println(String.format("%sreturn %s;", this.indent_text, text));
      return Unit.unit();
    }

    @Override public Unit statementVisitScope(
      final @Nonnull List<Unit> statements,
      final @Nonnull GASTScope s)
      throws ConstraintError
    {
      this.indentDecrease();
      this.w.println(String.format("%s}", this.indent_text));
      return Unit.unit();
    }

    @Override public void statementVisitScopePre(
      final @Nonnull GASTScope s)
      throws ConstraintError
    {
      this.w.println(String.format("%s{", this.indent_text));
      this.indentIncrease();
    }
  }

  static @Nonnull String formatCommaSeparatedList(
    final @Nonnull List<String> arguments)
  {
    final StringBuilder arg_text = new StringBuilder();
    for (int index = 0; index < arguments.size(); ++index) {
      arg_text.append(arguments.get(index));
      if ((index + 1) < arguments.size()) {
        arg_text.append(", ");
      }
    }
    return arg_text.toString();
  }

  private static void writeFragmentInput(
    final @Nonnull PrintWriter writer,
    final @Nonnull GVersion version,
    final @Nonnull GASTShaderFragmentInput i)
    throws ConstraintError
  {
    final GTypeName type_name = i.getTypeName();
    final GShaderInputName name = i.getName();

    version.versionAccept(new GVersionVisitor<Unit, ConstraintError>() {
      @Override public Unit versionVisitES(
        final @Nonnull GVersionES v)
        throws ConstraintError
      {
        if (v.compareTo(GVersionES.GLSL_ES_300) < 0) {
          writer.println(String.format(
            "varying %s %s;",
            type_name.show(),
            name.show()));
        } else {
          final TType type = i.getType();
          if ((type instanceof TInteger) || (type instanceof TVectorIType)) {
            writer.println(String.format(
              "flat in %s %s;",
              type_name.show(),
              name.show()));
          } else {
            writer.println(String.format(
              "in %s %s;",
              type_name.show(),
              name.show()));
          }
        }
        return Unit.unit();
      }

      @Override public Unit versionVisitFull(
        final @Nonnull GVersionFull v)
        throws ConstraintError
      {
        if (v.compareTo(GVersionFull.GLSL_130) < 0) {
          writer.println(String.format(
            "varying %s %s;",
            type_name.show(),
            name.show()));
        } else {
          writer.println(String.format(
            "in %s %s;",
            type_name.show(),
            name.show()));
        }
        return Unit.unit();
      }
    });
  }

  private static void writeFragmentInputs(
    final @Nonnull PrintWriter writer,
    final @Nonnull List<GASTShaderFragmentInput> inputs,
    final @Nonnull GVersion version)
    throws ConstraintError
  {
    for (final GASTShaderFragmentInput i : inputs) {
      GWriter.writeFragmentInput(writer, version, i);
    }
  }

  private static void writeFragmentMain(
    final @Nonnull PrintWriter writer,
    final @Nonnull GASTShaderMainFragment main,
    final int outputs,
    final @Nonnull GVersion version)
    throws ConstraintError
  {
    writer.println("void");
    writer.println("main (void)");
    writer.println("{");
    for (final GASTFragmentShaderStatement s : main.getStatements()) {
      s.fragmentStatementVisitableAccept(new FragmentStatementWriter(writer));
    }
    for (final GASTFragmentOutputAssignment w : main.getWrites()) {
      GWriter.writeFragmentOutputAssignment(writer, outputs, w, version);
    }
    writer.println("}");
  }

  private static void writeFragmentOutput(
    final @Nonnull PrintWriter writer,
    final @Nonnull GVersion version,
    final @Nonnull GASTShaderFragmentOutput o)
    throws ConstraintError
  {
    final GTypeName type = o.getType();
    final GShaderOutputName name = o.getName();

    version.versionAccept(new GVersionVisitor<Unit, ConstraintError>() {
      @Override public Unit versionVisitES(
        final @Nonnull GVersionES v)
        throws ConstraintError
      {
        if (v.compareTo(GVersionES.GLSL_ES_300) < 0) {
          // Nothing
        } else {
          writer.println(String.format("out %s %s;", type.show(), name.show()));
        }
        return Unit.unit();
      }

      @Override public Unit versionVisitFull(
        final @Nonnull GVersionFull v)
        throws ConstraintError
      {
        if (v.compareTo(GVersionFull.GLSL_130) < 0) {
          // Nothing
        } else {
          writer.println(String.format("out %s %s;", type.show(), name.show()));
        }
        return Unit.unit();
      }
    });
  }

  private static void writeFragmentOutputAssignment(
    final @Nonnull PrintWriter writer,
    final int outputs,
    final @Nonnull GASTFragmentOutputAssignment w,
    final @Nonnull GVersion version)
    throws ConstraintError
  {
    final String value =
      w.getValue().termNameVisitableAccept(
        new GTermNameVisitor<String, ConstraintError>() {
          @Override public String termNameVisitGlobal(
            final GTermNameGlobal n)
            throws ConstraintError
          {
            return n.show();
          }

          @Override public String termNameVisitLocal(
            final GTermNameLocal n)
            throws ConstraintError
          {
            return n.show();
          }
        });

    final Integer index = Integer.valueOf(w.getIndex());
    final String name = w.getName().show();
    version.versionAccept(new GVersionVisitor<Unit, ConstraintError>() {
      @Override public Unit versionVisitES(
        final @Nonnull GVersionES v)
        throws ConstraintError
      {
        if (v.compareTo(GVersionES.GLSL_ES_300) < 0) {
          if (outputs > 1) {
            writer.println(String.format(
              "  gl_FragData[%d] = %s;",
              index,
              value));
          } else {
            writer.println(String.format("  gl_FragColor = %s;", value));
          }
        } else {
          writer.println(String.format("  %s = %s;", name, value));
        }
        return Unit.unit();
      }

      @Override public Unit versionVisitFull(
        final @Nonnull GVersionFull v)
        throws ConstraintError
      {
        if (v.compareTo(GVersionFull.GLSL_130) < 0) {
          if (outputs > 1) {
            writer.println(String.format(
              "  gl_FragData[%d] = %s;",
              index,
              value));
          } else {
            writer.println(String.format("  gl_FragColor = %s;", value));
          }
        } else {
          writer.println(String.format("  %s = %s;", name, value));
        }
        return Unit.unit();
      }
    });

  }

  private static void writeFragmentOutputs(
    final @Nonnull PrintWriter writer,
    final @Nonnull List<GASTShaderFragmentOutput> outputs,
    final @Nonnull GVersion version)
    throws ConstraintError
  {
    for (final GASTShaderFragmentOutput o : outputs) {
      GWriter.writeFragmentOutput(writer, version, o);
    }
  }

  private static void writeFragmentParameter(
    final @Nonnull PrintWriter writer,
    final @Nonnull GASTShaderFragmentParameter p)
  {
    final GTypeName type = p.getType();
    final GShaderParameterName name = p.getName();
    writer.println(String.format("uniform %s %s;", type.show(), name.show()));
  }

  private static void writeFragmentParameters(
    final @Nonnull PrintWriter writer,
    final @Nonnull List<GASTShaderFragmentParameter> parameters)
  {
    for (final GASTShaderFragmentParameter p : parameters) {
      GWriter.writeFragmentParameter(writer, p);
    }
  }

  public static void writeFragmentShader(
    final @Nonnull OutputStream out,
    final @Nonnull GASTShaderFragment f)
    throws ConstraintError
  {

    final PrintWriter writer = new PrintWriter(out);
    final List<Pair<GTypeName, GASTTypeDeclaration>> types = f.getTypes();
    final List<Pair<GTermNameGlobal, GASTTermDeclaration>> terms =
      f.getTerms();

    final GVersion version = f.getGLSLVersion();
    GWriter.writeVersionDirective(writer, version);
    GWriter.writePrecision(writer, version);
    GWriter.writeTypes(writer, types);
    GWriter.writeTerms(writer, terms);
    GWriter.writeFragmentInputs(writer, f.getInputs(), version);
    GWriter.writeFragmentParameters(writer, f.getParameters());
    GWriter.writeFragmentOutputs(writer, f.getOutputs(), version);
    writer.println();

    GWriter.writeFragmentMain(
      writer,
      f.getMain(),
      f.getOutputs().size(),
      version);
    writer.flush();
  }

  @SuppressWarnings("boxing") private static void writeVersionDirective(
    final PrintWriter writer,
    final GVersion version)
  {
    writer.println(String.format("#version %d", version.getNumber()));
    writer.println();
  }

  private static void writePrecision(
    final PrintWriter writer,
    final GVersion version)
    throws ConstraintError
  {
    version.versionAccept(new GVersionVisitor<Unit, ConstraintError>() {
      @Override public Unit versionVisitES(
        final GVersionES v)
        throws ConstraintError
      {
        writer.println("precision highp float;");
        writer.println("precision highp int;");
        writer.println();
        return Unit.unit();
      }

      @Override public Unit versionVisitFull(
        final GVersionFull v)
        throws ConstraintError
      {
        return Unit.unit();
      }
    });
  }

  static void writeFunction(
    final @Nonnull PrintWriter w,
    final @Nonnull GASTTermFunction term)
    throws ConstraintError
  {
    w.println(term.getReturns().show());
    w.print(term.getName().show());
    w.print(" ");

    final List<Pair<GTermNameLocal, GTypeName>> parameters =
      term.getParameters();
    final int max = parameters.size();
    if (max == 0) {
      w.println("(void)");
    } else {
      w.println("(");
      for (int index = 0; index < max; ++index) {
        final Pair<GTermNameLocal, GTypeName> p = parameters.get(index);
        w.print(String.format("  %s %s", p.second.show(), p.first.show()));
        if ((index + 1) < max) {
          w.print(", ");
        }
        w.println();
      }
      w.print(") ");
    }

    term.getStatement().statementVisitableAccept(new StatementWriter(w, 0));
    w.println();
  }

  private static void writeTerms(
    final @Nonnull PrintWriter writer,
    final @Nonnull List<Pair<GTermNameGlobal, GASTTermDeclaration>> terms)
    throws ConstraintError
  {
    for (final Pair<GTermNameGlobal, GASTTermDeclaration> t : terms) {
      t.second
        .termDeclarationVisitableAccept(new GASTTermDeclarationVisitor<Unit, ConstraintError>() {
          @Override public Unit termVisitFunction(
            final GASTTermFunction term)
            throws ConstraintError
          {
            GWriter.writeFunction(writer, term);
            return Unit.unit();
          }

          @Override public Unit termVisitValue(
            final GASTTermValue term)
            throws ConstraintError
          {
            GWriter.writeValue(writer, term);
            return Unit.unit();
          }
        });
    }
  }

  static void writeTypeRecord(
    final @Nonnull PrintWriter writer,
    final @Nonnull GASTTypeRecord r)
  {
    writer.println("struct");
    writer.println(r.getName().show());
    writer.println("{");
    for (final Pair<GFieldName, GTypeName> f : r.getFields()) {
      writer.println(String.format(
        "  %s %s;",
        f.second.show(),
        f.first.show()));
    }
    writer.println("};");
    writer.println();
  }

  static void writeTypes(
    final @Nonnull PrintWriter writer,
    final @Nonnull List<Pair<GTypeName, GASTTypeDeclaration>> types)
    throws ConstraintError
  {
    for (final Pair<GTypeName, GASTTypeDeclaration> t : types) {
      t.second
        .typeDeclarationVisitableAccept(new GASTTypeDeclarationVisitor<Unit, ConstraintError>() {
          @Override public Unit typeVisitRecord(
            final @Nonnull GASTTypeRecord r)
            throws ConstraintError
          {
            GWriter.writeTypeRecord(writer, r);
            return Unit.unit();
          }
        });
    }
  }

  static void writeValue(
    final @Nonnull PrintWriter writer,
    final @Nonnull GASTTermValue term)
    throws ConstraintError
  {
    final String type_name = term.getType().show();
    final String term_name = term.getName().show();
    final String etext =
      term.getExpression().expressionVisitableAccept(new ExpressionWriter());

    writer.println(String.format("%s", type_name));
    writer.println(String.format("%s =", term_name));
    writer.println(String.format("  %s;", etext));
    writer.println();
  }

  private static void writeVertexInput(
    final @Nonnull PrintWriter writer,
    final @Nonnull GVersion version,
    final @Nonnull GASTShaderVertexInput i)
    throws ConstraintError
  {
    final GTypeName type = i.getType();
    final GShaderInputName name = i.getName();

    version.versionAccept(new GVersionVisitor<Unit, ConstraintError>() {
      @Override public Unit versionVisitES(
        final @Nonnull GVersionES v)
        throws ConstraintError
      {
        if (v.compareTo(GVersionES.GLSL_ES_300) < 0) {
          writer.println(String.format(
            "attribute %s %s;",
            type.show(),
            name.show()));
        } else {
          writer.println(String.format("in %s %s;", type.show(), name.show()));
        }
        return Unit.unit();
      }

      @Override public Unit versionVisitFull(
        final @Nonnull GVersionFull v)
        throws ConstraintError
      {
        if (v.compareTo(GVersionFull.GLSL_130) < 0) {
          writer.println(String.format(
            "attribute %s %s;",
            type.show(),
            name.show()));
        } else {
          writer.println(String.format("in %s %s;", type.show(), name.show()));
        }
        return Unit.unit();
      }
    });
  }

  private static void writeVertexInputs(
    final @Nonnull PrintWriter writer,
    final @Nonnull List<GASTShaderVertexInput> inputs,
    final @Nonnull GVersion version)
    throws ConstraintError
  {
    for (final GASTShaderVertexInput i : inputs) {
      GWriter.writeVertexInput(writer, version, i);
    }
  }

  private static void writeVertexMain(
    final @Nonnull PrintWriter writer,
    final @Nonnull GASTShaderMainVertex main)
    throws ConstraintError
  {
    writer.println();
    writer.println("void");
    writer.println("main (void)");
    writer.println("{");
    for (final GASTStatement s : main.getStatements()) {
      s.statementVisitableAccept(new StatementWriter(writer, 1));
    }
    for (final GASTVertexOutputAssignment w : main.getWrites()) {
      GWriter.writeVertexOutputAssignment(writer, w);
    }
    writer.println("}");
  }

  private static void writeVertexOutput(
    final @Nonnull PrintWriter writer,
    final @Nonnull GVersion version,
    final @Nonnull GASTShaderVertexOutput o)
    throws ConstraintError
  {
    final GTypeName type_name = o.getTypeName();
    final GShaderOutputName name = o.getName();

    version.versionAccept(new GVersionVisitor<Unit, ConstraintError>() {
      @Override public Unit versionVisitES(
        final @Nonnull GVersionES v)
        throws ConstraintError
      {
        if (v.compareTo(GVersionES.GLSL_ES_300) < 0) {
          writer.println(String.format(
            "varying %s %s;",
            type_name.show(),
            name.show()));
        } else {
          final TType type = o.getType();
          if ((type instanceof TInteger) || (type instanceof TVectorIType)) {
            writer.println(String.format(
              "flat out %s %s;",
              type_name.show(),
              name.show()));
          } else {
            writer.println(String.format(
              "out %s %s;",
              type_name.show(),
              name.show()));
          }
        }
        return Unit.unit();
      }

      @Override public Unit versionVisitFull(
        final @Nonnull GVersionFull v)
        throws ConstraintError
      {
        final TType type = o.getType();
        if (v.compareTo(GVersionFull.GLSL_130) < 0) {
          if ((type instanceof TInteger) || (type instanceof TVectorIType)) {
            writer.println(String.format(
              "flat %s %s;",
              type_name.show(),
              name.show()));
          } else {
            writer.println(String.format(
              "varying %s %s;",
              type_name.show(),
              name.show()));
          }
        } else {
          if ((type instanceof TInteger) || (type instanceof TVectorIType)) {
            writer.println(String.format(
              "flat out %s %s;",
              type_name.show(),
              name.show()));
          } else {
            writer.println(String.format(
              "out %s %s;",
              type_name.show(),
              name.show()));
          }
        }
        return Unit.unit();
      }
    });
  }

  private static void writeVertexOutputAssignment(
    final @Nonnull PrintWriter writer,
    final @Nonnull GASTVertexOutputAssignment w)
    throws ConstraintError
  {
    final String value =
      w.getValue().termNameVisitableAccept(
        new GTermNameVisitor<String, ConstraintError>() {
          @Override public String termNameVisitGlobal(
            final GTermNameGlobal n)
            throws ConstraintError
          {
            return n.show();
          }

          @Override public String termNameVisitLocal(
            final GTermNameLocal n)
            throws ConstraintError
          {
            return n.show();
          }
        });

    final String name = w.getName().show();
    writer.println(String.format("  %s = %s;", name, value));
  }

  private static void writeVertexOutputs(
    final @Nonnull PrintWriter writer,
    final @Nonnull List<GASTShaderVertexOutput> outputs,
    final @Nonnull GVersion version)
    throws ConstraintError
  {
    for (final GASTShaderVertexOutput o : outputs) {
      GWriter.writeVertexOutput(writer, version, o);
    }
  }

  private static void writeVertexParameter(
    final @Nonnull PrintWriter writer,
    final @Nonnull GASTShaderVertexParameter p)
  {
    final GTypeName type = p.getType();
    final GShaderParameterName name = p.getName();
    writer.println(String.format("uniform %s %s;", type.show(), name.show()));
  }

  private static void writeVertexParameters(
    final @Nonnull PrintWriter writer,
    final @Nonnull List<GASTShaderVertexParameter> parameters)
  {
    for (final GASTShaderVertexParameter p : parameters) {
      GWriter.writeVertexParameter(writer, p);
    }
  }

  public static void writeVertexShader(
    final @Nonnull OutputStream out,
    final @Nonnull GASTShaderVertex v)
    throws ConstraintError
  {
    final PrintWriter writer = new PrintWriter(out);
    final List<Pair<GTypeName, GASTTypeDeclaration>> types = v.getTypes();
    final List<Pair<GTermNameGlobal, GASTTermDeclaration>> terms =
      v.getTerms();

    final GVersion version = v.getGLSLVersion();
    GWriter.writeVersionDirective(writer, version);
    GWriter.writePrecision(writer, version);
    GWriter.writeTypes(writer, types);
    GWriter.writeTerms(writer, terms);
    GWriter.writeVertexInputs(writer, v.getInputs(), version);
    GWriter.writeVertexParameters(writer, v.getParameters());
    GWriter.writeVertexOutputs(writer, v.getOutputs(), version);
    GWriter.writeVertexMain(writer, v.getMain());
    writer.flush();
  }
}
