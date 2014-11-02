--
-- Copyright © 2014 <code@io7m.com> http://io7m.com
-- 
-- Permission to use, copy, modify, and/or distribute this software for any
-- purpose with or without fee is hereby granted, provided that the above
-- copyright notice and this permission notice appear in all copies.
-- 
-- THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
-- WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
-- MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
-- SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
-- WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
-- ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
-- IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
--

package com.io7m.parasol;

module Float is

  function absolute (x : float) : float =
    external com_io7m_parasol_float_absolute is
      vertex   true;
      fragment true;
    end;

  function add (
    v0 : float,
    v1 : float
  ) : float =
    external com_io7m_parasol_float_add is
      vertex   true;
      fragment true;
    end;

  function arc_cosine (x : float) : float =
    external com_io7m_parasol_float_arc_cosine is
      vertex   true;
      fragment true;
    end;

  function arc_sine (x : float) : float =
    external com_io7m_parasol_float_arc_sine is
      vertex   true;
      fragment true;
    end;

  function arc_tangent (x : float) : float =
    external com_io7m_parasol_float_arc_tangent is
      vertex   true;
      fragment true;
    end;

  function ceiling (x : float) : float =
    external com_io7m_parasol_float_ceiling is
      vertex   true;
      fragment true;
    end;

  function clamp (
    x   : float,
    min : float,
    max : float
  ) : float =
    external com_io7m_parasol_float_clamp is
      vertex   true;
      fragment true;
    end;

  function cosine (x : float) : float =
    external com_io7m_parasol_float_cosine is
      vertex   true;
      fragment true;
    end;

  function divide (
    x : float,
    y : float
  ) : float =
    external com_io7m_parasol_float_divide is
      vertex   true;
      fragment true;
    end;

  function equals (
    x : float,
    y : float
  ) : boolean =
    external com_io7m_parasol_float_equals is
      vertex   true;
      fragment true;
    end;

  function floor (x : float) : float =
    external com_io7m_parasol_float_floor is
      vertex   true;
      fragment true;
    end;

  function greater (
    x : float,
    y : float
  ) : boolean =
    external com_io7m_parasol_float_greater is
      vertex   true;
      fragment true;
    end;
    
  function greater_or_equal (
    x : float,
    y : float
  ) : boolean =
    external com_io7m_parasol_float_greater_or_equal is
      vertex   true;
      fragment true;
    end;

  function interpolate (
    x : float,
    y : float,
    a : float
  ) : float =
    external com_io7m_parasol_float_interpolate is
      vertex   true;
      fragment true;
    end;

  function is_infinite (x : float) : boolean =
    external com_io7m_parasol_float_is_infinite is
      vertex   true;
      fragment true;
    with
      -- This function cannot be emulated, assume all values are finite.
      false
    end;

  function is_nan (x : float) : boolean =
    external com_io7m_parasol_float_is_nan is
      vertex   true;
      fragment true;
    with
      -- This function cannot be emulated, assume all values are numbers.
      false
    end;

  function lesser (
    x : float,
    y : float
  ) : boolean =
    external com_io7m_parasol_float_lesser is
      vertex   true;
      fragment true;
    end;

  function lesser_or_equal (
    x : float,
    y : float
  ) : boolean =
    external com_io7m_parasol_float_lesser_or_equal is
      vertex   true;
      fragment true;
    end;

  function maximum (
    v0 : float,
    v1 : float
  ) : float =
    external com_io7m_parasol_float_maximum is
      vertex   true;
      fragment true;
    end;

  function minimum (
    v0 : float,
    v1 : float
  ) : float =
    external com_io7m_parasol_float_minimum is
      vertex   true;
      fragment true;
    end;

  function modulo (
    v0 : float,
    v1 : float
  ) : float =
    external com_io7m_parasol_float_modulo is
      vertex   true;
      fragment true;
    end;

  function multiply (
    v0 : float,
    v1 : float
  ) : float =
    external com_io7m_parasol_float_multiply is
      vertex   true;
      fragment true;
    end;

  function negate (
    x : float
  ) : float =
    external com_io7m_parasol_float_negate is
      vertex   true;
      fragment true;
    end;

  function power (
    x : float,
    e : float
  ) : float =
    external com_io7m_parasol_float_power is
      vertex   true;
      fragment true;
    end;

  function round (x : float) : float =
    external com_io7m_parasol_float_round is
      vertex   true;
      fragment true;
    end;

  function sign (x : float) : float =
    external com_io7m_parasol_float_sign is
      vertex   true;
      fragment true;
    with
      if equals (x, 0.0) then
        0.0
      else
        if greater (x, 0.0) then
          1.0
        else
          -1.0
        end
      end
    end;

  function sine (x : float) : float =
    external com_io7m_parasol_float_sine is
      vertex   true;
      fragment true;
    end;

  function square_root (x : float) : float =
    external com_io7m_parasol_float_square_root is
      vertex   true;
      fragment true;
    end;

  function subtract (
    v0 : float,
    v1 : float
  ) : float =
    external com_io7m_parasol_float_subtract is
      vertex   true;
      fragment true;
    end;

  function tangent (x : float) : float =
    external com_io7m_parasol_float_tangent is
      vertex   true;
      fragment true;
    end;

  function truncate (x : float) : float =
    external com_io7m_parasol_float_truncate is
      vertex   true;
      fragment true;
    with
      multiply (floor (absolute (x)), sign (x))
    end;

end;
