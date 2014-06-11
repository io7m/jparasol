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
import com.io7m.jfunctional.FunctionType;
import com.io7m.jfunctional.Option;
import com.io7m.jfunctional.OptionType;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;

/**
 * Metadata about a generated fragment shader.
 */

@EqualityStructural public final class ShaderMetaFragment implements
  ShaderMetaType
{
  private final OptionType<SortedMap<Version, String>> compact_mappings;
  private final SortedSet<FragmentInput>               fragment_inputs;
  private final SortedMap<Integer, FragmentOutput>     fragment_outputs;
  private final SortedSet<FragmentParameter>           fragment_parameters;
  private final String                                 name;
  private final SortedSet<Integer>                     supports_es;
  private final SortedSet<Integer>                     supports_full;

  ShaderMetaFragment(
    final OptionType<SortedMap<Version, String>> in_compact_mappings,
    final String in_name,
    final SortedSet<Integer> in_supports_es,
    final SortedSet<Integer> in_supports_full,
    final SortedSet<FragmentInput> in_fragment_inputs,
    final SortedMap<Integer, FragmentOutput> in_fragment_outputs,
    final SortedSet<FragmentParameter> in_fragment_parameters)
  {
    this.compact_mappings = in_compact_mappings;
    this.fragment_inputs = in_fragment_inputs;
    this.fragment_outputs = in_fragment_outputs;
    this.fragment_parameters = in_fragment_parameters;
    this.name = in_name;
    this.supports_es = in_supports_es;
    this.supports_full = in_supports_full;
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
    final ShaderMetaFragment other = (ShaderMetaFragment) obj;
    return this.compact_mappings.equals(other.compact_mappings)
      && this.fragment_inputs.equals(other.fragment_inputs)
      && this.fragment_outputs.equals(other.fragment_outputs)
      && this.fragment_parameters.equals(other.fragment_parameters)
      && this.name.equals(other.name)
      && this.supports_es.equals(other.supports_es)
      && this.supports_full.equals(other.supports_full);
  }

  @Override public
    OptionType<SortedMap<Version, String>>
    getCompactMappings()
  {
    return this.compact_mappings;
  }

  /**
   * @return The set of fragment shader inputs that were declared in the given
   *         program.
   */

  public SortedSet<FragmentInput> getDeclaredFragmentInputs()
  {
    return this.fragment_inputs;
  }

  /**
   * @return All of the declared fragment shader outputs.
   */

  public SortedMap<Integer, FragmentOutput> getDeclaredFragmentOutputs()
  {
    final SortedMap<Integer, FragmentOutput> r =
      Collections.unmodifiableSortedMap(this.fragment_outputs);
    assert r != null;
    return r;
  }

  /**
   * @return The set of fragment shader parameters that were declared in the
   *         given program.
   */

  public SortedSet<FragmentParameter> getDeclaredFragmentParameters()
  {
    return this.fragment_parameters;
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
    result = (prime * result) + this.fragment_inputs.hashCode();
    result = (prime * result) + this.fragment_outputs.hashCode();
    result = (prime * result) + this.fragment_parameters.hashCode();
    result = (prime * result) + this.name.hashCode();
    result = (prime * result) + this.supports_es.hashCode();
    result = (prime * result) + this.supports_full.hashCode();
    return result;
  }

  @Override public <A, E extends Exception> A matchType(
    final ShaderMetaVisitorType<A, E> v)
    throws E
  {
    return v.fragment(this);
  }

  @Override public Element toXML()
  {
    final Element root =
      new Element("g:meta-fragment", ShaderMeta.XML_URI_STRING);
    root.addAttribute(new Attribute(
      "g:version",
      ShaderMeta.XML_URI_STRING,
      Integer.toString(ShaderMeta.XML_VERSION)));
    root.appendChild(ShaderMeta.toXMLProgramName(this.name));
    root.appendChild(ShaderMeta.toXMLSupports(
      this.supports_es,
      this.supports_full));
    root.appendChild(this.toXMLParametersFragment());

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

  private Element toXMLDeclaredFragmentInputs()
  {
    final String uri = ShaderMeta.XML_URI_STRING;
    final Element e = new Element("g:declared-fragment-inputs", uri);
    for (final FragmentInput f : this.getDeclaredFragmentInputs()) {
      e.appendChild(f.toXML());
    }
    return e;
  }

  private Element toXMLDeclaredFragmentOutputs()
  {
    final String uri = ShaderMeta.XML_URI_STRING;
    final Element e = new Element("g:declared-fragment-outputs", uri);
    for (final FragmentOutput f : this.getDeclaredFragmentOutputs().values()) {
      e.appendChild(f.toXML());
    }
    return e;
  }

  private Element toXMLDeclaredFragmentParameters()
  {
    final String uri = ShaderMeta.XML_URI_STRING;
    final Element e = new Element("g:declared-fragment-parameters", uri);
    for (final FragmentParameter f : this.getDeclaredFragmentParameters()) {
      e.appendChild(f.toXML());
    }
    return e;
  }

  private Element toXMLParametersFragment()
  {
    final String uri = ShaderMeta.XML_URI_STRING;
    final Element e = new Element("g:parameters-fragment", uri);
    e.appendChild(this.toXMLDeclaredFragmentParameters());
    e.appendChild(this.toXMLDeclaredFragmentInputs());
    e.appendChild(this.toXMLDeclaredFragmentOutputs());
    return e;
  }

  @Override public ShaderMetaType withCompactMappings(
    final SortedMap<Version, String> mappings)
  {
    NullCheck.notNull(mappings, "Mappings");

    return new ShaderMetaFragment(
      Option.some(mappings),
      this.name,
      this.supports_es,
      this.supports_full,
      this.fragment_inputs,
      this.fragment_outputs,
      this.fragment_parameters);
  }
}
