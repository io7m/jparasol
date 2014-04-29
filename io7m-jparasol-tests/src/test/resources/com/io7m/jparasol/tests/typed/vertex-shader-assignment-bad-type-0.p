package x.y;

module M is

  value x =
    new vector_4f (0.0, 1.0, 2.0, 3.0);

  value y =
    new vector_3f (0.0, 1.0, 2.0);

  shader vertex v is
    out vertex out_0 : vector_4f;
  as
    out out_0 = y;  
  end;

end;