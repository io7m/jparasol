package x.y;

module M is

  shader vertex v is
    in in_0          : vector_4f;
    parameter p_0    : vector_4f;
    out vertex out_0 : vector_4f;
    out        out_1 : vector_4f;
  with
    value v0 = p_0;
    value v1 = in_0;
  as
    out out_0 = v0;
    out out_1 = v1;
  end;

  shader program p is
    vertex v;
    fragment v;
  end;

end;