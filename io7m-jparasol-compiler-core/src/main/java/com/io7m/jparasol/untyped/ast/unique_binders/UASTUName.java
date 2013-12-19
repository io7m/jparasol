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

package com.io7m.jparasol.untyped.ast.unique_binders;

import java.io.File;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.lexer.Position;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.lexer.Token.TokenIdentifierUpper;

public abstract class UASTUName
{
  /**
   * A name that refers to something built-in (such as a built-in name for a
   * shader input or output).
   */

  public static final class UASTUNameBuiltIn extends UASTUName
  {
    private final @Nonnull TokenIdentifierLower actual;

    public UASTUNameBuiltIn(
      final @Nonnull TokenIdentifierLower actual)
      throws ConstraintError
    {
      super(
        Constraints.constrainNotNull(actual, "Actual").getFile(),
        Constraints.constrainNotNull(actual, "Actual").getPosition());
      this.actual = actual;
    }

    public @Nonnull TokenIdentifierLower getActual()
    {
      return this.actual;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTUNameBuiltIn ");
      builder.append(this.actual);
      builder.append("]");
      return builder.toString();
    }

    @Override public String show()
    {
      final StringBuilder s = new StringBuilder();
      s.append("*");
      s.append(this.actual.getActual());
      return s.toString();
    }
  }

  /**
   * A fully qualified name.
   */

  public static final class UASTUNameGlobal extends UASTUName
  {
    private final @Nonnull TokenIdentifierUpper module;
    private final @Nonnull TokenIdentifierLower name;

    public UASTUNameGlobal(
      final @Nonnull TokenIdentifierUpper module,
      final @Nonnull TokenIdentifierLower actual)
      throws ConstraintError
    {
      super(
        Constraints.constrainNotNull(actual, "Actual").getFile(),
        Constraints.constrainNotNull(actual, "Actual").getPosition());
      this.module = Constraints.constrainNotNull(module, "Module");
      this.name = actual;
    }

    public final @Nonnull TokenIdentifierUpper getModule()
    {
      return this.module;
    }

    public final @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTUNameGlobal ");
      builder.append(this.module);
      builder.append(" ");
      builder.append(this.name);
      builder.append("]");
      return builder.toString();
    }

    @Override public String show()
    {
      final StringBuilder s = new StringBuilder();
      s.append("$");
      s.append(this.module.getActual());
      s.append(".");
      s.append(this.name.getActual());
      return s.toString();
    }
  }

  /**
   * A name that refers to a local variable.
   */

  public static final class UASTUNameLocal extends UASTUName
  {
    private final @Nonnull String               name;
    private final @Nonnull TokenIdentifierLower original;

    public UASTUNameLocal(
      final @Nonnull TokenIdentifierLower original,
      final @Nonnull String name)
      throws ConstraintError
    {
      super(
        Constraints.constrainNotNull(original, "Original").getFile(),
        Constraints.constrainNotNull(original, "Original").getPosition());
      this.original = Constraints.constrainNotNull(original, "Original");
      this.name = Constraints.constrainNotNull(name, "Name");
    }

    public @Nonnull String getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTUNameLocal ");
      builder.append(this.name);
      builder.append(" ");
      builder.append(this.original);
      builder.append("]");
      return builder.toString();
    }

    public @Nonnull TokenIdentifierLower getOriginal()
    {
      return this.original;
    }

    @Override public String show()
    {
      final StringBuilder s = new StringBuilder();
      s.append("&");
      s.append(this.name);
      return s.toString();
    }
  }

  /**
   * A name that refers to something at module level in the current module.
   */

  public static final class UASTUNameModuleLevel extends UASTUName
  {
    private final @Nonnull TokenIdentifierLower actual;

    public UASTUNameModuleLevel(
      final @Nonnull TokenIdentifierLower actual)
      throws ConstraintError
    {
      super(
        Constraints.constrainNotNull(actual, "Actual").getFile(),
        Constraints.constrainNotNull(actual, "Actual").getPosition());
      this.actual = actual;
    }

    public @Nonnull TokenIdentifierLower getActual()
    {
      return this.actual;
    }

    @Override public String show()
    {
      final StringBuilder s = new StringBuilder();
      s.append("%");
      s.append(this.actual.getActual());
      return s.toString();
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTUNameModuleLevel ");
      builder.append(this.actual);
      builder.append("]");
      return builder.toString();
    }
  }

  private final @Nonnull File     file;
  private final @Nonnull Position position;

  protected UASTUName(
    final @Nonnull File file,
    final @Nonnull Position position)
    throws ConstraintError
  {
    this.file = Constraints.constrainNotNull(file, "File");
    this.position = Constraints.constrainNotNull(position, "Position");
  }

  public final @Nonnull File getFile()
  {
    return this.file;
  }

  public final @Nonnull Position getPosition()
  {
    return this.position;
  }

  public abstract @Nonnull String show();
}
