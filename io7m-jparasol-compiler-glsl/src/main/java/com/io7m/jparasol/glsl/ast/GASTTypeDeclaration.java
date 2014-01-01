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

package com.io7m.jparasol.glsl.ast;

import java.util.List;

import javax.annotation.Nonnull;

import com.io7m.jaux.functional.Pair;

public abstract class GASTTypeDeclaration
{
  public static final class GASTTypeRecord extends GASTTypeDeclaration
  {
    private final @Nonnull GTypeName                         name;
    private final @Nonnull List<Pair<GFieldName, GTypeName>> fields;

    public GASTTypeRecord(
      final @Nonnull GTypeName name,
      final @Nonnull List<Pair<GFieldName, GTypeName>> fields)
    {
      this.name = name;
      this.fields = fields;
    }

    public @Nonnull GTypeName getName()
    {
      return this.name;
    }

    public @Nonnull List<Pair<GFieldName, GTypeName>> getFields()
    {
      return this.fields;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.fields.hashCode();
      result = (prime * result) + this.name.hashCode();
      return result;
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
      final GASTTypeRecord other = (GASTTypeRecord) obj;
      if (!this.fields.equals(other.fields)) {
        return false;
      }
      if (!this.name.equals(other.name)) {
        return false;
      }
      return true;
    }
  }
}
