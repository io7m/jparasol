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

package com.io7m.jparasol.glsl;

import java.io.File;

import com.io7m.jparasol.CompilerError;
import com.io7m.jparasol.lexer.Position;
import com.io7m.jparasol.lexer.TokenIdentifierLower;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDExternal;

/**
 * The type of FFI errors.
 */

public final class GFFIError extends CompilerError
{
  private static final long serialVersionUID;

  static {
    serialVersionUID = 6577036815422441152L;
  }

  /**
   * An unknown external was encountered.
   * 
   * @param external
   *          The external
   * @return An error
   */

  public static GFFIError unknownExternal(
    final TASTDExternal external)
  {
    final TokenIdentifierLower name = external.getName();
    final StringBuilder b = new StringBuilder();
    b.append("Unknown external ");
    b.append(name.getActual());
    return new GFFIError(b.toString(), name.getFile(), name.getPosition());
  }

  private GFFIError(
    final String message,
    final File file,
    final Position position)
  {
    super(message, file, position);
  }

  @Override public String getCategory()
  {
    return "ffi";
  }
}
