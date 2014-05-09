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

package com.io7m.jparasol.tests;

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jparasol.NameRestrictions;
import com.io7m.jparasol.NameRestrictions.NameRestricted;

@SuppressWarnings("static-method") public class NameRestrictionsTest
{
  @Test public void testDoubleUnderscore()
  {
    Assert.assertEquals(
      NameRestricted.NAME_RESTRICTED_CONTAINS_DOUBLE_UNDERSCORE,
      NameRestrictions.checkRestricted("a__b"));
  }

  @Test public void testEndsWithUnderscore()
  {
    Assert.assertEquals(
      NameRestricted.NAME_RESTRICTED_ENDS_WITH_UNDERSCORE,
      NameRestrictions.checkRestricted("a_"));
  }

  @Test public void testglPrefix()
  {
    Assert.assertEquals(
      NameRestricted.NAME_RESTRICTED_PREFIX_GL_UPPER,
      NameRestrictions.checkRestricted("GL"));
  }

  @Test public void testGLPrefix()
  {
    Assert.assertEquals(
      NameRestricted.NAME_RESTRICTED_PREFIX_GL_LOWER,
      NameRestrictions.checkRestricted("gl"));
  }

  @Test public void testKeywords()
  {
    for (final String k : NameRestrictions.KEYWORDS) {
      Assert.assertEquals(
        NameRestricted.NAME_RESTRICTED_KEYWORD,
        NameRestrictions.checkRestricted(k));
    }
  }
}
