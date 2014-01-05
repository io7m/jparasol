package x.y;

module M is

  shader fragment f is
    in inv4 : vector_4f;
    out k   : vector_4f as 0;
  with
    value inv4 = inv4;
    discard (inv4);
  as
    out k = inv4;
  end;

end;