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

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.glsl.ast.GTermName.GTermNameLocal;

/**
 * Fragment shader statements.
 */

@EqualityReference public abstract class GASTFragmentShaderStatement
{
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
    <A, E extends Throwable, V extends GASTFragmentShaderStatementVisitorType<A, E>>
    A
    fragmentStatementVisitableAccept(
      final V v)
      throws E;

  /**
   * The type of conditional discard statements.
   */

  @EqualityReference public static final class GASTFragmentConditionalDiscard extends
    GASTFragmentShaderStatement
  {
    private final GASTExpression condition;

    /**
     * Construct a statement.
     * 
     * @param in_condition
     *          The condition expression
     */

    public GASTFragmentConditionalDiscard(
      final GASTExpression in_condition)
    {
      this.condition = NullCheck.notNull(in_condition, "Condition");
    }

    @Override public
      <A, E extends Throwable, V extends GASTFragmentShaderStatementVisitorType<A, E>>
      A
      fragmentStatementVisitableAccept(
        final V v)
        throws E
    {
      return v.fragmentShaderConditionalDiscardVisit(this);
    }

    /**
     * @return The expression
     */

    public GASTExpression getCondition()
    {
      return this.condition;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[GASTFragmentConditionalDiscard ");
      builder.append(this.condition);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  /**
   * The type of local variables.
   */

  @EqualityReference public static final class GASTFragmentLocalVariable extends
    GASTFragmentShaderStatement
  {
    private final GASTExpression expression;
    private final GTermNameLocal name;
    private final GTypeName      type;

    /**
     * Construct a statement.
     * 
     * @param in_name
     *          The name of the variable
     * @param in_type
     *          The type
     * @param in_expression
     *          The expression
     */

    public GASTFragmentLocalVariable(
      final GTermNameLocal in_name,
      final GTypeName in_type,
      final GASTExpression in_expression)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.type = NullCheck.notNull(in_type, "Type");
      this.expression = NullCheck.notNull(in_expression, "Expression");
    }

    @Override public
      <A, E extends Throwable, V extends GASTFragmentShaderStatementVisitorType<A, E>>
      A
      fragmentStatementVisitableAccept(
        final V v)
        throws E
    {
      return v.fragmentShaderLocalVariableVisit(this);
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

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[GASTFragmentLocalVariable ");
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
   * The type of fragment shader output assignments.
   */

  @EqualityReference public static abstract class GASTFragmentOutputAssignment
  {
    private final GShaderOutputName name;
    private final GTermName         value;

    /**
     * Construct an output assignment
     * 
     * @param in_name
     *          The name
     * @param in_value
     *          The value
     */

    public GASTFragmentOutputAssignment(
      final GShaderOutputName in_name,
      final GTermName in_value)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.value = NullCheck.notNull(in_value, "Value");
    }

    /**
     * @return The name
     */

    public final GShaderOutputName getName()
    {
      return this.name;
    }

    /**
     * @return The value
     */

    public final GTermName getValue()
    {
      return this.value;
    }
  }

  /**
   * Specifically does not extend the statement class, in order to prevent
   * interleaving of writes with other statements.
   */

  @EqualityReference public static final class GASTFragmentOutputDataAssignment extends
    GASTFragmentOutputAssignment
  {
    private final int index;

    /**
     * Construct an assignment.
     * 
     * @param name
     *          The name
     * @param in_index
     *          The index
     * @param value
     *          The value
     */

    public GASTFragmentOutputDataAssignment(
      final GShaderOutputName name,
      final int in_index,
      final GTermName value)
    {
      super(name, value);
      this.index = in_index;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[GASTFragmentOutputDataAssignment ");
      builder.append(this.index);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }

    /**
     * @return The output index
     */

    public int getIndex()
    {
      return this.index;
    }
  }

  /**
   * Specifically does not extend the statement class, in order to prevent
   * interleaving of writes with other statements.
   */

  @EqualityReference public static final class GASTFragmentOutputDepthAssignment extends
    GASTFragmentOutputAssignment
  {
    /**
     * Construct an assignment.
     * 
     * @param name
     *          The name
     * @param value
     *          The value
     */

    public GASTFragmentOutputDepthAssignment(
      final GShaderOutputName name,
      final GTermName value)
    {
      super(name, value);
    }
  }
}
