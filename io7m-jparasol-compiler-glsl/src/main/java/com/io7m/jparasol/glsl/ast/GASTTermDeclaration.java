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

import java.util.List;

import javax.annotation.Nonnull;

import com.io7m.jaux.functional.Pair;
import com.io7m.jparasol.glsl.ast.GASTStatement.GASTScope;
import com.io7m.jparasol.glsl.ast.GTermName.GTermNameGlobal;
import com.io7m.jparasol.glsl.ast.GTermName.GTermNameLocal;

public abstract class GASTTermDeclaration implements
  GASTTermDeclarationVisitable
{
  public static final class GASTTermFunction extends GASTTermDeclaration
  {
    private final @Nonnull GTermNameGlobal                       name;
    private final @Nonnull List<Pair<GTermNameLocal, GTypeName>> parameters;
    private final @Nonnull GTypeName                             returns;
    private final @Nonnull GASTScope                             statement;

    public GASTTermFunction(
      final @Nonnull GTermNameGlobal name,
      final @Nonnull GTypeName returns,
      final @Nonnull List<Pair<GTermNameLocal, GTypeName>> parameters,
      final @Nonnull GASTScope statement)
    {
      this.name = name;
      this.returns = returns;
      this.parameters = parameters;
      this.statement = statement;
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
      final GASTTermFunction other = (GASTTermFunction) obj;
      if (!this.name.equals(other.name)) {
        return false;
      }
      if (!this.parameters.equals(other.parameters)) {
        return false;
      }
      if (!this.returns.equals(other.returns)) {
        return false;
      }
      if (!this.statement.equals(other.statement)) {
        return false;
      }
      return true;
    }

    @Override public @Nonnull GTermNameGlobal getName()
    {
      return this.name;
    }

    public @Nonnull List<Pair<GTermNameLocal, GTypeName>> getParameters()
    {
      return this.parameters;
    }

    public @Nonnull GTypeName getReturns()
    {
      return this.returns;
    }

    public @Nonnull GASTScope getStatement()
    {
      return this.statement;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.name.hashCode();
      result = (prime * result) + this.parameters.hashCode();
      result = (prime * result) + this.returns.hashCode();
      result = (prime * result) + this.statement.hashCode();
      return result;
    }

    @Override public
      <A, E extends Throwable, V extends GASTTermDeclarationVisitor<A, E>>
      A
      termDeclarationVisitableAccept(
        final V v)
        throws E
    {
      return v.termVisitFunction(this);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[GASTTermFunction ");
      builder.append(this.name);
      builder.append(" ");
      builder.append(this.returns);
      builder.append(" ");
      builder.append(this.parameters);
      builder.append(" ");
      builder.append(this.statement);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class GASTTermValue extends GASTTermDeclaration
  {
    private final @Nonnull GASTExpression  expression;
    private final @Nonnull GTermNameGlobal name;
    private final @Nonnull GTypeName       type;

    public GASTTermValue(
      final @Nonnull GTermNameGlobal name,
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
      final GASTTermValue other = (GASTTermValue) obj;
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

    @Override public @Nonnull GTermNameGlobal getName()
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
      <A, E extends Throwable, V extends GASTTermDeclarationVisitor<A, E>>
      A
      termDeclarationVisitableAccept(
        final V v)
        throws E
    {
      return v.termVisitValue(this);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[GASTTermValue ");
      builder.append(this.name);
      builder.append(" ");
      builder.append(this.type);
      builder.append(" ");
      builder.append(this.expression);
      builder.append("]");
      return builder.toString();
    }
  }

  public abstract @Nonnull GTermNameGlobal getName();
}
