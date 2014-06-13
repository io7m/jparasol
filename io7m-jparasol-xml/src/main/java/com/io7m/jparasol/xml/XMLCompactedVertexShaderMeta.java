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

import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

import nu.xom.Attribute;
import nu.xom.Element;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jparasol.core.CompactedVertexShaderMeta;
import com.io7m.jparasol.core.GVersion;
import com.io7m.jparasol.core.GVersionES;
import com.io7m.jparasol.core.GVersionFull;
import com.io7m.jparasol.core.GVersionType;
import com.io7m.jparasol.core.JPMissingHash;
import com.io7m.jparasol.core.VertexInput;
import com.io7m.jparasol.core.VertexOutput;
import com.io7m.jparasol.core.VertexParameter;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * Functions to convert {@link CompactedVertexShaderMeta} to/from XML.
 */

@EqualityReference public final class XMLCompactedVertexShaderMeta
{
  /**
   * @return A vertex shader from the given XML element.
   * @throws JPXMLValidityException
   *           On parse errors.
   * @throws JPMissingHash
   *           On missing hashes for supported versions.
   */

  public static CompactedVertexShaderMeta parseFromXML(
    final Element e)
    throws JPXMLValidityException,
      JPMissingHash
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

    final SortedMap<GVersionType, String> version_hash =
      XMLMeta.parseVersionHashes(e);

    return CompactedVertexShaderMeta.newMetadata(
      name,
      supports_es,
      supports_full,
      vertex_inputs,
      vertex_outputs,
      vertex_parameters,
      version_hash);
  }

  /**
   * @return The given metadata as XML
   */

  public static Element serializeToXML(
    final CompactedVertexShaderMeta v)
  {
    final Element root =
      new Element("g:meta-vertex-compacted", XMLMeta.XML_URI_STRING);
    root.addAttribute(new Attribute(
      "g:version",
      XMLMeta.XML_URI_STRING,
      Integer.toString(XMLMeta.XML_VERSION)));

    root.appendChild(XMLMeta.serializeProgramName(v.getName()));
    root.appendChild(XMLMeta.serializeSupports(
      v.getSupportsES(),
      v.getSupportsFull()));
    root.appendChild(XMLMeta.serializeParametersVertexToXML(
      v.getDeclaredVertexParameters(),
      v.getDeclaredVertexInputs(),
      v.getDeclaredVertexOutputs()));
    root.appendChild(XMLMeta.serializeVersionHashes(v.getVersionToHash()));

    return root;
  }

  private XMLCompactedVertexShaderMeta()
  {
    throw new UnreachableCodeException();
  }
}
