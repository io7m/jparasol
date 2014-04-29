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
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITWHETHER IN AN ACTION
 * OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN
 * CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.jparasol.untyped.ast.initial;

import java.util.ArrayList;
import java.util.List;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jfunctional.OptionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.ModulePath;
import com.io7m.jparasol.PackagePath;
import com.io7m.jparasol.lexer.Token.TokenDiscard;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.lexer.Token.TokenIdentifierUpper;
import com.io7m.jparasol.untyped.ast.initial.UASTIExpression.UASTIEVariable;

// CHECKSTYLE_JAVADOC:OFF

@EqualityReference public abstract class UASTIDeclaration
{
  /**
   * The type of local declarations.
   */

  @EqualityReference public static abstract class UASTIDeclarationLocalLevel extends
    UASTIDeclaration implements UASTILocalLevelVisitableType
  {
    public abstract TokenIdentifierLower getName();
  }

  /**
   * The type of module-level declarations.
   */

  @EqualityReference public static abstract class UASTIDeclarationModuleLevel extends
    UASTIDeclaration implements UASTIModuleLevelDeclarationVisitableType
  {
    public abstract TokenIdentifierLower getName();
  }

  /**
   * The type of shader-level declarations.
   */

  @EqualityReference public static abstract class UASTIDeclarationShaderLevel extends
    UASTIDeclaration
  {
    // Nothing
  }

  /**
   * The type of unit-level declarations.
   */

  @EqualityReference public static abstract class UASTIDeclarationUnitLevel extends
    UASTIDeclaration implements UASTIUnitLevelVisitableType
  {
    // Nothing
  }

  @EqualityReference public static final class UASTIDExternal
  {
    private final OptionType<UASTIExpression> emulation;
    private final boolean                     fragment_shader_allowed;
    private final TokenIdentifierLower        name;
    private final boolean                     vertex_shader_allowed;

    public UASTIDExternal(
      final TokenIdentifierLower in_name,
      final boolean in_vertex_shader_allowed,
      final boolean in_fragment_shader_allowed,
      final OptionType<UASTIExpression> in_emulation)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.vertex_shader_allowed = in_vertex_shader_allowed;
      this.fragment_shader_allowed = in_fragment_shader_allowed;
      this.emulation = NullCheck.notNull(in_emulation, "Emulation");
    }

    public OptionType<UASTIExpression> getEmulation()
    {
      return this.emulation;
    }

    public TokenIdentifierLower getName()
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
  }

  /**
   * The type of function declarations.
   */

  @EqualityReference public static abstract class UASTIDFunction extends
    UASTIDTerm implements UASTIFunctionVisitableType
  {
    // Nothing
  }

  @EqualityReference public static final class UASTIDFunctionArgument
  {
    private final TokenIdentifierLower name;
    private final UASTITypePath        type;

    public UASTIDFunctionArgument(
      final TokenIdentifierLower in_name,
      final UASTITypePath in_type)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.type = NullCheck.notNull(in_type, "Type");
    }

    public TokenIdentifierLower getName()
    {
      return this.name;
    }

    public UASTITypePath getType()
    {
      return this.type;
    }
  }

  /**
   * Fully defined functions.
   */

  @EqualityReference public static final class UASTIDFunctionDefined extends
    UASTIDFunction
  {
    private final List<UASTIDFunctionArgument> arguments;
    private final UASTIExpression              body;
    private final TokenIdentifierLower         name;
    private final UASTITypePath                return_type;

    public UASTIDFunctionDefined(
      final TokenIdentifierLower in_name,
      final List<UASTIDFunctionArgument> in_arguments,
      final UASTITypePath in_return_type,
      final UASTIExpression in_body)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.arguments = NullCheck.notNull(in_arguments, "Arguments");
      this.return_type = NullCheck.notNull(in_return_type, "Return type");
      this.body = NullCheck.notNull(in_body, "Body");
    }

    @Override public
      <A, B, E extends Throwable, V extends UASTIFunctionVisitorType<A, B, E>>
      A
      functionVisitableAccept(
        final V v)
        throws E
    {
      v.functionVisitDefinedPre(this);
      final List<B> args = new ArrayList<B>();
      for (final UASTIDFunctionArgument a : this.arguments) {
        final B x = v.functionVisitArgument(a);
        args.add(x);
      }
      return v.functionVisitDefined(args, this);
    }

    public List<UASTIDFunctionArgument> getArguments()
    {
      return this.arguments;
    }

    public UASTIExpression getBody()
    {
      return this.body;
    }

    @Override public TokenIdentifierLower getName()
    {
      return this.name;
    }

    public UASTITypePath getReturnType()
    {
      return this.return_type;
    }

    @Override public
      <A, E extends Throwable, V extends UASTIModuleLevelDeclarationVisitorType<A, E>>
      A
      moduleLevelVisitableAccept(
        final V v)
        throws E
    {
      return v.moduleVisitFunctionDefined(this);
    }
  }

  /**
   * Functions with external declarations (private FFI).
   */

  @EqualityReference public static final class UASTIDFunctionExternal extends
    UASTIDFunction
  {
    private final List<UASTIDFunctionArgument> arguments;
    private final UASTIDExternal               external;
    private final TokenIdentifierLower         name;
    private final UASTITypePath                return_type;

    public UASTIDFunctionExternal(
      final TokenIdentifierLower in_name,
      final List<UASTIDFunctionArgument> in_arguments,
      final UASTITypePath in_return_type,
      final UASTIDExternal in_external)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.arguments = NullCheck.notNull(in_arguments, "Arguments");
      this.return_type = NullCheck.notNull(in_return_type, "Return type");
      this.external = NullCheck.notNull(in_external, "External");
    }

    @Override public
      <A, B, E extends Throwable, V extends UASTIFunctionVisitorType<A, B, E>>
      A
      functionVisitableAccept(
        final V v)
        throws E
    {
      v.functionVisitExternalPre(this);
      final List<B> args = new ArrayList<B>();
      for (final UASTIDFunctionArgument a : this.arguments) {
        final B x = v.functionVisitArgument(a);
        args.add(x);
      }
      return v.functionVisitExternal(args, this);
    }

    public List<UASTIDFunctionArgument> getArguments()
    {
      return this.arguments;
    }

    public UASTIDExternal getExternal()
    {
      return this.external;
    }

    @Override public TokenIdentifierLower getName()
    {
      return this.name;
    }

    public UASTITypePath getReturnType()
    {
      return this.return_type;
    }

    @Override public
      <A, E extends Throwable, V extends UASTIModuleLevelDeclarationVisitorType<A, E>>
      A
      moduleLevelVisitableAccept(
        final V v)
        throws E
    {
      return v.moduleVisitFunctionExternal(this);
    }
  }

  /**
   * Import declarations.
   */

  @EqualityReference public static final class UASTIDImport extends
    UASTIDeclaration
  {
    private final ModulePath                       path;
    private final OptionType<TokenIdentifierUpper> rename;

    public UASTIDImport(
      final ModulePath in_path,
      final OptionType<TokenIdentifierUpper> in_rename)
    {
      this.path = NullCheck.notNull(in_path, "Path");
      this.rename = NullCheck.notNull(in_rename, "Rename");
    }

    public ModulePath getPath()
    {
      return this.path;
    }

    public OptionType<TokenIdentifierUpper> getRename()
    {
      return this.rename;
    }
  }

  /**
   * Module declarations.
   */

  @EqualityReference public static final class UASTIDModule extends
    UASTIDeclarationUnitLevel implements UASTIModuleVisitableType
  {
    private final List<UASTIDeclarationModuleLevel> declarations;
    private final List<UASTIDImport>                imports;
    private final ModulePath                        path;

    public UASTIDModule(
      final ModulePath in_path,
      final List<UASTIDImport> in_imports,
      final List<UASTIDeclarationModuleLevel> in_declarations)
    {
      this.path = NullCheck.notNull(in_path, "Name");
      this.imports = NullCheck.notNull(in_imports, "Imports");
      this.declarations = NullCheck.notNull(in_declarations, "Declarations");
    }

    public List<UASTIDeclarationModuleLevel> getDeclarations()
    {
      return this.declarations;
    }

    public List<UASTIDImport> getImports()
    {
      return this.imports;
    }

    public ModulePath getPath()
    {
      return this.path;
    }

    @Override public
      <M, I, D, E extends Throwable, V extends UASTIModuleVisitorType<M, I, D, E>>
      M
      moduleVisitableAccept(
        final V v)
        throws E
    {
      final UASTIModuleLevelDeclarationVisitorType<D, E> dv =
        v.moduleVisitPre(this);

      final List<I> r_imports = new ArrayList<I>();
      for (final UASTIDImport i : this.imports) {
        final I ri = v.moduleVisitImport(i);
        r_imports.add(ri);
      }

      final List<D> r_declarations = new ArrayList<D>();
      for (final UASTIDeclarationModuleLevel d : this.declarations) {
        final D rd = d.moduleLevelVisitableAccept(dv);
        r_declarations.add(rd);
      }

      return v.moduleVisit(r_imports, r_declarations, this);
    }

    @Override public
      <A, E extends Throwable, V extends UASTIUnitLevelVisitorType<A, E>>
      A
      unitVisitableAccept(
        final V v)
        throws E
    {
      return v.unitVisitModule(this);
    }
  }

  /**
   * Package declarations.
   */

  @EqualityReference public static final class UASTIDPackage extends
    UASTIDeclarationUnitLevel
  {
    private final PackagePath path;

    public UASTIDPackage(
      final PackagePath in_path)
    {
      this.path = NullCheck.notNull(in_path, "Path");
    }

    public PackagePath getPath()
    {
      return this.path;
    }

    @Override public
      <A, E extends Throwable, V extends UASTIUnitLevelVisitorType<A, E>>
      A
      unitVisitableAccept(
        final V v)
        throws E
    {
      return v.unitVisitPackage(this);
    }
  }

  /**
   * The type of shader declarations.
   */

  @EqualityReference public static abstract class UASTIDShader extends
    UASTIDeclarationModuleLevel
  {
    private final TokenIdentifierLower name;

    protected UASTIDShader(
      final TokenIdentifierLower in_name)
    {
      this.name = NullCheck.notNull(in_name, "Name");
    }

    @Override public final TokenIdentifierLower getName()
    {
      return this.name;
    }
  }

  @EqualityReference public static final class UASTIDShaderFragment extends
    UASTIDShader implements UASTIFragmentShaderVisitableType
  {
    private final List<UASTIDShaderFragmentInput>            inputs;
    private final List<UASTIDShaderFragmentLocal>            locals;
    private final List<UASTIDShaderFragmentOutput>           outputs;
    private final List<UASTIDShaderFragmentParameter>        parameters;
    private final List<UASTIDShaderFragmentOutputAssignment> writes;

    public UASTIDShaderFragment(
      final TokenIdentifierLower name,
      final List<UASTIDShaderFragmentInput> in_inputs,
      final List<UASTIDShaderFragmentOutput> in_outputs,
      final List<UASTIDShaderFragmentParameter> in_parameters,
      final List<UASTIDShaderFragmentLocal> in_locals,
      final List<UASTIDShaderFragmentOutputAssignment> in_writes)
    {
      super(name);
      this.inputs = NullCheck.notNull(in_inputs, "Inputs");
      this.outputs = NullCheck.notNull(in_outputs, "Outputs");
      this.parameters = NullCheck.notNull(in_parameters, "Parameters");
      this.locals = NullCheck.notNull(in_locals, "Locals");
      this.writes = NullCheck.notNull(in_writes, "Writes");
    }

    @Override public
      <F, PI, PP, PO, L, O, E extends Throwable, V extends UASTIFragmentShaderVisitorType<F, PI, PP, PO, L, O, E>>
      F
      fragmentShaderVisitableAccept(
        final V v)
        throws E
    {
      final List<PI> r_inputs = new ArrayList<PI>();
      for (final UASTIDShaderFragmentInput i : this.inputs) {
        final PI ri = v.fragmentShaderVisitInput(i);
        r_inputs.add(ri);
      }

      final UASTIFragmentShaderOutputVisitorType<PO, E> ov =
        v.fragmentShaderVisitOutputsPre();

      final List<PO> r_outputs = new ArrayList<PO>();
      for (final UASTIDShaderFragmentOutput o : this.outputs) {
        final PO ro = o.fragmentShaderOutputVisitableAccept(ov);
        r_outputs.add(ro);
      }

      final List<PP> r_parameters = new ArrayList<PP>();
      for (final UASTIDShaderFragmentParameter p : this.parameters) {
        final PP rp = v.fragmentShaderVisitParameter(p);
        r_parameters.add(rp);
      }

      final UASTIFragmentShaderLocalVisitorType<L, E> lv =
        v.fragmentShaderVisitLocalsPre();

      final List<L> r_locals = new ArrayList<L>();
      for (final UASTIDShaderFragmentLocal l : this.locals) {
        final L rl = l.fragmentShaderLocalVisitableAccept(lv);
        r_locals.add(rl);
      }

      final List<O> r_assigns = new ArrayList<O>();
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

    public List<UASTIDShaderFragmentInput> getInputs()
    {
      return this.inputs;
    }

    public List<UASTIDShaderFragmentLocal> getLocals()
    {
      return this.locals;
    }

    public List<UASTIDShaderFragmentOutput> getOutputs()
    {
      return this.outputs;
    }

    public List<UASTIDShaderFragmentParameter> getParameters()
    {
      return this.parameters;
    }

    public List<UASTIDShaderFragmentOutputAssignment> getWrites()
    {
      return this.writes;
    }

    @Override public
      <A, E extends Throwable, V extends UASTIModuleLevelDeclarationVisitorType<A, E>>
      A
      moduleLevelVisitableAccept(
        final V v)
        throws E
    {
      return v.moduleVisitFragmentShader(this);
    }
  }

  @EqualityReference public static final class UASTIDShaderFragmentInput extends
    UASTIDShaderFragmentParameters
  {
    public UASTIDShaderFragmentInput(
      final TokenIdentifierLower name,
      final UASTITypePath type)
    {
      super(name, type);
    }
  }

  @EqualityReference public static abstract class UASTIDShaderFragmentLocal extends
    UASTIDeclarationShaderLevel implements
    UASTIFragmentShaderLocalVisitableType
  {
    // Nothing
  }

  @EqualityReference public static final class UASTIDShaderFragmentLocalDiscard extends
    UASTIDShaderFragmentLocal
  {
    private final TokenDiscard    discard;
    private final UASTIExpression expression;

    public UASTIDShaderFragmentLocalDiscard(
      final TokenDiscard in_discard,
      final UASTIExpression in_expression)
    {
      this.discard = NullCheck.notNull(in_discard, "Discard");
      this.expression = NullCheck.notNull(in_expression, "Expression");
    }

    @Override public
      <L, E extends Throwable, V extends UASTIFragmentShaderLocalVisitorType<L, E>>
      L
      fragmentShaderLocalVisitableAccept(
        final V v)
        throws E
    {
      return v.fragmentShaderVisitLocalDiscard(this);
    }

    public TokenDiscard getDiscard()
    {
      return this.discard;
    }

    public UASTIExpression getExpression()
    {
      return this.expression;
    }
  }

  @EqualityReference public static final class UASTIDShaderFragmentLocalValue extends
    UASTIDShaderFragmentLocal
  {
    private final UASTIDValueLocal value;

    public UASTIDShaderFragmentLocalValue(
      final UASTIDValueLocal in_value)
    {
      this.value = NullCheck.notNull(in_value, "Value");
    }

    @Override public
      <L, E extends Throwable, V extends UASTIFragmentShaderLocalVisitorType<L, E>>
      L
      fragmentShaderLocalVisitableAccept(
        final V v)
        throws E
    {
      return v.fragmentShaderVisitLocalValue(this);
    }

    public UASTIDValueLocal getValue()
    {
      return this.value;
    }
  }

  @EqualityReference public static final class UASTIDShaderFragmentOutputAssignment extends
    UASTIDeclarationShaderLevel
  {
    private final TokenIdentifierLower name;
    private final UASTIEVariable       variable;

    public UASTIDShaderFragmentOutputAssignment(
      final TokenIdentifierLower in_name,
      final UASTIEVariable in_variable)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.variable = NullCheck.notNull(in_variable, "Variable");
    }

    public TokenIdentifierLower getName()
    {
      return this.name;
    }

    public UASTIEVariable getVariable()
    {
      return this.variable;
    }
  }

  @EqualityReference public static abstract class UASTIDShaderFragmentOutput extends
    UASTIDShaderFragmentParameters implements
    UASTIFragmentShaderOutputVisitableType
  {
    public UASTIDShaderFragmentOutput(
      final TokenIdentifierLower name,
      final UASTITypePath type)
    {
      super(name, type);
    }
  }

  @EqualityReference public static final class UASTIDShaderFragmentOutputData extends
    UASTIDShaderFragmentOutput
  {
    private final int index;

    public UASTIDShaderFragmentOutputData(
      final TokenIdentifierLower name,
      final UASTITypePath type,
      final int in_index)
    {
      super(name, type);
      this.index = in_index;
    }

    @Override public
      <O, E extends Throwable, V extends UASTIFragmentShaderOutputVisitorType<O, E>>
      O
      fragmentShaderOutputVisitableAccept(
        final V v)
        throws E
    {
      return v.fragmentShaderVisitOutputData(this);
    }

    public int getIndex()
    {
      return this.index;
    }
  }

  @EqualityReference public static final class UASTIDShaderFragmentOutputDepth extends
    UASTIDShaderFragmentOutput
  {
    public UASTIDShaderFragmentOutputDepth(
      final TokenIdentifierLower name,
      final UASTITypePath type)
    {
      super(name, type);
    }

    @Override public
      <O, E extends Throwable, V extends UASTIFragmentShaderOutputVisitorType<O, E>>
      O
      fragmentShaderOutputVisitableAccept(
        final V v)
        throws E
    {
      return v.fragmentShaderVisitOutputDepth(this);
    }
  }

  @EqualityReference public static final class UASTIDShaderFragmentParameter extends
    UASTIDShaderFragmentParameters
  {
    public UASTIDShaderFragmentParameter(
      final TokenIdentifierLower name,
      final UASTITypePath type)
    {
      super(name, type);
    }
  }

  @EqualityReference public static abstract class UASTIDShaderFragmentParameters extends
    UASTIDShaderParameters
  {
    UASTIDShaderFragmentParameters(
      final TokenIdentifierLower name,
      final UASTITypePath type)
    {
      super(name, type);
    }
  }

  @EqualityReference public static abstract class UASTIDShaderParameters extends
    UASTIDeclarationShaderLevel
  {
    private final TokenIdentifierLower name;
    private final UASTITypePath        type;

    UASTIDShaderParameters(
      final TokenIdentifierLower in_name,
      final UASTITypePath in_type)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.type = NullCheck.notNull(in_type, "Type");
    }

    public final TokenIdentifierLower getName()
    {
      return this.name;
    }

    public final UASTITypePath getType()
    {
      return this.type;
    }
  }

  @EqualityReference public static final class UASTIDShaderProgram extends
    UASTIDShader
  {
    private final UASTIShaderPath fragment_shader;
    private final UASTIShaderPath vertex_shader;

    public UASTIDShaderProgram(
      final TokenIdentifierLower name,
      final UASTIShaderPath in_vertex_shader,
      final UASTIShaderPath in_fragment_shader)
    {
      super(name);
      this.vertex_shader =
        NullCheck.notNull(in_vertex_shader, "Vertex shader");
      this.fragment_shader =
        NullCheck.notNull(in_fragment_shader, "Fragment shader");
    }

    public UASTIShaderPath getFragmentShader()
    {
      return this.fragment_shader;
    }

    public UASTIShaderPath getVertexShader()
    {
      return this.vertex_shader;
    }

    @Override public
      <A, E extends Throwable, V extends UASTIModuleLevelDeclarationVisitorType<A, E>>
      A
      moduleLevelVisitableAccept(
        final V v)
        throws E
    {
      return v.moduleVisitProgramShader(this);
    }
  }

  @EqualityReference public static final class UASTIDShaderVertex extends
    UASTIDShader implements UASTIVertexShaderVisitableType
  {
    private final List<UASTIDShaderVertexInput>            inputs;
    private final List<UASTIDShaderVertexLocalValue>       locals;
    private final List<UASTIDShaderVertexOutput>           outputs;
    private final List<UASTIDShaderVertexParameter>        parameters;
    private final List<UASTIDShaderVertexOutputAssignment> writes;

    public UASTIDShaderVertex(
      final TokenIdentifierLower name,
      final List<UASTIDShaderVertexInput> in_inputs,
      final List<UASTIDShaderVertexOutput> in_outputs,
      final List<UASTIDShaderVertexParameter> in_parameters,
      final List<UASTIDShaderVertexLocalValue> in_locals,
      final List<UASTIDShaderVertexOutputAssignment> in_writes)
    {
      super(name);
      this.inputs = NullCheck.notNull(in_inputs, "Inputs");
      this.outputs = NullCheck.notNull(in_outputs, "Outputs");
      this.parameters = NullCheck.notNull(in_parameters, "Parameters");
      this.locals = NullCheck.notNull(in_locals, "Values");
      this.writes = NullCheck.notNull(in_writes, "Writes");
    }

    public List<UASTIDShaderVertexInput> getInputs()
    {
      return this.inputs;
    }

    public List<UASTIDShaderVertexLocalValue> getLocals()
    {
      return this.locals;
    }

    public List<UASTIDShaderVertexOutput> getOutputs()
    {
      return this.outputs;
    }

    public List<UASTIDShaderVertexParameter> getParameters()
    {
      return this.parameters;
    }

    public List<UASTIDShaderVertexOutputAssignment> getWrites()
    {
      return this.writes;
    }

    @Override public
      <A, E extends Throwable, V extends UASTIModuleLevelDeclarationVisitorType<A, E>>
      A
      moduleLevelVisitableAccept(
        final V v)
        throws E
    {
      return v.moduleVisitVertexShader(this);
    }

    @Override public
      <VS, PI, PP, PO, L, O, E extends Throwable, V extends UASTIVertexShaderVisitorType<VS, PI, PP, PO, L, O, E>>
      VS
      vertexShaderVisitableAccept(
        final V v)
        throws E
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

      final UASTIVertexShaderLocalVisitorType<L, E> lv =
        v.vertexShaderVisitLocalsPre();

      final List<L> r_locals = new ArrayList<L>();
      for (final UASTIDShaderVertexLocalValue l : this.locals) {
        final L rl = lv.vertexShaderVisitLocalValue(l);
        r_locals.add(rl);
      }

      final List<O> r_assigns = new ArrayList<O>();
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

  @EqualityReference public static final class UASTIDShaderVertexInput extends
    UASTIDShaderVertexParameters
  {
    public UASTIDShaderVertexInput(
      final TokenIdentifierLower name,
      final UASTITypePath type)
    {
      super(name, type);
    }
  }

  @EqualityReference public static final class UASTIDShaderVertexLocalValue extends
    UASTIDeclarationShaderLevel
  {
    private final UASTIDValueLocal value;

    public UASTIDShaderVertexLocalValue(
      final UASTIDValueLocal in_value)
    {
      this.value = NullCheck.notNull(in_value, "Value");
    }

    public UASTIDValueLocal getValue()
    {
      return this.value;
    }
  }

  @EqualityReference public static final class UASTIDShaderVertexOutput extends
    UASTIDShaderVertexParameters
  {
    private final boolean main;

    public UASTIDShaderVertexOutput(
      final TokenIdentifierLower name,
      final UASTITypePath type,
      final boolean in_main)
    {
      super(name, type);
      this.main = in_main;
    }

    public boolean isMain()
    {
      return this.main;
    }
  }

  @EqualityReference public static final class UASTIDShaderVertexOutputAssignment extends
    UASTIDeclarationShaderLevel
  {
    private final TokenIdentifierLower name;
    private final UASTIEVariable       variable;

    public UASTIDShaderVertexOutputAssignment(
      final TokenIdentifierLower in_name,
      final UASTIEVariable in_variable)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.variable = NullCheck.notNull(in_variable, "Variable");
    }

    public TokenIdentifierLower getName()
    {
      return this.name;
    }

    public UASTIEVariable getVariable()
    {
      return this.variable;
    }
  }

  @EqualityReference public static final class UASTIDShaderVertexParameter extends
    UASTIDShaderVertexParameters
  {
    public UASTIDShaderVertexParameter(
      final TokenIdentifierLower name,
      final UASTITypePath type)
    {
      super(name, type);
    }
  }

  @EqualityReference public static abstract class UASTIDShaderVertexParameters extends
    UASTIDShaderParameters
  {
    UASTIDShaderVertexParameters(
      final TokenIdentifierLower name,
      final UASTITypePath type)
    {
      super(name, type);
    }
  }

  /**
   * The type of term declarations.
   */

  @EqualityReference public static abstract class UASTIDTerm extends
    UASTIDeclarationModuleLevel
  {
    // Nothing
  }

  /**
   * The type of local term declarations.
   */

  @EqualityReference public static abstract class UASTIDTermLocal extends
    UASTIDeclarationLocalLevel
  {
    // Nothing
  }

  /**
   * The type of type declarations.
   */

  @EqualityReference public static abstract class UASTIDType extends
    UASTIDeclarationModuleLevel
  {
    // Nothing
  }

  /**
   * Record declarations.
   */

  @EqualityReference public static final class UASTIDTypeRecord extends
    UASTIDType implements UASTIDRecordVisitableType
  {
    private final List<UASTIDTypeRecordField> fields;
    private final TokenIdentifierLower        name;

    public UASTIDTypeRecord(
      final TokenIdentifierLower in_name,
      final List<UASTIDTypeRecordField> in_fields)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.fields = NullCheck.notNull(in_fields, "Fields");
    }

    public List<UASTIDTypeRecordField> getFields()
    {
      return this.fields;
    }

    @Override public TokenIdentifierLower getName()
    {
      return this.name;
    }

    @Override public
      <A, E extends Throwable, V extends UASTIModuleLevelDeclarationVisitorType<A, E>>
      A
      moduleLevelVisitableAccept(
        final V v)
        throws E
    {
      return v.moduleVisitTypeRecord(this);
    }

    @Override public
      <A, B, E extends Throwable, V extends UASTIDRecordVisitorType<A, B, E>>
      A
      recordTypeVisitableAccept(
        final V v)
        throws E
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

  @EqualityReference public static final class UASTIDTypeRecordField
  {
    private final TokenIdentifierLower name;
    private final UASTITypePath        type;

    public UASTIDTypeRecordField(
      final TokenIdentifierLower in_name,
      final UASTITypePath in_type)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.type = NullCheck.notNull(in_type, "Type");
    }

    public TokenIdentifierLower getName()
    {
      return this.name;
    }

    public UASTITypePath getType()
    {
      return this.type;
    }
  }

  /**
   * Value declarations.
   */

  @EqualityReference public static abstract class UASTIDValue extends
    UASTIDTerm implements UASTIValueVisitableType
  {
    // Nothing
  }

  @EqualityReference public static final class UASTIDValueDefined extends
    UASTIDValue
  {
    private final OptionType<UASTITypePath> ascription;
    private final UASTIExpression           expression;
    private final TokenIdentifierLower      name;

    public UASTIDValueDefined(
      final TokenIdentifierLower in_name,
      final OptionType<UASTITypePath> in_ascription,
      final UASTIExpression in_expression)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.ascription = NullCheck.notNull(in_ascription, "Ascription");
      this.expression = NullCheck.notNull(in_expression, "Expression");
    }

    public OptionType<UASTITypePath> getAscription()
    {
      return this.ascription;
    }

    public UASTIExpression getExpression()
    {
      return this.expression;
    }

    @Override public TokenIdentifierLower getName()
    {
      return this.name;
    }

    @Override public
      <A, E extends Throwable, V extends UASTIModuleLevelDeclarationVisitorType<A, E>>
      A
      moduleLevelVisitableAccept(
        final V v)
        throws E
    {
      return v.moduleVisitValue(this);
    }

    @Override public
      <A, E extends Throwable, V extends UASTIValueVisitorType<A, E>>
      A
      valueVisitableAccept(
        final V v)
        throws E
    {
      return v.valueVisitDefined(this);
    }
  }

  /**
   * External value declarations.
   */

  @EqualityReference public static final class UASTIDValueExternal extends
    UASTIDValue
  {
    private final OptionType<UASTITypePath> ascription;
    private final UASTIDExternal            external;
    private final TokenIdentifierLower      name;

    public UASTIDValueExternal(
      final TokenIdentifierLower in_name,
      final OptionType<UASTITypePath> in_ascription,
      final UASTIDExternal in_external)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.ascription = NullCheck.notNull(in_ascription, "Ascription");
      this.external = NullCheck.notNull(in_external, "External");
    }

    public OptionType<UASTITypePath> getAscription()
    {
      return this.ascription;
    }

    public UASTIDExternal getExternal()
    {
      return this.external;
    }

    @Override public TokenIdentifierLower getName()
    {
      return this.name;
    }

    @Override public
      <A, E extends Throwable, V extends UASTIModuleLevelDeclarationVisitorType<A, E>>
      A
      moduleLevelVisitableAccept(
        final V v)
        throws E
    {
      return v.moduleVisitValueExternal(this);
    }

    @Override public
      <A, E extends Throwable, V extends UASTIValueVisitorType<A, E>>
      A
      valueVisitableAccept(
        final V v)
        throws E
    {
      return v.valueVisitExternal(this);
    }
  }

  /**
   * Local value declarations (let).
   */

  @EqualityReference public static final class UASTIDValueLocal extends
    UASTIDTermLocal
  {
    private final OptionType<UASTITypePath> ascription;
    private final UASTIExpression           expression;
    private final TokenIdentifierLower      name;

    public UASTIDValueLocal(
      final TokenIdentifierLower in_name,
      final OptionType<UASTITypePath> in_ascription,
      final UASTIExpression in_expression)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.ascription = NullCheck.notNull(in_ascription, "Ascription");
      this.expression = NullCheck.notNull(in_expression, "Expression");
    }

    public OptionType<UASTITypePath> getAscription()
    {
      return this.ascription;
    }

    public UASTIExpression getExpression()
    {
      return this.expression;
    }

    @Override public TokenIdentifierLower getName()
    {
      return this.name;
    }

    @Override public
      <A, E extends Throwable, V extends UASTILocalLevelVisitorType<A, E>>
      A
      localVisitableAccept(
        final V v)
        throws E
    {
      return v.localVisitValueLocal(this);
    }
  }
}
