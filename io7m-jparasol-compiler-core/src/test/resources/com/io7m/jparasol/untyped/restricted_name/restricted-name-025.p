package x.y;

module M is

  shader fragment f is
    out x__ : integer as 0;
    out out0 : vector_4f as 0;
  as
    out out0 = x;
  end;

end;
