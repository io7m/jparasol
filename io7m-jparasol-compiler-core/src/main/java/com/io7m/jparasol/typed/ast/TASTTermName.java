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

package com.io7m.jparasol.typed.ast;

import java.io.File;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.ModulePath;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.lexer.Position;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;

public abstract class TASTTermName implements TASTTermNameVisitable
{
  /**
   * A name that refers to something built-in (such as a built-in name for a
   * shader input or output).
   */

  public static final class TASTTermNameBuiltIn extends TASTTermName
  {
    private final @Nonnull TokenIdentifierLower actual;

    public TASTTermNameBuiltIn(
      final @Nonnull TokenIdentifierLower actual)
      throws ConstraintError
    {
      super(
        Constraints.constrainNotNull(actual, "Actual").getFile(),
        Constraints.constrainNotNull(actual, "Actual").getPosition());
      this.actual = actual;
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
      final TASTTermNameBuiltIn other = (TASTTermNameBuiltIn) obj;
      if (!this.actual.equals(other.actual)) {
        return false;
      }
      return true;
    }

    public @Nonnull TokenIdentifierLower getActual()
    {
      return this.actual;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.actual.hashCode();
      return result;
    }

    @Override public String show()
    {
      final StringBuilder s = new StringBuilder();
      s.append("*");
      s.append(this.actual.getActual());
      return s.toString();
    }

    @Override public
      <A, E extends Throwable, V extends TASTTermNameVisitor<A, E>>
      A
      termNameVisitableAccept(
        final @Nonnull V v)
        throws ConstraintError,
          E
    {
      return v.termNameVisitBuiltIn(this);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTNameBuiltIn ");
      builder.append(this.actual);
      builder.append("]");
      return builder.toString();
    }
  }

  /**
   * A fully qualified name.
   */

  public static final class TASTTermNameGlobal extends TASTTermName
  {
    private final @Nonnull ModulePathFlat       flat;
    private final @Nonnull TokenIdentifierLower name;
    private final @Nonnull ModulePath           path;

    public TASTTermNameGlobal(
      final @Nonnull ModulePath path,
      final @Nonnull TokenIdentifierLower actual)
      throws ConstraintError
    {
      super(
        Constraints.constrainNotNull(actual, "Actual").getFile(),
        Constraints.constrainNotNull(actual, "Actual").getPosition());
      this.path = Constraints.constrainNotNull(path, "Path");
      this.flat = ModulePathFlat.fromModulePath(path);
      this.name = actual;
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
      final TASTTermNameGlobal other = (TASTTermNameGlobal) obj;
      if (!this.flat.equals(other.flat)) {
        return false;
      }
      if (!this.name.equals(other.name)) {
        return false;
      }
      if (!this.path.equals(other.path)) {
        return false;
      }
      return true;
    }

    public @Nonnull ModulePathFlat getFlat()
    {
      return this.flat;
    }

    public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    public @Nonnull ModulePath getPath()
    {
      return this.path;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.flat.hashCode();
      result = (prime * result) + this.name.hashCode();
      result = (prime * result) + this.path.hashCode();
      return result;
    }

    @Override public String show()
    {
      final StringBuilder s = new StringBuilder();
      s.append("$");
      s.append(this.flat.getActual());
      s.append(".");
      s.append(this.name.getActual());
      return s.toString();
    }

    @Override public
      <A, E extends Throwable, V extends TASTTermNameVisitor<A, E>>
      A
      termNameVisitableAccept(
        final @Nonnull V v)
        throws ConstraintError,
          E
    {
      return v.termNameVisitGlobal(this);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTNameGlobal ");
      builder.append(this.path);
      builder.append(" ");
      builder.append(this.name);
      builder.append("]");
      return builder.toString();
    }
  }

  /**
   * A name that refers to a local variable.
   */

  public static final class TASTTermNameLocal extends TASTTermName
  {
    private final @Nonnull String               current;
    private final @Nonnull TokenIdentifierLower original;

    public TASTTermNameLocal(
      final @Nonnull TokenIdentifierLower original,
      final @Nonnull String current)
      throws ConstraintError
    {
      super(
        Constraints.constrainNotNull(original, "Original").getFile(),
        Constraints.constrainNotNull(original, "Original").getPosition());
      this.original = original;
      this.current = Constraints.constrainNotNull(current, "Current");
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
      final TASTTermNameLocal other = (TASTTermNameLocal) obj;
      if (!this.current.equals(other.current)) {
        return false;
      }
      if (!this.original.equals(other.original)) {
        return false;
      }
      return true;
    }

    public @Nonnull String getCurrent()
    {
      return this.current;
    }

    public @Nonnull TokenIdentifierLower getOriginal()
    {
      return this.original;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.current.hashCode();
      result = (prime * result) + this.original.hashCode();
      return result;
    }

    @Override public String show()
    {
      final StringBuilder s = new StringBuilder();
      s.append("&");
      s.append(this.current);
      return s.toString();
    }

    @Override public
      <A, E extends Throwable, V extends TASTTermNameVisitor<A, E>>
      A
      termNameVisitableAccept(
        final @Nonnull V v)
        throws ConstraintError,
          E
    {
      return v.termNameVisitLocal(this);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTNameLocal ");
      builder.append(this.current);
      builder.append(" ");
      builder.append(this.original);
      builder.append("]");
      return builder.toString();
    }
  }

  private final @Nonnull File     file;
  private final @Nonnull Position position;

  protected TASTTermName(
    final @Nonnull File file,
    final @Nonnull Position position)
    throws ConstraintError
  {
    this.file = Constraints.constrainNotNull(file, "File");
    this.position = Constraints.constrainNotNull(position, "Position");
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
    final TASTTermName other = (TASTTermName) obj;
    if (!this.file.equals(other.file)) {
      return false;
    }
    if (!this.position.equals(other.position)) {
      return false;
    }
    return true;
  }

  public final @Nonnull File getFile()
  {
    return this.file;
  }

  public final @Nonnull Position getPosition()
  {
    return this.position;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.file.hashCode();
    result = (prime * result) + this.position.hashCode();
    return result;
  }

  public abstract @Nonnull String show();
}