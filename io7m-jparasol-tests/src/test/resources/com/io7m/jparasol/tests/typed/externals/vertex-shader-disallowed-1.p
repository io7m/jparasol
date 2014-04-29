package x.y;

module N is

  function f (x : integer) : vector_4f =
    external xyz is
      vertex false;
      fragment true;
    end;

end;

module M is
  import x.y.N;

  value x = N.f (23);

  shader vertex vert is
    out vertex out_0 : vector_4f;
  as
    out out_0 = x;
  end;

end;