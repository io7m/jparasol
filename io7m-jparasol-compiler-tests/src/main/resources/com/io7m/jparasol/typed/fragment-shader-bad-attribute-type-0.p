package x.y;

module M is

  type t is record
    x : vector_4f
  end;

  shader fragment f is
    in  in_0  : t;
    out out_0 : vector_4f as 0;
  with
    value result = in_0.x;
  as
    out out_0 = result;
  end;

end;