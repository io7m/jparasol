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

import com.io7m.jparasol.CompilerError;
import com.io7m.jparasol.NameRestrictions.NameRestrictionsException;
import com.io7m.jparasol.lexer.Position;
import com.io7m.jparasol.lexer.TokenIdentifierUpper;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDModule;

/**
 * Errors raised during the unit combination phase.
 * 
 * @see UnitCombinerError
 */

public final class UnitCombinerError extends CompilerError
{
  private static final long serialVersionUID = 5359160308099372566L;

  /**
   * @return A unit combiner error
   */

  public static UnitCombinerError duplicateModule(
    final UASTIDModule original,
    final UASTIDModule current)
  {
    final TokenIdentifierUpper cn = current.getPath().getName();
    final StringBuilder m = new StringBuilder();
    m.append("The module named '");
    m.append(cn.getActual());
    m.append("' defined at ");
    m.append(cn.getFile());
    m.append(":");
    m.append(cn.getPosition());
    m.append(" conflicts with the module definition at ");
    m.append(original.getPath().getName().getFile());
    m.append(":");
    m.append(original.getPath().getName().getPosition());
    return new UnitCombinerError(cn.getFile(), cn.getPosition(), m.toString());
  }

  private UnitCombinerError(
    final File file,
    final Position position,
    final String message)
  {
    super(message, file, position);
  }

  /**
   * Construct a unit combiner error
   */

  public UnitCombinerError(
    final NameRestrictionsException x)
  {
    super(x, x.getMessage(), x.getFile(), x.getPosition());
  }

  @Override public String getCategory()
  {
    return "unit-combiner";
  }
}
