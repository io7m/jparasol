package x.y;

module M is

  type t is record
    x : vector_4f
  end;

  shader vertex v is
    in in_0          : t;
    out vertex out_0 : vector_4f;
  with
    value result = in_0.x;
  as
    out out_0 = result;
  end;

end;