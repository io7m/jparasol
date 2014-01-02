package com.io7m.jparasol.glsl.ast;

import java.util.List;

import javax.annotation.Nonnull;

import com.io7m.jparasol.glsl.ast.GASTFragmentShaderStatement.GASTFragmentOutputAssignment;
import com.io7m.jparasol.glsl.ast.GASTStatement.GASTVertexShaderStatement.GASTVertexOutputAssignment;

public abstract class GASTShaderMain
{
  public static final class GASTShaderMainFragment extends GASTShaderMain
  {
    private final @Nonnull List<GASTFragmentShaderStatement>  statements;
    private final @Nonnull List<GASTFragmentOutputAssignment> writes;

    public GASTShaderMainFragment(
      final @Nonnull List<GASTFragmentShaderStatement> statements,
      final @Nonnull List<GASTFragmentOutputAssignment> writes)
    {
      this.statements = statements;
      this.writes = writes;
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
      final GASTShaderMainFragment other = (GASTShaderMainFragment) obj;
      if (!this.statements.equals(other.statements)) {
        return false;
      }
      if (!this.writes.equals(other.writes)) {
        return false;
      }
      return true;
    }

    public @Nonnull List<GASTFragmentShaderStatement> getStatements()
    {
      return this.statements;
    }

    public @Nonnull List<GASTFragmentOutputAssignment> getWrites()
    {
      return this.writes;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.statements.hashCode();
      result = (prime * result) + this.writes.hashCode();
      return result;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[GASTTermFragmentMainFunction ");
      builder.append(this.statements);
      builder.append(" ");
      builder.append(this.writes);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class GASTShaderMainVertex extends GASTShaderMain
  {
    private final @Nonnull List<GASTStatement>              statements;
    private final @Nonnull List<GASTVertexOutputAssignment> writes;

    public GASTShaderMainVertex(
      final @Nonnull List<GASTStatement> statements,
      final @Nonnull List<GASTVertexOutputAssignment> writes)
    {
      this.statements = statements;
      this.writes = writes;
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
      final GASTShaderMainVertex other = (GASTShaderMainVertex) obj;
      if (!this.statements.equals(other.statements)) {
        return false;
      }
      if (!this.writes.equals(other.writes)) {
        return false;
      }
      return true;
    }

    public @Nonnull List<GASTStatement> getStatements()
    {
      return this.statements;
    }

    public @Nonnull List<GASTVertexOutputAssignment> getWrites()
    {
      return this.writes;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.statements.hashCode();
      result = (prime * result) + this.writes.hashCode();
      return result;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[GASTTermVertexMainFunction ");
      builder.append(this.statements);
      builder.append(" ");
      builder.append(this.writes);
      builder.append("]");
      return builder.toString();
    }
  }
}
