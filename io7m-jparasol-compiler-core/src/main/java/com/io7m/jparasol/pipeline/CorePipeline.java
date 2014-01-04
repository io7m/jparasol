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

package com.io7m.jparasol.pipeline;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jlog.Level;
import com.io7m.jlog.Log;
import com.io7m.jparasol.lexer.Lexer;
import com.io7m.jparasol.lexer.LexerError;
import com.io7m.jparasol.parser.Parser;
import com.io7m.jparasol.parser.ParserError;
import com.io7m.jparasol.typed.TypeChecker;
import com.io7m.jparasol.typed.TypeCheckerError;
import com.io7m.jparasol.typed.ast.TASTCompilation;
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

/**
 * A compilation pipeline for the core language. Produces a typed AST as
 * output.
 */

public final class CorePipeline
{
  private static final @Nonnull HashSet<String> STANDARD_LIBRARY;

  static {
    STANDARD_LIBRARY = new HashSet<String>();
    CorePipeline.STANDARD_LIBRARY.add("Float.p");
    CorePipeline.STANDARD_LIBRARY.add("Integer.p");
    CorePipeline.STANDARD_LIBRARY.add("Matrix3x3f.p");
    CorePipeline.STANDARD_LIBRARY.add("Matrix4x4f.p");
    CorePipeline.STANDARD_LIBRARY.add("Sampler2D.p");
    CorePipeline.STANDARD_LIBRARY.add("SamplerCube.p");
    CorePipeline.STANDARD_LIBRARY.add("Vector2f.p");
    CorePipeline.STANDARD_LIBRARY.add("Vector2i.p");
    CorePipeline.STANDARD_LIBRARY.add("Vector3f.p");
    CorePipeline.STANDARD_LIBRARY.add("Vector3i.p");
    CorePipeline.STANDARD_LIBRARY.add("Vector4f.p");
    CorePipeline.STANDARD_LIBRARY.add("Vector4i.p");
  }

  public static @Nonnull CorePipeline newPipeline(
    final @Nonnull Log log)
  {
    return new CorePipeline(log);
  }

  private final @Nonnull ArrayList<Input> inputs;
  private final @Nonnull Log              log;

  private CorePipeline(
    final @Nonnull Log log)
  {
    this.log = new Log(log, "pipeline");
    this.inputs = new ArrayList<Input>();
  }

  public void pipeAddInput(
    final @Nonnull Input input)
  {
    if (this.log.enabled(Level.LOG_DEBUG)) {
      final String internal = input.isInternal() ? "internal " : "";
      this.log.debug(String.format(
        "Added %sinput %s",
        internal,
        input.getFile()));
    }
    this.inputs.add(input);
  }

  public void pipeAddStandardLibrary()
  {
    try {
      for (final String b : CorePipeline.STANDARD_LIBRARY) {
        final String p = "/com/io7m/jparasol/" + b;
        @SuppressWarnings("resource") final InputStream s =
          CorePipeline.class.getResourceAsStream(p);
        assert s != null;
        final File f = new File(p);
        this.pipeAddInput(new FileInput(true, f, s));
      }
    } catch (final ConstraintError x) {
      throw new UnreachableCodeException(x);
    }
  }

  public @Nonnull TASTCompilation pipeCompile()
    throws LexerError,
      IOException,
      ConstraintError,
      ParserError,
      UnitCombinerError,
      ModuleStructureError,
      UniqueBindersError,
      TypeCheckerError,
      ResolverError
  {
    final ArrayList<UASTIUnit> units = new ArrayList<UASTIUnit>();
    for (final Input i : this.inputs) {
      final Lexer lexer = new Lexer(i.getStream());
      lexer.setFile(i.getFile());

      Parser parser;
      if (i.isInternal()) {
        parser = Parser.newInternalParser(lexer);
      } else {
        parser = Parser.newParser(lexer);
      }

      final UASTIUnit u = parser.unit();
      units.add(u);
    }

    final UASTICompilation combined = UASTICompilation.fromUnits(units);
    final ModuleStructure structure_checker =
      ModuleStructure.newModuleStructureChecker(combined, this.log);
    final UASTCCompilation checked = structure_checker.check();

    final UniqueBinders binder =
      UniqueBinders.newUniqueBinders(checked, this.log);
    final UASTUCompilation bound = binder.run();

    final Resolver resolver = Resolver.newResolver(bound, this.log);
    final UASTRCompilation resolved = resolver.run();

    final TypeChecker type_checker =
      TypeChecker.newTypeChecker(resolved, this.log);
    return type_checker.check();
  }

  public void pipeClose()
    throws IOException
  {
    for (final Input i : this.inputs) {
      i.getStream().close();
    }
  }
}
