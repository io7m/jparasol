package x.y;

module M is

  value y =
    new vector_4f (0.0, 1.0, 2.0, 3.0);
    
  value z = 23;

  shader fragment f is
    out       out_0 : vector_4f as 0;
    out depth od_0  : integer;
  as
    out out_0 = y;
    out od_0  = z;
  end;

end;