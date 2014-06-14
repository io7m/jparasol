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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jlog.LogUsableType;
import com.io7m.jnull.NullCheck;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * Lines of source code, including a hash of the lines.
 */

@EqualityReference public final class HashedLines
{
  /**
   * Load source code from a stream.
   * 
   * @param stream
   *          The input stream.
   * @param log
   *          A log interface.
   * @return Lines of source code.
   * @throws IOException
   *           On I/O errors.
   */

  public static HashedLines fromStream(
    final InputStream stream,
    final LogUsableType log)
    throws IOException
  {
    return HashedLines.newSource(HashedLines.readLinesStream(stream), log);
  }

  /**
   * Load source code from a stream, stripping version directives.
   * 
   * @param stream
   *          The input stream.
   * @param log
   *          A log interface.
   * @return Lines of source code.
   * @throws IOException
   *           On I/O errors.
   */

  public static HashedLines fromStreamStripped(
    final InputStream stream,
    final LogUsableType log)
    throws IOException
  {
    return HashedLines.newSource(HashedLines.readLinesStream(stream), log);
  }

  private static HashedLines newActual(
    final List<String> in_lines,
    final LogUsableType log,
    final boolean strip)
    throws IOException
  {
    try {
      final List<String> copy = new ArrayList<String>(in_lines);

      log.debug(copy.size() + " lines");

      if (copy.size() == 0) {
        throw new IOException("No source lines!");
      }

      if (strip) {
        log.debug("stripping version directives");
        if (copy.get(0).startsWith("#version ")) {
          copy.remove(0);
        }
      } else {
        log.debug("not stripping version directives");
      }

      if (copy.size() == 0) {
        throw new IOException("No source lines!");
      }

      final MessageDigest md = MessageDigest.getInstance("SHA-256");
      for (final String line : copy) {
        md.update(line.getBytes());
      }

      final StringBuilder hash = new StringBuilder();
      for (final byte b : md.digest()) {
        hash.append(String.format("%02x", b));
      }

      final String r = hash.toString();
      assert r != null;

      log.debug("hash " + r);
      return new HashedLines(copy, r);
    } catch (final NoSuchAlgorithmException e) {
      throw new UnreachableCodeException(e);
    }
  }

  /**
   * Load source from the given lines.
   * 
   * @param lines
   *          The lines.
   * @param log
   *          A log interface.
   * @return Lines of source code.
   * 
   * @throws IOException
   *           If an I/O error occurs, or the program is empty.
   */

  public static HashedLines newSource(
    final List<String> lines,
    final LogUsableType log)
    throws IOException
  {
    return HashedLines.newActual(lines, log, false);
  }

  /**
   * Copy the given source, stripping version directives.
   * 
   * @param lines
   *          The lines.
   * @param log
   *          A log interface.
   * @return Lines of source code.
   * 
   * @throws IOException
   *           If an I/O error occurs, or the program is empty.
   */

  public static HashedLines newSourceStripped(
    final List<String> lines,
    final LogUsableType log)
    throws IOException
  {
    return HashedLines.newActual(lines, log, true);
  }

  private static List<String> readLinesStream(
    final InputStream stream)
    throws IOException
  {
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

  private final String       hash;
  private final List<String> lines;

  private HashedLines(
    final List<String> in_lines,
    final String in_hash)
  {
    this.lines = NullCheck.notNullAll(in_lines, "Lines");
    this.hash = NullCheck.notNull(in_hash, "Hash");
  }

  /**
   * @return The SHA-256 hash of the source lines.
   */

  public String getHash()
  {
    return this.hash;
  }

  /**
   * @return A read-only view of the source lines.
   */

  public List<String> getLines()
  {
    final List<String> r = Collections.unmodifiableList(this.lines);
    assert r != null;
    return r;
  }
}
