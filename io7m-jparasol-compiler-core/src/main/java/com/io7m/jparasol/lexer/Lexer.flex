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

package com.io7m.jparasol.lexer;

import java.io.File;
import java.io.IOException;
import javax.annotation.Nonnull;
import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;

%%

%apiprivate
%class Lexer
%column
%public
%final
%line
%type Token
%unicode

%{

  public Token token()
    throws IOException,
      LexerError
  {
    return this.yylex();
  }

  private @Nonnull File file = new File("<stdin>");

  public void setFile(final @Nonnull File file)
    throws ConstraintError
  {
    this.file = Constraints.constrainNotNull(file, "File name");
  }

  public @Nonnull File getFile()
  {
    return this.file;
  }

  private Position position()
  {
    return new Position(this.yyline + 1, this.yycolumn);
  }

%}

LiteralDecimal = 0 | ("-")? [1-9]+ [0-9]*
LiteralFloat   = ("-")? {LiteralDecimal}+ "." {LiteralDecimal}+
LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
Whitespace     = [ \t\f\r\n]
Comment        = "--" {InputCharacter}* {LineTerminator}

NameLower = [a-z] ([a-z] | [A-Z] | [0-9] | "_")*
NameUpper = [A-Z] ([a-z] | [A-Z] | [0-9] | "_")*

%%

<YYINITIAL> "as"        { return new Token.TokenAs (this.getFile(), this.position()); }
<YYINITIAL> "true"      { return new Token.TokenLiteralBoolean (this.getFile(), this.position(), true); }
<YYINITIAL> "false"     { return new Token.TokenLiteralBoolean (this.getFile(), this.position(), false); }
<YYINITIAL> ":"         { return new Token.TokenColon (this.getFile(), this.position()); }
<YYINITIAL> ","         { return new Token.TokenComma (this.getFile(), this.position()); }
<YYINITIAL> "{"         { return new Token.TokenCurlyLeft (this.getFile(), this.position()); }
<YYINITIAL> "}"         { return new Token.TokenCurlyRight (this.getFile(), this.position()); }
<YYINITIAL> "discard"   { return new Token.TokenDiscard (this.getFile(), this.position()); }
<YYINITIAL> "."         { return new Token.TokenDot (this.getFile(), this.position()); }
<YYINITIAL> "else"      { return new Token.TokenElse (this.getFile(), this.position()); }
<YYINITIAL> "end"       { return new Token.TokenEnd (this.getFile(), this.position()); }
<YYINITIAL> "="         { return new Token.TokenEquals (this.getFile(), this.position()); }
<YYINITIAL> "external"  { return new Token.TokenExternal (this.getFile(), this.position()); }
<YYINITIAL> "fragment"  { return new Token.TokenFragment (this.getFile(), this.position()); }
<YYINITIAL> "function"  { return new Token.TokenFunction (this.getFile(), this.position()); }
<YYINITIAL> "if"        { return new Token.TokenIf (this.getFile(), this.position()); }
<YYINITIAL> "import"    { return new Token.TokenImport (this.getFile(), this.position()); }
<YYINITIAL> "in"        { return new Token.TokenIn (this.getFile(), this.position()); }
<YYINITIAL> "is"        { return new Token.TokenIs (this.getFile(), this.position()); }
<YYINITIAL> "let"       { return new Token.TokenLet (this.getFile(), this.position()); }
<YYINITIAL> "module"    { return new Token.TokenModule (this.getFile(), this.position()); }
<YYINITIAL> "new"       { return new Token.TokenNew (this.getFile(), this.position()); }
<YYINITIAL> "out"       { return new Token.TokenOut (this.getFile(), this.position()); }
<YYINITIAL> "package"   { return new Token.TokenPackage (this.getFile(), this.position()); }
<YYINITIAL> "parameter" { return new Token.TokenParameter (this.getFile(), this.position()); }
<YYINITIAL> "program"   { return new Token.TokenProgram (this.getFile(), this.position()); }
<YYINITIAL> "record"    { return new Token.TokenRecord (this.getFile(), this.position()); }
<YYINITIAL> "shader"    { return new Token.TokenShader (this.getFile(), this.position()); }
<YYINITIAL> "("         { return new Token.TokenRoundLeft (this.getFile(), this.position()); }
<YYINITIAL> ")"         { return new Token.TokenRoundRight (this.getFile(), this.position()); }
<YYINITIAL> ";"         { return new Token.TokenSemicolon (this.getFile(), this.position()); }
<YYINITIAL> "["         { return new Token.TokenSquareLeft (this.getFile(), this.position()); }
<YYINITIAL> "]"         { return new Token.TokenSquareRight (this.getFile(), this.position()); }
<YYINITIAL> "then"      { return new Token.TokenThen (this.getFile(), this.position()); }
<YYINITIAL> "type"      { return new Token.TokenType (this.getFile(), this.position()); }
<YYINITIAL> "value"     { return new Token.TokenValue (this.getFile(), this.position()); }
<YYINITIAL> "vertex"    { return new Token.TokenVertex (this.getFile(), this.position()); }
<YYINITIAL> "with"      { return new Token.TokenWith (this.getFile(), this.position()); }

<YYINITIAL> {
  { Comment        } { /* Ignore */ }
  { Whitespace     } { /* Ignore */ }
  { LiteralDecimal } { return Token.TokenLiteralIntegerDecimal.newIntegerDecimal(this.getFile(), this.position(), yytext()); }
  { LiteralFloat   } { return Token.TokenLiteralReal.newReal (this.getFile(), this.position(), yytext()); }
  { NameUpper      } { return new Token.TokenIdentifierUpper (this.getFile(), this.position(), yytext()); }
  { NameLower      } { return new Token.TokenIdentifierLower (this.getFile(), this.position(), yytext()); }
}

<<EOF>> { return new Token.TokenEOF (this.getFile(), this.position()); }

/* error fallback */
.|\n  { throw LexerError.errorAt("Illegal character <"+ yytext()+">", this.getFile(), this.position()); }
