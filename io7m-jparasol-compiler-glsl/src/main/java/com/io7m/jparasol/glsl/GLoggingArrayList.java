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

import java.util.ArrayList;
import java.util.Collection;

import com.io7m.jfunctional.FunctionType;
import com.io7m.jlog.LogLevel;
import com.io7m.jlog.LogUsableType;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;

/**
 * An extension of the {@link ArrayList} type that logs additions and
 * removals.
 * 
 * @param <T>
 *          The type of elements
 */

public final class GLoggingArrayList<T> extends ArrayList<T>
{
  private static final long             serialVersionUID;

  static {
    serialVersionUID = -4210494795249243709L;
  }

  private final LogUsableType           log;
  private final FunctionType<T, String> show;

  /**
   * Construct a new logging array list.
   * 
   * @param in_show
   *          The show function
   * @param in_log
   *          The log interface
   */

  public GLoggingArrayList(
    final FunctionType<T, String> in_show,
    final LogUsableType in_log)
  {
    super();
    this.show = NullCheck.notNull(in_show, "Show function");
    this.log = NullCheck.notNull(in_log, "Log");
  }

  @Override public void add(
    final int index,
    final T element)
  {
    if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
      this.log.debug(String.format("Added %s", this.show.call(element)));
    }
    super.add(index, element);
  }

  @Override public boolean add(
    final T e)
  {
    if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
      this.log.debug(String.format("Added %s", this.show.call(e)));
    }
    return super.add(e);
  }

  @Override public boolean addAll(
    final Collection<? extends T> c)
  {
    if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
      for (final T x : c) {
        this.log.debug(String.format("Added %s", this.show.call(x)));
      }
    }

    return super.addAll(c);
  }

  @Override public boolean addAll(
    final int index,
    final Collection<? extends T> c)
  {
    if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
      for (final T x : c) {
        this.log.debug(String.format("Added %s", this.show.call(x)));
      }
    }

    return super.addAll(index, c);
  }

  @Override public void clear()
  {
    if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
      this.log.debug("Cleared all");
    }
    super.clear();
  }

  @Override public T remove(
    final int index)
  {
    final T g = this.get(index);
    if (g != null) {
      if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
        this.log.debug(String.format("Removed %s", this.show.call(g)));
      }
    }
    return super.remove(index);
  }

  @Override public boolean remove(
    final @Nullable Object o)
  {
    if (o != null) {
      if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
        this.log.debug(String.format("Removed %s", o));
      }
    }
    return super.remove(o);
  }

  @Override public boolean removeAll(
    final @Nullable Collection<?> c)
  {
    if (c != null) {
      if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
        for (final Object x : c) {
          this.log.debug(String.format("Removed %s", x));
        }
      }
    }
    return super.removeAll(c);
  }
}
