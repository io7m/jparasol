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

package com.io7m.jparasol.tests.untyped;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.lexer.Lexer;
import com.io7m.jparasol.parser.Parser;
import com.io7m.jparasol.tests.parser.ParserTest;
import com.io7m.jparasol.untyped.UnitCombinerError;
import com.io7m.jparasol.untyped.ast.initial.UASTICompilation;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDModule;
import com.io7m.jparasol.untyped.ast.initial.UASTIUnit;
import com.io7m.junreachable.UnreachableCodeException;

@SuppressWarnings({ "static-method", "resource" }) public class UASTICompilationTest
{
  static UASTIUnit parseResource(
    final String name)
  {
    try {
      final InputStream is =
        ParserTest.class.getResourceAsStream("/com/io7m/jparasol/tests/"
          + name);
      final Lexer lexer = new Lexer(is);
      final Parser p = Parser.newParser(lexer);
      return p.unit();
    } catch (final Throwable x) {
      throw new UnreachableCodeException(x);
    }
  }

  @Test(expected = UnitCombinerError.class) public void testBadPackage()
    throws UnitCombinerError
  {
    final ArrayList<UASTIUnit> units = new ArrayList<UASTIUnit>();
    units.add(UASTICompilationTest
      .parseResource("untyped/restricted_name/restricted-name-000.p"));
    UASTICompilation.fromUnits(units);
  }

  @Test public void testCombine0()
    throws UnitCombinerError
  {
    final ArrayList<UASTIUnit> units = new ArrayList<UASTIUnit>();
    units.add(UASTICompilationTest.parseResource("parser/testDUnit0.p"));
    units.add(UASTICompilationTest.parseResource("parser/testDUnit1.p"));
    final UASTICompilation c = UASTICompilation.fromUnits(units);
    final Map<ModulePathFlat, UASTIDModule> m = c.getModules();

    Assert
      .assertTrue(m.containsKey(new ModulePathFlat("com.io7m.example.M")));
    Assert
      .assertTrue(m.containsKey(new ModulePathFlat("com.io7m.example.N")));
    Assert
      .assertTrue(m.containsKey(new ModulePathFlat("com.io7m.example.P")));
    Assert
      .assertTrue(m.containsKey(new ModulePathFlat("com.io7m.example.R")));
  }

  @Test(expected = UnitCombinerError.class) public void testConflict0()
    throws UnitCombinerError
  {
    final ArrayList<UASTIUnit> units = new ArrayList<UASTIUnit>();
    units.add(UASTICompilationTest.parseResource("parser/testDUnit0.p"));
    units.add(UASTICompilationTest.parseResource("parser/testDUnit0.p"));
    UASTICompilation.fromUnits(units);
  }
}
