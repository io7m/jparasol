package x.y;

module M is

  import com.io7m.parasol.Float as F;

  value x =
    F.sign (-1.0);

  shader vertex v is
    out vertex dummy : vector_4f;
  with
    value result = new vector_4f (x, x, x, x);
  as
    out dummy = result;
  end;

  shader fragment f is
    in dummy : vector_4f;
    out out0 : vector_4f as 0;
  with
    value result = new vector_4f (x, x, x, x);
  as
    out out0 = result;
  end;

  shader program p is
    vertex v;
    fragment f;
  end;

end;

