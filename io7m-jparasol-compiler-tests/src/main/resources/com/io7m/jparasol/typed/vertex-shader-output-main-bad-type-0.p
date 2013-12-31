package x.y;

module M is

  shader vertex v is
    out vertex out_0 : vector_3f;
  with
    value v = new vector_3f (0.0, 1.0, 2.0);
  as
    out out_0 = v;
  end;

end;