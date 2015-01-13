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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import com.io7m.jlog.Log;
import com.io7m.jlog.LogLevel;
import com.io7m.jlog.LogPolicyAllOn;
import com.io7m.jlog.LogPolicyProperties;
import com.io7m.jlog.LogPolicyType;
import com.io7m.jlog.LogType;
import com.io7m.jlog.LogUsableType;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.metaserializer.JPMetaDeserializerType;
import com.io7m.jparasol.metaserializer.JPMetaSerializerType;
import com.io7m.jparasol.metaserializer.JPSerializerException;
import com.io7m.jparasol.metaserializer.protobuf.JPProtobufMetaDeserializer;
import com.io7m.jparasol.metaserializer.protobuf.JPProtobufMetaSerializer;
import com.io7m.jparasol.metaserializer.xml.JPXMLMetaDeserializer;
import com.io7m.jparasol.metaserializer.xml.JPXMLMetaSerializer;
import com.io7m.jproperties.JProperties;
import com.io7m.jproperties.JPropertyException;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * Metadata converter.
 */

public final class ConvertMetaMain
{
  private static final Options OPTIONS;

  static {
    OPTIONS = ConvertMetaMain.makeOptions();
  }

  private static void commandConvert(
    final CommandLine line,
    final LogType log)
    throws IOException,
      JPSerializerException
  {
    final String[] args = line.getOptionValues("convert");
    final String source_name = NullCheck.notNull(args[0]);
    final String target_name = NullCheck.notNull(args[1]);
    final String source_type = ConvertMetaMain.getSuffix(source_name);
    final String target_type = ConvertMetaMain.getSuffix(target_name);

    final Map<String, JPMetaDeserializerType> ds =
      ConvertMetaMain.makeDeserializers(log);
    final Map<String, JPMetaSerializerType> ss =
      ConvertMetaMain.makeSerializers();

    final JPMetaDeserializerType d =
      ConvertMetaMain.getDeserializerForName(ds, line, source_type);
    final JPMetaSerializerType s =
      ConvertMetaMain.getSerializerForName(ss, line, target_type);

    final File source = new File(source_name);
    final File target = new File(target_name);

    final BufferedInputStream ssource =
      new BufferedInputStream(new FileInputStream(source));
    final BufferedOutputStream starget =
      new BufferedOutputStream(new FileOutputStream(target));

    s.metaSerializeShader(d.metaDeserializeShader(ssource), starget);
    starget.flush();
    starget.close();
    ssource.close();
  }

  private static JPMetaDeserializerType getDeserializerForName(
    final Map<String, JPMetaDeserializerType> ds,
    final CommandLine line,
    final String name)
  {
    final String actual;
    if (line.hasOption("input-format")) {
      actual = line.getOptionValue("input-format");
    } else {
      actual = name;
    }

    if (ds.containsKey(actual)) {
      return ds.get(actual);
    }

    throw ConvertMetaMain.unknownInputFormat(ds, actual);
  }

  private static JPMetaSerializerType getSerializerForName(
    final Map<String, JPMetaSerializerType> ss,
    final CommandLine line,
    final String name)
  {
    final String actual;
    if (line.hasOption("output-format")) {
      actual = line.getOptionValue("output-format");
    } else {
      actual = name;
    }

    if (ss.containsKey(actual)) {
      return ss.get(actual);
    }

    throw ConvertMetaMain.unknownOutputFormat(ss, actual);
  }

  private static String getSuffix(
    final String name)
  {
    final int pos = name.lastIndexOf('.');
    if ((pos > 0) && (pos < (name.length() - 1))) {
      return name.substring(pos + 1);
    }
    return "";
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

  /**
   * Run the converter, exiting on errors.
   *
   * @param args
   *          Command line arguments
   */

  public static void main(
    final String[] args)
  {
    if (args.length == 0) {
      ConvertMetaMain.showHelp();
      System.exit(0);
    }

    boolean stack_traces = false;
    try {
      final PosixParser parser = new PosixParser();
      final CommandLine line = parser.parse(ConvertMetaMain.OPTIONS, args);
      assert line != null;

      stack_traces = line.hasOption("log-stack-traces");
      final LogType log = ConvertMetaMain.makeLog(line);

      if (line.hasOption("help")) {
        ConvertMetaMain.showHelp();
        return;
      } else if (line.hasOption("convert")) {
        ConvertMetaMain.commandConvert(line, log);
        return;
      }

    } catch (final ParseException e) {
      System.err.println("convert-meta: error: " + e.getMessage());
      ConvertMetaMain.showHelp();
      if (stack_traces) {
        e.printStackTrace(System.err);
      }
      System.exit(1);
    } catch (final IOException e) {
      System.err.println("convert-meta: i/o error: " + e.getMessage());
      if (stack_traces) {
        e.printStackTrace(System.err);
      }
      System.exit(1);
    } catch (final JPSerializerException e) {
      System.err.println("convert-meta: serialization error: "
        + e.getMessage());
      if (stack_traces) {
        e.printStackTrace(System.err);
      }
      System.exit(1);
    } catch (final JPropertyException e) {
      System.err.println("convert-meta: configuration error: "
        + e.getMessage());
      if (stack_traces) {
        e.printStackTrace(System.err);
      }
      System.exit(1);
    }
  }

  private static Map<String, JPMetaDeserializerType> makeDeserializers(
    final LogUsableType log)
  {
    final Map<String, JPMetaDeserializerType> m =
      new HashMap<String, JPMetaDeserializerType>();
    m.put(
      JPProtobufMetaSerializer.SUGGESTED_FILENAME_SUFFIX,
      JPProtobufMetaDeserializer.newDeserializer());
    m.put(
      JPXMLMetaSerializer.SUGGESTED_FILENAME_SUFFIX,
      JPXMLMetaDeserializer.newDeserializer(log));
    return m;
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

    final LogType log = Log.newLog(policy, "convert-meta");
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
      OptionBuilder.withLongOpt("convert");
      OptionBuilder.hasArgs(2);
      OptionBuilder.withValueSeparator(' ');
      OptionBuilder.withArgName("from> <to");
      OptionBuilder.withDescription("Convert metadata");
      opts.addOption(OptionBuilder.create());
    }

    {
      OptionBuilder.withLongOpt("input-format");
      OptionBuilder.withArgName("format");
      OptionBuilder.hasArg();
      OptionBuilder
        .withDescription("Assume the input file is of the given format");
      opts.addOption(OptionBuilder.create());
    }

    {
      OptionBuilder.withLongOpt("output-format");
      OptionBuilder.withArgName("format");
      OptionBuilder.hasArg();
      OptionBuilder
        .withDescription("Require that the output file be of the given format");
      opts.addOption(OptionBuilder.create());
    }

    return opts;
  }

  private static Map<String, JPMetaSerializerType> makeSerializers()
  {
    final Map<String, JPMetaSerializerType> m =
      new HashMap<String, JPMetaSerializerType>();
    m.put(
      JPProtobufMetaSerializer.SUGGESTED_FILENAME_SUFFIX,
      JPProtobufMetaSerializer.newSerializer());
    m.put(
      JPXMLMetaSerializer.SUGGESTED_FILENAME_SUFFIX,
      JPXMLMetaSerializer.newSerializer());
    return m;
  }

  private static void showHelp()
  {
    final HelpFormatter formatter = new HelpFormatter();
    final PrintWriter pw = new PrintWriter(System.err);
    final String version = ConvertMetaMain.getVersion();

    pw.println("convert-meta: [options] --convert file0 file1");
    pw.println("           or [options] --version");
    pw.println();
    pw.println("  file[0 .. N] is a series of filenames containing metadata");
    pw.println();
    formatter.printOptions(pw, 120, ConvertMetaMain.OPTIONS, 2, 4);
    pw.println();
    pw.println("  Version: " + version);
    pw.println();
    pw.flush();
  }

  private static IllegalArgumentException unknownInputFormat(
    final Map<String, JPMetaDeserializerType> ds,
    final String name)
  {
    final StringBuilder s = new StringBuilder();
    s.append("Unknown input format '");
    s.append(name);
    s.append("'\n");
    s.append("Known formats include: ");
    for (final String s_name : ds.keySet()) {
      s.append(s_name);
      s.append(" ");
    }
    s.append("\n");
    final String r = s.toString();
    assert r != null;
    return new IllegalArgumentException(r);
  }

  private static IllegalArgumentException unknownOutputFormat(
    final Map<String, JPMetaSerializerType> ss,
    final String name)
  {
    final StringBuilder s = new StringBuilder();
    s.append("Unknown output format '");
    s.append(name);
    s.append("'\n");
    s.append("Known formats include: ");
    for (final String s_name : ss.keySet()) {
      s.append(s_name);
      s.append(" ");
    }
    s.append("\n");
    final String r = s.toString();
    assert r != null;
    return new IllegalArgumentException(r);
  }

  private ConvertMetaMain()
  {
    throw new UnreachableCodeException();
  }
}
