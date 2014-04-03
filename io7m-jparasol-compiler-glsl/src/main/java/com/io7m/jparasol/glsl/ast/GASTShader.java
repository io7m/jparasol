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

import javax.annotation.Nonnull;

import com.io7m.jaux.functional.Pair;
import com.io7m.jparasol.glsl.GVersion;
import com.io7m.jparasol.glsl.ast.GASTShaderMain.GASTShaderMainFragment;
import com.io7m.jparasol.glsl.ast.GASTShaderMain.GASTShaderMainVertex;
import com.io7m.jparasol.glsl.ast.GTermName.GTermNameGlobal;
import com.io7m.jparasol.typed.TType;

public abstract class GASTShader
{
  public static final class GASTShaderFragment extends GASTShader
  {
    private final @Nonnull GVersion                                         glsl_version;
    private final @Nonnull List<GASTShaderFragmentInput>                    inputs;
    private final @Nonnull GASTShaderMainFragment                           main;
    private final @Nonnull List<GASTShaderFragmentOutput>                   outputs;
    private final @Nonnull List<GASTShaderFragmentParameter>                parameter;
    private final @Nonnull List<Pair<GTermNameGlobal, GASTTermDeclaration>> terms;
    private final @Nonnull List<Pair<GTypeName, GASTTypeDeclaration>>       types;

    public GASTShaderFragment(
      final @Nonnull List<GASTShaderFragmentInput> in_inputs,
      final @Nonnull GASTShaderMainFragment in_main,
      final @Nonnull List<GASTShaderFragmentOutput> in_outputs,
      final @Nonnull List<GASTShaderFragmentParameter> in_parameter,
      final @Nonnull List<Pair<GTermNameGlobal, GASTTermDeclaration>> in_terms,
      final @Nonnull List<Pair<GTypeName, GASTTypeDeclaration>> in_types,
      final @Nonnull GVersion in_glsl_version)
    {
      this.inputs = in_inputs;
      this.main = in_main;
      this.outputs = in_outputs;
      this.parameter = in_parameter;
      this.terms = in_terms;
      this.types = in_types;
      this.glsl_version = in_glsl_version;
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
      final GASTShaderFragment other = (GASTShaderFragment) obj;
      if (!this.glsl_version.equals(other.glsl_version)) {
        return false;
      }
      if (!this.inputs.equals(other.inputs)) {
        return false;
      }
      if (!this.main.equals(other.main)) {
        return false;
      }
      if (!this.outputs.equals(other.outputs)) {
        return false;
      }
      if (!this.parameter.equals(other.parameter)) {
        return false;
      }
      if (!this.terms.equals(other.terms)) {
        return false;
      }
      if (!this.types.equals(other.types)) {
        return false;
      }
      return true;
    }

    public @Nonnull GVersion getGLSLVersion()
    {
      return this.glsl_version;
    }

    public @Nonnull List<GASTShaderFragmentInput> getInputs()
    {
      return this.inputs;
    }

    public @Nonnull GASTShaderMainFragment getMain()
    {
      return this.main;
    }

    public @Nonnull List<GASTShaderFragmentOutput> getOutputs()
    {
      return this.outputs;
    }

    public @Nonnull List<GASTShaderFragmentParameter> getParameters()
    {
      return this.parameter;
    }

    public @Nonnull
      List<Pair<GTermNameGlobal, GASTTermDeclaration>>
      getTerms()
    {
      return this.terms;
    }

    public @Nonnull List<Pair<GTypeName, GASTTypeDeclaration>> getTypes()
    {
      return this.types;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.glsl_version.hashCode();
      result = (prime * result) + this.inputs.hashCode();
      result = (prime * result) + this.main.hashCode();
      result = (prime * result) + this.outputs.hashCode();
      result = (prime * result) + this.parameter.hashCode();
      result = (prime * result) + this.terms.hashCode();
      result = (prime * result) + this.types.hashCode();
      return result;
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
      return builder.toString();
    }
  }

  public static final class GASTShaderFragmentInput
  {
    private final @Nonnull GShaderInputName name;
    private final @Nonnull TType            type;
    private final @Nonnull GTypeName        type_name;

    public GASTShaderFragmentInput(
      final @Nonnull GShaderInputName in_name,
      final @Nonnull GTypeName in_type_name,
      final @Nonnull TType in_type)
    {
      this.name = in_name;
      this.type_name = in_type_name;
      this.type = in_type;
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
      final GASTShaderFragmentInput other = (GASTShaderFragmentInput) obj;
      if (!this.name.equals(other.name)) {
        return false;
      }
      if (!this.type.equals(other.type)) {
        return false;
      }
      if (!this.type_name.equals(other.type_name)) {
        return false;
      }
      return true;
    }

    public @Nonnull GShaderInputName getName()
    {
      return this.name;
    }

    public @Nonnull TType getType()
    {
      return this.type;
    }

    public @Nonnull GTypeName getTypeName()
    {
      return this.type_name;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.name.hashCode();
      result = (prime * result) + this.type.hashCode();
      result = (prime * result) + this.type_name.hashCode();
      return result;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[GASTShaderFragmentInput ");
      builder.append(this.name);
      builder.append(" ");
      builder.append(this.type_name);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class GASTShaderFragmentOutput
  {
    private final int                        index;
    private final @Nonnull GShaderOutputName name;
    private final @Nonnull GTypeName         type;

    public GASTShaderFragmentOutput(
      final @Nonnull GShaderOutputName in_name,
      final int in_index,
      final @Nonnull GTypeName in_type)
    {
      this.name = in_name;
      this.index = in_index;
      this.type = in_type;
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
      final GASTShaderFragmentOutput other = (GASTShaderFragmentOutput) obj;
      if (this.index != other.index) {
        return false;
      }
      if (!this.name.equals(other.name)) {
        return false;
      }
      if (!this.type.equals(other.type)) {
        return false;
      }
      return true;
    }

    public int getIndex()
    {
      return this.index;
    }

    public @Nonnull GShaderOutputName getName()
    {
      return this.name;
    }

    public @Nonnull GTypeName getType()
    {
      return this.type;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.index;
      result = (prime * result) + this.name.hashCode();
      result = (prime * result) + this.type.hashCode();
      return result;
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
      return builder.toString();
    }
  }

  public static final class GASTShaderFragmentParameter
  {
    private final @Nonnull List<Pair<String, TType>> expanded;
    private final @Nonnull GShaderParameterName      name;
    private final @Nonnull GTypeName                 type;

    public GASTShaderFragmentParameter(
      final @Nonnull GShaderParameterName in_name,
      final @Nonnull GTypeName in_type,
      final @Nonnull List<Pair<String, TType>> in_expanded)
    {
      this.name = in_name;
      this.type = in_type;
      this.expanded = in_expanded;
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
      final GASTShaderFragmentParameter other =
        (GASTShaderFragmentParameter) obj;
      if (!this.expanded.equals(other.expanded)) {
        return false;
      }
      if (!this.name.equals(other.name)) {
        return false;
      }
      if (!this.type.equals(other.type)) {
        return false;
      }
      return true;
    }

    public @Nonnull List<Pair<String, TType>> getExpanded()
    {
      return this.expanded;
    }

    public @Nonnull GShaderParameterName getName()
    {
      return this.name;
    }

    public @Nonnull GTypeName getType()
    {
      return this.type;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.expanded.hashCode();
      result = (prime * result) + this.name.hashCode();
      result = (prime * result) + this.type.hashCode();
      return result;
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
      return builder.toString();
    }
  }

  public static final class GASTShaderVertex extends GASTShader
  {
    private final @Nonnull GVersion                                         glsl_version;
    private final @Nonnull List<GASTShaderVertexInput>                      inputs;
    private final @Nonnull GASTShaderMainVertex                             main;
    private final @Nonnull List<GASTShaderVertexOutput>                     outputs;
    private final @Nonnull List<GASTShaderVertexParameter>                  parameter;
    private final @Nonnull List<Pair<GTermNameGlobal, GASTTermDeclaration>> terms;
    private final @Nonnull List<Pair<GTypeName, GASTTypeDeclaration>>       types;

    public GASTShaderVertex(
      final @Nonnull List<GASTShaderVertexInput> in_inputs,
      final @Nonnull GASTShaderMainVertex in_main,
      final @Nonnull List<GASTShaderVertexOutput> in_outputs,
      final @Nonnull List<GASTShaderVertexParameter> in_parameter,
      final @Nonnull List<Pair<GTermNameGlobal, GASTTermDeclaration>> in_terms,
      final @Nonnull List<Pair<GTypeName, GASTTypeDeclaration>> in_types,
      final @Nonnull GVersion in_glsl_version)
    {
      this.inputs = in_inputs;
      this.main = in_main;
      this.outputs = in_outputs;
      this.parameter = in_parameter;
      this.terms = in_terms;
      this.types = in_types;
      this.glsl_version = in_glsl_version;
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
      final GASTShaderVertex other = (GASTShaderVertex) obj;
      if (!this.glsl_version.equals(other.glsl_version)) {
        return false;
      }
      if (!this.inputs.equals(other.inputs)) {
        return false;
      }
      if (!this.main.equals(other.main)) {
        return false;
      }
      if (!this.outputs.equals(other.outputs)) {
        return false;
      }
      if (!this.parameter.equals(other.parameter)) {
        return false;
      }
      if (!this.terms.equals(other.terms)) {
        return false;
      }
      if (!this.types.equals(other.types)) {
        return false;
      }
      return true;
    }

    public @Nonnull GVersion getGLSLVersion()
    {
      return this.glsl_version;
    }

    public @Nonnull List<GASTShaderVertexInput> getInputs()
    {
      return this.inputs;
    }

    public @Nonnull GASTShaderMainVertex getMain()
    {
      return this.main;
    }

    public @Nonnull List<GASTShaderVertexOutput> getOutputs()
    {
      return this.outputs;
    }

    public @Nonnull List<GASTShaderVertexParameter> getParameters()
    {
      return this.parameter;
    }

    public @Nonnull
      List<Pair<GTermNameGlobal, GASTTermDeclaration>>
      getTerms()
    {
      return this.terms;
    }

    public @Nonnull List<Pair<GTypeName, GASTTypeDeclaration>> getTypes()
    {
      return this.types;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.glsl_version.hashCode();
      result = (prime * result) + this.inputs.hashCode();
      result = (prime * result) + this.main.hashCode();
      result = (prime * result) + this.outputs.hashCode();
      result = (prime * result) + this.parameter.hashCode();
      result = (prime * result) + this.terms.hashCode();
      result = (prime * result) + this.types.hashCode();
      return result;
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
      return builder.toString();
    }
  }

  public static final class GASTShaderVertexInput
  {
    private final @Nonnull GShaderInputName name;
    private final @Nonnull GTypeName        type;

    public GASTShaderVertexInput(
      final @Nonnull GShaderInputName in_name,
      final @Nonnull GTypeName in_type)
    {
      this.name = in_name;
      this.type = in_type;
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
      final GASTShaderVertexInput other = (GASTShaderVertexInput) obj;
      if (!this.name.equals(other.name)) {
        return false;
      }
      if (!this.type.equals(other.type)) {
        return false;
      }
      return true;
    }

    public @Nonnull GShaderInputName getName()
    {
      return this.name;
    }

    public @Nonnull GTypeName getType()
    {
      return this.type;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.name.hashCode();
      result = (prime * result) + this.type.hashCode();
      return result;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[GASTShaderVertexInput ");
      builder.append(this.name);
      builder.append(" ");
      builder.append(this.type);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class GASTShaderVertexOutput
  {
    private final @Nonnull GShaderOutputName name;
    private final @Nonnull TType             type;
    private final @Nonnull GTypeName         type_name;

    public GASTShaderVertexOutput(
      final @Nonnull GShaderOutputName in_name,
      final @Nonnull GTypeName in_type_name,
      final @Nonnull TType in_type)
    {
      this.name = in_name;
      this.type_name = in_type_name;
      this.type = in_type;
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
      final GASTShaderVertexOutput other = (GASTShaderVertexOutput) obj;
      if (!this.name.equals(other.name)) {
        return false;
      }
      if (!this.type.equals(other.type)) {
        return false;
      }
      if (!this.type_name.equals(other.type_name)) {
        return false;
      }
      return true;
    }

    public @Nonnull GShaderOutputName getName()
    {
      return this.name;
    }

    public @Nonnull TType getType()
    {
      return this.type;
    }

    public @Nonnull GTypeName getTypeName()
    {
      return this.type_name;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.name.hashCode();
      result = (prime * result) + this.type.hashCode();
      result = (prime * result) + this.type_name.hashCode();
      return result;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[GASTShaderVertexOutput ");
      builder.append(this.name);
      builder.append(" ");
      builder.append(this.type_name);
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class GASTShaderVertexParameter
  {
    private final @Nonnull List<Pair<String, TType>> expanded;
    private final @Nonnull GShaderParameterName      name;
    private final @Nonnull GTypeName                 type;

    public GASTShaderVertexParameter(
      final @Nonnull GShaderParameterName in_name,
      final @Nonnull GTypeName in_type,
      final @Nonnull List<Pair<String, TType>> in_expanded)
    {
      this.name = in_name;
      this.type = in_type;
      this.expanded = in_expanded;
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
      final GASTShaderVertexParameter other = (GASTShaderVertexParameter) obj;
      if (!this.expanded.equals(other.expanded)) {
        return false;
      }
      if (!this.name.equals(other.name)) {
        return false;
      }
      if (!this.type.equals(other.type)) {
        return false;
      }
      return true;
    }

    public @Nonnull List<Pair<String, TType>> getExpanded()
    {
      return this.expanded;
    }

    public @Nonnull GShaderParameterName getName()
    {
      return this.name;
    }

    public @Nonnull GTypeName getType()
    {
      return this.type;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.expanded.hashCode();
      result = (prime * result) + this.name.hashCode();
      result = (prime * result) + this.type.hashCode();
      return result;
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
      return builder.toString();
    }
  }
}
