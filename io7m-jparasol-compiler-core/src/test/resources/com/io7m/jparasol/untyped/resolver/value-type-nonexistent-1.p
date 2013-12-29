package x.y;

module N is

end;

module M is

  import x.y.N;

  value x : N.nonexistent = 23;

end;