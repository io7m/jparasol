package x.y;

module Q is
  value x = new vector_4f (0.0, 1.0, 2.0, 3.0);
end;

module M is
  import x.y.Q;
  
  shader vertex v is
    out vertex xyz : vector_4f;
  as
    out xyz = Q.x;
  end;
end;

module N is
  import x.y.Q;

  shader fragment f is
    in xyz    : vector_4f;
    out out_0 : vector_4f as 0;
  as
    out out_0 = xyz;
  end;
end;

module O is
  import x.y.M;
  import x.y.N;

  shader program p is
    vertex M.v;
    fragment N.f;
  end;
end;
