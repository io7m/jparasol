package x.y;

module M is

  shader vertex v is
    in in_0          : nonexistent;
    out vertex out_0 : vector_4f;
  as
    out out_0 = in_0;
  end;

end;