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

package com.io7m.jparasol.tests.glsl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jparasol.UIError;
import com.io7m.jparasol.glsl.GVersion.GVersionES;
import com.io7m.jparasol.glsl.GVersion.GVersionFull;
import com.io7m.jparasol.glsl.GVersionNumberSetLexer;
import com.io7m.jparasol.glsl.GVersionNumberSetParser;
import com.io7m.jparasol.glsl.GVersionNumberSetParser.Segment;
import com.io7m.jparasol.lexer.LexerError;
import com.io7m.jparasol.parser.ParserError;

@SuppressWarnings("static-method") public final class GVersionNumberSetParserTest
{
  @SuppressWarnings("resource") public static void main(
    final String args[])
    throws LexerError,
      IOException,

      ParserError,
      UIError
  {
    final GVersionNumberSetLexer lexer =
      new GVersionNumberSetLexer(new FileInputStream(new File(args[0])));
    final GVersionNumberSetParser parser = new GVersionNumberSetParser(lexer);

    final TreeSet<GVersionFull> versions = new TreeSet<GVersionFull>();

    while (parser.isAtEOF() == false) {
      final List<Segment> s = parser.segments();
      versions.addAll(GVersionNumberSetParser.segmentsSetFull(s));
    }

    System.out.println(versions);
  }

  static GVersionNumberSetParser makeStringParser(
    final String text)
    throws IOException,
      LexerError

  {
    final ByteArrayInputStream bs = new ByteArrayInputStream(text.getBytes());
    final GVersionNumberSetLexer lexer = new GVersionNumberSetLexer(bs);
    return new GVersionNumberSetParser(lexer);
  }

  @Test public void testES_0()
    throws LexerError,
      IOException,

      ParserError,
      UIError
  {
    final GVersionNumberSetParser p =
      GVersionNumberSetParserTest.makeStringParser("100");

    final List<Segment> s = p.segments();
    final SortedSet<GVersionES> r = GVersionNumberSetParser.segmentsSetES(s);

    Assert.assertEquals(1, r.size());
    Assert.assertTrue(r.contains(GVersionES.GLSL_ES_100));
  }

  @Test public void testES_1()
    throws LexerError,
      IOException,

      ParserError,
      UIError
  {
    final GVersionNumberSetParser p =
      GVersionNumberSetParserTest.makeStringParser("[100,]");

    final List<Segment> s = p.segments();
    final SortedSet<GVersionES> r = GVersionNumberSetParser.segmentsSetES(s);

    Assert.assertEquals(GVersionES.ALL, r);
  }

  @Test public void testES_2()
    throws LexerError,
      IOException,

      ParserError,
      UIError
  {
    final GVersionNumberSetParser p =
      GVersionNumberSetParserTest.makeStringParser("[,]");

    final List<Segment> s = p.segments();
    final SortedSet<GVersionES> r = GVersionNumberSetParser.segmentsSetES(s);

    Assert.assertEquals(GVersionES.ALL, r);
  }

  @Test public void testES_3()
    throws LexerError,
      IOException,

      ParserError,
      UIError
  {
    final GVersionNumberSetParser p =
      GVersionNumberSetParserTest.makeStringParser("[,)");

    final List<Segment> s = p.segments();
    final SortedSet<GVersionES> r = GVersionNumberSetParser.segmentsSetES(s);

    Assert.assertEquals(1, r.size());
    Assert.assertTrue(r.contains(GVersionES.GLSL_ES_100));
  }

  @Test public void testES_4()
    throws LexerError,
      IOException,

      ParserError,
      UIError
  {
    final GVersionNumberSetParser p =
      GVersionNumberSetParserTest.makeStringParser("(,)");

    final List<Segment> s = p.segments();
    final SortedSet<GVersionES> r = GVersionNumberSetParser.segmentsSetES(s);

    Assert.assertEquals(0, r.size());
  }

  @Test public void testES_5()
    throws LexerError,
      IOException,

      ParserError,
      UIError
  {
    final GVersionNumberSetParser p =
      GVersionNumberSetParserTest.makeStringParser("(,]");

    final List<Segment> s = p.segments();
    final SortedSet<GVersionES> r = GVersionNumberSetParser.segmentsSetES(s);

    Assert.assertEquals(1, r.size());
    Assert.assertTrue(r.contains(GVersionES.GLSL_ES_300));
  }

  @Test public void testES_6()
    throws LexerError,
      IOException,

      ParserError,
      UIError
  {
    final GVersionNumberSetParser p =
      GVersionNumberSetParserTest.makeStringParser("(100,]");

    final List<Segment> s = p.segments();
    final SortedSet<GVersionES> r = GVersionNumberSetParser.segmentsSetES(s);

    Assert.assertEquals(1, r.size());
    Assert.assertTrue(r.contains(GVersionES.GLSL_ES_300));
  }

  @Test public void testES_7()
    throws LexerError,
      IOException,

      ParserError,
      UIError
  {
    final GVersionNumberSetParser p =
      GVersionNumberSetParserTest.makeStringParser("[,300)");

    final List<Segment> s = p.segments();
    final SortedSet<GVersionES> r = GVersionNumberSetParser.segmentsSetES(s);

    Assert.assertEquals(1, r.size());
    Assert.assertTrue(r.contains(GVersionES.GLSL_ES_100));
  }

  @Test public void testES_8()
    throws LexerError,
      IOException,

      ParserError,
      UIError
  {
    final GVersionNumberSetParser p =
      GVersionNumberSetParserTest.makeStringParser("(,300]");

    final List<Segment> s = p.segments();
    final SortedSet<GVersionES> r = GVersionNumberSetParser.segmentsSetES(s);

    Assert.assertEquals(1, r.size());
    Assert.assertTrue(r.contains(GVersionES.GLSL_ES_300));
  }

  @Test public void testES_Multi0()
    throws LexerError,
      IOException,

      ParserError,
      UIError
  {
    final GVersionNumberSetParser p =
      GVersionNumberSetParserTest.makeStringParser("100 , 300");

    final List<Segment> s0 = p.segments();
    final SortedSet<GVersionES> r0 =
      GVersionNumberSetParser.segmentsSetES(s0);
    final List<Segment> s1 = p.segments();
    final SortedSet<GVersionES> r1 =
      GVersionNumberSetParser.segmentsSetES(s1);

    Assert.assertEquals(1, r0.size());
    Assert.assertTrue(r0.contains(GVersionES.GLSL_ES_100));

    Assert.assertEquals(1, r1.size());
    Assert.assertTrue(r1.contains(GVersionES.GLSL_ES_300));
  }

  @Test public void testFull_0()
    throws LexerError,
      IOException,

      ParserError,
      UIError
  {
    final GVersionNumberSetParser p =
      GVersionNumberSetParserTest.makeStringParser("110");

    final List<Segment> s = p.segments();
    final SortedSet<GVersionFull> r =
      GVersionNumberSetParser.segmentsSetFull(s);

    Assert.assertEquals(1, r.size());
    Assert.assertTrue(r.contains(GVersionFull.GLSL_110));
  }

  @Test public void testFull_1()
    throws LexerError,
      IOException,

      ParserError,
      UIError
  {
    final GVersionNumberSetParser p =
      GVersionNumberSetParserTest.makeStringParser("[,]");

    final List<Segment> s = p.segments();
    final SortedSet<GVersionFull> r =
      GVersionNumberSetParser.segmentsSetFull(s);

    Assert.assertEquals(GVersionFull.ALL, r);
  }

  @Test public void testFull_10()
    throws LexerError,
      IOException,

      ParserError,
      UIError
  {
    final GVersionNumberSetParser p =
      GVersionNumberSetParserTest.makeStringParser("[,440]");

    final List<Segment> s = p.segments();
    final SortedSet<GVersionFull> r =
      GVersionNumberSetParser.segmentsSetFull(s);

    final SortedSet<GVersionFull> e = GVersionFull.ALL;

    Assert.assertEquals(e, r);
  }

  @Test public void testFull_11()
    throws LexerError,
      IOException,

      ParserError,
      UIError
  {
    final GVersionNumberSetParser p =
      GVersionNumberSetParserTest.makeStringParser("[,430]");

    final List<Segment> s = p.segments();
    final SortedSet<GVersionFull> r =
      GVersionNumberSetParser.segmentsSetFull(s);

    final SortedSet<GVersionFull> e =
      new TreeSet<GVersionFull>(GVersionFull.ALL);
    e.remove(GVersionFull.GLSL_440);

    Assert.assertEquals(e, r);
  }

  @Test public void testFull_12()
    throws LexerError,
      IOException,

      ParserError,
      UIError
  {
    final GVersionNumberSetParser p =
      GVersionNumberSetParserTest.makeStringParser("[,420]");

    final List<Segment> s = p.segments();
    final SortedSet<GVersionFull> r =
      GVersionNumberSetParser.segmentsSetFull(s);

    final SortedSet<GVersionFull> e =
      new TreeSet<GVersionFull>(GVersionFull.ALL);
    e.remove(GVersionFull.GLSL_440);
    e.remove(GVersionFull.GLSL_430);

    Assert.assertEquals(e, r);
  }

  @Test public void testFull_2()
    throws LexerError,
      IOException,

      ParserError,
      UIError
  {
    final GVersionNumberSetParser p =
      GVersionNumberSetParserTest.makeStringParser("[,)");

    final List<Segment> s = p.segments();
    final SortedSet<GVersionFull> r =
      GVersionNumberSetParser.segmentsSetFull(s);

    final TreeSet<GVersionFull> e =
      new TreeSet<GVersionFull>(GVersionFull.ALL);
    e.remove(GVersionFull.GLSL_UPPER);

    Assert.assertEquals(e, r);
  }

  @Test public void testFull_3()
    throws LexerError,
      IOException,

      ParserError,
      UIError
  {
    final GVersionNumberSetParser p =
      GVersionNumberSetParserTest.makeStringParser("(,]");

    final List<Segment> s = p.segments();
    final SortedSet<GVersionFull> r =
      GVersionNumberSetParser.segmentsSetFull(s);

    final TreeSet<GVersionFull> e =
      new TreeSet<GVersionFull>(GVersionFull.ALL);
    e.remove(GVersionFull.GLSL_LOWER);

    Assert.assertEquals(e, r);
  }

  @Test public void testFull_4()
    throws LexerError,
      IOException,

      ParserError,
      UIError
  {
    final GVersionNumberSetParser p =
      GVersionNumberSetParserTest.makeStringParser("(,)");

    final List<Segment> s = p.segments();
    final SortedSet<GVersionFull> r =
      GVersionNumberSetParser.segmentsSetFull(s);

    final TreeSet<GVersionFull> e =
      new TreeSet<GVersionFull>(GVersionFull.ALL);
    e.remove(GVersionFull.GLSL_LOWER);
    e.remove(GVersionFull.GLSL_UPPER);

    Assert.assertEquals(e, r);
  }

  @Test public void testFull_5()
    throws LexerError,
      IOException,

      ParserError,
      UIError
  {
    final GVersionNumberSetParser p =
      GVersionNumberSetParserTest.makeStringParser("[,330)");

    final List<Segment> s = p.segments();
    final SortedSet<GVersionFull> r =
      GVersionNumberSetParser.segmentsSetFull(s);

    final SortedSet<GVersionFull> e =
      GVersionFull.ALL.subSet(GVersionFull.GLSL_LOWER, GVersionFull.GLSL_330);

    Assert.assertEquals(e, r);
  }

  @Test public void testFull_6()
    throws LexerError,
      IOException,

      ParserError,
      UIError
  {
    final GVersionNumberSetParser p =
      GVersionNumberSetParserTest.makeStringParser("[,430)");

    final List<Segment> s = p.segments();
    final SortedSet<GVersionFull> r =
      GVersionNumberSetParser.segmentsSetFull(s);

    final SortedSet<GVersionFull> e =
      GVersionFull.ALL.subSet(GVersionFull.GLSL_LOWER, GVersionFull.GLSL_430);

    Assert.assertEquals(e, r);
  }

  @Test public void testFull_7()
    throws LexerError,
      IOException,

      ParserError,
      UIError
  {
    final GVersionNumberSetParser p =
      GVersionNumberSetParserTest.makeStringParser("[,440)");

    final List<Segment> s = p.segments();
    final SortedSet<GVersionFull> r =
      GVersionNumberSetParser.segmentsSetFull(s);

    final SortedSet<GVersionFull> e =
      GVersionFull.ALL.subSet(GVersionFull.GLSL_LOWER, GVersionFull.GLSL_440);

    Assert.assertEquals(e, r);
  }

  @Test public void testFull_8()
    throws LexerError,
      IOException,

      ParserError,
      UIError
  {
    final GVersionNumberSetParser p =
      GVersionNumberSetParserTest.makeStringParser("[,450)");

    final List<Segment> s = p.segments();
    final SortedSet<GVersionFull> r =
      GVersionNumberSetParser.segmentsSetFull(s);

    final SortedSet<GVersionFull> e = GVersionFull.ALL;

    Assert.assertEquals(e, r);
  }

  @Test public void testFull_9()
    throws LexerError,
      IOException,

      ParserError,
      UIError
  {
    final GVersionNumberSetParser p =
      GVersionNumberSetParserTest.makeStringParser("[,450]");

    final List<Segment> s = p.segments();
    final SortedSet<GVersionFull> r =
      GVersionNumberSetParser.segmentsSetFull(s);

    final SortedSet<GVersionFull> e = GVersionFull.ALL;

    Assert.assertEquals(e, r);
  }
}
