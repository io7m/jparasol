package x.y;

module M is
  shader vertex v is
    out vertex out0 : vector_4f;
  as
    out out0 = x;
  end;
  
  shader fragment v is
    out out0 : vector_4f as 0;
  as
    out out0 = x;
  end;
end;
