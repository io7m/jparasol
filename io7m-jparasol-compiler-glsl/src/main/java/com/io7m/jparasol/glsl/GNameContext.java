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
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;

import com.io7m.jlog.Log;
import com.io7m.jparasol.NameShow;

public final class GNameContext<T extends NameShow, U>
{
  public static @Nonnull
    <T extends NameShow, U>
    GNameContext<T, U>
    newContext(
      final @Nonnull Log log,
      final @Nonnull GNameConstructor<U> constructor)
  {
    return new GNameContext<T, U>(log, constructor);
  }

  private final @Nonnull GNameConstructor<U> constructor;
  private final @Nonnull Map<T, U>           existing;
  private final @Nonnull Log                 log;
  private final @Nonnull AtomicInteger       prime;
  private final @Nonnull HashSet<String>     used;

  private GNameContext(
    final @Nonnull Log log,
    final @Nonnull GNameConstructor<U> constructor)
  {
    this.log = new Log(log, "names");
    this.constructor = constructor;
    this.prime = new AtomicInteger(0);
    this.used = new HashSet<String>();
    this.existing = new HashMap<T, U>();
  }

  public @Nonnull U freshName(
    final @Nonnull T name)
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

  public @Nonnull U getName(
    final @Nonnull T name)
  {
    if (this.existing.containsKey(name)) {
      return this.existing.get(name);
    }
    return this.freshName(name);
  }
}
