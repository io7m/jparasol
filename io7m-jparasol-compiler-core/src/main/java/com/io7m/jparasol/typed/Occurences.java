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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jaux.functional.Unit;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDValueLocal;
import com.io7m.jparasol.typed.ast.TASTExpression;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTEApplication;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTEBoolean;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTEConditional;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTEInteger;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTELet;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTENew;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTEReal;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTERecord;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTERecordProjection;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTESwizzle;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTEVariable;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTRecordFieldAssignment;
import com.io7m.jparasol.typed.ast.TASTExpressionVisitor;
import com.io7m.jparasol.typed.ast.TASTLocalLevelVisitor;
import com.io7m.jparasol.typed.ast.TASTTermName.TASTTermNameExternal;
import com.io7m.jparasol.typed.ast.TASTTermName.TASTTermNameGlobal;
import com.io7m.jparasol.typed.ast.TASTTermName.TASTTermNameLocal;
import com.io7m.jparasol.typed.ast.TASTTermNameVisitor;

/**
 * Calculate the set of local variables that occur in a given expression.
 */

public final class Occurences
{
  private static final class Checker implements
    TASTExpressionVisitor<Unit, Unit, ConstraintError>
  {
    private final @Nonnull Set<String> check_names;
    private final @Nonnull Set<String> found_names;

    public Checker(
      final @Nonnull Set<String> names)
    {
      this.check_names = names;
      this.found_names = new HashSet<String>();
    }

    @Override public Unit expressionVisitApplication(
      final List<Unit> arguments,
      final TASTEApplication e)
      throws ConstraintError,
        ConstraintError
    {
      return Unit.unit();
    }

    @Override public boolean expressionVisitApplicationPre(
      final TASTEApplication e)
      throws ConstraintError,
        ConstraintError
    {
      return true;
    }

    @Override public Unit expressionVisitBoolean(
      final TASTEBoolean e)
      throws ConstraintError,
        ConstraintError
    {
      return Unit.unit();
    }

    @Override public Unit expressionVisitConditional(
      final Unit condition,
      final Unit left,
      final Unit right,
      final TASTEConditional e)
      throws ConstraintError,
        ConstraintError
    {
      return Unit.unit();
    }

    @Override public void expressionVisitConditionalConditionPost(
      final TASTEConditional e)
      throws ConstraintError,
        ConstraintError
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalConditionPre(
      final TASTEConditional e)
      throws ConstraintError,
        ConstraintError
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalLeftPost(
      final TASTEConditional e)
      throws ConstraintError,
        ConstraintError
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalLeftPre(
      final TASTEConditional e)
      throws ConstraintError,
        ConstraintError
    {
      // Nothing
    }

    @Override public boolean expressionVisitConditionalPre(
      final TASTEConditional e)
      throws ConstraintError,
        ConstraintError
    {
      return true;
    }

    @Override public void expressionVisitConditionalRightPost(
      final TASTEConditional e)
      throws ConstraintError,
        ConstraintError
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalRightPre(
      final TASTEConditional e)
      throws ConstraintError,
        ConstraintError
    {
      // Nothing
    }

    @Override public Unit expressionVisitInteger(
      final TASTEInteger e)
      throws ConstraintError,
        ConstraintError
    {
      return Unit.unit();
    }

    @Override public Unit expressionVisitLet(
      final List<Unit> bindings,
      final Unit body,
      final TASTELet e)
      throws ConstraintError,
        ConstraintError
    {
      return Unit.unit();
    }

    @Override public
      TASTLocalLevelVisitor<Unit, ConstraintError>
      expressionVisitLetPre(
        final TASTELet e)
        throws ConstraintError,
          ConstraintError
    {
      return new LocalChecker(this.check_names, this.found_names);
    }

    @Override public Unit expressionVisitNew(
      final List<Unit> arguments,
      final TASTENew e)
      throws ConstraintError,
        ConstraintError
    {
      return Unit.unit();
    }

    @Override public boolean expressionVisitNewPre(
      final TASTENew e)
      throws ConstraintError,
        ConstraintError
    {
      return true;
    }

    @Override public Unit expressionVisitReal(
      final TASTEReal e)
      throws ConstraintError,
        ConstraintError
    {
      return Unit.unit();
    }

    @Override public Unit expressionVisitRecord(
      final TASTERecord e)
      throws ConstraintError,
        ConstraintError
    {
      for (final TASTRecordFieldAssignment a : e.getAssignments()) {
        a.getExpression().expressionVisitableAccept(this);
      }

      return Unit.unit();
    }

    @Override public Unit expressionVisitRecordProjection(
      final Unit body,
      final TASTERecordProjection e)
      throws ConstraintError,
        ConstraintError
    {
      return Unit.unit();
    }

    @Override public boolean expressionVisitRecordProjectionPre(
      final TASTERecordProjection e)
      throws ConstraintError,
        ConstraintError
    {
      return true;
    }

    @Override public Unit expressionVisitSwizzle(
      final Unit body,
      final TASTESwizzle e)
      throws ConstraintError,
        ConstraintError
    {
      return Unit.unit();
    }

    @Override public boolean expressionVisitSwizzlePre(
      final TASTESwizzle e)
      throws ConstraintError,
        ConstraintError
    {
      return true;
    }

    @Override public Unit expressionVisitVariable(
      final TASTEVariable e)
      throws ConstraintError,
        ConstraintError
    {
      e.getName().termNameVisitableAccept(
        new TASTTermNameVisitor<Unit, ConstraintError>() {
          @Override public Unit termNameVisitGlobal(
            final @Nonnull TASTTermNameGlobal t)
            throws ConstraintError,
              ConstraintError
          {
            return Unit.unit();
          }

          @SuppressWarnings("synthetic-access") @Override public
            Unit
            termNameVisitLocal(
              final @Nonnull TASTTermNameLocal t)
              throws ConstraintError,
                ConstraintError
          {
            if (Checker.this.check_names.contains(t.getCurrent())) {
              Checker.this.found_names.add(t.getCurrent());
            }
            return Unit.unit();
          }

          @Override public Unit termNameVisitExternal(
            final @Nonnull TASTTermNameExternal t)
            throws ConstraintError,
              ConstraintError
          {
            return Unit.unit();
          }
        });

      return Unit.unit();
    }

    public @Nonnull Set<String> getFoundNames()
    {
      return this.found_names;
    }
  }

  private static final class LocalChecker implements
    TASTLocalLevelVisitor<Unit, ConstraintError>
  {
    private final @Nonnull Set<String> check_names;
    private final @Nonnull Set<String> found_names;

    public LocalChecker(
      final @Nonnull Set<String> in_check_names,
      final @Nonnull Set<String> in_found_names)
    {
      this.check_names = in_check_names;
      this.found_names = in_found_names;
    }

    @Override public Unit localVisitValueLocal(
      final @Nonnull TASTDValueLocal v)
      throws ConstraintError,
        ConstraintError
    {
      final String name = v.getName().getCurrent();
      if (this.check_names.contains(name)) {
        this.found_names.add(name);
      }
      return Unit.unit();
    }
  }

  public static @Nonnull Set<String> occursIn(
    final @Nonnull TASTExpression e,
    final @Nonnull Set<String> names)
    throws ConstraintError
  {
    Constraints.constrainNotNull(e, "Expression");
    Constraints.constrainNotNull(names, "Names");

    final Checker ec = new Checker(names);
    e.expressionVisitableAccept(ec);
    return ec.getFoundNames();
  }

  private Occurences()
  {
    throw new UnreachableCodeException();
  }
}
