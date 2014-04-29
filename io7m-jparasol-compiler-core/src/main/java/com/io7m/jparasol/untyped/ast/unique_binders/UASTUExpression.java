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

package com.io7m.jparasol.untyped.ast.unique_binders;

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
import com.io7m.jparasol.lexer.Token.TokenLiteralIntegerType;
import com.io7m.jparasol.lexer.Token.TokenLiteralReal;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDValueLocal;

// CHECKSTYLE_JAVADOC:OFF

@EqualityReference public abstract class UASTUExpression implements
  UASTUExpressionVisitableType
{
  @EqualityReference public static final class UASTUEApplication extends
    UASTUExpression
  {
    private final List<UASTUExpression> arguments;
    private final UniqueName            name;

    public UASTUEApplication(
      final UniqueName in_name,
      final List<UASTUExpression> in_arguments)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.arguments = NullCheck.notNull(in_arguments, "Arguments");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTUExpressionVisitorType<A, L, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      v.expressionVisitApplicationPre(this);
      final List<A> args = new ArrayList<A>();
      for (final UASTUExpression a : this.arguments) {
        final A x = a.expressionVisitableAccept(v);
        args.add(x);
      }
      return v.expressionVisitApplication(args, this);
    }

    public List<UASTUExpression> getArguments()
    {
      return this.arguments;
    }

    public UniqueName getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTUEApplication ");
      builder.append(this.name.show());
      builder.append(" ");
      builder.append(this.arguments);
      builder.append("]");
      return builder.toString();
    }
  }

  @EqualityReference public static final class UASTUEBoolean extends
    UASTUExpression
  {
    private final TokenLiteralBoolean token;

    public UASTUEBoolean(
      final Token.TokenLiteralBoolean in_token)
    {
      this.token = NullCheck.notNull(in_token, "Token");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTUExpressionVisitorType<A, L, E>>
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
      builder.append("[UASTUEBoolean ");
      builder.append(this.token.getValue());
      builder.append("]");
      return builder.toString();
    }
  }

  @EqualityReference public static final class UASTUEConditional extends
    UASTUExpression
  {
    private final UASTUExpression condition;
    private final UASTUExpression left;
    private final UASTUExpression right;
    private final TokenIf         token;

    public UASTUEConditional(
      final TokenIf in_token,
      final UASTUExpression in_condition,
      final UASTUExpression in_left,
      final UASTUExpression in_right)
    {
      this.token = NullCheck.notNull(in_token, "Token");
      this.condition = NullCheck.notNull(in_condition, "Condition");
      this.left = NullCheck.notNull(in_left, "Left");
      this.right = NullCheck.notNull(in_right, "Right");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTUExpressionVisitorType<A, L, E>>
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

    public UASTUExpression getCondition()
    {
      return this.condition;
    }

    public TokenIf getIf()
    {
      return this.token;
    }

    public UASTUExpression getLeft()
    {
      return this.left;
    }

    public UASTUExpression getRight()
    {
      return this.right;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTUEConditional ");
      builder.append(this.condition);
      builder.append(" ");
      builder.append(this.left);
      builder.append(" ");
      builder.append(this.right);
      builder.append("]");
      return builder.toString();
    }
  }

  @EqualityReference public static final class UASTUEInteger extends
    UASTUExpression
  {
    private final TokenLiteralIntegerType token;

    public UASTUEInteger(
      final Token.TokenLiteralIntegerType in_token)
    {
      this.token = NullCheck.notNull(in_token, "Token");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTUExpressionVisitorType<A, L, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      return v.expressionVisitInteger(this);
    }

    public TokenLiteralIntegerType getToken()
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
      builder.append("[UASTUEInteger ");
      builder.append(this.token.getValue());
      builder.append("]");
      return builder.toString();
    }
  }

  @EqualityReference public static final class UASTUELet extends
    UASTUExpression
  {
    private final List<UASTUDValueLocal> bindings;
    private final UASTUExpression        body;
    private final TokenLet               token;

    public UASTUELet(
      final TokenLet in_token,
      final List<UASTUDValueLocal> in_bindings,
      final UASTUExpression in_body)
    {
      this.token = NullCheck.notNull(in_token, "Token");
      this.bindings = NullCheck.notNull(in_bindings, "Bindings");
      this.body = NullCheck.notNull(in_body, "Body");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTUExpressionVisitorType<A, L, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      final UASTULocalLevelVisitorType<L, E> bv =
        v.expressionVisitLetPre(this);

      final List<L> r_bindings = new ArrayList<L>();
      for (final UASTUDValueLocal b : this.bindings) {
        final L rb = bv.localVisitValueLocal(b);
        r_bindings.add(rb);
      }

      final A x = this.body.expressionVisitableAccept(v);
      return v.expressionVisitLet(r_bindings, x, this);
    }

    public List<UASTUDValueLocal> getBindings()
    {
      return this.bindings;
    }

    public UASTUExpression getBody()
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
      builder.append("[UASTUELet [\n");
      for (final UASTUDValueLocal b : this.bindings) {
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

  @EqualityReference public static final class UASTUENew extends
    UASTUExpression
  {
    private final List<UASTUExpression> arguments;
    private final UASTUTypePath         name;

    public UASTUENew(
      final UASTUTypePath in_name,
      final List<UASTUExpression> in_arguments)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.arguments = NullCheck.notNull(in_arguments, "Arguments");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTUExpressionVisitorType<A, L, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      final List<A> args = new ArrayList<A>();
      for (final UASTUExpression b : this.arguments) {
        final A x = b.expressionVisitableAccept(v);
        args.add(x);
      }
      return v.expressionVisitNew(args, this);
    }

    public List<UASTUExpression> getArguments()
    {
      return this.arguments;
    }

    public UASTUTypePath getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTUENew ");
      builder.append(this.name);
      builder.append(" ");
      builder.append(this.arguments);
      builder.append("]");
      return builder.toString();
    }
  }

  @EqualityReference public static final class UASTUEReal extends
    UASTUExpression
  {
    private final TokenLiteralReal token;

    public UASTUEReal(
      final Token.TokenLiteralReal in_token)
    {
      this.token = NullCheck.notNull(in_token, "Token");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTUExpressionVisitorType<A, L, E>>
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
      builder.append("[UASTUEReal ");
      builder.append(this.token.getValue());
      builder.append("]");
      return builder.toString();
    }
  }

  @EqualityReference public static final class UASTUERecord extends
    UASTUExpression
  {
    private final List<UASTURecordFieldAssignment> assignments;
    private final UASTUTypePath                    type_path;

    public UASTUERecord(
      final UASTUTypePath in_type_path,
      final List<UASTURecordFieldAssignment> in_assignments)
    {
      this.type_path = NullCheck.notNull(in_type_path, "Type path");
      this.assignments = NullCheck.notNull(in_assignments, "Assignments");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTUExpressionVisitorType<A, L, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      return v.expressionVisitRecord(this);
    }

    public List<UASTURecordFieldAssignment> getAssignments()
    {
      return this.assignments;
    }

    public UASTUTypePath getTypePath()
    {
      return this.type_path;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTUERecord ");
      builder.append(this.type_path);
      builder.append(" [\n");
      for (final UASTURecordFieldAssignment a : this.assignments) {
        builder.append("  ");
        builder.append(a);
        builder.append("\n");
      }
      builder.append("]");
      return builder.toString();
    }
  }

  @EqualityReference public static final class UASTUERecordProjection extends
    UASTUExpression
  {
    private final UASTUExpression      expression;
    private final TokenIdentifierLower field;

    public UASTUERecordProjection(
      final UASTUExpression in_expression,
      final TokenIdentifierLower in_field)
    {
      this.expression = NullCheck.notNull(in_expression, "Expression");
      this.field = NullCheck.notNull(in_field, "Field");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTUExpressionVisitorType<A, L, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      v.expressionVisitRecordProjectionPre(this);
      final A x = this.expression.expressionVisitableAccept(v);
      return v.expressionVisitRecordProjection(x, this);
    }

    public UASTUExpression getExpression()
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
      builder.append("[UASTUERecordProjection ");
      builder.append(this.expression);
      builder.append(" ");
      builder.append(this.field.getActual());
      builder.append("]");
      return builder.toString();
    }
  }

  @EqualityReference public static final class UASTUESwizzle extends
    UASTUExpression
  {
    private final UASTUExpression            expression;
    private final List<TokenIdentifierLower> fields;

    public UASTUESwizzle(
      final UASTUExpression in_expression,
      final List<TokenIdentifierLower> in_fields)
    {
      this.expression = NullCheck.notNull(in_expression, "Expression");
      this.fields = NullCheck.notNull(in_fields, "Fields");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTUExpressionVisitorType<A, L, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      v.expressionVisitSwizzlePre(this);
      final A x = this.expression.expressionVisitableAccept(v);
      return v.expressionVisitSwizzle(x, this);
    }

    public UASTUExpression getExpression()
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
      builder.append("[UASTUESwizzle ");
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

  @EqualityReference public static final class UASTUEVariable extends
    UASTUExpression
  {
    private final UniqueName name;

    public UASTUEVariable(
      final UniqueName in_name)
    {
      this.name = NullCheck.notNull(in_name, "Name");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTUExpressionVisitorType<A, L, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      return v.expressionVisitVariable(this);
    }

    public UniqueName getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTUEVariable ");
      builder.append(this.name);
      builder.append("]");
      return builder.toString();
    }
  }

  @EqualityReference public static final class UASTURecordFieldAssignment
  {
    private final UASTUExpression      expression;
    private final TokenIdentifierLower name;

    public UASTURecordFieldAssignment(
      final TokenIdentifierLower in_name,
      final UASTUExpression in_expression)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.expression = NullCheck.notNull(in_expression, "Expression");
    }

    public UASTUExpression getExpression()
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
      builder.append("[UASTURecordFieldAssignment ");
      builder.append(this.name.getActual());
      builder.append(" ");
      builder.append(this.expression);
      builder.append("]");
      return builder.toString();
    }
  }
}
