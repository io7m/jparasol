package x.y;

module M is

  shader fragment v is
    in x__ : integer;
    out out0 : vector_4f as 0;
  as
    out out0 = x;
  end;

end;
