package x.y;

module M is
  import x.y.N;

  shader fragment f is
    out out0 : vector_4f as 0;
  as
    out out0 = x;
  end;

  value x = gl_FragCoord;

end;
