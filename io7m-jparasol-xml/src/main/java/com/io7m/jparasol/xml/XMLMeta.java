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
import java.util.Map;
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
import com.io7m.jlog.LogUsableType;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jparasol.core.CompiledShaderMetaType;
import com.io7m.jparasol.core.FragmentInput;
import com.io7m.jparasol.core.FragmentOutput;
import com.io7m.jparasol.core.FragmentParameter;
import com.io7m.jparasol.core.GVersionES;
import com.io7m.jparasol.core.GVersionFull;
import com.io7m.jparasol.core.GVersionType;
import com.io7m.jparasol.core.JPMissingHash;
import com.io7m.jparasol.core.VertexInput;
import com.io7m.jparasol.core.VertexOutput;
import com.io7m.jparasol.core.VertexParameter;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * General serialization functions.
 */

@EqualityReference public final class XMLMeta
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
      XML_URI = new URI(XMLMeta.XML_URI_STRING);
      XML_VERSION = 5;
    } catch (final URISyntaxException e) {
      throw new UnreachableCodeException(e);
    }
  }

  /**
   * Check that the document is of a supported version.
   * 
   * @param root
   *          The root element.
   * @throws JPXMLValidityException
   *           If the document is of the wrong version or invalid.
   */

  static void checkVersion(
    final Element root)
    throws JPXMLValidityException
  {
    final Attribute version =
      root.getAttribute("version", XMLMeta.XML_URI_STRING);
    assert version != null;

    try {
      final int version_number = Integer.parseInt(version.getValue());
      if (version_number != XMLMeta.XML_VERSION) {
        final StringBuilder message = new StringBuilder();
        message.append("Unsupported version ");
        message.append(version_number);
        message.append(", supported versions are: ");
        message.append(XMLMeta.XML_VERSION);
        throw new JPXMLValidityException(message.toString());
      }
    } catch (final NumberFormatException x) {
      final StringBuilder message = new StringBuilder();
      message
        .append("Could not parse 'version' attribute as numeric value: ");
      message.append(x.getMessage());
      throw new JPXMLValidityException(message.toString());
    }
  }

  /**
   * Extract metadata from the given document.
   * 
   * @throws JPXMLValidityException
   *           Iff the given document is not a valid metadata document.
   * @throws JPMissingHash
   *           On missing hashes for supported versions.
   */

  private static CompiledShaderMetaType fromDocument(
    final Document doc)
    throws JPXMLValidityException,
      JPMissingHash
  {
    NullCheck.notNull(doc, "Document");

    final Element root = doc.getRootElement();
    assert root != null;
    XMLMeta.checkVersion(root);

    if ("meta-vertex".equals(root.getLocalName())) {
      return XMLUncompactedVertexShaderMeta.parseFromXML(root);
    }

    if ("meta-vertex-compacted".equals(root.getLocalName())) {
      return XMLCompactedVertexShaderMeta.parseFromXML(root);
    }

    if ("meta-fragment".equals(root.getLocalName())) {
      return XMLUncompactedFragmentShaderMeta.parseFromXML(root);
    }

    if ("meta-fragment-compacted".equals(root.getLocalName())) {
      return XMLCompactedFragmentShaderMeta.parseFromXML(root);
    }

    if ("meta-program".equals(root.getLocalName())) {
      return XMLUncompactedProgramShaderMeta.parseFromXML(root);
    }

    final StringBuilder message = new StringBuilder();
    message
      .append("Expected one of {'meta-vertex','meta-vertex-compacted','meta-fragment','meta-fragment-compacted','meta-program'} but got '");
    message.append(root.getLocalName());
    message.append("'");
    throw new JPXMLValidityException(message.toString());
  }

  /**
   * Load XML metadata from the given stream.
   * 
   * @param stream
   *          The stream
   * @param log
   *          A log interface
   * @return XML metadata
   * @throws JPXMLException
   *           On XML-related errors.
   * @throws JPMissingHash
   *           On missing hashes for supported versions.
   */

  public static CompiledShaderMetaType fromStream(
    final InputStream stream,
    final LogUsableType log)
    throws JPXMLException,
      JPMissingHash
  {
    try {
      return XMLMeta.fromDocument(XMLMeta.fromStreamValidate(stream, log));
    } catch (final ValidityException e) {
      throw new JPXMLValidityException(e);
    } catch (final SAXException e) {
      throw new JPXMLException(e);
    } catch (final ParserConfigurationException e) {
      throw new JPXMLException(e);
    } catch (final ParsingException e) {
      throw new JPXMLException(e);
    } catch (final IOException e) {
      throw new JPXMLException(e);
    }
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
      XMLMeta.class.getResourceAsStream("/com/io7m/jparasol/meta.xsd");

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

      assert doc != null;
      return doc;
    } finally {
      xsd_stream.close();
    }
  }

  /**
   * @param root
   *          The root element.
   * @return The program name.
   */

  static String parseName(
    final Element root)
  {
    final Elements ename =
      root.getChildElements("program-name", XMLMeta.XML_URI_STRING);
    final String r = ename.get(0).getValue();
    assert r != null;
    return r;
  }

  /**
   * @param root
   *          The root element.
   * @return The set of supported versions.
   * @throws JPXMLValidityException
   *           If an error occurs whilst parsing.
   */

  static SortedSet<GVersionType> parseSupports(
    final Element root)
    throws JPXMLValidityException
  {
    final SortedSet<GVersionType> versions = new TreeSet<GVersionType>();

    final Elements supports =
      root.getChildElements("supports", XMLMeta.XML_URI_STRING);

    final Element es = supports.get(0);
    final Elements esc =
      es.getChildElements("version", XMLMeta.XML_URI_STRING);

    for (int index = 0; index < esc.size(); ++index) {
      final Element e = esc.get(index);
      assert e != null;
      versions.add(XMLGVersion.parseFromXML(e));
    }

    return versions;
  }

  /**
   * @param root
   *          The root element.
   * @return The version → hash mappings.
   * @throws JPXMLValidityException
   *           If an error occurs whilst parsing.
   */

  static SortedMap<GVersionType, String> parseVersionHashes(
    final Element root)
    throws JPXMLValidityException
  {
    final SortedMap<GVersionType, String> m =
      new TreeMap<GVersionType, String>();

    final Element ecm =
      root.getFirstChildElement("version-hashes", XMLMeta.XML_URI_STRING);
    final Elements ecms =
      ecm.getChildElements("version-hash", XMLMeta.XML_URI_STRING);

    for (int index = 0; index < ecms.size(); ++index) {
      final Element em = ecms.get(index);

      final Attribute av = em.getAttribute("number", XMLMeta.XML_URI_STRING);
      final Attribute aa = em.getAttribute("api", XMLMeta.XML_URI_STRING);

      try {
        final String hash = em.getValue();
        assert hash != null;
        final String v_api = aa.getValue();
        assert v_api != null;

        final GVersionType v;
        if (v_api.equals(GVersionES.API_NAME)) {
          v = new GVersionES(Integer.parseInt(av.getValue()));
        } else if (v_api.equals(GVersionFull.API_NAME)) {
          v = new GVersionFull(Integer.parseInt(av.getValue()));
        } else {
          throw new JPXMLValidityException("Unknown API '" + v_api + "'");
        }

        m.put(v, hash);
      } catch (final NumberFormatException x) {
        throw new JPXMLValidityException(
          "Could not parse 'version' number on 'mapping' element: "
            + x.getMessage());
      }
    }

    return m;
  }

  /**
   * Serialize parameters to XML.
   * 
   * @param fp
   *          The parameters.
   * @param fi
   *          The inputs.
   * @param fo
   *          The outputs.
   * @return An XML element.
   */

  static Element serializeParametersFragmentToXML(
    final SortedSet<FragmentParameter> fp,
    final SortedSet<FragmentInput> fi,
    final SortedMap<Integer, FragmentOutput> fo)
  {
    final String uri = XMLMeta.XML_URI_STRING;
    final Element e = new Element("g:parameters-fragment", uri);
    e.appendChild(XMLFragmentParameter
      .serializeDeclaredFragmentParametersToXML(fp));
    e.appendChild(XMLFragmentInput.serializeDeclaredFragmentInputsToXML(fi));
    e
      .appendChild(XMLFragmentOutput
        .serializeDeclaredFragmentOutputsToXML(fo));
    return e;
  }

  /**
   * Serialize parameters to XML.
   * 
   * @param vp
   *          The parameters.
   * @param vi
   *          The inputs.
   * @param vo
   *          The outputs.
   * @return An XML element.
   */

  static Element serializeParametersVertexToXML(
    final SortedSet<VertexParameter> vp,
    final SortedSet<VertexInput> vi,
    final SortedSet<VertexOutput> vo)
  {
    final String uri = XMLMeta.XML_URI_STRING;
    final Element e = new Element("g:parameters-vertex", uri);
    e.appendChild(XMLVertexParameter
      .serializeDeclaredVertexParametersToXML(vp));
    e.appendChild(XMLVertexInput.serializeDeclaredVertexInputsToXML(vi));
    e.appendChild(XMLVertexOutput.serializeDeclaredVertexOutputsToXML(vo));
    return e;
  }

  /**
   * Serialize the program name.
   * 
   * @param name
   *          The name.
   * @return An XML element.
   */

  static Element serializeProgramName(
    final String name)
  {
    final Element e = new Element("g:program-name", XMLMeta.XML_URI_STRING);
    e.appendChild(name);
    return e;
  }

  /**
   * Serialize version numbers.
   * 
   * @param supports_es
   *          The GLSL ES versions.
   * @param supports_full
   *          The GLSL versions.
   * @return An XML element.
   */

  static Element serializeSupports(
    final SortedSet<GVersionES> supports_es,
    final SortedSet<GVersionFull> supports_full)
  {
    final Element e = new Element("g:supports", XMLMeta.XML_URI_STRING);
    for (final GVersionES s : supports_es) {
      assert s != null;
      e.appendChild(XMLGVersionES.serializeToXML(s));
    }
    for (final GVersionFull s : supports_full) {
      assert s != null;
      e.appendChild(XMLGVersionFull.serializeToXML(s));
    }
    return e;
  }

  /**
   * Serialize a version → hash mapping.
   * 
   * @param v
   *          The version.
   * @param hash
   *          The hash.
   * @return An XML element.
   */

  static Element serializeVersionHash(
    final GVersionType v,
    final String hash)
  {
    final String uri = XMLMeta.XML_URI_STRING;
    final Element e = new Element("g:version-hash", uri);
    e.addAttribute(new Attribute("g:number", uri, Integer.toString(v
      .versionGetNumber())));
    e.addAttribute(new Attribute("g:api", uri, v.versionGetAPIName()));
    e.appendChild(hash);
    return e;
  }

  /**
   * Serialize version → hash mappings.
   * 
   * @param version_to_hash
   *          The mappings.
   * @return An XML element.
   */

  static Element serializeVersionHashes(
    final Map<GVersionType, String> version_to_hash)
  {
    final String uri = XMLMeta.XML_URI_STRING;
    final Element e = new Element("g:version-hashes", uri);

    for (final Entry<GVersionType, String> k : version_to_hash.entrySet()) {
      final GVersionType v = k.getKey();
      assert v != null;
      final String cs = k.getValue();
      assert cs != null;
      e.appendChild(XMLMeta.serializeVersionHash(v, cs));
    }

    return e;
  }

  private XMLMeta()
  {
    throw new UnreachableCodeException();
  }
}
