package x.y;

module M is
  shader fragment v is
    out out0 : vector_4f as 0;
    out out1 : vector_4f as 1;
  as
    out out0 = x;
  end;
end;
