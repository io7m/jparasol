package x.y;

module M is

  value xi =
    new vector_4i (0, 1, 2, 3);
  value xf =
    new vector_4f (0.0, 1.0, 2.0, 3.0);

  shader vertex v is
    out vertex out_0 : vector_4f;
    out        out_1 : vector_4i;
  as
    out out_0 = xf;
    out out_1 = xi;
  end;

end;