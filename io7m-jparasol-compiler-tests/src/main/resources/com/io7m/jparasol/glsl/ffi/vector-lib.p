package x.y;

module M is

  import com.io7m.parasol.Vector4f as V4F;
  import com.io7m.parasol.Vector3f as V3F;
  import com.io7m.parasol.Vector2f as V2F;
  import com.io7m.parasol.Vector4i as V4I;
  import com.io7m.parasol.Vector3i as V3I;
  import com.io7m.parasol.Vector2i as V2I;

  type t is record
    v4f : vector_4f,

    v4f_add             : vector_4f,
    v4f_dot             : float,
    v4f_subtract        : vector_4f,
    v4f_multiply        : vector_4f,
    v4f_magnitude       : float,
    v4f_interpolate     : vector_4f,
    v4f_normalize       : vector_4f,
    v4f_add_scalar      : vector_4f,
    v4f_multiply_scalar : vector_4f,
    v4f_negate          : vector_4f,
    v4f_reflect         : vector_4f,
    v4f_refract         : vector_4f,

    v3f_add             : vector_3f,
    v3f_dot             : float,
    v3f_subtract        : vector_3f,
    v3f_multiply        : vector_3f,
    v3f_magnitude       : float,
    v3f_interpolate     : vector_3f,
    v3f_normalize       : vector_3f,
    v3f_cross           : vector_3f,
    v3f_add_scalar      : vector_3f,
    v3f_multiply_scalar : vector_3f,
    v3f_negate          : vector_3f,
    v3f_reflect         : vector_3f,
    v3f_refract         : vector_3f,

    v2f_add             : vector_2f,
    v2f_dot             : float,
    v2f_subtract        : vector_2f,
    v2f_multiply        : vector_2f,
    v2f_magnitude       : float,
    v2f_interpolate     : vector_2f,
    v2f_normalize       : vector_2f,
    v2f_add_scalar      : vector_2f,
    v2f_multiply_scalar : vector_2f,
    v2f_negate          : vector_2f,
    v2f_reflect         : vector_2f,
    v2f_refract         : vector_2f,

    v4i_add             : vector_4i,
    v4i_dot             : float,
    v4i_subtract        : vector_4i,
    v4i_multiply        : vector_4i,
    v4i_magnitude       : float,
    v4i_interpolate     : vector_4i,
    v4i_normalize       : vector_4i,
    v4i_add_scalar      : vector_4i,
    v4i_multiply_scalar : vector_4i,
    v4i_negate          : vector_4i,
    v4i_reflect         : vector_4i,
    v4i_refract         : vector_4i,

    v3i_add             : vector_3i,
    v3i_dot             : float,
    v3i_subtract        : vector_3i,
    v3i_multiply        : vector_3i,
    v3i_magnitude       : float,
    v3i_interpolate     : vector_3i,
    v3i_normalize       : vector_3i,
    v3i_add_scalar      : vector_3i,
    v3i_multiply_scalar : vector_3i,
    v3i_negate          : vector_3i,
    v3i_reflect         : vector_3i,
    v3i_refract         : vector_3i,

    v2i_add             : vector_2i,
    v2i_dot             : float,
    v2i_subtract        : vector_2i,
    v2i_multiply        : vector_2i,
    v2i_interpolate     : vector_2i,
    v2i_magnitude       : float,
    v2i_normalize       : vector_2i,
    v2i_add_scalar      : vector_2i,
    v2i_multiply_scalar : vector_2i,
    v2i_negate          : vector_2i,
    v2i_reflect         : vector_2i,
    v2i_refract         : vector_2i
  end;

  function make (x : float, n : integer) : t =
    record t {
      v4f = new vector_4f (x, x, x, x),

      v4f_add             = V4F.add (new vector_4f (x, x, x, x), new vector_4f (x, x, x, x)),
      v4f_dot             = V4F.dot (new vector_4f (x, x, x, x), new vector_4f (x, x, x, x)),
      v4f_multiply        = V4F.multiply (new vector_4f (x, x, x, x), new vector_4f (x, x, x, x)),
      v4f_subtract        = V4F.subtract (new vector_4f (x, x, x, x), new vector_4f (x, x, x, x)),
      v4f_magnitude       = V4F.magnitude (new vector_4f (x, x, x, x)),
      v4f_interpolate     = V4F.interpolate (new vector_4f (x, x, x, x), new vector_4f (x, x, x, x), 1.0),
      v4f_normalize       = V4F.normalize (new vector_4f (x, x, x, x)),
      v4f_add_scalar      = V4F.add_scalar (new vector_4f (x, x, x, x), x),
      v4f_multiply_scalar = V4F.add_scalar (new vector_4f (x, x, x, x), x),
      v4f_negate          = V4F.negate (new vector_4f (x, x, x, x)),
      v4f_reflect         = V4F.reflect (new vector_4f (x, x, x, x), new vector_4f (x, x, x, x)),
      v4f_refract         = V4F.refract (new vector_4f (x, x, x, x), new vector_4f (x, x, x, x), 0.3),

      v3f_add             = V3F.add (new vector_3f (x, x, x), new vector_3f (x, x, x)),
      v3f_dot             = V3F.dot (new vector_3f (x, x, x), new vector_3f (x, x, x)),
      v3f_multiply        = V3F.multiply (new vector_3f (x, x, x), new vector_3f (x, x, x)),
      v3f_subtract        = V3F.subtract (new vector_3f (x, x, x), new vector_3f (x, x, x)),
      v3f_magnitude       = V3F.magnitude (new vector_3f (x, x, x)),
      v3f_interpolate     = V3F.interpolate (new vector_3f (x, x, x), new vector_3f (x, x, x), 1.0),
      v3f_normalize       = V3F.normalize (new vector_3f (x, x, x)),
      v3f_cross           = V3F.cross (new vector_3f (x, x, x), new vector_3f (x, x, x)),
      v3f_add_scalar      = V3F.add_scalar (new vector_3f (x, x, x), x),
      v3f_multiply_scalar = V3F.add_scalar (new vector_3f (x, x, x), x),
      v3f_negate          = V3F.negate (new vector_3f (x, x, x)),
      v3f_reflect         = V3F.reflect (new vector_3f (x, x, x), new vector_3f (x, x, x)),
      v3f_refract         = V3F.refract (new vector_3f (x, x, x), new vector_3f (x, x, x), 0.3),

      v2f_add             = V2F.add (new vector_2f (x, x), new vector_2f (x, x)),
      v2f_dot             = V2F.dot (new vector_2f (x, x), new vector_2f (x, x)),
      v2f_multiply        = V2F.multiply (new vector_2f (x, x), new vector_2f (x, x)),
      v2f_subtract        = V2F.subtract (new vector_2f (x, x), new vector_2f (x, x)),
      v2f_magnitude       = V2F.magnitude (new vector_2f (x, x)),
      v2f_interpolate     = V2F.interpolate (new vector_2f (x, x), new vector_2f (x, x), 1.0),
      v2f_normalize       = V2F.normalize (new vector_2f (x, x)),
      v2f_add_scalar      = V2F.add_scalar (new vector_2f (x, x), x),
      v2f_multiply_scalar = V2F.add_scalar (new vector_2f (x, x), x),
      v2f_negate          = V2F.negate (new vector_2f (x, x)),
      v2f_reflect         = V2F.reflect (new vector_2f (x, x), new vector_2f (x, x)),
      v2f_refract         = V2F.refract (new vector_2f (x, x), new vector_2f (x, x), 0.3),

      v4i_add             = V4I.add (new vector_4i (n, n, n, n), new vector_4i (n, n, n, n)),
      v4i_dot             = V4I.dot (new vector_4i (n, n, n, n), new vector_4i (n, n, n, n)),
      v4i_multiply        = V4I.multiply (new vector_4i (n, n, n, n), new vector_4i (n, n, n, n)),
      v4i_subtract        = V4I.subtract (new vector_4i (n, n, n, n), new vector_4i (n, n, n, n)),
      v4i_magnitude       = V4I.magnitude (new vector_4i (n, n, n, n)),
      v4i_interpolate     = V4I.interpolate (new vector_4i (n, n, n, n), new vector_4i (n, n, n, n), 1.0),
      v4i_normalize       = V4I.normalize (new vector_4i (n, n, n, n)),
      v4i_add_scalar      = V4I.add_scalar (new vector_4i (n, n, n, n), n),
      v4i_multiply_scalar = V4I.add_scalar (new vector_4i (n, n, n, n), n),
      v4i_negate          = V4I.negate (new vector_4i (n, n, n, n)),
      v4i_reflect         = V4I.reflect (new vector_4i (n, n, n, n), new vector_4i (n, n, n, n)),
      v4i_refract         = V4I.refract (new vector_4i (n, n, n, n), new vector_4i (n, n, n, n), 0.3),

      v3i_add             = V3I.add (new vector_3i (n, n, n), new vector_3i (n, n, n)),
      v3i_dot             = V3I.dot (new vector_3i (n, n, n), new vector_3i (n, n, n)),
      v3i_multiply        = V3I.multiply (new vector_3i (n, n, n), new vector_3i (n, n, n)),
      v3i_subtract        = V3I.subtract (new vector_3i (n, n, n), new vector_3i (n, n, n)),
      v3i_magnitude       = V3I.magnitude (new vector_3i (n, n, n)),
      v3i_interpolate     = V3I.interpolate (new vector_3i (n, n, n), new vector_3i (n, n, n), 1.0),
      v3i_normalize       = V3I.normalize (new vector_3i (n, n, n)),
      v3i_add_scalar      = V3I.add_scalar (new vector_3i (n, n, n), n),
      v3i_multiply_scalar = V3I.add_scalar (new vector_3i (n, n, n), n),
      v3i_negate          = V3I.negate (new vector_3i (n, n, n)),
      v3i_reflect         = V3I.reflect (new vector_3i (n, n, n), new vector_3i (n, n, n)),
      v3i_refract         = V3I.refract (new vector_3i (n, n, n), new vector_3i (n, n, n), 0.3),

      v2i_add             = V2I.add (new vector_2i (n, n), new vector_2i (n, n)),
      v2i_dot             = V2I.dot (new vector_2i (n, n), new vector_2i (n, n)),
      v2i_multiply        = V2I.multiply (new vector_2i (n, n), new vector_2i (n, n)),
      v2i_subtract        = V2I.subtract (new vector_2i (n, n), new vector_2i (n, n)),
      v2i_magnitude       = V2I.magnitude (new vector_2i (n, n)),
      v2i_interpolate     = V2I.interpolate (new vector_2i (n, n), new vector_2i (n, n), 1.0),
      v2i_normalize       = V2I.normalize (new vector_2i (n, n)),
      v2i_add_scalar      = V2I.add_scalar (new vector_2i (n, n), n),
      v2i_multiply_scalar = V2I.add_scalar (new vector_2i (n, n), n),
      v2i_negate          = V2I.negate (new vector_2i (n, n)),
      v2i_reflect         = V2I.reflect (new vector_2i (n, n), new vector_2i (n, n)),
      v2i_refract         = V2I.refract (new vector_2i (n, n), new vector_2i (n, n), 0.3)
    };

  shader vertex v is
    out vertex dummy : vector_4f;
  with
    value result = make (1.0, 1).v4f;
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

