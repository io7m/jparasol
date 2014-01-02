package x.y;

module M is
  
  function f (x : integer) : integer =
    external unknown_nonexistent_not_here is
      vertex true;
      fragment true;
    end;
  
end;