package x.y;

module M is

  shader vertex v is
  with
    value x__ = 23;
  as
    out gl_Position = x;
  end;

end;
