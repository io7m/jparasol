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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jfunctional.Option;
import com.io7m.jlog.LogUsableType;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * Functions to load shader metadata from XML.
 */

@EqualityReference public final class ShaderMeta
{
  @EqualityReference private static class TrivialErrorHandler implements
    ErrorHandler
  {
    private @Nullable SAXParseException exception;

    public TrivialErrorHandler()
    {

    }

    @Override public void error(
      final @Nullable SAXParseException e)
      throws SAXException
    {
      this.exception = e;
    }

    @Override public void fatalError(
      final @Nullable SAXParseException e)
      throws SAXException
    {
      this.exception = e;
    }

    public @Nullable SAXParseException getException()
    {
      return this.exception;
    }

    @Override public void warning(
      final @Nullable SAXParseException e)
      throws SAXException
    {
      this.exception = e;
    }
  }

  /**
   * The schema XML URI.
   */

  public static final URI    XML_URI;

  /**
   * The XML URI string.
   */

  public static final String XML_URI_STRING;

  /**
   * The schema version.
   */

  public static final int    XML_VERSION;

  static {
    try {
      XML_URI_STRING = "http://schemas.io7m.com/parasol/glsl-meta";
      XML_URI = new URI(ShaderMeta.XML_URI_STRING);
      XML_VERSION = 4;
    } catch (final URISyntaxException e) {
      throw new UnreachableCodeException(e);
    }
  }

  private static void checkVersion(
    final Element root)
    throws ValidityException
  {
    final Attribute version =
      root.getAttribute("version", ShaderMeta.XML_URI_STRING);
    assert version != null;

    try {
      final int version_number = Integer.parseInt(version.getValue());
      if (version_number != ShaderMeta.XML_VERSION) {
        final StringBuilder message = new StringBuilder();
        message.append("Unsupported version ");
        message.append(version_number);
        message.append(", supported versions are: ");
        message.append(ShaderMeta.XML_VERSION);
        throw new ValidityException(message.toString());
      }
    } catch (final NumberFormatException x) {
      final StringBuilder message = new StringBuilder();
      message
        .append("Could not parse 'version' attribute as numeric value: ");
      message.append(x.getMessage());
      throw new ValidityException(message.toString());
    }
  }

  /**
   * Extract metadata from the given document.
   * 
   * @throws ValidityException
   *           Iff the given document is not a valid metadata document.
   */

  private static ShaderMetaType fromDocument(
    final Document doc)
    throws ValidityException
  {
    NullCheck.notNull(doc, "Document");

    final Element root = doc.getRootElement();

    if ("meta-vertex".equals(root.getLocalName())) {
      return ShaderMeta.parseVertex(root);
    }

    if ("meta-fragment".equals(root.getLocalName())) {
      return ShaderMeta.parseFragment(root);
    }

    if ("meta-program".equals(root.getLocalName())) {
      return ShaderMeta.parseProgram(root);
    }

    final StringBuilder message = new StringBuilder();
    message
      .append("Expected one of {'meta-vertex','meta-fragment','meta-program'} but got '");
    message.append(root.getLocalName());
    message.append("'");
    throw new ValidityException(message.toString());
  }

  /**
   * Load XML metadata from the given stream.
   * 
   * @param stream
   *          The stream
   * @param log
   *          A log interface
   * @return XML metadata
   * @throws ParsingException
   *           On parse errors
   * @throws IOException
   *           On I/O errors
   * @throws SAXException
   *           On parse errors
   * @throws ParserConfigurationException
   *           On parser configuration errors
   */

  public static ShaderMetaType fromStream(
    final InputStream stream,
    final LogUsableType log)
    throws ParsingException,
      IOException,
      SAXException,
      ParserConfigurationException
  {
    return ShaderMeta
      .fromDocument(ShaderMeta.fromStreamValidate(stream, log));
  }

  static Document fromStreamValidate(
    final InputStream stream,
    final LogUsableType log)
    throws SAXException,
      ParserConfigurationException,
      ValidityException,
      ParsingException,
      IOException
  {
    NullCheck.notNull(stream, "Stream");
    NullCheck.notNull(log, "Log");

    final LogUsableType log_xml = log.with("xml");

    log_xml.debug("creating sax parser");
    final SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setValidating(false);
    factory.setNamespaceAware(true);

    log_xml.debug("opening schema");
    final InputStream xsd_stream =
      ShaderMeta.class.getResourceAsStream("/com/io7m/jparasol/meta.xsd");

    try {
      log_xml.debug("creating schema handler");
      final SchemaFactory sf =
        SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

      final Source[] sources = new Source[1];
      sources[0] = new StreamSource(xsd_stream);
      factory.setSchema(sf.newSchema(sources));

      final TrivialErrorHandler handler = new TrivialErrorHandler();
      final SAXParser parser = factory.newSAXParser();
      final XMLReader reader = parser.getXMLReader();
      reader.setErrorHandler(handler);

      log_xml.debug("parsing and validating");
      final Builder builder = new Builder(reader);
      final Document doc = builder.build(stream);

      final SAXParseException ex = handler.getException();
      if (ex != null) {
        throw ex;
      }

      return doc;
    } finally {
      xsd_stream.close();
    }
  }

  private static @Nullable SortedMap<Version, String> parseCompactions(
    final Element root)
    throws ValidityException
  {
    final SortedMap<Version, String> m = new TreeMap<Version, String>();

    final Elements ecm =
      root.getChildElements("compact-mappings", ShaderMeta.XML_URI_STRING);
    if (ecm.size() == 0) {
      return null;
    }

    final Elements ecms =
      ecm.get(0).getChildElements("mapping", ShaderMeta.XML_URI_STRING);

    for (int index = 0; index < ecms.size(); ++index) {
      final Element em = ecms.get(index);

      final Attribute av =
        em.getAttribute("number", ShaderMeta.XML_URI_STRING);
      final Attribute aa = em.getAttribute("api", ShaderMeta.XML_URI_STRING);

      try {
        final String hash = em.getValue();
        assert hash != null;
        final String v_api = aa.getValue();
        assert v_api != null;
        final API api = API.fromString(v_api);
        final Version v = new Version(Integer.parseInt(av.getValue()), api);
        m.put(v, hash);
      } catch (final NumberFormatException x) {
        throw new ValidityException(
          "Could not parse 'version' number on 'mapping' element: "
            + x.getMessage());
      }
    }

    return m;
  }

  private static ShaderMetaFragment parseFragment(
    final Element root)
    throws ValidityException
  {
    ShaderMeta.checkVersion(root);
    final String rname = ShaderMeta.parseName(root);
    final SortedSet<Integer> rsupports_es = ShaderMeta.parseSupportsES(root);
    final SortedSet<Integer> rsupports_full =
      ShaderMeta.parseSupportsFull(root);

    final Element params =
      root
        .getChildElements("parameters-fragment", ShaderMeta.XML_URI_STRING)
        .get(0);
    assert params != null;

    final SortedSet<FragmentInput> fragment_inputs =
      ShaderMeta.parseFragmentInputs(params);
    final SortedSet<FragmentParameter> fragment_parameters =
      ShaderMeta.parseFragmentParameters(params);
    final SortedMap<Integer, FragmentOutput> fragment_outputs =
      ShaderMeta.parseFragmentOutputs(params);

    final SortedMap<Version, String> compact_mappings =
      ShaderMeta.parseCompactions(root);

    return new ShaderMetaFragment(
      Option.of(compact_mappings),
      rname,
      rsupports_es,
      rsupports_full,
      fragment_inputs,
      fragment_outputs,
      fragment_parameters);
  }

  private static SortedSet<FragmentInput> parseFragmentInputs(
    final Element root)
  {
    final SortedSet<FragmentInput> rinputs = new TreeSet<FragmentInput>();

    final Elements eins =
      root.getChildElements(
        "declared-fragment-inputs",
        ShaderMeta.XML_URI_STRING);

    final Element ei = eins.get(0);
    final Elements eic =
      ei.getChildElements("input", ShaderMeta.XML_URI_STRING);

    for (int index = 0; index < eic.size(); ++index) {
      final Element e = eic.get(index);
      final Attribute an = e.getAttribute("name", ShaderMeta.XML_URI_STRING);
      final Attribute at = e.getAttribute("type", ShaderMeta.XML_URI_STRING);

      final FragmentInput o = new FragmentInput(an.getValue(), at.getValue());
      rinputs.add(o);
    }

    return rinputs;
  }

  private static SortedMap<Integer, FragmentOutput> parseFragmentOutputs(
    final Element root)
    throws ValidityException
  {
    final SortedMap<Integer, FragmentOutput> routputs =
      new TreeMap<Integer, FragmentOutput>();

    final Elements es =
      root.getChildElements(
        "declared-fragment-outputs",
        ShaderMeta.XML_URI_STRING);
    final Element eo = es.get(0);
    final Elements eoc =
      eo.getChildElements("fragment-output", ShaderMeta.XML_URI_STRING);

    for (int index = 0; index < eoc.size(); ++index) {
      final Element e = eoc.get(index);
      final Attribute an = e.getAttribute("name", ShaderMeta.XML_URI_STRING);
      final Attribute at = e.getAttribute("type", ShaderMeta.XML_URI_STRING);
      final Attribute ai = e.getAttribute("index", ShaderMeta.XML_URI_STRING);

      try {
        @SuppressWarnings({ "boxing" }) final FragmentOutput o =
          new FragmentOutput(
            Integer.parseInt(ai.getValue()),
            an.getValue(),
            at.getValue());
        routputs.put(o.getIndex(), o);
      } catch (final NumberFormatException x) {
        throw new ValidityException(
          "Could not parse 'index' number on 'fragment-output' element: "
            + x.getMessage());
      }
    }

    return routputs;
  }

  private static SortedSet<FragmentParameter> parseFragmentParameters(
    final Element root)
  {
    final SortedSet<FragmentParameter> rinputs =
      new TreeSet<FragmentParameter>();

    final Elements eins =
      root.getChildElements(
        "declared-fragment-parameters",
        ShaderMeta.XML_URI_STRING);

    final Element ei = eins.get(0);
    final Elements eic =
      ei.getChildElements("parameter", ShaderMeta.XML_URI_STRING);

    for (int index = 0; index < eic.size(); ++index) {
      final Element e = eic.get(index);
      final Attribute an = e.getAttribute("name", ShaderMeta.XML_URI_STRING);
      final Attribute at = e.getAttribute("type", ShaderMeta.XML_URI_STRING);

      final FragmentParameter o =
        new FragmentParameter(an.getValue(), at.getValue());
      rinputs.add(o);
    }

    return rinputs;
  }

  private static String parseFragmentShaderName(
    final Element root)
  {
    final Elements f =
      root.getChildElements("shader-fragment", ShaderMeta.XML_URI_STRING);

    return f.get(0).getValue();
  }

  private static String parseName(
    final Element root)
  {
    final Elements ename =
      root.getChildElements("program-name", ShaderMeta.XML_URI_STRING);
    return ename.get(0).getValue();
  }

  private static ShaderMetaProgram parseProgram(
    final Element root)
    throws ValidityException
  {
    ShaderMeta.checkVersion(root);
    final String rname = ShaderMeta.parseName(root);
    final SortedSet<Integer> rsupports_es = ShaderMeta.parseSupportsES(root);
    final SortedSet<Integer> rsupports_full =
      ShaderMeta.parseSupportsFull(root);

    final SortedSet<String> vertex_shaders =
      ShaderMeta.parseVertexShaderNames(root);
    final String fragment_shader = ShaderMeta.parseFragmentShaderName(root);

    return new ShaderMetaProgram(
      rname,
      rsupports_es,
      rsupports_full,
      vertex_shaders,
      fragment_shader);
  }

  private static SortedSet<Integer> parseSupportsES(
    final Element root)
    throws ValidityException
  {
    final SortedSet<Integer> versions = new TreeSet<Integer>();

    final Elements supports =
      root.getChildElements("supports", ShaderMeta.XML_URI_STRING);

    final Element es = supports.get(0);
    final Elements esc =
      es.getChildElements("version", ShaderMeta.XML_URI_STRING);

    for (int index = 0; index < esc.size(); ++index) {
      final Element e = esc.get(index);
      final Attribute av =
        e.getAttribute("number", ShaderMeta.XML_URI_STRING);
      final Attribute aa = e.getAttribute("api", ShaderMeta.XML_URI_STRING);

      try {
        final int avi = Integer.parseInt(av.getValue());

        final String aas = aa.getValue();
        if ("glsl-es".equals(aas)) {
          versions.add(Integer.valueOf(avi));
        }

      } catch (final NumberFormatException x) {
        throw new ValidityException(
          "Could not parse number attribute on version element");
      }
    }

    return versions;
  }

  private static SortedSet<Integer> parseSupportsFull(
    final Element root)
    throws ValidityException
  {
    final SortedSet<Integer> versions = new TreeSet<Integer>();

    final Elements supports =
      root.getChildElements("supports", ShaderMeta.XML_URI_STRING);

    final Element es = supports.get(0);
    final Elements esc =
      es.getChildElements("version", ShaderMeta.XML_URI_STRING);

    for (int index = 0; index < esc.size(); ++index) {
      final Element e = esc.get(index);
      final Attribute av =
        e.getAttribute("number", ShaderMeta.XML_URI_STRING);
      final Attribute aa = e.getAttribute("api", ShaderMeta.XML_URI_STRING);

      try {
        final int avi = Integer.parseInt(av.getValue());

        final String aas = aa.getValue();
        if ("glsl".equals(aas)) {
          versions.add(Integer.valueOf(avi));
        }

      } catch (final NumberFormatException x) {
        throw new ValidityException(
          "Could not parse number attribute on version element");
      }
    }

    return versions;
  }

  private static ShaderMetaVertex parseVertex(
    final Element root)
    throws ValidityException
  {
    ShaderMeta.checkVersion(root);
    final String rname = ShaderMeta.parseName(root);
    final SortedSet<Integer> rsupports_es = ShaderMeta.parseSupportsES(root);
    final SortedSet<Integer> rsupports_full =
      ShaderMeta.parseSupportsFull(root);

    final Element params =
      root
        .getChildElements("parameters-vertex", ShaderMeta.XML_URI_STRING)
        .get(0);
    assert params != null;

    final SortedSet<VertexInput> vertex_inputs =
      ShaderMeta.parseVertexInputs(params);
    final SortedSet<VertexParameter> vertex_parameters =
      ShaderMeta.parseVertexParameters(params);
    final SortedSet<VertexOutput> vertex_outputs =
      ShaderMeta.parseVertexOutputs(params);

    final SortedMap<Version, String> compact_mappings =
      ShaderMeta.parseCompactions(root);

    return new ShaderMetaVertex(
      Option.of(compact_mappings),
      rname,
      rsupports_es,
      rsupports_full,
      vertex_inputs,
      vertex_outputs,
      vertex_parameters);
  }

  private static SortedSet<VertexInput> parseVertexInputs(
    final Element root)
  {
    final SortedSet<VertexInput> rinputs = new TreeSet<VertexInput>();

    final Elements eins =
      root.getChildElements(
        "declared-vertex-inputs",
        ShaderMeta.XML_URI_STRING);
    final Element ei = eins.get(0);
    final Elements eic =
      ei.getChildElements("input", ShaderMeta.XML_URI_STRING);

    for (int index = 0; index < eic.size(); ++index) {
      final Element e = eic.get(index);
      final Attribute an = e.getAttribute("name", ShaderMeta.XML_URI_STRING);
      final Attribute at = e.getAttribute("type", ShaderMeta.XML_URI_STRING);
      final VertexInput o = new VertexInput(an.getValue(), at.getValue());
      rinputs.add(o);
    }

    return rinputs;
  }

  private static SortedSet<VertexOutput> parseVertexOutputs(
    final Element root)
  {
    final SortedSet<VertexOutput> routputs = new TreeSet<VertexOutput>();

    final Elements es =
      root.getChildElements(
        "declared-vertex-outputs",
        ShaderMeta.XML_URI_STRING);

    final Element eo = es.get(0);
    final Elements eoc =
      eo.getChildElements("output", ShaderMeta.XML_URI_STRING);

    for (int index = 0; index < eoc.size(); ++index) {
      final Element e = eoc.get(index);
      final Attribute an = e.getAttribute("name", ShaderMeta.XML_URI_STRING);
      final Attribute at = e.getAttribute("type", ShaderMeta.XML_URI_STRING);
      final VertexOutput o = new VertexOutput(an.getValue(), at.getValue());
      routputs.add(o);
    }

    return routputs;
  }

  private static SortedSet<VertexParameter> parseVertexParameters(
    final Element root)
  {
    final SortedSet<VertexParameter> rinputs = new TreeSet<VertexParameter>();

    final Elements eins =
      root.getChildElements(
        "declared-vertex-parameters",
        ShaderMeta.XML_URI_STRING);

    final Element ei = eins.get(0);
    final Elements eic =
      ei.getChildElements("parameter", ShaderMeta.XML_URI_STRING);

    for (int index = 0; index < eic.size(); ++index) {
      final Element e = eic.get(index);
      final Attribute an = e.getAttribute("name", ShaderMeta.XML_URI_STRING);
      final Attribute at = e.getAttribute("type", ShaderMeta.XML_URI_STRING);
      final VertexParameter o =
        new VertexParameter(an.getValue(), at.getValue());
      rinputs.add(o);
    }

    return rinputs;
  }

  private static SortedSet<String> parseVertexShaderNames(
    final Element root)
  {
    final SortedSet<String> names = new TreeSet<String>();

    final Elements vs =
      root.getChildElements("shaders-vertex", ShaderMeta.XML_URI_STRING);

    final Element shaders = vs.get(0);

    final Elements v =
      shaders.getChildElements("shader-vertex", ShaderMeta.XML_URI_STRING);

    for (int index = 0; index < v.size(); ++index) {
      names.add(v.get(index).getValue());
    }

    return names;
  }

  static Element toXMLCompactMapping(
    final Version v,
    final String hash)
  {
    final String uri = ShaderMeta.XML_URI_STRING;
    final Element e = new Element("g:mapping", uri);
    e.addAttribute(new Attribute("g:number", uri, Integer.toString(v
      .getVersion())));
    e.addAttribute(new Attribute("g:api", uri, v.getAPI().getName()));
    e.appendChild(hash);
    return e;
  }

  static Element toXMLCompactMappings(
    final SortedMap<Version, String> mappings)
  {
    final String uri = ShaderMeta.XML_URI_STRING;
    final Element e = new Element("g:compact-mappings", uri);

    for (final Entry<Version, String> k : mappings.entrySet()) {
      final Version v = k.getKey();
      final String cs = k.getValue();
      assert cs != null;

      switch (v.getAPI()) {
        case API_GLSL:
        {
          e.appendChild(ShaderMeta.toXMLCompactMapping(v, cs));
          break;
        }
        case API_GLSL_ES:
        {
          e.appendChild(ShaderMeta.toXMLCompactMapping(v, cs));
          break;
        }
      }
    }

    return e;
  }

  static Element toXMLProgramName(
    final String name)
  {
    final Element e =
      new Element("g:program-name", ShaderMeta.XML_URI_STRING);
    e.appendChild(name);
    return e;
  }

  static Element toXMLSupports(
    final SortedSet<Integer> supports_es,
    final SortedSet<Integer> supports_full)
  {
    final Element e = new Element("g:supports", ShaderMeta.XML_URI_STRING);
    for (final Integer s : supports_es) {
      assert s != null;
      e.appendChild(ShaderMeta.toXMLVersionES(s));
    }
    for (final Integer s : supports_full) {
      assert s != null;
      e.appendChild(ShaderMeta.toXMLVersionFull(s));
    }
    return e;
  }

  static Element toXMLVersionES(
    final Integer v)
  {
    final String uri = ShaderMeta.XML_URI_STRING;
    final Element e = new Element("g:version", uri);
    e.addAttribute(new Attribute("g:number", uri, v.toString()));
    e.addAttribute(new Attribute("g:api", uri, "glsl-es"));
    return e;
  }

  static Element toXMLVersionFull(
    final Integer v)
  {
    final String uri = ShaderMeta.XML_URI_STRING;
    final Element e = new Element("g:version", uri);
    e.addAttribute(new Attribute("g:number", uri, v.toString()));
    e.addAttribute(new Attribute("g:api", uri, "glsl"));
    return e;
  }

  private ShaderMeta()
  {
    throw new UnreachableCodeException();
  }
}
