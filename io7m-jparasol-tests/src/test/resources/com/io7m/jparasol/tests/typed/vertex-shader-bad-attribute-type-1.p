package x.y;

module M is

  type t is record
    x : vector_4f
  end;

  shader vertex v is
    in in_0          : vector_4f;
    out vertex out_0 : vector_4f;
    out out_1        : t;
  with
    value result = record t { x = in_0 };
  as
    out out_0 = in_0;
    out out_1 = result;
  end;

end;