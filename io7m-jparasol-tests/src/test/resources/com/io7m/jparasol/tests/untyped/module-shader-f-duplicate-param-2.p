package x.y;

module M is
  shader fragment v is
    parameter out0 : vector_4f;
    in out0 : vector_4f;
  as
    out out0 = x;
  end;
end;