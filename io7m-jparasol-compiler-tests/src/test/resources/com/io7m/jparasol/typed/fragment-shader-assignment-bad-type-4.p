package x.y;

module M is

  shader fragment f is
    out out_0     : vector_4f as 0;
    parameter p_0 : vector_3f;
  with
    value v = p_0;
  as
    out out_0 = v;
  end;

end;