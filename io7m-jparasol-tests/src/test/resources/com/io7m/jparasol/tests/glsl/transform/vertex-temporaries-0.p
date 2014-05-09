package x.y;

module M is

  type t is record
    x : float
  end;

  shader vertex v is
    out vertex out_0 : vector_4f;
    parameter r      : float;
  with
    value r =
      record t {
        x = r
      };
    value z =
      new vector_4f (r.x, r.x, r.x, r.x);
  as
    out out_0 = z;
  end;

end;