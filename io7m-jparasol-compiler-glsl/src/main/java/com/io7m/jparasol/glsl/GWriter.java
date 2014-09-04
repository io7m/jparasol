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

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jfunctional.OptionType;
import com.io7m.jfunctional.Pair;
import com.io7m.jfunctional.PartialFunctionType;
import com.io7m.jfunctional.Unit;
import com.io7m.jparasol.core.GVersionES;
import com.io7m.jparasol.core.GVersionFull;
import com.io7m.jparasol.core.GVersionType;
import com.io7m.jparasol.core.GVersionVisitorType;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEApplication;
import com.io7m.jparasol.glsl.ast.GASTExpression.GASTEApplicationExternal;
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
import com.io7m.jparasol.glsl.ast.GASTExpressionVisitorType;
import com.io7m.jparasol.glsl.ast.GASTFragmentShaderStatement;
import com.io7m.jparasol.glsl.ast.GASTFragmentShaderStatement.GASTFragmentConditionalDiscard;
import com.io7m.jparasol.glsl.ast.GASTFragmentShaderStatement.GASTFragmentLocalVariable;
import com.io7m.jparasol.glsl.ast.GASTFragmentShaderStatement.GASTFragmentOutputDataAssignment;
import com.io7m.jparasol.glsl.ast.GASTFragmentShaderStatement.GASTFragmentOutputDepthAssignment;
import com.io7m.jparasol.glsl.ast.GASTFragmentShaderStatementVisitorType;
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
import com.io7m.jparasol.glsl.ast.GASTStatementVisitorType;
import com.io7m.jparasol.glsl.ast.GASTTermDeclaration;
import com.io7m.jparasol.glsl.ast.GASTTermDeclaration.GASTTermFunction;
import com.io7m.jparasol.glsl.ast.GASTTermDeclaration.GASTTermValue;
import com.io7m.jparasol.glsl.ast.GASTTermDeclarationVisitorType;
import com.io7m.jparasol.glsl.ast.GASTTypeDeclaration;
import com.io7m.jparasol.glsl.ast.GASTTypeDeclaration.GASTTypeRecord;
import com.io7m.jparasol.glsl.ast.GASTTypeDeclarationVisitorType;
import com.io7m.jparasol.glsl.ast.GFieldName;
import com.io7m.jparasol.glsl.ast.GShaderInputName;
import com.io7m.jparasol.glsl.ast.GShaderOutputName;
import com.io7m.jparasol.glsl.ast.GShaderParameterName;
import com.io7m.jparasol.glsl.ast.GTermName.GTermNameExternal;
import com.io7m.jparasol.glsl.ast.GTermName.GTermNameGlobal;
import com.io7m.jparasol.glsl.ast.GTermName.GTermNameLocal;
import com.io7m.jparasol.glsl.ast.GTermNameVisitorType;
import com.io7m.jparasol.glsl.ast.GTypeName;
import com.io7m.jparasol.typed.TType;
import com.io7m.jparasol.typed.TType.TInteger;
import com.io7m.jparasol.typed.TType.TVectorIType;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * A GLSL shader serializer.
 */

@SuppressWarnings({ "boxing", "null", "synthetic-access" }) @EqualityReference public final class GWriter
{
  @EqualityReference private static final class ExpressionWriter implements
    GASTExpressionVisitorType<String, UnreachableCodeException>
  {
    public ExpressionWriter()
    {
      // Nothing
    }

    @Override public String expressionApplicationExternalVisit(
      final List<String> arguments,
      final GASTEApplicationExternal e)

    {
      final String arg_text = GWriter.formatCommaSeparatedList(arguments);
      return String.format("%s (%s)", e.getName().show(), arg_text);
    }

    @Override public void expressionApplicationExternalVisitPre(
      final GASTEApplicationExternal e)

    {
      // Nothing
    }

    @Override public String expressionApplicationVisit(
      final List<String> arguments,
      final GASTEApplication e)

    {
      final String arg_text = GWriter.formatCommaSeparatedList(arguments);
      return String.format("%s (%s)", e.getName().show(), arg_text);
    }

    @Override public void expressionApplicationVisitPre(
      final GASTEApplication e)

    {
      // Nothing
    }

    @Override public String expressionBinaryOpDivideVisit(
      final String left,
      final String right,
      final GASTEBinaryOpDivide e)

    {
      return String.format("(%s / %s)", left, right);
    }

    @Override public void expressionBinaryOpDivideVisitPre(
      final GASTEBinaryOpDivide e)

    {
      // Nothing
    }

    @Override public String expressionBinaryOpEqualVisit(
      final String left,
      final String right,
      final GASTEBinaryOpEqual e)

    {
      return String.format("(%s == %s)", left, right);
    }

    @Override public void expressionBinaryOpEqualVisitPre(
      final GASTEBinaryOpEqual e)

    {
      // Nothing
    }

    @Override public String expressionBinaryOpGreaterThanOrEqualVisit(
      final String left,
      final String right,
      final GASTEBinaryOpGreaterThanOrEqual e)

    {
      return String.format("(%s >= %s)", left, right);
    }

    @Override public void expressionBinaryOpGreaterThanOrEqualVisitPre(
      final GASTEBinaryOpGreaterThanOrEqual e)

    {
      // Nothing
    }

    @Override public String expressionBinaryOpGreaterThanVisit(
      final String left,
      final String right,
      final GASTEBinaryOpGreaterThan e)

    {
      return String.format("(%s > %s)", left, right);
    }

    @Override public void expressionBinaryOpGreaterThanVisitPre(
      final GASTEBinaryOpGreaterThan e)

    {
      // Nothing
    }

    @Override public String expressionBinaryOpLesserThanOrEqualVisit(
      final String left,
      final String right,
      final GASTEBinaryOpLesserThanOrEqual e)

    {
      return String.format("(%s <= %s)", left, right);
    }

    @Override public void expressionBinaryOpLesserThanOrEqualVisitPre(
      final GASTEBinaryOpLesserThanOrEqual e)

    {
      // Nothing
    }

    @Override public String expressionBinaryOpLesserThanVisit(
      final String left,
      final String right,
      final GASTEBinaryOpLesserThan e)

    {
      return String.format("(%s < %s)", left, right);
    }

    @Override public void expressionBinaryOpLesserThanVisitPre(
      final GASTEBinaryOpLesserThan e)

    {
      // Nothing
    }

    @Override public String expressionBinaryOpMultiplyVisit(
      final String left,
      final String right,
      final GASTEBinaryOpMultiply e)

    {
      return String.format("(%s * %s)", left, right);
    }

    @Override public void expressionBinaryOpMultiplyVisitPre(
      final GASTEBinaryOpMultiply e)

    {
      // Nothing
    }

    @Override public String expressionBinaryOpPlusVisit(
      final String left,
      final String right,
      final GASTEBinaryOpPlus e)

    {
      return String.format("(%s + %s)", left, right);
    }

    @Override public void expressionBinaryOpPlusVisitPre(
      final GASTEBinaryOpPlus e)

    {
      // Nothing
    }

    @Override public String expressionBinaryOpSubtractVisit(
      final String left,
      final String right,
      final GASTEBinaryOpSubtract e)

    {
      return String.format("(%s - %s)", left, right);
    }

    @Override public void expressionBinaryOpSubtractVisitPre(
      final GASTEBinaryOpSubtract e)

    {
      // Nothing
    }

    @Override public String expressionBooleanVisit(
      final GASTEBoolean e)

    {
      return Boolean.toString(e.getValue());
    }

    @Override public String expressionConstructionVisit(
      final List<String> arguments,
      final GASTEConstruction e)

    {
      final String arg_text = GWriter.formatCommaSeparatedList(arguments);
      return String.format("%s (%s)", e.getType().show(), arg_text);
    }

    @Override public void expressionConstructionVisitPre(
      final GASTEConstruction e)

    {
      // Nothing
    }

    @Override public String expressionFloatVisit(
      final GASTEFloat e)

    {
      return e.getValue().toPlainString();
    }

    @Override public String expressionIntegerVisit(
      final GASTEInteger e)

    {
      return e.getValue().toString();
    }

    @Override public String expressionProjectionVisit(
      final String body,
      final GASTEProjection e)

    {
      return String.format("%s.%s", body, e.getField().show());
    }

    @Override public void expressionProjectionVisitPre(
      final GASTEProjection e)

    {
      // Nothing
    }

    @Override public String expressionSwizzleVisit(
      final String body,
      final GASTESwizzle e)

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

    {
      // Nothing
    }

    @Override public String expressionUnaryOpNegateVisit(
      final String body,
      final GASTEUnaryOpNegate e)

    {
      return String.format("(-%s)", body);
    }

    @Override public void expressionUnaryOpNegateVisitPre(
      final GASTEUnaryOpNegate e)

    {
      // Nothing
    }

    @Override public String expressionVariableVisit(
      final GASTEVariable e)

    {
      return e.getTerm().show();
    }
  }

  @EqualityReference private static final class FragmentStatementWriter implements
    GASTFragmentShaderStatementVisitorType<Unit, UnreachableCodeException>
  {
    private final PrintWriter writer;

    public FragmentStatementWriter(
      final PrintWriter in_writer)
    {
      this.writer = in_writer;
    }

    @Override public Unit fragmentShaderConditionalDiscardVisit(
      final GASTFragmentConditionalDiscard s)

    {
      final String etext =
        s.getCondition().expressionVisitableAccept(new ExpressionWriter());
      this.writer.println(String.format("  if (%s) {", etext));
      this.writer.println("    discard;");
      this.writer.println("  }");
      return Unit.unit();
    }

    @Override public Unit fragmentShaderLocalVariableVisit(
      final GASTFragmentLocalVariable s)

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

  @EqualityReference private static final class StatementWriter implements
    GASTStatementVisitorType<Unit, UnreachableCodeException>
  {
    private static String makeIndentText(
      final int m)
    {
      final StringBuilder s = new StringBuilder();
      for (int i = 0; i < m; ++i) {
        s.append("  ");
      }
      return s.toString();
    }

    private int               indent;
    private String            indent_text;
    private final PrintWriter w;

    public StatementWriter(
      final PrintWriter in_w,
      final int in_indent)
    {
      this.w = in_w;
      this.indent = in_indent;
      this.indent_text = StatementWriter.makeIndentText(in_indent);
    }

    void indentDecrease()
    {
      assert this.indent > 0;
      --this.indent;
      this.indent_text = StatementWriter.makeIndentText(this.indent);
    }

    void indentIncrease()
    {
      ++this.indent;
      this.indent_text = StatementWriter.makeIndentText(this.indent);
    }

    @Override public Unit statementVisitConditional(
      final Unit left,
      final Unit right,
      final GASTConditional s)

    {
      return Unit.unit();
    }

    @Override public void statementVisitConditionalLeftPost(
      final GASTConditional s)

    {
      this.indentDecrease();
      this.w.println(String.format("%s} else {", this.indent_text));
    }

    @Override public void statementVisitConditionalLeftPre(
      final GASTConditional s)

    {
      this.indentIncrease();
    }

    @Override public void statementVisitConditionalPre(
      final GASTConditional s)

    {
      final String etext =
        s.getCondition().expressionVisitableAccept(new ExpressionWriter());
      this.w.println(String.format("%sif (%s) {", this.indent_text, etext));
    }

    @Override public void statementVisitConditionalRightPost(
      final GASTConditional s)

    {
      this.indentDecrease();
      this.w.println(String.format("%s}", this.indent_text));
    }

    @Override public void statementVisitConditionalRightPre(
      final GASTConditional s)

    {
      this.indentIncrease();
    }

    @Override public Unit statementVisitLocalVariable(
      final GASTLocalVariable s)

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

    {
      final String text =
        s.getExpression().expressionVisitableAccept(new ExpressionWriter());
      this.w.println(String.format("%sreturn %s;", this.indent_text, text));
      return Unit.unit();
    }

    @Override public Unit statementVisitScope(
      final List<Unit> statements,
      final GASTScope s)

    {
      this.indentDecrease();
      this.w.println(String.format("%s}", this.indent_text));
      return Unit.unit();
    }

    @Override public void statementVisitScopePre(
      final GASTScope s)

    {
      this.w.println(String.format("%s{", this.indent_text));
      this.indentIncrease();
    }
  }

  private static String formatCommaSeparatedList(
    final List<String> arguments)
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
    final PrintWriter writer,
    final GVersionType version,
    final GASTShaderFragmentInput i)

  {
    final GTypeName type_name = i.getTypeName();
    final GShaderInputName name = i.getName();

    version
      .versionAccept(new GVersionVisitorType<Unit, UnreachableCodeException>() {
        @Override public Unit versionVisitES(
          final GVersionES v)

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
          final GVersionFull v)

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
    final PrintWriter writer,
    final List<GASTShaderFragmentInput> inputs,
    final GVersionType version)

  {
    for (final GASTShaderFragmentInput i : inputs) {
      GWriter.writeFragmentInput(writer, version, i);
    }
  }

  private static void writeFragmentMain(
    final PrintWriter writer,
    final GASTShaderMainFragment main,
    final int outputs,
    final GVersionType version)

  {
    writer.println("void");
    writer.println("main (void)");
    writer.println("{");
    for (final GASTFragmentShaderStatement s : main.getStatements()) {
      s.fragmentStatementVisitableAccept(new FragmentStatementWriter(writer));
    }
    for (final GASTFragmentOutputDataAssignment w : main.getWrites()) {
      GWriter.writeFragmentOutputAssignment(writer, outputs, w, version);
    }

    GWriter.writeFragmentOutputDepth(writer, main.getDepthWrite());

    writer.println("}");
  }

  private static void writeFragmentOutput(
    final PrintWriter writer,
    final GVersionType version,
    final GASTShaderFragmentOutput o)
  {
    final GTypeName type = o.getType();
    final GShaderOutputName name = o.getName();

    version
      .versionAccept(new GVersionVisitorType<Unit, UnreachableCodeException>() {
        @Override public Unit versionVisitES(
          final GVersionES v)

        {
          if (v.compareTo(GVersionES.GLSL_ES_300) < 0) {
            // Nothing
          } else {
            writer.println(String.format(
              "layout(location = %d) out %s %s;",
              o.getIndex(),
              type.show(),
              name.show()));
          }
          return Unit.unit();
        }

        @Override public Unit versionVisitFull(
          final GVersionFull v)

        {
          if (v.compareTo(GVersionFull.GLSL_130) < 0) {
            // Nothing
          } else {
            if (v.compareTo(GVersionFull.GLSL_330) < 0) {
              writer.println(String.format(
                "out %s %s;",
                type.show(),
                name.show()));
            } else {
              writer.println(String.format(
                "layout(location = %d) out %s %s;",
                o.getIndex(),
                type.show(),
                name.show()));
            }
          }
          return Unit.unit();
        }
      });
  }

  private static void writeFragmentOutputAssignment(
    final PrintWriter writer,
    final int outputs,
    final GASTFragmentOutputDataAssignment w,
    final GVersionType version)

  {
    final String value =
      w.getValue().termNameVisitableAccept(
        new GTermNameVisitorType<String, UnreachableCodeException>() {
          @Override public String termNameVisitExternal(
            final GTermNameExternal n)

          {
            return n.show();
          }

          @Override public String termNameVisitGlobal(
            final GTermNameGlobal n)

          {
            return n.show();
          }

          @Override public String termNameVisitLocal(
            final GTermNameLocal n)

          {
            return n.show();
          }
        });

    final Integer index = Integer.valueOf(w.getIndex());
    final String name = w.getName().show();
    version
      .versionAccept(new GVersionVisitorType<Unit, UnreachableCodeException>() {
        @Override public Unit versionVisitES(
          final GVersionES v)

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
          final GVersionFull v)

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

  private static void writeFragmentOutputDepth(
    final PrintWriter writer,
    final OptionType<GASTFragmentOutputDepthAssignment> write)

  {
    write
      .mapPartial(new PartialFunctionType<GASTFragmentOutputDepthAssignment, Unit, UnreachableCodeException>() {
        @Override public Unit call(
          final GASTFragmentOutputDepthAssignment x)

        {
          writer.print("  gl_FragDepth = ");
          writer.print(x.getValue().show());
          writer.println(";");
          return Unit.unit();
        }
      });
  }

  private static void writeFragmentOutputs(
    final PrintWriter writer,
    final List<GASTShaderFragmentOutput> outputs,
    final GVersionType version)

  {
    for (final GASTShaderFragmentOutput o : outputs) {
      GWriter.writeFragmentOutput(writer, version, o);
    }
  }

  private static void writeFragmentParameter(
    final PrintWriter writer,
    final GASTShaderFragmentParameter p)
  {
    final GTypeName type = p.getType();
    final GShaderParameterName name = p.getName();
    writer.println(String.format("uniform %s %s;", type.show(), name.show()));
  }

  private static void writeFragmentParameters(
    final PrintWriter writer,
    final List<GASTShaderFragmentParameter> parameters)
  {
    for (final GASTShaderFragmentParameter p : parameters) {
      GWriter.writeFragmentParameter(writer, p);
    }
  }

  /**
   * Write the given GLSL shader to the given output stream.
   *
   * @param out
   *          The stream
   * @param f
   *          The shader
   * @param version_directive
   *          <code>true</code> if a version directive should be written
   */

  public static void writeFragmentShader(
    final OutputStream out,
    final GASTShaderFragment f,
    final boolean version_directive)
  {

    final PrintWriter writer = new PrintWriter(out);
    final List<Pair<GTypeName, GASTTypeDeclaration>> types = f.getTypes();
    final List<Pair<GTermNameGlobal, GASTTermDeclaration>> terms =
      f.getTerms();

    final GVersionType version = f.getGLSLVersion();
    if (version_directive) {
      GWriter.writeVersionDirective(writer, version);
    }
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

  private static void writeFunction(
    final PrintWriter w,
    final GASTTermFunction term)

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
        w.print(String.format("  in %s %s", p.getRight().show(), p
          .getLeft()
          .show()));
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

  private static void writePrecision(
    final PrintWriter writer,
    final GVersionType version)

  {
    version
      .versionAccept(new GVersionVisitorType<Unit, UnreachableCodeException>() {
        @Override public Unit versionVisitES(
          final GVersionES v)

        {
          writer.println("precision highp float;");
          writer.println("precision highp int;");
          writer.println();
          return Unit.unit();
        }

        @Override public Unit versionVisitFull(
          final GVersionFull v)

        {
          return Unit.unit();
        }
      });
  }

  private static void writeTerms(
    final PrintWriter writer,
    final List<Pair<GTermNameGlobal, GASTTermDeclaration>> terms)

  {
    for (final Pair<GTermNameGlobal, GASTTermDeclaration> t : terms) {
      t.getRight().termDeclarationVisitableAccept(
        new GASTTermDeclarationVisitorType<Unit, UnreachableCodeException>() {
          @Override public Unit termVisitFunction(
            final GASTTermFunction term)

          {
            GWriter.writeFunction(writer, term);
            return Unit.unit();
          }

          @Override public Unit termVisitValue(
            final GASTTermValue term)

          {
            GWriter.writeValue(writer, term);
            return Unit.unit();
          }
        });
    }
  }

  private static void writeTypeRecord(
    final PrintWriter writer,
    final GASTTypeRecord r)
  {
    writer.println("struct");
    writer.println(r.getName().show());
    writer.println("{");
    for (final Pair<GFieldName, GTypeName> f : r.getFields()) {
      writer.println(String.format("  %s %s;", f.getRight().show(), f
        .getLeft()
        .show()));
    }
    writer.println("};");
    writer.println();
  }

  private static void writeTypes(
    final PrintWriter writer,
    final List<Pair<GTypeName, GASTTypeDeclaration>> types)

  {
    for (final Pair<GTypeName, GASTTypeDeclaration> t : types) {
      t.getRight().typeDeclarationVisitableAccept(
        new GASTTypeDeclarationVisitorType<Unit, UnreachableCodeException>() {
          @Override public Unit typeVisitRecord(
            final GASTTypeRecord r)

          {
            GWriter.writeTypeRecord(writer, r);
            return Unit.unit();
          }
        });
    }
  }

  private static void writeValue(
    final PrintWriter writer,
    final GASTTermValue term)

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

  private static void writeVersionDirective(
    final PrintWriter writer,
    final GVersionType version)
  {
    writer.println(String.format("#version %d", version.versionGetNumber()));
    writer.println();
  }

  private static void writeVertexInput(
    final PrintWriter writer,
    final GVersionType version,
    final GASTShaderVertexInput i)

  {
    final GTypeName type = i.getType();
    final GShaderInputName name = i.getName();

    version
      .versionAccept(new GVersionVisitorType<Unit, UnreachableCodeException>() {
        @Override public Unit versionVisitES(
          final GVersionES v)

        {
          if (v.compareTo(GVersionES.GLSL_ES_300) < 0) {
            writer.println(String.format(
              "attribute %s %s;",
              type.show(),
              name.show()));
          } else {
            writer.println(String.format(
              "in %s %s;",
              type.show(),
              name.show()));
          }
          return Unit.unit();
        }

        @Override public Unit versionVisitFull(
          final GVersionFull v)

        {
          if (v.compareTo(GVersionFull.GLSL_130) < 0) {
            writer.println(String.format(
              "attribute %s %s;",
              type.show(),
              name.show()));
          } else {
            writer.println(String.format(
              "in %s %s;",
              type.show(),
              name.show()));
          }
          return Unit.unit();
        }
      });
  }

  private static void writeVertexInputs(
    final PrintWriter writer,
    final List<GASTShaderVertexInput> inputs,
    final GVersionType version)

  {
    for (final GASTShaderVertexInput i : inputs) {
      GWriter.writeVertexInput(writer, version, i);
    }
  }

  private static void writeVertexMain(
    final PrintWriter writer,
    final GASTShaderMainVertex main)

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
    final PrintWriter writer,
    final GVersionType version,
    final GASTShaderVertexOutput o)

  {
    final GTypeName type_name = o.getTypeName();
    final GShaderOutputName name = o.getName();

    version
      .versionAccept(new GVersionVisitorType<Unit, UnreachableCodeException>() {
        @Override public Unit versionVisitES(
          final GVersionES v)

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
          final GVersionFull v)

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
    final PrintWriter writer,
    final GASTVertexOutputAssignment w)

  {
    final String value =
      w.getValue().termNameVisitableAccept(
        new GTermNameVisitorType<String, UnreachableCodeException>() {
          @Override public String termNameVisitExternal(
            final GTermNameExternal n)

          {
            return n.show();
          }

          @Override public String termNameVisitGlobal(
            final GTermNameGlobal n)

          {
            return n.show();
          }

          @Override public String termNameVisitLocal(
            final GTermNameLocal n)

          {
            return n.show();
          }
        });

    final String name = w.getName().show();
    writer.println(String.format("  %s = %s;", name, value));
  }

  private static void writeVertexOutputs(
    final PrintWriter writer,
    final List<GASTShaderVertexOutput> outputs,
    final GVersionType version)

  {
    for (final GASTShaderVertexOutput o : outputs) {
      GWriter.writeVertexOutput(writer, version, o);
    }
  }

  private static void writeVertexParameter(
    final PrintWriter writer,
    final GASTShaderVertexParameter p)
  {
    final GTypeName type = p.getType();
    final GShaderParameterName name = p.getName();
    writer.println(String.format("uniform %s %s;", type.show(), name.show()));
  }

  private static void writeVertexParameters(
    final PrintWriter writer,
    final List<GASTShaderVertexParameter> parameters)
  {
    for (final GASTShaderVertexParameter p : parameters) {
      GWriter.writeVertexParameter(writer, p);
    }
  }

  /**
   * Write the given GLSL shader to the given output stream.
   *
   * @param out
   *          The stream
   * @param v
   *          The shader
   * @param version_directive
   *          <code>true</code> if a version directive should be written
   */

  public static void writeVertexShader(
    final OutputStream out,
    final GASTShaderVertex v,
    final boolean version_directive)
  {
    final PrintWriter writer = new PrintWriter(out);
    final List<Pair<GTypeName, GASTTypeDeclaration>> types = v.getTypes();
    final List<Pair<GTermNameGlobal, GASTTermDeclaration>> terms =
      v.getTerms();

    final GVersionType version = v.getGLSLVersion();
    if (version_directive) {
      GWriter.writeVersionDirective(writer, version);
    }
    GWriter.writePrecision(writer, version);
    GWriter.writeTypes(writer, types);
    GWriter.writeTerms(writer, terms);
    GWriter.writeVertexInputs(writer, v.getInputs(), version);
    GWriter.writeVertexParameters(writer, v.getParameters());
    GWriter.writeVertexOutputs(writer, v.getOutputs(), version);
    GWriter.writeVertexMain(writer, v.getMain());
    writer.flush();
  }

  private GWriter()
  {
    throw new UnreachableCodeException();
  }
}
