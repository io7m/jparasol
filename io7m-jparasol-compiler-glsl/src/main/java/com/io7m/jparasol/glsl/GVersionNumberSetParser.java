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

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jaux.functional.Option;
import com.io7m.jaux.functional.Option.None;
import com.io7m.jaux.functional.Option.Some;
import com.io7m.jparasol.UIError;
import com.io7m.jparasol.glsl.GVersion.GVersionES;
import com.io7m.jparasol.glsl.GVersion.GVersionFull;
import com.io7m.jparasol.glsl.GVersionNumberSetParser.Segment.SegmentAtom;
import com.io7m.jparasol.glsl.GVersionNumberSetParser.Segment.SegmentRange;
import com.io7m.jparasol.glsl.GVersionNumberSetToken.TokenLiteralIntegerDecimal;
import com.io7m.jparasol.glsl.GVersionNumberSetToken.Type;
import com.io7m.jparasol.lexer.LexerError;
import com.io7m.jparasol.parser.ParserError;

public final class GVersionNumberSetParser
{
  public static final class Bound
  {
    public final @Nonnull BoundType       type;
    public final @Nonnull Option<Integer> value;

    public Bound(
      final @Nonnull BoundType type,
      final @Nonnull Option<Integer> value)
    {
      this.type = type;
      this.value = value;
    }
  }

  public static enum BoundType
  {
    BOUND_EXCLUSIVE,
    BOUND_INCLUSIVE
  }

  public static abstract class Segment
  {
    public static final class SegmentAtom extends Segment
    {
      public final int value;

      public SegmentAtom(
        final int value)
      {
        this.value = value;
      }
    }

    public static final class SegmentRange extends Segment
    {
      public final @Nonnull Bound lower;
      public final @Nonnull Bound upper;

      public SegmentRange(
        final @Nonnull Bound lower,
        final @Nonnull Bound upper)
      {
        this.lower = lower;
        this.upper = upper;
      }
    }
  }

  private static @Nonnull GVersionES atomSetES(
    final @Nonnull SegmentAtom s)
    throws UIError,
      ConstraintError
  {
    return GVersionNumberSetParser.checkVersionES(s.value);
  }

  private static @Nonnull GVersionFull atomSetFull(
    final @Nonnull SegmentAtom s)
    throws UIError,
      ConstraintError
  {
    return GVersionNumberSetParser.checkVersionFull(s.value);
  }

  private static @Nonnull GVersionES checkVersionES(
    final int value)
    throws UIError,
      ConstraintError
  {
    final GVersionES v = new GVersionES(value);
    if (GVersionES.ALL.contains(v)) {
      return v;
    }
    throw UIError.versionUnknownES(value);
  }

  private static @Nonnull GVersionFull checkVersionFull(
    final int value)
    throws UIError,
      ConstraintError
  {
    final GVersionFull v = new GVersionFull(value);
    if (GVersionFull.ALL.contains(v)) {
      return v;
    }
    throw UIError.versionUnknown(value);
  }

  private static @Nonnull GVersionES rangeSetDecideLowerES(
    final @Nonnull SegmentRange s)
  {
    GVersionES lower = GVersionES.GLSL_ES_LOWER;

    switch (s.lower.type) {
      case BOUND_EXCLUSIVE:
      {
        if (s.lower.value.isSome()) {
          final Some<Integer> some = (Some<Integer>) s.lower.value;
          lower = new GVersionES(some.value.intValue() + 1);
        } else {
          lower = new GVersionES(lower.getNumber() + 1);
        }
        break;
      }
      case BOUND_INCLUSIVE:
      {
        if (s.lower.value.isSome()) {
          final Some<Integer> some = (Some<Integer>) s.lower.value;
          lower = new GVersionES(some.value.intValue());
        }
        break;
      }
    }
    return lower;
  }

  private static @Nonnull GVersionFull rangeSetDecideLowerFull(
    final @Nonnull SegmentRange s)
  {
    GVersionFull lower = GVersionFull.GLSL_LOWER;

    switch (s.lower.type) {
      case BOUND_EXCLUSIVE:
      {
        if (s.lower.value.isSome()) {
          final Some<Integer> some = (Some<Integer>) s.lower.value;
          lower = new GVersionFull(some.value.intValue() + 1);
        } else {
          lower = new GVersionFull(lower.getNumber() + 1);
        }
        break;
      }
      case BOUND_INCLUSIVE:
      {
        if (s.lower.value.isSome()) {
          final Some<Integer> some = (Some<Integer>) s.lower.value;
          lower = new GVersionFull(some.value.intValue());
        }
        break;
      }
    }
    return lower;
  }

  private static @Nonnull GVersionES rangeSetDecideUpperES(
    final @Nonnull SegmentRange s)
  {
    GVersionES upper =
      new GVersionES(GVersionES.GLSL_ES_UPPER.getNumber() + 1);

    switch (s.upper.type) {
      case BOUND_EXCLUSIVE:
      {
        if (s.upper.value.isSome()) {
          final Some<Integer> some = (Some<Integer>) s.upper.value;
          upper = new GVersionES(some.value.intValue());
        } else {
          upper = new GVersionES(upper.getNumber() - 1);
        }
        break;
      }
      case BOUND_INCLUSIVE:
      {
        if (s.upper.value.isSome()) {
          final Some<Integer> some = (Some<Integer>) s.upper.value;
          upper = new GVersionES(some.value.intValue() + 1);
        }
        break;
      }
    }
    return upper;
  }

  private static @Nonnull GVersionFull rangeSetDecideUpperFull(
    final @Nonnull SegmentRange s)
  {
    final int highest = GVersionFull.GLSL_UPPER.getNumber();

    /**
     * Because the set function in Java take inclusive lower and exclusive
     * upper bounds, the given bound must be converted to the correct
     * exclusive upper bound.
     */

    switch (s.upper.type) {
      case BOUND_EXCLUSIVE:
      {
        if (s.upper.value.isSome()) {
          final Some<Integer> some = (Some<Integer>) s.upper.value;
          final Integer x = some.value;

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
        if (s.upper.value.isSome()) {
          final Some<Integer> some = (Some<Integer>) s.upper.value;
          final Integer x = some.value;

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
    final @Nonnull SegmentRange s)
  {
    final GVersionES lower = GVersionNumberSetParser.rangeSetDecideLowerES(s);
    final GVersionES upper = GVersionNumberSetParser.rangeSetDecideUpperES(s);
    return GVersionES.ALL.subSet(lower, upper);
  }

  private static Collection<? extends GVersionFull> rangeSetFull(
    final @Nonnull SegmentRange s)
  {
    final GVersionFull lower =
      GVersionNumberSetParser.rangeSetDecideLowerFull(s);
    final GVersionFull upper =
      GVersionNumberSetParser.rangeSetDecideUpperFull(s);
    return GVersionFull.ALL.subSet(lower, upper);
  }

  public static @Nonnull SortedSet<GVersionES> segmentsSetES(
    final @Nonnull List<Segment> segments)
    throws UIError,
      ConstraintError
  {
    final TreeSet<GVersionES> current = new TreeSet<GVersionES>();

    for (final Segment s : segments) {
      if (s instanceof SegmentRange) {
        current.addAll(GVersionNumberSetParser.rangeSetES((SegmentRange) s));
      } else {
        current.add(GVersionNumberSetParser.atomSetES((SegmentAtom) s));
      }
    }

    return current;
  }

  public static @Nonnull SortedSet<GVersionFull> segmentsSetFull(
    final @Nonnull List<Segment> segments)
    throws UIError,
      ConstraintError
  {
    final TreeSet<GVersionFull> current = new TreeSet<GVersionFull>();

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

  private final @Nonnull GVersionNumberSetLexer lexer;
  private final @Nonnull StringBuilder          message;
  private @Nonnull GVersionNumberSetToken       token;

  public GVersionNumberSetParser(
    final @Nonnull GVersionNumberSetLexer lexer)
    throws ConstraintError,
      IOException,
      LexerError
  {
    this.lexer = Constraints.constrainNotNull(lexer, "Lexer");
    this.message = new StringBuilder();
    this.token = lexer.token();
  }

  @SuppressWarnings("boxing") public @Nonnull Bound boundLower()
    throws ParserError,
      ConstraintError,
      LexerError,
      IOException
  {
    final None<Integer> none = Option.none();

    this.parserExpectOneOf(new Type[] {
      Type.TOKEN_SQUARE_LEFT,
      Type.TOKEN_ROUND_LEFT });

    switch (this.token.getType()) {
      case TOKEN_ROUND_LEFT:
      {
        this.parserConsumeExact(Type.TOKEN_ROUND_LEFT);
        this.parserExpectOneOf(new Type[] {
          Type.TOKEN_LITERAL_INTEGER_DECIMAL,
          Type.TOKEN_COMMA });

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
          Type.TOKEN_COMMA });

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

  @SuppressWarnings("boxing") public @Nonnull Bound boundUpper()
    throws ParserError,
      ConstraintError,
      LexerError,
      IOException
  {
    final None<Integer> none = Option.none();

    this.parserExpectOneOf(new Type[] {
      Type.TOKEN_SQUARE_RIGHT,
      Type.TOKEN_LITERAL_INTEGER_DECIMAL,
      Type.TOKEN_ROUND_RIGHT });

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

  private @Nonnull Bound boundUpperClose(
    final Option<Integer> opt)
    throws ParserError,
      LexerError,
      IOException,
      ConstraintError
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
      LexerError,
      ConstraintError
  {
    this.token = this.lexer.token();
  }

  protected void parserConsumeExact(
    final @Nonnull GVersionNumberSetToken.Type type)
    throws ParserError,
      ConstraintError,
      IOException,
      LexerError
  {
    this.parserExpectExact(type);
    this.parserConsumeAny();
  }

  protected void parserExpectExact(
    final @Nonnull GVersionNumberSetToken.Type type)
    throws ParserError,
      ConstraintError
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
    final @Nonnull GVersionNumberSetToken.Type types[])
    throws ParserError,
      ConstraintError
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

  public @Nonnull SegmentAtom segmentAtom()
    throws ParserError,
      ConstraintError,
      LexerError,
      IOException
  {
    this.parserExpectExact(Type.TOKEN_LITERAL_INTEGER_DECIMAL);
    final TokenLiteralIntegerDecimal t =
      (TokenLiteralIntegerDecimal) this.token;
    this.parserConsumeExact(Type.TOKEN_LITERAL_INTEGER_DECIMAL);
    return new SegmentAtom(t.getValue().intValue());
  }

  public @Nonnull SegmentRange segmentRange()
    throws ParserError,
      ConstraintError,
      LexerError,
      IOException
  {
    this.parserExpectOneOf(new Type[] {
      Type.TOKEN_SQUARE_LEFT,
      Type.TOKEN_ROUND_LEFT });

    final Bound lower = this.boundLower();
    final Bound upper = this.boundUpper();

    if (this.token.getType() == Type.TOKEN_COMMA) {
      this.parserConsumeExact(Type.TOKEN_COMMA);
    }

    return new SegmentRange(lower, upper);
  }

  public boolean isAtEOF()
  {
    return this.token.getType() == Type.TOKEN_EOF;
  }

  public @Nonnull List<Segment> segments()
    throws ParserError,
      ConstraintError,
      LexerError,
      IOException
  {
    final ArrayList<Segment> s = new ArrayList<Segment>();

    for (;;) {
      this.parserExpectOneOf(new Type[] {
        Type.TOKEN_SQUARE_LEFT,
        Type.TOKEN_LITERAL_INTEGER_DECIMAL,
        Type.TOKEN_COMMA,
        Type.TOKEN_EOF,
        Type.TOKEN_ROUND_LEFT });

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
