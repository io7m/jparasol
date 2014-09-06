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

package com.io7m.jparasol.untyped;

import java.io.File;
import java.util.Map;

import com.io7m.jfunctional.Some;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.CompilerError;
import com.io7m.jparasol.ModulePath;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.NameRestrictions.NameRestrictionsException;
import com.io7m.jparasol.lexer.Position;
import com.io7m.jparasol.lexer.TokenIdentifierLower;
import com.io7m.jparasol.lexer.TokenIdentifierUpper;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDImport;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDShader;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDTerm;
import com.io7m.jparasol.untyped.ast.checked.UASTCDeclaration.UASTCDType;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDFunctionArgument;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragment;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentOutputData;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentOutputDepth;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderParameters;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderVertexOutput;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDTypeRecordField;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDValueExternal;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDValueLocal;
import com.io7m.jparasol.untyped.ast.initial.UASTIRecordFieldAssignment;

/**
 * Errors raised during the module structure checking phase.
 * 
 * @see ModuleStructure
 */

public final class ModuleStructureError extends CompilerError
{
  /**
   * Error codes for module structure checking.
   */

  public static enum Code
  {
    /**
     * Duplicate function argument.
     */

    MODULE_STRUCTURE_FUNCTION_ARGUMENT_DUPLICATE,

    /**
     * Duplicate module import.
     */

    MODULE_STRUCTURE_IMPORT_DUPLICATE,

    /**
     * Duplicate module import.
     */

    MODULE_STRUCTURE_IMPORT_IMPORT_CONFLICT,

    /**
     * Redunant module import.
     */

    MODULE_STRUCTURE_IMPORT_REDUNDANT,

    /**
     * Duplicate import/rename conflict.
     */

    MODULE_STRUCTURE_IMPORT_RENAME_CONFLICT,

    /**
     * Module imports itself.
     */

    MODULE_STRUCTURE_IMPORTS_SELF,

    /**
     * Duplicate record field.
     */

    MODULE_STRUCTURE_RECORD_FIELD_DUPLICATE,

    /**
     * Module rename/import conflict.
     */

    MODULE_STRUCTURE_RENAME_IMPORT_CONFLICT,

    /**
     * Module rename/rename conflict.
     */

    MODULE_STRUCTURE_RENAME_RENAME_CONFLICT,

    /**
     * Module contains a restricted name.
     */

    MODULE_STRUCTURE_RESTRICTED_NAME,

    /**
     * Duplicate shader.
     */

    MODULE_STRUCTURE_SHADER_CONFLICT,

    /**
     * Duplicate shader output assignment.
     */

    MODULE_STRUCTURE_SHADER_OUTPUT_ASSIGNMENT_CONFLICT,

    /**
     * Missing shader output assignment.
     */

    MODULE_STRUCTURE_SHADER_OUTPUT_ASSIGNMENT_MISSING,

    /**
     * Duplicate shader depth output.
     */

    MODULE_STRUCTURE_SHADER_OUTPUT_DEPTH_DUPLICATE,

    /**
     * Duplicate shader output index.
     */

    MODULE_STRUCTURE_SHADER_OUTPUT_INDEX_DUPLICATE,

    /**
     * Invalid shader output index.
     */

    MODULE_STRUCTURE_SHADER_OUTPUT_INDEX_INVALID,

    /**
     * Missing shader output index.
     */

    MODULE_STRUCTURE_SHADER_OUTPUT_INDEX_MISSING,

    /**
     * Missing main shader output.
     */

    MODULE_STRUCTURE_SHADER_OUTPUT_MISSING_MAIN,

    /**
     * Multiple main shader outputs.
     */

    MODULE_STRUCTURE_SHADER_OUTPUT_MULTIPLE_MAIN,

    /**
     * Duplicate shader parameters.
     */

    MODULE_STRUCTURE_SHADER_PARAMETER_CONFLICT,

    /**
     * Duplicate term.
     */

    MODULE_STRUCTURE_TERM_CONFLICT,

    /**
     * Duplicate type.
     */

    MODULE_STRUCTURE_TYPE_CONFLICT,

    /**
     * Expression provided for external.
     */

    MODULE_STRUCTURE_VALUE_EXTERNAL_HAS_EXPRESSION,

    /**
     * Type ascription missing for external.
     */

    MODULE_STRUCTURE_VALUE_EXTERNAL_LACKS_ASCRIPTION,
  }

  private static final long serialVersionUID = 8998482912668461862L;

  /**
   * @return A module structure error
   */

  public static ModuleStructureError moduleFunctionArgumentDuplicate(
    final UASTIDFunctionArgument current,
    final UASTIDFunctionArgument original)
  {
    NullCheck.notNull(current, "Current");
    NullCheck.notNull(original, "Original");

    final TokenIdentifierLower orig_name = original.getName();
    final StringBuilder m = new StringBuilder();
    m.append("Duplicate function argument ");
    m.append(current.getName().getActual());
    m.append(" conflicts with the definition at ");
    m.append(orig_name.getFile());
    m.append(":");
    m.append(orig_name.getPosition());
    return new ModuleStructureError(
      Code.MODULE_STRUCTURE_FUNCTION_ARGUMENT_DUPLICATE,
      m.toString(),
      orig_name.getFile(),
      orig_name.getPosition());
  }

  /**
   * @return A module structure error
   */

  public static ModuleStructureError moduleImportDuplicate(
    final UASTCDImport current,
    final UASTCDImport original)
  {
    NullCheck.notNull(current, "Current");
    NullCheck.notNull(original, "Original");

    final ModulePath curr_path = current.getPath();
    final ModulePath orig_path = original.getPath();
    final ModulePathFlat curr_flat = ModulePathFlat.fromModulePath(curr_path);
    final File orig_file = orig_path.getName().getFile();
    final Position orig_pos = orig_path.getName().getPosition();

    final StringBuilder m = new StringBuilder();
    m.append("Module ");
    m.append(curr_flat.getActual());
    m.append(" has already been imported by the declaration at ");
    m.append(orig_file);
    m.append(":");
    m.append(orig_pos);

    return new ModuleStructureError(
      Code.MODULE_STRUCTURE_IMPORT_DUPLICATE,
      m.toString(),
      orig_file,
      orig_pos);

  }

  /**
   * @return A module structure error
   */

  public static ModuleStructureError moduleImportImportConflict(
    final UASTCDImport current,
    final UASTCDImport original)
  {
    NullCheck.notNull(current, "Current");
    NullCheck.notNull(original, "Original");

    final ModulePath curr_path = current.getPath();
    final ModulePath orig_path = original.getPath();
    final ModulePathFlat curr_flat = ModulePathFlat.fromModulePath(curr_path);
    final File orig_file = orig_path.getName().getFile();
    final Position orig_pos = orig_path.getName().getPosition();
    final TokenIdentifierUpper curr_token = curr_path.getName();

    final StringBuilder m = new StringBuilder();
    m.append("Import of module ");
    m.append(curr_flat.getActual());
    m.append(" conflicts with the import at ");
    m.append(orig_file);
    m.append(":");
    m.append(orig_pos);

    return new ModuleStructureError(
      Code.MODULE_STRUCTURE_IMPORT_IMPORT_CONFLICT,
      m.toString(),
      curr_token.getFile(),
      curr_token.getPosition());
  }

  /**
   * @return A module structure error
   */

  public static ModuleStructureError moduleImportRedundantRename(
    final UASTCDImport i)
  {
    NullCheck.notNull(i, "Import");

    final TokenIdentifierUpper token = i.getPath().getName();
    final StringBuilder m = new StringBuilder();
    m.append("Renaming the imported module ");
    m.append(ModulePathFlat.fromModulePath(i.getPath()).show());
    m.append(" to the same name is redundant");
    return new ModuleStructureError(
      Code.MODULE_STRUCTURE_IMPORT_REDUNDANT,
      m.toString(),
      token.getFile(),
      token.getPosition());
  }

  /**
   * @return A module structure error
   */

  public static ModuleStructureError moduleImportRenameConflict(
    final UASTCDImport current,
    final UASTCDImport original)
  {
    NullCheck.notNull(current, "Current");
    NullCheck.notNull(original, "Original");

    final ModulePath curr_path = current.getPath();
    final ModulePath orig_path = original.getPath();
    final ModulePathFlat curr_flat = ModulePathFlat.fromModulePath(curr_path);
    final File orig_file = orig_path.getName().getFile();
    final Position orig_pos = orig_path.getName().getPosition();
    final TokenIdentifierUpper curr_token = curr_path.getName();

    final StringBuilder m = new StringBuilder();
    m.append("Import of module ");
    m.append(curr_flat.getActual());
    m.append(" conflicts with the rename at ");
    m.append(orig_file);
    m.append(":");
    m.append(orig_pos);

    return new ModuleStructureError(
      Code.MODULE_STRUCTURE_IMPORT_RENAME_CONFLICT,
      m.toString(),
      curr_token.getFile(),
      curr_token.getPosition());
  }

  /**
   * @return A module structure error
   */

  public static ModuleStructureError moduleImportsSelf(
    final UASTCDImport i)
  {
    NullCheck.notNull(i, "Import");

    final TokenIdentifierUpper token = i.getPath().getName();
    final StringBuilder m = new StringBuilder();
    m.append("Module ");
    m.append(ModulePathFlat.fromModulePath(i.getPath()).show());
    m.append(" imports itself");
    return new ModuleStructureError(
      Code.MODULE_STRUCTURE_IMPORTS_SELF,
      m.toString(),
      token.getFile(),
      token.getPosition());
  }

  /**
   * @return A module structure error
   */

  public static ModuleStructureError moduleRecordExpressionFieldDuplicate(
    final UASTIRecordFieldAssignment current,
    final UASTIRecordFieldAssignment original)
  {
    NullCheck.notNull(current, "Current");
    NullCheck.notNull(original, "Original");

    final TokenIdentifierLower orig_name = original.getName();
    final StringBuilder m = new StringBuilder();
    m.append("Duplicate record field ");
    m.append(current.getName().getActual());
    m.append(" conflicts with the expression at ");
    m.append(orig_name.getFile());
    m.append(":");
    m.append(orig_name.getPosition());
    return new ModuleStructureError(
      Code.MODULE_STRUCTURE_RECORD_FIELD_DUPLICATE,
      m.toString(),
      orig_name.getFile(),
      orig_name.getPosition());
  }

  /**
   * @return A module structure error
   */

  public static ModuleStructureError moduleRecordFieldDuplicate(
    final UASTIDTypeRecordField current,
    final UASTIDTypeRecordField original)
  {
    NullCheck.notNull(current, "Current");
    NullCheck.notNull(original, "Original");

    final TokenIdentifierLower orig_name = original.getName();
    final StringBuilder m = new StringBuilder();
    m.append("Duplicate record field ");
    m.append(current.getName().getActual());
    m.append(" conflicts with the definition at ");
    m.append(orig_name.getFile());
    m.append(":");
    m.append(orig_name.getPosition());
    return new ModuleStructureError(
      Code.MODULE_STRUCTURE_RECORD_FIELD_DUPLICATE,
      m.toString(),
      orig_name.getFile(),
      orig_name.getPosition());
  }

  /**
   * @return A module structure error
   */

  public static ModuleStructureError moduleRenameImportConflict(
    final UASTCDImport current,
    final UASTCDImport original)
  {
    NullCheck.notNull(current, "Current");
    NullCheck.notNull(original, "Original");

    final ModulePath curr_path = current.getPath();
    final ModulePath orig_path = original.getPath();
    final ModulePathFlat curr_flat = ModulePathFlat.fromModulePath(curr_path);
    final File orig_file = orig_path.getName().getFile();
    final Position orig_pos = orig_path.getName().getPosition();
    final TokenIdentifierUpper curr_token = curr_path.getName();

    final StringBuilder m = new StringBuilder();
    m.append("Rename of module ");
    m.append(curr_flat.getActual());
    m.append(" to ");

    final Some<TokenIdentifierUpper> some =
      (Some<TokenIdentifierUpper>) current.getRename();

    m.append(some.get().getActual());
    m.append(" conflicts with the import at ");
    m.append(orig_file);
    m.append(":");
    m.append(orig_pos);

    return new ModuleStructureError(
      Code.MODULE_STRUCTURE_RENAME_IMPORT_CONFLICT,
      m.toString(),
      curr_token.getFile(),
      curr_token.getPosition());
  }

  /**
   * @return A module structure error
   */

  public static ModuleStructureError moduleRenameRenameConflict(
    final UASTCDImport current,
    final UASTCDImport original)
  {
    NullCheck.notNull(current, "Current");
    NullCheck.notNull(original, "Original");

    final ModulePath curr_path = current.getPath();
    final ModulePath orig_path = original.getPath();
    final ModulePathFlat curr_flat = ModulePathFlat.fromModulePath(curr_path);
    final File orig_file = orig_path.getName().getFile();
    final Position orig_pos = orig_path.getName().getPosition();
    final TokenIdentifierUpper curr_token = curr_path.getName();

    final StringBuilder m = new StringBuilder();
    m.append("Rename of module ");
    m.append(curr_flat.getActual());
    m.append(" to ");
    final Some<TokenIdentifierUpper> some =
      (Some<TokenIdentifierUpper>) current.getRename();
    m.append(some.get().getActual());
    m.append(" conflicts with the rename at ");
    m.append(orig_file);
    m.append(":");
    m.append(orig_pos);

    return new ModuleStructureError(
      Code.MODULE_STRUCTURE_RENAME_RENAME_CONFLICT,
      m.toString(),
      curr_token.getFile(),
      curr_token.getPosition());
  }

  /**
   * @return A module structure error
   */

  public static ModuleStructureError moduleShaderConflict(
    final UASTCDShader shader,
    final UASTCDShader original)
  {
    NullCheck.notNull(shader, "Shader");
    NullCheck.notNull(original, "Original");

    final TokenIdentifierLower orig_name = original.getName();
    final StringBuilder m = new StringBuilder();
    m.append("The declaration of shader ");
    m.append(shader.getName().getActual());
    m.append(" conflicts with the definition at ");
    m.append(orig_name.getFile());
    m.append(":");
    m.append(orig_name.getPosition());
    return new ModuleStructureError(
      Code.MODULE_STRUCTURE_SHADER_CONFLICT,
      m.toString(),
      orig_name.getFile(),
      orig_name.getPosition());
  }

  /**
   * @return A module structure error
   */

  public static
    ModuleStructureError
    moduleShaderFragmentOutputDepthDuplicate(
      final UASTIDShaderFragmentOutputDepth existing,
      final UASTIDShaderFragmentOutputDepth current)
  {
    NullCheck.notNull(current, "Current");
    NullCheck.notNull(existing, "Existing");

    final TokenIdentifierLower name = current.getName();
    final StringBuilder m = new StringBuilder();
    m
      .append("At most one depth output is permitted in the definition of a fragment shader. The original output is at ");
    m.append(existing.getName().getPosition());
    return new ModuleStructureError(
      Code.MODULE_STRUCTURE_SHADER_OUTPUT_DEPTH_DUPLICATE,
      m.toString(),
      name.getFile(),
      name.getPosition());
  }

  /**
   * @return A module structure error
   */

  public static ModuleStructureError moduleShaderLocalConflict(
    final UASTIDValueLocal current,
    final UASTIDValueLocal original)
  {
    NullCheck.notNull(current, "Current");
    NullCheck.notNull(original, "Original");

    final TokenIdentifierLower orig_name = original.getName();
    final StringBuilder m = new StringBuilder();
    m.append("The declaration of term ");
    m.append(current.getName().getActual());
    m.append(" conflicts with the definition at ");
    m.append(orig_name.getFile());
    m.append(":");
    m.append(orig_name.getPosition());
    return new ModuleStructureError(
      Code.MODULE_STRUCTURE_TERM_CONFLICT,
      m.toString(),
      orig_name.getFile(),
      orig_name.getPosition());
  }

  /**
   * @return A module structure error
   */

  public static ModuleStructureError moduleShaderOutputAssignmentDuplicate(
    final TokenIdentifierLower current,
    final TokenIdentifierLower original)
  {
    NullCheck.notNull(current, "Current");
    NullCheck.notNull(original, "Original");

    final StringBuilder m = new StringBuilder();
    m.append("The output ");
    m.append(current.getActual());
    m.append(" has already been assigned at ");
    m.append(original.getFile());
    m.append(":");
    m.append(original.getPosition());
    return new ModuleStructureError(
      Code.MODULE_STRUCTURE_SHADER_OUTPUT_ASSIGNMENT_CONFLICT,
      m.toString(),
      original.getFile(),
      original.getPosition());
  }

  /**
   * @return A module structure error
   */

  public static ModuleStructureError moduleShaderOutputAssignmentMissing(
    final TokenIdentifierLower name)
  {
    NullCheck.notNull(name, "Name");

    final StringBuilder m = new StringBuilder();
    m.append("The shader output ");
    m.append(name.getActual());
    m.append(" is never assigned");
    return new ModuleStructureError(
      Code.MODULE_STRUCTURE_SHADER_OUTPUT_ASSIGNMENT_MISSING,
      m.toString(),
      name.getFile(),
      name.getPosition());
  }

  /**
   * @return A module structure error
   */

  public static ModuleStructureError moduleShaderOutputIndexDuplicate(
    final UASTIDShaderFragmentOutputData current,
    final UASTIDShaderFragmentOutputData original)
  {
    NullCheck.notNull(current, "Current");
    NullCheck.notNull(original, "Original");

    final TokenIdentifierLower orig_name = original.getName();
    final StringBuilder m = new StringBuilder();
    m.append("The output index ");
    m.append(current.getIndex());
    m.append(" conflicts with the index given on the output at ");
    m.append(orig_name.getFile());
    m.append(":");
    m.append(orig_name.getPosition());
    return new ModuleStructureError(
      Code.MODULE_STRUCTURE_SHADER_OUTPUT_INDEX_DUPLICATE,
      m.toString(),
      orig_name.getFile(),
      orig_name.getPosition());
  }

  /**
   * @return A module structure error
   */

  public static ModuleStructureError moduleShaderOutputIndexInvalid(
    final UASTIDShaderFragmentOutputData o)
  {
    NullCheck.notNull(o, "Output");

    final StringBuilder m = new StringBuilder();
    m.append("Shader output index ");
    m.append(o.getIndex());
    m.append(" for output ");
    m.append(o.getName().getActual());
    m.append(" is invalid: Must be >= 0.");
    return new ModuleStructureError(
      Code.MODULE_STRUCTURE_SHADER_OUTPUT_INDEX_INVALID,
      m.toString(),
      o.getName().getFile(),
      o.getName().getPosition());
  }

  /**
   * @return A module structure error
   */

  public static ModuleStructureError moduleShaderOutputIndexMissing(
    final UASTIDShaderFragment shader,
    final Map<Integer, UASTIDShaderFragmentOutputData> outputs,
    final int index)
  {
    NullCheck.notNull(shader, "Shader");
    NullCheck.notNull(outputs, "Outputs");

    final TokenIdentifierLower orig_name = shader.getName();
    final StringBuilder m = new StringBuilder();
    m.append("Shader outputs are discontinuous. Index ");
    m.append(index);
    m.append(" is missing from the set of indices ");
    m.append(outputs.keySet());
    return new ModuleStructureError(
      Code.MODULE_STRUCTURE_SHADER_OUTPUT_INDEX_MISSING,
      m.toString(),
      orig_name.getFile(),
      orig_name.getPosition());
  }

  /**
   * @return A module structure error
   */

  public static ModuleStructureError moduleShaderParameterDuplicate(
    final UASTIDShaderParameters current,
    final UASTIDShaderParameters original)
  {
    NullCheck.notNull(current, "Current");
    NullCheck.notNull(original, "Original");

    final TokenIdentifierLower orig_name = original.getName();
    final StringBuilder m = new StringBuilder();
    m.append("The declaration of shader parameter ");
    m.append(current.getName().getActual());
    m.append(" conflicts with the definition at ");
    m.append(orig_name.getFile());
    m.append(":");
    m.append(orig_name.getPosition());
    return new ModuleStructureError(
      Code.MODULE_STRUCTURE_SHADER_PARAMETER_CONFLICT,
      m.toString(),
      orig_name.getFile(),
      orig_name.getPosition());
  }

  /**
   * @return A module structure error
   */

  public static ModuleStructureError moduleShaderVertexOutputMissingMain(
    final TokenIdentifierLower name)
  {
    NullCheck.notNull(name, "Name");

    final StringBuilder m = new StringBuilder();
    m.append("The vertex shader ");
    m.append(name.getActual());
    m.append(" is missing a main output");

    return new ModuleStructureError(
      Code.MODULE_STRUCTURE_SHADER_OUTPUT_MISSING_MAIN,
      m.toString(),
      name.getFile(),
      name.getPosition());
  }

  /**
   * @return A module structure error
   */

  public static ModuleStructureError moduleShaderVertexOutputMultipleMains(
    final UASTIDShaderVertexOutput main,
    final UASTIDShaderVertexOutput o)
  {
    NullCheck.notNull(main, "Main");
    NullCheck.notNull(o, "Output");

    final StringBuilder m = new StringBuilder();
    m
      .append("Multiple vertex shader outputs are marked as the main vertex output\n");
    m.append("First output: ");
    m.append(main.getName().getActual());
    m.append(" (");
    m.append(main.getName().getFile());
    m.append(":");
    m.append(main.getName().getPosition());
    m.append(")\n");

    m.append("Conflicting output: ");
    m.append(o.getName().getActual());
    m.append(" (");
    m.append(o.getName().getFile());
    m.append(":");
    m.append(o.getName().getPosition());
    m.append(")");

    return new ModuleStructureError(
      Code.MODULE_STRUCTURE_SHADER_OUTPUT_MULTIPLE_MAIN,
      m.toString(),
      o.getName().getFile(),
      o.getName().getPosition());
  }

  /**
   * @return A module structure error
   */

  public static ModuleStructureError moduleTermConflict(
    final UASTCDTerm term,
    final UASTCDTerm original)
  {
    NullCheck.notNull(term, "Current");
    NullCheck.notNull(original, "Original");

    final TokenIdentifierLower orig_name = original.getName();
    final StringBuilder m = new StringBuilder();
    m.append("The declaration of term ");
    m.append(term.getName().getActual());
    m.append(" conflicts with the definition at ");
    m.append(orig_name.getFile());
    m.append(":");
    m.append(orig_name.getPosition());
    return new ModuleStructureError(
      Code.MODULE_STRUCTURE_TERM_CONFLICT,
      m.toString(),
      orig_name.getFile(),
      orig_name.getPosition());
  }

  /**
   * @return A module structure error
   */

  public static ModuleStructureError moduleTypeConflict(
    final UASTCDType type,
    final UASTCDType original)
  {
    NullCheck.notNull(type, "Current");
    NullCheck.notNull(original, "Original");

    final TokenIdentifierLower orig_name = original.getName();
    final StringBuilder m = new StringBuilder();
    m.append("The declaration of type ");
    m.append(type.getName().getActual());
    m.append(" conflicts with the definition at ");
    m.append(orig_name.getFile());
    m.append(":");
    m.append(orig_name.getPosition());
    return new ModuleStructureError(
      Code.MODULE_STRUCTURE_TYPE_CONFLICT,
      m.toString(),
      orig_name.getFile(),
      orig_name.getPosition());
  }

  /**
   * @return A module structure error
   */

  public static ModuleStructureError moduleValueExternalHasExpression(
    final UASTIDValueExternal v)
  {
    NullCheck.notNull(v, "Value");

    final TokenIdentifierLower orig_name = v.getName();
    final StringBuilder m = new StringBuilder();
    m.append("The declaration of external value ");
    m.append(orig_name.getActual());
    m.append(" provides an emulation expression - this is not permitted");
    return new ModuleStructureError(
      Code.MODULE_STRUCTURE_VALUE_EXTERNAL_HAS_EXPRESSION,
      m.toString(),
      orig_name.getFile(),
      orig_name.getPosition());
  }

  /**
   * @return A module structure error
   */

  public static ModuleStructureError moduleValueExternalLacksAscription(
    final UASTIDValueExternal v)
  {
    NullCheck.notNull(v, "Value");

    final TokenIdentifierLower orig_name = v.getName();
    final StringBuilder m = new StringBuilder();
    m.append("The declaration of external value ");
    m.append(orig_name.getActual());
    m.append(" lacks a required type ascription");
    return new ModuleStructureError(
      Code.MODULE_STRUCTURE_VALUE_EXTERNAL_LACKS_ASCRIPTION,
      m.toString(),
      orig_name.getFile(),
      orig_name.getPosition());
  }

  private final Code code;

  /**
   * Construct a module structure error
   */

  public ModuleStructureError(
    final Code in_code,
    final String message,
    final File file,
    final Position position)
  {
    super(message, file, position);
    this.code = NullCheck.notNull(in_code, "Code");
  }

  /**
   * Construct a module structure error
   */

  public ModuleStructureError(
    final NameRestrictionsException e)
  {
    super(e, e.getToken().getFile(), e.getToken().getPosition());
    this.code = Code.MODULE_STRUCTURE_RESTRICTED_NAME;
  }

  @Override public String getCategory()
  {
    return "module-structure";
  }

  /**
   * @return The error code
   */

  public Code getCode()
  {
    return this.code;
  }
}
