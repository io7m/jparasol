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
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.jparasol.untyped.ast.resolved;

import java.util.ArrayList;
import java.util.HashMap;
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

  public static final class UASTRDExternal
  {
    private final boolean                          fragment_shader_allowed;
    private final @Nonnull TokenIdentifierLower    name;
    private final boolean                          vertex_shader_allowed;
    private final @Nonnull Option<UASTRExpression> emulation;

    public UASTRDExternal(
      final @Nonnull TokenIdentifierLower in_name,
      final boolean in_vertex_shader_allowed,
      final boolean in_fragment_shader_allowed,
      final @Nonnull Option<UASTRExpression> in_emulation)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(in_name, "Name");
      this.vertex_shader_allowed = in_vertex_shader_allowed;
      this.fragment_shader_allowed = in_fragment_shader_allowed;
      this.emulation =
        Constraints.constrainNotNull(in_emulation, "Emulation");
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.emulation.hashCode();
      result =
        (prime * result) + (this.fragment_shader_allowed ? 1231 : 1237);
      result = (prime * result) + this.name.hashCode();
      result = (prime * result) + (this.vertex_shader_allowed ? 1231 : 1237);
      return result;
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
      final UASTRDExternal other = (UASTRDExternal) obj;
      if (!this.emulation.equals(other.emulation)) {
        return false;
      }
      if (this.fragment_shader_allowed != other.fragment_shader_allowed) {
        return false;
      }
      if (!this.name.equals(other.name)) {
        return false;
      }
      if (this.vertex_shader_allowed != other.vertex_shader_allowed) {
        return false;
      }
      return true;
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

    public @Nonnull Option<UASTRExpression> getEmulation()
    {
      return this.emulation;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTRDExternal ");
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

  public static abstract class UASTRDFunction extends UASTRDTerm implements
    UASTRFunctionVisitable
  {
    // Nothing
  }

  public static final class UASTRDFunctionArgument
  {
    private final @Nonnull UASTRTermNameLocal name;
    private final @Nonnull UASTRTypeName      type;

    public UASTRDFunctionArgument(
      final @Nonnull UASTRTermNameLocal in_name,
      final @Nonnull UASTRTypeName in_type)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(in_name, "Name");
      this.type = Constraints.constrainNotNull(in_type, "Type");
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
      final @Nonnull TokenIdentifierLower in_name,
      final @Nonnull List<UASTRDFunctionArgument> in_arguments,
      final @Nonnull UASTRTypeName in_return_type,
      final @Nonnull UASTRExpression in_body)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(in_name, "Name");
      this.arguments =
        Constraints.constrainNotNull(in_arguments, "Arguments");
      this.return_type =
        Constraints.constrainNotNull(in_return_type, "Return type");
      this.body = Constraints.constrainNotNull(in_body, "Body");
    }

    @Override public
      <A, B, E extends Throwable, V extends UASTRFunctionVisitor<A, B, E>>
      A
      functionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.functionVisitDefinedPre(this);
      final List<B> args = new ArrayList<B>();
      for (final UASTRDFunctionArgument a : this.arguments) {
        final B x = v.functionVisitArgument(a);
        args.add(x);
      }
      return v.functionVisitDefined(args, this);
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

    @Override public
      <T, E extends Throwable, V extends UASTRTermVisitor<T, E>>
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
    private final @Nonnull UASTRDExternal               external;
    private final @Nonnull TokenIdentifierLower         name;
    private final @Nonnull UASTRTypeName                return_type;

    public UASTRDFunctionExternal(
      final @Nonnull TokenIdentifierLower in_name,
      final @Nonnull List<UASTRDFunctionArgument> in_arguments,
      final @Nonnull UASTRTypeName in_return_type,
      final @Nonnull UASTRDExternal in_external)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(in_name, "Name");
      this.arguments =
        Constraints.constrainNotNull(in_arguments, "Arguments");
      this.return_type =
        Constraints.constrainNotNull(in_return_type, "Return type");
      this.external = Constraints.constrainNotNull(in_external, "External");
    }

    @Override public
      <A, B, E extends Throwable, V extends UASTRFunctionVisitor<A, B, E>>
      A
      functionVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      v.functionVisitExternalPre(this);
      final List<B> args = new ArrayList<B>();
      for (final UASTRDFunctionArgument a : this.arguments) {
        final B x = v.functionVisitArgument(a);
        args.add(x);
      }
      return v.functionVisitExternal(args, this);
    }

    public @Nonnull List<UASTRDFunctionArgument> getArguments()
    {
      return this.arguments;
    }

    public @Nonnull UASTRDExternal getExternal()
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

    @Override public
      <T, E extends Throwable, V extends UASTRTermVisitor<T, E>>
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
      builder.append("[UASTRDFunctionExternal ");
      builder.append(this.name.getActual());
      builder.append(" ");
      builder.append(this.arguments);
      builder.append(" ");
      builder.append(this.return_type.show());
      builder.append(" ");
      builder.append(this.external);
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
      final @Nonnull ModulePath in_path,
      final @Nonnull Option<TokenIdentifierUpper> in_rename)
      throws ConstraintError
    {
      this.path = Constraints.constrainNotNull(in_path, "Path");
      this.rename = Constraints.constrainNotNull(in_rename, "Rename");
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

  public static final class UASTRDModule extends UASTRDeclarationUnitLevel implements
    UASTRModuleVisitable
  {
    private final @Nonnull List<UASTRDeclarationModuleLevel> declarations;
    private final @Nonnull ModulePathFlat                    flat;
    private final @Nonnull Map<ModulePathFlat, UASTRDImport> imported_modules;
    private final @Nonnull Map<String, UASTRDImport>         imported_names;
    private final @Nonnull Map<String, UASTRDImport>         imported_renames;
    private final @Nonnull List<UASTRDImport>                imports;
    private final @Nonnull ModulePath                        path;
    private final @Nonnull List<String>                      shader_topology;
    private final @Nonnull Map<String, UASTRDShader>         shaders;
    private final @Nonnull List<String>                      term_topology;
    private final @Nonnull Map<String, UASTRDTerm>           terms;
    private final @Nonnull List<String>                      type_topology;
    private final @Nonnull Map<String, UASTRDType>           types;

    public UASTRDModule(
      final @Nonnull ModulePath in_path,
      final @Nonnull List<UASTRDImport> in_imports,
      final @Nonnull Map<ModulePathFlat, UASTRDImport> in_imported_modules,
      final @Nonnull Map<String, UASTRDImport> in_imported_names,
      final @Nonnull Map<String, UASTRDImport> in_imported_renames,
      final @Nonnull List<UASTRDeclarationModuleLevel> in_declarations,
      final @Nonnull Map<String, UASTRDTerm> in_terms,
      final @Nonnull List<String> in_term_topology,
      final @Nonnull Map<String, UASTRDType> in_types,
      final @Nonnull List<String> in_type_topology,
      final @Nonnull Map<String, UASTRDShader> in_shaders,
      final @Nonnull List<String> in_shader_topology)
      throws ConstraintError
    {
      this.path = Constraints.constrainNotNull(in_path, "Path");
      this.flat = ModulePathFlat.fromModulePath(in_path);

      this.imports = Constraints.constrainNotNull(in_imports, "Imports");
      this.imported_modules =
        Constraints.constrainNotNull(in_imported_modules, "Imported modules");
      this.imported_names =
        Constraints.constrainNotNull(in_imported_names, "Imported names");
      this.imported_renames =
        Constraints.constrainNotNull(in_imported_renames, "Imported renames");

      this.declarations =
        Constraints.constrainNotNull(in_declarations, "Declarations");

      this.terms = Constraints.constrainNotNull(in_terms, "Terms");
      this.term_topology =
        Constraints.constrainNotNull(in_term_topology, "Term topology");
      this.types = Constraints.constrainNotNull(in_types, "Types");
      this.type_topology =
        Constraints.constrainNotNull(in_type_topology, "Type topology");
      this.shaders = Constraints.constrainNotNull(in_shaders, "Shaders");
      this.shader_topology =
        Constraints.constrainNotNull(in_shader_topology, "Shader topology");
    }

    public @Nonnull List<UASTRDeclarationModuleLevel> getDeclarations()
    {
      return this.declarations;
    }

    public @Nonnull ModulePathFlat getFlat()
    {
      return this.flat;
    }

    public @Nonnull Map<ModulePathFlat, UASTRDImport> getImportedModules()
    {
      return this.imported_modules;
    }

    public @Nonnull Map<String, UASTRDImport> getImportedNames()
    {
      return this.imported_names;
    }

    public @Nonnull Map<String, UASTRDImport> getImportedRenames()
    {
      return this.imported_renames;
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

    public @Nonnull List<String> getShaderTopology()
    {
      return this.shader_topology;
    }

    public @Nonnull Map<String, UASTRDTerm> getTerms()
    {
      return this.terms;
    }

    public @Nonnull List<String> getTermTopology()
    {
      return this.term_topology;
    }

    public @Nonnull Map<String, UASTRDType> getTypes()
    {
      return this.types;
    }

    public @Nonnull List<String> getTypeTopology()
    {
      return this.type_topology;
    }

    @Override public
      <M, I, D, DTE extends D, DTY extends D, DS extends D, E extends Throwable, V extends UASTRModuleVisitor<M, I, D, DTE, DTY, DS, E>>
      M
      moduleVisitableAccept(
        final V v)
        throws E,
          ConstraintError
    {
      final List<I> r_imports = new ArrayList<I>();
      for (final UASTRDImport i : this.imports) {
        final I r = v.moduleVisitImport(i);
        r_imports.add(r);
      }

      final List<D> r_decls = new ArrayList<D>();
      final Map<String, DTY> r_types = new HashMap<String, DTY>();
      final Map<String, DTE> r_terms = new HashMap<String, DTE>();
      final Map<String, DS> r_shaders = new HashMap<String, DS>();

      {
        final UASTRTypeVisitor<DTY, E> tv = v.moduleTypesPre(this);
        for (final String k : this.types.keySet()) {
          final UASTRDType ty = this.types.get(k);
          final DTY r = ty.typeVisitableAccept(tv);
          r_types.put(k, r);
          r_decls.add(r);
        }
      }

      {
        final UASTRTermVisitor<DTE, E> tv = v.moduleTermsPre(this);
        for (final String k : this.terms.keySet()) {
          final UASTRDTerm t = this.terms.get(k);
          final DTE r = t.termVisitableAccept(tv);
          r_terms.put(k, r);
          r_decls.add(r);
        }
      }

      {
        final UASTRShaderVisitor<DS, E> tv = v.moduleShadersPre(this);
        for (final String k : this.shaders.keySet()) {
          final UASTRDShader t = this.shaders.get(k);
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
      final @Nonnull PackagePath in_path)
      throws ConstraintError
    {
      this.path = Constraints.constrainNotNull(in_path, "Path");
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
    UASTRDeclarationModuleLevel implements UASTRShaderVisitable
  {
    private final @Nonnull TokenIdentifierLower name;

    protected UASTRDShader(
      final @Nonnull TokenIdentifierLower in_name)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(in_name, "Name");
    }

    @Override public final @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }
  }

  public static final class UASTRDShaderFragment extends UASTRDShader implements
    UASTRFragmentShaderVisitable
  {
    private final @Nonnull List<UASTRDShaderFragmentInput>            inputs;
    private final @Nonnull List<UASTRDShaderFragmentLocal>            locals;
    private final @Nonnull List<UASTRDShaderFragmentOutput>           outputs;
    private final @Nonnull List<UASTRDShaderFragmentParameter>        parameters;
    private final @Nonnull List<UASTRDShaderFragmentOutputAssignment> writes;

    public UASTRDShaderFragment(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull List<UASTRDShaderFragmentInput> in_inputs,
      final @Nonnull List<UASTRDShaderFragmentOutput> in_outputs,
      final @Nonnull List<UASTRDShaderFragmentParameter> in_parameters,
      final @Nonnull List<UASTRDShaderFragmentLocal> in_locals,
      final @Nonnull List<UASTRDShaderFragmentOutputAssignment> in_writes)
      throws ConstraintError
    {
      super(name);
      this.inputs = Constraints.constrainNotNull(in_inputs, "Inputs");
      this.outputs = Constraints.constrainNotNull(in_outputs, "Outputs");
      this.parameters =
        Constraints.constrainNotNull(in_parameters, "Parameters");
      this.locals = Constraints.constrainNotNull(in_locals, "Locals");
      this.writes = Constraints.constrainNotNull(in_writes, "Writes");
    }

    @Override public
      <F, PI, PP, PO, L, O, E extends Throwable, V extends UASTRFragmentShaderVisitor<F, PI, PP, PO, L, O, E>>
      F
      fragmentShaderVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      final List<PI> r_inputs = new ArrayList<PI>();
      for (final UASTRDShaderFragmentInput i : this.inputs) {
        final PI ri = v.fragmentShaderVisitInput(i);
        r_inputs.add(ri);
      }

      final UASTRFragmentShaderOutputVisitor<PO, E> vo =
        v.fragmentShaderVisitOutputsPre();

      final List<PO> r_outputs = new ArrayList<PO>();
      for (final UASTRDShaderFragmentOutput o : this.outputs) {
        final PO ro = o.fragmentShaderOutputVisitableAccept(vo);
        r_outputs.add(ro);
      }

      final List<PP> r_parameters = new ArrayList<PP>();
      for (final UASTRDShaderFragmentParameter p : this.parameters) {
        final PP rp = v.fragmentShaderVisitParameter(p);
        r_parameters.add(rp);
      }

      final UASTRFragmentShaderLocalVisitor<L, E> lv =
        v.fragmentShaderVisitLocalsPre();

      final ArrayList<L> r_locals = new ArrayList<L>();
      for (final UASTRDShaderFragmentLocal l : this.locals) {
        final L rl = l.fragmentShaderLocalVisitableAccept(lv);
        r_locals.add(rl);
      }

      final ArrayList<O> r_assigns = new ArrayList<O>();
      for (final UASTRDShaderFragmentOutputAssignment w : this.writes) {
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

    @Override public
      <T, E extends Throwable, V extends UASTRShaderVisitor<T, E>>
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
      final @Nonnull UASTRTermNameLocal in_name,
      final @Nonnull UASTRTypeName type)
      throws ConstraintError
    {
      super(type);
      this.name = Constraints.constrainNotNull(in_name, "Name");
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
    UASTRDeclarationShaderLevel implements UASTRFragmentShaderLocalVisitable
  {
    // Nothing
  }

  public static final class UASTRDShaderFragmentLocalDiscard extends
    UASTRDShaderFragmentLocal
  {
    private final @Nonnull TokenDiscard    discard;
    private final @Nonnull UASTRExpression expression;

    public UASTRDShaderFragmentLocalDiscard(
      final TokenDiscard in_discard,
      final UASTRExpression in_expression)
      throws ConstraintError
    {
      this.discard = Constraints.constrainNotNull(in_discard, "Discard");
      this.expression =
        Constraints.constrainNotNull(in_expression, "Expression");
    }

    @Override public
      <L, E extends Throwable, V extends UASTRFragmentShaderLocalVisitor<L, E>>
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
      final @Nonnull UASTRDValueLocal in_value)
      throws ConstraintError
    {
      this.value = Constraints.constrainNotNull(in_value, "Value");
    }

    @Override public
      <L, E extends Throwable, V extends UASTRFragmentShaderLocalVisitor<L, E>>
      L
      fragmentShaderLocalVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.fragmentShaderVisitLocalValue(this);
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

  public static abstract class UASTRDShaderFragmentOutput extends
    UASTRDShaderFragmentParameters implements
    UASTRFragmentShaderOutputVisitable
  {
    private final @Nonnull TokenIdentifierLower name;

    public UASTRDShaderFragmentOutput(
      final @Nonnull TokenIdentifierLower in_name,
      final @Nonnull UASTRTypeName type)
      throws ConstraintError
    {
      super(type);
      this.name = Constraints.constrainNotNull(in_name, "Name");
    }

    public final @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }
  }

  public static final class UASTRDShaderFragmentOutputDepth extends
    UASTRDShaderFragmentOutput
  {
    public UASTRDShaderFragmentOutputDepth(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTRTypeName type)
      throws ConstraintError
    {
      super(name, type);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTRDShaderFragmentOutputDepth ");
      builder.append(super.getName().getActual());
      builder.append(" ");
      builder.append(super.getType());
      builder.append("]");
      return builder.toString();
    }

    @Override public
      <O, E extends Throwable, V extends UASTRFragmentShaderOutputVisitor<O, E>>
      O
      fragmentShaderOutputVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.fragmentShaderVisitOutputDepth(this);
    }
  }

  public static final class UASTRDShaderFragmentOutputData extends
    UASTRDShaderFragmentOutput
  {
    private final int index;

    public UASTRDShaderFragmentOutputData(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull UASTRTypeName type,
      final int in_index)
      throws ConstraintError
    {
      super(name, type);
      this.index = in_index;
    }

    public int getIndex()
    {
      return this.index;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTRDShaderFragmentOutputData ");
      builder.append(super.getName().getActual());
      builder.append(" ");
      builder.append(super.getType());
      builder.append(" ");
      builder.append(this.index);
      builder.append("]");
      return builder.toString();
    }

    @Override public
      <O, E extends Throwable, V extends UASTRFragmentShaderOutputVisitor<O, E>>
      O
      fragmentShaderOutputVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.fragmentShaderVisitOutputData(this);
    }
  }

  public static final class UASTRDShaderFragmentOutputAssignment extends
    UASTRDeclarationShaderLevel
  {
    private final @Nonnull TokenIdentifierLower name;
    private final @Nonnull UASTREVariable       variable;

    public UASTRDShaderFragmentOutputAssignment(
      final @Nonnull TokenIdentifierLower in_name,
      final @Nonnull UASTREVariable in_variable)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(in_name, "Name");
      this.variable = Constraints.constrainNotNull(in_variable, "Variable");
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
      final @Nonnull UASTRTermNameLocal in_name,
      final @Nonnull UASTRTypeName type)
      throws ConstraintError
    {
      super(type);
      this.name = Constraints.constrainNotNull(in_name, "Name");
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
      final @Nonnull UASTRTypeName in_type)
      throws ConstraintError
    {
      this.type = Constraints.constrainNotNull(in_type, "Type");
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
      final @Nonnull UASTRShaderName in_vertex_shader,
      final @Nonnull UASTRShaderName in_fragment_shader)
      throws ConstraintError
    {
      super(name);
      this.vertex_shader =
        Constraints.constrainNotNull(in_vertex_shader, "Vertex shader");
      this.fragment_shader =
        Constraints.constrainNotNull(in_fragment_shader, "Fragment shader");
    }

    public @Nonnull UASTRShaderName getFragmentShader()
    {
      return this.fragment_shader;
    }

    public @Nonnull UASTRShaderName getVertexShader()
    {
      return this.vertex_shader;
    }

    @Override public
      <T, E extends Throwable, V extends UASTRShaderVisitor<T, E>>
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
      builder.append("[UASTRDShaderProgram ");
      builder.append(this.fragment_shader.show());
      builder.append(" ");
      builder.append(this.vertex_shader.show());
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class UASTRDShaderVertex extends UASTRDShader implements
    UASTRVertexShaderVisitable
  {
    private final @Nonnull List<UASTRDShaderVertexInput>            inputs;
    private final @Nonnull List<UASTRDShaderVertexOutput>           outputs;
    private final @Nonnull List<UASTRDShaderVertexParameter>        parameters;
    private final @Nonnull List<UASTRDShaderVertexLocalValue>       values;
    private final @Nonnull List<UASTRDShaderVertexOutputAssignment> writes;

    public UASTRDShaderVertex(
      final @Nonnull TokenIdentifierLower name,
      final @Nonnull List<UASTRDShaderVertexInput> in_inputs,
      final @Nonnull List<UASTRDShaderVertexOutput> in_outputs,
      final @Nonnull List<UASTRDShaderVertexParameter> in_parameters,
      final @Nonnull List<UASTRDShaderVertexLocalValue> in_values,
      final @Nonnull List<UASTRDShaderVertexOutputAssignment> in_writes)
      throws ConstraintError
    {
      super(name);
      this.inputs = Constraints.constrainNotNull(in_inputs, "Inputs");
      this.outputs = Constraints.constrainNotNull(in_outputs, "Outputs");
      this.parameters =
        Constraints.constrainNotNull(in_parameters, "Parameters");
      this.values = Constraints.constrainNotNull(in_values, "Values");
      this.writes = Constraints.constrainNotNull(in_writes, "Writes");
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

    @Override public
      <T, E extends Throwable, V extends UASTRShaderVisitor<T, E>>
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

    @Override public
      <VS, PI, PP, PO, L, O, E extends Throwable, V extends UASTRVertexShaderVisitor<VS, PI, PP, PO, L, O, E>>
      VS
      vertexShaderVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      final List<PI> r_inputs = new ArrayList<PI>();
      for (final UASTRDShaderVertexInput i : this.inputs) {
        final PI ri = v.vertexShaderVisitInput(i);
        r_inputs.add(ri);
      }

      final List<PO> r_outputs = new ArrayList<PO>();
      for (final UASTRDShaderVertexOutput o : this.outputs) {
        final PO ro = v.vertexShaderVisitOutput(o);
        r_outputs.add(ro);
      }

      final List<PP> r_parameters = new ArrayList<PP>();
      for (final UASTRDShaderVertexParameter p : this.parameters) {
        final PP rp = v.vertexShaderVisitParameter(p);
        r_parameters.add(rp);
      }

      final UASTRVertexShaderLocalVisitor<L, E> lv =
        v.vertexShaderVisitLocalsPre();

      final ArrayList<L> r_locals = new ArrayList<L>();
      for (final UASTRDShaderVertexLocalValue l : this.values) {
        final L rl = lv.vertexShaderVisitLocalValue(l);
        r_locals.add(rl);
      }

      final ArrayList<O> r_assigns = new ArrayList<O>();
      for (final UASTRDShaderVertexOutputAssignment w : this.writes) {
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

  public static final class UASTRDShaderVertexInput extends
    UASTRDShaderVertexParameters
  {
    private final @Nonnull UASTRTermNameLocal name;

    public UASTRDShaderVertexInput(
      final @Nonnull UASTRTermNameLocal in_name,
      final @Nonnull UASTRTypeName type)
      throws ConstraintError
    {
      super(type);
      this.name = Constraints.constrainNotNull(in_name, "Name");
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
      final @Nonnull UASTRDValueLocal in_value)
      throws ConstraintError
    {
      this.value = Constraints.constrainNotNull(in_value, "Value");
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
    private final boolean                       main;
    private final @Nonnull TokenIdentifierLower name;

    public UASTRDShaderVertexOutput(
      final @Nonnull TokenIdentifierLower in_name,
      final @Nonnull UASTRTypeName type,
      final boolean in_main)
      throws ConstraintError
    {
      super(type);
      this.name = Constraints.constrainNotNull(in_name, "Name");
      this.main = in_main;
    }

    public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    public boolean isMain()
    {
      return this.main;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTRDShaderVertexOutput ");
      builder.append(this.name);
      builder.append(" ");
      builder.append(this.main);
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
      final @Nonnull TokenIdentifierLower in_name,
      final @Nonnull UASTREVariable in_variable)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(in_name, "Name");
      this.variable = Constraints.constrainNotNull(in_variable, "Variable");
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
      final @Nonnull UASTRTermNameLocal in_name,
      final @Nonnull UASTRTypeName type)
      throws ConstraintError
    {
      super(type);
      this.name = Constraints.constrainNotNull(in_name, "Name");
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

  public static abstract class UASTRDTerm extends UASTRDeclarationModuleLevel implements
    UASTRTermVisitable
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

  public static abstract class UASTRDType extends UASTRDeclarationModuleLevel implements
    UASTRTypeVisitable
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
      final @Nonnull TokenIdentifierLower in_name,
      final @Nonnull List<UASTRDTypeRecordField> in_fields)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(in_name, "Name");
      this.fields = Constraints.constrainNotNull(in_fields, "Fields");
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

    @Override public
      <T, E extends Throwable, V extends UASTRTypeVisitor<T, E>>
      T
      typeVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.typeVisitTypeRecord(this);
    }
  }

  public static final class UASTRDTypeRecordField
  {
    private final @Nonnull TokenIdentifierLower name;
    private final @Nonnull UASTRTypeName        type;

    public UASTRDTypeRecordField(
      final @Nonnull TokenIdentifierLower in_name,
      final @Nonnull UASTRTypeName in_type)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(in_name, "Name");
      this.type = Constraints.constrainNotNull(in_type, "Type");
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

  public static abstract class UASTRDValue extends UASTRDTerm implements
    UASTRValueVisitable
  {
    @Override public abstract @Nonnull TokenIdentifierLower getName();
  }

  public static final class UASTRDValueExternal extends UASTRDValue
  {
    private final @Nonnull UASTRTypeName        ascription;
    private final @Nonnull UASTRDExternal       external;
    private final @Nonnull TokenIdentifierLower name;

    public UASTRDValueExternal(
      final @Nonnull TokenIdentifierLower in_name,
      final @Nonnull UASTRTypeName in_ascription,
      final @Nonnull UASTRDExternal in_external)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(in_name, "Name");
      this.ascription =
        Constraints.constrainNotNull(in_ascription, "Ascription");
      this.external = Constraints.constrainNotNull(in_external, "External");
      assert this.external.getEmulation().isNone();
    }

    public @Nonnull UASTRTypeName getAscription()
    {
      return this.ascription;
    }

    public @Nonnull UASTRDExternal getExternal()
    {
      return this.external;
    }

    @Override public @Nonnull TokenIdentifierLower getName()
    {
      return this.name;
    }

    @Override public
      <T, E extends Throwable, V extends UASTRTermVisitor<T, E>>
      T
      termVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.termVisitValueExternal(this);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTRDValueExternal ");
      builder.append(this.name.getActual());
      builder.append(" ");
      builder.append(this.ascription);
      builder.append(" ");
      builder.append(this.external);
      builder.append("]");
      return builder.toString();
    }

    @Override public
      <A, E extends Throwable, V extends UASTRValueVisitor<A, E>>
      A
      valueVisitableAccept(
        final V v)
        throws E,
          ConstraintError
    {
      return v.valueVisitExternal(this);
    }
  }

  /**
   * Value declarations.
   */

  public static final class UASTRDValueDefined extends UASTRDValue
  {
    private final @Nonnull Option<UASTRTypeName> ascription;
    private final @Nonnull UASTRExpression       expression;
    private final @Nonnull TokenIdentifierLower  name;

    public UASTRDValueDefined(
      final @Nonnull TokenIdentifierLower in_name,
      final @Nonnull Option<UASTRTypeName> in_ascription,
      final @Nonnull UASTRExpression in_expression)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(in_name, "Name");
      this.ascription =
        Constraints.constrainNotNull(in_ascription, "Ascription");
      this.expression =
        Constraints.constrainNotNull(in_expression, "Expression");
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

    @Override public
      <T, E extends Throwable, V extends UASTRTermVisitor<T, E>>
      T
      termVisitableAccept(
        final @Nonnull V v)
        throws E,
          ConstraintError
    {
      return v.termVisitValueDefined(this);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTRDValueDefined ");
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

    @Override public
      <A, E extends Throwable, V extends UASTRValueVisitor<A, E>>
      A
      valueVisitableAccept(
        final V v)
        throws E,
          ConstraintError
    {
      return v.valueVisitDefined(this);
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
      final @Nonnull UASTRTermNameLocal in_name,
      final @Nonnull Option<UASTRTypeName> in_ascription,
      final @Nonnull UASTRExpression in_expression)
      throws ConstraintError
    {
      this.name = Constraints.constrainNotNull(in_name, "Name");
      this.ascription =
        Constraints.constrainNotNull(in_ascription, "Ascription");
      this.expression =
        Constraints.constrainNotNull(in_expression, "Expression");
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
