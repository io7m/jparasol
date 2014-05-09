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

package com.io7m.jparasol.typed.ast;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.lexer.Token;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.lexer.Token.TokenLet;
import com.io7m.jparasol.lexer.Token.TokenLiteralBoolean;
import com.io7m.jparasol.lexer.Token.TokenLiteralIntegerType;
import com.io7m.jparasol.lexer.Token.TokenLiteralReal;
import com.io7m.jparasol.typed.TType;
import com.io7m.jparasol.typed.TType.TBoolean;
import com.io7m.jparasol.typed.TType.TFloat;
import com.io7m.jparasol.typed.TType.TInteger;
import com.io7m.jparasol.typed.TType.TRecord;
import com.io7m.jparasol.typed.TType.TValueType;
import com.io7m.jparasol.typed.TType.TVectorType;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDValueLocal;

/**
 * Typed expressions.
 */

// CHECKSTYLE_JAVADOC:OFF

@EqualityReference public abstract class TASTExpression
{
  /**
   * Accept a generic visitor.
   * 
   * @param v
   *          The visitor
   * @return A value of <code>A</code>
   * @throws E
   *           If <code>v</code> throws <code>E</code>
   */

  public abstract
    <A, L, E extends Throwable, V extends TASTExpressionVisitorType<A, L, E>>
    A
    expressionVisitableAccept(
      final V v)
      throws E;

  @EqualityReference public static final class TASTEApplication extends
    TASTExpression
  {
    private final List<TASTExpression> arguments;
    private final TASTTermName         name;
    private final TType                type;

    public TASTEApplication(
      final TASTTermName in_name,
      final List<TASTExpression> in_arguments,
      final TType in_type)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.arguments = NullCheck.notNull(in_arguments, "Arguments");
      this.type = NullCheck.notNull(in_type, "Type");
    }

    @Override public
      <A, L, E extends Throwable, V extends TASTExpressionVisitorType<A, L, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      final boolean traverse = v.expressionVisitApplicationPre(this);

      List<A> args = null;
      if (traverse) {
        args = new ArrayList<A>();
        for (final TASTExpression a : this.arguments) {
          final A x = a.expressionVisitableAccept(v);
          args.add(x);
        }
      }

      return v.expressionVisitApplication(args, this);
    }

    public List<TASTExpression> getArguments()
    {
      return this.arguments;
    }

    public TASTTermName getName()
    {
      return this.name;
    }

    @Override public TType getType()
    {
      return this.type;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTEApplication ");
      builder.append(this.name.show());
      builder.append(" ");
      builder.append(this.arguments);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TASTEBoolean extends
    TASTExpression
  {
    private final TokenLiteralBoolean token;

    public TASTEBoolean(
      final Token.TokenLiteralBoolean in_token)
    {
      this.token = NullCheck.notNull(in_token, "Token");
    }

    @Override public
      <A, L, E extends Throwable, V extends TASTExpressionVisitorType<A, L, E>>
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

    @Override public TType getType()
    {
      return TBoolean.get();
    }

    public boolean getValue()
    {
      return this.token.getValue();
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTEBoolean ");
      builder.append(this.getValue());
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TASTEConditional extends
    TASTExpression
  {
    private final TASTExpression condition;
    private final TASTExpression left;
    private final TASTExpression right;

    public TASTEConditional(
      final TASTExpression in_condition,
      final TASTExpression in_left,
      final TASTExpression in_right)
    {
      this.condition = NullCheck.notNull(in_condition, "Condition");
      this.left = NullCheck.notNull(in_left, "Left");
      this.right = NullCheck.notNull(in_right, "Right");

      assert (this.condition.getType().equals(TBoolean.get()));
      assert (this.left.getType().equals(this.right.getType()));
    }

    @Override public
      <A, L, E extends Throwable, V extends TASTExpressionVisitorType<A, L, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      final boolean traverse = v.expressionVisitConditionalPre(this);

      A c = null;
      A l = null;
      A r = null;

      if (traverse) {
        v.expressionVisitConditionalConditionPre(this);
        c = this.condition.expressionVisitableAccept(v);
        v.expressionVisitConditionalConditionPost(this);

        v.expressionVisitConditionalLeftPre(this);
        l = this.left.expressionVisitableAccept(v);
        v.expressionVisitConditionalLeftPost(this);

        v.expressionVisitConditionalRightPre(this);
        r = this.right.expressionVisitableAccept(v);
        v.expressionVisitConditionalRightPost(this);
      }

      return v.expressionVisitConditional(c, l, r, this);
    }

    public TASTExpression getCondition()
    {
      return this.condition;
    }

    public TASTExpression getLeft()
    {
      return this.left;
    }

    public TASTExpression getRight()
    {
      return this.right;
    }

    @Override public TType getType()
    {
      return this.left.getType();
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTEConditional ");
      builder.append(this.condition);
      builder.append(" ");
      builder.append(this.left);
      builder.append(" ");
      builder.append(this.right);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TASTEInteger extends
    TASTExpression
  {
    private final TokenLiteralIntegerType token;

    public TASTEInteger(
      final Token.TokenLiteralIntegerType in_token)
    {
      this.token = NullCheck.notNull(in_token, "Token");
    }

    @Override public
      <A, L, E extends Throwable, V extends TASTExpressionVisitorType<A, L, E>>
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

    @Override public TType getType()
    {
      return TInteger.get();
    }

    public BigInteger getValue()
    {
      return this.token.getValue();
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTEInteger ");
      builder.append(this.getValue());
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TASTELet extends
    TASTExpression
  {
    private final List<TASTDValueLocal> bindings;
    private final TASTExpression        body;
    private final TokenLet              token;

    public TASTELet(
      final TokenLet in_token,
      final List<TASTDValueLocal> in_bindings,
      final TASTExpression in_body)
    {
      this.token = NullCheck.notNull(in_token, "Token");
      this.bindings = NullCheck.notNull(in_bindings, "Bindings");
      this.body = NullCheck.notNull(in_body, "Body");
    }

    @Override public
      <A, L, E extends Throwable, V extends TASTExpressionVisitorType<A, L, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      final TASTLocalLevelVisitorType<L, E> bv =
        v.expressionVisitLetPre(this);

      List<L> r_bindings = null;
      A x = null;

      if (bv != null) {
        r_bindings = new ArrayList<L>();
        for (final TASTDValueLocal b : this.bindings) {
          final L rb = bv.localVisitValueLocal(b);
          r_bindings.add(rb);
        }
        x = this.body.expressionVisitableAccept(v);
      }

      return v.expressionVisitLet(r_bindings, x, this);
    }

    public List<TASTDValueLocal> getBindings()
    {
      return this.bindings;
    }

    public TASTExpression getBody()
    {
      return this.body;
    }

    public TokenLet getToken()
    {
      return this.token;
    }

    @Override public TType getType()
    {
      return this.body.getType();
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTELet ");
      builder.append(this.bindings);
      builder.append(" ");
      builder.append(this.body);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TASTENew extends
    TASTExpression
  {
    private final List<TASTExpression> arguments;
    private final TValueType           type;

    public TASTENew(
      final TValueType in_type,
      final List<TASTExpression> in_arguments)
    {
      this.type = NullCheck.notNull(in_type, "Type");
      this.arguments = NullCheck.notNull(in_arguments, "Arguments");
    }

    @Override public
      <A, L, E extends Throwable, V extends TASTExpressionVisitorType<A, L, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      final boolean traverse = v.expressionVisitNewPre(this);

      List<A> args = null;
      if (traverse) {
        args = new ArrayList<A>();
        for (final TASTExpression b : this.arguments) {
          final A x = b.expressionVisitableAccept(v);
          args.add(x);
        }
      }

      return v.expressionVisitNew(args, this);
    }

    public List<TASTExpression> getArguments()
    {
      return this.arguments;
    }

    @Override public TType getType()
    {
      return this.type;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTENew ");
      builder.append(this.type.getName());
      builder.append(" ");
      builder.append(this.arguments);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TASTEReal extends
    TASTExpression
  {
    private final TokenLiteralReal token;

    public TASTEReal(
      final Token.TokenLiteralReal in_token)
    {
      this.token = NullCheck.notNull(in_token, "Token");
    }

    @Override public
      <A, L, E extends Throwable, V extends TASTExpressionVisitorType<A, L, E>>
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

    @Override public TType getType()
    {
      return TFloat.get();
    }

    public BigDecimal getValue()
    {
      return this.token.getValue();
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTEReal ");
      builder.append(this.getValue());
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TASTERecord extends
    TASTExpression
  {
    private final List<TASTRecordFieldAssignment> assignments;
    private final TRecord                         type;

    public TASTERecord(
      final TRecord in_type,
      final List<TASTRecordFieldAssignment> in_assignments)
    {
      this.type = NullCheck.notNull(in_type, "Type path");
      this.assignments = NullCheck.notNull(in_assignments, "Assignments");
    }

    @Override public
      <A, L, E extends Throwable, V extends TASTExpressionVisitorType<A, L, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      return v.expressionVisitRecord(this);
    }

    public List<TASTRecordFieldAssignment> getAssignments()
    {
      return this.assignments;
    }

    @Override public TType getType()
    {
      return this.type;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTERecord ");
      builder.append(this.type.getName());
      builder.append(" ");
      builder.append(this.assignments);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TASTERecordProjection extends
    TASTExpression
  {
    private final TASTExpression       expression;
    private final TokenIdentifierLower field;
    private final TValueType           type;

    public TASTERecordProjection(
      final TValueType in_type,
      final TASTExpression in_expression,
      final TokenIdentifierLower in_field)
    {
      this.type = NullCheck.notNull(in_type, "Type");
      this.expression = NullCheck.notNull(in_expression, "Expression");
      this.field = NullCheck.notNull(in_field, "Field");
    }

    @Override public
      <A, L, E extends Throwable, V extends TASTExpressionVisitorType<A, L, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      final boolean traverse = v.expressionVisitRecordProjectionPre(this);
      A x = null;
      if (traverse) {
        x = this.expression.expressionVisitableAccept(v);
      }
      return v.expressionVisitRecordProjection(x, this);
    }

    public TASTExpression getExpression()
    {
      return this.expression;
    }

    public TokenIdentifierLower getField()
    {
      return this.field;
    }

    @Override public TType getType()
    {
      return this.type;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTERecordProjection ");
      builder.append(this.expression);
      builder.append(" ");
      builder.append(this.field);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TASTESwizzle extends
    TASTExpression
  {
    private final TASTExpression             expression;
    private final List<TokenIdentifierLower> fields;
    private final TValueType                 type;

    public TASTESwizzle(
      final TValueType in_type,
      final TASTExpression in_expression,
      final List<TokenIdentifierLower> in_fields)
    {
      this.type = NullCheck.notNull(in_type, "Type");
      this.expression = NullCheck.notNull(in_expression, "Expression");
      this.fields = NullCheck.notNull(in_fields, "Fields");
      assert (this.expression.getType() instanceof TVectorType);
    }

    @Override public
      <A, L, E extends Throwable, V extends TASTExpressionVisitorType<A, L, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      final boolean traverse = v.expressionVisitSwizzlePre(this);
      A x = null;
      if (traverse) {
        x = this.expression.expressionVisitableAccept(v);
      }
      return v.expressionVisitSwizzle(x, this);
    }

    public TASTExpression getExpression()
    {
      return this.expression;
    }

    public List<TokenIdentifierLower> getFields()
    {
      return this.fields;
    }

    @Override public TType getType()
    {
      return this.type;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTESwizzle ");
      builder.append(this.expression);
      for (final TokenIdentifierLower f : this.fields) {
        builder.append(" ");
        builder.append(f.getActual());
      }
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TASTEVariable extends
    TASTExpression
  {
    private final TASTTermName name;
    private final TType        type;

    public TASTEVariable(
      final TType in_type,
      final TASTTermName in_name)
    {
      this.type = NullCheck.notNull(in_type, "Type");
      this.name = NullCheck.notNull(in_name, "Name");
    }

    @Override public
      <A, L, E extends Throwable, V extends TASTExpressionVisitorType<A, L, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      return v.expressionVisitVariable(this);
    }

    public TASTTermName getName()
    {
      return this.name;
    }

    @Override public TType getType()
    {
      return this.type;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTEVariable ");
      builder.append(this.name.show());
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TASTRecordFieldAssignment
  {
    private final TASTExpression       expression;
    private final TokenIdentifierLower name;

    public TASTRecordFieldAssignment(
      final TokenIdentifierLower in_name,
      final TASTExpression in_expression)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.expression = NullCheck.notNull(in_expression, "Expression");
    }

    public TASTExpression getExpression()
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
      builder.append("[TASTRecordFieldAssignment ");
      builder.append(this.name.getActual());
      builder.append(" ");
      builder.append(this.expression);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  public abstract TType getType();
}
