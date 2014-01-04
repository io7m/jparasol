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

package com.io7m.jparasol;

import java.io.File;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.lexer.Position;

public abstract class CompilerError extends Exception
{
  private static final long       serialVersionUID = -926502044525419698L;
  private final @Nonnull File     file;
  private final @Nonnull Position position;

  public CompilerError(
    final @Nonnull File file,
    final @Nonnull Position position)
    throws ConstraintError
  {
    this.file = Constraints.constrainNotNull(file, "File");
    this.position = Constraints.constrainNotNull(position, "Position");
  }

  public CompilerError(
    final @Nonnull String message,
    final @Nonnull File file,
    final @Nonnull Position position)
    throws ConstraintError
  {
    super(message);
    this.file = Constraints.constrainNotNull(file, "File");
    this.position = Constraints.constrainNotNull(position, "Position");
  }

  public CompilerError(
    final @Nonnull Throwable x,
    final @Nonnull File file,
    final @Nonnull Position position)
    throws ConstraintError
  {
    super(x);
    this.file = Constraints.constrainNotNull(file, "File");
    this.position = Constraints.constrainNotNull(position, "Position");
  }

  public CompilerError(
    final @Nonnull Throwable x,
    final @Nonnull String message,
    final @Nonnull File file,
    final @Nonnull Position position)
    throws ConstraintError
  {
    super(message, x);
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

  public abstract @Nonnull String getCategory();
}
