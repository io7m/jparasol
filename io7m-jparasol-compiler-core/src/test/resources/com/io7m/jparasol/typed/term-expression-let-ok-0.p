package x.y;

module M is

  function f (
    x : float,
    y : float) 
  : float = x;

  value z =
    let
      value a = f (23.0, 42.0);
      value b = f (a, 23.0);
      value c = f (b, 42.0);
    in
      new vector_3f (a, b, c)
    end;

end;