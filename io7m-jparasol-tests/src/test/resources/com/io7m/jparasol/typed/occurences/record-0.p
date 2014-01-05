package x.y;

module M is

  type t is record
    x : integer,
    y : integer,
    z : integer
  end;

  function f  (x : integer) : t =
    record t {
      x = x,
      y = x,
      z = x
    };

end;