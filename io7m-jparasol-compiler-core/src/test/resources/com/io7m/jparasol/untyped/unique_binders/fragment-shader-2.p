package x.y;

module M is

  shader fragment f is
    out k : vector_4f as 0;
  as
    out k = gl_FragCoord;
  end;

end;