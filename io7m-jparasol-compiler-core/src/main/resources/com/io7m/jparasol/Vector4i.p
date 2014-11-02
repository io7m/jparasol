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

module Vector4i is

  function add (
    v0 : vector_4i,
    v1 : vector_4i
  ) : vector_4i =
    external com_io7m_parasol_vector4i_add is
      vertex   true;
      fragment true;
    end;

  function add_scalar (
    v : vector_4i,
    x : integer
  ) : vector_4i =
    external com_io7m_parasol_vector4i_add_scalar is
      vertex   true;
      fragment true;
    end;

  function divide (
    v0 : vector_4i,
    v1 : vector_4i
  ) : vector_4i =
    external com_io7m_parasol_vector4i_divide is
      vertex   true;
      fragment true;
    end;
    
  function divide_scalar (
    v0 : vector_4i,
    x  : integer
  ) : vector_4i =
    external com_io7m_parasol_vector4i_divide_scalar is
      vertex   true;
      fragment true;
    end;

  function dot (
    v0 : vector_4i,
    v1 : vector_4i
  ) : float =
    external com_io7m_parasol_vector4i_dot is
      vertex   true;
      fragment true;
    end;

  function interpolate (
    v0 : vector_4i,
    v1 : vector_4i,
    a  : float
  ) : vector_4i =
    external com_io7m_parasol_vector4i_interpolate is
      vertex   true;
      fragment true;
    end;

  function maximum (
    v0 : vector_4i,
    v1 : vector_4i
  ) : vector_4i =
    external com_io7m_parasol_vector4i_maximum is
      vertex   true;
      fragment true;
    end;

  function minimum (
    v0 : vector_4i,
    v1 : vector_4i
  ) : vector_4i =
    external com_io7m_parasol_vector4i_minimum is
      vertex   true;
      fragment true;
    end;

  function magnitude (v : vector_4i) : float =
    external com_io7m_parasol_vector4i_magnitude is
      vertex   true;
      fragment true;
    end;

  function multiply (
    v0 : vector_4i,
    v1 : vector_4i
  ) : vector_4i =
    external com_io7m_parasol_vector4i_multiply is
      vertex   true;
      fragment true;
    end;

  function multiply_scalar (
    v : vector_4i,
    x : integer
  ) : vector_4i =
    external com_io7m_parasol_vector4i_multiply_scalar is
      vertex   true;
      fragment true;
    end;

  function negate (v : vector_4i) : vector_4i =
    external com_io7m_parasol_vector4i_negate is
      vertex   true;
      fragment true;
    end;

  function normalize (v : vector_4i) : vector_4i =
    external com_io7m_parasol_vector4i_normalize is
      vertex   true;
      fragment true;
    end;

  function reflect (
    v0 : vector_4i,
    v1 : vector_4i
  ) : vector_4i =
    external com_io7m_parasol_vector4i_reflect is
      vertex   true;
      fragment true;
    end;

  function refract (
    v0 : vector_4i,
    v1 : vector_4i,
    r  : float
  ) : vector_4i =
    external com_io7m_parasol_vector4i_refract is
      vertex   true;
      fragment true;
    end;

  function subtract (
    v0 : vector_4i,
    v1 : vector_4i
  ) : vector_4i =
    external com_io7m_parasol_vector4i_subtract is
      vertex   true;
      fragment true;
    end;

  function subtract_scalar (
    v : vector_4i,
    f : integer
  ) : vector_4i =
    external com_io7m_parasol_vector4i_subtract_scalar is
      vertex   true;
      fragment true;
    end;

end;
