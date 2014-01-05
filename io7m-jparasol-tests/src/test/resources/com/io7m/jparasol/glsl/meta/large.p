package x;

module M is

  import com.io7m.parasol.Vector4f  as V4;
  import com.io7m.parasol.Sampler2D as S2;

  shader vertex v is
    in in_0          : vector_3f;
    in in_1          : vector_4f;
    parameter p_0    : vector_4f;
    out vertex out_0 : vector_4f;
    out        out_1 : vector_4f;
  with
    value result =
      V4.add (in_1, V4.add (p_0, in_0 [x y z x]));
  as
    out out_0 = result;
    out out_1 = result;
  end;

  shader fragment f is
    in out_0       : vector_4f;
    in out_1       : vector_4f;
    parameter pf_0 : sampler_2d;
    out f_out_0    : vector_4f as 0;
  with
    value result =
      S2.texture (pf_0, V4.add (out_0, out_1) [x y]);
  as
    out f_out_0 = result;
  end;

  shader program p is
    vertex v;
    fragment f;
  end;

end;