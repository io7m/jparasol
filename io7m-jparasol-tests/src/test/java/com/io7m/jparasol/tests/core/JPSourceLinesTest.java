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

package com.io7m.jparasol.tests.core;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jparasol.core.JPSourceLines;

@SuppressWarnings({ "null", "static-method" }) public final class JPSourceLinesTest
{
  @Test public void testLines()
    throws IOException
  {
    final List<String> lines =
      JPSourceLines.fromStream(JPSourceLinesTest.class
        .getResourceAsStream("/com/io7m/jparasol/tests/core/lines.txt"));
    Assert.assertEquals(5, lines.size());
    Assert.assertEquals("Line 0", lines.get(0));
    Assert.assertEquals("Line 1", lines.get(1));
    Assert.assertEquals("Line 2", lines.get(2));
    Assert.assertEquals("Line 3", lines.get(3));
    Assert.assertEquals("Line 4", lines.get(4));
  }
}
