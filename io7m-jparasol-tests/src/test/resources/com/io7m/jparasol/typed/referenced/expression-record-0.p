package x.y;

module M is

  type t is record
    x : float
  end;

  function g (x : float) : float =
    x;

  function f (x : float) : t =
    record t {
      x = g (x)
    };

  shader vertex v is
    out vertex out_0 : vector_4f;
  with
    value r =
      f (23.0);
    value v =
      new vector_4f (r.x, r.x, r.x, r.x);
  as
    out out_0 = v;
  end;

end;