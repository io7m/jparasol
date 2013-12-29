package x.y;

module M is

  value y =
    new vector_3f (0.0, 1.0, 2.0);

  shader fragment f is
    out out_0 : vector_4f as 0;
  as
    out out_0 = y;  
  end;

end;