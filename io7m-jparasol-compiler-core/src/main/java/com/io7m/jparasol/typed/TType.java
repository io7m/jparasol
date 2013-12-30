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
import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jparasol.typed.TTypeName.TTypeNameBuiltIn;
import com.io7m.jparasol.typed.TTypeName.TTypeNameGlobal;
import com.io7m.jparasol.typed.ast.TASTExpression;

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

    private final @Nonnull TTypeNameBuiltIn name;

    private TBoolean()
    {
      try {
        this.name = new TTypeNameBuiltIn("boolean");
      } catch (final ConstraintError e) {
        throw new UnreachableCodeException(e);
      }
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

    @Override public TTypeNameBuiltIn getName()
    {
      return this.name;
    }

    @Override public int hashCode()
    {
      return this.getName().hashCode();
    }

    @Override public String toString()
    {
      return "[TBoolean]";
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

    private final @Nonnull TTypeNameBuiltIn name;

    private TFloat()
    {
      try {
        this.name = new TTypeNameBuiltIn("float");
      } catch (final ConstraintError e) {
        throw new UnreachableCodeException(e);
      }
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

    @Override public TTypeNameBuiltIn getName()
    {
      return this.name;
    }

    @Override public int hashCode()
    {
      return this.getName().hashCode();
    }

    @Override public String toString()
    {
      return "[TFloat]";
    }
  }

  /**
   * Function type.
   */

  public static final class TFunction extends TType
  {
    private final @Nonnull List<TFunctionArgument> arguments;
    private final @Nonnull TValueType              return_type;
    private final @Nonnull TTypeNameBuiltIn        name;

    public TFunction(
      final @Nonnull List<TFunctionArgument> arguments,
      final @Nonnull TValueType return_type)
      throws ConstraintError
    {
      this.arguments = Constraints.constrainNotNull(arguments, "Arguments");
      this.return_type =
        Constraints.constrainNotNull(return_type, "Return type");

      final StringBuilder m = new StringBuilder();
      m.append(TType.formatFunctionArguments(this.arguments));
      m.append(" → ");
      m.append(this.return_type.getName());
      this.name = new TTypeNameBuiltIn(m.toString());
    }

    public @Nonnull List<TFunctionArgument> getArguments()
    {
      return this.arguments;
    }

    @Override public int getComponentCount()
    {
      return 1;
    }

    @Override public List<TConstructor> getConstructors()
    {
      return new ArrayList<TConstructor>();
    }

    @Override public TTypeNameBuiltIn getName()
    {
      return this.name;
    }

    public @Nonnull TValueType getReturnType()
    {
      return this.return_type;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TFunction ");
      builder.append(this.arguments);
      builder.append(" ");
      builder.append(this.return_type);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class TFunctionArgument
  {
    private final @Nonnull String     name;
    private final @Nonnull TValueType type;

    public TFunctionArgument(
      final @Nonnull String name,
      final @Nonnull TValueType type)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.type = Constraints.constrainNotNull(type, "Type");
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
      final TFunctionArgument other = (TFunctionArgument) obj;
      if (!this.name.equals(other.name)) {
        return false;
      }
      if (!this.type.equals(other.type)) {
        return false;
      }
      return true;
    }

    public @Nonnull String getName()
    {
      return this.name;
    }

    public @Nonnull TValueType getType()
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

    private final @Nonnull TTypeNameBuiltIn name;

    private TInteger()
    {
      try {
        this.name = new TTypeNameBuiltIn("integer");
      } catch (final ConstraintError e) {
        throw new UnreachableCodeException(e);
      }
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

    @Override public @Nonnull TTypeNameBuiltIn getName()
    {
      return this.name;
    }

    @Override public int hashCode()
    {
      return this.getName().hashCode();
    }

    @Override public String toString()
    {
      return "[TInteger]";
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

    private final @Nonnull TTypeNameBuiltIn name;

    private TMatrix3x3F()
    {
      try {
        this.name = new TTypeNameBuiltIn("matrix_3x3f");
      } catch (final ConstraintError e) {
        throw new UnreachableCodeException(e);
      }
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

    @Override public @Nonnull TTypeNameBuiltIn getName()
    {
      return this.name;
    }

    @Override public int hashCode()
    {
      return this.getName().hashCode();
    }

    @Override public String toString()
    {
      return "[TMatrix3x3F]";
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

    private final @Nonnull TTypeNameBuiltIn name;

    private TMatrix4x4F()
    {
      try {
        this.name = new TTypeNameBuiltIn("matrix_4x4f");
      } catch (final ConstraintError e) {
        throw new UnreachableCodeException(e);
      }
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

    @Override public @Nonnull TTypeNameBuiltIn getName()
    {
      return this.name;
    }

    @Override public int hashCode()
    {
      return this.getName().hashCode();
    }

    @Override public String toString()
    {
      return "[TMatrix4x4F]";
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
   * The record type.
   */

  public static final class TRecord extends TManifestType
  {
    private final @Nonnull List<TRecordField> fields;
    private final @Nonnull TTypeNameGlobal    name;

    public TRecord(
      final @Nonnull TTypeNameGlobal name,
      final @Nonnull List<TRecordField> fields)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.fields = Constraints.constrainNotNull(fields, "Fields");
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
      return 1;
    }

    @Override public List<TConstructor> getConstructors()
    {
      return new ArrayList<TType.TConstructor>();
    }

    public @Nonnull List<TRecordField> getFields()
    {
      return this.fields;
    }

    @Override public @Nonnull TTypeNameGlobal getName()
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

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TRecord ");
      builder.append(this.name);
      builder.append(" ");
      builder.append(this.fields);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class TRecordField
  {
    private final @Nonnull String        name;
    private final @Nonnull TManifestType type;

    public TRecordField(
      final @Nonnull String name,
      final @Nonnull TManifestType type)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.type = Constraints.constrainNotNull(type, "Type");
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
      final TRecordField other = (TRecordField) obj;
      if (!this.name.equals(other.name)) {
        return false;
      }
      if (!this.type.equals(other.type)) {
        return false;
      }
      return true;
    }

    public @Nonnull String getName()
    {
      return this.name;
    }

    public @Nonnull TManifestType getType()
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
      builder.append("[TRecordField ");
      builder.append(this.name);
      builder.append(" ");
      builder.append(this.type);
      builder.append("]");
      return builder.toString();
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

    private final @Nonnull TTypeNameBuiltIn name;

    private TSampler2D()
    {
      try {
        this.name = new TTypeNameBuiltIn("sampler_2d");
      } catch (final ConstraintError e) {
        throw new UnreachableCodeException(e);
      }
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

    @Override public @Nonnull TTypeNameBuiltIn getName()
    {
      return this.name;
    }

    @Override public int hashCode()
    {
      return this.getName().hashCode();
    }

    @Override public String toString()
    {
      return "[TSampler2D]";
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

    private final @Nonnull TTypeNameBuiltIn name;

    private TSamplerCube()
    {
      try {
        this.name = new TTypeNameBuiltIn("sampler_cube");
      } catch (final ConstraintError e) {
        throw new UnreachableCodeException(e);
      }
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

    @Override public @Nonnull TTypeNameBuiltIn getName()
    {
      return this.name;
    }

    @Override public int hashCode()
    {
      return this.getName().hashCode();
    }

    @Override public String toString()
    {
      return "[TSamplerCube]";
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

  public static abstract class TVectorFType extends TVectorType
  {
    @Override public final TValueType getComponentType()
    {
      return TFloat.get();
    }

    @Override public TManifestType getSwizzleType(
      final int size)
    {
      switch (size) {
        case 1:
          return TFloat.get();
        case 2:
          return TVector2F.get();
        case 3:
          return TVector3F.get();
        case 4:
          return TVector4F.get();
        default:
          throw new UnreachableCodeException();
      }
    }
  }

  public static abstract class TVectorIType extends TVectorType
  {
    @Override public final TValueType getComponentType()
    {
      return TInteger.get();
    }

    @Override public TManifestType getSwizzleType(
      final int size)
    {
      switch (size) {
        case 1:
          return TInteger.get();
        case 2:
          return TVector2I.get();
        case 3:
          return TVector3I.get();
        case 4:
          return TVector4I.get();
        default:
          throw new UnreachableCodeException();
      }
    }
  }

  /**
   * 2D floating point vector type.
   */

  public static final class TVector2F extends TVectorFType
  {
    private final static @Nonnull TVector2F INSTANCE = new TVector2F();

    public static @Nonnull TVector2F get()
    {
      return TVector2F.INSTANCE;
    }

    private final @Nonnull TTypeNameBuiltIn name;

    private TVector2F()
    {
      try {
        this.name = new TTypeNameBuiltIn("vector_2f");
      } catch (final ConstraintError e) {
        throw new UnreachableCodeException(e);
      }
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

    @Override public @Nonnull TTypeNameBuiltIn getName()
    {
      return this.name;
    }

    @Override public int hashCode()
    {
      return this.getName().hashCode();
    }

    @Override public String toString()
    {
      return "[TVector2F]";
    }
  }

  /**
   * 2D integer vector type.
   */

  public static final class TVector2I extends TVectorIType
  {
    private final static @Nonnull TVector2I INSTANCE = new TVector2I();

    public static @Nonnull TVector2I get()
    {
      return TVector2I.INSTANCE;
    }

    private final @Nonnull TTypeNameBuiltIn name;

    private TVector2I()
    {
      try {
        this.name = new TTypeNameBuiltIn("vector_2i");
      } catch (final ConstraintError e) {
        throw new UnreachableCodeException(e);
      }
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

    @Override public @Nonnull TTypeNameBuiltIn getName()
    {
      return this.name;
    }

    @Override public int hashCode()
    {
      return this.getName().hashCode();
    }

    @Override public String toString()
    {
      return "[TVector2I]";
    }
  }

  /**
   * 3D floating point vector type.
   */

  public static final class TVector3F extends TVectorFType
  {
    private final static @Nonnull TVector3F INSTANCE = new TVector3F();

    public static @Nonnull TVector3F get()
    {
      return TVector3F.INSTANCE;
    }

    private final @Nonnull TTypeNameBuiltIn name;

    private TVector3F()
    {
      try {
        this.name = new TTypeNameBuiltIn("vector_3f");
      } catch (final ConstraintError e) {
        throw new UnreachableCodeException(e);
      }
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

    @Override public @Nonnull TTypeNameBuiltIn getName()
    {
      return this.name;
    }

    @Override public int hashCode()
    {
      return this.getName().hashCode();
    }

    @Override public String toString()
    {
      return "[TVector3F]";
    }
  }

  /**
   * 3D integer vector type.
   */

  public static final class TVector3I extends TVectorIType
  {
    private final static @Nonnull TVector3I INSTANCE = new TVector3I();

    public static @Nonnull TVector3I get()
    {
      return TVector3I.INSTANCE;
    }

    private final @Nonnull TTypeNameBuiltIn name;

    private TVector3I()
    {
      try {
        this.name = new TTypeNameBuiltIn("vector_3i");
      } catch (final ConstraintError e) {
        throw new UnreachableCodeException(e);
      }
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

    @Override public @Nonnull TTypeNameBuiltIn getName()
    {
      return this.name;
    }

    @Override public int hashCode()
    {
      return this.getName().hashCode();
    }

    @Override public String toString()
    {
      return "[TVector3I]";
    }
  }

  /**
   * 4D floating point vector type.
   */

  public static final class TVector4F extends TVectorFType
  {
    private final static @Nonnull TVector4F INSTANCE = new TVector4F();

    public static @Nonnull TVector4F get()
    {
      return TVector4F.INSTANCE;
    }

    private final @Nonnull TTypeNameBuiltIn name;

    private TVector4F()
    {
      try {
        this.name = new TTypeNameBuiltIn("vector_4f");
      } catch (final ConstraintError e) {
        throw new UnreachableCodeException(e);
      }
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

    @Override public @Nonnull TTypeNameBuiltIn getName()
    {
      return this.name;
    }

    @Override public int hashCode()
    {
      return this.getName().hashCode();
    }

    @Override public String toString()
    {
      return "[TVector4F]";
    }
  }

  /**
   * 4D integer vector type.
   */

  public static final class TVector4I extends TVectorIType
  {
    private final static @Nonnull TVector4I INSTANCE = new TVector4I();

    public static @Nonnull TVector4I get()
    {
      return TVector4I.INSTANCE;
    }

    private final @Nonnull TTypeNameBuiltIn name;

    private TVector4I()
    {
      try {
        this.name = new TTypeNameBuiltIn("vector_4i");
      } catch (final ConstraintError e) {
        throw new UnreachableCodeException(e);
      }
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

    @Override public @Nonnull TTypeNameBuiltIn getName()
    {
      return this.name;
    }

    @Override public int hashCode()
    {
      return this.getName().hashCode();
    }

    @Override public String toString()
    {
      return "[TVector4I]";
    }
  }

  /**
   * Values of a vector type can be used in swizzle expressions.
   */

  public static abstract class TVectorType extends TManifestType
  {
    public abstract @Nonnull List<String> getComponentNames();

    public abstract @Nonnull TValueType getComponentType();

    public abstract @Nonnull TManifestType getSwizzleType(
      int size);
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

  public static @Nonnull String formatFunctionArguments(
    final @Nonnull List<TFunctionArgument> arguments)
  {
    final StringBuilder m = new StringBuilder();
    m.append("(");
    for (int index = 0; index < arguments.size(); ++index) {
      m.append(arguments.get(index).getType().getName());
      if ((index + 1) < arguments.size()) {
        m.append(" × ");
      }
    }
    m.append(")");
    return m.toString();
  }

  public static @Nonnull String formatTypeExpressionList(
    final @Nonnull List<TASTExpression> es)
  {
    final StringBuilder m = new StringBuilder();
    m.append("(");
    for (int index = 0; index < es.size(); ++index) {
      m.append(es.get(index).getType().getName());
      if ((index + 1) < es.size()) {
        m.append(" × ");
      }
    }
    m.append(")");
    return m.toString();
  }

  public static @Nonnull <T extends TType> String formatTypeList(
    final @Nonnull List<T> list)
  {
    final StringBuilder m = new StringBuilder();
    m.append("(");
    for (int index = 0; index < list.size(); ++index) {
      m.append(list.get(index).getName());
      if ((index + 1) < list.size()) {
        m.append(" × ");
      }
    }
    m.append(")");
    return m.toString();
  }

  public static @Nonnull Map<TTypeNameBuiltIn, TType> getBaseTypesByName()
  {
    final HashMap<TTypeNameBuiltIn, TType> m =
      new HashMap<TTypeNameBuiltIn, TType>();

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

  public abstract @Nonnull TTypeName getName();

  public final @Nonnull String getShowName()
  {
    return this.getName().show();
  }
}
