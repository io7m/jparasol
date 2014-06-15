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

package com.io7m.jparasol.glsl.serialization;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jfunctional.None;
import com.io7m.jfunctional.OptionType;
import com.io7m.jfunctional.OptionVisitorType;
import com.io7m.jfunctional.Some;
import com.io7m.jlog.LogType;
import com.io7m.jlog.LogUsableType;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.core.GVersionES;
import com.io7m.jparasol.core.GVersionFull;
import com.io7m.jparasol.core.GVersionType;
import com.io7m.jparasol.core.GVersionVisitorType;
import com.io7m.jparasol.core.JPCompactedFragmentShader;
import com.io7m.jparasol.core.JPCompactedFragmentShaderMeta;
import com.io7m.jparasol.core.JPCompactedVertexShader;
import com.io7m.jparasol.core.JPCompactedVertexShaderMeta;
import com.io7m.jparasol.core.JPHashedLines;
import com.io7m.jparasol.core.JPUncompactedFragmentShader;
import com.io7m.jparasol.core.JPUncompactedFragmentShaderMeta;
import com.io7m.jparasol.core.JPUncompactedProgramShaderMeta;
import com.io7m.jparasol.core.JPUncompactedVertexShader;
import com.io7m.jparasol.core.JPUncompactedVertexShaderMeta;
import com.io7m.jparasol.xml.XMLCompactedFragmentShaderMeta;
import com.io7m.jparasol.xml.XMLCompactedVertexShaderMeta;
import com.io7m.jparasol.xml.XMLUncompactedFragmentShaderMeta;
import com.io7m.jparasol.xml.XMLUncompactedProgramShaderMeta;
import com.io7m.jparasol.xml.XMLUncompactedVertexShaderMeta;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * A serializer that writes shaders into a zip archive.
 */

@EqualityReference public final class GSerializerZip implements
  GSerializerType
{
  /**
   * Construct a new serializer that will write shaders to the root of the
   * given zip file stream.
   * 
   * @param in_stream
   *          The zip output stream.
   * @param in_log
   *          A log interface.
   * 
   * @return A new serializer.
   */

  public static GSerializerType newSerializer(
    final ZipOutputStream in_stream,
    final LogUsableType in_log)
  {
    return new GSerializerZip(in_stream, in_log);
  }

  private static void serializeDocument(
    final ZipOutputStream stream,
    final Element root)
    throws UnsupportedEncodingException,
      IOException
  {
    final Document doc = new Document(root);
    final Serializer s = new Serializer(stream, "UTF-8");
    s.setIndent(2);
    s.setMaxLength(160);
    s.write(doc);
    s.flush();
  }

  private static String sourceNameForHash(
    final String hash,
    final String suffix)
  {
    final String r = String.format("%s.%s", hash, suffix);
    assert r != null;
    return r;
  }

  @SuppressWarnings("boxing") private static String sourceNameForVersion(
    final GVersionType version,
    final String suffix)
  {
    return version
      .versionAccept(new GVersionVisitorType<String, UnreachableCodeException>() {
        @Override public String versionVisitES(
          final GVersionES v)
        {
          final String r =
            String.format("glsl-es-%d.%s", v.versionGetNumber(), suffix);
          assert r != null;
          return r;
        }

        @Override public String versionVisitFull(
          final GVersionFull v)
        {
          final String r =
            String.format("glsl-%d.%s", v.versionGetNumber(), suffix);
          assert r != null;
          return r;
        }
      });
  }

  private final LogType         log;
  private final ZipOutputStream stream;

  private GSerializerZip(
    final ZipOutputStream in_stream,
    final LogUsableType in_log)
  {
    this.stream = NullCheck.notNull(in_stream, "Stream");
    this.log = NullCheck.notNull(in_log, "Log").with("serializer-zip");
  }

  private void announceFile(
    final String name)
  {
    final String r = String.format("file %s", name);
    assert r != null;
    this.log.debug(r);
  }

  private void announceShader(
    final String name)
  {
    final String r = String.format("shader %s", name);
    assert r != null;
    this.log.debug(r);
  }

  @Override public void close()
    throws IOException
  {
    this.log.debug("closing");
    this.stream.flush();
    this.stream.close();
  }

  @Override public void serializeCompactedFragmentShader(
    final JPCompactedFragmentShader shader)
    throws IOException
  {
    final JPCompactedFragmentShaderMeta meta = shader.getMeta();
    assert meta != null;

    this.announceShader(meta.getName());
    this.serializeCompactedFragmentShaderMeta(meta);
    this
      .writeCompactedSources(meta.getName(), shader.getSourcesByHash(), "f");
  }

  private void serializeCompactedFragmentShaderMeta(
    final JPCompactedFragmentShaderMeta meta)
    throws FileNotFoundException,
      UnsupportedEncodingException,
      IOException
  {
    final String name = String.format("%s/meta.xml", meta.getName());
    assert name != null;
    this.announceFile(name);

    final ZipEntry entry = new ZipEntry(name);
    entry.setMethod(ZipEntry.DEFLATED);
    this.stream.putNextEntry(entry);

    final Element root = XMLCompactedFragmentShaderMeta.serializeToXML(meta);
    GSerializerZip.serializeDocument(this.stream, root);

    this.stream.closeEntry();
  }

  @Override public void serializeCompactedVertexShader(
    final JPCompactedVertexShader shader)
    throws IOException
  {
    final JPCompactedVertexShaderMeta meta = shader.getMeta();
    assert meta != null;

    this.announceShader(meta.getName());
    this.serializeCompactedVertexShaderMeta(meta);
    this
      .writeCompactedSources(meta.getName(), shader.getSourcesByHash(), "v");
  }

  private void serializeCompactedVertexShaderMeta(
    final JPCompactedVertexShaderMeta meta)
    throws IOException
  {
    final String name = String.format("%s/meta.xml", meta.getName());
    assert name != null;
    this.announceFile(name);

    final ZipEntry entry = new ZipEntry(name);
    entry.setMethod(ZipEntry.DEFLATED);
    this.stream.putNextEntry(entry);

    final Element root = XMLCompactedVertexShaderMeta.serializeToXML(meta);
    GSerializerZip.serializeDocument(this.stream, root);

    this.stream.closeEntry();
  }

  @Override public void serializeUncompactedFragmentShader(
    final JPUncompactedFragmentShader shader)
    throws IOException
  {
    final JPUncompactedFragmentShaderMeta meta = shader.getMeta();
    assert meta != null;

    this.announceShader(meta.getName());
    this.serializeUncompactedFragmentShaderMeta(meta);
    this.writeUncompactedSources(meta.getName(), shader.getSources(), "f");
  }

  private void serializeUncompactedFragmentShaderMeta(
    final JPUncompactedFragmentShaderMeta meta)
    throws FileNotFoundException,
      UnsupportedEncodingException,
      IOException
  {
    final String name = String.format("%s/meta.xml", meta.getName());
    assert name != null;
    this.announceFile(name);

    final ZipEntry entry = new ZipEntry(name);
    entry.setMethod(ZipEntry.DEFLATED);
    this.stream.putNextEntry(entry);

    final Element root =
      XMLUncompactedFragmentShaderMeta.serializeToXML(meta);
    GSerializerZip.serializeDocument(this.stream, root);

    this.stream.closeEntry();
  }

  @Override public void serializeUncompactedProgramShader(
    final JPUncompactedProgramShaderMeta meta,
    final OptionType<String> name)
    throws IOException
  {
    final String actual =
      name.accept(new OptionVisitorType<String, String>() {
        @Override public String none(
          final None<String> n)
        {
          return meta.getName();
        }

        @Override public String some(
          final Some<String> s)
        {
          return s.get();
        }
      });

    this.announceShader(actual);
    this.serializeUncompactedProgramShaderMeta(meta, actual);
  }

  private void serializeUncompactedProgramShaderMeta(
    final JPUncompactedProgramShaderMeta meta,
    final String program_name)
    throws FileNotFoundException,
      UnsupportedEncodingException,
      IOException
  {
    final String name = String.format("%s/meta.xml", program_name);
    assert name != null;
    this.announceFile(name);

    final ZipEntry entry = new ZipEntry(name);
    entry.setMethod(ZipEntry.DEFLATED);
    this.stream.putNextEntry(entry);
    final Element root = XMLUncompactedProgramShaderMeta.serializeToXML(meta);
    GSerializerZip.serializeDocument(this.stream, root);
    this.stream.closeEntry();
  }

  @Override public void serializeUncompactedVertexShader(
    final JPUncompactedVertexShader shader)
    throws IOException
  {
    final JPUncompactedVertexShaderMeta meta = shader.getMeta();
    assert meta != null;

    this.announceShader(shader.getName());
    this.serializeUncompactedVertexShaderMeta(meta);
    this.writeUncompactedSources(meta.getName(), shader.getSources(), "v");
  }

  private void serializeUncompactedVertexShaderMeta(
    final JPUncompactedVertexShaderMeta meta)
    throws FileNotFoundException,
      UnsupportedEncodingException,
      IOException
  {
    final String name = String.format("%s/meta.xml", meta.getName());
    assert name != null;
    this.announceFile(name);

    final ZipEntry entry = new ZipEntry(name);
    entry.setMethod(ZipEntry.DEFLATED);
    this.stream.putNextEntry(entry);

    final Element root = XMLUncompactedVertexShaderMeta.serializeToXML(meta);
    GSerializerZip.serializeDocument(this.stream, root);

    this.stream.closeEntry();
  }

  private void writeCompactedSources(
    final String shader,
    final Map<String, JPHashedLines> by_hash,
    final String suffix)
    throws IOException
  {
    for (final String version : by_hash.keySet()) {
      assert version != null;
      final JPHashedLines source = by_hash.get(version);
      assert source != null;
      final String name = GSerializerZip.sourceNameForHash(version, suffix);
      final String file = String.format("%s/%s", shader, name);
      assert file != null;
      this.writeSourcesOnce(file, source.getLines());
    }
  }

  private void writeSourcesOnce(
    final String file,
    final List<String> sources)
    throws IOException
  {
    this.announceFile(file);

    final ZipEntry entry = new ZipEntry(file);
    entry.setMethod(ZipEntry.DEFLATED);
    this.stream.putNextEntry(entry);

    final PrintWriter writer = new PrintWriter(this.stream);
    try {
      for (final String l : sources) {
        writer.println(l);
      }
    } finally {
      writer.flush();
    }

    this.stream.closeEntry();
  }

  private void writeUncompactedSources(
    final String shader,
    final Map<GVersionType, List<String>> map,
    final String suffix)
    throws IOException
  {
    for (final GVersionType version : map.keySet()) {
      assert version != null;
      final List<String> sources = map.get(version);
      assert sources != null;
      final String name =
        GSerializerZip.sourceNameForVersion(version, suffix);
      final String file = String.format("%s/%s", shader, name);
      assert file != null;
      this.writeSourcesOnce(file, sources);
    }
  }
}
