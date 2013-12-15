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

import java.io.File;
import java.util.Map;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Option;
import com.io7m.jparasol.CompilerError;
import com.io7m.jparasol.ModulePath;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.NameRestrictions.NameRestrictionsException;
import com.io7m.jparasol.lexer.Position;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.lexer.Token.TokenIdentifierUpper;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDFunctionArgument;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDImport;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShader;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragment;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderFragmentOutput;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDShaderParameters;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDTerm;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDType;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDTypeRecordField;
import com.io7m.jparasol.untyped.ast.initial.UASTIDeclaration.UASTIDValueLocal;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpression.UASTIRecordFieldAssignment;
import com.io7m.jparasol.untyped.ast.initial.UASTIUnchecked;

public final class ModuleStructureError extends CompilerError
{
  static enum Code
  {
    MODULE_STRUCTURE_FUNCTION_ARGUMENT_DUPLICATE,
    MODULE_STRUCTURE_IMPORT_DUPLICATE,
    MODULE_STRUCTURE_IMPORT_IMPORT_CONFLICT,
    MODULE_STRUCTURE_IMPORT_REDUNDANT,
    MODULE_STRUCTURE_IMPORT_RENAME_CONFLICT,
    MODULE_STRUCTURE_IMPORTS_SELF,
    MODULE_STRUCTURE_RECORD_FIELD_DUPLICATE,
    MODULE_STRUCTURE_RENAME_IMPORT_CONFLICT,
    MODULE_STRUCTURE_RENAME_RENAME_CONFLICT,
    MODULE_STRUCTURE_RESTRICTED_NAME,
    MODULE_STRUCTURE_SHADER_CONFLICT,
    MODULE_STRUCTURE_SHADER_OUTPUT_ASSIGNMENT_CONFLICT,
    MODULE_STRUCTURE_SHADER_OUTPUT_ASSIGNMENT_MISSING,
    MODULE_STRUCTURE_SHADER_OUTPUT_INDEX_DUPLICATE,
    MODULE_STRUCTURE_SHADER_OUTPUT_INDEX_INVALID,
    MODULE_STRUCTURE_SHADER_OUTPUT_INDEX_MISSING,
    MODULE_STRUCTURE_SHADER_PARAMETER_CONFLICT,
    MODULE_STRUCTURE_TERM_CONFLICT,
    MODULE_STRUCTURE_TYPE_CONFLICT,
  }

  private static final long serialVersionUID = 8998482912668461862L;

  public static ModuleStructureError moduleFunctionArgumentDuplicate(
    final @Nonnull UASTIDFunctionArgument<UASTIUnchecked> current,
    final @Nonnull UASTIDFunctionArgument<UASTIUnchecked> original)
    throws ConstraintError
  {
    final TokenIdentifierLower orig_name = original.getName();
    final StringBuilder m = new StringBuilder();
    m.append("Duplicate function argument '");
    m.append(current.getName().getActual());
    m.append("' conflicts with the definition at ");
    m.append(orig_name.getFile());
    m.append(":");
    m.append(orig_name.getPosition());
    return new ModuleStructureError(
      Code.MODULE_STRUCTURE_FUNCTION_ARGUMENT_DUPLICATE,
      m.toString(),
      orig_name.getFile(),
      orig_name.getPosition());
  }

  public static @Nonnull ModuleStructureError moduleImportDuplicate(
    final @Nonnull UASTIDImport<UASTIUnchecked> current,
    final @Nonnull UASTIDImport<UASTIUnchecked> original)
    throws ConstraintError
  {
    final ModulePath curr_path = current.getPath();
    final ModulePath orig_path = original.getPath();
    final ModulePathFlat curr_flat = ModulePathFlat.fromModulePath(curr_path);
    final File orig_file = orig_path.getName().getFile();
    final Position orig_pos = orig_path.getName().getPosition();

    final StringBuilder m = new StringBuilder();
    m.append("Module '");
    m.append(curr_flat.getActual());
    m.append("' has already been imported by the declaration at ");
    m.append(orig_file);
    m.append(":");
    m.append(orig_pos);

    return new ModuleStructureError(
      Code.MODULE_STRUCTURE_IMPORT_DUPLICATE,
      m.toString(),
      orig_file,
      orig_pos);

  }

  public static ModuleStructureError moduleImportImportConflict(
    final @Nonnull UASTIDImport<UASTIUnchecked> current,
    final @Nonnull UASTIDImport<UASTIUnchecked> original)
    throws ConstraintError
  {
    final ModulePath curr_path = current.getPath();
    final ModulePath orig_path = original.getPath();
    final ModulePathFlat curr_flat = ModulePathFlat.fromModulePath(curr_path);
    final File orig_file = orig_path.getName().getFile();
    final Position orig_pos = orig_path.getName().getPosition();
    final TokenIdentifierUpper curr_token = curr_path.getName();

    final StringBuilder m = new StringBuilder();
    m.append("Import of module '");
    m.append(curr_flat.getActual());
    m.append("' conflicts with the import at ");
    m.append(orig_file);
    m.append(":");
    m.append(orig_pos);

    return new ModuleStructureError(
      Code.MODULE_STRUCTURE_IMPORT_IMPORT_CONFLICT,
      m.toString(),
      curr_token.getFile(),
      curr_token.getPosition());
  }

  public static @Nonnull ModuleStructureError moduleImportRedundantRename(
    final @Nonnull UASTIDImport<UASTIUnchecked> i)
    throws ConstraintError
  {
    final TokenIdentifierUpper token = i.getPath().getName();
    final StringBuilder m = new StringBuilder();
    m.append("Renaming the imported module '");
    m.append(ModulePathFlat.fromModulePath(i.getPath()));
    m.append("' to the same name is redundant");
    return new ModuleStructureError(
      Code.MODULE_STRUCTURE_IMPORT_REDUNDANT,
      m.toString(),
      token.getFile(),
      token.getPosition());
  }

  public static ModuleStructureError moduleImportRenameConflict(
    final @Nonnull UASTIDImport<UASTIUnchecked> current,
    final @Nonnull UASTIDImport<UASTIUnchecked> original)
    throws ConstraintError
  {
    final ModulePath curr_path = current.getPath();
    final ModulePath orig_path = original.getPath();
    final ModulePathFlat curr_flat = ModulePathFlat.fromModulePath(curr_path);
    final File orig_file = orig_path.getName().getFile();
    final Position orig_pos = orig_path.getName().getPosition();
    final TokenIdentifierUpper curr_token = curr_path.getName();

    final StringBuilder m = new StringBuilder();
    m.append("Import of module '");
    m.append(curr_flat.getActual());
    m.append("' conflicts with the rename at ");
    m.append(orig_file);
    m.append(":");
    m.append(orig_pos);

    return new ModuleStructureError(
      Code.MODULE_STRUCTURE_IMPORT_RENAME_CONFLICT,
      m.toString(),
      curr_token.getFile(),
      curr_token.getPosition());
  }

  public static @Nonnull ModuleStructureError moduleImportsSelf(
    final @Nonnull UASTIDImport<UASTIUnchecked> i)
    throws ConstraintError
  {
    final TokenIdentifierUpper token = i.getPath().getName();
    final StringBuilder m = new StringBuilder();
    m.append("Module '");
    m.append(ModulePathFlat.fromModulePath(i.getPath()));
    m.append("' imports itself");
    return new ModuleStructureError(
      Code.MODULE_STRUCTURE_IMPORTS_SELF,
      m.toString(),
      token.getFile(),
      token.getPosition());
  }

  public static ModuleStructureError moduleRecordExpressionFieldDuplicate(
    final @Nonnull UASTIRecordFieldAssignment<UASTIUnchecked> current,
    final @Nonnull UASTIRecordFieldAssignment<UASTIUnchecked> original)
    throws ConstraintError
  {
    final TokenIdentifierLower orig_name = original.getName();
    final StringBuilder m = new StringBuilder();
    m.append("Duplicate record field '");
    m.append(current.getName().getActual());
    m.append("' conflicts with the expression at ");
    m.append(orig_name.getFile());
    m.append(":");
    m.append(orig_name.getPosition());
    return new ModuleStructureError(
      Code.MODULE_STRUCTURE_RECORD_FIELD_DUPLICATE,
      m.toString(),
      orig_name.getFile(),
      orig_name.getPosition());
  }

  public static ModuleStructureError moduleRecordFieldDuplicate(
    final @Nonnull UASTIDTypeRecordField<UASTIUnchecked> current,
    final @Nonnull UASTIDTypeRecordField<UASTIUnchecked> original)
    throws ConstraintError
  {
    final TokenIdentifierLower orig_name = original.getName();
    final StringBuilder m = new StringBuilder();
    m.append("Duplicate record field '");
    m.append(current.getName().getActual());
    m.append("' conflicts with the definition at ");
    m.append(orig_name.getFile());
    m.append(":");
    m.append(orig_name.getPosition());
    return new ModuleStructureError(
      Code.MODULE_STRUCTURE_RECORD_FIELD_DUPLICATE,
      m.toString(),
      orig_name.getFile(),
      orig_name.getPosition());
  }

  public static ModuleStructureError moduleRenameImportConflict(
    final @Nonnull UASTIDImport<UASTIUnchecked> current,
    final @Nonnull UASTIDImport<UASTIUnchecked> original)
    throws ConstraintError
  {
    final ModulePath curr_path = current.getPath();
    final ModulePath orig_path = original.getPath();
    final ModulePathFlat curr_flat = ModulePathFlat.fromModulePath(curr_path);
    final File orig_file = orig_path.getName().getFile();
    final Position orig_pos = orig_path.getName().getPosition();
    final TokenIdentifierUpper curr_token = curr_path.getName();

    final StringBuilder m = new StringBuilder();
    m.append("Rename of module '");
    m.append(curr_flat.getActual());
    m.append("' to '");
    m.append(((Option.Some<TokenIdentifierUpper>) current.getRename()).value
      .getActual());
    m.append("' conflicts with the import at ");
    m.append(orig_file);
    m.append(":");
    m.append(orig_pos);

    return new ModuleStructureError(
      Code.MODULE_STRUCTURE_RENAME_IMPORT_CONFLICT,
      m.toString(),
      curr_token.getFile(),
      curr_token.getPosition());
  }

  public static ModuleStructureError moduleRenameRenameConflict(
    final @Nonnull UASTIDImport<UASTIUnchecked> current,
    final @Nonnull UASTIDImport<UASTIUnchecked> original)
    throws ConstraintError
  {
    final ModulePath curr_path = current.getPath();
    final ModulePath orig_path = original.getPath();
    final ModulePathFlat curr_flat = ModulePathFlat.fromModulePath(curr_path);
    final File orig_file = orig_path.getName().getFile();
    final Position orig_pos = orig_path.getName().getPosition();
    final TokenIdentifierUpper curr_token = curr_path.getName();

    final StringBuilder m = new StringBuilder();
    m.append("Rename of module '");
    m.append(curr_flat.getActual());
    m.append("' to '");
    m.append(((Option.Some<TokenIdentifierUpper>) current.getRename()).value
      .getActual());
    m.append("' conflicts with the rename at ");
    m.append(orig_file);
    m.append(":");
    m.append(orig_pos);

    return new ModuleStructureError(
      Code.MODULE_STRUCTURE_RENAME_RENAME_CONFLICT,
      m.toString(),
      curr_token.getFile(),
      curr_token.getPosition());
  }

  public static ModuleStructureError moduleShaderConflict(
    final @Nonnull UASTIDShader<UASTIUnchecked> current,
    final @Nonnull UASTIDShader<UASTIUnchecked> original)
    throws ConstraintError
  {
    final TokenIdentifierLower orig_name = original.getName();
    final StringBuilder m = new StringBuilder();
    m.append("The declaration of shader '");
    m.append(current.getName().getActual());
    m.append("' conflicts with the definition at ");
    m.append(orig_name.getFile());
    m.append(":");
    m.append(orig_name.getPosition());
    return new ModuleStructureError(
      Code.MODULE_STRUCTURE_SHADER_CONFLICT,
      m.toString(),
      orig_name.getFile(),
      orig_name.getPosition());
  }

  public static ModuleStructureError moduleShaderLocalConflict(
    final @Nonnull UASTIDValueLocal<UASTIUnchecked> current,
    final @Nonnull UASTIDValueLocal<UASTIUnchecked> original)
    throws ConstraintError
  {
    final TokenIdentifierLower orig_name = original.getName();
    final StringBuilder m = new StringBuilder();
    m.append("The declaration of term '");
    m.append(current.getName().getActual());
    m.append("' conflicts with the definition at ");
    m.append(orig_name.getFile());
    m.append(":");
    m.append(orig_name.getPosition());
    return new ModuleStructureError(
      Code.MODULE_STRUCTURE_TERM_CONFLICT,
      m.toString(),
      orig_name.getFile(),
      orig_name.getPosition());
  }

  public static ModuleStructureError moduleShaderOutputAssignmentDuplicate(
    final @Nonnull TokenIdentifierLower current,
    final @Nonnull TokenIdentifierLower original)
    throws ConstraintError
  {
    final StringBuilder m = new StringBuilder();
    m.append("The output '");
    m.append(current.getActual());
    m.append("' has already been assigned at ");
    m.append(original.getFile());
    m.append(":");
    m.append(original.getPosition());
    return new ModuleStructureError(
      Code.MODULE_STRUCTURE_SHADER_OUTPUT_ASSIGNMENT_CONFLICT,
      m.toString(),
      original.getFile(),
      original.getPosition());
  }

  public static ModuleStructureError moduleShaderOutputAssignmentMissing(
    final TokenIdentifierLower name)
    throws ConstraintError
  {
    final StringBuilder m = new StringBuilder();
    m.append("The shader output '");
    m.append(name.getActual());
    m.append("' is never assigned");
    return new ModuleStructureError(
      Code.MODULE_STRUCTURE_SHADER_OUTPUT_ASSIGNMENT_MISSING,
      m.toString(),
      name.getFile(),
      name.getPosition());
  }

  public static @Nonnull
    ModuleStructureError
    moduleShaderOutputIndexDuplicate(
      final @Nonnull UASTIDShaderFragmentOutput<UASTIUnchecked> current,
      final @Nonnull UASTIDShaderFragmentOutput<UASTIUnchecked> original)
      throws ConstraintError
  {
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

  public static @Nonnull ModuleStructureError moduleShaderOutputIndexInvalid(
    final @Nonnull UASTIDShaderFragmentOutput<UASTIUnchecked> o)
    throws ConstraintError
  {
    final StringBuilder m = new StringBuilder();
    m.append("Shader output index ");
    m.append(o.getIndex());
    m.append(" for output '");
    m.append(o.getName().getActual());
    m.append("' is invalid: Must be >= 0.");
    return new ModuleStructureError(
      Code.MODULE_STRUCTURE_SHADER_OUTPUT_INDEX_INVALID,
      m.toString(),
      o.getName().getFile(),
      o.getName().getPosition());
  }

  public static @Nonnull
    ModuleStructureError
    moduleShaderOutputIndexMissing(
      final @Nonnull UASTIDShaderFragment<UASTIUnchecked> shader,
      final @Nonnull Map<Integer, UASTIDShaderFragmentOutput<UASTIUnchecked>> outputs,
      final int index)
      throws ConstraintError
  {
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

  public static @Nonnull ModuleStructureError moduleShaderParameterDuplicate(
    final @Nonnull UASTIDShaderParameters<UASTIUnchecked> current,
    final @Nonnull UASTIDShaderParameters<UASTIUnchecked> original)
    throws ConstraintError
  {
    final TokenIdentifierLower orig_name = original.getName();
    final StringBuilder m = new StringBuilder();
    m.append("The declaration of shader parameter '");
    m.append(current.getName().getActual());
    m.append("' conflicts with the definition at ");
    m.append(orig_name.getFile());
    m.append(":");
    m.append(orig_name.getPosition());
    return new ModuleStructureError(
      Code.MODULE_STRUCTURE_SHADER_PARAMETER_CONFLICT,
      m.toString(),
      orig_name.getFile(),
      orig_name.getPosition());
  }

  public static ModuleStructureError moduleTermConflict(
    final @Nonnull UASTIDTerm<UASTIUnchecked> current,
    final @Nonnull UASTIDTerm<UASTIUnchecked> original)
    throws ConstraintError
  {
    final TokenIdentifierLower orig_name = original.getName();
    final StringBuilder m = new StringBuilder();
    m.append("The declaration of term '");
    m.append(current.getName().getActual());
    m.append("' conflicts with the definition at ");
    m.append(orig_name.getFile());
    m.append(":");
    m.append(orig_name.getPosition());
    return new ModuleStructureError(
      Code.MODULE_STRUCTURE_TERM_CONFLICT,
      m.toString(),
      orig_name.getFile(),
      orig_name.getPosition());
  }

  public static ModuleStructureError moduleTypeConflict(
    final @Nonnull UASTIDType<UASTIUnchecked> current,
    final @Nonnull UASTIDType<UASTIUnchecked> original)
    throws ConstraintError
  {
    final TokenIdentifierLower orig_name = original.getName();
    final StringBuilder m = new StringBuilder();
    m.append("The declaration of type '");
    m.append(current.getName().getActual());
    m.append("' conflicts with the definition at ");
    m.append(orig_name.getFile());
    m.append(":");
    m.append(orig_name.getPosition());
    return new ModuleStructureError(
      Code.MODULE_STRUCTURE_TYPE_CONFLICT,
      m.toString(),
      orig_name.getFile(),
      orig_name.getPosition());
  }

  private final @Nonnull Code code;

  public ModuleStructureError(
    final @Nonnull Code code,
    final @Nonnull String message,
    final @Nonnull File file,
    final @Nonnull Position position)
    throws ConstraintError
  {
    super(message, file, position);
    this.code = Constraints.constrainNotNull(code, "Code");
  }

  public ModuleStructureError(
    final NameRestrictionsException e)
    throws ConstraintError
  {
    super(e, e.getToken().getFile(), e.getToken().getPosition());
    this.code = Code.MODULE_STRUCTURE_RESTRICTED_NAME;
  }

  public final @Nonnull Code getCode()
  {
    return this.code;
  }
}
