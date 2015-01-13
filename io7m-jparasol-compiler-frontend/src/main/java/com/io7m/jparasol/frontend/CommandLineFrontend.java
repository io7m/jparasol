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
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import com.io7m.jfunctional.Pair;
import com.io7m.jlog.Log;
import com.io7m.jlog.LogLevel;
import com.io7m.jlog.LogPolicyAllOn;
import com.io7m.jlog.LogPolicyProperties;
import com.io7m.jlog.LogPolicyType;
import com.io7m.jlog.LogType;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.CompilerError;
import com.io7m.jparasol.UIError;
import com.io7m.jparasol.core.GVersionES;
import com.io7m.jparasol.core.GVersionFull;
import com.io7m.jparasol.glsl.GVersionNumberSetLexer;
import com.io7m.jparasol.glsl.GVersionNumberSetParser;
import com.io7m.jparasol.glsl.GVersionNumberSetParser.Segment;
import com.io7m.jparasol.glsl.compactor.GCompactorException;
import com.io7m.jparasol.glsl.serialization.GSerializerFile;
import com.io7m.jparasol.glsl.serialization.GSerializerType;
import com.io7m.jparasol.glsl.serialization.GSerializerZip;
import com.io7m.jparasol.lexer.LexerError;
import com.io7m.jparasol.lexer.Position;
import com.io7m.jparasol.metaserializer.JPMetaSerializerType;
import com.io7m.jparasol.metaserializer.xml.JPXMLMetaSerializer;
import com.io7m.jparasol.parser.ParserError;
import com.io7m.jparasol.typed.ast.TASTShaderNameFlat;
import com.io7m.jproperties.JProperties;
import com.io7m.jproperties.JPropertyException;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * Command line compiler frontend.
 */

public final class CommandLineFrontend
{
  private static final Options OPTIONS;

  static {
    OPTIONS = CommandLineFrontend.makeOptions();
  }

  private static void commandCheck(
    final Compiler compiler,
    final CommandLine line)
    throws IOException,
      CompilerError,
      GCompactorException
  {
    try {
      final List<File> sources = new ArrayList<File>();
      for (final String f : line.getArgs()) {
        sources.add(new File(f));
      }

      final CompilerBatch batch = CompilerBatch.newBatch();
      compiler.setRequiredES(CommandLineFrontend.getRequiredES(line));
      compiler.setRequiredFull(CommandLineFrontend.getRequiredFull(line));
      compiler.setCompacting(false);
      compiler.setGeneratingCode(false);
      compiler.runForFiles(batch, sources);
    } catch (final JPFrontendMissingSerializer e) {
      throw new UnreachableCodeException(e);
    }
  }

  private static void commandCompileBatch(
    final LogType log,
    final Compiler compiler,
    final CommandLine line)
    throws IOException,
      JPBatchException,
      CompilerError,
      GCompactorException
  {
    try {
      final String[] args = line.getArgs();
      if (args.length < 3) {
        throw UIError.incorrectCommandLine("Too few arguments provided");
      }
      final File output = new File(args[0]);
      final File batch_file = new File(args[1]);
      final File source_file = new File(args[2]);

      final CompilerBatch batch = CompilerBatch.newBatchFromFile(batch_file);
      final List<File> sources =
        CommandLineFrontend.parseSources(source_file);
      final GSerializerType serializer =
        CommandLineFrontend.makeSerializer(log, line, output);

      compiler.setRequiredES(CommandLineFrontend.getRequiredES(line));
      compiler.setRequiredFull(CommandLineFrontend.getRequiredFull(line));
      compiler.setSerializer(serializer);
      compiler.setCompacting(line.hasOption("compact"));
      compiler.setGeneratingCode(true);
      compiler.runForFiles(batch, sources);
      serializer.close();

    } catch (final JPFrontendMissingSerializer e) {
      throw new UnreachableCodeException(e);
    }
  }

  private static void commandCompileOne(
    final LogType log,
    final Compiler compiler,
    final CommandLine line)
    throws JPBatchDuplicateShader,
      IOException,
      CompilerError,
      GCompactorException
  {
    try {
      final String[] args = line.getArgs();
      if (args.length < 3) {
        throw UIError.incorrectCommandLine("Too few arguments provided");
      }

      final File output = new File(args[0]);
      final TASTShaderNameFlat name =
        TASTShaderNameFlat.parse(
          NullCheck.notNull(args[1], "args[1]"),
          Pair.pair(new File("<stdin>"), Position.ZERO));
      final CompilerBatch batch = CompilerBatch.newBatch();
      batch.addShader(name);

      final List<File> sources = new ArrayList<File>();
      for (int index = 2; index < args.length; ++index) {
        sources.add(new File(args[index]));
      }

      final GSerializerType serializer =
        CommandLineFrontend.makeSerializer(log, line, output);

      compiler.setRequiredES(CommandLineFrontend.getRequiredES(line));
      compiler.setRequiredFull(CommandLineFrontend.getRequiredFull(line));
      compiler.setSerializer(serializer);
      compiler.setCompacting(line.hasOption("compact"));
      compiler.setGeneratingCode(true);
      compiler.runForFiles(batch, sources);
      serializer.close();
    } catch (final JPFrontendMissingSerializer e) {
      throw new UnreachableCodeException(e);
    }
  }

  private static void commandShowCompilerVersion()
  {
    System.out.println(CommandLineFrontend.getVersion());
  }

  private static void commandShowGLSLVersions()
  {
    for (final GVersionFull v : GVersionFull.ALL) {
      System.out.println(v.versionGetLongName());
    }
    for (final GVersionES v : GVersionES.ALL) {
      System.out.println(v.versionGetLongName());
    }
  }

  private static SortedSet<GVersionES> getRequiredES(
    final CommandLine line)
    throws LexerError,
      IOException,
      ParserError,
      UIError
  {
    final SortedSet<GVersionES> versions = new TreeSet<GVersionES>();
    if (line.hasOption("require-glsl-es")) {
      final String text = line.getOptionValue("require-glsl-es");
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
    if (line.hasOption("require-glsl")) {
      final String text = line.getOptionValue("require-glsl");
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
      CommandLineFrontend.class.getPackage().getImplementationVersion();
    if (pack == null) {
      return "unavailable";
    }
    return pack;
  }

  private static ExecutorService makeExecutor(
    final CommandLine line,
    final LogType log)
    throws UIError
  {
    final ExecutorService exec;
    if (line.hasOption("threads")) {
      try {
        final Integer count = Integer.valueOf(line.getOptionValue("threads"));
        exec = Executors.newFixedThreadPool(count.intValue());
        log.debug("using " + count + " threads");
      } catch (final NumberFormatException e) {
        throw UIError
          .incorrectCommandLine("Could not parse thread count value: "
            + e.getMessage());
      }
    } else {
      exec = Executors.newSingleThreadExecutor();
    }
    assert exec != null;
    return exec;
  }

  private static LogType makeLog(
    final CommandLine line)
    throws IOException,
      JPropertyException
  {
    final LogPolicyType policy;
    if (line.hasOption("log-properties")) {
      final String file = line.getOptionValue("log-properties");
      final Properties props = JProperties.fromFile(new File(file));
      policy = LogPolicyProperties.newPolicy(props, "com.io7m.jparasol");
    } else {
      policy = LogPolicyAllOn.newPolicy(LogLevel.LOG_INFO);
    }

    final LogType log = Log.newLog(policy, "compiler");
    return log;
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
      OptionBuilder.withLongOpt("show-glsl-versions");
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
      OptionBuilder.withLongOpt("require-glsl");
      OptionBuilder.hasArgs(1);
      OptionBuilder.withArgName("version-set");
      OptionBuilder
        .withDescription("Require GLSL source code for the given set of GLSL versions, failing if any of the versions cannot be satisfied");
      opts.addOption(OptionBuilder.create());
    }

    {
      OptionBuilder.withLongOpt("require-glsl-es");
      OptionBuilder.hasArgs(1);
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
      OptionBuilder.withLongOpt("log-stack-traces");
      OptionBuilder
        .withDescription("Enable logging of exception stack traces");
      opts.addOption(OptionBuilder.create());
    }

    {
      OptionBuilder.withLongOpt("log-properties");
      OptionBuilder.hasArgs(1);
      OptionBuilder.withArgName("properties");
      OptionBuilder
        .withDescription("Configure logging based on the given property file");
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
      OptionBuilder.withLongOpt("zip-append");
      OptionBuilder
        .withDescription("When using --zip, append shaders to the existing zip archive instead of a creating a new archive");
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

  @SuppressWarnings("resource") private static
    GSerializerType
    makeSerializer(
      final LogType log,
      final CommandLine line,
      final File output)
      throws ZipException,
        IOException
  {
    final JPMetaSerializerType meta_serializer =
      JPXMLMetaSerializer.newSerializer();

    final GSerializerType serializer;
    if (line.hasOption("zip")) {
      final ZipOutputStream zip_stream;
      if (line.hasOption("zip-append")) {
        zip_stream = CopyZip.copyZip(log, output);
      } else {
        zip_stream =
          new ZipOutputStream(
            new FileOutputStream(output),
            Charset.forName("UTF-8"));
      }

      assert zip_stream != null;
      serializer =
        GSerializerZip.newSerializer(meta_serializer, zip_stream, log);
    } else {
      serializer =
        GSerializerFile.newSerializer(meta_serializer, output, true);
    }
    assert serializer != null;
    return serializer;
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
   * Construct and execute the frontend.
   *
   * @param args
   *          Command line arguments.
   * @throws Exception
   *           On errors.
   */

  public static void run(
    final String[] args)
    throws Exception
  {
    boolean stack_traces = false;

    final LogType error_log =
      Log.newLog(LogPolicyAllOn.newPolicy(LogLevel.LOG_DEBUG), "compiler");

    try {
      if (args.length == 0) {
        CommandLineFrontend.showHelp();
        return;
      }

      final PosixParser parser = new PosixParser();
      final CommandLine line =
        parser.parse(CommandLineFrontend.OPTIONS, args);
      assert line != null;

      stack_traces = line.hasOption("log-stack-traces");

      final LogType log = CommandLineFrontend.makeLog(line);
      final ExecutorService exec =
        CommandLineFrontend.makeExecutor(line, log);
      assert exec != null;

      final Compiler compiler = Compiler.newCompiler(log, exec);

      try {
        if (line.hasOption("compile-batch")) {
          CommandLineFrontend.commandCompileBatch(log, compiler, line);
          return;
        } else if (line.hasOption("compile-one")) {
          CommandLineFrontend.commandCompileOne(log, compiler, line);
          return;
        } else if (line.hasOption("check")) {
          CommandLineFrontend.commandCheck(compiler, line);
          return;
        } else if (line.hasOption("show-glsl-versions")) {
          CommandLineFrontend.commandShowGLSLVersions();
          return;
        } else if (line.hasOption("version")) {
          CommandLineFrontend.commandShowCompilerVersion();
          return;
        } else {
          CommandLineFrontend.showHelp();
        }
      } finally {
        exec.shutdown();
      }

    } catch (final ParseException e) {
      final String r = e.getMessage();
      assert r != null;
      error_log.error(r);
      CommandLineFrontend.showHelp();
      throw e;
    } catch (final JPBatchException e) {
      error_log.error("invalid batch: " + e.getMessage());
      CommandLineFrontend.showStackTraceIfNecessary(stack_traces, e);
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
      final String r = s.toString();
      assert r != null;
      error_log.error(r);
      CommandLineFrontend.showStackTraceIfNecessary(stack_traces, x);
      throw x;
    } catch (final IOException x) {
      final String r = x.getMessage();
      assert r != null;
      error_log.error("i/o error: " + r);
      CommandLineFrontend.showStackTraceIfNecessary(stack_traces, x);
      throw x;
    } catch (final Exception x) {
      final StringBuilder s = new StringBuilder();
      s.append("internal compiler error: ");
      s.append(x.getClass().getCanonicalName());
      s.append(" ");
      s.append(": ");
      s.append(x.getMessage());
      final String r = s.toString();
      assert r != null;
      error_log.critical(r);
      x.printStackTrace(System.err);
      throw x;
    }
  }

  private static void showHelp()
  {
    final HelpFormatter formatter = new HelpFormatter();
    final PrintWriter pw = new PrintWriter(System.err);
    final String version = CommandLineFrontend.getVersion();

    pw
      .println("parasol-c: [options] --compile-one output shader file0 [file1 ... fileN]");
    pw
      .println("        or [options] --compile-batch output batch-list source-list");
    pw.println("        or [options] --check file0 [file1 ... fileN]");
    pw.println("        or [options] --show-glsl-versions");
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
    formatter.printOptions(pw, 120, CommandLineFrontend.OPTIONS, 2, 4);
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
    final boolean stack_traces,
    final Throwable e)
  {
    if (stack_traces) {
      e.printStackTrace(System.err);
    }
  }

  private CommandLineFrontend()
  {
    throw new UnreachableCodeException();
  }
}
