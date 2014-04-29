package x.y;

module M is
  shader fragment f is
    out       out0 : vector_4f as 0;
    out depth od_0 : float;
    out depth od_1 : float;
  as
    out out0 = x;
  end;
end;
