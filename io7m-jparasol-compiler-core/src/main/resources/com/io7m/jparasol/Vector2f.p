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

module Vector2f is

  function add (
    v0 : vector_2f,
    v1 : vector_2f
  ) : vector_2f =
    external com_io7m_parasol_vector2f_add is
      vertex   true;
      fragment true;
    end;

  function add_scalar (
    v : vector_2f,
    x : float
  ) : vector_2f =
    external com_io7m_parasol_vector2f_add_scalar is
      vertex   true;
      fragment true;
    end;

  function divide (
    v0 : vector_2f,
    v1 : vector_2f
  ) : vector_2f =
    external com_io7m_parasol_vector2f_divide is
      vertex   true;
      fragment true;
    end;
    
  function divide_scalar (
    v0 : vector_2f,
    x  : float
  ) : vector_2f =
    external com_io7m_parasol_vector2f_divide_scalar is
      vertex   true;
      fragment true;
    end;

  function dot (
    v0 : vector_2f,
    v1 : vector_2f
  ) : float =
    external com_io7m_parasol_vector2f_dot is
      vertex   true;
      fragment true;
    end;

  function interpolate (
    v0 : vector_2f,
    v1 : vector_2f,
    a  : float
  ) : vector_2f =
    external com_io7m_parasol_vector2f_interpolate is
      vertex   true;
      fragment true;
    end;

  function magnitude (v : vector_2f) : float =
    external com_io7m_parasol_vector2f_magnitude is
      vertex   true;
      fragment true;
    end;

  function multiply (
    v0 : vector_2f,
    v1 : vector_2f
  ) : vector_2f =
    external com_io7m_parasol_vector2f_multiply is
      vertex   true;
      fragment true;
    end;

  function multiply_scalar (
    v : vector_2f,
    x : float
  ) : vector_2f =
    external com_io7m_parasol_vector2f_multiply_scalar is
      vertex   true;
      fragment true;
    end;

  function negate (v : vector_2f) : vector_2f =
    external com_io7m_parasol_vector2f_negate is
      vertex   true;
      fragment true;
    end;

  function normalize (v : vector_2f) : vector_2f =
    external com_io7m_parasol_vector2f_normalize is
      vertex   true;
      fragment true;
    end;

  function reflect (
    v0 : vector_2f,
    v1 : vector_2f
  ) : vector_2f =
    external com_io7m_parasol_vector2f_reflect is
      vertex   true;
      fragment true;
    end;

  function refract (
    v0 : vector_2f,
    v1 : vector_2f,
    r  : float
  ) : vector_2f =
    external com_io7m_parasol_vector2f_refract is
      vertex   true;
      fragment true;
    end;

  function subtract (
    v0 : vector_2f,
    v1 : vector_2f
  ) : vector_2f =
    external com_io7m_parasol_vector2f_subtract is
      vertex   true;
      fragment true;
    end;

  function subtract_scalar (
    v : vector_2f,
    f : float
  ) : vector_2f =
    external com_io7m_parasol_vector2f_subtract_scalar is
      vertex   true;
      fragment true;
    end;

end;
