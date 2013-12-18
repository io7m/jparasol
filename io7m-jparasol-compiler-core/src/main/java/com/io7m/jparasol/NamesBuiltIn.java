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

package com.io7m.jparasol;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

public final class NamesBuiltIn
{
  public static final Set<String> FRAGMENT_SHADER_INPUTS;
  public static final Set<String> VERTEX_SHADER_OUTPUTS;

  static {
    VERTEX_SHADER_OUTPUTS = NamesBuiltIn.makeVertexShaderOutputs();
    FRAGMENT_SHADER_INPUTS = NamesBuiltIn.makeFragmentShaderInputs();
  }

  private static @Nonnull Set<String> makeFragmentShaderInputs()
  {
    final HashSet<String> s = new HashSet<String>();
    s.add("gl_FragCoord");
    return Collections.unmodifiableSet(s);
  }

  private static @Nonnull Set<String> makeVertexShaderOutputs()
  {
    final HashSet<String> s = new HashSet<String>();
    s.add("gl_Position");
    return Collections.unmodifiableSet(s);
  }
}
