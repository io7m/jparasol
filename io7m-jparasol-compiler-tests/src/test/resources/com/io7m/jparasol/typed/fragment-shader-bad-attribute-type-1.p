package x.y;

module M is

  type t is record
    x : vector_4f
  end;

  shader fragment f is
    in  in_0  : vector_4f;
    out out_0 : t as 0;
  with
    value result = record t {
      x = new vector_4f (0.0, 1.0, 2.0, 3.0) 
    };
  as
    out out_0 = result;
  end;

end;