package x.y;

module M is

  type t is record
    x : u
  end;

  type u is record
    x : t
  end;

end;