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

import java.util.List;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jfunctional.Pair;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.glsl.ast.GASTStatement.GASTScope;
import com.io7m.jparasol.glsl.ast.GTermName.GTermNameGlobal;
import com.io7m.jparasol.glsl.ast.GTermName.GTermNameLocal;

/**
 * The type of term declarations.
 */

@EqualityReference public abstract class GASTTermDeclaration
{
  /**
   * The type of function declarations.
   */

  @EqualityReference public static final class GASTTermFunction extends
    GASTTermDeclaration
  {
    private final GTermNameGlobal                       name;
    private final List<Pair<GTermNameLocal, GTypeName>> parameters;
    private final GTypeName                             returns;
    private final GASTScope                             statement;

    /**
     * Construct a function declaration.
     * 
     * @param in_name
     *          The name
     * @param in_returns
     *          The return type
     * @param in_parameters
     *          The parameters
     * @param in_statement
     *          The body of the function
     */

    public GASTTermFunction(
      final GTermNameGlobal in_name,
      final GTypeName in_returns,
      final List<Pair<GTermNameLocal, GTypeName>> in_parameters,
      final GASTScope in_statement)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.returns = NullCheck.notNull(in_returns, "Return");
      this.parameters = NullCheck.notNull(in_parameters, "Parameters");
      this.statement = NullCheck.notNull(in_statement, "Body");
    }

    @Override public GTermNameGlobal getName()
    {
      return this.name;
    }

    /**
     * @return The parameters
     */

    public List<Pair<GTermNameLocal, GTypeName>> getParameters()
    {
      return this.parameters;
    }

    /**
     * @return The return type
     */

    public GTypeName getReturns()
    {
      return this.returns;
    }

    /**
     * @return The body of the function
     */

    public GASTScope getStatement()
    {
      return this.statement;
    }

    @Override public
      <A, E extends Throwable, V extends GASTTermDeclarationVisitorType<A, E>>
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
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  /**
   * The type of value declarations.
   */

  @EqualityReference public static final class GASTTermValue extends
    GASTTermDeclaration
  {
    private final GASTExpression  expression;
    private final GTermNameGlobal name;
    private final GTypeName       type;

    /**
     * Construct a value declaration.
     * 
     * @param in_name
     *          The name
     * @param in_type
     *          The type
     * @param in_expression
     *          The body
     */

    public GASTTermValue(
      final GTermNameGlobal in_name,
      final GTypeName in_type,
      final GASTExpression in_expression)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.type = NullCheck.notNull(in_type, "Type");
      this.expression = NullCheck.notNull(in_expression, "Body");
    }

    /**
     * @return The body
     */

    public GASTExpression getExpression()
    {
      return this.expression;
    }

    @Override public GTermNameGlobal getName()
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
      <A, E extends Throwable, V extends GASTTermDeclarationVisitorType<A, E>>
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
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  /**
   * @return The name of the type
   */

  public abstract GTermNameGlobal getName();

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
    <A, E extends Throwable, V extends GASTTermDeclarationVisitorType<A, E>>
    A
    termDeclarationVisitableAccept(
      final V v)
      throws E;
}
