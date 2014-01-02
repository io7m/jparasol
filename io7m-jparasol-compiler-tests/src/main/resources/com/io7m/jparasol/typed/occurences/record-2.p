package x.y;

module M is

  type t is record
    x : integer,
    y : integer,
    z : integer
  end;

  value y = 23;

  value f =
    let value x = y; in
      record t {
        x = x,
        y = x,
        z = x
      }
    end;

end;