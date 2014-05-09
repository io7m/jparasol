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

import java.util.ArrayList;
import java.util.List;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.glsl.ast.GTermName.GTermNameLocal;

/**
 * The type of GLSL statements.
 */

@EqualityReference public abstract class GASTStatement
{
  /**
   * A conditional statement.
   */

  @EqualityReference public static final class GASTConditional extends
    GASTStatement
  {
    private final GASTExpression condition;
    private final GASTStatement  left;
    private final GASTStatement  right;

    /**
     * Construct a statement.
     * 
     * @param in_condition
     *          The condition
     * @param in_left
     *          The left branch
     * @param in_right
     *          The right branch
     */

    public GASTConditional(
      final GASTExpression in_condition,
      final GASTStatement in_left,
      final GASTStatement in_right)
    {
      this.condition = NullCheck.notNull(in_condition, "Condition");
      this.left = NullCheck.notNull(in_left, "Left branch");
      this.right = NullCheck.notNull(in_right, "Right branch");
    }

    /**
     * @return The condition
     */

    public GASTExpression getCondition()
    {
      return this.condition;
    }

    /**
     * @return The left branch
     */

    public GASTStatement getLeft()
    {
      return this.left;
    }

    /**
     * @return The right branch
     */

    public GASTStatement getRight()
    {
      return this.right;
    }

    @Override public
      <A, E extends Throwable, V extends GASTStatementVisitorType<A, E>>
      A
      statementVisitableAccept(
        final V v)
        throws E
    {
      v.statementVisitConditionalPre(this);

      v.statementVisitConditionalLeftPre(this);
      final A l = this.left.statementVisitableAccept(v);
      v.statementVisitConditionalLeftPost(this);

      v.statementVisitConditionalRightPre(this);
      final A r = this.right.statementVisitableAccept(v);
      v.statementVisitConditionalRightPost(this);

      return v.statementVisitConditional(l, r, this);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[GASTConditional ");
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

  /**
   * A local variable.
   */

  @EqualityReference public static final class GASTLocalVariable extends
    GASTStatement
  {
    private final GASTExpression expression;
    private final GTermNameLocal name;
    private final GTypeName      type;

    /**
     * Construct a statement.
     * 
     * @param in_name
     *          The name
     * @param in_type
     *          The type name
     * @param in_expression
     *          The body
     */

    public GASTLocalVariable(
      final GTermNameLocal in_name,
      final GTypeName in_type,
      final GASTExpression in_expression)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.type = NullCheck.notNull(in_type, "Type");
      this.expression = NullCheck.notNull(in_expression, "Expression");
    }

    /**
     * @return The expression
     */

    public GASTExpression getExpression()
    {
      return this.expression;
    }

    /**
     * @return The name
     */

    public GTermNameLocal getName()
    {
      return this.name;
    }

    /**
     * @return The type
     */

    public GTypeName getType()
    {
      return this.type;
    }

    @Override public
      <A, E extends Throwable, V extends GASTStatementVisitorType<A, E>>
      A
      statementVisitableAccept(
        final V v)
        throws E
    {
      return v.statementVisitLocalVariable(this);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[GASTLocalVariable ");
      builder.append(this.name);
      builder.append(" ");
      builder.append(this.type);
      builder.append(" ");
      builder.append(this.expression);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }

  }

  /**
   * A return statement.
   */

  @EqualityReference public static final class GASTReturn extends
    GASTStatement
  {
    private final GASTExpression expression;

    /**
     * Construct a statement.
     * 
     * @param in_expression
     *          The body
     */

    public GASTReturn(
      final GASTExpression in_expression)
    {
      this.expression = NullCheck.notNull(in_expression, "Expression");
    }

    /**
     * @return The expression
     */

    public GASTExpression getExpression()
    {
      return this.expression;
    }

    @Override public
      <A, E extends Throwable, V extends GASTStatementVisitorType<A, E>>
      A
      statementVisitableAccept(
        final V v)
        throws E
    {
      return v.statementVisitReturn(this);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[GASTReturn ");
      builder.append(this.expression);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  /**
   * An explicit scope.
   */

  @EqualityReference public static final class GASTScope extends
    GASTStatement
  {
    private final List<GASTStatement> statements;

    /**
     * Construct a scope.
     * 
     * @param in_statements
     *          A list of statements
     */

    public GASTScope(
      final List<GASTStatement> in_statements)
    {
      this.statements = NullCheck.notNull(in_statements, "Statements");
    }

    /**
     * @return A list of statements
     */

    public List<GASTStatement> getStatements()
    {
      return this.statements;
    }

    @Override public
      <A, E extends Throwable, V extends GASTStatementVisitorType<A, E>>
      A
      statementVisitableAccept(
        final V v)
        throws E
    {
      v.statementVisitScopePre(this);
      final List<A> xs = new ArrayList<A>();
      for (final GASTStatement s : this.statements) {
        xs.add(s.statementVisitableAccept(v));
      }
      return v.statementVisitScope(xs, this);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[GASTScope ");
      builder.append(this.statements);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  /**
   * A vertex shader statement.
   */

  @EqualityReference public static abstract class GASTVertexShaderStatement
  {
    /**
     * An output assignment.
     */

    @EqualityReference public static final class GASTVertexOutputAssignment extends
      GASTVertexShaderStatement
    {
      private final GShaderOutputName name;
      private final GTermName         value;

      /**
       * Construct a statement.
       * 
       * @param in_name
       *          The name
       * @param in_value
       *          The value
       */

      public GASTVertexOutputAssignment(
        final GShaderOutputName in_name,
        final GTermName in_value)
      {
        this.name = NullCheck.notNull(in_name, "Name");
        this.value = NullCheck.notNull(in_value, "Value");
      }

      /**
       * @return The output name
       */

      public GShaderOutputName getName()
      {
        return this.name;
      }

      /**
       * @return The value
       */

      public GTermName getValue()
      {
        return this.value;
      }

      @Override public String toString()
      {
        final StringBuilder builder = new StringBuilder();
        builder.append("[GASTVertexOutputAssignment ");
        builder.append(this.name);
        builder.append(" ");
        builder.append(this.value);
        builder.append("]");
        final String r = builder.toString();
        assert r != null;
        return r;
      }
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
    <A, E extends Throwable, V extends GASTStatementVisitorType<A, E>>
    A
    statementVisitableAccept(
      final V v)
      throws E;
}
