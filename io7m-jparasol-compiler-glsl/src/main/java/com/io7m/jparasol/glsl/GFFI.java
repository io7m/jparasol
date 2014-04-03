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

package com.io7m.jparasol.glsl;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jaux.functional.Function;
import com.io7m.jaux.functional.Option.Some;
import com.io7m.jlog.Level;
import com.io7m.jlog.Log;
import com.io7m.jparasol.glsl.GVersion.GVersionES;
import com.io7m.jparasol.glsl.GVersion.GVersionFull;
import com.io7m.jparasol.glsl.ast.GASTExpression;
import com.io7m.jparasol.lexer.Position;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.typed.ast.TASTDeclaration;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDExternal;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDFunctionDefined;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDFunctionExternal;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDValueDefined;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDValueExternal;
import com.io7m.jparasol.typed.ast.TASTExpression;
import com.io7m.jparasol.typed.ast.TASTTermName;

public final class GFFI
{
  private static final class GLSL_ES_orLessThanGLSL120 implements
    Function<GVersion, Boolean>
  {
    public GLSL_ES_orLessThanGLSL120()
    {
      // Nothing
    }

    @Override public Boolean call(
      final GVersion x)
    {
      try {
        return x
          .versionAccept(new GVersionVisitor<Boolean, ConstraintError>() {
            @Override public Boolean versionVisitES(
              final GVersionES v)
              throws ConstraintError
            {
              return Boolean.TRUE;
            }

            @Override public Boolean versionVisitFull(
              final GVersionFull v)
              throws ConstraintError
            {
              if (v.compareTo(GVersionFull.GLSL_120) <= 0) {
                return Boolean.TRUE;
              }
              return Boolean.FALSE;
            }
          });
      } catch (final ConstraintError e) {
        throw new UnreachableCodeException(e);
      }
    }
  }

  private static final @Nonnull Map<String, Function<GVersion, Boolean>>                  DECLARATION_FUNCTION_REQUIRED;
  private static final @Nonnull Map<String, Function<TASTDValueExternal, TASTExpression>> DECLARATION_VALUE_EXPRESSIONS;
  private static final @Nonnull Map<String, GFFIExpressionEmitter>                        EXPRESSION_EMITTERS;
  private static final @Nonnull Function<GVersion, Boolean>                               GLSL_ES_OR_LT_GLSL120;

  static {
    GLSL_ES_OR_LT_GLSL120 = new GLSL_ES_orLessThanGLSL120();

    DECLARATION_FUNCTION_REQUIRED =
      new HashMap<String, Function<GVersion, Boolean>>();
    GFFI.DECLARATION_FUNCTION_REQUIRED.put(
      "com_io7m_parasol_float_is_nan",
      GFFI.GLSL_ES_OR_LT_GLSL120);
    GFFI.DECLARATION_FUNCTION_REQUIRED.put(
      "com_io7m_parasol_float_is_infinite",
      GFFI.GLSL_ES_OR_LT_GLSL120);
    GFFI.DECLARATION_FUNCTION_REQUIRED.put(
      "com_io7m_parasol_float_sign",
      GFFI.GLSL_ES_OR_LT_GLSL120);
    GFFI.DECLARATION_FUNCTION_REQUIRED.put(
      "com_io7m_parasol_float_truncate",
      GFFI.GLSL_ES_OR_LT_GLSL120);

    DECLARATION_VALUE_EXPRESSIONS =
      new HashMap<String, Function<TASTDValueExternal, TASTExpression>>();
    GFFI.DECLARATION_VALUE_EXPRESSIONS.put(
      "com_io7m_parasol_fragment_coordinate",
      new Function<TASTDeclaration.TASTDValueExternal, TASTExpression>() {
        @Override public TASTExpression call(
          final TASTDValueExternal x)
        {
          try {
            final File file = new File("<generated>");
            final TokenIdentifierLower original =
              new TokenIdentifierLower(file, Position.ZERO, "gl_FragCoord");
            final TASTTermName name =
              new TASTTermName.TASTTermNameExternal(original, "gl_FragCoord");
            return new TASTExpression.TASTEVariable(x.getType(), name);
          } catch (final ConstraintError e) {
            throw new UnreachableCodeException(e);
          }
        }
      });

    EXPRESSION_EMITTERS = new HashMap<String, GFFIExpressionEmitter>();

    /**
     * Automatically generated by ffi-values.sh, do not edit.
     */

    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_float_absolute",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_float_absolute(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_float_add",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_float_add(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_float_arc_cosine",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_float_arc_cosine(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_float_arc_sine",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_float_arc_sine(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_float_arc_tangent",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_float_arc_tangent(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_float_ceiling",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_float_ceiling(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_float_clamp",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_float_clamp(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_float_cosine",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_float_cosine(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_float_divide",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_float_divide(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_float_equals",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_float_equals(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_float_floor",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_float_floor(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_float_greater",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_float_greater(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_float_greater_or_equal",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters
            .com_io7m_parasol_float_greater_or_equal(f, arguments, version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_float_interpolate",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_float_interpolate(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_float_is_infinite",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_float_is_infinite(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_float_is_nan",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_float_is_nan(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_float_lesser",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_float_lesser(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_float_lesser_or_equal",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters
            .com_io7m_parasol_float_lesser_or_equal(f, arguments, version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_float_maximum",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_float_maximum(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_float_minimum",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_float_minimum(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_float_modulo",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_float_modulo(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_float_multiply",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_float_multiply(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_float_power",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_float_power(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_float_round",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_float_round(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_float_sign",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_float_sign(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_float_sine",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_float_sine(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_float_square_root",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_float_square_root(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_float_subtract",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_float_subtract(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_float_tangent",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_float_tangent(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_float_truncate",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_float_truncate(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_integer_add",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_integer_add(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_integer_divide",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_integer_divide(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_integer_multiply",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_integer_multiply(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_integer_subtract",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_integer_subtract(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_matrix3x3f_multiply",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_matrix3x3f_multiply(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_matrix3x3f_multiply_vector",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters
            .com_io7m_parasol_matrix3x3f_multiply_vector(
              f,
              arguments,
              version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_matrix4x4f_multiply",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_matrix4x4f_multiply(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_matrix4x4f_multiply_vector",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters
            .com_io7m_parasol_matrix4x4f_multiply_vector(
              f,
              arguments,
              version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_sampler2d_texture",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_sampler2d_texture(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_sampler2d_texture_projective_3f",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters
            .com_io7m_parasol_sampler2d_texture_projective_3f(
              f,
              arguments,
              version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_sampler2d_texture_projective_4f",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters
            .com_io7m_parasol_sampler2d_texture_projective_4f(
              f,
              arguments,
              version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_sampler_cube_texture",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters
            .com_io7m_parasol_sampler_cube_texture(f, arguments, version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector2f_add",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector2f_add(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector2f_add_scalar",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector2f_add_scalar(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector2f_dot",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector2f_dot(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector2f_interpolate",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters
            .com_io7m_parasol_vector2f_interpolate(f, arguments, version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector2f_magnitude",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector2f_magnitude(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector2f_multiply",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector2f_multiply(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector2f_multiply_scalar",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters
            .com_io7m_parasol_vector2f_multiply_scalar(f, arguments, version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector2f_negate",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector2f_negate(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector2f_normalize",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector2f_normalize(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector2f_reflect",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector2f_reflect(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector2f_refract",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector2f_refract(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector2f_subtract",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector2f_subtract(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector2i_add",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector2i_add(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector2i_add_scalar",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector2i_add_scalar(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector2i_dot",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector2i_dot(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector2i_interpolate",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters
            .com_io7m_parasol_vector2i_interpolate(f, arguments, version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector2i_magnitude",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector2i_magnitude(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector2i_multiply",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector2i_multiply(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector2i_multiply_scalar",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters
            .com_io7m_parasol_vector2i_multiply_scalar(f, arguments, version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector2i_negate",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector2i_negate(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector2i_normalize",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector2i_normalize(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector2i_reflect",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector2i_reflect(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector2i_refract",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector2i_refract(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector2i_subtract",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector2i_subtract(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector3f_add",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector3f_add(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector3f_add_scalar",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector3f_add_scalar(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector3f_cross",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector3f_cross(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector3f_dot",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector3f_dot(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector3f_interpolate",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters
            .com_io7m_parasol_vector3f_interpolate(f, arguments, version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector3f_magnitude",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector3f_magnitude(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector3f_multiply",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector3f_multiply(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector3f_multiply_scalar",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters
            .com_io7m_parasol_vector3f_multiply_scalar(f, arguments, version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector3f_negate",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector3f_negate(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector3f_normalize",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector3f_normalize(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector3f_reflect",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector3f_reflect(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector3f_refract",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector3f_refract(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector3f_subtract",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector3f_subtract(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector3i_add",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector3i_add(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector3i_add_scalar",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector3i_add_scalar(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector3i_dot",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector3i_dot(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector3i_interpolate",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters
            .com_io7m_parasol_vector3i_interpolate(f, arguments, version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector3i_magnitude",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector3i_magnitude(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector3i_multiply",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector3i_multiply(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector3i_multiply_scalar",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters
            .com_io7m_parasol_vector3i_multiply_scalar(f, arguments, version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector3i_negate",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector3i_negate(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector3i_normalize",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector3i_normalize(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector3i_reflect",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector3i_reflect(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector3i_refract",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector3i_refract(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector3i_subtract",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector3i_subtract(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector4f_add",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector4f_add(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector4f_add_scalar",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector4f_add_scalar(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector4f_dot",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector4f_dot(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector4f_interpolate",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters
            .com_io7m_parasol_vector4f_interpolate(f, arguments, version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector4f_magnitude",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector4f_magnitude(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector4f_multiply",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector4f_multiply(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector4f_multiply_scalar",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters
            .com_io7m_parasol_vector4f_multiply_scalar(f, arguments, version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector4f_negate",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector4f_negate(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector4f_normalize",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector4f_normalize(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector4f_reflect",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector4f_reflect(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector4f_refract",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector4f_refract(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector4f_subtract",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector4f_subtract(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector4i_add",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector4i_add(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector4i_add_scalar",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector4i_add_scalar(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector4i_dot",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector4i_dot(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector4i_interpolate",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters
            .com_io7m_parasol_vector4i_interpolate(f, arguments, version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector4i_magnitude",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector4i_magnitude(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector4i_multiply",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector4i_multiply(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector4i_multiply_scalar",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters
            .com_io7m_parasol_vector4i_multiply_scalar(f, arguments, version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector4i_negate",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector4i_negate(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector4i_normalize",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector4i_normalize(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector4i_reflect",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector4i_reflect(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector4i_refract",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector4i_refract(
            f,
            arguments,
            version);
        }
      });
    GFFI.EXPRESSION_EMITTERS.put(
      "com_io7m_parasol_vector4i_subtract",
      new GFFIExpressionEmitter() {
        @Override public @Nonnull GFFIExpression emitExpression(
          final @Nonnull TASTDFunctionExternal f,
          final @Nonnull List<GASTExpression> arguments,
          final @Nonnull GVersion version)
          throws ConstraintError
        {
          return GFFIExpressionEmitters.com_io7m_parasol_vector4i_subtract(
            f,
            arguments,
            version);
        }
      });

  }

  public static @Nonnull GFFI newFFI(
    final @Nonnull Log log)
  {
    return new GFFI(log);
  }

  private final @Nonnull Log log;

  private GFFI(
    final @Nonnull Log in_log)
  {
    this.log = new Log(in_log, "ffi");
  }

  public @Nonnull GFFIExpression getExpression(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
    throws ConstraintError,
      GFFIError
  {
    final TASTDExternal ext = f.getExternal();
    final TokenIdentifierLower ename = ext.getName();
    final String name = ename.getActual();

    if (this.log.enabled(Level.LOG_DEBUG)) {
      this.log.debug(String.format(
        "expression for %s at %s:%s",
        name,
        ename.getFile(),
        ename.getPosition()));
    }

    if (GFFI.EXPRESSION_EMITTERS.containsKey(name)) {
      final GFFIExpressionEmitter e = GFFI.EXPRESSION_EMITTERS.get(name);
      return e.emitExpression(f, arguments, version);
    }
    throw GFFIError.unknownExternal(ext);
  }

  public @CheckForNull TASTDFunctionDefined getFunctionDefinition(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull GVersion version)
    throws ConstraintError
  {
    final TASTDExternal ext = f.getExternal();
    final TokenIdentifierLower ename = ext.getName();
    final String name = ename.getActual();

    if (this.log.enabled(Level.LOG_DEBUG)) {
      this.log.debug(String.format(
        "definition for %s at %s:%s",
        name,
        ename.getFile(),
        ename.getPosition()));
    }

    final Function<GVersion, Boolean> require_check =
      GFFI.DECLARATION_FUNCTION_REQUIRED.get(name);
    if (require_check != null) {
      final boolean required = require_check.call(version).booleanValue();
      if (required) {
        if (this.log.enabled(Level.LOG_DEBUG)) {
          this.log.debug(String.format(
            "fallback required for %s at %s:%s",
            name,
            ename.getFile(),
            ename.getPosition()));
        }

        assert ext.getEmulation().isSome();
        final Some<TASTExpression> emu =
          (Some<TASTExpression>) ext.getEmulation();
        return new TASTDFunctionDefined(
          f.getName(),
          f.getArguments(),
          emu.value,
          f.getType());
      }
    }

    if (this.log.enabled(Level.LOG_DEBUG)) {
      this.log.debug(String.format(
        "no fallback required for %s at %s:%s",
        name,
        ename.getFile(),
        ename.getPosition()));
    }

    return null;
  }

  public @CheckForNull TASTDValueDefined getValueDefinition(
    final @Nonnull TASTDValueExternal v,
    final @Nonnull GVersion version)
    throws GFFIError,
      ConstraintError
  {
    final TASTDExternal ext = v.getExternal();
    final TokenIdentifierLower ename = ext.getName();
    final String name = ename.getActual();

    if (this.log.enabled(Level.LOG_DEBUG)) {
      this.log.debug(String.format(
        "definition for %s at %s:%s",
        name,
        ename.getFile(),
        ename.getPosition()));
    }

    if (GFFI.DECLARATION_VALUE_EXPRESSIONS.containsKey(name) == false) {
      throw GFFIError.unknownExternal(ext);
    }

    final Function<TASTDValueExternal, TASTExpression> emitter =
      GFFI.DECLARATION_VALUE_EXPRESSIONS.get(name);

    final TASTExpression exp = emitter.call(v);
    assert exp.getType().equals(v.getType());
    return new TASTDValueDefined(v.getName(), exp);
  }
}
