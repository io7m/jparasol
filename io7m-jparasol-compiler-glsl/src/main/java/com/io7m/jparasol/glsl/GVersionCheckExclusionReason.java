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

package com.io7m.jparasol.glsl;

import java.io.File;

import com.io7m.jequality.annotations.EqualityStructural;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jparasol.lexer.Position;

/**
 * The reason a particular GLSL version was excluded.
 */

@EqualityStructural public final class GVersionCheckExclusionReason
{
  private final File     file;
  private final String   message;
  private final Position position;

  /**
   * Construct an exclusion reason.
   * 
   * @param in_file
   *          The file
   * @param in_position
   *          The position in the file
   * @param in_message
   *          The exclusion reason
   */

  public GVersionCheckExclusionReason(
    final File in_file,
    final Position in_position,
    final String in_message)
  {
    this.file = NullCheck.notNull(in_file, "File");
    this.position = NullCheck.notNull(in_position, "Position");
    this.message = NullCheck.notNull(in_message, "Message");
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
    final GVersionCheckExclusionReason other =
      (GVersionCheckExclusionReason) obj;
    if (!this.file.equals(other.file)) {
      return false;
    }
    if (!this.message.equals(other.message)) {
      return false;
    }
    if (!this.position.equals(other.position)) {
      return false;
    }
    return true;
  }

  /**
   * @return The file
   */

  public File getFile()
  {
    return this.file;
  }

  /**
   * @return The message
   */

  public String getMessage()
  {
    return this.message;
  }

  /**
   * @return The position
   */

  public Position getPosition()
  {
    return this.position;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.file.hashCode();
    result = (prime * result) + this.message.hashCode();
    result = (prime * result) + this.position.hashCode();
    return result;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[GVersionCheckExclusionReason ");
    builder.append(this.file);
    builder.append(":");
    builder.append(this.position);
    builder.append(" ");
    builder.append(this.message);
    builder.append("]");
    final String r = builder.toString();
    assert r != null;
    return r;
  }
}
