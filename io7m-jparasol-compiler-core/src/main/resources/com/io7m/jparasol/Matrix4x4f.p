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

module Matrix4x4f is

  function multiply (
    m0 : matrix_4x4f,
    m1 : matrix_4x4f
  ) : matrix_4x4f =
    external com_io7m_parasol_matrix4x4f_multiply is
      vertex   true;
      fragment true;
    end;

  function multiply_vector (
    m : matrix_4x4f,
    v : vector_4f
  ) : vector_4f =
    external com_io7m_parasol_matrix4x4f_multiply_vector is
      vertex   true;
      fragment true;
    end;

end;
