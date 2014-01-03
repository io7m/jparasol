package x.y;

module M is

  import com.io7m.parasol.Sampler2D   as S2;
  import com.io7m.parasol.SamplerCube as SC;

  type t is record
    v4f  : vector_4f,
    t2   : vector_4f,
    tc   : vector_4f,
    t2p3 : vector_4f,
    t2p4 : vector_4f
  end;

  function make (
    t2 : sampler_2d,
    tc : sampler_cube,
    x  : float
  ) : t =
    record t {
      v4f  = new vector_4f (x, x, x, x),
      t2   = S2.texture (t2, new vector_2f (x, x)),
      tc   = SC.texture (tc, new vector_3f (x, x, x)),
      t2p3 = S2.texture_projective_3f (t2, new vector_3f (x, x, x)),
      t2p4 = S2.texture_projective_4f (t2, new vector_4f (x, x, x, x))
    };

  shader vertex v is
    parameter s2     : sampler_2d;
    parameter sc     : sampler_cube;
    out vertex dummy : vector_4f;
  with
    value result = make (s2, sc, 1.0).v4f;
  as
    out dummy       = result;
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

