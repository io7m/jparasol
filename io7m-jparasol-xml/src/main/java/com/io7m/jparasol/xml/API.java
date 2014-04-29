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

package com.io7m.jparasol.xml;

import com.io7m.jnull.NullCheck;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * The GLSL API.
 */

public enum API
{
  /**
   * GLSL.
   */

  API_GLSL("glsl"),

  /**
   * GLSL ES.
   */

  API_GLSL_ES("glsl-es");

  /**
   * Convert an API name to an API.
   * 
   * @param value
   *          The name
   * @return An API
   */

  public static API fromString(
    final String value)
  {
    NullCheck.notNull(value, "Value");
    if ("glsl".equals(value)) {
      return API_GLSL;
    }
    if ("glsl-es".equals(value)) {
      return API_GLSL_ES;
    }

    throw new UnreachableCodeException();
  }

  private final String name;

  private API(
    final String in_name)
  {
    this.name = in_name;
  }

  final String getName()
  {
    return this.name;
  }

  @Override public String toString()
  {
    return this.name;
  }
}
