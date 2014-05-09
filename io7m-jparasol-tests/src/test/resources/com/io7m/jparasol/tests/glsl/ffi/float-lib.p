package x.y;

module M is

  import com.io7m.parasol.Float as F;

  type t is record
    v4f                : vector_4f,
    f_absolute         : float,
    f_add              : float,
    f_arc_cosine       : float,
    f_arc_sine         : float,
    f_arc_tangent      : float,
    f_ceiling          : float,
    f_clamp            : float,
    f_cosine           : float,
    f_divide           : float,
    f_floor            : float,
    f_greater          : boolean,
    f_greater_or_equal : boolean,
    f_interpolate      : float,
    f_is_infinite      : boolean,
    f_is_nan           : boolean,
    f_lesser           : boolean,
    f_lesser_or_equal  : boolean,
    f_maximum          : float,
    f_minimum          : float,
    f_modulo           : float,
    f_multiply         : float,
    f_power            : float,
    f_round            : float,
    f_sign             : float,
    f_sine             : float,
    f_square_root      : float,
    f_subtract         : float,
    f_tangent          : float,
    f_truncate         : float
  end;

  function make (x : float) : t =
    record t {
      v4f                = new vector_4f (x, x, x, x),
      f_absolute         = F.absolute (x),
      f_add              = F.add (x, x),
      f_arc_cosine       = F.arc_cosine (x),
      f_arc_sine         = F.arc_sine (x),
      f_arc_tangent      = F.arc_tangent (x),
      f_ceiling          = F.ceiling (x),
      f_clamp            = F.clamp (x, 0.0, 1.0),
      f_cosine           = F.cosine (x),
      f_divide           = F.divide (x, 1.0),
      f_floor            = F.floor (x),
      f_greater          = F.greater (1.0, 0.0),
      f_greater_or_equal = F.greater_or_equal (1.0, 0.0),
      f_interpolate      = F.interpolate (0.0, x, 1.0),
      f_is_infinite      = F.is_infinite (x),
      f_is_nan           = F.is_nan (x),
      f_lesser           = F.lesser (0.0, 1.0),
      f_lesser_or_equal  = F.lesser_or_equal (0.0, 1.0),
      f_maximum          = F.maximum (x, 0.0),
      f_minimum          = F.minimum (x, 0.0),
      f_modulo           = F.modulo (x, 1.0),
      f_multiply         = F.multiply (x, 1.0),
      f_power            = F.power (x, 2.0),
      f_round            = F.round (x),
      f_sign             = F.sign (x),
      f_sine             = F.sine (x),
      f_square_root      = F.square_root (x),
      f_subtract         = F.subtract (x, x),
      f_tangent          = F.tangent (x),
      f_truncate         = F.truncate (x)
    };

  shader vertex v is
    out vertex dummy : vector_4f;
  with
    value result = make (1.0).v4f;
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

