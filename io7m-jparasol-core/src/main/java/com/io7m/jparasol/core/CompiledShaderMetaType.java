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

package com.io7m.jparasol.core;

import java.util.SortedSet;

import com.io7m.jfunctional.OptionType;

/**
 * The type of compiled shader metadata.
 */

public interface CompiledShaderMetaType
{
  /**
   * @return The fully-qualified name of the shader.
   */

  String getName();

  /**
   * @return The name of the GLSL source code file for the given version, or
   *         {@link com.io7m.jfunctional.None} if there is no file for the
   *         given version.
   */

  OptionType<String> getSourceCodeFilename(
    final GVersionType v);

  /**
   * @return The supported versions of GLSL ES.
   */

  SortedSet<GVersionES> getSupportsES();

  /**
   * @return The supported versions of GLSL.
   */

  SortedSet<GVersionFull> getSupportsFull();
}
