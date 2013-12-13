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

package com.io7m.jparasol.untyped.ast.initial;

import java.util.List;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Option;
import com.io7m.jparasol.ModulePath;
import com.io7m.jparasol.PackagePath;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.lexer.Token.TokenIdentifierUpper;

public abstract class UASTIDeclaration<S extends UASTIStatus>
{
  /**
   * The type of function declarations.
   */

  public static abstract class UASTIDFunction<S extends UASTIStatus> extends
    UASTIDTerm<S>
  {

  }

  public static final class UASTIDFunctionArgument<S extends UASTIStatus>
  {
    private final @Nonnull TokenIdentifierLower name;
    private final @Nonnull UASTITypePath        type;

    public UASTIDFunctionArgument(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTITypePath type)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.type = Constraints.constrainNotNull(type, "Type");
    }

    public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    public @Nonnull UASTITypePath getType()
    {
      return this.type;
    }
  }

  /**
   * Fully defined functions.
   */

  public static final class UASTIDFunctionDefined<S extends UASTIStatus> extends
    UASTIDFunction<S>
  {
    private final @Nonnull List<UASTIDFunctionArgument<S>> arguments;
    private final @Nonnull UASTIExpression<S>              body;
    private final @Nonnull TokenIdentifierLower            name;
    private final @Nonnull UASTITypePath                   return_type;

    public UASTIDFunctionDefined(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull List<UASTIDFunctionArgument<S>> arguments,
      final @Nonnull UASTITypePath return_type,
      final @Nonnull UASTIExpression<S> body)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.arguments = Constraints.constrainNotNull(arguments, "Arguments");
      this.return_type =
        Constraints.constrainNotNull(return_type, "Return type");
      this.body = Constraints.constrainNotNull(body, "Body");
    }

    public @Nonnull List<UASTIDFunctionArgument<S>> getArguments()
    {
      return this.arguments;
    }

    public @Nonnull UASTIExpression<S> getBody()
    {
      return this.body;
    }

    public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    public @Nonnull UASTITypePath getReturnType()
    {
      return this.return_type;
    }
  }

  /**
   * Functions with external declarations (private FFI).
   */

  public static final class UASTIDFunctionExternal<S extends UASTIStatus> extends
    UASTIDFunction<S>
  {
    private final @Nonnull List<UASTIDFunctionArgument<S>> arguments;
    private final @Nonnull TokenIdentifierLower            external;
    private final @Nonnull TokenIdentifierLower            name;
    private final @Nonnull UASTITypePath                   return_type;

    public UASTIDFunctionExternal(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull List<UASTIDFunctionArgument<S>> arguments,
      final @Nonnull UASTITypePath return_type,
      final @Nonnull TokenIdentifierLower external)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.arguments = Constraints.constrainNotNull(arguments, "Arguments");
      this.return_type =
        Constraints.constrainNotNull(return_type, "Return type");
      this.external = Constraints.constrainNotNull(external, "External");
    }

    public @Nonnull List<UASTIDFunctionArgument<S>> getArguments()
    {
      return this.arguments;
    }

    public @Nonnull TokenIdentifierLower getExternal()
    {
      return this.external;
    }

    public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    public @Nonnull UASTITypePath getReturnType()
    {
      return this.return_type;
    }
  }

  /**
   * Import declarations.
   */

  public static final class UASTIDImport<S extends UASTIStatus> extends
    UASTIDeclaration<S>
  {
    private final @Nonnull ModulePath                   path;
    private final @Nonnull Option<TokenIdentifierUpper> rename;

    public UASTIDImport(
      final @Nonnull ModulePath path,
      final @Nonnull Option<TokenIdentifierUpper> rename)
      throws ConstraintError
    {
      this.path = Constraints.constrainNotNull(path, "Path");
      this.rename = Constraints.constrainNotNull(rename, "Rename");
    }

    public @Nonnull ModulePath getPath()
    {
      return this.path;
    }

    public @Nonnull Option<TokenIdentifierUpper> getRename()
    {
      return this.rename;
    }
  }

  /**
   * Import declarations.
   */

  public static final class UASTIDPackage<S extends UASTIStatus> extends
    UASTIDeclaration<S>
  {
    private final @Nonnull PackagePath path;

    public UASTIDPackage(
      final @Nonnull PackagePath path)
      throws ConstraintError
    {
      this.path = Constraints.constrainNotNull(path, "Path");
    }

    public @Nonnull PackagePath getPath()
    {
      return this.path;
    }
  }

  /**
   * The type of term declarations.
   */

  public static abstract class UASTIDTerm<S extends UASTIStatus> extends
    UASTIDeclaration<S>
  {

  }

  /**
   * The type of type declarations.
   */

  public static abstract class UASTIDType<S extends UASTIStatus> extends
    UASTIDeclaration<S>
  {

  }

  /**
   * Record declarations.
   */

  public static final class UASTIDTypeRecord<S extends UASTIStatus> extends
    UASTIDType<S>
  {
    private final @Nonnull List<UASTIDTypeRecordField<S>> fields;
    private final @Nonnull TokenIdentifierLower           name;

    public UASTIDTypeRecord(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull List<UASTIDTypeRecordField<S>> fields)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.fields = Constraints.constrainNotNull(fields, "Fields");
    }

    public @Nonnull List<UASTIDTypeRecordField<S>> getFields()
    {
      return this.fields;
    }

    public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }
  }

  public static final class UASTIDTypeRecordField<S extends UASTIStatus>
  {
    private final @Nonnull TokenIdentifierLower name;
    private final @Nonnull UASTITypePath        type;

    public UASTIDTypeRecordField(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTITypePath type)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.type = Constraints.constrainNotNull(type, "Type");
    }

    public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    public @Nonnull UASTITypePath getType()
    {
      return this.type;
    }
  }

  /**
   * Value declarations.
   */

  public static final class UASTIDValue<S extends UASTIStatus> extends
    UASTIDTerm<S>
  {
    private final @Nonnull Option<UASTITypePath> ascription;
    private final @Nonnull UASTIExpression<S>    expression;
    private final @Nonnull TokenIdentifierLower  name;

    public UASTIDValue(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull Option<UASTITypePath> ascription,
      final @Nonnull UASTIExpression<S> expression)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.ascription =
        Constraints.constrainNotNull(ascription, "Ascription");
      this.expression =
        Constraints.constrainNotNull(expression, "Expression");
    }

    public @Nonnull Option<UASTITypePath> getAscription()
    {
      return this.ascription;
    }

    public @Nonnull UASTIExpression<S> getExpression()
    {
      return this.expression;
    }

    public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }
  }

  /**
   * Local value declarations (let).
   */

  public static final class UASTIDValueLocal<S extends UASTIStatus> extends
    UASTIDTerm<S>
  {
    private final @Nonnull Option<UASTITypePath> ascription;
    private final @Nonnull UASTIExpression<S>    expression;
    private final @Nonnull TokenIdentifierLower  name;

    public UASTIDValueLocal(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull Option<UASTITypePath> ascription,
      final @Nonnull UASTIExpression<S> expression)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.ascription =
        Constraints.constrainNotNull(ascription, "Ascription");
      this.expression =
        Constraints.constrainNotNull(expression, "Expression");
    }

    public @Nonnull Option<UASTITypePath> getAscription()
    {
      return this.ascription;
    }

    public @Nonnull UASTIExpression<S> getExpression()
    {
      return this.expression;
    }

    public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }
  }
}
