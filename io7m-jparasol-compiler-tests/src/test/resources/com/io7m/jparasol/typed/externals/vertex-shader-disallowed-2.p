package x.y;

module N is

  value x : vector_4f =
    external xyz is
      vertex false;
      fragment true;
    end;

end;

module M is
  import x.y.N;

  shader vertex vert is
    out vertex out_0 : vector_4f;
  as
    out out_0 = N.x;
  end;

end;