package x.y;

module M is

  shader fragment f is
    out out0 : vector_4f as 0;
  with
    discard (x__);
  as
    out out0 = x;
  end;

end;
