package x.y;

module M is

  function f (x : integer) : vector_4f =
    external xyz is
      vertex false;
      fragment true;
    end;

  value x = f (23);

  shader vertex vert is
  as
    out gl_Position = x;
  end;

end;