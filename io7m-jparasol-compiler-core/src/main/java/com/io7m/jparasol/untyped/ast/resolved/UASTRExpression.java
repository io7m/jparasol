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

package com.io7m.jparasol.untyped.ast.resolved;

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
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDValueLocal;

// CHECKSTYLE_JAVADOC:OFF

@EqualityReference public abstract class UASTRExpression implements
  UASTRExpressionVisitableType
{
  @EqualityReference public static final class UASTREApplication extends
    UASTRExpression
  {
    private final List<UASTRExpression> arguments;
    private final UASTRTermName         name;

    public UASTREApplication(
      final UASTRTermName in_name,
      final List<UASTRExpression> in_arguments)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.arguments = NullCheck.notNull(in_arguments, "Arguments");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTRExpressionVisitorType<A, L, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      v.expressionVisitApplicationPre(this);
      final List<A> args = new ArrayList<A>();
      for (final UASTRExpression a : this.arguments) {
        final A x = a.expressionVisitableAccept(v);
        args.add(x);
      }
      return v.expressionVisitApplication(args, this);
    }

    public List<UASTRExpression> getArguments()
    {
      return this.arguments;
    }

    public UASTRTermName getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTREApplication ");
      builder.append(this.name.show());
      builder.append(" ");
      builder.append(this.arguments);
      builder.append("]");
      return builder.toString();
    }
  }

  @EqualityReference public static final class UASTREBoolean extends
    UASTRExpression
  {
    private final TokenLiteralBoolean token;

    public UASTREBoolean(
      final Token.TokenLiteralBoolean in_token)
    {
      this.token = NullCheck.notNull(in_token, "Token");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTRExpressionVisitorType<A, L, E>>
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
      builder.append("[UASTREBoolean ");
      builder.append(this.getValue());
      builder.append("]");
      return builder.toString();
    }
  }

  @EqualityReference public static final class UASTREConditional extends
    UASTRExpression
  {
    private final UASTRExpression condition;
    private final UASTRExpression left;
    private final UASTRExpression right;
    private final TokenIf         token;

    public UASTREConditional(
      final TokenIf in_token,
      final UASTRExpression in_condition,
      final UASTRExpression in_left,
      final UASTRExpression in_right)
    {
      this.token = NullCheck.notNull(in_token, "Token");
      this.condition = NullCheck.notNull(in_condition, "Condition");
      this.left = NullCheck.notNull(in_left, "Left");
      this.right = NullCheck.notNull(in_right, "Right");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTRExpressionVisitorType<A, L, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      v.expressionVisitConditionalConditionPre(this);
      final A c = this.condition.expressionVisitableAccept(v);
      v.expressionVisitConditionalConditionPost(this);

      v.expressionVisitConditionalLeftPre(this);
      final A l = this.left.expressionVisitableAccept(v);
      v.expressionVisitConditionalLeftPost(this);

      v.expressionVisitConditionalRightPre(this);
      final A r = this.right.expressionVisitableAccept(v);
      v.expressionVisitConditionalRightPost(this);

      return v.expressionVisitConditional(c, l, r, this);
    }

    public UASTRExpression getCondition()
    {
      return this.condition;
    }

    public TokenIf getIf()
    {
      return this.token;
    }

    public UASTRExpression getLeft()
    {
      return this.left;
    }

    public UASTRExpression getRight()
    {
      return this.right;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTREConditional ");
      builder.append(this.condition);
      builder.append(" ");
      builder.append(this.left);
      builder.append(" ");
      builder.append(this.right);
      builder.append("]");
      return builder.toString();
    }
  }

  @EqualityReference public static final class UASTREInteger extends
    UASTRExpression
  {
    private final TokenLiteralInteger token;

    public UASTREInteger(
      final Token.TokenLiteralInteger in_token)
    {
      this.token = NullCheck.notNull(in_token, "Token");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTRExpressionVisitorType<A, L, E>>
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
      builder.append("[UASTREInteger ");
      builder.append(this.getValue());
      builder.append("]");
      return builder.toString();
    }
  }

  @EqualityReference public static final class UASTRELet extends
    UASTRExpression
  {
    private final List<UASTRDValueLocal> bindings;
    private final UASTRExpression        body;
    private final TokenLet               token;

    public UASTRELet(
      final TokenLet in_token,
      final List<UASTRDValueLocal> in_bindings,
      final UASTRExpression in_body)
    {
      this.token = NullCheck.notNull(in_token, "Token");
      this.bindings = NullCheck.notNull(in_bindings, "Bindings");
      this.body = NullCheck.notNull(in_body, "Body");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTRExpressionVisitorType<A, L, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      final UASTRLocalLevelVisitorType<L, E> bv =
        v.expressionVisitLetPre(this);

      final List<L> r_bindings = new ArrayList<L>();
      for (final UASTRDValueLocal b : this.bindings) {
        final L rb = bv.localVisitValueLocal(b);
        r_bindings.add(rb);
      }

      final A x = this.body.expressionVisitableAccept(v);
      return v.expressionVisitLet(r_bindings, x, this);
    }

    public List<UASTRDValueLocal> getBindings()
    {
      return this.bindings;
    }

    public UASTRExpression getBody()
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
      builder.append("[UASTRELet ");
      builder.append(this.bindings);
      builder.append(" ");
      builder.append(this.body);
      builder.append("]");
      return builder.toString();
    }
  }

  @EqualityReference public static final class UASTREMatrixColumnAccess extends
    UASTRExpression
  {
    private final TokenLiteralInteger column;
    private final UASTRExpression         expression;

    public UASTREMatrixColumnAccess(
      final UASTRExpression in_expression,
      final TokenLiteralInteger in_column)
    {
      this.expression = NullCheck.notNull(in_expression, "Expression");
      this.column = NullCheck.notNull(in_column, "Column");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTRExpressionVisitorType<A, L, E>>
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

    public UASTRExpression getExpression()
    {
      return this.expression;
    }
  }

  @EqualityReference public static final class UASTRENew extends
    UASTRExpression
  {
    private final List<UASTRExpression> arguments;
    private final UASTRTypeName         name;

    public UASTRENew(
      final UASTRTypeName in_name,
      final List<UASTRExpression> in_arguments)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.arguments = NullCheck.notNull(in_arguments, "Arguments");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTRExpressionVisitorType<A, L, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      final List<A> args = new ArrayList<A>();
      for (final UASTRExpression b : this.arguments) {
        final A x = b.expressionVisitableAccept(v);
        args.add(x);
      }
      return v.expressionVisitNew(args, this);
    }

    public List<UASTRExpression> getArguments()
    {
      return this.arguments;
    }

    public UASTRTypeName getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTRENew ");
      builder.append(this.name.show());
      builder.append(" ");
      builder.append(this.arguments);
      builder.append("]");
      return builder.toString();
    }
  }

  @EqualityReference public static final class UASTREReal extends
    UASTRExpression
  {
    private final TokenLiteralReal token;

    public UASTREReal(
      final Token.TokenLiteralReal in_token)
    {
      this.token = NullCheck.notNull(in_token, "Token");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTRExpressionVisitorType<A, L, E>>
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
      builder.append("[UASTREReal ");
      builder.append(this.getValue());
      builder.append("]");
      return builder.toString();
    }
  }

  @EqualityReference public static final class UASTRERecord extends
    UASTRExpression
  {
    private final List<UASTRRecordFieldAssignment> assignments;
    private final UASTRTypeName                    type_path;

    public UASTRERecord(
      final UASTRTypeName in_type_path,
      final List<UASTRRecordFieldAssignment> in_assignments)
    {
      this.type_path = NullCheck.notNull(in_type_path, "Type path");
      this.assignments = NullCheck.notNull(in_assignments, "Assignments");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTRExpressionVisitorType<A, L, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      return v.expressionVisitRecord(this);
    }

    public List<UASTRRecordFieldAssignment> getAssignments()
    {
      return this.assignments;
    }

    public UASTRTypeName getTypePath()
    {
      return this.type_path;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTRERecord ");
      builder.append(this.type_path.show());
      builder.append(" ");
      builder.append(this.assignments);
      builder.append("]");
      return builder.toString();
    }
  }

  @EqualityReference public static final class UASTRERecordProjection extends
    UASTRExpression
  {
    private final UASTRExpression      expression;
    private final TokenIdentifierLower field;

    public UASTRERecordProjection(
      final UASTRExpression in_expression,
      final TokenIdentifierLower in_field)
    {
      this.expression = NullCheck.notNull(in_expression, "Expression");
      this.field = NullCheck.notNull(in_field, "Field");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTRExpressionVisitorType<A, L, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      v.expressionVisitRecordProjectionPre(this);
      final A x = this.expression.expressionVisitableAccept(v);
      return v.expressionVisitRecordProjection(x, this);
    }

    public UASTRExpression getExpression()
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
      builder.append("[UASTRERecordProjection ");
      builder.append(this.expression);
      builder.append(" ");
      builder.append(this.field);
      builder.append("]");
      return builder.toString();
    }
  }

  @EqualityReference public static final class UASTRESwizzle extends
    UASTRExpression
  {
    private final UASTRExpression            expression;
    private final List<TokenIdentifierLower> fields;

    public UASTRESwizzle(
      final UASTRExpression in_expression,
      final List<TokenIdentifierLower> in_fields)
    {
      this.expression = NullCheck.notNull(in_expression, "Expression");
      this.fields = NullCheck.notNull(in_fields, "Fields");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTRExpressionVisitorType<A, L, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      v.expressionVisitSwizzlePre(this);
      final A x = this.expression.expressionVisitableAccept(v);
      return v.expressionVisitSwizzle(x, this);
    }

    public UASTRExpression getExpression()
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
      builder.append("[UASTRESwizzle ");
      builder.append(this.expression);
      for (final TokenIdentifierLower f : this.fields) {
        builder.append(" ");
        builder.append(f.getActual());
      }
      builder.append("]");
      return builder.toString();
    }
  }

  @EqualityReference public static final class UASTREVariable extends
    UASTRExpression
  {
    private final UASTRTermName name;

    public UASTREVariable(
      final UASTRTermName in_name)
    {
      this.name = NullCheck.notNull(in_name, "Name");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTRExpressionVisitorType<A, L, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      return v.expressionVisitVariable(this);
    }

    public UASTRTermName getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTREVariable ");
      builder.append(this.name.show());
      builder.append("]");
      return builder.toString();
    }
  }

  @EqualityReference public static final class UASTRRecordFieldAssignment
  {
    private final UASTRExpression      expression;
    private final TokenIdentifierLower name;

    public UASTRRecordFieldAssignment(
      final TokenIdentifierLower in_name,
      final UASTRExpression in_expression)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.expression = NullCheck.notNull(in_expression, "Expression");
    }

    public UASTRExpression getExpression()
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
      builder.append("[UASTRRecordFieldAssignment ");
      builder.append(this.name.getActual());
      builder.append(" ");
      builder.append(this.expression);
      builder.append("]");
      return builder.toString();
    }
  }
}
