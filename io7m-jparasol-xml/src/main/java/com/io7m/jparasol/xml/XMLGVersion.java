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
import nu.xom.ValidityException;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jparasol.core.GVersionES;
import com.io7m.jparasol.core.GVersionFull;
import com.io7m.jparasol.core.GVersionType;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * Functions to convert {@link GVersionType} to/from XML.
 */

@EqualityReference public final class XMLGVersion
{
  private XMLGVersion()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Parse a version number from the given element.
   * 
   * @param e
   *          The element.
   * @return A version number.
   * @throws ValidityException
   *           If an error occurs during parsing.
   */

  public static GVersionType parseFromXML(
    final Element e)
    throws ValidityException
  {
    final Attribute av = e.getAttribute("number", XMLMeta.XML_URI_STRING);
    final Attribute aa = e.getAttribute("api", XMLMeta.XML_URI_STRING);

    try {
      final int avi = Integer.parseInt(av.getValue());

      final String aas = aa.getValue();
      if ("glsl-es".equals(aas)) {
        return new GVersionES(avi);
      }
      if ("glsl".equals(aas)) {
        return new GVersionFull(avi);
      }

      throw new ValidityException(String.format(
        "API must be 'glsl-es' or 'glsl' (got '%s')",
        aas));

    } catch (final NumberFormatException x) {
      throw new ValidityException(
        "Could not parse number attribute on version element");
    }
  }
}
