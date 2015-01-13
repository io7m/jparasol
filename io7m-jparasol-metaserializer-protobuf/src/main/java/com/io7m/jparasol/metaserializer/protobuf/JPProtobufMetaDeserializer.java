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
import java.io.InputStream;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jfunctional.Option;
import com.io7m.jfunctional.OptionType;
import com.io7m.jfunctional.Some;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.core.GVersionES;
import com.io7m.jparasol.core.GVersionFull;
import com.io7m.jparasol.core.GVersionType;
import com.io7m.jparasol.core.JPCompactedFragmentShaderMeta;
import com.io7m.jparasol.core.JPCompactedVertexShaderMeta;
import com.io7m.jparasol.core.JPCompiledShaderMetaType;
import com.io7m.jparasol.core.JPFragmentInput;
import com.io7m.jparasol.core.JPFragmentOutput;
import com.io7m.jparasol.core.JPFragmentParameter;
import com.io7m.jparasol.core.JPFragmentShaderMetaType;
import com.io7m.jparasol.core.JPMissingHash;
import com.io7m.jparasol.core.JPUncompactedFragmentShaderMeta;
import com.io7m.jparasol.core.JPUncompactedProgramShaderMeta;
import com.io7m.jparasol.core.JPUncompactedVertexShaderMeta;
import com.io7m.jparasol.core.JPVertexInput;
import com.io7m.jparasol.core.JPVertexOutput;
import com.io7m.jparasol.core.JPVertexParameter;
import com.io7m.jparasol.core.JPVertexShaderMetaType;
import com.io7m.jparasol.metaserializer.JPMetaDeserializerType;
import com.io7m.jparasol.metaserializer.JPSerializerException;
import com.io7m.jparasol.metaserializer.protobuf.types.ProgramMeta;
import com.io7m.jparasol.metaserializer.protobuf.types.ProgramMeta.Meta;
import com.io7m.jparasol.metaserializer.protobuf.types.ProgramMeta.Meta.ActualCase;
import com.io7m.jparasol.metaserializer.protobuf.types.ProgramMeta.PFragmentShaderCommon;
import com.io7m.jparasol.metaserializer.protobuf.types.ProgramMeta.PFragmentShaderCompacted;
import com.io7m.jparasol.metaserializer.protobuf.types.ProgramMeta.PFragmentShaderInput;
import com.io7m.jparasol.metaserializer.protobuf.types.ProgramMeta.PFragmentShaderOutput;
import com.io7m.jparasol.metaserializer.protobuf.types.ProgramMeta.PFragmentShaderParameter;
import com.io7m.jparasol.metaserializer.protobuf.types.ProgramMeta.PFragmentShaderUncompacted;
import com.io7m.jparasol.metaserializer.protobuf.types.ProgramMeta.PGLSLVersion;
import com.io7m.jparasol.metaserializer.protobuf.types.ProgramMeta.PProgramShader;
import com.io7m.jparasol.metaserializer.protobuf.types.ProgramMeta.PVersionHash;
import com.io7m.jparasol.metaserializer.protobuf.types.ProgramMeta.PVertexShaderCommon;
import com.io7m.jparasol.metaserializer.protobuf.types.ProgramMeta.PVertexShaderCompacted;
import com.io7m.jparasol.metaserializer.protobuf.types.ProgramMeta.PVertexShaderInput;
import com.io7m.jparasol.metaserializer.protobuf.types.ProgramMeta.PVertexShaderOutput;
import com.io7m.jparasol.metaserializer.protobuf.types.ProgramMeta.PVertexShaderParameter;
import com.io7m.jparasol.metaserializer.protobuf.types.ProgramMeta.PVertexShaderUncompacted;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * A metadata deserializer that uses Protobuf.
 */

@EqualityReference public final class JPProtobufMetaDeserializer implements
  JPMetaDeserializerType
{
  private static Map<GVersionType, String> makeFragmentHashes(
    final PFragmentShaderCompacted fc)
  {
    final Map<GVersionType, String> in_version_to_hash =
      new HashMap<GVersionType, String>();
    final List<PVersionHash> vh = NullCheck.notNull(fc.getHashesList());
    JPProtobufMetaDeserializer.unpackHashes(vh, in_version_to_hash);
    return in_version_to_hash;
  }

  private static SortedSet<JPFragmentInput> makeFragmentInputs(
    final PFragmentShaderCommon common)
  {
    final List<PFragmentShaderInput> vi =
      NullCheck.notNull(common.getInputsList());
    final SortedSet<JPFragmentInput> in_fragment_inputs =
      new TreeSet<JPFragmentInput>();
    JPProtobufMetaDeserializer.unpackFragmentInputs(vi, in_fragment_inputs);
    return in_fragment_inputs;
  }

  private static SortedMap<Integer, JPFragmentOutput> makeFragmentOutputs(
    final PFragmentShaderCommon common)
  {
    final List<PFragmentShaderOutput> vo =
      NullCheck.notNull(common.getOutputsList());
    final SortedMap<Integer, JPFragmentOutput> in_fragment_outputs =
      new TreeMap<Integer, JPFragmentOutput>();
    JPProtobufMetaDeserializer.unpackFragmentOutputs(vo, in_fragment_outputs);
    return in_fragment_outputs;
  }

  private static SortedSet<JPFragmentParameter> makeFragmentParameters(
    final PFragmentShaderCommon common)
  {
    final List<PFragmentShaderParameter> vp =
      NullCheck.notNull(common.getParametersList());
    final SortedSet<JPFragmentParameter> in_fragment_parameters =
      new TreeSet<JPFragmentParameter>();
    JPProtobufMetaDeserializer.unpackFragmentParameters(
      vp,
      in_fragment_parameters);
    return in_fragment_parameters;
  }

  private static InputStream makeLimitedStream(
    final InputStream in,
    final int size)
  {
    final InputStream limited = new InputStream() {
      private int count;

      @Override public void close()
        throws IOException
      {
        // Nothing
      }

      @Override public int read()
        throws IOException
      {
        if (this.count >= size) {
          return -1;
        }
        final int r = in.read();
        if (r == -1) {
          return r;
        }
        ++this.count;
        return r;
      }
    };
    return limited;
  }

  private static Map<GVersionType, String> makeVertexHashes(
    final PVertexShaderCompacted v)
  {
    final Map<GVersionType, String> in_version_to_hash =
      new HashMap<GVersionType, String>();
    final List<PVersionHash> vh = NullCheck.notNull(v.getHashesList());
    JPProtobufMetaDeserializer.unpackHashes(vh, in_version_to_hash);
    return in_version_to_hash;
  }

  private static SortedSet<JPVertexInput> makeVertexInputs(
    final PVertexShaderCommon common)
  {
    final List<PVertexShaderInput> vi =
      NullCheck.notNull(common.getInputsList());
    final SortedSet<JPVertexInput> in_vertex_inputs =
      new TreeSet<JPVertexInput>();
    JPProtobufMetaDeserializer.unpackVertexInputs(vi, in_vertex_inputs);
    return in_vertex_inputs;
  }

  private static SortedSet<JPVertexOutput> makeVertexOutputs(
    final PVertexShaderCommon common)
  {
    final List<PVertexShaderOutput> vo =
      NullCheck.notNull(common.getOutputsList());
    final SortedSet<JPVertexOutput> in_vertex_outputs =
      new TreeSet<JPVertexOutput>();
    JPProtobufMetaDeserializer.unpackVertexOutputs(vo, in_vertex_outputs);
    return in_vertex_outputs;
  }

  private static SortedSet<JPVertexParameter> makeVertexParameters(
    final PVertexShaderCommon common)
  {
    final List<PVertexShaderParameter> vp =
      NullCheck.notNull(common.getParametersList());
    final SortedSet<JPVertexParameter> in_vertex_parameters =
      new TreeSet<JPVertexParameter>();
    JPProtobufMetaDeserializer.unpackVertexParameters(
      vp,
      in_vertex_parameters);
    return in_vertex_parameters;
  }

  /**
   * @return A new metadata deserializer
   */

  public static JPMetaDeserializerType newDeserializer()
  {
    return new JPProtobufMetaDeserializer();
  }

  private static Meta parse(
    final InputStream in)
    throws IOException,
      JPSerializerException
  {
    JPProtobufHeader.readAndCheckVersion(in);
    final int size = JPProtobufHeader.readSize(in);
    InputStream limited = null;
    try {
      limited = JPProtobufMetaDeserializer.makeLimitedStream(in, size);
      return NullCheck.notNull(ProgramMeta.Meta.parseFrom(limited));
    } finally {
      if (limited != null) {
        limited.close();
      }
    }
  }

  private static JPCompactedFragmentShaderMeta unpackFragmentCompacted(
    final PFragmentShaderCompacted fc)
    throws JPMissingHash
  {
    final PFragmentShaderCommon common = NullCheck.notNull(fc.getCommon());
    final String in_name = NullCheck.notNull(common.getProgramName());

    final List<PGLSLVersion> supp =
      NullCheck.notNull(common.getSupportsList());
    final SortedSet<GVersionES> in_supports_es = new TreeSet<GVersionES>();
    final SortedSet<GVersionFull> in_supports_full =
      new TreeSet<GVersionFull>();
    JPProtobufMetaDeserializer.unpackVersions(
      supp,
      in_supports_es,
      in_supports_full);

    final SortedSet<JPFragmentInput> in_fragment_inputs =
      JPProtobufMetaDeserializer.makeFragmentInputs(common);
    final SortedMap<Integer, JPFragmentOutput> in_fragment_outputs =
      JPProtobufMetaDeserializer.makeFragmentOutputs(common);
    final SortedSet<JPFragmentParameter> in_fragment_parameters =
      JPProtobufMetaDeserializer.makeFragmentParameters(common);
    final Map<GVersionType, String> in_version_hashes =
      JPProtobufMetaDeserializer.makeFragmentHashes(fc);

    return JPCompactedFragmentShaderMeta.newMetadata(
      in_name,
      in_supports_es,
      in_supports_full,
      in_fragment_inputs,
      in_fragment_outputs,
      in_fragment_parameters,
      in_version_hashes);
  }

  private static void unpackFragmentInputs(
    final List<PFragmentShaderInput> in,
    final SortedSet<JPFragmentInput> out)
  {
    for (final PFragmentShaderInput i : in) {
      assert i != null;
      out.add(JPFragmentInput.newInput(
        NullCheck.notNull(i.getName()),
        NullCheck.notNull(i.getType())));
    }
  }

  private static void unpackFragmentOutputs(
    final List<PFragmentShaderOutput> in,
    final SortedMap<Integer, JPFragmentOutput> out)
  {
    for (final PFragmentShaderOutput o : in) {
      assert o != null;
      final Integer index = NullCheck.notNull(Integer.valueOf(o.getIndex()));
      final JPFragmentOutput r =
        JPFragmentOutput.newOutput(
          NullCheck.notNull(o.getName()),
          index,
          NullCheck.notNull(o.getType()));
      out.put(index, r);
    }
  }

  private static void unpackFragmentParameters(
    final List<PFragmentShaderParameter> in,
    final SortedSet<JPFragmentParameter> out)
  {
    for (final PFragmentShaderParameter p : in) {
      assert p != null;
      out.add(JPFragmentParameter.newParameter(
        NullCheck.notNull(p.getName()),
        NullCheck.notNull(p.getType())));
    }
  }

  private static JPUncompactedFragmentShaderMeta unpackFragmentUncompacted(
    final PFragmentShaderUncompacted fc)
  {
    final PFragmentShaderCommon common = NullCheck.notNull(fc.getCommon());
    final String in_name = NullCheck.notNull(common.getProgramName());

    final List<PGLSLVersion> supp =
      NullCheck.notNull(common.getSupportsList());
    final SortedSet<GVersionES> in_supports_es = new TreeSet<GVersionES>();
    final SortedSet<GVersionFull> in_supports_full =
      new TreeSet<GVersionFull>();
    JPProtobufMetaDeserializer.unpackVersions(
      supp,
      in_supports_es,
      in_supports_full);

    final SortedSet<JPFragmentInput> in_fragment_inputs =
      JPProtobufMetaDeserializer.makeFragmentInputs(common);
    final SortedMap<Integer, JPFragmentOutput> in_fragment_outputs =
      JPProtobufMetaDeserializer.makeFragmentOutputs(common);
    final SortedSet<JPFragmentParameter> in_fragment_parameters =
      JPProtobufMetaDeserializer.makeFragmentParameters(common);

    return JPUncompactedFragmentShaderMeta.newMetadata(
      in_name,
      in_supports_es,
      in_supports_full,
      in_fragment_inputs,
      in_fragment_outputs,
      in_fragment_parameters);
  }

  private static void unpackHashes(
    final List<PVersionHash> in,
    final Map<GVersionType, String> out)
  {
    for (final PVersionHash i : in) {
      assert i != null;
      final GVersionType gv = JPProtobufMetaDeserializer.unpackVersionType(i);
      out.put(gv, NullCheck.notNull(i.getValue()));
    }
  }

  private static JPUncompactedProgramShaderMeta unpackProgram(
    final PProgramShader p)
  {
    final String in_name = NullCheck.notNull(p.getProgramName());

    final List<PGLSLVersion> supp = NullCheck.notNull(p.getSupportsList());
    final SortedSet<GVersionES> in_supports_es = new TreeSet<GVersionES>();
    final SortedSet<GVersionFull> in_supports_full =
      new TreeSet<GVersionFull>();
    JPProtobufMetaDeserializer.unpackVersions(
      supp,
      in_supports_es,
      in_supports_full);

    final SortedSet<String> in_vertex_shaders = new TreeSet<String>();
    for (int index = 0; index < p.getVertexShadersCount(); ++index) {
      in_vertex_shaders.add(p.getVertexShaders(index));
    }

    final String in_fragment_shader =
      NullCheck.notNull(p.getFragmentShader());
    return JPUncompactedProgramShaderMeta.newMetadata(
      in_name,
      in_supports_es,
      in_supports_full,
      in_fragment_shader,
      in_vertex_shaders);
  }

  private static JPCompiledShaderMetaType unpackShader(
    final OptionType<EnumSet<Meta.ActualCase>> expected,
    final Meta m)
    throws JPSerializerException
  {
    if (expected.isSome()) {
      final Some<EnumSet<Meta.ActualCase>> some =
        (Some<EnumSet<Meta.ActualCase>>) expected;
      final EnumSet<ActualCase> set = some.get();

      if (set.contains(m.getActualCase()) == false) {
        final StringBuilder s = new StringBuilder();
        s.append("Wrong type of shader metadata.\n");
        s.append("  Expected one of: ");
        s.append(set);
        s.append("\n");
        s.append("  Got: ");
        s.append(m.getActualCase());
        s.append("\n");
        final String r = s.toString();
        assert r != null;
        throw new JPSerializerException(r);
      }
    }

    try {
      switch (m.getActualCase()) {
        case ACTUAL_NOT_SET:
        {
          throw new JPSerializerException(
            "Expected metadata, but got nothing.");
        }
        case FRAGMENT_COMPACTED:
        {
          final PFragmentShaderCompacted fc =
            NullCheck.notNull(m.getFragmentCompacted());
          return JPProtobufMetaDeserializer.unpackFragmentCompacted(fc);
        }
        case FRAGMENT_UNCOMPACTED:
        {
          final PFragmentShaderUncompacted fc =
            NullCheck.notNull(m.getFragmentUncompacted());
          return JPProtobufMetaDeserializer.unpackFragmentUncompacted(fc);
        }
        case PROGRAM:
        {
          final PProgramShader p = NullCheck.notNull(m.getProgram());
          return JPProtobufMetaDeserializer.unpackProgram(p);
        }
        case VERTEX_COMPACTED:
        {
          final PVertexShaderCompacted vc =
            NullCheck.notNull(m.getVertexCompacted());
          return JPProtobufMetaDeserializer.unpackVertexCompacted(vc);
        }
        case VERTEX_UNCOMPACTED:
        {
          final PVertexShaderUncompacted vc =
            NullCheck.notNull(m.getVertexUncompacted());
          return JPProtobufMetaDeserializer.unpackVertexUncompacted(vc);
        }
      }
    } catch (final JPMissingHash e) {
      throw new JPSerializerException(e);
    }

    throw new UnreachableCodeException();
  }

  private static void unpackVersions(
    final List<PGLSLVersion> versions,
    final SortedSet<GVersionES> in_supports_es,
    final SortedSet<GVersionFull> in_supports_full)
  {
    for (final PGLSLVersion v : versions) {
      assert v != null;
      switch (v.getApi()) {
        case PGLSL_ES:
        {
          in_supports_es.add(new GVersionES(v.getNumber()));
          break;
        }
        case PGLSL_FULL:
        {
          in_supports_full.add(new GVersionFull(v.getNumber()));
          break;
        }
      }
    }
  }

  private static GVersionType unpackVersionType(
    final PVersionHash i)
  {
    switch (i.getApi()) {
      case PGLSL_ES:
      {
        return new GVersionES(i.getNumber());
      }
      case PGLSL_FULL:
      {
        return new GVersionFull(i.getNumber());
      }
    }

    throw new UnreachableCodeException();
  }

  private static JPCompactedVertexShaderMeta unpackVertexCompacted(
    final PVertexShaderCompacted v)
    throws JPMissingHash
  {
    final PVertexShaderCommon common = NullCheck.notNull(v.getCommon());
    final String in_name = NullCheck.notNull(common.getProgramName());

    final List<PGLSLVersion> supp =
      NullCheck.notNull(common.getSupportsList());
    final SortedSet<GVersionES> in_supports_es = new TreeSet<GVersionES>();
    final SortedSet<GVersionFull> in_supports_full =
      new TreeSet<GVersionFull>();
    JPProtobufMetaDeserializer.unpackVersions(
      supp,
      in_supports_es,
      in_supports_full);

    final SortedSet<JPVertexInput> in_vertex_inputs =
      JPProtobufMetaDeserializer.makeVertexInputs(common);
    final SortedSet<JPVertexOutput> in_vertex_outputs =
      JPProtobufMetaDeserializer.makeVertexOutputs(common);
    final SortedSet<JPVertexParameter> in_vertex_parameters =
      JPProtobufMetaDeserializer.makeVertexParameters(common);
    final Map<GVersionType, String> in_version_to_hash =
      JPProtobufMetaDeserializer.makeVertexHashes(v);

    return JPCompactedVertexShaderMeta.newMetadata(
      in_name,
      in_supports_es,
      in_supports_full,
      in_vertex_inputs,
      in_vertex_outputs,
      in_vertex_parameters,
      in_version_to_hash);
  }

  private static void unpackVertexInputs(
    final List<PVertexShaderInput> in,
    final SortedSet<JPVertexInput> out)
  {
    for (final PVertexShaderInput i : in) {
      assert i != null;
      out.add(JPVertexInput.newInput(
        NullCheck.notNull(i.getName()),
        NullCheck.notNull(i.getType())));
    }
  }

  private static void unpackVertexOutputs(
    final List<PVertexShaderOutput> in,
    final SortedSet<JPVertexOutput> out)
  {
    for (final PVertexShaderOutput o : in) {
      assert o != null;
      out.add(JPVertexOutput.newOutput(
        NullCheck.notNull(o.getName()),
        NullCheck.notNull(o.getType())));
    }
  }

  private static void unpackVertexParameters(
    final List<PVertexShaderParameter> in,
    final SortedSet<JPVertexParameter> out)
  {
    for (final PVertexShaderParameter p : in) {
      assert p != null;
      out.add(JPVertexParameter.newParameter(
        NullCheck.notNull(p.getName()),
        NullCheck.notNull(p.getType())));
    }
  }

  private static JPUncompactedVertexShaderMeta unpackVertexUncompacted(
    final PVertexShaderUncompacted vc)
  {
    final PVertexShaderCommon common = NullCheck.notNull(vc.getCommon());
    final String in_name = NullCheck.notNull(common.getProgramName());

    final List<PGLSLVersion> supp =
      NullCheck.notNull(common.getSupportsList());
    final SortedSet<GVersionES> in_supports_es = new TreeSet<GVersionES>();
    final SortedSet<GVersionFull> in_supports_full =
      new TreeSet<GVersionFull>();
    JPProtobufMetaDeserializer.unpackVersions(
      supp,
      in_supports_es,
      in_supports_full);

    final SortedSet<JPVertexInput> in_vertex_inputs =
      JPProtobufMetaDeserializer.makeVertexInputs(common);

    final SortedSet<JPVertexOutput> in_vertex_outputs =
      JPProtobufMetaDeserializer.makeVertexOutputs(common);

    final SortedSet<JPVertexParameter> in_vertex_parameters =
      JPProtobufMetaDeserializer.makeVertexParameters(common);

    return JPUncompactedVertexShaderMeta.newMetadata(
      in_name,
      in_supports_es,
      in_supports_full,
      in_vertex_inputs,
      in_vertex_outputs,
      in_vertex_parameters);
  }

  private JPProtobufMetaDeserializer()
  {

  }

  @Override public JPFragmentShaderMetaType metaDeserializeFragmentShader(
    final InputStream in)
    throws IOException,
      JPSerializerException
  {
    NullCheck.notNull(in);
    final Meta m = JPProtobufMetaDeserializer.parse(in);
    final OptionType<EnumSet<ActualCase>> wanted =
      Option.some(EnumSet.of(
        ActualCase.FRAGMENT_COMPACTED,
        ActualCase.FRAGMENT_UNCOMPACTED));
    return (JPFragmentShaderMetaType) JPProtobufMetaDeserializer
      .unpackShader(wanted, m);
  }

  @Override public
    JPCompactedFragmentShaderMeta
    metaDeserializeFragmentShaderCompacted(
      final InputStream in)
      throws IOException,
        JPSerializerException
  {
    NullCheck.notNull(in);
    final Meta m = JPProtobufMetaDeserializer.parse(in);
    final OptionType<EnumSet<ActualCase>> wanted =
      Option.some(EnumSet.of(ActualCase.FRAGMENT_COMPACTED));
    return (JPCompactedFragmentShaderMeta) JPProtobufMetaDeserializer
      .unpackShader(wanted, m);
  }

  @Override public
    JPUncompactedFragmentShaderMeta
    metaDeserializeFragmentShaderUncompacted(
      final InputStream in)
      throws IOException,
        JPSerializerException
  {
    NullCheck.notNull(in);
    final Meta m = JPProtobufMetaDeserializer.parse(in);
    final OptionType<EnumSet<ActualCase>> wanted =
      Option.some(EnumSet.of(ActualCase.FRAGMENT_UNCOMPACTED));
    return (JPUncompactedFragmentShaderMeta) JPProtobufMetaDeserializer
      .unpackShader(wanted, m);
  }

  @Override public
    JPUncompactedProgramShaderMeta
    metaDeserializeProgramShaderUncompacted(
      final InputStream in)
      throws IOException,
        JPSerializerException
  {
    NullCheck.notNull(in);
    final Meta m = JPProtobufMetaDeserializer.parse(in);
    final OptionType<EnumSet<ActualCase>> wanted =
      Option.some(EnumSet.of(ActualCase.PROGRAM));
    return (JPUncompactedProgramShaderMeta) JPProtobufMetaDeserializer
      .unpackShader(wanted, m);
  }

  @Override public JPCompiledShaderMetaType metaDeserializeShader(
    final InputStream in)
    throws IOException,
      JPSerializerException
  {
    NullCheck.notNull(in);
    final Meta m = JPProtobufMetaDeserializer.parse(in);
    final OptionType<EnumSet<ActualCase>> wanted = Option.none();
    return JPProtobufMetaDeserializer.unpackShader(wanted, m);
  }

  @Override public JPVertexShaderMetaType metaDeserializeVertexShader(
    final InputStream in)
    throws IOException,
      JPSerializerException
  {
    NullCheck.notNull(in);
    final Meta m = JPProtobufMetaDeserializer.parse(in);
    final OptionType<EnumSet<ActualCase>> wanted =
      Option.some(EnumSet.of(
        ActualCase.VERTEX_COMPACTED,
        ActualCase.VERTEX_UNCOMPACTED));
    return (JPVertexShaderMetaType) JPProtobufMetaDeserializer.unpackShader(
      wanted,
      m);
  }

  @Override public
    JPCompactedVertexShaderMeta
    metaDeserializeVertexShaderCompacted(
      final InputStream in)
      throws IOException,
        JPSerializerException
  {
    NullCheck.notNull(in);
    final Meta m = JPProtobufMetaDeserializer.parse(in);
    final OptionType<EnumSet<ActualCase>> wanted =
      Option.some(EnumSet.of(ActualCase.VERTEX_COMPACTED));
    return (JPCompactedVertexShaderMeta) JPProtobufMetaDeserializer
      .unpackShader(wanted, m);
  }

  @Override public
    JPUncompactedVertexShaderMeta
    metaDeserializeVertexShaderUncompacted(
      final InputStream in)
      throws IOException,
        JPSerializerException
  {
    NullCheck.notNull(in);
    final Meta m = JPProtobufMetaDeserializer.parse(in);
    final OptionType<EnumSet<ActualCase>> wanted =
      Option.some(EnumSet.of(ActualCase.VERTEX_UNCOMPACTED));
    return (JPUncompactedVertexShaderMeta) JPProtobufMetaDeserializer
      .unpackShader(wanted, m);
  }

  @Override public String metaGetSuggestedFilename()
  {
    return JPProtobufMetaSerializer.SUGGESTED_FILENAME;
  }

  @Override public String metaGetSuggestedFilenameSuffix()
  {
    return JPProtobufMetaSerializer.SUGGESTED_FILENAME_SUFFIX;
  }
}
