package x.y;

module M is

  type t is record
    x : float
  end;

  value x =
    record t {
      x = 23.0
    };

  shader fragment f is
    out out_0 : vector_4f as 0;
  with
    value v = new vector_4f (x.x, x.x, x.x, x.x);
  as
    out out_0 = v;
  end;

end;