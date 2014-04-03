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

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Pair;

public final class Batch
{
  public static @Nonnull Batch fromFile(
    final @Nonnull File base,
    final @Nonnull File file)
    throws IOException,
      ConstraintError
  {
    Constraints.constrainNotNull(base, "Base");
    Constraints.constrainNotNull(file, "File");

    final FileInputStream stream = new FileInputStream(file);
    try {
      return Batch.fromStream(base, stream);
    } finally {
      stream.close();
    }
  }

  @SuppressWarnings("resource") public static @Nonnull Batch fromStream(
    final @Nonnull File base,
    final @Nonnull InputStream stream)
    throws IOException,
      ConstraintError
  {
    Constraints.constrainNotNull(base, "Base");
    Constraints.constrainNotNull(stream, "Stream");

    final BufferedReader reader =
      new BufferedReader(new InputStreamReader(stream));
    final ArrayList<Pair<String, String>> targets =
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
      targets.add(new Pair<String, String>(out, shader));
      ++line_number;
    }

    return new Batch(base, targets);
  }

  private final @Nonnull File                            base;
  private final @Nonnull ArrayList<Pair<String, String>> targets;

  private Batch(
    final @Nonnull File in_base,
    final @Nonnull ArrayList<Pair<String, String>> in_targets)
    throws ConstraintError
  {
    this.base = Constraints.constrainNotNull(in_base, "Base");
    this.targets = Constraints.constrainNotNull(in_targets, "Targets");
  }

  public @Nonnull File getBase()
  {
    return this.base;
  }

  public @Nonnull List<Pair<String, String>> getTargets()
  {
    return Collections.unmodifiableList(this.targets);
  }
}
