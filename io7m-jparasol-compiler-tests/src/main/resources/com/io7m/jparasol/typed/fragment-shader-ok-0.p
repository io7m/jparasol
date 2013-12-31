package x.y;

module M is

  shader fragment f is
    in in_0       : vector_4f;
    parameter p_0 : vector_4f;
    out out_0     : vector_4f as 0;
    out out_1     : vector_4f as 1;
  with
    value v0 = p_0;
    value v1 = in_0;
  as
    out out_0 = v0;
    out out_1 = v1;
  end;

end;