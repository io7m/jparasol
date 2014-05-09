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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jfunctional.Pair;
import com.io7m.jnull.NullCheck;

/**
 * <p>
 * A batch of shader names to be compiled.
 * </p>
 * <p>
 * A batch is conceptually a list of pairs, with the left element of a pair
 * denoting the output name of a GLSL shader, and the right element denoting
 * the (fully-qualified) name of a Parasol program to compile.
 * </p>
 */

@EqualityReference public final class Batch
{
  /**
   * Load a batch from a file.
   * 
   * @param base
   *          The base directory
   * @param file
   *          The batch file
   * @return A batch
   * @throws IOException
   *           If an I/O error occurs
   */

  public static Batch fromFile(
    final File base,
    final File file)
    throws IOException
  {
    NullCheck.notNull(base, "Base");
    NullCheck.notNull(file, "File");

    final FileInputStream stream = new FileInputStream(file);
    try {
      return Batch.fromStream(base, stream);
    } finally {
      stream.close();
    }
  }

  /**
   * Load a batch from a stream.
   * 
   * @param base
   *          The base directory
   * @param stream
   *          The stream
   * @return A batch
   * @throws IOException
   *           If an I/O error occurs
   */

  @SuppressWarnings("resource") public static Batch fromStream(
    final File base,
    final InputStream stream)
    throws IOException
  {
    NullCheck.notNull(base, "Base");
    NullCheck.notNull(stream, "Stream");

    final BufferedReader reader =
      new BufferedReader(new InputStreamReader(stream));
    final List<Pair<String, String>> targets =
      new ArrayList<Pair<String, String>>();

    int line_number = 1;

    for (;;) {
      final String line = reader.readLine();
      if (line == null) {
        break;
      }
      final String[] segments = line.split(":");
      if (segments.length != 2) {
        throw new IllegalArgumentException("Line "
          + line_number
          + ": format must be: output , \":\" , shader");
      }
      final String out = segments[0].trim();
      final String shader = segments[1].trim();
      assert out != null;
      assert shader != null;
      targets.add(Pair.pair(out, shader));
      ++line_number;
    }

    return new Batch(base, targets);
  }

  private final File                       base;
  private final List<Pair<String, String>> targets;

  private Batch(
    final File in_base,
    final List<Pair<String, String>> in_targets)
  {
    this.base = NullCheck.notNull(in_base, "Base");
    this.targets = NullCheck.notNullAll(in_targets, "Targets");
  }

  /**
   * @return The base directory
   */

  public File getBase()
  {
    return this.base;
  }

  /**
   * @return The list of shaders to be compiled.
   */

  public List<Pair<String, String>> getTargets()
  {
    final List<Pair<String, String>> r =
      Collections.unmodifiableList(this.targets);
    assert r != null;
    return r;
  }
}
