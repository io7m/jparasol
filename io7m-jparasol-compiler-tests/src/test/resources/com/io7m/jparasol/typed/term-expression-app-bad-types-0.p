package x.y;

module M is

  function f (
    x : integer,
    y : integer
  ) : integer = x;

  value z = f (23, true);

end;