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

import java.util.ArrayList;
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
    UASTIDeclaration<S> implements UASTIModuleLevelDeclarationVisitable<S>
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
      <A, B, E extends Throwable, V extends UASTIFunctionVisitor<A, B, S, E>>
      A
      functionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.functionVisitDefinedPre(this);
      final List<B> args = new ArrayList<B>();
      for (final UASTIDFunctionArgument<S> a : this.arguments) {
        final B x = v.functionVisitArgument(a);
        args.add(x);
      }
      return v.functionVisitDefined(args, this);
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
      <A, E extends Throwable, V extends UASTIModuleLevelDeclarationVisitor<A, S, E>>
      A
      moduleLevelVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.moduleVisitFunctionDefined(this);
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
      <A, B, E extends Throwable, V extends UASTIFunctionVisitor<A, B, S, E>>
      A
      functionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.functionVisitExternalPre(this);
      final List<B> args = new ArrayList<B>();
      for (final UASTIDFunctionArgument<S> a : this.arguments) {
        final B x = v.functionVisitArgument(a);
        args.add(x);
      }
      return v.functionVisitExternal(args, this);
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
      <A, E extends Throwable, V extends UASTIModuleLevelDeclarationVisitor<A, S, E>>
      A
      moduleLevelVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.moduleVisitFunctionExternal(this);
    }
  }

  /**
   * Import declarations.
   */

  public static final class UASTIDImport<S extends UASTIStatus> extends
    UASTIDeclaration<S>
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
  }

  /**
   * Module declarations.
   */

  public static final class UASTIDModule<S extends UASTIStatus> extends
    UASTIDeclarationUnitLevel<S> implements UASTIModuleVisitable<S>
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
      <A, E extends Throwable, V extends UASTIUnitLevelVisitor<A, S, E>>
      A
      unitVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.unitVisitModule(this);
    }

    @Override public
      <M, I, D, E extends Throwable, V extends UASTIModuleVisitor<M, I, D, S, E>>
      M
      moduleVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      final UASTIModuleLevelDeclarationVisitor<D, S, E> dv =
        v.moduleVisitPre(this);

      final ArrayList<I> r_imports = new ArrayList<I>();
      for (final UASTIDImport<S> i : this.imports) {
        final I ri = v.moduleVisitImport(i);
        r_imports.add(ri);
      }

      final ArrayList<D> r_declarations = new ArrayList<D>();
      for (final UASTIDeclarationModuleLevel<S> d : this.declarations) {
        final D rd = d.moduleLevelVisitableAccept(dv);
        r_declarations.add(rd);
      }

      return v.moduleVisit(r_imports, r_declarations, this);
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
      <A, E extends Throwable, V extends UASTIUnitLevelVisitor<A, S, E>>
      A
      unitVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.unitVisitPackage(this);
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
      <F, PI, PP, PO, L, O, E extends Throwable, V extends UASTIFragmentShaderVisitor<F, PI, PP, PO, L, O, S, E>>
      F
      fragmentShaderVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      final List<PI> r_inputs = new ArrayList<PI>();
      for (final UASTIDShaderFragmentInput<S> i : this.inputs) {
        final PI ri = v.fragmentShaderVisitInput(i);
        r_inputs.add(ri);
      }

      final List<PO> r_outputs = new ArrayList<PO>();
      for (final UASTIDShaderFragmentOutput<S> o : this.outputs) {
        final PO ro = v.fragmentShaderVisitOutput(o);
        r_outputs.add(ro);
      }

      final List<PP> r_parameters = new ArrayList<PP>();
      for (final UASTIDShaderFragmentParameter<S> p : this.parameters) {
        final PP rp = v.fragmentShaderVisitParameter(p);
        r_parameters.add(rp);
      }

      final UASTIFragmentShaderLocalVisitor<L, S, E> lv =
        v.fragmentShaderVisitLocalsPre();

      final ArrayList<L> r_locals = new ArrayList<L>();
      for (final UASTIDShaderFragmentLocal<S> l : this.locals) {
        final L rl = l.fragmentShaderLocalVisitableAccept(lv);
        r_locals.add(rl);
      }

      final ArrayList<O> r_assigns = new ArrayList<O>();
      for (final UASTIDShaderFragmentOutputAssignment<S> w : this.writes) {
        final O rw = v.fragmentShaderVisitOutputAssignment(w);
        r_assigns.add(rw);
      }

      return v.fragmentShaderVisit(
        r_inputs,
        r_parameters,
        r_outputs,
        r_locals,
        r_assigns,
        this);
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
      <A, E extends Throwable, V extends UASTIModuleLevelDeclarationVisitor<A, S, E>>
      A
      moduleLevelVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.moduleVisitFragmentShader(this);
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
  }

  public static abstract class UASTIDShaderFragmentLocal<S extends UASTIStatus> extends
    UASTIDeclarationShaderLevel<S> implements
    UASTIFragmentShaderLocalVisitable<S>
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

    public @Nonnull TokenDiscard getDiscard()
    {
      return this.discard;
    }

    public @Nonnull UASTIExpression<S> getExpression()
    {
      return this.expression;
    }

    @Override public
      <L, E extends Throwable, V extends UASTIFragmentShaderLocalVisitor<L, S, E>>
      L
      fragmentShaderLocalVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.fragmentShaderVisitLocalDiscard(this);
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
      <L, E extends Throwable, V extends UASTIFragmentShaderLocalVisitor<L, S, E>>
      L
      fragmentShaderLocalVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.fragmentShaderVisitLocalValue(this);
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

    public int getIndex()
    {
      return this.index;
    }
  }

  public static final class UASTIDShaderFragmentOutputAssignment<S extends UASTIStatus> extends
    UASTIDeclarationShaderLevel<S>
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
  }

  public static abstract class UASTIDShaderFragmentParameters<S extends UASTIStatus> extends
    UASTIDShaderParameters<S>
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
      <A, E extends Throwable, V extends UASTIModuleLevelDeclarationVisitor<A, S, E>>
      A
      moduleLevelVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.moduleVisitProgramShader(this);
    }
  }

  public static final class UASTIDShaderVertex<S extends UASTIStatus> extends
    UASTIDShader<S> implements UASTIVertexShaderVisitable<S>
  {
    private final @Nonnull List<UASTIDShaderVertexInput<S>>            inputs;
    private final @Nonnull List<UASTIDShaderVertexOutput<S>>           outputs;
    private final @Nonnull List<UASTIDShaderVertexParameter<S>>        parameters;
    private final @Nonnull List<UASTIDShaderVertexLocalValue<S>>       locals;
    private final @Nonnull List<UASTIDShaderVertexOutputAssignment<S>> writes;

    public UASTIDShaderVertex(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull List<UASTIDShaderVertexInput<S>> inputs,
      final @Nonnull List<UASTIDShaderVertexOutput<S>> outputs,
      final @Nonnull List<UASTIDShaderVertexParameter<S>> parameters,
      final @Nonnull List<UASTIDShaderVertexLocalValue<S>> locals,
      final @Nonnull List<UASTIDShaderVertexOutputAssignment<S>> writes)
      throws ConstraintError
    {
      super(name);
      this.inputs = Constraints.constrainNotNull(inputs, "Inputs");
      this.outputs = Constraints.constrainNotNull(outputs, "Outputs");
      this.parameters =
        Constraints.constrainNotNull(parameters, "Parameters");
      this.locals = Constraints.constrainNotNull(locals, "Values");
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

    public @Nonnull List<UASTIDShaderVertexLocalValue<S>> getLocals()
    {
      return this.locals;
    }

    public @Nonnull List<UASTIDShaderVertexOutputAssignment<S>> getWrites()
    {
      return this.writes;
    }

    @Override public
      <A, E extends Throwable, V extends UASTIModuleLevelDeclarationVisitor<A, S, E>>
      A
      moduleLevelVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.moduleVisitVertexShader(this);
    }

    @Override public
      <VS, PI, PP, PO, L, O, E extends Throwable, V extends UASTIVertexShaderVisitor<VS, PI, PP, PO, L, O, S, E>>
      VS
      vertexShaderVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      final List<PI> r_inputs = new ArrayList<PI>();
      for (final UASTIDShaderVertexInput<S> i : this.inputs) {
        final PI ri = v.vertexShaderVisitInput(i);
        r_inputs.add(ri);
      }

      final List<PO> r_outputs = new ArrayList<PO>();
      for (final UASTIDShaderVertexOutput<S> o : this.outputs) {
        final PO ro = v.vertexShaderVisitOutput(o);
        r_outputs.add(ro);
      }

      final List<PP> r_parameters = new ArrayList<PP>();
      for (final UASTIDShaderVertexParameter<S> p : this.parameters) {
        final PP rp = v.vertexShaderVisitParameter(p);
        r_parameters.add(rp);
      }

      final UASTIVertexShaderLocalVisitor<L, S, E> lv =
        v.vertexShaderVisitLocalsPre();

      final ArrayList<L> r_locals = new ArrayList<L>();
      for (final UASTIDShaderVertexLocalValue<S> l : this.locals) {
        final L rl = lv.vertexShaderVisitLocalValue(l);
        r_locals.add(rl);
      }

      final ArrayList<O> r_assigns = new ArrayList<O>();
      for (final UASTIDShaderVertexOutputAssignment<S> w : this.writes) {
        final O rw = v.vertexShaderVisitOutputAssignment(w);
        r_assigns.add(rw);
      }

      return v.vertexShaderVisit(
        r_inputs,
        r_parameters,
        r_outputs,
        r_locals,
        r_assigns,
        this);
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
  }

  public static final class UASTIDShaderVertexLocalValue<S extends UASTIStatus> extends
    UASTIDeclarationShaderLevel<S>
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
  }

  public static final class UASTIDShaderVertexOutputAssignment<S extends UASTIStatus> extends
    UASTIDeclarationShaderLevel<S>
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
  }

  public static abstract class UASTIDShaderVertexParameters<S extends UASTIStatus> extends
    UASTIDShaderParameters<S>
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
      <A, E extends Throwable, V extends UASTIModuleLevelDeclarationVisitor<A, S, E>>
      A
      moduleLevelVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.moduleVisitTypeRecord(this);
    }

    @Override public
      <A, B, E extends Throwable, V extends UASTIDRecordVisitor<A, B, S, E>>
      A
      recordTypeVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.recordTypeVisitPre(this);

      final List<B> new_fields = new ArrayList<B>();
      for (final UASTIDTypeRecordField<S> f : this.fields) {
        final B x = v.recordTypeVisitField(f);
        new_fields.add(x);
      }

      return v.recordTypeVisit(new_fields, this);
    }
  }

  public static final class UASTIDTypeRecordField<S extends UASTIStatus>
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
      <A, E extends Throwable, V extends UASTIModuleLevelDeclarationVisitor<A, S, E>>
      A
      moduleLevelVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.moduleVisitValue(this);
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
      <A, E extends Throwable, V extends UASTILocalLevelVisitor<A, S, E>>
      A
      localVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.localVisitValueLocal(this);
    }
  }
}
