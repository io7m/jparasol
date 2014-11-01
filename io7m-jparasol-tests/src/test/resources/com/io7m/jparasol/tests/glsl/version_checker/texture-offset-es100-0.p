package x.y;

module M is

  function f (x : float) : vector_4f =
    external f is
      restrict 100 es;
      vertex   true;
      fragment true;
    end;
  
  shader fragment f is
    out out_0 : vector_4f as 0;
  with
    value x = f (23.0);
  as
    out out_0 = x;
  end;

  shader vertex v is
    out vertex out_0 : vector_4f;
  with
    value x = f (23.0);
  as
    out out_0 = x;
  end;

end;