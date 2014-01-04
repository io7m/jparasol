package x.y;

module N is

  value x : vector_4f =
    external xyz is
      vertex true;
      fragment false;
    end;

end;

module M is
  import x.y.N;

  shader fragment frag is
    out out_0 : vector_4f as 0;
  as
    out out_0 = N.x;
  end;

end;