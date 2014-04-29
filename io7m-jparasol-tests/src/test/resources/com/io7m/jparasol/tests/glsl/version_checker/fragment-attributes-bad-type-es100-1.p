package x.y;

module M is

  value x =
    new vector_4f (0.0, 1.0, 2.0, 3.0);

  shader fragment f is
    in  in_0  : vector_4i;
    out out_0 : vector_4f as 0;
  as
    out out_0 = x;
  end;

end;