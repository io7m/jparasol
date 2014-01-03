package x.y;

module M is

  function f (x : integer) : integer =
    external xyz is
      vertex true;
      fragment true;
    with
      23
    end;

end;