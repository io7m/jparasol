module M is
  
  import K as K0;
  import Q as Q0;
  
  type t is record
    x : integer
  end;

  function f (x : integer) : integer = x;
  
  value z : integer = 23;

  shader fragment fs is
    out out_pos : vector_4f as 0;
  as
    out out_pos = in_pos;
  end;
      
  shader vertex vs is
    out out_pos : vector_4f;
  as
    out out_pos = in_pos;
  end;
      
  shader program ps is
    vertex v;
    fragment f;
  end;

end
