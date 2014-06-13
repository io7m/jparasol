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

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.core.JParasolException;
import com.io7m.jparasol.lexer.Position;

/**
 * The base type of compilation error.
 */

@EqualityReference public abstract class CompilerError extends
  JParasolException
{
  private static final long serialVersionUID = -926502044525419698L;
  private final File        file;
  private final Position    position;

  /**
   * Construct a new compiler error.
   * 
   * @param message
   *          The message
   * @param in_file
   *          The file
   * @param in_position
   *          The position
   */

  public CompilerError(
    final String message,
    final File in_file,
    final Position in_position)
  {
    super(message);
    this.file = NullCheck.notNull(in_file, "File");
    this.position = NullCheck.notNull(in_position, "Position");
  }

  /**
   * Construct a new compiler error.
   * 
   * @param x
   *          The cause
   * @param in_file
   *          The file
   * @param in_position
   *          The position
   */

  public CompilerError(
    final Throwable x,
    final File in_file,
    final Position in_position)
  {
    super(x);
    this.file = NullCheck.notNull(in_file, "File");
    this.position = NullCheck.notNull(in_position, "Position");
  }

  /**
   * Construct a new compiler error.
   * 
   * @param x
   *          The cause
   * @param message
   *          The message
   * @param in_file
   *          The file
   * @param in_position
   *          The position
   */

  public CompilerError(
    final Throwable x,
    final String message,
    final File in_file,
    final Position in_position)
  {
    super(message, x);
    this.file = NullCheck.notNull(in_file, "File");
    this.position = NullCheck.notNull(in_position, "Position");
  }

  /**
   * @return The name of the category of error
   */

  public abstract String getCategory();

  /**
   * @return The file from where the error originates
   */

  public final File getFile()
  {
    return this.file;
  }

  /**
   * @return The position in the file from where the error originates
   */

  public final Position getPosition()
  {
    return this.position;
  }
}
