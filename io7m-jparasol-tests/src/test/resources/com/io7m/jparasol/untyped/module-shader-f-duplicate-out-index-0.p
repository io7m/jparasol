package x.y;

module M is
  shader fragment f is
    out out0 : vector_4f as 0;
    out out1 : vector_4f as 0;
  as
    out out0 = x;
    out out1 = x;
  end;
end;
