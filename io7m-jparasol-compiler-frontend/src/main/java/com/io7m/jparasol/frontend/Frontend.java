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
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import nu.xom.Serializer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import com.io7m.jfunctional.Pair;
import com.io7m.jfunctional.Unit;
import com.io7m.jlog.Log;
import com.io7m.jlog.LogLevel;
import com.io7m.jlog.LogPolicyAllOn;
import com.io7m.jlog.LogUsableType;
import com.io7m.jparasol.CompilerError;
import com.io7m.jparasol.UIError;
import com.io7m.jparasol.glsl.GFFIError;
import com.io7m.jparasol.glsl.GMeta;
import com.io7m.jparasol.glsl.GVersion;
import com.io7m.jparasol.glsl.GVersion.GVersionES;
import com.io7m.jparasol.glsl.GVersion.GVersionFull;
import com.io7m.jparasol.glsl.GVersionCheckerError;
import com.io7m.jparasol.glsl.GVersionNumberSetLexer;
import com.io7m.jparasol.glsl.GVersionNumberSetParser;
import com.io7m.jparasol.glsl.GVersionNumberSetParser.Segment;
import com.io7m.jparasol.glsl.GVersionVisitorType;
import com.io7m.jparasol.glsl.GWriter;
import com.io7m.jparasol.glsl.ast.GASTShader.GASTShaderFragment;
import com.io7m.jparasol.glsl.ast.GASTShader.GASTShaderVertex;
import com.io7m.jparasol.glsl.pipeline.GPipeline;
import com.io7m.jparasol.lexer.LexerError;
import com.io7m.jparasol.lexer.Position;
import com.io7m.jparasol.parser.ParserError;
import com.io7m.jparasol.pipeline.CorePipeline;
import com.io7m.jparasol.pipeline.FileInput;
import com.io7m.jparasol.typed.ast.TASTCompilation;
import com.io7m.jparasol.typed.ast.TASTShaderNameFlat;
import com.io7m.junreachable.UnimplementedCodeException;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * Command line compiler frontend.
 */

public final class Frontend
{
  private Frontend()
  {

  }

  private static boolean       DEBUG;
  private static final Options OPTIONS;

  static {
    OPTIONS = Frontend.makeOptions();
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
      OptionBuilder.withLongOpt("compile");
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
      OptionBuilder.withLongOpt("threads");
      OptionBuilder.hasArg(true);
      OptionBuilder.withArgName("count");
      OptionBuilder
        .withDescription("Set the number of threads to use during code generation");
      opts.addOption(OptionBuilder.create());
    }

    return opts;
  }

  private static List<Pair<File, TASTShaderNameFlat>> parseBatches(
    final File basedir,
    final File batch_file)
    throws IOException,
      UIError
  {
    final BufferedReader reader =
      new BufferedReader(new FileReader(batch_file));

    try {

      int line_no = 1;
      final Map<String, Integer> outputs = new HashMap<String, Integer>();
      final List<Pair<File, TASTShaderNameFlat>> batches =
        new ArrayList<Pair<File, TASTShaderNameFlat>>();

      for (;;) {
        final String line = reader.readLine();
        if (line == null) {
          break;
        }
        final String[] segments = line.split(":");
        if (segments.length != 2) {
          throw UIError.badBatch(
            line_no,
            batch_file,
            "Format must be: output , \":\" , shader-name");
        }

        final String output = segments[0].trim();
        if (output.isEmpty()) {
          throw UIError.badBatch(
            line_no,
            batch_file,
            "Format must be: output , \":\" , shader-name");
        }
        if (outputs.containsKey(output)) {
          final Integer old_line = outputs.get(output);
          final String s =
            String.format(
              "Output %s already specified at line %d",
              output,
              old_line);
          assert s != null;
          throw UIError.badBatch(line_no, batch_file, s);
        }
        outputs.put(output, Integer.valueOf(line_no));
        final String shader = segments[1].trim();
        final TASTShaderNameFlat shader_name =
          TASTShaderNameFlat.parse(
            shader,
            Pair.pair(batch_file, new Position(line_no, 0)));

        ++line_no;
        final File file = new File(basedir, output);
        batches.add(Pair.pair(file, shader_name));
      }

      return batches;
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
   * @throws ParseException
   *           On XML parse errors
   * @throws CompilerError
   *           On compilation errors
   * @throws IOException
   *           On I/O errors
   * @throws InterruptedException
   *           On threading errors
   */

  public static void run(
    final LogUsableType log,
    final String[] args)
    throws ParseException,
      CompilerError,
      IOException,
      InterruptedException
  {
    try {
      Frontend.runActual(log, args);
    } catch (final ParseException e) {
      log.error(e.getMessage());
      Frontend.showHelp();
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
    } catch (final UnimplementedCodeException x) {
      log.critical("internal compiler error: " + x.getMessage());
      x.printStackTrace(System.err);
      throw x;
    } catch (final UnreachableCodeException x) {
      log.critical("internal compiler error: " + x.getMessage());
      x.printStackTrace(System.err);
      throw x;
    } catch (final AssertionError x) {
      log.critical("internal compiler error: " + x.getMessage());
      x.printStackTrace(System.err);
      throw x;
    } catch (final InterruptedException x) {
      log.error(x.getMessage());
      Frontend.showStackTraceIfNecessary(x);
      throw x;
    }
  }

  private static void runActual(
    final LogUsableType log,
    final String[] args)
    throws ParseException,
      IOException,
      InterruptedException,
      CompilerError
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
      } catch (final NumberFormatException e) {
        throw UIError
          .incorrectCommandLine("Could not parse thread count value: "
            + e.getMessage());
      }
    } else {
      exec = Executors.newSingleThreadExecutor();
    }

    if (line.hasOption("compile-batch")) {
      Frontend.runCompileBatch(logx, exec, line);
      return;
    } else if (line.hasOption("compile")) {
      Frontend.runCompile(logx, exec, line);
      return;
    } else if (line.hasOption("check")) {
      Frontend.runCheck(logx, line);
      return;
    } else if (line.hasOption("show-versions")) {
      Frontend.runShowGLSLVersions(logx, line);
      return;
    } else if (line.hasOption("version")) {
      Frontend.runShowCompilerVersion(logx, line);
      return;
    } else {
      Frontend.showHelp();
    }
  }

  private static void runCheck(
    final LogUsableType logx,
    final CommandLine line)
    throws IOException,
      CompilerError
  {
    final List<File> files = new ArrayList<File>();
    for (final String f : line.getArgs()) {
      files.add(new File(f));
    }

    Frontend.runCompileActual(logx, files);
  }

  private static void runCompile(
    final LogUsableType logx,
    final ExecutorService exec,
    final CommandLine line)
    throws IOException,
      InterruptedException,
      CompilerError
  {
    final SortedSet<GVersionES> require_es = Frontend.getRequiredES(line);
    final SortedSet<GVersionFull> require_full =
      Frontend.getRequiredFull(line);

    final String[] args = line.getArgs();
    if (args.length < 3) {
      throw UIError.incorrectCommandLine("Too few arguments provided");
    }
    final File output = new File(args[0]);
    final Pair<File, Position> meta =
      Pair.pair(new File("command line"), new Position(0, 0));
    final TASTShaderNameFlat shader = TASTShaderNameFlat.parse(args[1], meta);

    final List<File> files = new ArrayList<File>();
    for (int index = 2; index < args.length; ++index) {
      files.add(new File(args[index]));
    }

    final TASTCompilation typed = Frontend.runCompileActual(logx, files);

    try {
      try {
        final Future<Unit> result = exec.submit(new Callable<Unit>() {
          @SuppressWarnings("synthetic-access") @Override public Unit call()
            throws Exception
          {
            Frontend.runCompileMakeGLSL(
              logx,
              require_es,
              require_full,
              output,
              shader,
              typed);
            return Unit.unit();
          }
        });
        result.get();
      } catch (final ExecutionException e) {
        Frontend.handleRunCompileMakeGLSLException(e);
      } catch (final InterruptedException e) {
        throw e;
      }
    } finally {
      exec.shutdown();
    }
  }

  private static void handleRunCompileMakeGLSLException(
    final ExecutionException e)
    throws UIError,
      GFFIError,
      GVersionCheckerError,
      IOException
  {
    final Throwable cause = e.getCause();
    assert cause != null;

    if (cause instanceof UIError) {
      throw (UIError) cause;
    }

    if (cause instanceof GFFIError) {
      throw (GFFIError) cause;
    }

    if (cause instanceof GVersionCheckerError) {
      throw (GVersionCheckerError) cause;
    }

    if (cause instanceof IOException) {
      throw (IOException) cause;
    }

    throw new UnreachableCodeException(e);
  }

  private static TASTCompilation runCompileActual(
    final LogUsableType logx,
    final List<File> files)
    throws IOException,
      CompilerError
  {
    final CorePipeline pipe = CorePipeline.newPipeline(logx);
    pipe.pipeAddStandardLibrary();

    for (final File file : files) {
      pipe
        .pipeAddInput(new FileInput(false, file, new FileInputStream(file)));
    }

    final TASTCompilation typed = pipe.pipeCompile();
    pipe.pipeClose();

    logx.debug("core compilation done");
    return typed;
  }

  private static void runCompileBatch(
    final LogUsableType logx,
    final ExecutorService exec,
    final CommandLine line)
    throws IOException,
      InterruptedException,
      CompilerError
  {
    try {
      final SortedSet<GVersionES> require_es = Frontend.getRequiredES(line);
      final SortedSet<GVersionFull> require_full =
        Frontend.getRequiredFull(line);

      final String[] args = line.getArgs();
      if (args.length < 3) {
        throw UIError.incorrectCommandLine("Too few arguments provided");
      }
      final File basedir = new File(args[0]);
      final File batch_file = new File(args[1]);
      final File source_file = new File(args[2]);

      final List<Pair<File, TASTShaderNameFlat>> batches =
        Frontend.parseBatches(basedir, batch_file);
      final List<File> sources = Frontend.parseSources(source_file);

      final TASTCompilation typed = Frontend.runCompileActual(logx, sources);

      final List<Future<Unit>> batch_futures = new ArrayList<Future<Unit>>();
      for (final Pair<File, TASTShaderNameFlat> batch : batches) {
        logx.debug("submitting batch job: " + batch.getRight());

        final Future<Unit> f = exec.submit(new Callable<Unit>() {
          @SuppressWarnings("synthetic-access") @Override public Unit call()
            throws Exception
          {
            Frontend.runCompileMakeGLSL(
              logx,
              require_es,
              require_full,
              batch.getLeft(),
              batch.getRight(),
              typed);
            return Unit.unit();
          }
        });
        batch_futures.add(f);
      }

      for (final Future<Unit> f : batch_futures) {
        try {
          f.get();
        } catch (final ExecutionException e) {
          Frontend.handleRunCompileMakeGLSLException(e);
        }
      }

    } finally {
      exec.shutdown();
    }
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

  private static void runCompileMakeGLSL(
    final LogUsableType logx,
    final SortedSet<GVersionES> require_es,
    final SortedSet<GVersionFull> require_full,
    final File output_directory,
    final TASTShaderNameFlat shader,
    final TASTCompilation typed)
    throws UIError,
      GFFIError,
      GVersionCheckerError,
      IOException
  {
    final GPipeline gpipe = GPipeline.newPipeline(typed, logx);
    final Map<GVersion, Pair<GASTShaderVertex, GASTShaderFragment>> shaders =
      gpipe.makeProgram(shader, require_es, require_full);

    Frontend.writeShaders(logx, output_directory, shaders, shader);
  }

  private static void runShowCompilerVersion(
    final LogUsableType logx,
    final CommandLine line)
  {
    System.out.println(Frontend.getVersion());
  }

  private static void runShowGLSLVersions(
    final LogUsableType logx,
    final CommandLine line)
  {
    for (final GVersionFull v : GVersionFull.ALL) {
      System.out.println(v.getLongName());
    }
    for (final GVersionES v : GVersionES.ALL) {
      System.out.println(v.getLongName());
    }
  }

  private static void showHelp()
  {
    final HelpFormatter formatter = new HelpFormatter();
    final PrintWriter pw = new PrintWriter(System.err);
    final String version = Frontend.getVersion();

    pw
      .println("parasol-c: [options] --compile output.pc shader         file0 [file1 ... fileN]");
    pw
      .println("        or [options] --compile-batch basedir batch-list source-list");
    pw
      .println("        or [options] --check                            file0 [file1 ... fileN]");
    pw.println("        or [options] --show-versions");
    pw.println("        or [options] --version");
    pw.println();
    pw
      .println("  Where: output.pc    is a directory that will be populated with GLSL files");
    pw
      .println("         basedir      is a directory that will be prefixed to the names given in batches");
    pw
      .println("         shader       is the fully-qualified name of a shading program");
    pw
      .println("         batch-list   is a file containing (output.pc , \":\" , shader) tuples, separated by newlines");
    pw
      .println("         source-list  is a file containing a set of filenames, separated by newlines");
    pw
      .println("         file[0 .. N] is a series of filenames containing source code");
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
      .println("     Example: 120,[140,330],430 selects versions 120, 140, 150, 330, and 430");
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

  private static void writeMeta(
    final LogUsableType log,
    final File output_directory,
    final Map<GVersion, Pair<GASTShaderVertex, GASTShaderFragment>> shaders,
    final TASTShaderNameFlat shader)
    throws IOException
  {
    final File meta = new File(output_directory, "meta.xml");
    log.debug("writing " + meta);

    final FileOutputStream mout = new FileOutputStream(meta);
    final Serializer serial = new Serializer(mout, "UTF-8");
    serial.setIndent(2);
    serial.setMaxLength(80);
    serial.write(GMeta.make(shader, shaders));
    serial.flush();

    mout.flush();
    mout.close();
  }

  static void writeShader(
    final LogUsableType logx,
    final File v_name,
    final File f_name,
    final Pair<GASTShaderVertex, GASTShaderFragment> pair)
    throws IOException
  {
    logx.debug("writing " + v_name);
    final FileOutputStream vout = new FileOutputStream(v_name);
    GWriter.writeVertexShader(vout, pair.getLeft());
    vout.flush();
    vout.close();

    logx.debug("writing " + f_name);
    final FileOutputStream fout = new FileOutputStream(f_name);
    GWriter.writeFragmentShader(fout, pair.getRight());
    fout.flush();
    fout.close();
  }

  private static void writeShaders(
    final LogUsableType logx,
    final File output_directory,
    final Map<GVersion, Pair<GASTShaderVertex, GASTShaderFragment>> shaders,
    final TASTShaderNameFlat shader)
    throws IOException
  {
    logx.info(String.format("writing shader %s", shader.show()));

    output_directory.mkdirs();
    assert output_directory.isDirectory();

    Frontend.writeMeta(logx, output_directory, shaders, shader);

    for (final GVersion v : shaders.keySet()) {
      final Pair<GASTShaderVertex, GASTShaderFragment> pair = shaders.get(v);

      v.versionAccept(new GVersionVisitorType<Unit, IOException>() {
        @Override public Unit versionVisitES(
          final GVersionES version)
          throws IOException
        {
          final File v_name =
            new File(output_directory, "glsl-es-"
              + version.getNumber()
              + ".v");
          final File f_name =
            new File(output_directory, "glsl-es-"
              + version.getNumber()
              + ".f");
          Frontend.writeShader(logx, v_name, f_name, pair);
          return Unit.unit();
        }

        @Override public Unit versionVisitFull(
          final GVersionFull version)
          throws IOException
        {
          final File v_name =
            new File(output_directory, "glsl-" + version.getNumber() + ".v");
          final File f_name =
            new File(output_directory, "glsl-" + version.getNumber() + ".f");
          Frontend.writeShader(logx, v_name, f_name, pair);
          return Unit.unit();
        }
      });
    }
  }
}
