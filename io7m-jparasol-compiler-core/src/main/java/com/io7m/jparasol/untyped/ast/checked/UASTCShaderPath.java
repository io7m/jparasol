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

package com.io7m.jparasol.untyped.ast.checked;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jfunctional.OptionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.lexer.TokenIdentifierLower;
import com.io7m.jparasol.lexer.TokenIdentifierUpper;

/**
 * A path to a shader.
 */

// CHECKSTYLE_JAVADOC:OFF

@EqualityReference public final class UASTCShaderPath
{
  private final OptionType<TokenIdentifierUpper> module;
  private final TokenIdentifierLower             name;

  public UASTCShaderPath(
    final OptionType<TokenIdentifierUpper> in_module,
    final TokenIdentifierLower in_name)
  {
    this.module = NullCheck.notNull(in_module, "Module");
    this.name = NullCheck.notNull(in_name, "Name");
  }

  public OptionType<TokenIdentifierUpper> getModule()
  {
    return this.module;
  }

  public TokenIdentifierLower getName()
  {
    return this.name;
  }
}
