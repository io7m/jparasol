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

import javax.annotation.Nonnull;

import com.io7m.jparasol.glsl.ast.GTermName.GTermNameLocal;

public abstract class GASTFragmentShaderStatement implements
  GASTFragmentShaderStatementVisitable
{
  public static final class GASTFragmentConditionalDiscard extends
    GASTFragmentShaderStatement
  {
    private final @Nonnull GASTExpression condition;

    public GASTFragmentConditionalDiscard(
      final @Nonnull GASTExpression in_condition)
    {
      this.condition = in_condition;
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
      final GASTFragmentShaderStatement.GASTFragmentConditionalDiscard other =
        (GASTFragmentShaderStatement.GASTFragmentConditionalDiscard) obj;
      if (!this.condition.equals(other.condition)) {
        return false;
      }
      return true;
    }

    @Override public
      <A, E extends Throwable, V extends GASTFragmentShaderStatementVisitor<A, E>>
      A
      fragmentStatementVisitableAccept(
        final V v)
        throws E
    {
      return v.fragmentShaderConditionalDiscardVisit(this);
    }

    public @Nonnull GASTExpression getCondition()
    {
      return this.condition;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.condition.hashCode();
      return result;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[GASTFragmentConditionalDiscard ");
      builder.append(this.condition);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class GASTFragmentLocalVariable extends
    GASTFragmentShaderStatement
  {
    private final @Nonnull GASTExpression expression;
    private final @Nonnull GTermNameLocal name;
    private final @Nonnull GTypeName      type;

    public GASTFragmentLocalVariable(
      final @Nonnull GTermNameLocal in_name,
      final @Nonnull GTypeName in_type,
      final @Nonnull GASTExpression in_expression)
    {
      this.name = in_name;
      this.type = in_type;
      this.expression = in_expression;
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
      final GASTFragmentLocalVariable other = (GASTFragmentLocalVariable) obj;
      if (!this.expression.equals(other.expression)) {
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
      <A, E extends Throwable, V extends GASTFragmentShaderStatementVisitor<A, E>>
      A
      fragmentStatementVisitableAccept(
        final V v)
        throws E
    {
      return v.fragmentShaderLocalVariableVisit(this);
    }

    public @Nonnull GASTExpression getExpression()
    {
      return this.expression;
    }

    public @Nonnull GTermNameLocal getName()
    {
      return this.name;
    }

    public @Nonnull GTypeName getType()
    {
      return this.type;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.expression.hashCode();
      result = (prime * result) + this.name.hashCode();
      result = (prime * result) + this.type.hashCode();
      return result;
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
      return builder.toString();
    }
  }

  public static abstract class GASTFragmentOutputAssignment
  {
    private final @Nonnull GShaderOutputName name;
    private final @Nonnull GTermName         value;

    public GASTFragmentOutputAssignment(
      final @Nonnull GShaderOutputName in_name,
      final @Nonnull GTermName in_value)
    {
      this.name = in_name;
      this.value = in_value;
    }

    public @Nonnull GShaderOutputName getName()
    {
      return this.name;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.name.hashCode();
      result = (prime * result) + this.value.hashCode();
      return result;
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
      final GASTFragmentOutputAssignment other =
        (GASTFragmentOutputAssignment) obj;
      return this.name.equals(other.name) && this.value.equals(other.value);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[GASTFragmentOutputAssignment ");
      builder.append(this.name);
      builder.append(" ");
      builder.append(this.value);
      builder.append("]");
      return builder.toString();
    }

    public @Nonnull GTermName getValue()
    {
      return this.value;
    }
  }

  /**
   * Specifically does not extend the statement class, in order to prevent
   * interleaving of writes with other statements.
   */

  public static final class GASTFragmentOutputDataAssignment extends
    GASTFragmentOutputAssignment
  {
    private final int index;

    public GASTFragmentOutputDataAssignment(
      final @Nonnull GShaderOutputName name,
      final int in_index,
      final @Nonnull GTermName value)
    {
      super(name, value);
      this.index = in_index;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = super.hashCode();
      result = (prime * result) + this.index;
      return result;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[GASTFragmentOutputDataAssignment ");
      builder.append(this.index);
      builder.append("]");
      return builder.toString();
    }

    @Override public boolean equals(
      final Object obj)
    {
      if (this == obj) {
        return true;
      }
      if (!super.equals(obj)) {
        return false;
      }
      if (this.getClass() != obj.getClass()) {
        return false;
      }
      final GASTFragmentOutputDataAssignment other =
        (GASTFragmentOutputDataAssignment) obj;
      if (this.index != other.index) {
        return false;
      }
      return true;
    }

    public int getIndex()
    {
      return this.index;
    }
  }

  /**
   * Specifically does not extend the statement class, in order to prevent
   * interleaving of writes with other statements.
   */

  public static final class GASTFragmentOutputDepthAssignment extends
    GASTFragmentOutputAssignment
  {
    public GASTFragmentOutputDepthAssignment(
      final @Nonnull GShaderOutputName name,
      final @Nonnull GTermName value)
    {
      super(name, value);
    }
  }
}
