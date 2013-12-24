package x.y;

module N is

end;

module M is

  import x.y.N;

  value x = new N.nonexistent (23);

end;