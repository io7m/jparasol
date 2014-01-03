package x.y;

module M is

  type t is record
    x : integer
  end;

  shader fragment f is
    parameter k      : t;
    out vertex out_0 : vector_4f as 0;
  with
    value v =
      new vector_4f (0.0, 1.0, 2.0, 3.0);
  as
    out out_0 = v;
  end;

end;