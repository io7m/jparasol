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
import java.io.IOException;
import javax.annotation.Nonnull;
import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.lexer.LexerError;
import com.io7m.jparasol.lexer.Position;
import com.io7m.jparasol.glsl.GVersionNumberSetToken.TokenLiteralIntegerDecimal;

%%

%apiprivate
%class GVersionNumberSetLexer
%column
%public
%final
%line
%type GVersionNumberSetToken
%unicode

%{

  public GVersionNumberSetToken token()
    throws IOException,
      LexerError
  {
    return this.yylex();
  }

  private Position getPosition()
  {
    return new Position(this.yyline + 1, this.yycolumn);
  }
  
  private final  File file = new File("<stdin>");

  public File getFile()
  {
    return this.file;
  }

%}

LiteralDecimal = 0 | ("-")? [1-9]+ [0-9]*
Whitespace     = [ \t\f\r\n]

%%

<YYINITIAL> "(" { return new GVersionNumberSetToken.TokenRoundLeft (this.file, this.getPosition()); }
<YYINITIAL> ")" { return new GVersionNumberSetToken.TokenRoundRight (this.file, this.getPosition()); }
<YYINITIAL> "," { return new GVersionNumberSetToken.TokenComma (this.file, this.getPosition()); }
<YYINITIAL> "[" { return new GVersionNumberSetToken.TokenSquareLeft (this.file, this.getPosition()); }
<YYINITIAL> "]" { return new GVersionNumberSetToken.TokenSquareRight (this.file, this.getPosition()); }
<YYINITIAL> {
  { Whitespace     } { /* Ignore */ }
  { LiteralDecimal } { 
    return TokenLiteralIntegerDecimal.newIntegerDecimal(
      this.file,
      this.getPosition(),
      yytext()); 
  }
}

<<EOF>> { return new GVersionNumberSetToken.TokenEOF (this.file, this.getPosition()); }

/* error fallback */
.|\n  { 
  throw new LexerError("Illegal character <"+ yytext()+">", this.file, this.getPosition()); 
}
