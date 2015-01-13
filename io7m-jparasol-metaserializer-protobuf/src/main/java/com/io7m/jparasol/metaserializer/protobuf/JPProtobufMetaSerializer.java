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

package com.io7m.jparasol.metaserializer.protobuf;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.core.GVersionES;
import com.io7m.jparasol.core.GVersionFull;
import com.io7m.jparasol.core.GVersionType;
import com.io7m.jparasol.core.GVersionVisitorType;
import com.io7m.jparasol.core.JPCompactedFragmentShaderMeta;
import com.io7m.jparasol.core.JPCompactedVertexShaderMeta;
import com.io7m.jparasol.core.JPCompiledShaderMetaType;
import com.io7m.jparasol.core.JPCompiledShaderMetaVisitorType;
import com.io7m.jparasol.core.JPFragmentInput;
import com.io7m.jparasol.core.JPFragmentOutput;
import com.io7m.jparasol.core.JPFragmentParameter;
import com.io7m.jparasol.core.JPFragmentShaderMetaType;
import com.io7m.jparasol.core.JPFragmentShaderMetaVisitorType;
import com.io7m.jparasol.core.JPUncompactedFragmentShaderMeta;
import com.io7m.jparasol.core.JPUncompactedProgramShaderMeta;
import com.io7m.jparasol.core.JPUncompactedVertexShaderMeta;
import com.io7m.jparasol.core.JPVertexInput;
import com.io7m.jparasol.core.JPVertexOutput;
import com.io7m.jparasol.core.JPVertexParameter;
import com.io7m.jparasol.core.JPVertexShaderMetaType;
import com.io7m.jparasol.core.JPVertexShaderMetaVisitorType;
import com.io7m.jparasol.metaserializer.JPMetaSerializerType;
import com.io7m.jparasol.metaserializer.protobuf.types.ProgramMeta;
import com.io7m.jparasol.metaserializer.protobuf.types.ProgramMeta.Meta;
import com.io7m.jparasol.metaserializer.protobuf.types.ProgramMeta.PGLSLApi;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * A metadata serializer that uses Protobuf.
 */

@EqualityReference public final class JPProtobufMetaSerializer implements
  JPMetaSerializerType
{
  /**
   * Metadata format version.
   */

  public static final int    META_VERSION = 1;

  /**
   * The suggested metadata filename.
   */

  public static final String SUGGESTED_FILENAME;

  /**
   * The suggested metadata filename suffix.
   */

  public static final String SUGGESTED_FILENAME_SUFFIX;

  static {
    SUGGESTED_FILENAME_SUFFIX = "ppsm";
    SUGGESTED_FILENAME =
      "meta." + JPProtobufMetaSerializer.SUGGESTED_FILENAME_SUFFIX;
  }

  private static void makeFragmentShaderCommonInputs(
    final SortedSet<JPFragmentInput> frag_ins,
    final ProgramMeta.PFragmentShaderCommon.Builder fsco)
  {
    for (final JPFragmentInput fi : frag_ins) {
      assert fi != null;
      final ProgramMeta.PFragmentShaderInput.Builder pfsi =
        ProgramMeta.PFragmentShaderInput.newBuilder();
      pfsi.setName(fi.getName());
      pfsi.setType(fi.getType());
      fsco.addInputs(pfsi);
    }
  }

  private static void makeFragmentShaderCommonOutputs(
    final SortedMap<Integer, JPFragmentOutput> frag_outs,
    final ProgramMeta.PFragmentShaderCommon.Builder fsco)
  {
    for (final Integer index : frag_outs.keySet()) {
      assert index != null;
      final JPFragmentOutput out = frag_outs.get(index);
      assert out != null;
      final ProgramMeta.PFragmentShaderOutput.Builder pfso =
        ProgramMeta.PFragmentShaderOutput.newBuilder();
      pfso.setIndex(index.intValue());
      pfso.setName(out.getName());
      pfso.setType(out.getType());
      fsco.addOutputs(pfso);
    }
  }

  private static void makeFragmentShaderCommonParameters(
    final SortedSet<JPFragmentParameter> frag_params,
    final ProgramMeta.PFragmentShaderCommon.Builder fsco)
  {
    for (final JPFragmentParameter p : frag_params) {
      assert p != null;
      final ProgramMeta.PFragmentShaderParameter.Builder pfsp =
        ProgramMeta.PFragmentShaderParameter.newBuilder();
      pfsp.setName(p.getName());
      pfsp.setType(p.getType());
      fsco.addParameters(pfsp);
    }
  }

  private static void makeFragmentShaderCommonVersions(
    final SortedSet<GVersionES> es,
    final SortedSet<GVersionFull> full,
    final ProgramMeta.PFragmentShaderCommon.Builder fsco)
  {
    for (final GVersionES v : es) {
      assert v != null;
      final ProgramMeta.PGLSLVersion.Builder pv =
        ProgramMeta.PGLSLVersion.newBuilder();
      pv.setApi(PGLSLApi.PGLSL_ES);
      pv.setNumber(v.versionGetNumber());
      fsco.addSupports(pv);
    }

    for (final GVersionFull v : full) {
      assert v != null;
      final ProgramMeta.PGLSLVersion.Builder pv =
        ProgramMeta.PGLSLVersion.newBuilder();
      pv.setApi(PGLSLApi.PGLSL_FULL);
      pv.setNumber(v.versionGetNumber());
      fsco.addSupports(pv);
    }
  }

  private static void makeFragmentShaderCompactedHashes(
    final ProgramMeta.PFragmentShaderCompacted.Builder fsc,
    final Map<GVersionType, String> frag_version_hashes)
  {
    for (final GVersionType v : frag_version_hashes.keySet()) {
      assert v != null;
      final String h = frag_version_hashes.get(v);
      final ProgramMeta.PVersionHash.Builder pvh =
        ProgramMeta.PVersionHash.newBuilder();
      pvh
        .setApi(v
          .versionAccept(new GVersionVisitorType<ProgramMeta.PGLSLApi, UnreachableCodeException>() {
            @Override public PGLSLApi versionVisitES(
              final GVersionES ve)
            {
              return PGLSLApi.PGLSL_ES;
            }

            @Override public PGLSLApi versionVisitFull(
              final GVersionFull vf)
            {
              return PGLSLApi.PGLSL_FULL;
            }
          }));
      pvh.setValue(h);
      pvh.setNumber(v.versionGetNumber());
      fsc.addHashes(pvh);
    }
  }

  private static void makeProgramVersions(
    final SortedSet<GVersionES> es,
    final SortedSet<GVersionFull> full,
    final ProgramMeta.PProgramShader.Builder ps)
  {
    for (final GVersionES v : es) {
      assert v != null;
      final ProgramMeta.PGLSLVersion.Builder pv =
        ProgramMeta.PGLSLVersion.newBuilder();
      pv.setApi(PGLSLApi.PGLSL_ES);
      pv.setNumber(v.versionGetNumber());
      ps.addSupports(pv);
    }

    for (final GVersionFull v : full) {
      assert v != null;
      final ProgramMeta.PGLSLVersion.Builder pv =
        ProgramMeta.PGLSLVersion.newBuilder();
      pv.setApi(PGLSLApi.PGLSL_FULL);
      pv.setNumber(v.versionGetNumber());
      ps.addSupports(pv);
    }
  }

  private static void makeVertexShaderCommonInputs(
    final SortedSet<JPVertexInput> vertex_ins,
    final ProgramMeta.PVertexShaderCommon.Builder vsco)
  {
    for (final JPVertexInput fi : vertex_ins) {
      assert fi != null;
      final ProgramMeta.PVertexShaderInput.Builder pfsi =
        ProgramMeta.PVertexShaderInput.newBuilder();
      pfsi.setName(fi.getName());
      pfsi.setType(fi.getType());
      vsco.addInputs(pfsi);
    }
  }

  private static void makeVertexShaderCommonOutputs(
    final SortedSet<JPVertexOutput> vertex_outs,
    final ProgramMeta.PVertexShaderCommon.Builder vsco)
  {
    for (final JPVertexOutput o : vertex_outs) {
      assert o != null;
      final ProgramMeta.PVertexShaderOutput.Builder pfso =
        ProgramMeta.PVertexShaderOutput.newBuilder();
      pfso.setName(o.getName());
      pfso.setType(o.getType());
      vsco.addOutputs(pfso);
    }
  }

  private static void makeVertexShaderCommonParameters(
    final SortedSet<JPVertexParameter> vertex_params,
    final ProgramMeta.PVertexShaderCommon.Builder vsco)
  {
    for (final JPVertexParameter p : vertex_params) {
      assert p != null;
      final ProgramMeta.PVertexShaderParameter.Builder pfsp =
        ProgramMeta.PVertexShaderParameter.newBuilder();
      pfsp.setName(p.getName());
      pfsp.setType(p.getType());
      vsco.addParameters(pfsp);
    }
  }

  private static void makeVertexShaderCommonVersions(
    final SortedSet<GVersionES> es,
    final SortedSet<GVersionFull> full,
    final ProgramMeta.PVertexShaderCommon.Builder vsco)
  {
    for (final GVersionES v : es) {
      assert v != null;
      final ProgramMeta.PGLSLVersion.Builder pv =
        ProgramMeta.PGLSLVersion.newBuilder();
      pv.setApi(PGLSLApi.PGLSL_ES);
      pv.setNumber(v.versionGetNumber());
      vsco.addSupports(pv);
    }

    for (final GVersionFull v : full) {
      assert v != null;
      final ProgramMeta.PGLSLVersion.Builder pv =
        ProgramMeta.PGLSLVersion.newBuilder();
      pv.setApi(PGLSLApi.PGLSL_FULL);
      pv.setNumber(v.versionGetNumber());
      vsco.addSupports(pv);
    }
  }

  private static void makeVertexShaderCompactedHashes(
    final ProgramMeta.PVertexShaderCompacted.Builder vsc,
    final Map<GVersionType, String> version_hashes)
  {
    for (final GVersionType v : version_hashes.keySet()) {
      assert v != null;
      final String h = version_hashes.get(v);
      final ProgramMeta.PVersionHash.Builder pvh =
        ProgramMeta.PVersionHash.newBuilder();
      pvh
        .setApi(v
          .versionAccept(new GVersionVisitorType<ProgramMeta.PGLSLApi, UnreachableCodeException>() {
            @Override public PGLSLApi versionVisitES(
              final GVersionES ve)
            {
              return PGLSLApi.PGLSL_ES;
            }

            @Override public PGLSLApi versionVisitFull(
              final GVersionFull vf)
            {
              return PGLSLApi.PGLSL_FULL;
            }
          }));
      pvh.setValue(h);
      pvh.setNumber(v.versionGetNumber());
      vsc.addHashes(pvh);
    }
  }

  /**
   * @return A new metadata serializer
   */

  public static JPMetaSerializerType newSerializer()
  {
    return new JPProtobufMetaSerializer();
  }

  private static void writeMeta(
    final Meta meta,
    final OutputStream out)
    throws IOException
  {
    JPProtobufHeader.writeHeaderAndSize(out, meta.getSerializedSize());
    meta.writeTo(out);
  }

  private JPProtobufMetaSerializer()
  {

  }

  @Override public String metaGetSuggestedFilename()
  {
    return JPProtobufMetaSerializer.SUGGESTED_FILENAME;
  }

  @Override public String metaGetSuggestedFilenameSuffix()
  {
    return JPProtobufMetaSerializer.SUGGESTED_FILENAME_SUFFIX;
  }

  @Override public void metaSerializeCompactedFragmentShader(
    final JPCompactedFragmentShaderMeta meta,
    final OutputStream stream)
    throws IOException
  {
    NullCheck.notNull(meta);
    NullCheck.notNull(stream);

    final ProgramMeta.PFragmentShaderCommon.Builder fsco =
      ProgramMeta.PFragmentShaderCommon.newBuilder();
    fsco.setProgramName(meta.getName());

    JPProtobufMetaSerializer.makeFragmentShaderCommonInputs(
      meta.getDeclaredFragmentInputs(),
      fsco);
    JPProtobufMetaSerializer.makeFragmentShaderCommonOutputs(
      meta.getDeclaredFragmentOutputs(),
      fsco);
    JPProtobufMetaSerializer.makeFragmentShaderCommonParameters(
      meta.getDeclaredFragmentParameters(),
      fsco);
    JPProtobufMetaSerializer.makeFragmentShaderCommonVersions(
      meta.getSupportsES(),
      meta.getSupportsFull(),
      fsco);

    final ProgramMeta.PFragmentShaderCompacted.Builder fsc =
      ProgramMeta.PFragmentShaderCompacted.newBuilder();
    fsc.setCommon(fsco);

    JPProtobufMetaSerializer.makeFragmentShaderCompactedHashes(
      fsc,
      meta.getVersionToHash());

    final ProgramMeta.Meta.Builder pmb = ProgramMeta.Meta.newBuilder();
    pmb.setFragmentCompacted(fsc);

    final Meta result = pmb.build();
    assert result != null;
    JPProtobufMetaSerializer.writeMeta(result, stream);
  }

  @Override public void metaSerializeCompactedVertexShader(
    final JPCompactedVertexShaderMeta meta,
    final OutputStream stream)
    throws IOException
  {
    NullCheck.notNull(meta);
    NullCheck.notNull(stream);

    final ProgramMeta.PVertexShaderCommon.Builder vsco =
      ProgramMeta.PVertexShaderCommon.newBuilder();
    vsco.setProgramName(meta.getName());

    JPProtobufMetaSerializer.makeVertexShaderCommonInputs(
      meta.getDeclaredVertexInputs(),
      vsco);
    JPProtobufMetaSerializer.makeVertexShaderCommonOutputs(
      meta.getDeclaredVertexOutputs(),
      vsco);
    JPProtobufMetaSerializer.makeVertexShaderCommonParameters(
      meta.getDeclaredVertexParameters(),
      vsco);
    JPProtobufMetaSerializer.makeVertexShaderCommonVersions(
      meta.getSupportsES(),
      meta.getSupportsFull(),
      vsco);

    final ProgramMeta.PVertexShaderCompacted.Builder vsc =
      ProgramMeta.PVertexShaderCompacted.newBuilder();
    vsc.setCommon(vsco);

    JPProtobufMetaSerializer.makeVertexShaderCompactedHashes(
      vsc,
      meta.getVersionToHash());

    final ProgramMeta.Meta.Builder pmb = ProgramMeta.Meta.newBuilder();
    pmb.setVertexCompacted(vsc);

    final Meta result = pmb.build();
    assert result != null;
    JPProtobufMetaSerializer.writeMeta(result, stream);
  }

  @Override public void metaSerializeFragmentShader(
    final JPFragmentShaderMetaType meta,
    final OutputStream out)
    throws IOException
  {
    NullCheck.notNull(meta);
    NullCheck.notNull(out);

    final JPProtobufMetaSerializer s = this;
    meta
      .matchFragmentMeta(new JPFragmentShaderMetaVisitorType<Unit, IOException>() {
        @Override public Unit compacted(
          final JPCompactedFragmentShaderMeta m)
          throws IOException
        {
          s.metaSerializeCompactedFragmentShader(m, out);
          return Unit.unit();
        }

        @Override public Unit uncompacted(
          final JPUncompactedFragmentShaderMeta m)
          throws IOException
        {
          s.metaSerializeUncompactedFragmentShader(m, out);
          return Unit.unit();
        }
      });
  }

  @Override public void metaSerializeShader(
    final JPCompiledShaderMetaType meta,
    final OutputStream out)
    throws IOException
  {
    NullCheck.notNull(meta);
    NullCheck.notNull(out);

    final JPProtobufMetaSerializer s = this;
    meta.matchMeta(new JPCompiledShaderMetaVisitorType<Unit, IOException>() {
      @Override public Unit compactedFragment(
        final JPCompactedFragmentShaderMeta m)
        throws IOException
      {
        s.metaSerializeCompactedFragmentShader(m, out);
        return Unit.unit();
      }

      @Override public Unit compactedVertex(
        final JPCompactedVertexShaderMeta m)
        throws IOException
      {
        s.metaSerializeCompactedVertexShader(m, out);
        return Unit.unit();
      }

      @Override public Unit uncompactedFragment(
        final JPUncompactedFragmentShaderMeta m)
        throws IOException
      {
        s.metaSerializeUncompactedFragmentShader(m, out);
        return Unit.unit();
      }

      @Override public Unit uncompactedProgram(
        final JPUncompactedProgramShaderMeta m)
        throws IOException
      {
        s.metaSerializeUncompactedProgram(m, out);
        return Unit.unit();
      }

      @Override public Unit uncompactedVertex(
        final JPUncompactedVertexShaderMeta m)
        throws IOException
      {
        s.metaSerializeUncompactedVertexShader(m, out);
        return Unit.unit();
      }
    });
  }

  @Override public void metaSerializeUncompactedFragmentShader(
    final JPUncompactedFragmentShaderMeta meta,
    final OutputStream stream)
    throws IOException
  {
    NullCheck.notNull(meta);
    NullCheck.notNull(stream);

    final ProgramMeta.PFragmentShaderCommon.Builder fsco =
      ProgramMeta.PFragmentShaderCommon.newBuilder();
    fsco.setProgramName(meta.getName());

    JPProtobufMetaSerializer.makeFragmentShaderCommonInputs(
      meta.getDeclaredFragmentInputs(),
      fsco);
    JPProtobufMetaSerializer.makeFragmentShaderCommonOutputs(
      meta.getDeclaredFragmentOutputs(),
      fsco);
    JPProtobufMetaSerializer.makeFragmentShaderCommonParameters(
      meta.getDeclaredFragmentParameters(),
      fsco);
    JPProtobufMetaSerializer.makeFragmentShaderCommonVersions(
      meta.getSupportsES(),
      meta.getSupportsFull(),
      fsco);

    final ProgramMeta.PFragmentShaderUncompacted.Builder fsu =
      ProgramMeta.PFragmentShaderUncompacted.newBuilder();
    fsu.setCommon(fsco);

    final ProgramMeta.Meta.Builder pmb = ProgramMeta.Meta.newBuilder();
    pmb.setFragmentUncompacted(fsu);

    final Meta result = pmb.build();
    assert result != null;
    JPProtobufMetaSerializer.writeMeta(result, stream);
  }

  @Override public void metaSerializeUncompactedProgram(
    final JPUncompactedProgramShaderMeta meta,
    final OutputStream stream)
    throws IOException
  {
    NullCheck.notNull(meta);
    NullCheck.notNull(stream);

    final ProgramMeta.PProgramShader.Builder ps =
      ProgramMeta.PProgramShader.newBuilder();
    ps.setProgramName(meta.getName());
    ps.setFragmentShader(meta.getFragmentShader());

    for (final String v : meta.getVertexShaders()) {
      ps.addVertexShaders(v);
    }

    JPProtobufMetaSerializer.makeProgramVersions(
      meta.getSupportsES(),
      meta.getSupportsFull(),
      ps);

    final ProgramMeta.Meta.Builder pmb = ProgramMeta.Meta.newBuilder();
    pmb.setProgram(ps);

    final Meta result = pmb.build();
    assert result != null;
    JPProtobufMetaSerializer.writeMeta(result, stream);
  }

  @Override public void metaSerializeUncompactedVertexShader(
    final JPUncompactedVertexShaderMeta meta,
    final OutputStream stream)
    throws IOException
  {
    NullCheck.notNull(meta);
    NullCheck.notNull(stream);

    final ProgramMeta.PVertexShaderCommon.Builder vsco =
      ProgramMeta.PVertexShaderCommon.newBuilder();
    vsco.setProgramName(meta.getName());

    JPProtobufMetaSerializer.makeVertexShaderCommonInputs(
      meta.getDeclaredVertexInputs(),
      vsco);
    JPProtobufMetaSerializer.makeVertexShaderCommonOutputs(
      meta.getDeclaredVertexOutputs(),
      vsco);
    JPProtobufMetaSerializer.makeVertexShaderCommonParameters(
      meta.getDeclaredVertexParameters(),
      vsco);
    JPProtobufMetaSerializer.makeVertexShaderCommonVersions(
      meta.getSupportsES(),
      meta.getSupportsFull(),
      vsco);

    final ProgramMeta.PVertexShaderUncompacted.Builder vsu =
      ProgramMeta.PVertexShaderUncompacted.newBuilder();
    vsu.setCommon(vsco);

    final ProgramMeta.Meta.Builder pmb = ProgramMeta.Meta.newBuilder();
    pmb.setVertexUncompacted(vsu);

    final Meta result = pmb.build();
    assert result != null;
    JPProtobufMetaSerializer.writeMeta(result, stream);
  }

  @Override public void metaSerializeVertexShader(
    final JPVertexShaderMetaType meta,
    final OutputStream out)
    throws IOException
  {
    NullCheck.notNull(meta);
    NullCheck.notNull(out);

    final JPProtobufMetaSerializer s = this;
    meta
      .matchVertexMeta(new JPVertexShaderMetaVisitorType<Unit, IOException>() {
        @Override public Unit compacted(
          final JPCompactedVertexShaderMeta m)
          throws IOException
        {
          s.metaSerializeCompactedVertexShader(m, out);
          return Unit.unit();
        }

        @Override public Unit uncompacted(
          final JPUncompactedVertexShaderMeta m)
          throws IOException
        {
          s.metaSerializeUncompactedVertexShader(m, out);
          return Unit.unit();
        }
      });
  }
}
