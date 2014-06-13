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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipOutputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import com.io7m.jfunctional.OptionType;
import com.io7m.jfunctional.Pair;
import com.io7m.jlog.Log;
import com.io7m.jlog.LogLevel;
import com.io7m.jlog.LogPolicyAllOn;
import com.io7m.jlog.LogUsableType;
import com.io7m.jparasol.CompilerError;
import com.io7m.jparasol.UIError;
import com.io7m.jparasol.core.GVersionES;
import com.io7m.jparasol.core.GVersionFull;
import com.io7m.jparasol.core.UncompactedFragmentShader;
import com.io7m.jparasol.core.UncompactedProgramShaderMeta;
import com.io7m.jparasol.core.UncompactedVertexShader;
import com.io7m.jparasol.glsl.GVersionNumberSetLexer;
import com.io7m.jparasol.glsl.GVersionNumberSetParser;
import com.io7m.jparasol.glsl.GVersionNumberSetParser.Segment;
import com.io7m.jparasol.glsl.compactor.GCompactor;
import com.io7m.jparasol.glsl.compactor.GCompactorException;
import com.io7m.jparasol.glsl.pipeline.GCompilation;
import com.io7m.jparasol.glsl.pipeline.GCompiledFragmentShader;
import com.io7m.jparasol.glsl.pipeline.GCompiledProgram;
import com.io7m.jparasol.glsl.pipeline.GCompiledVertexShader;
import com.io7m.jparasol.glsl.pipeline.GPipeline;
import com.io7m.jparasol.glsl.serialization.GSerializerFile;
import com.io7m.jparasol.glsl.serialization.GSerializerType;
import com.io7m.jparasol.glsl.serialization.GSerializerZip;
import com.io7m.jparasol.lexer.LexerError;
import com.io7m.jparasol.lexer.Position;
import com.io7m.jparasol.parser.ParserError;
import com.io7m.jparasol.pipeline.CorePipeline;
import com.io7m.jparasol.pipeline.FileInput;
import com.io7m.jparasol.typed.ast.TASTCompilation;
import com.io7m.jparasol.typed.ast.TASTShaderNameFlat;

/**
 * Command line compiler frontend.
 */

public final class Frontend
{
  private static boolean       DEBUG;
  private static final Options OPTIONS;

  static {
    OPTIONS = Frontend.makeOptions();
  }

  private static void commandCheck(
    final LogUsableType logx,
    final CommandLine line)
    throws IOException,
      CompilerError
  {
    final List<File> sources = new ArrayList<File>();
    for (final String f : line.getArgs()) {
      sources.add(new File(f));
    }

    Frontend.runCompileActualCore(logx, sources);
  }

  private static void commandCompileBatch(
    final LogUsableType logx,
    final ExecutorService exec,
    final CommandLine line)
    throws IOException,
      CompilerError,
      NoSuchAlgorithmException,
      GCompactorException,
      JPBatchException
  {
    final SortedSet<GVersionES> require_es = Frontend.getRequiredES(line);
    final SortedSet<GVersionFull> require_full =
      Frontend.getRequiredFull(line);

    final String[] args = line.getArgs();
    if (args.length < 3) {
      throw UIError.incorrectCommandLine("Too few arguments provided");
    }
    final File output = new File(args[0]);
    final File batch_file = new File(args[1]);
    final File source_file = new File(args[2]);

    final Batch batch = Batch.newBatchFromFile(batch_file);
    final List<File> sources = Frontend.parseSources(source_file);

    GSerializerType serializer;
    if (line.hasOption("zip")) {
      @SuppressWarnings("resource") final ZipOutputStream zip_stream =
        new ZipOutputStream(
          new FileOutputStream(output),
          Charset.forName("UTF-8"));
      serializer = GSerializerZip.newSerializer(zip_stream, logx);
    } else {
      serializer = GSerializerFile.newSerializer(output, true);
    }

    Frontend.runCompileActual(
      logx,
      exec,
      batch,
      sources,
      require_es,
      require_full,
      serializer,
      line.hasOption("compact"));

    serializer.close();
  }

  private static void commandCompileOne(
    final LogUsableType logx,
    final ExecutorService exec,
    final CommandLine line)
    throws IOException,
      CompilerError,
      NoSuchAlgorithmException,
      GCompactorException,
      JPBatchException
  {
    final SortedSet<GVersionES> require_es = Frontend.getRequiredES(line);
    final SortedSet<GVersionFull> require_full =
      Frontend.getRequiredFull(line);

    final String[] args = line.getArgs();
    if (args.length < 3) {
      throw UIError.incorrectCommandLine("Too few arguments provided");
    }

    final File output = new File(args[0]);
    final TASTShaderNameFlat name =
      TASTShaderNameFlat.parse(
        args[1],
        Pair.pair(new File("<stdin>"), Position.ZERO));
    final Batch batch = Batch.newBatch();
    batch.addShader(name);

    final List<File> sources = new ArrayList<File>();
    for (int index = 2; index < args.length; ++index) {
      sources.add(new File(args[index]));
    }

    final GSerializerType serializer;
    if (line.hasOption("zip")) {
      @SuppressWarnings("resource") final ZipOutputStream zip_stream =
        new ZipOutputStream(
          new FileOutputStream(output),
          Charset.forName("UTF-8"));
      serializer = GSerializerZip.newSerializer(zip_stream, logx);
    } else {
      serializer = GSerializerFile.newSerializer(output, true);
    }

    Frontend.runCompileActual(
      logx,
      exec,
      batch,
      sources,
      require_es,
      require_full,
      serializer,
      line.hasOption("compact"));

    serializer.close();
  }

  private static void commandShowCompilerVersion(
    final LogUsableType logx,
    final CommandLine line)
  {
    System.out.println(Frontend.getVersion());
  }

  private static void commandShowGLSLVersions(
    final LogUsableType logx,
    final CommandLine line)
  {
    for (final GVersionFull v : GVersionFull.ALL) {
      System.out.println(v.versionGetLongName());
    }
    for (final GVersionES v : GVersionES.ALL) {
      System.out.println(v.versionGetLongName());
    }
  }

  /**
   * @param debug
   *          <code>true</code> if debug logging should be enabled
   * @return A log interface
   */

  public static LogUsableType getLog(
    final boolean debug)
  {
    return Log.newLog(LogPolicyAllOn.newPolicy(debug
      ? LogLevel.LOG_DEBUG
      : LogLevel.LOG_INFO), "compiler");
  }

  private static SortedSet<GVersionES> getRequiredES(
    final CommandLine line)
    throws LexerError,
      IOException,
      ParserError,
      UIError
  {
    final SortedSet<GVersionES> versions = new TreeSet<GVersionES>();
    if (line.hasOption("require-es")) {
      final String text = line.getOptionValue("require-es");
      final ByteArrayInputStream bs =
        new ByteArrayInputStream(text.getBytes());
      final GVersionNumberSetLexer lexer = new GVersionNumberSetLexer(bs);
      final GVersionNumberSetParser parser =
        new GVersionNumberSetParser(lexer);
      while (parser.isAtEOF() == false) {
        final List<Segment> segments = parser.segments();
        versions.addAll(GVersionNumberSetParser.segmentsSetES(segments));
      }
    }
    return versions;
  }

  private static SortedSet<GVersionFull> getRequiredFull(
    final CommandLine line)
    throws LexerError,
      IOException,
      UIError,
      ParserError
  {
    final SortedSet<GVersionFull> versions = new TreeSet<GVersionFull>();
    if (line.hasOption("require-full")) {
      final String text = line.getOptionValue("require-full");
      final ByteArrayInputStream bs =
        new ByteArrayInputStream(text.getBytes());
      final GVersionNumberSetLexer lexer = new GVersionNumberSetLexer(bs);
      final GVersionNumberSetParser parser =
        new GVersionNumberSetParser(lexer);
      while (parser.isAtEOF() == false) {
        final List<Segment> segments = parser.segments();
        versions.addAll(GVersionNumberSetParser.segmentsSetFull(segments));
      }
    }
    return versions;
  }

  private static String getVersion()
  {
    final String pack =
      Frontend.class.getPackage().getImplementationVersion();
    if (pack == null) {
      return "unavailable";
    }
    return pack;
  }

  private static Options makeOptions()
  {
    final Options opts = new Options();

    {
      final Option o =
        new Option("h", "help", false, "Show this help message");
      opts.addOption(o);
    }

    {
      OptionBuilder.withLongOpt("show-versions");
      OptionBuilder.withDescription("Show the available GLSL versions");
      opts.addOption(OptionBuilder.create());
    }

    {
      final OptionGroup og = new OptionGroup();
      OptionBuilder.withLongOpt("check");
      OptionBuilder
        .withDescription("Parse and type-check all source files, but do not produce GLSL source");
      og.addOption(OptionBuilder.create());
      opts.addOptionGroup(og);
    }

    {
      OptionBuilder.withLongOpt("require-full");
      OptionBuilder.hasArgs(2);
      OptionBuilder.withArgName("version-set");
      OptionBuilder
        .withDescription("Require GLSL source code for the given set of GLSL versions, failing if any of the versions cannot be satisfied");
      opts.addOption(OptionBuilder.create());
    }

    {
      OptionBuilder.withLongOpt("require-es");
      OptionBuilder.hasArgs(2);
      OptionBuilder.withArgName("version-set");
      OptionBuilder
        .withDescription("Require GLSL ES source code for the given set of GLSL ES versions, failing if any of the versions cannot be satisfied");
      opts.addOption(OptionBuilder.create());
    }

    {
      final OptionGroup og = new OptionGroup();
      OptionBuilder.withLongOpt("compile-one");
      OptionBuilder
        .withDescription("Compile a specific shader program to GLSL source");
      og.addOption(OptionBuilder.create());
      opts.addOptionGroup(og);
    }

    {
      final OptionGroup og = new OptionGroup();
      OptionBuilder.withLongOpt("compile-batch");
      OptionBuilder
        .withDescription("Produce multiple GLSL programs from a set of sources");
      og.addOption(OptionBuilder.create());
      opts.addOptionGroup(og);
    }

    {
      final OptionGroup og = new OptionGroup();
      OptionBuilder.withLongOpt("version");
      OptionBuilder.withDescription("Display compiler version");
      og.addOption(OptionBuilder.create());
      opts.addOptionGroup(og);
    }

    {
      OptionBuilder.withLongOpt("debug");
      OptionBuilder
        .withDescription("Enable debugging (debug messages, exception backtraces)");
      opts.addOption(OptionBuilder.create());
    }

    {
      OptionBuilder.withLongOpt("compact");
      OptionBuilder
        .withDescription("Enable compaction (eliminates duplicate source files)");
      opts.addOption(OptionBuilder.create());
    }

    {
      OptionBuilder.withLongOpt("zip");
      OptionBuilder
        .withDescription("Write shaders to a zip archive instead of a directory");
      opts.addOption(OptionBuilder.create());
    }

    {
      OptionBuilder.withLongOpt("threads");
      OptionBuilder.hasArg(true);
      OptionBuilder.withArgName("count");
      OptionBuilder
        .withDescription("Set the number of threads to use during code generation");
      opts.addOption(OptionBuilder.create());
    }

    return opts;
  }

  private static List<File> parseSources(
    final File source_file)
    throws IOException
  {
    final BufferedReader reader =
      new BufferedReader(new FileReader(source_file));
    final List<File> files = new ArrayList<File>();

    try {
      for (;;) {
        final String line = reader.readLine();
        if (line == null) {
          break;
        }

        final File file = new File(line);
        files.add(file);
      }
      return files;
    } finally {
      reader.close();
    }
  }

  /**
   * Run the compiler with the given command line arguments.
   * 
   * @param log
   *          A log interface
   * @param args
   *          Command line arguments
   * @throws Exception
   *           On errors.
   */

  public static void run(
    final LogUsableType log,
    final String[] args)
    throws Exception
  {
    try {
      Frontend.runActual(log, args);
    } catch (final ParseException e) {
      log.error(e.getMessage());
      Frontend.showHelp();
      throw e;
    } catch (final JPBatchException e) {
      log.error("invalid batch: " + e.getMessage());
      Frontend.showStackTraceIfNecessary(e);
      throw e;
    } catch (final CompilerError x) {
      final StringBuilder s = new StringBuilder();
      s.append(x.getFile());
      s.append(":");
      s.append(x.getPosition());
      s.append(": ");
      s.append(x.getCategory());
      s.append(": ");
      s.append(x.getMessage());
      log.error(s.toString());
      Frontend.showStackTraceIfNecessary(x);
      throw x;
    } catch (final IOException x) {
      log.error(x.getMessage());
      Frontend.showStackTraceIfNecessary(x);
      throw x;
    } catch (final Exception x) {
      final StringBuilder s = new StringBuilder();
      s.append("internal compiler error: ");
      s.append(x.getClass().getCanonicalName());
      s.append(" ");
      s.append(": ");
      s.append(x.getMessage());
      log.critical(s.toString());
      x.printStackTrace(System.err);
      throw x;
    }
  }

  private static void runActual(
    final LogUsableType log,
    final String[] args)
    throws ParseException,
      IOException,
      CompilerError,
      NoSuchAlgorithmException,
      GCompactorException,
      JPBatchException
  {
    LogUsableType logx = log;

    if (args.length == 0) {
      Frontend.showHelp();
      return;
    }

    final PosixParser parser = new PosixParser();
    final CommandLine line = parser.parse(Frontend.OPTIONS, args);

    if (line.hasOption("debug")) {
      Frontend.DEBUG = true;
      logx = Frontend.getLog(true);
    }

    final ExecutorService exec;
    if (line.hasOption("threads")) {
      try {
        final Integer count = Integer.valueOf(line.getOptionValue("threads"));
        exec = Executors.newFixedThreadPool(count.intValue());
        logx.debug("Using " + count + " threads");
      } catch (final NumberFormatException e) {
        throw UIError
          .incorrectCommandLine("Could not parse thread count value: "
            + e.getMessage());
      }
    } else {
      exec = Executors.newSingleThreadExecutor();
    }

    try {
      if (line.hasOption("compile-batch")) {
        Frontend.commandCompileBatch(logx, exec, line);
        return;
      } else if (line.hasOption("compile-one")) {
        Frontend.commandCompileOne(logx, exec, line);
        return;
      } else if (line.hasOption("check")) {
        Frontend.commandCheck(logx, line);
        return;
      } else if (line.hasOption("show-versions")) {
        Frontend.commandShowGLSLVersions(logx, line);
        return;
      } else if (line.hasOption("version")) {
        Frontend.commandShowCompilerVersion(logx, line);
        return;
      } else {
        Frontend.showHelp();
      }
    } finally {
      exec.shutdown();
    }
  }

  private static void runCompileActual(
    final LogUsableType logx,
    final ExecutorService exec,
    final Batch batch,
    final List<File> sources,
    final SortedSet<GVersionES> require_es,
    final SortedSet<GVersionFull> require_full,
    final GSerializerType serializer,
    final boolean compact)
    throws CompilerError,
      IOException,
      NoSuchAlgorithmException,
      GCompactorException
  {
    final TASTCompilation typed =
      Frontend.runCompileActualCore(logx, sources);

    Frontend.runCompileGenerateGLSL(
      logx,
      exec,
      batch,
      typed,
      require_es,
      require_full,
      serializer,
      compact);
  }

  private static TASTCompilation runCompileActualCore(
    final LogUsableType logx,
    final List<File> sources)
    throws FileNotFoundException,
      CompilerError,
      IOException
  {
    final CorePipeline pipe = CorePipeline.newPipeline(logx);
    pipe.pipeAddStandardLibrary();

    for (final File file : sources) {
      final FileInput input =
        new FileInput(false, file, new FileInputStream(file));
      pipe.pipeAddInput(input);
    }

    final TASTCompilation typed = pipe.pipeCompile();
    pipe.pipeClose();

    logx.debug("core compilation done");
    return typed;
  }

  private static void runCompileGenerateGLSL(
    final LogUsableType logx,
    final ExecutorService exec,
    final Batch batch,
    final TASTCompilation typed,
    final SortedSet<GVersionES> require_es,
    final SortedSet<GVersionFull> require_full,
    final GSerializerType serializer,
    final boolean compact)
    throws CompilerError,
      IOException,
      NoSuchAlgorithmException,
      GCompactorException
  {
    final GPipeline pipe = GPipeline.newPipeline(typed, exec, logx);

    final GCompilation results =
      pipe.transformPrograms(batch.getShaders(), require_es, require_full);

    logx.debug("serializing");

    {
      final Map<TASTShaderNameFlat, GCompiledVertexShader> shaders =
        results.getShadersVertex();

      for (final TASTShaderNameFlat name : shaders.keySet()) {
        final GCompiledVertexShader shader = shaders.get(name);
        final UncompactedVertexShader flat =
          shader.flatten(logx.with("flatten"));

        if (compact) {
          serializer.serializeCompactedVertexShader(GCompactor
            .compactSerializedVertexShader(flat, logx.with("compactor")));
        } else {
          serializer.serializeUncompactedVertexShader(flat);
        }
      }
    }

    {
      final Map<TASTShaderNameFlat, GCompiledFragmentShader> shaders =
        results.getShadersFragment();

      for (final TASTShaderNameFlat name : shaders.keySet()) {
        final GCompiledFragmentShader shader = shaders.get(name);
        final UncompactedFragmentShader flat =
          shader.flatten(logx.with("flatten"));

        if (compact) {
          serializer.serializeCompactedFragmentShader(GCompactor
            .compactSerializedFragmentShader(flat, logx.with("compactor")));
        } else {
          serializer.serializeUncompactedFragmentShader(flat);
        }
      }
    }

    {
      final Map<TASTShaderNameFlat, GCompiledProgram> shaders =
        results.getShadersProgram();
      final SortedMap<TASTShaderNameFlat, String> outputs =
        batch.getOutputsByShader();
      final OptionType<String> none = com.io7m.jfunctional.Option.none();

      for (final TASTShaderNameFlat name : shaders.keySet()) {
        final GCompiledProgram program = shaders.get(name);
        final UncompactedProgramShaderMeta flat = program.flatten(logx);

        if (outputs.containsKey(name)) {
          final String output = outputs.get(name);
          assert output != null;

          serializer.serializeUncompactedProgramShader(
            flat,
            com.io7m.jfunctional.Option.some(output));
        } else {
          serializer.serializeUncompactedProgramShader(flat, none);
        }
      }
    }
  }

  private static void showHelp()
  {
    final HelpFormatter formatter = new HelpFormatter();
    final PrintWriter pw = new PrintWriter(System.err);
    final String version = Frontend.getVersion();

    pw
      .println("parasol-c: [options] --compile-one output shader file0 [file1 ... fileN]");
    pw
      .println("        or [options] --compile-batch output batch-list source-list");
    pw.println("        or [options] --check file0 [file1 ... fileN]");
    pw.println("        or [options] --show-versions");
    pw.println("        or [options] --version");
    pw.println();
    pw
      .println("  Where: output           is a directory (unless --zip is specified) that will be populated with GLSL shaders");
    pw
      .println("         shader           is the fully-qualified name of a shading program");
    pw
      .println("         batch-list       is a file containing (output , ':' , shader) tuples, separated by newlines");
    pw
      .println("         source-list      is a file containing a set of filenames, separated by newlines");
    pw
      .println("         file[0 .. N]     is a series of filenames containing source code");
    pw.println();
    formatter.printOptions(pw, 120, Frontend.OPTIONS, 2, 4);
    pw.println();
    pw
      .println("   Where: version-set     := version-segment ( ',' version-segment )*");
    pw.println("          version-segment := version-exact | version-range");
    pw.println("          version-exact   := integer");
    pw
      .println("          version-range   := ('(' | '[') integer? ',' integer? (')' | ']')");
    pw.println();
    pw
      .println("     Where '[' and ']' denote inclusive bounds, and '(' ')' denote exclusive bounds.");
    pw.println();
    pw.println("     Example: 130 selects version 130");
    pw.println("     Example: [120, 150] selects versions 120 to 150");
    pw.println("     Example: (120, 150] selects versions 130 to 150");
    pw
      .println("     Example: 120,[140,330],440 selects versions 120, 140, 150, 330, and 440");
    pw.println();
    pw.println("  Version: " + version);
    pw.println();
    pw.flush();
  }

  private static void showStackTraceIfNecessary(
    final Throwable x)
  {
    if (Frontend.DEBUG) {
      x.printStackTrace(System.err);
    }
  }

  private Frontend()
  {

  }
}
