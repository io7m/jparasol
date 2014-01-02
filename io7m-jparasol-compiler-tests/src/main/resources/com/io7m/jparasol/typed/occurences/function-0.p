package x.y;

module M is

  value y = 23.0;
  
  value z = 24.0;

  function f (
    x : float
  ) : vector_3f =
    new vector_3f (x, y, z);

end;