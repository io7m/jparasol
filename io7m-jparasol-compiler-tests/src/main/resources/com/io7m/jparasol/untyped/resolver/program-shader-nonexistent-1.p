package x.y;

module M is

  shader program p is
    vertex v;
    fragment nonexistent;
  end;

  shader vertex v is
    out vertex out_0 : vector_4f;
  with
    value x = new vector_4f (1.0, 1.0, 1.0, 1.0);
  as
    out out_0 = x;
  end;

end;