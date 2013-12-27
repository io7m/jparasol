package x.y;

module N is
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

module M is
  import x.y.N as R;

  shader program p is
    vertex N.v;
    fragment N.f;
  end;
end;