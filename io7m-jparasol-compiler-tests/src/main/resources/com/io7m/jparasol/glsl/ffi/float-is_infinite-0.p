package x.y;

module M is

  import com.io7m.parasol.Float as F;

  value x =
    F.is_infinite (-1.0);

  value y =
    if x then
      new vector_4f (0.0, 1.0, 2.0, 3.0)
    else
      new vector_4f (0.0, 1.0, 2.0, 3.0)
    end;

  shader vertex v is
    out vertex dummy : vector_4f;
  as
    out dummy = y;
  end;

  shader fragment f is
    in dummy : vector_4f;
    out out0 : vector_4f as 0;
  as
    out out0 = y;
  end;

  shader program p is
    vertex v;
    fragment f;
  end;

end;

