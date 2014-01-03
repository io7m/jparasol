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

package com.io7m.jparasol.glsl.ast;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.io7m.jparasol.glsl.ast.GTermName.GTermNameGlobal;
import com.io7m.jparasol.typed.TType;

public abstract class GASTExpression implements GASTExpressionVisitable
{
  public static final class GASTEApplication extends GASTExpression
  {
    private final @Nonnull List<GASTExpression> arguments;
    private final @Nonnull GTermNameGlobal      name;
    private final @Nonnull TType                type;

    public GASTEApplication(
      final @Nonnull GTermNameGlobal name,
      final @Nonnull TType type,
      final @Nonnull List<GASTExpression> arguments)
    {
      this.name = name;
      this.type = type;
      this.arguments = arguments;
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
      final GASTEApplication other = (GASTEApplication) obj;
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
      <A, E extends Throwable, V extends GASTExpressionVisitor<A, E>>
      A
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E
    {
      v.expressionApplicationVisitPre(this);
      final List<A> args = new ArrayList<A>();
      for (final GASTExpression a : this.arguments) {
        final A x = a.expressionVisitableAccept(v);
        args.add(x);
      }
      return v.expressionApplicationVisit(args, this);
    }

    public @Nonnull List<GASTExpression> getArguments()
    {
      return this.arguments;
    }

    public @Nonnull GTermNameGlobal getName()
    {
      return this.name;
    }

    public @Nonnull TType getType()
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
      builder.append("[GASTEApplication ");
      builder.append(this.name);
      builder.append(" ");
      builder.append(this.type);
      builder.append(" ");
      builder.append(this.arguments);
      builder.append("]");
      return builder.toString();
    }
  }

  public static abstract class GASTEBinaryOp extends GASTExpression
  {
    public static final class GASTEBinaryOpDivide extends GASTEBinaryOp
    {
      public GASTEBinaryOpDivide(
        final @Nonnull GASTExpression left,
        final @Nonnull GASTExpression right)
      {
        super(left, right);
      }

      @Override public
        <A, E extends Throwable, V extends GASTExpressionVisitor<A, E>>
        A
        expressionVisitableAccept(
          final V v)
          throws E
      {
        v.expressionBinaryOpDivideVisitPre(this);
        final A l = this.getLeft().expressionVisitableAccept(v);
        final A r = this.getRight().expressionVisitableAccept(v);
        return v.expressionBinaryOpDivideVisit(l, r, this);
      }

      @Override public String toString()
      {
        final StringBuilder builder = new StringBuilder();
        builder.append("[GASTEBinaryOpDivide ");
        builder.append(this.getLeft());
        builder.append(" ");
        builder.append(this.getRight());
        builder.append("]");
        return builder.toString();
      }
    }

    public static final class GASTEBinaryOpEqual extends GASTEBinaryOp
    {
      public GASTEBinaryOpEqual(
        final @Nonnull GASTExpression left,
        final @Nonnull GASTExpression right)
      {
        super(left, right);
      }

      @Override public
        <A, E extends Throwable, V extends GASTExpressionVisitor<A, E>>
        A
        expressionVisitableAccept(
          final V v)
          throws E
      {
        v.expressionBinaryOpEqualVisitPre(this);
        final A l = this.getLeft().expressionVisitableAccept(v);
        final A r = this.getRight().expressionVisitableAccept(v);
        return v.expressionBinaryOpEqualVisit(l, r, this);
      }

      @Override public String toString()
      {
        final StringBuilder builder = new StringBuilder();
        builder.append("[GASTEBinaryOpEqual ");
        builder.append(this.getLeft());
        builder.append(" ");
        builder.append(this.getRight());
        builder.append("]");
        return builder.toString();
      }
    }

    public static final class GASTEBinaryOpGreaterThan extends GASTEBinaryOp
    {
      public GASTEBinaryOpGreaterThan(
        final @Nonnull GASTExpression left,
        final @Nonnull GASTExpression right)
      {
        super(left, right);
      }

      @Override public
        <A, E extends Throwable, V extends GASTExpressionVisitor<A, E>>
        A
        expressionVisitableAccept(
          final V v)
          throws E
      {
        v.expressionBinaryOpGreaterThanVisitPre(this);
        final A l = this.getLeft().expressionVisitableAccept(v);
        final A r = this.getRight().expressionVisitableAccept(v);
        return v.expressionBinaryOpGreaterThanVisit(l, r, this);
      }

      @Override public String toString()
      {
        final StringBuilder builder = new StringBuilder();
        builder.append("[GASTEBinaryOpGreaterThan ");
        builder.append(this.getLeft());
        builder.append(" ");
        builder.append(this.getRight());
        builder.append("]");
        return builder.toString();
      }
    }

    public static final class GASTEBinaryOpGreaterThanOrEqual extends
      GASTEBinaryOp
    {
      public GASTEBinaryOpGreaterThanOrEqual(
        final @Nonnull GASTExpression left,
        final @Nonnull GASTExpression right)
      {
        super(left, right);
      }

      @Override public
        <A, E extends Throwable, V extends GASTExpressionVisitor<A, E>>
        A
        expressionVisitableAccept(
          final V v)
          throws E
      {
        v.expressionBinaryOpGreaterThanOrEqualVisitPre(this);
        final A l = this.getLeft().expressionVisitableAccept(v);
        final A r = this.getRight().expressionVisitableAccept(v);
        return v.expressionBinaryOpGreaterThanOrEqualVisit(l, r, this);
      }

      @Override public String toString()
      {
        final StringBuilder builder = new StringBuilder();
        builder.append("[GASTEBinaryOpGreaterThanOrEqual ");
        builder.append(this.getLeft());
        builder.append(" ");
        builder.append(this.getRight());
        builder.append("]");
        return builder.toString();
      }
    }

    public static final class GASTEBinaryOpLesserThan extends GASTEBinaryOp
    {
      public GASTEBinaryOpLesserThan(
        final @Nonnull GASTExpression left,
        final @Nonnull GASTExpression right)
      {
        super(left, right);
      }

      @Override public
        <A, E extends Throwable, V extends GASTExpressionVisitor<A, E>>
        A
        expressionVisitableAccept(
          final V v)
          throws E
      {
        v.expressionBinaryOpLesserThanVisitPre(this);
        final A l = this.getLeft().expressionVisitableAccept(v);
        final A r = this.getRight().expressionVisitableAccept(v);
        return v.expressionBinaryOpLesserThanVisit(l, r, this);
      }

      @Override public String toString()
      {
        final StringBuilder builder = new StringBuilder();
        builder.append("[GASTEBinaryOpLesserThan ");
        builder.append(this.getLeft());
        builder.append(" ");
        builder.append(this.getRight());
        builder.append("]");
        return builder.toString();
      }
    }

    public static final class GASTEBinaryOpLesserThanOrEqual extends
      GASTEBinaryOp
    {
      public GASTEBinaryOpLesserThanOrEqual(
        final @Nonnull GASTExpression left,
        final @Nonnull GASTExpression right)
      {
        super(left, right);
      }

      @Override public
        <A, E extends Throwable, V extends GASTExpressionVisitor<A, E>>
        A
        expressionVisitableAccept(
          final V v)
          throws E
      {
        v.expressionBinaryOpLesserThanOrEqualVisitPre(this);
        final A l = this.getLeft().expressionVisitableAccept(v);
        final A r = this.getRight().expressionVisitableAccept(v);
        return v.expressionBinaryOpLesserThanOrEqualVisit(l, r, this);
      }

      @Override public String toString()
      {
        final StringBuilder builder = new StringBuilder();
        builder.append("[GASTEBinaryOpLesserThanOrEqual ");
        builder.append(this.getLeft());
        builder.append(" ");
        builder.append(this.getRight());
        builder.append("]");
        return builder.toString();
      }
    }

    public static final class GASTEBinaryOpMultiply extends GASTEBinaryOp
    {
      public GASTEBinaryOpMultiply(
        final @Nonnull GASTExpression left,
        final @Nonnull GASTExpression right)
      {
        super(left, right);
      }

      @Override public
        <A, E extends Throwable, V extends GASTExpressionVisitor<A, E>>
        A
        expressionVisitableAccept(
          final V v)
          throws E
      {
        v.expressionBinaryOpMultiplyVisitPre(this);
        final A l = this.getLeft().expressionVisitableAccept(v);
        final A r = this.getRight().expressionVisitableAccept(v);
        return v.expressionBinaryOpMultiplyVisit(l, r, this);
      }

      @Override public String toString()
      {
        final StringBuilder builder = new StringBuilder();
        builder.append("[GASTEBinaryOpMultiply ");
        builder.append(this.getLeft());
        builder.append(" ");
        builder.append(this.getRight());
        builder.append("]");
        return builder.toString();
      }
    }

    public static final class GASTEBinaryOpPlus extends GASTEBinaryOp
    {
      public GASTEBinaryOpPlus(
        final @Nonnull GASTExpression left,
        final @Nonnull GASTExpression right)
      {
        super(left, right);
      }

      @Override public
        <A, E extends Throwable, V extends GASTExpressionVisitor<A, E>>
        A
        expressionVisitableAccept(
          final V v)
          throws E
      {
        v.expressionBinaryOpPlusVisitPre(this);
        final A l = this.getLeft().expressionVisitableAccept(v);
        final A r = this.getRight().expressionVisitableAccept(v);
        return v.expressionBinaryOpPlusVisit(l, r, this);
      }

      @Override public String toString()
      {
        final StringBuilder builder = new StringBuilder();
        builder.append("[GASTEBinaryOpPlus ");
        builder.append(this.getLeft());
        builder.append(" ");
        builder.append(this.getRight());
        builder.append("]");
        return builder.toString();
      }
    }

    public static final class GASTEBinaryOpSubtract extends GASTEBinaryOp
    {
      public GASTEBinaryOpSubtract(
        final @Nonnull GASTExpression left,
        final @Nonnull GASTExpression right)
      {
        super(left, right);
      }

      @Override public
        <A, E extends Throwable, V extends GASTExpressionVisitor<A, E>>
        A
        expressionVisitableAccept(
          final V v)
          throws E
      {
        v.expressionBinaryOpSubtractVisitPre(this);
        final A l = this.getLeft().expressionVisitableAccept(v);
        final A r = this.getRight().expressionVisitableAccept(v);
        return v.expressionBinaryOpSubtractVisit(l, r, this);
      }

      @Override public String toString()
      {
        final StringBuilder builder = new StringBuilder();
        builder.append("[GASTEBinaryOpSubtract ");
        builder.append(this.getLeft());
        builder.append(" ");
        builder.append(this.getRight());
        builder.append("]");
        return builder.toString();
      }
    }

    private final @Nonnull GASTExpression left;
    private final @Nonnull GASTExpression right;

    protected GASTEBinaryOp(
      final @Nonnull GASTExpression left,
      final @Nonnull GASTExpression right)
    {
      this.left = left;
      this.right = right;
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
      final GASTEBinaryOp other = (GASTEBinaryOp) obj;
      if (!this.left.equals(other.left)) {
        return false;
      }
      if (!this.right.equals(other.right)) {
        return false;
      }
      return true;
    }

    public @Nonnull GASTExpression getLeft()
    {
      return this.left;
    }

    public @Nonnull GASTExpression getRight()
    {
      return this.right;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.left.hashCode();
      result = (prime * result) + this.right.hashCode();
      return result;
    }
  }

  public static final class GASTEBoolean extends GASTExpression
  {
    private final boolean value;

    public GASTEBoolean(
      final boolean value)
    {
      this.value = value;
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
      final GASTEBoolean other = (GASTEBoolean) obj;
      return this.value == other.value;
    }

    @Override public
      <A, E extends Throwable, V extends GASTExpressionVisitor<A, E>>
      A
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E
    {
      return v.expressionBooleanVisit(this);
    }

    public boolean getValue()
    {
      return this.value;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + (this.value ? 1 : 0);
      return result;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[GASTEBoolean ");
      builder.append(this.value);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class GASTEConstruction extends GASTExpression
  {
    private final @Nonnull List<GASTExpression> arguments;
    private final @Nonnull GTypeName            type;

    public GASTEConstruction(
      final @Nonnull GTypeName type,
      final @Nonnull List<GASTExpression> arguments)
    {
      this.type = type;
      this.arguments = arguments;
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
      final GASTEConstruction other = (GASTEConstruction) obj;
      if (!this.arguments.equals(other.arguments)) {
        return false;
      }
      if (!this.type.equals(other.type)) {
        return false;
      }
      return true;
    }

    @Override public
      <A, E extends Throwable, V extends GASTExpressionVisitor<A, E>>
      A
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E
    {
      v.expressionConstructionVisitPre(this);
      final List<A> args = new ArrayList<A>();
      for (final GASTExpression a : this.arguments) {
        final A x = a.expressionVisitableAccept(v);
        args.add(x);
      }
      return v.expressionConstructionVisit(args, this);
    }

    public @Nonnull List<GASTExpression> getArguments()
    {
      return this.arguments;
    }

    public @Nonnull GTypeName getType()
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
      builder.append("[GASTEConstruction ");
      builder.append(this.type);
      builder.append(" ");
      builder.append(this.arguments);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class GASTEFloat extends GASTExpression
  {
    private final @Nonnull BigDecimal value;

    public GASTEFloat(
      final @Nonnull BigDecimal value)
    {
      this.value = value;
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
      final GASTEFloat other = (GASTEFloat) obj;
      return this.value.equals(other.value);
    }

    @Override public
      <A, E extends Throwable, V extends GASTExpressionVisitor<A, E>>
      A
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E
    {
      return v.expressionFloatVisit(this);
    }

    public @Nonnull BigDecimal getValue()
    {
      return this.value;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.value.hashCode();
      return result;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[GASTEFloat ");
      builder.append(this.value);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class GASTEInteger extends GASTExpression
  {
    private final @Nonnull BigInteger value;

    public GASTEInteger(
      final @Nonnull BigInteger value)
    {
      this.value = value;
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
      final GASTEInteger other = (GASTEInteger) obj;
      return this.value.equals(other.value);
    }

    @Override public
      <A, E extends Throwable, V extends GASTExpressionVisitor<A, E>>
      A
      expressionVisitableAccept(
        final @Nonnull V v)
        throws E
    {
      return v.expressionIntegerVisit(this);
    }

    public @Nonnull BigInteger getValue()
    {
      return this.value;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.value.hashCode();
      return result;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[GASTEInteger ");
      builder.append(this.value);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class GASTEProjection extends GASTExpression
  {
    private final @Nonnull GASTExpression body;
    private final @Nonnull GFieldName     field;
    private final @Nonnull TType          type;

    public GASTEProjection(
      final @Nonnull GASTExpression body,
      final @Nonnull GFieldName field,
      final @Nonnull TType type)
    {
      this.body = body;
      this.field = field;
      this.type = type;
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
      final GASTEProjection other = (GASTEProjection) obj;
      if (!this.body.equals(other.body)) {
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
      <A, E extends Throwable, V extends GASTExpressionVisitor<A, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      v.expressionProjectionVisitPre(this);
      final A x = this.body.expressionVisitableAccept(v);
      return v.expressionProjectionVisit(x, this);
    }

    public @Nonnull GASTExpression getBody()
    {
      return this.body;
    }

    public @Nonnull GFieldName getField()
    {
      return this.field;
    }

    public @Nonnull TType getType()
    {
      return this.type;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.body.hashCode();
      result = (prime * result) + this.field.hashCode();
      result = (prime * result) + this.type.hashCode();
      return result;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[GASTEProjection ");
      builder.append(this.body);
      builder.append(" ");
      builder.append(this.field);
      builder.append(" ");
      builder.append(this.type);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class GASTESwizzle extends GASTExpression
  {
    private final @Nonnull GASTExpression   body;
    private final @Nonnull List<GFieldName> fields;
    private final @Nonnull TType            type;

    public GASTESwizzle(
      final @Nonnull GASTExpression body,
      final @Nonnull List<GFieldName> fields,
      final @Nonnull TType type)
    {
      this.body = body;
      this.fields = fields;
      this.type = type;
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
      final GASTESwizzle other = (GASTESwizzle) obj;
      if (!this.body.equals(other.body)) {
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
      <A, E extends Throwable, V extends GASTExpressionVisitor<A, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      v.expressionSwizzleVisitPre(this);
      final A x = this.body.expressionVisitableAccept(v);
      return v.expressionSwizzleVisit(x, this);
    }

    public @Nonnull GASTExpression getBody()
    {
      return this.body;
    }

    public @Nonnull List<GFieldName> getFields()
    {
      return this.fields;
    }

    public @Nonnull TType getType()
    {
      return this.type;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.body.hashCode();
      result = (prime * result) + this.fields.hashCode();
      result = (prime * result) + this.type.hashCode();
      return result;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[GASTESwizzle ");
      builder.append(this.body);
      builder.append(" ");
      builder.append(this.fields);
      builder.append(" ");
      builder.append(this.type);
      builder.append("]");
      return builder.toString();
    }
  }

  public static abstract class GASTEUnaryOp extends GASTExpression
  {
    public static final class GASTEUnaryOpNegate extends GASTEUnaryOp
    {
      public GASTEUnaryOpNegate(
        final @Nonnull GASTExpression body)
      {
        super(body);
      }

      @Override public
        <A, E extends Throwable, V extends GASTExpressionVisitor<A, E>>
        A
        expressionVisitableAccept(
          final V v)
          throws E
      {
        v.expressionUnaryOpNegateVisitPre(this);
        final A x = this.getBody().expressionVisitableAccept(v);
        return v.expressionUnaryOpNegateVisit(x, this);
      }

      @Override public String toString()
      {
        final StringBuilder builder = new StringBuilder();
        builder.append("[GASTEUnaryOpNegate ");
        builder.append(this.getBody());
        builder.append("]");
        return builder.toString();
      }
    }

    private final @Nonnull GASTExpression body;

    protected GASTEUnaryOp(
      final @Nonnull GASTExpression body)
    {
      this.body = body;
    }

    public @Nonnull GASTExpression getBody()
    {
      return this.body;
    }
  }

  public static final class GASTEVariable extends GASTExpression
  {
    private final @Nonnull GTermName term;
    private final @Nonnull GTypeName type;

    public GASTEVariable(
      final @Nonnull GTypeName type,
      final @Nonnull GTermName term)
    {
      this.type = type;
      this.term = term;
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
      final GASTEVariable other = (GASTEVariable) obj;
      if (!this.term.equals(other.term)) {
        return false;
      }
      if (!this.type.equals(other.type)) {
        return false;
      }
      return true;
    }

    @Override public
      <A, E extends Throwable, V extends GASTExpressionVisitor<A, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      return v.expressionVariableVisit(this);
    }

    public @Nonnull GTermName getTerm()
    {
      return this.term;
    }

    public @Nonnull GTypeName getType()
    {
      return this.type;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.term.hashCode();
      result = (prime * result) + this.type.hashCode();
      return result;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[GASTEVariable ");
      builder.append(this.type);
      builder.append(" ");
      builder.append(this.term);
      builder.append("]");
      return builder.toString();
    }
  }
}
