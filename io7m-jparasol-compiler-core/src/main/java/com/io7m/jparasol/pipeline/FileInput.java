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

package com.io7m.jparasol.pipeline;

import java.io.File;
import java.io.InputStream;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;

public final class FileInput implements Input
{
  private final @Nonnull File        file;
  private final boolean              internal;
  private final @Nonnull InputStream stream;

  @Override public @Nonnull File getFile()
  {
    return this.file;
  }

  @Override public boolean isInternal()
  {
    return this.internal;
  }

  @Override public @Nonnull InputStream getStream()
  {
    return this.stream;
  }

  public FileInput(
    final boolean internal,
    final @Nonnull File file,
    final @Nonnull InputStream stream)
    throws ConstraintError
  {
    this.internal = internal;
    this.file = Constraints.constrainNotNull(file, "File");
    this.stream = Constraints.constrainNotNull(stream, "Stream");
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
    final FileInput other = (FileInput) obj;
    if (!this.file.equals(other.file)) {
      return false;
    }
    if (this.internal != other.internal) {
      return false;
    }
    if (!this.stream.equals(other.stream)) {
      return false;
    }
    return true;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.file.hashCode();
    result = (prime * result) + (this.internal ? 1231 : 1237);
    result = (prime * result) + this.stream.hashCode();
    return result;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[Input ");
    builder.append(this.internal);
    builder.append(" ");
    builder.append(this.file);
    builder.append(" ");
    builder.append(this.stream);
    builder.append("]");
    return builder.toString();
  }
}
