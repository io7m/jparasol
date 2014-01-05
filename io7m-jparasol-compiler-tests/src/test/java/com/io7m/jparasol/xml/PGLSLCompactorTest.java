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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.SortedMap;
import java.util.SortedSet;

import javax.annotation.Nonnull;
import javax.xml.parsers.ParserConfigurationException;

import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;

public final class PGLSLCompactorTest
{
  @SuppressWarnings("static-method") @Test(
    expected = Constraints.ConstraintError.class) public void testNull0()
    throws ValidityException,
      NoSuchAlgorithmException,
      ParsingException,
      IOException,
      SAXException,
      ParserConfigurationException,
      ConstraintError
  {
    PGLSLCompactor.newCompactor(null, null, null);
  }

  @SuppressWarnings("static-method") @Test(
    expected = Constraints.ConstraintError.class) public void testNull1()
    throws ValidityException,
      NoSuchAlgorithmException,
      ParsingException,
      IOException,
      SAXException,
      ParserConfigurationException,
      ConstraintError
  {
    PGLSLCompactor.newCompactor(new File("x"), null, null);
  }

  @SuppressWarnings("static-method") @Test(
    expected = Constraints.ConstraintError.class) public void testNull2()
    throws ValidityException,
      NoSuchAlgorithmException,
      ParsingException,
      IOException,
      SAXException,
      ParserConfigurationException,
      ConstraintError
  {
    PGLSLCompactor.newCompactor(new File("x"), new File("x"), null);
  }

  @Test(expected = Constraints.ConstraintError.class) public
    void
    testAlreadyCompacted()
      throws ValidityException,
        NoSuchAlgorithmException,
        ParsingException,
        IOException,
        SAXException,
        ParserConfigurationException,
        ConstraintError
  {
    PGLSLCompactor.newCompactor(
      this.get("fwd_LS_AO_BT_NV_compact"),
      PGLSLCompactorTest.temporary("alreadyCompacted"),
      TestData.getLog());
  }

  @Test public void testCompactCorrect()
    throws ValidityException,
      NoSuchAlgorithmException,
      ParsingException,
      IOException,
      SAXException,
      ParserConfigurationException,
      ConstraintError
  {
    final File temp = PGLSLCompactorTest.temporary("newlyCompacted");
    final File orig = this.get("fwd_LS_AO_BT_NV");
    final File meta_file = new File(orig, "meta.xml");
    final PGLSLMetaXML meta_orig = PGLSLCompactorTest.parseMeta(meta_file);

    PGLSLCompactor.newCompactor(orig, temp, TestData.getLog());

    final File meta_compact_file = new File(temp, "meta.xml");
    final PGLSLMetaXML meta_compact =
      PGLSLCompactorTest.parseMeta(meta_compact_file);

    {
      final SortedSet<FragmentInput> orig_data =
        meta_orig.getDeclaredFragmentInputs();
      final SortedSet<FragmentInput> comp_data =
        meta_compact.getDeclaredFragmentInputs();
      Assert.assertEquals(orig_data, comp_data);
    }

    {
      final SortedMap<Integer, FragmentOutput> orig_data =
        meta_orig.getDeclaredFragmentOutputs();
      final SortedMap<Integer, FragmentOutput> comp_data =
        meta_compact.getDeclaredFragmentOutputs();
      Assert.assertEquals(orig_data, comp_data);
    }

    {
      final SortedSet<FragmentParameter> orig_data =
        meta_orig.getDeclaredFragmentParameters();
      final SortedSet<FragmentParameter> comp_data =
        meta_compact.getDeclaredFragmentParameters();
      Assert.assertEquals(orig_data, comp_data);
    }

    {
      final SortedSet<VertexInput> orig_data =
        meta_orig.getDeclaredVertexInputs();
      final SortedSet<VertexInput> comp_data =
        meta_compact.getDeclaredVertexInputs();
      Assert.assertEquals(orig_data, comp_data);
    }

    {
      final SortedSet<VertexOutput> orig_data =
        meta_orig.getDeclaredVertexOutputs();
      final SortedSet<VertexOutput> comp_data =
        meta_compact.getDeclaredVertexOutputs();
      Assert.assertEquals(orig_data, comp_data);
    }

    {
      final SortedSet<VertexParameter> orig_data =
        meta_orig.getDeclaredVertexParameters();
      final SortedSet<VertexParameter> comp_data =
        meta_compact.getDeclaredVertexParameters();
      Assert.assertEquals(orig_data, comp_data);
    }

    Assert.assertTrue(meta_compact.getCompactMappings().size() > 1);
  }

  static @Nonnull PGLSLMetaXML parseMeta(
    final File file)
  {
    try {
      final InputStream stream = new FileInputStream(file);
      final PGLSLMetaXML meta =
        PGLSLMetaXML.fromStream(stream, TestData.getLog());
      stream.close();
      return meta;
    } catch (final IOException e) {
      e.printStackTrace();
      throw new UnreachableCodeException(e);
    } catch (final ParsingException e) {
      e.printStackTrace();
      throw new UnreachableCodeException(e);
    } catch (final SAXException e) {
      e.printStackTrace();
      throw new UnreachableCodeException(e);
    } catch (final ParserConfigurationException e) {
      e.printStackTrace();
      throw new UnreachableCodeException(e);
    } catch (final ConstraintError e) {
      e.printStackTrace();
      throw new UnreachableCodeException(e);
    }
  }

  private @Nonnull File get(
    final String name)
    throws FileNotFoundException,
      IOException
  {
    final File base = TestData.getTestDataDirectory();
    final File ext =
      new File(
        new File(new File(new File(base, "programs"), "com"), "io7m"),
        "parasol");
    return new File(ext, name);
  }

  private static @Nonnull File temporary(
    final @Nonnull String name)
    throws FileNotFoundException,
      IOException
  {
    final File dir = new File(TestData.getTestDataDirectory(), name);
    return dir;
  }
}
