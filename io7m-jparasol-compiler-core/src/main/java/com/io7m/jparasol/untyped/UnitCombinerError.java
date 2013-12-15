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

package com.io7m.jparasol.untyped;

import java.io.File;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.CompilerError;
import com.io7m.jparasol.NameRestrictions.NameRestrictionsException;
import com.io7m.jparasol.lexer.Position;
import com.io7m.jparasol.lexer.Token.TokenIdentifierUpper;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDModule;
import com.io7m.jparasol.untyped.ast.initial.UASTIUnchecked;

public final class UnitCombinerError extends CompilerError
{
  private static final long serialVersionUID = 5359160308099372566L;

  public static UnitCombinerError duplicateModule(
    final @Nonnull UASTIDModule<UASTIUnchecked> original,
    final @Nonnull UASTIDModule<UASTIUnchecked> current)
    throws ConstraintError
  {
    final TokenIdentifierUpper cn = current.getName();
    final StringBuilder m = new StringBuilder();
    m.append("The module named '");
    m.append(cn.getActual());
    m.append("' defined at ");
    m.append(cn.getFile());
    m.append(":");
    m.append(cn.getPosition());
    m.append(" conflicts with the module definition at ");
    m.append(original.getName().getFile());
    m.append(":");
    m.append(original.getName().getPosition());
    return new UnitCombinerError(cn.getFile(), cn.getPosition(), m.toString());
  }

  private UnitCombinerError(
    final @Nonnull File file,
    final @Nonnull Position position,
    final @Nonnull String message)
    throws ConstraintError
  {
    super(message, file, position);
  }

  public UnitCombinerError(
    final @Nonnull NameRestrictionsException x)
    throws ConstraintError
  {
    super(x, x.getMessage(), x.getFile(), x.getPosition());
  }
}
