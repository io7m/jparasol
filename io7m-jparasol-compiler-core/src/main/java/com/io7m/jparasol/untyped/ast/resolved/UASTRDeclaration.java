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

package com.io7m.jparasol.untyped.ast.resolved;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Function;
import com.io7m.jaux.functional.Option;
import com.io7m.jparasol.ModulePath;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.PackagePath;
import com.io7m.jparasol.lexer.Token.TokenDiscard;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.lexer.Token.TokenIdentifierUpper;
import com.io7m.jparasol.untyped.ast.resolved.UASTRExpression.UASTREVariable;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTermName.UASTRTermNameLocal;

public abstract class UASTRDeclaration
{
  /**
   * The type of local declarations.
   */

  public static abstract class UASTRDeclarationLocalLevel extends
    UASTRDeclaration
  {
    // Nothing
  }

  /**
   * The type of module-level declarations.
   */

  public static abstract class UASTRDeclarationModuleLevel extends
    UASTRDeclaration
  {
    public abstract @Nonnull TokenIdentifierLower getName();
  }

  /**
   * The type of shader-level declarations.
   */

  public static abstract class UASTRDeclarationShaderLevel extends
    UASTRDeclaration
  {
    // Nothing
  }

  /**
   * The type of unit-level declarations.
   */

  public static abstract class UASTRDeclarationUnitLevel extends
    UASTRDeclaration
  {
    // Nothing
  }

  /**
   * The type of function declarations.
   */

  public static abstract class UASTRDFunction extends UASTRDTerm
  {
    // Nothing
  }

  public static final class UASTRDFunctionArgument
  {
    private final @Nonnull UASTRTermNameLocal name;
    private final @Nonnull UASTRTypeName      type;

    public UASTRDFunctionArgument(
      final @Nonnull UASTRTermNameLocal name,
      final @Nonnull UASTRTypeName type)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.type = Constraints.constrainNotNull(type, "Type");
    }

    public @Nonnull UASTRTermNameLocal getName()
    {
      return this.name;
    }

    public @Nonnull UASTRTypeName getType()
    {
      return this.type;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTRDFunctionArgument ");
      builder.append(this.name.show());
      builder.append(" ");
      builder.append(this.type.show());
      builder.append("]");
      return builder.toString();
    }
  }

  /**
   * Fully defined functions.
   */

  public static final class UASTRDFunctionDefined extends UASTRDFunction
  {
    private final @Nonnull List<UASTRDFunctionArgument> arguments;
    private final @Nonnull UASTRExpression              body;
    private final @Nonnull TokenIdentifierLower         name;
    private final @Nonnull UASTRTypeName                return_type;

    public UASTRDFunctionDefined(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull List<UASTRDFunctionArgument> arguments,
      final @Nonnull UASTRTypeName return_type,
      final @Nonnull UASTRExpression body)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.arguments = Constraints.constrainNotNull(arguments, "Arguments");
      this.return_type =
        Constraints.constrainNotNull(return_type, "Return type");
      this.body = Constraints.constrainNotNull(body, "Body");
    }

    public @Nonnull List<UASTRDFunctionArgument> getArguments()
    {
      return this.arguments;
    }

    public @Nonnull UASTRExpression getBody()
    {
      return this.body;
    }

    @Override public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    public @Nonnull UASTRTypeName getReturnType()
    {
      return this.return_type;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTRDFunctionDefined ");
      builder.append(this.name.getActual());
      builder.append(" ");
      builder.append(this.arguments);
      builder.append(" ");
      builder.append(this.return_type.show());
      builder.append(" ");
      builder.append(this.body);
      builder.append("]");
      return builder.toString();
    }
  }

  /**
   * Functions with external declarations (private FFI).
   */

  public static final class UASTRDFunctionExternal extends UASTRDFunction
  {
    private final @Nonnull List<UASTRDFunctionArgument> arguments;
    private final @Nonnull TokenIdentifierLower         external;
    private final @Nonnull TokenIdentifierLower         name;
    private final @Nonnull UASTRTypeName                return_type;

    public UASTRDFunctionExternal(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull List<UASTRDFunctionArgument> arguments,
      final @Nonnull UASTRTypeName return_type,
      final @Nonnull TokenIdentifierLower external)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.arguments = Constraints.constrainNotNull(arguments, "Arguments");
      this.return_type =
        Constraints.constrainNotNull(return_type, "Return type");
      this.external = Constraints.constrainNotNull(external, "External");
    }

    public @Nonnull List<UASTRDFunctionArgument> getArguments()
    {
      return this.arguments;
    }

    public @Nonnull TokenIdentifierLower getExternal()
    {
      return this.external;
    }

    @Override public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    public @Nonnull UASTRTypeName getReturnType()
    {
      return this.return_type;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTRDFunctionExternal ");
      builder.append(this.name.getActual());
      builder.append(" ");
      builder.append(this.arguments);
      builder.append(" ");
      builder.append(this.return_type.show());
      builder.append(" ");
      builder.append(this.external.getActual());
      builder.append("]");
      return builder.toString();
    }
  }

  /**
   * Import declarations.
   */

  public static final class UASTRDImport extends UASTRDeclaration
  {
    private final @Nonnull ModulePath                   path;
    private final @Nonnull Option<TokenIdentifierUpper> rename;

    public UASTRDImport(
      final @Nonnull ModulePath path,
      final @Nonnull Option<TokenIdentifierUpper> rename)
      throws ConstraintError
    {
      this.path = Constraints.constrainNotNull(path, "Path");
      this.rename = Constraints.constrainNotNull(rename, "Rename");
    }

    public @Nonnull ModulePath getPath()
    {
      return this.path;
    }

    public @Nonnull Option<TokenIdentifierUpper> getRename()
    {
      return this.rename;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTRDImport ");
      builder.append(this.path);
      builder.append(" ");
      builder.append(this.rename);
      builder.append("]");
      return builder.toString();
    }
  }

  /**
   * Module declarations.
   */

  public static final class UASTRDModule extends UASTRDeclarationUnitLevel
  {
    private final @Nonnull List<UASTRDeclarationModuleLevel> declarations;
    private final @Nonnull Map<ModulePathFlat, UASTRDImport> imported_modules;
    private final @Nonnull Map<String, UASTRDImport>         imported_names;
    private final @Nonnull Map<String, UASTRDImport>         imported_renames;
    private final @Nonnull List<UASTRDImport>                imports;
    private final @Nonnull ModulePath                        path;
    private final @Nonnull Map<String, UASTRDShader>         shaders;
    private final @Nonnull Map<String, UASTRDTerm>           terms;
    private final @Nonnull Map<String, UASTRDType>           types;

    public UASTRDModule(
      final @Nonnull ModulePath path,
      final @Nonnull List<UASTRDImport> imports,
      final @Nonnull Map<ModulePathFlat, UASTRDImport> imported_modules,
      final @Nonnull Map<String, UASTRDImport> imported_names,
      final @Nonnull Map<String, UASTRDImport> imported_renames,
      final @Nonnull List<UASTRDeclarationModuleLevel> declarations,
      final @Nonnull Map<String, UASTRDTerm> terms,
      final @Nonnull Map<String, UASTRDType> types,
      final @Nonnull Map<String, UASTRDShader> shaders)
      throws ConstraintError
    {
      this.path = Constraints.constrainNotNull(path, "Path");

      this.imports = Constraints.constrainNotNull(imports, "Imports");
      this.imported_modules =
        Constraints.constrainNotNull(imported_modules, "Imported modules");
      this.imported_names =
        Constraints.constrainNotNull(imported_names, "Imported names");
      this.imported_renames =
        Constraints.constrainNotNull(imported_renames, "Imported renames");

      this.declarations =
        Constraints.constrainNotNull(declarations, "Declarations");
      this.terms = Constraints.constrainNotNull(terms, "Terms");
      this.types = Constraints.constrainNotNull(types, "Types");
      this.shaders = Constraints.constrainNotNull(shaders, "Shaders");
    }

    public @Nonnull List<UASTRDeclarationModuleLevel> getDeclarations()
    {
      return this.declarations;
    }

    public @Nonnull List<UASTRDImport> getImports()
    {
      return this.imports;
    }

    public @Nonnull ModulePath getPath()
    {
      return this.path;
    }

    public @Nonnull Map<String, UASTRDShader> getShaders()
    {
      return this.shaders;
    }

    public @Nonnull Map<String, UASTRDTerm> getTerms()
    {
      return this.terms;
    }

    public @Nonnull Map<String, UASTRDType> getTypes()
    {
      return this.types;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTRDModule ");
      builder.append(ModulePathFlat.fromModulePath(this.path).getActual());
      builder.append("\n");

      if (this.imports.isEmpty()) {
        builder.append("  []\n");
      } else {
        for (int index = 0; index < this.imports.size(); ++index) {
          if (index == 0) {
            builder.append("  [");
          } else {
            builder.append("  ");
          }
          builder.append(this.imports.get(index));
          if ((index + 1) == this.imports.size()) {
            builder.append("]\n");
          } else {
            builder.append("\n");
          }
        }
      }

      if (this.declarations.isEmpty()) {
        builder.append("  []\n");
      } else {
        for (int index = 0; index < this.declarations.size(); ++index) {
          if (index == 0) {
            builder.append("  [\n    ");
          } else {
            builder.append("    ");
          }
          builder.append(this.declarations.get(index));
          if ((index + 1) == this.declarations.size()) {
            builder.append("\n  ]\n");
          } else {
            builder.append("\n");
          }
        }
      }

      builder.append("]");
      return builder.toString();
    }
  }

  /**
   * Package declarations.
   */

  public static final class UASTRDPackage extends UASTRDeclarationUnitLevel
  {
    private final @Nonnull PackagePath path;

    public UASTRDPackage(
      final @Nonnull PackagePath path)
      throws ConstraintError
    {
      this.path = Constraints.constrainNotNull(path, "Path");
    }

    public @Nonnull PackagePath getPath()
    {
      return this.path;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTRDPackage ");
      builder.append(this.path);
      builder.append("]");
      return builder.toString();
    }
  }

  /**
   * The type of shader declarations.
   */

  public static abstract class UASTRDShader extends
    UASTRDeclarationModuleLevel
  {
    private final @Nonnull TokenIdentifierLower name;

    protected UASTRDShader(
      final @Nonnull TokenIdentifierLower name)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
    }

    @Override public final @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }
  }

  public static final class UASTRDShaderFragment extends UASTRDShader
  {
    private final @Nonnull List<UASTRDShaderFragmentInput>            inputs;
    private final @Nonnull List<UASTRDShaderFragmentLocal>            locals;
    private final @Nonnull List<UASTRDShaderFragmentOutput>           outputs;
    private final @Nonnull List<UASTRDShaderFragmentParameter>        parameters;
    private final @Nonnull List<UASTRDShaderFragmentOutputAssignment> writes;

    public UASTRDShaderFragment(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull List<UASTRDShaderFragmentInput> inputs,
      final @Nonnull List<UASTRDShaderFragmentOutput> outputs,
      final @Nonnull List<UASTRDShaderFragmentParameter> parameters,
      final @Nonnull List<UASTRDShaderFragmentLocal> locals,
      final @Nonnull List<UASTRDShaderFragmentOutputAssignment> writes)
      throws ConstraintError
    {
      super(name);
      this.inputs = Constraints.constrainNotNull(inputs, "Inputs");
      this.outputs = Constraints.constrainNotNull(outputs, "Outputs");
      this.parameters =
        Constraints.constrainNotNull(parameters, "Parameters");
      this.locals = Constraints.constrainNotNull(locals, "Locals");
      this.writes = Constraints.constrainNotNull(writes, "Writes");
    }

    public @Nonnull List<UASTRDShaderFragmentInput> getInputs()
    {
      return this.inputs;
    }

    public @Nonnull List<UASTRDShaderFragmentLocal> getLocals()
    {
      return this.locals;
    }

    public @Nonnull List<UASTRDShaderFragmentOutput> getOutputs()
    {
      return this.outputs;
    }

    public @Nonnull List<UASTRDShaderFragmentParameter> getParameters()
    {
      return this.parameters;
    }

    public @Nonnull List<UASTRDShaderFragmentOutputAssignment> getWrites()
    {
      return this.writes;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTRDShaderFragment ");
      builder.append(this.inputs);
      builder.append(" ");
      builder.append(this.parameters);
      builder.append(" ");
      builder.append(this.outputs);
      builder.append(" ");
      builder.append(this.locals);
      builder.append(" ");
      builder.append(this.writes);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class UASTRDShaderFragmentInput extends
    UASTRDShaderFragmentParameters
  {
    private final @Nonnull UASTRTermNameLocal name;

    public UASTRDShaderFragmentInput(
      final @Nonnull UASTRTermNameLocal name,
      final @Nonnull UASTRTypeName type)
      throws ConstraintError
    {
      super(type);
      this.name = Constraints.constrainNotNull(name, "Name");
    }

    public @Nonnull UASTRTermNameLocal getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTRDShaderFragmentInput ");
      builder.append(this.name.show());
      builder.append("]");
      return builder.toString();
    }
  }

  public static abstract class UASTRDShaderFragmentLocal extends
    UASTRDeclarationShaderLevel
  {
    // Nothing
  }

  public static final class UASTRDShaderFragmentLocalDiscard extends
    UASTRDShaderFragmentLocal
  {
    private final @Nonnull TokenDiscard    discard;
    private final @Nonnull UASTRExpression expression;

    public UASTRDShaderFragmentLocalDiscard(
      final TokenDiscard discard,
      final UASTRExpression expression)
      throws ConstraintError
    {
      this.discard = Constraints.constrainNotNull(discard, "Discard");
      this.expression =
        Constraints.constrainNotNull(expression, "Expression");
    }

    public @Nonnull TokenDiscard getDiscard()
    {
      return this.discard;
    }

    public @Nonnull UASTRExpression getExpression()
    {
      return this.expression;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTRDShaderFragmentLocalDiscard ");
      builder.append(this.expression);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class UASTRDShaderFragmentLocalValue extends
    UASTRDShaderFragmentLocal
  {
    private final @Nonnull UASTRDValueLocal value;

    public UASTRDShaderFragmentLocalValue(
      final @Nonnull UASTRDValueLocal value)
      throws ConstraintError
    {
      this.value = Constraints.constrainNotNull(value, "Value");
    }

    public @Nonnull UASTRDValueLocal getValue()
    {
      return this.value;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTRDShaderFragmentLocalValue ");
      builder.append(this.value);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class UASTRDShaderFragmentOutput extends
    UASTRDShaderFragmentParameters
  {
    private final int                           index;
    private final @Nonnull TokenIdentifierLower name;

    public UASTRDShaderFragmentOutput(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTRTypeName type,
      final int index)
      throws ConstraintError
    {
      super(type);
      this.name = Constraints.constrainNotNull(name, "Name");
      this.index = index;
    }

    public int getIndex()
    {
      return this.index;
    }

    public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTRDShaderFragmentOutput ");
      builder.append(this.name.getActual());
      builder.append(" ");
      builder.append(this.index);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class UASTRDShaderFragmentOutputAssignment extends
    UASTRDeclarationShaderLevel
  {
    private final @Nonnull TokenIdentifierLower name;
    private final @Nonnull UASTREVariable       variable;

    public UASTRDShaderFragmentOutputAssignment(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTREVariable variable)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.variable = Constraints.constrainNotNull(variable, "Variable");
    }

    public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    public @Nonnull UASTREVariable getVariable()
    {
      return this.variable;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTRDShaderFragmentOutputAssignment ");
      builder.append(this.name.getActual());
      builder.append(" ");
      builder.append(this.variable);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class UASTRDShaderFragmentParameter extends
    UASTRDShaderFragmentParameters
  {
    private final @Nonnull UASTRTermNameLocal name;

    public UASTRDShaderFragmentParameter(
      final @Nonnull UASTRTermNameLocal name,
      final @Nonnull UASTRTypeName type)
      throws ConstraintError
    {
      super(type);
      this.name = Constraints.constrainNotNull(name, "Name");
    }

    public @Nonnull UASTRTermNameLocal getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTRDShaderFragmentParameter ");
      builder.append(this.name.show());
      builder.append("]");
      return builder.toString();
    }
  }

  public static abstract class UASTRDShaderFragmentParameters extends
    UASTRDShaderParameters
  {
    UASTRDShaderFragmentParameters(
      final @Nonnull UASTRTypeName type)
      throws ConstraintError
    {
      super(type);
    }
  }

  public static abstract class UASTRDShaderParameters extends
    UASTRDeclarationShaderLevel
  {
    private final @Nonnull UASTRTypeName type;

    UASTRDShaderParameters(
      final @Nonnull UASTRTypeName type)
      throws ConstraintError
    {
      this.type = Constraints.constrainNotNull(type, "Type");
    }

    public final @Nonnull UASTRTypeName getType()
    {
      return this.type;
    }
  }

  public static final class UASTRDShaderProgram extends UASTRDShader
  {
    private final @Nonnull UASTRShaderName fragment_shader;
    private final @Nonnull UASTRShaderName vertex_shader;

    public UASTRDShaderProgram(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTRShaderName vertex_shader,
      final @Nonnull UASTRShaderName fragment_shader)
      throws ConstraintError
    {
      super(name);
      this.vertex_shader =
        Constraints.constrainNotNull(vertex_shader, "Vertex shader");
      this.fragment_shader =
        Constraints.constrainNotNull(fragment_shader, "Fragment shader");
    }

    public @Nonnull UASTRShaderName getFragmentShader()
    {
      return this.fragment_shader;
    }

    public @Nonnull UASTRShaderName getVertexShader()
    {
      return this.vertex_shader;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTRDShaderProgram ");
      builder.append(this.fragment_shader.show());
      builder.append(" ");
      builder.append(this.vertex_shader.show());
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class UASTRDShaderVertex extends UASTRDShader
  {
    private final @Nonnull List<UASTRDShaderVertexInput>            inputs;
    private final @Nonnull List<UASTRDShaderVertexOutput>           outputs;
    private final @Nonnull List<UASTRDShaderVertexParameter>        parameters;
    private final @Nonnull List<UASTRDShaderVertexLocalValue>       values;
    private final @Nonnull List<UASTRDShaderVertexOutputAssignment> writes;

    public UASTRDShaderVertex(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull List<UASTRDShaderVertexInput> inputs,
      final @Nonnull List<UASTRDShaderVertexOutput> outputs,
      final @Nonnull List<UASTRDShaderVertexParameter> parameters,
      final @Nonnull List<UASTRDShaderVertexLocalValue> values,
      final @Nonnull List<UASTRDShaderVertexOutputAssignment> writes)
      throws ConstraintError
    {
      super(name);
      this.inputs = Constraints.constrainNotNull(inputs, "Inputs");
      this.outputs = Constraints.constrainNotNull(outputs, "Outputs");
      this.parameters =
        Constraints.constrainNotNull(parameters, "Parameters");
      this.values = Constraints.constrainNotNull(values, "Values");
      this.writes = Constraints.constrainNotNull(writes, "Writes");
    }

    public @Nonnull List<UASTRDShaderVertexInput> getInputs()
    {
      return this.inputs;
    }

    public @Nonnull List<UASTRDShaderVertexOutput> getOutputs()
    {
      return this.outputs;
    }

    public @Nonnull List<UASTRDShaderVertexParameter> getParameters()
    {
      return this.parameters;
    }

    public @Nonnull List<UASTRDShaderVertexLocalValue> getValues()
    {
      return this.values;
    }

    public @Nonnull List<UASTRDShaderVertexOutputAssignment> getWrites()
    {
      return this.writes;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTRDShaderVertex ");
      builder.append(this.inputs);
      builder.append(" ");
      builder.append(this.parameters);
      builder.append(" ");
      builder.append(this.outputs);
      builder.append(" ");
      builder.append(this.values);
      builder.append(" ");
      builder.append(this.writes);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class UASTRDShaderVertexInput extends
    UASTRDShaderVertexParameters
  {
    private final @Nonnull UASTRTermNameLocal name;

    public UASTRDShaderVertexInput(
      final @Nonnull UASTRTermNameLocal name,
      final @Nonnull UASTRTypeName type)
      throws ConstraintError
    {
      super(type);
      this.name = Constraints.constrainNotNull(name, "Name");
    }

    public @Nonnull UASTRTermNameLocal getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTRDShaderVertexInput ");
      builder.append(this.name.show());
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class UASTRDShaderVertexLocalValue extends
    UASTRDeclarationShaderLevel
  {
    private final @Nonnull UASTRDValueLocal value;

    public UASTRDShaderVertexLocalValue(
      final @Nonnull UASTRDValueLocal value)
      throws ConstraintError
    {
      this.value = Constraints.constrainNotNull(value, "Value");
    }

    public @Nonnull UASTRDValueLocal getValue()
    {
      return this.value;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTRDShaderVertexLocalValue ");
      builder.append(this.value);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class UASTRDShaderVertexOutput extends
    UASTRDShaderVertexParameters
  {
    private final @Nonnull TokenIdentifierLower name;

    public UASTRDShaderVertexOutput(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTRTypeName type)
      throws ConstraintError
    {
      super(type);
      this.name = Constraints.constrainNotNull(name, "Name");
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTRDShaderVertexOutput ");
      builder.append(this.name.getActual());
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class UASTRDShaderVertexOutputAssignment extends
    UASTRDeclarationShaderLevel
  {
    private final @Nonnull TokenIdentifierLower name;
    private final @Nonnull UASTREVariable       variable;

    public UASTRDShaderVertexOutputAssignment(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTREVariable variable)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.variable = Constraints.constrainNotNull(variable, "Variable");
    }

    public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    public @Nonnull UASTREVariable getVariable()
    {
      return this.variable;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTRDShaderVertexOutputAssignment ");
      builder.append(this.name.getActual());
      builder.append(" ");
      builder.append(this.variable);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class UASTRDShaderVertexParameter extends
    UASTRDShaderVertexParameters
  {
    private final @Nonnull UASTRTermNameLocal name;

    public UASTRDShaderVertexParameter(
      final @Nonnull UASTRTermNameLocal name,
      final @Nonnull UASTRTypeName type)
      throws ConstraintError
    {
      super(type);
      this.name = Constraints.constrainNotNull(name, "Name");
    }

    public @Nonnull UASTRTermNameLocal getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTRDShaderVertexParameter ");
      builder.append(this.name.show());
      builder.append("]");
      return builder.toString();
    }
  }

  public static abstract class UASTRDShaderVertexParameters extends
    UASTRDShaderParameters
  {
    UASTRDShaderVertexParameters(
      final @Nonnull UASTRTypeName type)
      throws ConstraintError
    {
      super(type);
    }
  }

  /**
   * The type of term declarations.
   */

  public static abstract class UASTRDTerm extends UASTRDeclarationModuleLevel
  {
    // Nothing
  }

  /**
   * The type of local term declarations.
   */

  public static abstract class UASTRDTermLocal extends
    UASTRDeclarationLocalLevel
  {
    // Nothing
  }

  /**
   * The type of type declarations.
   */

  public static abstract class UASTRDType extends UASTRDeclarationModuleLevel
  {
    // Nothing
  }

  /**
   * Record declarations.
   */

  public static final class UASTRDTypeRecord extends UASTRDType
  {
    private final @Nonnull List<UASTRDTypeRecordField> fields;
    private final @Nonnull TokenIdentifierLower        name;

    public UASTRDTypeRecord(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull List<UASTRDTypeRecordField> fields)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.fields = Constraints.constrainNotNull(fields, "Fields");
    }

    public @Nonnull List<UASTRDTypeRecordField> getFields()
    {
      return this.fields;
    }

    @Override public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTRDTypeRecord ");
      builder.append(this.name.getActual());
      builder.append(" ");
      builder.append(this.fields);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class UASTRDTypeRecordField
  {
    private final @Nonnull TokenIdentifierLower name;
    private final @Nonnull UASTRTypeName        type;

    public UASTRDTypeRecordField(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTRTypeName type)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.type = Constraints.constrainNotNull(type, "Type");
    }

    public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    public @Nonnull UASTRTypeName getType()
    {
      return this.type;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTRDTypeRecordField ");
      builder.append(this.name.getActual());
      builder.append(" ");
      builder.append(this.type.show());
      builder.append("]");
      return builder.toString();
    }
  }

  /**
   * Value declarations.
   */

  public static final class UASTRDValue extends UASTRDTerm
  {
    private final @Nonnull Option<UASTRTypeName> ascription;
    private final @Nonnull UASTRExpression       expression;
    private final @Nonnull TokenIdentifierLower  name;

    public UASTRDValue(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull Option<UASTRTypeName> ascription,
      final @Nonnull UASTRExpression expression)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.ascription =
        Constraints.constrainNotNull(ascription, "Ascription");
      this.expression =
        Constraints.constrainNotNull(expression, "Expression");
    }

    public @Nonnull Option<UASTRTypeName> getAscription()
    {
      return this.ascription;
    }

    public @Nonnull UASTRExpression getExpression()
    {
      return this.expression;
    }

    @Override public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTRDValue ");
      builder.append(this.name.getActual());
      builder.append(" ");
      builder.append(this.ascription
        .map(new Function<UASTRTypeName, String>() {
          @Override public String call(
            final @Nonnull UASTRTypeName x)
          {
            return x.show();
          }
        }));
      builder.append(" ");
      builder.append(this.expression);
      builder.append("]");
      return builder.toString();
    }
  }

  /**
   * Local value declarations (let).
   */

  public static final class UASTRDValueLocal extends UASTRDTermLocal
  {
    private final @Nonnull Option<UASTRTypeName> ascription;
    private final @Nonnull UASTRExpression       expression;
    private final @Nonnull UASTRTermNameLocal    name;

    public UASTRDValueLocal(
      final @Nonnull UASTRTermNameLocal name,
      final @Nonnull Option<UASTRTypeName> ascription,
      final @Nonnull UASTRExpression expression)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.ascription =
        Constraints.constrainNotNull(ascription, "Ascription");
      this.expression =
        Constraints.constrainNotNull(expression, "Expression");
    }

    public @Nonnull Option<UASTRTypeName> getAscription()
    {
      return this.ascription;
    }

    public @Nonnull UASTRExpression getExpression()
    {
      return this.expression;
    }

    public @Nonnull UASTRTermNameLocal getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTRDValueLocal ");
      builder.append(this.name.show());
      builder.append(" ");
      builder.append(this.ascription
        .map(new Function<UASTRTypeName, String>() {
          @Override public String call(
            final UASTRTypeName x)
          {
            return x.show();
          }
        }));
      builder.append(" ");
      builder.append(this.expression);
      builder.append("]");
      return builder.toString();
    }
  }
}
