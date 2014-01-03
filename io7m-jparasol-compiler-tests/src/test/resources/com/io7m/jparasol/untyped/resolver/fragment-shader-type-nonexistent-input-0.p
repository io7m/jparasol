package x.y;

module M is

  shader fragment f is
    in in_0   : nonexistent;
    out out_0 : vector_4f as 0;
  as
    out out_0 = in_0;
  end;

end;