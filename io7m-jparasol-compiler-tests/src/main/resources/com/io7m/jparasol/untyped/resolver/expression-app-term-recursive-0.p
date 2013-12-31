package x.y;

module M is

  function f (x : integer) : integer = x;

  value y = x;

  value x = f (y);

end;