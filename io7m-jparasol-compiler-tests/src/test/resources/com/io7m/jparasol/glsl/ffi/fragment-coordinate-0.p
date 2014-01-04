package x.y;

module M is

  import com.io7m.parasol.Fragment as F;

  shader vertex v is
    out vertex dummy : vector_4f;
  with
    value result = new vector_4f (0.0, 1.0, 2.0, 3.0);
  as
    out dummy = result;
  end;

  shader fragment f is
    out out0 : vector_4f as 0;
  as
    out out0 = F.coordinate;
  end;

  shader program p is
    vertex v;
    fragment f;
  end;

end;

