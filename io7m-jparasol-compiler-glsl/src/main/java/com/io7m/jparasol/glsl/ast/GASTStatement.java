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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.io7m.jparasol.glsl.ast.GTermName.GTermNameLocal;

public abstract class GASTStatement implements GASTStatementVisitable
{
  public static final class GASTConditional extends GASTStatement
  {
    private final @Nonnull GASTExpression condition;
    private final @Nonnull GASTStatement  left;
    private final @Nonnull GASTStatement  right;

    public GASTConditional(
      final @Nonnull GASTExpression condition,
      final @Nonnull GASTStatement left,
      final @Nonnull GASTStatement right)
    {
      this.condition = condition;
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
      final GASTConditional other = (GASTConditional) obj;
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

    public @Nonnull GASTExpression getCondition()
    {
      return this.condition;
    }

    public @Nonnull GASTStatement getLeft()
    {
      return this.left;
    }

    public @Nonnull GASTStatement getRight()
    {
      return this.right;
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

    @Override public
      <A, E extends Throwable, V extends GASTStatementVisitor<A, E>>
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
      return builder.toString();
    }
  }

  public static final class GASTLocalVariable extends GASTStatement
  {
    private final @Nonnull GASTExpression expression;
    private final @Nonnull GTermNameLocal name;
    private final @Nonnull GTypeName      type;

    public GASTLocalVariable(
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
      final GASTLocalVariable other = (GASTLocalVariable) obj;
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

    @Override public
      <A, E extends Throwable, V extends GASTStatementVisitor<A, E>>
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
      return builder.toString();
    }

  }

  public static final class GASTReturn extends GASTStatement
  {
    private final @Nonnull GASTExpression expression;

    public GASTReturn(
      final @Nonnull GASTExpression expression)
    {
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
      final GASTReturn other = (GASTReturn) obj;
      if (!this.expression.equals(other.expression)) {
        return false;
      }
      return true;
    }

    public @Nonnull GASTExpression getExpression()
    {
      return this.expression;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.expression.hashCode();
      return result;
    }

    @Override public
      <A, E extends Throwable, V extends GASTStatementVisitor<A, E>>
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
      return builder.toString();
    }
  }

  public static final class GASTScope extends GASTStatement
  {
    private final @Nonnull List<GASTStatement> statements;

    public GASTScope(
      final @Nonnull List<GASTStatement> statements)
    {
      this.statements = statements;
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
      final GASTScope other = (GASTScope) obj;
      if (!this.statements.equals(other.statements)) {
        return false;
      }
      return true;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.statements.hashCode();
      return result;
    }

    @Override public
      <A, E extends Throwable, V extends GASTStatementVisitor<A, E>>
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
      return builder.toString();
    }
  }

  public static abstract class GASTVertexShaderStatement
  {
    public static final class GASTVertexOutputAssignment extends
      GASTVertexShaderStatement
    {
      private final @Nonnull GShaderOutputName name;
      private final @Nonnull GTermName         value;

      public GASTVertexOutputAssignment(
        final @Nonnull GShaderOutputName name,
        final @Nonnull GTermName value)
      {
        this.name = name;
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
        final GASTVertexOutputAssignment other =
          (GASTVertexOutputAssignment) obj;
        if (!this.name.equals(other.name)) {
          return false;
        }
        if (!this.value.equals(other.value)) {
          return false;
        }
        return true;
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
        result = (prime * result) + this.name.hashCode();
        result = (prime * result) + this.value.hashCode();
        return result;
      }

      @Override public String toString()
      {
        final StringBuilder builder = new StringBuilder();
        builder.append("[GASTVertexOutputAssignment ");
        builder.append(this.name);
        builder.append(" ");
        builder.append(this.value);
        builder.append("]");
        return builder.toString();
      }
    }
  }
}
