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
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;

import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.ValidityException;

import org.xml.sax.SAXException;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jlog.LogUsableType;
import com.io7m.jnull.NullCheck;

/**
 * <p>
 * A compactor that eliminates duplicate GLSL shader files.
 * </p>
 */

@EqualityReference public final class PGLSLCompactor
{
  @EqualityReference private static final class Source
  {
    @SuppressWarnings("boxing") public static Source newSource(
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

      log.debug("hash " + hash.toString());
      return new Source(lines, hash.toString());
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
      return Collections.unmodifiableList(this.lines);
    }
  }

  private static PGLSLMetaXML getMeta(
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
      return PGLSLMetaXML.fromStream(meta_stream, log);
    } finally {
      meta_stream.close();
    }
  }

  private static void loadSources(
    final LogUsableType log,
    final SortedSet<Integer> supports,
    final String name_prefix,
    final File directory,
    final NavigableMap<Integer, Source> vertex_sources_by_version,
    final NavigableMap<Integer, Source> fragment_sources_by_version,
    final Map<String, Source> sources_by_hash)
    throws IOException,
      NoSuchAlgorithmException
  {
    final NavigableSet<Integer> supported = new TreeSet<Integer>(supports);
    final Iterator<Integer> iter = supported.descendingIterator();

    while (iter.hasNext()) {
      final Integer version = iter.next();

      final StringBuilder v_name = new StringBuilder();
      v_name.append(name_prefix);
      v_name.append(version);
      v_name.append(".v");
      final File v_file = new File(directory, v_name.toString());

      final StringBuilder f_name = new StringBuilder();
      f_name.append(name_prefix);
      f_name.append(version);
      f_name.append(".f");
      final File f_file = new File(directory, f_name.toString());

      log.debug("reading " + v_file);
      final List<String> v_lines = PGLSLCompactor.readLines(v_file);
      if (v_lines.size() < 1) {
        throw new IOException(v_file + " is empty!");
      }
      PGLSLCompactor.stripVersion(v_file, v_lines);
      final Source v_source = Source.newSource(v_lines, log);

      log.debug("reading " + f_file);
      final List<String> f_lines = PGLSLCompactor.readLines(f_file);
      if (f_lines.size() < 1) {
        throw new IOException(f_file + " is empty!");
      }
      PGLSLCompactor.stripVersion(f_file, f_lines);
      final Source f_source = Source.newSource(f_lines, log);

      vertex_sources_by_version.put(version, v_source);
      fragment_sources_by_version.put(version, f_source);

      sources_by_hash.put(v_source.getHash(), v_source);
      sources_by_hash.put(f_source.getHash(), f_source);
    }
  }

  /**
   * Construct a new compactor.
   * 
   * @param directory
   *          The directory
   * @param output_directory
   *          The output directory
   * @param log
   *          The log
   * @return A new compactor
   * @throws ValidityException
   *           On XML parser errors
   * @throws ParsingException
   *           On XML parser errors
   * @throws IOException
   *           On I/O errors
   * @throws NoSuchAlgorithmException
   *           On JVMs that don't support the required hashing algorithms
   * @throws SAXException
   *           On XML parser errors
   * @throws ParserConfigurationException
   *           On XML parser errors
   */

  public static PGLSLCompactor newCompactor(
    final File directory,
    final File output_directory,
    final LogUsableType log)
    throws ValidityException,
      ParsingException,
      IOException,
      NoSuchAlgorithmException,
      SAXException,
      ParserConfigurationException
  {
    return new PGLSLCompactor(directory, output_directory, log);
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
      throw new IOException("File " + file + " does not begin with #version");
    }
    lines.remove(0);
  }

  private final File                                 directory;
  private final NavigableMap<Integer, Source>        es_fragment_sources_by_version;
  private final NavigableMap<Integer, Source>        es_vertex_sources_by_version;
  private final NavigableMap<Integer, Source>        full_fragment_sources_by_version;
  private final NavigableMap<Integer, Source>        full_vertex_sources_by_version;
  private final LogUsableType                        log;
  private final PGLSLMetaXML                         meta;
  private final File                                 output_directory;
  private final Map<String, Source>                  sources_by_hash;
  private final SortedMap<Version, CompactedShaders> mappings;

  private PGLSLCompactor(
    final File in_directory,
    final File in_output_directory,
    final LogUsableType in_log)
    throws ValidityException,
      ParsingException,
      IOException,
      NoSuchAlgorithmException,
      SAXException,
      ParserConfigurationException
  {
    this.log = NullCheck.notNull(in_log, "Log").with("compactor");
    this.directory = NullCheck.notNull(in_directory, "Directory");
    this.output_directory =
      NullCheck.notNull(in_output_directory, "Output directory");

    this.meta = PGLSLCompactor.getMeta(in_directory, in_log);
    if (this.meta.isCompacted()) {
      throw new IllegalArgumentException("Program is already compacted");
    }

    this.es_vertex_sources_by_version = new TreeMap<Integer, Source>();
    this.es_fragment_sources_by_version = new TreeMap<Integer, Source>();
    this.full_vertex_sources_by_version = new TreeMap<Integer, Source>();
    this.full_fragment_sources_by_version = new TreeMap<Integer, Source>();
    this.sources_by_hash = new HashMap<String, Source>();
    this.mappings = new TreeMap<Version, CompactedShaders>();

    PGLSLCompactor.loadSources(
      in_log,
      this.meta.getSupportsES(),
      "glsl-es-",
      in_directory,
      this.es_vertex_sources_by_version,
      this.es_fragment_sources_by_version,
      this.sources_by_hash);

    assert this.es_fragment_sources_by_version.size() == this.es_vertex_sources_by_version
      .size();

    this.makeMappingsForAPI(
      this.es_fragment_sources_by_version,
      this.es_vertex_sources_by_version,
      API.API_GLSL_ES);

    PGLSLCompactor.loadSources(
      in_log,
      this.meta.getSupportsFull(),
      "glsl-",
      in_directory,
      this.full_vertex_sources_by_version,
      this.full_fragment_sources_by_version,
      this.sources_by_hash);

    assert this.full_fragment_sources_by_version.size() == this.full_vertex_sources_by_version
      .size();

    this.makeMappingsForAPI(
      this.full_fragment_sources_by_version,
      this.full_vertex_sources_by_version,
      API.API_GLSL);

    this.compact();
  }

  private void makeMappingsForAPI(
    final NavigableMap<Integer, Source> fragment_sources,
    final NavigableMap<Integer, Source> vertex_sources,
    final API api)
  {
    for (final Integer v : fragment_sources.keySet()) {
      assert vertex_sources.containsKey(v);
      final Version ver = new Version(v.intValue(), api);
      final String hash_vertex = vertex_sources.get(v).getHash();
      final String hash_fragment = fragment_sources.get(v).getHash();
      final CompactedShaders cs =
        new CompactedShaders(hash_vertex, hash_fragment);
      this.mappings.put(ver, cs);
    }
  }

  private void compact()
    throws IOException
  {
    this.log.debug("creating " + this.output_directory);

    if (this.output_directory.mkdirs() == false) {
      if (this.output_directory.isDirectory() == false) {
        throw new IOException("Could not create " + this.output_directory);
      }
    }

    for (final Entry<String, Source> e : this.sources_by_hash.entrySet()) {
      final File file = new File(this.output_directory, e.getKey() + ".g");
      this.log.debug("writing " + file);
      PGLSLCompactor.writeSource(file, e.getValue());
    }

    {
      final PGLSLMetaXML meta_extra = this.meta.withCompaction(this.mappings);

      final File out_meta = new File(this.output_directory, "meta.xml");
      this.log.debug("writing " + out_meta);

      final FileOutputStream stream = new FileOutputStream(out_meta);
      final Serializer s = new Serializer(stream, "UTF-8");
      s.setIndent(2);
      s.setMaxLength(160);
      s.write(new Document(meta_extra.toXML()));
      s.flush();
      stream.close();
    }
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
}
