package x.y;

module M is

  type t is record
    x : integer,
    y : integer,
    z : integer
  end;
  
  value x = 23;

  value f =
    record t {
      x = x,
      y = x,
      z = x
    };

end;