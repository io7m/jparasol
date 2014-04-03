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

package com.io7m.jparasol.glsl.ast;

import javax.annotation.Nonnull;

import com.io7m.jparasol.NameShow;

public abstract class GTermName implements GTermNameVisitable, NameShow
{
  public static final class GTermNameExternal extends GTermName
  {
    private final @Nonnull String name;

    public GTermNameExternal(
      final @Nonnull String in_name)
    {
      this.name = in_name;
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
      final GTermNameExternal other = (GTermNameExternal) obj;
      return this.name.equals(other.name);
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.name.hashCode();
      return result;
    }

    @Override public @Nonnull String show()
    {
      return this.name;
    }

    @Override public
      <A, E extends Throwable, V extends GTermNameVisitor<A, E>>
      A
      termNameVisitableAccept(
        final V v)
        throws E
    {
      return v.termNameVisitExternal(this);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[GTermNameExternal ");
      builder.append(this.name);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class GTermNameGlobal extends GTermName
  {
    private final @Nonnull String name;

    public GTermNameGlobal(
      final @Nonnull String in_name)
    {
      this.name = in_name;
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
      final GTermNameGlobal other = (GTermNameGlobal) obj;
      return this.name.equals(other.name);
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.name.hashCode();
      return result;
    }

    @Override public @Nonnull String show()
    {
      return this.name;
    }

    @Override public
      <A, E extends Throwable, V extends GTermNameVisitor<A, E>>
      A
      termNameVisitableAccept(
        final V v)
        throws E
    {
      return v.termNameVisitGlobal(this);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[GTermNameGlobal ");
      builder.append(this.name);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class GTermNameLocal extends GTermName
  {
    private final @Nonnull String name;

    public GTermNameLocal(
      final @Nonnull String in_name)
    {
      this.name = in_name;
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
      final GTermNameLocal other = (GTermNameLocal) obj;
      return this.name.equals(other.name);
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.name.hashCode();
      return result;
    }

    @Override public String show()
    {
      return this.name;
    }

    @Override public
      <A, E extends Throwable, V extends GTermNameVisitor<A, E>>
      A
      termNameVisitableAccept(
        final V v)
        throws E
    {
      return v.termNameVisitLocal(this);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[GTermNameLocal ");
      builder.append(this.name);
      builder.append("]");
      return builder.toString();
    }
  }
}
