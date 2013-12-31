package x.y;

module N is

end;

module M is

  import x.y.N;

  value x = record N.nonexistent { x = 23 };

end;