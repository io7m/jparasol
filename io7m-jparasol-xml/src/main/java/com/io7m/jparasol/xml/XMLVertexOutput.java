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
import nu.xom.Elements;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jparasol.core.JPVertexOutput;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * Functions to convert {@link VertexOutput} to/from XML.
 */

@EqualityReference final class XMLVertexOutput
{
  /**
   * @param e
   *          The root element.
   * @return A set of declared vertex outputs.
   */

  static SortedSet<JPVertexOutput> parseDeclaredOutputsFromXML(
    final Element e)
  {
    final SortedSet<JPVertexOutput> rinputs = new TreeSet<JPVertexOutput>();

    final Elements eic = e.getChildElements("output", XMLMeta.XML_URI_STRING);

    for (int index = 0; index < eic.size(); ++index) {
      final Element ei = eic.get(index);
      assert ei != null;
      final JPVertexOutput i = XMLVertexOutput.parseFromXML(ei);
      rinputs.add(i);
    }

    return rinputs;
  }

  /**
   * @return A vertex output from the given XML element.
   */

  static JPVertexOutput parseFromXML(
    final Element e)
  {
    final Attribute an = e.getAttribute("name", XMLMeta.XML_URI_STRING);
    final Attribute at = e.getAttribute("type", XMLMeta.XML_URI_STRING);
    final String v_an = an.getValue();
    assert v_an != null;
    final String v_at = at.getValue();
    assert v_at != null;
    return JPVertexOutput.newOutput(v_an, v_at);
  }

  /**
   * @param vo
   *          The set of outputs.
   * @return The outputs as XML.
   */

  static Element serializeDeclaredVertexOutputsToXML(
    final SortedSet<JPVertexOutput> vo)
  {
    final String uri = XMLMeta.XML_URI_STRING;
    final Element e = new Element("g:declared-vertex-outputs", uri);
    for (final JPVertexOutput v : vo) {
      assert v != null;
      e.appendChild(XMLVertexOutput.serializeToXML(v));
    }
    return e;
  }

  /**
   * @return The current output as XML
   */

  static Element serializeToXML(
    final JPVertexOutput o)
  {
    final String uri = XMLMeta.XML_URI_STRING;
    final Element e = new Element("g:output", uri);
    e.addAttribute(new Attribute("g:name", uri, o.getName()));
    e.addAttribute(new Attribute("g:type", uri, o.getType()));
    return e;
  }

  private XMLVertexOutput()
  {
    throw new UnreachableCodeException();
  }
}
