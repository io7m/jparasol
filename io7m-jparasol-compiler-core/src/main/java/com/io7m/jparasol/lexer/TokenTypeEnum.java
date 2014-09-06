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

package com.io7m.jparasol.lexer;

// CHECKSTYLE_JAVADOC:OFF

public enum TokenTypeEnum
{
  TOKEN_AS("keyword 'as'"),
  TOKEN_CASE("keyword 'case'"),
  TOKEN_COLON("colon"),
  TOKEN_COMMA("comma"),
  TOKEN_CURLY_LEFT("'{'"),
  TOKEN_CURLY_RIGHT("'}'"),
  TOKEN_DEFAULT("keyword 'default'"),
  TOKEN_DEPTH("keyword 'depth'"),
  TOKEN_DISCARD("keyword 'discard'"),
  TOKEN_DOT("'.'"),
  TOKEN_ELSE("keyword 'else'"),
  TOKEN_END("keyword 'end'"),
  TOKEN_EOF("EOF"),
  TOKEN_EQUALS("'='"),
  TOKEN_EXTERNAL("keyword 'external'"),
  TOKEN_FRAGMENT("keyword 'fragment'"),
  TOKEN_FUNCTION("keyword 'function'"),
  TOKEN_IDENTIFIER_LOWER("lowercase identifier"),
  TOKEN_IDENTIFIER_UPPER("uppercase identifier"),
  TOKEN_IF("keyword 'if'"),
  TOKEN_IMPORT("keyword 'import'"),
  TOKEN_IN("keyword 'in'"),
  TOKEN_IS("keyword 'is'"),
  TOKEN_LET("keyword 'let'"),
  TOKEN_LITERAL_BOOLEAN("boolean literal ('true' | 'false')"),
  TOKEN_LITERAL_INTEGER_DECIMAL("integer literal (decimal)"),
  TOKEN_LITERAL_REAL("real literal (decimal)"),
  TOKEN_MATCH("keyword 'match'"),
  TOKEN_MODULE("keyword 'module'"),
  TOKEN_NEW("keyword 'new'"),
  TOKEN_OUT("keyword 'out'"),
  TOKEN_PACKAGE("keyword 'package'"),
  TOKEN_PARAMETER("keyword 'parameter'"),
  TOKEN_PROGRAM("keyword 'program'"),
  TOKEN_RECORD("keyword 'record'"),
  TOKEN_ROUND_LEFT("'('"),
  TOKEN_ROUND_RIGHT("')'"),
  TOKEN_SEMICOLON("';'"),
  TOKEN_SHADER("keyword 'shader'"),
  TOKEN_SQUARE_LEFT("'['"),
  TOKEN_SQUARE_RIGHT("']'"),
  TOKEN_THEN("keyword 'then'"),
  TOKEN_TYPE("keyword 'type'"),
  TOKEN_VALUE("keyword 'value'"),
  TOKEN_VERTEX("keyword 'vertex'"),
  TOKEN_WITH("keyword 'with'");

  private final String description;

  private TokenTypeEnum(
    final String in_description)
  {
    this.description = in_description;
  }

  public String getDescription()
  {
    return this.description;
  }
}
