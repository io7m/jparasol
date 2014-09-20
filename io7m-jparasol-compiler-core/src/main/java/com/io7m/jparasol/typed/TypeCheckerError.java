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

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.io7m.jfunctional.Unit;
import com.io7m.jparasol.CompilerError;
import com.io7m.jparasol.lexer.Position;
import com.io7m.jparasol.lexer.Token;
import com.io7m.jparasol.lexer.Token.TokenDiscard;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.lexer.Token.TokenIf;
import com.io7m.jparasol.lexer.Token.TokenLiteralInteger;
import com.io7m.jparasol.typed.TType.TBoolean;
import com.io7m.jparasol.typed.TType.TConstructor;
import com.io7m.jparasol.typed.TType.TFunctionArgument;
import com.io7m.jparasol.typed.TType.TManifestType;
import com.io7m.jparasol.typed.TType.TMatrixType;
import com.io7m.jparasol.typed.TType.TRecord;
import com.io7m.jparasol.typed.TType.TRecordField;
import com.io7m.jparasol.typed.TType.TValueType;
import com.io7m.jparasol.typed.TType.TVector4F;
import com.io7m.jparasol.typed.TType.TVectorType;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShader;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragment;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderFragmentInput;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderProgram;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDShaderVertex;
import com.io7m.jparasol.typed.ast.TASTExpression;
import com.io7m.jparasol.typed.ast.TASTShaderVisitorType;
import com.io7m.jparasol.typed.ast.TASTTermName.TASTTermNameLocal;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDShaderFragmentInput;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDShaderFragmentOutput;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDShaderFragmentOutputDepth;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDShaderVertexInput;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDShaderVertexOutput;
import com.io7m.jparasol.untyped.ast.resolved.UASTRShaderName;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTermName;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTypeName;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTypeName.UASTRTypeNameBuiltIn;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTypeName.UASTRTypeNameGlobal;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTypeNameVisitorType;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * The type of type errors.
 */

public final class TypeCheckerError extends CompilerError
{
  /**
   * Type error codes.
   */

  public static enum Code
  {
    /**
     * Incorrect types for function application.
     */

    TYPE_ERROR_EXPRESSION_APPLICATION_BAD_TYPES,

    /**
     * Attempting to apply a value of a non-function.
     */

    TYPE_ERROR_EXPRESSION_APPLICATION_NOT_FUNCTION_TYPE,

    /**
     * Non-boolean condition on conditional.
     */

    TYPE_ERROR_EXPRESSION_CONDITION_NOT_BOOLEAN,

    /**
     * Attempted to access a column of a non-matrix type.
     */

    TYPE_ERROR_EXPRESSION_MATRIX_COLUMN_ACCESS_NOT_COLUMN,

    /**
     * Matrix index is out of bounds.
     */

    TYPE_ERROR_EXPRESSION_MATRIX_COLUMN_ACCESS_OUT_OF_BOUNDS,

    /**
     * No available constructor for the given list of expressions.
     */

    TYPE_ERROR_EXPRESSION_NEW_NO_APPROPRIATE_CONSTRUCTORS,

    /**
     * Type has no constructor.
     */

    TYPE_ERROR_EXPRESSION_NEW_TYPE_NOT_CONSTRUCTABLE,

    /**
     * Incorrect record field type.
     */

    TYPE_ERROR_EXPRESSION_RECORD_FIELD_BAD_TYPE,

    /**
     * Unknown record field.
     */

    TYPE_ERROR_EXPRESSION_RECORD_FIELD_UNKNOWN,

    /**
     * Record fields left unassigned.
     */

    TYPE_ERROR_EXPRESSION_RECORD_FIELDS_UNASSIGNED,

    /**
     * Value is not of a record type.
     */

    TYPE_ERROR_EXPRESSION_RECORD_NOT_RECORD_TYPE,

    /**
     * Nonexistent field in record projection.
     */

    TYPE_ERROR_EXPRESSION_RECORD_PROJECTION_NO_SUCH_FIELD,

    /**
     * Target of record projection is not of a record type.
     */

    TYPE_ERROR_EXPRESSION_RECORD_PROJECTION_NOT_RECORD,

    /**
     * Target of swizzle expression is not of a vector type.
     */

    TYPE_ERROR_EXPRESSION_SWIZZLE_NOT_VECTOR,

    /**
     * Too many components in swizzle expression.
     */

    TYPE_ERROR_EXPRESSION_SWIZZLE_TOO_MANY_COMPONENTS,

    /**
     * Unknown component(s) in swizzle expression.
     */

    TYPE_ERROR_EXPRESSION_SWIZZLE_UNKNOWN_COMPONENT,

    /**
     * Returned value incompatible with declared return type.
     */

    TYPE_ERROR_FUNCTION_BODY_RETURN_INCOMPATIBLE,

    /**
     * Record field is of non-manifest type.
     */

    TYPE_ERROR_RECORD_FIELD_NOT_MANIFEST,

    /**
     * Shader value or output assignment of incorrect type.
     */

    TYPE_ERROR_SHADER_ASSIGNMENT_BAD_TYPE,

    /**
     * Shader attribute is of a type that is not allowed by GLSL.
     */

    TYPE_ERROR_SHADER_BAD_ATTRIBUTE_TYPE,

    /**
     * Shader depth output is not a scalar floating point.
     */

    TYPE_ERROR_SHADER_DEPTH_NOT_FLOAT,

    /**
     * Shader discard expression is not boolean.
     */

    TYPE_ERROR_SHADER_DISCARD_NOT_BOOLEAN,

    /**
     * Main shader output is not of an appropriate type.
     */

    TYPE_ERROR_SHADER_OUTPUT_MAIN_BAD_TYPE,

    /**
     * Incorrect type of shader specified (vertex shader where fragment shader
     * is required, etc).
     */

    TYPE_ERROR_SHADER_WRONG_SHADER_TYPE,

    /**
     * Vertex and fragment shaders are incompatible.
     */

    TYPE_ERROR_SHADERS_INCOMPATIBLE,

    /**
     * The type ascribed to a value does not match the type of the value.
     */

    TYPE_ERROR_VALUE_ASCRIPTION_MISMATCH,

    /**
     * Non-value type used where a value type is required.
     */

    TYPE_ERROR_VALUE_NON_VALUE_TYPE
  }

  private static final long serialVersionUID = 8186811920948826949L;

  /**
   * @return A type error
   */

  public static TypeCheckerError shaderAssignmentBadType(
    final TokenIdentifierLower name,
    final TValueType out_type,
    final TType type)
  {
    final StringBuilder m = new StringBuilder();
    m.append("The shader output ");
    m.append(name.getActual());
    m.append(" is of type ");
    m.append(out_type.getShowName());
    m.append(" but an expression was given of type ");
    m.append(type.getShowName());

    final String r = m.toString();
    assert r != null;
    return new TypeCheckerError(
      name.getFile(),
      name.getPosition(),
      Code.TYPE_ERROR_SHADER_ASSIGNMENT_BAD_TYPE,
      r);
  }

  /**
   * @return A type error
   */

  public static TypeCheckerError shaderDiscardNotBoolean(
    final TokenDiscard discard,
    final TType type)
  {
    final StringBuilder m = new StringBuilder();
    m.append("A discard expression must be of type ");
    m.append(TBoolean.get().getShowName());
    m.append(" but an expression was given of type ");
    m.append(type.getShowName());
    final String r = m.toString();
    assert r != null;
    return new TypeCheckerError(
      discard.getFile(),
      discard.getPosition(),
      Code.TYPE_ERROR_SHADER_DISCARD_NOT_BOOLEAN,
      r);
  }

  /**
   * @return A type error
   */

  public static TypeCheckerError shaderFragmentInputBadType(
    final UASTRDShaderFragmentInput i)
  {
    return new TypeCheckerError(
      i.getName().getFile(),
      i.getName().getPosition(),
      Code.TYPE_ERROR_SHADER_BAD_ATTRIBUTE_TYPE,
      "An input of a fragment shader cannot be of a record type");
  }

  /**
   * @return A type error
   */

  public static TypeCheckerError shaderFragmentOutputBadType(
    final UASTRDShaderFragmentOutput o)
  {
    return new TypeCheckerError(
      o.getName().getFile(),
      o.getName().getPosition(),
      Code.TYPE_ERROR_SHADER_BAD_ATTRIBUTE_TYPE,
      "An output of a fragment shader cannot be of a record type");
  }

  /**
   * @return A type error
   */

  public static TypeCheckerError shaderFragmentOutputDepthWrongType(
    final UASTRDShaderFragmentOutputDepth d)
  {
    return new TypeCheckerError(
      d.getName().getFile(),
      d.getName().getPosition(),
      Code.TYPE_ERROR_SHADER_DEPTH_NOT_FLOAT,
      "The depth output of a fragment shader must be of type float");
  }

  /**
   * @return A type error
   */

  public static TypeCheckerError shaderNotFragment(
    final UASTRShaderName name,
    final TASTDShader shader)
  {
    final StringBuilder m = new StringBuilder();
    m.append("The name ");
    m.append(name.show());
    m.append(" refers to a ");

    shader
      .shaderVisitableAccept(new TASTShaderVisitorType<Unit, UnreachableCodeException>() {
        @Override public Unit moduleVisitFragmentShader(
          final TASTDShaderFragment f)
        {
          throw new UnreachableCodeException();
        }

        @Override public Unit moduleVisitProgramShader(
          final TASTDShaderProgram p)
        {
          m.append("program shader");
          return Unit.unit();
        }

        @Override public Unit moduleVisitVertexShader(
          final TASTDShaderVertex f)
        {
          m.append("vertex shader");
          return Unit.unit();
        }
      });

    m.append(" but a fragment shader is required");
    final String r = m.toString();
    assert r != null;
    return new TypeCheckerError(name.getName().getFile(), name
      .getName()
      .getPosition(), Code.TYPE_ERROR_SHADER_WRONG_SHADER_TYPE, r);
  }

  /**
   * @return A type error
   */

  public static TypeCheckerError shaderNotVertex(
    final UASTRShaderName name,
    final TASTDShader shader)
  {
    final StringBuilder m = new StringBuilder();
    m.append("The name ");
    m.append(name.show());
    m.append(" refers to a ");

    shader
      .shaderVisitableAccept(new TASTShaderVisitorType<Unit, UnreachableCodeException>() {
        @Override public Unit moduleVisitFragmentShader(
          final TASTDShaderFragment f)
        {
          m.append("fragment shader");
          return Unit.unit();
        }

        @Override public Unit moduleVisitProgramShader(
          final TASTDShaderProgram p)
        {
          m.append("program shader");
          return Unit.unit();
        }

        @Override public Unit moduleVisitVertexShader(
          final TASTDShaderVertex f)
        {
          throw new UnreachableCodeException();
        }
      });

    m.append(" but a vertex shader is required");
    final String r = m.toString();
    assert r != null;
    return new TypeCheckerError(name.getName().getFile(), name
      .getName()
      .getPosition(), Code.TYPE_ERROR_SHADER_WRONG_SHADER_TYPE, r);
  }

  /**
   * @return A type error
   */

  public static TypeCheckerError shadersNotCompatible(
    final TokenIdentifierLower program,
    final TASTDShaderFragment fs,
    final Set<String> assigned,
    final Map<String, TValueType> wrong_types)
  {
    final StringBuilder m = new StringBuilder();
    m.append("The shaders for program ");
    m.append(program.getActual());
    m.append(" are incompatible.\n");
    m.append("Problems with each fragment shader input are indicated:\n");

    for (final TASTDShaderFragmentInput f : fs.getInputs()) {
      final TASTTermNameLocal fi_name = f.getName();
      if (assigned.contains(fi_name.getCurrent()) == false) {
        m.append("  ");
        m.append(fi_name.getCurrent());
        m.append(" : ");
        m.append(f.getType().getShowName());
        m.append(" has no matching vertex shader output\n");
      } else if (wrong_types.containsKey(fi_name.getCurrent())) {
        final TValueType type = wrong_types.get(fi_name.getCurrent());
        assert type != null;
        m.append("  ");
        m.append(fi_name.getCurrent());
        m.append(" : ");
        m.append(f.getType().getShowName());
        m.append(" is incompatible with the vertex shader output of type ");
        m.append(type.getShowName());
        m.append("\n");
      }
    }

    final String r = m.toString();
    assert r != null;
    return new TypeCheckerError(
      program.getFile(),
      program.getPosition(),
      Code.TYPE_ERROR_SHADERS_INCOMPATIBLE,
      r);
  }

  /**
   * @return A type error
   */

  public static TypeCheckerError shaderVertexInputBadType(
    final UASTRDShaderVertexInput i)
  {
    return new TypeCheckerError(
      i.getName().getFile(),
      i.getName().getPosition(),
      Code.TYPE_ERROR_SHADER_BAD_ATTRIBUTE_TYPE,
      "An input of a vertex shader cannot be of a record type");
  }

  /**
   * @return A type error
   */

  public static TypeCheckerError shaderVertexMainOutputBadType(
    final UASTRDShaderVertexOutput o,
    final TType t)
  {
    final StringBuilder m = new StringBuilder();
    m.append("The main output of a vertex shader must be of type ");
    m.append(TVector4F.get().getShowName());
    m.append(" but the given output is of type ");
    m.append(t.getShowName());

    final String r = m.toString();
    assert r != null;
    return new TypeCheckerError(o.getName().getFile(), o
      .getName()
      .getPosition(), Code.TYPE_ERROR_SHADER_OUTPUT_MAIN_BAD_TYPE, r);
  }

  /**
   * @return A type error
   */

  public static TypeCheckerError shaderVertexOutputBadType(
    final UASTRDShaderVertexOutput o)
  {
    return new TypeCheckerError(
      o.getName().getFile(),
      o.getName().getPosition(),
      Code.TYPE_ERROR_SHADER_BAD_ATTRIBUTE_TYPE,
      "An output of a vertex shader cannot be of a record type");
  }

  /**
   * @return A type error
   */

  public static TypeCheckerError termExpressionApplicationBadTypes(
    final UASTRTermName name,
    final List<TFunctionArgument> expected,
    final List<TASTExpression> got)
  {
    final StringBuilder m = new StringBuilder();
    m.append("Incorrect argument types for application of function ");
    m.append(name.getName().getActual());
    m.append("\n");
    m.append("Expected: ");
    m.append(TType.formatFunctionArguments(expected));
    m.append("\n");
    m.append("Got:      ");
    m.append(TType.formatTypeExpressionList(got));

    final String r = m.toString();
    assert r != null;
    return new TypeCheckerError(
      name.getFile(),
      name.getPosition(),
      Code.TYPE_ERROR_EXPRESSION_APPLICATION_BAD_TYPES,
      r);
  }

  /**
   * @return A type error
   */

  public static TypeCheckerError termExpressionApplicationNotFunctionType(
    final UASTRTermName name,
    final TType t)
  {
    final StringBuilder m = new StringBuilder();
    m.append("Cannot apply the term ");
    m.append(name.getName().getActual());
    m.append(" to the given arguments; ");
    m.append(name.getName().getActual());
    m.append(" is of the non-function type ");
    m.append(t.getShowName());

    final String r = m.toString();
    assert r != null;
    return new TypeCheckerError(
      name.getFile(),
      name.getPosition(),
      Code.TYPE_ERROR_EXPRESSION_APPLICATION_NOT_FUNCTION_TYPE,
      r);
  }

  /**
   * @return A type error
   */

  public static TypeCheckerError termExpressionMatrixColumnAccessNotMatrix(
    final TASTExpression body,
    final Token t)
  {
    final StringBuilder m = new StringBuilder();
    m
      .append("Only expressions of matrix types can be accessed by column. The given expression is of type ");
    m.append(body.getType().getShowName());

    final String r = m.toString();
    assert r != null;
    return new TypeCheckerError(
      t.getFile(),
      t.getPosition(),
      Code.TYPE_ERROR_EXPRESSION_MATRIX_COLUMN_ACCESS_NOT_COLUMN,
      r);
  }

  /**
   * @return A type error
   */

  public static TypeCheckerError termExpressionMatrixColumnAccessOutOfBounds(
    final TokenLiteralInteger column,
    final TMatrixType mt)
  {
    final StringBuilder m = new StringBuilder();
    m.append("The given matrix column index ");
    m.append(column.getValue());
    m.append(" is out of bounds for the type ");
    m.append(mt.getShowName());
    m.append("\n");
    m.append("The index must be in the range [0, ");
    m.append(mt.getColumns() - 1);
    m.append("]\n");

    final String r = m.toString();
    assert r != null;
    return new TypeCheckerError(
      column.getFile(),
      column.getPosition(),
      Code.TYPE_ERROR_EXPRESSION_MATRIX_COLUMN_ACCESS_OUT_OF_BOUNDS,
      r);
  }

  /**
   * @return A type error
   */

  public static TypeCheckerError termExpressionNewNoAppropriateConstructors(
    final UASTRTypeName name,
    final List<TASTExpression> arguments,
    final List<TConstructor> constructors)
  {
    final StringBuilder m = new StringBuilder();
    m.append("No appropriate constructor for type ");
    m.append(name.getName().getActual());
    m.append(" for arguments ");
    m.append(TType.formatTypeExpressionList(arguments));
    m.append("\n");
    m.append("Available constructors are:\n");

    for (final TConstructor c : constructors) {
      m.append("  ");
      m.append(TType.formatTypeList(c.getParameters()));
      m.append(" → ");
      m.append(name.getName().getActual());
      m.append("\n");
    }

    final String r = m.toString();
    assert r != null;
    return new TypeCheckerError(
      name.getName().getFile(),
      name.getName().getPosition(),
      Code.TYPE_ERROR_EXPRESSION_NEW_NO_APPROPRIATE_CONSTRUCTORS,
      r);
  }

  /**
   * @return A type error
   */

  public static TypeCheckerError termExpressionNewNotConstructable(
    final UASTRTypeName name)
  {
    final TokenIdentifierLower n_name = name.getName();
    final StringBuilder m = new StringBuilder();
    m.append("The type ");
    m.append(n_name);
    m.append(" is not constructable");

    final String r = m.toString();
    assert r != null;
    return new TypeCheckerError(
      n_name.getFile(),
      n_name.getPosition(),
      Code.TYPE_ERROR_EXPRESSION_NEW_TYPE_NOT_CONSTRUCTABLE,
      r);
  }

  /**
   * @return A type error
   */

  public static TypeCheckerError termExpressionRecordBadFieldType(
    final TokenIdentifierLower field_name,
    final TManifestType expected_type,
    final TType got_type)
  {
    final StringBuilder m = new StringBuilder();
    m.append("The type of field ");
    m.append(field_name.getActual());
    m.append(" is ");
    m.append(expected_type.getShowName());
    m.append(" but an expression was given of type ");
    m.append(got_type.getShowName());

    final String r = m.toString();
    assert r != null;
    return new TypeCheckerError(
      field_name.getFile(),
      field_name.getPosition(),
      Code.TYPE_ERROR_EXPRESSION_RECORD_FIELD_BAD_TYPE,
      r);
  }

  /**
   * @return A type error
   */

  public static TypeCheckerError termExpressionRecordFieldsUnassigned(
    final UASTRTypeName name,
    final List<TRecordField> unassigned)
  {
    final TokenIdentifierLower n_name = name.getName();
    final StringBuilder m = new StringBuilder();
    m
      .append("The record expression leaves the following fields unassigned:\n");

    for (final TRecordField u : unassigned) {
      m.append("  ");
      m.append(u.getName());
      m.append(" : ");
      m.append(u.getType().getShowName());
      m.append("\n");
    }

    final String r = m.toString();
    assert r != null;
    return new TypeCheckerError(
      n_name.getFile(),
      n_name.getPosition(),
      Code.TYPE_ERROR_EXPRESSION_RECORD_FIELDS_UNASSIGNED,
      r);
  }

  /**
   * @return A type error
   */

  public static TypeCheckerError termExpressionRecordNotRecordType(
    final UASTRTypeName name)
  {
    final TokenIdentifierLower n_name = name.getName();
    final StringBuilder m = new StringBuilder();
    m.append("The type ");
    m.append(n_name.getActual());
    m
      .append(" is not a record type and therefore values cannot be constructed with record expressions");

    final String r = m.toString();
    assert r != null;
    return new TypeCheckerError(
      n_name.getFile(),
      n_name.getPosition(),
      Code.TYPE_ERROR_EXPRESSION_RECORD_NOT_RECORD_TYPE,
      r);
  }

  /**
   * @return A type error
   */

  public static TypeCheckerError termExpressionRecordProjectionNoSuchField(
    final TRecord tr,
    final TokenIdentifierLower field)
  {
    final StringBuilder m = new StringBuilder();
    m.append("The type ");
    m.append(tr.getShowName());
    m.append(" does not have a field named ");
    m.append(field.getActual());
    m.append("\n");
    m.append("Available fields are:\n");

    for (final TRecordField f : tr.getFields()) {
      m.append("  ");
      m.append(f.getName());
      m.append(" : ");
      m.append(f.getType().getShowName());
      m.append("\n");
    }

    final String r = m.toString();
    assert r != null;
    return new TypeCheckerError(
      field.getFile(),
      field.getPosition(),
      Code.TYPE_ERROR_EXPRESSION_RECORD_PROJECTION_NO_SUCH_FIELD,
      r);
  }

  /**
   * @return A type error
   */

  public static TypeCheckerError termExpressionRecordProjectionNotRecord(
    final TASTExpression body,
    final TokenIdentifierLower field)
  {
    final StringBuilder m = new StringBuilder();
    m.append("The type of the given expression is ");
    m.append(body.getType().getShowName());
    m
      .append(", which is not a record type and therefore the expression cannot be the body of a record projection");

    final String r = m.toString();
    assert r != null;
    return new TypeCheckerError(
      field.getFile(),
      field.getPosition(),
      Code.TYPE_ERROR_EXPRESSION_RECORD_PROJECTION_NOT_RECORD,
      r);
  }

  /**
   * @return A type error
   */

  public static TypeCheckerError termExpressionRecordUnknownField(
    final TokenIdentifierLower field_name,
    final TRecord record)
  {
    final StringBuilder m = new StringBuilder();
    m.append("The type ");
    m.append(record.getShowName());
    m.append(" does not contain a field named ");
    m.append(field_name.getActual());
    m.append("\n");
    m.append("Available fields are:\n");

    for (final TRecordField f : record.getFields()) {
      m.append("  ");
      m.append(f.getName());
      m.append(" : ");
      m.append(f.getType().getShowName());
      m.append("\n");
    }

    final String r = m.toString();
    assert r != null;
    return new TypeCheckerError(
      field_name.getFile(),
      field_name.getPosition(),
      Code.TYPE_ERROR_EXPRESSION_RECORD_FIELD_UNKNOWN,
      r);
  }

  /**
   * @return A type error
   */

  public static TypeCheckerError termExpressionSwizzleNotVector(
    final TASTExpression body,
    final TokenIdentifierLower first_field)
  {
    final StringBuilder m = new StringBuilder();
    m
      .append("Only expressions of vector types can be swizzled. The given expression is of type ");
    m.append(body.getType().getShowName());

    final String r = m.toString();
    assert r != null;
    return new TypeCheckerError(
      first_field.getFile(),
      first_field.getPosition(),
      Code.TYPE_ERROR_EXPRESSION_SWIZZLE_NOT_VECTOR,
      r);
  }

  /**
   * @return A type error
   */

  public static TypeCheckerError termExpressionSwizzleTooManyFields(
    final TokenIdentifierLower first,
    final int size)
  {
    final StringBuilder m = new StringBuilder();
    m.append("Swizzle expressions can contain at most four components (");
    m.append(size);
    m.append(" were given)");

    final String r = m.toString();
    assert r != null;
    return new TypeCheckerError(
      first.getFile(),
      first.getPosition(),
      Code.TYPE_ERROR_EXPRESSION_SWIZZLE_TOO_MANY_COMPONENTS,
      r);
  }

  /**
   * @return A type error
   */

  public static TypeCheckerError termExpressionSwizzleUnknownField(
    final TVectorType tv,
    final TokenIdentifierLower f)
  {
    final StringBuilder m = new StringBuilder();
    m.append("The type ");
    m.append(tv.getShowName());
    m.append(" does not have a component named ");
    m.append(f.getActual());
    m.append("\n");
    m.append("Available components are: ");

    for (final String name : tv.getComponentNames()) {
      m.append(name);
      m.append(" ");
    }

    final String r = m.toString();
    assert r != null;
    return new TypeCheckerError(
      f.getFile(),
      f.getPosition(),
      Code.TYPE_ERROR_EXPRESSION_SWIZZLE_UNKNOWN_COMPONENT,
      r);
  }

  /**
   * @return A type error
   */

  public static TypeCheckerError termFunctionBodyReturnMismatch(
    final TokenIdentifierLower name,
    final TType expected,
    final TType got)
  {
    final StringBuilder m = new StringBuilder();
    m.append("The body of function ");
    m.append(name.getActual());
    m.append(" is expected to be of type ");
    m.append(expected.getShowName());
    m.append(" but an expression was given of type ");
    m.append(got.getShowName());

    final String r = m.toString();
    assert r != null;
    return new TypeCheckerError(
      name.getFile(),
      name.getPosition(),
      Code.TYPE_ERROR_FUNCTION_BODY_RETURN_INCOMPATIBLE,
      r);
  }

  /**
   * @return A type error
   */

  public static TypeCheckerError termValueExpressionAscriptionMismatch(
    final TokenIdentifierLower name,
    final TType expected,
    final TType got)
  {
    final StringBuilder m = new StringBuilder();
    m.append("The ascription on value ");
    m.append(name.getActual());
    m.append(" requires that the type must be ");
    m.append(expected.getShowName());
    m.append(" but an expression was given of type ");
    m.append(got.getShowName());

    final String r = m.toString();
    assert r != null;
    return new TypeCheckerError(
      name.getFile(),
      name.getPosition(),
      Code.TYPE_ERROR_VALUE_ASCRIPTION_MISMATCH,
      r);
  }

  /**
   * @return A type error
   */

  public static TypeCheckerError termValueNotValueType(
    final TokenIdentifierLower name,
    final TType type)
  {
    final StringBuilder m = new StringBuilder();
    m.append("The body of value ");
    m.append(name.getActual());
    m.append(" is of a non-value type ");
    m.append(type.getShowName());

    final String r = m.toString();
    assert r != null;
    return new TypeCheckerError(
      name.getFile(),
      name.getPosition(),
      Code.TYPE_ERROR_VALUE_NON_VALUE_TYPE,
      r);
  }

  /**
   * @return A type error
   */

  public static TypeCheckerError typeConditionNotBoolean(
    final TokenIf token,
    final TASTExpression condition)
  {
    final StringBuilder m = new StringBuilder();
    m
      .append("The condition expression of a condition must be of type boolean, but an expression was given here of type ");
    m.append(condition.getType().getShowName());

    final String r = m.toString();
    assert r != null;
    return new TypeCheckerError(
      token.getFile(),
      token.getPosition(),
      Code.TYPE_ERROR_EXPRESSION_CONDITION_NOT_BOOLEAN,
      r);
  }

  /**
   * @return A type error
   */

  public static TypeCheckerError typeRecordFieldNotManifest(
    final UASTRTypeName type_name,
    final TType ty)
  {
    final StringBuilder m = new StringBuilder();
    m.append("The (non-manifest) type ");
    m.append(ty.getShowName());
    m.append(" cannot be used as the type of a record field");

    return type_name
      .typeNameVisitableAccept(new UASTRTypeNameVisitorType<TypeCheckerError, UnreachableCodeException>() {
        @SuppressWarnings("synthetic-access") @Override public
          TypeCheckerError
          typeNameVisitBuiltIn(
            final UASTRTypeNameBuiltIn t)
        {
          final TokenIdentifierLower name = t.getName();
          final String r = m.toString();
          assert r != null;
          return new TypeCheckerError(
            name.getFile(),
            name.getPosition(),
            Code.TYPE_ERROR_RECORD_FIELD_NOT_MANIFEST,
            r);
        }

        @SuppressWarnings("synthetic-access") @Override public
          TypeCheckerError
          typeNameVisitGlobal(
            final UASTRTypeNameGlobal t)
        {
          final TokenIdentifierLower name = t.getName();
          final String r = m.toString();
          assert r != null;
          return new TypeCheckerError(
            name.getFile(),
            name.getPosition(),
            Code.TYPE_ERROR_RECORD_FIELD_NOT_MANIFEST,
            r);
        }
      });
  }

  private final Code code;

  private TypeCheckerError(
    final File file,
    final Position position,
    final Code in_code,
    final String message)
  {
    super(message, file, position);
    this.code = in_code;
  }

  @Override public String getCategory()
  {
    return "type-checker";
  }

  /**
   * @return The error code.
   */

  public Code getCode()
  {
    return this.code;
  }
}
