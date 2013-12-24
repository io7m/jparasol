package x.y;

module M is

  value x =
    let value k : nonexistent = 23; in
      k
    end;

end;