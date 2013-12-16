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
  public static final class UASTUNameBuiltIn extends UASTUName
  {
    private final @Nonnull TokenIdentifierLower actual;

    public UASTUNameBuiltIn(
      final @Nonnull File file,
      final @Nonnull Position position,
      final @Nonnull TokenIdentifierLower actual)
      throws ConstraintError
    {
      super(file, position);
      this.actual = Constraints.constrainNotNull(actual, "Actual");
    }

    public @Nonnull TokenIdentifierLower getActual()
    {
      return this.actual;
    }
  }

  public static final class UASTUNameGlobal extends UASTUName
  {
    private final @Nonnull TokenIdentifierUpper module;
    private final @Nonnull TokenIdentifierLower name;

    public UASTUNameGlobal(
      final @Nonnull File file,
      final @Nonnull Position position,
      final @Nonnull TokenIdentifierUpper module,
      final @Nonnull TokenIdentifierLower name)
      throws ConstraintError
    {
      super(file, position);
      this.module = Constraints.constrainNotNull(module, "Module");
      this.name = Constraints.constrainNotNull(name, "Name");
    }

    public final @Nonnull TokenIdentifierUpper getModule()
    {
      return this.module;
    }

    public final @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }
  }

  public static final class UASTUNameLocal extends UASTUName
  {
    private final @Nonnull String               name;
    private final @Nonnull TokenIdentifierLower original;

    public UASTUNameLocal(
      final @Nonnull File file,
      final @Nonnull Position position,
      final @Nonnull TokenIdentifierLower original,
      final @Nonnull String name)
      throws ConstraintError
    {
      super(file, position);
      this.original = Constraints.constrainNotNull(original, "Original");
      this.name = Constraints.constrainNotNull(name, "Name");
    }

    public @Nonnull String getName()
    {
      return this.name;
    }

    public @Nonnull TokenIdentifierLower getOriginal()
    {
      return this.original;
    }
  }

  public static final class UASTUNameModuleLevel extends UASTUName
  {
    private final @Nonnull TokenIdentifierLower actual;

    public UASTUNameModuleLevel(
      final @Nonnull File file,
      final @Nonnull Position position,
      final @Nonnull TokenIdentifierLower actual)
      throws ConstraintError
    {
      super(file, position);
      this.actual = Constraints.constrainNotNull(actual, "Actual");
    }

    public @Nonnull TokenIdentifierLower getActual()
    {
      return this.actual;
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
}
