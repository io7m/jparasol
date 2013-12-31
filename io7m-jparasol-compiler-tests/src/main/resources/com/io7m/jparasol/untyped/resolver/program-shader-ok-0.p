package x.y;

module M is

  shader program p is
    vertex v;
    fragment f;
  end;

  shader vertex v is
  with
    value x = new vector_4f (1.0, 1.0, 1.0, 1.0);
  as
    out gl_Position = x;
  end;

  shader fragment f is
    out out_0 : vector_4f as 0;
  with
    value x = new vector_4f (1.0, 1.0, 1.0, 1.0);
  as
    out out_0 = x;
  end;

end;