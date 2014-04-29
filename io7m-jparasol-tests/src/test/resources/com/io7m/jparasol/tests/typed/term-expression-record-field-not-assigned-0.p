package x.y;

module M is

  type t is record
    z : integer,
    x : boolean
  end;

  value x = record t {
    z = 23
  };

end;