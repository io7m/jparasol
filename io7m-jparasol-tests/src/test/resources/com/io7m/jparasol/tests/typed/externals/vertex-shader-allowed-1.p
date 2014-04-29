package x.y;

module M is

  value x : vector_4f =
    external xyz is
      vertex true;
      fragment true;
    end;

  shader vertex vert is
    out vertex out_0 : vector_4f;
  as
    out out_0 = x;
  end;

end;