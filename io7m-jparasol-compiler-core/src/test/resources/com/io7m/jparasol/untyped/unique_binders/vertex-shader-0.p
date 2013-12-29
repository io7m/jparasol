package x.y;

module M is

  shader vertex v is
    in inv4 : vector_4f;
  with
    value inv4 = inv4;
  as
    out gl_Position = inv4;
  end;

end;