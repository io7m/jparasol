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
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.jparasol.glsl.ast;

import java.util.List;

import javax.annotation.Nonnull;

import com.io7m.jaux.functional.Pair;
import com.io7m.jparasol.glsl.ast.GASTShaderMain.GASTShaderMainFragment;
import com.io7m.jparasol.glsl.ast.GASTShaderMain.GASTShaderMainVertex;
import com.io7m.jparasol.glsl.ast.GTermName.GTermNameGlobal;
import com.io7m.jparasol.typed.TType;

public abstract class GASTShader
{
  public static final class GASTShaderFragment extends GASTShader
  {
    private final @Nonnull List<GASTShaderFragmentInput>                    inputs;
    private final @Nonnull GASTShaderMainFragment                           main;
    private final @Nonnull List<GASTShaderFragmentOutput>                   outputs;
    private final @Nonnull List<GASTShaderFragmentParameter>                parameter;
    private final @Nonnull List<Pair<GTermNameGlobal, GASTTermDeclaration>> terms;
    private final @Nonnull List<Pair<GTypeName, GASTTypeDeclaration>>       types;

    public GASTShaderFragment(
      final @Nonnull List<GASTShaderFragmentInput> inputs,
      final @Nonnull GASTShaderMainFragment main,
      final @Nonnull List<GASTShaderFragmentOutput> outputs,
      final @Nonnull List<GASTShaderFragmentParameter> parameter,
      final @Nonnull List<Pair<GTermNameGlobal, GASTTermDeclaration>> terms,
      final @Nonnull List<Pair<GTypeName, GASTTypeDeclaration>> types)
    {
      this.inputs = inputs;
      this.main = main;
      this.outputs = outputs;
      this.parameter = parameter;
      this.terms = terms;
      this.types = types;
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
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class GASTShaderFragmentInput
  {
    private final @Nonnull GShaderInputName name;
    private final @Nonnull GTypeName        type;

    public GASTShaderFragmentInput(
      final @Nonnull GShaderInputName name,
      final @Nonnull GTypeName type)
    {
      this.name = name;
      this.type = type;
    }

    public @Nonnull GShaderInputName getName()
    {
      return this.name;
    }

    public @Nonnull GTypeName getType()
    {
      return this.type;
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
      return true;
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
      builder.append("[GASTShaderFragmentInput ");
      builder.append(this.name);
      builder.append(" ");
      builder.append(this.type);
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
      final @Nonnull GShaderOutputName name,
      final int index,
      final @Nonnull GTypeName type)
    {
      this.name = name;
      this.index = index;
      this.type = type;
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
      final @Nonnull GShaderParameterName name,
      final @Nonnull GTypeName type,
      final @Nonnull List<Pair<String, TType>> expanded)
    {
      this.name = name;
      this.type = type;
      this.expanded = expanded;
    }

    public @Nonnull GShaderParameterName getName()
    {
      return this.name;
    }

    public @Nonnull GTypeName getType()
    {
      return this.type;
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
    private final @Nonnull List<GASTShaderVertexInput>                      inputs;
    private final @Nonnull GASTShaderMainVertex                             main;
    private final @Nonnull List<GASTShaderVertexOutput>                     outputs;
    private final @Nonnull List<GASTShaderVertexParameter>                  parameter;
    private final @Nonnull List<Pair<GTermNameGlobal, GASTTermDeclaration>> terms;
    private final @Nonnull List<Pair<GTypeName, GASTTypeDeclaration>>       types;

    public GASTShaderVertex(
      final @Nonnull List<GASTShaderVertexInput> inputs,
      final @Nonnull GASTShaderMainVertex main,
      final @Nonnull List<GASTShaderVertexOutput> outputs,
      final @Nonnull List<GASTShaderVertexParameter> parameter,
      final @Nonnull List<Pair<GTermNameGlobal, GASTTermDeclaration>> terms,
      final @Nonnull List<Pair<GTypeName, GASTTypeDeclaration>> types)
    {
      this.inputs = inputs;
      this.main = main;
      this.outputs = outputs;
      this.parameter = parameter;
      this.terms = terms;
      this.types = types;
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
      builder.append("]");
      return builder.toString();
    }
  }

  public static final class GASTShaderVertexInput
  {
    private final @Nonnull GShaderInputName name;
    private final @Nonnull GTypeName        type;

    public GASTShaderVertexInput(
      final @Nonnull GShaderInputName name,
      final @Nonnull GTypeName type)
    {
      this.name = name;
      this.type = type;
    }

    public @Nonnull GShaderInputName getName()
    {
      return this.name;
    }

    public @Nonnull GTypeName getType()
    {
      return this.type;
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
    private final @Nonnull GTypeName         type;

    public GASTShaderVertexOutput(
      final @Nonnull GShaderOutputName name,
      final @Nonnull GTypeName type)
    {
      this.name = name;
      this.type = type;
    }

    public @Nonnull GShaderOutputName getName()
    {
      return this.name;
    }

    public @Nonnull GTypeName getType()
    {
      return this.type;
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
      return true;
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
      builder.append("[GASTShaderVertexOutput ");
      builder.append(this.name);
      builder.append(" ");
      builder.append(this.type);
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
      final @Nonnull GShaderParameterName name,
      final @Nonnull GTypeName type,
      final @Nonnull List<Pair<String, TType>> expanded)
    {
      this.name = name;
      this.type = type;
      this.expanded = expanded;
    }

    public @Nonnull GShaderParameterName getName()
    {
      return this.name;
    }

    public @Nonnull GTypeName getType()
    {
      return this.type;
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
