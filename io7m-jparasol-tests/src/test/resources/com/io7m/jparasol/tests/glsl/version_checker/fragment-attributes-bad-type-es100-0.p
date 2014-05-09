package x.y;

module M is

  value xi =
    new vector_4i (0, 1, 2, 3);

  shader fragment f is
    out out_0 : vector_4i as 0;
  as
    out out_0 = xi;
  end;

end;