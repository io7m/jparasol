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

package com.io7m.jparasol.glsl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jfunctional.Option;
import com.io7m.jfunctional.OptionType;
import com.io7m.jfunctional.Some;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.UIError;
import com.io7m.jparasol.core.GVersionES;
import com.io7m.jparasol.core.GVersionFull;
import com.io7m.jparasol.glsl.GVersionNumberSetParser.Segment.SegmentAtom;
import com.io7m.jparasol.glsl.GVersionNumberSetParser.Segment.SegmentRange;
import com.io7m.jparasol.glsl.GVersionNumberSetToken.TokenLiteralIntegerDecimal;
import com.io7m.jparasol.glsl.GVersionNumberSetToken.Type;
import com.io7m.jparasol.lexer.LexerError;
import com.io7m.jparasol.parser.ParserError;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * A version number set parser.
 */

@EqualityReference public final class GVersionNumberSetParser
{
  /**
   * A version number bound.
   */

  @EqualityReference public static final class Bound
  {
    private final BoundType           type;
    private final OptionType<Integer> value;

    /**
     * Construct a bound.
     * 
     * @param in_type
     *          The type of bound
     * @param in_value
     *          The bound value
     */

    public Bound(
      final BoundType in_type,
      final OptionType<Integer> in_value)
    {
      this.type = NullCheck.notNull(in_type, "Type");
      this.value = NullCheck.notNull(in_value, "Value");
    }

    BoundType getType()
    {
      return this.type;
    }

    OptionType<Integer> getValue()
    {
      return this.value;
    }
  }

  /**
   * The type of version bounds.
   */

  public static enum BoundType
  {
    /**
     * The bound is exclusive.
     */

    BOUND_EXCLUSIVE,

    /**
     * The bound is inclusive.
     */

    BOUND_INCLUSIVE
  }

  /**
   * A version number segment.
   */

  @EqualityReference public static abstract class Segment
  {
    /**
     * A version number.
     */

    @EqualityReference public static final class SegmentAtom extends Segment
    {
      private final int value;

      int getValue()
      {
        return this.value;
      }

      /**
       * Construct an atom.
       * 
       * @param in_value
       *          The number
       */

      public SegmentAtom(
        final int in_value)
      {
        this.value = in_value;
      }
    }

    /**
     * A version number range.
     */

    @EqualityReference public static final class SegmentRange extends Segment
    {
      private final Bound lower;
      private final Bound upper;

      Bound getLower()
      {
        return this.lower;
      }

      Bound getUpper()
      {
        return this.upper;
      }

      /**
       * Construct a range.
       * 
       * @param in_lower
       *          The lower bound
       * @param in_upper
       *          The upper bound
       */

      public SegmentRange(
        final Bound in_lower,
        final Bound in_upper)
      {
        this.lower = NullCheck.notNull(in_lower, "Lower bound");
        this.upper = NullCheck.notNull(in_upper, "Upper bound");
      }
    }
  }

  private static GVersionES atomSetES(
    final SegmentAtom s)
    throws UIError
  {
    return GVersionNumberSetParser.checkVersionES(s.getValue());
  }

  private static GVersionFull atomSetFull(
    final SegmentAtom s)
    throws UIError
  {
    return GVersionNumberSetParser.checkVersionFull(s.getValue());
  }

  private static GVersionES checkVersionES(
    final int value)
    throws UIError
  {
    final GVersionES v = new GVersionES(value);
    if (GVersionES.ALL.contains(v)) {
      return v;
    }
    throw UIError.versionUnknownES(value);
  }

  private static GVersionFull checkVersionFull(
    final int value)
    throws UIError
  {
    final GVersionFull v = new GVersionFull(value);
    if (GVersionFull.ALL.contains(v)) {
      return v;
    }
    throw UIError.versionUnknown(value);
  }

  private static GVersionES rangeSetDecideLowerES(
    final SegmentRange s)
  {
    GVersionES lower = GVersionES.GLSL_ES_LOWER;

    switch (s.getLower().getType()) {
      case BOUND_EXCLUSIVE:
      {
        if (s.getLower().getValue().isSome()) {
          final Some<Integer> some = (Some<Integer>) s.getLower().getValue();
          lower = new GVersionES(some.get().intValue() + 1);
        } else {
          lower = new GVersionES(lower.versionGetNumber() + 1);
        }
        break;
      }
      case BOUND_INCLUSIVE:
      {
        if (s.getLower().getValue().isSome()) {
          final Some<Integer> some = (Some<Integer>) s.getLower().getValue();
          lower = new GVersionES(some.get().intValue());
        }
        break;
      }
    }
    return lower;
  }

  private static GVersionFull rangeSetDecideLowerFull(
    final SegmentRange s)
  {
    GVersionFull lower = GVersionFull.GLSL_LOWER;

    switch (s.getLower().getType()) {
      case BOUND_EXCLUSIVE:
      {
        if (s.getLower().getValue().isSome()) {
          final Some<Integer> some = (Some<Integer>) s.getLower().getValue();
          lower = new GVersionFull(some.get().intValue() + 1);
        } else {
          lower = new GVersionFull(lower.versionGetNumber() + 1);
        }
        break;
      }
      case BOUND_INCLUSIVE:
      {
        if (s.getLower().getValue().isSome()) {
          final Some<Integer> some = (Some<Integer>) s.getLower().getValue();
          lower = new GVersionFull(some.get().intValue());
        }
        break;
      }
    }
    return lower;
  }

  private static GVersionES rangeSetDecideUpperES(
    final SegmentRange s)
  {
    GVersionES upper =
      new GVersionES(GVersionES.GLSL_ES_UPPER.versionGetNumber() + 1);

    switch (s.getUpper().getType()) {
      case BOUND_EXCLUSIVE:
      {
        if (s.getUpper().getValue().isSome()) {
          final Some<Integer> some = (Some<Integer>) s.getUpper().getValue();
          upper = new GVersionES(some.get().intValue());
        } else {
          upper = new GVersionES(upper.versionGetNumber() - 1);
        }
        break;
      }
      case BOUND_INCLUSIVE:
      {
        if (s.getUpper().getValue().isSome()) {
          final Some<Integer> some = (Some<Integer>) s.getUpper().getValue();
          upper = new GVersionES(some.get().intValue() + 1);
        }
        break;
      }
    }
    return upper;
  }

  private static GVersionFull rangeSetDecideUpperFull(
    final SegmentRange s)
  {
    final int highest = GVersionFull.GLSL_UPPER.versionGetNumber();

    /**
     * Because the set function in Java take inclusive lower and exclusive
     * upper bounds, the given bound must be converted to the correct
     * exclusive upper bound.
     */

    switch (s.getUpper().getType()) {
      case BOUND_EXCLUSIVE:
      {
        if (s.getUpper().getValue().isSome()) {
          final Some<Integer> some = (Some<Integer>) s.getUpper().getValue();
          final Integer x = some.get();

          /**
           * If an exclusive upper bound was requested, and the bound is
           * greater than any available version (highest), then the bound
           * effectively becomes inclusive and therefore an exclusive upper
           * bound of (highest + 1) will cover the correct values.
           */

          if (x.intValue() > highest) {
            return new GVersionFull(highest + 1);
          }

          /**
           * Otherwise, the bound is already exclusive, so use it.
           */

          return new GVersionFull(x.intValue());
        }

        return GVersionFull.GLSL_UPPER;
      }
      case BOUND_INCLUSIVE:
      {
        if (s.getUpper().getValue().isSome()) {
          final Some<Integer> some = (Some<Integer>) s.getUpper().getValue();
          final Integer x = some.get();

          final SortedSet<GVersionFull> higher =
            GVersionFull.ALL.tailSet(new GVersionFull(x.intValue() + 1));

          /**
           * If the set of values greater than the requested inclusive bound
           * contains one or more values, then the first value in that set can
           * be used as the exclusive upper bound.
           */

          if (higher.size() >= 1) {
            return higher.first();
          }

          /**
           * Otherwise, the bound is fine as it is.
           */

          return new GVersionFull(x.intValue() + 1);
        }

        return new GVersionFull(highest + 1);
      }
    }

    throw new UnreachableCodeException();
  }

  private static Collection<? extends GVersionES> rangeSetES(
    final SegmentRange s)
  {
    final GVersionES lower = GVersionNumberSetParser.rangeSetDecideLowerES(s);
    final GVersionES upper = GVersionNumberSetParser.rangeSetDecideUpperES(s);
    return GVersionES.ALL.subSet(lower, upper);
  }

  private static Collection<? extends GVersionFull> rangeSetFull(
    final SegmentRange s)
  {
    final GVersionFull lower =
      GVersionNumberSetParser.rangeSetDecideLowerFull(s);
    final GVersionFull upper =
      GVersionNumberSetParser.rangeSetDecideUpperFull(s);
    return GVersionFull.ALL.subSet(lower, upper);
  }

  /**
   * Calculate a set of GLSL ES version numbers from the given segments.
   * 
   * @param segments
   *          The segments
   * @return A set of version numbers
   * @throws UIError
   *           On mistakes
   */

  public static SortedSet<GVersionES> segmentsSetES(
    final List<Segment> segments)
    throws UIError
  {
    NullCheck.notNullAll(segments, "Segments");

    final SortedSet<GVersionES> current = new TreeSet<GVersionES>();

    for (final Segment s : segments) {
      if (s instanceof SegmentRange) {
        current.addAll(GVersionNumberSetParser.rangeSetES((SegmentRange) s));
      } else {
        current.add(GVersionNumberSetParser.atomSetES((SegmentAtom) s));
      }
    }

    return current;
  }

  /**
   * Calculate a set of GLSL version numbers from the given segments.
   * 
   * @param segments
   *          The segments
   * @return A set of version numbers
   * @throws UIError
   *           On mistakes
   */

  public static SortedSet<GVersionFull> segmentsSetFull(
    final List<Segment> segments)
    throws UIError
  {
    NullCheck.notNullAll(segments, "Segments");

    final SortedSet<GVersionFull> current = new TreeSet<GVersionFull>();

    for (final Segment s : segments) {
      if (s instanceof SegmentRange) {
        current
          .addAll(GVersionNumberSetParser.rangeSetFull((SegmentRange) s));
      } else {
        current.add(GVersionNumberSetParser.atomSetFull((SegmentAtom) s));
      }
    }

    return current;
  }

  private final GVersionNumberSetLexer lexer;
  private final StringBuilder          message;
  private GVersionNumberSetToken       token;

  /**
   * Construct a new version number set parser.
   * 
   * @param in_lexer
   *          The lexer
   * @throws IOException
   *           On I/O errors
   * @throws LexerError
   *           On lexical errors
   */

  public GVersionNumberSetParser(
    final GVersionNumberSetLexer in_lexer)
    throws IOException,
      LexerError
  {
    this.lexer = NullCheck.notNull(in_lexer, "Lexer");
    this.message = new StringBuilder();
    this.token = in_lexer.token();
  }

  /**
   * Parse a lower bound.
   * 
   * @return A lower bound
   * @throws ParserError
   *           If a parse error occurs
   * @throws LexerError
   *           If a lexical error occurs
   * @throws IOException
   *           If an I/O error occurs
   */

  @SuppressWarnings("boxing") public Bound boundLower()
    throws ParserError,
      LexerError,
      IOException
  {
    final OptionType<Integer> none = Option.none();

    this.parserExpectOneOf(new Type[] {
      Type.TOKEN_SQUARE_LEFT,
      Type.TOKEN_ROUND_LEFT, });

    switch (this.token.getType()) {
      case TOKEN_ROUND_LEFT:
      {
        this.parserConsumeExact(Type.TOKEN_ROUND_LEFT);
        this.parserExpectOneOf(new Type[] {
          Type.TOKEN_LITERAL_INTEGER_DECIMAL,
          Type.TOKEN_COMMA, });

        switch (this.token.getType()) {
          case TOKEN_COMMA:
          {
            this.parserConsumeExact(Type.TOKEN_COMMA);
            return new Bound(BoundType.BOUND_EXCLUSIVE, none);
          }
          case TOKEN_LITERAL_INTEGER_DECIMAL:
          {
            final TokenLiteralIntegerDecimal x =
              (TokenLiteralIntegerDecimal) this.token;

            this.parserConsumeExact(Type.TOKEN_LITERAL_INTEGER_DECIMAL);
            this.parserConsumeExact(Type.TOKEN_COMMA);
            return new Bound(BoundType.BOUND_EXCLUSIVE, Option.some(x
              .getValue()
              .intValue()));
          }
          // $CASES-OMITTED$
          default:
            throw new UnreachableCodeException();
        }
      }
      case TOKEN_SQUARE_LEFT:
      {
        this.parserConsumeExact(Type.TOKEN_SQUARE_LEFT);
        this.parserExpectOneOf(new Type[] {
          Type.TOKEN_LITERAL_INTEGER_DECIMAL,
          Type.TOKEN_COMMA, });

        switch (this.token.getType()) {
          case TOKEN_COMMA:
          {
            this.parserConsumeExact(Type.TOKEN_COMMA);
            return new Bound(BoundType.BOUND_INCLUSIVE, none);
          }
          case TOKEN_LITERAL_INTEGER_DECIMAL:
          {
            final TokenLiteralIntegerDecimal x =
              (TokenLiteralIntegerDecimal) this.token;
            this.parserConsumeExact(Type.TOKEN_LITERAL_INTEGER_DECIMAL);
            this.parserConsumeExact(Type.TOKEN_COMMA);
            return new Bound(BoundType.BOUND_INCLUSIVE, Option.some(x
              .getValue()
              .intValue()));
          }
          // $CASES-OMITTED$
          default:
            throw new UnreachableCodeException();
        }
      }
      // $CASES-OMITTED$
      default:
        throw new UnreachableCodeException();
    }
  }

  /**
   * Parse an upper bound.
   * 
   * @return An upper bound
   * @throws ParserError
   *           On parse errors
   * @throws LexerError
   *           On lexical errors
   * @throws IOException
   *           On I/O errors
   */

  @SuppressWarnings("boxing") public Bound boundUpper()
    throws ParserError,
      LexerError,
      IOException
  {
    final OptionType<Integer> none = Option.none();

    this.parserExpectOneOf(new Type[] {
      Type.TOKEN_SQUARE_RIGHT,
      Type.TOKEN_LITERAL_INTEGER_DECIMAL,
      Type.TOKEN_ROUND_RIGHT, });

    switch (this.token.getType()) {
      case TOKEN_LITERAL_INTEGER_DECIMAL:
      {
        final TokenLiteralIntegerDecimal x =
          (TokenLiteralIntegerDecimal) this.token;
        this.parserConsumeExact(Type.TOKEN_LITERAL_INTEGER_DECIMAL);
        return this.boundUpperClose(Option.some(x.getValue().intValue()));
      }
      case TOKEN_ROUND_RIGHT:
      {
        return this.boundUpperClose(none);
      }
      case TOKEN_SQUARE_RIGHT:
      {
        return this.boundUpperClose(none);
      }
      // $CASES-OMITTED$
      default:
        break;
    }

    throw new UnreachableCodeException();
  }

  private Bound boundUpperClose(
    final OptionType<Integer> opt)
    throws ParserError,
      LexerError,
      IOException
  {
    switch (this.token.getType()) {
      case TOKEN_ROUND_RIGHT:
      {
        this.parserConsumeExact(Type.TOKEN_ROUND_RIGHT);
        return new Bound(BoundType.BOUND_EXCLUSIVE, opt);
      }
      case TOKEN_SQUARE_RIGHT:
      {
        this.parserConsumeExact(Type.TOKEN_SQUARE_RIGHT);
        return new Bound(BoundType.BOUND_INCLUSIVE, opt);
      }
      // $CASES-OMITTED$
      default:
        break;
    }

    throw new UnreachableCodeException();
  }

  protected void parserConsumeAny()
    throws IOException,
      LexerError
  {
    this.token = this.lexer.token();
  }

  protected void parserConsumeExact(
    final GVersionNumberSetToken.Type type)
    throws ParserError,
      IOException,
      LexerError
  {
    this.parserExpectExact(type);
    this.parserConsumeAny();
  }

  protected void parserExpectExact(
    final GVersionNumberSetToken.Type type)
    throws ParserError
  {
    if (this.token.getType() != type) {
      this.message.setLength(0);
      this.message.append("Expected ");
      this.message.append(type.getDescription());
      this.message.append(" but got ");
      this.parserShowToken();
      throw new ParserError(
        this.message.toString(),
        this.lexer.getFile(),
        this.token.getPosition());
    }
  }

  protected void parserExpectOneOf(
    final GVersionNumberSetToken.Type[] types)
    throws ParserError
  {
    for (final GVersionNumberSetToken.Type want : types) {
      if (this.token.getType() == want) {
        return;
      }
    }

    this.message.setLength(0);
    this.message.append("Expected one of {");
    for (int index = 0; index < types.length; ++index) {
      final GVersionNumberSetToken.Type t = types[index];
      this.message.append(t);
      if ((index + 1) != types.length) {
        this.message.append(", ");
      }
    }
    this.message.append("} but got ");
    this.parserShowToken();
    throw new ParserError(
      this.message.toString(),
      this.lexer.getFile(),
      this.token.getPosition());
  }

  private void parserShowToken()
  {
    this.message.append(this.token.getType().getDescription());
  }

  /**
   * Parse an atom.
   * 
   * @return An atom
   * @throws ParserError
   *           On parse errors
   * @throws LexerError
   *           On lexical errors
   * @throws IOException
   *           On I/O errors
   */

  public SegmentAtom segmentAtom()
    throws ParserError,
      LexerError,
      IOException
  {
    this.parserExpectExact(Type.TOKEN_LITERAL_INTEGER_DECIMAL);
    final TokenLiteralIntegerDecimal t =
      (TokenLiteralIntegerDecimal) this.token;
    this.parserConsumeExact(Type.TOKEN_LITERAL_INTEGER_DECIMAL);
    return new SegmentAtom(t.getValue().intValue());
  }

  /**
   * Parse a range
   * 
   * @return A range
   * @throws ParserError
   *           On parse errors
   * @throws LexerError
   *           On lexical errors
   * @throws IOException
   *           On I/O errors
   */

  public SegmentRange segmentRange()
    throws ParserError,
      LexerError,
      IOException
  {
    this.parserExpectOneOf(new Type[] {
      Type.TOKEN_SQUARE_LEFT,
      Type.TOKEN_ROUND_LEFT, });

    final Bound lower = this.boundLower();
    final Bound upper = this.boundUpper();

    if (this.token.getType() == Type.TOKEN_COMMA) {
      this.parserConsumeExact(Type.TOKEN_COMMA);
    }

    return new SegmentRange(lower, upper);
  }

  /**
   * @return <code>true</code> if the parser is at EOF
   */

  public boolean isAtEOF()
  {
    return this.token.getType() == Type.TOKEN_EOF;
  }

  /**
   * @return A list of version number segments
   * @throws ParserError
   *           If a parse error occurs
   * @throws LexerError
   *           If a lexical error occurs
   * @throws IOException
   *           If an I/O error occurs
   */

  public List<Segment> segments()
    throws ParserError,
      LexerError,
      IOException
  {
    final List<Segment> s = new ArrayList<Segment>();

    for (;;) {
      this.parserExpectOneOf(new Type[] {
        Type.TOKEN_SQUARE_LEFT,
        Type.TOKEN_LITERAL_INTEGER_DECIMAL,
        Type.TOKEN_COMMA,
        Type.TOKEN_EOF,
        Type.TOKEN_ROUND_LEFT, });

      switch (this.token.getType()) {
        case TOKEN_EOF:
        {
          return s;
        }
        case TOKEN_COMMA:
        {
          this.parserConsumeExact(Type.TOKEN_COMMA);
          return s;
        }
        case TOKEN_LITERAL_INTEGER_DECIMAL:
        {
          s.add(this.segmentAtom());
          break;
        }
        case TOKEN_ROUND_LEFT:
        {
          s.add(this.segmentRange());
          break;
        }
        case TOKEN_SQUARE_LEFT:
        {
          s.add(this.segmentRange());
          break;
        }
        // $CASES-OMITTED$
        default:
          throw new UnreachableCodeException();
      }
    }
  }
}
