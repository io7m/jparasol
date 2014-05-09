package x.y;

module M is

  type t is record
    x : float
  end;

  value x =
    record t {
      x = 23.0
    };

  shader vertex v is
    out vertex out_0 : vector_4f;
  with
    value v = new vector_4f (x.x, x.x, x.x, x.x);
  as
    out out_0 = v;
  end;

end;