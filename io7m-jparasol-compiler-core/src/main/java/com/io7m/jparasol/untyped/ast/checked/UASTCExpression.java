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
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITWHETHER IN AN ACTION
 * OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN
 * CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.jparasol.untyped.ast.checked;

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
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDValueLocal;

public abstract class UASTCExpression implements UASTCExpressionVisitable
{
  public static final class UASTCEApplication extends UASTCExpression
  {
    private final @Nonnull List<UASTCExpression> arguments;
    private final @Nonnull UASTCValuePath        name;

    public UASTCEApplication(
      final @Nonnull UASTCValuePath name,
      final @Nonnull List<UASTCExpression> arguments)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.arguments = Constraints.constrainNotNull(arguments, "Arguments");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTCExpressionVisitor<A, L, E>>
      A
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.expressionVisitApplicationPre(this);
      final List<A> args = new ArrayList<A>();
      for (final UASTCExpression a : this.arguments) {
        final A x = a.expressionVisitableAccept(v);
        args.add(x);
      }
      return v.expressionVisitApplication(args, this);
    }

    public @Nonnull List<UASTCExpression> getArguments()
    {
      return this.arguments;
    }

    public @Nonnull UASTCValuePath getName()
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

  public static final class UASTCEBoolean extends UASTCExpression
  {
    private final @Nonnull TokenLiteralBoolean token;

    public UASTCEBoolean(
      final @Nonnull Token.TokenLiteralBoolean token)
      throws ConstraintError
    {
      this.token = Constraints.constrainNotNull(token, "Token");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTCExpressionVisitor<A, L, E>>
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
      builder.append("[UASTCEBoolean ");
      builder.append(this.token.getValue());
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class UASTCEConditional extends UASTCExpression
  {
    private final @Nonnull UASTCExpression condition;
    private final @Nonnull UASTCExpression left;
    private final @Nonnull UASTCExpression right;
    private final @Nonnull TokenIf         token;

    public UASTCEConditional(
      final @Nonnull TokenIf token,
      final @Nonnull UASTCExpression condition,
      final @Nonnull UASTCExpression left,
      final @Nonnull UASTCExpression right)
      throws ConstraintError
    {
      this.token = Constraints.constrainNotNull(token, "Token");
      this.condition = Constraints.constrainNotNull(condition, "Condition");
      this.left = Constraints.constrainNotNull(left, "Left");
      this.right = Constraints.constrainNotNull(right, "Right");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTCExpressionVisitor<A, L, E>>
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

    public @Nonnull UASTCExpression getCondition()
    {
      return this.condition;
    }

    public @Nonnull UASTCExpression getLeft()
    {
      return this.left;
    }

    public @Nonnull UASTCExpression getRight()
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

    public @Nonnull TokenIf getIf()
    {
      return this.token;
    }
  }

  public static final class UASTCEInteger extends UASTCExpression
  {
    private final @Nonnull TokenLiteralInteger token;

    public UASTCEInteger(
      final @Nonnull Token.TokenLiteralInteger token)
      throws ConstraintError
    {
      this.token = Constraints.constrainNotNull(token, "Token");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTCExpressionVisitor<A, L, E>>
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
      builder.append("[UASTCEInteger ");
      builder.append(this.token.getValue());
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class UASTCELet extends UASTCExpression
  {
    private final @Nonnull List<UASTCDValueLocal> bindings;
    private final @Nonnull UASTCExpression        body;
    private final @Nonnull TokenLet               token;

    public UASTCELet(
      final @Nonnull TokenLet token,
      final @Nonnull List<UASTCDValueLocal> bindings,
      final @Nonnull UASTCExpression body)
      throws ConstraintError
    {
      this.token = Constraints.constrainNotNull(token, "Token");
      this.bindings = Constraints.constrainNotNull(bindings, "Bindings");
      this.body = Constraints.constrainNotNull(body, "Body");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTCExpressionVisitor<A, L, E>>
      A
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      final UASTCLocalLevelVisitor<L, E> bv = v.expressionVisitLetPre(this);

      final ArrayList<L> r_bindings = new ArrayList<L>();
      for (final UASTCDValueLocal b : this.bindings) {
        final L rb = bv.localVisitValueLocal(b);
        r_bindings.add(rb);
      }

      final A x = this.body.expressionVisitableAccept(v);
      return v.expressionVisitLet(r_bindings, x, this);
    }

    public @Nonnull List<UASTCDValueLocal> getBindings()
    {
      return this.bindings;
    }

    public @Nonnull UASTCExpression getBody()
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

  public static final class UASTCENew extends UASTCExpression
  {
    private final @Nonnull List<UASTCExpression> arguments;
    private final @Nonnull UASTCTypePath         name;

    public UASTCENew(
      final @Nonnull UASTCTypePath name,
      final @Nonnull List<UASTCExpression> arguments)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.arguments = Constraints.constrainNotNull(arguments, "Arguments");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTCExpressionVisitor<A, L, E>>
      A
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      final List<A> args = new ArrayList<A>();
      for (final UASTCExpression b : this.arguments) {
        final A x = b.expressionVisitableAccept(v);
        args.add(x);
      }
      return v.expressionVisitNew(args, this);
    }

    public @Nonnull List<UASTCExpression> getArguments()
    {
      return this.arguments;
    }

    public @Nonnull UASTCTypePath getName()
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

  public static final class UASTCEReal extends UASTCExpression
  {
    private final @Nonnull TokenLiteralReal token;

    public UASTCEReal(
      final @Nonnull Token.TokenLiteralReal token)
      throws ConstraintError
    {
      this.token = Constraints.constrainNotNull(token, "Token");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTCExpressionVisitor<A, L, E>>
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
      builder.append("[UASTCEReal ");
      builder.append(this.token.getValue());
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class UASTCERecord extends UASTCExpression
  {
    private final @Nonnull List<UASTCRecordFieldAssignment> assignments;
    private final @Nonnull UASTCTypePath                    type_path;

    public UASTCERecord(
      final @Nonnull UASTCTypePath type_path,
      final @Nonnull List<UASTCRecordFieldAssignment> assignments)
      throws ConstraintError
    {
      this.type_path = Constraints.constrainNotNull(type_path, "Type path");
      this.assignments =
        Constraints.constrainNotNull(assignments, "Assignments");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTCExpressionVisitor<A, L, E>>
      A
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.expressionVisitRecord(this);
    }

    public @Nonnull List<UASTCRecordFieldAssignment> getAssignments()
    {
      return this.assignments;
    }

    public @Nonnull UASTCTypePath getTypePath()
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

  public static final class UASTCERecordProjection extends UASTCExpression
  {
    private final @Nonnull UASTCExpression      expression;
    private final @Nonnull TokenIdentifierLower field;

    public UASTCERecordProjection(
      final @Nonnull UASTCExpression expression,
      final @Nonnull TokenIdentifierLower field)
      throws ConstraintError
    {
      this.expression =
        Constraints.constrainNotNull(expression, "Expression");
      this.field = Constraints.constrainNotNull(field, "Field");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTCExpressionVisitor<A, L, E>>
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

    public @Nonnull UASTCExpression getExpression()
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
      builder.append("[UASTCERecordProjection ");
      builder.append(this.expression);
      builder.append(" ");
      builder.append(this.field.getActual());
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class UASTCESwizzle extends UASTCExpression
  {
    private final @Nonnull UASTCExpression            expression;
    private final @Nonnull List<TokenIdentifierLower> fields;

    public UASTCESwizzle(
      final @Nonnull UASTCExpression expression,
      final @Nonnull List<TokenIdentifierLower> fields)
      throws ConstraintError
    {
      this.expression =
        Constraints.constrainNotNull(expression, "Expression");
      this.fields = Constraints.constrainNotNull(fields, "Fields");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTCExpressionVisitor<A, L, E>>
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

    public @Nonnull UASTCExpression getExpression()
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

  public static final class UASTCEVariable extends UASTCExpression
  {
    private final @Nonnull UASTCValuePath name;

    public UASTCEVariable(
      final @Nonnull UASTCValuePath name)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTCExpressionVisitor<A, L, E>>
      A
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.expressionVisitVariable(this);
    }

    public @Nonnull UASTCValuePath getName()
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

  public static final class UASTCRecordFieldAssignment
  {
    private final @Nonnull UASTCExpression      expression;
    private final @Nonnull TokenIdentifierLower name;

    public UASTCRecordFieldAssignment(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTCExpression expression)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.expression =
        Constraints.constrainNotNull(expression, "Expression");
    }

    public @Nonnull UASTCExpression getExpression()
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
      builder.append("[UASTCRecordFieldAssignment ");
      builder.append(this.name.getActual());
      builder.append(" ");
      builder.append(this.expression);
      builder.append("]");
      return builder.toString();
    }
  }
}
