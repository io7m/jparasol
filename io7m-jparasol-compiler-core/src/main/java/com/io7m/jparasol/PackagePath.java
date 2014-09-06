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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.io7m.jequality.annotations.EqualityStructural;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jparasol.lexer.Position;
import com.io7m.jparasol.lexer.TokenIdentifierLower;

/**
 * The type of package paths.
 */

@SuppressWarnings("synthetic-access") @EqualityStructural public final class PackagePath
{
  /**
   * The type of mutable package path builders.
   */

  public interface BuilderType
  {
    /**
     * Add a path component.
     * 
     * @param c
     *          The component
     */

    void addComponent(
      final TokenIdentifierLower c);

    /**
     * Add a "fake" (that is; did not come from a source file) path component.
     * 
     * @param c
     *          The component
     */

    void addFakeComponent(
      final String c);

    /**
     * @return A new package path, raising an error if the resulting path is
     *         invalid.
     */

    PackagePath build();
  }

  /**
   * @return A new mutable path builder.
   */

  public static BuilderType newBuilder()
  {
    return new BuilderType() {
      private final List<TokenIdentifierLower> components =
                                                            new ArrayList<TokenIdentifierLower>();

      @Override public void addComponent(
        final TokenIdentifierLower c)
      {
        NullCheck.notNull(c, "Component");
        this.components.add(c);
      }

      @Override public void addFakeComponent(
        final String c)
      {
        NullCheck.notNull(c, "Component");

        if (c.isEmpty()) {
          throw new IllegalArgumentException(
            "Package path component is empty");
        }
        if (Character.isLowerCase(c.charAt(0)) == false) {
          throw new IllegalArgumentException(
            "Package path component is not lowercase");
        }

        final File file = new File("<generated>");
        final Position position = Position.ZERO;
        final TokenIdentifierLower token =
          new TokenIdentifierLower(file, position, c);
        this.addComponent(token);
      }

      @Override public PackagePath build()
      {
        return new PackagePath(this.components);
      }
    };
  }

  private final List<TokenIdentifierLower> components;

  private PackagePath(
    final List<TokenIdentifierLower> in_components)
  {
    this.components = NullCheck.notNullAll(in_components, "Components");
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
    final PackagePath other = (PackagePath) obj;
    if (!this.components.equals(other.components)) {
      return false;
    }
    return true;
  }

  /**
   * @return The list of tokens that make up the path.
   */

  public List<TokenIdentifierLower> getComponents()
  {
    final List<TokenIdentifierLower> r =
      Collections.unmodifiableList(this.components);
    assert r != null;
    return r;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.components.hashCode();
    return result;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[PackagePath components=");
    builder.append(this.components);
    builder.append("]");
    final String r = builder.toString();
    assert r != null;
    return r;
  }
}
