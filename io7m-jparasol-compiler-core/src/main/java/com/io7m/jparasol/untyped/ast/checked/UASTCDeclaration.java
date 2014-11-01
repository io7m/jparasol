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

package com.io7m.jparasol.untyped.ast.checked;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jfunctional.OptionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.ModulePath;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.PackagePath;
import com.io7m.jparasol.core.GVersionES;
import com.io7m.jparasol.core.GVersionFull;
import com.io7m.jparasol.lexer.Token.TokenDiscard;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.lexer.Token.TokenIdentifierUpper;
import com.io7m.jparasol.untyped.ast.checked.UASTCExpression.UASTCEVariable;

// CHECKSTYLE_JAVADOC:OFF

@EqualityReference public abstract class UASTCDeclaration
{
  /**
   * The type of local declarations.
   */

  @EqualityReference public static abstract class UASTCDeclarationLocalLevel extends
    UASTCDeclaration implements UASTCLocalLevelVisitableType
  {
    public abstract TokenIdentifierLower getName();
  }

  /**
   * The type of module-level declarations.
   */

  @EqualityReference public static abstract class UASTCDeclarationModuleLevel extends
    UASTCDeclaration
  {
    public abstract TokenIdentifierLower getName();
  }

  /**
   * The type of shader-level declarations.
   */

  @EqualityReference public static abstract class UASTCDeclarationShaderLevel extends
    UASTCDeclaration
  {
    // Nothing
  }

  /**
   * The type of unit-level declarations.
   */

  @EqualityReference public static abstract class UASTCDeclarationUnitLevel extends
    UASTCDeclaration implements UASTCUnitLevelVisitableType
  {
    // Nothing
  }

  @EqualityReference public static final class UASTCDExternal
  {
    private final OptionType<UASTCExpression> emulation;
    private final boolean                     fragment_shader_allowed;
    private final TokenIdentifierLower        name;
    private final boolean                     vertex_shader_allowed;
    private final SortedSet<GVersionES>       supported_es;
    private final SortedSet<GVersionFull>     supported_full;

    public SortedSet<GVersionES> getSupportedES()
    {
      return this.supported_es;
    }

    public SortedSet<GVersionFull> getSupportedFull()
    {
      return this.supported_full;
    }

    public UASTCDExternal(
      final TokenIdentifierLower in_name,
      final boolean in_vertex_shader_allowed,
      final boolean in_fragment_shader_allowed,
      final OptionType<UASTCExpression> in_emulation,
      final SortedSet<GVersionES> in_supported_es,
      final SortedSet<GVersionFull> in_supported_full)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.vertex_shader_allowed = in_vertex_shader_allowed;
      this.fragment_shader_allowed = in_fragment_shader_allowed;
      this.emulation = NullCheck.notNull(in_emulation, "Emulation");
      this.supported_es =
        NullCheck.notNullAll(in_supported_es, "Supported ES versions");
      this.supported_full =
        NullCheck.notNullAll(in_supported_full, "Supported full versions");
    }

    public OptionType<UASTCExpression> getEmulation()
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
      builder.append("[UASTCDExternal ");
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

  @EqualityReference public static abstract class UASTCDFunction extends
    UASTCDTerm implements UASTCFunctionVisitableType
  {
    // Nothing
  }

  @EqualityReference public static final class UASTCDFunctionArgument
  {
    private final TokenIdentifierLower name;
    private final UASTCTypePath        type;

    public UASTCDFunctionArgument(
      final TokenIdentifierLower in_name,
      final UASTCTypePath in_type)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.type = NullCheck.notNull(in_type, "Type");
    }

    public TokenIdentifierLower getName()
    {
      return this.name;
    }

    public UASTCTypePath getType()
    {
      return this.type;
    }
  }

  /**
   * Fully defined functions.
   */

  @EqualityReference public static final class UASTCDFunctionDefined extends
    UASTCDFunction
  {
    private final List<UASTCDFunctionArgument> arguments;
    private final UASTCExpression              body;
    private final TokenIdentifierLower         name;
    private final UASTCTypePath                return_type;

    public UASTCDFunctionDefined(
      final TokenIdentifierLower in_name,
      final List<UASTCDFunctionArgument> in_arguments,
      final UASTCTypePath in_return_type,
      final UASTCExpression in_body)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.arguments = NullCheck.notNull(in_arguments, "Arguments");
      this.return_type = NullCheck.notNull(in_return_type, "Return type");
      this.body = NullCheck.notNull(in_body, "Body");
    }

    @Override public
      <A, B, E extends Throwable, V extends UASTCFunctionVisitorType<A, B, E>>
      A
      functionVisitableAccept(
        final V v)
        throws E
    {
      v.functionVisitDefinedPre(this);
      final List<B> args = new ArrayList<B>();
      for (final UASTCDFunctionArgument a : this.arguments) {
        final B x = v.functionVisitArgument(a);
        args.add(x);
      }
      return v.functionVisitDefined(args, this);
    }

    public List<UASTCDFunctionArgument> getArguments()
    {
      return this.arguments;
    }

    public UASTCExpression getBody()
    {
      return this.body;
    }

    @Override public TokenIdentifierLower getName()
    {
      return this.name;
    }

    public UASTCTypePath getReturnType()
    {
      return this.return_type;
    }

    @Override public
      <T, E extends Throwable, V extends UASTCTermVisitorType<T, E>>
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

  @EqualityReference public static final class UASTCDFunctionExternal extends
    UASTCDFunction
  {
    private final List<UASTCDFunctionArgument> arguments;
    private final UASTCDExternal               external;
    private final TokenIdentifierLower         name;
    private final UASTCTypePath                return_type;

    public UASTCDFunctionExternal(
      final TokenIdentifierLower in_name,
      final List<UASTCDFunctionArgument> in_arguments,
      final UASTCTypePath in_return_type,
      final UASTCDExternal in_external)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.arguments = NullCheck.notNull(in_arguments, "Arguments");
      this.return_type = NullCheck.notNull(in_return_type, "Return type");
      this.external = NullCheck.notNull(in_external, "External");
    }

    @Override public
      <A, B, E extends Throwable, V extends UASTCFunctionVisitorType<A, B, E>>
      A
      functionVisitableAccept(
        final V v)
        throws E
    {
      v.functionVisitExternalPre(this);
      final List<B> args = new ArrayList<B>();
      for (final UASTCDFunctionArgument a : this.arguments) {
        final B x = v.functionVisitArgument(a);
        args.add(x);
      }
      return v.functionVisitExternal(args, this);
    }

    public List<UASTCDFunctionArgument> getArguments()
    {
      return this.arguments;
    }

    public UASTCDExternal getExternal()
    {
      return this.external;
    }

    @Override public TokenIdentifierLower getName()
    {
      return this.name;
    }

    public UASTCTypePath getReturnType()
    {
      return this.return_type;
    }

    @Override public
      <T, E extends Throwable, V extends UASTCTermVisitorType<T, E>>
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
      builder.append("[UASTCDFunctionDefined ");
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

  @EqualityReference public static final class UASTCDImport extends
    UASTCDeclaration
  {
    private final ModulePath                       path;
    private final OptionType<TokenIdentifierUpper> rename;

    public UASTCDImport(
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

  @EqualityReference public static final class UASTCDModule extends
    UASTCDeclarationUnitLevel implements UASTCModuleVisitableType
  {
    private final List<UASTCDeclarationModuleLevel> declarations;
    private final Map<ModulePathFlat, UASTCDImport> imported_modules;
    private final Map<String, UASTCDImport>         imported_names;
    private final Map<String, UASTCDImport>         imported_renames;
    private final List<UASTCDImport>                imports;
    private final ModulePath                        path;
    private final Map<String, UASTCDShader>         shaders;
    private final Map<String, UASTCDTerm>           terms;
    private final Map<String, UASTCDType>           types;

    public UASTCDModule(
      final ModulePath in_path,
      final List<UASTCDImport> in_imports,
      final Map<ModulePathFlat, UASTCDImport> in_imported_modules,
      final Map<String, UASTCDImport> in_imported_names,
      final Map<String, UASTCDImport> in_imported_renames,
      final List<UASTCDeclarationModuleLevel> in_declarations,
      final Map<String, UASTCDTerm> in_terms,
      final Map<String, UASTCDType> in_types,
      final Map<String, UASTCDShader> in_shaders)
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

    public List<UASTCDeclarationModuleLevel> getDeclarations()
    {
      return this.declarations;
    }

    public Map<ModulePathFlat, UASTCDImport> getImportedModules()
    {
      return this.imported_modules;
    }

    public Map<String, UASTCDImport> getImportedNames()
    {
      return this.imported_names;
    }

    public Map<String, UASTCDImport> getImportedRenames()
    {
      return this.imported_renames;
    }

    public List<UASTCDImport> getImports()
    {
      return this.imports;
    }

    public ModulePath getPath()
    {
      return this.path;
    }

    public Map<String, UASTCDShader> getShaders()
    {
      return this.shaders;
    }

    public Map<String, UASTCDTerm> getTerms()
    {
      return this.terms;
    }

    public Map<String, UASTCDType> getTypes()
    {
      return this.types;
    }

    @Override public
      <M, I, D, DTE extends D, DTY extends D, DS extends D, E extends Throwable, V extends UASTCModuleVisitorType<M, I, D, DTE, DTY, DS, E>>
      M
      moduleVisitableAccept(
        final V v)
        throws E
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
        final UASTCTypeVisitorType<DTY, E> tv = v.moduleTypesPre(this);
        for (final String k : this.types.keySet()) {
          final UASTCDType ty = this.types.get(k);
          final DTY r = ty.typeVisitableAccept(tv);
          r_types.put(k, r);
          r_decls.add(r);
        }
      }

      {
        final UASTCTermVisitorType<DTE, E> tv = v.moduleTermsPre(this);
        for (final String k : this.terms.keySet()) {
          final UASTCDTerm t = this.terms.get(k);
          final DTE r = t.termVisitableAccept(tv);
          r_terms.put(k, r);
          r_decls.add(r);
        }
      }

      {
        final UASTCShaderVisitorType<DS, E> tv = v.moduleShadersPre(this);
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
      <A, E extends Throwable, V extends UASTCUnitLevelVisitorType<A, E>>
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

  @EqualityReference public static final class UASTCDPackage extends
    UASTCDeclarationUnitLevel
  {
    private final PackagePath path;

    public UASTCDPackage(
      final PackagePath in_path)
    {
      this.path = NullCheck.notNull(in_path, "Path");
    }

    public PackagePath getPath()
    {
      return this.path;
    }

    @Override public
      <A, E extends Throwable, V extends UASTCUnitLevelVisitorType<A, E>>
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

  @EqualityReference public static abstract class UASTCDShader extends
    UASTCDeclarationModuleLevel implements UASTCShaderVisitableType
  {
    private final TokenIdentifierLower name;

    protected UASTCDShader(
      final TokenIdentifierLower in_name)
    {
      this.name = NullCheck.notNull(in_name, "Name");
    }

    @Override public final TokenIdentifierLower getName()
    {
      return this.name;
    }
  }

  @EqualityReference public static final class UASTCDShaderFragment extends
    UASTCDShader implements UASTCFragmentShaderVisitableType
  {
    private final List<UASTCDShaderFragmentInput>            inputs;
    private final List<UASTCDShaderFragmentLocal>            locals;
    private final List<UASTCDShaderFragmentOutput>           outputs;
    private final List<UASTCDShaderFragmentParameter>        parameters;
    private final List<UASTCDShaderFragmentOutputAssignment> writes;

    public UASTCDShaderFragment(
      final TokenIdentifierLower name,
      final List<UASTCDShaderFragmentInput> in_inputs,
      final List<UASTCDShaderFragmentOutput> in_outputs,
      final List<UASTCDShaderFragmentParameter> in_parameters,
      final List<UASTCDShaderFragmentLocal> in_locals,
      final List<UASTCDShaderFragmentOutputAssignment> in_writes)
    {
      super(name);
      this.inputs = NullCheck.notNull(in_inputs, "Inputs");
      this.outputs = NullCheck.notNull(in_outputs, "Outputs");
      this.parameters = NullCheck.notNull(in_parameters, "Parameters");
      this.locals = NullCheck.notNull(in_locals, "Locals");
      this.writes = NullCheck.notNull(in_writes, "Writes");
    }

    @Override public
      <F, PI, PP, PO, L, O, E extends Throwable, V extends UASTCFragmentShaderVisitorType<F, PI, PP, PO, L, O, E>>
      F
      fragmentShaderVisitableAccept(
        final V v)
        throws E
    {
      final List<PI> r_inputs = new ArrayList<PI>();
      for (final UASTCDShaderFragmentInput i : this.inputs) {
        final PI ri = v.fragmentShaderVisitInput(i);
        r_inputs.add(ri);
      }

      final UASTCFragmentShaderOutputVisitorType<PO, E> ov =
        v.fragmentShaderVisitOutputsPre();

      final List<PO> r_outputs = new ArrayList<PO>();
      for (final UASTCDShaderFragmentOutput o : this.outputs) {
        final PO ro = o.fragmentShaderOutputVisitableAccept(ov);
        r_outputs.add(ro);
      }

      final List<PP> r_parameters = new ArrayList<PP>();
      for (final UASTCDShaderFragmentParameter p : this.parameters) {
        final PP rp = v.fragmentShaderVisitParameter(p);
        r_parameters.add(rp);
      }

      final UASTCFragmentShaderLocalVisitorType<L, E> lv =
        v.fragmentShaderVisitLocalsPre();

      final List<L> r_locals = new ArrayList<L>();
      for (final UASTCDShaderFragmentLocal l : this.locals) {
        final L rl = l.fragmentShaderLocalVisitableAccept(lv);
        r_locals.add(rl);
      }

      final List<O> r_assigns = new ArrayList<O>();
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

    public List<UASTCDShaderFragmentInput> getInputs()
    {
      return this.inputs;
    }

    public List<UASTCDShaderFragmentLocal> getLocals()
    {
      return this.locals;
    }

    public List<UASTCDShaderFragmentOutput> getOutputs()
    {
      return this.outputs;
    }

    public List<UASTCDShaderFragmentParameter> getParameters()
    {
      return this.parameters;
    }

    public List<UASTCDShaderFragmentOutputAssignment> getWrites()
    {
      return this.writes;
    }

    @Override public
      <T, E extends Throwable, V extends UASTCShaderVisitorType<T, E>>
      T
      shaderVisitableAccept(
        final V v)
        throws E
    {
      return v.moduleVisitFragmentShader(this);
    }
  }

  @EqualityReference public static final class UASTCDShaderFragmentInput extends
    UASTCDShaderFragmentParameters
  {
    public UASTCDShaderFragmentInput(
      final TokenIdentifierLower name,
      final UASTCTypePath type)
    {
      super(name, type);
    }
  }

  @EqualityReference public static abstract class UASTCDShaderFragmentLocal extends
    UASTCDeclarationShaderLevel implements
    UASTCFragmentShaderLocalVisitableType
  {
    // Nothing
  }

  @EqualityReference public static final class UASTCDShaderFragmentLocalDiscard extends
    UASTCDShaderFragmentLocal
  {
    private final TokenDiscard    discard;
    private final UASTCExpression expression;

    public UASTCDShaderFragmentLocalDiscard(
      final TokenDiscard in_discard,
      final UASTCExpression in_expression)
    {
      this.discard = NullCheck.notNull(in_discard, "Discard");
      this.expression = NullCheck.notNull(in_expression, "Expression");
    }

    @Override public
      <L, E extends Throwable, V extends UASTCFragmentShaderLocalVisitorType<L, E>>
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

    public UASTCExpression getExpression()
    {
      return this.expression;
    }
  }

  @EqualityReference public static final class UASTCDShaderFragmentLocalValue extends
    UASTCDShaderFragmentLocal
  {
    private final UASTCDValueLocal value;

    public UASTCDShaderFragmentLocalValue(
      final UASTCDValueLocal in_value)
    {
      this.value = NullCheck.notNull(in_value, "Value");
    }

    @Override public
      <L, E extends Throwable, V extends UASTCFragmentShaderLocalVisitorType<L, E>>
      L
      fragmentShaderLocalVisitableAccept(
        final V v)
        throws E
    {
      return v.fragmentShaderVisitLocalValue(this);
    }

    public UASTCDValueLocal getValue()
    {
      return this.value;
    }
  }

  @EqualityReference public static abstract class UASTCDShaderFragmentOutput extends
    UASTCDShaderFragmentParameters implements
    UASTCFragmentShaderOutputVisitableType
  {
    public UASTCDShaderFragmentOutput(
      final TokenIdentifierLower name,
      final UASTCTypePath type)
    {
      super(name, type);
    }
  }

  @EqualityReference public static final class UASTCDShaderFragmentOutputAssignment extends
    UASTCDeclarationShaderLevel
  {
    private final TokenIdentifierLower name;
    private final UASTCEVariable       variable;

    public UASTCDShaderFragmentOutputAssignment(
      final TokenIdentifierLower in_name,
      final UASTCEVariable in_variable)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.variable = NullCheck.notNull(in_variable, "Variable");
    }

    public TokenIdentifierLower getName()
    {
      return this.name;
    }

    public UASTCEVariable getVariable()
    {
      return this.variable;
    }
  }

  @EqualityReference public static final class UASTCDShaderFragmentOutputData extends
    UASTCDShaderFragmentOutput
  {
    private final int index;

    public UASTCDShaderFragmentOutputData(
      final TokenIdentifierLower name,
      final UASTCTypePath type,
      final int in_index)
    {
      super(name, type);
      this.index = in_index;
    }

    @Override public
      <O, E extends Throwable, V extends UASTCFragmentShaderOutputVisitorType<O, E>>
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

  @EqualityReference public static final class UASTCDShaderFragmentOutputDepth extends
    UASTCDShaderFragmentOutput
  {
    public UASTCDShaderFragmentOutputDepth(
      final TokenIdentifierLower name,
      final UASTCTypePath type)
    {
      super(name, type);
    }

    @Override public
      <O, E extends Throwable, V extends UASTCFragmentShaderOutputVisitorType<O, E>>
      O
      fragmentShaderOutputVisitableAccept(
        final V v)
        throws E
    {
      return v.fragmentShaderVisitOutputDepth(this);
    }
  }

  @EqualityReference public static final class UASTCDShaderFragmentParameter extends
    UASTCDShaderFragmentParameters
  {
    public UASTCDShaderFragmentParameter(
      final TokenIdentifierLower name,
      final UASTCTypePath type)
    {
      super(name, type);
    }
  }

  @EqualityReference public static abstract class UASTCDShaderFragmentParameters extends
    UASTCDShaderParameters
  {
    UASTCDShaderFragmentParameters(
      final TokenIdentifierLower name,
      final UASTCTypePath type)
    {
      super(name, type);
    }
  }

  @EqualityReference public static abstract class UASTCDShaderParameters extends
    UASTCDeclarationShaderLevel
  {
    private final TokenIdentifierLower name;
    private final UASTCTypePath        type;

    UASTCDShaderParameters(
      final TokenIdentifierLower in_name,
      final UASTCTypePath in_type)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.type = NullCheck.notNull(in_type, "Type");
    }

    public final TokenIdentifierLower getName()
    {
      return this.name;
    }

    public final UASTCTypePath getType()
    {
      return this.type;
    }
  }

  @EqualityReference public static final class UASTCDShaderProgram extends
    UASTCDShader
  {
    private final UASTCShaderPath fragment_shader;
    private final UASTCShaderPath vertex_shader;

    public UASTCDShaderProgram(
      final TokenIdentifierLower name,
      final UASTCShaderPath in_vertex_shader,
      final UASTCShaderPath in_fragment_shader)
    {
      super(name);
      this.vertex_shader =
        NullCheck.notNull(in_vertex_shader, "Vertex shader");
      this.fragment_shader =
        NullCheck.notNull(in_fragment_shader, "Fragment shader");
    }

    public UASTCShaderPath getFragmentShader()
    {
      return this.fragment_shader;
    }

    public UASTCShaderPath getVertexShader()
    {
      return this.vertex_shader;
    }

    @Override public
      <T, E extends Throwable, V extends UASTCShaderVisitorType<T, E>>
      T
      shaderVisitableAccept(
        final V v)
        throws E
    {
      return v.moduleVisitProgramShader(this);
    }
  }

  @EqualityReference public static final class UASTCDShaderVertex extends
    UASTCDShader implements UASTCVertexShaderVisitableType
  {
    private final List<UASTCDShaderVertexInput>            inputs;
    private final List<UASTCDShaderVertexLocalValue>       locals;
    private final List<UASTCDShaderVertexOutput>           outputs;
    private final List<UASTCDShaderVertexParameter>        parameters;
    private final List<UASTCDShaderVertexOutputAssignment> writes;

    public UASTCDShaderVertex(
      final TokenIdentifierLower name,
      final List<UASTCDShaderVertexInput> in_inputs,
      final List<UASTCDShaderVertexOutput> in_outputs,
      final List<UASTCDShaderVertexParameter> in_parameters,
      final List<UASTCDShaderVertexLocalValue> in_locals,
      final List<UASTCDShaderVertexOutputAssignment> in_writes)
    {
      super(name);
      this.inputs = NullCheck.notNull(in_inputs, "Inputs");
      this.outputs = NullCheck.notNull(in_outputs, "Outputs");
      this.parameters = NullCheck.notNull(in_parameters, "Parameters");
      this.locals = NullCheck.notNull(in_locals, "Values");
      this.writes = NullCheck.notNull(in_writes, "Writes");
    }

    public List<UASTCDShaderVertexInput> getInputs()
    {
      return this.inputs;
    }

    public List<UASTCDShaderVertexLocalValue> getLocals()
    {
      return this.locals;
    }

    public List<UASTCDShaderVertexOutput> getOutputs()
    {
      return this.outputs;
    }

    public List<UASTCDShaderVertexParameter> getParameters()
    {
      return this.parameters;
    }

    public List<UASTCDShaderVertexOutputAssignment> getWrites()
    {
      return this.writes;
    }

    @Override public
      <T, E extends Throwable, V extends UASTCShaderVisitorType<T, E>>
      T
      shaderVisitableAccept(
        final V v)
        throws E
    {
      return v.moduleVisitVertexShader(this);
    }

    @Override public
      <VS, PI, PP, PO, L, O, E extends Throwable, V extends UASTCVertexShaderVisitorType<VS, PI, PP, PO, L, O, E>>
      VS
      vertexShaderVisitableAccept(
        final V v)
        throws E
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

      final UASTCVertexShaderLocalVisitorType<L, E> lv =
        v.vertexShaderVisitLocalsPre();

      final List<L> r_locals = new ArrayList<L>();
      for (final UASTCDShaderVertexLocalValue l : this.locals) {
        final L rl = lv.vertexShaderVisitLocalValue(l);
        r_locals.add(rl);
      }

      final List<O> r_assigns = new ArrayList<O>();
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

  @EqualityReference public static final class UASTCDShaderVertexInput extends
    UASTCDShaderVertexParameters
  {
    public UASTCDShaderVertexInput(
      final TokenIdentifierLower name,
      final UASTCTypePath type)
    {
      super(name, type);
    }
  }

  @EqualityReference public static final class UASTCDShaderVertexLocalValue extends
    UASTCDeclarationShaderLevel
  {
    private final UASTCDValueLocal value;

    public UASTCDShaderVertexLocalValue(
      final UASTCDValueLocal in_value)
    {
      this.value = NullCheck.notNull(in_value, "Value");
    }

    public UASTCDValueLocal getValue()
    {
      return this.value;
    }
  }

  @EqualityReference public static final class UASTCDShaderVertexOutput extends
    UASTCDShaderVertexParameters
  {
    private final boolean main;

    public UASTCDShaderVertexOutput(
      final TokenIdentifierLower name,
      final UASTCTypePath type,
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

  @EqualityReference public static final class UASTCDShaderVertexOutputAssignment extends
    UASTCDeclarationShaderLevel
  {
    private final TokenIdentifierLower name;
    private final UASTCEVariable       variable;

    public UASTCDShaderVertexOutputAssignment(
      final TokenIdentifierLower in_name,
      final UASTCEVariable in_variable)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.variable = NullCheck.notNull(in_variable, "Variable");
    }

    public TokenIdentifierLower getName()
    {
      return this.name;
    }

    public UASTCEVariable getVariable()
    {
      return this.variable;
    }
  }

  @EqualityReference public static final class UASTCDShaderVertexParameter extends
    UASTCDShaderVertexParameters
  {
    public UASTCDShaderVertexParameter(
      final TokenIdentifierLower name,
      final UASTCTypePath type)
    {
      super(name, type);
    }
  }

  @EqualityReference public static abstract class UASTCDShaderVertexParameters extends
    UASTCDShaderParameters
  {
    UASTCDShaderVertexParameters(
      final TokenIdentifierLower name,
      final UASTCTypePath type)
    {
      super(name, type);
    }
  }

  /**
   * The type of term declarations.
   */

  @EqualityReference public static abstract class UASTCDTerm extends
    UASTCDeclarationModuleLevel implements UASTCTermVisitableType
  {
    // Nothing
  }

  /**
   * The type of local term declarations.
   */

  @EqualityReference public static abstract class UASTCDTermLocal extends
    UASTCDeclarationLocalLevel
  {
    // Nothing
  }

  /**
   * The type of type declarations.
   */

  @EqualityReference public static abstract class UASTCDType extends
    UASTCDeclarationModuleLevel implements UASTCTypeVisitableType
  {
    // Nothing
  }

  /**
   * Record declarations.
   */

  @EqualityReference public static final class UASTCDTypeRecord extends
    UASTCDType implements UASTCDRecordVisitableType
  {
    private final List<UASTCDTypeRecordField> fields;
    private final TokenIdentifierLower        name;

    public UASTCDTypeRecord(
      final TokenIdentifierLower in_name,
      final List<UASTCDTypeRecordField> in_fields)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.fields = NullCheck.notNull(in_fields, "Fields");
    }

    public List<UASTCDTypeRecordField> getFields()
    {
      return this.fields;
    }

    @Override public TokenIdentifierLower getName()
    {
      return this.name;
    }

    @Override public
      <A, B, E extends Throwable, V extends UASTCDRecordVisitorType<A, B, E>>
      A
      recordTypeVisitableAccept(
        final V v)
        throws E
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
      <T, E extends Throwable, V extends UASTCTypeVisitorType<T, E>>
      T
      typeVisitableAccept(
        final V v)
        throws E
    {
      return v.typeVisitTypeRecord(this);
    }
  }

  @EqualityReference public static final class UASTCDTypeRecordField
  {
    private final TokenIdentifierLower name;
    private final UASTCTypePath        type;

    public UASTCDTypeRecordField(
      final TokenIdentifierLower in_name,
      final UASTCTypePath in_type)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.type = NullCheck.notNull(in_type, "Type");
    }

    public TokenIdentifierLower getName()
    {
      return this.name;
    }

    public UASTCTypePath getType()
    {
      return this.type;
    }
  }

  /**
   * Value declarations.
   */

  @EqualityReference public static abstract class UASTCDValue extends
    UASTCDTerm implements UASTCValueVisitableType
  {
    // Nothing
  }

  @EqualityReference public static final class UASTCDValueDefined extends
    UASTCDValue
  {
    private final OptionType<UASTCTypePath> ascription;
    private final UASTCExpression           expression;
    private final TokenIdentifierLower      name;

    public UASTCDValueDefined(
      final TokenIdentifierLower in_name,
      final OptionType<UASTCTypePath> in_ascription,
      final UASTCExpression in_expression)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.ascription = NullCheck.notNull(in_ascription, "Ascription");
      this.expression = NullCheck.notNull(in_expression, "Expression");
    }

    public OptionType<UASTCTypePath> getAscription()
    {
      return this.ascription;
    }

    public UASTCExpression getExpression()
    {
      return this.expression;
    }

    @Override public TokenIdentifierLower getName()
    {
      return this.name;
    }

    @Override public
      <T, E extends Throwable, V extends UASTCTermVisitorType<T, E>>
      T
      termVisitableAccept(
        final V v)
        throws E
    {
      return v.termVisitValue(this);
    }

    @Override public
      <A, E extends Throwable, V extends UASTCValueVisitorType<A, E>>
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

  @EqualityReference public static final class UASTCDValueExternal extends
    UASTCDValue
  {
    private final UASTCTypePath        ascription;
    private final UASTCDExternal       external;
    private final TokenIdentifierLower name;

    public UASTCDValueExternal(
      final TokenIdentifierLower in_name,
      final UASTCTypePath in_ascription,
      final UASTCDExternal in_external)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.ascription = NullCheck.notNull(in_ascription, "Ascription");
      this.external = NullCheck.notNull(in_external, "External");
      assert this.external.getEmulation().isNone();
    }

    public UASTCTypePath getAscription()
    {
      return this.ascription;
    }

    public UASTCDExternal getExternal()
    {
      return this.external;
    }

    @Override public TokenIdentifierLower getName()
    {
      return this.name;
    }

    @Override public
      <T, E extends Throwable, V extends UASTCTermVisitorType<T, E>>
      T
      termVisitableAccept(
        final V v)
        throws E
    {
      return v.termVisitValueExternal(this);
    }

    @Override public
      <A, E extends Throwable, V extends UASTCValueVisitorType<A, E>>
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

  @EqualityReference public static final class UASTCDValueLocal extends
    UASTCDTermLocal
  {
    private final OptionType<UASTCTypePath> ascription;
    private final UASTCExpression           expression;
    private final TokenIdentifierLower      name;

    public UASTCDValueLocal(
      final TokenIdentifierLower in_name,
      final OptionType<UASTCTypePath> in_ascription,
      final UASTCExpression in_expression)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.ascription = NullCheck.notNull(in_ascription, "Ascription");
      this.expression = NullCheck.notNull(in_expression, "Expression");
    }

    public OptionType<UASTCTypePath> getAscription()
    {
      return this.ascription;
    }

    public UASTCExpression getExpression()
    {
      return this.expression;
    }

    @Override public TokenIdentifierLower getName()
    {
      return this.name;
    }

    @Override public
      <A, E extends Throwable, V extends UASTCLocalLevelVisitorType<A, E>>
      A
      localVisitableAccept(
        final V v)
        throws E
    {
      return v.localVisitValueLocal(this);
    }
  }
}
