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

package com.io7m.jparasol.glsl.ast;

import java.util.List;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jfunctional.Pair;
import com.io7m.jnull.NullCheck;

/**
 * The type of type declarations.
 */

@EqualityReference public abstract class GASTTypeDeclaration
{
  /**
   * A record declaration type.
   */

  @EqualityReference public static final class GASTTypeRecord extends
    GASTTypeDeclaration
  {
    private final List<Pair<GFieldName, GTypeName>> fields;
    private final GTypeName                         name;

    /**
     * Construct a record type.
     * 
     * @param in_name
     *          The name of the type
     * @param in_fields
     *          The record fields, in declaration order
     */

    public GASTTypeRecord(
      final GTypeName in_name,
      final List<Pair<GFieldName, GTypeName>> in_fields)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.fields = NullCheck.notNull(in_fields, "Fields");
    }

    /**
     * @return The list of fields, in declaration order
     */

    public List<Pair<GFieldName, GTypeName>> getFields()
    {
      return this.fields;
    }

    @Override public GTypeName getName()
    {
      return this.name;
    }

    @Override public
      <A, E extends Throwable, V extends GASTTypeDeclarationVisitorType<A, E>>
      A
      typeDeclarationVisitableAccept(
        final V v)
        throws E
    {
      return v.typeVisitRecord(this);
    }
  }

  /**
   * @return The name of the type
   */

  public abstract GTypeName getName();

  /**
   * Accept a generic visitor.
   * 
   * @param v
   *          The visitor
   * @return The value returned by the visitor
   * @throws E
   *           If the visitor raises <code>E</code>
   */

  public abstract
    <A, E extends Throwable, V extends GASTTypeDeclarationVisitorType<A, E>>
    A
    typeDeclarationVisitableAccept(
      final V v)
      throws E;
}
