package x.y;

module N is

end;

module M is

  import x.y.N;

  value x =
    let value k : N.nonexistent = 23; in
      k
    end;

end;