package x.y;

module M is

  type t is record
    z : integer,
    b : boolean,
    r : float 
  end;

  value x = record t {
    z = 23,
    b = true,
    r = 23.0
  };

end;