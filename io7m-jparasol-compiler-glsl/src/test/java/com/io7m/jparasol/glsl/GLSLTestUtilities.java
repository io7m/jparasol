package com.io7m.jparasol.glsl;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jlog.Log;
import com.io7m.jparasol.ModulePath;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.PackagePath;
import com.io7m.jparasol.lexer.Lexer;
import com.io7m.jparasol.lexer.Position;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.lexer.Token.TokenIdentifierUpper;
import com.io7m.jparasol.parser.Parser;
import com.io7m.jparasol.typed.TypeChecker;
import com.io7m.jparasol.typed.TypeCheckerError;
import com.io7m.jparasol.typed.ast.TASTCompilation;
import com.io7m.jparasol.typed.ast.TASTRDeclaration.TASTDModule;
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

public final class GLSLTestUtilities
{
  static TASTCompilation typed(
    final String[] names)
  {
    TypeChecker tc;

    try {
      final Log log = GLSLTestUtilities.getLog();
      final List<UASTIUnit> units = GLSLTestUtilities.parseUnits(names);
      final UASTICompilation initial = UASTICompilation.fromUnits(units);
      final ModuleStructure mc =
        ModuleStructure.newModuleStructureChecker(initial, log);
      final UASTCCompilation checked = mc.check();
      final UniqueBinders ub = UniqueBinders.newUniqueBinders(checked, log);
      final UASTUCompilation unique = ub.run();
      final Resolver nr = Resolver.newResolver(unique, log);
      final UASTRCompilation resolved = nr.run();
      tc = TypeChecker.newTypeChecker(resolved, log);
      return tc.check();
    } catch (final ConstraintError e) {
      e.printStackTrace();
      throw new UnreachableCodeException(e);
    } catch (final UniqueBindersError e) {
      e.printStackTrace();
      throw new UnreachableCodeException(e);
    } catch (final UnitCombinerError e) {
      e.printStackTrace();
      throw new UnreachableCodeException(e);
    } catch (final ModuleStructureError e) {
      e.printStackTrace();
      throw new UnreachableCodeException(e);
    } catch (final ResolverError e) {
      e.printStackTrace();
      throw new UnreachableCodeException(e);
    } catch (final TypeCheckerError e) {
      e.printStackTrace();
      throw new UnreachableCodeException(e);
    }
  }

  public static Log getLog()
  {
    final Properties properties = new Properties();
    return new Log(properties, "com.io7m.jparasol", "tests");
  }

  static TASTCompilation typedInternal(
    final String[] names)
  {
    TypeChecker tc;

    try {
      final Log log = GLSLTestUtilities.getLog();
      final List<UASTIUnit> units =
        GLSLTestUtilities.parseUnitsInternal(names);
      final UASTICompilation initial = UASTICompilation.fromUnits(units);
      final ModuleStructure mc =
        ModuleStructure.newModuleStructureChecker(initial, log);
      final UASTCCompilation checked = mc.check();
      final UniqueBinders ub = UniqueBinders.newUniqueBinders(checked, log);
      final UASTUCompilation unique = ub.run();
      final Resolver nr = Resolver.newResolver(unique, log);
      final UASTRCompilation resolved = nr.run();
      tc = TypeChecker.newTypeChecker(resolved, log);
      return tc.check();
    } catch (final ConstraintError e) {
      e.printStackTrace();
      throw new UnreachableCodeException(e);
    } catch (final UniqueBindersError e) {
      e.printStackTrace();
      throw new UnreachableCodeException(e);
    } catch (final UnitCombinerError e) {
      e.printStackTrace();
      throw new UnreachableCodeException(e);
    } catch (final ModuleStructureError e) {
      e.printStackTrace();
      throw new UnreachableCodeException(e);
    } catch (final ResolverError e) {
      e.printStackTrace();
      throw new UnreachableCodeException(e);
    } catch (final TypeCheckerError e) {
      e.printStackTrace();
      throw new UnreachableCodeException(e);
    }
  }

  static TASTDModule getModule(
    final @Nonnull TASTCompilation comp,
    final @Nonnull String pp,
    final @Nonnull String name)
    throws ConstraintError
  {
    final ModulePath path = GLSLTestUtilities.getModuleMakePath(pp, name);
    final ModulePathFlat flat = ModulePathFlat.fromModulePath(path);
    return comp.getModules().get(flat);
  }

  static @Nonnull ModulePath getModuleMakePath(
    final @Nonnull String pp,
    final @Nonnull String name)
    throws ConstraintError
  {
    final String[] segments = pp.split("\\.");
    final ArrayList<TokenIdentifierLower> tokens =
      new ArrayList<TokenIdentifierLower>();

    final File file = new File("<stdin>");
    final Position pos = new Position(0, 0);
    for (int index = 0; index < segments.length; ++index) {
      tokens.add(new TokenIdentifierLower(file, pos, segments[index]));
    }

    final TokenIdentifierUpper tname =
      new TokenIdentifierUpper(file, pos, name);
    return new ModulePath(new PackagePath(tokens), tname);
  }

  @SuppressWarnings("resource") static UASTIUnit parseUnit(
    final String name,
    final boolean internal)
  {
    try {
      final InputStream is =
        GLSLTestUtilities.class
          .getResourceAsStream("/com/io7m/jparasol/glsl/" + name);
      final Lexer lexer = new Lexer(is);
      if (internal) {
        final Parser p = Parser.newInternalParser(lexer);
        return p.unit();
      }
      final Parser p = Parser.newParser(lexer);
      return p.unit();
    } catch (final Throwable x) {
      x.printStackTrace();
      System.err.println("UNREACHABLE: " + x);
      throw new UnreachableCodeException(x);
    }
  }

  public static List<UASTIUnit> parseUnits(
    final String[] names)
  {
    final List<UASTIUnit> units = new ArrayList<UASTIUnit>();

    for (final String name : names) {
      units.add(GLSLTestUtilities.parseUnit(name, false));
    }

    return units;
  }

  public static List<UASTIUnit> parseUnitsInternal(
    final String[] names)
  {
    final List<UASTIUnit> units = new ArrayList<UASTIUnit>();

    for (final String name : names) {
      units.add(GLSLTestUtilities.parseUnit(name, true));
    }

    return units;
  }
}
