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

package com.io7m.jparasol.xml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.parsers.ParserConfigurationException;

import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.ValidityException;

import org.xml.sax.SAXException;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jfunctional.Unit;
import com.io7m.jlog.LogType;
import com.io7m.jlog.LogUsableType;
import com.io7m.jnull.NullCheck;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * <p>
 * A compactor that eliminates duplicate GLSL shader files.
 * </p>
 */

@EqualityReference @SuppressWarnings({ "boxing", "synthetic-access" }) public final class Compactor
{
  @EqualityReference private static final class Source
  {
    public static Source fromFile(
      final File file,
      final LogUsableType log)
      throws IOException,
        NoSuchAlgorithmException
    {
      log.debug("reading " + file);
      final List<String> lines = Source.readLines(file);
      if (lines.size() < 1) {
        throw new IOException(file + " is empty!");
      }
      Source.stripVersion(file, lines);
      return Source.newSource(lines, log);
    }

    public static Source newSource(
      final List<String> lines,
      final LogUsableType log)
      throws NoSuchAlgorithmException
    {
      log.debug(lines.size() + " lines");

      final MessageDigest md = MessageDigest.getInstance("SHA-256");
      for (final String line : lines) {
        md.update(line.getBytes());
      }

      final StringBuilder hash = new StringBuilder();
      for (final byte b : md.digest()) {
        hash.append(String.format("%02x", b));
      }

      final String r = hash.toString();
      assert r != null;

      log.debug("hash " + r);
      return new Source(lines, r);
    }

    private static List<String> readLines(
      final File file)
      throws IOException
    {
      final BufferedReader br = new BufferedReader(new FileReader(file));
      try {
        final List<String> lines = new ArrayList<String>();

        for (;;) {
          final String line = br.readLine();
          if (line == null) {
            break;
          }
          lines.add(line);
        }

        return lines;
      } finally {
        br.close();
      }
    }

    private static void stripVersion(
      final File file,
      final List<String> lines)
      throws IOException
    {
      if (lines.get(0).startsWith("#version ") == false) {
        throw new IOException("File "
          + file
          + " does not begin with #version");
      }
      lines.remove(0);
    }

    private final String       hash;
    private final List<String> lines;

    Source(
      final List<String> in_lines,
      final String in_hash)
    {
      this.lines = NullCheck.notNull(in_lines, "Lines");
      this.hash = NullCheck.notNull(in_hash, "Hash");
    }

    public String getHash()
    {
      return this.hash;
    }

    public List<String> getLines()
    {
      final List<String> r = Collections.unmodifiableList(this.lines);
      assert r != null;
      return r;
    }
  }

  private static void compactFragment(
    final File directory,
    final File output_directory,
    final ShaderMetaFragment p,
    final LogUsableType log)
    throws NoSuchAlgorithmException,
      IOException
  {
    final SortedMap<Version, String> mappings =
      new TreeMap<Version, String>();
    final Map<String, Source> sources = new HashMap<String, Source>();

    for (final Integer v : p.getSupportsES()) {
      final StringBuilder f_name = new StringBuilder();
      f_name.append("glsl-es-");
      f_name.append(v);
      f_name.append(".f");
      final File f_file = new File(directory, f_name.toString());
      final Source source = Source.fromFile(f_file, log);
      mappings.put(
        new Version(v.intValue(), API.API_GLSL_ES),
        source.getHash());
      sources.put(source.getHash(), source);
    }

    for (final Integer v : p.getSupportsFull()) {
      final StringBuilder f_name = new StringBuilder();
      f_name.append("glsl-");
      f_name.append(v);
      f_name.append(".f");
      final File f_file = new File(directory, f_name.toString());
      final Source source = Source.fromFile(f_file, log);
      mappings.put(new Version(v.intValue(), API.API_GLSL), source.getHash());
      sources.put(source.getHash(), source);
    }

    Compactor.writeSources(output_directory, log, sources, ".f");
    Compactor.writeMappings(output_directory, p, log, mappings);
  }

  private static void compactProgram(
    final File output_directory,
    final ShaderMetaProgram p,
    final LogUsableType log)
    throws FileNotFoundException,
      UnsupportedEncodingException,
      IOException
  {
    Compactor.writeMeta(output_directory, log, p);
  }

  /**
   * Read a shader from the given input directory, and write compacted sources
   * to the output directory.
   * 
   * @param in_directory
   *          The input directory.
   * @param in_output_directory
   *          The output directory.
   * @param in_log
   *          A log interface.
   * @throws CompactorException
   *           On errors.
   */

  public static void compactShader(
    final File in_directory,
    final File in_output_directory,
    final LogUsableType in_log)
    throws CompactorException
  {
    try {
      final LogType log = NullCheck.notNull(in_log, "Log").with("compactor");
      NullCheck.notNull(in_directory, "Directory");
      NullCheck.notNull(in_output_directory, "Output directory");

      final ShaderMetaType meta = Compactor.getMeta(in_directory, in_log);
      if (meta.getCompactMappings().isSome()) {
        throw new IllegalArgumentException("Program is already compacted");
      }

      in_output_directory.mkdirs();
      if (in_output_directory.isDirectory() == false) {
        throw new IOException(String.format(
          "Not a directory: %s",
          in_output_directory));
      }

      meta.matchType(new ShaderMetaVisitorType<Unit, CompactorException>() {
        @Override public Unit fragment(
          final ShaderMetaFragment p)
          throws CompactorException
        {
          try {
            Compactor.compactFragment(
              in_directory,
              in_output_directory,
              p,
              log);
            return Unit.unit();
          } catch (final Exception e) {
            throw new CompactorException(e);
          }
        }

        @Override public Unit program(
          final ShaderMetaProgram p)
          throws CompactorException
        {
          try {
            Compactor.compactProgram(in_output_directory, p, log);
            return Unit.unit();
          } catch (final Exception e) {
            throw new CompactorException(e);
          }
        }

        @Override public Unit vertex(
          final ShaderMetaVertex p)
          throws CompactorException
        {
          try {
            Compactor
              .compactVertex(in_directory, in_output_directory, p, log);
            return Unit.unit();
          } catch (final Exception e) {
            throw new CompactorException(e);
          }
        }
      });

    } catch (final ValidityException e) {
      throw new CompactorException(e);
    } catch (final ParsingException e) {
      throw new CompactorException(e);
    } catch (final IOException e) {
      throw new CompactorException(e);
    } catch (final SAXException e) {
      throw new CompactorException(e);
    } catch (final ParserConfigurationException e) {
      throw new CompactorException(e);
    }
  }

  private static void compactVertex(
    final File directory,
    final File output_directory,
    final ShaderMetaVertex p,
    final LogUsableType log)
    throws NoSuchAlgorithmException,
      IOException
  {
    final SortedMap<Version, String> mappings =
      new TreeMap<Version, String>();
    final Map<String, Source> sources = new HashMap<String, Source>();

    for (final Integer v : p.getSupportsES()) {
      final StringBuilder v_name = new StringBuilder();
      v_name.append("glsl-es-");
      v_name.append(v);
      v_name.append(".v");
      final File v_file = new File(directory, v_name.toString());
      final Source source = Source.fromFile(v_file, log);
      mappings.put(
        new Version(v.intValue(), API.API_GLSL_ES),
        source.getHash());
      sources.put(source.getHash(), source);
    }

    for (final Integer v : p.getSupportsFull()) {
      final StringBuilder v_name = new StringBuilder();
      v_name.append("glsl-");
      v_name.append(v);
      v_name.append(".v");
      final File v_file = new File(directory, v_name.toString());
      final Source source = Source.fromFile(v_file, log);
      mappings.put(new Version(v.intValue(), API.API_GLSL), source.getHash());
      sources.put(source.getHash(), source);
    }

    Compactor.writeSources(output_directory, log, sources, ".v");
    Compactor.writeMappings(output_directory, p, log, mappings);
  }

  private static ShaderMetaType getMeta(
    final File directory,
    final LogUsableType log)
    throws ValidityException,
      ParsingException,
      IOException,
      SAXException,
      ParserConfigurationException
  {
    final File meta_file = new File(directory, "meta.xml");
    final FileInputStream meta_stream = new FileInputStream(meta_file);
    try {
      return ShaderMeta.fromStream(meta_stream, log);
    } finally {
      meta_stream.close();
    }
  }

  private static void writeMappings(
    final File output_directory,
    final ShaderMetaType p,
    final LogUsableType log,
    final SortedMap<Version, String> mappings)
    throws FileNotFoundException,
      UnsupportedEncodingException,
      IOException
  {
    final ShaderMetaType p_mod = p.withCompactMappings(mappings);
    Compactor.writeMeta(output_directory, log, p_mod);
  }

  private static void writeMeta(
    final File output_directory,
    final LogUsableType log,
    final ShaderMetaType p_mod)
    throws FileNotFoundException,
      UnsupportedEncodingException,
      IOException
  {
    final File out_meta = new File(output_directory, "meta.xml");
    log.debug("writing " + out_meta);

    final FileOutputStream stream = new FileOutputStream(out_meta);
    final Serializer s = new Serializer(stream, "UTF-8");
    s.setIndent(2);
    s.setMaxLength(160);
    s.write(new Document(p_mod.toXML()));
    s.flush();
    stream.close();
  }

  private static void writeSource(
    final File file,
    final Source value)
    throws IOException
  {
    final PrintWriter b =
      new PrintWriter(new BufferedWriter(new FileWriter(file)));
    try {
      for (final String l : value.getLines()) {
        b.println(l);
      }
    } finally {
      b.flush();
      b.close();
    }
  }

  private static void writeSources(
    final File output_directory,
    final LogUsableType log,
    final Map<String, Source> sources,
    final String suffix)
    throws IOException
  {
    for (final String hash : sources.keySet()) {
      assert hash != null;
      final Source source = sources.get(hash);
      assert source != null;
      final File file = new File(output_directory, hash + suffix);
      log.debug("writing " + file);
      Compactor.writeSource(file, source);
    }
  }

  private Compactor()
  {
    throw new UnreachableCodeException();
  }

}
