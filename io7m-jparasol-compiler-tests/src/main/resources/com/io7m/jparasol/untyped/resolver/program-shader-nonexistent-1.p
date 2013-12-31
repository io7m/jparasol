package x.y;

module M is

  shader program p is
    vertex v;
    fragment nonexistent;
  end;

  shader vertex v is
  with
    value x = new vector_4f (1.0, 1.0, 1.0, 1.0);
  as
    out gl_Position = x;
  end;

end;