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

package com.io7m.jparasol.frontend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.io7m.jfunctional.Pair;
import com.io7m.jparasol.UIError;
import com.io7m.jparasol.lexer.Position;
import com.io7m.jparasol.typed.ast.TASTShaderNameFlat;

/**
 * A list of programs to be compiled.
 */

@SuppressWarnings({ "boxing", "null" }) public final class Batch
{
  /**
   * @return A new empty batch.
   */

  public static Batch newBatch()
  {
    return new Batch();
  }

  /**
   * @return A batch loaded from the given file.
   * 
   * @throws IOException
   *           On I/O errors.
   * @throws JPBatchException
   *           If the batch file is invalid.
   */

  public static Batch newBatchFromFile(
    final File batch_file)
    throws IOException,
      JPBatchException
  {
    final BufferedReader reader =
      new BufferedReader(new FileReader(batch_file));

    final Batch batch = Batch.newBatch();

    try {
      int line_no = 1;

      for (;;) {
        final String line = reader.readLine();
        if (line == null) {
          break;
        }
        final String[] segments = line.split(":");
        if (segments.length != 2) {
          final String r =
            String
              .format(
                "Invalid batch at line %d: Format must be: [ output ] , ':' , shader-name",
                line_no);
          assert r != null;
          throw new JPBatchInvalidLine(r);
        }

        final String output = segments[0].trim();
        assert output != null;
        final String name = segments[1].trim();
        assert name != null;

        final TASTShaderNameFlat shader_name =
          TASTShaderNameFlat.parse(
            name,
            Pair.pair(batch_file, new Position(line_no, 0)));

        if (output.isEmpty()) {
          batch.addShader(shader_name);
        } else {
          batch.addShaderWithOutputName(shader_name, output);
        }

        ++line_no;
      }

      return batch;
    } catch (final UIError e) {
      throw new JPBatchInvalidShader(e.getMessage());
    } finally {
      reader.close();
    }
  }

  private final SortedMap<TASTShaderNameFlat, String> outputs_by_shader;
  private final SortedSet<TASTShaderNameFlat>         shaders;
  private final SortedMap<String, TASTShaderNameFlat> shaders_by_output;

  private Batch()
  {
    this.shaders = new TreeSet<TASTShaderNameFlat>();
    this.outputs_by_shader = new TreeMap<TASTShaderNameFlat, String>();
    this.shaders_by_output = new TreeMap<String, TASTShaderNameFlat>();
  }

  /**
   * Add a shader to the batch.
   * 
   * @param shader
   *          The name of the shader.
   * @throws JPBatchDuplicateShader
   *           If the shader has already been added.
   */

  public void addShader(
    final TASTShaderNameFlat shader)
    throws JPBatchException
  {
    if (this.shaders.contains(shader)) {
      throw new JPBatchDuplicateShader(String.format(
        "Shader %s has already been added",
        shader.show()));
    }

    this.shaders.add(shader);
  }

  /**
   * Add a shader to the batch with the given output name.
   * 
   * @param shader
   *          The name of the shader.
   * @param name
   *          The output name of the shader.
   * @throws JPBatchInvalidOutput
   *           If the output name contains a dot.
   * @throws JPBatchDuplicateShader
   *           If the shader has already been added.
   * @throws JPBatchDuplicateOutput
   *           If the given output name has already been used.
   */

  public void addShaderWithOutputName(
    final TASTShaderNameFlat shader,
    final String name)
    throws JPBatchException
  {
    if (this.shaders.contains(shader)) {
      throw new JPBatchDuplicateShader(String.format(
        "Shader %s has already been added",
        shader.show()));
    }

    if (name.contains(".")) {
      throw new JPBatchInvalidOutput(String.format(
        "Output %s contains '.' (for shader %s)",
        name,
        shader.show()));
    }

    if (this.shaders_by_output.containsKey(name)) {
      throw new JPBatchDuplicateOutput(String.format(
        "Output %s has already been added (for shader %s)",
        name,
        shader.show()));
    }

    this.shaders.add(shader);
    this.outputs_by_shader.put(shader, name);
    this.shaders_by_output.put(name, shader);
  }

  /**
   * @return The output names associated with shaders.
   */

  public SortedMap<TASTShaderNameFlat, String> getOutputsByShader()
  {
    final SortedMap<TASTShaderNameFlat, String> r =
      Collections.unmodifiableSortedMap(this.outputs_by_shader);
    assert r != null;
    return r;
  }

  /**
   * @return The set of shaders.
   */

  public SortedSet<TASTShaderNameFlat> getShaders()
  {
    final SortedSet<TASTShaderNameFlat> r =
      Collections.unmodifiableSortedSet(this.shaders);
    assert r != null;
    return r;
  }

  /**
   * @return The shaders associated with output names.
   */

  public SortedMap<String, TASTShaderNameFlat> getShadersByOutput()
  {
    final SortedMap<String, TASTShaderNameFlat> r =
      Collections.unmodifiableSortedMap(this.shaders_by_output);
    assert r != null;
    return r;
  }
}
