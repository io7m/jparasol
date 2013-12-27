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
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITWHETHER IN AN ACTION
 * OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN
 * CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
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

public abstract class UASTIDeclaration
{
  /**
   * The type of local declarations.
   */

  public static abstract class UASTIDeclarationLocalLevel extends
    UASTIDeclaration implements UASTILocalLevelVisitable
  {
    public abstract @Nonnull TokenIdentifierLower getName();
  }

  /**
   * The type of module-level declarations.
   */

  public static abstract class UASTIDeclarationModuleLevel extends
    UASTIDeclaration implements UASTIModuleLevelDeclarationVisitable
  {
    public abstract @Nonnull TokenIdentifierLower getName();
  }

  /**
   * The type of shader-level declarations.
   */

  public static abstract class UASTIDeclarationShaderLevel extends
    UASTIDeclaration
  {
    // Nothing
  }

  /**
   * The type of unit-level declarations.
   */

  public static abstract class UASTIDeclarationUnitLevel extends
    UASTIDeclaration implements UASTIUnitLevelVisitable
  {
    // Nothing
  }

  /**
   * The type of function declarations.
   */

  public static abstract class UASTIDFunction extends UASTIDTerm implements
    UASTIFunctionVisitable
  {
    // Nothing
  }

  public static final class UASTIDFunctionArgument
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

  public static final class UASTIDFunctionDefined extends UASTIDFunction
  {
    private final @Nonnull List<UASTIDFunctionArgument> arguments;
    private final @Nonnull UASTIExpression              body;
    private final @Nonnull TokenIdentifierLower         name;
    private final @Nonnull UASTITypePath                return_type;

    public UASTIDFunctionDefined(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull List<UASTIDFunctionArgument> arguments,
      final @Nonnull UASTITypePath return_type,
      final @Nonnull UASTIExpression body)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.arguments = Constraints.constrainNotNull(arguments, "Arguments");
      this.return_type =
        Constraints.constrainNotNull(return_type, "Return type");
      this.body = Constraints.constrainNotNull(body, "Body");
    }

    @Override public
      <A, B, E extends Throwable, V extends UASTIFunctionVisitor<A, B, E>>
      A
      functionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.functionVisitDefinedPre(this);
      final List<B> args = new ArrayList<B>();
      for (final UASTIDFunctionArgument a : this.arguments) {
        final B x = v.functionVisitArgument(a);
        args.add(x);
      }
      return v.functionVisitDefined(args, this);
    }

    public @Nonnull List<UASTIDFunctionArgument> getArguments()
    {
      return this.arguments;
    }

    public @Nonnull UASTIExpression getBody()
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
      <A, E extends Throwable, V extends UASTIModuleLevelDeclarationVisitor<A, E>>
      A
      moduleLevelVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.moduleVisitFunctionDefined(this);
    }
  }

  public static final class UASTIDExternal
  {
    private final @Nonnull TokenIdentifierLower name;
    private final boolean                       vertex_shader_allowed;
    private final boolean                       fragment_shader_allowed;

    public UASTIDExternal(
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

    public boolean isVertexShaderAllowed()
    {
      return this.vertex_shader_allowed;
    }

    public boolean isFragmentShaderAllowed()
    {
      return this.fragment_shader_allowed;
    }
  }

  /**
   * Functions with external declarations (private FFI).
   */

  public static final class UASTIDFunctionExternal extends UASTIDFunction
  {
    private final @Nonnull List<UASTIDFunctionArgument> arguments;
    private final @Nonnull UASTIDExternal               external;
    private final @Nonnull TokenIdentifierLower         name;
    private final @Nonnull UASTITypePath                return_type;

    public UASTIDFunctionExternal(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull List<UASTIDFunctionArgument> arguments,
      final @Nonnull UASTITypePath return_type,
      final @Nonnull UASTIDExternal external)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.arguments = Constraints.constrainNotNull(arguments, "Arguments");
      this.return_type =
        Constraints.constrainNotNull(return_type, "Return type");
      this.external = Constraints.constrainNotNull(external, "External");
    }

    @Override public
      <A, B, E extends Throwable, V extends UASTIFunctionVisitor<A, B, E>>
      A
      functionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.functionVisitExternalPre(this);
      final List<B> args = new ArrayList<B>();
      for (final UASTIDFunctionArgument a : this.arguments) {
        final B x = v.functionVisitArgument(a);
        args.add(x);
      }
      return v.functionVisitExternal(args, this);
    }

    public @Nonnull List<UASTIDFunctionArgument> getArguments()
    {
      return this.arguments;
    }

    public @Nonnull UASTIDExternal getExternal()
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
      <A, E extends Throwable, V extends UASTIModuleLevelDeclarationVisitor<A, E>>
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

  public static final class UASTIDImport extends UASTIDeclaration
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

    @Override public boolean equals(
      final Object obj)
    {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (this.getClass() != obj.getClass()) {
        return false;
      }
      final UASTIDImport other = (UASTIDImport) obj;
      if (!this.path.equals(other.path)) {
        return false;
      }
      if (!this.rename.equals(other.rename)) {
        return false;
      }
      return true;
    }

    public @Nonnull ModulePath getPath()
    {
      return this.path;
    }

    public @Nonnull Option<TokenIdentifierUpper> getRename()
    {
      return this.rename;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.path.hashCode();
      result = (prime * result) + this.rename.hashCode();
      return result;
    }
  }

  /**
   * Module declarations.
   */

  public static final class UASTIDModule extends UASTIDeclarationUnitLevel implements
    UASTIModuleVisitable
  {
    private final @Nonnull List<UASTIDeclarationModuleLevel> declarations;
    private final @Nonnull List<UASTIDImport>                imports;
    private final @Nonnull ModulePath                        path;

    public UASTIDModule(
      final @Nonnull ModulePath path,
      final @Nonnull List<UASTIDImport> imports,
      final @Nonnull List<UASTIDeclarationModuleLevel> declarations)
      throws ConstraintError
    {
      this.path = Constraints.constrainNotNull(path, "Name");
      this.imports = Constraints.constrainNotNull(imports, "Imports");
      this.declarations =
        Constraints.constrainNotNull(declarations, "Declarations");
    }

    public @Nonnull List<UASTIDeclarationModuleLevel> getDeclarations()
    {
      return this.declarations;
    }

    public @Nonnull List<UASTIDImport> getImports()
    {
      return this.imports;
    }

    public @Nonnull ModulePath getPath()
    {
      return this.path;
    }

    @Override public
      <M, I, D, E extends Throwable, V extends UASTIModuleVisitor<M, I, D, E>>
      M
      moduleVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      final UASTIModuleLevelDeclarationVisitor<D, E> dv =
        v.moduleVisitPre(this);

      final ArrayList<I> r_imports = new ArrayList<I>();
      for (final UASTIDImport i : this.imports) {
        final I ri = v.moduleVisitImport(i);
        r_imports.add(ri);
      }

      final ArrayList<D> r_declarations = new ArrayList<D>();
      for (final UASTIDeclarationModuleLevel d : this.declarations) {
        final D rd = d.moduleLevelVisitableAccept(dv);
        r_declarations.add(rd);
      }

      return v.moduleVisit(r_imports, r_declarations, this);
    }

    @Override public
      <A, E extends Throwable, V extends UASTIUnitLevelVisitor<A, E>>
      A
      unitVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.unitVisitModule(this);
    }
  }

  /**
   * Package declarations.
   */

  public static final class UASTIDPackage extends UASTIDeclarationUnitLevel
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
      <A, E extends Throwable, V extends UASTIUnitLevelVisitor<A, E>>
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

  public static abstract class UASTIDShader extends
    UASTIDeclarationModuleLevel
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

  public static final class UASTIDShaderFragment extends UASTIDShader implements
    UASTIFragmentShaderVisitable
  {
    private final @Nonnull List<UASTIDShaderFragmentInput>            inputs;
    private final @Nonnull List<UASTIDShaderFragmentLocal>            locals;
    private final @Nonnull List<UASTIDShaderFragmentOutput>           outputs;
    private final @Nonnull List<UASTIDShaderFragmentParameter>        parameters;
    private final @Nonnull List<UASTIDShaderFragmentOutputAssignment> writes;

    public UASTIDShaderFragment(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull List<UASTIDShaderFragmentInput> inputs,
      final @Nonnull List<UASTIDShaderFragmentOutput> outputs,
      final @Nonnull List<UASTIDShaderFragmentParameter> parameters,
      final @Nonnull List<UASTIDShaderFragmentLocal> locals,
      final @Nonnull List<UASTIDShaderFragmentOutputAssignment> writes)
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
      <F, PI, PP, PO, L, O, E extends Throwable, V extends UASTIFragmentShaderVisitor<F, PI, PP, PO, L, O, E>>
      F
      fragmentShaderVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      final List<PI> r_inputs = new ArrayList<PI>();
      for (final UASTIDShaderFragmentInput i : this.inputs) {
        final PI ri = v.fragmentShaderVisitInput(i);
        r_inputs.add(ri);
      }

      final List<PO> r_outputs = new ArrayList<PO>();
      for (final UASTIDShaderFragmentOutput o : this.outputs) {
        final PO ro = v.fragmentShaderVisitOutput(o);
        r_outputs.add(ro);
      }

      final List<PP> r_parameters = new ArrayList<PP>();
      for (final UASTIDShaderFragmentParameter p : this.parameters) {
        final PP rp = v.fragmentShaderVisitParameter(p);
        r_parameters.add(rp);
      }

      final UASTIFragmentShaderLocalVisitor<L, E> lv =
        v.fragmentShaderVisitLocalsPre();

      final ArrayList<L> r_locals = new ArrayList<L>();
      for (final UASTIDShaderFragmentLocal l : this.locals) {
        final L rl = l.fragmentShaderLocalVisitableAccept(lv);
        r_locals.add(rl);
      }

      final ArrayList<O> r_assigns = new ArrayList<O>();
      for (final UASTIDShaderFragmentOutputAssignment w : this.writes) {
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

    public @Nonnull List<UASTIDShaderFragmentInput> getInputs()
    {
      return this.inputs;
    }

    public @Nonnull List<UASTIDShaderFragmentLocal> getLocals()
    {
      return this.locals;
    }

    public @Nonnull List<UASTIDShaderFragmentOutput> getOutputs()
    {
      return this.outputs;
    }

    public @Nonnull List<UASTIDShaderFragmentParameter> getParameters()
    {
      return this.parameters;
    }

    public @Nonnull List<UASTIDShaderFragmentOutputAssignment> getWrites()
    {
      return this.writes;
    }

    @Override public
      <A, E extends Throwable, V extends UASTIModuleLevelDeclarationVisitor<A, E>>
      A
      moduleLevelVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.moduleVisitFragmentShader(this);
    }
  }

  public static final class UASTIDShaderFragmentInput extends
    UASTIDShaderFragmentParameters
  {
    public UASTIDShaderFragmentInput(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTITypePath type)
      throws ConstraintError
    {
      super(name, type);
    }
  }

  public static abstract class UASTIDShaderFragmentLocal extends
    UASTIDeclarationShaderLevel implements UASTIFragmentShaderLocalVisitable
  {
    // Nothing
  }

  public static final class UASTIDShaderFragmentLocalDiscard extends
    UASTIDShaderFragmentLocal
  {
    private final @Nonnull TokenDiscard    discard;
    private final @Nonnull UASTIExpression expression;

    public UASTIDShaderFragmentLocalDiscard(
      final TokenDiscard discard,
      final UASTIExpression expression)
      throws ConstraintError
    {
      this.discard = Constraints.constrainNotNull(discard, "Discard");
      this.expression =
        Constraints.constrainNotNull(expression, "Expression");
    }

    @Override public
      <L, E extends Throwable, V extends UASTIFragmentShaderLocalVisitor<L, E>>
      L
      fragmentShaderLocalVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.fragmentShaderVisitLocalDiscard(this);
    }

    public @Nonnull TokenDiscard getDiscard()
    {
      return this.discard;
    }

    public @Nonnull UASTIExpression getExpression()
    {
      return this.expression;
    }
  }

  public static final class UASTIDShaderFragmentLocalValue extends
    UASTIDShaderFragmentLocal
  {
    private final @Nonnull UASTIDValueLocal value;

    public UASTIDShaderFragmentLocalValue(
      final @Nonnull UASTIDValueLocal value)
      throws ConstraintError
    {
      this.value = Constraints.constrainNotNull(value, "Value");
    }

    @Override public
      <L, E extends Throwable, V extends UASTIFragmentShaderLocalVisitor<L, E>>
      L
      fragmentShaderLocalVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.fragmentShaderVisitLocalValue(this);
    }

    public @Nonnull UASTIDValueLocal getValue()
    {
      return this.value;
    }
  }

  public static final class UASTIDShaderFragmentOutput extends
    UASTIDShaderFragmentParameters
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

  public static final class UASTIDShaderFragmentOutputAssignment extends
    UASTIDeclarationShaderLevel
  {
    private final @Nonnull TokenIdentifierLower name;
    private final @Nonnull UASTIEVariable       variable;

    public UASTIDShaderFragmentOutputAssignment(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTIEVariable variable)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.variable = Constraints.constrainNotNull(variable, "Variable");
    }

    public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    public @Nonnull UASTIEVariable getVariable()
    {
      return this.variable;
    }
  }

  public static final class UASTIDShaderFragmentParameter extends
    UASTIDShaderFragmentParameters
  {
    public UASTIDShaderFragmentParameter(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTITypePath type)
      throws ConstraintError
    {
      super(name, type);
    }
  }

  public static abstract class UASTIDShaderFragmentParameters extends
    UASTIDShaderParameters
  {
    UASTIDShaderFragmentParameters(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTITypePath type)
      throws ConstraintError
    {
      super(name, type);
    }
  }

  public static abstract class UASTIDShaderParameters extends
    UASTIDeclarationShaderLevel
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

  public static final class UASTIDShaderProgram extends UASTIDShader
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
      <A, E extends Throwable, V extends UASTIModuleLevelDeclarationVisitor<A, E>>
      A
      moduleLevelVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.moduleVisitProgramShader(this);
    }
  }

  public static final class UASTIDShaderVertex extends UASTIDShader implements
    UASTIVertexShaderVisitable
  {
    private final @Nonnull List<UASTIDShaderVertexInput>            inputs;
    private final @Nonnull List<UASTIDShaderVertexLocalValue>       locals;
    private final @Nonnull List<UASTIDShaderVertexOutput>           outputs;
    private final @Nonnull List<UASTIDShaderVertexParameter>        parameters;
    private final @Nonnull List<UASTIDShaderVertexOutputAssignment> writes;

    public UASTIDShaderVertex(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull List<UASTIDShaderVertexInput> inputs,
      final @Nonnull List<UASTIDShaderVertexOutput> outputs,
      final @Nonnull List<UASTIDShaderVertexParameter> parameters,
      final @Nonnull List<UASTIDShaderVertexLocalValue> locals,
      final @Nonnull List<UASTIDShaderVertexOutputAssignment> writes)
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

    public @Nonnull List<UASTIDShaderVertexInput> getInputs()
    {
      return this.inputs;
    }

    public @Nonnull List<UASTIDShaderVertexLocalValue> getLocals()
    {
      return this.locals;
    }

    public @Nonnull List<UASTIDShaderVertexOutput> getOutputs()
    {
      return this.outputs;
    }

    public @Nonnull List<UASTIDShaderVertexParameter> getParameters()
    {
      return this.parameters;
    }

    public @Nonnull List<UASTIDShaderVertexOutputAssignment> getWrites()
    {
      return this.writes;
    }

    @Override public
      <A, E extends Throwable, V extends UASTIModuleLevelDeclarationVisitor<A, E>>
      A
      moduleLevelVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.moduleVisitVertexShader(this);
    }

    @Override public
      <VS, PI, PP, PO, L, O, E extends Throwable, V extends UASTIVertexShaderVisitor<VS, PI, PP, PO, L, O, E>>
      VS
      vertexShaderVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      final List<PI> r_inputs = new ArrayList<PI>();
      for (final UASTIDShaderVertexInput i : this.inputs) {
        final PI ri = v.vertexShaderVisitInput(i);
        r_inputs.add(ri);
      }

      final List<PO> r_outputs = new ArrayList<PO>();
      for (final UASTIDShaderVertexOutput o : this.outputs) {
        final PO ro = v.vertexShaderVisitOutput(o);
        r_outputs.add(ro);
      }

      final List<PP> r_parameters = new ArrayList<PP>();
      for (final UASTIDShaderVertexParameter p : this.parameters) {
        final PP rp = v.vertexShaderVisitParameter(p);
        r_parameters.add(rp);
      }

      final UASTIVertexShaderLocalVisitor<L, E> lv =
        v.vertexShaderVisitLocalsPre();

      final ArrayList<L> r_locals = new ArrayList<L>();
      for (final UASTIDShaderVertexLocalValue l : this.locals) {
        final L rl = lv.vertexShaderVisitLocalValue(l);
        r_locals.add(rl);
      }

      final ArrayList<O> r_assigns = new ArrayList<O>();
      for (final UASTIDShaderVertexOutputAssignment w : this.writes) {
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

  public static final class UASTIDShaderVertexInput extends
    UASTIDShaderVertexParameters
  {
    public UASTIDShaderVertexInput(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTITypePath type)
      throws ConstraintError
    {
      super(name, type);
    }
  }

  public static final class UASTIDShaderVertexLocalValue extends
    UASTIDeclarationShaderLevel
  {
    private final @Nonnull UASTIDValueLocal value;

    public UASTIDShaderVertexLocalValue(
      final @Nonnull UASTIDValueLocal value)
      throws ConstraintError
    {
      this.value = Constraints.constrainNotNull(value, "Value");
    }

    public @Nonnull UASTIDValueLocal getValue()
    {
      return this.value;
    }
  }

  public static final class UASTIDShaderVertexOutput extends
    UASTIDShaderVertexParameters
  {
    public UASTIDShaderVertexOutput(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTITypePath type)
      throws ConstraintError
    {
      super(name, type);
    }
  }

  public static final class UASTIDShaderVertexOutputAssignment extends
    UASTIDeclarationShaderLevel
  {
    private final @Nonnull TokenIdentifierLower name;
    private final @Nonnull UASTIEVariable       variable;

    public UASTIDShaderVertexOutputAssignment(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTIEVariable variable)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.variable = Constraints.constrainNotNull(variable, "Variable");
    }

    public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    public @Nonnull UASTIEVariable getVariable()
    {
      return this.variable;
    }
  }

  public static final class UASTIDShaderVertexParameter extends
    UASTIDShaderVertexParameters
  {
    public UASTIDShaderVertexParameter(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTITypePath type)
      throws ConstraintError
    {
      super(name, type);
    }
  }

  public static abstract class UASTIDShaderVertexParameters extends
    UASTIDShaderParameters
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

  public static abstract class UASTIDTerm extends UASTIDeclarationModuleLevel
  {
    // Nothing
  }

  /**
   * The type of local term declarations.
   */

  public static abstract class UASTIDTermLocal extends
    UASTIDeclarationLocalLevel
  {
    // Nothing
  }

  /**
   * The type of type declarations.
   */

  public static abstract class UASTIDType extends UASTIDeclarationModuleLevel
  {
    // Nothing
  }

  /**
   * Record declarations.
   */

  public static final class UASTIDTypeRecord extends UASTIDType implements
    UASTIDRecordVisitable
  {
    private final @Nonnull List<UASTIDTypeRecordField> fields;
    private final @Nonnull TokenIdentifierLower        name;

    public UASTIDTypeRecord(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull List<UASTIDTypeRecordField> fields)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.fields = Constraints.constrainNotNull(fields, "Fields");
    }

    public @Nonnull List<UASTIDTypeRecordField> getFields()
    {
      return this.fields;
    }

    @Override public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    @Override public
      <A, E extends Throwable, V extends UASTIModuleLevelDeclarationVisitor<A, E>>
      A
      moduleLevelVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.moduleVisitTypeRecord(this);
    }

    @Override public
      <A, B, E extends Throwable, V extends UASTIDRecordVisitor<A, B, E>>
      A
      recordTypeVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.recordTypeVisitPre(this);

      final List<B> new_fields = new ArrayList<B>();
      for (final UASTIDTypeRecordField f : this.fields) {
        final B x = v.recordTypeVisitField(f);
        new_fields.add(x);
      }

      return v.recordTypeVisit(new_fields, this);
    }
  }

  public static final class UASTIDTypeRecordField
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

  public static final class UASTIDValue extends UASTIDTerm
  {
    private final @Nonnull Option<UASTITypePath> ascription;
    private final @Nonnull UASTIExpression       expression;
    private final @Nonnull TokenIdentifierLower  name;

    public UASTIDValue(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull Option<UASTITypePath> ascription,
      final @Nonnull UASTIExpression expression)
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

    public @Nonnull UASTIExpression getExpression()
    {
      return this.expression;
    }

    @Override public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    @Override public
      <A, E extends Throwable, V extends UASTIModuleLevelDeclarationVisitor<A, E>>
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

  public static final class UASTIDValueLocal extends UASTIDTermLocal
  {
    private final @Nonnull Option<UASTITypePath> ascription;
    private final @Nonnull UASTIExpression       expression;
    private final @Nonnull TokenIdentifierLower  name;

    public UASTIDValueLocal(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull Option<UASTITypePath> ascription,
      final @Nonnull UASTIExpression expression)
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

    public @Nonnull UASTIExpression getExpression()
    {
      return this.expression;
    }

    @Override public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    @Override public
      <A, E extends Throwable, V extends UASTILocalLevelVisitor<A, E>>
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
