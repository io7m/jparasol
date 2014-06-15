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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.io7m.jnull.NullCheck;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * Utilities for reading sources from a stream.
 */

public final class JPSourceLines
{
  /**
   * @param stream
   *          The stream.
   * @return Lines of source code from the stream.
   * @throws IOException
   *           On I/O errors.
   */

  public static List<String> fromStream(
    final InputStream stream)
    throws IOException
  {
    NullCheck.notNull(stream, "Stream");

    final BufferedReader br =
      new BufferedReader(new InputStreamReader(stream));

    try {
      final List<String> lines = new ArrayList<String>();

      for (;;) {
        final String line = br.readLine();
        if (line == null) {
          break;
        }
        lines.add(line);
      }

      return lines;
    } finally {
      br.close();
    }
  }

  private JPSourceLines()
  {
    throw new UnreachableCodeException();
  }
}
