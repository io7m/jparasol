package x.y;

module M is

  type t is record
    x : float
  end;

  shader fragment f is
    out out_0   : vector_4f as 0;
    parameter r : float;
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