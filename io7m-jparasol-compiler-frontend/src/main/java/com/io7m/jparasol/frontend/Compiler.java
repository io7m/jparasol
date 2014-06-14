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

package com.io7m.jparasol.frontend;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;

import com.io7m.jfunctional.OptionType;
import com.io7m.jlog.LogLevel;
import com.io7m.jlog.LogUsableType;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jparasol.CompilerError;
import com.io7m.jparasol.core.GVersionES;
import com.io7m.jparasol.core.GVersionFull;
import com.io7m.jparasol.core.UncompactedFragmentShader;
import com.io7m.jparasol.core.UncompactedProgramShaderMeta;
import com.io7m.jparasol.core.UncompactedVertexShader;
import com.io7m.jparasol.glsl.compactor.GCompactor;
import com.io7m.jparasol.glsl.compactor.GCompactorException;
import com.io7m.jparasol.glsl.pipeline.GCompilation;
import com.io7m.jparasol.glsl.pipeline.GCompiledFragmentShader;
import com.io7m.jparasol.glsl.pipeline.GCompiledProgram;
import com.io7m.jparasol.glsl.pipeline.GCompiledVertexShader;
import com.io7m.jparasol.glsl.pipeline.GPipeline;
import com.io7m.jparasol.glsl.serialization.GSerializerType;
import com.io7m.jparasol.pipeline.CorePipeline;
import com.io7m.jparasol.pipeline.FileInput;
import com.io7m.jparasol.typed.ast.TASTCompilation;
import com.io7m.jparasol.typed.ast.TASTShaderNameFlat;

/**
 * Mindlessly simple compiler frontend.
 */

@SuppressWarnings("boxing") public final class Compiler
{
  private boolean                       compact;
  private final ExecutorService         exec;
  private boolean                       generate_code;
  private final LogUsableType           log;
  private final SortedSet<GVersionES>   required_es;
  private final SortedSet<GVersionFull> required_full;
  private @Nullable GSerializerType     serializer;

  /**
   * Construct a new compiler.
   * 
   * @param in_log
   *          The log.
   * @param in_exec
   *          The executor.
   * @return A new compiler.
   */

  public static Compiler newCompiler(
    final LogUsableType in_log,
    final ExecutorService in_exec)
  {
    return new Compiler(in_log, in_exec);
  }

  private Compiler(
    final LogUsableType in_log,
    final ExecutorService in_exec)
  {
    this.log = NullCheck.notNull(in_log, "Log");
    this.exec = NullCheck.notNull(in_exec, "Executor");

    this.required_es = new TreeSet<GVersionES>();
    this.required_full = new TreeSet<GVersionFull>();
    this.compact = true;
    this.generate_code = false;
  }

  /**
   * @return The required GLSL ES versions.
   */

  public SortedSet<GVersionES> getRequiredES()
  {
    return this.required_es;
  }

  /**
   * @return The required GLSL versions.
   */

  public SortedSet<GVersionFull> getRequiredFull()
  {
    return this.required_full;
  }

  /**
   * @return <code>true</code> if the compiler will compact code.
   */

  public boolean isCompacting()
  {
    return this.compact;
  }

  /**
   * @return <code>true</code> if the compiler will generate code.
   */

  public boolean isGeneratingCode()
  {
    return this.generate_code;
  }

  private TASTCompilation runCompile(
    final List<File> sources)
    throws FileNotFoundException,
      CompilerError,
      IOException
  {
    this.log.debug("starting compilation");
    final double started = System.nanoTime();

    final CorePipeline pipe = CorePipeline.newPipeline(this.log);
    pipe.pipeAddStandardLibrary();

    for (final File file : sources) {
      assert file != null;
      @SuppressWarnings("resource") final FileInput input =
        new FileInput(false, file, new FileInputStream(file));
      pipe.pipeAddInput(input);
    }

    final TASTCompilation typed = pipe.pipeCompile();
    pipe.pipeClose();

    if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
      final double ended = System.nanoTime();
      final double seconds = (ended - started) / 1000000000.0;
      final String s = String.format("compilation complete in %fs", seconds);
      assert s != null;
      this.log.debug(s);
    }

    return typed;
  }

  private void runCompileGenerateGLSL(
    final CompilerBatch batch,
    final TASTCompilation typed,
    final GSerializerType serializer_actual)
    throws CompilerError,
      IOException,
      GCompactorException
  {
    final GPipeline pipe = GPipeline.newPipeline(typed, this.exec, this.log);
    final GCompilation results = this.runGLSLTransform(batch, pipe);
    this.runGLSLSerializeVertexShaders(serializer_actual, results);
    this.runGLSLSerializeFragmentShaders(serializer_actual, results);
    this.runGLSLSerializeProgramShaders(batch, serializer_actual, results);
  }

  /**
   * Compile all of the given source files, generating shaders according to
   * the given batch.
   * 
   * @param batch
   *          The batch.
   * @param sources
   *          The list of source files.
   * 
   * @throws IOException
   *           On I/O errors.
   * @throws CompilerError
   *           On compilation errors.
   * @throws JPFrontendMissingSerializer
   *           Attempted to generate code without a serializer.
   * @throws GCompactorException
   *           On compaction errors.
   */

  public void runForFiles(
    final CompilerBatch batch,
    final List<File> sources)
    throws CompilerError,
      IOException,
      JPFrontendMissingSerializer,
      GCompactorException
  {
    final TASTCompilation typed = this.runCompile(sources);

    if (this.generate_code) {
      final GSerializerType s = this.serializer;
      if (s == null) {
        throw new JPFrontendMissingSerializer("Serializer is unset");
      }
      this.runCompileGenerateGLSL(batch, typed, s);
    }
  }

  private void runGLSLSerializeFragmentShaders(
    final GSerializerType serializer_actual,
    final GCompilation results)
    throws IOException,
      GCompactorException
  {
    {
      final Map<TASTShaderNameFlat, GCompiledFragmentShader> shaders =
        results.getShadersFragment();

      {
        final String s =
          String.format(
            "starting serialization of %d fragment shaders",
            shaders.size());
        assert s != null;
        this.log.debug(s);
      }

      final double started = System.nanoTime();

      for (final TASTShaderNameFlat name : shaders.keySet()) {
        final GCompiledFragmentShader shader = shaders.get(name);
        final UncompactedFragmentShader flat =
          shader.flatten(this.log.with("flatten"));

        if (this.compact) {
          serializer_actual
            .serializeCompactedFragmentShader(GCompactor
              .compactSerializedFragmentShader(
                flat,
                this.log.with("compactor")));
        } else {
          serializer_actual.serializeUncompactedFragmentShader(flat);
        }
      }

      if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
        final double ended = System.nanoTime();
        final double seconds = (ended - started) / 1000000000.0;
        final String s =
          String.format(
            "serialization of %d fragment shaders complete in %fs",
            shaders.size(),
            seconds);
        assert s != null;
        this.log.debug(s);
      }
    }
  }

  private void runGLSLSerializeProgramShaders(
    final CompilerBatch batch,
    final GSerializerType serializer_actual,
    final GCompilation results)
    throws IOException
  {
    {
      final Map<TASTShaderNameFlat, GCompiledProgram> shaders =
        results.getShadersProgram();

      {
        final String s =
          String.format(
            "starting serialization of %d fragment shaders",
            shaders.size());
        assert s != null;
        this.log.debug(s);
      }

      final double started = System.nanoTime();

      final SortedMap<TASTShaderNameFlat, String> outputs =
        batch.getOutputsByShader();
      final OptionType<String> none = com.io7m.jfunctional.Option.none();

      for (final TASTShaderNameFlat name : shaders.keySet()) {
        final GCompiledProgram program = shaders.get(name);
        final UncompactedProgramShaderMeta flat = program.flatten(this.log);

        if (outputs.containsKey(name)) {
          final String output = outputs.get(name);
          assert output != null;

          serializer_actual.serializeUncompactedProgramShader(
            flat,
            com.io7m.jfunctional.Option.some(output));
        } else {
          serializer_actual.serializeUncompactedProgramShader(flat, none);
        }
      }

      if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
        final double ended = System.nanoTime();
        final double seconds = (ended - started) / 1000000000.0;
        final String s =
          String.format(
            "serialization of %d program shaders complete in %fs",
            shaders.size(),
            seconds);
        assert s != null;
        this.log.debug(s);
      }
    }
  }

  private void runGLSLSerializeVertexShaders(
    final GSerializerType serializer_actual,
    final GCompilation results)
    throws IOException,
      GCompactorException
  {
    {
      final Map<TASTShaderNameFlat, GCompiledVertexShader> shaders =
        results.getShadersVertex();

      {
        final String s =
          String.format(
            "starting serialization of %d vertex shaders",
            shaders.size());
        assert s != null;
        this.log.debug(s);
      }

      final double started = System.nanoTime();

      for (final TASTShaderNameFlat name : shaders.keySet()) {
        final GCompiledVertexShader shader = shaders.get(name);
        final UncompactedVertexShader flat =
          shader.flatten(this.log.with("flatten"));

        if (this.compact) {
          serializer_actual.serializeCompactedVertexShader(GCompactor
            .compactSerializedVertexShader(flat, this.log.with("compactor")));
        } else {
          serializer_actual.serializeUncompactedVertexShader(flat);
        }
      }

      if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
        final double ended = System.nanoTime();
        final double seconds = (ended - started) / 1000000000.0;
        final String s =
          String.format(
            "serialization of %d vertex shaders complete in %fs",
            shaders.size(),
            seconds);
        assert s != null;
        this.log.debug(s);
      }
    }
  }

  private GCompilation runGLSLTransform(
    final CompilerBatch batch,
    final GPipeline pipe)
    throws CompilerError
  {
    final GCompilation results;
    {
      final SortedSet<TASTShaderNameFlat> shaders = batch.getShaders();

      {
        final String s =
          String.format(
            "starting GLSL transform of %d programs",
            shaders.size());
        assert s != null;
        this.log.debug(s);
      }

      final double started = System.nanoTime();

      results =
        pipe.transformPrograms(shaders, this.required_es, this.required_full);

      if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
        final double ended = System.nanoTime();
        final double seconds = (ended - started) / 1000000000.0;
        final String s =
          String.format(
            "GLSL transform of %d programs complete in %fs",
            shaders.size(),
            seconds);
        assert s != null;
        this.log.debug(s);
      }
    }
    return results;
  }

  /**
   * Set whether or not the compiler will compact code.
   * 
   * @param c
   *          <code>true</code> If the compiler should compact code.
   */

  public void setCompacting(
    final boolean c)
  {
    this.compact = c;
  }

  /**
   * Set whether or not the compiler will generate code.
   * 
   * @param c
   *          <code>true</code> If the compiler should generate code.
   */

  public void setGeneratingCode(
    final boolean c)
  {
    this.generate_code = c;
  }

  /**
   * Set the required GLSL ES versions.
   * 
   * @param v
   *          The versions.
   */

  public void setRequiredES(
    final SortedSet<GVersionES> v)
  {
    NullCheck.notNull(v, "Versions");
    this.required_es.clear();
    this.required_es.addAll(v);
  }

  /**
   * Set the required GLSL versions.
   * 
   * @param v
   *          The versions.
   */

  public void setRequiredFull(
    final SortedSet<GVersionFull> v)
  {
    NullCheck.notNull(v, "Versions");
    this.required_full.clear();
    this.required_full.addAll(v);
  }

  /**
   * Set the serializer.
   * 
   * @param s
   *          The serializer.
   */

  public void setSerializer(
    final GSerializerType s)
  {
    this.serializer = NullCheck.notNull(s, "Serializer");
  }
}
