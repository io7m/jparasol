package x.y;

module M is

  value v = new vector_4f (0.0, 1.0, 2.0, 3.0);

  shader fragment f is
    out out_0 : vector_4f as 0;
  with
    discard (23);
  as
    out out_0 = v;
  end;

end;