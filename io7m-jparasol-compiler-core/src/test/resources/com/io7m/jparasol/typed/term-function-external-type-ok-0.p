package x.y;

module M is

  function f (x : integer) : integer =
    external xyz is
      vertex true;
      fragment true;
    end;

end;