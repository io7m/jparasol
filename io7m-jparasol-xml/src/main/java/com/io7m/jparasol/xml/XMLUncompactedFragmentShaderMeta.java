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
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.core.GVersion;
import com.io7m.jparasol.core.GVersionES;
import com.io7m.jparasol.core.GVersionFull;
import com.io7m.jparasol.core.GVersionType;
import com.io7m.jparasol.core.JPFragmentInput;
import com.io7m.jparasol.core.JPFragmentOutput;
import com.io7m.jparasol.core.JPFragmentParameter;
import com.io7m.jparasol.core.JPUncompactedFragmentShaderMeta;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * Functions to convert {@link JPUncompactedFragmentShaderMeta} to/from XML.
 */

@EqualityReference public final class XMLUncompactedFragmentShaderMeta
{
  /**
   * @param e
   *          An XML element.
   * @return A fragment shader from the given XML element.
   * @throws JPXMLValidityException
   *           On parse errors.
   */

  public static JPUncompactedFragmentShaderMeta parseFromXML(
    final Element e)
    throws JPXMLValidityException
  {
    NullCheck.notNull(e, "Element");

    final String name = XMLMeta.parseName(e);
    final SortedSet<GVersionType> supports = XMLMeta.parseSupports(e);
    final SortedSet<GVersionES> supports_es = new TreeSet<GVersionES>();
    final SortedSet<GVersionFull> supports_full = new TreeSet<GVersionFull>();
    GVersion.filterVersions(supports, supports_es, supports_full);

    final SortedSet<JPFragmentInput> fragment_inputs;
    final SortedMap<Integer, JPFragmentOutput> fragment_outputs;
    final SortedSet<JPFragmentParameter> fragment_parameters;

    final Element params =
      e.getFirstChildElement("parameters-fragment", XMLMeta.XML_URI_STRING);
    assert params != null;

    {
      final Element ei =
        params.getFirstChildElement(
          "declared-fragment-inputs",
          XMLMeta.XML_URI_STRING);
      assert ei != null;
      fragment_inputs = XMLFragmentInput.parseDeclaredInputsFromXML(ei);
    }

    {
      final Element eo =
        params.getFirstChildElement(
          "declared-fragment-outputs",
          XMLMeta.XML_URI_STRING);
      assert eo != null;
      fragment_outputs = XMLFragmentOutput.parseDeclaredOutputsFromXML(eo);
    }

    {
      final Element ep =
        params.getFirstChildElement(
          "declared-fragment-parameters",
          XMLMeta.XML_URI_STRING);
      assert ep != null;
      fragment_parameters =
        XMLFragmentParameter.parseDeclaredParametersFromXML(ep);
    }

    return JPUncompactedFragmentShaderMeta.newMetadata(
      name,
      supports_es,
      supports_full,
      fragment_inputs,
      fragment_outputs,
      fragment_parameters);
  }

  /**
   * @param f
   *          Metadata.
   * @return The given metadata as XML
   */

  public static Element serializeToXML(
    final JPUncompactedFragmentShaderMeta f)
  {
    NullCheck.notNull(f, "Metadata");

    final Element root =
      new Element("g:meta-fragment", XMLMeta.XML_URI_STRING);
    root.addAttribute(new Attribute(
      "g:version",
      XMLMeta.XML_URI_STRING,
      Integer.toString(XMLMeta.XML_VERSION)));

    root.appendChild(XMLMeta.serializeProgramName(f.getName()));
    root.appendChild(XMLMeta.serializeSupports(
      f.getSupportsES(),
      f.getSupportsFull()));
    root.appendChild(XMLMeta.serializeParametersFragmentToXML(
      f.getDeclaredFragmentParameters(),
      f.getDeclaredFragmentInputs(),
      f.getDeclaredFragmentOutputs()));

    return root;
  }

  private XMLUncompactedFragmentShaderMeta()
  {
    throw new UnreachableCodeException();
  }
}
