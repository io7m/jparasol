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

import com.io7m.jequality.annotations.EqualityStructural;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;

/**
 * An input based on a file.
 */

@EqualityStructural public final class FileInput implements InputType
{
  private final File        file;
  private final boolean     internal;
  private final InputStream stream;

  /**
   * Construct a new file input.
   * 
   * @param in_internal
   *          <code>true</code> if the unit to be parsed is "internal"
   *          (standard library)
   * @param in_file
   *          The file name
   * @param in_stream
   *          An open stream for the file
   */

  public FileInput(
    final boolean in_internal,
    final File in_file,
    final InputStream in_stream)
  {
    this.internal = in_internal;
    this.file = NullCheck.notNull(in_file, "File");
    this.stream = NullCheck.notNull(in_stream, "Stream");
  }

  @Override public boolean equals(
    final @Nullable Object obj)
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

  @Override public File getFile()
  {
    return this.file;
  }

  @Override public InputStream getStream()
  {
    return this.stream;
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

  @Override public boolean isInternal()
  {
    return this.internal;
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
    final String r = builder.toString();
    assert r != null;
    return r;
  }
}
