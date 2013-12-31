package x.y;

module M is

  value x =
    new vector_4f (0.0, 1.0, 2.0, 3.0);

  value y = x;

  value z = x;

  shader fragment f is
    out out_0 : vector_4f as 0;
  as
    out out_0 = y;
  end;

end;