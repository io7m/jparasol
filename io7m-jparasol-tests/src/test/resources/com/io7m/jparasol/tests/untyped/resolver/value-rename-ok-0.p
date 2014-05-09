package x.y;

module N is
  value x = 23;
end;

module M is
  import x.y.N as R;
  value x = R.x;
end;