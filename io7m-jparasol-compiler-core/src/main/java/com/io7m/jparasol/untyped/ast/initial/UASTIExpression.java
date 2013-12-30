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

package com.io7m.jparasol.untyped.ast.initial;

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
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDValueLocal;

public abstract class UASTIExpression implements UASTIExpressionVisitable
{
  public static final class UASTIEApplication extends UASTIExpression
  {
    private final @Nonnull List<UASTIExpression> arguments;
    private final @Nonnull UASTIValuePath        name;

    public UASTIEApplication(
      final @Nonnull UASTIValuePath name,
      final @Nonnull List<UASTIExpression> arguments)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.arguments = Constraints.constrainNotNull(arguments, "Arguments");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTIExpressionVisitor<A, L, E>>
      A
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.expressionVisitApplicationPre(this);
      final List<A> args = new ArrayList<A>();
      for (final UASTIExpression a : this.arguments) {
        final A x = a.expressionVisitableAccept(v);
        args.add(x);
      }
      return v.expressionVisitApplication(args, this);
    }

    public @Nonnull List<UASTIExpression> getArguments()
    {
      return this.arguments;
    }

    public @Nonnull UASTIValuePath getName()
    {
      return this.name;
    }
  }

  public static final class UASTIEBoolean extends UASTIExpression
  {
    private final @Nonnull TokenLiteralBoolean token;

    public UASTIEBoolean(
      final @Nonnull Token.TokenLiteralBoolean token)
      throws ConstraintError
    {
      this.token = Constraints.constrainNotNull(token, "Token");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTIExpressionVisitor<A, L, E>>
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
  }

  public static final class UASTIEConditional extends UASTIExpression
  {
    private final @Nonnull UASTIExpression condition;
    private final @Nonnull UASTIExpression left;
    private final @Nonnull UASTIExpression right;
    private final @Nonnull TokenIf         token;

    public UASTIEConditional(
      final @Nonnull TokenIf token,
      final @Nonnull UASTIExpression condition,
      final @Nonnull UASTIExpression left,
      final @Nonnull UASTIExpression right)
      throws ConstraintError
    {
      this.token = Constraints.constrainNotNull(token, "Token");
      this.condition = Constraints.constrainNotNull(condition, "Condition");
      this.left = Constraints.constrainNotNull(left, "Left");
      this.right = Constraints.constrainNotNull(right, "Right");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTIExpressionVisitor<A, L, E>>
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

    public @Nonnull UASTIExpression getCondition()
    {
      return this.condition;
    }

    public @Nonnull TokenIf getIf()
    {
      return this.token;
    }

    public @Nonnull UASTIExpression getLeft()
    {
      return this.left;
    }

    public @Nonnull UASTIExpression getRight()
    {
      return this.right;
    }
  }

  public static final class UASTIEInteger extends UASTIExpression
  {
    private final @Nonnull TokenLiteralInteger token;

    public UASTIEInteger(
      final @Nonnull Token.TokenLiteralInteger token)
      throws ConstraintError
    {
      this.token = Constraints.constrainNotNull(token, "Token");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTIExpressionVisitor<A, L, E>>
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
  }

  public static final class UASTIELet extends UASTIExpression
  {
    private final @Nonnull List<UASTIDValueLocal> bindings;
    private final @Nonnull UASTIExpression        body;
    private final @Nonnull TokenLet               token;

    public UASTIELet(
      final @Nonnull TokenLet token,
      final @Nonnull List<UASTIDValueLocal> bindings,
      final @Nonnull UASTIExpression body)
      throws ConstraintError
    {
      this.token = Constraints.constrainNotNull(token, "Token");
      this.bindings = Constraints.constrainNotNull(bindings, "Bindings");
      this.body = Constraints.constrainNotNull(body, "Body");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTIExpressionVisitor<A, L, E>>
      A
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      final UASTILocalLevelVisitor<L, E> bv = v.expressionVisitLetPre(this);

      final ArrayList<L> r_bindings = new ArrayList<L>();
      for (final UASTIDValueLocal b : this.bindings) {
        final L rb = bv.localVisitValueLocal(b);
        r_bindings.add(rb);
      }

      final A x = this.body.expressionVisitableAccept(v);
      return v.expressionVisitLet(r_bindings, x, this);
    }

    public @Nonnull List<UASTIDValueLocal> getBindings()
    {
      return this.bindings;
    }

    public @Nonnull UASTIExpression getBody()
    {
      return this.body;
    }

    public @Nonnull TokenLet getToken()
    {
      return this.token;
    }
  }

  public static final class UASTIENew extends UASTIExpression
  {
    private final @Nonnull List<UASTIExpression> arguments;
    private final @Nonnull UASTITypePath         name;

    public UASTIENew(
      final @Nonnull UASTITypePath name,
      final @Nonnull List<UASTIExpression> arguments)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.arguments = Constraints.constrainNotNull(arguments, "Arguments");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTIExpressionVisitor<A, L, E>>
      A
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      final List<A> args = new ArrayList<A>();
      for (final UASTIExpression b : this.arguments) {
        final A x = b.expressionVisitableAccept(v);
        args.add(x);
      }
      return v.expressionVisitNew(args, this);
    }

    public @Nonnull List<UASTIExpression> getArguments()
    {
      return this.arguments;
    }

    public @Nonnull UASTITypePath getName()
    {
      return this.name;
    }
  }

  public static final class UASTIEReal extends UASTIExpression
  {
    private final @Nonnull TokenLiteralReal token;

    public UASTIEReal(
      final @Nonnull Token.TokenLiteralReal token)
      throws ConstraintError
    {
      this.token = Constraints.constrainNotNull(token, "Token");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTIExpressionVisitor<A, L, E>>
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
  }

  public static final class UASTIERecord extends UASTIExpression
  {
    private final @Nonnull List<UASTIRecordFieldAssignment> assignments;
    private final @Nonnull UASTITypePath                    type_path;

    public UASTIERecord(
      final @Nonnull UASTITypePath type_path,
      final @Nonnull List<UASTIRecordFieldAssignment> assignments)
      throws ConstraintError
    {
      this.type_path = Constraints.constrainNotNull(type_path, "Type path");
      this.assignments =
        Constraints.constrainNotNull(assignments, "Assignments");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTIExpressionVisitor<A, L, E>>
      A
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.expressionVisitRecord(this);
    }

    public @Nonnull List<UASTIRecordFieldAssignment> getAssignments()
    {
      return this.assignments;
    }

    public @Nonnull UASTITypePath getTypePath()
    {
      return this.type_path;
    }
  }

  public static final class UASTIERecordProjection extends UASTIExpression
  {
    private final @Nonnull UASTIExpression      expression;
    private final @Nonnull TokenIdentifierLower field;

    public UASTIERecordProjection(
      final @Nonnull UASTIExpression expression,
      final @Nonnull TokenIdentifierLower field)
      throws ConstraintError
    {
      this.expression =
        Constraints.constrainNotNull(expression, "Expression");
      this.field = Constraints.constrainNotNull(field, "Field");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTIExpressionVisitor<A, L, E>>
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

    public @Nonnull UASTIExpression getExpression()
    {
      return this.expression;
    }

    public @Nonnull TokenIdentifierLower getField()
    {
      return this.field;
    }
  }

  public static final class UASTIESwizzle extends UASTIExpression
  {
    private final @Nonnull UASTIExpression            expression;
    private final @Nonnull List<TokenIdentifierLower> fields;

    public UASTIESwizzle(
      final @Nonnull UASTIExpression expression,
      final @Nonnull List<TokenIdentifierLower> fields)
      throws ConstraintError
    {
      this.expression =
        Constraints.constrainNotNull(expression, "Expression");
      this.fields = Constraints.constrainNotNull(fields, "Fields");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTIExpressionVisitor<A, L, E>>
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

    public @Nonnull UASTIExpression getExpression()
    {
      return this.expression;
    }

    public @Nonnull List<TokenIdentifierLower> getFields()
    {
      return this.fields;
    }
  }

  public static final class UASTIEVariable extends UASTIExpression
  {
    private final @Nonnull UASTIValuePath name;

    public UASTIEVariable(
      final @Nonnull UASTIValuePath name)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTIExpressionVisitor<A, L, E>>
      A
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.expressionVisitVariable(this);
    }

    public @Nonnull UASTIValuePath getName()
    {
      return this.name;
    }
  }

  public static final class UASTIRecordFieldAssignment
  {
    private final @Nonnull UASTIExpression      expression;
    private final @Nonnull TokenIdentifierLower name;

    public UASTIRecordFieldAssignment(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTIExpression expression)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.expression =
        Constraints.constrainNotNull(expression, "Expression");
    }

    public @Nonnull UASTIExpression getExpression()
    {
      return this.expression;
    }

    public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }
  }
}
