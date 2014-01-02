package x.y;

module M is

  function f (
    x : integer
  ) : integer = x;

  value x =
    new integer (f (23));

end;