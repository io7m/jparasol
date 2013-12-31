package x.y;

module M is

  function f (x : integer) : vector_4f =
    external xyz is
      vertex false;
      fragment true;
    end;

  value x = f (23);

  shader vertex vert is
    out vertex out_0 : vector_4f;
  as
    out out_0 = x;
  end;

end;