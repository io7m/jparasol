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
import com.io7m.jparasol.typed.TType;
import com.io7m.jparasol.typed.ast.TASTShaderNameFlat;
import com.io7m.jparasol.xml.PGLSLMetaXML;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * Metadata about a generated GLSL program.
 */

@EqualityReference public final class GMeta
{
  private static void declaredFragmentInputs(
    final Pair<GASTShaderVertex, GASTShaderFragment> program,
    final String uri,
    final Element efi)
  {
    for (final GASTShaderFragmentInput i : program.getRight().getInputs()) {
      final Element e = new Element("g:input", uri);
      e.addAttribute(new Attribute("g:name", uri, i.getName().show()));
      e.addAttribute(new Attribute("g:type", uri, i.getTypeName().show()));
      efi.appendChild(e);
    }
  }

  private static void declaredFragmentOutputs(
    final Pair<GASTShaderVertex, GASTShaderFragment> program,
    final String uri,
    final Element efo)
  {
    for (final GASTShaderFragmentOutput i : program.getRight().getOutputs()) {
      final Element e = new Element("g:fragment-output", uri);
      e.addAttribute(new Attribute("g:name", uri, i.getName().show()));
      e.addAttribute(new Attribute("g:index", uri, Integer.toString(i
        .getIndex())));
      e.addAttribute(new Attribute("g:type", uri, i.getType().show()));
      efo.appendChild(e);
    }
  }

  private static void declaredFragmentParameters(
    final Pair<GASTShaderVertex, GASTShaderFragment> program,
    final String uri,
    final Element efp)
  {
    for (final GASTShaderFragmentParameter p : program
      .getRight()
      .getParameters()) {
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
    final Pair<GASTShaderVertex, GASTShaderFragment> program,
    final String uri,
    final Element evi)
  {
    for (final GASTShaderVertexInput i : program.getLeft().getInputs()) {
      final Element e = new Element("g:input", uri);
      e.addAttribute(new Attribute("g:name", uri, i.getName().show()));
      e.addAttribute(new Attribute("g:type", uri, i.getType().show()));
      evi.appendChild(e);
    }
  }

  private static void declaredVertexOutputs(
    final Pair<GASTShaderVertex, GASTShaderFragment> program,
    final String uri,
    final Element evo)
  {
    for (final GASTShaderVertexOutput i : program.getLeft().getOutputs()) {
      final Element e = new Element("g:output", uri);
      e.addAttribute(new Attribute("g:name", uri, i.getName().show()));
      e.addAttribute(new Attribute("g:type", uri, i.getTypeName().show()));
      evo.appendChild(e);
    }
  }

  private static void declaredVertexParameters(
    final Pair<GASTShaderVertex, GASTShaderFragment> program,
    final String uri,
    final Element evp)
  {
    for (final GASTShaderVertexParameter p : program
      .getLeft()
      .getParameters()) {
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
   * Construct a file of metadata about the given program.
   * 
   * @param shader
   *          The original shader name
   * @param asts
   *          The generated GLSL shaders
   * @return Metadata
   */

  public static Document make(
    final TASTShaderNameFlat shader,
    final Map<GVersion, Pair<GASTShaderVertex, GASTShaderFragment>> asts)
  {
    NullCheck.notNull(shader, "Shader");
    NullCheck.notNull(asts, "ASTs");

    final Pair<GASTShaderVertex, GASTShaderFragment> program =
      asts.entrySet().iterator().next().getValue();
    assert program != null;

    final String uri = PGLSLMetaXML.XML_URI_STRING;
    final Element r = new Element("g:meta", uri);

    {
      final Attribute a =
        new Attribute(
          "g:version",
          uri,
          Integer.toString(PGLSLMetaXML.XML_VERSION));
      r.addAttribute(a);
    }

    final Element ep = new Element("g:program-name", uri);
    ep.appendChild(shader.show());

    final Element es = new Element("g:supports", uri);
    GMeta.supportedVersions(asts, uri, es);
    final Element evp = new Element("g:declared-vertex-parameters", uri);
    GMeta.declaredVertexParameters(program, uri, evp);
    final Element efp = new Element("g:declared-fragment-parameters", uri);
    GMeta.declaredFragmentParameters(program, uri, efp);
    final Element evi = new Element("g:declared-vertex-inputs", uri);
    GMeta.declaredVertexInputs(program, uri, evi);
    final Element efi = new Element("g:declared-fragment-inputs", uri);
    GMeta.declaredFragmentInputs(program, uri, efi);
    final Element evo = new Element("g:declared-vertex-outputs", uri);
    GMeta.declaredVertexOutputs(program, uri, evo);
    final Element efo = new Element("g:declared-fragment-outputs", uri);
    GMeta.declaredFragmentOutputs(program, uri, efo);

    r.appendChild(ep);
    r.appendChild(es);
    r.appendChild(evp);
    r.appendChild(efp);
    r.appendChild(evi);
    r.appendChild(efi);
    r.appendChild(evo);
    r.appendChild(efo);
    return new Document(r);
  }

  private static void supportedVersions(
    final Map<GVersion, Pair<GASTShaderVertex, GASTShaderFragment>> asts,
    final String uri,
    final Element es)
  {
    for (final GVersion v : asts.keySet()) {
      final Element e = new Element("g:version", uri);
      es.appendChild(e);

      final String is = Integer.toString(v.getNumber());
      e.addAttribute(new Attribute("g:number", uri, is));
      v
        .versionAccept(new GVersionVisitorType<Unit, UnreachableCodeException>() {
          @Override public Unit versionVisitES(
            final GVersionES version)
          {
            e.addAttribute(new Attribute("g:api", uri, "glsl-es"));
            return Unit.unit();
          }

          @Override public Unit versionVisitFull(
            final GVersionFull version)
          {
            e.addAttribute(new Attribute("g:api", uri, "glsl"));
            return Unit.unit();
          }
        });
    }
  }

  private GMeta()
  {
    throw new UnreachableCodeException();
  }
}
