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

package com.io7m.jparasol.metaserializer.xml;

import java.util.SortedSet;
import java.util.TreeSet;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.core.GVersion;
import com.io7m.jparasol.core.GVersionES;
import com.io7m.jparasol.core.GVersionFull;
import com.io7m.jparasol.core.GVersionType;
import com.io7m.jparasol.core.JPUncompactedProgramShaderMeta;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * Functions to convert {@link JPUncompactedProgramShaderMeta} to/from XML.
 */

@EqualityReference public final class XMLUncompactedProgramShaderMeta
{
  /**
   * @param root
   *          The root element.
   * @return The fragment shader name.
   */

  static String parseFragmentShaderName(
    final Element root)
  {
    final Elements f =
      root.getChildElements("shader-fragment", XMLMeta.XML_URI_STRING);

    final String r = f.get(0).getValue();
    assert r != null;
    return r;
  }

  /**
   * @return A program shader from the given XML element.
   * @throws JPXMLValidityException
   *           On parse errors.
   */

  public static JPUncompactedProgramShaderMeta parseFromXML(
    final Element e)
    throws JPXMLValidityException
  {
    NullCheck.notNull(e, "Element");

    final String name = XMLMeta.parseName(e);
    final SortedSet<GVersionType> supports = XMLMeta.parseSupports(e);
    final SortedSet<GVersionES> supports_es = new TreeSet<GVersionES>();
    final SortedSet<GVersionFull> supports_full = new TreeSet<GVersionFull>();
    GVersion.filterVersions(supports, supports_es, supports_full);

    final String fragment_name =
      XMLUncompactedProgramShaderMeta.parseFragmentShaderName(e);
    final SortedSet<String> vertex_names =
      XMLUncompactedProgramShaderMeta.parseVertexShaderNames(e);

    return JPUncompactedProgramShaderMeta.newMetadata(
      name,
      supports_es,
      supports_full,
      fragment_name,
      vertex_names);
  }

  /**
   * @param root
   *          The root element.
   * @return The list of vertex shaders.
   */

  static SortedSet<String> parseVertexShaderNames(
    final Element root)
  {
    final SortedSet<String> names = new TreeSet<String>();

    final Elements vs =
      root.getChildElements("shaders-vertex", XMLMeta.XML_URI_STRING);

    final Element shaders = vs.get(0);

    final Elements v =
      shaders.getChildElements("shader-vertex", XMLMeta.XML_URI_STRING);

    for (int index = 0; index < v.size(); ++index) {
      names.add(v.get(index).getValue());
    }

    return names;
  }

  /**
   * @param fragment_name
   *          The fragment shader name.
   * @return The fragment shader name as XML.
   */

  static Element serializeFragmentShaderToXML(
    final String fragment_name)
  {
    final Element e =
      new Element("g:shader-fragment", XMLMeta.XML_URI_STRING);
    e.appendChild(fragment_name);
    return e;
  }

  /**
   * @return The given metadata as XML
   */

  public static Element serializeToXML(
    final JPUncompactedProgramShaderMeta f)
  {
    NullCheck.notNull(f, "Metadata");

    final Element root =
      new Element("g:meta-program", XMLMeta.XML_URI_STRING);
    root.addAttribute(new Attribute(
      "g:version",
      XMLMeta.XML_URI_STRING,
      Integer.toString(XMLMeta.XML_VERSION)));

    root.appendChild(XMLMeta.serializeProgramName(f.getName()));
    root.appendChild(XMLMeta.serializeSupports(
      f.getSupportsES(),
      f.getSupportsFull()));
    root.appendChild(XMLUncompactedProgramShaderMeta
      .serializeVertexShadersToXML(f.getVertexShaders()));
    root.appendChild(XMLUncompactedProgramShaderMeta
      .serializeFragmentShaderToXML(f.getFragmentShader()));

    return root;
  }

  /**
   * @param vertex_names
   *          The vertex shader names.
   * @return The vertex shader names as XML.
   */

  static Element serializeVertexShadersToXML(
    final SortedSet<String> vertex_names)
  {
    final Element e = new Element("g:shaders-vertex", XMLMeta.XML_URI_STRING);

    for (final String v : vertex_names) {
      final Element ee =
        new Element("g:shader-vertex", XMLMeta.XML_URI_STRING);
      ee.appendChild(v);
      e.appendChild(ee);
    }

    return e;
  }

  private XMLUncompactedProgramShaderMeta()
  {
    throw new UnreachableCodeException();
  }
}
