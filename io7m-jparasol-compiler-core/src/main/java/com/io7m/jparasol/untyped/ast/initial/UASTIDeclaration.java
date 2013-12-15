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

package com.io7m.jparasol.untyped.ast.initial;

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
import com.io7m.jparasol.untyped.ast.initial.UASTIExpression.UASTIEVariable;

public abstract class UASTIDeclaration<S extends UASTIStatus>
{
  /**
   * The type of local declarations.
   */

  public static abstract class UASTIDeclarationLocalLevel<S extends UASTIStatus> extends
    UASTIDeclaration<S> implements UASTILocalLevelVisitable<S>
  {
    public abstract @Nonnull TokenIdentifierLower getName();
  }

  /**
   * The type of module-level declarations.
   */

  public static abstract class UASTIDeclarationModuleLevel<S extends UASTIStatus> extends
    UASTIDeclaration<S> implements UASTIModuleLevelVisitable<S>
  {
    public abstract @Nonnull TokenIdentifierLower getName();
  }

  /**
   * The type of shader-level declarations.
   */

  public static abstract class UASTIDeclarationShaderLevel<S extends UASTIStatus> extends
    UASTIDeclaration<S>
  {
    // Nothing
  }

  /**
   * The type of unit-level declarations.
   */

  public static abstract class UASTIDeclarationUnitLevel<S extends UASTIStatus> extends
    UASTIDeclaration<S> implements UASTIUnitLevelVisitable<S>
  {
    // Nothing
  }

  /**
   * The type of function declarations.
   */

  public static abstract class UASTIDFunction<S extends UASTIStatus> extends
    UASTIDTerm<S> implements UASTIFunctionVisitable<S>
  {
    // Nothing
  }

  public static final class UASTIDFunctionArgument<S extends UASTIStatus>
  {
    private final @Nonnull TokenIdentifierLower name;
    private final @Nonnull UASTITypePath        type;

    public UASTIDFunctionArgument(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTITypePath type)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.type = Constraints.constrainNotNull(type, "Type");
    }

    public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    public @Nonnull UASTITypePath getType()
    {
      return this.type;
    }
  }

  /**
   * Fully defined functions.
   */

  public static final class UASTIDFunctionDefined<S extends UASTIStatus> extends
    UASTIDFunction<S>
  {
    private final @Nonnull List<UASTIDFunctionArgument<S>> arguments;
    private final @Nonnull UASTIExpression<S>              body;
    private final @Nonnull TokenIdentifierLower            name;
    private final @Nonnull UASTITypePath                   return_type;

    public UASTIDFunctionDefined(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull List<UASTIDFunctionArgument<S>> arguments,
      final @Nonnull UASTITypePath return_type,
      final @Nonnull UASTIExpression<S> body)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.arguments = Constraints.constrainNotNull(arguments, "Arguments");
      this.return_type =
        Constraints.constrainNotNull(return_type, "Return type");
      this.body = Constraints.constrainNotNull(body, "Body");
    }

    @Override public
      <E extends Throwable, V extends UASTIFunctionVisitor<S, E>>
      void
      functionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.functionVisitDefined(this);
    }

    public @Nonnull List<UASTIDFunctionArgument<S>> getArguments()
    {
      return this.arguments;
    }

    public @Nonnull UASTIExpression<S> getBody()
    {
      return this.body;
    }

    @Override public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    public @Nonnull UASTITypePath getReturnType()
    {
      return this.return_type;
    }

    @Override public
      <E extends Throwable, V extends UASTIModuleLevelVisitor<S, E>>
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

  public static final class UASTIDFunctionExternal<S extends UASTIStatus> extends
    UASTIDFunction<S>
  {
    private final @Nonnull List<UASTIDFunctionArgument<S>> arguments;
    private final @Nonnull TokenIdentifierLower            external;
    private final @Nonnull TokenIdentifierLower            name;
    private final @Nonnull UASTITypePath                   return_type;

    public UASTIDFunctionExternal(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull List<UASTIDFunctionArgument<S>> arguments,
      final @Nonnull UASTITypePath return_type,
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
      <E extends Throwable, V extends UASTIFunctionVisitor<S, E>>
      void
      functionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.functionVisitExternal(this);
    }

    public @Nonnull List<UASTIDFunctionArgument<S>> getArguments()
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

    public @Nonnull UASTITypePath getReturnType()
    {
      return this.return_type;
    }

    @Override public
      <E extends Throwable, V extends UASTIModuleLevelVisitor<S, E>>
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

  public static final class UASTIDImport<S extends UASTIStatus> extends
    UASTIDeclaration<S> implements UASTIModuleLevelVisitable<S>
  {
    private final @Nonnull ModulePath                   path;
    private final @Nonnull Option<TokenIdentifierUpper> rename;

    public UASTIDImport(
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
      <E extends Throwable, V extends UASTIModuleLevelVisitor<S, E>>
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

  public static final class UASTIDModule<S extends UASTIStatus> extends
    UASTIDeclarationUnitLevel<S> implements UASTIModuleLevelVisitable<S>
  {
    private final @Nonnull List<UASTIDeclarationModuleLevel<S>> declarations;
    private final @Nonnull List<UASTIDImport<S>>                imports;
    private final @Nonnull TokenIdentifierUpper                 name;

    public UASTIDModule(
      final @Nonnull TokenIdentifierUpper name,
      final @Nonnull List<UASTIDImport<S>> imports,
      final @Nonnull List<UASTIDeclarationModuleLevel<S>> declarations)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.imports = Constraints.constrainNotNull(imports, "Imports");
      this.declarations =
        Constraints.constrainNotNull(declarations, "Declarations");
    }

    public @Nonnull List<UASTIDeclarationModuleLevel<S>> getDeclarations()
    {
      return this.declarations;
    }

    public @Nonnull List<UASTIDImport<S>> getImports()
    {
      return this.imports;
    }

    public @Nonnull TokenIdentifierUpper getName()
    {
      return this.name;
    }

    @Override public
      <E extends Throwable, V extends UASTIModuleLevelVisitor<S, E>>
      void
      moduleVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.moduleVisitModule(this);
      for (final UASTIDImport<S> i : this.imports) {
        i.moduleVisitableAccept(v);
      }
      for (final UASTIDeclarationModuleLevel<S> d : this.declarations) {
        d.moduleVisitableAccept(v);
      }
    }

    @Override public
      <E extends Throwable, V extends UASTIUnitLevelVisitor<S, E>>
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

  public static final class UASTIDPackage<S extends UASTIStatus> extends
    UASTIDeclarationUnitLevel<S>
  {
    private final @Nonnull PackagePath path;

    public UASTIDPackage(
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
      <E extends Throwable, V extends UASTIUnitLevelVisitor<S, E>>
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

  public static abstract class UASTIDShader<S extends UASTIStatus> extends
    UASTIDeclarationModuleLevel<S>
  {
    private final @Nonnull TokenIdentifierLower name;

    protected UASTIDShader(
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

  public static final class UASTIDShaderFragment<S extends UASTIStatus> extends
    UASTIDShader<S> implements UASTIFragmentShaderVisitable<S>
  {
    private final @Nonnull List<UASTIDShaderFragmentInput<S>>            inputs;
    private final @Nonnull List<UASTIDShaderFragmentLocal<S>>            locals;
    private final @Nonnull List<UASTIDShaderFragmentOutput<S>>           outputs;
    private final @Nonnull List<UASTIDShaderFragmentParameter<S>>        parameters;
    private final @Nonnull List<UASTIDShaderFragmentOutputAssignment<S>> writes;

    public UASTIDShaderFragment(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull List<UASTIDShaderFragmentInput<S>> inputs,
      final @Nonnull List<UASTIDShaderFragmentOutput<S>> outputs,
      final @Nonnull List<UASTIDShaderFragmentParameter<S>> parameters,
      final @Nonnull List<UASTIDShaderFragmentLocal<S>> locals,
      final @Nonnull List<UASTIDShaderFragmentOutputAssignment<S>> writes)
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
      <E extends Throwable, V extends UASTIFragmentShaderVisitor<S, E>>
      void
      fragmentShaderVisitableAccept(
        final V v)
        throws E,
          ConstraintError
    {
      v.fragmentShaderVisit(this);

      for (final UASTIDShaderFragmentInput<S> i : this.inputs) {
        i.fragmentShaderVisitableAccept(v);
      }
      for (final UASTIDShaderFragmentOutput<S> o : this.outputs) {
        o.fragmentShaderVisitableAccept(v);
      }
      for (final UASTIDShaderFragmentParameter<S> p : this.parameters) {
        p.fragmentShaderVisitableAccept(v);
      }
      for (final UASTIDShaderFragmentLocal<S> l : this.locals) {
        l.fragmentShaderVisitableAccept(v);
      }
      for (final UASTIDShaderFragmentOutputAssignment<S> w : this.writes) {
        w.fragmentShaderVisitableAccept(v);
      }
    }

    public @Nonnull List<UASTIDShaderFragmentInput<S>> getInputs()
    {
      return this.inputs;
    }

    public @Nonnull List<UASTIDShaderFragmentLocal<S>> getLocals()
    {
      return this.locals;
    }

    public @Nonnull List<UASTIDShaderFragmentOutput<S>> getOutputs()
    {
      return this.outputs;
    }

    public @Nonnull List<UASTIDShaderFragmentParameter<S>> getParameters()
    {
      return this.parameters;
    }

    public @Nonnull List<UASTIDShaderFragmentOutputAssignment<S>> getWrites()
    {
      return this.writes;
    }

    @Override public
      <E extends Throwable, V extends UASTIModuleLevelVisitor<S, E>>
      void
      moduleVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.moduleVisitFragmentShader(this);
    }
  }

  public static final class UASTIDShaderFragmentInput<S extends UASTIStatus> extends
    UASTIDShaderFragmentParameters<S>
  {
    public UASTIDShaderFragmentInput(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTITypePath type)
      throws ConstraintError
    {
      super(name, type);
    }

    @Override public
      <E extends Throwable, V extends UASTIFragmentShaderVisitor<S, E>>
      void
      fragmentShaderVisitableAccept(
        final V v)
        throws E,
          ConstraintError
    {
      v.fragmentShaderVisitInput(this);
    }
  }

  public static abstract class UASTIDShaderFragmentLocal<S extends UASTIStatus> extends
    UASTIDeclarationShaderLevel<S> implements UASTIFragmentShaderVisitable<S>
  {
    // Nothing
  }

  public static final class UASTIDShaderFragmentLocalDiscard<S extends UASTIStatus> extends
    UASTIDShaderFragmentLocal<S>
  {
    private final @Nonnull TokenDiscard       discard;
    private final @Nonnull UASTIExpression<S> expression;

    public UASTIDShaderFragmentLocalDiscard(
      final TokenDiscard discard,
      final UASTIExpression<S> expression)
      throws ConstraintError
    {
      this.discard = Constraints.constrainNotNull(discard, "Discard");
      this.expression =
        Constraints.constrainNotNull(expression, "Expression");
    }

    @Override public
      <E extends Throwable, V extends UASTIFragmentShaderVisitor<S, E>>
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

    public @Nonnull UASTIExpression<S> getExpression()
    {
      return this.expression;
    }
  }

  public static final class UASTIDShaderFragmentLocalValue<S extends UASTIStatus> extends
    UASTIDShaderFragmentLocal<S>
  {
    private final @Nonnull UASTIDValueLocal<S> value;

    public UASTIDShaderFragmentLocalValue(
      final @Nonnull UASTIDValueLocal<S> value)
      throws ConstraintError
    {
      this.value = Constraints.constrainNotNull(value, "Value");
    }

    @Override public
      <E extends Throwable, V extends UASTIFragmentShaderVisitor<S, E>>
      void
      fragmentShaderVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.fragmentShaderVisitLocalValue(this);
    }

    public @Nonnull UASTIDValueLocal<S> getValue()
    {
      return this.value;
    }
  }

  public static final class UASTIDShaderFragmentOutput<S extends UASTIStatus> extends
    UASTIDShaderFragmentParameters<S>
  {
    private final int index;

    public UASTIDShaderFragmentOutput(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTITypePath type,
      final int index)
      throws ConstraintError
    {
      super(name, type);
      this.index = index;
    }

    @Override public
      <E extends Throwable, V extends UASTIFragmentShaderVisitor<S, E>>
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

  public static final class UASTIDShaderFragmentOutputAssignment<S extends UASTIStatus> extends
    UASTIDeclarationShaderLevel<S> implements UASTIFragmentShaderVisitable<S>
  {
    private final @Nonnull TokenIdentifierLower name;
    private final @Nonnull UASTIEVariable<S>    variable;

    public UASTIDShaderFragmentOutputAssignment(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTIEVariable<S> variable)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.variable = Constraints.constrainNotNull(variable, "Variable");
    }

    @Override public
      <E extends Throwable, V extends UASTIFragmentShaderVisitor<S, E>>
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

    public @Nonnull UASTIEVariable<S> getVariable()
    {
      return this.variable;
    }
  }

  public static final class UASTIDShaderFragmentParameter<S extends UASTIStatus> extends
    UASTIDShaderFragmentParameters<S>
  {
    public UASTIDShaderFragmentParameter(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTITypePath type)
      throws ConstraintError
    {
      super(name, type);
    }

    @Override public
      <E extends Throwable, V extends UASTIFragmentShaderVisitor<S, E>>
      void
      fragmentShaderVisitableAccept(
        final V v)
        throws E,
          ConstraintError
    {
      v.fragmentShaderVisitParameter(this);
    }
  }

  public static abstract class UASTIDShaderFragmentParameters<S extends UASTIStatus> extends
    UASTIDShaderParameters<S> implements UASTIFragmentShaderVisitable<S>
  {
    UASTIDShaderFragmentParameters(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTITypePath type)
      throws ConstraintError
    {
      super(name, type);
    }
  }

  public static abstract class UASTIDShaderParameters<S extends UASTIStatus> extends
    UASTIDeclarationShaderLevel<S>
  {
    private final @Nonnull TokenIdentifierLower name;
    private final @Nonnull UASTITypePath        type;

    UASTIDShaderParameters(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTITypePath type)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.type = Constraints.constrainNotNull(type, "Type");
    }

    public final @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    public final @Nonnull UASTITypePath getType()
    {
      return this.type;
    }
  }

  public static final class UASTIDShaderProgram<S extends UASTIStatus> extends
    UASTIDShader<S>
  {
    private final @Nonnull UASTIShaderPath fragment_shader;
    private final @Nonnull UASTIShaderPath vertex_shader;

    public UASTIDShaderProgram(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTIShaderPath vertex_shader,
      final @Nonnull UASTIShaderPath fragment_shader)
      throws ConstraintError
    {
      super(name);
      this.vertex_shader =
        Constraints.constrainNotNull(vertex_shader, "Vertex shader");
      this.fragment_shader =
        Constraints.constrainNotNull(fragment_shader, "Fragment shader");
    }

    public @Nonnull UASTIShaderPath getFragmentShader()
    {
      return this.fragment_shader;
    }

    public @Nonnull UASTIShaderPath getVertexShader()
    {
      return this.vertex_shader;
    }

    @Override public
      <E extends Throwable, V extends UASTIModuleLevelVisitor<S, E>>
      void
      moduleVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.moduleVisitProgramShader(this);
    }
  }

  public static final class UASTIDShaderVertex<S extends UASTIStatus> extends
    UASTIDShader<S> implements UASTIVertexShaderVisitable<S>
  {
    private final @Nonnull List<UASTIDShaderVertexInput<S>>            inputs;
    private final @Nonnull List<UASTIDShaderVertexOutput<S>>           outputs;
    private final @Nonnull List<UASTIDShaderVertexParameter<S>>        parameters;
    private final @Nonnull List<UASTIDShaderVertexLocalValue<S>>       values;
    private final @Nonnull List<UASTIDShaderVertexOutputAssignment<S>> writes;

    public UASTIDShaderVertex(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull List<UASTIDShaderVertexInput<S>> inputs,
      final @Nonnull List<UASTIDShaderVertexOutput<S>> outputs,
      final @Nonnull List<UASTIDShaderVertexParameter<S>> parameters,
      final @Nonnull List<UASTIDShaderVertexLocalValue<S>> values,
      final @Nonnull List<UASTIDShaderVertexOutputAssignment<S>> writes)
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

    public @Nonnull List<UASTIDShaderVertexInput<S>> getInputs()
    {
      return this.inputs;
    }

    public @Nonnull List<UASTIDShaderVertexOutput<S>> getOutputs()
    {
      return this.outputs;
    }

    public @Nonnull List<UASTIDShaderVertexParameter<S>> getParameters()
    {
      return this.parameters;
    }

    public @Nonnull List<UASTIDShaderVertexLocalValue<S>> getValues()
    {
      return this.values;
    }

    public @Nonnull List<UASTIDShaderVertexOutputAssignment<S>> getWrites()
    {
      return this.writes;
    }

    @Override public
      <E extends Throwable, V extends UASTIModuleLevelVisitor<S, E>>
      void
      moduleVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.moduleVisitVertexShader(this);
    }

    @Override public
      <E extends Throwable, V extends UASTIVertexShaderVisitor<S, E>>
      void
      vertexShaderVisitableAccept(
        final V v)
        throws E,
          ConstraintError
    {
      v.vertexShaderVisit(this);

      for (final UASTIDShaderVertexInput<S> i : this.inputs) {
        i.vertexShaderVisitableAccept(v);
      }
      for (final UASTIDShaderVertexOutput<S> o : this.outputs) {
        o.vertexShaderVisitableAccept(v);
      }
      for (final UASTIDShaderVertexParameter<S> p : this.parameters) {
        p.vertexShaderVisitableAccept(v);
      }
      for (final UASTIDShaderVertexLocalValue<S> l : this.values) {
        l.vertexShaderVisitableAccept(v);
      }
      for (final UASTIDShaderVertexOutputAssignment<S> w : this.writes) {
        w.vertexShaderVisitableAccept(v);
      }
    }
  }

  public static final class UASTIDShaderVertexInput<S extends UASTIStatus> extends
    UASTIDShaderVertexParameters<S>
  {
    public UASTIDShaderVertexInput(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTITypePath type)
      throws ConstraintError
    {
      super(name, type);
    }

    @Override public
      <E extends Throwable, V extends UASTIVertexShaderVisitor<S, E>>
      void
      vertexShaderVisitableAccept(
        final V v)
        throws E,
          ConstraintError
    {
      v.vertexShaderVisitInput(this);
    }
  }

  public static final class UASTIDShaderVertexLocalValue<S extends UASTIStatus> extends
    UASTIDeclarationShaderLevel<S> implements UASTIVertexShaderVisitable<S>
  {
    private final @Nonnull UASTIDValueLocal<S> value;

    public UASTIDShaderVertexLocalValue(
      final @Nonnull UASTIDValueLocal<S> value)
      throws ConstraintError
    {
      this.value = Constraints.constrainNotNull(value, "Value");
    }

    public @Nonnull UASTIDValueLocal<S> getValue()
    {
      return this.value;
    }

    @Override public
      <E extends Throwable, V extends UASTIVertexShaderVisitor<S, E>>
      void
      vertexShaderVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.vertexShaderVisitLocalValue(this);
    }
  }

  public static final class UASTIDShaderVertexOutput<S extends UASTIStatus> extends
    UASTIDShaderVertexParameters<S>
  {
    public UASTIDShaderVertexOutput(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTITypePath type)
      throws ConstraintError
    {
      super(name, type);
    }

    @Override public
      <E extends Throwable, V extends UASTIVertexShaderVisitor<S, E>>
      void
      vertexShaderVisitableAccept(
        final V v)
        throws E,
          ConstraintError
    {
      v.vertexShaderVisitOutput(this);
    }
  }

  public static final class UASTIDShaderVertexOutputAssignment<S extends UASTIStatus> extends
    UASTIDeclarationShaderLevel<S> implements UASTIVertexShaderVisitable<S>
  {
    private final @Nonnull TokenIdentifierLower name;
    private final @Nonnull UASTIEVariable<S>    variable;

    public UASTIDShaderVertexOutputAssignment(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTIEVariable<S> variable)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.variable = Constraints.constrainNotNull(variable, "Variable");
    }

    public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    public @Nonnull UASTIEVariable<S> getVariable()
    {
      return this.variable;
    }

    @Override public
      <E extends Throwable, V extends UASTIVertexShaderVisitor<S, E>>
      void
      vertexShaderVisitableAccept(
        final V v)
        throws E,
          ConstraintError
    {
      v.vertexShaderVisitOutputAssignment(this);
    }
  }

  public static final class UASTIDShaderVertexParameter<S extends UASTIStatus> extends
    UASTIDShaderVertexParameters<S>
  {
    public UASTIDShaderVertexParameter(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTITypePath type)
      throws ConstraintError
    {
      super(name, type);
    }

    @Override public
      <E extends Throwable, V extends UASTIVertexShaderVisitor<S, E>>
      void
      vertexShaderVisitableAccept(
        final V v)
        throws E,
          ConstraintError
    {
      v.vertexShaderVisitParameter(this);
    }
  }

  public static abstract class UASTIDShaderVertexParameters<S extends UASTIStatus> extends
    UASTIDShaderParameters<S> implements UASTIVertexShaderVisitable<S>
  {
    UASTIDShaderVertexParameters(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTITypePath type)
      throws ConstraintError
    {
      super(name, type);
    }
  }

  /**
   * The type of term declarations.
   */

  public static abstract class UASTIDTerm<S extends UASTIStatus> extends
    UASTIDeclarationModuleLevel<S>
  {
    // Nothing
  }

  /**
   * The type of local term declarations.
   */

  public static abstract class UASTIDTermLocal<S extends UASTIStatus> extends
    UASTIDeclarationLocalLevel<S>
  {
    // Nothing
  }

  /**
   * The type of type declarations.
   */

  public static abstract class UASTIDType<S extends UASTIStatus> extends
    UASTIDeclarationModuleLevel<S>
  {
    // Nothing
  }

  /**
   * Record declarations.
   */

  public static final class UASTIDTypeRecord<S extends UASTIStatus> extends
    UASTIDType<S> implements UASTIDRecordVisitable<S>
  {
    private final @Nonnull List<UASTIDTypeRecordField<S>> fields;
    private final @Nonnull TokenIdentifierLower           name;

    public UASTIDTypeRecord(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull List<UASTIDTypeRecordField<S>> fields)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.fields = Constraints.constrainNotNull(fields, "Fields");
    }

    public @Nonnull List<UASTIDTypeRecordField<S>> getFields()
    {
      return this.fields;
    }

    @Override public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    @Override public
      <E extends Throwable, V extends UASTIModuleLevelVisitor<S, E>>
      void
      moduleVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.moduleVisitTypeRecord(this);
    }

    @Override public
      <E extends Throwable, V extends UASTIDRecordVisitor<S, E>>
      void
      recordTypeVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.recordTypeVisit(this);
      for (final UASTIDTypeRecordField<S> f : this.fields) {
        f.recordTypeVisitableAccept(v);
      }
    }
  }

  public static final class UASTIDTypeRecordField<S extends UASTIStatus> implements
    UASTIDRecordVisitable<S>
  {
    private final @Nonnull TokenIdentifierLower name;
    private final @Nonnull UASTITypePath        type;

    public UASTIDTypeRecordField(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTITypePath type)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.type = Constraints.constrainNotNull(type, "Type");
    }

    public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    public @Nonnull UASTITypePath getType()
    {
      return this.type;
    }

    @Override public
      <E extends Throwable, V extends UASTIDRecordVisitor<S, E>>
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

  public static final class UASTIDValue<S extends UASTIStatus> extends
    UASTIDTerm<S>
  {
    private final @Nonnull Option<UASTITypePath> ascription;
    private final @Nonnull UASTIExpression<S>    expression;
    private final @Nonnull TokenIdentifierLower  name;

    public UASTIDValue(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull Option<UASTITypePath> ascription,
      final @Nonnull UASTIExpression<S> expression)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.ascription =
        Constraints.constrainNotNull(ascription, "Ascription");
      this.expression =
        Constraints.constrainNotNull(expression, "Expression");
    }

    public @Nonnull Option<UASTITypePath> getAscription()
    {
      return this.ascription;
    }

    public @Nonnull UASTIExpression<S> getExpression()
    {
      return this.expression;
    }

    @Override public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    @Override public
      <E extends Throwable, V extends UASTIModuleLevelVisitor<S, E>>
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

  public static final class UASTIDValueLocal<S extends UASTIStatus> extends
    UASTIDTermLocal<S>
  {
    private final @Nonnull Option<UASTITypePath> ascription;
    private final @Nonnull UASTIExpression<S>    expression;
    private final @Nonnull TokenIdentifierLower  name;

    public UASTIDValueLocal(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull Option<UASTITypePath> ascription,
      final @Nonnull UASTIExpression<S> expression)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.ascription =
        Constraints.constrainNotNull(ascription, "Ascription");
      this.expression =
        Constraints.constrainNotNull(expression, "Expression");
    }

    public @Nonnull Option<UASTITypePath> getAscription()
    {
      return this.ascription;
    }

    public @Nonnull UASTIExpression<S> getExpression()
    {
      return this.expression;
    }

    @Override public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    @Override public
      <E extends Throwable, V extends UASTILocalLevelVisitor<S, E>>
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
