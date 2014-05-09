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

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.xml.sax.SAXException;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jlog.Log;
import com.io7m.jlog.LogPolicyProperties;
import com.io7m.jlog.LogPolicyType;
import com.io7m.jlog.LogType;
import com.io7m.jproperties.JPropertyException;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * The main compactor program.
 */

@EqualityReference public final class PGLSLCompactorMain
{
  private PGLSLCompactorMain()
  {
    throw new UnreachableCodeException();
  }

  /**
   * The main program.
   * 
   * @param args
   *          Command line arguments
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
   * @throws JPropertyException
   *           On malformed config files
   */

  public static void main(
    final String[] args)
    throws ValidityException,
      ParsingException,
      IOException,
      NoSuchAlgorithmException,
      SAXException,
      ParserConfigurationException,
      JPropertyException
  {
    if (args.length != 2) {
      throw new IllegalArgumentException("usage: input output");
    }

    final File input = new File(args[0]);
    final File output = new File(args[1]);
    final Properties props = new Properties();

    final LogPolicyType policy =
      LogPolicyProperties.newPolicy(props, "com.io7m.jparasol.xml");
    final LogType logx = Log.newLog(policy, "compactor");
    PGLSLCompactor.newCompactor(input, output, logx);
  }
}
