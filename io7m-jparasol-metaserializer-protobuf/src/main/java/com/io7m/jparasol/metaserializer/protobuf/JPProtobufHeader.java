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

package com.io7m.jparasol.metaserializer.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jintegers.Unsigned32;
import com.io7m.jparasol.metaserializer.JPSerializerException;
import com.io7m.junreachable.UnreachableCodeException;

@EqualityReference final class JPProtobufHeader
{
  /**
   * The maximum size of serialized shader metadata.
   */

  public static final int MAXIMUM_DATA_SIZE = 1000000;

  @SuppressWarnings("boxing") private static void checkHeader(
    final byte[] b)
    throws JPSerializerException
  {
    boolean ok = true;
    ok = ok && (b[0] == 'P');
    ok = ok && (b[1] == 'P');
    ok = ok && (b[2] == 'S');
    ok = ok && (b[3] == 'M');

    if (!ok) {
      final StringBuilder s = new StringBuilder();
      final String exp =
        String.format(
          "  Expected: %x %x %x %x\n",
          (int) 'P',
          (int) 'P',
          (int) 'S',
          (int) 'M');
      final String got =
        String.format("  Got:      %x %x %x %x\n", b[0], b[1], b[2], b[3]);
      s.append("Invalid header on file.\n");
      s.append(exp);
      s.append(got);
      final String r = s.toString();
      assert r != null;
      throw new JPSerializerException(r);
    }
  }

  static void readAndCheckVersion(
    final InputStream in)
    throws IOException,
      JPSerializerException
  {
    final int v = JPProtobufHeader.readHeader(in);
    if (v != JPProtobufMetaSerializer.META_VERSION) {
      final StringBuilder s = new StringBuilder();
      s.append("Unsupported metadata version.\n");
      s.append("  Expected: ");
      s.append(JPProtobufMetaSerializer.META_VERSION);
      s.append("\n");
      s.append("  Got: ");
      s.append(v);
      s.append("\n");
      final String r = s.toString();
      assert r != null;
      throw new JPSerializerException(r);
    }
  }

  static int readHeader(
    final InputStream in)
    throws IOException,
      JPSerializerException
  {
    final byte[] b = new byte[4];
    in.read(b, 0, 4);
    JPProtobufHeader.checkHeader(b);
    in.read(b, 0, 4);
    final ByteBuffer bb = ByteBuffer.wrap(b);
    bb.order(ByteOrder.BIG_ENDIAN);
    return (int) Unsigned32.unpackFromBuffer(bb, 0);
  }

  static int readSize(
    final InputStream in)
    throws IOException,
      JPSerializerException
  {
    final byte[] b = new byte[4];
    in.read(b, 0, 4);
    final ByteBuffer bb = ByteBuffer.wrap(b);
    bb.order(ByteOrder.BIG_ENDIAN);
    final int r = (int) Unsigned32.unpackFromBuffer(bb, 0);
    if ((r < 1) || (r > JPProtobufHeader.MAXIMUM_DATA_SIZE)) {
      final StringBuilder s = new StringBuilder();
      s.append("Invalid data size received.\n");
      s.append("  Expected: 0 < r < ");
      s.append(JPProtobufHeader.MAXIMUM_DATA_SIZE);
      s.append("\n");
      s.append("  Got: r == ");
      s.append(r);
      s.append("\n");
      final String rs = s.toString();
      assert rs != null;
      throw new JPSerializerException(rs);
    }
    return r;
  }

  static void writeHeaderAndSize(
    final OutputStream out,
    final int size)
    throws IOException
  {
    final ByteBuffer b = ByteBuffer.allocate(4);
    b.order(ByteOrder.BIG_ENDIAN);
    b.put(0, (byte) 'P');
    b.put(1, (byte) 'P');
    b.put(2, (byte) 'S');
    b.put(3, (byte) 'M');
    out.write(b.array(), 0, 4);
    Unsigned32.packToBuffer(JPProtobufMetaSerializer.META_VERSION, b, 0);
    out.write(b.array(), 0, 4);
    Unsigned32.packToBuffer(size, b, 0);
    out.write(b.array(), 0, 4);
  }

  private JPProtobufHeader()
  {
    throw new UnreachableCodeException();
  }
}
