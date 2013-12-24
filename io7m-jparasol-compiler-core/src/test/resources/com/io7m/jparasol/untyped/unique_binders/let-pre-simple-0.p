package x.y;

module M is

  value x = 23;

  value y =
    let
      value y = x;
    in
      y
    end;

end;