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

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.lexer.Token;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.lexer.Token.TokenIf;
import com.io7m.jparasol.lexer.Token.TokenLet;
import com.io7m.jparasol.lexer.Token.TokenLiteralBoolean;
import com.io7m.jparasol.lexer.Token.TokenLiteralInteger;
import com.io7m.jparasol.lexer.Token.TokenLiteralReal;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUDeclaration.UASTUDValueLocal;

public abstract class UASTUExpression implements UASTUExpressionVisitable
{
  public static final class UASTUEApplication extends UASTUExpression
  {
    private final @Nonnull List<UASTUExpression> arguments;
    private final @Nonnull UniqueName            name;

    public UASTUEApplication(
      final @Nonnull UniqueName in_name,
      final @Nonnull List<UASTUExpression> in_arguments)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(in_name, "Name");
      this.arguments =
        Constraints.constrainNotNull(in_arguments, "Arguments");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTUExpressionVisitor<A, L, E>>
      A
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.expressionVisitApplicationPre(this);
      final List<A> args = new ArrayList<A>();
      for (final UASTUExpression a : this.arguments) {
        final A x = a.expressionVisitableAccept(v);
        args.add(x);
      }
      return v.expressionVisitApplication(args, this);
    }

    public @Nonnull List<UASTUExpression> getArguments()
    {
      return this.arguments;
    }

    public @Nonnull UniqueName getName()
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

  public static final class UASTUEBoolean extends UASTUExpression
  {
    private final @Nonnull TokenLiteralBoolean token;

    public UASTUEBoolean(
      final @Nonnull Token.TokenLiteralBoolean in_token)
      throws ConstraintError
    {
      this.token = Constraints.constrainNotNull(in_token, "Token");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTUExpressionVisitor<A, L, E>>
      A
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.expressionVisitBoolean(this);
    }

    public @Nonnull TokenLiteralBoolean getToken()
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

  public static final class UASTUEConditional extends UASTUExpression
  {
    private final @Nonnull UASTUExpression condition;
    private final @Nonnull UASTUExpression left;
    private final @Nonnull UASTUExpression right;
    private final @Nonnull TokenIf         token;

    public UASTUEConditional(
      final @Nonnull TokenIf in_token,
      final @Nonnull UASTUExpression in_condition,
      final @Nonnull UASTUExpression in_left,
      final @Nonnull UASTUExpression in_right)
      throws ConstraintError
    {
      this.token = Constraints.constrainNotNull(in_token, "Token");
      this.condition =
        Constraints.constrainNotNull(in_condition, "Condition");
      this.left = Constraints.constrainNotNull(in_left, "Left");
      this.right = Constraints.constrainNotNull(in_right, "Right");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTUExpressionVisitor<A, L, E>>
      A
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.expressionVisitConditionalPre(this);
      final A c = this.condition.expressionVisitableAccept(v);
      final A l = this.left.expressionVisitableAccept(v);
      final A r = this.right.expressionVisitableAccept(v);
      return v.expressionVisitConditional(c, l, r, this);
    }

    public @Nonnull UASTUExpression getCondition()
    {
      return this.condition;
    }

    public TokenIf getIf()
    {
      return this.token;
    }

    public @Nonnull UASTUExpression getLeft()
    {
      return this.left;
    }

    public @Nonnull UASTUExpression getRight()
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

  public static final class UASTUEInteger extends UASTUExpression
  {
    private final @Nonnull TokenLiteralInteger token;

    public UASTUEInteger(
      final @Nonnull Token.TokenLiteralInteger in_token)
      throws ConstraintError
    {
      this.token = Constraints.constrainNotNull(in_token, "Token");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTUExpressionVisitor<A, L, E>>
      A
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.expressionVisitInteger(this);
    }

    public @Nonnull TokenLiteralInteger getToken()
    {
      return this.token;
    }

    public @Nonnull BigInteger getValue()
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

  public static final class UASTUELet extends UASTUExpression
  {
    private final @Nonnull List<UASTUDValueLocal> bindings;
    private final @Nonnull UASTUExpression        body;
    private final @Nonnull TokenLet               token;

    public UASTUELet(
      final @Nonnull TokenLet in_token,
      final @Nonnull List<UASTUDValueLocal> in_bindings,
      final @Nonnull UASTUExpression in_body)
      throws ConstraintError
    {
      this.token = Constraints.constrainNotNull(in_token, "Token");
      this.bindings = Constraints.constrainNotNull(in_bindings, "Bindings");
      this.body = Constraints.constrainNotNull(in_body, "Body");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTUExpressionVisitor<A, L, E>>
      A
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      final UASTULocalLevelVisitor<L, E> bv = v.expressionVisitLetPre(this);

      final ArrayList<L> r_bindings = new ArrayList<L>();
      for (final UASTUDValueLocal b : this.bindings) {
        final L rb = bv.localVisitValueLocal(b);
        r_bindings.add(rb);
      }

      final A x = this.body.expressionVisitableAccept(v);
      return v.expressionVisitLet(r_bindings, x, this);
    }

    public @Nonnull List<UASTUDValueLocal> getBindings()
    {
      return this.bindings;
    }

    public @Nonnull UASTUExpression getBody()
    {
      return this.body;
    }

    public @Nonnull TokenLet getToken()
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

  public static final class UASTUENew extends UASTUExpression
  {
    private final @Nonnull List<UASTUExpression> arguments;
    private final @Nonnull UASTUTypePath         name;

    public UASTUENew(
      final @Nonnull UASTUTypePath in_name,
      final @Nonnull List<UASTUExpression> in_arguments)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(in_name, "Name");
      this.arguments =
        Constraints.constrainNotNull(in_arguments, "Arguments");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTUExpressionVisitor<A, L, E>>
      A
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      final List<A> args = new ArrayList<A>();
      for (final UASTUExpression b : this.arguments) {
        final A x = b.expressionVisitableAccept(v);
        args.add(x);
      }
      return v.expressionVisitNew(args, this);
    }

    public @Nonnull List<UASTUExpression> getArguments()
    {
      return this.arguments;
    }

    public @Nonnull UASTUTypePath getName()
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

  public static final class UASTUEReal extends UASTUExpression
  {
    private final @Nonnull TokenLiteralReal token;

    public UASTUEReal(
      final @Nonnull Token.TokenLiteralReal in_token)
      throws ConstraintError
    {
      this.token = Constraints.constrainNotNull(in_token, "Token");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTUExpressionVisitor<A, L, E>>
      A
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.expressionVisitReal(this);
    }

    public @Nonnull TokenLiteralReal getToken()
    {
      return this.token;
    }

    public @Nonnull BigDecimal getValue()
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

  public static final class UASTUERecord extends UASTUExpression
  {
    private final @Nonnull List<UASTURecordFieldAssignment> assignments;
    private final @Nonnull UASTUTypePath                    type_path;

    public UASTUERecord(
      final @Nonnull UASTUTypePath in_type_path,
      final @Nonnull List<UASTURecordFieldAssignment> in_assignments)
      throws ConstraintError
    {
      this.type_path =
        Constraints.constrainNotNull(in_type_path, "Type path");
      this.assignments =
        Constraints.constrainNotNull(in_assignments, "Assignments");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTUExpressionVisitor<A, L, E>>
      A
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.expressionVisitRecord(this);
    }

    public @Nonnull List<UASTURecordFieldAssignment> getAssignments()
    {
      return this.assignments;
    }

    public @Nonnull UASTUTypePath getTypePath()
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

  public static final class UASTUERecordProjection extends UASTUExpression
  {
    private final @Nonnull UASTUExpression      expression;
    private final @Nonnull TokenIdentifierLower field;

    public UASTUERecordProjection(
      final @Nonnull UASTUExpression in_expression,
      final @Nonnull TokenIdentifierLower in_field)
      throws ConstraintError
    {
      this.expression =
        Constraints.constrainNotNull(in_expression, "Expression");
      this.field = Constraints.constrainNotNull(in_field, "Field");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTUExpressionVisitor<A, L, E>>
      A
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.expressionVisitRecordProjectionPre(this);
      final A x = this.expression.expressionVisitableAccept(v);
      return v.expressionVisitRecordProjection(x, this);
    }

    public @Nonnull UASTUExpression getExpression()
    {
      return this.expression;
    }

    public @Nonnull TokenIdentifierLower getField()
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

  public static final class UASTUESwizzle extends UASTUExpression
  {
    private final @Nonnull UASTUExpression            expression;
    private final @Nonnull List<TokenIdentifierLower> fields;

    public UASTUESwizzle(
      final @Nonnull UASTUExpression in_expression,
      final @Nonnull List<TokenIdentifierLower> in_fields)
      throws ConstraintError
    {
      this.expression =
        Constraints.constrainNotNull(in_expression, "Expression");
      this.fields = Constraints.constrainNotNull(in_fields, "Fields");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTUExpressionVisitor<A, L, E>>
      A
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.expressionVisitSwizzlePre(this);
      final A x = this.expression.expressionVisitableAccept(v);
      return v.expressionVisitSwizzle(x, this);
    }

    public @Nonnull UASTUExpression getExpression()
    {
      return this.expression;
    }

    public @Nonnull List<TokenIdentifierLower> getFields()
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

  public static final class UASTUEVariable extends UASTUExpression
  {
    private final @Nonnull UniqueName name;

    public UASTUEVariable(
      final @Nonnull UniqueName in_name)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(in_name, "Name");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTUExpressionVisitor<A, L, E>>
      A
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.expressionVisitVariable(this);
    }

    public @Nonnull UniqueName getName()
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

  public static final class UASTURecordFieldAssignment
  {
    private final @Nonnull UASTUExpression      expression;
    private final @Nonnull TokenIdentifierLower name;

    public UASTURecordFieldAssignment(
      final @Nonnull TokenIdentifierLower in_name,
      final @Nonnull UASTUExpression in_expression)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(in_name, "Name");
      this.expression =
        Constraints.constrainNotNull(in_expression, "Expression");
    }

    public @Nonnull UASTUExpression getExpression()
    {
      return this.expression;
    }

    public @Nonnull TokenIdentifierLower getName()
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
