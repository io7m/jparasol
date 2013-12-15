package x.y;

module M is

  shader vertex v is
    out x__ : integer;
  as
    out gl_Position = x;
  end;

end;
