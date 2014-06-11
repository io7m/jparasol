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
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.Serializer;

import org.junit.Test;
import org.xml.sax.SAXException;

import com.io7m.jparasol.CompilerError;
import com.io7m.jparasol.glsl.GMeta;
import com.io7m.jparasol.glsl.GVersion.GVersionES;
import com.io7m.jparasol.glsl.GVersion.GVersionFull;
import com.io7m.jparasol.glsl.pipeline.GCompilation;
import com.io7m.jparasol.glsl.pipeline.GCompiledProgram;
import com.io7m.jparasol.glsl.pipeline.GPipeline;
import com.io7m.jparasol.tests.TestPipeline;
import com.io7m.jparasol.tests.TestUtilities;
import com.io7m.jparasol.typed.ast.TASTShaderNameFlat;
import com.io7m.jparasol.xml.ShaderMeta;

@SuppressWarnings({ "null", "static-method" }) public final class GMetaValidationTest
{
  /**
   * Ensure that the output of GMeta is valid according to the schema.
   * 
   * @throws CompilerError
   *           On errors.
   */

  @Test public void testValidation()
    throws IOException,
      ParsingException,
      SAXException,
      ParserConfigurationException,
      CompilerError
  {
    final GPipeline gpipe =
      TestPipeline.makeGPipeline(new String[] { "glsl/meta/large.p" });

    final Set<TASTShaderNameFlat> program_names =
      new HashSet<TASTShaderNameFlat>();
    final TASTShaderNameFlat program_name =
      TestPipeline.shaderName("x.M", "p");
    program_names.add(program_name);
    final GCompilation comp =
      gpipe
        .transformPrograms(program_names, GVersionES.ALL, GVersionFull.ALL);
    final GCompiledProgram p = comp.getShadersProgram().get(program_name);
    final Document meta = GMeta.ofProgram(p);
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
    ShaderMeta.fromStream(input, TestUtilities.getLog());
  }
}
