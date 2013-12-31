package x.y;

module M is

  function f (x : integer) : vector_4f =
    external xyz is
      vertex false;
      fragment true;
    end;

  value x = f (23);

  shader fragment frag is
    out out_0 : vector_4f as 0;
  as
    out out_0 = x;
  end;

end;