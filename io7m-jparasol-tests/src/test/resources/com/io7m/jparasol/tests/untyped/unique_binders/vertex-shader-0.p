package x.y;

module M is

  shader vertex v is
    out vertex out_0 : vector_4f;
    in         inv4  : vector_4f;
  with
    value inv4 = inv4;
  as
    out out_0 = inv4;
  end;

end;