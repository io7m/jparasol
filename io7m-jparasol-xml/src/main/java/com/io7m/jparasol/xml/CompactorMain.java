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

package com.io7m.jparasol.xml;

import java.io.File;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jlog.Log;
import com.io7m.jlog.LogLevel;
import com.io7m.jlog.LogPolicyAllOn;
import com.io7m.jlog.LogPolicyType;
import com.io7m.jlog.LogType;
import com.io7m.jproperties.JPropertyException;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * The main compactor program.
 */

@EqualityReference public final class CompactorMain
{
  /**
   * The main program.
   * 
   * @param args
   *          Command line arguments
   * @throws JPropertyException
   *           On malformed config files
   * @throws CompactorException
   *           On compaction errors.
   */

  public static void main(
    final String[] args)
    throws CompactorException,
      JPropertyException
  {
    if (args.length != 2) {
      throw new IllegalArgumentException("usage: input output");
    }

    final File input = new File(args[0]);
    final File output = new File(args[1]);

    final LogPolicyType policy = LogPolicyAllOn.newPolicy(LogLevel.LOG_DEBUG);
    final LogType logx = Log.newLog(policy, "compactor");
    Compactor.compactShader(input, output, logx);
  }

  private CompactorMain()
  {
    throw new UnreachableCodeException();
  }
}
