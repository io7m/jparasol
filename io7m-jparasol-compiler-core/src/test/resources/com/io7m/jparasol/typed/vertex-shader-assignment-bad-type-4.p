package x.y;

module M is

  shader vertex v is
    parameter p_0 : vector_3f;
  with
    value v = p_0;
  as
    out gl_Position = v;
  end;

end;