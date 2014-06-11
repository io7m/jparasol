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

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.annotation.Nonnull;
import javax.xml.parsers.ParserConfigurationException;

import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.io7m.jlog.Log;
import com.io7m.jlog.LogLevel;
import com.io7m.jlog.LogPolicyAllOn;
import com.io7m.jlog.LogPolicyType;
import com.io7m.jlog.LogType;
import com.io7m.jnull.NonNull;
import com.io7m.jnull.NullCheckException;
import com.io7m.jparasol.xml.FragmentInput;
import com.io7m.jparasol.xml.FragmentOutput;
import com.io7m.jparasol.xml.FragmentParameter;
import com.io7m.jparasol.xml.ShaderMeta;
import com.io7m.jparasol.xml.ShaderMetaFragment;
import com.io7m.jparasol.xml.ShaderMetaProgram;
import com.io7m.jparasol.xml.ShaderMetaVertex;
import com.io7m.jparasol.xml.VertexInput;
import com.io7m.jparasol.xml.VertexOutput;
import com.io7m.jparasol.xml.VertexParameter;

@SuppressWarnings({ "null", "static-method" }) public class ShaderMetaXMLTest
{
  private static final Object z = null;

  @SuppressWarnings("unchecked") private static @NonNull <T> T actuallyNull()
  {
    return (T) ShaderMetaXMLTest.z;
  }

  private static @Nonnull InputStream get(
    final @Nonnull String file)
  {
    final InputStream s =
      ShaderMetaXMLTest.class
        .getResourceAsStream("/com/io7m/jparasol/tests/xml/" + file);
    return s;
  }

  private static LogType getLog()
  {
    final LogPolicyType policy = LogPolicyAllOn.newPolicy(LogLevel.LOG_DEBUG);
    return Log.newLog(policy, "tests");
  }

  @Test public void testActualFragment()
    throws ParsingException,
      IOException,
      SAXException,
      ParserConfigurationException
  {
    final ShaderMetaFragment p =
      (ShaderMetaFragment) ShaderMeta.fromStream(
        ShaderMetaXMLTest.get("t-actual-fragment.xml"),
        ShaderMetaXMLTest.getLog());

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

    Assert.assertEquals(2, p.getSupportsES().size());
    Assert.assertEquals(10, p.getSupportsFull().size());
  }

  @Test public void testActualProgram()
    throws ParsingException,
      IOException,
      SAXException,
      ParserConfigurationException
  {
    final ShaderMetaProgram p =
      (ShaderMetaProgram) ShaderMeta.fromStream(
        ShaderMetaXMLTest.get("t-actual-program.xml"),
        ShaderMetaXMLTest.getLog());

    Assert
      .assertEquals(
        "com.io7m.renderer.ForwardLitDirectionalSpecularNormalMapped.fwd_LD_S_N",
        p.getName());

    Assert
      .assertEquals(
        "com.io7m.renderer.ForwardLitDirectionalSpecularNormalMapped.fwd_LD_S_N",
        p.getFragmentShader());
    Assert.assertEquals(2, p.getVertexShaders().size());
    Assert.assertTrue(p.getVertexShaders().contains(
      "com.io7m.renderer.Vertex.standard_0"));
    Assert.assertTrue(p.getVertexShaders().contains(
      "com.io7m.renderer.Vertex.standard_1"));

    Assert.assertEquals(2, p.getSupportsES().size());
    Assert.assertEquals(10, p.getSupportsFull().size());
  }

  @Test public void testActualVertex()
    throws ParsingException,
      IOException,
      SAXException,
      ParserConfigurationException
  {
    final ShaderMetaVertex p =
      (ShaderMetaVertex) ShaderMeta.fromStream(
        ShaderMetaXMLTest.get("t-actual-vertex.xml"),
        ShaderMetaXMLTest.getLog());

    Assert
      .assertEquals(
        "com.io7m.renderer.ForwardLitDirectionalSpecularNormalMapped.fwd_LD_S_N",
        p.getName());

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

  @Test(expected = SAXException.class) public void testEmpty()
    throws ParsingException,
      IOException,
      SAXException,
      ParserConfigurationException
  {
    ShaderMeta.fromStream(
      ShaderMetaXMLTest.get("t-empty.xml"),
      ShaderMetaXMLTest.getLog());
  }

  @Test public void testMinimalFragment()
    throws ParsingException,
      IOException,
      SAXException,
      ParserConfigurationException
  {
    final ShaderMetaFragment p =
      (ShaderMetaFragment) ShaderMeta.fromStream(
        ShaderMetaXMLTest.get("t-minimal-fragment.xml"),
        ShaderMetaXMLTest.getLog());

    Assert.assertEquals("com.io7m.example.p", p.getName());
    Assert.assertEquals(1, p.getDeclaredFragmentOutputs().size());
    Assert.assertEquals(0, p.getDeclaredFragmentParameters().size());
    Assert.assertEquals(0, p.getDeclaredFragmentInputs().size());
    Assert.assertEquals(0, p.getSupportsES().size());
    Assert.assertEquals(1, p.getSupportsFull().size());
  }

  @Test public void testMinimalProgram()
    throws ParsingException,
      IOException,
      SAXException,
      ParserConfigurationException
  {
    final ShaderMetaProgram p =
      (ShaderMetaProgram) ShaderMeta.fromStream(
        ShaderMetaXMLTest.get("t-minimal-program.xml"),
        ShaderMetaXMLTest.getLog());

    Assert.assertEquals("com.io7m.example.p", p.getName());
    Assert.assertEquals(0, p.getSupportsES().size());
    Assert.assertEquals(1, p.getSupportsFull().size());
    Assert.assertEquals("com.io7m.renderer.example.f", p.getFragmentShader());
    Assert.assertEquals(1, p.getVertexShaders().size());
    Assert.assertTrue(p.getVertexShaders().contains(
      "com.io7m.renderer.Vertex.standard_0"));
  }

  @Test public void testMinimalVertex()
    throws ParsingException,
      IOException,
      SAXException,
      ParserConfigurationException
  {
    final ShaderMetaVertex p =
      (ShaderMetaVertex) ShaderMeta.fromStream(
        ShaderMetaXMLTest.get("t-minimal-vertex.xml"),
        ShaderMetaXMLTest.getLog());

    Assert.assertEquals("com.io7m.example.p", p.getName());
    Assert.assertEquals(1, p.getDeclaredVertexOutputs().size());
    Assert.assertEquals(0, p.getDeclaredVertexParameters().size());
    Assert.assertEquals(0, p.getDeclaredVertexInputs().size());
    Assert.assertEquals(0, p.getSupportsES().size());
    Assert.assertEquals(1, p.getSupportsFull().size());
  }

  @Test(expected = NullCheckException.class) public void testNull()
    throws ParsingException,
      IOException,
      SAXException,
      ParserConfigurationException
  {
    ShaderMeta.fromStream(
      (InputStream) ShaderMetaXMLTest.actuallyNull(),
      ShaderMetaXMLTest.getLog());
  }

  @Test(expected = SAXException.class) public void testProgramNameWrong()
    throws ParsingException,
      IOException,
      SAXException,
      ParserConfigurationException
  {
    ShaderMeta.fromStream(
      ShaderMetaXMLTest.get("t-program-name-wrong.xml"),
      ShaderMetaXMLTest.getLog());
  }

  @Test(expected = ValidityException.class) public
    void
    testProgramVersionWrong()
      throws ParsingException,
        IOException,
        SAXException,
        ParserConfigurationException
  {
    ShaderMeta.fromStream(
      ShaderMetaXMLTest.get("t-version-wrong.xml"),
      ShaderMetaXMLTest.getLog());
  }

  @Test(expected = SAXException.class) public void testWrongNamespace()
    throws ParsingException,
      IOException,
      SAXException,
      ParserConfigurationException
  {
    ShaderMeta.fromStream(
      ShaderMetaXMLTest.get("t-wrong-namespace.xml"),
      ShaderMetaXMLTest.getLog());
  }

  @Test(expected = SAXException.class) public void testWrongRoot()
    throws ParsingException,
      IOException,
      SAXException,
      ParserConfigurationException
  {
    ShaderMeta.fromStream(
      ShaderMetaXMLTest.get("t-wrong-root.xml"),
      ShaderMetaXMLTest.getLog());
  }
}
