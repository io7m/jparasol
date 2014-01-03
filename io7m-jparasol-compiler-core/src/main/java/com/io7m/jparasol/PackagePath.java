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

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.lexer.Position;
import com.io7m.jparasol.lexer.Token;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;

public final class PackagePath
{
  public interface Builder
  {
    public void addComponent(
      final @Nonnull TokenIdentifierLower c)
      throws ConstraintError;

    public void addFakeComponent(
      final @Nonnull String c)
      throws ConstraintError;

    public @Nonnull PackagePath build()
      throws ConstraintError;
  }

  public static @Nonnull Builder newBuilder()
  {
    return new Builder() {
      private final @Nonnull List<TokenIdentifierLower> components =
                                                                     new ArrayList<Token.TokenIdentifierLower>();

      @Override public PackagePath build()
        throws ConstraintError
      {
        return new PackagePath(this.components);
      }

      @Override public void addFakeComponent(
        final @Nonnull String c)
        throws ConstraintError
      {
        Constraints.constrainNotNull(c, "Component");

        final File file = new File("<generated>");
        final Position position = Position.ZERO;
        final TokenIdentifierLower token =
          new TokenIdentifierLower(file, position, c);
        this.addComponent(token);
      }

      @Override public void addComponent(
        final TokenIdentifierLower c)
        throws ConstraintError
      {
        Constraints.constrainNotNull(c, "Component");
        this.components.add(c);
      }
    };
  }

  private final @Nonnull List<TokenIdentifierLower> components;

  private PackagePath(
    final @Nonnull List<TokenIdentifierLower> components)
    throws ConstraintError
  {
    this.components = Constraints.constrainNotNull(components, "Components");
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
    final PackagePath other = (PackagePath) obj;
    if (!this.components.equals(other.components)) {
      return false;
    }
    return true;
  }

  public @Nonnull List<TokenIdentifierLower> getComponents()
  {
    return Collections.unmodifiableList(this.components);
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
    return builder.toString();
  }
}
