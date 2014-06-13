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
import java.util.TreeMap;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jparasol.core.FragmentOutput;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * Functions to convert {@link FragmentOutput} to/from XML.
 */

@EqualityReference final class XMLFragmentOutput
{
  /**
   * @param e
   *          The root element.
   * @return A set of declared fragment outputs.
   * @throws JPXMLValidityException
   *           If a problem occurs during parsing.
   */

  static SortedMap<Integer, FragmentOutput> parseDeclaredOutputsFromXML(
    final Element e)
    throws JPXMLValidityException
  {
    final SortedMap<Integer, FragmentOutput> routputs =
      new TreeMap<Integer, FragmentOutput>();

    final Elements eoc =
      e.getChildElements("fragment-output", XMLMeta.XML_URI_STRING);

    for (int index = 0; index < eoc.size(); ++index) {
      final Element eo = eoc.get(index);
      assert eo != null;
      final FragmentOutput o = XMLFragmentOutput.parseFromXML(eo);
      routputs.put(o.getIndex(), o);
    }

    return routputs;
  }

  /**
   * @return A fragment output from the given XML element.
   * @throws JPXMLValidityException
   *           If a problem occurs whilst parsing.
   */

  static FragmentOutput parseFromXML(
    final Element e)
    throws JPXMLValidityException
  {
    final Attribute an = e.getAttribute("name", XMLMeta.XML_URI_STRING);
    final Attribute at = e.getAttribute("type", XMLMeta.XML_URI_STRING);
    final Attribute ai = e.getAttribute("index", XMLMeta.XML_URI_STRING);

    try {
      final String an_v = an.getValue();
      assert an_v != null;
      final String at_v = at.getValue();
      assert at_v != null;
      final String ai_v = ai.getValue();
      assert ai_v != null;
      final Integer pi = Integer.decode(ai_v);
      assert pi != null;

      return FragmentOutput.newOutput(an_v, pi, at_v);
    } catch (final NumberFormatException x) {
      throw new JPXMLValidityException(
        "Could not parse 'index' number on 'fragment-output' element: "
          + x.getMessage());
    }
  }

  /**
   * @param fo
   *          The set of outputs.
   * @return The outputs as XML.
   */

  static Element serializeDeclaredFragmentOutputsToXML(
    final SortedMap<Integer, FragmentOutput> fo)
  {
    final String uri = XMLMeta.XML_URI_STRING;
    final Element e = new Element("g:declared-fragment-outputs", uri);
    for (final Integer i : fo.keySet()) {
      assert i != null;
      final FragmentOutput o = fo.get(i);
      assert o != null;
      e.appendChild(XMLFragmentOutput.serializeToXML(o));
    }
    return e;
  }

  /**
   * @return The current output as XML
   */

  static Element serializeToXML(
    final FragmentOutput i)
  {
    final String uri = XMLMeta.XML_URI_STRING;
    final Element e = new Element("g:fragment-output", uri);
    e.addAttribute(new Attribute("g:name", uri, i.getName()));
    e.addAttribute(new Attribute("g:type", uri, i.getType()));
    e.addAttribute(new Attribute("g:index", uri, i.getIndex().toString()));
    return e;
  }

  private XMLFragmentOutput()
  {
    throw new UnreachableCodeException();
  }
}
