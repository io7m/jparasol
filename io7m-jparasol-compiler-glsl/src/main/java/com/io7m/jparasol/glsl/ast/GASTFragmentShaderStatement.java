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
      final @Nonnull GASTExpression condition)
    {
      this.condition = condition;
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
      final @Nonnull GTermNameLocal name,
      final @Nonnull GTypeName type,
      final @Nonnull GASTExpression expression)
    {
      this.name = name;
      this.type = type;
      this.expression = expression;
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

  /**
   * Specifically does not extend the statement class, in order to prevent
   * interleaving of writes with other statements.
   */

  public static final class GASTFragmentOutputAssignment
  {
    private final int                        index;
    private final @Nonnull GShaderOutputName name;
    private final @Nonnull GTermName         value;

    public GASTFragmentOutputAssignment(
      final @Nonnull GShaderOutputName name,
      final int index,
      final @Nonnull GTermName value)
    {
      this.name = name;
      this.index = index;
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
      final GASTFragmentShaderStatement.GASTFragmentOutputAssignment other =
        (GASTFragmentShaderStatement.GASTFragmentOutputAssignment) obj;
      if (this.index != other.index) {
        return false;
      }
      if (!this.name.equals(other.name)) {
        return false;
      }
      if (!this.value.equals(other.value)) {
        return false;
      }
      return true;
    }

    public int getIndex()
    {
      return this.index;
    }

    public @Nonnull GShaderOutputName getName()
    {
      return this.name;
    }

    public @Nonnull GTermName getValue()
    {
      return this.value;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.index;
      result = (prime * result) + this.name.hashCode();
      result = (prime * result) + this.value.hashCode();
      return result;
    }
  }
}
