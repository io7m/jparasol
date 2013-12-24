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

package com.io7m.jparasol.typed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnimplementedCodeException;

public abstract class TType
{
  /**
   * The main boolean type.
   */

  public static final class TBoolean extends TManifestType
  {
    private final static @Nonnull TBoolean INSTANCE = new TBoolean();

    public static @Nonnull TBoolean get()
    {
      return TBoolean.INSTANCE;
    }

    private TBoolean()
    {

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
      return true;
    }

    @Override public int getComponentCount()
    {
      return 1;
    }

    @Override public @Nonnull List<TConstructor> getConstructors()
    {
      final List<TConstructor> constructors = new ArrayList<TConstructor>();
      constructors.add(TConstructor
        .newConstructor(new TValueType[] { TBoolean.get() }));
      return constructors;
    }

    @Override public String getName()
    {
      return "boolean";
    }

    @Override public int hashCode()
    {
      return this.getName().hashCode();
    }
  }

  /**
   * A constructor.
   */

  public static final class TConstructor
  {
    static @Nonnull TConstructor newConstructor(
      final TValueType[] parameters)
    {
      return new TConstructor(Arrays.asList(parameters));
    }

    private final @Nonnull List<TValueType> parameters;

    private TConstructor(
      final @Nonnull List<TValueType> parameters)
    {
      this.parameters = parameters;
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
      final TConstructor other = (TConstructor) obj;
      if (!this.parameters.equals(other.parameters)) {
        return false;
      }
      return true;
    }

    public int getComponentCount()
    {
      int sum = 0;

      for (int index = 0; index < this.parameters.size(); ++index) {
        sum += this.parameters.get(index).getComponentCount();
      }

      return sum;
    }

    public @Nonnull List<TValueType> getParameters()
    {
      return this.parameters;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.parameters.hashCode();
      return result;
    }

    public @Nonnull String show()
    {
      final StringBuilder sb = new StringBuilder();

      sb.append("(");

      final int max = this.parameters.size();
      for (int index = 0; index < max; ++index) {
        sb.append(this.parameters.get(index).getName());
        if ((index + 1) == max) {
          sb.append(")");
        } else {
          sb.append(", ");
        }
      }

      return sb.toString();
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TConstructor ");
      builder.append(this.parameters);
      builder.append("]");
      return builder.toString();
    }
  }

  /**
   * The main floating point type.
   */

  public static final class TFloat extends TManifestType
  {
    private final static @Nonnull TFloat INSTANCE = new TFloat();

    public static @Nonnull TFloat get()
    {
      return TFloat.INSTANCE;
    }

    private TFloat()
    {

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
      return true;
    }

    @Override public int getComponentCount()
    {
      return 1;
    }

    @Override public @Nonnull List<TConstructor> getConstructors()
    {
      final List<TConstructor> constructors = new ArrayList<TConstructor>();
      constructors.add(TConstructor.newConstructor(new TValueType[] { TFloat
        .get() }));
      constructors.add(TConstructor
        .newConstructor(new TValueType[] { TInteger.get() }));
      return constructors;
    }

    @Override public String getName()
    {
      return "float";
    }

    @Override public int hashCode()
    {
      return this.getName().hashCode();
    }
  }

  /**
   * The main integer type.
   */

  public static final class TInteger extends TManifestType
  {
    private final static @Nonnull TInteger INSTANCE = new TInteger();

    public static @Nonnull TInteger get()
    {
      return TInteger.INSTANCE;
    }

    private TInteger()
    {

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
      return true;
    }

    @Override public int getComponentCount()
    {
      return 1;
    }

    @Override public @Nonnull List<TConstructor> getConstructors()
    {
      final List<TConstructor> constructors = new ArrayList<TConstructor>();
      constructors.add(TConstructor
        .newConstructor(new TValueType[] { TInteger.get() }));
      constructors.add(TConstructor.newConstructor(new TValueType[] { TFloat
        .get() }));
      return constructors;
    }

    @Override public String getName()
    {
      return "integer";
    }

    @Override public int hashCode()
    {
      return this.getName().hashCode();
    }
  }

  /**
   * Values of a manifest type can be used as record fields.
   * 
   * Notably, texture samplers are not manifest types.
   */

  public static abstract class TManifestType extends TValueType
  {
    // Nothing
  }

  /**
   * 3x3 matrix type.
   */

  public static final class TMatrix3x3F extends TMatrixType
  {
    private final static @Nonnull TMatrix3x3F INSTANCE = new TMatrix3x3F();

    public static @Nonnull TMatrix3x3F get()
    {
      return TMatrix3x3F.INSTANCE;
    }

    private TMatrix3x3F()
    {

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
      return true;
    }

    @Override public int getComponentCount()
    {
      return 3 * 3;
    }

    @Override public @Nonnull List<TConstructor> getConstructors()
    {
      final List<TConstructor> constructors = new ArrayList<TConstructor>();
      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TVector3F.get(),
        TVector3F.get(),
        TVector3F.get() }));
      return constructors;
    }

    @Override public String getName()
    {
      return "matrix_3x3f";
    }

    @Override public int hashCode()
    {
      return this.getName().hashCode();
    }
  }

  /**
   * 4x4 matrix type.
   */

  public static final class TMatrix4x4F extends TMatrixType
  {
    private final static @Nonnull TMatrix4x4F INSTANCE = new TMatrix4x4F();

    public static @Nonnull TMatrix4x4F get()
    {
      return TMatrix4x4F.INSTANCE;
    }

    private TMatrix4x4F()
    {

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
      return true;
    }

    @Override public int getComponentCount()
    {
      return 4 * 4;
    }

    @Override public @Nonnull List<TConstructor> getConstructors()
    {
      final List<TConstructor> constructors = new ArrayList<TConstructor>();
      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TVector4F.get(),
        TVector4F.get(),
        TVector4F.get(),
        TVector4F.get() }));
      return constructors;
    }

    @Override public String getName()
    {
      return "matrix_4x4f";
    }

    @Override public int hashCode()
    {
      return this.getName().hashCode();
    }
  }

  /**
   * Matrix types.
   */

  public static abstract class TMatrixType extends TManifestType
  {
    // Nothing
  }

  /**
   * The main integer type.
   */

  public static final class TRecord extends TManifestType
  {
    private final @Nonnull String name;

    private TRecord(
      final @Nonnull String name)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
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
      final TRecord other = (TRecord) obj;
      if (!this.name.equals(other.name)) {
        return false;
      }
      return true;
    }

    @Override public int getComponentCount()
    {
      // TODO: XXX
      throw new UnimplementedCodeException();
    }

    @Override public List<TConstructor> getConstructors()
    {
      return new ArrayList<TType.TConstructor>();
    }

    @Override public String getName()
    {
      return this.name;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.name.hashCode();
      return result;
    }
  }

  /**
   * 2D texture sampler type.
   */

  public static final class TSampler2D extends TValueType
  {
    private final static @Nonnull TSampler2D INSTANCE = new TSampler2D();

    public static @Nonnull TSampler2D get()
    {
      return TSampler2D.INSTANCE;
    }

    private TSampler2D()
    {

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
      return true;
    }

    @Override public int getComponentCount()
    {
      return 1;
    }

    @Override public @Nonnull List<TConstructor> getConstructors()
    {
      return new ArrayList<TConstructor>();
    }

    @Override public String getName()
    {
      return "sampler_2d";
    }

    @Override public int hashCode()
    {
      return this.getName().hashCode();
    }
  }

  /**
   * Cube texture sampler type.
   */

  public static final class TSamplerCube extends TValueType
  {
    private final static @Nonnull TSamplerCube INSTANCE = new TSamplerCube();

    public static @Nonnull TSamplerCube get()
    {
      return TSamplerCube.INSTANCE;
    }

    private TSamplerCube()
    {

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
      return true;
    }

    @Override public int getComponentCount()
    {
      return 1;
    }

    @Override public @Nonnull List<TConstructor> getConstructors()
    {
      return new ArrayList<TConstructor>();
    }

    @Override public String getName()
    {
      return "sampler_cube";
    }

    @Override public int hashCode()
    {
      return this.getName().hashCode();
    }
  }

  /**
   * Values of a value type can be passed around as values; they can be passed
   * to functions as arguments, returned from functions, etc.
   * 
   * Notably, values of function types are not values.
   */

  public static abstract class TValueType extends TType
  {
    // Nothing
  }

  /**
   * 2D floating point vector type.
   */

  public static final class TVector2F extends TVectorType
  {
    private final static @Nonnull TVector2F INSTANCE = new TVector2F();

    public static @Nonnull TVector2F get()
    {
      return TVector2F.INSTANCE;
    }

    private TVector2F()
    {

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
      return true;
    }

    @Override public int getComponentCount()
    {
      return 2;
    }

    @SuppressWarnings("synthetic-access") @Override public
      List<String>
      getComponentNames()
    {
      return TType.VECTOR_2_COMPONENT_NAMES;
    }

    @Override public TValueType getComponentType()
    {
      return TFloat.get();
    }

    @Override public @Nonnull List<TConstructor> getConstructors()
    {
      final List<TConstructor> constructors = new ArrayList<TConstructor>();
      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TFloat.get(),
        TFloat.get() }));
      constructors.add(TConstructor
        .newConstructor(new TValueType[] { TVector2F.get() }));
      return constructors;
    }

    @Override public String getName()
    {
      return "vector_2f";
    }

    @Override public int hashCode()
    {
      return this.getName().hashCode();
    }
  }

  /**
   * 2D integer vector type.
   */

  public static final class TVector2I extends TVectorType
  {
    private final static @Nonnull TVector2I INSTANCE = new TVector2I();

    public static @Nonnull TVector2I get()
    {
      return TVector2I.INSTANCE;
    }

    private TVector2I()
    {

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
      return true;
    }

    @Override public int getComponentCount()
    {
      return 2;
    }

    @SuppressWarnings("synthetic-access") @Override public
      List<String>
      getComponentNames()
    {
      return TType.VECTOR_2_COMPONENT_NAMES;
    }

    @Override public TValueType getComponentType()
    {
      return TInteger.get();
    }

    @Override public @Nonnull List<TConstructor> getConstructors()
    {
      final List<TConstructor> constructors = new ArrayList<TConstructor>();
      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TInteger.get(),
        TInteger.get() }));
      constructors.add(TConstructor
        .newConstructor(new TValueType[] { TVector2I.get() }));
      return constructors;
    }

    @Override public String getName()
    {
      return "vector_2i";
    }

    @Override public int hashCode()
    {
      return this.getName().hashCode();
    }
  }

  /**
   * 3D floating point vector type.
   */

  public static final class TVector3F extends TVectorType
  {
    private final static @Nonnull TVector3F INSTANCE = new TVector3F();

    public static @Nonnull TVector3F get()
    {
      return TVector3F.INSTANCE;
    }

    private TVector3F()
    {

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
      return true;
    }

    @Override public int getComponentCount()
    {
      return 3;
    }

    @SuppressWarnings("synthetic-access") @Override public
      List<String>
      getComponentNames()
    {
      return TType.VECTOR_3_COMPONENT_NAMES;
    }

    @Override public TValueType getComponentType()
    {
      return TFloat.get();
    }

    @Override public @Nonnull List<TConstructor> getConstructors()
    {
      final List<TConstructor> constructors = new ArrayList<TConstructor>();

      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TFloat.get(),
        TFloat.get(),
        TFloat.get() }));

      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TVector2F.get(),
        TFloat.get() }));

      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TFloat.get(),
        TVector2F.get() }));

      constructors.add(TConstructor
        .newConstructor(new TValueType[] { TVector3F.get() }));

      return constructors;
    }

    @Override public String getName()
    {
      return "vector_3f";
    }

    @Override public int hashCode()
    {
      return this.getName().hashCode();
    }
  }

  /**
   * 3D integer vector type.
   */

  public static final class TVector3I extends TVectorType
  {
    private final static @Nonnull TVector3I INSTANCE = new TVector3I();

    public static @Nonnull TVector3I get()
    {
      return TVector3I.INSTANCE;
    }

    private TVector3I()
    {

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
      return true;
    }

    @Override public int getComponentCount()
    {
      return 3;
    }

    @SuppressWarnings("synthetic-access") @Override public
      List<String>
      getComponentNames()
    {
      return TType.VECTOR_3_COMPONENT_NAMES;
    }

    @Override public TValueType getComponentType()
    {
      return TInteger.get();
    }

    @Override public @Nonnull List<TConstructor> getConstructors()
    {
      final List<TConstructor> constructors = new ArrayList<TConstructor>();

      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TInteger.get(),
        TInteger.get(),
        TInteger.get() }));

      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TVector2I.get(),
        TInteger.get() }));

      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TInteger.get(),
        TVector2I.get() }));

      constructors.add(TConstructor
        .newConstructor(new TValueType[] { TVector3I.get() }));

      return constructors;
    }

    @Override public String getName()
    {
      return "vector_3f";
    }

    @Override public int hashCode()
    {
      return this.getName().hashCode();
    }
  }

  /**
   * 4D floating point vector type.
   */

  public static final class TVector4F extends TVectorType
  {
    private final static @Nonnull TVector4F INSTANCE = new TVector4F();

    public static @Nonnull TVector4F get()
    {
      return TVector4F.INSTANCE;
    }

    private TVector4F()
    {

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
      return true;
    }

    @Override public int getComponentCount()
    {
      return 4;
    }

    @SuppressWarnings("synthetic-access") @Override public
      List<String>
      getComponentNames()
    {
      return TType.VECTOR_4_COMPONENT_NAMES;
    }

    @Override public TValueType getComponentType()
    {
      return TFloat.get();
    }

    @Override public @Nonnull List<TConstructor> getConstructors()
    {
      final List<TConstructor> constructors = new ArrayList<TConstructor>();

      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TFloat.get(),
        TFloat.get(),
        TFloat.get(),
        TFloat.get() }));

      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TVector2F.get(),
        TFloat.get(),
        TFloat.get() }));

      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TFloat.get(),
        TFloat.get(),
        TVector2F.get() }));

      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TFloat.get(),
        TVector2F.get(),
        TFloat.get() }));

      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TVector2F.get(),
        TVector2F.get() }));

      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TVector3F.get(),
        TFloat.get() }));

      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TFloat.get(),
        TVector3F.get() }));

      constructors.add(TConstructor
        .newConstructor(new TValueType[] { TVector4F.get() }));

      return constructors;
    }

    @Override public String getName()
    {
      return "vector_4f";
    }

    @Override public int hashCode()
    {
      return this.getName().hashCode();
    }
  }

  /**
   * 4D integer vector type.
   */

  public static final class TVector4I extends TVectorType
  {
    private final static @Nonnull TVector4I INSTANCE = new TVector4I();

    public static @Nonnull TVector4I get()
    {
      return TVector4I.INSTANCE;
    }

    private TVector4I()
    {

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
      return true;
    }

    @Override public int getComponentCount()
    {
      return 4;
    }

    @SuppressWarnings("synthetic-access") @Override public
      List<String>
      getComponentNames()
    {
      return TType.VECTOR_4_COMPONENT_NAMES;
    }

    @Override public TValueType getComponentType()
    {
      return TInteger.get();
    }

    @Override public @Nonnull List<TConstructor> getConstructors()
    {
      final List<TConstructor> constructors = new ArrayList<TConstructor>();

      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TInteger.get(),
        TInteger.get(),
        TInteger.get(),
        TInteger.get() }));

      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TVector2I.get(),
        TInteger.get(),
        TInteger.get() }));

      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TInteger.get(),
        TInteger.get(),
        TVector2I.get() }));

      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TInteger.get(),
        TVector2I.get(),
        TInteger.get() }));

      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TVector2I.get(),
        TVector2I.get() }));

      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TVector3I.get(),
        TInteger.get() }));

      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TInteger.get(),
        TVector3I.get() }));

      constructors.add(TConstructor
        .newConstructor(new TValueType[] { TVector4I.get() }));

      return constructors;
    }

    @Override public String getName()
    {
      return "vector_4i";
    }

    @Override public int hashCode()
    {
      return this.getName().hashCode();
    }
  }

  /**
   * Values of a vector type can be used in swizzle expressions.
   */

  public static abstract class TVectorType extends TManifestType
  {
    public abstract @Nonnull List<String> getComponentNames();

    public abstract @Nonnull TValueType getComponentType();
  }

  private static final @Nonnull List<String> VECTOR_2_COMPONENT_NAMES;

  private static final @Nonnull List<String> VECTOR_3_COMPONENT_NAMES;

  private static final @Nonnull List<String> VECTOR_4_COMPONENT_NAMES;

  static {
    VECTOR_2_COMPONENT_NAMES =
      Collections.unmodifiableList(Arrays.asList(new String[] { "x", "y" }));

    VECTOR_3_COMPONENT_NAMES =
      Collections.unmodifiableList(Arrays
        .asList(new String[] { "x", "y", "z" }));

    VECTOR_4_COMPONENT_NAMES =
      Collections.unmodifiableList(Arrays.asList(new String[] {
    "x",
    "y",
    "z",
    "w" }));
  }

  public static @Nonnull Map<String, TType> getBaseTypes()
  {
    final HashMap<String, TType> m = new HashMap<String, TType>();
    m.put(TBoolean.get().getName(), TBoolean.get());
    m.put(TInteger.get().getName(), TInteger.get());
    m.put(TFloat.get().getName(), TFloat.get());
    m.put(TMatrix3x3F.get().getName(), TMatrix3x3F.get());
    m.put(TMatrix4x4F.get().getName(), TMatrix4x4F.get());
    m.put(TSampler2D.get().getName(), TSampler2D.get());
    m.put(TSamplerCube.get().getName(), TSamplerCube.get());
    m.put(TVector2F.get().getName(), TVector2F.get());
    m.put(TVector3F.get().getName(), TVector3F.get());
    m.put(TVector4F.get().getName(), TVector4F.get());
    m.put(TVector2I.get().getName(), TVector2I.get());
    m.put(TVector3I.get().getName(), TVector3I.get());
    m.put(TVector4I.get().getName(), TVector4I.get());
    return Collections.unmodifiableMap(m);
  }

  /**
   * Return the number of components in the given type.
   */

  public abstract int getComponentCount();

  /**
   * Retrieve the list of available constructors for the type. An empty list
   * means the type is not constructable.
   */

  public abstract @Nonnull List<TConstructor> getConstructors();

  /**
   * Retrieve the name of the type as it appears in Parasol programs.
   */

  public abstract @Nonnull String getName();
}
