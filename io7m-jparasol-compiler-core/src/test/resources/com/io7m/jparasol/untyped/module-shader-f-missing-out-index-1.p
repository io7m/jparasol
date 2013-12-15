package x.y;

module M is
  shader fragment f is
    out out0 : vector_4f as 1;
    out out1 : vector_4f as 2;
  as
    out out0 = x;
    out out1 = x;
  end;
end;
