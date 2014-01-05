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

import javax.annotation.Nonnull;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Pair;
import com.io7m.jaux.functional.Unit;
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

public final class GMeta
{
  public static @Nonnull
    Document
    make(
      final @Nonnull TASTShaderNameFlat shader,
      final @Nonnull Map<GVersion, Pair<GASTShaderVertex, GASTShaderFragment>> asts)
      throws ConstraintError
  {
    Constraints.constrainNotNull(shader, "Shader");
    Constraints.constrainNotNull(asts, "ASTs");

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
    for (final GVersion v : asts.keySet()) {
      final Element e = new Element("g:version", uri);
      es.appendChild(e);

      final String is = Integer.toString(v.getNumber());
      e.addAttribute(new Attribute("g:number", uri, is));
      v.versionAccept(new GVersionVisitor<Unit, ConstraintError>() {
        @Override public Unit versionVisitES(
          final @Nonnull GVersionES version)
          throws ConstraintError
        {
          e.addAttribute(new Attribute("g:api", uri, "glsl-es"));
          return Unit.unit();
        }

        @Override public Unit versionVisitFull(
          final @Nonnull GVersionFull version)
          throws ConstraintError
        {
          e.addAttribute(new Attribute("g:api", uri, "glsl"));
          return Unit.unit();
        }
      });
    }

    final Element evp = new Element("g:declared-vertex-parameters", uri);
    for (final GASTShaderVertexParameter p : program.first.getParameters()) {
      for (final Pair<String, TType> x : p.getExpanded()) {
        final Element e = new Element("g:parameter", uri);
        e.addAttribute(new Attribute("g:name", uri, x.first));
        e.addAttribute(new Attribute("g:type", uri, GLSLTypeNames
          .getTypeName(x.second)
          .show()));
        evp.appendChild(e);
      }
    }

    final Element efp = new Element("g:declared-fragment-parameters", uri);
    for (final GASTShaderFragmentParameter p : program.second.getParameters()) {
      for (final Pair<String, TType> x : p.getExpanded()) {
        final Element e = new Element("g:parameter", uri);
        e.addAttribute(new Attribute("g:name", uri, x.first));
        e.addAttribute(new Attribute("g:type", uri, GLSLTypeNames
          .getTypeName(x.second)
          .show()));
        efp.appendChild(e);
      }
    }

    final Element evi = new Element("g:declared-vertex-inputs", uri);
    for (final GASTShaderVertexInput i : program.first.getInputs()) {
      final Element e = new Element("g:input", uri);
      e.addAttribute(new Attribute("g:name", uri, i.getName().show()));
      e.addAttribute(new Attribute("g:type", uri, i.getType().show()));
      evi.appendChild(e);
    }

    final Element efi = new Element("g:declared-fragment-inputs", uri);
    for (final GASTShaderFragmentInput i : program.second.getInputs()) {
      final Element e = new Element("g:input", uri);
      e.addAttribute(new Attribute("g:name", uri, i.getName().show()));
      e.addAttribute(new Attribute("g:type", uri, i.getTypeName().show()));
      efi.appendChild(e);
    }

    final Element evo = new Element("g:declared-vertex-outputs", uri);
    for (final GASTShaderVertexOutput i : program.first.getOutputs()) {
      final Element e = new Element("g:output", uri);
      e.addAttribute(new Attribute("g:name", uri, i.getName().show()));
      e.addAttribute(new Attribute("g:type", uri, i.getTypeName().show()));
      evo.appendChild(e);
    }

    final Element efo = new Element("g:declared-fragment-outputs", uri);
    for (final GASTShaderFragmentOutput i : program.second.getOutputs()) {
      final Element e = new Element("g:fragment-output", uri);
      e.addAttribute(new Attribute("g:name", uri, i.getName().show()));
      e.addAttribute(new Attribute("g:index", uri, Integer.toString(i
        .getIndex())));
      e.addAttribute(new Attribute("g:type", uri, i.getType().show()));
      efo.appendChild(e);
    }

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
}
