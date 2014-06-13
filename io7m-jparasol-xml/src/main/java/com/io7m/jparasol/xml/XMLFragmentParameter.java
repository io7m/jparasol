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
import com.io7m.jparasol.core.FragmentParameter;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * Functions to convert {@link FragmentParameter} to/from XML.
 */

@EqualityReference final class XMLFragmentParameter
{
  /**
   * @param e
   *          The root element.
   * @return A set of declared fragment inputs.
   */

  static SortedSet<FragmentParameter> parseDeclaredParametersFromXML(
    final Element e)
  {
    final SortedSet<FragmentParameter> rinputs =
      new TreeSet<FragmentParameter>();

    final Elements eic =
      e.getChildElements("parameter", XMLMeta.XML_URI_STRING);

    for (int index = 0; index < eic.size(); ++index) {
      final Element ei = eic.get(index);
      assert ei != null;
      final FragmentParameter i = XMLFragmentParameter.parseFromXML(ei);
      rinputs.add(i);
    }

    return rinputs;
  }

  /**
   * @return A fragment parameter from the given XML element.
   */

  static FragmentParameter parseFromXML(
    final Element e)
  {
    final Attribute an = e.getAttribute("name", XMLMeta.XML_URI_STRING);
    final Attribute at = e.getAttribute("type", XMLMeta.XML_URI_STRING);
    final String v_an = an.getValue();
    assert v_an != null;
    final String v_at = at.getValue();
    assert v_at != null;
    return FragmentParameter.newParameter(v_an, v_at);
  }

  /**
   * @param vp
   *          The set of parameters.
   * @return The parameters as XML.
   */

  static Element serializeDeclaredFragmentParametersToXML(
    final SortedSet<FragmentParameter> vp)
  {
    final String uri = XMLMeta.XML_URI_STRING;
    final Element e = new Element("g:declared-fragment-parameters", uri);
    for (final FragmentParameter v : vp) {
      assert v != null;
      e.appendChild(XMLFragmentParameter.serializeToXML(v));
    }
    return e;
  }

  /**
   * @return The current parameter as XML
   */

  static Element serializeToXML(
    final FragmentParameter i)
  {
    final String uri = XMLMeta.XML_URI_STRING;
    final Element e = new Element("g:parameter", uri);
    e.addAttribute(new Attribute("g:name", uri, i.getName()));
    e.addAttribute(new Attribute("g:type", uri, i.getType()));
    return e;
  }

  private XMLFragmentParameter()
  {
    throw new UnreachableCodeException();
  }
}
