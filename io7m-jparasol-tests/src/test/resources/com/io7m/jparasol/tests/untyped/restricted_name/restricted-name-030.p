package x.y;

module M is

  shader fragment f is
    parameter x__ : integer;
    out out0 : vector_4f as 0;
  as
    out out0 = x;
  end;

end;
