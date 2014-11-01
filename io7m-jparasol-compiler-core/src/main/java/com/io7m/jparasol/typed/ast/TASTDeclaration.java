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

package com.io7m.jparasol.typed.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jfunctional.OptionType;
import com.io7m.jfunctional.PartialFunctionType;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.ModulePath;
import com.io7m.jparasol.ModulePathFlat;
import com.io7m.jparasol.PackagePath;
import com.io7m.jparasol.core.GVersionES;
import com.io7m.jparasol.core.GVersionFull;
import com.io7m.jparasol.lexer.Token.TokenDiscard;
import com.io7m.jparasol.lexer.Token.TokenIdentifierLower;
import com.io7m.jparasol.lexer.Token.TokenIdentifierUpper;
import com.io7m.jparasol.typed.TType;
import com.io7m.jparasol.typed.TType.TFloat;
import com.io7m.jparasol.typed.TType.TFunction;
import com.io7m.jparasol.typed.TType.TManifestType;
import com.io7m.jparasol.typed.TType.TRecord;
import com.io7m.jparasol.typed.TType.TValueType;
import com.io7m.jparasol.typed.ast.TASTExpression.TASTEVariable;
import com.io7m.jparasol.typed.ast.TASTTermName.TASTTermNameLocal;
import com.io7m.junreachable.UnreachableCodeException;

// CHECKSTYLE_JAVADOC:OFF

@EqualityReference public abstract class TASTDeclaration
{
  /**
   * The type of local declarations.
   */

  @EqualityReference public static abstract class TASTDeclarationLocalLevel extends
    TASTDeclaration
  {
    // Nothing
  }

  /**
   * The type of module-level declarations.
   */

  @EqualityReference public static abstract class TASTDeclarationModuleLevel extends
    TASTDeclaration
  {
    public abstract TokenIdentifierLower getName();
  }

  /**
   * The type of shader-level declarations.
   */

  @EqualityReference public static abstract class TASTDeclarationShaderLevel extends
    TASTDeclaration
  {
    // Nothing
  }

  /**
   * The type of unit-level declarations.
   */

  @EqualityReference public static abstract class TASTDeclarationUnitLevel extends
    TASTDeclaration
  {
    // Nothing
  }

  @EqualityReference public static final class TASTDExternal
  {
    private final OptionType<TASTExpression> emulation;
    private final boolean                    fragment_shader_allowed;
    private final TokenIdentifierLower       name;
    private final boolean                    vertex_shader_allowed;
    private final SortedSet<GVersionES>      supported_es;
    private final SortedSet<GVersionFull>    supported_full;

    public SortedSet<GVersionES> getSupportedES()
    {
      return this.supported_es;
    }

    public SortedSet<GVersionFull> getSupportedFull()
    {
      return this.supported_full;
    }

    public TASTDExternal(
      final TokenIdentifierLower in_name,
      final boolean in_vertex_shader_allowed,
      final boolean in_fragment_shader_allowed,
      final OptionType<TASTExpression> in_emulation,
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

    public OptionType<TASTExpression> getEmulation()
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
      builder.append("[TASTDExternal ");
      builder.append(this.name);
      builder.append(" ");
      builder.append(this.vertex_shader_allowed);
      builder.append(" ");
      builder.append(this.fragment_shader_allowed);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  /**
   * The type of function declarations.
   */

  @EqualityReference public static abstract class TASTDFunction extends
    TASTDTerm
  {
    @Override public abstract TFunction getType();
  }

  @EqualityReference public static final class TASTDFunctionArgument
  {
    private final TASTTermNameLocal name;
    private final TValueType        type;

    public TASTDFunctionArgument(
      final TASTTermNameLocal in_name,
      final TValueType in_type)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.type = NullCheck.notNull(in_type, "Type");
    }

    public TASTTermNameLocal getName()
    {
      return this.name;
    }

    public TValueType getType()
    {
      return this.type;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTDFunctionArgument ");
      builder.append(this.name.show());
      builder.append(" ");
      builder.append(this.type.getName());
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  /**
   * Fully defined functions.
   */

  @EqualityReference public static final class TASTDFunctionDefined extends
    TASTDFunction
  {
    private final List<TASTDFunctionArgument> arguments;
    private final TASTExpression              body;
    private final TokenIdentifierLower        name;
    private final TFunction                   type;

    public TASTDFunctionDefined(
      final TokenIdentifierLower in_name,
      final List<TASTDFunctionArgument> in_arguments,
      final TASTExpression in_body,
      final TFunction in_type)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.arguments = NullCheck.notNull(in_arguments, "Arguments");
      this.body = NullCheck.notNull(in_body, "Body");
      this.type = NullCheck.notNull(in_type, "Type");

      assert in_body.getType().equals(in_type.getReturnType());
    }

    public List<TASTDFunctionArgument> getArguments()
    {
      return this.arguments;
    }

    public TASTExpression getBody()
    {
      return this.body;
    }

    @Override public TokenIdentifierLower getName()
    {
      return this.name;
    }

    public TType getReturnType()
    {
      return this.body.getType();
    }

    @Override public TFunction getType()
    {
      return this.type;
    }

    @Override public
      <T, E extends Throwable, V extends TASTTermVisitorType<T, E>>
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
      builder.append("[TASTDFunctionDefined ");
      builder.append(this.name.getActual());
      builder.append(" ");
      builder.append(this.arguments);
      builder.append(" ");
      builder.append(this.body);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  /**
   * Functions with external declarations (private FFI).
   */

  @EqualityReference public static final class TASTDFunctionExternal extends
    TASTDFunction
  {
    private final List<TASTDFunctionArgument> arguments;
    private final TASTDExternal               external;
    private final TokenIdentifierLower        name;
    private final TFunction                   type;

    public TASTDFunctionExternal(
      final TokenIdentifierLower in_name,
      final List<TASTDFunctionArgument> in_arguments,
      final TFunction in_type,
      final TASTDExternal in_external)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.arguments = NullCheck.notNull(in_arguments, "Arguments");
      this.type = NullCheck.notNull(in_type, "Type");
      this.external = NullCheck.notNull(in_external, "External");

      this.external
        .getEmulation()
        .mapPartial(
          new PartialFunctionType<TASTExpression, Unit, UnreachableCodeException>() {
            @Override public Unit call(
              final TASTExpression x)
            {
              assert x.getType().equals(in_type.getReturnType());
              return Unit.unit();
            }
          });
    }

    public List<TASTDFunctionArgument> getArguments()
    {
      return this.arguments;
    }

    public TASTDExternal getExternal()
    {
      return this.external;
    }

    @Override public TokenIdentifierLower getName()
    {
      return this.name;
    }

    @Override public TFunction getType()
    {
      return this.type;
    }

    @Override public
      <T, E extends Throwable, V extends TASTTermVisitorType<T, E>>
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
      builder.append("[TASTDFunctionExternal ");
      builder.append(this.name.getActual());
      builder.append(" ");
      builder.append(this.arguments);
      builder.append(" ");
      builder.append(this.type.getName());
      builder.append(" ");
      builder.append(this.external);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  /**
   * Import declarations.
   */

  @EqualityReference public static final class TASTDImport extends
    TASTDeclaration
  {
    private final ModulePath                       path;
    private final OptionType<TokenIdentifierUpper> rename;

    public TASTDImport(
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
      builder.append("[TASTDImport ");
      builder.append(this.path);
      builder.append(" ");
      builder.append(this.rename);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  /**
   * Module declarations.
   */

  @EqualityReference public static final class TASTDModule extends
    TASTDeclarationUnitLevel
  {
    private final Map<ModulePathFlat, TASTDImport> imported_modules;
    private final Map<String, TASTDImport>         imported_names;
    private final Map<String, TASTDImport>         imported_renames;
    private final List<TASTDImport>                imports;
    private final ModulePath                       path;
    private final List<String>                     shader_topology;
    private final Map<String, TASTDShader>         shaders;
    private final List<String>                     term_topology;
    private final Map<String, TASTDTerm>           terms;
    private final List<String>                     type_topology;
    private final Map<String, TASTDType>           types;

    public TASTDModule(
      final ModulePath in_path,
      final List<TASTDImport> in_imports,
      final Map<ModulePathFlat, TASTDImport> in_imported_modules,
      final Map<String, TASTDImport> in_imported_names,
      final Map<String, TASTDImport> in_imported_renames,
      final Map<String, TASTDTerm> in_terms,
      final List<String> in_term_topology,
      final Map<String, TASTDType> in_types,
      final List<String> in_type_topology,
      final Map<String, TASTDShader> in_shaders,
      final List<String> in_shader_topology)
    {
      this.path = NullCheck.notNull(in_path, "Path");

      this.imports = NullCheck.notNull(in_imports, "Imports");
      this.imported_modules =
        NullCheck.notNull(in_imported_modules, "Imported modules");
      this.imported_names =
        NullCheck.notNull(in_imported_names, "Imported names");
      this.imported_renames =
        NullCheck.notNull(in_imported_renames, "Imported renames");

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

    public List<TASTDImport> getImports()
    {
      return this.imports;
    }

    public ModulePath getPath()
    {
      return this.path;
    }

    public Map<String, TASTDShader> getShaders()
    {
      return this.shaders;
    }

    public List<String> getShaderTopology()
    {
      return this.shader_topology;
    }

    public Map<String, TASTDTerm> getTerms()
    {
      return this.terms;
    }

    public Map<String, TASTDType> getTypes()
    {
      return this.types;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTDModule ");
      builder.append(ModulePathFlat.fromModulePath(this.path).getActual());
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  /**
   * Package declarations.
   */

  @EqualityReference public static final class TASTDPackage extends
    TASTDeclarationUnitLevel
  {
    private final PackagePath path;

    public TASTDPackage(
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
      builder.append("[TASTDPackage ");
      builder.append(this.path);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  /**
   * The type of shader declarations.
   */

  @EqualityReference public static abstract class TASTDShader extends
    TASTDeclarationModuleLevel
  {
    private final TokenIdentifierLower name;

    protected TASTDShader(
      final TokenIdentifierLower in_name)
    {
      this.name = NullCheck.notNull(in_name, "Name");
    }

    @Override public final TokenIdentifierLower getName()
    {
      return this.name;
    }

    public abstract
      <T, E extends Throwable, V extends TASTShaderVisitorType<T, E>>
      T
      shaderVisitableAccept(
        final V v)
        throws E;
  }

  @EqualityReference public static final class TASTDShaderFragment extends
    TASTDShader
  {
    private final List<TASTDShaderFragmentInput>            inputs;
    private final List<TASTDShaderFragmentLocal>            locals;
    private final List<TASTDShaderFragmentOutput>           outputs;
    private final List<TASTDShaderFragmentParameter>        parameters;
    private final List<TASTDShaderFragmentOutputAssignment> writes;

    public TASTDShaderFragment(
      final TokenIdentifierLower name,
      final List<TASTDShaderFragmentInput> in_inputs,
      final List<TASTDShaderFragmentOutput> in_outputs,
      final List<TASTDShaderFragmentParameter> in_parameters,
      final List<TASTDShaderFragmentLocal> in_locals,
      final List<TASTDShaderFragmentOutputAssignment> in_writes)
    {
      super(name);
      this.inputs = NullCheck.notNull(in_inputs, "Inputs");
      this.outputs = NullCheck.notNull(in_outputs, "Outputs");
      this.parameters = NullCheck.notNull(in_parameters, "Parameters");
      this.locals = NullCheck.notNull(in_locals, "Locals");
      this.writes = NullCheck.notNull(in_writes, "Writes");
    }

    public
      <F, PI, PP, PO, L, O, E extends Throwable, V extends TASTFragmentShaderVisitorType<F, PI, PP, PO, L, O, E>>
      F
      fragmentShaderVisitableAccept(
        final V v)
        throws E
    {
      final List<PI> r_inputs = new ArrayList<PI>();
      for (final TASTDShaderFragmentInput i : this.inputs) {
        assert i != null;
        final PI ri = v.fragmentShaderVisitInput(i);
        r_inputs.add(ri);
      }

      final List<PO> r_outputs = new ArrayList<PO>();
      for (final TASTDShaderFragmentOutput o : this.outputs) {
        assert o != null;
        final PO ro = v.fragmentShaderVisitOutput(o);
        r_outputs.add(ro);
      }

      final List<PP> r_parameters = new ArrayList<PP>();
      for (final TASTDShaderFragmentParameter p : this.parameters) {
        assert p != null;
        final PP rp = v.fragmentShaderVisitParameter(p);
        r_parameters.add(rp);
      }

      final List<L> r_locals = new ArrayList<L>();
      final TASTFragmentShaderLocalVisitorType<L, E> lv =
        v.fragmentShaderVisitLocalsPre();
      if (lv != null) {
        for (final TASTDShaderFragmentLocal l : this.locals) {
          final L rl = l.fragmentShaderLocalVisitableAccept(lv);
          r_locals.add(rl);
        }
      }

      final List<O> r_assigns = new ArrayList<O>();
      for (final TASTDShaderFragmentOutputAssignment w : this.writes) {
        assert w != null;
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

    public List<TASTDShaderFragmentInput> getInputs()
    {
      return this.inputs;
    }

    public List<TASTDShaderFragmentLocal> getLocals()
    {
      return this.locals;
    }

    public List<TASTDShaderFragmentOutput> getOutputs()
    {
      return this.outputs;
    }

    public List<TASTDShaderFragmentParameter> getParameters()
    {
      return this.parameters;
    }

    public List<TASTDShaderFragmentOutputAssignment> getWrites()
    {
      return this.writes;
    }

    @Override public
      <T, E extends Throwable, V extends TASTShaderVisitorType<T, E>>
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
      builder.append("[TASTDShaderFragment ");
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
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TASTDShaderFragmentInput extends
    TASTDShaderFragmentParameters
  {
    private final TASTTermNameLocal name;

    public TASTDShaderFragmentInput(
      final TASTTermNameLocal in_name,
      final TValueType type)
    {
      super(type);
      this.name = NullCheck.notNull(in_name, "Name");
    }

    public TASTTermNameLocal getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTDShaderFragmentInput ");
      builder.append(this.name.show());
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static abstract class TASTDShaderFragmentLocal extends
    TASTDeclarationShaderLevel
  {
    public abstract
      <L, E extends Throwable, V extends TASTFragmentShaderLocalVisitorType<L, E>>
      L
      fragmentShaderLocalVisitableAccept(
        final V v)
        throws E;
  }

  @EqualityReference public static final class TASTDShaderFragmentLocalDiscard extends
    TASTDShaderFragmentLocal
  {
    private final TokenDiscard   discard;
    private final TASTExpression expression;

    public TASTDShaderFragmentLocalDiscard(
      final TokenDiscard in_discard,
      final TASTExpression in_expression)
    {
      this.discard = NullCheck.notNull(in_discard, "Discard");
      this.expression = NullCheck.notNull(in_expression, "Expression");
    }

    @Override public
      <L, E extends Throwable, V extends TASTFragmentShaderLocalVisitorType<L, E>>
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

    public TASTExpression getExpression()
    {
      return this.expression;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTDShaderFragmentLocalDiscard ");
      builder.append(this.expression);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TASTDShaderFragmentLocalValue extends
    TASTDShaderFragmentLocal
  {
    private final TASTDValueLocal value;

    public TASTDShaderFragmentLocalValue(
      final TASTDValueLocal in_value)
    {
      this.value = NullCheck.notNull(in_value, "Value");
    }

    @Override public
      <L, E extends Throwable, V extends TASTFragmentShaderLocalVisitorType<L, E>>
      L
      fragmentShaderLocalVisitableAccept(
        final V v)
        throws E
    {
      return v.fragmentShaderVisitLocalValue(this);
    }

    public TASTDValueLocal getValue()
    {
      return this.value;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTDShaderFragmentLocalValue ");
      builder.append(this.value);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static abstract class TASTDShaderFragmentOutput extends
    TASTDShaderFragmentParameters
  {
    private final TokenIdentifierLower name;

    public TASTDShaderFragmentOutput(
      final TokenIdentifierLower in_name,
      final TValueType type)
    {
      super(type);
      this.name = NullCheck.notNull(in_name, "Name");
    }

    public abstract
      <O, E extends Throwable, V extends TASTFragmentShaderOutputVisitorType<O, E>>
      O
      fragmentShaderOutputVisitableAccept(
        final V v)
        throws E;

    public final TokenIdentifierLower getName()
    {
      return this.name;
    }
  }

  @EqualityReference public static final class TASTDShaderFragmentOutputAssignment extends
    TASTDeclarationShaderLevel
  {
    private final TokenIdentifierLower name;
    private final TASTEVariable        variable;

    public TASTDShaderFragmentOutputAssignment(
      final TokenIdentifierLower in_name,
      final TASTEVariable in_variable)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.variable = NullCheck.notNull(in_variable, "Variable");
    }

    public TokenIdentifierLower getName()
    {
      return this.name;
    }

    public TASTEVariable getVariable()
    {
      return this.variable;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTDShaderFragmentOutputAssignment ");
      builder.append(this.name.getActual());
      builder.append(" ");
      builder.append(this.variable);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TASTDShaderFragmentOutputData extends
    TASTDShaderFragmentOutput
  {
    private final int index;

    public TASTDShaderFragmentOutputData(
      final TokenIdentifierLower name,
      final TValueType type,
      final int in_index)
    {
      super(name, type);
      this.index = in_index;
    }

    @Override public
      <O, E extends Throwable, V extends TASTFragmentShaderOutputVisitorType<O, E>>
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
      builder.append("[TASTDShaderFragmentOutputData index=");
      builder.append(this.index);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TASTDShaderFragmentOutputDepth extends
    TASTDShaderFragmentOutput
  {
    public TASTDShaderFragmentOutputDepth(
      final TokenIdentifierLower name)
    {
      super(name, TFloat.get());
    }

    @Override public
      <O, E extends Throwable, V extends TASTFragmentShaderOutputVisitorType<O, E>>
      O
      fragmentShaderOutputVisitableAccept(
        final V v)
        throws E
    {
      return v.fragmentShaderVisitOutputDepth(this);
    }
  }

  @EqualityReference public static final class TASTDShaderFragmentParameter extends
    TASTDShaderFragmentParameters
  {
    private final TASTTermNameLocal name;

    public TASTDShaderFragmentParameter(
      final TASTTermNameLocal in_name,
      final TValueType type)
    {
      super(type);
      this.name = NullCheck.notNull(in_name, "Name");
    }

    public TASTTermNameLocal getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTDShaderFragmentParameter ");
      builder.append(this.name.show());
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static abstract class TASTDShaderFragmentParameters extends
    TASTDShaderParameters
  {
    TASTDShaderFragmentParameters(
      final TValueType type)
    {
      super(type);
    }
  }

  @EqualityReference public static abstract class TASTDShaderParameters extends
    TASTDeclarationShaderLevel
  {
    private final TValueType type;

    TASTDShaderParameters(
      final TValueType in_type)
    {
      this.type = NullCheck.notNull(in_type, "Type");
    }

    public final TValueType getType()
    {
      return this.type;
    }
  }

  @EqualityReference public static final class TASTDShaderProgram extends
    TASTDShader
  {
    private final TASTShaderName fragment_shader;
    private final TASTShaderName vertex_shader;

    public TASTDShaderProgram(
      final TokenIdentifierLower name,
      final TASTShaderName in_vertex_shader,
      final TASTShaderName in_fragment_shader)
    {
      super(name);
      this.vertex_shader =
        NullCheck.notNull(in_vertex_shader, "Vertex shader");
      this.fragment_shader =
        NullCheck.notNull(in_fragment_shader, "Fragment shader");
    }

    public TASTShaderName getFragmentShader()
    {
      return this.fragment_shader;
    }

    public TASTShaderName getVertexShader()
    {
      return this.vertex_shader;
    }

    @Override public
      <T, E extends Throwable, V extends TASTShaderVisitorType<T, E>>
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
      builder.append("[TASTDShaderProgram ");
      builder.append(this.fragment_shader.show());
      builder.append(" ");
      builder.append(this.vertex_shader.show());
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TASTDShaderVertex extends
    TASTDShader
  {
    private final List<TASTDShaderVertexInput>            inputs;
    private final List<TASTDShaderVertexOutput>           outputs;
    private final List<TASTDShaderVertexParameter>        parameters;
    private final List<TASTDShaderVertexLocalValue>       values;
    private final List<TASTDShaderVertexOutputAssignment> writes;

    public TASTDShaderVertex(
      final TokenIdentifierLower name,
      final List<TASTDShaderVertexInput> in_inputs,
      final List<TASTDShaderVertexOutput> in_outputs,
      final List<TASTDShaderVertexParameter> in_parameters,
      final List<TASTDShaderVertexLocalValue> in_values,
      final List<TASTDShaderVertexOutputAssignment> in_writes)
    {
      super(name);
      this.inputs = NullCheck.notNull(in_inputs, "Inputs");
      this.outputs = NullCheck.notNull(in_outputs, "Outputs");
      this.parameters = NullCheck.notNull(in_parameters, "Parameters");
      this.values = NullCheck.notNull(in_values, "Values");
      this.writes = NullCheck.notNull(in_writes, "Writes");
    }

    public List<TASTDShaderVertexInput> getInputs()
    {
      return this.inputs;
    }

    public List<TASTDShaderVertexOutput> getOutputs()
    {
      return this.outputs;
    }

    public List<TASTDShaderVertexParameter> getParameters()
    {
      return this.parameters;
    }

    public List<TASTDShaderVertexLocalValue> getValues()
    {
      return this.values;
    }

    public List<TASTDShaderVertexOutputAssignment> getWrites()
    {
      return this.writes;
    }

    @Override public
      <T, E extends Throwable, V extends TASTShaderVisitorType<T, E>>
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
      builder.append("[TASTDShaderVertex ");
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
      final String r = builder.toString();
      assert r != null;
      return r;
    }

    public
      <VS, PI, PP, PO, L, O, E extends Throwable, V extends TASTVertexShaderVisitorType<VS, PI, PP, PO, L, O, E>>
      VS
      vertexShaderVisitableAccept(
        final V v)
        throws E
    {
      final List<PI> r_inputs = new ArrayList<PI>();
      for (final TASTDShaderVertexInput i : this.inputs) {
        assert i != null;
        final PI ri = v.vertexShaderVisitInput(i);
        r_inputs.add(ri);
      }

      final List<PO> r_outputs = new ArrayList<PO>();
      for (final TASTDShaderVertexOutput o : this.outputs) {
        assert o != null;
        final PO ro = v.vertexShaderVisitOutput(o);
        r_outputs.add(ro);
      }

      final List<PP> r_parameters = new ArrayList<PP>();
      for (final TASTDShaderVertexParameter p : this.parameters) {
        assert p != null;
        final PP rp = v.vertexShaderVisitParameter(p);
        r_parameters.add(rp);
      }

      final TASTVertexShaderLocalVisitorType<L, E> lv =
        v.vertexShaderVisitLocalsPre();

      final List<L> r_locals = new ArrayList<L>();
      if (lv != null) {
        for (final TASTDShaderVertexLocalValue l : this.values) {
          assert l != null;
          final L rl = lv.vertexShaderVisitLocalValue(l);
          r_locals.add(rl);
        }
      }

      final List<O> r_assigns = new ArrayList<O>();
      for (final TASTDShaderVertexOutputAssignment w : this.writes) {
        assert w != null;
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

  @EqualityReference public static final class TASTDShaderVertexInput extends
    TASTDShaderVertexParameters
  {
    private final TASTTermNameLocal name;

    public TASTDShaderVertexInput(
      final TASTTermNameLocal in_name,
      final TValueType type)
    {
      super(type);
      this.name = NullCheck.notNull(in_name, "Name");
    }

    public TASTTermNameLocal getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTDShaderVertexInput ");
      builder.append(this.name.show());
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TASTDShaderVertexLocalValue extends
    TASTDeclarationShaderLevel
  {
    private final TASTDValueLocal value;

    public TASTDShaderVertexLocalValue(
      final TASTDValueLocal in_value)
    {
      this.value = NullCheck.notNull(in_value, "Value");
    }

    public TASTDValueLocal getValue()
    {
      return this.value;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTDShaderVertexLocalValue ");
      builder.append(this.value);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TASTDShaderVertexOutput extends
    TASTDShaderVertexParameters
  {
    private final boolean              main;
    private final TokenIdentifierLower name;

    public TASTDShaderVertexOutput(
      final TokenIdentifierLower in_name,
      final TValueType type,
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
      builder.append("[TASTDShaderVertexOutput ");
      builder.append(this.name);
      builder.append(" ");
      builder.append(this.main);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TASTDShaderVertexOutputAssignment extends
    TASTDeclarationShaderLevel
  {
    private final TokenIdentifierLower name;
    private final TASTEVariable        variable;

    public TASTDShaderVertexOutputAssignment(
      final TokenIdentifierLower in_name,
      final TASTEVariable in_variable)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.variable = NullCheck.notNull(in_variable, "Variable");
    }

    public TokenIdentifierLower getName()
    {
      return this.name;
    }

    public TASTEVariable getVariable()
    {
      return this.variable;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTDShaderVertexOutputAssignment ");
      builder.append(this.name.getActual());
      builder.append(" ");
      builder.append(this.variable);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static final class TASTDShaderVertexParameter extends
    TASTDShaderVertexParameters
  {
    private final TASTTermNameLocal name;

    public TASTDShaderVertexParameter(
      final TASTTermNameLocal in_name,
      final TValueType type)
    {
      super(type);
      this.name = NullCheck.notNull(in_name, "Name");
    }

    public TASTTermNameLocal getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTDShaderVertexParameter ");
      builder.append(this.name.show());
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  @EqualityReference public static abstract class TASTDShaderVertexParameters extends
    TASTDShaderParameters
  {
    TASTDShaderVertexParameters(
      final TValueType type)
    {
      super(type);
    }
  }

  /**
   * The type of term declarations.
   */

  @EqualityReference public static abstract class TASTDTerm extends
    TASTDeclarationModuleLevel
  {
    public abstract TType getType();

    public abstract
      <T, E extends Throwable, V extends TASTTermVisitorType<T, E>>
      T
      termVisitableAccept(
        final V v)
        throws E;
  }

  /**
   * The type of local term declarations.
   */

  @EqualityReference public static abstract class TASTDTermLocal extends
    TASTDeclarationLocalLevel
  {
    // Nothing
  }

  /**
   * The type of type declarations.
   */

  @EqualityReference public static abstract class TASTDType extends
    TASTDeclarationModuleLevel
  {
    public abstract TType getType();

    public abstract
      <T, E extends Throwable, V extends TASTTypeVisitorType<T, E>>
      T
      typeVisitableAccept(
        final V v)
        throws E;
  }

  /**
   * Record declarations.
   */

  @EqualityReference public static final class TASTDTypeRecord extends
    TASTDType
  {
    private final List<TASTDTypeRecordField> fields;
    private final TokenIdentifierLower       name;
    private final TRecord                    type;

    public TASTDTypeRecord(
      final TokenIdentifierLower in_name,
      final List<TASTDTypeRecordField> in_fields,
      final TRecord in_type)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.type = NullCheck.notNull(in_type, "Type");
      this.fields = NullCheck.notNull(in_fields, "Fields");
    }

    public List<TASTDTypeRecordField> getFields()
    {
      return this.fields;
    }

    @Override public TokenIdentifierLower getName()
    {
      return this.name;
    }

    @Override public TRecord getType()
    {
      return this.type;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTDTypeRecord ");
      builder.append(this.name.getActual());
      builder.append(" ");
      builder.append(this.fields);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }

    @Override public
      <T, E extends Throwable, V extends TASTTypeVisitorType<T, E>>
      T
      typeVisitableAccept(
        final V v)
        throws E
    {
      return v.typeVisitTypeRecord(this);
    }
  }

  @EqualityReference public static final class TASTDTypeRecordField
  {
    private final TokenIdentifierLower name;
    private final TManifestType        type;

    public TASTDTypeRecordField(
      final TokenIdentifierLower in_name,
      final TManifestType in_type)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.type = NullCheck.notNull(in_type, "Type");
    }

    public TokenIdentifierLower getName()
    {
      return this.name;
    }

    public TManifestType getType()
    {
      return this.type;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTDTypeRecordField ");
      builder.append(this.name.getActual());
      builder.append(" ");
      builder.append(this.type.getName());
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  /**
   * Value declarations.
   */

  @EqualityReference public static abstract class TASTDValue extends
    TASTDTerm
  {
    // Nothing
  }

  @EqualityReference public static final class TASTDValueDefined extends
    TASTDValue
  {
    private final TASTExpression       expression;
    private final TokenIdentifierLower name;

    public TASTDValueDefined(
      final TokenIdentifierLower in_name,
      final TASTExpression in_expression)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.expression = NullCheck.notNull(in_expression, "Expression");
    }

    public TASTExpression getExpression()
    {
      return this.expression;
    }

    @Override public TokenIdentifierLower getName()
    {
      return this.name;
    }

    @Override public TType getType()
    {
      return this.expression.getType();
    }

    @Override public
      <T, E extends Throwable, V extends TASTTermVisitorType<T, E>>
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
      builder.append("[TASTDValue ");
      builder.append(this.name.getActual());
      builder.append(" ");
      builder.append(this.expression);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  /**
   * External value declarations.
   */

  @EqualityReference public static final class TASTDValueExternal extends
    TASTDValue
  {
    private final TASTDExternal        external;
    private final TokenIdentifierLower name;
    private final TValueType           type;

    public TASTDValueExternal(
      final TokenIdentifierLower in_name,
      final TValueType in_type,
      final TASTDExternal in_external)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.type = NullCheck.notNull(in_type, "Type");
      this.external = NullCheck.notNull(in_external, "External");
      assert this.external.getEmulation().isNone();
    }

    public TASTDExternal getExternal()
    {
      return this.external;
    }

    @Override public TokenIdentifierLower getName()
    {
      return this.name;
    }

    @Override public TType getType()
    {
      return this.type;
    }

    @Override public
      <T, E extends Throwable, V extends TASTTermVisitorType<T, E>>
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
      builder.append("[TASTDValueExternal ");
      builder.append(this.external);
      builder.append(" ");
      builder.append(this.name);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  /**
   * Local value declarations (let).
   */

  @EqualityReference public static final class TASTDValueLocal extends
    TASTDTermLocal
  {
    private final TASTExpression    expression;
    private final TASTTermNameLocal name;

    public TASTDValueLocal(
      final TASTTermNameLocal in_name,
      final TASTExpression in_expression)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.expression = NullCheck.notNull(in_expression, "Expression");
    }

    public TASTExpression getExpression()
    {
      return this.expression;
    }

    public TASTTermNameLocal getName()
    {
      return this.name;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[TASTDValueLocal ");
      builder.append(this.name.show());
      builder.append(" ");
      builder.append(this.expression);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }
}
