package x.y;

module M is
  function f (x : integer) : integer = 23;
  function f (y : float) : float = external xyz is vertex true; fragment true; end;
end;
