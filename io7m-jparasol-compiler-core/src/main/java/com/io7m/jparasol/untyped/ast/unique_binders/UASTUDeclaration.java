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
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFIT WHETHER IN AN ACTION
 * OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN
 * CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.jparasol.untyped.ast.unique_binders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jfunctional.FunctionType;
import com.io7m.jfunctional.OptionType;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.ModulePath;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.PackagePath;
import com.io7m.jparasol.lexer.Token.TokenDiscard;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.lexer.Token.TokenIdentifierUpper;
import com.io7m.jparasol.untyped.ast.unique_binders.UASTUExpression.UASTUEVariable;
import com.io7m.jparasol.untyped.ast.unique_binders.UniqueName.UniqueNameLocal;

// CHECKSTYLE:OFF

@EqualityReference public abstract class UASTUDeclaration
{
  /**
   * The type of local declarations.
   */

  @EqualityReference public static abstract class UASTUDeclarationLocalLevel extends
    UASTUDeclaration implements UASTULocalLevelVisitableType
  {
    public abstract UniqueNameLocal getName();
  }

  /**
   * The type of module-level declarations.
   */

  @EqualityReference public static abstract class UASTUDeclarationModuleLevel extends
    UASTUDeclaration
  {
    public abstract TokenIdentifierLower getName();
  }

  /**
   * The type of shader-level declarations.
   */

  @EqualityReference public static abstract class UASTUDeclarationShaderLevel extends
    UASTUDeclaration
  {
    // Nothing
  }

  /**
   * The type of unit-level declarations.
   */

  @EqualityReference public static abstract class UASTUDeclarationUnitLevel extends
    UASTUDeclaration implements UASTUUnitLevelVisitableType
  {
    // Nothing
  }

  @EqualityReference public static final class UASTUDExternal
  {
    private final OptionType<UASTUExpression> emulation;
    private final boolean                     fragment_shader_allowed;
    private final TokenIdentifierLower        name;
    private final boolean                     vertex_shader_allowed;

    public UASTUDExternal(
      final TokenIdentifierLower in_name,
      final boolean in_vertex_shader_allowed,
      final boolean in_fragment_shader_allowed,
      final OptionType<UASTUExpression> in_emulation)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.vertex_shader_allowed = in_vertex_shader_allowed;
      this.fragment_shader_allowed = in_fragment_shader_allowed;
      this.emulation = NullCheck.notNull(in_emulation, "Emulation");
    }

    public OptionType<UASTUExpression> getEmulation()
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
  }

  /**
   * The type of function declarations.
   */

  @EqualityReference public static abstract class UASTUDFunction extends
    UASTUDTerm implements UASTUFunctionVisitableType
  {
    // Nothing
  }

  @EqualityReference public static final class UASTUDFunctionArgument
  {
    private final UniqueNameLocal name;
    private final UASTUTypePath   type;

    public UASTUDFunctionArgument(
      final UniqueNameLocal in_name,
      final UASTUTypePath in_type)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.type = NullCheck.notNull(in_type, "Type");
    }

    public UniqueNameLocal getName()
    {
      return this.name;
    }

    public UASTUTypePath getType()
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

  @EqualityReference public static final class UASTUDFunctionDefined extends
    UASTUDFunction
  {
    private final List<UASTUDFunctionArgument> arguments;
    private final UASTUExpression              body;
    private final TokenIdentifierLower         name;
    private final UASTUTypePath                return_type;

    public UASTUDFunctionDefined(
      final TokenIdentifierLower in_name,
      final List<UASTUDFunctionArgument> in_arguments,
      final UASTUTypePath in_return_type,
      final UASTUExpression in_body)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.arguments = NullCheck.notNull(in_arguments, "Arguments");
      this.return_type = NullCheck.notNull(in_return_type, "Return type");
      this.body = NullCheck.notNull(in_body, "Body");
    }

    @Override public
      <A, B, E extends Throwable, V extends UASTUFunctionVisitorType<A, B, E>>
      A
      functionVisitableAccept(
        final V v)
        throws E
    {
      v.functionVisitDefinedPre(this);
      final List<B> args = new ArrayList<B>();
      for (final UASTUDFunctionArgument a : this.arguments) {
        final B x = v.functionVisitArgument(a);
        args.add(x);
      }
      return v.functionVisitDefined(args, this);
    }

    public List<UASTUDFunctionArgument> getArguments()
    {
      return this.arguments;
    }

    public UASTUExpression getBody()
    {
      return this.body;
    }

    @Override public TokenIdentifierLower getName()
    {
      return this.name;
    }

    public UASTUTypePath getReturnType()
    {
      return this.return_type;
    }

    @Override public
      <T, E extends Throwable, V extends UASTUTermVisitorType<T, E>>
      T
      termVisitableAccept(
        final V v)
        throws E
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

  /**
   * Functions with external declarations (private FFI).
   */

  @EqualityReference public static final class UASTUDFunctionExternal extends
    UASTUDFunction
  {
    private final List<UASTUDFunctionArgument> arguments;
    private final UASTUDExternal               external;
    private final TokenIdentifierLower         name;
    private final UASTUTypePath                return_type;

    public UASTUDFunctionExternal(
      final TokenIdentifierLower in_name,
      final List<UASTUDFunctionArgument> in_arguments,
      final UASTUTypePath in_return_type,
      final UASTUDExternal in_external)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.arguments = NullCheck.notNull(in_arguments, "Arguments");
      this.return_type = NullCheck.notNull(in_return_type, "Return type");
      this.external = NullCheck.notNull(in_external, "External");
    }

    @Override public
      <A, B, E extends Throwable, V extends UASTUFunctionVisitorType<A, B, E>>
      A
      functionVisitableAccept(
        final V v)
        throws E
    {
      v.functionVisitExternalPre(this);
      final List<B> args = new ArrayList<B>();
      for (final UASTUDFunctionArgument a : this.arguments) {
        final B x = v.functionVisitArgument(a);
        args.add(x);
      }
      return v.functionVisitExternal(args, this);
    }

    public List<UASTUDFunctionArgument> getArguments()
    {
      return this.arguments;
    }

    public UASTUDExternal getExternal()
    {
      return this.external;
    }

    @Override public TokenIdentifierLower getName()
    {
      return this.name;
    }

    public UASTUTypePath getReturnType()
    {
      return this.return_type;
    }

    @Override public
      <T, E extends Throwable, V extends UASTUTermVisitorType<T, E>>
      T
      termVisitableAccept(
        final V v)
        throws E
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

  @EqualityReference public static final class UASTUDImport extends
    UASTUDeclaration
  {
    private final ModulePath                       path;
    private final OptionType<TokenIdentifierUpper> rename;

    public UASTUDImport(
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

  @EqualityReference public static final class UASTUDModule extends
    UASTUDeclarationUnitLevel implements UASTUModuleVisitableType
  {
    private final List<UASTUDeclarationModuleLevel> declarations;
    private final Map<ModulePathFlat, UASTUDImport> imported_modules;
    private final Map<String, UASTUDImport>         imported_names;
    private final Map<String, UASTUDImport>         imported_renames;
    private final List<UASTUDImport>                imports;
    private final ModulePath                        path;
    private final Map<String, UASTUDShader>         shaders;
    private final Map<String, UASTUDTerm>           terms;
    private final Map<String, UASTUDType>           types;

    public UASTUDModule(
      final ModulePath in_path,
      final List<UASTUDImport> in_imports,
      final Map<ModulePathFlat, UASTUDImport> in_imported_modules,
      final Map<String, UASTUDImport> in_imported_names,
      final Map<String, UASTUDImport> in_imported_renames,
      final List<UASTUDeclarationModuleLevel> in_declarations,
      final Map<String, UASTUDTerm> in_terms,
      final Map<String, UASTUDType> in_types,
      final Map<String, UASTUDShader> in_shaders)
    {
      this.path = NullCheck.notNull(in_path, "Path");

      this.imports = NullCheck.notNull(in_imports, "Imports");
      this.imported_modules =
        NullCheck.notNull(in_imported_modules, "Imported modules");
      this.imported_names =
        NullCheck.notNull(in_imported_names, "Imported names");
      this.imported_renames =
        NullCheck.notNull(in_imported_renames, "Imported renames");

      this.declarations = NullCheck.notNull(in_declarations, "Declarations");
      this.terms = NullCheck.notNull(in_terms, "Terms");
      this.types = NullCheck.notNull(in_types, "Types");
      this.shaders = NullCheck.notNull(in_shaders, "Shaders");
    }

    public List<UASTUDeclarationModuleLevel> getDeclarations()
    {
      return this.declarations;
    }

    public Map<ModulePathFlat, UASTUDImport> getImportedModules()
    {
      return this.imported_modules;
    }

    public Map<String, UASTUDImport> getImportedNames()
    {
      return this.imported_names;
    }

    public Map<String, UASTUDImport> getImportedRenames()
    {
      return this.imported_renames;
    }

    public List<UASTUDImport> getImports()
    {
      return this.imports;
    }

    public ModulePath getPath()
    {
      return this.path;
    }

    public Map<String, UASTUDShader> getShaders()
    {
      return this.shaders;
    }

    public Map<String, UASTUDTerm> getTerms()
    {
      return this.terms;
    }

    public Map<String, UASTUDType> getTypes()
    {
      return this.types;
    }

    @Override public
      <M, I, D, DTE extends D, DTY extends D, DS extends D, E extends Throwable, V extends UASTUModuleVisitorType<M, I, D, DTE, DTY, DS, E>>
      M
      moduleVisitableAccept(
        final V v)
        throws E
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
        final UASTUTypeVisitorType<DTY, E> tv = v.moduleTypesPre(this);
        for (final String k : this.types.keySet()) {
          final UASTUDType ty = this.types.get(k);
          final DTY r = ty.typeVisitableAccept(tv);
          r_types.put(k, r);
          r_decls.add(r);
        }
      }

      {
        final UASTUTermVisitorType<DTE, E> tv = v.moduleTermsPre(this);
        for (final String k : this.terms.keySet()) {
          final UASTUDTerm t = this.terms.get(k);
          final DTE r = t.termVisitableAccept(tv);
          r_terms.put(k, r);
          r_decls.add(r);
        }
      }

      {
        final UASTUShaderVisitorType<DS, E> tv = v.moduleShadersPre(this);
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
      <A, E extends Throwable, V extends UASTUUnitLevelVisitorType<A, E>>
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

  @EqualityReference public static final class UASTUDPackage extends
    UASTUDeclarationUnitLevel
  {
    private final PackagePath path;

    public UASTUDPackage(
      final PackagePath in_path)
    {
      this.path = NullCheck.notNull(in_path, "Path");
    }

    public PackagePath getPath()
    {
      return this.path;
    }

    @Override public
      <A, E extends Throwable, V extends UASTUUnitLevelVisitorType<A, E>>
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

  @EqualityReference public static abstract class UASTUDShader extends
    UASTUDeclarationModuleLevel implements UASTUShaderVisitableType
  {
    private final TokenIdentifierLower name;

    protected UASTUDShader(
      final TokenIdentifierLower in_name)
    {
      this.name = NullCheck.notNull(in_name, "Name");
    }

    @Override public final TokenIdentifierLower getName()
    {
      return this.name;
    }
  }

  @EqualityReference public static final class UASTUDShaderFragment extends
    UASTUDShader implements UASTUFragmentShaderVisitableType
  {
    private final List<UASTUDShaderFragmentInput>            inputs;
    private final List<UASTUDShaderFragmentLocal>            locals;
    private final List<UASTUDShaderFragmentOutput>           outputs;
    private final List<UASTUDShaderFragmentParameter>        parameters;
    private final List<UASTUDShaderFragmentOutputAssignment> writes;

    public UASTUDShaderFragment(
      final TokenIdentifierLower name,
      final List<UASTUDShaderFragmentInput> in_inputs,
      final List<UASTUDShaderFragmentOutput> in_outputs,
      final List<UASTUDShaderFragmentParameter> in_parameters,
      final List<UASTUDShaderFragmentLocal> in_locals,
      final List<UASTUDShaderFragmentOutputAssignment> in_writes)
    {
      super(name);
      this.inputs = NullCheck.notNull(in_inputs, "Inputs");
      this.outputs = NullCheck.notNull(in_outputs, "Outputs");
      this.parameters = NullCheck.notNull(in_parameters, "Parameters");
      this.locals = NullCheck.notNull(in_locals, "Locals");
      this.writes = NullCheck.notNull(in_writes, "Writes");
    }

    @Override public
      <F, PI, PP, PO, L, O, E extends Throwable, V extends UASTUFragmentShaderVisitorType<F, PI, PP, PO, L, O, E>>
      F
      fragmentShaderVisitableAccept(
        final V v)
        throws E
    {
      final List<PI> r_inputs = new ArrayList<PI>();
      for (final UASTUDShaderFragmentInput i : this.inputs) {
        final PI ri = v.fragmentShaderVisitInput(i);
        r_inputs.add(ri);
      }

      final UASTUFragmentShaderOutputVisitorType<PO, E> ov =
        v.fragmentShaderVisitOutputsPre();

      final List<PO> r_outputs = new ArrayList<PO>();
      for (final UASTUDShaderFragmentOutput o : this.outputs) {
        final PO ro = o.fragmentShaderOutputVisitableAccept(ov);
        r_outputs.add(ro);
      }

      final List<PP> r_parameters = new ArrayList<PP>();
      for (final UASTUDShaderFragmentParameter p : this.parameters) {
        final PP rp = v.fragmentShaderVisitParameter(p);
        r_parameters.add(rp);
      }

      final UASTUFragmentShaderLocalVisitorType<L, E> lv =
        v.fragmentShaderVisitLocalsPre();

      final List<L> r_locals = new ArrayList<L>();
      for (final UASTUDShaderFragmentLocal l : this.locals) {
        final L rl = l.fragmentShaderLocalVisitableAccept(lv);
        r_locals.add(rl);
      }

      final List<O> r_assigns = new ArrayList<O>();
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

    public List<UASTUDShaderFragmentInput> getInputs()
    {
      return this.inputs;
    }

    public List<UASTUDShaderFragmentLocal> getLocals()
    {
      return this.locals;
    }

    public List<UASTUDShaderFragmentOutput> getOutputs()
    {
      return this.outputs;
    }

    public List<UASTUDShaderFragmentParameter> getParameters()
    {
      return this.parameters;
    }

    public List<UASTUDShaderFragmentOutputAssignment> getWrites()
    {
      return this.writes;
    }

    @Override public
      <T, E extends Throwable, V extends UASTUShaderVisitorType<T, E>>
      T
      shaderVisitableAccept(
        final V v)
        throws E
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

  @EqualityReference public static final class UASTUDShaderFragmentInput extends
    UASTUDShaderFragmentParameters
  {
    public UASTUDShaderFragmentInput(
      final UniqueNameLocal name,
      final UASTUTypePath type)
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

  @EqualityReference public static abstract class UASTUDShaderFragmentLocal extends
    UASTUDeclarationShaderLevel implements
    UASTUFragmentShaderLocalVisitableType
  {
    // Nothing
  }

  @EqualityReference public static final class UASTUDShaderFragmentLocalDiscard extends
    UASTUDShaderFragmentLocal
  {
    private final TokenDiscard    discard;
    private final UASTUExpression expression;

    public UASTUDShaderFragmentLocalDiscard(
      final TokenDiscard in_discard,
      final UASTUExpression in_expression)
    {
      this.discard = NullCheck.notNull(in_discard, "Discard");
      this.expression = NullCheck.notNull(in_expression, "Expression");
    }

    @Override public
      <L, E extends Throwable, V extends UASTUFragmentShaderLocalVisitorType<L, E>>
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

    public UASTUExpression getExpression()
    {
      return this.expression;
    }
  }

  @EqualityReference public static final class UASTUDShaderFragmentLocalValue extends
    UASTUDShaderFragmentLocal
  {
    private final UASTUDValueLocal value;

    public UASTUDShaderFragmentLocalValue(
      final UASTUDValueLocal in_value)
    {
      this.value = NullCheck.notNull(in_value, "Value");
    }

    @Override public
      <L, E extends Throwable, V extends UASTUFragmentShaderLocalVisitorType<L, E>>
      L
      fragmentShaderLocalVisitableAccept(
        final V v)
        throws E
    {
      return v.fragmentShaderVisitLocalValue(this);
    }

    public UASTUDValueLocal getValue()
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

  @EqualityReference public static abstract class UASTUDShaderFragmentOutput extends
    UASTUDShaderFragmentParameters implements
    UASTUFragmentShaderOutputVisitableType
  {
    public UASTUDShaderFragmentOutput(
      final UniqueNameLocal name,
      final UASTUTypePath type)
    {
      super(name, type);
    }
  }

  @EqualityReference public static final class UASTUDShaderFragmentOutputAssignment extends
    UASTUDeclarationShaderLevel
  {
    private final TokenIdentifierLower name;
    private final UASTUEVariable       variable;

    public UASTUDShaderFragmentOutputAssignment(
      final TokenIdentifierLower in_name,
      final UASTUEVariable in_variable)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.variable = NullCheck.notNull(in_variable, "Variable");
    }

    public TokenIdentifierLower getName()
    {
      return this.name;
    }

    public UASTUEVariable getVariable()
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

  @EqualityReference public static final class UASTUDShaderFragmentOutputData extends
    UASTUDShaderFragmentOutput
  {
    private final int index;

    public UASTUDShaderFragmentOutputData(
      final UniqueNameLocal name,
      final UASTUTypePath type,
      final int in_index)
    {
      super(name, type);
      this.index = in_index;
    }

    @Override public
      <O, E extends Throwable, V extends UASTUFragmentShaderOutputVisitorType<O, E>>
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

  @EqualityReference public static final class UASTUDShaderFragmentOutputDepth extends
    UASTUDShaderFragmentOutput
  {
    public UASTUDShaderFragmentOutputDepth(
      final UniqueNameLocal name,
      final UASTUTypePath type)
    {
      super(name, type);
    }

    @Override public
      <O, E extends Throwable, V extends UASTUFragmentShaderOutputVisitorType<O, E>>
      O
      fragmentShaderOutputVisitableAccept(
        final V v)
        throws E
    {
      return v.fragmentShaderVisitOutputDepth(this);
    }
  }

  @EqualityReference public static final class UASTUDShaderFragmentParameter extends
    UASTUDShaderFragmentParameters
  {
    public UASTUDShaderFragmentParameter(
      final UniqueNameLocal name,
      final UASTUTypePath type)
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

  @EqualityReference public static abstract class UASTUDShaderFragmentParameters extends
    UASTUDShaderParameters
  {
    UASTUDShaderFragmentParameters(
      final UniqueNameLocal name,
      final UASTUTypePath type)
    {
      super(name, type);
    }
  }

  @EqualityReference public static abstract class UASTUDShaderParameters extends
    UASTUDeclarationShaderLevel
  {
    private final UniqueNameLocal name;
    private final UASTUTypePath   type;

    UASTUDShaderParameters(
      final UniqueNameLocal in_name,
      final UASTUTypePath in_type)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.type = NullCheck.notNull(in_type, "Type");
    }

    public final UniqueNameLocal getName()
    {
      return this.name;
    }

    public final UASTUTypePath getType()
    {
      return this.type;
    }
  }

  @EqualityReference public static final class UASTUDShaderProgram extends
    UASTUDShader
  {
    private final UASTUShaderPath fragment_shader;
    private final UASTUShaderPath vertex_shader;

    public UASTUDShaderProgram(
      final TokenIdentifierLower name,
      final UASTUShaderPath in_vertex_shader,
      final UASTUShaderPath in_fragment_shader)
    {
      super(name);
      this.vertex_shader =
        NullCheck.notNull(in_vertex_shader, "Vertex shader");
      this.fragment_shader =
        NullCheck.notNull(in_fragment_shader, "Fragment shader");
    }

    public UASTUShaderPath getFragmentShader()
    {
      return this.fragment_shader;
    }

    public UASTUShaderPath getVertexShader()
    {
      return this.vertex_shader;
    }

    @Override public
      <T, E extends Throwable, V extends UASTUShaderVisitorType<T, E>>
      T
      shaderVisitableAccept(
        final V v)
        throws E
    {
      return v.moduleVisitProgramShader(this);
    }
  }

  @EqualityReference public static final class UASTUDShaderVertex extends
    UASTUDShader implements UASTUVertexShaderVisitableType
  {
    private final List<UASTUDShaderVertexInput>            inputs;
    private final List<UASTUDShaderVertexLocalValue>       locals;
    private final List<UASTUDShaderVertexOutput>           outputs;
    private final List<UASTUDShaderVertexParameter>        parameters;
    private final List<UASTUDShaderVertexOutputAssignment> writes;

    public UASTUDShaderVertex(
      final TokenIdentifierLower name,
      final List<UASTUDShaderVertexInput> in_inputs,
      final List<UASTUDShaderVertexOutput> in_outputs,
      final List<UASTUDShaderVertexParameter> in_parameters,
      final List<UASTUDShaderVertexLocalValue> in_locals,
      final List<UASTUDShaderVertexOutputAssignment> in_writes)
    {
      super(name);
      this.inputs = NullCheck.notNull(in_inputs, "Inputs");
      this.outputs = NullCheck.notNull(in_outputs, "Outputs");
      this.parameters = NullCheck.notNull(in_parameters, "Parameters");
      this.locals = NullCheck.notNull(in_locals, "Values");
      this.writes = NullCheck.notNull(in_writes, "Writes");
    }

    public List<UASTUDShaderVertexInput> getInputs()
    {
      return this.inputs;
    }

    public List<UASTUDShaderVertexLocalValue> getLocals()
    {
      return this.locals;
    }

    public List<UASTUDShaderVertexOutput> getOutputs()
    {
      return this.outputs;
    }

    public List<UASTUDShaderVertexParameter> getParameters()
    {
      return this.parameters;
    }

    public List<UASTUDShaderVertexOutputAssignment> getWrites()
    {
      return this.writes;
    }

    @Override public
      <T, E extends Throwable, V extends UASTUShaderVisitorType<T, E>>
      T
      shaderVisitableAccept(
        final V v)
        throws E
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
      <VS, PI, PP, PO, L, O, E extends Throwable, V extends UASTUVertexShaderVisitorType<VS, PI, PP, PO, L, O, E>>
      VS
      vertexShaderVisitableAccept(
        final V v)
        throws E
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

      final UASTUVertexShaderLocalVisitorType<L, E> lv =
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

  @EqualityReference public static final class UASTUDShaderVertexInput extends
    UASTUDShaderVertexParameters
  {
    public UASTUDShaderVertexInput(
      final UniqueNameLocal name,
      final UASTUTypePath type)
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

  @EqualityReference public static final class UASTUDShaderVertexLocalValue extends
    UASTUDeclarationShaderLevel
  {
    private final UASTUDValueLocal value;

    public UASTUDShaderVertexLocalValue(
      final UASTUDValueLocal in_value)
    {
      this.value = NullCheck.notNull(in_value, "Value");
    }

    public UASTUDValueLocal getValue()
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

  @EqualityReference public static final class UASTUDShaderVertexOutput extends
    UASTUDShaderVertexParameters
  {
    private final boolean main;

    public UASTUDShaderVertexOutput(
      final UniqueNameLocal name,
      final UASTUTypePath type,
      final boolean in_main)
    {
      super(name, type);
      this.main = in_main;
    }

    public boolean isMain()
    {
      return this.main;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTUDShaderVertexOutput ");
      builder.append(this.getName());
      builder.append(" ");
      builder.append(this.getType());
      builder.append(" ");
      builder.append(this.main);
      builder.append("]");
      return builder.toString();
    }
  }

  @EqualityReference public static final class UASTUDShaderVertexOutputAssignment extends
    UASTUDeclarationShaderLevel
  {
    private final TokenIdentifierLower name;
    private final UASTUEVariable       variable;

    public UASTUDShaderVertexOutputAssignment(
      final TokenIdentifierLower in_name,
      final UASTUEVariable in_variable)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.variable = NullCheck.notNull(in_variable, "Variable");
    }

    public TokenIdentifierLower getName()
    {
      return this.name;
    }

    public UASTUEVariable getVariable()
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

  @EqualityReference public static final class UASTUDShaderVertexParameter extends
    UASTUDShaderVertexParameters
  {
    public UASTUDShaderVertexParameter(
      final UniqueNameLocal name,
      final UASTUTypePath type)
    {
      super(name, type);
    }
  }

  @EqualityReference public static abstract class UASTUDShaderVertexParameters extends
    UASTUDShaderParameters
  {
    UASTUDShaderVertexParameters(
      final UniqueNameLocal name,
      final UASTUTypePath type)
    {
      super(name, type);
    }
  }

  /**
   * The type of term declarations.
   */

  @EqualityReference public static abstract class UASTUDTerm extends
    UASTUDeclarationModuleLevel implements UASTUTermVisitableType
  {
    // Nothing
  }

  /**
   * The type of local term declarations.
   */

  @EqualityReference public static abstract class UASTUDTermLocal extends
    UASTUDeclarationLocalLevel
  {
    // Nothing
  }

  /**
   * The type of type declarations.
   */

  @EqualityReference public static abstract class UASTUDType extends
    UASTUDeclarationModuleLevel implements UASTUTypeVisitableType
  {
    // Nothing
  }

  /**
   * Record declarations.
   */

  @EqualityReference public static final class UASTUDTypeRecord extends
    UASTUDType implements UASTUDRecordVisitableType
  {
    private final List<UASTUDTypeRecordField> fields;
    private final TokenIdentifierLower        name;

    public UASTUDTypeRecord(
      final TokenIdentifierLower in_name,
      final List<UASTUDTypeRecordField> in_fields)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.fields = NullCheck.notNull(in_fields, "Fields");
    }

    public List<UASTUDTypeRecordField> getFields()
    {
      return this.fields;
    }

    @Override public TokenIdentifierLower getName()
    {
      return this.name;
    }

    @Override public
      <A, B, E extends Throwable, V extends UASTUDRecordVisitorType<A, B, E>>
      A
      recordTypeVisitableAccept(
        final V v)
        throws E
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
      <T, E extends Throwable, V extends UASTUTypeVisitorType<T, E>>
      T
      typeVisitableAccept(
        final V v)
        throws E
    {
      return v.typeVisitTypeRecord(this);
    }
  }

  @EqualityReference public static final class UASTUDTypeRecordField
  {
    private final TokenIdentifierLower name;
    private final UASTUTypePath        type;

    public UASTUDTypeRecordField(
      final TokenIdentifierLower in_name,
      final UASTUTypePath in_type)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.type = NullCheck.notNull(in_type, "Type");
    }

    public TokenIdentifierLower getName()
    {
      return this.name;
    }

    public UASTUTypePath getType()
    {
      return this.type;
    }
  }

  /**
   * Value declarations.
   */

  @EqualityReference public static abstract class UASTUDValue extends
    UASTUDTerm implements UASTUValueVisitableType
  {

  }

  @EqualityReference public static final class UASTUDValueDefined extends
    UASTUDValue
  {
    private final OptionType<UASTUTypePath> ascription;
    private final UASTUExpression           expression;
    private final TokenIdentifierLower      name;

    public UASTUDValueDefined(
      final TokenIdentifierLower in_name,
      final OptionType<UASTUTypePath> in_ascription,
      final UASTUExpression in_expression)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.ascription = NullCheck.notNull(in_ascription, "Ascription");
      this.expression = NullCheck.notNull(in_expression, "Expression");
    }

    public OptionType<UASTUTypePath> getAscription()
    {
      return this.ascription;
    }

    public UASTUExpression getExpression()
    {
      return this.expression;
    }

    @Override public TokenIdentifierLower getName()
    {
      return this.name;
    }

    @Override public
      <T, E extends Throwable, V extends UASTUTermVisitorType<T, E>>
      T
      termVisitableAccept(
        final V v)
        throws E
    {
      return v.termVisitValueDefined(this);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTUDValue ");
      builder.append(this.name.getActual());
      builder.append(" ");

      this.ascription.map(new FunctionType<UASTUTypePath, Unit>() {
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

    @Override public
      <A, E extends Throwable, V extends UASTUValueVisitorType<A, E>>
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

  @EqualityReference public static final class UASTUDValueExternal extends
    UASTUDValue
  {
    private final UASTUTypePath        ascription;
    private final UASTUDExternal       external;
    private final TokenIdentifierLower name;

    public UASTUDValueExternal(
      final TokenIdentifierLower in_name,
      final UASTUTypePath in_ascription,
      final UASTUDExternal in_external)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.ascription = NullCheck.notNull(in_ascription, "Ascription");
      this.external = NullCheck.notNull(in_external, "External");
      assert this.external.getEmulation().isNone();
    }

    public UASTUTypePath getAscription()
    {
      return this.ascription;
    }

    public UASTUDExternal getExternal()
    {
      return this.external;
    }

    @Override public TokenIdentifierLower getName()
    {
      return this.name;
    }

    @Override public
      <T, E extends Throwable, V extends UASTUTermVisitorType<T, E>>
      T
      termVisitableAccept(
        final V v)
        throws E
    {
      return v.termVisitValueExternal(this);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTUDValueExternal ");
      builder.append(this.name.getActual());
      builder.append(" ");
      builder.append(this.external);
      builder.append("]");
      return builder.toString();
    }

    @Override public
      <A, E extends Throwable, V extends UASTUValueVisitorType<A, E>>
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

  @EqualityReference public static final class UASTUDValueLocal extends
    UASTUDTermLocal
  {
    private final OptionType<UASTUTypePath> ascription;
    private final UASTUExpression           expression;
    private final UniqueNameLocal           name;

    public UASTUDValueLocal(
      final UniqueNameLocal in_name,
      final OptionType<UASTUTypePath> in_ascription,
      final UASTUExpression in_expression)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.ascription = NullCheck.notNull(in_ascription, "Ascription");
      this.expression = NullCheck.notNull(in_expression, "Expression");
    }

    public OptionType<UASTUTypePath> getAscription()
    {
      return this.ascription;
    }

    public UASTUExpression getExpression()
    {
      return this.expression;
    }

    @Override public UniqueNameLocal getName()
    {
      return this.name;
    }

    @Override public
      <A, E extends Throwable, V extends UASTULocalLevelVisitorType<A, E>>
      A
      localVisitableAccept(
        final V v)
        throws E
    {
      return v.localVisitValueLocal(this);
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[UASTUDValueLocal ");
      builder.append(this.name.show());
      builder.append(" ");

      this.ascription.map(new FunctionType<UASTUTypePath, Unit>() {
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
