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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.Serializer;

import org.junit.Test;
import org.xml.sax.SAXException;

import com.io7m.jfunctional.Pair;
import com.io7m.jparasol.UIError;
import com.io7m.jparasol.glsl.GFFIError;
import com.io7m.jparasol.glsl.GMeta;
import com.io7m.jparasol.glsl.GVersion;
import com.io7m.jparasol.glsl.GVersion.GVersionES;
import com.io7m.jparasol.glsl.GVersion.GVersionFull;
import com.io7m.jparasol.glsl.GVersionCheckerError;
import com.io7m.jparasol.glsl.ast.GASTShader.GASTShaderFragment;
import com.io7m.jparasol.glsl.ast.GASTShader.GASTShaderVertex;
import com.io7m.jparasol.glsl.pipeline.GPipeline;
import com.io7m.jparasol.tests.TestPipeline;
import com.io7m.jparasol.tests.TestUtilities;
import com.io7m.jparasol.xml.PGLSLMetaXML;

@SuppressWarnings("static-method") public final class GMetaValidationTest
{
  /**
   * Ensure that the output of GMeta is valid according to the schema.
   */

  @Test public void testValidation()
    throws UIError,
      GFFIError,
      GVersionCheckerError,
      IOException,
      ParsingException,
      SAXException,
      ParserConfigurationException
  {
    final GPipeline gpipe =
      TestPipeline.makeGPipeline(new String[] { "glsl/meta/large.p" });

    final Map<GVersion, Pair<GASTShaderVertex, GASTShaderFragment>> asts =
      gpipe.makeProgram(
        TestPipeline.shaderName("x.M", "p"),
        GVersionES.ALL,
        GVersionFull.ALL);

    final Document meta =
      GMeta.make(TestPipeline.shaderName("x.M", "p"), asts);

    final ByteArrayOutputStream output = new ByteArrayOutputStream();

    {
      final Serializer serial = new Serializer(output, "UTF-8");
      serial.setIndent(2);
      serial.setMaxLength(80);
      serial.write(meta);
      serial.flush();
    }

    {
      final Serializer serial = new Serializer(System.out, "UTF-8");
      serial.setIndent(2);
      serial.setMaxLength(80);
      serial.write(meta);
      serial.flush();
    }

    final byte[] bytes = output.toByteArray();
    final ByteArrayInputStream input = new ByteArrayInputStream(bytes);
    PGLSLMetaXML.fromStream(input, TestUtilities.getLog());
  }
}
