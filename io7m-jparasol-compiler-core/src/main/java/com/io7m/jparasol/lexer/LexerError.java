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

package com.io7m.jparasol.lexer;

import java.io.File;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;

public final class LexerError extends Exception
{
  private static final long       serialVersionUID;

  static {
    serialVersionUID = 260713896414675714L;
  }

  private final @Nonnull File     file;
  private final @Nonnull Position position;

  public LexerError(
    final @Nonnull Exception cause,
    final @Nonnull File file,
    final @Nonnull Position position)
    throws ConstraintError
  {
    super(cause);
    this.file = Constraints.constrainNotNull(file, "File");
    this.position = Constraints.constrainNotNull(position, "Position");
  }

  public LexerError(
    final @Nonnull Exception cause,
    final @Nonnull String message,
    final @Nonnull File file,
    final @Nonnull Position position)
    throws ConstraintError
  {
    super(message, cause);
    this.file = Constraints.constrainNotNull(file, "File");
    this.position = Constraints.constrainNotNull(position, "Position");
  }

  public LexerError(
    final @Nonnull String message,
    final @Nonnull File file,
    final @Nonnull Position position)
    throws ConstraintError
  {
    super(message);
    this.file = Constraints.constrainNotNull(file, "File");
    this.position = Constraints.constrainNotNull(position, "Position");
  }

  public @Nonnull File getFile()
  {
    return this.file;
  }

  public @Nonnull Position getPosition()
  {
    return this.position;
  }
}
