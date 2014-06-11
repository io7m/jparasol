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

package com.io7m.jparasol.glsl;

import java.util.Map;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jfunctional.Pair;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.glsl.GVersion.GVersionES;
import com.io7m.jparasol.glsl.GVersion.GVersionFull;
import com.io7m.jparasol.glsl.ast.GASTShader.GASTShaderFragment;
import com.io7m.jparasol.glsl.ast.GASTShader.GASTShaderFragmentInput;
import com.io7m.jparasol.glsl.ast.GASTShader.GASTShaderFragmentOutput;
import com.io7m.jparasol.glsl.ast.GASTShader.GASTShaderFragmentParameter;
import com.io7m.jparasol.glsl.ast.GASTShader.GASTShaderVertex;
import com.io7m.jparasol.glsl.ast.GASTShader.GASTShaderVertexInput;
import com.io7m.jparasol.glsl.ast.GASTShader.GASTShaderVertexOutput;
import com.io7m.jparasol.glsl.ast.GASTShader.GASTShaderVertexParameter;
import com.io7m.jparasol.glsl.pipeline.GCompiledProgram;
import com.io7m.jparasol.typed.TType;
import com.io7m.jparasol.typed.ast.TASTShaderNameFlat;
import com.io7m.jparasol.xml.ShaderMeta;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * Metadata about a generated GLSL program.
 */

@EqualityReference public final class GMeta
{
  private static void declaredFragmentInputs(
    final GASTShaderFragment program,
    final String uri,
    final Element efi)
  {
    for (final GASTShaderFragmentInput i : program.getInputs()) {
      final Element e = new Element("g:input", uri);
      e.addAttribute(new Attribute("g:name", uri, i.getName().show()));
      e.addAttribute(new Attribute("g:type", uri, i.getTypeName().show()));
      efi.appendChild(e);
    }
  }

  private static void declaredFragmentOutputs(
    final GASTShaderFragment program,
    final String uri,
    final Element efo)
  {
    for (final GASTShaderFragmentOutput i : program.getOutputs()) {
      final Element e = new Element("g:fragment-output", uri);
      e.addAttribute(new Attribute("g:name", uri, i.getName().show()));
      e.addAttribute(new Attribute("g:index", uri, Integer.toString(i
        .getIndex())));
      e.addAttribute(new Attribute("g:type", uri, i.getType().show()));
      efo.appendChild(e);
    }
  }

  private static void declaredFragmentParameters(
    final GASTShaderFragment program,
    final String uri,
    final Element efp)
  {
    for (final GASTShaderFragmentParameter p : program.getParameters()) {
      for (final Pair<String, TType> x : p.getExpanded()) {
        final Element e = new Element("g:parameter", uri);
        e.addAttribute(new Attribute("g:name", uri, x.getLeft()));
        e.addAttribute(new Attribute("g:type", uri, GLSLTypeNames
          .getTypeName(x.getRight())
          .show()));
        efp.appendChild(e);
      }
    }
  }

  private static void declaredVertexInputs(
    final GASTShaderVertex program,
    final String uri,
    final Element evi)
  {
    for (final GASTShaderVertexInput i : program.getInputs()) {
      final Element e = new Element("g:input", uri);
      e.addAttribute(new Attribute("g:name", uri, i.getName().show()));
      e.addAttribute(new Attribute("g:type", uri, i.getType().show()));
      evi.appendChild(e);
    }
  }

  private static void declaredVertexOutputs(
    final GASTShaderVertex program,
    final String uri,
    final Element evo)
  {
    for (final GASTShaderVertexOutput i : program.getOutputs()) {
      final Element e = new Element("g:output", uri);
      e.addAttribute(new Attribute("g:name", uri, i.getName().show()));
      e.addAttribute(new Attribute("g:type", uri, i.getTypeName().show()));
      evo.appendChild(e);
    }
  }

  private static void declaredVertexParameters(
    final GASTShaderVertex program,
    final String uri,
    final Element evp)
  {
    for (final GASTShaderVertexParameter p : program.getParameters()) {
      for (final Pair<String, TType> x : p.getExpanded()) {
        final Element e = new Element("g:parameter", uri);
        e.addAttribute(new Attribute("g:name", uri, x.getLeft()));
        e.addAttribute(new Attribute("g:type", uri, GLSLTypeNames
          .getTypeName(x.getRight())
          .show()));
        evp.appendChild(e);
      }
    }
  }

  /**
   * Construct a file of metadata about the given fragment shader.
   * 
   * @param shader_name
   *          The original fragment shader name
   * @param asts
   *          The generated GLSL for the shader
   * @return Metadata
   */

  public static Document ofFragmentShader(
    final TASTShaderNameFlat shader_name,
    final Map<GVersion, GASTShaderFragment> asts)
  {
    NullCheck.notNull(shader_name, "Shader name");
    NullCheck.notNull(asts, "ASTs");

    final GASTShaderFragment program = asts.values().iterator().next();
    assert program != null;

    final String uri = ShaderMeta.XML_URI_STRING;
    final Element r = new Element("g:meta-fragment", uri);

    {
      final Attribute a =
        new Attribute(
          "g:version",
          uri,
          Integer.toString(ShaderMeta.XML_VERSION));
      r.addAttribute(a);
    }

    final Element ep = new Element("g:program-name", uri);
    ep.appendChild(shader_name.show());
    final Element es = new Element("g:supports", uri);
    for (final GVersion v : asts.keySet()) {
      assert v != null;
      GMeta.supportedVersion(v, uri, es);
    }

    final Element epv = new Element("g:parameters-fragment", uri);
    {
      final Element evi = new Element("g:declared-fragment-inputs", uri);
      GMeta.declaredFragmentInputs(program, uri, evi);
      final Element evp = new Element("g:declared-fragment-parameters", uri);
      GMeta.declaredFragmentParameters(program, uri, evp);
      final Element evo = new Element("g:declared-fragment-outputs", uri);
      GMeta.declaredFragmentOutputs(program, uri, evo);
      epv.appendChild(evi);
      epv.appendChild(evp);
      epv.appendChild(evo);
    }

    r.appendChild(ep);
    r.appendChild(es);
    r.appendChild(epv);
    return new Document(r);
  }

  /**
   * Construct a file of metadata about the given program.
   * 
   * @param program
   *          The compiled program
   * @return Metadata
   */

  public static Document ofProgram(
    final GCompiledProgram program)
  {
    NullCheck.notNull(program, "Program");

    final String uri = ShaderMeta.XML_URI_STRING;
    final Element r = new Element("g:meta-program", uri);

    {
      final Attribute a =
        new Attribute(
          "g:version",
          uri,
          Integer.toString(ShaderMeta.XML_VERSION));
      r.addAttribute(a);
    }

    final Element ep = new Element("g:program-name", uri);
    ep.appendChild(program.getName().show());
    final Element es = new Element("g:supports", uri);
    for (final GVersion v : program.getVersionsES()) {
      assert v != null;
      GMeta.supportedVersion(v, uri, es);
    }
    for (final GVersion v : program.getVersionsFull()) {
      assert v != null;
      GMeta.supportedVersion(v, uri, es);
    }

    final Element ev = new Element("g:shaders-vertex", uri);
    for (final TASTShaderNameFlat name : program.getShadersVertex().keySet()) {
      final Element eve = new Element("g:shader-vertex", uri);
      eve.appendChild(name.show());
      ev.appendChild(eve);
    }

    final Element ef = new Element("g:shader-fragment", uri);
    ef.appendChild(program.getShaderFragmentName().show());

    r.appendChild(ep);
    r.appendChild(es);
    r.appendChild(ev);
    r.appendChild(ef);
    return new Document(r);
  }

  /**
   * Construct a file of metadata about the given vertex shader.
   * 
   * @param shader_name
   *          The original vertex shader name
   * @param asts
   *          The generated GLSL for the shader
   * @return Metadata
   */

  public static Document ofVertexShader(
    final TASTShaderNameFlat shader_name,
    final Map<GVersion, GASTShaderVertex> asts)
  {
    NullCheck.notNull(shader_name, "Shader name");
    NullCheck.notNull(asts, "ASTs");

    final GASTShaderVertex program = asts.values().iterator().next();
    assert program != null;

    final String uri = ShaderMeta.XML_URI_STRING;
    final Element r = new Element("g:meta-vertex", uri);

    {
      final Attribute a =
        new Attribute(
          "g:version",
          uri,
          Integer.toString(ShaderMeta.XML_VERSION));
      r.addAttribute(a);
    }

    final Element ep = new Element("g:program-name", uri);
    ep.appendChild(shader_name.show());
    final Element es = new Element("g:supports", uri);
    for (final GVersion v : asts.keySet()) {
      assert v != null;
      GMeta.supportedVersion(v, uri, es);
    }

    final Element epv = new Element("g:parameters-vertex", uri);
    {
      final Element evi = new Element("g:declared-vertex-inputs", uri);
      GMeta.declaredVertexInputs(program, uri, evi);
      final Element evp = new Element("g:declared-vertex-parameters", uri);
      GMeta.declaredVertexParameters(program, uri, evp);
      final Element evo = new Element("g:declared-vertex-outputs", uri);
      GMeta.declaredVertexOutputs(program, uri, evo);
      epv.appendChild(evi);
      epv.appendChild(evp);
      epv.appendChild(evo);
    }

    r.appendChild(ep);
    r.appendChild(es);
    r.appendChild(epv);
    return new Document(r);
  }

  private static void supportedVersion(
    final GVersion version,
    final String uri,
    final Element es)
  {
    final Element e = new Element("g:version", uri);
    es.appendChild(e);

    final String is = Integer.toString(version.getNumber());
    e.addAttribute(new Attribute("g:number", uri, is));
    version
      .versionAccept(new GVersionVisitorType<Unit, UnreachableCodeException>() {
        @Override public Unit versionVisitES(
          final GVersionES _)
        {
          e.addAttribute(new Attribute("g:api", uri, "glsl-es"));
          return Unit.unit();
        }

        @Override public Unit versionVisitFull(
          final GVersionFull _)
        {
          e.addAttribute(new Attribute("g:api", uri, "glsl"));
          return Unit.unit();
        }
      });
  }

  private GMeta()
  {
    throw new UnreachableCodeException();
  }
}
