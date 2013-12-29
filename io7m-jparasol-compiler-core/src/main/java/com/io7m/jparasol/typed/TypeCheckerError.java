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

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jaux.functional.Unit;
import com.io7m.jparasol.CompilerError;
import com.io7m.jparasol.lexer.Position;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.lexer.Token.TokenIf;
import com.io7m.jparasol.typed.TType.TConstructor;
import com.io7m.jparasol.typed.TType.TFunctionArgument;
import com.io7m.jparasol.typed.TType.TManifestType;
import com.io7m.jparasol.typed.TType.TRecord;
import com.io7m.jparasol.typed.TType.TRecordField;
import com.io7m.jparasol.typed.TType.TValueType;
import com.io7m.jparasol.typed.TType.TVectorType;
import com.io7m.jparasol.typed.ast.TASTExpression;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDShader;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDShaderFragment;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDShaderFragmentInput;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDShaderProgram;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDShaderVertex;
import com.io7m.jparasol.typed.ast.TASTShaderVisitor;
import com.io7m.jparasol.typed.ast.TASTTermName.TASTTermNameLocal;
import com.io7m.jparasol.untyped.ast.resolved.UASTRShaderName;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTermName;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTypeName;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTypeName.UASTRTypeNameBuiltIn;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTypeName.UASTRTypeNameGlobal;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTypeNameVisitor;

public final class TypeCheckerError extends CompilerError
{
  public static enum Code
  {
    TYPE_ERROR_EXPRESSION_APPLICATION_BAD_TYPES,
    TYPE_ERROR_EXPRESSION_APPLICATION_NOT_FUNCTION_TYPE,
    TYPE_ERROR_EXPRESSION_CONDITION_NOT_BOOLEAN,
    TYPE_ERROR_EXPRESSION_NEW_NO_APPROPRIATE_CONSTRUCTORS,
    TYPE_ERROR_EXPRESSION_NEW_TYPE_NOT_CONSTRUCTABLE,
    TYPE_ERROR_EXPRESSION_RECORD_FIELD_BAD_TYPE,
    TYPE_ERROR_EXPRESSION_RECORD_FIELD_UNKNOWN,
    TYPE_ERROR_EXPRESSION_RECORD_FIELDS_UNASSIGNED,
    TYPE_ERROR_EXPRESSION_RECORD_NOT_RECORD_TYPE,
    TYPE_ERROR_EXPRESSION_RECORD_PROJECTION_NO_SUCH_FIELD,
    TYPE_ERROR_EXPRESSION_RECORD_PROJECTION_NOT_RECORD,
    TYPE_ERROR_EXPRESSION_SWIZZLE_NOT_VECTOR,
    TYPE_ERROR_EXPRESSION_SWIZZLE_TOO_MANY_COMPONENTS,
    TYPE_ERROR_EXPRESSION_SWIZZLE_UNKNOWN_COMPONENT,
    TYPE_ERROR_FUNCTION_BODY_RETURN_INCOMPATIBLE,
    TYPE_ERROR_RECORD_FIELD_NOT_MANIFEST,
    TYPE_ERROR_SHADER_ASSIGNMENT_BAD_TYPE,
    TYPE_ERROR_SHADER_WRONG_SHADER_TYPE,
    TYPE_ERROR_SHADERS_INCOMPATIBLE,
    TYPE_ERROR_VALUE_ASCRIPTION_MISMATCH,
    TYPE_ERROR_VALUE_NON_VALUE_TYPE
  }

  private static final long serialVersionUID = 8186811920948826949L;

  public static @Nonnull TypeCheckerError shaderAssignmentBadType(
    final @Nonnull TokenIdentifierLower name,
    final @Nonnull TValueType out_type,
    final @Nonnull TType type)
    throws ConstraintError
  {
    final StringBuilder m = new StringBuilder();
    m.append("The shader output ");
    m.append(name.getActual());
    m.append(" is of type ");
    m.append(out_type.getName());
    m.append(" but an expression was given of type ");
    m.append(type.getName());

    return new TypeCheckerError(
      name.getFile(),
      name.getPosition(),
      Code.TYPE_ERROR_SHADER_ASSIGNMENT_BAD_TYPE,
      m.toString());
  }

  public static @Nonnull TypeCheckerError shaderNotFragment(
    final @Nonnull UASTRShaderName name,
    final @Nonnull TASTDShader shader)
    throws ConstraintError
  {
    final StringBuilder m = new StringBuilder();
    m.append("The name ");
    m.append(name.show());
    m.append(" refers to a ");

    shader
      .shaderVisitableAccept(new TASTShaderVisitor<Unit, ConstraintError>() {
        @Override public Unit moduleVisitFragmentShader(
          final TASTDShaderFragment f)
          throws ConstraintError,
            ConstraintError
        {
          throw new UnreachableCodeException();
        }

        @Override public Unit moduleVisitProgramShader(
          final TASTDShaderProgram p)
          throws ConstraintError,
            ConstraintError
        {
          m.append("program shader");
          return Unit.unit();
        }

        @Override public Unit moduleVisitVertexShader(
          final TASTDShaderVertex f)
          throws ConstraintError,
            ConstraintError
        {
          m.append("vertex shader");
          return Unit.unit();
        }
      });

    m.append(" but a fragment shader is required");
    return new TypeCheckerError(name.getName().getFile(), name
      .getName()
      .getPosition(), Code.TYPE_ERROR_SHADER_WRONG_SHADER_TYPE, m.toString());
  }

  public static @Nonnull TypeCheckerError shaderNotVertex(
    final @Nonnull UASTRShaderName name,
    final @Nonnull TASTDShader shader)
    throws ConstraintError
  {
    final StringBuilder m = new StringBuilder();
    m.append("The name ");
    m.append(name.show());
    m.append(" refers to a ");

    shader
      .shaderVisitableAccept(new TASTShaderVisitor<Unit, ConstraintError>() {
        @Override public Unit moduleVisitFragmentShader(
          final TASTDShaderFragment f)
          throws ConstraintError,
            ConstraintError
        {
          m.append("fragment shader");
          return Unit.unit();
        }

        @Override public Unit moduleVisitProgramShader(
          final TASTDShaderProgram p)
          throws ConstraintError,
            ConstraintError
        {
          m.append("program shader");
          return Unit.unit();
        }

        @Override public Unit moduleVisitVertexShader(
          final TASTDShaderVertex f)
          throws ConstraintError,
            ConstraintError
        {
          throw new UnreachableCodeException();
        }
      });

    m.append(" but a vertex shader is required");
    return new TypeCheckerError(name.getName().getFile(), name
      .getName()
      .getPosition(), Code.TYPE_ERROR_SHADER_WRONG_SHADER_TYPE, m.toString());
  }

  public static @Nonnull TypeCheckerError shadersNotCompatible(
    final @Nonnull TokenIdentifierLower program,
    final @Nonnull TASTDShaderVertex vs,
    final @Nonnull TASTDShaderFragment fs,
    final @Nonnull Set<String> assigned,
    final @Nonnull Map<String, TValueType> wrong_types)
    throws ConstraintError
  {
    final StringBuilder m = new StringBuilder();
    m.append("The shaders for program ");
    m.append(program.getActual());
    m.append("are incompatible.\n");
    m.append("Problems with each fragment shader input are indicated:\n");

    for (final TASTDShaderFragmentInput f : fs.getInputs()) {
      final TASTTermNameLocal fi_name = f.getName();
      if (assigned.contains(fi_name.getCurrent()) == false) {
        m.append("  ");
        m.append(fi_name.getCurrent());
        m.append(" : ");
        m.append(f.getType().getName());
        m.append(" has no matching vertex shader output\n");
      } else {
        assert wrong_types.containsKey(fi_name.getCurrent());
        final TValueType type = wrong_types.get(fi_name.getCurrent());
        m.append("  ");
        m.append(fi_name.getCurrent());
        m.append(" : ");
        m.append(f.getType().getName());
        m.append(" is incompatible with the vertex shader output of type ");
        m.append(type.getName());
        m.append("\n");
      }
    }

    return new TypeCheckerError(
      program.getFile(),
      program.getPosition(),
      Code.TYPE_ERROR_SHADERS_INCOMPATIBLE,
      m.toString());
  }

  public static @Nonnull TypeCheckerError termExpressionApplicationBadTypes(
    final @Nonnull UASTRTermName name,
    final @Nonnull List<TFunctionArgument> expected,
    final @Nonnull List<TASTExpression> got)
    throws ConstraintError
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
    return new TypeCheckerError(
      name.getFile(),
      name.getPosition(),
      Code.TYPE_ERROR_EXPRESSION_APPLICATION_BAD_TYPES,
      m.toString());
  }

  public static @Nonnull
    TypeCheckerError
    termExpressionApplicationNotFunctionType(
      final @Nonnull UASTRTermName name,
      final @Nonnull TType t)
      throws ConstraintError
  {
    final StringBuilder m = new StringBuilder();
    m.append("Cannot apply the term ");
    m.append(name.getName().getActual());
    m.append(" to the given arguments; ");
    m.append(name.getName().getActual());
    m.append(" is of the non-function type ");
    m.append(t.getName());
    return new TypeCheckerError(
      name.getFile(),
      name.getPosition(),
      Code.TYPE_ERROR_EXPRESSION_APPLICATION_NOT_FUNCTION_TYPE,
      m.toString());
  }

  public static @Nonnull
    TypeCheckerError
    termExpressionNewNoAppropriateConstructors(
      final @Nonnull UASTRTypeName name,
      final @Nonnull List<TASTExpression> arguments,
      final @Nonnull List<TConstructor> constructors)
      throws ConstraintError
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

    return new TypeCheckerError(
      name.getName().getFile(),
      name.getName().getPosition(),
      Code.TYPE_ERROR_EXPRESSION_NEW_NO_APPROPRIATE_CONSTRUCTORS,
      m.toString());
  }

  public static @Nonnull TypeCheckerError termExpressionNewNotConstructable(
    final @Nonnull UASTRTypeName name)
    throws ConstraintError
  {
    final TokenIdentifierLower n_name = name.getName();
    final StringBuilder m = new StringBuilder();
    m.append("The type ");
    m.append(n_name);
    m.append(" is not constructable");

    return new TypeCheckerError(
      n_name.getFile(),
      n_name.getPosition(),
      Code.TYPE_ERROR_EXPRESSION_NEW_TYPE_NOT_CONSTRUCTABLE,
      m.toString());
  }

  public static @Nonnull TypeCheckerError termExpressionRecordBadFieldType(
    final @Nonnull TokenIdentifierLower field_name,
    final @Nonnull TManifestType expected_type,
    final @Nonnull TType got_type)
    throws ConstraintError
  {
    final StringBuilder m = new StringBuilder();
    m.append("The type of field ");
    m.append(field_name.getActual());
    m.append(" is ");
    m.append(expected_type.getName());
    m.append(" but an expression was given of type ");
    m.append(got_type.getName());
    return new TypeCheckerError(
      field_name.getFile(),
      field_name.getPosition(),
      Code.TYPE_ERROR_EXPRESSION_RECORD_FIELD_BAD_TYPE,
      m.toString());
  }

  public static @Nonnull
    TypeCheckerError
    termExpressionRecordFieldsUnassigned(
      final @Nonnull UASTRTypeName name,
      final @Nonnull List<TRecordField> unassigned)
      throws ConstraintError
  {
    final TokenIdentifierLower n_name = name.getName();
    final StringBuilder m = new StringBuilder();
    m
      .append("The record expression leaves the following fields unassigned:\n");

    for (final TRecordField u : unassigned) {
      m.append("  ");
      m.append(u.getName());
      m.append(" : ");
      m.append(u.getType().getName());
      m.append("\n");
    }

    return new TypeCheckerError(
      n_name.getFile(),
      n_name.getPosition(),
      Code.TYPE_ERROR_EXPRESSION_RECORD_FIELDS_UNASSIGNED,
      m.toString());
  }

  public static @Nonnull TypeCheckerError termExpressionRecordNotRecordType(
    final @Nonnull UASTRTypeName name)
    throws ConstraintError
  {
    final TokenIdentifierLower n_name = name.getName();
    final StringBuilder m = new StringBuilder();
    m.append("The type ");
    m.append(n_name.getActual());
    m
      .append(" is not a record type and therefore values cannot be constructed with record expressions");

    return new TypeCheckerError(
      n_name.getFile(),
      n_name.getPosition(),
      Code.TYPE_ERROR_EXPRESSION_RECORD_NOT_RECORD_TYPE,
      m.toString());
  }

  public static @Nonnull
    TypeCheckerError
    termExpressionRecordProjectionNoSuchField(
      final @Nonnull TRecord tr,
      final @Nonnull TokenIdentifierLower field)
      throws ConstraintError
  {
    final StringBuilder m = new StringBuilder();
    m.append("The type ");
    m.append(tr.getName());
    m.append(" does not have a field named ");
    m.append(field.getActual());
    m.append("\n");
    m.append("Available fields are:\n");

    for (final TRecordField f : tr.getFields()) {
      m.append("  ");
      m.append(f.getName());
      m.append(" : ");
      m.append(f.getType().getName());
      m.append("\n");
    }

    return new TypeCheckerError(
      field.getFile(),
      field.getPosition(),
      Code.TYPE_ERROR_EXPRESSION_RECORD_PROJECTION_NO_SUCH_FIELD,
      m.toString());
  }

  public static @Nonnull
    TypeCheckerError
    termExpressionRecordProjectionNotRecord(
      final @Nonnull TASTExpression body,
      final @Nonnull TokenIdentifierLower field)
      throws ConstraintError
  {
    final StringBuilder m = new StringBuilder();
    m.append("The type of the given expression is ");
    m.append(body.getType().getName());
    m
      .append(", which is not a record type and therefore the expression cannot be the body of a record projection");
    return new TypeCheckerError(
      field.getFile(),
      field.getPosition(),
      Code.TYPE_ERROR_EXPRESSION_RECORD_PROJECTION_NOT_RECORD,
      m.toString());
  }

  public static @Nonnull TypeCheckerError termExpressionRecordUnknownField(
    final @Nonnull TokenIdentifierLower field_name,
    final @Nonnull TRecord record)
    throws ConstraintError
  {
    final StringBuilder m = new StringBuilder();
    m.append("The type ");
    m.append(record.getName());
    m.append(" does not contain a field named ");
    m.append(field_name.getActual());
    m.append("\n");
    m.append("Available fields are:\n");

    for (final TRecordField f : record.getFields()) {
      m.append("  ");
      m.append(f.getName());
      m.append(" : ");
      m.append(f.getType().getName());
      m.append("\n");
    }

    return new TypeCheckerError(
      field_name.getFile(),
      field_name.getPosition(),
      Code.TYPE_ERROR_EXPRESSION_RECORD_FIELD_UNKNOWN,
      m.toString());
  }

  public static @Nonnull TypeCheckerError termExpressionSwizzleNotVector(
    final @Nonnull TASTExpression body,
    final @Nonnull TokenIdentifierLower first_field)
    throws ConstraintError
  {
    final StringBuilder m = new StringBuilder();
    m
      .append("Only expressions of vector types can be swizzled. The given expression is of type ");
    m.append(body.getType().getName());

    return new TypeCheckerError(
      first_field.getFile(),
      first_field.getPosition(),
      Code.TYPE_ERROR_EXPRESSION_SWIZZLE_NOT_VECTOR,
      m.toString());
  }

  public static @Nonnull TypeCheckerError termExpressionSwizzleTooManyFields(
    final @Nonnull TokenIdentifierLower first,
    final int size)
    throws ConstraintError
  {
    final StringBuilder m = new StringBuilder();
    m.append("Swizzle expressions can contain at most four components (");
    m.append(size);
    m.append(" were given)");

    return new TypeCheckerError(
      first.getFile(),
      first.getPosition(),
      Code.TYPE_ERROR_EXPRESSION_SWIZZLE_TOO_MANY_COMPONENTS,
      m.toString());
  }

  public static @Nonnull TypeCheckerError termExpressionSwizzleUnknownField(
    final @Nonnull TVectorType tv,
    final @Nonnull TokenIdentifierLower f)
    throws ConstraintError
  {
    final StringBuilder m = new StringBuilder();
    m.append("The type ");
    m.append(tv.getName());
    m.append(" does not have a component named ");
    m.append(f.getActual());
    m.append("\n");
    m.append("Available components are: ");

    for (final String name : tv.getComponentNames()) {
      m.append(name);
      m.append(" ");
    }

    return new TypeCheckerError(
      f.getFile(),
      f.getPosition(),
      Code.TYPE_ERROR_EXPRESSION_SWIZZLE_UNKNOWN_COMPONENT,
      m.toString());
  }

  public static @Nonnull TypeCheckerError termFunctionBodyReturnMismatch(
    final @Nonnull TokenIdentifierLower name,
    final @Nonnull TType expected,
    final @Nonnull TType got)
    throws ConstraintError
  {
    final StringBuilder m = new StringBuilder();
    m.append("The body of function ");
    m.append(name.getActual());
    m.append(" is expected to be of type ");
    m.append(expected.getName());
    m.append(" but an expression was given of type ");
    m.append(got.getName());
    return new TypeCheckerError(
      name.getFile(),
      name.getPosition(),
      Code.TYPE_ERROR_FUNCTION_BODY_RETURN_INCOMPATIBLE,
      m.toString());
  }

  public static @Nonnull
    TypeCheckerError
    termValueExpressionAscriptionMismatch(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull TType expected,
      final @Nonnull TType got)
      throws ConstraintError
  {
    final StringBuilder m = new StringBuilder();
    m.append("The ascription on value ");
    m.append(name.getActual());
    m.append(" requires that the type must be ");
    m.append(expected.getName());
    m.append(" but an expression was given of type ");
    m.append(got.getName());
    return new TypeCheckerError(
      name.getFile(),
      name.getPosition(),
      Code.TYPE_ERROR_VALUE_ASCRIPTION_MISMATCH,
      m.toString());
  }

  public static @Nonnull TypeCheckerError termValueNotValueType(
    final @Nonnull TokenIdentifierLower name,
    final @Nonnull TType type)
    throws ConstraintError
  {
    final StringBuilder m = new StringBuilder();
    m.append("The body of value ");
    m.append(name.getActual());
    m.append(" is of a non-value type ");
    m.append(type.getName());
    return new TypeCheckerError(
      name.getFile(),
      name.getPosition(),
      Code.TYPE_ERROR_VALUE_NON_VALUE_TYPE,
      m.toString());
  }

  public static @Nonnull TypeCheckerError typeConditionNotBoolean(
    final @Nonnull TokenIf token,
    final @Nonnull TASTExpression condition)
    throws ConstraintError
  {
    final StringBuilder m = new StringBuilder();
    m
      .append("The condition expression of a condition must be of type boolean, but an expression was given here of type ");
    m.append(condition.getType().getName());

    return new TypeCheckerError(
      token.getFile(),
      token.getPosition(),
      Code.TYPE_ERROR_EXPRESSION_CONDITION_NOT_BOOLEAN,
      m.toString());
  }

  public static @Nonnull TypeCheckerError typeRecordFieldNotManifest(
    final @Nonnull UASTRTypeName type_name,
    final @Nonnull TType ty)
    throws ConstraintError
  {
    final StringBuilder m = new StringBuilder();
    m.append("The (non-manifest) type ");
    m.append(ty.getName());
    m.append(" cannot be used as the type of a record field");

    return type_name
      .typeNameVisitableAccept(new UASTRTypeNameVisitor<TypeCheckerError, ConstraintError>() {
        @SuppressWarnings("synthetic-access") @Override public
          TypeCheckerError
          typeNameVisitBuiltIn(
            final @Nonnull UASTRTypeNameBuiltIn t)
            throws ConstraintError
        {
          final TokenIdentifierLower name = t.getName();
          return new TypeCheckerError(
            name.getFile(),
            name.getPosition(),
            Code.TYPE_ERROR_RECORD_FIELD_NOT_MANIFEST,
            m.toString());
        }

        @SuppressWarnings("synthetic-access") @Override public
          TypeCheckerError
          typeNameVisitGlobal(
            final @Nonnull UASTRTypeNameGlobal t)
            throws ConstraintError
        {
          final TokenIdentifierLower name = t.getName();
          return new TypeCheckerError(
            name.getFile(),
            name.getPosition(),
            Code.TYPE_ERROR_RECORD_FIELD_NOT_MANIFEST,
            m.toString());
        }
      });
  }

  private final @Nonnull Code code;

  private TypeCheckerError(
    final @Nonnull File file,
    final @Nonnull Position position,
    final @Nonnull Code code,
    final @Nonnull String message)
    throws ConstraintError
  {
    super(message, file, position);
    this.code = code;
  }

  public @Nonnull Code getCode()
  {
    return this.code;
  }
}
