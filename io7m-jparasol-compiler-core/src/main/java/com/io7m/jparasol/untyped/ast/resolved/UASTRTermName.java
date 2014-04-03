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

package com.io7m.jparasol.untyped.ast.resolved;

import java.io.File;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.ModulePath;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.lexer.Position;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;

public abstract class UASTRTermName implements UASTRTermNameVisitable
{
  /**
   * A fully qualified name.
   */

  public static final class UASTRTermNameGlobal extends UASTRTermName
  {
    private final @Nonnull ModulePathFlat       flat;
    private final @Nonnull TokenIdentifierLower name;
    private final @Nonnull ModulePath           path;

    public UASTRTermNameGlobal(
      final @Nonnull ModulePath in_path,
      final @Nonnull TokenIdentifierLower actual)
      throws ConstraintError
    {
      super(
        Constraints.constrainNotNull(actual, "Actual").getFile(),
        Constraints.constrainNotNull(actual, "Actual").getPosition());
      this.path = Constraints.constrainNotNull(in_path, "Path");
      this.flat = ModulePathFlat.fromModulePath(in_path);
      this.name = actual;
    }

    public @Nonnull ModulePathFlat getFlat()
    {
      return this.flat;
    }

    @Override public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    public @Nonnull ModulePath getPath()
    {
      return this.path;
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
      <A, E extends Throwable, V extends UASTRTermNameVisitor<A, E>>
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
      builder.append("[UASTRNameGlobal ");
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

  public static final class UASTRTermNameLocal extends UASTRTermName
  {
    private final @Nonnull String               current;
    private final @Nonnull TokenIdentifierLower original;

    public UASTRTermNameLocal(
      final @Nonnull TokenIdentifierLower in_original,
      final @Nonnull String in_current)
      throws ConstraintError
    {
      super(
        Constraints.constrainNotNull(in_original, "Original").getFile(),
        Constraints.constrainNotNull(in_original, "Original").getPosition());
      this.original = in_original;
      this.current = Constraints.constrainNotNull(in_current, "Current");
    }

    public @Nonnull String getCurrent()
    {
      return this.current;
    }

    @Override public TokenIdentifierLower getName()
    {
      return this.original;
    }

    public @Nonnull TokenIdentifierLower getOriginal()
    {
      return this.original;
    }

    @Override public String show()
    {
      final StringBuilder s = new StringBuilder();
      s.append("&");
      s.append(this.current);
      return s.toString();
    }

    @Override public
      <A, E extends Throwable, V extends UASTRTermNameVisitor<A, E>>
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
      builder.append("[UASTRNameLocal ");
      builder.append(this.current);
      builder.append(" ");
      builder.append(this.original);
      builder.append("]");
      return builder.toString();
    }
  }

  private final @Nonnull File     file;
  private final @Nonnull Position position;

  protected UASTRTermName(
    final @Nonnull File in_file,
    final @Nonnull Position in_position)
    throws ConstraintError
  {
    this.file = Constraints.constrainNotNull(in_file, "File");
    this.position = Constraints.constrainNotNull(in_position, "Position");
  }

  public final @Nonnull File getFile()
  {
    return this.file;
  }

  public abstract @Nonnull TokenIdentifierLower getName();

  public final @Nonnull Position getPosition()
  {
    return this.position;
  }

  public abstract @Nonnull String show();
}
