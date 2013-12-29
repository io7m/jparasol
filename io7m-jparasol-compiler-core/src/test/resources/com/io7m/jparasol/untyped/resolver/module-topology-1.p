package x.y;

module M is

end;

module N is
  import x.y.M;
end;

module P is
  import x.y.M;
end;

module Q is
  import x.y.P;
end;