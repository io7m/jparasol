package x.y;

module M is

  shader fragment f is
    out out_0 : vector_4f as 0;
  with
    value v = gl_FragCoord [x y z];
  as
    out out_0 = v;
  end;

end;