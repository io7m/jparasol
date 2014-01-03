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

package com.io7m.jparasol.untyped;

import java.io.File;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.CompilerError;
import com.io7m.jparasol.NameRestrictions.NameRestrictionsException;
import com.io7m.jparasol.lexer.Position;

public final class UniqueBindersError extends CompilerError
{
  private static final long serialVersionUID = 5359160308099372566L;

  private UniqueBindersError(
    final @Nonnull File file,
    final @Nonnull Position position,
    final @Nonnull String message)
    throws ConstraintError
  {
    super(message, file, position);
  }

  public UniqueBindersError(
    final @Nonnull NameRestrictionsException x)
    throws ConstraintError
  {
    super(x, x.getMessage(), x.getFile(), x.getPosition());
  }
}
