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

import nu.xom.Attribute;
import nu.xom.Element;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jparasol.core.GVersionFull;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * Serialization for {@link GVersionFull}.
 */

@EqualityReference final class XMLGVersionFull
{
  /**
   * Serialize the given version to XML.
   * 
   * @param v
   *          The version.
   * @return An XML element.
   */

  static Element serializeToXML(
    final GVersionFull v)
  {
    final String uri = XMLMeta.XML_URI_STRING;
    final Element e = new Element("g:version", uri);
    final String vn = Integer.toString(v.versionGetNumber());
    e.addAttribute(new Attribute("g:number", uri, vn));
    e.addAttribute(new Attribute("g:api", uri, v.versionGetAPIName()));
    return e;
  }

  private XMLGVersionFull()
  {
    throw new UnreachableCodeException();
  }
}
