package x.y;

module M is

  shader vertex v is
    in in_0       : vector_4f;
    parameter p_0 : vector_4f;
    out out_0     : vector_4f;
  with
    value v0 = p_0;
    value v1 = in_0;
  as
    out gl_Position = v0;
    out out_0       = v1;
  end;

end;