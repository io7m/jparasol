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

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;

import javax.annotation.Nonnull;
import javax.xml.parsers.ParserConfigurationException;

import nu.xom.ParsingException;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jlog.Log;

public class PGLSLMetaXMLTest
{
  @SuppressWarnings("static-method") @Test(expected = ConstraintError.class) public
    void
    testNull()
      throws ConstraintError,
        ParsingException,
        IOException,
        SAXException,
        ParserConfigurationException
  {
    PGLSLMetaXML.fromStream(null, PGLSLMetaXMLTest.getLog());
  }

  private static Log getLog()
  {
    final Properties props = new Properties();
    return new Log(props, "com.io7m.parasol.xml.xml.xml", "test");
  }

  @SuppressWarnings("static-method") @Test(expected = SAXException.class) public
    void
    testWrongNamespace()
      throws ConstraintError,
        ParsingException,
        IOException,
        SAXException,
        ParserConfigurationException
  {
    PGLSLMetaXML.fromStream(
      PGLSLMetaXMLTest.get("t-wrong-namespace.xml"),
      PGLSLMetaXMLTest.getLog());
  }

  @SuppressWarnings("static-method") @Test(expected = SAXException.class) public
    void
    testWrongRoot()
      throws ConstraintError,
        ParsingException,
        IOException,
        SAXException,
        ParserConfigurationException
  {
    PGLSLMetaXML.fromStream(
      PGLSLMetaXMLTest.get("t-wrong-root.xml"),
      PGLSLMetaXMLTest.getLog());
  }

  @SuppressWarnings("static-method") @Test(expected = SAXException.class) public
    void
    testEmpty()
      throws ConstraintError,
        ParsingException,
        IOException,
        SAXException,
        ParserConfigurationException
  {
    PGLSLMetaXML.fromStream(
      PGLSLMetaXMLTest.get("t-empty.xml"),
      PGLSLMetaXMLTest.getLog());
  }

  @SuppressWarnings("static-method") @Test(expected = SAXException.class) public
    void
    testProgramNameWrong()
      throws ConstraintError,
        ParsingException,
        IOException,
        SAXException,
        ParserConfigurationException
  {
    PGLSLMetaXML.fromStream(
      PGLSLMetaXMLTest.get("t-program-name-wrong.xml"),
      PGLSLMetaXMLTest.getLog());
  }

  @SuppressWarnings("static-method") @Test public void testMinimal()
    throws ConstraintError,
      ParsingException,
      IOException,
      SAXException,
      ParserConfigurationException
  {
    final PGLSLMetaXML p =
      PGLSLMetaXML.fromStream(
        PGLSLMetaXMLTest.get("t-minimal.xml"),
        PGLSLMetaXMLTest.getLog());

    Assert.assertEquals("com.io7m.example.p", p.getName());
    Assert.assertEquals(1, p.getDeclaredFragmentOutputs().size());
    Assert.assertEquals(0, p.getSupportsES().size());
    Assert.assertEquals(1, p.getSupportsFull().size());
  }

  @SuppressWarnings("static-method") @Test public void testActual()
    throws ConstraintError,
      ParsingException,
      IOException,
      SAXException,
      ParserConfigurationException
  {
    final PGLSLMetaXML p =
      PGLSLMetaXML.fromStream(
        PGLSLMetaXMLTest.get("t-actual.xml"),
        PGLSLMetaXMLTest.getLog());

    Assert
      .assertEquals(
        "com.io7m.renderer.ForwardLitDirectionalSpecularNormalMapped.fwd_LD_S_N",
        p.getName());

    Assert.assertEquals(3, p.getDeclaredFragmentOutputs().size());

    {
      final FragmentOutput o0 =
        p.getDeclaredFragmentOutputs().get(Integer.valueOf(0));
      Assert.assertEquals(Integer.valueOf(0), o0.getIndex());
      Assert.assertEquals("out_0", o0.getName());
      Assert.assertEquals("vec4", o0.getType());
    }

    {
      final FragmentOutput o1 =
        p.getDeclaredFragmentOutputs().get(Integer.valueOf(1));
      Assert.assertEquals(Integer.valueOf(1), o1.getIndex());
      Assert.assertEquals("out_1", o1.getName());
      Assert.assertEquals("vec4", o1.getType());
    }

    {
      final FragmentOutput o2 =
        p.getDeclaredFragmentOutputs().get(Integer.valueOf(2));
      Assert.assertEquals(Integer.valueOf(2), o2.getIndex());
      Assert.assertEquals("out_2", o2.getName());
      Assert.assertEquals("vec4", o2.getType());
    }

    Assert.assertEquals(3, p.getDeclaredFragmentInputs().size());

    {
      final Iterator<FragmentInput> iter =
        p.getDeclaredFragmentInputs().iterator();

      {
        final FragmentInput i = iter.next();
        Assert.assertEquals("f_normal", i.getName());
        Assert.assertEquals("vec3", i.getType());
      }

      {
        final FragmentInput i = iter.next();
        Assert.assertEquals("f_position", i.getName());
        Assert.assertEquals("vec4", i.getType());
      }

      {
        final FragmentInput i = iter.next();
        Assert.assertEquals("f_tangent", i.getName());
        Assert.assertEquals("vec3", i.getType());
      }
    }

    {
      final Iterator<FragmentParameter> iter =
        p.getDeclaredFragmentParameters().iterator();

      {
        final FragmentParameter i = iter.next();
        Assert.assertEquals("other", i.getName());
        Assert.assertEquals("int", i.getType());
      }

      {
        final FragmentParameter i = iter.next();
        Assert.assertEquals("t_albedo", i.getName());
        Assert.assertEquals("sampler2D", i.getType());
      }

      {
        final FragmentParameter i = iter.next();
        Assert.assertEquals("t_normal", i.getName());
        Assert.assertEquals("sampler2D", i.getType());
      }
    }

    {
      final Iterator<VertexInput> iter =
        p.getDeclaredVertexInputs().iterator();

      {
        final VertexInput i = iter.next();
        Assert.assertEquals("v_normal", i.getName());
        Assert.assertEquals("vec3", i.getType());
      }

      {
        final VertexInput i = iter.next();
        Assert.assertEquals("v_position", i.getName());
        Assert.assertEquals("vec4", i.getType());
      }
    }

    {
      final Iterator<VertexOutput> iter =
        p.getDeclaredVertexOutputs().iterator();

      {
        final VertexOutput i = iter.next();
        Assert.assertEquals("f_normal", i.getName());
        Assert.assertEquals("vec3", i.getType());
      }

      {
        final VertexOutput i = iter.next();
        Assert.assertEquals("f_position", i.getName());
        Assert.assertEquals("vec4", i.getType());
      }

      {
        final VertexOutput i = iter.next();
        Assert.assertEquals("f_tangent", i.getName());
        Assert.assertEquals("vec3", i.getType());
      }
    }

    {
      final Iterator<VertexParameter> iter =
        p.getDeclaredVertexParameters().iterator();

      {
        final VertexParameter i = iter.next();
        Assert.assertEquals("something", i.getName());
        Assert.assertEquals("int", i.getType());
      }
    }

    Assert.assertEquals(2, p.getSupportsES().size());
    Assert.assertEquals(10, p.getSupportsFull().size());
  }

  private static @Nonnull InputStream get(
    final @Nonnull String file)
  {
    final InputStream s =
      PGLSLMetaXMLTest.class.getResourceAsStream("/com/io7m/jparasol/xml/"
        + file);
    return s;
  }
}
