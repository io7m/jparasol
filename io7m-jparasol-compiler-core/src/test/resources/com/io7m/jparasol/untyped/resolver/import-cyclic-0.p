package x.y;

module M is
  import x.y.N;
end;

module N is
  import x.y.M;
end;
