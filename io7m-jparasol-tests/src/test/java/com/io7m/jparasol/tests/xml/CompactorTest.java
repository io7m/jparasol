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

package com.io7m.jparasol.tests.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.SortedMap;
import java.util.SortedSet;

import javax.annotation.Nonnull;
import javax.xml.parsers.ParserConfigurationException;

import nu.xom.ParsingException;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.io7m.jfunctional.Some;
import com.io7m.jlog.LogUsableType;
import com.io7m.jnull.NonNull;
import com.io7m.jnull.NullCheckException;
import com.io7m.jparasol.xml.Compactor;
import com.io7m.jparasol.xml.CompactorException;
import com.io7m.jparasol.xml.FragmentInput;
import com.io7m.jparasol.xml.FragmentOutput;
import com.io7m.jparasol.xml.FragmentParameter;
import com.io7m.jparasol.xml.ShaderMeta;
import com.io7m.jparasol.xml.ShaderMetaFragment;
import com.io7m.jparasol.xml.ShaderMetaProgram;
import com.io7m.jparasol.xml.ShaderMetaType;
import com.io7m.jparasol.xml.ShaderMetaVertex;
import com.io7m.jparasol.xml.Version;
import com.io7m.jparasol.xml.VertexInput;
import com.io7m.jparasol.xml.VertexOutput;
import com.io7m.jparasol.xml.VertexParameter;
import com.io7m.junreachable.UnreachableCodeException;

@SuppressWarnings({ "null", "static-method" }) public final class CompactorTest
{
  private static final Object z = null;

  @SuppressWarnings("unchecked") private static @NonNull <T> T actuallyNull()
  {
    return (T) CompactorTest.z;
  }

  static @Nonnull ShaderMetaType parseMeta(
    final File file)
  {
    try {
      final InputStream stream = new FileInputStream(file);
      final ShaderMetaType meta =
        ShaderMeta.fromStream(stream, TestData.getLog());
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
    }
  }

  private static @Nonnull File temporary(
    final @Nonnull String name)
    throws FileNotFoundException,
      IOException
  {
    final File dir = new File(TestData.getTestDataDirectory(), name);
    return dir;
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

  @Test(expected = IllegalArgumentException.class) public
    void
    testAlreadyCompactedFragment()
      throws CompactorException,
        FileNotFoundException,
        IOException
  {
    Compactor.compactShader(
      this.get("fwd_LS_AO_BT_NV_fragment_compact"),
      CompactorTest.temporary("alreadyCompacted"),
      TestData.getLog());
  }

  @Test(expected = IllegalArgumentException.class) public
    void
    testAlreadyCompactedVertex()
      throws CompactorException,
        FileNotFoundException,
        IOException
  {
    Compactor.compactShader(
      this.get("fwd_LS_AO_BT_NV_vertex_compact"),
      CompactorTest.temporary("alreadyCompacted"),
      TestData.getLog());
  }

  @Test public void testCompactCorrectFragment()
    throws CompactorException,
      FileNotFoundException,
      IOException
  {
    final File temp = CompactorTest.temporary("newlyCompacted");
    final File orig = this.get("fwd_LS_AO_BT_NV_fragment");
    final File meta_file = new File(orig, "meta.xml");
    final ShaderMetaFragment meta_orig =
      (ShaderMetaFragment) CompactorTest.parseMeta(meta_file);

    Compactor.compactShader(orig, temp, TestData.getLog());

    final File meta_compact_file = new File(temp, "meta.xml");
    final ShaderMetaFragment meta_compact =
      (ShaderMetaFragment) CompactorTest.parseMeta(meta_compact_file);

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

    final Some<SortedMap<Version, String>> some =
      (Some<SortedMap<Version, String>>) meta_compact.getCompactMappings();

    Assert.assertTrue(some.get().size() > 1);
  }

  @Test public void testCompactCorrectProgram()
    throws CompactorException,
      FileNotFoundException,
      IOException
  {
    final File temp = CompactorTest.temporary("newlyCompacted");
    final File orig = this.get("fwd_LS_AO_BT_NV_program");
    final File meta_file = new File(orig, "meta.xml");
    final ShaderMetaProgram meta_orig =
      (ShaderMetaProgram) CompactorTest.parseMeta(meta_file);

    Compactor.compactShader(orig, temp, TestData.getLog());

    final File meta_compact_file = new File(temp, "meta.xml");
    final ShaderMetaProgram meta_compact =
      (ShaderMetaProgram) CompactorTest.parseMeta(meta_compact_file);

    Assert.assertTrue(meta_compact.getCompactMappings().isNone());
    Assert.assertEquals(meta_orig, meta_compact);
  }

  @Test public void testCompactCorrectVertex()
    throws CompactorException,
      FileNotFoundException,
      IOException
  {
    final File temp = CompactorTest.temporary("newlyCompacted");
    final File orig = this.get("fwd_LS_AO_BT_NV_vertex");
    final File meta_file = new File(orig, "meta.xml");
    final ShaderMetaVertex meta_orig =
      (ShaderMetaVertex) CompactorTest.parseMeta(meta_file);

    Compactor.compactShader(orig, temp, TestData.getLog());

    final File meta_compact_file = new File(temp, "meta.xml");
    final ShaderMetaVertex meta_compact =
      (ShaderMetaVertex) CompactorTest.parseMeta(meta_compact_file);

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

    final Some<SortedMap<Version, String>> some =
      (Some<SortedMap<Version, String>>) meta_compact.getCompactMappings();

    Assert.assertTrue(some.get().size() > 1);
  }

  @Test(expected = NullCheckException.class) public void testNull0()
    throws CompactorException
  {
    Compactor.compactShader(
      (File) CompactorTest.actuallyNull(),
      (File) CompactorTest.actuallyNull(),
      (LogUsableType) CompactorTest.actuallyNull());
  }

  @Test(expected = NullCheckException.class) public void testNull1()
    throws CompactorException
  {
    Compactor.compactShader(
      new File("x"),
      (File) CompactorTest.actuallyNull(),
      (LogUsableType) CompactorTest.actuallyNull());
  }

  @Test(expected = NullCheckException.class) public void testNull2()
    throws CompactorException
  {
    Compactor.compactShader(
      new File("x"),
      new File("x"),
      (LogUsableType) CompactorTest.actuallyNull());
  }
}
