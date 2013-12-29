package x.y;

module M is

  shader vertex v is
  with
    value x = nonexistent;
  as
    out gl_Position = x;
  end;

end;