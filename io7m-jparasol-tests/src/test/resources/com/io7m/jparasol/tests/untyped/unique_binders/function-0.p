package x.y;

module M is

  function f (x : integer) : integer =
    let value f = x; in
      f
    end;

end;