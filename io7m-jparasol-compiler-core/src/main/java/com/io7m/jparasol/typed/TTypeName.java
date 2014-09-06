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

package com.io7m.jparasol.typed;

import com.io7m.jequality.annotations.EqualityStructural;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jparasol.ModulePath;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.NameShowType;
import com.io7m.jparasol.lexer.TokenIdentifierLower;

/**
 * The type of type names.
 */

@EqualityStructural public abstract class TTypeName implements NameShowType
{
  /**
   * A built-in type name.
   */

  @EqualityStructural public static final class TTypeNameBuiltIn extends
    TTypeName
  {
    private final String name;

    /**
     * Construct a built-in type name.
     * 
     * @param in_name
     *          The name
     */

    public TTypeNameBuiltIn(
      final String in_name)
    {
      this.name = NullCheck.notNull(in_name, "Name");
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
      final TTypeNameBuiltIn other = (TTypeNameBuiltIn) obj;
      if (!this.name.equals(other.name)) {
        return false;
      }
      return true;
    }

    /**
     * @return The name
     */

    public String getName()
    {
      return this.name;
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

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TTypeNameBuiltIn ");
      builder.append(this.name);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }

    @Override public
      <A, E extends Throwable, V extends TTypeNameVisitorType<A, E>>
      A
      typeNameVisitableAccept(
        final V v)
        throws E
    {
      return v.typeNameVisitBuiltIn(this);
    }
  }

  /**
   * A global type name.
   */

  @EqualityStructural public static final class TTypeNameGlobal extends
    TTypeName
  {
    private final ModulePathFlat       flat;
    private final TokenIdentifierLower name;
    private final ModulePath           path;

    /**
     * Construct a global type name.
     * 
     * @param in_path
     *          The module path
     * @param in_name
     *          The name of the type
     */

    public TTypeNameGlobal(
      final ModulePath in_path,
      final TokenIdentifierLower in_name)
    {
      this.path = NullCheck.notNull(in_path, "Path");
      this.name = NullCheck.notNull(in_name, "Name");
      this.flat = ModulePathFlat.fromModulePath(in_path);
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
      final TTypeNameGlobal other = (TTypeNameGlobal) obj;
      if (!this.name.equals(other.name)) {
        return false;
      }
      if (!this.flat.equals(other.flat)) {
        return false;
      }
      return true;
    }

    /**
     * @return The flattened module path
     */

    public ModulePathFlat getFlat()
    {
      return this.flat;
    }

    /**
     * @return The name of the type
     */

    public TokenIdentifierLower getName()
    {
      return this.name;
    }

    /**
     * @return The module path
     */

    public ModulePath getPath()
    {
      return this.path;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.name.hashCode();
      result = (prime * result) + this.path.hashCode();
      return result;
    }

    @Override public String show()
    {
      final StringBuilder s = new StringBuilder();
      s.append(this.flat.getActual());
      s.append(".");
      s.append(this.name.getActual());
      final String r = s.toString();
      assert r != null;
      return r;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TTypeNameGlobal ");
      builder.append(this.path);
      builder.append(" ");
      builder.append(this.name);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }

    @Override public
      <A, E extends Throwable, V extends TTypeNameVisitorType<A, E>>
      A
      typeNameVisitableAccept(
        final V v)
        throws E
    {
      return v.typeNameVisitGlobal(this);
    }
  }

  @Override public abstract boolean equals(
    @Nullable Object obj);

  @Override public abstract int hashCode();

  /**
   * Accept a generic visitor.
   * 
   * @param v
   *          The visitor
   * @return The value returned by the visitor
   * @throws E
   *           If the visitor raises <code>E</code>
   */

  public abstract
    <A, E extends Throwable, V extends TTypeNameVisitorType<A, E>>
    A
    typeNameVisitableAccept(
      final V v)
      throws E;
}
