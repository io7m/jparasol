package x.y;

module M is

  value x =
    let
      value y = 23.0;
      value z = 24.0;
      value w = 25.0;
    in
      let
        value q = new vector_4f (y, z, w, y);
      in
        q
      end
    end;

  shader fragment f is
    in  in_0      : vector_4f;
    parameter p_0 : vector_4f;
    out out_0     : vector_4f as 0;
  with
    discard (true);
  as
    out out_0 = x;
  end;

end;