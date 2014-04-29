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

package com.io7m.jparasol.untyped.ast.resolved;

import java.io.File;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.ModulePath;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.lexer.Position;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;

// CHECKSTYLE_JAVADOC:OFF

@EqualityReference public abstract class UASTRTermName implements
  UASTRTermNameVisitableType
{
  /**
   * A fully qualified name.
   */

  @EqualityReference public static final class UASTRTermNameGlobal extends
    UASTRTermName
  {
    private final ModulePathFlat       flat;
    private final TokenIdentifierLower name;
    private final ModulePath           path;

    public UASTRTermNameGlobal(
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

    public ModulePathFlat getFlat()
    {
      return this.flat;
    }

    @Override public TokenIdentifierLower getName()
    {
      return this.name;
    }

    public ModulePath getPath()
    {
      return this.path;
    }

    @Override public String show()
    {
      final StringBuilder s = new StringBuilder();
      s.append("$");
      s.append(this.flat.getActual());
      s.append(".");
      s.append(this.name.getActual());
      return s.toString();
    }

    @Override public
      <A, E extends Throwable, V extends UASTRTermNameVisitorType<A, E>>
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
      builder.append("[UASTRNameGlobal ");
      builder.append(this.path);
      builder.append(" ");
      builder.append(this.name);
      builder.append("]");
      return builder.toString();
    }
  }

  /**
   * A name that refers to a local variable.
   */

  @EqualityReference public static final class UASTRTermNameLocal extends
    UASTRTermName
  {
    private final String               current;
    private final TokenIdentifierLower original;

    public UASTRTermNameLocal(
      final TokenIdentifierLower in_original,
      final String in_current)
    {
      super(NullCheck.notNull(in_original, "Original").getFile(), NullCheck
        .notNull(in_original, "Original")
        .getPosition());
      this.original = in_original;
      this.current = NullCheck.notNull(in_current, "Current");
    }

    public String getCurrent()
    {
      return this.current;
    }

    @Override public TokenIdentifierLower getName()
    {
      return this.original;
    }

    public TokenIdentifierLower getOriginal()
    {
      return this.original;
    }

    @Override public String show()
    {
      final StringBuilder s = new StringBuilder();
      s.append("&");
      s.append(this.current);
      return s.toString();
    }

    @Override public
      <A, E extends Throwable, V extends UASTRTermNameVisitorType<A, E>>
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
      builder.append("[UASTRNameLocal ");
      builder.append(this.current);
      builder.append(" ");
      builder.append(this.original);
      builder.append("]");
      return builder.toString();
    }
  }

  private final File     file;
  private final Position position;

  protected UASTRTermName(
    final File in_file,
    final Position in_position)
  {
    this.file = NullCheck.notNull(in_file, "File");
    this.position = NullCheck.notNull(in_position, "Position");
  }

  public final File getFile()
  {
    return this.file;
  }

  public abstract TokenIdentifierLower getName();

  public final Position getPosition()
  {
    return this.position;
  }

  public abstract String show();
}
