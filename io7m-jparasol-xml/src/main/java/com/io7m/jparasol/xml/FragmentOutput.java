/*
 * Copyright © 2013 <code@io7m.com> http://io7m.com
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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import nu.xom.Attribute;
import nu.xom.Element;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;

/**
 * A fragment shader output.
 */

@Immutable public final class FragmentOutput implements
  Comparable<FragmentOutput>
{
  private final @Nonnull Integer index;
  private final @Nonnull String  name;
  private final @Nonnull String  type;

  FragmentOutput(
    final @Nonnull Integer in_index,
    final @Nonnull String in_name,
    final @Nonnull String in_type)
    throws ConstraintError
  {
    this.index = Constraints.constrainNotNull(in_index, "Index");
    this.name = Constraints.constrainNotNull(in_name, "Output name");
    this.type = Constraints.constrainNotNull(in_type, "Output type");
  }

  @Override public int compareTo(
    final FragmentOutput o)
  {
    return this.name.compareTo(o.name);
  }

  @Override public boolean equals(
    final Object obj)
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
    final FragmentOutput other = (FragmentOutput) obj;
    if (!this.index.equals(other.index)) {
      return false;
    }
    if (!this.name.equals(other.name)) {
      return false;
    }
    if (!this.type.equals(other.type)) {
      return false;
    }
    return true;
  }

  /**
   * Retrieve the integer index of the output. This is used to address the
   * output on older versions of OpenGL that do not support named fragment
   * shader outputs.
   */

  public @Nonnull Integer getIndex()
  {
    return this.index;
  }

  /**
   * Retrieve the name of the output. This is used to address the output on
   * newer versions of OpenGL that support named fragment shader outputs.
   */

  public @Nonnull String getName()
  {
    return this.name;
  }

  /**
   * Retrieve the name of the type of the output. This is the type as it
   * appears in GLSL. For example, most fragment shaders have outputs of type
   * <code>"vec4"</code>.
   */

  public @Nonnull String getType()
  {
    return this.type;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.index.hashCode();
    result = (prime * result) + this.name.hashCode();
    result = (prime * result) + this.type.hashCode();
    return result;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[FragmentOutput ");
    builder.append(this.index);
    builder.append(" ");
    builder.append(this.name);
    builder.append(" ");
    builder.append(this.type);
    builder.append("]");
    return builder.toString();
  }

  public @Nonnull Element toXML()
  {
    final String uri = PGLSLMetaXML.XML_URI_STRING;
    final Element e = new Element("g:fragment-output", uri);
    e.addAttribute(new Attribute("g:name", uri, this.name));
    e.addAttribute(new Attribute("g:type", uri, this.type));
    e.addAttribute(new Attribute("g:index", uri, this.index.toString()));
    return e;
  }
}
