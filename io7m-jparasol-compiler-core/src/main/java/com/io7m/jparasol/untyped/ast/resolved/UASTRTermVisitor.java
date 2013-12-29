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

package com.io7m.jparasol.untyped.ast.resolved;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDFunctionDefined;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDFunctionExternal;
import com.io7m.jparasol.untyped.ast.resolved.UASTRDeclaration.UASTRDValue;

public interface UASTRTermVisitor<T, E extends Throwable>
{
  public T termVisitFunctionDefined(
    final @Nonnull UASTRDFunctionDefined f)
    throws E,
      ConstraintError;

  public T termVisitFunctionExternal(
    final @Nonnull UASTRDFunctionExternal f)
    throws E,
      ConstraintError;

  public T termVisitValue(
    final @Nonnull UASTRDValue v)
    throws E,
      ConstraintError;
}
