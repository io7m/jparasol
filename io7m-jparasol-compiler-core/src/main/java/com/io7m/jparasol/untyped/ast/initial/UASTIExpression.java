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

package com.io7m.jparasol.untyped.ast.initial;

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
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDValueLocal;

// CHECKSTYLE_JAVADOC:OFF

@EqualityReference public abstract class UASTIExpression implements
  UASTIExpressionVisitableType
{
  @EqualityReference public static final class UASTIEApplication extends
    UASTIExpression
  {
    private final List<UASTIExpression> arguments;
    private final UASTIValuePath        name;

    public UASTIEApplication(
      final UASTIValuePath in_name,
      final List<UASTIExpression> in_arguments)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.arguments = NullCheck.notNull(in_arguments, "Arguments");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTIExpressionVisitorType<A, L, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      v.expressionVisitApplicationPre(this);
      final List<A> args = new ArrayList<A>();
      for (final UASTIExpression a : this.arguments) {
        final A x = a.expressionVisitableAccept(v);
        args.add(x);
      }
      return v.expressionVisitApplication(args, this);
    }

    public List<UASTIExpression> getArguments()
    {
      return this.arguments;
    }

    public UASTIValuePath getName()
    {
      return this.name;
    }
  }

  @EqualityReference public static final class UASTIEBoolean extends
    UASTIExpression
  {
    private final TokenLiteralBoolean token;

    public UASTIEBoolean(
      final Token.TokenLiteralBoolean in_token)
    {
      this.token = NullCheck.notNull(in_token, "Token");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTIExpressionVisitorType<A, L, E>>
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
  }

  @EqualityReference public static final class UASTIEConditional extends
    UASTIExpression
  {
    private final UASTIExpression condition;
    private final UASTIExpression left;
    private final UASTIExpression right;
    private final TokenIf         token;

    public UASTIEConditional(
      final TokenIf in_token,
      final UASTIExpression in_condition,
      final UASTIExpression in_left,
      final UASTIExpression in_right)
    {
      this.token = NullCheck.notNull(in_token, "Token");
      this.condition = NullCheck.notNull(in_condition, "Condition");
      this.left = NullCheck.notNull(in_left, "Left");
      this.right = NullCheck.notNull(in_right, "Right");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTIExpressionVisitorType<A, L, E>>
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

    public UASTIExpression getCondition()
    {
      return this.condition;
    }

    public TokenIf getIf()
    {
      return this.token;
    }

    public UASTIExpression getLeft()
    {
      return this.left;
    }

    public UASTIExpression getRight()
    {
      return this.right;
    }
  }

  @EqualityReference public static final class UASTIEInteger extends
    UASTIExpression
  {
    private final TokenLiteralInteger token;

    public UASTIEInteger(
      final Token.TokenLiteralInteger in_token)
    {
      this.token = NullCheck.notNull(in_token, "Token");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTIExpressionVisitorType<A, L, E>>
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
  }

  @EqualityReference public static final class UASTIELet extends
    UASTIExpression
  {
    private final List<UASTIDValueLocal> bindings;
    private final UASTIExpression        body;
    private final TokenLet               token;

    public UASTIELet(
      final TokenLet in_token,
      final List<UASTIDValueLocal> in_bindings,
      final UASTIExpression in_body)
    {
      this.token = NullCheck.notNull(in_token, "Token");
      this.bindings = NullCheck.notNull(in_bindings, "Bindings");
      this.body = NullCheck.notNull(in_body, "Body");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTIExpressionVisitorType<A, L, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      final UASTILocalLevelVisitorType<L, E> bv =
        v.expressionVisitLetPre(this);

      final List<L> r_bindings = new ArrayList<L>();
      for (final UASTIDValueLocal b : this.bindings) {
        final L rb = bv.localVisitValueLocal(b);
        r_bindings.add(rb);
      }

      final A x = this.body.expressionVisitableAccept(v);
      return v.expressionVisitLet(r_bindings, x, this);
    }

    public List<UASTIDValueLocal> getBindings()
    {
      return this.bindings;
    }

    public UASTIExpression getBody()
    {
      return this.body;
    }

    public TokenLet getToken()
    {
      return this.token;
    }
  }

  @EqualityReference public static final class UASTIEMatrixColumnAccess extends
    UASTIExpression
  {
    private final TokenLiteralInteger column;
    private final UASTIExpression         expression;

    public UASTIEMatrixColumnAccess(
      final UASTIExpression in_expression,
      final TokenLiteralInteger in_column)
    {
      this.expression = NullCheck.notNull(in_expression, "Expression");
      this.column = NullCheck.notNull(in_column, "Column");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTIExpressionVisitorType<A, L, E>>
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

    public UASTIExpression getExpression()
    {
      return this.expression;
    }
  }

  @EqualityReference public static final class UASTIENew extends
    UASTIExpression
  {
    private final List<UASTIExpression> arguments;
    private final UASTITypePath         name;

    public UASTIENew(
      final UASTITypePath in_name,
      final List<UASTIExpression> in_arguments)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.arguments = NullCheck.notNull(in_arguments, "Arguments");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTIExpressionVisitorType<A, L, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      final List<A> args = new ArrayList<A>();
      for (final UASTIExpression b : this.arguments) {
        final A x = b.expressionVisitableAccept(v);
        args.add(x);
      }
      return v.expressionVisitNew(args, this);
    }

    public List<UASTIExpression> getArguments()
    {
      return this.arguments;
    }

    public UASTITypePath getName()
    {
      return this.name;
    }
  }

  @EqualityReference public static final class UASTIEReal extends
    UASTIExpression
  {
    private final TokenLiteralReal token;

    public UASTIEReal(
      final Token.TokenLiteralReal in_token)
    {
      this.token = NullCheck.notNull(in_token, "Token");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTIExpressionVisitorType<A, L, E>>
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
  }

  @EqualityReference public static final class UASTIERecord extends
    UASTIExpression
  {
    private final List<UASTIRecordFieldAssignment> assignments;
    private final UASTITypePath                    type_path;

    public UASTIERecord(
      final UASTITypePath in_type_path,
      final List<UASTIRecordFieldAssignment> in_assignments)
    {
      this.type_path = NullCheck.notNull(in_type_path, "Type path");
      this.assignments = NullCheck.notNull(in_assignments, "Assignments");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTIExpressionVisitorType<A, L, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      return v.expressionVisitRecord(this);
    }

    public List<UASTIRecordFieldAssignment> getAssignments()
    {
      return this.assignments;
    }

    public UASTITypePath getTypePath()
    {
      return this.type_path;
    }
  }

  @EqualityReference public static final class UASTIERecordProjection extends
    UASTIExpression
  {
    private final UASTIExpression      expression;
    private final TokenIdentifierLower field;

    public UASTIERecordProjection(
      final UASTIExpression in_expression,
      final TokenIdentifierLower in_field)
    {
      this.expression = NullCheck.notNull(in_expression, "Expression");
      this.field = NullCheck.notNull(in_field, "Field");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTIExpressionVisitorType<A, L, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      v.expressionVisitRecordProjectionPre(this);
      final A x = this.expression.expressionVisitableAccept(v);
      return v.expressionVisitRecordProjection(x, this);
    }

    public UASTIExpression getExpression()
    {
      return this.expression;
    }

    public TokenIdentifierLower getField()
    {
      return this.field;
    }
  }

  @EqualityReference public static final class UASTIESwizzle extends
    UASTIExpression
  {
    private final UASTIExpression            expression;
    private final List<TokenIdentifierLower> fields;

    public UASTIESwizzle(
      final UASTIExpression in_expression,
      final List<TokenIdentifierLower> in_fields)
    {
      this.expression = NullCheck.notNull(in_expression, "Expression");
      this.fields = NullCheck.notNull(in_fields, "Fields");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTIExpressionVisitorType<A, L, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      v.expressionVisitSwizzlePre(this);
      final A x = this.expression.expressionVisitableAccept(v);
      return v.expressionVisitSwizzle(x, this);
    }

    public UASTIExpression getExpression()
    {
      return this.expression;
    }

    public List<TokenIdentifierLower> getFields()
    {
      return this.fields;
    }
  }

  @EqualityReference public static final class UASTIEVariable extends
    UASTIExpression
  {
    private final UASTIValuePath name;

    public UASTIEVariable(
      final UASTIValuePath in_name)
    {
      this.name = NullCheck.notNull(in_name, "Name");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTIExpressionVisitorType<A, L, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      return v.expressionVisitVariable(this);
    }

    public UASTIValuePath getName()
    {
      return this.name;
    }
  }

  @EqualityReference public static final class UASTIRecordFieldAssignment
  {
    private final UASTIExpression      expression;
    private final TokenIdentifierLower name;

    public UASTIRecordFieldAssignment(
      final TokenIdentifierLower in_name,
      final UASTIExpression in_expression)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.expression = NullCheck.notNull(in_expression, "Expression");
    }

    public UASTIExpression getExpression()
    {
      return this.expression;
    }

    public TokenIdentifierLower getName()
    {
      return this.name;
    }
  }
}
