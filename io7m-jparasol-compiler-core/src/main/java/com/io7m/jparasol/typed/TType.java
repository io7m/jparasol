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

package com.io7m.jparasol.typed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.typed.TTypeName.TTypeNameBuiltIn;
import com.io7m.jparasol.typed.TTypeName.TTypeNameGlobal;
import com.io7m.jparasol.typed.ast.TASTExpression;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * The type of Parasol types.
 */

// CHECKSTYLE_SPACE:OFF

@EqualityReference public abstract class TType
{
  /**
   * The main boolean type.
   */

  @EqualityReference public static final class TBoolean extends TManifestType
  {
    private final static TBoolean INSTANCE = new TBoolean();

    /**
     * @return The boolean type singleton
     */

    public static TBoolean get()
    {
      return TBoolean.INSTANCE;
    }

    private final TTypeNameBuiltIn name;

    private TBoolean()
    {
      this.name = new TTypeNameBuiltIn("boolean");
    }

    @Override public int getComponentCount()
    {
      return 1;
    }

    @Override public List<TConstructor> getConstructors()
    {
      final List<TConstructor> constructors = new ArrayList<TConstructor>();

      constructors.add(TConstructor
        .newConstructor(new TValueType[] { TBoolean.get() }));
      constructors.add(TConstructor.newConstructor(new TValueType[] { TFloat
        .get(), }));
      constructors.add(TConstructor
        .newConstructor(new TValueType[] { TInteger.get() }));

      return constructors;
    }

    @Override public TTypeNameBuiltIn getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      return "[TBoolean]";
    }

    @Override public
      <A, E extends Throwable, V extends TTypeVisitorType<A, E>>
      A
      ttypeVisitableAccept(
        final V v)
        throws E
    {
      return v.typeVisitBoolean(this);
    }
  }

  /**
   * A constructor.
   */

  @EqualityReference public static final class TConstructor
  {
    /**
     * Construct a new constructor.
     *
     * @param parameters
     *          The types of the parameters
     * @return A constructor
     */

    public static TConstructor newConstructor(
      final TValueType[] parameters)
    {
      NullCheck.notNull(parameters, "Parameters");
      final List<TValueType> r = Arrays.asList(parameters);
      assert r != null;
      return new TConstructor(r);
    }

    private final List<TValueType> parameters;

    private TConstructor(
      final List<TValueType> in_parameters)
    {
      this.parameters = in_parameters;
    }

    /**
     * @return The number of parameters in the constructor
     */

    public int getComponentCount()
    {
      int sum = 0;

      for (int index = 0; index < this.parameters.size(); ++index) {
        sum += this.parameters.get(index).getComponentCount();
      }

      return sum;
    }

    /**
     * @return A list of parameter types
     */

    public List<TValueType> getParameters()
    {
      return this.parameters;
    }

    /**
     * @return A humanly readable image of the constructor
     */

    public String show()
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

      final String r = sb.toString();
      assert r != null;
      return r;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TConstructor ");
      builder.append(this.parameters);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  /**
   * The main floating point type.
   */

  @EqualityReference public static final class TFloat extends TManifestType
  {
    private final static TFloat INSTANCE = new TFloat();

    /**
     * @return The float type singleton
     */

    public static TFloat get()
    {
      return TFloat.INSTANCE;
    }

    private final TTypeNameBuiltIn name;

    private TFloat()
    {
      this.name = new TTypeNameBuiltIn("float");
    }

    @Override public int getComponentCount()
    {
      return 1;
    }

    @Override public List<TConstructor> getConstructors()
    {
      final List<TConstructor> constructors = new ArrayList<TConstructor>();

      constructors.add(TConstructor.newConstructor(new TValueType[] { TFloat
        .get(), }));
      constructors.add(TConstructor
        .newConstructor(new TValueType[] { TInteger.get() }));
      constructors.add(TConstructor
        .newConstructor(new TValueType[] { TBoolean.get() }));

      return constructors;
    }

    @Override public TTypeNameBuiltIn getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      return "[TFloat]";
    }

    @Override public
      <A, E extends Throwable, V extends TTypeVisitorType<A, E>>
      A
      ttypeVisitableAccept(
        final V v)
        throws E
    {
      return v.typeVisitFloat(this);
    }
  }

  /**
   * Function type.
   */

  @EqualityReference public static final class TFunction extends TType
  {
    private final List<TFunctionArgument> arguments;
    private final TTypeNameBuiltIn        name;
    private final TValueType              return_type;

    /**
     * Construct a function type.
     *
     * @param in_arguments
     *          The list of argument types
     * @param in_return_type
     *          The return type
     */

    public TFunction(
      final List<TFunctionArgument> in_arguments,
      final TValueType in_return_type)
    {
      this.arguments = NullCheck.notNull(in_arguments, "Arguments");
      this.return_type = NullCheck.notNull(in_return_type, "Return type");

      final StringBuilder m = new StringBuilder();
      m.append(TType.formatFunctionArguments(this.arguments));
      m.append(" → ");
      m.append(this.return_type.getShowName());
      final String r = m.toString();
      assert r != null;
      this.name = new TTypeNameBuiltIn(r);
    }

    /**
     * @return The list of argument types
     */

    public List<TFunctionArgument> getArguments()
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

    /**
     * @return The return type
     */

    public TValueType getReturnType()
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
      final String r = builder.toString();
      assert r != null;
      return r;
    }

    @Override public
      <A, E extends Throwable, V extends TTypeVisitorType<A, E>>
      A
      ttypeVisitableAccept(
        final V v)
        throws E
    {
      return v.typeVisitFunction(this);
    }
  }

  /**
   * Function argument type.
   */

  @EqualityReference public static final class TFunctionArgument
  {
    private final String     name;
    private final TValueType type;

    /**
     * Construct a function argument.
     *
     * @param in_name
     *          The name
     * @param in_type
     *          The type
     */

    public TFunctionArgument(
      final String in_name,
      final TValueType in_type)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.type = NullCheck.notNull(in_type, "Type");
    }

    /**
     * @return The name
     */

    public String getName()
    {
      return this.name;
    }

    /**
     * @return The argument type
     */

    public TType getType()
    {
      return this.type;
    }
  }

  /**
   * The main integer type.
   */

  @EqualityReference public static final class TInteger extends TManifestType
  {
    private final static TInteger INSTANCE = new TInteger();

    /**
     * @return The integer type singleton
     */

    public static TInteger get()
    {
      return TInteger.INSTANCE;
    }

    private final TTypeNameBuiltIn name;

    private TInteger()
    {
      this.name = new TTypeNameBuiltIn("integer");
    }

    @Override public int getComponentCount()
    {
      return 1;
    }

    @Override public List<TConstructor> getConstructors()
    {
      final List<TConstructor> constructors = new ArrayList<TConstructor>();

      constructors.add(TConstructor
        .newConstructor(new TValueType[] { TInteger.get(), }));
      constructors.add(TConstructor.newConstructor(new TValueType[] { TFloat
        .get(), }));
      constructors.add(TConstructor
        .newConstructor(new TValueType[] { TBoolean.get(), }));

      return constructors;
    }

    @Override public TTypeNameBuiltIn getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      return "[TInteger]";
    }

    @Override public
      <A, E extends Throwable, V extends TTypeVisitorType<A, E>>
      A
      ttypeVisitableAccept(
        final V v)
        throws E
    {
      return v.typeVisitInteger(this);
    }
  }

  /**
   * Values of a manifest type can be used as record fields.
   *
   * Notably, texture samplers are not manifest types.
   */

  @EqualityReference public static abstract class TManifestType extends
    TValueType
  {
    // Nothing
  }

  /**
   * 3x3 matrix type.
   */

  @EqualityReference public static final class TMatrix3x3F extends
    TMatrixType
  {
    private final static TMatrix3x3F INSTANCE = new TMatrix3x3F();

    /**
     * @return The 3x3 matrix type singleton
     */

    public static TMatrix3x3F get()
    {
      return TMatrix3x3F.INSTANCE;
    }

    private final TTypeNameBuiltIn name;

    private TMatrix3x3F()
    {
      this.name = new TTypeNameBuiltIn("matrix_3x3f");
    }

    @Override int getColumns()
    {
      return 3;
    }

    @Override TVectorType getColumnType()
    {
      return TVector3F.get();
    }

    @Override public int getComponentCount()
    {
      return 3 * 3;
    }

    @Override public List<TConstructor> getConstructors()
    {
      final List<TConstructor> constructors = new ArrayList<TConstructor>();
      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TVector3F.get(),
        TVector3F.get(),
        TVector3F.get(), }));
      return constructors;
    }

    @Override public TTypeNameBuiltIn getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      return "[TMatrix3x3F]";
    }

    @Override public
      <A, E extends Throwable, V extends TTypeVisitorType<A, E>>
      A
      ttypeVisitableAccept(
        final V v)
        throws E
    {
      return v.typeVisitMatrix3x3F(this);
    }
  }

  /**
   * 4x4 matrix type.
   */

  @EqualityReference public static final class TMatrix4x4F extends
    TMatrixType
  {
    private final static TMatrix4x4F INSTANCE = new TMatrix4x4F();

    /**
     * @return The 4x4 matrix type singleton
     */

    public static TMatrix4x4F get()
    {
      return TMatrix4x4F.INSTANCE;
    }

    private final TTypeNameBuiltIn name;

    private TMatrix4x4F()
    {
      this.name = new TTypeNameBuiltIn("matrix_4x4f");
    }

    @Override int getColumns()
    {
      return 4;
    }

    @Override TVectorType getColumnType()
    {
      return TVector4F.get();
    }

    @Override public int getComponentCount()
    {
      return 4 * 4;
    }

    @Override public List<TConstructor> getConstructors()
    {
      final List<TConstructor> constructors = new ArrayList<TConstructor>();
      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TVector4F.get(),
        TVector4F.get(),
        TVector4F.get(),
        TVector4F.get(), }));
      return constructors;
    }

    @Override public TTypeNameBuiltIn getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      return "[TMatrix4x4F]";
    }

    @Override public
      <A, E extends Throwable, V extends TTypeVisitorType<A, E>>
      A
      ttypeVisitableAccept(
        final V v)
        throws E
    {
      return v.typeVisitMatrix4x4F(this);
    }
  }

  /**
   * Matrix types.
   */

  @EqualityReference public static abstract class TMatrixType extends
    TManifestType
  {
    /**
     * @return The number of matrix columns
     */

    abstract int getColumns();

    /**
     * @return The type of matrix columns
     */

    abstract TVectorType getColumnType();
  }

  /**
   * The record type.
   */

  @EqualityReference public static final class TRecord extends TManifestType
  {
    private final List<TRecordField> fields;
    private final TTypeNameGlobal    name;

    /**
     * Construct a record type.
     *
     * @param in_name
     *          The name of the type
     * @param in_fields
     *          The record fields
     */

    public TRecord(
      final TTypeNameGlobal in_name,
      final List<TRecordField> in_fields)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.fields = NullCheck.notNull(in_fields, "Fields");
    }

    @Override public int getComponentCount()
    {
      return 1;
    }

    @Override public List<TConstructor> getConstructors()
    {
      return new ArrayList<TType.TConstructor>();
    }

    /**
     * @return The list of record fields in declaration order
     */

    public List<TRecordField> getFields()
    {
      return this.fields;
    }

    @Override public TTypeNameGlobal getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TRecord ");
      builder.append(this.name);
      builder.append(" ");
      builder.append(this.fields);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }

    @Override public
      <A, E extends Throwable, V extends TTypeVisitorType<A, E>>
      A
      ttypeVisitableAccept(
        final V v)
        throws E
    {
      return v.typeVisitRecord(this);
    }
  }

  /**
   * The type of record fields.
   */

  @EqualityReference public static final class TRecordField
  {
    private final String        name;
    private final TManifestType type;

    /**
     * Construct a record field.
     *
     * @param in_name
     *          The field name
     * @param in_type
     *          The field type
     */

    public TRecordField(
      final String in_name,
      final TManifestType in_type)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.type = NullCheck.notNull(in_type, "Type");
    }

    /**
     * @return The name of the record field
     */

    public String getName()
    {
      return this.name;
    }

    /**
     * @return The type of the record field
     */

    public TManifestType getType()
    {
      return this.type;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TRecordField ");
      builder.append(this.name);
      builder.append(" ");
      builder.append(this.type);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  /**
   * 2D texture sampler type.
   */

  @EqualityReference public static final class TSampler2D extends TValueType
  {
    private final static TSampler2D INSTANCE = new TSampler2D();

    /**
     * @return The sampler type singleton
     */

    public static TSampler2D get()
    {
      return TSampler2D.INSTANCE;
    }

    private final TTypeNameBuiltIn name;

    private TSampler2D()
    {
      this.name = new TTypeNameBuiltIn("sampler_2d");
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

    @Override public String toString()
    {
      return "[TSampler2D]";
    }

    @Override public
      <A, E extends Throwable, V extends TTypeVisitorType<A, E>>
      A
      ttypeVisitableAccept(
        final V v)
        throws E
    {
      return v.typeVisitSampler2D(this);
    }
  }

  /**
   * Cube texture sampler type.
   */

  @EqualityReference public static final class TSamplerCube extends
    TValueType
  {
    private final static TSamplerCube INSTANCE = new TSamplerCube();

    /**
     * @return The sampler type singleton
     */

    public static TSamplerCube get()
    {
      return TSamplerCube.INSTANCE;
    }

    private final TTypeNameBuiltIn name;

    private TSamplerCube()
    {
      this.name = new TTypeNameBuiltIn("sampler_cube");
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

    @Override public String toString()
    {
      return "[TSamplerCube]";
    }

    @Override public
      <A, E extends Throwable, V extends TTypeVisitorType<A, E>>
      A
      ttypeVisitableAccept(
        final V v)
        throws E
    {
      return v.typeVisitSamplerCube(this);
    }
  }

  /**
   * Values of a value type can be passed around as values; they can be passed
   * to functions as arguments, returned from functions, etc.
   *
   * Notably, values of function types are not values.
   */

  @EqualityReference public static abstract class TValueType extends TType
  {
    // Nothing
  }

  /**
   * 2D floating point vector type.
   */

  @EqualityReference public static final class TVector2F extends TVectorFType
  {
    private final static TVector2F INSTANCE = new TVector2F();

    /**
     * @return The vector type singleton
     */

    public static TVector2F get()
    {
      return TVector2F.INSTANCE;
    }

    private final TTypeNameBuiltIn name;

    private TVector2F()
    {
      this.name = new TTypeNameBuiltIn("vector_2f");
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

    @Override public List<TConstructor> getConstructors()
    {
      final List<TConstructor> constructors = new ArrayList<TConstructor>();
      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TFloat.get(),
        TFloat.get(), }));
      constructors.add(TConstructor
        .newConstructor(new TValueType[] { TVector2F.get() }));
      return constructors;
    }

    @Override public TTypeNameBuiltIn getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      return "[TVector2F]";
    }

    @Override public
      <A, E extends Throwable, V extends TTypeVisitorType<A, E>>
      A
      ttypeVisitableAccept(
        final V v)
        throws E
    {
      return v.typeVisitVector2F(this);
    }
  }

  /**
   * 2D integer vector type.
   */

  @EqualityReference public static final class TVector2I extends TVectorIType
  {
    private final static TVector2I INSTANCE = new TVector2I();

    /**
     * @return The vector type singleton
     */

    public static TVector2I get()
    {
      return TVector2I.INSTANCE;
    }

    private final TTypeNameBuiltIn name;

    private TVector2I()
    {
      this.name = new TTypeNameBuiltIn("vector_2i");
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

    @Override public List<TConstructor> getConstructors()
    {
      final List<TConstructor> constructors = new ArrayList<TConstructor>();
      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TInteger.get(),
        TInteger.get(), }));
      constructors.add(TConstructor
        .newConstructor(new TValueType[] { TVector2I.get() }));
      return constructors;
    }

    @Override public TTypeNameBuiltIn getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      return "[TVector2I]";
    }

    @Override public
      <A, E extends Throwable, V extends TTypeVisitorType<A, E>>
      A
      ttypeVisitableAccept(
        final V v)
        throws E
    {
      return v.typeVisitVector2I(this);
    }
  }

  /**
   * 3D floating point vector type.
   */

  @EqualityReference public static final class TVector3F extends TVectorFType
  {
    private final static TVector3F INSTANCE = new TVector3F();

    /**
     * @return The vector type singleton
     */

    public static TVector3F get()
    {
      return TVector3F.INSTANCE;
    }

    private final TTypeNameBuiltIn name;

    private TVector3F()
    {
      this.name = new TTypeNameBuiltIn("vector_3f");
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

    @Override public List<TConstructor> getConstructors()
    {
      final List<TConstructor> constructors = new ArrayList<TConstructor>();

      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TFloat.get(),
        TFloat.get(),
        TFloat.get(), }));

      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TVector2F.get(),
        TFloat.get(), }));

      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TFloat.get(),
        TVector2F.get(), }));

      constructors.add(TConstructor
        .newConstructor(new TValueType[] { TVector3F.get() }));

      return constructors;
    }

    @Override public TTypeNameBuiltIn getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      return "[TVector3F]";
    }

    @Override public
      <A, E extends Throwable, V extends TTypeVisitorType<A, E>>
      A
      ttypeVisitableAccept(
        final V v)
        throws E
    {
      return v.typeVisitVector3F(this);
    }
  }

  /**
   * 3D integer vector type.
   */

  @EqualityReference public static final class TVector3I extends TVectorIType
  {
    private final static TVector3I INSTANCE = new TVector3I();

    /**
     * @return The vector type singleton
     */

    public static TVector3I get()
    {
      return TVector3I.INSTANCE;
    }

    private final TTypeNameBuiltIn name;

    private TVector3I()
    {
      this.name = new TTypeNameBuiltIn("vector_3i");
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

    @Override public List<TConstructor> getConstructors()
    {
      final List<TConstructor> constructors = new ArrayList<TConstructor>();

      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TInteger.get(),
        TInteger.get(),
        TInteger.get(), }));

      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TVector2I.get(),
        TInteger.get(), }));

      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TInteger.get(),
        TVector2I.get(), }));

      constructors.add(TConstructor
        .newConstructor(new TValueType[] { TVector3I.get() }));

      return constructors;
    }

    @Override public TTypeNameBuiltIn getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      return "[TVector3I]";
    }

    @Override public
      <A, E extends Throwable, V extends TTypeVisitorType<A, E>>
      A
      ttypeVisitableAccept(
        final V v)
        throws E
    {
      return v.typeVisitVector3I(this);
    }
  }

  /**
   * 4D floating point vector type.
   */

  @EqualityReference public static final class TVector4F extends TVectorFType
  {
    private final static TVector4F INSTANCE = new TVector4F();

    /**
     * @return The vector type singleton
     */

    public static TVector4F get()
    {
      return TVector4F.INSTANCE;
    }

    private final TTypeNameBuiltIn name;

    private TVector4F()
    {
      this.name = new TTypeNameBuiltIn("vector_4f");
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

    @Override public List<TConstructor> getConstructors()
    {
      final List<TConstructor> constructors = new ArrayList<TConstructor>();

      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TFloat.get(),
        TFloat.get(),
        TFloat.get(),
        TFloat.get(), }));

      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TVector2F.get(),
        TFloat.get(),
        TFloat.get(), }));

      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TFloat.get(),
        TFloat.get(),
        TVector2F.get(), }));

      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TFloat.get(),
        TVector2F.get(),
        TFloat.get(), }));

      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TVector2F.get(),
        TVector2F.get(), }));

      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TVector3F.get(),
        TFloat.get(), }));

      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TFloat.get(),
        TVector3F.get(), }));

      constructors.add(TConstructor
        .newConstructor(new TValueType[] { TVector4F.get() }));

      return constructors;
    }

    @Override public TTypeNameBuiltIn getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      return "[TVector4F]";
    }

    @Override public
      <A, E extends Throwable, V extends TTypeVisitorType<A, E>>
      A
      ttypeVisitableAccept(
        final V v)
        throws E
    {
      return v.typeVisitVector4F(this);
    }
  }

  /**
   * 4D integer vector type.
   */

  @EqualityReference public static final class TVector4I extends TVectorIType
  {
    private final static TVector4I INSTANCE = new TVector4I();

    /**
     * @return The vector type singleton
     */

    public static TVector4I get()
    {
      return TVector4I.INSTANCE;
    }

    private final TTypeNameBuiltIn name;

    private TVector4I()
    {
      this.name = new TTypeNameBuiltIn("vector_4i");
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

    @Override public List<TConstructor> getConstructors()
    {
      final List<TConstructor> constructors = new ArrayList<TConstructor>();

      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TInteger.get(),
        TInteger.get(),
        TInteger.get(),
        TInteger.get(), }));

      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TVector2I.get(),
        TInteger.get(),
        TInteger.get(), }));

      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TInteger.get(),
        TInteger.get(),
        TVector2I.get(), }));

      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TInteger.get(),
        TVector2I.get(),
        TInteger.get(), }));

      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TVector2I.get(),
        TVector2I.get(), }));

      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TVector3I.get(),
        TInteger.get(), }));

      constructors.add(TConstructor.newConstructor(new TValueType[] {
        TInteger.get(),
        TVector3I.get(), }));

      constructors.add(TConstructor
        .newConstructor(new TValueType[] { TVector4I.get() }));

      return constructors;
    }

    @Override public TTypeNameBuiltIn getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      return "[TVector4I]";
    }

    @Override public
      <A, E extends Throwable, V extends TTypeVisitorType<A, E>>
      A
      ttypeVisitableAccept(
        final V v)
        throws E
    {
      return v.typeVisitVector4I(this);
    }
  }

  /**
   * The type of floating point vectors.
   */

  @EqualityReference public static abstract class TVectorFType extends
    TVectorType
  {
    @Override public final TValueType getComponentType()
    {
      return TFloat.get();
    }

    @Override public final TManifestType getSwizzleType(
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

  /**
   * The type of integer vectors.
   */

  @EqualityReference public static abstract class TVectorIType extends
    TVectorType
  {
    @Override public final TValueType getComponentType()
    {
      return TInteger.get();
    }

    @Override public final TManifestType getSwizzleType(
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
   * Values of a vector type can be used in swizzle expressions.
   */

  @EqualityReference public static abstract class TVectorType extends
    TManifestType
  {
    /**
     * @return The list of names of components
     */

    public abstract List<String> getComponentNames();

    /**
     * @return The type of the components
     */

    public abstract TValueType getComponentType();

    /**
     * @return The resulting type when swizzling with the given number of
     *         fields.
     * @param size
     *          The number of fields included in the swizzle
     */

    public abstract TManifestType getSwizzleType(
      int size);
  }

  private static final List<String> VECTOR_2_COMPONENT_NAMES;
  private static final List<String> VECTOR_3_COMPONENT_NAMES;
  private static final List<String> VECTOR_4_COMPONENT_NAMES;

  static {
    VECTOR_2_COMPONENT_NAMES =
      NullCheck.notNull(Collections.unmodifiableList(Arrays
        .asList(new String[] { "x", "y" })));
    VECTOR_3_COMPONENT_NAMES =
      NullCheck.notNull(Collections.unmodifiableList(Arrays
        .asList(new String[] { "x", "y", "z" })));
    VECTOR_4_COMPONENT_NAMES =
      NullCheck.notNull(Collections.unmodifiableList(Arrays
        .asList(new String[] { "x", "y", "z", "w" })));
  }

  /**
   * @param arguments
   *          A list of function arguments
   * @return A formatted image of the given list of function arguments
   */

  public static String formatFunctionArguments(
    final List<TFunctionArgument> arguments)
  {
    final StringBuilder m = new StringBuilder();
    m.append("(");
    for (int index = 0; index < arguments.size(); ++index) {
      m.append(arguments.get(index).getType().getShowName());
      if ((index + 1) < arguments.size()) {
        m.append(" × ");
      }
    }
    m.append(")");
    final String r = m.toString();
    assert r != null;
    return r;
  }

  /**
   * @param es
   *          A list of expressions
   * @return A formatted image of the given list of expressions
   */

  public static String formatTypeExpressionList(
    final List<TASTExpression> es)
  {
    final StringBuilder m = new StringBuilder();
    m.append("(");
    for (int index = 0; index < es.size(); ++index) {
      m.append(es.get(index).getType().getShowName());
      if ((index + 1) < es.size()) {
        m.append(" × ");
      }
    }
    m.append(")");
    final String r = m.toString();
    assert r != null;
    return r;
  }

  /**
   * @param list
   *          A list of types
   * @return A formatted image of the given list of types
   */

  public static <T extends TType> String formatTypeList(
    final List<T> list)
  {
    final StringBuilder m = new StringBuilder();
    m.append("(");
    for (int index = 0; index < list.size(); ++index) {
      m.append(list.get(index).getShowName());
      if ((index + 1) < list.size()) {
        m.append(" × ");
      }
    }
    m.append(")");
    final String r = m.toString();
    assert r != null;
    return r;
  }

  /**
   * @return A map of the built in types, by name.
   */

  public static Map<TTypeNameBuiltIn, TType> getBaseTypesByName()
  {
    final Map<TTypeNameBuiltIn, TType> m =
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
    final Map<TTypeNameBuiltIn, TType> r = Collections.unmodifiableMap(m);
    assert r != null;
    return r;
  }

  /**
   * @return The number of components in the given type.
   */

  public abstract int getComponentCount();

  /**
   * @return The list of available constructors for the type. An empty list
   *         means the type is not constructable.
   */

  public abstract List<TConstructor> getConstructors();

  /**
   * @return The name of the type
   */

  public abstract TTypeName getName();

  /**
   * @return The a humanly-readable image of the type name
   */

  public final String getShowName()
  {
    return this.getName().show();
  }

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
    <A, E extends Throwable, V extends TTypeVisitorType<A, E>>
    A
    ttypeVisitableAccept(
      final V v)
      throws E;
}
