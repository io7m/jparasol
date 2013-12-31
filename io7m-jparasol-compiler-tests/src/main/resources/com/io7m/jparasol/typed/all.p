package x.y;

module M is

  type t is record
    x : integer,
    y : integer
  end;

  type q is record
    t : t
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

  value a : integer = 23;

  value z =
    new vector_3f (0.0, 1.0, 0.0) [x y z];

  value z4 =
    z [x y z x];

  value w = record t {
    x = 23,
    y = 23
  };
  
  value wq = record q {
    t = record t {
      x = 23,
      y = 23
    }
  };

  function plus (
    a : integer,
    b : integer
  ) : integer =
    a;

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
    out out0 = z4;
  end;
  
  shader fragment f is
    out out0 : vector_4f as 0;
  with
    discard (false);
  as
    out out0 = z4;
  end;
  
  shader program p is
    vertex v;
    fragment f;
  end;

end;
