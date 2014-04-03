package com.io7m.jparasol.xml;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;

/**
 * A mapping from a version number to a pair of shaders.
 */

@Immutable public final class CompactedShaders
{
  private final @Nonnull String fragment_shader;
  private final @Nonnull String vertex_shader;

  CompactedShaders(
    final @Nonnull String in_vertex_shader,
    final @Nonnull String in_fragment_shader)
    throws ConstraintError
  {
    this.vertex_shader =
      Constraints.constrainNotNull(in_vertex_shader, "Vertex shader");
    this.fragment_shader =
      Constraints.constrainNotNull(in_fragment_shader, "Fragment shader");
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
    final CompactedShaders other = (CompactedShaders) obj;
    if (!this.fragment_shader.equals(other.fragment_shader)) {
      return false;
    }
    if (!this.vertex_shader.equals(other.vertex_shader)) {
      return false;
    }
    return true;
  }

  public @Nonnull String getFragmentShader()
  {
    return this.fragment_shader;
  }

  public @Nonnull String getVertexShader()
  {
    return this.vertex_shader;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.fragment_shader.hashCode();
    result = (prime * result) + this.vertex_shader.hashCode();
    return result;
  }
}
