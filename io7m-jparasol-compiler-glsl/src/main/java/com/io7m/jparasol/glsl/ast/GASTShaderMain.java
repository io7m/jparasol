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
import com.io7m.jfunctional.OptionType;
import com.io7m.jparasol.glsl.ast.GASTFragmentShaderStatement.GASTFragmentOutputDataAssignment;
import com.io7m.jparasol.glsl.ast.GASTFragmentShaderStatement.GASTFragmentOutputDepthAssignment;
import com.io7m.jparasol.glsl.ast.GASTVertexShaderStatement.GASTVertexOutputAssignment;

/**
 * A shader main function.
 */

@EqualityReference public abstract class GASTShaderMain
{
  /**
   * A fragment shader main function.
   */

  @EqualityReference public static final class GASTShaderMainFragment extends
    GASTShaderMain
  {
    private final OptionType<GASTFragmentOutputDepthAssignment> depth_write;
    private final List<GASTFragmentShaderStatement>             statements;
    private final List<GASTFragmentOutputDataAssignment>        writes;

    /**
     * Construct a main function.
     *
     * @param in_statements
     *          The list of statements
     * @param in_writes
     *          The list of output writes
     * @param in_depth_write
     *          The depth write, if any
     */

    public GASTShaderMainFragment(
      final List<GASTFragmentShaderStatement> in_statements,
      final List<GASTFragmentOutputDataAssignment> in_writes,
      final OptionType<GASTFragmentOutputDepthAssignment> in_depth_write)
    {
      this.statements = in_statements;
      this.writes = in_writes;
      this.depth_write = in_depth_write;
    }

    /**
     * @return The depth write, if any
     */

    public OptionType<GASTFragmentOutputDepthAssignment> getDepthWrite()
    {
      return this.depth_write;
    }

    /**
     * @return The list of statements
     */

    public List<GASTFragmentShaderStatement> getStatements()
    {
      return this.statements;
    }

    /**
     * @return The list of writes
     */

    public List<GASTFragmentOutputDataAssignment> getWrites()
    {
      return this.writes;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[GASTShaderMainFragment statements=");
      builder.append(this.statements);
      builder.append(" writes=");
      builder.append(this.writes);
      builder.append(" depth_write=");
      builder.append(this.depth_write);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  /**
   * A vertex shader main function.
   */

  @EqualityReference public static final class GASTShaderMainVertex extends
    GASTShaderMain
  {
    private final List<GASTStatementType>          statements;
    private final List<GASTVertexOutputAssignment> writes;

    /**
     * Construct a main function.
     *
     * @param in_statements
     *          The list of statements
     * @param in_writes
     *          The list of output writes
     */

    public GASTShaderMainVertex(
      final List<GASTStatementType> in_statements,
      final List<GASTVertexOutputAssignment> in_writes)
    {
      this.statements = in_statements;
      this.writes = in_writes;
    }

    /**
     * @return The list of statements
     */

    public List<GASTStatementType> getStatements()
    {
      return this.statements;
    }

    /**
     * @return The list of statements
     */

    public List<GASTVertexOutputAssignment> getWrites()
    {
      return this.writes;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[GASTTermVertexMainFunction ");
      builder.append(this.statements);
      builder.append(" ");
      builder.append(this.writes);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }
}
