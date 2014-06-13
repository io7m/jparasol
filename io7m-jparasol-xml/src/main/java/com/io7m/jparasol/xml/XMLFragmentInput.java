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
import com.io7m.jparasol.core.FragmentInput;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * Functions to convert {@link FragmentInput} to/from XML.
 */

@EqualityReference final class XMLFragmentInput
{
  /**
   * @param e
   *          The root element.
   * @return A set of declared fragment inputs.
   */

  static SortedSet<FragmentInput> parseDeclaredInputsFromXML(
    final Element e)
  {
    final SortedSet<FragmentInput> rinputs = new TreeSet<FragmentInput>();

    final Elements eic = e.getChildElements("input", XMLMeta.XML_URI_STRING);

    for (int index = 0; index < eic.size(); ++index) {
      final Element ei = eic.get(index);
      assert ei != null;
      final FragmentInput i = XMLFragmentInput.parseFromXML(ei);
      rinputs.add(i);
    }

    return rinputs;
  }

  /**
   * @return A fragment input from the given XML element.
   */

  static FragmentInput parseFromXML(
    final Element e)
  {
    final Attribute an = e.getAttribute("name", XMLMeta.XML_URI_STRING);
    final Attribute at = e.getAttribute("type", XMLMeta.XML_URI_STRING);
    final String v_an = an.getValue();
    assert v_an != null;
    final String v_at = at.getValue();
    assert v_at != null;
    return FragmentInput.newInput(v_an, v_at);
  }

  /**
   * @param vi
   *          The set of inputs.
   * @return The inputs as XML.
   */

  static Element serializeDeclaredFragmentInputsToXML(
    final SortedSet<FragmentInput> vi)
  {
    final String uri = XMLMeta.XML_URI_STRING;
    final Element e = new Element("g:declared-fragment-inputs", uri);
    for (final FragmentInput v : vi) {
      assert v != null;
      e.appendChild(XMLFragmentInput.serializeToXML(v));
    }
    return e;
  }

  /**
   * @return The current input as XML
   */

  static Element serializeToXML(
    final FragmentInput i)
  {
    final String uri = XMLMeta.XML_URI_STRING;
    final Element e = new Element("g:input", uri);
    e.addAttribute(new Attribute("g:name", uri, i.getName()));
    e.addAttribute(new Attribute("g:type", uri, i.getType()));
    return e;
  }

  private XMLFragmentInput()
  {
    throw new UnreachableCodeException();
  }
}
