package x.y;

module M is

  function k (
    c : integer
  ) : integer =
    c;

  function i (
    z : integer
  ) : integer =
    let
      value c = k (23);
    in
      c
    end;

end;