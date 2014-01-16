package x.y;

module M is

  import com.io7m.parasol.Float as F;

  function f (x : float) : float =
    let value max = 23.0; in
      F.maximum (x, max)
    end;

  shader vertex v is
    out vertex out_0 : vector_4f;
    parameter r      : float;
  with
    value z = new vector_4f (f (23.0), f (23.0), f (23.0), f (23.0));
  as
    out out_0 = z;
  end;

end;