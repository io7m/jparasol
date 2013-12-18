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

package com.io7m.jparasol.untyped.ast.unique_binders;

import java.util.List;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Function;
import com.io7m.jaux.functional.Option;
import com.io7m.jparasol.ModulePath;
import com.io7m.jparasol.PackagePath;
import com.io7m.jparasol.lexer.Token.TokenDiscard;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.lexer.Token.TokenIdentifierUpper;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUEVariable;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUName.UASTUNameLocal;

public abstract class UASTUDeclaration
{
  /**
   * The type of local declarations.
   */

  public static abstract class UASTUDeclarationLocalLevel extends
    UASTUDeclaration
  {
    // Nothing
  }

  /**
   * The type of module-level declarations.
   */

  public static abstract class UASTUDeclarationModuleLevel extends
    UASTUDeclaration
  {
    public abstract @Nonnull TokenIdentifierLower getName();
  }

  /**
   * The type of shader-level declarations.
   */

  public static abstract class UASTUDeclarationShaderLevel extends
    UASTUDeclaration
  {
    // Nothing
  }

  /**
   * The type of unit-level declarations.
   */

  public static abstract class UASTUDeclarationUnitLevel extends
    UASTUDeclaration
  {
    // Nothing
  }

  /**
   * The type of function declarations.
   */

  public static abstract class UASTUDFunction extends UASTUDTerm
  {
    // Nothing
  }

  public static final class UASTUDFunctionArgument
  {
    private final @Nonnull UASTUNameLocal name;
    private final @Nonnull UASTUTypePath  type;

    public UASTUDFunctionArgument(
      final @Nonnull UASTUNameLocal name,
      final @Nonnull UASTUTypePath type)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.type = Constraints.constrainNotNull(type, "Type");
    }

    public @Nonnull UASTUNameLocal getName()
    {
      return this.name;
    }

    public @Nonnull UASTUTypePath getType()
    {
      return this.type;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTUDFunctionArgument ");
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

  public static final class UASTUDFunctionDefined extends UASTUDFunction
  {
    private final @Nonnull List<UASTUDFunctionArgument> arguments;
    private final @Nonnull UASTUExpression              body;
    private final @Nonnull TokenIdentifierLower         name;
    private final @Nonnull UASTUTypePath                return_type;

    public UASTUDFunctionDefined(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull List<UASTUDFunctionArgument> arguments,
      final @Nonnull UASTUTypePath return_type,
      final @Nonnull UASTUExpression body)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.arguments = Constraints.constrainNotNull(arguments, "Arguments");
      this.return_type =
        Constraints.constrainNotNull(return_type, "Return type");
      this.body = Constraints.constrainNotNull(body, "Body");
    }

    public @Nonnull List<UASTUDFunctionArgument> getArguments()
    {
      return this.arguments;
    }

    public @Nonnull UASTUExpression getBody()
    {
      return this.body;
    }

    @Override public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    public @Nonnull UASTUTypePath getReturnType()
    {
      return this.return_type;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTUDFunctionDefined ");
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

  public static final class UASTUDFunctionExternal extends UASTUDFunction
  {
    private final @Nonnull List<UASTUDFunctionArgument> arguments;
    private final @Nonnull TokenIdentifierLower         external;
    private final @Nonnull TokenIdentifierLower         name;
    private final @Nonnull UASTUTypePath                return_type;

    public UASTUDFunctionExternal(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull List<UASTUDFunctionArgument> arguments,
      final @Nonnull UASTUTypePath return_type,
      final @Nonnull TokenIdentifierLower external)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.arguments = Constraints.constrainNotNull(arguments, "Arguments");
      this.return_type =
        Constraints.constrainNotNull(return_type, "Return type");
      this.external = Constraints.constrainNotNull(external, "External");
    }

    public @Nonnull List<UASTUDFunctionArgument> getArguments()
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

    public @Nonnull UASTUTypePath getReturnType()
    {
      return this.return_type;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTUDFunctionExternal ");
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

  public static final class UASTUDImport extends UASTUDeclaration
  {
    private final @Nonnull ModulePath                   path;
    private final @Nonnull Option<TokenIdentifierUpper> rename;

    public UASTUDImport(
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
      builder.append("[UASTUDImport ");
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

  public static final class UASTUDModule extends UASTUDeclarationUnitLevel
  {
    private final @Nonnull List<UASTUDeclarationModuleLevel> declarations;
    private final @Nonnull List<UASTUDImport>                imports;
    private final @Nonnull TokenIdentifierUpper              name;

    public UASTUDModule(
      final @Nonnull TokenIdentifierUpper name,
      final @Nonnull List<UASTUDImport> imports,
      final @Nonnull List<UASTUDeclarationModuleLevel> declarations)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.imports = Constraints.constrainNotNull(imports, "Imports");
      this.declarations =
        Constraints.constrainNotNull(declarations, "Declarations");
    }

    public @Nonnull List<UASTUDeclarationModuleLevel> getDeclarations()
    {
      return this.declarations;
    }

    public @Nonnull List<UASTUDImport> getImports()
    {
      return this.imports;
    }

    public @Nonnull TokenIdentifierUpper getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTUDModule ");
      builder.append(this.name.getActual());
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

  public static final class UASTUDPackage extends UASTUDeclarationUnitLevel
  {
    private final @Nonnull PackagePath path;

    public UASTUDPackage(
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
      builder.append("[UASTUDPackage ");
      builder.append(this.path);
      builder.append("]");
      return builder.toString();
    }
  }

  /**
   * The type of shader declarations.
   */

  public static abstract class UASTUDShader extends
    UASTUDeclarationModuleLevel
  {
    private final @Nonnull TokenIdentifierLower name;

    protected UASTUDShader(
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

  public static final class UASTUDShaderFragment extends UASTUDShader
  {
    private final @Nonnull List<UASTUDShaderFragmentInput>            inputs;
    private final @Nonnull List<UASTUDShaderFragmentLocal>            locals;
    private final @Nonnull List<UASTUDShaderFragmentOutput>           outputs;
    private final @Nonnull List<UASTUDShaderFragmentParameter>        parameters;
    private final @Nonnull List<UASTUDShaderFragmentOutputAssignment> writes;

    public UASTUDShaderFragment(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull List<UASTUDShaderFragmentInput> inputs,
      final @Nonnull List<UASTUDShaderFragmentOutput> outputs,
      final @Nonnull List<UASTUDShaderFragmentParameter> parameters,
      final @Nonnull List<UASTUDShaderFragmentLocal> locals,
      final @Nonnull List<UASTUDShaderFragmentOutputAssignment> writes)
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

    public @Nonnull List<UASTUDShaderFragmentInput> getInputs()
    {
      return this.inputs;
    }

    public @Nonnull List<UASTUDShaderFragmentLocal> getLocals()
    {
      return this.locals;
    }

    public @Nonnull List<UASTUDShaderFragmentOutput> getOutputs()
    {
      return this.outputs;
    }

    public @Nonnull List<UASTUDShaderFragmentParameter> getParameters()
    {
      return this.parameters;
    }

    public @Nonnull List<UASTUDShaderFragmentOutputAssignment> getWrites()
    {
      return this.writes;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTUDShaderFragment ");
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

  public static final class UASTUDShaderFragmentInput extends
    UASTUDShaderFragmentParameters
  {
    private final @Nonnull UASTUNameLocal name;

    public UASTUDShaderFragmentInput(
      final @Nonnull UASTUNameLocal name,
      final @Nonnull UASTUTypePath type)
      throws ConstraintError
    {
      super(type);
      this.name = Constraints.constrainNotNull(name, "Name");
    }

    public @Nonnull UASTUNameLocal getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTUDShaderFragmentInput ");
      builder.append(this.name);
      builder.append("]");
      return builder.toString();
    }
  }

  public static abstract class UASTUDShaderFragmentLocal extends
    UASTUDeclarationShaderLevel
  {
    // Nothing
  }

  public static final class UASTUDShaderFragmentLocalDiscard extends
    UASTUDShaderFragmentLocal
  {
    private final @Nonnull TokenDiscard    discard;
    private final @Nonnull UASTUExpression expression;

    public UASTUDShaderFragmentLocalDiscard(
      final TokenDiscard discard,
      final UASTUExpression expression)
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

    public @Nonnull UASTUExpression getExpression()
    {
      return this.expression;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTUDShaderFragmentLocalDiscard ");
      builder.append(this.discard);
      builder.append(" ");
      builder.append(this.expression);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class UASTUDShaderFragmentLocalValue extends
    UASTUDShaderFragmentLocal
  {
    private final @Nonnull UASTUDValueLocal value;

    public UASTUDShaderFragmentLocalValue(
      final @Nonnull UASTUDValueLocal value)
      throws ConstraintError
    {
      this.value = Constraints.constrainNotNull(value, "Value");
    }

    public @Nonnull UASTUDValueLocal getValue()
    {
      return this.value;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTUDShaderFragmentLocalValue ");
      builder.append(this.value);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class UASTUDShaderFragmentOutput extends
    UASTUDShaderFragmentParameters
  {
    private final int                           index;
    private final @Nonnull TokenIdentifierLower name;

    public UASTUDShaderFragmentOutput(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTUTypePath type,
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
      builder.append("[UASTUDShaderFragmentOutput ");
      builder.append(this.name);
      builder.append(" ");
      builder.append(this.index);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class UASTUDShaderFragmentOutputAssignment extends
    UASTUDeclarationShaderLevel
  {
    private final @Nonnull TokenIdentifierLower name;
    private final @Nonnull UASTUEVariable       variable;

    public UASTUDShaderFragmentOutputAssignment(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTUEVariable variable)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.variable = Constraints.constrainNotNull(variable, "Variable");
    }

    public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    public @Nonnull UASTUEVariable getVariable()
    {
      return this.variable;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTUDShaderFragmentOutputAssignment ");
      builder.append(this.name.getActual());
      builder.append(" ");
      builder.append(this.variable);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class UASTUDShaderFragmentParameter extends
    UASTUDShaderFragmentParameters
  {
    private final @Nonnull UASTUNameLocal name;

    public UASTUDShaderFragmentParameter(
      final @Nonnull UASTUNameLocal name,
      final @Nonnull UASTUTypePath type)
      throws ConstraintError
    {
      super(type);
      this.name = Constraints.constrainNotNull(name, "Name");
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTUDShaderFragmentParameter ");
      builder.append(this.name.show());
      builder.append("]");
      return builder.toString();
    }
  }

  public static abstract class UASTUDShaderFragmentParameters extends
    UASTUDShaderParameters
  {
    UASTUDShaderFragmentParameters(
      final @Nonnull UASTUTypePath type)
      throws ConstraintError
    {
      super(type);
    }
  }

  public static abstract class UASTUDShaderParameters extends
    UASTUDeclarationShaderLevel
  {
    private final @Nonnull UASTUTypePath type;

    UASTUDShaderParameters(
      final @Nonnull UASTUTypePath type)
      throws ConstraintError
    {
      this.type = Constraints.constrainNotNull(type, "Type");
    }

    public final @Nonnull UASTUTypePath getType()
    {
      return this.type;
    }
  }

  public static final class UASTUDShaderProgram extends UASTUDShader
  {
    private final @Nonnull UASTUShaderPath fragment_shader;
    private final @Nonnull UASTUShaderPath vertex_shader;

    public UASTUDShaderProgram(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTUShaderPath vertex_shader,
      final @Nonnull UASTUShaderPath fragment_shader)
      throws ConstraintError
    {
      super(name);
      this.vertex_shader =
        Constraints.constrainNotNull(vertex_shader, "Vertex shader");
      this.fragment_shader =
        Constraints.constrainNotNull(fragment_shader, "Fragment shader");
    }

    public @Nonnull UASTUShaderPath getFragmentShader()
    {
      return this.fragment_shader;
    }

    public @Nonnull UASTUShaderPath getVertexShader()
    {
      return this.vertex_shader;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTUDShaderProgram ");
      builder.append(this.fragment_shader.show());
      builder.append(" ");
      builder.append(this.vertex_shader.show());
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class UASTUDShaderVertex extends UASTUDShader
  {
    private final @Nonnull List<UASTUDShaderVertexInput>            inputs;
    private final @Nonnull List<UASTUDShaderVertexOutput>           outputs;
    private final @Nonnull List<UASTUDShaderVertexParameter>        parameters;
    private final @Nonnull List<UASTUDShaderVertexLocalValue>       values;
    private final @Nonnull List<UASTUDShaderVertexOutputAssignment> writes;

    public UASTUDShaderVertex(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull List<UASTUDShaderVertexInput> inputs,
      final @Nonnull List<UASTUDShaderVertexOutput> outputs,
      final @Nonnull List<UASTUDShaderVertexParameter> parameters,
      final @Nonnull List<UASTUDShaderVertexLocalValue> values,
      final @Nonnull List<UASTUDShaderVertexOutputAssignment> writes)
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

    public @Nonnull List<UASTUDShaderVertexInput> getInputs()
    {
      return this.inputs;
    }

    public @Nonnull List<UASTUDShaderVertexOutput> getOutputs()
    {
      return this.outputs;
    }

    public @Nonnull List<UASTUDShaderVertexParameter> getParameters()
    {
      return this.parameters;
    }

    public @Nonnull List<UASTUDShaderVertexLocalValue> getValues()
    {
      return this.values;
    }

    public @Nonnull List<UASTUDShaderVertexOutputAssignment> getWrites()
    {
      return this.writes;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTUDShaderVertex ");
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

  public static final class UASTUDShaderVertexInput extends
    UASTUDShaderVertexParameters
  {
    private final @Nonnull UASTUNameLocal name;

    public UASTUDShaderVertexInput(
      final @Nonnull UASTUNameLocal name,
      final @Nonnull UASTUTypePath type)
      throws ConstraintError
    {
      super(type);
      this.name = Constraints.constrainNotNull(name, "Name");
    }

    public @Nonnull UASTUNameLocal getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTUDShaderVertexInput ");
      builder.append(this.name.show());
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class UASTUDShaderVertexLocalValue extends
    UASTUDeclarationShaderLevel
  {
    private final @Nonnull UASTUDValueLocal value;

    public UASTUDShaderVertexLocalValue(
      final @Nonnull UASTUDValueLocal value)
      throws ConstraintError
    {
      this.value = Constraints.constrainNotNull(value, "Value");
    }

    public @Nonnull UASTUDValueLocal getValue()
    {
      return this.value;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTUDShaderVertexLocalValue ");
      builder.append(this.value);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class UASTUDShaderVertexOutput extends
    UASTUDShaderVertexParameters
  {
    private final @Nonnull TokenIdentifierLower name;

    public UASTUDShaderVertexOutput(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTUTypePath type)
      throws ConstraintError
    {
      super(type);
      this.name = Constraints.constrainNotNull(name, "Name");
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTUDShaderVertexOutput ");
      builder.append(this.name);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class UASTUDShaderVertexOutputAssignment extends
    UASTUDeclarationShaderLevel
  {
    private final @Nonnull TokenIdentifierLower name;
    private final @Nonnull UASTUEVariable       variable;

    public UASTUDShaderVertexOutputAssignment(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTUEVariable variable)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.variable = Constraints.constrainNotNull(variable, "Variable");
    }

    public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    public @Nonnull UASTUEVariable getVariable()
    {
      return this.variable;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTUDShaderVertexOutputAssignment ");
      builder.append(this.name.getActual());
      builder.append(" ");
      builder.append(this.variable);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class UASTUDShaderVertexParameter extends
    UASTUDShaderVertexParameters
  {
    private final @Nonnull UASTUNameLocal name;

    public UASTUDShaderVertexParameter(
      final @Nonnull UASTUNameLocal name,
      final @Nonnull UASTUTypePath type)
      throws ConstraintError
    {
      super(type);
      this.name = Constraints.constrainNotNull(name, "Name");
    }

    public @Nonnull UASTUNameLocal getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTUDShaderVertexParameter ");
      builder.append(this.name.show());
      builder.append("]");
      return builder.toString();
    }
  }

  public static abstract class UASTUDShaderVertexParameters extends
    UASTUDShaderParameters
  {
    UASTUDShaderVertexParameters(
      final @Nonnull UASTUTypePath type)
      throws ConstraintError
    {
      super(type);
    }
  }

  /**
   * The type of term declarations.
   */

  public static abstract class UASTUDTerm extends UASTUDeclarationModuleLevel
  {
    // Nothing
  }

  /**
   * The type of local term declarations.
   */

  public static abstract class UASTUDTermLocal extends
    UASTUDeclarationLocalLevel
  {
    // Nothing
  }

  /**
   * The type of type declarations.
   */

  public static abstract class UASTUDType extends UASTUDeclarationModuleLevel
  {
    // Nothing
  }

  /**
   * Record declarations.
   */

  public static final class UASTUDTypeRecord extends UASTUDType
  {
    private final @Nonnull List<UASTUDTypeRecordField> fields;
    private final @Nonnull TokenIdentifierLower        name;

    public UASTUDTypeRecord(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull List<UASTUDTypeRecordField> fields)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.fields = Constraints.constrainNotNull(fields, "Fields");
    }

    public @Nonnull List<UASTUDTypeRecordField> getFields()
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
      builder.append("[UASTUDTypeRecord ");
      builder.append(this.name.getActual());
      builder.append(" ");
      builder.append(this.fields);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class UASTUDTypeRecordField
  {
    private final @Nonnull TokenIdentifierLower name;
    private final @Nonnull UASTUTypePath        type;

    public UASTUDTypeRecordField(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTUTypePath type)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.type = Constraints.constrainNotNull(type, "Type");
    }

    public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    public @Nonnull UASTUTypePath getType()
    {
      return this.type;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTUDTypeRecordField ");
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

  public static final class UASTUDValue extends UASTUDTerm
  {
    private final @Nonnull Option<UASTUTypePath> ascription;
    private final @Nonnull UASTUExpression       expression;
    private final @Nonnull TokenIdentifierLower  name;

    public UASTUDValue(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull Option<UASTUTypePath> ascription,
      final @Nonnull UASTUExpression expression)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.ascription =
        Constraints.constrainNotNull(ascription, "Ascription");
      this.expression =
        Constraints.constrainNotNull(expression, "Expression");
    }

    public @Nonnull Option<UASTUTypePath> getAscription()
    {
      return this.ascription;
    }

    public @Nonnull UASTUExpression getExpression()
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
      builder.append("[UASTUDValue ");
      builder.append(this.name);
      builder.append(" ");
      builder.append(this.ascription
        .map(new Function<UASTUTypePath, String>() {
          @Override public String call(
            final @Nonnull UASTUTypePath x)
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

  public static final class UASTUDValueLocal extends UASTUDTermLocal
  {
    private final @Nonnull Option<UASTUTypePath> ascription;
    private final @Nonnull UASTUExpression       expression;
    private final @Nonnull UASTUNameLocal        name;

    public UASTUDValueLocal(
      final @Nonnull UASTUNameLocal name,
      final @Nonnull Option<UASTUTypePath> ascription,
      final @Nonnull UASTUExpression expression)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.ascription =
        Constraints.constrainNotNull(ascription, "Ascription");
      this.expression =
        Constraints.constrainNotNull(expression, "Expression");
    }

    public @Nonnull Option<UASTUTypePath> getAscription()
    {
      return this.ascription;
    }

    public @Nonnull UASTUExpression getExpression()
    {
      return this.expression;
    }

    public @Nonnull UASTUNameLocal getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTUDValueLocal ");
      builder.append(this.name.show());
      builder.append(" ");
      builder.append(this.ascription
        .map(new Function<UASTUTypePath, String>() {
          @Override public String call(
            final UASTUTypePath x)
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
