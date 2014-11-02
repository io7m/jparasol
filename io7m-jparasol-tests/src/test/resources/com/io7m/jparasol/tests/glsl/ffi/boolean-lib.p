package x.y;

module M is

  import com.io7m.parasol.Boolean as B;

  type t is record
    v4f : vector_4f,
    or  : boolean
  end;

  function make (x : boolean) : t =
    record t {
      v4f = new vector_4f (new float (x), new float (x), new float (x), new float (x)),
      or  = B.or (x, x)
    };

  shader vertex v is
    out vertex dummy : vector_4f;
  with
    value result = make (true).v4f;
  as
    out dummy = result;
  end;

  shader fragment f is
    in dummy : vector_4f;
    out out0 : vector_4f as 0;
  as
    out out0 = dummy;
  end;

  shader program p is
    vertex v;
    fragment f;
  end;

end;

