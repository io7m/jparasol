package x.y;

module M is
  import x.y.N;

  shader fragment f is
    out out0 : vector_4f as 0;
  with
    value x = gl_FragCoord;
    discard (gl_FragCoord.x);
  as
    out out0 = x;
  end;

end;
