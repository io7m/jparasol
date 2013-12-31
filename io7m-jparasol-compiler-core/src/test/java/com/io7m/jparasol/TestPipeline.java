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

package com.io7m.jparasol;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jlog.Log;
import com.io7m.jparasol.lexer.Lexer;
import com.io7m.jparasol.lexer.LexerError;
import com.io7m.jparasol.lexer.Position;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.lexer.Token.TokenIdentifierUpper;
import com.io7m.jparasol.parser.Parser;
import com.io7m.jparasol.parser.ParserError;
import com.io7m.jparasol.parser.ParserTest;
import com.io7m.jparasol.typed.Externals;
import com.io7m.jparasol.typed.ExternalsError;
import com.io7m.jparasol.typed.TypeChecker;
import com.io7m.jparasol.typed.TypeCheckerError;
import com.io7m.jparasol.typed.ast.TASTCompilation;
import com.io7m.jparasol.typed.ast.TASTDeclaration.TASTDModule;
import com.io7m.jparasol.untyped.ModuleStructure;
import com.io7m.jparasol.untyped.ModuleStructureError;
import com.io7m.jparasol.untyped.Resolver;
import com.io7m.jparasol.untyped.ResolverError;
import com.io7m.jparasol.untyped.UniqueBinders;
import com.io7m.jparasol.untyped.UniqueBindersError;
import com.io7m.jparasol.untyped.UnitCombinerError;
import com.io7m.jparasol.untyped.ast.checked.UASTCCompilation;
import com.io7m.jparasol.untyped.ast.initial.UASTICompilation;
import com.io7m.jparasol.untyped.ast.initial.UASTIUnit;
import com.io7m.jparasol.untyped.ast.resolved.UASTRCompilation;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUCompilation;

public final class TestPipeline
{
  public static TASTCompilation externalChecked(
    final String[] names)
    throws ExternalsError
  {
    try {
      final Log log = TestUtilities.getLog();
      final TASTCompilation typed = TestPipeline.typed(names);
      final Externals ec = Externals.newExternalsChecker(log);
      ec.check(typed);
      return typed;
    } catch (final ConstraintError e) {
      e.printStackTrace();
      throw new UnreachableCodeException(e);
    } catch (final TypeCheckerError e) {
      e.printStackTrace();
      throw new UnreachableCodeException(e);
    }
  }

  public static TASTCompilation externalCheckedInternal(
    final String[] names)
    throws ExternalsError
  {
    try {
      final Log log = TestUtilities.getLog();
      final TASTCompilation typed = TestPipeline.typedInternal(names);
      final Externals ec = Externals.newExternalsChecker(log);
      ec.check(typed);
      return typed;
    } catch (final ConstraintError e) {
      e.printStackTrace();
      throw new UnreachableCodeException(e);
    } catch (final TypeCheckerError e) {
      e.printStackTrace();
      throw new UnreachableCodeException(e);
    }
  }

  public static UASTCCompilation checked(
    final String[] names)
    throws ConstraintError,
      ModuleStructureError
  {
    try {
      final Log log = TestUtilities.getLog();
      final UASTICompilation c = TestPipeline.combined(names);
      final ModuleStructure mc =
        ModuleStructure.newModuleStructureChecker(c, log);
      return mc.check();
    } catch (final UnitCombinerError e) {
      e.printStackTrace();
      throw new UnreachableCodeException(e);
    }
  }

  public static UASTCCompilation checkedInternal(
    final String[] names)
    throws ConstraintError,
      ModuleStructureError
  {
    try {
      final Log log = TestUtilities.getLog();
      final UASTICompilation c = TestPipeline.combinedInternal(names);
      final ModuleStructure mc =
        ModuleStructure.newModuleStructureChecker(c, log);
      return mc.check();
    } catch (final UnitCombinerError e) {
      e.printStackTrace();
      throw new UnreachableCodeException(e);
    }
  }

  public static UASTICompilation combined(
    final String[] names)
    throws UnitCombinerError,
      ConstraintError
  {
    try {
      final List<UASTIUnit> units = TestPipeline.parseUnits(names);
      final UASTICompilation initial = UASTICompilation.fromUnits(units);
      return initial;
    } catch (final LexerError e) {
      e.printStackTrace();
      throw new UnreachableCodeException(e);
    } catch (final ParserError e) {
      e.printStackTrace();
      throw new UnreachableCodeException(e);
    } catch (final IOException e) {
      e.printStackTrace();
      throw new UnreachableCodeException(e);
    }
  }

  public static UASTICompilation combinedInternal(
    final String[] names)
    throws UnitCombinerError,
      ConstraintError
  {
    try {
      final List<UASTIUnit> units = TestPipeline.parseUnitsInternal(names);
      final UASTICompilation initial = UASTICompilation.fromUnits(units);
      return initial;
    } catch (final LexerError e) {
      e.printStackTrace();
      throw new UnreachableCodeException(e);
    } catch (final ParserError e) {
      e.printStackTrace();
      throw new UnreachableCodeException(e);
    } catch (final IOException e) {
      e.printStackTrace();
      throw new UnreachableCodeException(e);
    }
  }

  public static TASTDModule getModule(
    final @Nonnull TASTCompilation comp,
    final @Nonnull String pp,
    final @Nonnull String name)
  {
    try {
      final ModulePath path = TestPipeline.getModuleMakePath(pp, name);
      final ModulePathFlat flat = ModulePathFlat.fromModulePath(path);
      return comp.getModules().get(flat);
    } catch (final ConstraintError x) {
      throw new UnreachableCodeException(x);
    }
  }

  public static @Nonnull ModulePath getModuleMakePath(
    final @Nonnull String pp,
    final @Nonnull String name)
    throws ConstraintError
  {
    final String[] segments = pp.split("\\.");
    final ArrayList<TokenIdentifierLower> tokens =
      new ArrayList<TokenIdentifierLower>();

    final File file = new File("<stdin>");
    final Position pos = new Position(0, 0);
    for (final String segment : segments) {
      tokens.add(new TokenIdentifierLower(file, pos, segment));
    }

    final TokenIdentifierUpper tname =
      new TokenIdentifierUpper(file, pos, name);
    return new ModulePath(new PackagePath(tokens), tname);
  }

  @SuppressWarnings("resource") public static UASTIUnit parseUnit(
    final String name,
    final boolean internal)
    throws LexerError,
      IOException,
      ConstraintError,
      ParserError
  {
    final InputStream is =
      ParserTest.class.getResourceAsStream("/com/io7m/jparasol/" + name);
    final Lexer lexer = new Lexer(is);
    if (internal) {
      final Parser p = Parser.newInternalParser(lexer);
      return p.unit();
    }
    final Parser p = Parser.newParser(lexer);
    return p.unit();
  }

  public static List<UASTIUnit> parseUnits(
    final String[] names)
    throws LexerError,
      ParserError,
      IOException,
      ConstraintError
  {
    final List<UASTIUnit> units = new ArrayList<UASTIUnit>();

    for (final String name : names) {
      units.add(TestPipeline.parseUnit(name, false));
    }

    return units;
  }

  public static List<UASTIUnit> parseUnitsInternal(
    final String[] names)
    throws LexerError,
      ParserError,
      IOException,
      ConstraintError
  {
    final List<UASTIUnit> units = new ArrayList<UASTIUnit>();

    for (final String name : names) {
      units.add(TestPipeline.parseUnit(name, true));
    }

    return units;
  }

  public static UASTRCompilation resolved(
    final String[] names)
    throws ConstraintError,
      ResolverError
  {
    try {
      final Log log = TestUtilities.getLog();
      final UASTUCompilation unique = TestPipeline.unique(names);
      final Resolver nr = Resolver.newResolver(unique, log);
      return nr.run();
    } catch (final UniqueBindersError e) {
      e.printStackTrace();
      throw new UnreachableCodeException(e);
    }
  }

  public static UASTRCompilation resolvedInternal(
    final String[] names)
    throws ConstraintError,
      ResolverError
  {
    try {
      final Log log = TestUtilities.getLog();
      final UASTUCompilation unique = TestPipeline.uniqueInternal(names);
      final Resolver nr = Resolver.newResolver(unique, log);
      return nr.run();
    } catch (final UniqueBindersError e) {
      e.printStackTrace();
      throw new UnreachableCodeException(e);
    }
  }

  public static TASTCompilation typed(
    final String[] names)
    throws TypeCheckerError
  {
    try {
      final Log log = TestUtilities.getLog();
      final UASTRCompilation resolved = TestPipeline.resolved(names);
      final TypeChecker tc = TypeChecker.newTypeChecker(resolved, log);
      return tc.check();
    } catch (final ResolverError e) {
      e.printStackTrace();
      throw new UnreachableCodeException(e);
    } catch (final ConstraintError e) {
      e.printStackTrace();
      throw new UnreachableCodeException(e);
    }
  }

  public static TASTCompilation typedInternal(
    final String[] names)
    throws TypeCheckerError
  {
    try {
      final Log log = TestUtilities.getLog();
      final UASTRCompilation resolved = TestPipeline.resolvedInternal(names);
      final TypeChecker tc = TypeChecker.newTypeChecker(resolved, log);
      return tc.check();
    } catch (final ResolverError e) {
      e.printStackTrace();
      throw new UnreachableCodeException(e);
    } catch (final ConstraintError e) {
      e.printStackTrace();
      throw new UnreachableCodeException(e);
    }
  }

  public static UASTUCompilation unique(
    final String[] names)
    throws UniqueBindersError
  {
    try {
      final Log log = TestUtilities.getLog();
      final UASTCCompilation checked = TestPipeline.checked(names);
      final UniqueBinders ub = UniqueBinders.newUniqueBinders(checked, log);
      return ub.run();
    } catch (final ModuleStructureError e) {
      e.printStackTrace();
      throw new UnreachableCodeException(e);
    } catch (final ConstraintError e) {
      e.printStackTrace();
      throw new UnreachableCodeException(e);
    }
  }

  public static UASTUCompilation uniqueInternal(
    final String[] names)
    throws UniqueBindersError
  {
    try {
      final Log log = TestUtilities.getLog();
      final UASTCCompilation checked = TestPipeline.checkedInternal(names);
      final UniqueBinders ub = UniqueBinders.newUniqueBinders(checked, log);
      return ub.run();
    } catch (final ModuleStructureError e) {
      e.printStackTrace();
      throw new UnreachableCodeException(e);
    } catch (final ConstraintError e) {
      e.printStackTrace();
      throw new UnreachableCodeException(e);
    }
  }
}
