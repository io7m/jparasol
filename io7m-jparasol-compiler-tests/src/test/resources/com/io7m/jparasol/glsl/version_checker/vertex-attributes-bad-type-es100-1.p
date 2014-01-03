package x.y;

module M is

  value xf =
    new vector_4f (0.0, 1.0, 2.0, 3.0);

  shader vertex v is
    in         in_0  : vector_4i;
    out vertex out_0 : vector_4f;
  as
    out out_0 = xf;
  end;

end;