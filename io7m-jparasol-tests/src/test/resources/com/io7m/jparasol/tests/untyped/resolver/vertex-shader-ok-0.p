package x.y;

module M is

  shader vertex v is
    in in_0          : vector_4f;
    parameter p_0    : vector_3f;
    out vertex out_0 : vector_4f;
    out        out_1 : vector_4f;
  as
    out out_1 = p_0;
    out out_0 = in_0;
  end;

end;