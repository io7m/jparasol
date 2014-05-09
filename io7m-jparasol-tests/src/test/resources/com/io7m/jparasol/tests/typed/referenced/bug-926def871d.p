package x;

module M is

  type t is record
    x : float,
    y : float,
    z : float
  end;

  type u is record
    t : t
  end;

  type v is record
    u : u
  end;

  value x =
    new vector_4f (0.0, 1.0, 2.0, 3.0);

  shader vertex v is
    out vertex clip : vector_4f;
  as
    out clip = x;
  end;

  shader fragment f is
    parameter thing : v;
    out out_0 : vector_4f as 0;
  with
    value k =
      new vector_4f (0.0, 1.0, 2.0, 3.0);
  as
    out out_0 = k;
  end;

  shader program p is
    vertex v;
    fragment f;
  end;

end;
