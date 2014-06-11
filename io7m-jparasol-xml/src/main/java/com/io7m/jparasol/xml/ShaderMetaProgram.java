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

import java.util.Collections;
import java.util.SortedMap;
import java.util.SortedSet;

import nu.xom.Attribute;
import nu.xom.Element;

import com.io7m.jequality.annotations.EqualityStructural;
import com.io7m.jfunctional.Option;
import com.io7m.jfunctional.OptionType;
import com.io7m.jnull.Nullable;

/**
 * Metadata about a generated program.
 */

@EqualityStructural public final class ShaderMetaProgram implements
  ShaderMetaType
{
  private final String             fragment_shader;
  private final String             name;
  private final SortedSet<Integer> supports_es;
  private final SortedSet<Integer> supports_full;
  private final SortedSet<String>  vertex_shaders;

  ShaderMetaProgram(
    final String in_name,
    final SortedSet<Integer> in_supports_es,
    final SortedSet<Integer> in_supports_full,
    final SortedSet<String> in_vertex_shaders,
    final String in_fragment_shader)
  {
    this.name = in_name;
    this.supports_es = in_supports_es;
    this.supports_full = in_supports_full;
    this.vertex_shaders = in_vertex_shaders;
    this.fragment_shader = in_fragment_shader;
  }

  @Override public boolean equals(
    final @Nullable Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    final ShaderMetaProgram other = (ShaderMetaProgram) obj;
    return this.fragment_shader.equals(other.fragment_shader)
      && this.name.equals(other.name)
      && this.supports_es.equals(other.supports_es)
      && this.supports_full.equals(other.supports_full)
      && this.vertex_shaders.equals(other.vertex_shaders);
  }

  @Override public
    OptionType<SortedMap<Version, String>>
    getCompactMappings()
  {
    return Option.none();
  }

  /**
   * @return The fragment shader.
   */

  public String getFragmentShader()
  {
    return this.fragment_shader;
  }

  @Override public String getName()
  {
    return this.name;
  }

  @Override public SortedSet<Integer> getSupportsES()
  {
    return this.supports_es;
  }

  @Override public SortedSet<Integer> getSupportsFull()
  {
    return this.supports_full;
  }

  /**
   * @return The set of vertex shaders against which the fragment shader has
   *         been type-checked.
   */

  public SortedSet<String> getVertexShaders()
  {
    final SortedSet<String> r =
      Collections.unmodifiableSortedSet(this.vertex_shaders);
    assert r != null;
    return r;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.fragment_shader.hashCode();
    result = (prime * result) + this.name.hashCode();
    result = (prime * result) + this.supports_es.hashCode();
    result = (prime * result) + this.supports_full.hashCode();
    result = (prime * result) + this.vertex_shaders.hashCode();
    return result;
  }

  @Override public <A, E extends Exception> A matchType(
    final ShaderMetaVisitorType<A, E> v)
    throws E
  {
    return v.program(this);
  }

  @Override public Element toXML()
  {
    final Element root =
      new Element("g:meta-program", ShaderMeta.XML_URI_STRING);
    root.addAttribute(new Attribute(
      "g:version",
      ShaderMeta.XML_URI_STRING,
      Integer.toString(ShaderMeta.XML_VERSION)));
    root.appendChild(ShaderMeta.toXMLProgramName(this.name));
    root.appendChild(ShaderMeta.toXMLSupports(
      this.supports_es,
      this.supports_full));
    root.appendChild(this.toXMLVertexShaders());
    root.appendChild(this.toXMLFragmentShaders());
    return root;
  }

  private Element toXMLFragmentShaders()
  {
    final Element e =
      new Element("g:shader-fragment", ShaderMeta.XML_URI_STRING);
    e.appendChild(this.fragment_shader);
    return e;
  }

  private Element toXMLVertexShaders()
  {
    final Element e =
      new Element("g:shaders-vertex", ShaderMeta.XML_URI_STRING);

    for (final String v : this.vertex_shaders) {
      final Element ee =
        new Element("g:shader-vertex", ShaderMeta.XML_URI_STRING);
      ee.appendChild(v);
      e.appendChild(ee);
    }

    return e;
  }

  @Override public ShaderMetaType withCompactMappings(
    final SortedMap<Version, String> mappings)
  {
    return this;
  }
}
