package x.y;

module N is

  function f (x : integer) : vector_4f =
    external xyz is
      vertex true;
      fragment false;
    end;

end;

module M is
  import x.y.N;

  value x = N.f (23);

  shader fragment frag is
    out out_0 : vector_4f as 0;
  as
    out out_0 = x;
  end;

end;