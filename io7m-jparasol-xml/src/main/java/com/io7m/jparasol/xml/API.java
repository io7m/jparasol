package com.io7m.jparasol.xml;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;

public enum API
{
  API_GLSL("glsl"),
  API_GLSL_ES("glsl-es");

  final @Nonnull String name;

  private API(
    final @Nonnull String name)
  {
    this.name = name;
  }

  @Override public @Nonnull String toString()
  {
    return this.name;
  }

  public static @Nonnull API fromString(
    final @Nonnull String value)
    throws ConstraintError
  {
    Constraints.constrainNotNull(value, "Value");
    if (value.equals("glsl")) {
      return API_GLSL;
    }
    if (value.equals("glsl-es")) {
      return API_GLSL_ES;
    }

    throw new UnreachableCodeException();
  }
}
