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

package com.io7m.jparasol.glsl.ast;

import java.util.List;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jfunctional.Pair;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.core.GVersionType;
import com.io7m.jparasol.glsl.ast.GASTShaderMain.GASTShaderMainFragment;
import com.io7m.jparasol.glsl.ast.GASTShaderMain.GASTShaderMainVertex;
import com.io7m.jparasol.glsl.ast.GTermName.GTermNameGlobal;
import com.io7m.jparasol.typed.TType;

/**
 * The type of GLSL shaders.
 */

@EqualityReference public abstract class GASTShader
{
  /**
   * A fragment shader.
   */

  @EqualityReference public static final class GASTShaderFragment extends
    GASTShader
  {
    private final GVersionType                                     glsl_version;
    private final List<GASTShaderFragmentInput>                    inputs;
    private final GASTShaderMainFragment                           main;
    private final List<GASTShaderFragmentOutput>                   outputs;
    private final List<GASTShaderFragmentParameter>                parameter;
    private final List<Pair<GTermNameGlobal, GASTTermDeclaration>> terms;
    private final List<Pair<GTypeName, GASTTypeDeclaration>>       types;

    /**
     * Construct a fragment shader.
     * 
     * @param in_inputs
     *          The inputs
     * @param in_main
     *          The main function
     * @param in_outputs
     *          The outputs
     * @param in_parameter
     *          The parameters
     * @param in_terms
     *          The terms
     * @param in_types
     *          The types
     * @param in_glsl_version
     *          The GLSL version
     */

    public GASTShaderFragment(
      final List<GASTShaderFragmentInput> in_inputs,
      final GASTShaderMainFragment in_main,
      final List<GASTShaderFragmentOutput> in_outputs,
      final List<GASTShaderFragmentParameter> in_parameter,
      final List<Pair<GTermNameGlobal, GASTTermDeclaration>> in_terms,
      final List<Pair<GTypeName, GASTTypeDeclaration>> in_types,
      final GVersionType in_glsl_version)
    {
      this.inputs = NullCheck.notNullAll(in_inputs, "Inputs");
      this.main = NullCheck.notNull(in_main, "Main");
      this.outputs = NullCheck.notNullAll(in_outputs, "Outputs");
      this.parameter = NullCheck.notNullAll(in_parameter, "Parameters");
      this.terms = NullCheck.notNullAll(in_terms, "Terms");
      this.types = NullCheck.notNullAll(in_types, "Types");
      this.glsl_version = NullCheck.notNull(in_glsl_version, "Version");
    }

    /**
     * @return The GLSL version
     */

    public GVersionType getGLSLVersion()
    {
      return this.glsl_version;
    }

    /**
     * @return The inputs
     */

    public List<GASTShaderFragmentInput> getInputs()
    {
      return this.inputs;
    }

    /**
     * @return The main function
     */

    public GASTShaderMainFragment getMain()
    {
      return this.main;
    }

    /**
     * @return The outputs
     */

    public List<GASTShaderFragmentOutput> getOutputs()
    {
      return this.outputs;
    }

    /**
     * @return The parameters
     */

    public List<GASTShaderFragmentParameter> getParameters()
    {
      return this.parameter;
    }

    /**
     * @return The terms
     */

    public List<Pair<GTermNameGlobal, GASTTermDeclaration>> getTerms()
    {
      return this.terms;
    }

    /**
     * @return The types
     */

    public List<Pair<GTypeName, GASTTypeDeclaration>> getTypes()
    {
      return this.types;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[GASTShaderFragment ");
      builder.append(this.inputs);
      builder.append(" ");
      builder.append(this.main);
      builder.append(" ");
      builder.append(this.outputs);
      builder.append(" ");
      builder.append(this.parameter);
      builder.append(" ");
      builder.append(this.terms);
      builder.append(" ");
      builder.append(this.types);
      builder.append(" ");
      builder.append(this.glsl_version);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  /**
   * A fragment shader input.
   */

  @EqualityReference public static final class GASTShaderFragmentInput
  {
    private final GShaderInputName name;
    private final TType            type;
    private final GTypeName        type_name;

    /**
     * Construct an input.
     * 
     * @param in_name
     *          The name
     * @param in_type_name
     *          The name of the type
     * @param in_type
     *          The type
     */

    public GASTShaderFragmentInput(
      final GShaderInputName in_name,
      final GTypeName in_type_name,
      final TType in_type)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.type_name = NullCheck.notNull(in_type_name, "Type name");
      this.type = NullCheck.notNull(in_type, "Type");
    }

    /**
     * @return The input name
     */

    public GShaderInputName getName()
    {
      return this.name;
    }

    /**
     * @return The input type
     */

    public TType getType()
    {
      return this.type;
    }

    /**
     * @return The input type name
     */

    public GTypeName getTypeName()
    {
      return this.type_name;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[GASTShaderFragmentInput ");
      builder.append(this.name);
      builder.append(" ");
      builder.append(this.type_name);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  /**
   * A fragment shader output.
   */

  @EqualityReference public static final class GASTShaderFragmentOutput
  {
    private final int               index;
    private final GShaderOutputName name;
    private final GTypeName         type;

    /**
     * Construct an output.
     * 
     * @param in_name
     *          The name
     * @param in_index
     *          The output index
     * @param in_type
     *          The type
     */

    public GASTShaderFragmentOutput(
      final GShaderOutputName in_name,
      final int in_index,
      final GTypeName in_type)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.index = in_index;
      this.type = NullCheck.notNull(in_type, "Type name");
    }

    /**
     * @return The output index
     */

    public int getIndex()
    {
      return this.index;
    }

    /**
     * @return The output name
     */

    public GShaderOutputName getName()
    {
      return this.name;
    }

    /**
     * @return The output type
     */

    public GTypeName getType()
    {
      return this.type;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[GASTShaderFragmentOutput ");
      builder.append(this.name);
      builder.append(" ");
      builder.append(this.index);
      builder.append(" ");
      builder.append(this.type);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  /**
   * A fragment shader parameter.
   */

  @EqualityReference public static final class GASTShaderFragmentParameter
  {
    private final List<Pair<String, TType>> expanded;
    private final GShaderParameterName      name;
    private final GTypeName                 type;

    /**
     * Construct a fragment shader parameter.
     * 
     * @param in_name
     *          The name
     * @param in_type
     *          The type
     * @param in_expanded
     *          The expanded parameters
     */

    public GASTShaderFragmentParameter(
      final GShaderParameterName in_name,
      final GTypeName in_type,
      final List<Pair<String, TType>> in_expanded)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.type = NullCheck.notNull(in_type, "Type");
      this.expanded =
        NullCheck.notNullAll(in_expanded, "Expanded parameters");
    }

    /**
     * @return The expanded parameters
     */

    public List<Pair<String, TType>> getExpanded()
    {
      return this.expanded;
    }

    /**
     * @return The parameter name
     */

    public GShaderParameterName getName()
    {
      return this.name;
    }

    /**
     * @return The parameter type
     */

    public GTypeName getType()
    {
      return this.type;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[GASTShaderFragmentParameter ");
      builder.append(this.name);
      builder.append(" ");
      builder.append(this.type);
      builder.append(" ");
      builder.append(this.expanded);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  /**
   * A vertex shader.
   */

  @EqualityReference public static final class GASTShaderVertex extends
    GASTShader
  {
    private final GVersionType                                     glsl_version;
    private final List<GASTShaderVertexInput>                      inputs;
    private final GASTShaderMainVertex                             main;
    private final List<GASTShaderVertexOutput>                     outputs;
    private final List<GASTShaderVertexParameter>                  parameter;
    private final List<Pair<GTermNameGlobal, GASTTermDeclaration>> terms;
    private final List<Pair<GTypeName, GASTTypeDeclaration>>       types;

    /**
     * Construct a vertex shader.
     * 
     * @param in_inputs
     *          The inputs
     * @param in_main
     *          The main function
     * @param in_outputs
     *          The outputs
     * @param in_parameter
     *          The parameters
     * @param in_terms
     *          The terms
     * @param in_types
     *          The types
     * @param in_glsl_version
     *          The GLSL version
     */

    public GASTShaderVertex(
      final List<GASTShaderVertexInput> in_inputs,
      final GASTShaderMainVertex in_main,
      final List<GASTShaderVertexOutput> in_outputs,
      final List<GASTShaderVertexParameter> in_parameter,
      final List<Pair<GTermNameGlobal, GASTTermDeclaration>> in_terms,
      final List<Pair<GTypeName, GASTTypeDeclaration>> in_types,
      final GVersionType in_glsl_version)
    {
      this.inputs = NullCheck.notNullAll(in_inputs, "Inputs");
      this.main = NullCheck.notNull(in_main, "Main");
      this.outputs = NullCheck.notNullAll(in_outputs, "Outputs");
      this.parameter = NullCheck.notNullAll(in_parameter, "Parameters");
      this.terms = NullCheck.notNullAll(in_terms, "Terms");
      this.types = NullCheck.notNullAll(in_types, "Types");
      this.glsl_version = NullCheck.notNull(in_glsl_version, "Version");
    }

    /**
     * @return The GLSL version
     */

    public GVersionType getGLSLVersion()
    {
      return this.glsl_version;
    }

    /**
     * @return The inputs
     */

    public List<GASTShaderVertexInput> getInputs()
    {
      return this.inputs;
    }

    /**
     * @return The main function
     */

    public GASTShaderMainVertex getMain()
    {
      return this.main;
    }

    /**
     * @return The outputs
     */

    public List<GASTShaderVertexOutput> getOutputs()
    {
      return this.outputs;
    }

    /**
     * @return The parameters
     */

    public List<GASTShaderVertexParameter> getParameters()
    {
      return this.parameter;
    }

    /**
     * @return The terms
     */

    public List<Pair<GTermNameGlobal, GASTTermDeclaration>> getTerms()
    {
      return this.terms;
    }

    /**
     * @return The types
     */

    public List<Pair<GTypeName, GASTTypeDeclaration>> getTypes()
    {
      return this.types;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[GASTShaderVertex ");
      builder.append(this.inputs);
      builder.append(" ");
      builder.append(this.main);
      builder.append(" ");
      builder.append(this.outputs);
      builder.append(" ");
      builder.append(this.parameter);
      builder.append(" ");
      builder.append(this.terms);
      builder.append(" ");
      builder.append(this.types);
      builder.append(" ");
      builder.append(this.glsl_version);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  /**
   * A vertex shader input.
   */

  @EqualityReference public static final class GASTShaderVertexInput
  {
    private final GShaderInputName name;
    private final GTypeName        type;

    /**
     * Construct an input.
     * 
     * @param in_name
     *          The name
     * @param in_type
     *          The type
     */

    public GASTShaderVertexInput(
      final GShaderInputName in_name,
      final GTypeName in_type)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.type = NullCheck.notNull(in_type, "Type");
    }

    /**
     * @return The input name
     */

    public GShaderInputName getName()
    {
      return this.name;
    }

    /**
     * @return The input type
     */

    public GTypeName getType()
    {
      return this.type;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[GASTShaderVertexInput ");
      builder.append(this.name);
      builder.append(" ");
      builder.append(this.type);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  /**
   * A vertex shader output.
   */

  @EqualityReference public static final class GASTShaderVertexOutput
  {
    private final GShaderOutputName name;
    private final TType             type;
    private final GTypeName         type_name;

    /**
     * Construct an output.
     * 
     * @param in_name
     *          The name
     * @param in_type_name
     *          The type name
     * @param in_type
     *          The type
     */

    public GASTShaderVertexOutput(
      final GShaderOutputName in_name,
      final GTypeName in_type_name,
      final TType in_type)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.type_name = NullCheck.notNull(in_type_name, "Type name");
      this.type = NullCheck.notNull(in_type, "Type");
    }

    /**
     * @return The output name
     */

    public GShaderOutputName getName()
    {
      return this.name;
    }

    /**
     * @return The output type
     */

    public TType getType()
    {
      return this.type;
    }

    /**
     * @return The output type name
     */

    public GTypeName getTypeName()
    {
      return this.type_name;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[GASTShaderVertexOutput ");
      builder.append(this.name);
      builder.append(" ");
      builder.append(this.type_name);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }

  /**
   * A vertex shader parameter.
   */

  @EqualityReference public static final class GASTShaderVertexParameter
  {
    private final List<Pair<String, TType>> expanded;
    private final GShaderParameterName      name;
    private final GTypeName                 type;

    /**
     * Construct a parameter.
     * 
     * @param in_name
     *          The name
     * @param in_type
     *          The type
     * @param in_expanded
     *          The expanded parameters
     */

    public GASTShaderVertexParameter(
      final GShaderParameterName in_name,
      final GTypeName in_type,
      final List<Pair<String, TType>> in_expanded)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.type = NullCheck.notNull(in_type, "Type");
      this.expanded =
        NullCheck.notNullAll(in_expanded, "Expanded parameters");
    }

    /**
     * @return The expanded parameters
     */

    public List<Pair<String, TType>> getExpanded()
    {
      return this.expanded;
    }

    /**
     * @return The parameter name
     */

    public GShaderParameterName getName()
    {
      return this.name;
    }

    /**
     * @return The parameter type
     */

    public GTypeName getType()
    {
      return this.type;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[GASTShaderVertexParameter ");
      builder.append(this.name);
      builder.append(" ");
      builder.append(this.type);
      builder.append(" ");
      builder.append(this.expanded);
      builder.append("]");
      final String r = builder.toString();
      assert r != null;
      return r;
    }
  }
}
