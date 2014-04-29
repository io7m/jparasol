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

import com.io7m.jequality.annotations.EqualityStructural;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;

/**
 * A fragment shader input.
 */

@EqualityStructural public final class FragmentInput implements
  Comparable<FragmentInput>
{
  private final String name;
  private final String type;

  FragmentInput(
    final String in_name,
    final String in_type)
  {
    this.name = NullCheck.notNull(in_name, "Input name");
    this.type = NullCheck.notNull(in_type, "Input type");
  }

  @Override public int compareTo(
    final FragmentInput o)
  {
    return this.name.compareTo(o.name);
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
    final FragmentInput other = (FragmentInput) obj;
    if (!this.name.equals(other.name)) {
      return false;
    }
    if (!this.type.equals(other.type)) {
      return false;
    }
    return true;
  }

  /**
   * @return The name of the input.
   */

  public String getName()
  {
    return this.name;
  }

  /**
   * @return The name of the type of the input. This is the type as it appears
   *         in GLSL.
   */

  public String getType()
  {
    return this.type;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.name.hashCode();
    result = (prime * result) + this.type.hashCode();
    return result;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[FragmentInput ");
    builder.append(this.name);
    builder.append(" ");
    builder.append(this.type);
    builder.append("]");
    final String r = builder.toString();
    assert r != null;
    return r;
  }

  /**
   * @return The current input as XML
   */

  public Element toXML()
  {
    final String uri = PGLSLMetaXML.XML_URI_STRING;
    final Element e = new Element("g:input", uri);
    e.addAttribute(new Attribute("g:name", uri, this.name));
    e.addAttribute(new Attribute("g:type", uri, this.type));
    return e;
  }
}
