package x.y;

module M is

  shader fragment f is
    out out_0 : vector_4f as 0;
  with
    discard (later);
    value later = 23;
  as
    out out_0 = p_0;
  end;

end;