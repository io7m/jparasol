package x.y;

module M is
  shader fragment v is
    in out0 : vector_4f;
    out out0 : vector_4f as 0;
  as
    out out0 = x;
  end;
end;
