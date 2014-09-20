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
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITWHETHER IN AN ACTION
 * OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN
 * CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.jparasol.untyped.ast.checked;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.lexer.Token;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.lexer.Token.TokenIf;
import com.io7m.jparasol.lexer.Token.TokenLet;
import com.io7m.jparasol.lexer.Token.TokenLiteralBoolean;
import com.io7m.jparasol.lexer.Token.TokenLiteralInteger;
import com.io7m.jparasol.lexer.Token.TokenLiteralReal;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDValueLocal;

// CHECKSTYLE_JAVADOC:OFF

@EqualityReference public abstract class UASTCExpression implements
  UASTCExpressionVisitableType
{
  @EqualityReference public static final class UASTCEApplication extends
    UASTCExpression
  {
    private final List<UASTCExpression> arguments;
    private final UASTCValuePath        name;

    public UASTCEApplication(
      final UASTCValuePath in_name,
      final List<UASTCExpression> in_arguments)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.arguments = NullCheck.notNull(in_arguments, "Arguments");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTCExpressionVisitorType<A, L, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      v.expressionVisitApplicationPre(this);
      final List<A> args = new ArrayList<A>();
      for (final UASTCExpression a : this.arguments) {
        final A x = a.expressionVisitableAccept(v);
        args.add(x);
      }
      return v.expressionVisitApplication(args, this);
    }

    public List<UASTCExpression> getArguments()
    {
      return this.arguments;
    }

    public UASTCValuePath getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTCEApplication ");
      builder.append(this.name.show());
      builder.append(" ");
      builder.append(this.arguments);
      builder.append("]");
      return builder.toString();
    }
  }

  @EqualityReference public static final class UASTCEBoolean extends
    UASTCExpression
  {
    private final TokenLiteralBoolean token;

    public UASTCEBoolean(
      final Token.TokenLiteralBoolean in_token)
    {
      this.token = NullCheck.notNull(in_token, "Token");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTCExpressionVisitorType<A, L, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      return v.expressionVisitBoolean(this);
    }

    public TokenLiteralBoolean getToken()
    {
      return this.token;
    }

    public boolean getValue()
    {
      return this.token.getValue();
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTCEBoolean ");
      builder.append(this.token.getValue());
      builder.append("]");
      return builder.toString();
    }
  }

  @EqualityReference public static final class UASTCEConditional extends
    UASTCExpression
  {
    private final UASTCExpression condition;
    private final UASTCExpression left;
    private final UASTCExpression right;
    private final TokenIf         token;

    public UASTCEConditional(
      final TokenIf in_token,
      final UASTCExpression in_condition,
      final UASTCExpression in_left,
      final UASTCExpression in_right)
    {
      this.token = NullCheck.notNull(in_token, "Token");
      this.condition = NullCheck.notNull(in_condition, "Condition");
      this.left = NullCheck.notNull(in_left, "Left");
      this.right = NullCheck.notNull(in_right, "Right");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTCExpressionVisitorType<A, L, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      v.expressionVisitConditionalPre(this);
      final A c = this.condition.expressionVisitableAccept(v);
      final A l = this.left.expressionVisitableAccept(v);
      final A r = this.right.expressionVisitableAccept(v);
      return v.expressionVisitConditional(c, l, r, this);
    }

    public UASTCExpression getCondition()
    {
      return this.condition;
    }

    public TokenIf getIf()
    {
      return this.token;
    }

    public UASTCExpression getLeft()
    {
      return this.left;
    }

    public UASTCExpression getRight()
    {
      return this.right;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTCEConditional ");
      builder.append(this.condition);
      builder.append(" ");
      builder.append(this.left);
      builder.append(" ");
      builder.append(this.right);
      builder.append("]");
      return builder.toString();
    }
  }

  @EqualityReference public static final class UASTCEInteger extends
    UASTCExpression
  {
    private final TokenLiteralInteger token;

    public UASTCEInteger(
      final Token.TokenLiteralInteger in_token)
    {
      this.token = NullCheck.notNull(in_token, "Token");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTCExpressionVisitorType<A, L, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      return v.expressionVisitInteger(this);
    }

    public TokenLiteralInteger getToken()
    {
      return this.token;
    }

    public BigInteger getValue()
    {
      return this.token.getValue();
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTCEInteger ");
      builder.append(this.token.getValue());
      builder.append("]");
      return builder.toString();
    }
  }

  @EqualityReference public static final class UASTCELet extends
    UASTCExpression
  {
    private final List<UASTCDValueLocal> bindings;
    private final UASTCExpression        body;
    private final TokenLet               token;

    public UASTCELet(
      final TokenLet in_token,
      final List<UASTCDValueLocal> in_bindings,
      final UASTCExpression in_body)
    {
      this.token = NullCheck.notNull(in_token, "Token");
      this.bindings = NullCheck.notNull(in_bindings, "Bindings");
      this.body = NullCheck.notNull(in_body, "Body");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTCExpressionVisitorType<A, L, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      final UASTCLocalLevelVisitorType<L, E> bv =
        v.expressionVisitLetPre(this);

      final List<L> r_bindings = new ArrayList<L>();
      for (final UASTCDValueLocal b : this.bindings) {
        final L rb = bv.localVisitValueLocal(b);
        r_bindings.add(rb);
      }

      final A x = this.body.expressionVisitableAccept(v);
      return v.expressionVisitLet(r_bindings, x, this);
    }

    public List<UASTCDValueLocal> getBindings()
    {
      return this.bindings;
    }

    public UASTCExpression getBody()
    {
      return this.body;
    }

    public TokenLet getToken()
    {
      return this.token;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTCELet [\n");
      for (final UASTCDValueLocal b : this.bindings) {
        builder.append("  ");
        builder.append(b);
        builder.append("\n");
      }
      builder.append("] [\n");
      builder.append(this.body);
      builder.append("\n]");
      return builder.toString();
    }
  }

  @EqualityReference public static final class UASTCEMatrixColumnAccess extends
    UASTCExpression
  {
    private final TokenLiteralInteger column;
    private final UASTCExpression         expression;

    public UASTCEMatrixColumnAccess(
      final UASTCExpression in_expression,
      final TokenLiteralInteger in_column)
    {
      this.expression = NullCheck.notNull(in_expression, "Expression");
      this.column = NullCheck.notNull(in_column, "Column");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTCExpressionVisitorType<A, L, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      v.expressionVisitMatrixColumnAccessPre(this);
      final A x = this.expression.expressionVisitableAccept(v);
      return v.expressionVisitMatrixColumnAccess(x, this);
    }

    public TokenLiteralInteger getColumn()
    {
      return this.column;
    }

    public UASTCExpression getExpression()
    {
      return this.expression;
    }
  }

  @EqualityReference public static final class UASTCENew extends
    UASTCExpression
  {
    private final List<UASTCExpression> arguments;
    private final UASTCTypePath         name;

    public UASTCENew(
      final UASTCTypePath in_name,
      final List<UASTCExpression> in_arguments)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.arguments = NullCheck.notNull(in_arguments, "Arguments");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTCExpressionVisitorType<A, L, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      final List<A> args = new ArrayList<A>();
      for (final UASTCExpression b : this.arguments) {
        final A x = b.expressionVisitableAccept(v);
        args.add(x);
      }
      return v.expressionVisitNew(args, this);
    }

    public List<UASTCExpression> getArguments()
    {
      return this.arguments;
    }

    public UASTCTypePath getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTCENew ");
      builder.append(this.name);
      builder.append(" ");
      builder.append(this.arguments);
      builder.append("]");
      return builder.toString();
    }
  }

  @EqualityReference public static final class UASTCEReal extends
    UASTCExpression
  {
    private final TokenLiteralReal token;

    public UASTCEReal(
      final Token.TokenLiteralReal in_token)
    {
      this.token = NullCheck.notNull(in_token, "Token");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTCExpressionVisitorType<A, L, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      return v.expressionVisitReal(this);
    }

    public TokenLiteralReal getToken()
    {
      return this.token;
    }

    public BigDecimal getValue()
    {
      return this.token.getValue();
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTCEReal ");
      builder.append(this.token.getValue());
      builder.append("]");
      return builder.toString();
    }
  }

  @EqualityReference public static final class UASTCERecord extends
    UASTCExpression
  {
    private final List<UASTCRecordFieldAssignment> assignments;
    private final UASTCTypePath                    type_path;

    public UASTCERecord(
      final UASTCTypePath in_type_path,
      final List<UASTCRecordFieldAssignment> in_assignments)
    {
      this.type_path = NullCheck.notNull(in_type_path, "Type path");
      this.assignments = NullCheck.notNull(in_assignments, "Assignments");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTCExpressionVisitorType<A, L, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      return v.expressionVisitRecord(this);
    }

    public List<UASTCRecordFieldAssignment> getAssignments()
    {
      return this.assignments;
    }

    public UASTCTypePath getTypePath()
    {
      return this.type_path;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTCERecord ");
      builder.append(this.type_path);
      builder.append(" [\n");
      for (final UASTCRecordFieldAssignment a : this.assignments) {
        builder.append("  ");
        builder.append(a);
        builder.append("\n");
      }
      builder.append("]");
      return builder.toString();
    }
  }

  @EqualityReference public static final class UASTCERecordProjection extends
    UASTCExpression
  {
    private final UASTCExpression      expression;
    private final TokenIdentifierLower field;

    public UASTCERecordProjection(
      final UASTCExpression in_expression,
      final TokenIdentifierLower in_field)
    {
      this.expression = NullCheck.notNull(in_expression, "Expression");
      this.field = NullCheck.notNull(in_field, "Field");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTCExpressionVisitorType<A, L, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      v.expressionVisitRecordProjectionPre(this);
      final A x = this.expression.expressionVisitableAccept(v);
      return v.expressionVisitRecordProjection(x, this);
    }

    public UASTCExpression getExpression()
    {
      return this.expression;
    }

    public TokenIdentifierLower getField()
    {
      return this.field;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTCERecordProjection ");
      builder.append(this.expression);
      builder.append(" ");
      builder.append(this.field.getActual());
      builder.append("]");
      return builder.toString();
    }
  }

  @EqualityReference public static final class UASTCESwizzle extends
    UASTCExpression
  {
    private final UASTCExpression            expression;
    private final List<TokenIdentifierLower> fields;

    public UASTCESwizzle(
      final UASTCExpression in_expression,
      final List<TokenIdentifierLower> in_fields)
    {
      this.expression = NullCheck.notNull(in_expression, "Expression");
      this.fields = NullCheck.notNull(in_fields, "Fields");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTCExpressionVisitorType<A, L, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      v.expressionVisitSwizzlePre(this);
      final A x = this.expression.expressionVisitableAccept(v);
      return v.expressionVisitSwizzle(x, this);
    }

    public UASTCExpression getExpression()
    {
      return this.expression;
    }

    public List<TokenIdentifierLower> getFields()
    {
      return this.fields;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTCESwizzle ");
      builder.append(this.expression);
      builder.append(" [");
      for (final TokenIdentifierLower f : this.fields) {
        builder.append(f.getActual());
        builder.append(" ");
      }
      builder.append("]]");
      return builder.toString();
    }
  }

  @EqualityReference public static final class UASTCEVariable extends
    UASTCExpression
  {
    private final UASTCValuePath name;

    public UASTCEVariable(
      final UASTCValuePath in_name)
    {
      this.name = NullCheck.notNull(in_name, "Name");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTCExpressionVisitorType<A, L, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      return v.expressionVisitVariable(this);
    }

    public UASTCValuePath getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTCEVariable ");
      builder.append(this.name);
      builder.append("]");
      return builder.toString();
    }
  }

  @EqualityReference public static final class UASTCRecordFieldAssignment
  {
    private final UASTCExpression      expression;
    private final TokenIdentifierLower name;

    public UASTCRecordFieldAssignment(
      final TokenIdentifierLower in_name,
      final UASTCExpression in_expression)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.expression = NullCheck.notNull(in_expression, "Expression");
    }

    public UASTCExpression getExpression()
    {
      return this.expression;
    }

    public TokenIdentifierLower getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTCRecordFieldAssignment ");
      builder.append(this.name.getActual());
      builder.append(" ");
      builder.append(this.expression);
      builder.append("]");
      return builder.toString();
    }
  }
}
