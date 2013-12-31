package x.y;

module M is

  shader vertex v is
    in in_0       : vector_4f;
    parameter p_0 : vector_3f;
    out out_0     : vector_4f;
  as
    out gl_Position = p_0;
    out out_0       = in_0;
  end;

end;