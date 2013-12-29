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

package com.io7m.jparasol.untyped.ast.unique_binders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Function;
import com.io7m.jaux.functional.Option;
import com.io7m.jaux.functional.Unit;
import com.io7m.jparasol.ModulePath;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.PackagePath;
import com.io7m.jparasol.lexer.Token.TokenDiscard;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.lexer.Token.TokenIdentifierUpper;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUEVariable;
import com.io7m.jparasol.untyped.ast.unique_binders.UniqueName.UniqueNameLocal;

public abstract class UASTUDeclaration
{
  /**
   * The type of local declarations.
   */

  public static abstract class UASTUDeclarationLocalLevel extends
    UASTUDeclaration implements UASTULocalLevelVisitable
  {
    public abstract @Nonnull UniqueNameLocal getName();
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
    private final @Nonnull UniqueNameLocal name;
    private final @Nonnull UASTUTypePath   type;

    public UASTUDFunctionArgument(
      final @Nonnull UniqueNameLocal name,
      final @Nonnull UASTUTypePath type)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.type = Constraints.constrainNotNull(type, "Type");
    }

    public @Nonnull UniqueNameLocal getName()
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
      builder.append(this.name);
      builder.append(" ");
      builder.append(this.type);
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

    @Override public
      <A, B, E extends Throwable, V extends UASTUFunctionVisitor<A, B, E>>
      A
      functionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.functionVisitDefinedPre(this);
      final List<B> args = new ArrayList<B>();
      for (final UASTUDFunctionArgument a : this.arguments) {
        final B x = v.functionVisitArgument(a);
        args.add(x);
      }
      return v.functionVisitDefined(args, this);
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
      <T, E extends Throwable, V extends UASTUTermVisitor<T, E>>
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
      builder.append("[UASTUDFunctionDefined ");
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

  public static final class UASTUDExternal
  {
    private final @Nonnull TokenIdentifierLower name;
    private final boolean                       vertex_shader_allowed;
    private final boolean                       fragment_shader_allowed;

    public UASTUDExternal(
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

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTUDExternal ");
      builder.append(this.name);
      builder.append(" ");
      builder.append(this.vertex_shader_allowed);
      builder.append(" ");
      builder.append(this.fragment_shader_allowed);
      builder.append("]");
      return builder.toString();
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

  public static final class UASTUDFunctionExternal extends UASTUDFunction
  {
    private final @Nonnull List<UASTUDFunctionArgument> arguments;
    private final @Nonnull UASTUDExternal               external;
    private final @Nonnull TokenIdentifierLower         name;
    private final @Nonnull UASTUTypePath                return_type;

    public UASTUDFunctionExternal(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull List<UASTUDFunctionArgument> arguments,
      final @Nonnull UASTUTypePath return_type,
      final @Nonnull UASTUDExternal external)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.arguments = Constraints.constrainNotNull(arguments, "Arguments");
      this.return_type =
        Constraints.constrainNotNull(return_type, "Return type");
      this.external = Constraints.constrainNotNull(external, "External");
    }

    @Override public
      <A, B, E extends Throwable, V extends UASTUFunctionVisitor<A, B, E>>
      A
      functionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.functionVisitExternalPre(this);
      final List<B> args = new ArrayList<B>();
      for (final UASTUDFunctionArgument a : this.arguments) {
        final B x = v.functionVisitArgument(a);
        args.add(x);
      }
      return v.functionVisitExternal(args, this);
    }

    public @Nonnull List<UASTUDFunctionArgument> getArguments()
    {
      return this.arguments;
    }

    public @Nonnull UASTUDExternal getExternal()
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
      <T, E extends Throwable, V extends UASTUTermVisitor<T, E>>
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
      builder.append("[UASTUDFunctionDefined ");
      builder.append(this.name);
      builder.append(" ");
      builder.append(this.arguments);
      builder.append(" ");
      builder.append(this.return_type);
      builder.append(" ");
      builder.append(this.external);
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
      final UASTUDImport other = (UASTUDImport) obj;
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

  public static final class UASTUDModule extends UASTUDeclarationUnitLevel implements
    UASTUModuleVisitable
  {
    private final @Nonnull List<UASTUDeclarationModuleLevel> declarations;
    private final @Nonnull Map<ModulePathFlat, UASTUDImport> imported_modules;
    private final @Nonnull Map<String, UASTUDImport>         imported_names;
    private final @Nonnull Map<String, UASTUDImport>         imported_renames;
    private final @Nonnull List<UASTUDImport>                imports;
    private final @Nonnull ModulePath                        path;
    private final @Nonnull Map<String, UASTUDShader>         shaders;
    private final @Nonnull Map<String, UASTUDTerm>           terms;
    private final @Nonnull Map<String, UASTUDType>           types;

    public UASTUDModule(
      final @Nonnull ModulePath path,
      final @Nonnull List<UASTUDImport> imports,
      final @Nonnull Map<ModulePathFlat, UASTUDImport> imported_modules,
      final @Nonnull Map<String, UASTUDImport> imported_names,
      final @Nonnull Map<String, UASTUDImport> imported_renames,
      final @Nonnull List<UASTUDeclarationModuleLevel> declarations,
      final @Nonnull Map<String, UASTUDTerm> terms,
      final @Nonnull Map<String, UASTUDType> types,
      final @Nonnull Map<String, UASTUDShader> shaders)
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

    public @Nonnull List<UASTUDeclarationModuleLevel> getDeclarations()
    {
      return this.declarations;
    }

    public @Nonnull Map<ModulePathFlat, UASTUDImport> getImportedModules()
    {
      return this.imported_modules;
    }

    public @Nonnull Map<String, UASTUDImport> getImportedNames()
    {
      return this.imported_names;
    }

    public @Nonnull Map<String, UASTUDImport> getImportedRenames()
    {
      return this.imported_renames;
    }

    public @Nonnull List<UASTUDImport> getImports()
    {
      return this.imports;
    }

    public @Nonnull ModulePath getPath()
    {
      return this.path;
    }

    public @Nonnull Map<String, UASTUDShader> getShaders()
    {
      return this.shaders;
    }

    public @Nonnull Map<String, UASTUDTerm> getTerms()
    {
      return this.terms;
    }

    public @Nonnull Map<String, UASTUDType> getTypes()
    {
      return this.types;
    }

    @Override public
      <M, I, D, DTE extends D, DTY extends D, DS extends D, E extends Throwable, V extends UASTUModuleVisitor<M, I, D, DTE, DTY, DS, E>>
      M
      moduleVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      final List<I> r_imports = new ArrayList<I>();
      for (final UASTUDImport i : this.imports) {
        final I r = v.moduleVisitImport(i);
        r_imports.add(r);
      }

      final List<D> r_decls = new ArrayList<D>();
      final Map<String, DTY> r_types = new HashMap<String, DTY>();
      final Map<String, DTE> r_terms = new HashMap<String, DTE>();
      final Map<String, DS> r_shaders = new HashMap<String, DS>();

      {
        final UASTUTypeVisitor<DTY, E> tv = v.moduleTypesPre(this);
        for (final String k : this.types.keySet()) {
          final UASTUDType ty = this.types.get(k);
          final DTY r = ty.typeVisitableAccept(tv);
          r_types.put(k, r);
          r_decls.add(r);
        }
      }

      {
        final UASTUTermVisitor<DTE, E> tv = v.moduleTermsPre(this);
        for (final String k : this.terms.keySet()) {
          final UASTUDTerm t = this.terms.get(k);
          final DTE r = t.termVisitableAccept(tv);
          r_terms.put(k, r);
          r_decls.add(r);
        }
      }

      {
        final UASTUShaderVisitor<DS, E> tv = v.moduleShadersPre(this);
        for (final String k : this.shaders.keySet()) {
          final UASTUDShader t = this.shaders.get(k);
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

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTUDModule ");
      builder.append(ModulePathFlat.fromModulePath(this.path).getActual());
      builder.append(" [\n");
      for (final UASTUDImport i : this.imports) {
        builder.append("  ");
        builder.append(i);
        builder.append("\n");
      }
      for (final UASTUDeclarationModuleLevel d : this.declarations) {
        builder.append("  ");
        builder.append(d);
        builder.append("\n");
      }
      builder.append("]]");
      return builder.toString();
    }

    @Override public
      <A, E extends Throwable, V extends UASTUUnitLevelVisitor<A, E>>
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
      <A, E extends Throwable, V extends UASTUUnitLevelVisitor<A, E>>
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

  public static abstract class UASTUDShader extends
    UASTUDeclarationModuleLevel implements UASTUShaderVisitable
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
      <F, PI, PP, PO, L, O, E extends Throwable, V extends UASTUFragmentShaderVisitor<F, PI, PP, PO, L, O, E>>
      F
      fragmentShaderVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      final List<PI> r_inputs = new ArrayList<PI>();
      for (final UASTUDShaderFragmentInput i : this.inputs) {
        final PI ri = v.fragmentShaderVisitInput(i);
        r_inputs.add(ri);
      }

      final List<PO> r_outputs = new ArrayList<PO>();
      for (final UASTUDShaderFragmentOutput o : this.outputs) {
        final PO ro = v.fragmentShaderVisitOutput(o);
        r_outputs.add(ro);
      }

      final List<PP> r_parameters = new ArrayList<PP>();
      for (final UASTUDShaderFragmentParameter p : this.parameters) {
        final PP rp = v.fragmentShaderVisitParameter(p);
        r_parameters.add(rp);
      }

      final UASTUFragmentShaderLocalVisitor<L, E> lv =
        v.fragmentShaderVisitLocalsPre();

      final ArrayList<L> r_locals = new ArrayList<L>();
      for (final UASTUDShaderFragmentLocal l : this.locals) {
        final L rl = l.fragmentShaderLocalVisitableAccept(lv);
        r_locals.add(rl);
      }

      final ArrayList<O> r_assigns = new ArrayList<O>();
      for (final UASTUDShaderFragmentOutputAssignment w : this.writes) {
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
      <T, E extends Throwable, V extends UASTUShaderVisitor<T, E>>
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
      builder.append("[UASTUDShaderFragment ");
      builder.append(this.getName().getActual());
      builder.append(" [\n");

      if (this.inputs.isEmpty() == false) {
        for (final UASTUDShaderFragmentInput i : this.inputs) {
          builder.append("    ");
          builder.append(i);
          builder.append("\n");
        }
        builder.append("\n");
      }

      if (this.parameters.isEmpty() == false) {
        for (final UASTUDShaderFragmentParameter i : this.parameters) {
          builder.append("    ");
          builder.append(i);
          builder.append("\n");
        }
        builder.append("\n");
      }

      if (this.outputs.isEmpty() == false) {
        for (final UASTUDShaderFragmentOutput o : this.outputs) {
          builder.append("    ");
          builder.append(o);
          builder.append("\n");
        }
        builder.append("\n");
      }

      if (this.locals.isEmpty() == false) {
        for (final UASTUDShaderFragmentLocal l : this.locals) {
          builder.append("    ");
          builder.append(l);
          builder.append("\n");
        }
        builder.append("\n");
      }

      if (this.writes.isEmpty() == false) {
        for (final UASTUDShaderFragmentOutputAssignment w : this.writes) {
          builder.append("    ");
          builder.append(w);
          builder.append("\n");
        }
      }
      builder.append("  ]\n");
      builder.append("]");
      return builder.toString();
    }

  }

  public static final class UASTUDShaderFragmentInput extends
    UASTUDShaderFragmentParameters
  {
    public UASTUDShaderFragmentInput(
      final @Nonnull UniqueNameLocal name,
      final @Nonnull UASTUTypePath type)
      throws ConstraintError
    {
      super(name, type);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTUDShaderFragmentInput ");
      builder.append(this.getName());
      builder.append(" ");
      builder.append(this.getType());
      builder.append("]");
      return builder.toString();
    }
  }

  public static abstract class UASTUDShaderFragmentLocal extends
    UASTUDeclarationShaderLevel implements UASTUFragmentShaderLocalVisitable
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
      <L, E extends Throwable, V extends UASTUFragmentShaderLocalVisitor<L, E>>
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
      <L, E extends Throwable, V extends UASTUFragmentShaderLocalVisitor<L, E>>
      L
      fragmentShaderLocalVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.fragmentShaderVisitLocalValue(this);
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
    private final int index;

    public UASTUDShaderFragmentOutput(
      final @Nonnull UniqueNameLocal name,
      final @Nonnull UASTUTypePath type,
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

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTUDShaderFragmentOutput ");
      builder.append(this.getName());
      builder.append(" ");
      builder.append(this.getType());
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
      builder.append(this.name);
      builder.append(" ");
      builder.append(this.variable);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class UASTUDShaderFragmentParameter extends
    UASTUDShaderFragmentParameters
  {
    public UASTUDShaderFragmentParameter(
      final @Nonnull UniqueNameLocal name,
      final @Nonnull UASTUTypePath type)
      throws ConstraintError
    {
      super(name, type);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTUDShaderFragmentParameter ");
      builder.append(this.getName());
      builder.append(" ");
      builder.append(this.getType());
      builder.append("]");
      return builder.toString();
    }
  }

  public static abstract class UASTUDShaderFragmentParameters extends
    UASTUDShaderParameters
  {
    UASTUDShaderFragmentParameters(
      final @Nonnull UniqueNameLocal name,
      final @Nonnull UASTUTypePath type)
      throws ConstraintError
    {
      super(name, type);
    }
  }

  public static abstract class UASTUDShaderParameters extends
    UASTUDeclarationShaderLevel
  {
    private final @Nonnull UniqueNameLocal name;
    private final @Nonnull UASTUTypePath   type;

    UASTUDShaderParameters(
      final @Nonnull UniqueNameLocal name,
      final @Nonnull UASTUTypePath type)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(name, "Name");
      this.type = Constraints.constrainNotNull(type, "Type");
    }

    public final @Nonnull UniqueNameLocal getName()
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
      <T, E extends Throwable, V extends UASTUShaderVisitor<T, E>>
      T
      shaderVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.moduleVisitProgramShader(this);
    }
  }

  public static final class UASTUDShaderVertex extends UASTUDShader implements
    UASTUVertexShaderVisitable
  {
    private final @Nonnull List<UASTUDShaderVertexInput>            inputs;
    private final @Nonnull List<UASTUDShaderVertexLocalValue>       locals;
    private final @Nonnull List<UASTUDShaderVertexOutput>           outputs;
    private final @Nonnull List<UASTUDShaderVertexParameter>        parameters;
    private final @Nonnull List<UASTUDShaderVertexOutputAssignment> writes;

    public UASTUDShaderVertex(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull List<UASTUDShaderVertexInput> inputs,
      final @Nonnull List<UASTUDShaderVertexOutput> outputs,
      final @Nonnull List<UASTUDShaderVertexParameter> parameters,
      final @Nonnull List<UASTUDShaderVertexLocalValue> locals,
      final @Nonnull List<UASTUDShaderVertexOutputAssignment> writes)
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

    public @Nonnull List<UASTUDShaderVertexInput> getInputs()
    {
      return this.inputs;
    }

    public @Nonnull List<UASTUDShaderVertexLocalValue> getLocals()
    {
      return this.locals;
    }

    public @Nonnull List<UASTUDShaderVertexOutput> getOutputs()
    {
      return this.outputs;
    }

    public @Nonnull List<UASTUDShaderVertexParameter> getParameters()
    {
      return this.parameters;
    }

    public @Nonnull List<UASTUDShaderVertexOutputAssignment> getWrites()
    {
      return this.writes;
    }

    @Override public
      <T, E extends Throwable, V extends UASTUShaderVisitor<T, E>>
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
      builder.append("[UASTUDShaderVertex ");
      builder.append(this.getName().getActual());
      builder.append(" [\n");

      if (this.inputs.isEmpty() == false) {
        for (final UASTUDShaderVertexInput i : this.inputs) {
          builder.append("    ");
          builder.append(i);
          builder.append("\n");
        }
        builder.append("\n");
      }

      if (this.parameters.isEmpty() == false) {
        for (final UASTUDShaderVertexParameter i : this.parameters) {
          builder.append("    ");
          builder.append(i);
          builder.append("\n");
        }
        builder.append("\n");
      }

      if (this.outputs.isEmpty() == false) {
        for (final UASTUDShaderVertexOutput o : this.outputs) {
          builder.append("    ");
          builder.append(o);
          builder.append("\n");
        }
        builder.append("\n");
      }

      if (this.locals.isEmpty() == false) {
        for (final UASTUDShaderVertexLocalValue l : this.locals) {
          builder.append("    ");
          builder.append(l);
          builder.append("\n");
        }
        builder.append("\n");
      }

      if (this.writes.isEmpty() == false) {
        for (final UASTUDShaderVertexOutputAssignment w : this.writes) {
          builder.append("    ");
          builder.append(w);
          builder.append("\n");
        }
      }
      builder.append("  ]\n");
      builder.append("]");
      return builder.toString();
    }

    @Override public
      <VS, PI, PP, PO, L, O, E extends Throwable, V extends UASTUVertexShaderVisitor<VS, PI, PP, PO, L, O, E>>
      VS
      vertexShaderVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      final List<PI> r_inputs = new ArrayList<PI>();
      for (final UASTUDShaderVertexInput i : this.inputs) {
        final PI ri = v.vertexShaderVisitInput(i);
        r_inputs.add(ri);
      }

      final List<PO> r_outputs = new ArrayList<PO>();
      for (final UASTUDShaderVertexOutput o : this.outputs) {
        final PO ro = v.vertexShaderVisitOutput(o);
        r_outputs.add(ro);
      }

      final List<PP> r_parameters = new ArrayList<PP>();
      for (final UASTUDShaderVertexParameter p : this.parameters) {
        final PP rp = v.vertexShaderVisitParameter(p);
        r_parameters.add(rp);
      }

      final UASTUVertexShaderLocalVisitor<L, E> lv =
        v.vertexShaderVisitLocalsPre();

      final ArrayList<L> r_locals = new ArrayList<L>();
      for (final UASTUDShaderVertexLocalValue l : this.locals) {
        final L rl = lv.vertexShaderVisitLocalValue(l);
        r_locals.add(rl);
      }

      final ArrayList<O> r_assigns = new ArrayList<O>();
      for (final UASTUDShaderVertexOutputAssignment w : this.writes) {
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

  public static final class UASTUDShaderVertexInput extends
    UASTUDShaderVertexParameters
  {
    public UASTUDShaderVertexInput(
      final @Nonnull UniqueNameLocal name,
      final @Nonnull UASTUTypePath type)
      throws ConstraintError
    {
      super(name, type);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTUDShaderVertexInput ");
      builder.append(this.getName());
      builder.append(" ");
      builder.append(this.getType());
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
    public UASTUDShaderVertexOutput(
      final @Nonnull UniqueNameLocal name,
      final @Nonnull UASTUTypePath type)
      throws ConstraintError
    {
      super(name, type);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTUDShaderVertexOutput ");
      builder.append(this.getName());
      builder.append(" ");
      builder.append(this.getType());
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
      builder.append(this.getName());
      builder.append(" ");
      builder.append(this.getVariable());
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class UASTUDShaderVertexParameter extends
    UASTUDShaderVertexParameters
  {
    public UASTUDShaderVertexParameter(
      final @Nonnull UniqueNameLocal name,
      final @Nonnull UASTUTypePath type)
      throws ConstraintError
    {
      super(name, type);
    }
  }

  public static abstract class UASTUDShaderVertexParameters extends
    UASTUDShaderParameters
  {
    UASTUDShaderVertexParameters(
      final @Nonnull UniqueNameLocal name,
      final @Nonnull UASTUTypePath type)
      throws ConstraintError
    {
      super(name, type);
    }
  }

  /**
   * The type of term declarations.
   */

  public static abstract class UASTUDTerm extends UASTUDeclarationModuleLevel implements
    UASTUTermVisitable
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

  public static abstract class UASTUDType extends UASTUDeclarationModuleLevel implements
    UASTUTypeVisitable
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
      <A, B, E extends Throwable, V extends UASTUDRecordVisitor<A, B, E>>
      A
      recordTypeVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.recordTypeVisitPre(this);

      final List<B> new_fields = new ArrayList<B>();
      for (final UASTUDTypeRecordField f : this.fields) {
        final B x = v.recordTypeVisitField(f);
        new_fields.add(x);
      }

      return v.recordTypeVisit(new_fields, this);
    }

    @Override public
      <T, E extends Throwable, V extends UASTUTypeVisitor<T, E>>
      T
      typeVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.typeVisitTypeRecord(this);
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
      <T, E extends Throwable, V extends UASTUTermVisitor<T, E>>
      T
      termVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.termVisitValue(this);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTUDValue ");
      builder.append(this.name.getActual());
      builder.append(" ");

      this.ascription.map(new Function<UASTUTypePath, Unit>() {
        @Override public Unit call(
          final UASTUTypePath x)
        {
          builder.append(x.show());
          builder.append(" ");
          return Unit.unit();
        }
      });

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
    private final @Nonnull UniqueNameLocal       name;

    public UASTUDValueLocal(
      final @Nonnull UniqueNameLocal name,
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

    @Override public @Nonnull UniqueNameLocal getName()
    {
      return this.name;
    }

    @Override public
      <A, E extends Throwable, V extends UASTULocalLevelVisitor<A, E>>
      A
      localVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.localVisitValueLocal(this);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTUDValueLocal ");
      builder.append(this.name.show());
      builder.append(" ");

      this.ascription.map(new Function<UASTUTypePath, Unit>() {
        @Override public Unit call(
          final UASTUTypePath x)
        {
          builder.append(x.show());
          builder.append(" ");
          return Unit.unit();
        }
      });

      builder.append(this.expression);
      builder.append("]");
      return builder.toString();
    }
  }
}
