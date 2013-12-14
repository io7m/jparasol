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

package com.io7m.jparasol.untyped;

import javax.annotation.Nonnull;

import com.io7m.jlog.Log;
import com.io7m.jparasol.NameRestrictions;

/**
 * Reject insane modules. Specifically, reject:
 * 
 * For modules:
 * 
 * <ul>
 * <li>Module names that are restricted, according to {@link NameRestrictions}
 * .</li>
 * </ul>
 * 
 * For imports:
 * 
 * <ul>
 * <li>Duplicate imports (<code>import x.Y; import x.Y;</code>)</li>
 * <li>Duplicate renames for imports (
 * <code>import x.Y as A; import x.Z as A;</code>)</li>
 * <li>Imports that conflict with renames (
 * <code>import x.Y as A; import x.A;</code>)</li>
 * <li>Redundant import renames (<code>import x.Y as Y;</code>)</li>
 * </ul>
 * 
 * For terms/types in modules:
 * 
 * <ul>
 * <li>Terms that have names that are restricted, according to
 * {@link NameRestrictions}.</li>
 * <li>Multiple function declarations with the same name (
 * <code>function f ...; function f ...;</code>)</li>
 * <li>Multiple value declarations with the same name in the same scope (
 * <code>value x = ...; value x = ...;</code>)</li>
 * <li>Multiple function arguments with the same name (
 * <code>function f (x : int, x : int)</code>)</li>
 * <li>Record expressions with duplicate field names (
 * <code>{ x = 23, x = 23 }</code>)</li>
 * </ul>
 * 
 * For types in modules:
 * 
 * <ul>
 * <li>Multiple type declarations with the same name (
 * <code>type t ...; type t ...;</code>)</li>
 * <li>Type declarations with names matching any of those of the built-in
 * types</li>
 * <li>Record type declarations with duplicate field names (
 * <code>type t is record x : int, x : int end</code>)</li>
 * </ul>
 * 
 * For shaders in modules:
 * 
 * <ul>
 * <li>Shaders with names that are restricted, according to
 * <code>NameRestrictions</code>.</li>
 * <li>Shaders with inputs, outputs, or parameters with names that are
 * restricted, according to {@link NameRestrictions}.</li>
 * <li>Multiple shader declarations with the same name (
 * <code>shader vertex v is ...; shader fragment v is ...;</code>)</li>
 * <li>Multiple input, output, or parameters with the same name</li>
 * <li>Multiple local values with the same name</li>
 * <li>Multiple output assignments to the same name</li>
 * <li>Missing output assignments</li>
 * </ul>
 * 
 * For vertex shaders:
 * 
 * <ul>
 * <li>Require that exactly one output assignment exists to the built-in
 * output <code>gl_Position</code></li>
 * </ul>
 * 
 * For fragment shaders:
 * 
 * <ul>
 * <li>Multiple outputs with the same index (
 * <code>out out0 : vector4f as 0; out out1 : vector4f as 0;</code>)</li>
 * <li>Outputs with discontinuous indices (
 * <code>out out0 : vector4f as 2; out out1 : vector4f as 0;</code>)</li>
 * <li>Outputs with negative indices</li>
 * </ul>
 * 
 * For units:
 * 
 * <ul>
 * <li>Modules that import themselves (
 * <code>package x.y; module K is import x.y.K; end</code>)</li>
 * </ul>
 */

public final class ModuleStructure
{
  public static @Nonnull ModuleStructure newModuleStructureChecker(
    final @Nonnull Log log)
  {
    return new ModuleStructure(log);
  }

  private final @Nonnull Log log;

  public ModuleStructure(
    final @Nonnull Log log)
  {
    this.log = new Log(log, "module-structure");
  }
}
