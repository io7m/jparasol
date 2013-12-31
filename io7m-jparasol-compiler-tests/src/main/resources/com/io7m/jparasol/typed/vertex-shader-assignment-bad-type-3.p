package x.y;

module M is

  shader vertex v is
    parameter p_0    : vector_3f;
    out vertex out_0 : vector_4f;
  as
    out out_0 = p_0;
  end;

end;