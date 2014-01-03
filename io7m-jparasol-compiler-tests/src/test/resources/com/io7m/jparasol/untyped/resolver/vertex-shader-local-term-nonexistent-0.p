package x.y;

module M is

  shader vertex v is
    out vertex out_0 : vector_4f;
  with
    value x = nonexistent;
  as
    out out_0 = x;
  end;

end;