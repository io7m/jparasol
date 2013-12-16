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

package com.io7m.jparasol.untyped.ast.unique_binders;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Option;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.lexer.Token.TokenIdentifierUpper;

public final class UASTUTypePath
{
  private final @Nonnull Option<TokenIdentifierUpper> module;
  private final @Nonnull TokenIdentifierLower         name;

  public UASTUTypePath(
    final @Nonnull Option<TokenIdentifierUpper> module,
    final @Nonnull TokenIdentifierLower name)
    throws ConstraintError
  {
    this.module = Constraints.constrainNotNull(module, "Module");
    this.name = Constraints.constrainNotNull(name, "Name");
  }

  public @Nonnull Option<TokenIdentifierUpper> getModule()
  {
    return this.module;
  }

  public @Nonnull TokenIdentifierLower getName()
  {
    return this.name;
  }
}
