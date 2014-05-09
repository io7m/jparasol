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

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
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
import com.io7m.jparasol.typed.ast.TASTExpressionVisitorType;
import com.io7m.jparasol.typed.ast.TASTLocalLevelVisitorType;
import com.io7m.jparasol.typed.ast.TASTTermName.TASTTermNameExternal;
import com.io7m.jparasol.typed.ast.TASTTermName.TASTTermNameGlobal;
import com.io7m.jparasol.typed.ast.TASTTermName.TASTTermNameLocal;
import com.io7m.jparasol.typed.ast.TASTTermNameVisitorType;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * Calculate the set of local variables that occur in a given expression.
 */

@EqualityReference public final class Occurences
{
  @EqualityReference private static final class Checker implements
    TASTExpressionVisitorType<Unit, Unit, UnreachableCodeException>
  {
    private final Set<String> check_names;
    private final Set<String> found_names;

    public Checker(
      final Set<String> names)
    {
      this.check_names = names;
      this.found_names = new HashSet<String>();
    }

    @Override public Unit expressionVisitApplication(
      final List<Unit> arguments,
      final TASTEApplication e)
      throws UnreachableCodeException,
        UnreachableCodeException
    {
      return Unit.unit();
    }

    @Override public boolean expressionVisitApplicationPre(
      final TASTEApplication e)
    {
      return true;
    }

    @Override public Unit expressionVisitBoolean(
      final TASTEBoolean e)
    {
      return Unit.unit();
    }

    @Override public Unit expressionVisitConditional(
      final Unit condition,
      final Unit left,
      final Unit right,
      final TASTEConditional e)
    {
      return Unit.unit();
    }

    @Override public void expressionVisitConditionalConditionPost(
      final TASTEConditional e)
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalConditionPre(
      final TASTEConditional e)
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalLeftPost(
      final TASTEConditional e)
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalLeftPre(
      final TASTEConditional e)
    {
      // Nothing
    }

    @Override public boolean expressionVisitConditionalPre(
      final TASTEConditional e)
    {
      return true;
    }

    @Override public void expressionVisitConditionalRightPost(
      final TASTEConditional e)
    {
      // Nothing
    }

    @Override public void expressionVisitConditionalRightPre(
      final TASTEConditional e)
    {
      // Nothing
    }

    @Override public Unit expressionVisitInteger(
      final TASTEInteger e)
    {
      return Unit.unit();
    }

    @Override public Unit expressionVisitLet(
      final List<Unit> bindings,
      final Unit body,
      final TASTELet e)
    {
      return Unit.unit();
    }

    @Override public @Nullable
      TASTLocalLevelVisitorType<Unit, UnreachableCodeException>
      expressionVisitLetPre(
        final TASTELet e)
    {
      return new LocalChecker(this.check_names, this.found_names);
    }

    @Override public Unit expressionVisitNew(
      final List<Unit> arguments,
      final TASTENew e)
    {
      return Unit.unit();
    }

    @Override public boolean expressionVisitNewPre(
      final TASTENew e)
    {
      return true;
    }

    @Override public Unit expressionVisitReal(
      final TASTEReal e)
    {
      return Unit.unit();
    }

    @Override public Unit expressionVisitRecord(
      final TASTERecord e)
    {
      for (final TASTRecordFieldAssignment a : e.getAssignments()) {
        a.getExpression().expressionVisitableAccept(this);
      }

      return Unit.unit();
    }

    @Override public Unit expressionVisitRecordProjection(
      final Unit body,
      final TASTERecordProjection e)
    {
      return Unit.unit();
    }

    @Override public boolean expressionVisitRecordProjectionPre(
      final @Nullable TASTERecordProjection e)
    {
      return true;
    }

    @Override public Unit expressionVisitSwizzle(
      final Unit body,
      final TASTESwizzle e)
    {
      return Unit.unit();
    }

    @Override public boolean expressionVisitSwizzlePre(
      final TASTESwizzle e)
    {
      return true;
    }

    @Override public Unit expressionVisitVariable(
      final TASTEVariable e)
    {
      e.getName().termNameVisitableAccept(
        new TASTTermNameVisitorType<Unit, UnreachableCodeException>() {
          @Override public Unit termNameVisitExternal(
            final TASTTermNameExternal t)
          {
            return Unit.unit();
          }

          @Override public Unit termNameVisitGlobal(
            final TASTTermNameGlobal t)
          {
            return Unit.unit();
          }

          @SuppressWarnings("synthetic-access") @Override public
            Unit
            termNameVisitLocal(
              final TASTTermNameLocal t)
          {
            if (Checker.this.check_names.contains(t.getCurrent())) {
              Checker.this.found_names.add(t.getCurrent());
            }
            return Unit.unit();
          }
        });

      return Unit.unit();
    }

    public Set<String> getFoundNames()
    {
      return this.found_names;
    }
  }

  @EqualityReference private static final class LocalChecker implements
    TASTLocalLevelVisitorType<Unit, UnreachableCodeException>
  {
    private final Set<String> check_names;
    private final Set<String> found_names;

    public LocalChecker(
      final Set<String> in_check_names,
      final Set<String> in_found_names)
    {
      this.check_names = in_check_names;
      this.found_names = in_found_names;
    }

    @Override public Unit localVisitValueLocal(
      final TASTDValueLocal v)
    {
      final String name = v.getName().getCurrent();
      if (this.check_names.contains(name)) {
        this.found_names.add(name);
      }
      return Unit.unit();
    }
  }

  /**
   * @param e
   *          The expression
   * @param names
   *          The names
   * @return The occurences of the given names in the given expression
   */

  public static Set<String> occursIn(
    final TASTExpression e,
    final Set<String> names)
  {
    NullCheck.notNull(e, "Expression");
    NullCheck.notNullAll(names, "Names");

    final Checker ec = new Checker(names);
    e.expressionVisitableAccept(ec);
    return ec.getFoundNames();
  }

  private Occurences()
  {
    throw new UnreachableCodeException();
  }
}
