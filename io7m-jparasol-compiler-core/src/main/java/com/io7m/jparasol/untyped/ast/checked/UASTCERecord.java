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
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITWHETHER IN AN ACTION
 * OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN
 * CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.jparasol.untyped.ast.checked;

// CHECKSTYLE_JAVADOC:OFF

import java.util.List;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jnull.NullCheck;

@EqualityReference public final class UASTCERecord implements
  UASTCExpressionType
{
  private final List<UASTCRecordFieldAssignment> assignments;
  private final UASTCTypePath                    type_path;

  public UASTCERecord(
    final UASTCTypePath in_type_path,
    final List<UASTCRecordFieldAssignment> in_assignments)
  {
    this.type_path = NullCheck.notNull(in_type_path, "Type path");
    this.assignments = NullCheck.notNull(in_assignments, "Assignments");
  }

  @Override public
    <A, C, L, E extends Throwable, V extends UASTCExpressionVisitorType<A, C, L, E>>
    A
    expressionVisitableAccept(
      final V v)
      throws E
  {
    return v.expressionVisitRecord(this);
  }

  public List<UASTCRecordFieldAssignment> getAssignments()
  {
    return this.assignments;
  }

  public UASTCTypePath getTypePath()
  {
    return this.type_path;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[UASTCERecord ");
    builder.append(this.type_path);
    builder.append(" [\n");
    for (final UASTCRecordFieldAssignment a : this.assignments) {
      builder.append("  ");
      builder.append(a);
      builder.append("\n");
    }
    builder.append("]");
    return builder.toString();
  }
}
