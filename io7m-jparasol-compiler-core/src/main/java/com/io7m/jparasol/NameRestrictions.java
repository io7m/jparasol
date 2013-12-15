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

package com.io7m.jparasol;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jparasol.lexer.Token.TokenIdentifier;

public final class NameRestrictions
{
  static enum NameRestricted
  {
    NAME_OK,
    NAME_RESTRICTED_CONTAINS_DOUBLE_UNDERSCORE,
    NAME_RESTRICTED_ENDS_WITH_UNDERSCORE,
    NAME_RESTRICTED_KEYWORD,
    NAME_RESTRICTED_PREFIX_GL_LOWER,
    NAME_RESTRICTED_PREFIX_GL_UPPER
  }

  public static class NameRestrictionsException extends CompilerError
  {
    private static final long              serialVersionUID;

    static {
      serialVersionUID = 1510464423712276429L;
    }

    private final @Nonnull NameRestricted  code;
    private final @Nonnull TokenIdentifier token;

    public NameRestrictionsException(
      final @Nonnull NameRestricted code,
      final @Nonnull TokenIdentifier token,
      final @Nonnull String message)
      throws ConstraintError
    {
      super(
        message,
        Constraints.constrainNotNull(token, "Token").getFile(),
        token.getPosition());
      this.code = Constraints.constrainNotNull(code, "Code");
      this.token = token;
    }

    public @Nonnull NameRestricted getCode()
    {
      return this.code;
    }

    public @Nonnull TokenIdentifier getToken()
    {
      return this.token;
    }
  }

  public static final @Nonnull Set<String> KEYWORDS;

  static {
    KEYWORDS = NameRestrictions.makeKeywords();
  }

  public static @Nonnull NameRestricted checkRestricted(
    final @Nonnull String name)
  {
    if (name.startsWith("gl")) {
      return NameRestricted.NAME_RESTRICTED_PREFIX_GL_LOWER;
    }
    if (name.startsWith("GL")) {
      return NameRestricted.NAME_RESTRICTED_PREFIX_GL_UPPER;
    }
    if (name.contains("__")) {
      return NameRestricted.NAME_RESTRICTED_CONTAINS_DOUBLE_UNDERSCORE;
    }
    if (name.endsWith("_")) {
      return NameRestricted.NAME_RESTRICTED_ENDS_WITH_UNDERSCORE;
    }
    if (NameRestrictions.KEYWORDS.contains(name)) {
      return NameRestricted.NAME_RESTRICTED_KEYWORD;
    }
    return NameRestricted.NAME_OK;
  }

  public static void checkRestrictedExceptional(
    final @Nonnull TokenIdentifier token)
    throws NameRestrictionsException,
      ConstraintError
  {
    final String actual = token.getActual();
    final NameRestricted code = NameRestrictions.checkRestricted(actual);

    switch (code) {
      case NAME_OK:
      {
        return;
      }
      case NAME_RESTRICTED_CONTAINS_DOUBLE_UNDERSCORE:
      {
        final StringBuilder m = new StringBuilder();
        m.append("The name '");
        m.append(actual);
        m
          .append("' contains '__', which is reserved in GLSL and therefore cannot be used as a name here.");
        throw new NameRestrictionsException(code, token, m.toString());
      }
      case NAME_RESTRICTED_ENDS_WITH_UNDERSCORE:
      {
        final StringBuilder m = new StringBuilder();
        m.append("The name '");
        m.append(actual);
        m
          .append("' ends with '_', which is reserved in GLSL and therefore cannot be used as a name here.");
        throw new NameRestrictionsException(code, token, m.toString());
      }
      case NAME_RESTRICTED_KEYWORD:
      {
        final StringBuilder m = new StringBuilder();
        m.append("The name '");
        m.append(actual);
        m
          .append("' is a keyword in GLSL and therefore cannot be used as a name here.");
        throw new NameRestrictionsException(code, token, m.toString());
      }
      case NAME_RESTRICTED_PREFIX_GL_LOWER:
      {
        final StringBuilder m = new StringBuilder();
        m.append("The name '");
        m.append(actual);
        m
          .append("' begins with 'gl', which is reserved in GLSL and therefore cannot be used as a name here.");
        throw new NameRestrictionsException(code, token, m.toString());
      }
      case NAME_RESTRICTED_PREFIX_GL_UPPER:
      {
        final StringBuilder m = new StringBuilder();
        m.append("The name '");
        m.append(actual);
        m
          .append("' begins with 'GL', which is reserved in GLSL and therefore cannot be used as a name here.");
        throw new NameRestrictionsException(code, token, m.toString());
      }
    }
  }

  /**
   * The complete set of keywords as of GLSL 4.3.
   */

  private static @Nonnull Set<String> makeKeywords()
  {
    final HashSet<String> s = new HashSet<String>();

    s.add("active");
    s.add("asm");
    s.add("atomic_uint");
    s.add("attribute");
    s.add("bool");
    s.add("break");
    s.add("buffer");
    s.add("bvec2");
    s.add("bvec3");
    s.add("bvec4");
    s.add("case");
    s.add("cast");
    s.add("centroid");
    s.add("class");
    s.add("coherent");
    s.add("common");
    s.add("const");
    s.add("continue");
    s.add("default");
    s.add("discard");
    s.add("dmat2");
    s.add("dmat2x2");
    s.add("dmat2x3");
    s.add("dmat2x4");
    s.add("dmat3");
    s.add("dmat3x2");
    s.add("dmat3x3");
    s.add("dmat3x4");
    s.add("dmat4");
    s.add("dmat4x2");
    s.add("dmat4x3");
    s.add("dmat4x4");
    s.add("do");
    s.add("double");
    s.add("dvec2");
    s.add("dvec3");
    s.add("dvec4");
    s.add("else");
    s.add("enum");
    s.add("extern");
    s.add("external");
    s.add("false");
    s.add("filter");
    s.add("fixed");
    s.add("flat");
    s.add("float");
    s.add("for");
    s.add("fvec2");
    s.add("fvec3");
    s.add("fvec4");
    s.add("goto");
    s.add("half");
    s.add("highp");
    s.add("hvec2");
    s.add("hvec3");
    s.add("hvec4");
    s.add("if");
    s.add("iimage1D");
    s.add("iimage1DArray");
    s.add("iimage2D");
    s.add("iimage2DArray");
    s.add("iimage2DMS");
    s.add("iimage2DMSArray");
    s.add("iimage2DRect");
    s.add("iimage3D");
    s.add("iimageBuffer");
    s.add("iimageCube");
    s.add("iimageCubeArray");
    s.add("image1D");
    s.add("image1DArray");
    s.add("image2D");
    s.add("image2DArray");
    s.add("image2DMS");
    s.add("image2DMSArray");
    s.add("image2DRect");
    s.add("image3D");
    s.add("imageBuffer");
    s.add("imageCube");
    s.add("imageCubeArray");
    s.add("in");
    s.add("inline");
    s.add("inout");
    s.add("input");
    s.add("int");
    s.add("interface");
    s.add("invariant");
    s.add("isampler1D");
    s.add("isampler1DArray");
    s.add("isampler2D");
    s.add("isampler2DArray");
    s.add("isampler2DMS");
    s.add("isampler2DMSArray");
    s.add("isampler2DRect");
    s.add("isampler3D");
    s.add("isamplerBuffer");
    s.add("isamplerCube");
    s.add("isamplerCubeArray");
    s.add("ivec2");
    s.add("ivec3");
    s.add("ivec4");
    s.add("layout");
    s.add("long");
    s.add("lowp");
    s.add("mat2");
    s.add("mat2x2");
    s.add("mat2x3");
    s.add("mat2x4");
    s.add("mat3");
    s.add("mat3x2");
    s.add("mat3x3");
    s.add("mat3x4");
    s.add("mat4");
    s.add("mat4x2");
    s.add("mat4x3");
    s.add("mat4x4");
    s.add("mediump");
    s.add("namespace");
    s.add("noinline");
    s.add("noperspective");
    s.add("out");
    s.add("output");
    s.add("packed");
    s.add("partition");
    s.add("patch");
    s.add("precision");
    s.add("public");
    s.add("readonly");
    s.add("resource");
    s.add("restrict");
    s.add("return");
    s.add("row_major");
    s.add("sample");
    s.add("sampler1D");
    s.add("sampler1DArray");
    s.add("sampler1DArrayShadow");
    s.add("sampler1DShadow");
    s.add("sampler2D");
    s.add("sampler2DArray");
    s.add("sampler2DArrayShadow");
    s.add("sampler2DMS");
    s.add("sampler2DMSArray");
    s.add("sampler2DRect");
    s.add("sampler2DRectShadow");
    s.add("sampler2DShadow");
    s.add("sampler3D");
    s.add("sampler3DRect");
    s.add("samplerBuffer");
    s.add("samplerCube");
    s.add("samplerCubeArray");
    s.add("samplerCubeArrayShadow");
    s.add("samplerCubeShadow");
    s.add("shared");
    s.add("short");
    s.add("sizeof");
    s.add("smooth");
    s.add("static");
    s.add("struct");
    s.add("subroutine");
    s.add("superp");
    s.add("switch");
    s.add("template");
    s.add("this");
    s.add("true");
    s.add("typedef");
    s.add("uimage1D");
    s.add("uimage1DArray");
    s.add("uimage2D");
    s.add("uimage2DArray");
    s.add("uimage2DMS");
    s.add("uimage2DMSArray");
    s.add("uimage2DRect");
    s.add("uimage3D");
    s.add("uimageBuffer");
    s.add("uimageCube");
    s.add("uimageCubeArray");
    s.add("uint");
    s.add("uniform");
    s.add("union");
    s.add("unsigned");
    s.add("usampler1D");
    s.add("usampler1DArray");
    s.add("usampler2D");
    s.add("usampler2DArray");
    s.add("usampler2DMS");
    s.add("usampler2DMSArray");
    s.add("usampler2DRect");
    s.add("usampler3D");
    s.add("usamplerBuffer");
    s.add("usamplerCube");
    s.add("usamplerCubeArray");
    s.add("using");
    s.add("uvec2");
    s.add("uvec3");
    s.add("uvec4");
    s.add("varying");
    s.add("vec2");
    s.add("vec3");
    s.add("vec4");
    s.add("void");
    s.add("volatile");
    s.add("while");
    s.add("writeonly");

    return Collections.unmodifiableSet(s);
  }
}
