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

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jfunctional.FunctionType;
import com.io7m.jfunctional.OptionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.ModulePath;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.PackagePath;
import com.io7m.jparasol.lexer.TokenDiscard;
import com.io7m.jparasol.lexer.TokenIdentifierLower;
import com.io7m.jparasol.lexer.TokenIdentifierUpper;
import com.io7m.jparasol.untyped.ast.resolved.UASTRTermName.UASTRTermNameLocal;

// CHECKSTYLE_JAVADOC:OFF

@EqualityReference public abstract class UASTRDeclaration
{
  /**
   * The type of local declarations.
   */

  @EqualityReference public static abstract class UASTRDeclarationLocalLevel extends
    UASTRDeclaration
  {
    // Nothing
  }

  /**
   * The type of module-level declarations.
   */

  @EqualityReference public static abstract class UASTRDeclarationModuleLevel extends
    UASTRDeclaration
  {
    public abstract TokenIdentifierLower getName();
  }

  /**
   * The type of shader-level declarations.
   */

  @EqualityReference public static abstract class UASTRDeclarationShaderLevel extends
    UASTRDeclaration
  {
    // Nothing
  }

  /**
   * The type of unit-level declarations.
   */

  @EqualityReference public static abstract class UASTRDeclarationUnitLevel extends
    UASTRDeclaration
  {
    // Nothing
  }

  @EqualityReference public static final class UASTRDExternal
  {
    private final OptionType<UASTRExpressionType> emulation;
    private final boolean                     fragment_shader_allowed;
    private final TokenIdentifierLower        name;
    private final boolean                     vertex_shader_allowed;

    public UASTRDExternal(
      final TokenIdentifierLower in_name,
      final boolean in_vertex_shader_allowed,
      final boolean in_fragment_shader_allowed,
      final OptionType<UASTRExpressionType> in_emulation)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.vertex_shader_allowed = in_vertex_shader_allowed;
      this.fragment_shader_allowed = in_fragment_shader_allowed;
      this.emulation = NullCheck.notNull(in_emulation, "Emulation");
    }

    public OptionType<UASTRExpressionType> getEmulation()
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

  @EqualityReference public static abstract class UASTRDFunction extends
    UASTRDTerm implements UASTRFunctionVisitableType
  {
    // Nothing
  }

  @EqualityReference public static final class UASTRDFunctionArgument
  {
    private final UASTRTermNameLocal name;
    private final UASTRTypeName      type;

    public UASTRDFunctionArgument(
      final UASTRTermNameLocal in_name,
      final UASTRTypeName in_type)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.type = NullCheck.notNull(in_type, "Type");
    }

    public UASTRTermNameLocal getName()
    {
      return this.name;
    }

    public UASTRTypeName getType()
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

  @EqualityReference public static final class UASTRDFunctionDefined extends
    UASTRDFunction
  {
    private final List<UASTRDFunctionArgument> arguments;
    private final UASTRExpressionType              body;
    private final TokenIdentifierLower         name;
    private final UASTRTypeName                return_type;

    public UASTRDFunctionDefined(
      final TokenIdentifierLower in_name,
      final List<UASTRDFunctionArgument> in_arguments,
      final UASTRTypeName in_return_type,
      final UASTRExpressionType in_body)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.arguments = NullCheck.notNull(in_arguments, "Arguments");
      this.return_type = NullCheck.notNull(in_return_type, "Return type");
      this.body = NullCheck.notNull(in_body, "Body");
    }

    @Override public
      <A, B, E extends Throwable, V extends UASTRFunctionVisitorType<A, B, E>>
      A
      functionVisitableAccept(
        final V v)
        throws E
    {
      v.functionVisitDefinedPre(this);
      final List<B> args = new ArrayList<B>();
      for (final UASTRDFunctionArgument a : this.arguments) {
        final B x = v.functionVisitArgument(a);
        args.add(x);
      }
      return v.functionVisitDefined(args, this);
    }

    public List<UASTRDFunctionArgument> getArguments()
    {
      return this.arguments;
    }

    public UASTRExpressionType getBody()
    {
      return this.body;
    }

    @Override public TokenIdentifierLower getName()
    {
      return this.name;
    }

    public UASTRTypeName getReturnType()
    {
      return this.return_type;
    }

    @Override public
      <T, E extends Throwable, V extends UASTRTermVisitorType<T, E>>
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

  @EqualityReference public static final class UASTRDFunctionExternal extends
    UASTRDFunction
  {
    private final List<UASTRDFunctionArgument> arguments;
    private final UASTRDExternal               external;
    private final TokenIdentifierLower         name;
    private final UASTRTypeName                return_type;

    public UASTRDFunctionExternal(
      final TokenIdentifierLower in_name,
      final List<UASTRDFunctionArgument> in_arguments,
      final UASTRTypeName in_return_type,
      final UASTRDExternal in_external)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.arguments = NullCheck.notNull(in_arguments, "Arguments");
      this.return_type = NullCheck.notNull(in_return_type, "Return type");
      this.external = NullCheck.notNull(in_external, "External");
    }

    @Override public
      <A, B, E extends Throwable, V extends UASTRFunctionVisitorType<A, B, E>>
      A
      functionVisitableAccept(
        final V v)
        throws E
    {
      v.functionVisitExternalPre(this);
      final List<B> args = new ArrayList<B>();
      for (final UASTRDFunctionArgument a : this.arguments) {
        final B x = v.functionVisitArgument(a);
        args.add(x);
      }
      return v.functionVisitExternal(args, this);
    }

    public List<UASTRDFunctionArgument> getArguments()
    {
      return this.arguments;
    }

    public UASTRDExternal getExternal()
    {
      return this.external;
    }

    @Override public TokenIdentifierLower getName()
    {
      return this.name;
    }

    public UASTRTypeName getReturnType()
    {
      return this.return_type;
    }

    @Override public
      <T, E extends Throwable, V extends UASTRTermVisitorType<T, E>>
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

  @EqualityReference public static final class UASTRDImport extends
    UASTRDeclaration
  {
    private final ModulePath                       path;
    private final OptionType<TokenIdentifierUpper> rename;

    public UASTRDImport(
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

  @EqualityReference public static final class UASTRDModule extends
    UASTRDeclarationUnitLevel implements UASTRModuleVisitableType
  {
    private final List<UASTRDeclarationModuleLevel> declarations;
    private final ModulePathFlat                    flat;
    private final Map<ModulePathFlat, UASTRDImport> imported_modules;
    private final Map<String, UASTRDImport>         imported_names;
    private final Map<String, UASTRDImport>         imported_renames;
    private final List<UASTRDImport>                imports;
    private final ModulePath                        path;
    private final List<String>                      shader_topology;
    private final Map<String, UASTRDShader>         shaders;
    private final List<String>                      term_topology;
    private final Map<String, UASTRDTerm>           terms;
    private final List<String>                      type_topology;
    private final Map<String, UASTRDType>           types;

    public UASTRDModule(
      final ModulePath in_path,
      final List<UASTRDImport> in_imports,
      final Map<ModulePathFlat, UASTRDImport> in_imported_modules,
      final Map<String, UASTRDImport> in_imported_names,
      final Map<String, UASTRDImport> in_imported_renames,
      final List<UASTRDeclarationModuleLevel> in_declarations,
      final Map<String, UASTRDTerm> in_terms,
      final List<String> in_term_topology,
      final Map<String, UASTRDType> in_types,
      final List<String> in_type_topology,
      final Map<String, UASTRDShader> in_shaders,
      final List<String> in_shader_topology)
    {
      this.path = NullCheck.notNull(in_path, "Path");
      this.flat = ModulePathFlat.fromModulePath(in_path);

      this.imports = NullCheck.notNull(in_imports, "Imports");
      this.imported_modules =
        NullCheck.notNull(in_imported_modules, "Imported modules");
      this.imported_names =
        NullCheck.notNull(in_imported_names, "Imported names");
      this.imported_renames =
        NullCheck.notNull(in_imported_renames, "Imported renames");

      this.declarations = NullCheck.notNull(in_declarations, "Declarations");

      this.terms = NullCheck.notNull(in_terms, "Terms");
      this.term_topology =
        NullCheck.notNull(in_term_topology, "Term topology");
      this.types = NullCheck.notNull(in_types, "Types");
      this.type_topology =
        NullCheck.notNull(in_type_topology, "Type topology");
      this.shaders = NullCheck.notNull(in_shaders, "Shaders");
      this.shader_topology =
        NullCheck.notNull(in_shader_topology, "Shader topology");
    }

    public List<UASTRDeclarationModuleLevel> getDeclarations()
    {
      return this.declarations;
    }

    public ModulePathFlat getFlat()
    {
      return this.flat;
    }

    public Map<ModulePathFlat, UASTRDImport> getImportedModules()
    {
      return this.imported_modules;
    }

    public Map<String, UASTRDImport> getImportedNames()
    {
      return this.imported_names;
    }

    public Map<String, UASTRDImport> getImportedRenames()
    {
      return this.imported_renames;
    }

    public List<UASTRDImport> getImports()
    {
      return this.imports;
    }

    public ModulePath getPath()
    {
      return this.path;
    }

    public Map<String, UASTRDShader> getShaders()
    {
      return this.shaders;
    }

    public List<String> getShaderTopology()
    {
      return this.shader_topology;
    }

    public Map<String, UASTRDTerm> getTerms()
    {
      return this.terms;
    }

    public List<String> getTermTopology()
    {
      return this.term_topology;
    }

    public Map<String, UASTRDType> getTypes()
    {
      return this.types;
    }

    public List<String> getTypeTopology()
    {
      return this.type_topology;
    }

    @Override public
      <M, I, D, DTE extends D, DTY extends D, DS extends D, E extends Throwable, V extends UASTRModuleVisitorType<M, I, D, DTE, DTY, DS, E>>
      M
      moduleVisitableAccept(
        final V v)
        throws E
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
        final UASTRTypeVisitorType<DTY, E> tv = v.moduleTypesPre(this);
        for (final String k : this.types.keySet()) {
          final UASTRDType ty = this.types.get(k);
          final DTY r = ty.typeVisitableAccept(tv);
          r_types.put(k, r);
          r_decls.add(r);
        }
      }

      {
        final UASTRTermVisitorType<DTE, E> tv = v.moduleTermsPre(this);
        for (final String k : this.terms.keySet()) {
          final UASTRDTerm t = this.terms.get(k);
          final DTE r = t.termVisitableAccept(tv);
          r_terms.put(k, r);
          r_decls.add(r);
        }
      }

      {
        final UASTRShaderVisitorType<DS, E> tv = v.moduleShadersPre(this);
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

  @EqualityReference public static final class UASTRDPackage extends
    UASTRDeclarationUnitLevel
  {
    private final PackagePath path;

    public UASTRDPackage(
      final PackagePath in_path)
    {
      this.path = NullCheck.notNull(in_path, "Path");
    }

    public PackagePath getPath()
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

  @EqualityReference public static abstract class UASTRDShader extends
    UASTRDeclarationModuleLevel implements UASTRShaderVisitableType
  {
    private final TokenIdentifierLower name;

    protected UASTRDShader(
      final TokenIdentifierLower in_name)
    {
      this.name = NullCheck.notNull(in_name, "Name");
    }

    @Override public final TokenIdentifierLower getName()
    {
      return this.name;
    }
  }

  @EqualityReference public static final class UASTRDShaderFragment extends
    UASTRDShader implements UASTRFragmentShaderVisitableType
  {
    private final List<UASTRDShaderFragmentInput>            inputs;
    private final List<UASTRDShaderFragmentLocal>            locals;
    private final List<UASTRDShaderFragmentOutput>           outputs;
    private final List<UASTRDShaderFragmentParameter>        parameters;
    private final List<UASTRDShaderFragmentOutputAssignment> writes;

    public UASTRDShaderFragment(
      final TokenIdentifierLower name,
      final List<UASTRDShaderFragmentInput> in_inputs,
      final List<UASTRDShaderFragmentOutput> in_outputs,
      final List<UASTRDShaderFragmentParameter> in_parameters,
      final List<UASTRDShaderFragmentLocal> in_locals,
      final List<UASTRDShaderFragmentOutputAssignment> in_writes)
    {
      super(name);
      this.inputs = NullCheck.notNull(in_inputs, "Inputs");
      this.outputs = NullCheck.notNull(in_outputs, "Outputs");
      this.parameters = NullCheck.notNull(in_parameters, "Parameters");
      this.locals = NullCheck.notNull(in_locals, "Locals");
      this.writes = NullCheck.notNull(in_writes, "Writes");
    }

    @Override public
      <F, PI, PP, PO, L, O, E extends Throwable, V extends UASTRFragmentShaderVisitorType<F, PI, PP, PO, L, O, E>>
      F
      fragmentShaderVisitableAccept(
        final V v)
        throws E
    {
      final List<PI> r_inputs = new ArrayList<PI>();
      for (final UASTRDShaderFragmentInput i : this.inputs) {
        final PI ri = v.fragmentShaderVisitInput(i);
        r_inputs.add(ri);
      }

      final UASTRFragmentShaderOutputVisitorType<PO, E> vo =
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

      final UASTRFragmentShaderLocalVisitorType<L, E> lv =
        v.fragmentShaderVisitLocalsPre();

      final List<L> r_locals = new ArrayList<L>();
      for (final UASTRDShaderFragmentLocal l : this.locals) {
        final L rl = l.fragmentShaderLocalVisitableAccept(lv);
        r_locals.add(rl);
      }

      final List<O> r_assigns = new ArrayList<O>();
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

    public List<UASTRDShaderFragmentInput> getInputs()
    {
      return this.inputs;
    }

    public List<UASTRDShaderFragmentLocal> getLocals()
    {
      return this.locals;
    }

    public List<UASTRDShaderFragmentOutput> getOutputs()
    {
      return this.outputs;
    }

    public List<UASTRDShaderFragmentParameter> getParameters()
    {
      return this.parameters;
    }

    public List<UASTRDShaderFragmentOutputAssignment> getWrites()
    {
      return this.writes;
    }

    @Override public
      <T, E extends Throwable, V extends UASTRShaderVisitorType<T, E>>
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

  @EqualityReference public static final class UASTRDShaderFragmentInput extends
    UASTRDShaderFragmentParameters
  {
    private final UASTRTermNameLocal name;

    public UASTRDShaderFragmentInput(
      final UASTRTermNameLocal in_name,
      final UASTRTypeName type)
    {
      super(type);
      this.name = NullCheck.notNull(in_name, "Name");
    }

    public UASTRTermNameLocal getName()
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

  @EqualityReference public static abstract class UASTRDShaderFragmentLocal extends
    UASTRDeclarationShaderLevel implements
    UASTRFragmentShaderLocalVisitableType
  {
    // Nothing
  }

  @EqualityReference public static final class UASTRDShaderFragmentLocalDiscard extends
    UASTRDShaderFragmentLocal
  {
    private final TokenDiscard    discard;
    private final UASTRExpressionType expression;

    public UASTRDShaderFragmentLocalDiscard(
      final TokenDiscard in_discard,
      final UASTRExpressionType in_expression)
    {
      this.discard = NullCheck.notNull(in_discard, "Discard");
      this.expression = NullCheck.notNull(in_expression, "Expression");
    }

    @Override public
      <L, E extends Throwable, V extends UASTRFragmentShaderLocalVisitorType<L, E>>
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

    public UASTRExpressionType getExpression()
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

  @EqualityReference public static final class UASTRDShaderFragmentLocalValue extends
    UASTRDShaderFragmentLocal
  {
    private final UASTRDValueLocal value;

    public UASTRDShaderFragmentLocalValue(
      final UASTRDValueLocal in_value)
    {
      this.value = NullCheck.notNull(in_value, "Value");
    }

    @Override public
      <L, E extends Throwable, V extends UASTRFragmentShaderLocalVisitorType<L, E>>
      L
      fragmentShaderLocalVisitableAccept(
        final V v)
        throws E
    {
      return v.fragmentShaderVisitLocalValue(this);
    }

    public UASTRDValueLocal getValue()
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

  @EqualityReference public static abstract class UASTRDShaderFragmentOutput extends
    UASTRDShaderFragmentParameters implements
    UASTRFragmentShaderOutputVisitableType
  {
    private final TokenIdentifierLower name;

    public UASTRDShaderFragmentOutput(
      final TokenIdentifierLower in_name,
      final UASTRTypeName type)
    {
      super(type);
      this.name = NullCheck.notNull(in_name, "Name");
    }

    public final TokenIdentifierLower getName()
    {
      return this.name;
    }
  }

  @EqualityReference public static final class UASTRDShaderFragmentOutputAssignment extends
    UASTRDeclarationShaderLevel
  {
    private final TokenIdentifierLower name;
    private final UASTREVariable       variable;

    public UASTRDShaderFragmentOutputAssignment(
      final TokenIdentifierLower in_name,
      final UASTREVariable in_variable)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.variable = NullCheck.notNull(in_variable, "Variable");
    }

    public TokenIdentifierLower getName()
    {
      return this.name;
    }

    public UASTREVariable getVariable()
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

  @EqualityReference public static final class UASTRDShaderFragmentOutputData extends
    UASTRDShaderFragmentOutput
  {
    private final int index;

    public UASTRDShaderFragmentOutputData(
      final TokenIdentifierLower name,
      final UASTRTypeName type,
      final int in_index)

    {
      super(name, type);
      this.index = in_index;
    }

    @Override public
      <O, E extends Throwable, V extends UASTRFragmentShaderOutputVisitorType<O, E>>
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
      builder.append("[UASTRDShaderFragmentOutputData ");
      builder.append(super.getName().getActual());
      builder.append(" ");
      builder.append(super.getType());
      builder.append(" ");
      builder.append(this.index);
      builder.append("]");
      return builder.toString();
    }
  }

  @EqualityReference public static final class UASTRDShaderFragmentOutputDepth extends
    UASTRDShaderFragmentOutput
  {
    public UASTRDShaderFragmentOutputDepth(
      final TokenIdentifierLower name,
      final UASTRTypeName type)

    {
      super(name, type);
    }

    @Override public
      <O, E extends Throwable, V extends UASTRFragmentShaderOutputVisitorType<O, E>>
      O
      fragmentShaderOutputVisitableAccept(
        final V v)
        throws E
    {
      return v.fragmentShaderVisitOutputDepth(this);
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
  }

  @EqualityReference public static final class UASTRDShaderFragmentParameter extends
    UASTRDShaderFragmentParameters
  {
    private final UASTRTermNameLocal name;

    public UASTRDShaderFragmentParameter(
      final UASTRTermNameLocal in_name,
      final UASTRTypeName type)
    {
      super(type);
      this.name = NullCheck.notNull(in_name, "Name");
    }

    public UASTRTermNameLocal getName()
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

  @EqualityReference public static abstract class UASTRDShaderFragmentParameters extends
    UASTRDShaderParameters
  {
    UASTRDShaderFragmentParameters(
      final UASTRTypeName type)
    {
      super(type);
    }
  }

  @EqualityReference public static abstract class UASTRDShaderParameters extends
    UASTRDeclarationShaderLevel
  {
    private final UASTRTypeName type;

    UASTRDShaderParameters(
      final UASTRTypeName in_type)

    {
      this.type = NullCheck.notNull(in_type, "Type");
    }

    public final UASTRTypeName getType()
    {
      return this.type;
    }
  }

  @EqualityReference public static final class UASTRDShaderProgram extends
    UASTRDShader
  {
    private final UASTRShaderName fragment_shader;
    private final UASTRShaderName vertex_shader;

    public UASTRDShaderProgram(
      final TokenIdentifierLower name,
      final UASTRShaderName in_vertex_shader,
      final UASTRShaderName in_fragment_shader)
    {
      super(name);
      this.vertex_shader =
        NullCheck.notNull(in_vertex_shader, "Vertex shader");
      this.fragment_shader =
        NullCheck.notNull(in_fragment_shader, "Fragment shader");
    }

    public UASTRShaderName getFragmentShader()
    {
      return this.fragment_shader;
    }

    public UASTRShaderName getVertexShader()
    {
      return this.vertex_shader;
    }

    @Override public
      <T, E extends Throwable, V extends UASTRShaderVisitorType<T, E>>
      T
      shaderVisitableAccept(
        final V v)
        throws E
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

  @EqualityReference public static final class UASTRDShaderVertex extends
    UASTRDShader implements UASTRVertexShaderVisitableType
  {
    private final List<UASTRDShaderVertexInput>            inputs;
    private final List<UASTRDShaderVertexOutput>           outputs;
    private final List<UASTRDShaderVertexParameter>        parameters;
    private final List<UASTRDShaderVertexLocalValue>       values;
    private final List<UASTRDShaderVertexOutputAssignment> writes;

    public UASTRDShaderVertex(
      final TokenIdentifierLower name,
      final List<UASTRDShaderVertexInput> in_inputs,
      final List<UASTRDShaderVertexOutput> in_outputs,
      final List<UASTRDShaderVertexParameter> in_parameters,
      final List<UASTRDShaderVertexLocalValue> in_values,
      final List<UASTRDShaderVertexOutputAssignment> in_writes)
    {
      super(name);
      this.inputs = NullCheck.notNull(in_inputs, "Inputs");
      this.outputs = NullCheck.notNull(in_outputs, "Outputs");
      this.parameters = NullCheck.notNull(in_parameters, "Parameters");
      this.values = NullCheck.notNull(in_values, "Values");
      this.writes = NullCheck.notNull(in_writes, "Writes");
    }

    public List<UASTRDShaderVertexInput> getInputs()
    {
      return this.inputs;
    }

    public List<UASTRDShaderVertexOutput> getOutputs()
    {
      return this.outputs;
    }

    public List<UASTRDShaderVertexParameter> getParameters()
    {
      return this.parameters;
    }

    public List<UASTRDShaderVertexLocalValue> getValues()
    {
      return this.values;
    }

    public List<UASTRDShaderVertexOutputAssignment> getWrites()
    {
      return this.writes;
    }

    @Override public
      <T, E extends Throwable, V extends UASTRShaderVisitorType<T, E>>
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
      <VS, PI, PP, PO, L, O, E extends Throwable, V extends UASTRVertexShaderVisitorType<VS, PI, PP, PO, L, O, E>>
      VS
      vertexShaderVisitableAccept(
        final V v)
        throws E
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

      final UASTRVertexShaderLocalVisitorType<L, E> lv =
        v.vertexShaderVisitLocalsPre();

      final List<L> r_locals = new ArrayList<L>();
      for (final UASTRDShaderVertexLocalValue l : this.values) {
        final L rl = lv.vertexShaderVisitLocalValue(l);
        r_locals.add(rl);
      }

      final List<O> r_assigns = new ArrayList<O>();
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

  @EqualityReference public static final class UASTRDShaderVertexInput extends
    UASTRDShaderVertexParameters
  {
    private final UASTRTermNameLocal name;

    public UASTRDShaderVertexInput(
      final UASTRTermNameLocal in_name,
      final UASTRTypeName type)
    {
      super(type);
      this.name = NullCheck.notNull(in_name, "Name");
    }

    public UASTRTermNameLocal getName()
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

  @EqualityReference public static final class UASTRDShaderVertexLocalValue extends
    UASTRDeclarationShaderLevel
  {
    private final UASTRDValueLocal value;

    public UASTRDShaderVertexLocalValue(
      final UASTRDValueLocal in_value)
    {
      this.value = NullCheck.notNull(in_value, "Value");
    }

    public UASTRDValueLocal getValue()
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

  @EqualityReference public static final class UASTRDShaderVertexOutput extends
    UASTRDShaderVertexParameters
  {
    private final boolean              main;
    private final TokenIdentifierLower name;

    public UASTRDShaderVertexOutput(
      final TokenIdentifierLower in_name,
      final UASTRTypeName type,
      final boolean in_main)
    {
      super(type);
      this.name = NullCheck.notNull(in_name, "Name");
      this.main = in_main;
    }

    public TokenIdentifierLower getName()
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

  @EqualityReference public static final class UASTRDShaderVertexOutputAssignment extends
    UASTRDeclarationShaderLevel
  {
    private final TokenIdentifierLower name;
    private final UASTREVariable       variable;

    public UASTRDShaderVertexOutputAssignment(
      final TokenIdentifierLower in_name,
      final UASTREVariable in_variable)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.variable = NullCheck.notNull(in_variable, "Variable");
    }

    public TokenIdentifierLower getName()
    {
      return this.name;
    }

    public UASTREVariable getVariable()
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

  @EqualityReference public static final class UASTRDShaderVertexParameter extends
    UASTRDShaderVertexParameters
  {
    private final UASTRTermNameLocal name;

    public UASTRDShaderVertexParameter(
      final UASTRTermNameLocal in_name,
      final UASTRTypeName type)
    {
      super(type);
      this.name = NullCheck.notNull(in_name, "Name");
    }

    public UASTRTermNameLocal getName()
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

  @EqualityReference public static abstract class UASTRDShaderVertexParameters extends
    UASTRDShaderParameters
  {
    UASTRDShaderVertexParameters(
      final UASTRTypeName type)

    {
      super(type);
    }
  }

  /**
   * The type of term declarations.
   */

  @EqualityReference public static abstract class UASTRDTerm extends
    UASTRDeclarationModuleLevel implements UASTRTermVisitableType
  {
    // Nothing
  }

  /**
   * The type of local term declarations.
   */

  @EqualityReference public static abstract class UASTRDTermLocal extends
    UASTRDeclarationLocalLevel
  {
    // Nothing
  }

  /**
   * The type of type declarations.
   */

  @EqualityReference public static abstract class UASTRDType extends
    UASTRDeclarationModuleLevel implements UASTRTypeVisitableType
  {
    // Nothing
  }

  /**
   * Record declarations.
   */

  @EqualityReference public static final class UASTRDTypeRecord extends
    UASTRDType
  {
    private final List<UASTRDTypeRecordField> fields;
    private final TokenIdentifierLower        name;

    public UASTRDTypeRecord(
      final TokenIdentifierLower in_name,
      final List<UASTRDTypeRecordField> in_fields)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.fields = NullCheck.notNull(in_fields, "Fields");
    }

    public List<UASTRDTypeRecordField> getFields()
    {
      return this.fields;
    }

    @Override public TokenIdentifierLower getName()
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
      <T, E extends Throwable, V extends UASTRTypeVisitorType<T, E>>
      T
      typeVisitableAccept(
        final V v)
        throws E
    {
      return v.typeVisitTypeRecord(this);
    }
  }

  @EqualityReference public static final class UASTRDTypeRecordField
  {
    private final TokenIdentifierLower name;
    private final UASTRTypeName        type;

    public UASTRDTypeRecordField(
      final TokenIdentifierLower in_name,
      final UASTRTypeName in_type)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.type = NullCheck.notNull(in_type, "Type");
    }

    public TokenIdentifierLower getName()
    {
      return this.name;
    }

    public UASTRTypeName getType()
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

  @EqualityReference public static abstract class UASTRDValue extends
    UASTRDTerm implements UASTRValueVisitableType
  {
    @Override public abstract TokenIdentifierLower getName();
  }

  /**
   * Value declarations.
   */

  @EqualityReference public static final class UASTRDValueDefined extends
    UASTRDValue
  {
    private final OptionType<UASTRTypeName> ascription;
    private final UASTRExpressionType           expression;
    private final TokenIdentifierLower      name;

    public UASTRDValueDefined(
      final TokenIdentifierLower in_name,
      final OptionType<UASTRTypeName> in_ascription,
      final UASTRExpressionType in_expression)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.ascription = NullCheck.notNull(in_ascription, "Ascription");
      this.expression = NullCheck.notNull(in_expression, "Expression");
    }

    public OptionType<UASTRTypeName> getAscription()
    {
      return this.ascription;
    }

    public UASTRExpressionType getExpression()
    {
      return this.expression;
    }

    @Override public TokenIdentifierLower getName()
    {
      return this.name;
    }

    @Override public
      <T, E extends Throwable, V extends UASTRTermVisitorType<T, E>>
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
      builder.append("[UASTRDValueDefined ");
      builder.append(this.name.getActual());
      builder.append(" ");
      builder.append(this.ascription
        .map(new FunctionType<UASTRTypeName, String>() {
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

    @Override public
      <A, E extends Throwable, V extends UASTRValueVisitorType<A, E>>
      A
      valueVisitableAccept(
        final V v)
        throws E
    {
      return v.valueVisitDefined(this);
    }
  }

  @EqualityReference public static final class UASTRDValueExternal extends
    UASTRDValue
  {
    private final UASTRTypeName        ascription;
    private final UASTRDExternal       external;
    private final TokenIdentifierLower name;

    public UASTRDValueExternal(
      final TokenIdentifierLower in_name,
      final UASTRTypeName in_ascription,
      final UASTRDExternal in_external)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.ascription = NullCheck.notNull(in_ascription, "Ascription");
      this.external = NullCheck.notNull(in_external, "External");
      assert this.external.getEmulation().isNone();
    }

    public UASTRTypeName getAscription()
    {
      return this.ascription;
    }

    public UASTRDExternal getExternal()
    {
      return this.external;
    }

    @Override public TokenIdentifierLower getName()
    {
      return this.name;
    }

    @Override public
      <T, E extends Throwable, V extends UASTRTermVisitorType<T, E>>
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
      <A, E extends Throwable, V extends UASTRValueVisitorType<A, E>>
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

  @EqualityReference public static final class UASTRDValueLocal extends
    UASTRDTermLocal
  {
    private final OptionType<UASTRTypeName> ascription;
    private final UASTRExpressionType           expression;
    private final UASTRTermNameLocal        name;

    public UASTRDValueLocal(
      final UASTRTermNameLocal in_name,
      final OptionType<UASTRTypeName> in_ascription,
      final UASTRExpressionType in_expression)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.ascription = NullCheck.notNull(in_ascription, "Ascription");
      this.expression = NullCheck.notNull(in_expression, "Expression");
    }

    public OptionType<UASTRTypeName> getAscription()
    {
      return this.ascription;
    }

    public UASTRExpressionType getExpression()
    {
      return this.expression;
    }

    public UASTRTermNameLocal getName()
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
        .map(new FunctionType<UASTRTypeName, String>() {
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
