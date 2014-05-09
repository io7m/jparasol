package x.y;

module M is
  shader vertex v is
    out vertex out0 : vector_4f;
    out vertex out1 : vector_4f;
  as
    out out0 = x;
    out out1 = x;
  end;

  value x =
    new vector_4f (0.0, 1.0, 2.0, 3.0);

end;
