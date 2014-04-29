package x.y;

module M is

  shader program p is
    vertex nonexistent;
    fragment f;
  end;

  shader fragment f is
    out out_0 : vector_4f as 0;
  with
    value x = new vector_4f (1.0, 1.0, 1.0, 1.0);
  as
    out out_0 = x;
  end;

end;