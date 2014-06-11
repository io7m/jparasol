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

import nu.xom.Attribute;
import nu.xom.Element;

import com.io7m.jequality.annotations.EqualityStructural;
import com.io7m.jfunctional.FunctionType;
import com.io7m.jfunctional.Option;
import com.io7m.jfunctional.OptionType;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;

/**
 * Metadata about a generated vertex shader.
 */

@EqualityStructural public final class ShaderMetaVertex implements
  ShaderMetaType
{
  private final OptionType<SortedMap<Version, String>> compact_mappings;
  private final String                                 name;
  private final SortedSet<Integer>                     supports_es;
  private final SortedSet<Integer>                     supports_full;
  private final SortedSet<VertexInput>                 vertex_inputs;
  private final SortedSet<VertexOutput>                vertex_outputs;
  private final SortedSet<VertexParameter>             vertex_parameters;

  ShaderMetaVertex(
    final OptionType<SortedMap<Version, String>> in_compact_mappings,
    final String in_name,
    final SortedSet<Integer> in_supports_es,
    final SortedSet<Integer> in_supports_full,
    final SortedSet<VertexInput> in_vertex_inputs,
    final SortedSet<VertexOutput> in_vertex_outputs,
    final SortedSet<VertexParameter> in_vertex_parameters)
  {
    this.compact_mappings = in_compact_mappings;
    this.name = in_name;
    this.supports_es = in_supports_es;
    this.supports_full = in_supports_full;
    this.vertex_inputs = in_vertex_inputs;
    this.vertex_outputs = in_vertex_outputs;
    this.vertex_parameters = in_vertex_parameters;
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
    final ShaderMetaVertex other = (ShaderMetaVertex) obj;
    return this.compact_mappings.equals(other.compact_mappings)
      && this.name.equals(other.name)
      && this.supports_es.equals(other.supports_es)
      && this.supports_full.equals(other.supports_full)
      && this.vertex_inputs.equals(other.vertex_inputs)
      && this.vertex_outputs.equals(other.vertex_outputs)
      && this.vertex_parameters.equals(other.vertex_parameters);
  }

  @Override public
    OptionType<SortedMap<Version, String>>
    getCompactMappings()
  {
    return this.compact_mappings;
  }

  /**
   * @return The set of vertex shader inputs that were declared in the given
   *         program.
   */

  public SortedSet<VertexInput> getDeclaredVertexInputs()
  {
    return this.vertex_inputs;
  }

  /**
   * @return The set of vertex shader outputs that were declared in the given
   *         program.
   */

  public SortedSet<VertexOutput> getDeclaredVertexOutputs()
  {
    return this.vertex_outputs;
  }

  /**
   * @return The set of vertex shader parameters that were declared in the
   *         given program.
   */

  public SortedSet<VertexParameter> getDeclaredVertexParameters()
  {
    return this.vertex_parameters;
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

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.compact_mappings.hashCode();
    result = (prime * result) + this.name.hashCode();
    result = (prime * result) + this.supports_es.hashCode();
    result = (prime * result) + this.supports_full.hashCode();
    result = (prime * result) + this.vertex_inputs.hashCode();
    result = (prime * result) + this.vertex_outputs.hashCode();
    result = (prime * result) + this.vertex_parameters.hashCode();
    return result;
  }

  @Override public <A, E extends Exception> A matchType(
    final ShaderMetaVisitorType<A, E> v)
    throws E
  {
    return v.vertex(this);
  }

  @Override public Element toXML()
  {
    final Element root =
      new Element("g:meta-vertex", ShaderMeta.XML_URI_STRING);
    root.addAttribute(new Attribute(
      "g:version",
      ShaderMeta.XML_URI_STRING,
      Integer.toString(ShaderMeta.XML_VERSION)));
    root.appendChild(ShaderMeta.toXMLProgramName(this.name));
    root.appendChild(ShaderMeta.toXMLSupports(
      this.supports_es,
      this.supports_full));
    root.appendChild(this.toXMLParametersVertex());

    this.compact_mappings
      .map(new FunctionType<SortedMap<Version, String>, Unit>() {
        @Override public Unit call(
          final SortedMap<Version, String> map)
        {
          root.appendChild(ShaderMeta.toXMLCompactMappings(map));
          return Unit.unit();
        }
      });

    return root;
  }

  private Element toXMLDeclaredVertexInputs()
  {
    final String uri = ShaderMeta.XML_URI_STRING;
    final Element e = new Element("g:declared-vertex-inputs", uri);
    for (final VertexInput v : this.getDeclaredVertexInputs()) {
      e.appendChild(v.toXML());
    }
    return e;
  }

  private Element toXMLDeclaredVertexOutputs()
  {
    final String uri = ShaderMeta.XML_URI_STRING;
    final Element e = new Element("g:declared-vertex-outputs", uri);
    for (final VertexOutput v : this.getDeclaredVertexOutputs()) {
      e.appendChild(v.toXML());
    }
    return e;
  }

  private Element toXMLDeclaredVertexParameters()
  {
    final String uri = ShaderMeta.XML_URI_STRING;
    final Element e = new Element("g:declared-vertex-parameters", uri);
    for (final VertexParameter v : this.getDeclaredVertexParameters()) {
      e.appendChild(v.toXML());
    }
    return e;
  }

  private Element toXMLParametersVertex()
  {
    final String uri = ShaderMeta.XML_URI_STRING;
    final Element e = new Element("g:parameters-vertex", uri);
    e.appendChild(this.toXMLDeclaredVertexParameters());
    e.appendChild(this.toXMLDeclaredVertexInputs());
    e.appendChild(this.toXMLDeclaredVertexOutputs());
    return e;
  }

  @Override public ShaderMetaType withCompactMappings(
    final SortedMap<Version, String> mappings)
  {
    NullCheck.notNull(mappings, "Mappings");

    return new ShaderMetaVertex(
      Option.some(mappings),
      this.name,
      this.supports_es,
      this.supports_full,
      this.vertex_inputs,
      this.vertex_outputs,
      this.vertex_parameters);
  }
}
