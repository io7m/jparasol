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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jfunctional.None;
import com.io7m.jfunctional.OptionType;
import com.io7m.jfunctional.OptionVisitorType;
import com.io7m.jfunctional.Some;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.core.CompactedFragmentShader;
import com.io7m.jparasol.core.CompactedFragmentShaderMeta;
import com.io7m.jparasol.core.CompactedVertexShader;
import com.io7m.jparasol.core.CompactedVertexShaderMeta;
import com.io7m.jparasol.core.GVersionES;
import com.io7m.jparasol.core.GVersionFull;
import com.io7m.jparasol.core.GVersionType;
import com.io7m.jparasol.core.GVersionVisitorType;
import com.io7m.jparasol.core.HashedLines;
import com.io7m.jparasol.core.UncompactedFragmentShader;
import com.io7m.jparasol.core.UncompactedFragmentShaderMeta;
import com.io7m.jparasol.core.UncompactedProgramShaderMeta;
import com.io7m.jparasol.core.UncompactedVertexShader;
import com.io7m.jparasol.core.UncompactedVertexShaderMeta;
import com.io7m.jparasol.xml.XMLCompactedFragmentShaderMeta;
import com.io7m.jparasol.xml.XMLCompactedVertexShaderMeta;
import com.io7m.jparasol.xml.XMLUncompactedFragmentShaderMeta;
import com.io7m.jparasol.xml.XMLUncompactedProgramShaderMeta;
import com.io7m.jparasol.xml.XMLUncompactedVertexShaderMeta;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * A serializer that writes shaders into a given directory.
 */

@EqualityReference public final class GSerializerFile implements
  GSerializerType
{
  private static void createOutputDirectory(
    final boolean replace,
    final File out_dir)
    throws IOException
  {
    if (out_dir.exists() && (replace == false)) {
      throw new IOException(String.format("File already exists: %s", out_dir));
    }

    out_dir.mkdirs();
    if (out_dir.isDirectory() == false) {
      throw new IOException(String.format("Not a directory: %s", out_dir));
    }
  }

  /**
   * Construct a new serializer that will write shaders to the given base
   * directory.
   * 
   * @param in_base
   *          The base directory.
   * @param in_replace
   *          Whether or not writing should replace existing files.
   * 
   * @return A new serializer.
   */

  public static GSerializerType newSerializer(
    final File in_base,
    final boolean in_replace)
  {
    return new GSerializerFile(in_base, in_replace);
  }

  private static void serializeCompactedFragmentShaderMeta(
    final CompactedFragmentShaderMeta meta,
    final File out_dir)
    throws FileNotFoundException,
      UnsupportedEncodingException,
      IOException
  {
    final File out_meta = new File(out_dir, "meta.xml");
    final FileOutputStream out_meta_stream = new FileOutputStream(out_meta);
    try {
      final Element root =
        XMLCompactedFragmentShaderMeta.serializeToXML(meta);
      GSerializerFile.serializeDocument(out_meta_stream, root);
    } finally {
      out_meta_stream.close();
    }
  }

  private static void serializeCompactedVertexShaderMeta(
    final CompactedVertexShaderMeta meta,
    final File out_dir)
    throws FileNotFoundException,
      UnsupportedEncodingException,
      IOException
  {
    final File out_meta = new File(out_dir, "meta.xml");
    final FileOutputStream out_meta_stream = new FileOutputStream(out_meta);
    try {
      final Element root = XMLCompactedVertexShaderMeta.serializeToXML(meta);
      GSerializerFile.serializeDocument(out_meta_stream, root);
    } finally {
      out_meta_stream.close();
    }
  }

  private static void serializeDocument(
    final FileOutputStream out_meta_stream,
    final Element root)
    throws UnsupportedEncodingException,
      IOException
  {
    final Document doc = new Document(root);
    final Serializer s = new Serializer(out_meta_stream, "UTF-8");
    s.setIndent(2);
    s.setMaxLength(160);
    s.write(doc);
    s.flush();
  }

  private static void serializeUncompactedFragmentShaderMeta(
    final UncompactedFragmentShaderMeta meta,
    final File out_dir)
    throws FileNotFoundException,
      UnsupportedEncodingException,
      IOException
  {
    final File out_meta = new File(out_dir, "meta.xml");
    final FileOutputStream out_meta_stream = new FileOutputStream(out_meta);
    try {
      final Element root =
        XMLUncompactedFragmentShaderMeta.serializeToXML(meta);
      GSerializerFile.serializeDocument(out_meta_stream, root);
    } finally {
      out_meta_stream.close();
    }
  }

  private static void serializeUncompactedProgramShaderMeta(
    final UncompactedProgramShaderMeta meta,
    final File out_dir)
    throws IOException
  {
    final File out_meta = new File(out_dir, "meta.xml");
    final FileOutputStream out_meta_stream = new FileOutputStream(out_meta);
    try {
      final Element root =
        XMLUncompactedProgramShaderMeta.serializeToXML(meta);
      GSerializerFile.serializeDocument(out_meta_stream, root);
    } finally {
      out_meta_stream.close();
    }
  }

  private static void serializeUncompactedVertexShaderMeta(
    final UncompactedVertexShaderMeta meta,
    final File out_dir)
    throws FileNotFoundException,
      UnsupportedEncodingException,
      IOException
  {
    final File out_meta = new File(out_dir, "meta.xml");
    final FileOutputStream out_meta_stream = new FileOutputStream(out_meta);
    try {
      final Element root =
        XMLUncompactedVertexShaderMeta.serializeToXML(meta);
      GSerializerFile.serializeDocument(out_meta_stream, root);
    } finally {
      out_meta_stream.close();
    }
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

  private static void writeCompactedSources(
    final File out_dir,
    final Map<String, HashedLines> by_hash,
    final String suffix)
    throws FileNotFoundException
  {
    for (final String version : by_hash.keySet()) {
      assert version != null;
      final HashedLines source = by_hash.get(version);
      assert source != null;
      final String name = GSerializerFile.sourceNameForHash(version, suffix);
      final File file = new File(out_dir, name);
      GSerializerFile.writeSourcesOnce(file, source.getLines());
    }
  }

  private static void writeSourcesOnce(
    final File file,
    final List<String> sources)
    throws FileNotFoundException
  {
    final PrintWriter writer = new PrintWriter(file);
    try {
      for (final String l : sources) {
        writer.println(l);
      }
    } finally {
      writer.flush();
      writer.close();
    }
  }

  private static void writeUncompactedSources(
    final File out_dir,
    final Map<GVersionType, List<String>> map,
    final String suffix)
    throws FileNotFoundException
  {
    for (final GVersionType version : map.keySet()) {
      assert version != null;
      final List<String> source = map.get(version);
      assert source != null;
      final String name =
        GSerializerFile.sourceNameForVersion(version, suffix);
      final File file = new File(out_dir, name);
      GSerializerFile.writeSourcesOnce(file, source);
    }
  }

  private final File    base;
  private final boolean replace;

  private GSerializerFile(
    final File in_base,
    final boolean in_replace)
  {
    this.base = NullCheck.notNull(in_base, "Base directory");
    this.replace = in_replace;
  }

  @Override public void close()
    throws IOException
  {
    // Nothing.
  }

  @Override public void serializeCompactedFragmentShader(
    final CompactedFragmentShader shader)
    throws IOException
  {
    NullCheck.notNull(shader, "Shader");

    final File out_dir = new File(this.base, shader.getName());
    GSerializerFile.createOutputDirectory(this.replace, out_dir);

    GSerializerFile.serializeCompactedFragmentShaderMeta(
      shader.getMeta(),
      out_dir);

    GSerializerFile.writeCompactedSources(
      out_dir,
      shader.getSourcesByHash(),
      "f");
  }

  @Override public void serializeCompactedVertexShader(
    final CompactedVertexShader shader)
    throws IOException
  {
    NullCheck.notNull(shader, "Shader");

    final File out_dir = new File(this.base, shader.getName());
    GSerializerFile.createOutputDirectory(this.replace, out_dir);

    GSerializerFile.serializeCompactedVertexShaderMeta(
      shader.getMeta(),
      out_dir);

    GSerializerFile.writeCompactedSources(
      out_dir,
      shader.getSourcesByHash(),
      "v");
  }

  @Override public void serializeUncompactedFragmentShader(
    final UncompactedFragmentShader shader)
    throws IOException
  {
    NullCheck.notNull(shader, "Shader");

    final File out_dir = new File(this.base, shader.getName());
    GSerializerFile.createOutputDirectory(this.replace, out_dir);

    GSerializerFile.serializeUncompactedFragmentShaderMeta(
      shader.getMeta(),
      out_dir);

    GSerializerFile
      .writeUncompactedSources(out_dir, shader.getSources(), "f");
  }

  @Override public void serializeUncompactedProgramShader(
    final UncompactedProgramShaderMeta meta,
    final OptionType<String> name)
    throws IOException
  {
    NullCheck.notNull(meta, "Meta");

    @SuppressWarnings("synthetic-access") final File out_dir =
      name.accept(new OptionVisitorType<String, File>() {
        @Override public File none(
          final None<String> n)
        {
          return new File(GSerializerFile.this.base, meta.getName());
        }

        @Override public File some(
          final Some<String> s)
        {
          return new File(GSerializerFile.this.base, s.get());
        }
      });

    GSerializerFile.createOutputDirectory(this.replace, out_dir);
    GSerializerFile.serializeUncompactedProgramShaderMeta(meta, out_dir);
  }

  @Override public void serializeUncompactedVertexShader(
    final UncompactedVertexShader shader)
    throws IOException
  {
    NullCheck.notNull(shader, "Shader");

    final File out_dir = new File(this.base, shader.getName());
    GSerializerFile.createOutputDirectory(this.replace, out_dir);

    GSerializerFile.serializeUncompactedVertexShaderMeta(
      shader.getMeta(),
      out_dir);

    GSerializerFile
      .writeUncompactedSources(out_dir, shader.getSources(), "v");
  }
}
