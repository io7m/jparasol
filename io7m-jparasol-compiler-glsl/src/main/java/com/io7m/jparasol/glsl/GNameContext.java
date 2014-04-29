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

package com.io7m.jparasol.glsl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jlog.LogUsableType;
import com.io7m.jnull.NullCheck;
import com.io7m.jparasol.NameShowType;

/**
 * A name context.
 * 
 * @param <T>
 *          The type of initial names
 * @param <U>
 *          The type of fresh names
 */

@EqualityReference public final class GNameContext<T extends NameShowType, U>
{
  /**
   * Construct a new name context.
   * 
   * @param log
   *          A log interface
   * @param constructor
   *          A name constructor
   * @return A new name context
   */

  public static <T extends NameShowType, U> GNameContext<T, U> newContext(
    final LogUsableType log,
    final GNameConstructorType<U> constructor)
  {
    return new GNameContext<T, U>(log, constructor);
  }

  private final GNameConstructorType<U> constructor;
  private final Map<T, U>               existing;
  private final LogUsableType           log;
  private final AtomicInteger           prime;
  private final Set<String>             used;

  private GNameContext(
    final LogUsableType in_log,
    final GNameConstructorType<U> in_constructor)
  {
    this.log = NullCheck.notNull(in_log, "Log").with("names");
    this.constructor = in_constructor;
    this.prime = new AtomicInteger(0);
    this.used = new HashSet<String>();
    this.existing = new HashMap<T, U>();
  }

  /**
   * @param name
   *          The initial name
   * @return A fresh name based on <code>name</code>
   */

  public U freshName(
    final T name)
  {
    final String base = name.show().replace('.', '_');
    final StringBuilder s = new StringBuilder();
    s.append(base);

    for (;;) {
      if (this.used.contains(s.toString())) {
        s.setLength(0);
        s.append(base);
        s.append(this.prime.incrementAndGet());
      } else {
        break;
      }
    }

    final String st = s.toString();
    final U actual = this.constructor.newName(st);
    this.existing.put(name, actual);
    this.used.add(st);
    this.log.debug(String.format("Fresh name %s", st));
    return actual;
  }

  /**
   * @param name
   *          The name
   * @return The name associated with the given name.
   */

  public U getName(
    final T name)
  {
    if (this.existing.containsKey(name)) {
      return this.existing.get(name);
    }
    return this.freshName(name);
  }
}
