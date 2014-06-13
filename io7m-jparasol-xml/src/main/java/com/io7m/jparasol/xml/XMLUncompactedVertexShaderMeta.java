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

package com.io7m.jparasol.xml;

import java.util.SortedSet;
import java.util.TreeSet;

import nu.xom.Attribute;
import nu.xom.Element;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jparasol.core.GVersion;
import com.io7m.jparasol.core.GVersionES;
import com.io7m.jparasol.core.GVersionFull;
import com.io7m.jparasol.core.GVersionType;
import com.io7m.jparasol.core.UncompactedVertexShaderMeta;
import com.io7m.jparasol.core.VertexInput;
import com.io7m.jparasol.core.VertexOutput;
import com.io7m.jparasol.core.VertexParameter;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * Functions to convert {@link UncompactedVertexShaderMeta} to/from XML.
 */

@EqualityReference public final class XMLUncompactedVertexShaderMeta
{
  /**
   * @return A vertex shader from the given XML element.
   * @throws JPXMLValidityException
   *           On parse errors.
   */

  public static UncompactedVertexShaderMeta parseFromXML(
    final Element e)
    throws JPXMLValidityException
  {
    final String name = XMLMeta.parseName(e);
    final SortedSet<GVersionType> supports = XMLMeta.parseSupports(e);
    final SortedSet<GVersionES> supports_es = new TreeSet<GVersionES>();
    final SortedSet<GVersionFull> supports_full = new TreeSet<GVersionFull>();
    GVersion.filterVersions(supports, supports_es, supports_full);

    final SortedSet<VertexInput> vertex_inputs;
    final SortedSet<VertexOutput> vertex_outputs;
    final SortedSet<VertexParameter> vertex_parameters;

    final Element params =
      e.getFirstChildElement("parameters-vertex", XMLMeta.XML_URI_STRING);
    assert params != null;

    {
      final Element ei =
        params.getFirstChildElement(
          "declared-vertex-inputs",
          XMLMeta.XML_URI_STRING);
      assert ei != null;
      vertex_inputs = XMLVertexInput.parseDeclaredInputsFromXML(ei);
    }

    {
      final Element eo =
        params.getFirstChildElement(
          "declared-vertex-outputs",
          XMLMeta.XML_URI_STRING);
      assert eo != null;
      vertex_outputs = XMLVertexOutput.parseDeclaredOutputsFromXML(eo);
    }

    {
      final Element ep =
        params.getFirstChildElement(
          "declared-vertex-parameters",
          XMLMeta.XML_URI_STRING);
      assert ep != null;
      vertex_parameters =
        XMLVertexParameter.parseDeclaredParametersFromXML(ep);
    }

    return UncompactedVertexShaderMeta.newMetadata(
      name,
      supports_es,
      supports_full,
      vertex_inputs,
      vertex_outputs,
      vertex_parameters);
  }

  /**
   * @return The given metadata as XML
   */

  public static Element serializeToXML(
    final UncompactedVertexShaderMeta f)
  {
    final Element root = new Element("g:meta-vertex", XMLMeta.XML_URI_STRING);
    root.addAttribute(new Attribute(
      "g:version",
      XMLMeta.XML_URI_STRING,
      Integer.toString(XMLMeta.XML_VERSION)));

    root.appendChild(XMLMeta.serializeProgramName(f.getName()));
    root.appendChild(XMLMeta.serializeSupports(
      f.getSupportsES(),
      f.getSupportsFull()));
    root.appendChild(XMLMeta.serializeParametersVertexToXML(
      f.getDeclaredVertexParameters(),
      f.getDeclaredVertexInputs(),
      f.getDeclaredVertexOutputs()));

    return root;
  }

  private XMLUncompactedVertexShaderMeta()
  {
    throw new UnreachableCodeException();
  }
}
