package x.y;

module M is

  value x = new vector_4f (0.0, 1.0, 2.0, 3.0);

  shader vertex v is
    out vertex xyz : vector_4f;
  as
    out xyz         = x;
  end;

  shader fragment f is
    in xyz    : vector_4f;
    out out_0 : vector_4f as 0;
  as
    out out_0 = xyz;
  end;

  shader program p is
    vertex v;
    fragment f;
  end;

end;