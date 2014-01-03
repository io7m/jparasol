package x.y;

module M is
  shader vertex v is
    out vertex out_0 : vector_4f;
  with
    value x =
      new vector_4f (0.0, 1.0, 2.0, 3.0);
  as
    out out_0 = x;
  end;
  
  shader fragment f is
    out out_0 : vector_4f as 0;
  with
    value x =
      new vector_4f (0.0, 1.0, 2.0, 3.0);
  as
    out out_0 = x;
  end;
  
  shader program p is
    vertex v;
    fragment f;
  end;
end;

module N is
  shader vertex v is
    out vertex out_0 : vector_4f;
  with
    value x =
      new vector_4f (0.0, 1.0, 2.0, 3.0);
  as
    out out_0 = x;
  end;
  
  shader fragment f is
    out out_0 : vector_4f as 0;
  with
    value x =
      new vector_4f (0.0, 1.0, 2.0, 3.0);
  as
    out out_0 = x;
  end;
  
  shader program p is
    vertex v;
    fragment f;
  end;
end;

module P is
  shader vertex v is
    out vertex out_0 : vector_4f;
  with
    value x =
      new vector_4f (0.0, 1.0, 2.0, 3.0);
  as
    out out_0 = x;
  end;
  
  shader fragment f is
    out out_0 : vector_4f as 0;
  with
    value x =
      new vector_4f (0.0, 1.0, 2.0, 3.0);
  as
    out out_0 = x;
  end;
  
  shader program p is
    vertex v;
    fragment f;
  end;
end;
