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

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.lexer.Token;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.lexer.Token.TokenLet;
import com.io7m.jparasol.lexer.Token.TokenLiteralBoolean;
import com.io7m.jparasol.lexer.Token.TokenLiteralInteger;
import com.io7m.jparasol.lexer.Token.TokenLiteralReal;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDValueLocal;

public abstract class UASTIExpression<S extends UASTIStatus>
{
  public static final class UASTIEApplication<S extends UASTIStatus> extends
    UASTIExpression<S>
  {
    private final @Nonnull List<UASTIExpression<S>> arguments;
    private final @Nonnull UASTIValuePath           name;

    public UASTIEApplication(
      final @Nonnull UASTIValuePath name,
      final @Nonnull List<UASTIExpression<S>> arguments)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.arguments = Constraints.constrainNotNull(arguments, "Arguments");
    }

    public @Nonnull List<UASTIExpression<S>> getArguments()
    {
      return this.arguments;
    }

    public @Nonnull UASTIValuePath getName()
    {
      return this.name;
    }
  }

  public static final class UASTIEBoolean<S extends UASTIStatus> extends
    UASTIExpression<S>
  {
    private final @Nonnull TokenLiteralBoolean token;

    public UASTIEBoolean(
      final @Nonnull Token.TokenLiteralBoolean token)
      throws ConstraintError
    {
      this.token = Constraints.constrainNotNull(token, "Token");
    }

    public @Nonnull TokenLiteralBoolean getToken()
    {
      return this.token;
    }

    public boolean getValue()
    {
      return this.token.getValue();
    }
  }

  public static final class UASTIEConditional<S extends UASTIStatus> extends
    UASTIExpression<S>
  {
    private final @Nonnull UASTIExpression<S> condition;
    private final @Nonnull UASTIExpression<S> left;
    private final @Nonnull UASTIExpression<S> right;

    public UASTIEConditional(
      final @Nonnull UASTIExpression<S> condition,
      final @Nonnull UASTIExpression<S> left,
      final @Nonnull UASTIExpression<S> right)
      throws ConstraintError
    {
      this.condition = Constraints.constrainNotNull(condition, "Condition");
      this.left = Constraints.constrainNotNull(left, "Left");
      this.right = Constraints.constrainNotNull(right, "Right");
    }

    public @Nonnull UASTIExpression<S> getCondition()
    {
      return this.condition;
    }

    public @Nonnull UASTIExpression<S> getLeft()
    {
      return this.left;
    }

    public @Nonnull UASTIExpression<S> getRight()
    {
      return this.right;
    }
  }

  public static final class UASTIEInteger<S extends UASTIStatus> extends
    UASTIExpression<S>
  {
    private final @Nonnull TokenLiteralInteger token;

    public UASTIEInteger(
      final @Nonnull Token.TokenLiteralInteger token)
      throws ConstraintError
    {
      this.token = Constraints.constrainNotNull(token, "Token");
    }

    public @Nonnull TokenLiteralInteger getToken()
    {
      return this.token;
    }

    public @Nonnull BigDecimal getValue()
    {
      return this.token.getValue();
    }
  }

  public static final class UASTIELet<S extends UASTIStatus> extends
    UASTIExpression<S>
  {
    private final @Nonnull List<UASTIDValueLocal<S>> bindings;
    private final @Nonnull UASTIExpression<S>        body;
    private final @Nonnull TokenLet                  token;

    public UASTIELet(
      final @Nonnull TokenLet token,
      final @Nonnull List<UASTIDeclaration.UASTIDValueLocal<S>> bindings,
      final @Nonnull UASTIExpression<S> body)
      throws ConstraintError
    {
      this.token = Constraints.constrainNotNull(token, "Token");
      this.bindings = Constraints.constrainNotNull(bindings, "Bindings");
      this.body = Constraints.constrainNotNull(body, "Body");
    }

    public @Nonnull List<UASTIDValueLocal<S>> getBindings()
    {
      return this.bindings;
    }

    public @Nonnull UASTIExpression<S> getBody()
    {
      return this.body;
    }

    public @Nonnull TokenLet getToken()
    {
      return this.token;
    }
  }

  public static final class UASTIENew<S extends UASTIStatus> extends
    UASTIExpression<S>
  {
    private final @Nonnull List<UASTIExpression<S>> arguments;
    private final @Nonnull UASTITypePath            name;

    public UASTIENew(
      final @Nonnull UASTITypePath name,
      final @Nonnull List<UASTIExpression<S>> arguments)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.arguments = Constraints.constrainNotNull(arguments, "Arguments");
    }

    public @Nonnull List<UASTIExpression<S>> getArguments()
    {
      return this.arguments;
    }

    public @Nonnull UASTITypePath getName()
    {
      return this.name;
    }
  }

  public static final class UASTIEReal<S extends UASTIStatus> extends
    UASTIExpression<S>
  {
    private final @Nonnull TokenLiteralReal token;

    public UASTIEReal(
      final @Nonnull Token.TokenLiteralReal token)
      throws ConstraintError
    {
      this.token = Constraints.constrainNotNull(token, "Token");
    }

    public @Nonnull TokenLiteralReal getToken()
    {
      return this.token;
    }

    public @Nonnull BigDecimal getValue()
    {
      return this.token.getValue();
    }
  }

  public static final class UASTIERecord<S extends UASTIStatus> extends
    UASTIExpression<S>
  {
    private final @Nonnull List<UASTIRecordFieldAssignment<S>> assignments;
    private final @Nonnull UASTITypePath                       type_path;

    public UASTIERecord(
      final @Nonnull UASTITypePath type_path,
      final @Nonnull List<UASTIRecordFieldAssignment<S>> assignments)
      throws ConstraintError
    {
      this.type_path = Constraints.constrainNotNull(type_path, "Type path");
      this.assignments =
        Constraints.constrainNotNull(assignments, "Assignments");
    }

    public @Nonnull List<UASTIRecordFieldAssignment<S>> getAssignments()
    {
      return this.assignments;
    }

    public @Nonnull UASTITypePath getTypePath()
    {
      return this.type_path;
    }
  }

  public static final class UASTIERecordProjection<S extends UASTIStatus> extends
    UASTIExpression<S>
  {
    private final @Nonnull UASTIExpression<S>   expression;
    private final @Nonnull TokenIdentifierLower field;

    public UASTIERecordProjection(
      final @Nonnull UASTIExpression<S> expression,
      final @Nonnull TokenIdentifierLower field)
      throws ConstraintError
    {
      this.expression =
        Constraints.constrainNotNull(expression, "Expression");
      this.field = Constraints.constrainNotNull(field, "Field");
    }

    public @Nonnull UASTIExpression<S> getExpression()
    {
      return this.expression;
    }

    public @Nonnull TokenIdentifierLower getField()
    {
      return this.field;
    }
  }

  public static final class UASTIESwizzle<S extends UASTIStatus> extends
    UASTIExpression<S>
  {
    private final @Nonnull UASTIExpression<S>         expression;
    private final @Nonnull List<TokenIdentifierLower> fields;

    public UASTIESwizzle(
      final @Nonnull UASTIExpression<S> expression,
      final @Nonnull List<TokenIdentifierLower> fields)
      throws ConstraintError
    {
      this.expression =
        Constraints.constrainNotNull(expression, "Expression");
      this.fields = Constraints.constrainNotNull(fields, "Fields");
    }

    public @Nonnull UASTIExpression<S> getExpression()
    {
      return this.expression;
    }

    public @Nonnull List<TokenIdentifierLower> getFields()
    {
      return this.fields;
    }
  }

  public static final class UASTIEVariable<S extends UASTIStatus> extends
    UASTIExpression<S>
  {
    private final @Nonnull UASTIValuePath name;

    public UASTIEVariable(
      final @Nonnull UASTIValuePath name)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
    }

    public @Nonnull UASTIValuePath getName()
    {
      return this.name;
    }
  }

  public static final class UASTIRecordFieldAssignment<S extends UASTIStatus>
  {
    private final @Nonnull UASTIExpression<S>   expression;
    private final @Nonnull TokenIdentifierLower name;

    public UASTIRecordFieldAssignment(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTIExpression<S> expression)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.expression =
        Constraints.constrainNotNull(expression, "Expression");
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
