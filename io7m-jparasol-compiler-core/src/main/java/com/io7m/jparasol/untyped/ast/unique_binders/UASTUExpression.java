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
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.jparasol.untyped.ast.unique_binders;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.lexer.Token;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
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
    private final @Nonnull UASTUName             name;

    public UASTUEApplication(
      final @Nonnull UASTUName name,
      final @Nonnull List<UASTUExpression> arguments)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.arguments = Constraints.constrainNotNull(arguments, "Arguments");
    }

    @Override public
      <E extends Throwable, V extends UASTUExpressionVisitor<E>>
      void
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.expressionVisitApplication(this);
      for (final UASTUExpression a : this.arguments) {
        a.expressionVisitableAccept(v);
      }
    }

    public @Nonnull List<UASTUExpression> getArguments()
    {
      return this.arguments;
    }

    public @Nonnull UASTUName getName()
    {
      return this.name;
    }
  }

  public static final class UASTUEBoolean extends UASTUExpression
  {
    private final @Nonnull TokenLiteralBoolean token;

    public UASTUEBoolean(
      final @Nonnull Token.TokenLiteralBoolean token)
      throws ConstraintError
    {
      this.token = Constraints.constrainNotNull(token, "Token");
    }

    @Override public
      <E extends Throwable, V extends UASTUExpressionVisitor<E>>
      void
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.expressionVisitBoolean(this);
    }

    public @Nonnull TokenLiteralBoolean getToken()
    {
      return this.token;
    }

    public boolean getValue()
    {
      return this.token.getValue();
    }
  }

  public static final class UASTUEConditional extends UASTUExpression
  {
    private final @Nonnull UASTUExpression condition;
    private final @Nonnull UASTUExpression left;
    private final @Nonnull UASTUExpression right;

    public UASTUEConditional(
      final @Nonnull UASTUExpression condition,
      final @Nonnull UASTUExpression left,
      final @Nonnull UASTUExpression right)
      throws ConstraintError
    {
      this.condition = Constraints.constrainNotNull(condition, "Condition");
      this.left = Constraints.constrainNotNull(left, "Left");
      this.right = Constraints.constrainNotNull(right, "Right");
    }

    @Override public
      <E extends Throwable, V extends UASTUExpressionVisitor<E>>
      void
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.expressionVisitConditional(this);
      this.condition.expressionVisitableAccept(v);
      this.left.expressionVisitableAccept(v);
      this.right.expressionVisitableAccept(v);
    }

    public @Nonnull UASTUExpression getCondition()
    {
      return this.condition;
    }

    public @Nonnull UASTUExpression getLeft()
    {
      return this.left;
    }

    public @Nonnull UASTUExpression getRight()
    {
      return this.right;
    }
  }

  public static final class UASTUEInteger extends UASTUExpression
  {
    private final @Nonnull TokenLiteralInteger token;

    public UASTUEInteger(
      final @Nonnull Token.TokenLiteralInteger token)
      throws ConstraintError
    {
      this.token = Constraints.constrainNotNull(token, "Token");
    }

    @Override public
      <E extends Throwable, V extends UASTUExpressionVisitor<E>>
      void
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.expressionVisitInteger(this);
    }

    public @Nonnull TokenLiteralInteger getToken()
    {
      return this.token;
    }

    public @Nonnull BigDecimal getValue()
    {
      return this.token.getValue();
    }
  }

  public static final class UASTUELet extends UASTUExpression
  {
    private final @Nonnull List<UASTUDValueLocal> bindings;
    private final @Nonnull UASTUExpression        body;
    private final @Nonnull TokenLet               token;

    public UASTUELet(
      final @Nonnull TokenLet token,
      final @Nonnull List<UASTUDValueLocal> bindings,
      final @Nonnull UASTUExpression body)
      throws ConstraintError
    {
      this.token = Constraints.constrainNotNull(token, "Token");
      this.bindings = Constraints.constrainNotNull(bindings, "Bindings");
      this.body = Constraints.constrainNotNull(body, "Body");
    }

    @Override public
      <E extends Throwable, V extends UASTUExpressionVisitor<E>>
      void
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.expressionVisitLet(this);
      this.body.expressionVisitableAccept(v);
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
  }

  public static final class UASTUENew extends UASTUExpression
  {
    private final @Nonnull List<UASTUExpression> arguments;
    private final @Nonnull UASTUTypePath         name;

    public UASTUENew(
      final @Nonnull UASTUTypePath name,
      final @Nonnull List<UASTUExpression> arguments)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.arguments = Constraints.constrainNotNull(arguments, "Arguments");
    }

    @Override public
      <E extends Throwable, V extends UASTUExpressionVisitor<E>>
      void
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.expressionVisitNew(this);
      for (final UASTUExpression b : this.arguments) {
        b.expressionVisitableAccept(v);
      }
    }

    public @Nonnull List<UASTUExpression> getArguments()
    {
      return this.arguments;
    }

    public @Nonnull UASTUTypePath getName()
    {
      return this.name;
    }
  }

  public static final class UASTUEReal extends UASTUExpression
  {
    private final @Nonnull TokenLiteralReal token;

    public UASTUEReal(
      final @Nonnull Token.TokenLiteralReal token)
      throws ConstraintError
    {
      this.token = Constraints.constrainNotNull(token, "Token");
    }

    @Override public
      <E extends Throwable, V extends UASTUExpressionVisitor<E>>
      void
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.expressionVisitReal(this);
    }

    public @Nonnull TokenLiteralReal getToken()
    {
      return this.token;
    }

    public @Nonnull BigDecimal getValue()
    {
      return this.token.getValue();
    }
  }

  public static final class UASTUERecord extends UASTUExpression
  {
    private final @Nonnull List<UASTURecordFieldAssignment> assignments;
    private final @Nonnull UASTUTypePath                    type_path;

    public UASTUERecord(
      final @Nonnull UASTUTypePath type_path,
      final @Nonnull List<UASTURecordFieldAssignment> assignments)
      throws ConstraintError
    {
      this.type_path = Constraints.constrainNotNull(type_path, "Type path");
      this.assignments =
        Constraints.constrainNotNull(assignments, "Assignments");
    }

    @Override public
      <E extends Throwable, V extends UASTUExpressionVisitor<E>>
      void
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.expressionVisitRecord(this);
    }

    public @Nonnull List<UASTURecordFieldAssignment> getAssignments()
    {
      return this.assignments;
    }

    public @Nonnull UASTUTypePath getTypePath()
    {
      return this.type_path;
    }
  }

  public static final class UASTUERecordProjection extends UASTUExpression
  {
    private final @Nonnull UASTUExpression      expression;
    private final @Nonnull TokenIdentifierLower field;

    public UASTUERecordProjection(
      final @Nonnull UASTUExpression expression,
      final @Nonnull TokenIdentifierLower field)
      throws ConstraintError
    {
      this.expression =
        Constraints.constrainNotNull(expression, "Expression");
      this.field = Constraints.constrainNotNull(field, "Field");
    }

    @Override public
      <E extends Throwable, V extends UASTUExpressionVisitor<E>>
      void
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.expressionVisitRecordProjection(this);
      this.expression.expressionVisitableAccept(v);
    }

    public @Nonnull UASTUExpression getExpression()
    {
      return this.expression;
    }

    public @Nonnull TokenIdentifierLower getField()
    {
      return this.field;
    }
  }

  public static final class UASTUESwizzle extends UASTUExpression
  {
    private final @Nonnull UASTUExpression            expression;
    private final @Nonnull List<TokenIdentifierLower> fields;

    public UASTUESwizzle(
      final @Nonnull UASTUExpression expression,
      final @Nonnull List<TokenIdentifierLower> fields)
      throws ConstraintError
    {
      this.expression =
        Constraints.constrainNotNull(expression, "Expression");
      this.fields = Constraints.constrainNotNull(fields, "Fields");
    }

    @Override public
      <E extends Throwable, V extends UASTUExpressionVisitor<E>>
      void
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.expressionVisitSwizzle(this);
      this.expression.expressionVisitableAccept(v);
    }

    public @Nonnull UASTUExpression getExpression()
    {
      return this.expression;
    }

    public @Nonnull List<TokenIdentifierLower> getFields()
    {
      return this.fields;
    }
  }

  public static final class UASTUEVariable extends UASTUExpression
  {
    private final @Nonnull UASTUName name;

    public UASTUEVariable(
      final @Nonnull UASTUName name)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
    }

    @Override public
      <E extends Throwable, V extends UASTUExpressionVisitor<E>>
      void
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.expressionVisitVariable(this);
    }

    public @Nonnull UASTUName getName()
    {
      return this.name;
    }
  }

  public static final class UASTURecordFieldAssignment
  {
    private final @Nonnull UASTUExpression      expression;
    private final @Nonnull TokenIdentifierLower name;

    public UASTURecordFieldAssignment(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTUExpression expression)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.expression =
        Constraints.constrainNotNull(expression, "Expression");
    }

    public @Nonnull UASTUExpression getExpression()
    {
      return this.expression;
    }

    public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }
  }
}
