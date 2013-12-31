package x.y;

module M is

  shader vertex v is
    parameter p_0 : vector_3f;
    out vertex out_0 : vector_4f;
  with
    value v0 = p_0;
    value v1 = v0;
  as
    out out_0 = v1;
  end;

end;