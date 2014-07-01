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

package com.io7m.jparasol.frontend;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import com.io7m.jlog.LogUsableType;
import com.io7m.jnull.NullCheck;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * Utility functions for copying zip files.
 */

public final class CopyZip
{
  private CopyZip()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Rename the zip file at <code>file</code> to <code>file.tmp</code>, copy
   * the contents of <code>file.tmp</code> to <code>file</code> and return a
   * stream that allows new items to be appended to <code>file</code>.
   * 
   * @param log
   *          A log interface
   * @param file
   *          A zip file
   * @return An output stream for <code>file</code>
   * @throws ZipException
   *           On zip i/o errors.
   * @throws IOException
   *           On i/o errors.
   */

  @SuppressWarnings("null") public static ZipOutputStream copyZip(
    final LogUsableType log,
    final File file)
    throws ZipException,
      IOException
  {
    NullCheck.notNull(log, "Log");
    NullCheck.notNull(file, "File");

    final File in_zip_tmp =
      new File(String.format("%s.tmp", file.toString()));
    log.debug(String.format("renaming '%s' to '%s'", file, in_zip_tmp));

    final boolean r = file.renameTo(in_zip_tmp);
    if (r == false) {
      throw new IOException(String.format(
        "Renaming '%s' to '%s' failed",
        file,
        in_zip_tmp));
    }

    log.debug(String.format("copying '%s' to '%s'", in_zip_tmp, file));

    final ZipFile in_zip = new ZipFile(in_zip_tmp);

    final ZipOutputStream out_zip_stream =
      new ZipOutputStream(
        new FileOutputStream(file),
        Charset.forName("UTF-8"));

    final Enumeration<? extends ZipEntry> entries = in_zip.entries();
    while (entries.hasMoreElements()) {
      final ZipEntry e = entries.nextElement();
      out_zip_stream.putNextEntry(e);
      CopyZip.copyZipEntry(in_zip.getInputStream(e), out_zip_stream);
      out_zip_stream.closeEntry();
    }

    in_zip.close();
    return out_zip_stream;
  }

  private static void copyZipEntry(
    final InputStream in,
    final OutputStream out)
    throws IOException
  {
    final byte[] buffer = new byte[65536];

    for (;;) {
      final int r = in.read(buffer);
      if (r == -1) {
        break;
      }
      out.write(buffer, 0, r);
    }

    out.flush();
  }
}
