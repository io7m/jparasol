package x.y;

module M is

  shader fragment f is
    out out0 : vector_4f as 0;
  with
    discard (k__);
  as
    out out0 = z;
  end;

end;
