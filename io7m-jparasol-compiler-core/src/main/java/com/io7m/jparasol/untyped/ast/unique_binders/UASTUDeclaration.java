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
    UASTUDeclaration implements UASTULocalLevelVisitable
  {
    public abstract @Nonnull TokenIdentifierLower getName();
  }

  /**
   * The type of module-level declarations.
   */

  public static abstract class UASTUDeclarationModuleLevel extends
    UASTUDeclaration implements UASTUModuleLevelVisitable
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
    UASTUDeclaration implements UASTUUnitLevelVisitable
  {
    // Nothing
  }

  /**
   * The type of function declarations.
   */

  public static abstract class UASTUDFunction extends UASTUDTerm implements
    UASTUFunctionVisitable
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

    @Override public
      <E extends Throwable, V extends UASTUFunctionVisitor<E>>
      void
      functionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.functionVisitDefined(this);
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

    @Override public
      <E extends Throwable, V extends UASTUModuleLevelVisitor<E>>
      void
      moduleVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.moduleVisitFunctionDefined(this);
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

    @Override public
      <E extends Throwable, V extends UASTUFunctionVisitor<E>>
      void
      functionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.functionVisitExternal(this);
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

    @Override public
      <E extends Throwable, V extends UASTUModuleLevelVisitor<E>>
      void
      moduleVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.moduleVisitFunctionExternal(this);
    }
  }

  /**
   * Import declarations.
   */

  public static final class UASTUDImport extends UASTUDeclaration implements
    UASTUModuleLevelVisitable
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

    @Override public
      <E extends Throwable, V extends UASTUModuleLevelVisitor<E>>
      void
      moduleVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.moduleVisitImport(this);
    }
  }

  /**
   * Module declarations.
   */

  public static final class UASTUDModule extends UASTUDeclarationUnitLevel implements
    UASTUModuleLevelVisitable
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

    @Override public
      <E extends Throwable, V extends UASTUModuleLevelVisitor<E>>
      void
      moduleVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.moduleVisitModule(this);
      for (final UASTUDImport i : this.imports) {
        i.moduleVisitableAccept(v);
      }
      for (final UASTUDeclarationModuleLevel d : this.declarations) {
        d.moduleVisitableAccept(v);
      }
    }

    @Override public
      <E extends Throwable, V extends UASTUUnitLevelVisitor<E>>
      void
      unitVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.unitVisitModule(this);
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

    @Override public
      <E extends Throwable, V extends UASTUUnitLevelVisitor<E>>
      void
      unitVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.unitVisitPackage(this);
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

  public static final class UASTUDShaderFragment extends UASTUDShader implements
    UASTUFragmentShaderVisitable
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

    @Override public
      <E extends Throwable, V extends UASTUFragmentShaderVisitor<E>>
      void
      fragmentShaderVisitableAccept(
        final V v)
        throws E,
          ConstraintError
    {
      v.fragmentShaderVisit(this);

      for (final UASTUDShaderFragmentInput i : this.inputs) {
        i.fragmentShaderVisitableAccept(v);
      }
      for (final UASTUDShaderFragmentOutput o : this.outputs) {
        o.fragmentShaderVisitableAccept(v);
      }
      for (final UASTUDShaderFragmentParameter p : this.parameters) {
        p.fragmentShaderVisitableAccept(v);
      }
      for (final UASTUDShaderFragmentLocal l : this.locals) {
        l.fragmentShaderVisitableAccept(v);
      }
      for (final UASTUDShaderFragmentOutputAssignment w : this.writes) {
        w.fragmentShaderVisitableAccept(v);
      }
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

    @Override public
      <E extends Throwable, V extends UASTUModuleLevelVisitor<E>>
      void
      moduleVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.moduleVisitFragmentShader(this);
    }
  }

  public static final class UASTUDShaderFragmentInput extends
    UASTUDShaderFragmentParameters
  {
    public UASTUDShaderFragmentInput(
      final @Nonnull UASTUNameLocal name,
      final @Nonnull UASTUTypePath type)
      throws ConstraintError
    {
      super(name, type);
    }

    @Override public
      <E extends Throwable, V extends UASTUFragmentShaderVisitor<E>>
      void
      fragmentShaderVisitableAccept(
        final V v)
        throws E,
          ConstraintError
    {
      v.fragmentShaderVisitInput(this);
    }
  }

  public static abstract class UASTUDShaderFragmentLocal extends
    UASTUDeclarationShaderLevel implements UASTUFragmentShaderVisitable
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

    @Override public
      <E extends Throwable, V extends UASTUFragmentShaderVisitor<E>>
      void
      fragmentShaderVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.fragmentShaderVisitLocalDiscard(this);
    }

    public @Nonnull TokenDiscard getDiscard()
    {
      return this.discard;
    }

    public @Nonnull UASTUExpression getExpression()
    {
      return this.expression;
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

    @Override public
      <E extends Throwable, V extends UASTUFragmentShaderVisitor<E>>
      void
      fragmentShaderVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.fragmentShaderVisitLocalValue(this);
    }

    public @Nonnull UASTUDValueLocal getValue()
    {
      return this.value;
    }
  }

  public static final class UASTUDShaderFragmentOutput extends
    UASTUDShaderFragmentParameters
  {
    private final int index;

    public UASTUDShaderFragmentOutput(
      final @Nonnull UASTUNameLocal name,
      final @Nonnull UASTUTypePath type,
      final int index)
      throws ConstraintError
    {
      super(name, type);
      this.index = index;
    }

    @Override public
      <E extends Throwable, V extends UASTUFragmentShaderVisitor<E>>
      void
      fragmentShaderVisitableAccept(
        final V v)
        throws E,
          ConstraintError
    {
      v.fragmentShaderVisitOutput(this);
    }

    public int getIndex()
    {
      return this.index;
    }
  }

  public static final class UASTUDShaderFragmentOutputAssignment extends
    UASTUDeclarationShaderLevel implements UASTUFragmentShaderVisitable
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

    @Override public
      <E extends Throwable, V extends UASTUFragmentShaderVisitor<E>>
      void
      fragmentShaderVisitableAccept(
        final V v)
        throws E,
          ConstraintError
    {
      v.fragmentShaderVisitOutputAssignment(this);
    }

    public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    public @Nonnull UASTUEVariable getVariable()
    {
      return this.variable;
    }
  }

  public static final class UASTUDShaderFragmentParameter extends
    UASTUDShaderFragmentParameters
  {
    public UASTUDShaderFragmentParameter(
      final @Nonnull UASTUNameLocal name,
      final @Nonnull UASTUTypePath type)
      throws ConstraintError
    {
      super(name, type);
    }

    @Override public
      <E extends Throwable, V extends UASTUFragmentShaderVisitor<E>>
      void
      fragmentShaderVisitableAccept(
        final V v)
        throws E,
          ConstraintError
    {
      v.fragmentShaderVisitParameter(this);
    }
  }

  public static abstract class UASTUDShaderFragmentParameters extends
    UASTUDShaderParameters implements UASTUFragmentShaderVisitable
  {
    UASTUDShaderFragmentParameters(
      final @Nonnull UASTUNameLocal name,
      final @Nonnull UASTUTypePath type)
      throws ConstraintError
    {
      super(name, type);
    }
  }

  public static abstract class UASTUDShaderParameters extends
    UASTUDeclarationShaderLevel
  {
    private final @Nonnull UASTUNameLocal name;
    private final @Nonnull UASTUTypePath  type;

    UASTUDShaderParameters(
      final @Nonnull UASTUNameLocal name,
      final @Nonnull UASTUTypePath type)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.type = Constraints.constrainNotNull(type, "Type");
    }

    public final @Nonnull UASTUNameLocal getName()
    {
      return this.name;
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

    @Override public
      <E extends Throwable, V extends UASTUModuleLevelVisitor<E>>
      void
      moduleVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.moduleVisitProgramShader(this);
    }
  }

  public static final class UASTUDShaderVertex extends UASTUDShader implements
    UASTUVertexShaderVisitable
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

    @Override public
      <E extends Throwable, V extends UASTUModuleLevelVisitor<E>>
      void
      moduleVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.moduleVisitVertexShader(this);
    }

    @Override public
      <E extends Throwable, V extends UASTUVertexShaderVisitor<E>>
      void
      vertexShaderVisitableAccept(
        final V v)
        throws E,
          ConstraintError
    {
      v.vertexShaderVisit(this);

      for (final UASTUDShaderVertexInput i : this.inputs) {
        i.vertexShaderVisitableAccept(v);
      }
      for (final UASTUDShaderVertexOutput o : this.outputs) {
        o.vertexShaderVisitableAccept(v);
      }
      for (final UASTUDShaderVertexParameter p : this.parameters) {
        p.vertexShaderVisitableAccept(v);
      }
      for (final UASTUDShaderVertexLocalValue l : this.values) {
        l.vertexShaderVisitableAccept(v);
      }
      for (final UASTUDShaderVertexOutputAssignment w : this.writes) {
        w.vertexShaderVisitableAccept(v);
      }
    }
  }

  public static final class UASTUDShaderVertexInput extends
    UASTUDShaderVertexParameters
  {
    public UASTUDShaderVertexInput(
      final @Nonnull UASTUNameLocal name,
      final @Nonnull UASTUTypePath type)
      throws ConstraintError
    {
      super(name, type);
    }

    @Override public
      <E extends Throwable, V extends UASTUVertexShaderVisitor<E>>
      void
      vertexShaderVisitableAccept(
        final V v)
        throws E,
          ConstraintError
    {
      v.vertexShaderVisitInput(this);
    }
  }

  public static final class UASTUDShaderVertexLocalValue extends
    UASTUDeclarationShaderLevel implements UASTUVertexShaderVisitable
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

    @Override public
      <E extends Throwable, V extends UASTUVertexShaderVisitor<E>>
      void
      vertexShaderVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.vertexShaderVisitLocalValue(this);
    }
  }

  public static final class UASTUDShaderVertexOutput extends
    UASTUDShaderVertexParameters
  {
    public UASTUDShaderVertexOutput(
      final @Nonnull UASTUNameLocal name,
      final @Nonnull UASTUTypePath type)
      throws ConstraintError
    {
      super(name, type);
    }

    @Override public
      <E extends Throwable, V extends UASTUVertexShaderVisitor<E>>
      void
      vertexShaderVisitableAccept(
        final V v)
        throws E,
          ConstraintError
    {
      v.vertexShaderVisitOutput(this);
    }
  }

  public static final class UASTUDShaderVertexOutputAssignment extends
    UASTUDeclarationShaderLevel implements UASTUVertexShaderVisitable
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

    @Override public
      <E extends Throwable, V extends UASTUVertexShaderVisitor<E>>
      void
      vertexShaderVisitableAccept(
        final V v)
        throws E,
          ConstraintError
    {
      v.vertexShaderVisitOutputAssignment(this);
    }
  }

  public static final class UASTUDShaderVertexParameter extends
    UASTUDShaderVertexParameters
  {
    public UASTUDShaderVertexParameter(
      final @Nonnull UASTUNameLocal name,
      final @Nonnull UASTUTypePath type)
      throws ConstraintError
    {
      super(name, type);
    }

    @Override public
      <E extends Throwable, V extends UASTUVertexShaderVisitor<E>>
      void
      vertexShaderVisitableAccept(
        final V v)
        throws E,
          ConstraintError
    {
      v.vertexShaderVisitParameter(this);
    }
  }

  public static abstract class UASTUDShaderVertexParameters extends
    UASTUDShaderParameters implements UASTUVertexShaderVisitable
  {
    UASTUDShaderVertexParameters(
      final @Nonnull UASTUNameLocal name,
      final @Nonnull UASTUTypePath type)
      throws ConstraintError
    {
      super(name, type);
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

  public static final class UASTUDTypeRecord extends UASTUDType implements
    UASTUDRecordVisitable
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

    @Override public
      <E extends Throwable, V extends UASTUModuleLevelVisitor<E>>
      void
      moduleVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.moduleVisitTypeRecord(this);
    }

    @Override public
      <E extends Throwable, V extends UASTUDRecordVisitor<E>>
      void
      recordTypeVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.recordTypeVisit(this);
      for (final UASTUDTypeRecordField f : this.fields) {
        f.recordTypeVisitableAccept(v);
      }
    }
  }

  public static final class UASTUDTypeRecordField implements
    UASTUDRecordVisitable
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

    @Override public
      <E extends Throwable, V extends UASTUDRecordVisitor<E>>
      void
      recordTypeVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.recordTypeVisitField(this);
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

    @Override public
      <E extends Throwable, V extends UASTUModuleLevelVisitor<E>>
      void
      moduleVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.moduleVisitValue(this);
    }
  }

  /**
   * Local value declarations (let).
   */

  public static final class UASTUDValueLocal extends UASTUDTermLocal
  {
    private final @Nonnull Option<UASTUTypePath> ascription;
    private final @Nonnull UASTUExpression       expression;
    private final @Nonnull TokenIdentifierLower  name;

    public UASTUDValueLocal(
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

    @Override public
      <E extends Throwable, V extends UASTULocalLevelVisitor<E>>
      void
      localVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.localVisitValueLocal(this);
    }
  }
}
