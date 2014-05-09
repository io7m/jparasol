package x.y;

module M is
  shader vertex v is
    out vertex out0 : vector_4f;
    out        out1 : vector_4f;
  as
    out out0 = x;
  end;
end;
