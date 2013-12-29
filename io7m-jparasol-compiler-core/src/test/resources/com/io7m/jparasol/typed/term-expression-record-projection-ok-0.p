package x.y;

module M is

  type t is record
    r : float
  end;

  value x = record t { r = 23.0 }.r;

end;