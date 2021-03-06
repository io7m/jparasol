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

package com.io7m.jparasol.untyped.ast.unique_binders;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jfunctional.OptionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.lexer.Token;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.lexer.Token.TokenIdentifierUpper;

/**
 * A path to a shader.
 */

// CHECKSTYLE_JAVADOC:OFF

@EqualityReference public final class UASTUShaderPath
{
  private final OptionType<Token.TokenIdentifierUpper> module;
  private final Token.TokenIdentifierLower             name;

  public UASTUShaderPath(
    final OptionType<TokenIdentifierUpper> in_module,
    final TokenIdentifierLower in_name)
  {
    this.module = NullCheck.notNull(in_module, "Module");
    this.name = NullCheck.notNull(in_name, "Name");
  }

  public OptionType<Token.TokenIdentifierUpper> getModule()
  {
    return this.module;
  }

  public Token.TokenIdentifierLower getName()
  {
    return this.name;
  }
}
