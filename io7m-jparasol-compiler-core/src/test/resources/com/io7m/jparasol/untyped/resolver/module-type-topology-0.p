package x.y;

module M is
  type x is record
    x : integer
  end;

  type y is record
    x : x
  end;
  
  type z is record
    x : y
  end;
end;
