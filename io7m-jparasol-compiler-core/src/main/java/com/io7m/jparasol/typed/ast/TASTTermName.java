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

package com.io7m.jparasol.typed.ast;

import java.io.File;

import com.io7m.jequality.annotations.EqualityStructural;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jparasol.ModulePath;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.NameShowType;
import com.io7m.jparasol.lexer.Position;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;

/**
 * A term name.
 */

@EqualityStructural public abstract class TASTTermName implements
  NameShowType
{
  /**
   * A name that refers to an external. Will only appear in typed ASTs if
   * inserted there via an FFI mechanism.
   */

  @EqualityStructural public static final class TASTTermNameExternal extends
    TASTTermName
  {
    private final String               current;
    private final TokenIdentifierLower token;

    /**
     * Construct an external term name.
     * 
     * @param in_token
     *          The token
     * @param in_current
     *          The name
     */

    public TASTTermNameExternal(
      final TokenIdentifierLower in_token,
      final String in_current)
    {
      super(NullCheck.notNull(in_token, "Token").getFile(), NullCheck
        .notNull(in_token, "Token")
        .getPosition());
      this.token = in_token;
      this.current = NullCheck.notNull(in_current, "Current");
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
      if (super.baseEquals(obj) == false) {
        return false;
      }

      final TASTTermNameExternal other = (TASTTermNameExternal) obj;
      if (!this.current.equals(other.current)) {
        return false;
      }
      if (!this.token.equals(other.token)) {
        return false;
      }
      return true;
    }

    /**
     * @return The name as a string.
     */

    public String getCurrent()
    {
      return this.current;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = super.baseHashCode();
      result = (prime * result) + this.current.hashCode();
      result = (prime * result) + this.token.hashCode();
      return result;
    }

    @Override public String show()
    {
      return this.getCurrent();
    }

    @Override public
      <A, E extends Throwable, V extends TASTTermNameVisitorType<A, E>>
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
      builder.append("[TASTNameExternal ");
      builder.append(this.current);
      builder.append(" ");
      builder.append(this.token);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  /**
   * A fully qualified name.
   */

  @EqualityStructural public static final class TASTTermNameGlobal extends
    TASTTermName
  {
    private final ModulePathFlat       flat;
    private final TokenIdentifierLower name;
    private final ModulePath           path;

    /**
     * Construct a global term name.
     * 
     * @param in_path
     *          The module path
     * @param actual
     *          The name
     */

    public TASTTermNameGlobal(
      final ModulePath in_path,
      final TokenIdentifierLower actual)
    {
      super(NullCheck.notNull(actual, "Actual").getFile(), NullCheck.notNull(
        actual,
        "Actual").getPosition());
      this.path = NullCheck.notNull(in_path, "Path");
      this.flat = ModulePathFlat.fromModulePath(in_path);
      this.name = actual;
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
      if (super.baseEquals(obj) == false) {
        return false;
      }

      final TASTTermNameGlobal other = (TASTTermNameGlobal) obj;
      if (!this.flat.equals(other.flat)) {
        return false;
      }
      if (!this.name.equals(other.name)) {
        return false;
      }
      if (!this.path.equals(other.path)) {
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
     * @return The name
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
      int result = super.baseHashCode();
      result = (prime * result) + this.flat.hashCode();
      result = (prime * result) + this.name.hashCode();
      result = (prime * result) + this.path.hashCode();
      return result;
    }

    @Override public String show()
    {
      final StringBuilder s = new StringBuilder();
      s.append("$");
      s.append(this.flat.getActual());
      s.append(".");
      s.append(this.name.getActual());
      final String r = s.toString();
      assert r != null;
      return r;
    }

    @Override public
      <A, E extends Throwable, V extends TASTTermNameVisitorType<A, E>>
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
      builder.append("[TASTNameGlobal ");
      builder.append(this.path);
      builder.append(" ");
      builder.append(this.name);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  /**
   * A name that refers to a local variable.
   */

  @EqualityStructural public static final class TASTTermNameLocal extends
    TASTTermName
  {
    private final String               current;
    private final TokenIdentifierLower original;

    /**
     * Construct a local term name.
     * 
     * @param in_original
     *          The original name
     * @param in_current
     *          The current name
     */

    public TASTTermNameLocal(
      final TokenIdentifierLower in_original,
      final String in_current)
    {
      super(NullCheck.notNull(in_original, "Original").getFile(), NullCheck
        .notNull(in_original, "Original")
        .getPosition());
      this.original = in_original;
      this.current = NullCheck.notNull(in_current, "Current");
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
      if (super.baseEquals(obj) == false) {
        return false;
      }

      final TASTTermNameLocal other = (TASTTermNameLocal) obj;
      if (!this.current.equals(other.current)) {
        return false;
      }
      if (!this.original.equals(other.original)) {
        return false;
      }
      return true;
    }

    /**
     * @return The current name as a string
     */

    public String getCurrent()
    {
      return this.current;
    }

    /**
     * @return The original name as it appeared in source code
     */

    public TokenIdentifierLower getOriginal()
    {
      return this.original;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = super.baseHashCode();
      result = (prime * result) + this.current.hashCode();
      result = (prime * result) + this.original.hashCode();
      return result;
    }

    @Override public String show()
    {
      return this.getCurrent();
    }

    @Override public
      <A, E extends Throwable, V extends TASTTermNameVisitorType<A, E>>
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
      builder.append("[TASTNameLocal ");
      builder.append(this.current);
      builder.append(" ");
      builder.append(this.original);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  private final File     file;
  private final Position position;

  protected TASTTermName(
    final File in_file,
    final Position in_position)
  {
    this.file = NullCheck.notNull(in_file, "File");
    this.position = NullCheck.notNull(in_position, "Position");
  }

  protected final boolean baseEquals(
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
    final TASTTermName other = (TASTTermName) obj;
    if (!this.file.equals(other.file)) {
      return false;
    }
    if (!this.position.equals(other.position)) {
      return false;
    }
    return true;
  }

  protected final int baseHashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.file.hashCode();
    result = (prime * result) + this.position.hashCode();
    return result;
  }

  @Override public abstract boolean equals(
    @Nullable Object obj);

  /**
   * @return The file in which the name appears
   */

  public final File getFile()
  {
    return this.file;
  }

  /**
   * @return The position at which the name appears
   */

  public final Position getPosition()
  {
    return this.position;
  }

  @Override public abstract int hashCode();

  /**
   * Accept a term name visitor.
   * 
   * @param v
   *          The visitor
   * @return The value returned by the visitor
   * @throws E
   *           If the visitor raises <code>E</code>
   */

  public abstract
    <A, E extends Throwable, V extends TASTTermNameVisitorType<A, E>>
    A
    termNameVisitableAccept(
      final V v)
      throws E;
}
