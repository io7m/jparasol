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
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDValueLocal;

public abstract class UASTRExpression implements UASTRExpressionVisitable
{
  public static final class UASTREApplication extends UASTRExpression
  {
    private final @Nonnull List<UASTRExpression> arguments;
    private final @Nonnull UASTRTermName         name;

    public UASTREApplication(
      final @Nonnull UASTRTermName name,
      final @Nonnull List<UASTRExpression> arguments)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.arguments = Constraints.constrainNotNull(arguments, "Arguments");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTRExpressionVisitor<A, L, E>>
      A
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.expressionVisitApplicationPre(this);
      final List<A> args = new ArrayList<A>();
      for (final UASTRExpression a : this.arguments) {
        final A x = a.expressionVisitableAccept(v);
        args.add(x);
      }
      return v.expressionVisitApplication(args, this);
    }

    public @Nonnull List<UASTRExpression> getArguments()
    {
      return this.arguments;
    }

    public @Nonnull UASTRTermName getName()
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

  public static final class UASTREBoolean extends UASTRExpression
  {
    private final @Nonnull TokenLiteralBoolean token;

    public UASTREBoolean(
      final @Nonnull Token.TokenLiteralBoolean token)
      throws ConstraintError
    {
      this.token = Constraints.constrainNotNull(token, "Token");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTRExpressionVisitor<A, L, E>>
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
      builder.append("[UASTREBoolean ");
      builder.append(this.getValue());
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class UASTREConditional extends UASTRExpression
  {
    private final @Nonnull UASTRExpression condition;
    private final @Nonnull UASTRExpression left;
    private final @Nonnull UASTRExpression right;
    private final @Nonnull TokenIf         token;

    public UASTREConditional(
      final @Nonnull TokenIf token,
      final @Nonnull UASTRExpression condition,
      final @Nonnull UASTRExpression left,
      final @Nonnull UASTRExpression right)
      throws ConstraintError
    {
      this.token = Constraints.constrainNotNull(token, "Token");
      this.condition = Constraints.constrainNotNull(condition, "Condition");
      this.left = Constraints.constrainNotNull(left, "Left");
      this.right = Constraints.constrainNotNull(right, "Right");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTRExpressionVisitor<A, L, E>>
      A
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
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

    public @Nonnull UASTRExpression getCondition()
    {
      return this.condition;
    }

    public @Nonnull TokenIf getIf()
    {
      return this.token;
    }

    public @Nonnull UASTRExpression getLeft()
    {
      return this.left;
    }

    public @Nonnull UASTRExpression getRight()
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

  public static final class UASTREInteger extends UASTRExpression
  {
    private final @Nonnull TokenLiteralInteger token;

    public UASTREInteger(
      final @Nonnull Token.TokenLiteralInteger token)
      throws ConstraintError
    {
      this.token = Constraints.constrainNotNull(token, "Token");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTRExpressionVisitor<A, L, E>>
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
      builder.append("[UASTREInteger ");
      builder.append(this.getValue());
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class UASTRELet extends UASTRExpression
  {
    private final @Nonnull List<UASTRDValueLocal> bindings;
    private final @Nonnull UASTRExpression        body;
    private final @Nonnull TokenLet               token;

    public UASTRELet(
      final @Nonnull TokenLet token,
      final @Nonnull List<UASTRDValueLocal> bindings,
      final @Nonnull UASTRExpression body)
      throws ConstraintError
    {
      this.token = Constraints.constrainNotNull(token, "Token");
      this.bindings = Constraints.constrainNotNull(bindings, "Bindings");
      this.body = Constraints.constrainNotNull(body, "Body");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTRExpressionVisitor<A, L, E>>
      A
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      final UASTRLocalLevelVisitor<L, E> bv = v.expressionVisitLetPre(this);

      final ArrayList<L> r_bindings = new ArrayList<L>();
      for (final UASTRDValueLocal b : this.bindings) {
        final L rb = bv.localVisitValueLocal(b);
        r_bindings.add(rb);
      }

      final A x = this.body.expressionVisitableAccept(v);
      return v.expressionVisitLet(r_bindings, x, this);
    }

    public @Nonnull List<UASTRDValueLocal> getBindings()
    {
      return this.bindings;
    }

    public @Nonnull UASTRExpression getBody()
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
      builder.append("[UASTRELet ");
      builder.append(this.bindings);
      builder.append(" ");
      builder.append(this.body);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class UASTRENew extends UASTRExpression
  {
    private final @Nonnull List<UASTRExpression> arguments;
    private final @Nonnull UASTRTypeName         name;

    public UASTRENew(
      final @Nonnull UASTRTypeName name,
      final @Nonnull List<UASTRExpression> arguments)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.arguments = Constraints.constrainNotNull(arguments, "Arguments");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTRExpressionVisitor<A, L, E>>
      A
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      final List<A> args = new ArrayList<A>();
      for (final UASTRExpression b : this.arguments) {
        final A x = b.expressionVisitableAccept(v);
        args.add(x);
      }
      return v.expressionVisitNew(args, this);
    }

    public @Nonnull List<UASTRExpression> getArguments()
    {
      return this.arguments;
    }

    public @Nonnull UASTRTypeName getName()
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

  public static final class UASTREReal extends UASTRExpression
  {
    private final @Nonnull TokenLiteralReal token;

    public UASTREReal(
      final @Nonnull Token.TokenLiteralReal token)
      throws ConstraintError
    {
      this.token = Constraints.constrainNotNull(token, "Token");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTRExpressionVisitor<A, L, E>>
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
      builder.append("[UASTREReal ");
      builder.append(this.getValue());
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class UASTRERecord extends UASTRExpression
  {
    private final @Nonnull List<UASTRRecordFieldAssignment> assignments;
    private final @Nonnull UASTRTypeName                    type_path;

    public UASTRERecord(
      final @Nonnull UASTRTypeName type_path,
      final @Nonnull List<UASTRRecordFieldAssignment> assignments)
      throws ConstraintError
    {
      this.type_path = Constraints.constrainNotNull(type_path, "Type path");
      this.assignments =
        Constraints.constrainNotNull(assignments, "Assignments");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTRExpressionVisitor<A, L, E>>
      A
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.expressionVisitRecord(this);
    }

    public @Nonnull List<UASTRRecordFieldAssignment> getAssignments()
    {
      return this.assignments;
    }

    public @Nonnull UASTRTypeName getTypePath()
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

  public static final class UASTRERecordProjection extends UASTRExpression
  {
    private final @Nonnull UASTRExpression      expression;
    private final @Nonnull TokenIdentifierLower field;

    public UASTRERecordProjection(
      final @Nonnull UASTRExpression expression,
      final @Nonnull TokenIdentifierLower field)
      throws ConstraintError
    {
      this.expression =
        Constraints.constrainNotNull(expression, "Expression");
      this.field = Constraints.constrainNotNull(field, "Field");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTRExpressionVisitor<A, L, E>>
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

    public @Nonnull UASTRExpression getExpression()
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
      builder.append("[UASTRERecordProjection ");
      builder.append(this.expression);
      builder.append(" ");
      builder.append(this.field);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class UASTRESwizzle extends UASTRExpression
  {
    private final @Nonnull UASTRExpression            expression;
    private final @Nonnull List<TokenIdentifierLower> fields;

    public UASTRESwizzle(
      final @Nonnull UASTRExpression expression,
      final @Nonnull List<TokenIdentifierLower> fields)
      throws ConstraintError
    {
      this.expression =
        Constraints.constrainNotNull(expression, "Expression");
      this.fields = Constraints.constrainNotNull(fields, "Fields");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTRExpressionVisitor<A, L, E>>
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

    public @Nonnull UASTRExpression getExpression()
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

  public static final class UASTREVariable extends UASTRExpression
  {
    private final @Nonnull UASTRTermName name;

    public UASTREVariable(
      final @Nonnull UASTRTermName name)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
    }

    @Override public
      <A, L, E extends Throwable, V extends UASTRExpressionVisitor<A, L, E>>
      A
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.expressionVisitVariable(this);
    }

    public @Nonnull UASTRTermName getName()
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

  public static final class UASTRRecordFieldAssignment
  {
    private final @Nonnull UASTRExpression      expression;
    private final @Nonnull TokenIdentifierLower name;

    public UASTRRecordFieldAssignment(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTRExpression expression)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.expression =
        Constraints.constrainNotNull(expression, "Expression");
    }

    public @Nonnull UASTRExpression getExpression()
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
      builder.append("[UASTRRecordFieldAssignment ");
      builder.append(this.name.getActual());
      builder.append(" ");
      builder.append(this.expression);
      builder.append("]");
      return builder.toString();
    }
  }
}
