/*
 * Copyright © 2013 <code@io7m.com> http://io7m.com
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
import java.util.Collections;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
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

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jlog.Log;

/**
 * Metadata about a GLSL program generated by Parasol.
 */

@Immutable public final class PGLSLMetaXML
{
  private static class TrivialErrorHandler implements ErrorHandler
  {
    private @CheckForNull SAXParseException exception;

    public TrivialErrorHandler()
    {

    }

    @Override public void error(
      final SAXParseException e)
      throws SAXException
    {
      this.exception = e;
    }

    @Override public void fatalError(
      final SAXParseException e)
      throws SAXException
    {
      this.exception = e;
    }

    public @CheckForNull SAXParseException getException()
    {
      return this.exception;
    }

    @Override public void warning(
      final SAXParseException e)
      throws SAXException
    {
      this.exception = e;
    }
  }

  public static final @Nonnull String XML_URI_STRING;
  public static final @Nonnull URI    XML_URI;
  public static final int             XML_VERSION;

  static {
    try {
      XML_URI_STRING = "http://schemas.io7m.com/parasol/glsl-meta";
      XML_URI = new URI(PGLSLMetaXML.XML_URI_STRING);
      XML_VERSION = 3;
    } catch (final URISyntaxException e) {
      throw new UnreachableCodeException(e);
    }
  }

  /**
   * Extract metadata from the given document.
   * 
   * @throws ConstraintError
   *           Iff <code>doc == null</code>.
   * @throws ValidityException
   *           Iff the given document is not a valid metadata document.
   */

  private static @Nonnull PGLSLMetaXML fromDocument(
    final @Nonnull Document doc)
    throws ValidityException,
      ConstraintError
  {
    Constraints.constrainNotNull(doc, "Document");

    final Element root = doc.getRootElement();

    if (root.getLocalName().equals("meta") == false) {
      final StringBuilder message = new StringBuilder();
      message.append("Expected an element 'meta', but got '");
      message.append(root.getLocalName());
      message.append("'");
      throw new ValidityException(message.toString());
    }

    final Attribute version =
      root.getAttribute("version", PGLSLMetaXML.XML_URI_STRING);
    assert version != null;

    try {
      final int version_number = Integer.parseInt(version.getValue());
      if (version_number != PGLSLMetaXML.XML_VERSION) {
        final StringBuilder message = new StringBuilder();
        message.append("Unsupported version ");
        message.append(version_number);
        message.append(", supported versions are: ");
        message.append(PGLSLMetaXML.XML_VERSION);
        throw new ValidityException(message.toString());
      }
    } catch (final NumberFormatException x) {
      final StringBuilder message = new StringBuilder();
      message
        .append("Could not parse 'version' attribute as numeric value: ");
      message.append(x.getMessage());
      throw new ValidityException(message.toString());
    }

    final String rname = PGLSLMetaXML.parseName(root);
    final SortedSet<Integer> rsupports_es =
      PGLSLMetaXML.parseSupportsES(root);
    final SortedSet<Integer> rsupports_full =
      PGLSLMetaXML.parseSupportsFull(root);

    final SortedSet<FragmentInput> fragment_inputs =
      PGLSLMetaXML.parseFragmentInputs(root);
    final SortedSet<FragmentParameter> fragment_parameters =
      PGLSLMetaXML.parseFragmentParameters(root);
    final SortedMap<Integer, FragmentOutput> fragment_outputs =
      PGLSLMetaXML.parseFragmentOutputs(root);

    final SortedSet<VertexInput> vertex_inputs =
      PGLSLMetaXML.parseVertexInputs(root);
    final SortedSet<VertexParameter> vertex_parameters =
      PGLSLMetaXML.parseVertexParameters(root);
    final SortedSet<VertexOutput> vertex_outputs =
      PGLSLMetaXML.parseVertexOutputs(root);

    final SortedMap<Version, CompactedShaders> compact_mappings =
      PGLSLMetaXML.parseCompactions(root);

    return new PGLSLMetaXML(
      rname,
      rsupports_es,
      rsupports_full,
      vertex_inputs,
      vertex_parameters,
      vertex_outputs,
      fragment_inputs,
      fragment_parameters,
      fragment_outputs,
      compact_mappings);
  }

  public static @Nonnull PGLSLMetaXML fromStream(
    final @Nonnull InputStream stream,
    final @Nonnull Log log)
    throws ConstraintError,
      ParsingException,
      IOException,
      SAXException,
      ParserConfigurationException
  {
    return PGLSLMetaXML.fromDocument(PGLSLMetaXML.fromStreamValidate(
      stream,
      log));
  }

  static @Nonnull Document fromStreamValidate(
    final @Nonnull InputStream stream,
    final @Nonnull Log log)
    throws SAXException,
      ConstraintError,
      ParserConfigurationException,
      ValidityException,
      ParsingException,
      IOException
  {
    Constraints.constrainNotNull(stream, "Stream");
    Constraints.constrainNotNull(log, "Log");

    final Log log_xml = new Log(log, "xml");

    log_xml.debug("creating sax parser");
    final SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setValidating(false);
    factory.setNamespaceAware(true);

    log_xml.debug("opening schema");
    final InputStream xsd_stream =
      PGLSLMetaXML.class.getResourceAsStream("/com/io7m/jparasol/meta.xsd");

    try {
      log_xml.debug("creating schema handler");
      final SchemaFactory schemaFactory =
        SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
      factory.setSchema(schemaFactory
        .newSchema(new Source[] { new StreamSource(xsd_stream) }));

      final TrivialErrorHandler handler = new TrivialErrorHandler();
      final SAXParser parser = factory.newSAXParser();
      final XMLReader reader = parser.getXMLReader();
      reader.setErrorHandler(handler);

      log_xml.debug("parsing and validating");
      final Builder builder = new Builder(reader);
      final Document doc = builder.build(stream);

      if (handler.getException() != null) {
        throw handler.getException();
      }

      return doc;
    } finally {
      xsd_stream.close();
    }
  }

  private static @CheckForNull
    SortedMap<Version, CompactedShaders>
    parseCompactions(
      final @Nonnull Element root)
      throws ValidityException,
        ConstraintError
  {
    final TreeMap<Version, CompactedShaders> m =
      new TreeMap<Version, CompactedShaders>();

    final Elements ecm =
      root.getChildElements("compact-mappings", PGLSLMetaXML.XML_URI_STRING);
    if (ecm.size() == 0) {
      return null;
    }

    final Elements ecms =
      ecm.get(0).getChildElements("mapping", PGLSLMetaXML.XML_URI_STRING);

    for (int index = 0; index < ecms.size(); ++index) {
      final Element em = ecms.get(index);

      final Attribute av =
        em.getAttribute("number", PGLSLMetaXML.XML_URI_STRING);
      final Attribute afh =
        em.getAttribute("fragment-hash", PGLSLMetaXML.XML_URI_STRING);
      final Attribute avh =
        em.getAttribute("vertex-hash", PGLSLMetaXML.XML_URI_STRING);
      final Attribute aa =
        em.getAttribute("api", PGLSLMetaXML.XML_URI_STRING);

      try {
        final CompactedShaders cs =
          new CompactedShaders(avh.getValue(), afh.getValue());
        final API api = API.fromString(aa.getValue());
        final Version v = new Version(Integer.parseInt(av.getValue()), api);
        m.put(v, cs);
      } catch (final NumberFormatException x) {
        throw new ValidityException(
          "Could not parse 'version' number on 'mapping' element: "
            + x.getMessage());
      }
    }

    return m;
  }

  private static @Nonnull SortedSet<FragmentInput> parseFragmentInputs(
    final @Nonnull Element root)
    throws ConstraintError
  {
    final SortedSet<FragmentInput> rinputs = new TreeSet<FragmentInput>();

    final Elements eins =
      root.getChildElements(
        "declared-fragment-inputs",
        PGLSLMetaXML.XML_URI_STRING);

    final Element ei = eins.get(0);
    final Elements eic =
      ei.getChildElements("input", PGLSLMetaXML.XML_URI_STRING);

    for (int index = 0; index < eic.size(); ++index) {
      final Element e = eic.get(index);
      final Attribute an =
        e.getAttribute("name", PGLSLMetaXML.XML_URI_STRING);
      final Attribute at =
        e.getAttribute("type", PGLSLMetaXML.XML_URI_STRING);

      final FragmentInput o = new FragmentInput(an.getValue(), at.getValue());
      rinputs.add(o);
    }

    return rinputs;
  }

  private static @Nonnull
    SortedMap<Integer, FragmentOutput>
    parseFragmentOutputs(
      final @Nonnull Element root)
      throws ValidityException,
        ConstraintError
  {
    final TreeMap<Integer, FragmentOutput> routputs =
      new TreeMap<Integer, FragmentOutput>();

    final Elements es =
      root.getChildElements(
        "declared-fragment-outputs",
        PGLSLMetaXML.XML_URI_STRING);
    final Element eo = es.get(0);
    final Elements eoc =
      eo.getChildElements("fragment-output", PGLSLMetaXML.XML_URI_STRING);

    for (int index = 0; index < eoc.size(); ++index) {
      final Element e = eoc.get(index);
      final Attribute an =
        e.getAttribute("name", PGLSLMetaXML.XML_URI_STRING);
      final Attribute at =
        e.getAttribute("type", PGLSLMetaXML.XML_URI_STRING);
      final Attribute ai =
        e.getAttribute("index", PGLSLMetaXML.XML_URI_STRING);

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

  private static @Nonnull
    SortedSet<FragmentParameter>
    parseFragmentParameters(
      final @Nonnull Element root)
      throws ConstraintError
  {
    final SortedSet<FragmentParameter> rinputs =
      new TreeSet<FragmentParameter>();

    final Elements eins =
      root.getChildElements(
        "declared-fragment-parameters",
        PGLSLMetaXML.XML_URI_STRING);

    final Element ei = eins.get(0);
    final Elements eic =
      ei.getChildElements("parameter", PGLSLMetaXML.XML_URI_STRING);

    for (int index = 0; index < eic.size(); ++index) {
      final Element e = eic.get(index);
      final Attribute an =
        e.getAttribute("name", PGLSLMetaXML.XML_URI_STRING);
      final Attribute at =
        e.getAttribute("type", PGLSLMetaXML.XML_URI_STRING);

      final FragmentParameter o =
        new FragmentParameter(an.getValue(), at.getValue());
      rinputs.add(o);
    }

    return rinputs;
  }

  private static @Nonnull String parseName(
    final @Nonnull Element root)
  {
    final Elements ename =
      root.getChildElements("program-name", PGLSLMetaXML.XML_URI_STRING);
    return ename.get(0).getValue();
  }

  private static @Nonnull SortedSet<Integer> parseSupportsES(
    final @Nonnull Element root)
    throws ValidityException
  {
    final TreeSet<Integer> versions = new TreeSet<Integer>();

    final Elements supports =
      root.getChildElements("supports", PGLSLMetaXML.XML_URI_STRING);

    final Element es = supports.get(0);
    final Elements esc =
      es.getChildElements("version", PGLSLMetaXML.XML_URI_STRING);

    for (int index = 0; index < esc.size(); ++index) {
      final Element e = esc.get(index);
      final Attribute av =
        e.getAttribute("number", PGLSLMetaXML.XML_URI_STRING);
      final Attribute aa = e.getAttribute("api", PGLSLMetaXML.XML_URI_STRING);

      try {
        final int avi = Integer.parseInt(av.getValue());

        final String aas = aa.getValue();
        if (aas.equals("glsl-es")) {
          versions.add(Integer.valueOf(avi));
        }

      } catch (final NumberFormatException x) {
        throw new ValidityException(
          "Could not parse number attribute on version element");
      }
    }

    return versions;
  }

  private static @Nonnull SortedSet<Integer> parseSupportsFull(
    final @Nonnull Element root)
    throws ValidityException
  {
    final TreeSet<Integer> versions = new TreeSet<Integer>();

    final Elements supports =
      root.getChildElements("supports", PGLSLMetaXML.XML_URI_STRING);

    final Element es = supports.get(0);
    final Elements esc =
      es.getChildElements("version", PGLSLMetaXML.XML_URI_STRING);

    for (int index = 0; index < esc.size(); ++index) {
      final Element e = esc.get(index);
      final Attribute av =
        e.getAttribute("number", PGLSLMetaXML.XML_URI_STRING);
      final Attribute aa = e.getAttribute("api", PGLSLMetaXML.XML_URI_STRING);

      try {
        final int avi = Integer.parseInt(av.getValue());

        final String aas = aa.getValue();
        if (aas.equals("glsl")) {
          versions.add(Integer.valueOf(avi));
        }

      } catch (final NumberFormatException x) {
        throw new ValidityException(
          "Could not parse number attribute on version element");
      }
    }

    return versions;
  }

  private static @Nonnull SortedSet<VertexInput> parseVertexInputs(
    final @Nonnull Element root)
    throws ConstraintError
  {
    final SortedSet<VertexInput> rinputs = new TreeSet<VertexInput>();

    final Elements eins =
      root.getChildElements(
        "declared-vertex-inputs",
        PGLSLMetaXML.XML_URI_STRING);
    final Element ei = eins.get(0);
    final Elements eic =
      ei.getChildElements("input", PGLSLMetaXML.XML_URI_STRING);

    for (int index = 0; index < eic.size(); ++index) {
      final Element e = eic.get(index);
      final Attribute an =
        e.getAttribute("name", PGLSLMetaXML.XML_URI_STRING);
      final Attribute at =
        e.getAttribute("type", PGLSLMetaXML.XML_URI_STRING);
      final VertexInput o = new VertexInput(an.getValue(), at.getValue());
      rinputs.add(o);
    }

    return rinputs;
  }

  private static @Nonnull SortedSet<VertexOutput> parseVertexOutputs(
    final @Nonnull Element root)
    throws ConstraintError
  {
    final SortedSet<VertexOutput> routputs = new TreeSet<VertexOutput>();

    final Elements es =
      root.getChildElements(
        "declared-vertex-outputs",
        PGLSLMetaXML.XML_URI_STRING);

    final Element eo = es.get(0);
    final Elements eoc =
      eo.getChildElements("output", PGLSLMetaXML.XML_URI_STRING);

    for (int index = 0; index < eoc.size(); ++index) {
      final Element e = eoc.get(index);
      final Attribute an =
        e.getAttribute("name", PGLSLMetaXML.XML_URI_STRING);
      final Attribute at =
        e.getAttribute("type", PGLSLMetaXML.XML_URI_STRING);
      final VertexOutput o = new VertexOutput(an.getValue(), at.getValue());
      routputs.add(o);
    }

    return routputs;
  }

  private static @Nonnull SortedSet<VertexParameter> parseVertexParameters(
    final @Nonnull Element root)
    throws ConstraintError
  {
    final SortedSet<VertexParameter> rinputs = new TreeSet<VertexParameter>();

    final Elements eins =
      root.getChildElements(
        "declared-vertex-parameters",
        PGLSLMetaXML.XML_URI_STRING);

    final Element ei = eins.get(0);
    final Elements eic =
      ei.getChildElements("parameter", PGLSLMetaXML.XML_URI_STRING);

    for (int index = 0; index < eic.size(); ++index) {
      final Element e = eic.get(index);
      final Attribute an =
        e.getAttribute("name", PGLSLMetaXML.XML_URI_STRING);
      final Attribute at =
        e.getAttribute("type", PGLSLMetaXML.XML_URI_STRING);
      final VertexParameter o =
        new VertexParameter(an.getValue(), at.getValue());
      rinputs.add(o);
    }

    return rinputs;
  }

  private static @Nonnull Element toXMLCompactMapping(
    final @Nonnull Version v,
    final @Nonnull CompactedShaders cs)
  {
    final String uri = PGLSLMetaXML.XML_URI_STRING;
    final Element e = new Element("g:mapping", uri);
    e.addAttribute(new Attribute("g:number", uri, Integer.toString(v
      .getVersion())));
    e.addAttribute(new Attribute("g:api", uri, v.getAPI().name));
    e.addAttribute(new Attribute("g:fragment-hash", uri, cs
      .getFragmentShader()));
    e.addAttribute(new Attribute("g:vertex-hash", uri, cs.getVertexShader()));
    return e;
  }

  private static @Nonnull Element toXMLVersionES(
    final @Nonnull Integer v)
  {
    final String uri = PGLSLMetaXML.XML_URI_STRING;
    final Element e = new Element("g:version", uri);
    e.addAttribute(new Attribute("g:number", uri, v.toString()));
    e.addAttribute(new Attribute("g:api", uri, "glsl-es"));
    return e;
  }

  private static @Nonnull Element toXMLVersionFull(
    final @Nonnull Integer v)
  {
    final String uri = PGLSLMetaXML.XML_URI_STRING;
    final Element e = new Element("g:version", uri);
    e.addAttribute(new Attribute("g:number", uri, v.toString()));
    e.addAttribute(new Attribute("g:api", uri, "glsl"));
    return e;
  }

  private final @CheckForNull SortedMap<Version, CompactedShaders> compact_mappings;
  private final @Nonnull SortedSet<FragmentInput>                  fragment_inputs;
  private final @Nonnull SortedMap<Integer, FragmentOutput>        fragment_outputs;
  private final @Nonnull SortedSet<FragmentParameter>              fragment_parameters;
  private final @Nonnull String                                    name;
  private final @Nonnull SortedSet<Integer>                        supports_es;
  private final @Nonnull SortedSet<Integer>                        supports_full;
  private final @Nonnull SortedSet<VertexInput>                    vertex_inputs;
  private final @Nonnull SortedSet<VertexOutput>                   vertex_outputs;
  private final @Nonnull SortedSet<VertexParameter>                vertex_parameters;

  private PGLSLMetaXML(
    final @Nonnull String in_name,
    final @Nonnull SortedSet<Integer> in_supports_es,
    final @Nonnull SortedSet<Integer> in_supports_full,
    final @Nonnull SortedSet<VertexInput> in_vertex_inputs,
    final @Nonnull SortedSet<VertexParameter> in_vertex_parameters,
    final @Nonnull SortedSet<VertexOutput> in_vertex_outputs,
    final @Nonnull SortedSet<FragmentInput> in_fragment_inputs,
    final @Nonnull SortedSet<FragmentParameter> in_fragment_parameters,
    final @Nonnull SortedMap<Integer, FragmentOutput> in_fragment_outputs,
    final @CheckForNull SortedMap<Version, CompactedShaders> in_compact_mappings)
    throws ConstraintError
  {
    this.name = Constraints.constrainNotNull(in_name, "Name");
    this.supports_es =
      Constraints.constrainNotNull(in_supports_es, "ES versions");
    this.supports_full =
      Constraints.constrainNotNull(in_supports_full, "Full versions");

    this.vertex_parameters =
      Constraints.constrainNotNull(in_vertex_parameters, "Vertex parameters");
    this.vertex_inputs =
      Constraints.constrainNotNull(in_vertex_inputs, "Vertex inputs");
    this.vertex_outputs =
      Constraints.constrainNotNull(in_vertex_outputs, "Vertex outputs");

    this.fragment_parameters =
      Constraints.constrainNotNull(
        in_fragment_parameters,
        "Fragment parameters");
    this.fragment_inputs =
      Constraints.constrainNotNull(in_fragment_inputs, "Fragment inputs");
    this.fragment_outputs =
      Constraints.constrainNotNull(in_fragment_outputs, "Fragment outputs");

    this.compact_mappings = in_compact_mappings;
  }

  /**
   * Return the mappings from version numbers to file hashes, iff the shaders
   * for this program are stored in compacted form.
   * 
   * @throws ConstraintError
   *           Iff {@link #isCompacted()} <code>== false</code>.
   */

  public @Nonnull SortedMap<Version, CompactedShaders> getCompactMappings()
    throws ConstraintError
  {
    Constraints.constrainArbitrary(
      this.isCompacted(),
      "Shaders are compacted");
    return Collections.unmodifiableSortedMap(this.compact_mappings);
  }

  /**
   * Retrieve the set of fragment shader inputs that were declared in the
   * given program.
   */

  public @Nonnull SortedSet<FragmentInput> getDeclaredFragmentInputs()
  {
    return this.fragment_inputs;
  }

  /**
   * Retrieve all of the declared fragment shader outputs.
   */

  public @Nonnull
    SortedMap<Integer, FragmentOutput>
    getDeclaredFragmentOutputs()
  {
    return Collections.unmodifiableSortedMap(this.fragment_outputs);
  }

  /**
   * Retrieve the set of fragment shader parameters that were declared in the
   * given program.
   */

  public @Nonnull
    SortedSet<FragmentParameter>
    getDeclaredFragmentParameters()
  {
    return this.fragment_parameters;
  }

  /**
   * Retrieve the set of vertex shader inputs that were declared in the given
   * program.
   */

  public @Nonnull SortedSet<VertexInput> getDeclaredVertexInputs()
  {
    return this.vertex_inputs;
  }

  /**
   * Retrieve the set of vertex shader outputs that were declared in the given
   * program.
   */

  public @Nonnull SortedSet<VertexOutput> getDeclaredVertexOutputs()
  {
    return this.vertex_outputs;
  }

  /**
   * Retrieve the set of vertex shader parameters that were declared in the
   * given program.
   */

  public @Nonnull SortedSet<VertexParameter> getDeclaredVertexParameters()
  {
    return this.vertex_parameters;
  }

  /**
   * Retrieve the fully-qualified name of the program as it was declared in
   * the original Parasol source.
   */

  public @Nonnull String getName()
  {
    return this.name;
  }

  /**
   * Retrieve all of the versions of GLSL ES on which this program is
   * supported.
   */

  public @Nonnull SortedSet<Integer> getSupportsES()
  {
    return Collections.unmodifiableSortedSet(this.supports_es);
  }

  /**
   * Retrieve all of the versions of GLSL on which this program is supported.
   */

  public @Nonnull SortedSet<Integer> getSupportsFull()
  {
    return Collections.unmodifiableSortedSet(this.supports_full);
  }

  /**
   * Return <code>true</code> if the shaders for this program are stored in
   * compacted form.
   */

  public boolean isCompacted()
  {
    return this.compact_mappings != null;
  }

  /**
   * Serialize metadata to XML.
   */

  public @Nonnull Element toXML()
  {
    final Element root = new Element("g:meta", PGLSLMetaXML.XML_URI_STRING);
    root.addAttribute(new Attribute(
      "g:version",
      PGLSLMetaXML.XML_URI_STRING,
      Integer.toString(PGLSLMetaXML.XML_VERSION)));
    root.appendChild(this.toXMLProgramName());
    root.appendChild(this.toXMLSupports());
    root.appendChild(this.toXMLDeclaredVertexParameters());
    root.appendChild(this.toXMLDeclaredFragmentParameters());
    root.appendChild(this.toXMLDeclaredVertexInputs());
    root.appendChild(this.toXMLDeclaredFragmentInputs());
    root.appendChild(this.toXMLDeclaredVertexOutputs());
    root.appendChild(this.toXMLDeclaredFragmentOutputs());

    if (this.isCompacted()) {
      root.appendChild(this.toXMLCompactMappings());
    }
    return root;
  }

  private @Nonnull Element toXMLCompactMappings()
  {
    final String uri = PGLSLMetaXML.XML_URI_STRING;
    final Element e = new Element("g:compact-mappings", uri);

    for (final Entry<Version, CompactedShaders> k : this.compact_mappings
      .entrySet()) {
      final Version v = k.getKey();
      final CompactedShaders cs = k.getValue();
      switch (v.getAPI()) {
        case API_GLSL:
        {
          e.appendChild(PGLSLMetaXML.toXMLCompactMapping(v, cs));
          break;
        }
        case API_GLSL_ES:
        {
          e.appendChild(PGLSLMetaXML.toXMLCompactMapping(v, cs));
          break;
        }
      }
    }

    return e;
  }

  private @Nonnull Element toXMLDeclaredFragmentInputs()
  {
    final String uri = PGLSLMetaXML.XML_URI_STRING;
    final Element e = new Element("g:declared-fragment-inputs", uri);
    for (final FragmentInput f : this.getDeclaredFragmentInputs()) {
      e.appendChild(f.toXML());
    }
    return e;
  }

  private @Nonnull Element toXMLDeclaredFragmentOutputs()
  {
    final String uri = PGLSLMetaXML.XML_URI_STRING;
    final Element e = new Element("g:declared-fragment-outputs", uri);
    for (final FragmentOutput f : this.getDeclaredFragmentOutputs().values()) {
      e.appendChild(f.toXML());
    }
    return e;
  }

  private @Nonnull Element toXMLDeclaredFragmentParameters()
  {
    final String uri = PGLSLMetaXML.XML_URI_STRING;
    final Element e = new Element("g:declared-fragment-parameters", uri);
    for (final FragmentParameter f : this.getDeclaredFragmentParameters()) {
      e.appendChild(f.toXML());
    }
    return e;
  }

  private @Nonnull Element toXMLDeclaredVertexInputs()
  {
    final String uri = PGLSLMetaXML.XML_URI_STRING;
    final Element e = new Element("g:declared-vertex-inputs", uri);
    for (final VertexInput v : this.getDeclaredVertexInputs()) {
      e.appendChild(v.toXML());
    }
    return e;
  }

  private @Nonnull Element toXMLDeclaredVertexOutputs()
  {
    final String uri = PGLSLMetaXML.XML_URI_STRING;
    final Element e = new Element("g:declared-vertex-outputs", uri);
    for (final VertexOutput v : this.getDeclaredVertexOutputs()) {
      e.appendChild(v.toXML());
    }
    return e;
  }

  private @Nonnull Element toXMLDeclaredVertexParameters()
  {
    final String uri = PGLSLMetaXML.XML_URI_STRING;
    final Element e = new Element("g:declared-vertex-parameters", uri);
    for (final VertexParameter v : this.getDeclaredVertexParameters()) {
      e.appendChild(v.toXML());
    }
    return e;
  }

  private @Nonnull Element toXMLProgramName()
  {
    final Element e =
      new Element("g:program-name", PGLSLMetaXML.XML_URI_STRING);
    e.appendChild(this.name);
    return e;
  }

  private @Nonnull Element toXMLSupports()
  {
    final Element e = new Element("g:supports", PGLSLMetaXML.XML_URI_STRING);
    for (final Integer s : this.getSupportsES()) {
      e.appendChild(PGLSLMetaXML.toXMLVersionES(s));
    }
    for (final Integer s : this.getSupportsFull()) {
      e.appendChild(PGLSLMetaXML.toXMLVersionFull(s));
    }
    return e;
  }

  /**
   * Return this metadata with the added compaction mappings
   * <code>mappings</code>.
   * 
   * @throws ConstraintError
   *           Iff this program is already compacted.
   */

  public @Nonnull PGLSLMetaXML withCompaction(
    final @Nonnull SortedMap<Version, CompactedShaders> mappings)
    throws ConstraintError
  {
    Constraints.constrainArbitrary(
      this.isCompacted() == false,
      "Not compacted");

    return new PGLSLMetaXML(
      this.name,
      this.supports_es,
      this.supports_full,
      this.vertex_inputs,
      this.vertex_parameters,
      this.vertex_outputs,
      this.fragment_inputs,
      this.fragment_parameters,
      this.fragment_outputs,
      mappings);
  }
}
