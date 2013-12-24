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
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFIT WHETHER IN AN ACTION
 * OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN
 * CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.jparasol.untyped.ast.checked;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.io7m.jparasol.untyped.ast.checked.UASTCExpression.UASTCEVariable;

public abstract class UASTCDeclaration
{
  /**
   * The type of local declarations.
   */

  public static abstract class UASTCDeclarationLocalLevel extends
    UASTCDeclaration implements UASTCLocalLevelVisitable
  {
    public abstract @Nonnull TokenIdentifierLower getName();
  }

  /**
   * The type of module-level declarations.
   */

  public static abstract class UASTCDeclarationModuleLevel extends
    UASTCDeclaration
  {
    public abstract @Nonnull TokenIdentifierLower getName();
  }

  /**
   * The type of shader-level declarations.
   */

  public static abstract class UASTCDeclarationShaderLevel extends
    UASTCDeclaration
  {
    // Nothing
  }

  /**
   * The type of unit-level declarations.
   */

  public static abstract class UASTCDeclarationUnitLevel extends
    UASTCDeclaration implements UASTCUnitLevelVisitable
  {
    // Nothing
  }

  /**
   * The type of function declarations.
   */

  public static abstract class UASTCDFunction extends UASTCDTerm implements
    UASTCFunctionVisitable
  {
    // Nothing
  }

  public static final class UASTCDFunctionArgument
  {
    private final @Nonnull TokenIdentifierLower name;
    private final @Nonnull UASTCTypePath        type;

    public UASTCDFunctionArgument(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTCTypePath type)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.type = Constraints.constrainNotNull(type, "Type");
    }

    public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    public @Nonnull UASTCTypePath getType()
    {
      return this.type;
    }
  }

  /**
   * Fully defined functions.
   */

  public static final class UASTCDFunctionDefined extends UASTCDFunction
  {
    private final @Nonnull List<UASTCDFunctionArgument> arguments;
    private final @Nonnull UASTCExpression              body;
    private final @Nonnull TokenIdentifierLower         name;
    private final @Nonnull UASTCTypePath                return_type;

    public UASTCDFunctionDefined(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull List<UASTCDFunctionArgument> arguments,
      final @Nonnull UASTCTypePath return_type,
      final @Nonnull UASTCExpression body)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.arguments = Constraints.constrainNotNull(arguments, "Arguments");
      this.return_type =
        Constraints.constrainNotNull(return_type, "Return type");
      this.body = Constraints.constrainNotNull(body, "Body");
    }

    @Override public
      <A, B, E extends Throwable, V extends UASTCFunctionVisitor<A, B, E>>
      A
      functionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.functionVisitDefinedPre(this);
      final List<B> args = new ArrayList<B>();
      for (final UASTCDFunctionArgument a : this.arguments) {
        final B x = v.functionVisitArgument(a);
        args.add(x);
      }
      return v.functionVisitDefined(args, this);
    }

    public @Nonnull List<UASTCDFunctionArgument> getArguments()
    {
      return this.arguments;
    }

    public @Nonnull UASTCExpression getBody()
    {
      return this.body;
    }

    @Override public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    public @Nonnull UASTCTypePath getReturnType()
    {
      return this.return_type;
    }

    @Override public
      <T, E extends Throwable, V extends UASTCTermVisitor<T, E>>
      T
      termVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.termVisitFunctionDefined(this);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTCDFunctionDefined ");
      builder.append(this.name);
      builder.append(" ");
      builder.append(this.arguments);
      builder.append(" ");
      builder.append(this.return_type);
      builder.append(" ");
      builder.append(this.body);
      builder.append("]");
      return builder.toString();
    }
  }

  /**
   * Functions with external declarations (private FFI).
   */

  public static final class UASTCDFunctionExternal extends UASTCDFunction
  {
    private final @Nonnull List<UASTCDFunctionArgument> arguments;
    private final @Nonnull TokenIdentifierLower         external;
    private final @Nonnull TokenIdentifierLower         name;
    private final @Nonnull UASTCTypePath                return_type;

    public UASTCDFunctionExternal(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull List<UASTCDFunctionArgument> arguments,
      final @Nonnull UASTCTypePath return_type,
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
      <A, B, E extends Throwable, V extends UASTCFunctionVisitor<A, B, E>>
      A
      functionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.functionVisitExternalPre(this);
      final List<B> args = new ArrayList<B>();
      for (final UASTCDFunctionArgument a : this.arguments) {
        final B x = v.functionVisitArgument(a);
        args.add(x);
      }
      return v.functionVisitExternal(args, this);
    }

    public @Nonnull List<UASTCDFunctionArgument> getArguments()
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

    public @Nonnull UASTCTypePath getReturnType()
    {
      return this.return_type;
    }

    @Override public
      <T, E extends Throwable, V extends UASTCTermVisitor<T, E>>
      T
      termVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.termVisitFunctionExternal(this);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTCDFunctionDefined ");
      builder.append(this.name);
      builder.append(" ");
      builder.append(this.arguments);
      builder.append(" ");
      builder.append(this.return_type);
      builder.append(" ");
      builder.append(this.external.getActual());
      builder.append("]");
      return builder.toString();
    }
  }

  /**
   * Import declarations.
   */

  public static final class UASTCDImport extends UASTCDeclaration
  {
    private final @Nonnull ModulePath                   path;
    private final @Nonnull Option<TokenIdentifierUpper> rename;

    public UASTCDImport(
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
      final UASTCDImport other = (UASTCDImport) obj;
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

  public static final class UASTCDModule extends UASTCDeclarationUnitLevel implements
    UASTCModuleVisitable
  {
    private final @Nonnull List<UASTCDeclarationModuleLevel> declarations;
    private final @Nonnull Map<ModulePathFlat, UASTCDImport> imported_modules;
    private final @Nonnull Map<String, UASTCDImport>         imported_names;
    private final @Nonnull Map<String, UASTCDImport>         imported_renames;
    private final @Nonnull List<UASTCDImport>                imports;
    private final @Nonnull ModulePath                        path;
    private final @Nonnull Map<String, UASTCDShader>         shaders;
    private final @Nonnull Map<String, UASTCDTerm>           terms;
    private final @Nonnull Map<String, UASTCDType>           types;

    public UASTCDModule(
      final @Nonnull ModulePath path,
      final @Nonnull List<UASTCDImport> imports,
      final @Nonnull Map<ModulePathFlat, UASTCDImport> imported_modules,
      final @Nonnull Map<String, UASTCDImport> imported_names,
      final @Nonnull Map<String, UASTCDImport> imported_renames,
      final @Nonnull List<UASTCDeclarationModuleLevel> declarations,
      final @Nonnull Map<String, UASTCDTerm> terms,
      final @Nonnull Map<String, UASTCDType> types,
      final @Nonnull Map<String, UASTCDShader> shaders)
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

    public @Nonnull List<UASTCDeclarationModuleLevel> getDeclarations()
    {
      return this.declarations;
    }

    public @Nonnull Map<ModulePathFlat, UASTCDImport> getImportedModules()
    {
      return this.imported_modules;
    }

    public @Nonnull Map<String, UASTCDImport> getImportedNames()
    {
      return this.imported_names;
    }

    public @Nonnull Map<String, UASTCDImport> getImportedRenames()
    {
      return this.imported_renames;
    }

    public @Nonnull List<UASTCDImport> getImports()
    {
      return this.imports;
    }

    public @Nonnull ModulePath getPath()
    {
      return this.path;
    }

    public @Nonnull Map<String, UASTCDShader> getShaders()
    {
      return this.shaders;
    }

    public @Nonnull Map<String, UASTCDTerm> getTerms()
    {
      return this.terms;
    }

    public @Nonnull Map<String, UASTCDType> getTypes()
    {
      return this.types;
    }

    @Override public
      <M, I, D, DTE extends D, DTY extends D, DS extends D, E extends Throwable, V extends UASTCModuleVisitor<M, I, D, DTE, DTY, DS, E>>
      M
      moduleVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      final List<I> r_imports = new ArrayList<I>();
      for (final UASTCDImport i : this.imports) {
        final I r = v.moduleVisitImport(i);
        r_imports.add(r);
      }

      final List<D> r_decls = new ArrayList<D>();
      final Map<String, DTY> r_types = new HashMap<String, DTY>();
      final Map<String, DTE> r_terms = new HashMap<String, DTE>();
      final Map<String, DS> r_shaders = new HashMap<String, DS>();

      {
        final UASTCTypeVisitor<DTY, E> tv = v.moduleTypesPre(this);
        for (final String k : this.types.keySet()) {
          final UASTCDType ty = this.types.get(k);
          final DTY r = ty.typeVisitableAccept(tv);
          r_types.put(k, r);
          r_decls.add(r);
        }
      }

      {
        final UASTCTermVisitor<DTE, E> tv = v.moduleTermsPre(this);
        for (final String k : this.terms.keySet()) {
          final UASTCDTerm t = this.terms.get(k);
          final DTE r = t.termVisitableAccept(tv);
          r_terms.put(k, r);
          r_decls.add(r);
        }
      }

      {
        final UASTCShaderVisitor<DS, E> tv = v.moduleShadersPre(this);
        for (final String k : this.shaders.keySet()) {
          final UASTCDShader t = this.shaders.get(k);
          final DS r = t.shaderVisitableAccept(tv);
          r_shaders.put(k, r);
          r_decls.add(r);
        }
      }

      return v.moduleVisit(
        r_imports,
        r_decls,
        r_terms,
        r_types,
        r_shaders,
        this);
    }

    @Override public
      <A, E extends Throwable, V extends UASTCUnitLevelVisitor<A, E>>
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

  public static final class UASTCDPackage extends UASTCDeclarationUnitLevel
  {
    private final @Nonnull PackagePath path;

    public UASTCDPackage(
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
      <A, E extends Throwable, V extends UASTCUnitLevelVisitor<A, E>>
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

  public static abstract class UASTCDShader extends
    UASTCDeclarationModuleLevel implements UASTCShaderVisitable
  {
    private final @Nonnull TokenIdentifierLower name;

    protected UASTCDShader(
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

  public static final class UASTCDShaderFragment extends UASTCDShader implements
    UASTCFragmentShaderVisitable
  {
    private final @Nonnull List<UASTCDShaderFragmentInput>            inputs;
    private final @Nonnull List<UASTCDShaderFragmentLocal>            locals;
    private final @Nonnull List<UASTCDShaderFragmentOutput>           outputs;
    private final @Nonnull List<UASTCDShaderFragmentParameter>        parameters;
    private final @Nonnull List<UASTCDShaderFragmentOutputAssignment> writes;

    public UASTCDShaderFragment(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull List<UASTCDShaderFragmentInput> inputs,
      final @Nonnull List<UASTCDShaderFragmentOutput> outputs,
      final @Nonnull List<UASTCDShaderFragmentParameter> parameters,
      final @Nonnull List<UASTCDShaderFragmentLocal> locals,
      final @Nonnull List<UASTCDShaderFragmentOutputAssignment> writes)
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
      <F, PI, PP, PO, L, O, E extends Throwable, V extends UASTCFragmentShaderVisitor<F, PI, PP, PO, L, O, E>>
      F
      fragmentShaderVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      final List<PI> r_inputs = new ArrayList<PI>();
      for (final UASTCDShaderFragmentInput i : this.inputs) {
        final PI ri = v.fragmentShaderVisitInput(i);
        r_inputs.add(ri);
      }

      final List<PO> r_outputs = new ArrayList<PO>();
      for (final UASTCDShaderFragmentOutput o : this.outputs) {
        final PO ro = v.fragmentShaderVisitOutput(o);
        r_outputs.add(ro);
      }

      final List<PP> r_parameters = new ArrayList<PP>();
      for (final UASTCDShaderFragmentParameter p : this.parameters) {
        final PP rp = v.fragmentShaderVisitParameter(p);
        r_parameters.add(rp);
      }

      final UASTCFragmentShaderLocalVisitor<L, E> lv =
        v.fragmentShaderVisitLocalsPre();

      final ArrayList<L> r_locals = new ArrayList<L>();
      for (final UASTCDShaderFragmentLocal l : this.locals) {
        final L rl = l.fragmentShaderLocalVisitableAccept(lv);
        r_locals.add(rl);
      }

      final ArrayList<O> r_assigns = new ArrayList<O>();
      for (final UASTCDShaderFragmentOutputAssignment w : this.writes) {
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

    public @Nonnull List<UASTCDShaderFragmentInput> getInputs()
    {
      return this.inputs;
    }

    public @Nonnull List<UASTCDShaderFragmentLocal> getLocals()
    {
      return this.locals;
    }

    public @Nonnull List<UASTCDShaderFragmentOutput> getOutputs()
    {
      return this.outputs;
    }

    public @Nonnull List<UASTCDShaderFragmentParameter> getParameters()
    {
      return this.parameters;
    }

    public @Nonnull List<UASTCDShaderFragmentOutputAssignment> getWrites()
    {
      return this.writes;
    }

    @Override public
      <T, E extends Throwable, V extends UASTCShaderVisitor<T, E>>
      T
      shaderVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.moduleVisitFragmentShader(this);
    }
  }

  public static final class UASTCDShaderFragmentInput extends
    UASTCDShaderFragmentParameters
  {
    public UASTCDShaderFragmentInput(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTCTypePath type)
      throws ConstraintError
    {
      super(name, type);
    }
  }

  public static abstract class UASTCDShaderFragmentLocal extends
    UASTCDeclarationShaderLevel implements UASTCFragmentShaderLocalVisitable
  {
    // Nothing
  }

  public static final class UASTCDShaderFragmentLocalDiscard extends
    UASTCDShaderFragmentLocal
  {
    private final @Nonnull TokenDiscard    discard;
    private final @Nonnull UASTCExpression expression;

    public UASTCDShaderFragmentLocalDiscard(
      final TokenDiscard discard,
      final UASTCExpression expression)
      throws ConstraintError
    {
      this.discard = Constraints.constrainNotNull(discard, "Discard");
      this.expression =
        Constraints.constrainNotNull(expression, "Expression");
    }

    @Override public
      <L, E extends Throwable, V extends UASTCFragmentShaderLocalVisitor<L, E>>
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

    public @Nonnull UASTCExpression getExpression()
    {
      return this.expression;
    }
  }

  public static final class UASTCDShaderFragmentLocalValue extends
    UASTCDShaderFragmentLocal
  {
    private final @Nonnull UASTCDValueLocal value;

    public UASTCDShaderFragmentLocalValue(
      final @Nonnull UASTCDValueLocal value)
      throws ConstraintError
    {
      this.value = Constraints.constrainNotNull(value, "Value");
    }

    @Override public
      <L, E extends Throwable, V extends UASTCFragmentShaderLocalVisitor<L, E>>
      L
      fragmentShaderLocalVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.fragmentShaderVisitLocalValue(this);
    }

    public @Nonnull UASTCDValueLocal getValue()
    {
      return this.value;
    }
  }

  public static final class UASTCDShaderFragmentOutput extends
    UASTCDShaderFragmentParameters
  {
    private final int index;

    public UASTCDShaderFragmentOutput(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTCTypePath type,
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

  public static final class UASTCDShaderFragmentOutputAssignment extends
    UASTCDeclarationShaderLevel
  {
    private final @Nonnull TokenIdentifierLower name;
    private final @Nonnull UASTCEVariable       variable;

    public UASTCDShaderFragmentOutputAssignment(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTCEVariable variable)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.variable = Constraints.constrainNotNull(variable, "Variable");
    }

    public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    public @Nonnull UASTCEVariable getVariable()
    {
      return this.variable;
    }
  }

  public static final class UASTCDShaderFragmentParameter extends
    UASTCDShaderFragmentParameters
  {
    public UASTCDShaderFragmentParameter(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTCTypePath type)
      throws ConstraintError
    {
      super(name, type);
    }
  }

  public static abstract class UASTCDShaderFragmentParameters extends
    UASTCDShaderParameters
  {
    UASTCDShaderFragmentParameters(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTCTypePath type)
      throws ConstraintError
    {
      super(name, type);
    }
  }

  public static abstract class UASTCDShaderParameters extends
    UASTCDeclarationShaderLevel
  {
    private final @Nonnull TokenIdentifierLower name;
    private final @Nonnull UASTCTypePath        type;

    UASTCDShaderParameters(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTCTypePath type)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.type = Constraints.constrainNotNull(type, "Type");
    }

    public final @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    public final @Nonnull UASTCTypePath getType()
    {
      return this.type;
    }
  }

  public static final class UASTCDShaderProgram extends UASTCDShader
  {
    private final @Nonnull UASTCShaderPath fragment_shader;
    private final @Nonnull UASTCShaderPath vertex_shader;

    public UASTCDShaderProgram(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTCShaderPath vertex_shader,
      final @Nonnull UASTCShaderPath fragment_shader)
      throws ConstraintError
    {
      super(name);
      this.vertex_shader =
        Constraints.constrainNotNull(vertex_shader, "Vertex shader");
      this.fragment_shader =
        Constraints.constrainNotNull(fragment_shader, "Fragment shader");
    }

    public @Nonnull UASTCShaderPath getFragmentShader()
    {
      return this.fragment_shader;
    }

    public @Nonnull UASTCShaderPath getVertexShader()
    {
      return this.vertex_shader;
    }

    @Override public
      <T, E extends Throwable, V extends UASTCShaderVisitor<T, E>>
      T
      shaderVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.moduleVisitProgramShader(this);
    }
  }

  public static final class UASTCDShaderVertex extends UASTCDShader implements
    UASTCVertexShaderVisitable
  {
    private final @Nonnull List<UASTCDShaderVertexInput>            inputs;
    private final @Nonnull List<UASTCDShaderVertexLocalValue>       locals;
    private final @Nonnull List<UASTCDShaderVertexOutput>           outputs;
    private final @Nonnull List<UASTCDShaderVertexParameter>        parameters;
    private final @Nonnull List<UASTCDShaderVertexOutputAssignment> writes;

    public UASTCDShaderVertex(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull List<UASTCDShaderVertexInput> inputs,
      final @Nonnull List<UASTCDShaderVertexOutput> outputs,
      final @Nonnull List<UASTCDShaderVertexParameter> parameters,
      final @Nonnull List<UASTCDShaderVertexLocalValue> locals,
      final @Nonnull List<UASTCDShaderVertexOutputAssignment> writes)
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

    public @Nonnull List<UASTCDShaderVertexInput> getInputs()
    {
      return this.inputs;
    }

    public @Nonnull List<UASTCDShaderVertexLocalValue> getLocals()
    {
      return this.locals;
    }

    public @Nonnull List<UASTCDShaderVertexOutput> getOutputs()
    {
      return this.outputs;
    }

    public @Nonnull List<UASTCDShaderVertexParameter> getParameters()
    {
      return this.parameters;
    }

    public @Nonnull List<UASTCDShaderVertexOutputAssignment> getWrites()
    {
      return this.writes;
    }

    @Override public
      <T, E extends Throwable, V extends UASTCShaderVisitor<T, E>>
      T
      shaderVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.moduleVisitVertexShader(this);
    }

    @Override public
      <VS, PI, PP, PO, L, O, E extends Throwable, V extends UASTCVertexShaderVisitor<VS, PI, PP, PO, L, O, E>>
      VS
      vertexShaderVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      final List<PI> r_inputs = new ArrayList<PI>();
      for (final UASTCDShaderVertexInput i : this.inputs) {
        final PI ri = v.vertexShaderVisitInput(i);
        r_inputs.add(ri);
      }

      final List<PO> r_outputs = new ArrayList<PO>();
      for (final UASTCDShaderVertexOutput o : this.outputs) {
        final PO ro = v.vertexShaderVisitOutput(o);
        r_outputs.add(ro);
      }

      final List<PP> r_parameters = new ArrayList<PP>();
      for (final UASTCDShaderVertexParameter p : this.parameters) {
        final PP rp = v.vertexShaderVisitParameter(p);
        r_parameters.add(rp);
      }

      final UASTCVertexShaderLocalVisitor<L, E> lv =
        v.vertexShaderVisitLocalsPre();

      final ArrayList<L> r_locals = new ArrayList<L>();
      for (final UASTCDShaderVertexLocalValue l : this.locals) {
        final L rl = lv.vertexShaderVisitLocalValue(l);
        r_locals.add(rl);
      }

      final ArrayList<O> r_assigns = new ArrayList<O>();
      for (final UASTCDShaderVertexOutputAssignment w : this.writes) {
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

  public static final class UASTCDShaderVertexInput extends
    UASTCDShaderVertexParameters
  {
    public UASTCDShaderVertexInput(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTCTypePath type)
      throws ConstraintError
    {
      super(name, type);
    }
  }

  public static final class UASTCDShaderVertexLocalValue extends
    UASTCDeclarationShaderLevel
  {
    private final @Nonnull UASTCDValueLocal value;

    public UASTCDShaderVertexLocalValue(
      final @Nonnull UASTCDValueLocal value)
      throws ConstraintError
    {
      this.value = Constraints.constrainNotNull(value, "Value");
    }

    public @Nonnull UASTCDValueLocal getValue()
    {
      return this.value;
    }
  }

  public static final class UASTCDShaderVertexOutput extends
    UASTCDShaderVertexParameters
  {
    public UASTCDShaderVertexOutput(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTCTypePath type)
      throws ConstraintError
    {
      super(name, type);
    }
  }

  public static final class UASTCDShaderVertexOutputAssignment extends
    UASTCDeclarationShaderLevel
  {
    private final @Nonnull TokenIdentifierLower name;
    private final @Nonnull UASTCEVariable       variable;

    public UASTCDShaderVertexOutputAssignment(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTCEVariable variable)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.variable = Constraints.constrainNotNull(variable, "Variable");
    }

    public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    public @Nonnull UASTCEVariable getVariable()
    {
      return this.variable;
    }
  }

  public static final class UASTCDShaderVertexParameter extends
    UASTCDShaderVertexParameters
  {
    public UASTCDShaderVertexParameter(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTCTypePath type)
      throws ConstraintError
    {
      super(name, type);
    }
  }

  public static abstract class UASTCDShaderVertexParameters extends
    UASTCDShaderParameters
  {
    UASTCDShaderVertexParameters(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTCTypePath type)
      throws ConstraintError
    {
      super(name, type);
    }
  }

  /**
   * The type of term declarations.
   */

  public static abstract class UASTCDTerm extends UASTCDeclarationModuleLevel implements
    UASTCTermVisitable
  {
    // Nothing
  }

  /**
   * The type of local term declarations.
   */

  public static abstract class UASTCDTermLocal extends
    UASTCDeclarationLocalLevel
  {
    // Nothing
  }

  /**
   * The type of type declarations.
   */

  public static abstract class UASTCDType extends UASTCDeclarationModuleLevel implements
    UASTCTypeVisitable
  {
    // Nothing
  }

  /**
   * Record declarations.
   */

  public static final class UASTCDTypeRecord extends UASTCDType implements
    UASTCDRecordVisitable
  {
    private final @Nonnull List<UASTCDTypeRecordField> fields;
    private final @Nonnull TokenIdentifierLower        name;

    public UASTCDTypeRecord(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull List<UASTCDTypeRecordField> fields)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.fields = Constraints.constrainNotNull(fields, "Fields");
    }

    public @Nonnull List<UASTCDTypeRecordField> getFields()
    {
      return this.fields;
    }

    @Override public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    @Override public
      <A, B, E extends Throwable, V extends UASTCDRecordVisitor<A, B, E>>
      A
      recordTypeVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.recordTypeVisitPre(this);

      final List<B> new_fields = new ArrayList<B>();
      for (final UASTCDTypeRecordField f : this.fields) {
        final B x = v.recordTypeVisitField(f);
        new_fields.add(x);
      }

      return v.recordTypeVisit(new_fields, this);
    }

    @Override public
      <T, E extends Throwable, V extends UASTCTypeVisitor<T, E>>
      T
      typeVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.typeVisitTypeRecord(this);
    }
  }

  public static final class UASTCDTypeRecordField
  {
    private final @Nonnull TokenIdentifierLower name;
    private final @Nonnull UASTCTypePath        type;

    public UASTCDTypeRecordField(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTCTypePath type)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.type = Constraints.constrainNotNull(type, "Type");
    }

    public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    public @Nonnull UASTCTypePath getType()
    {
      return this.type;
    }
  }

  /**
   * Value declarations.
   */

  public static final class UASTCDValue extends UASTCDTerm
  {
    private final @Nonnull Option<UASTCTypePath> ascription;
    private final @Nonnull UASTCExpression       expression;
    private final @Nonnull TokenIdentifierLower  name;

    public UASTCDValue(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull Option<UASTCTypePath> ascription,
      final @Nonnull UASTCExpression expression)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.ascription =
        Constraints.constrainNotNull(ascription, "Ascription");
      this.expression =
        Constraints.constrainNotNull(expression, "Expression");
    }

    public @Nonnull Option<UASTCTypePath> getAscription()
    {
      return this.ascription;
    }

    public @Nonnull UASTCExpression getExpression()
    {
      return this.expression;
    }

    @Override public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    @Override public
      <T, E extends Throwable, V extends UASTCTermVisitor<T, E>>
      T
      termVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.termVisitValue(this);
    }
  }

  /**
   * Local value declarations (let).
   */

  public static final class UASTCDValueLocal extends UASTCDTermLocal
  {
    private final @Nonnull Option<UASTCTypePath> ascription;
    private final @Nonnull UASTCExpression       expression;
    private final @Nonnull TokenIdentifierLower  name;

    public UASTCDValueLocal(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull Option<UASTCTypePath> ascription,
      final @Nonnull UASTCExpression expression)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.ascription =
        Constraints.constrainNotNull(ascription, "Ascription");
      this.expression =
        Constraints.constrainNotNull(expression, "Expression");
    }

    public @Nonnull Option<UASTCTypePath> getAscription()
    {
      return this.ascription;
    }

    public @Nonnull UASTCExpression getExpression()
    {
      return this.expression;
    }

    @Override public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    @Override public
      <A, E extends Throwable, V extends UASTCLocalLevelVisitor<A, E>>
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