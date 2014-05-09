package x.y;

module M is

  shader fragment f is
    out out_0     : vector_4f as 0;
    parameter p_0 : vector_3f;
  as
    out out_0 = p_0;
  end;

end;