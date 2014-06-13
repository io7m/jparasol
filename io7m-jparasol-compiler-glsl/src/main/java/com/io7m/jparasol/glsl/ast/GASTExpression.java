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

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.glsl.ast.GTermName.GTermNameExternal;
import com.io7m.jparasol.glsl.ast.GTermName.GTermNameGlobal;
import com.io7m.jparasol.typed.TType;

/**
 * The type of GLSL expressions.
 */

@EqualityReference public abstract class GASTExpression
{
  /**
   * The type of function applications.
   */

  @EqualityReference public static final class GASTEApplication extends
    GASTExpression
  {
    private final List<GASTExpression> arguments;
    private final GTermNameGlobal      name;
    private final TType                type;

    /**
     * Construct an expression.
     * 
     * @param in_name
     *          The function name
     * @param in_type
     *          The type of expression
     * @param in_arguments
     *          The arguments
     */

    public GASTEApplication(
      final GTermNameGlobal in_name,
      final TType in_type,
      final List<GASTExpression> in_arguments)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.type = NullCheck.notNull(in_type, "Type");
      this.arguments = NullCheck.notNull(in_arguments, "Arguments");
    }

    @Override public
      <A, E extends Throwable, V extends GASTExpressionVisitorType<A, E>>
      A
      expressionVisitableAccept(
        final V v)
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

    /**
     * @return The list of arguments
     */

    public List<GASTExpression> getArguments()
    {
      return this.arguments;
    }

    /**
     * @return The function name
     */

    public GTermNameGlobal getName()
    {
      return this.name;
    }

    /**
     * @return The return type
     */

    public TType getType()
    {
      return this.type;
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
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  /**
   * The type of (external) function applications.
   */

  @EqualityReference public static final class GASTEApplicationExternal extends
    GASTExpression
  {
    private final List<GASTExpression> arguments;
    private final GTermNameExternal    name;
    private final TType                type;

    /**
     * Construct an expression.
     * 
     * @param in_name
     *          The function name
     * @param in_type
     *          The type of expression
     * @param in_arguments
     *          The arguments
     */

    public GASTEApplicationExternal(
      final GTermNameExternal in_name,
      final TType in_type,
      final List<GASTExpression> in_arguments)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.type = NullCheck.notNull(in_type, "Type");
      this.arguments = NullCheck.notNull(in_arguments, "Arguments");
    }

    @Override public
      <A, E extends Throwable, V extends GASTExpressionVisitorType<A, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      v.expressionApplicationExternalVisitPre(this);
      final List<A> args = new ArrayList<A>();
      for (final GASTExpression a : this.arguments) {
        final A x = a.expressionVisitableAccept(v);
        args.add(x);
      }
      return v.expressionApplicationExternalVisit(args, this);
    }

    /**
     * @return The arguments
     */

    public List<GASTExpression> getArguments()
    {
      return this.arguments;
    }

    /**
     * @return The function name
     */

    public GTermNameExternal getName()
    {
      return this.name;
    }

    /**
     * @return The return type
     */

    public TType getType()
    {
      return this.type;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[GASTEApplicationExternal ");
      builder.append(this.name);
      builder.append(" ");
      builder.append(this.type);
      builder.append(" ");
      builder.append(this.arguments);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  /**
   * The type of binary op expressions.
   */

  @EqualityReference public static abstract class GASTEBinaryOp extends
    GASTExpression
  {
    /**
     * The type of divisions.
     */

    @EqualityReference public static final class GASTEBinaryOpDivide extends
      GASTEBinaryOp
    {
      /**
       * Construct a binary op expression.
       * 
       * @param left
       *          The left expression
       * @param right
       *          The right expression
       */

      public GASTEBinaryOpDivide(
        final GASTExpression left,
        final GASTExpression right)
      {
        super(left, right);
      }

      @Override public
        <A, E extends Throwable, V extends GASTExpressionVisitorType<A, E>>
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
        final String r = builder.toString();
        assert r != null;
        return r;
      }
    }

    /**
     * The type of equality comparisons.
     */

    @EqualityReference public static final class GASTEBinaryOpEqual extends
      GASTEBinaryOp
    {
      /**
       * Construct a binary op expression.
       * 
       * @param left
       *          The left expression
       * @param right
       *          The right expression
       */

      public GASTEBinaryOpEqual(
        final GASTExpression left,
        final GASTExpression right)
      {
        super(left, right);
      }

      @Override public
        <A, E extends Throwable, V extends GASTExpressionVisitorType<A, E>>
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
        final String r = builder.toString();
        assert r != null;
        return r;
      }
    }

    /**
     * The type of greater-than comparisons.
     */

    @EqualityReference public static final class GASTEBinaryOpGreaterThan extends
      GASTEBinaryOp
    {
      /**
       * Construct a binary op expression.
       * 
       * @param left
       *          The left expression
       * @param right
       *          The right expression
       */

      public GASTEBinaryOpGreaterThan(
        final GASTExpression left,
        final GASTExpression right)
      {
        super(left, right);
      }

      @Override public
        <A, E extends Throwable, V extends GASTExpressionVisitorType<A, E>>
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
        final String r = builder.toString();
        assert r != null;
        return r;
      }
    }

    /**
     * The type of greater-than-or-equal comparisons.
     */

    @EqualityReference public static final class GASTEBinaryOpGreaterThanOrEqual extends
      GASTEBinaryOp
    {
      /**
       * Construct a binary op expression.
       * 
       * @param left
       *          The left expression
       * @param right
       *          The right expression
       */

      public GASTEBinaryOpGreaterThanOrEqual(
        final GASTExpression left,
        final GASTExpression right)
      {
        super(left, right);
      }

      @Override public
        <A, E extends Throwable, V extends GASTExpressionVisitorType<A, E>>
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
        final String r = builder.toString();
        assert r != null;
        return r;
      }
    }

    /**
     * The type of less-than comparisons.
     */

    @EqualityReference public static final class GASTEBinaryOpLesserThan extends
      GASTEBinaryOp
    {
      /**
       * Construct a binary op expression.
       * 
       * @param left
       *          The left expression
       * @param right
       *          The right expression
       */

      public GASTEBinaryOpLesserThan(
        final GASTExpression left,
        final GASTExpression right)
      {
        super(left, right);
      }

      @Override public
        <A, E extends Throwable, V extends GASTExpressionVisitorType<A, E>>
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
        final String r = builder.toString();
        assert r != null;
        return r;
      }
    }

    /**
     * The type of less-than-or-equal comparisons.
     */

    @EqualityReference public static final class GASTEBinaryOpLesserThanOrEqual extends
      GASTEBinaryOp
    {
      /**
       * Construct a binary op expression.
       * 
       * @param left
       *          The left expression
       * @param right
       *          The right expression
       */

      public GASTEBinaryOpLesserThanOrEqual(
        final GASTExpression left,
        final GASTExpression right)
      {
        super(left, right);
      }

      @Override public
        <A, E extends Throwable, V extends GASTExpressionVisitorType<A, E>>
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
        final String r = builder.toString();
        assert r != null;
        return r;
      }
    }

    /**
     * The type of multiplications.
     */

    @EqualityReference public static final class GASTEBinaryOpMultiply extends
      GASTEBinaryOp
    {
      /**
       * Construct a binary op expression.
       * 
       * @param left
       *          The left expression
       * @param right
       *          The right expression
       */

      public GASTEBinaryOpMultiply(
        final GASTExpression left,
        final GASTExpression right)
      {
        super(left, right);
      }

      @Override public
        <A, E extends Throwable, V extends GASTExpressionVisitorType<A, E>>
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
        final String r = builder.toString();
        assert r != null;
        return r;
      }
    }

    /**
     * The type of additions.
     */

    @EqualityReference public static final class GASTEBinaryOpPlus extends
      GASTEBinaryOp
    {
      /**
       * Construct a binary op expression.
       * 
       * @param left
       *          The left expression
       * @param right
       *          The right expression
       */

      public GASTEBinaryOpPlus(
        final GASTExpression left,
        final GASTExpression right)
      {
        super(left, right);
      }

      @Override public
        <A, E extends Throwable, V extends GASTExpressionVisitorType<A, E>>
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
        final String r = builder.toString();
        assert r != null;
        return r;
      }
    }

    /**
     * The type of subtractions.
     */

    @EqualityReference public static final class GASTEBinaryOpSubtract extends
      GASTEBinaryOp
    {
      /**
       * Construct a binary op expression.
       * 
       * @param left
       *          The left expression
       * @param right
       *          The right expression
       */

      public GASTEBinaryOpSubtract(
        final GASTExpression left,
        final GASTExpression right)
      {
        super(left, right);
      }

      @Override public
        <A, E extends Throwable, V extends GASTExpressionVisitorType<A, E>>
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
        final String r = builder.toString();
        assert r != null;
        return r;
      }
    }

    private final GASTExpression left;
    private final GASTExpression right;

    protected GASTEBinaryOp(
      final GASTExpression in_left,
      final GASTExpression in_right)
    {
      this.left = NullCheck.notNull(in_left, "Left expression");
      this.right = NullCheck.notNull(in_right, "Right expression");
    }

    /**
     * @return The left expression
     */

    public final GASTExpression getLeft()
    {
      return this.left;
    }

    /**
     * @return The right expression
     */

    public final GASTExpression getRight()
    {
      return this.right;
    }
  }

  /**
   * The type of boolean constants.
   */

  @EqualityReference public static final class GASTEBoolean extends
    GASTExpression
  {
    private final boolean value;

    /**
     * Construct a constant.
     * 
     * @param in_value
     *          The value
     */

    public GASTEBoolean(
      final boolean in_value)
    {
      this.value = in_value;
    }

    @Override public
      <A, E extends Throwable, V extends GASTExpressionVisitorType<A, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      return v.expressionBooleanVisit(this);
    }

    /**
     * @return The value
     */

    public boolean getValue()
    {
      return this.value;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[GASTEBoolean ");
      builder.append(this.value);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  /**
   * The type of constructions.
   */

  @EqualityReference public static final class GASTEConstruction extends
    GASTExpression
  {
    private final List<GASTExpression> arguments;
    private final GTypeName            type;

    /**
     * Construct a new value.
     * 
     * @param in_type
     *          The type
     * @param in_arguments
     *          The arguments
     */

    public GASTEConstruction(
      final GTypeName in_type,
      final List<GASTExpression> in_arguments)
    {
      this.type = NullCheck.notNull(in_type, "Type");
      this.arguments = NullCheck.notNull(in_arguments, "Arguments");
    }

    @Override public
      <A, E extends Throwable, V extends GASTExpressionVisitorType<A, E>>
      A
      expressionVisitableAccept(
        final V v)
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

    /**
     * @return The arguments
     */

    public List<GASTExpression> getArguments()
    {
      return this.arguments;
    }

    /**
     * @return The type
     */

    public GTypeName getType()
    {
      return this.type;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[GASTEConstruction ");
      builder.append(this.type);
      builder.append(" ");
      builder.append(this.arguments);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  /**
   * The type of float constants.
   */

  @EqualityReference public static final class GASTEFloat extends
    GASTExpression
  {
    private final BigDecimal value;

    /**
     * Construct a constant.
     * 
     * @param in_value
     *          The value
     */

    public GASTEFloat(
      final BigDecimal in_value)
    {
      this.value = NullCheck.notNull(in_value, "Value");
    }

    @Override public
      <A, E extends Throwable, V extends GASTExpressionVisitorType<A, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      return v.expressionFloatVisit(this);
    }

    /**
     * @return The value
     */

    public BigDecimal getValue()
    {
      return this.value;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[GASTEFloat ");
      builder.append(this.value);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  /**
   * The type of integer constants.
   */

  @EqualityReference public static final class GASTEInteger extends
    GASTExpression
  {
    private final BigInteger value;

    /**
     * Construct a constant.
     * 
     * @param in_value
     *          The value
     */

    public GASTEInteger(
      final BigInteger in_value)
    {
      this.value = NullCheck.notNull(in_value, "Value");
    }

    @Override public
      <A, E extends Throwable, V extends GASTExpressionVisitorType<A, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      return v.expressionIntegerVisit(this);
    }

    /**
     * @return The value
     */

    public BigInteger getValue()
    {
      return this.value;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[GASTEInteger ");
      builder.append(this.value);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  /**
   * The type of record projections.
   */

  @EqualityReference public static final class GASTEProjection extends
    GASTExpression
  {
    private final GASTExpression body;
    private final GFieldName     field;
    private final TType          type;

    /**
     * Construct a record projection.
     * 
     * @param in_body
     *          The left-hand side of the projection
     * @param in_field
     *          The field
     * @param in_type
     *          The type
     */

    public GASTEProjection(
      final GASTExpression in_body,
      final GFieldName in_field,
      final TType in_type)
    {
      this.body = NullCheck.notNull(in_body, "Body");
      this.field = NullCheck.notNull(in_field, "Field");
      this.type = NullCheck.notNull(in_type, "Type");
    }

    @Override public
      <A, E extends Throwable, V extends GASTExpressionVisitorType<A, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      v.expressionProjectionVisitPre(this);
      final A x = this.body.expressionVisitableAccept(v);
      return v.expressionProjectionVisit(x, this);
    }

    /**
     * @return The body
     */

    public GASTExpression getBody()
    {
      return this.body;
    }

    /**
     * @return The field
     */

    public GFieldName getField()
    {
      return this.field;
    }

    /**
     * @return The type
     */

    public TType getType()
    {
      return this.type;
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
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  /**
   * The type of swizzle expressions.
   */

  @EqualityReference public static final class GASTESwizzle extends
    GASTExpression
  {
    private final GASTExpression   body;
    private final List<GFieldName> fields;
    private final TType            type;

    /**
     * Construct a swizzle expression.
     * 
     * @param in_body
     *          The body
     * @param in_fields
     *          The list of fields
     * @param in_type
     *          The resulting type
     */

    public GASTESwizzle(
      final GASTExpression in_body,
      final List<GFieldName> in_fields,
      final TType in_type)
    {
      this.body = NullCheck.notNull(in_body, "Body");
      this.fields = NullCheck.notNull(in_fields, "Fields");
      this.type = NullCheck.notNull(in_type, "Type");
    }

    @Override public
      <A, E extends Throwable, V extends GASTExpressionVisitorType<A, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      v.expressionSwizzleVisitPre(this);
      final A x = this.body.expressionVisitableAccept(v);
      return v.expressionSwizzleVisit(x, this);
    }

    /**
     * @return The expression body
     */

    public GASTExpression getBody()
    {
      return this.body;
    }

    /**
     * @return The list of fields
     */

    public List<GFieldName> getFields()
    {
      return this.fields;
    }

    /**
     * @return The resulting type
     */

    public TType getType()
    {
      return this.type;
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
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  /**
   * The type of unary op expressions.
   */

  @EqualityReference public static abstract class GASTEUnaryOp extends
    GASTExpression
  {
    /**
     * The type of negations.
     */

    @EqualityReference public static final class GASTEUnaryOpNegate extends
      GASTEUnaryOp
    {
      /**
       * Construct a negation.
       * 
       * @param body
       *          The body of the expression
       */

      public GASTEUnaryOpNegate(
        final GASTExpression body)
      {
        super(body);
      }

      @Override public
        <A, E extends Throwable, V extends GASTExpressionVisitorType<A, E>>
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
        final String r = builder.toString();
        assert r != null;
        return r;
      }
    }

    private final GASTExpression body;

    protected GASTEUnaryOp(
      final GASTExpression in_body)
    {
      this.body = NullCheck.notNull(in_body, "Body");
    }

    /**
     * @return The expression body
     */

    public final GASTExpression getBody()
    {
      return this.body;
    }
  }

  /**
   * The type of variables.
   */

  @EqualityReference public static final class GASTEVariable extends
    GASTExpression
  {
    private final GTermName term;
    private final GTypeName type;

    /**
     * Construct a new variable.
     * 
     * @param in_type
     *          The type
     * @param in_term
     *          The term
     */

    public GASTEVariable(
      final GTypeName in_type,
      final GTermName in_term)
    {
      this.type = NullCheck.notNull(in_type, "Type");
      this.term = NullCheck.notNull(in_term, "Term");
    }

    @Override public
      <A, E extends Throwable, V extends GASTExpressionVisitorType<A, E>>
      A
      expressionVisitableAccept(
        final V v)
        throws E
    {
      return v.expressionVariableVisit(this);
    }

    /**
     * @return The variable name
     */

    public GTermName getTerm()
    {
      return this.term;
    }

    /**
     * @return The type name
     */

    public GTypeName getType()
    {
      return this.type;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[GASTEVariable ");
      builder.append(this.type);
      builder.append(" ");
      builder.append(this.term);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  /**
   * Accept a generic visitor.
   * 
   * @param v
   *          The visitor
   * @return The value returned by the visitor
   * @throws E
   *           If the visitor raises <code>E</code>
   */

  public abstract
    <A, E extends Throwable, V extends GASTExpressionVisitorType<A, E>>
    A
    expressionVisitableAccept(
      final V v)
      throws E;
}
