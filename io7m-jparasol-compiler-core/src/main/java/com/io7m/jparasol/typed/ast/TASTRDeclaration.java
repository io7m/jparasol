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

package com.io7m.jparasol.typed.ast;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Option;
import com.io7m.jparasol.ModulePath;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.PackagePath;
import com.io7m.jparasol.lexer.Token.TokenDiscard;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.lexer.Token.TokenIdentifierUpper;
import com.io7m.jparasol.typed.TType;
import com.io7m.jparasol.typed.TType.TFunction;
import com.io7m.jparasol.typed.TType.TManifestType;
import com.io7m.jparasol.typed.TType.TRecord;
import com.io7m.jparasol.typed.TType.TValueType;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTEVariable;
import com.io7m.jparasol.typed.ast.TASTTermName.TASTTermNameLocal;

public abstract class TASTRDeclaration
{
  /**
   * The type of local declarations.
   */

  public static abstract class TASTDeclarationLocalLevel extends
    TASTRDeclaration
  {
    // Nothing
  }

  /**
   * The type of module-level declarations.
   */

  public static abstract class TASTDeclarationModuleLevel extends
    TASTRDeclaration
  {
    public abstract @Nonnull TokenIdentifierLower getName();
  }

  /**
   * The type of shader-level declarations.
   */

  public static abstract class TASTDeclarationShaderLevel extends
    TASTRDeclaration
  {
    // Nothing
  }

  /**
   * The type of unit-level declarations.
   */

  public static abstract class TASTDeclarationUnitLevel extends
    TASTRDeclaration
  {
    // Nothing
  }

  public static final class TASTDExternal
  {
    private final boolean                       fragment_shader_allowed;
    private final @Nonnull TokenIdentifierLower name;
    private final boolean                       vertex_shader_allowed;

    public TASTDExternal(
      final @Nonnull TokenIdentifierLower name,
      final boolean vertex_shader_allowed,
      final boolean fragment_shader_allowed)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.vertex_shader_allowed = vertex_shader_allowed;
      this.fragment_shader_allowed = fragment_shader_allowed;
    }

    public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    public boolean isFragmentShaderAllowed()
    {
      return this.fragment_shader_allowed;
    }

    public boolean isVertexShaderAllowed()
    {
      return this.vertex_shader_allowed;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTDExternal ");
      builder.append(this.name);
      builder.append(" ");
      builder.append(this.vertex_shader_allowed);
      builder.append(" ");
      builder.append(this.fragment_shader_allowed);
      builder.append("]");
      return builder.toString();
    }
  }

  /**
   * The type of function declarations.
   */

  public static abstract class TASTDFunction extends TASTDTerm
  {
    @Override public abstract @Nonnull TFunction getType();
  }

  public static final class TASTDFunctionArgument
  {
    private final @Nonnull TASTTermNameLocal name;
    private final @Nonnull TValueType        type;

    public TASTDFunctionArgument(
      final @Nonnull TASTTermNameLocal name,
      final @Nonnull TValueType type)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.type = Constraints.constrainNotNull(type, "Type");
    }

    public @Nonnull TASTTermNameLocal getName()
    {
      return this.name;
    }

    public @Nonnull TValueType getType()
    {
      return this.type;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTDFunctionArgument ");
      builder.append(this.name.show());
      builder.append(" ");
      builder.append(this.type.getName());
      builder.append("]");
      return builder.toString();
    }
  }

  /**
   * Fully defined functions.
   */

  public static final class TASTDFunctionDefined extends TASTDFunction
  {
    private final @Nonnull List<TASTDFunctionArgument> arguments;
    private final @Nonnull TASTExpression              body;
    private final @Nonnull TokenIdentifierLower        name;
    private final @Nonnull TFunction                   type;

    public TASTDFunctionDefined(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull List<TASTDFunctionArgument> arguments,
      final @Nonnull TASTExpression body,
      final @Nonnull TFunction type)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.arguments = Constraints.constrainNotNull(arguments, "Arguments");
      this.body = Constraints.constrainNotNull(body, "Body");
      this.type = Constraints.constrainNotNull(type, "Type");

      assert body.getType().equals(type.getReturnType());
    }

    public @Nonnull List<TASTDFunctionArgument> getArguments()
    {
      return this.arguments;
    }

    public @Nonnull TASTExpression getBody()
    {
      return this.body;
    }

    @Override public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    public @Nonnull TType getReturnType()
    {
      return this.body.getType();
    }

    @Override public @Nonnull TFunction getType()
    {
      return this.type;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTDFunctionDefined ");
      builder.append(this.name.getActual());
      builder.append(" ");
      builder.append(this.arguments);
      builder.append(" ");
      builder.append(this.body);
      builder.append("]");
      return builder.toString();
    }
  }

  /**
   * Functions with external declarations (private FFI).
   */

  public static final class TASTDFunctionExternal extends TASTDFunction
  {
    private final @Nonnull List<TASTDFunctionArgument> arguments;
    private final @Nonnull TASTDExternal               external;
    private final @Nonnull TokenIdentifierLower        name;
    private final @Nonnull TFunction                   type;

    public TASTDFunctionExternal(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull List<TASTDFunctionArgument> arguments,
      final @Nonnull TFunction type,
      final @Nonnull TASTDExternal external)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.arguments = Constraints.constrainNotNull(arguments, "Arguments");
      this.type = Constraints.constrainNotNull(type, "Type");
      this.external = Constraints.constrainNotNull(external, "External");
    }

    public @Nonnull List<TASTDFunctionArgument> getArguments()
    {
      return this.arguments;
    }

    public @Nonnull TASTDExternal getExternal()
    {
      return this.external;
    }

    @Override public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    @Override public @Nonnull TFunction getType()
    {
      return this.type;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTDFunctionExternal ");
      builder.append(this.name.getActual());
      builder.append(" ");
      builder.append(this.arguments);
      builder.append(" ");
      builder.append(this.type.getName());
      builder.append(" ");
      builder.append(this.external);
      builder.append("]");
      return builder.toString();
    }
  }

  /**
   * Import declarations.
   */

  public static final class TASTDImport extends TASTRDeclaration
  {
    private final @Nonnull ModulePath                   path;
    private final @Nonnull Option<TokenIdentifierUpper> rename;

    public TASTDImport(
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
      builder.append("[TASTDImport ");
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

  public static final class TASTDModule extends TASTDeclarationUnitLevel
  {
    private final @Nonnull List<TASTDeclarationModuleLevel> declarations;
    private final @Nonnull Map<ModulePathFlat, TASTDImport> imported_modules;
    private final @Nonnull Map<String, TASTDImport>         imported_names;
    private final @Nonnull Map<String, TASTDImport>         imported_renames;
    private final @Nonnull List<TASTDImport>                imports;
    private final @Nonnull ModulePath                       path;
    private final @Nonnull List<String>                     shader_topology;
    private final @Nonnull Map<String, TASTDShader>         shaders;
    private final @Nonnull List<String>                     term_topology;
    private final @Nonnull Map<String, TASTDTerm>           terms;
    private final @Nonnull List<String>                     type_topology;
    private final @Nonnull Map<String, TASTDType>           types;

    public TASTDModule(
      final @Nonnull ModulePath path,
      final @Nonnull List<TASTDImport> imports,
      final @Nonnull Map<ModulePathFlat, TASTDImport> imported_modules,
      final @Nonnull Map<String, TASTDImport> imported_names,
      final @Nonnull Map<String, TASTDImport> imported_renames,
      final @Nonnull List<TASTDeclarationModuleLevel> declarations,
      final @Nonnull Map<String, TASTDTerm> terms,
      final @Nonnull List<String> term_topology,
      final @Nonnull Map<String, TASTDType> types,
      final @Nonnull List<String> type_topology,
      final @Nonnull Map<String, TASTDShader> shaders,
      final @Nonnull List<String> shader_topology)
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
      this.term_topology =
        Constraints.constrainNotNull(term_topology, "Term topology");
      this.types = Constraints.constrainNotNull(types, "Types");
      this.type_topology =
        Constraints.constrainNotNull(type_topology, "Type topology");
      this.shaders = Constraints.constrainNotNull(shaders, "Shaders");
      this.shader_topology =
        Constraints.constrainNotNull(shader_topology, "Shader topology");
    }

    public @Nonnull List<TASTDeclarationModuleLevel> getDeclarations()
    {
      return this.declarations;
    }

    public @Nonnull List<TASTDImport> getImports()
    {
      return this.imports;
    }

    public @Nonnull List<String> getShaderTopology()
    {
      return this.shader_topology;
    }

    public @Nonnull ModulePath getPath()
    {
      return this.path;
    }

    public @Nonnull Map<String, TASTDShader> getShaders()
    {
      return this.shaders;
    }

    public @Nonnull Map<String, TASTDTerm> getTerms()
    {
      return this.terms;
    }

    public @Nonnull Map<String, TASTDType> getTypes()
    {
      return this.types;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTDModule ");
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

  public static final class TASTDPackage extends TASTDeclarationUnitLevel
  {
    private final @Nonnull PackagePath path;

    public TASTDPackage(
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
      builder.append("[TASTDPackage ");
      builder.append(this.path);
      builder.append("]");
      return builder.toString();
    }
  }

  /**
   * The type of shader declarations.
   */

  public static abstract class TASTDShader extends TASTDeclarationModuleLevel implements
    TASTShaderVisitable
  {
    private final @Nonnull TokenIdentifierLower name;

    protected TASTDShader(
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

  public static final class TASTDShaderFragment extends TASTDShader
  {
    private final @Nonnull List<TASTDShaderFragmentInput>            inputs;
    private final @Nonnull List<TASTDShaderFragmentLocal>            locals;
    private final @Nonnull List<TASTDShaderFragmentOutput>           outputs;
    private final @Nonnull List<TASTDShaderFragmentParameter>        parameters;
    private final @Nonnull List<TASTDShaderFragmentOutputAssignment> writes;

    public TASTDShaderFragment(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull List<TASTDShaderFragmentInput> inputs,
      final @Nonnull List<TASTDShaderFragmentOutput> outputs,
      final @Nonnull List<TASTDShaderFragmentParameter> parameters,
      final @Nonnull List<TASTDShaderFragmentLocal> locals,
      final @Nonnull List<TASTDShaderFragmentOutputAssignment> writes)
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

    public @Nonnull List<TASTDShaderFragmentInput> getInputs()
    {
      return this.inputs;
    }

    public @Nonnull List<TASTDShaderFragmentLocal> getLocals()
    {
      return this.locals;
    }

    public @Nonnull List<TASTDShaderFragmentOutput> getOutputs()
    {
      return this.outputs;
    }

    public @Nonnull List<TASTDShaderFragmentParameter> getParameters()
    {
      return this.parameters;
    }

    public @Nonnull List<TASTDShaderFragmentOutputAssignment> getWrites()
    {
      return this.writes;
    }

    @Override public
      <T, E extends Throwable, V extends TASTShaderVisitor<T, E>>
      T
      shaderVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.moduleVisitFragmentShader(this);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTDShaderFragment ");
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

  public static final class TASTDShaderFragmentInput extends
    TASTDShaderFragmentParameters
  {
    private final @Nonnull TASTTermNameLocal name;

    public TASTDShaderFragmentInput(
      final @Nonnull TASTTermNameLocal name,
      final @Nonnull TValueType type)
      throws ConstraintError
    {
      super(type);
      this.name = Constraints.constrainNotNull(name, "Name");
    }

    public @Nonnull TASTTermNameLocal getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTDShaderFragmentInput ");
      builder.append(this.name.show());
      builder.append("]");
      return builder.toString();
    }
  }

  public static abstract class TASTDShaderFragmentLocal extends
    TASTDeclarationShaderLevel
  {
    // Nothing
  }

  public static final class TASTDShaderFragmentLocalDiscard extends
    TASTDShaderFragmentLocal
  {
    private final @Nonnull TokenDiscard   discard;
    private final @Nonnull TASTExpression expression;

    public TASTDShaderFragmentLocalDiscard(
      final TokenDiscard discard,
      final TASTExpression expression)
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

    public @Nonnull TASTExpression getExpression()
    {
      return this.expression;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTDShaderFragmentLocalDiscard ");
      builder.append(this.expression);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class TASTDShaderFragmentLocalValue extends
    TASTDShaderFragmentLocal
  {
    private final @Nonnull TASTDValueLocal value;

    public TASTDShaderFragmentLocalValue(
      final @Nonnull TASTDValueLocal value)
      throws ConstraintError
    {
      this.value = Constraints.constrainNotNull(value, "Value");
    }

    public @Nonnull TASTDValueLocal getValue()
    {
      return this.value;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTDShaderFragmentLocalValue ");
      builder.append(this.value);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class TASTDShaderFragmentOutput extends
    TASTDShaderFragmentParameters
  {
    private final int                           index;
    private final @Nonnull TokenIdentifierLower name;

    public TASTDShaderFragmentOutput(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull TValueType type,
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
      builder.append("[TASTDShaderFragmentOutput ");
      builder.append(this.name.getActual());
      builder.append(" ");
      builder.append(this.index);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class TASTDShaderFragmentOutputAssignment extends
    TASTDeclarationShaderLevel
  {
    private final @Nonnull TokenIdentifierLower name;
    private final @Nonnull TASTEVariable        variable;

    public TASTDShaderFragmentOutputAssignment(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull TASTEVariable variable)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.variable = Constraints.constrainNotNull(variable, "Variable");
    }

    public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    public @Nonnull TASTEVariable getVariable()
    {
      return this.variable;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTDShaderFragmentOutputAssignment ");
      builder.append(this.name.getActual());
      builder.append(" ");
      builder.append(this.variable);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class TASTDShaderFragmentParameter extends
    TASTDShaderFragmentParameters
  {
    private final @Nonnull TASTTermNameLocal name;

    public TASTDShaderFragmentParameter(
      final @Nonnull TASTTermNameLocal name,
      final @Nonnull TValueType type)
      throws ConstraintError
    {
      super(type);
      this.name = Constraints.constrainNotNull(name, "Name");
    }

    public @Nonnull TASTTermNameLocal getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTDShaderFragmentParameter ");
      builder.append(this.name.show());
      builder.append("]");
      return builder.toString();
    }
  }

  public static abstract class TASTDShaderFragmentParameters extends
    TASTDShaderParameters
  {
    TASTDShaderFragmentParameters(
      final @Nonnull TValueType type)
      throws ConstraintError
    {
      super(type);
    }
  }

  public static abstract class TASTDShaderParameters extends
    TASTDeclarationShaderLevel
  {
    private final @Nonnull TValueType type;

    TASTDShaderParameters(
      final @Nonnull TValueType type)
      throws ConstraintError
    {
      this.type = Constraints.constrainNotNull(type, "Type");
    }

    public final @Nonnull TValueType getType()
    {
      return this.type;
    }
  }

  public static final class TASTDShaderProgram extends TASTDShader
  {
    private final @Nonnull TASTShaderName fragment_shader;
    private final @Nonnull TASTShaderName vertex_shader;

    public TASTDShaderProgram(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull TASTShaderName vertex_shader,
      final @Nonnull TASTShaderName fragment_shader)
      throws ConstraintError
    {
      super(name);
      this.vertex_shader =
        Constraints.constrainNotNull(vertex_shader, "Vertex shader");
      this.fragment_shader =
        Constraints.constrainNotNull(fragment_shader, "Fragment shader");
    }

    public @Nonnull TASTShaderName getFragmentShader()
    {
      return this.fragment_shader;
    }

    public @Nonnull TASTShaderName getVertexShader()
    {
      return this.vertex_shader;
    }

    @Override public
      <T, E extends Throwable, V extends TASTShaderVisitor<T, E>>
      T
      shaderVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.moduleVisitProgramShader(this);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTDShaderProgram ");
      builder.append(this.fragment_shader.show());
      builder.append(" ");
      builder.append(this.vertex_shader.show());
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class TASTDShaderVertex extends TASTDShader
  {
    private final @Nonnull List<TASTDShaderVertexInput>            inputs;
    private final @Nonnull List<TASTDShaderVertexOutput>           outputs;
    private final @Nonnull List<TASTDShaderVertexParameter>        parameters;
    private final @Nonnull List<TASTDShaderVertexLocalValue>       values;
    private final @Nonnull List<TASTDShaderVertexOutputAssignment> writes;

    public TASTDShaderVertex(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull List<TASTDShaderVertexInput> inputs,
      final @Nonnull List<TASTDShaderVertexOutput> outputs,
      final @Nonnull List<TASTDShaderVertexParameter> parameters,
      final @Nonnull List<TASTDShaderVertexLocalValue> values,
      final @Nonnull List<TASTDShaderVertexOutputAssignment> writes)
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

    public @Nonnull List<TASTDShaderVertexInput> getInputs()
    {
      return this.inputs;
    }

    public @Nonnull List<TASTDShaderVertexOutput> getOutputs()
    {
      return this.outputs;
    }

    public @Nonnull List<TASTDShaderVertexParameter> getParameters()
    {
      return this.parameters;
    }

    public @Nonnull List<TASTDShaderVertexLocalValue> getValues()
    {
      return this.values;
    }

    public @Nonnull List<TASTDShaderVertexOutputAssignment> getWrites()
    {
      return this.writes;
    }

    @Override public
      <T, E extends Throwable, V extends TASTShaderVisitor<T, E>>
      T
      shaderVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.moduleVisitVertexShader(this);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTDShaderVertex ");
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

  public static final class TASTDShaderVertexInput extends
    TASTDShaderVertexParameters
  {
    private final @Nonnull TASTTermNameLocal name;

    public TASTDShaderVertexInput(
      final @Nonnull TASTTermNameLocal name,
      final @Nonnull TValueType type)
      throws ConstraintError
    {
      super(type);
      this.name = Constraints.constrainNotNull(name, "Name");
    }

    public @Nonnull TASTTermNameLocal getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTDShaderVertexInput ");
      builder.append(this.name.show());
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class TASTDShaderVertexLocalValue extends
    TASTDeclarationShaderLevel
  {
    private final @Nonnull TASTDValueLocal value;

    public TASTDShaderVertexLocalValue(
      final @Nonnull TASTDValueLocal value)
      throws ConstraintError
    {
      this.value = Constraints.constrainNotNull(value, "Value");
    }

    public @Nonnull TASTDValueLocal getValue()
    {
      return this.value;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTDShaderVertexLocalValue ");
      builder.append(this.value);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class TASTDShaderVertexOutput extends
    TASTDShaderVertexParameters
  {
    private final @Nonnull TokenIdentifierLower name;

    public TASTDShaderVertexOutput(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull TValueType type)
      throws ConstraintError
    {
      super(type);
      this.name = Constraints.constrainNotNull(name, "Name");
    }

    public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTDShaderVertexOutput ");
      builder.append(this.name.getActual());
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class TASTDShaderVertexOutputAssignment extends
    TASTDeclarationShaderLevel
  {
    private final @Nonnull TokenIdentifierLower name;
    private final @Nonnull TASTEVariable        variable;

    public TASTDShaderVertexOutputAssignment(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull TASTEVariable variable)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.variable = Constraints.constrainNotNull(variable, "Variable");
    }

    public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    public @Nonnull TASTEVariable getVariable()
    {
      return this.variable;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTDShaderVertexOutputAssignment ");
      builder.append(this.name.getActual());
      builder.append(" ");
      builder.append(this.variable);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class TASTDShaderVertexParameter extends
    TASTDShaderVertexParameters
  {
    private final @Nonnull TASTTermNameLocal name;

    public TASTDShaderVertexParameter(
      final @Nonnull TASTTermNameLocal name,
      final @Nonnull TValueType type)
      throws ConstraintError
    {
      super(type);
      this.name = Constraints.constrainNotNull(name, "Name");
    }

    public @Nonnull TASTTermNameLocal getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTDShaderVertexParameter ");
      builder.append(this.name.show());
      builder.append("]");
      return builder.toString();
    }
  }

  public static abstract class TASTDShaderVertexParameters extends
    TASTDShaderParameters
  {
    TASTDShaderVertexParameters(
      final @Nonnull TValueType type)
      throws ConstraintError
    {
      super(type);
    }
  }

  /**
   * The type of term declarations.
   */

  public static abstract class TASTDTerm extends TASTDeclarationModuleLevel
  {
    public abstract @Nonnull TType getType();
  }

  /**
   * The type of local term declarations.
   */

  public static abstract class TASTDTermLocal extends
    TASTDeclarationLocalLevel
  {
    // Nothing
  }

  /**
   * The type of type declarations.
   */

  public static abstract class TASTDType extends TASTDeclarationModuleLevel
  {
    public abstract @Nonnull TType getType();
  }

  /**
   * Record declarations.
   */

  public static final class TASTDTypeRecord extends TASTDType
  {
    private final @Nonnull List<TASTDTypeRecordField> fields;
    private final @Nonnull TokenIdentifierLower       name;
    private final @Nonnull TRecord                    type;

    public TASTDTypeRecord(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull List<TASTDTypeRecordField> fields,
      final @Nonnull TRecord type)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.type = Constraints.constrainNotNull(type, "Type");
      this.fields = Constraints.constrainNotNull(fields, "Fields");
    }

    public @Nonnull List<TASTDTypeRecordField> getFields()
    {
      return this.fields;
    }

    @Override public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    @Override public @Nonnull TRecord getType()
    {
      return this.type;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTDTypeRecord ");
      builder.append(this.name.getActual());
      builder.append(" ");
      builder.append(this.fields);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class TASTDTypeRecordField
  {
    private final @Nonnull TokenIdentifierLower name;
    private final @Nonnull TManifestType        type;

    public TASTDTypeRecordField(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull TManifestType type)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.type = Constraints.constrainNotNull(type, "Type");
    }

    public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    public @Nonnull TManifestType getType()
    {
      return this.type;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTDTypeRecordField ");
      builder.append(this.name.getActual());
      builder.append(" ");
      builder.append(this.type.getName());
      builder.append("]");
      return builder.toString();
    }
  }

  /**
   * Value declarations.
   */

  public static final class TASTDValue extends TASTDTerm
  {
    private final @Nonnull TASTExpression       expression;
    private final @Nonnull TokenIdentifierLower name;

    public TASTDValue(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull TASTExpression expression)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.expression =
        Constraints.constrainNotNull(expression, "Expression");
    }

    public @Nonnull TASTExpression getExpression()
    {
      return this.expression;
    }

    @Override public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    @Override public TType getType()
    {
      return this.expression.getType();
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTDValue ");
      builder.append(this.name.getActual());
      builder.append(" ");
      builder.append(this.expression);
      builder.append("]");
      return builder.toString();
    }
  }

  /**
   * Local value declarations (let).
   */

  public static final class TASTDValueLocal extends TASTDTermLocal
  {
    private final @Nonnull TASTExpression    expression;
    private final @Nonnull TASTTermNameLocal name;

    public TASTDValueLocal(
      final @Nonnull TASTTermNameLocal name,
      final @Nonnull TASTExpression expression)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.expression =
        Constraints.constrainNotNull(expression, "Expression");
    }

    public @Nonnull TASTExpression getExpression()
    {
      return this.expression;
    }

    public @Nonnull TASTTermNameLocal getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTDValueLocal ");
      builder.append(this.name.show());
      builder.append(" ");
      builder.append(this.expression);
      builder.append("]");
      return builder.toString();
    }
  }
}
