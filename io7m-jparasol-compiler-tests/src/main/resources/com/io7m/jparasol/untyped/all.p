package x.y;

module M is

  type t is record
    x : integer,
    y : integer
  end;

  value x =
    if true then
      false
    else
      true
    end;
    
  value y =
    let value k = 23; in
      k
    end;

  value a : integer = 23.0;

  value z =
    new vector_3f (0.0, 1.0, 0.0) [x y z];

  value w = record t {
    x = 23,
    y = 23,
    z = 23
  };

  function f (
    a : integer,
    b : integer
  ) : integer =
    let 
      value k = plus (a, 23);
      value m = plus (b, 23);
    in
      if true then
        k
      else
        m
      end
    end;

  function g (
    a : integer,
    b : integer
  ) : integer =
    external xyz is
      vertex true;
      fragment true;
    end;

  shader vertex v is
    out vertex out0 : vector_4f;
  as
    out out0 = z;
  end;
  
  shader fragment f is
    out out0 : vector_4f as 0;
  with
    discard (w [x]);
  as
    out out0 = z;
  end;
  
  shader program p is
    vertex v;
    fragment f;
  end;

end;
