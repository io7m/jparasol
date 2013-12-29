package x.y;

module N is

  type u is record
    x : integer
  end;

end;

module M is

  import x.y.N;

  type t is record
    x : N.u
  end;

end;