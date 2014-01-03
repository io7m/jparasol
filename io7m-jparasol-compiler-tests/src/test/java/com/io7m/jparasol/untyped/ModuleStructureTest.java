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

package com.io7m.jparasol.untyped;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.TestUtilities;
import com.io7m.jparasol.lexer.Lexer;
import com.io7m.jparasol.parser.Parser;
import com.io7m.jparasol.parser.ParserTest;
import com.io7m.jparasol.untyped.ModuleStructureError.Code;
import com.io7m.jparasol.untyped.ast.checked.UASTCCompilation;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDModule;
import com.io7m.jparasol.untyped.ast.initial.UASTICompilation;
import com.io7m.jparasol.untyped.ast.initial.UASTIUnit;

public final class ModuleStructureTest
{
  static UASTCCompilation check(
    final String names[])
    throws ConstraintError,
      ModuleStructureError
  {
    final ModuleStructure c =
      ModuleStructure.newModuleStructureChecker(
        ModuleStructureTest.parseAndCombine(names),
        TestUtilities.getLog());
    return c.check();
  }

  static boolean checkExists(
    final @Nonnull String name)
  {
    final URL r =
      ParserTest.class.getResource("/com/io7m/jparasol/untyped/" + name);
    return r != null;
  }

  static UASTCCompilation checkInternal(
    final String names[])
    throws ConstraintError,
      ModuleStructureError
  {
    final ModuleStructure c =
      ModuleStructure.newModuleStructureChecker(
        ModuleStructureTest.parseAndCombineInternal(names),
        TestUtilities.getLog());
    return c.check();
  }

  static void checkMustFailWithCode(
    final String[] names,
    final ModuleStructureError.Code code)
    throws ConstraintError,
      ModuleStructureError
  {
    boolean caught = false;

    try {
      ModuleStructureTest.check(names);
    } catch (final ModuleStructureError e) {
      caught = true;
      Assert.assertEquals(code, e.getCode());
      throw e;
    } catch (final ConstraintError e) {
      e.printStackTrace();
      throw e;
    } finally {
      Assert.assertTrue("Caught exception", caught);
    }
  }

  static void checkMustFailWithCodeInternal(
    final String[] names,
    final ModuleStructureError.Code code)
    throws ConstraintError,
      ModuleStructureError
  {
    boolean caught = false;

    try {
      ModuleStructureTest.checkInternal(names);
    } catch (final ModuleStructureError e) {
      caught = true;
      Assert.assertEquals(code, e.getCode());
      throw e;
    } catch (final ConstraintError e) {
      e.printStackTrace();
      throw e;
    } finally {
      Assert.assertTrue("Caught exception", caught);
    }
  }

  static UASTICompilation parseAndCombine(
    final String names[])
  {
    try {
      final List<UASTIUnit> units = ModuleStructureTest.parseUnits(names);
      return UASTICompilation.fromUnits(units);
    } catch (final Throwable x) {
      throw new UnreachableCodeException(x);
    }
  }

  static UASTICompilation parseAndCombineInternal(
    final String names[])
  {
    try {
      final List<UASTIUnit> units =
        ModuleStructureTest.parseUnitsInternal(names);
      return UASTICompilation.fromUnits(units);
    } catch (final Throwable x) {
      throw new UnreachableCodeException(x);
    }
  }

  @SuppressWarnings("resource") private static UASTIUnit parseUnit(
    final String name,
    final boolean internal)
  {
    try {
      final InputStream is =
        ParserTest.class.getResourceAsStream("/com/io7m/jparasol/untyped/"
          + name);
      final Lexer lexer = new Lexer(is);
      if (internal) {
        final Parser p = Parser.newInternalParser(lexer);
        return p.unit();
      }
      final Parser p = Parser.newParser(lexer);
      return p.unit();
    } catch (final Throwable x) {
      x.printStackTrace();
      System.err.println("UNREACHABLE: " + x);
      throw new UnreachableCodeException(x);
    }
  }

  private static List<UASTIUnit> parseUnits(
    final String[] names)
  {
    final List<UASTIUnit> units = new ArrayList<UASTIUnit>();

    for (final String name : names) {
      units.add(ModuleStructureTest.parseUnit(name, false));
    }

    return units;
  }

  private static List<UASTIUnit> parseUnitsInternal(
    final String[] names)
  {
    final List<UASTIUnit> units = new ArrayList<UASTIUnit>();

    for (final String name : names) {
      units.add(ModuleStructureTest.parseUnit(name, true));
    }

    return units;
  }

  @SuppressWarnings("static-method") @Test public void testAll()
    throws ModuleStructureError,
      ConstraintError
  {
    final UASTCCompilation uc =
      ModuleStructureTest.checkInternal(new String[] { "all.p" });

    final ModulePathFlat first = uc.getModules().keySet().iterator().next();
    Assert.assertEquals("x.y.M", first.getActual());

    final UASTCDModule module = uc.getModules().get(first);
    Assert.assertTrue(module.getTerms().containsKey("x"));
    Assert.assertTrue(module.getTerms().containsKey("y"));
    Assert.assertTrue(module.getTerms().containsKey("a"));
    Assert.assertTrue(module.getTerms().containsKey("z"));
    Assert.assertTrue(module.getTerms().containsKey("w"));
    Assert.assertTrue(module.getTerms().containsKey("f"));
    Assert.assertTrue(module.getTerms().containsKey("g"));

    Assert.assertTrue(module.getTypes().containsKey("t"));

    Assert.assertTrue(module.getShaders().containsKey("v"));
    Assert.assertTrue(module.getShaders().containsKey("f"));
    Assert.assertTrue(module.getShaders().containsKey("p"));
  }

  @SuppressWarnings("static-method") @Test(
    expected = ModuleStructureError.class) public
    void
    testModuleDuplicateFunction()
      throws ConstraintError,
        ModuleStructureError
  {
    ModuleStructureTest.checkMustFailWithCode(
      new String[] { "module-duplicate-function.p" },
      Code.MODULE_STRUCTURE_TERM_CONFLICT);
  }

  @SuppressWarnings("static-method") @Test(
    expected = ModuleStructureError.class) public
    void
    testModuleDuplicateFunctionExternal()
      throws ConstraintError,
        ModuleStructureError
  {
    ModuleStructureTest.checkMustFailWithCodeInternal(
      new String[] { "module-duplicate-function-external.p" },
      Code.MODULE_STRUCTURE_TERM_CONFLICT);
  }

  @SuppressWarnings("static-method") @Test(
    expected = ModuleStructureError.class) public
    void
    testModuleDuplicateType()
      throws ConstraintError,
        ModuleStructureError
  {
    ModuleStructureTest.checkMustFailWithCode(
      new String[] { "module-duplicate-type.p" },
      Code.MODULE_STRUCTURE_TYPE_CONFLICT);
  }

  @SuppressWarnings("static-method") @Test(
    expected = ModuleStructureError.class) public
    void
    testModuleDuplicateValue()
      throws ConstraintError,
        ModuleStructureError
  {
    ModuleStructureTest.checkMustFailWithCode(
      new String[] { "module-duplicate-value.p" },
      Code.MODULE_STRUCTURE_TERM_CONFLICT);
  }

  @SuppressWarnings("static-method") @Test(
    expected = ModuleStructureError.class) public
    void
    testModuleDuplicateValueLetLet()
      throws ConstraintError,
        ModuleStructureError
  {
    ModuleStructureTest.checkMustFailWithCode(
      new String[] { "module-duplicate-value-let-let.p" },
      Code.MODULE_STRUCTURE_TERM_CONFLICT);
  }

  @SuppressWarnings("static-method") @Test(
    expected = ModuleStructureError.class) public
    void
    testModuleFunctionArgumentsDuplicate()
      throws ConstraintError,
        ModuleStructureError
  {
    ModuleStructureTest.checkMustFailWithCode(
      new String[] { "module-function-arguments-duplicate.p" },
      Code.MODULE_STRUCTURE_FUNCTION_ARGUMENT_DUPLICATE);
  }

  @SuppressWarnings("static-method") @Test(
    expected = ModuleStructureError.class) public
    void
    testModuleFunctionExternalArgumentsDuplicate()
      throws ConstraintError,
        ModuleStructureError
  {
    ModuleStructureTest.checkMustFailWithCodeInternal(
      new String[] { "module-function-external-arguments-duplicate.p" },
      Code.MODULE_STRUCTURE_FUNCTION_ARGUMENT_DUPLICATE);
  }

  @SuppressWarnings("static-method") @Test(
    expected = ModuleStructureError.class) public
    void
    testModuleImportDuplicate()
      throws ConstraintError,
        ModuleStructureError
  {
    ModuleStructureTest.checkMustFailWithCode(
      new String[] { "module-import-duplicate.p" },
      Code.MODULE_STRUCTURE_IMPORT_DUPLICATE);
  }

  @SuppressWarnings("static-method") @Test(
    expected = ModuleStructureError.class) public
    void
    testModuleImportImportConflict()
      throws ConstraintError,
        ModuleStructureError
  {
    ModuleStructureTest.checkMustFailWithCode(
      new String[] { "module-import-import-conflict.p" },
      Code.MODULE_STRUCTURE_IMPORT_IMPORT_CONFLICT);
  }

  @SuppressWarnings("static-method") @Test(
    expected = ModuleStructureError.class) public
    void
    testModuleImportRenameConflict()
      throws ConstraintError,
        ModuleStructureError
  {
    ModuleStructureTest.checkMustFailWithCode(
      new String[] { "module-import-rename-conflict.p" },
      Code.MODULE_STRUCTURE_IMPORT_RENAME_CONFLICT);
  }

  @SuppressWarnings("static-method") @Test(
    expected = ModuleStructureError.class) public
    void
    testModuleImportsSelf()
      throws ConstraintError,
        ModuleStructureError
  {
    ModuleStructureTest.checkMustFailWithCode(
      new String[] { "module-import-self.p" },
      Code.MODULE_STRUCTURE_IMPORTS_SELF);
  }

  @SuppressWarnings("static-method") @Test(
    expected = ModuleStructureError.class) public
    void
    testModuleNameRestricted()
      throws ConstraintError,
        ModuleStructureError
  {
    ModuleStructureTest.checkMustFailWithCode(
      new String[] { "module-name-restricted.p" },
      Code.MODULE_STRUCTURE_RESTRICTED_NAME);
  }

  @SuppressWarnings("static-method") @Test(
    expected = ModuleStructureError.class) public
    void
    testModuleRecordExpressionFieldDuplicate()
      throws ConstraintError,
        ModuleStructureError
  {
    ModuleStructureTest.checkMustFailWithCode(
      new String[] { "module-record-expression-fields-duplicate.p" },
      Code.MODULE_STRUCTURE_RECORD_FIELD_DUPLICATE);
  }

  @SuppressWarnings("static-method") @Test(
    expected = ModuleStructureError.class) public
    void
    testModuleRecordFieldDuplicate()
      throws ConstraintError,
        ModuleStructureError
  {
    ModuleStructureTest.checkMustFailWithCode(
      new String[] { "module-type-duplicate-fields.p" },
      Code.MODULE_STRUCTURE_RECORD_FIELD_DUPLICATE);
  }

  @SuppressWarnings("static-method") @Test(
    expected = ModuleStructureError.class) public
    void
    testModuleRedundantRename()
      throws ConstraintError,
        ModuleStructureError
  {
    ModuleStructureTest.checkMustFailWithCode(
      new String[] { "module-import-rename-redundant.p" },
      Code.MODULE_STRUCTURE_IMPORT_REDUNDANT);
  }

  @SuppressWarnings("static-method") @Test(
    expected = ModuleStructureError.class) public
    void
    testModuleRenameImportConflict()
      throws ConstraintError,
        ModuleStructureError
  {
    ModuleStructureTest.checkMustFailWithCode(
      new String[] { "module-rename-import-conflict.p" },
      Code.MODULE_STRUCTURE_RENAME_IMPORT_CONFLICT);
  }

  @SuppressWarnings("static-method") @Test(
    expected = ModuleStructureError.class) public
    void
    testModuleRenameRenameConflict()
      throws ConstraintError,
        ModuleStructureError
  {
    ModuleStructureTest.checkMustFailWithCode(
      new String[] { "module-rename-rename-conflict.p" },
      Code.MODULE_STRUCTURE_RENAME_RENAME_CONFLICT);
  }

  @SuppressWarnings("static-method") @Test(
    expected = ModuleStructureError.class) public
    void
    testModuleRenameRestricted()
      throws ConstraintError,
        ModuleStructureError
  {
    ModuleStructureTest.checkMustFailWithCode(
      new String[] { "module-rename-restricted.p" },
      Code.MODULE_STRUCTURE_RESTRICTED_NAME);
  }

  @SuppressWarnings("static-method") @Test(
    expected = ModuleStructureError.class) public
    void
    testModuleShaderDuplicate()
      throws ConstraintError,
        ModuleStructureError
  {
    ModuleStructureTest.checkMustFailWithCode(
      new String[] { "module-shader-duplicate.p" },
      Code.MODULE_STRUCTURE_SHADER_CONFLICT);
  }

  @SuppressWarnings("static-method") @Test(
    expected = ModuleStructureError.class) public
    void
    testModuleShaderFragmentDuplicateOutIndex_0()
      throws ConstraintError,
        ModuleStructureError
  {
    ModuleStructureTest.checkMustFailWithCode(
      new String[] { "module-shader-f-duplicate-out-index-0.p" },
      Code.MODULE_STRUCTURE_SHADER_OUTPUT_INDEX_DUPLICATE);
  }

  @SuppressWarnings("static-method") @Test(
    expected = ModuleStructureError.class) public
    void
    testModuleShaderFragmentDuplicateParam_0()
      throws ConstraintError,
        ModuleStructureError
  {
    ModuleStructureTest.checkMustFailWithCode(
      new String[] { "module-shader-f-duplicate-param-0.p" },
      Code.MODULE_STRUCTURE_SHADER_PARAMETER_CONFLICT);
  }

  @SuppressWarnings("static-method") @Test(
    expected = ModuleStructureError.class) public
    void
    testModuleShaderFragmentDuplicateParam_1()
      throws ConstraintError,
        ModuleStructureError
  {
    ModuleStructureTest.checkMustFailWithCode(
      new String[] { "module-shader-f-duplicate-param-1.p" },
      Code.MODULE_STRUCTURE_SHADER_PARAMETER_CONFLICT);
  }

  @SuppressWarnings("static-method") @Test(
    expected = ModuleStructureError.class) public
    void
    testModuleShaderFragmentDuplicateParam_2()
      throws ConstraintError,
        ModuleStructureError
  {
    ModuleStructureTest.checkMustFailWithCode(
      new String[] { "module-shader-f-duplicate-param-2.p" },
      Code.MODULE_STRUCTURE_SHADER_PARAMETER_CONFLICT);
  }

  @SuppressWarnings("static-method") @Test(
    expected = ModuleStructureError.class) public
    void
    testModuleShaderFragmentDuplicateParam_3()
      throws ConstraintError,
        ModuleStructureError
  {
    ModuleStructureTest.checkMustFailWithCode(
      new String[] { "module-shader-f-duplicate-param-3.p" },
      Code.MODULE_STRUCTURE_SHADER_PARAMETER_CONFLICT);
  }

  @SuppressWarnings("static-method") @Test(
    expected = ModuleStructureError.class) public
    void
    testModuleShaderFragmentDuplicateValues_0()
      throws ConstraintError,
        ModuleStructureError
  {
    ModuleStructureTest.checkMustFailWithCode(
      new String[] { "module-shader-f-duplicate-values-0.p" },
      Code.MODULE_STRUCTURE_TERM_CONFLICT);
  }

  @SuppressWarnings("static-method") @Test(
    expected = ModuleStructureError.class) public
    void
    testModuleShaderFragmentDuplicateWrite_0()
      throws ConstraintError,
        ModuleStructureError
  {
    ModuleStructureTest.checkMustFailWithCode(
      new String[] { "module-shader-f-duplicate-write-0.p" },
      Code.MODULE_STRUCTURE_SHADER_OUTPUT_ASSIGNMENT_CONFLICT);
  }

  @SuppressWarnings("static-method") @Test(
    expected = ModuleStructureError.class) public
    void
    testModuleShaderFragmentInvalidOutIndex_0()
      throws ConstraintError,
        ModuleStructureError
  {
    ModuleStructureTest.checkMustFailWithCode(
      new String[] { "module-shader-f-negative-out-index-0.p" },
      Code.MODULE_STRUCTURE_SHADER_OUTPUT_INDEX_INVALID);
  }

  @SuppressWarnings("static-method") @Test(
    expected = ModuleStructureError.class) public
    void
    testModuleShaderFragmentMissingOutIndex_0()
      throws ConstraintError,
        ModuleStructureError
  {
    ModuleStructureTest.checkMustFailWithCode(
      new String[] { "module-shader-f-missing-out-index-0.p" },
      Code.MODULE_STRUCTURE_SHADER_OUTPUT_INDEX_MISSING);
  }

  @SuppressWarnings("static-method") @Test(
    expected = ModuleStructureError.class) public
    void
    testModuleShaderFragmentMissingOutIndex_1()
      throws ConstraintError,
        ModuleStructureError
  {
    ModuleStructureTest.checkMustFailWithCode(
      new String[] { "module-shader-f-missing-out-index-1.p" },
      Code.MODULE_STRUCTURE_SHADER_OUTPUT_INDEX_MISSING);
  }

  @SuppressWarnings("static-method") @Test(
    expected = ModuleStructureError.class) public
    void
    testModuleShaderFragmentMissingWrite_0()
      throws ConstraintError,
        ModuleStructureError
  {
    ModuleStructureTest.checkMustFailWithCode(
      new String[] { "module-shader-f-missing-write-0.p" },
      Code.MODULE_STRUCTURE_SHADER_OUTPUT_ASSIGNMENT_MISSING);
  }

  @SuppressWarnings("static-method") @Test(
    expected = ModuleStructureError.class) public
    void
    testModuleShaderVertexDuplicateParam_0()
      throws ConstraintError,
        ModuleStructureError
  {
    ModuleStructureTest.checkMustFailWithCode(
      new String[] { "module-shader-v-duplicate-param-0.p" },
      Code.MODULE_STRUCTURE_SHADER_PARAMETER_CONFLICT);
  }

  @SuppressWarnings("static-method") @Test(
    expected = ModuleStructureError.class) public
    void
    testModuleShaderVertexDuplicateParam_1()
      throws ConstraintError,
        ModuleStructureError
  {
    ModuleStructureTest.checkMustFailWithCode(
      new String[] { "module-shader-v-duplicate-param-1.p" },
      Code.MODULE_STRUCTURE_SHADER_PARAMETER_CONFLICT);
  }

  @SuppressWarnings("static-method") @Test(
    expected = ModuleStructureError.class) public
    void
    testModuleShaderVertexDuplicateParam_2()
      throws ConstraintError,
        ModuleStructureError
  {
    ModuleStructureTest.checkMustFailWithCode(
      new String[] { "module-shader-v-duplicate-param-2.p" },
      Code.MODULE_STRUCTURE_SHADER_PARAMETER_CONFLICT);
  }

  @SuppressWarnings("static-method") @Test(
    expected = ModuleStructureError.class) public
    void
    testModuleShaderVertexDuplicateParam_3()
      throws ConstraintError,
        ModuleStructureError
  {
    ModuleStructureTest.checkMustFailWithCode(
      new String[] { "module-shader-v-duplicate-param-3.p" },
      Code.MODULE_STRUCTURE_SHADER_PARAMETER_CONFLICT);
  }

  @SuppressWarnings("static-method") @Test(
    expected = ModuleStructureError.class) public
    void
    testModuleShaderVertexDuplicateValues_0()
      throws ConstraintError,
        ModuleStructureError
  {
    ModuleStructureTest.checkMustFailWithCode(
      new String[] { "module-shader-v-duplicate-values-0.p" },
      Code.MODULE_STRUCTURE_TERM_CONFLICT);
  }

  @SuppressWarnings("static-method") @Test(
    expected = ModuleStructureError.class) public
    void
    testModuleShaderVertexDuplicateWrite_0()
      throws ConstraintError,
        ModuleStructureError
  {
    ModuleStructureTest.checkMustFailWithCode(
      new String[] { "module-shader-v-duplicate-write-0.p" },
      Code.MODULE_STRUCTURE_SHADER_OUTPUT_ASSIGNMENT_CONFLICT);
  }

  @SuppressWarnings("static-method") @Test(
    expected = ModuleStructureError.class) public
    void
    testModuleShaderVertexMissingMain_0()
      throws ConstraintError,
        ModuleStructureError
  {
    ModuleStructureTest.checkMustFailWithCode(
      new String[] { "module-shader-v-missing-main-0.p" },
      Code.MODULE_STRUCTURE_SHADER_OUTPUT_MISSING_MAIN);
  }

  @SuppressWarnings("static-method") @Test(
    expected = ModuleStructureError.class) public
    void
    testModuleShaderVertexMissingWrite_0()
      throws ConstraintError,
        ModuleStructureError
  {
    ModuleStructureTest.checkMustFailWithCode(
      new String[] { "module-shader-v-missing-write-0.p" },
      Code.MODULE_STRUCTURE_SHADER_OUTPUT_ASSIGNMENT_MISSING);
  }

  @SuppressWarnings("static-method") @Test(
    expected = ModuleStructureError.class) public
    void
    testModuleShaderVertexMultipleMain_0()
      throws ConstraintError,
        ModuleStructureError
  {
    ModuleStructureTest.checkMustFailWithCode(
      new String[] { "module-shader-v-multiple-main-0.p" },
      Code.MODULE_STRUCTURE_SHADER_OUTPUT_MULTIPLE_MAIN);
  }

  @SuppressWarnings({ "boxing", "static-method" }) @Test public
    void
    testRestrictedNames()
      throws ConstraintError
  {
    final int max = 302;
    int caught = 0;
    int checked = 0;

    // Skip 000 as this is now handled by the unit combiner
    for (int index = 1; index <= max; ++index) {
      final String name =
        String.format("restricted_name/restricted-name-%03d.p", index);
      try {
        System.err.println("Checking " + name);

        if (ModuleStructureTest.checkExists(name)) {
          ++checked;
          ModuleStructureTest.checkInternal(new String[] { name });
          System.out.println("*** Failed to catch error for " + index);
        }
      } catch (final ModuleStructureError x) {
        Assert.assertEquals(
          Code.MODULE_STRUCTURE_RESTRICTED_NAME,
          x.getCode());
        ++caught;
      }
    }

    System.err.println("Checked " + checked + ", caught " + caught);
    Assert.assertEquals(caught, checked);
  }
}
