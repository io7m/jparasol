package x.y;

module N is
  value x = 23;
end;

module M is
  import x.y.N;
  function f (x : integer) : integer = N.x;
end;