package x.y;

module M is

  shader vertex v is
    out vertex out_0 : vector_4f;
  with
    value v =
      new vector_4f (0.0, 1.0, 2.0, 3.0);
  as
    out out_0 = v;
  end;

end;