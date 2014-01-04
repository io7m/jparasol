package x.y;

module M is

  value x : vector_4f =
    external xyz is
      vertex true;
      fragment false;
    end;

  shader fragment frag is
    out out_0 : vector_4f as 0;
  as
    out out_0 = x;
  end;

end;