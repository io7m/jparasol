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

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.lexer.Token;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.lexer.Token.TokenLet;
import com.io7m.jparasol.lexer.Token.TokenLiteralBoolean;
import com.io7m.jparasol.lexer.Token.TokenLiteralInteger;
import com.io7m.jparasol.lexer.Token.TokenLiteralReal;
import com.io7m.jparasol.typed.TType;
import com.io7m.jparasol.typed.TType.TBoolean;
import com.io7m.jparasol.typed.TType.TFloat;
import com.io7m.jparasol.typed.TType.TInteger;
import com.io7m.jparasol.typed.TType.TRecord;
import com.io7m.jparasol.typed.TType.TValueType;
import com.io7m.jparasol.typed.TType.TVectorType;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDValueLocal;

public abstract class TASTExpression implements TASTExpressionVisitable
{
  public static final class TASTEApplication extends TASTExpression
  {
    private final @Nonnull List<TASTExpression> arguments;
    private final @Nonnull TASTTermName         name;
    private final @Nonnull TType                type;

    public TASTEApplication(
      final @Nonnull TASTTermName name,
      final @Nonnull List<TASTExpression> arguments,
      final @Nonnull TType type)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.arguments = Constraints.constrainNotNull(arguments, "Arguments");
      this.type = Constraints.constrainNotNull(type, "Type");
    }

    @Override public boolean equals(
      final Object obj)
    {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (this.getClass() != obj.getClass()) {
        return false;
      }
      final TASTEApplication other = (TASTEApplication) obj;
      if (!this.arguments.equals(other.arguments)) {
        return false;
      }
      if (!this.name.equals(other.name)) {
        return false;
      }
      if (!this.type.equals(other.type)) {
        return false;
      }
      return true;
    }

    @Override public
      <A, L, E extends Throwable, V extends TASTExpressionVisitor<A, L, E>>
      A
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
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

    public @Nonnull List<TASTExpression> getArguments()
    {
      return this.arguments;
    }

    public @Nonnull TASTTermName getName()
    {
      return this.name;
    }

    @Override public @Nonnull TType getType()
    {
      return this.type;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.arguments.hashCode();
      result = (prime * result) + this.name.hashCode();
      result = (prime * result) + this.type.hashCode();
      return result;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTEApplication ");
      builder.append(this.name.show());
      builder.append(" ");
      builder.append(this.arguments);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class TASTEBoolean extends TASTExpression
  {
    private final @Nonnull TokenLiteralBoolean token;

    public TASTEBoolean(
      final @Nonnull Token.TokenLiteralBoolean token)
      throws ConstraintError
    {
      this.token = Constraints.constrainNotNull(token, "Token");
    }

    @Override public boolean equals(
      final Object obj)
    {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (this.getClass() != obj.getClass()) {
        return false;
      }
      final TASTEBoolean other = (TASTEBoolean) obj;
      if (!this.token.equals(other.token)) {
        return false;
      }
      return true;
    }

    @Override public
      <A, L, E extends Throwable, V extends TASTExpressionVisitor<A, L, E>>
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

    @Override public TType getType()
    {
      return TBoolean.get();
    }

    public boolean getValue()
    {
      return this.token.getValue();
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.token.hashCode();
      return result;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTEBoolean ");
      builder.append(this.getValue());
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class TASTEConditional extends TASTExpression
  {
    private final @Nonnull TASTExpression condition;
    private final @Nonnull TASTExpression left;
    private final @Nonnull TASTExpression right;

    public TASTEConditional(
      final @Nonnull TASTExpression condition,
      final @Nonnull TASTExpression left,
      final @Nonnull TASTExpression right)
      throws ConstraintError
    {
      this.condition = Constraints.constrainNotNull(condition, "Condition");
      this.left = Constraints.constrainNotNull(left, "Left");
      this.right = Constraints.constrainNotNull(right, "Right");

      assert (this.condition.getType().equals(TBoolean.get()));
      assert (this.left.getType().equals(this.right.getType()));
    }

    @Override public boolean equals(
      final Object obj)
    {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (this.getClass() != obj.getClass()) {
        return false;
      }
      final TASTEConditional other = (TASTEConditional) obj;
      if (!this.condition.equals(other.condition)) {
        return false;
      }
      if (!this.left.equals(other.left)) {
        return false;
      }
      if (!this.right.equals(other.right)) {
        return false;
      }
      return true;
    }

    @Override public
      <A, L, E extends Throwable, V extends TASTExpressionVisitor<A, L, E>>
      A
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
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

    public @Nonnull TASTExpression getCondition()
    {
      return this.condition;
    }

    public @Nonnull TASTExpression getLeft()
    {
      return this.left;
    }

    public @Nonnull TASTExpression getRight()
    {
      return this.right;
    }

    @Override public TType getType()
    {
      return this.left.getType();
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.condition.hashCode();
      result = (prime * result) + this.left.hashCode();
      result = (prime * result) + this.right.hashCode();
      return result;
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
      return builder.toString();
    }
  }

  public static final class TASTEInteger extends TASTExpression
  {
    private final @Nonnull TokenLiteralInteger token;

    public TASTEInteger(
      final @Nonnull Token.TokenLiteralInteger token)
      throws ConstraintError
    {
      this.token = Constraints.constrainNotNull(token, "Token");
    }

    @Override public boolean equals(
      final Object obj)
    {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (this.getClass() != obj.getClass()) {
        return false;
      }
      final TASTEInteger other = (TASTEInteger) obj;
      if (!this.token.equals(other.token)) {
        return false;
      }
      return true;
    }

    @Override public
      <A, L, E extends Throwable, V extends TASTExpressionVisitor<A, L, E>>
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

    @Override public TType getType()
    {
      return TInteger.get();
    }

    public @Nonnull BigInteger getValue()
    {
      return this.token.getValue();
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.token.hashCode();
      return result;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTEInteger ");
      builder.append(this.getValue());
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class TASTELet extends TASTExpression
  {
    private final @Nonnull List<TASTDValueLocal> bindings;
    private final @Nonnull TASTExpression        body;
    private final @Nonnull TokenLet              token;

    public TASTELet(
      final @Nonnull TokenLet token,
      final @Nonnull List<TASTDValueLocal> bindings,
      final @Nonnull TASTExpression body)
      throws ConstraintError
    {
      this.token = Constraints.constrainNotNull(token, "Token");
      this.bindings = Constraints.constrainNotNull(bindings, "Bindings");
      this.body = Constraints.constrainNotNull(body, "Body");
    }

    @Override public boolean equals(
      final Object obj)
    {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (this.getClass() != obj.getClass()) {
        return false;
      }
      final TASTELet other = (TASTELet) obj;
      if (!this.bindings.equals(other.bindings)) {
        return false;
      }
      if (!this.body.equals(other.body)) {
        return false;
      }
      if (!this.token.equals(other.token)) {
        return false;
      }
      return true;
    }

    @Override public
      <A, L, E extends Throwable, V extends TASTExpressionVisitor<A, L, E>>
      A
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      final TASTLocalLevelVisitor<L, E> bv = v.expressionVisitLetPre(this);

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

    public @Nonnull List<TASTDValueLocal> getBindings()
    {
      return this.bindings;
    }

    public @Nonnull TASTExpression getBody()
    {
      return this.body;
    }

    public @Nonnull TokenLet getToken()
    {
      return this.token;
    }

    @Override public TType getType()
    {
      return this.body.getType();
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.bindings.hashCode();
      result = (prime * result) + this.body.hashCode();
      result = (prime * result) + this.token.hashCode();
      return result;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTELet ");
      builder.append(this.bindings);
      builder.append(" ");
      builder.append(this.body);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class TASTENew extends TASTExpression
  {
    private final @Nonnull List<TASTExpression> arguments;
    private final @Nonnull TValueType           type;

    public TASTENew(
      final @Nonnull TValueType type,
      final @Nonnull List<TASTExpression> arguments)
      throws ConstraintError
    {
      this.type = Constraints.constrainNotNull(type, "Type");
      this.arguments = Constraints.constrainNotNull(arguments, "Arguments");
    }

    @Override public boolean equals(
      final Object obj)
    {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (this.getClass() != obj.getClass()) {
        return false;
      }
      final TASTENew other = (TASTENew) obj;
      if (!this.arguments.equals(other.arguments)) {
        return false;
      }
      if (!this.type.equals(other.type)) {
        return false;
      }
      return true;
    }

    @Override public
      <A, L, E extends Throwable, V extends TASTExpressionVisitor<A, L, E>>
      A
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
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

    public @Nonnull List<TASTExpression> getArguments()
    {
      return this.arguments;
    }

    @Override public TType getType()
    {
      return this.type;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.arguments.hashCode();
      result = (prime * result) + this.type.hashCode();
      return result;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTENew ");
      builder.append(this.type.getName());
      builder.append(" ");
      builder.append(this.arguments);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class TASTEReal extends TASTExpression
  {
    private final @Nonnull TokenLiteralReal token;

    public TASTEReal(
      final @Nonnull Token.TokenLiteralReal token)
      throws ConstraintError
    {
      this.token = Constraints.constrainNotNull(token, "Token");
    }

    @Override public boolean equals(
      final Object obj)
    {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (this.getClass() != obj.getClass()) {
        return false;
      }
      final TASTEReal other = (TASTEReal) obj;
      if (!this.token.equals(other.token)) {
        return false;
      }
      return true;
    }

    @Override public
      <A, L, E extends Throwable, V extends TASTExpressionVisitor<A, L, E>>
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

    @Override public TType getType()
    {
      return TFloat.get();
    }

    public @Nonnull BigDecimal getValue()
    {
      return this.token.getValue();
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.token.hashCode();
      return result;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTEReal ");
      builder.append(this.getValue());
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class TASTERecord extends TASTExpression
  {
    private final @Nonnull List<TASTRecordFieldAssignment> assignments;
    private final @Nonnull TRecord                         type;

    public TASTERecord(
      final @Nonnull TRecord type,
      final @Nonnull List<TASTRecordFieldAssignment> assignments)
      throws ConstraintError
    {
      this.type = Constraints.constrainNotNull(type, "Type path");
      this.assignments =
        Constraints.constrainNotNull(assignments, "Assignments");
    }

    @Override public boolean equals(
      final Object obj)
    {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (this.getClass() != obj.getClass()) {
        return false;
      }
      final TASTERecord other = (TASTERecord) obj;
      if (!this.assignments.equals(other.assignments)) {
        return false;
      }
      if (!this.type.equals(other.type)) {
        return false;
      }
      return true;
    }

    @Override public
      <A, L, E extends Throwable, V extends TASTExpressionVisitor<A, L, E>>
      A
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.expressionVisitRecord(this);
    }

    public @Nonnull List<TASTRecordFieldAssignment> getAssignments()
    {
      return this.assignments;
    }

    @Override public TType getType()
    {
      return this.type;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.assignments.hashCode();
      result = (prime * result) + this.type.hashCode();
      return result;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTERecord ");
      builder.append(this.type.getName());
      builder.append(" ");
      builder.append(this.assignments);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class TASTERecordProjection extends TASTExpression
  {
    private final @Nonnull TASTExpression       expression;
    private final @Nonnull TokenIdentifierLower field;
    private final @Nonnull TValueType           type;

    public TASTERecordProjection(
      final @Nonnull TValueType type,
      final @Nonnull TASTExpression expression,
      final @Nonnull TokenIdentifierLower field)
      throws ConstraintError
    {
      this.type = Constraints.constrainNotNull(type, "Type");
      this.expression =
        Constraints.constrainNotNull(expression, "Expression");
      this.field = Constraints.constrainNotNull(field, "Field");
    }

    @Override public boolean equals(
      final Object obj)
    {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (this.getClass() != obj.getClass()) {
        return false;
      }
      final TASTERecordProjection other = (TASTERecordProjection) obj;
      if (!this.expression.equals(other.expression)) {
        return false;
      }
      if (!this.field.equals(other.field)) {
        return false;
      }
      if (!this.type.equals(other.type)) {
        return false;
      }
      return true;
    }

    @Override public
      <A, L, E extends Throwable, V extends TASTExpressionVisitor<A, L, E>>
      A
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      final boolean traverse = v.expressionVisitRecordProjectionPre(this);
      A x = null;
      if (traverse) {
        x = this.expression.expressionVisitableAccept(v);
      }
      return v.expressionVisitRecordProjection(x, this);
    }

    public @Nonnull TASTExpression getExpression()
    {
      return this.expression;
    }

    public @Nonnull TokenIdentifierLower getField()
    {
      return this.field;
    }

    @Override public TType getType()
    {
      return this.type;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.expression.hashCode();
      result = (prime * result) + this.field.hashCode();
      result = (prime * result) + this.type.hashCode();
      return result;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTERecordProjection ");
      builder.append(this.expression);
      builder.append(" ");
      builder.append(this.field);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class TASTESwizzle extends TASTExpression
  {
    private final @Nonnull TASTExpression             expression;
    private final @Nonnull List<TokenIdentifierLower> fields;
    private final @Nonnull TValueType                 type;

    public TASTESwizzle(
      final @Nonnull TValueType type,
      final @Nonnull TASTExpression expression,
      final @Nonnull List<TokenIdentifierLower> fields)
      throws ConstraintError
    {
      this.type = Constraints.constrainNotNull(type, "Type");
      this.expression =
        Constraints.constrainNotNull(expression, "Expression");
      this.fields = Constraints.constrainNotNull(fields, "Fields");
      assert (this.expression.getType() instanceof TVectorType);
    }

    @Override public boolean equals(
      final Object obj)
    {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (this.getClass() != obj.getClass()) {
        return false;
      }
      final TASTESwizzle other = (TASTESwizzle) obj;
      if (!this.expression.equals(other.expression)) {
        return false;
      }
      if (!this.fields.equals(other.fields)) {
        return false;
      }
      if (!this.type.equals(other.type)) {
        return false;
      }
      return true;
    }

    @Override public
      <A, L, E extends Throwable, V extends TASTExpressionVisitor<A, L, E>>
      A
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      final boolean traverse = v.expressionVisitSwizzlePre(this);
      A x = null;
      if (traverse) {
        x = this.expression.expressionVisitableAccept(v);
      }
      return v.expressionVisitSwizzle(x, this);
    }

    public @Nonnull TASTExpression getExpression()
    {
      return this.expression;
    }

    public @Nonnull List<TokenIdentifierLower> getFields()
    {
      return this.fields;
    }

    @Override public TType getType()
    {
      return this.type;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.expression.hashCode();
      result = (prime * result) + this.fields.hashCode();
      result = (prime * result) + this.type.hashCode();
      return result;
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
      return builder.toString();
    }
  }

  public static final class TASTEVariable extends TASTExpression
  {
    private final @Nonnull TASTTermName name;
    private final @Nonnull TType        type;

    public TASTEVariable(
      final @Nonnull TType type,
      final @Nonnull TASTTermName name)
      throws ConstraintError
    {
      this.type = Constraints.constrainNotNull(type, "Type");
      this.name = Constraints.constrainNotNull(name, "Name");
    }

    @Override public boolean equals(
      final Object obj)
    {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (this.getClass() != obj.getClass()) {
        return false;
      }
      final TASTEVariable other = (TASTEVariable) obj;
      if (!this.name.equals(other.name)) {
        return false;
      }
      if (!this.type.equals(other.type)) {
        return false;
      }
      return true;
    }

    @Override public
      <A, L, E extends Throwable, V extends TASTExpressionVisitor<A, L, E>>
      A
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.expressionVisitVariable(this);
    }

    public @Nonnull TASTTermName getName()
    {
      return this.name;
    }

    @Override public @Nonnull TType getType()
    {
      return this.type;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.name.hashCode();
      result = (prime * result) + this.type.hashCode();
      return result;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTEVariable ");
      builder.append(this.name.show());
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class TASTRecordFieldAssignment
  {
    private final @Nonnull TASTExpression       expression;
    private final @Nonnull TokenIdentifierLower name;

    public TASTRecordFieldAssignment(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull TASTExpression expression)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.expression =
        Constraints.constrainNotNull(expression, "Expression");
    }

    @Override public boolean equals(
      final Object obj)
    {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (this.getClass() != obj.getClass()) {
        return false;
      }
      final TASTRecordFieldAssignment other = (TASTRecordFieldAssignment) obj;
      if (!this.expression.equals(other.expression)) {
        return false;
      }
      if (!this.name.equals(other.name)) {
        return false;
      }
      return true;
    }

    public @Nonnull TASTExpression getExpression()
    {
      return this.expression;
    }

    public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.expression.hashCode();
      result = (prime * result) + this.name.hashCode();
      return result;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTRecordFieldAssignment ");
      builder.append(this.name.getActual());
      builder.append(" ");
      builder.append(this.expression);
      builder.append("]");
      return builder.toString();
    }
  }

  public abstract @Nonnull TType getType();
}
