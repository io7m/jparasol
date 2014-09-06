package x.y;

module M is

  import com.io7m.parasol.Matrix4x4f        as M4;

  shader vertex switch_demo_v is
    in a_f         : float;
    parameter u_m4 : matrix_4x4f;
    out vertex f_position_clip : vector_4f;
  with
    value k = new vector_4f (
      a_f, a_f, a_f, a_f
    );

    value clip_position =
      M4.multiply_vector (u_m4, k);
  as
    out f_position_clip = clip_position;
  end;

  function get (x : integer) : float =
    match x with
      case 0: 0.0
      case 1: 1.0
      case 2: 2.0
      case 3: 3.0
      default: 4.0
    end;

  shader fragment switch_demo_f is
    out out_0 : vector_4f as 0;
  with
    value rgba = new vector_4f (get(0), get(1), get(2), get(3));
  as
    out out_0 = rgba;
  end;

  shader program switch_demo is
    vertex   switch_demo_v;
    fragment switch_demo_f;
  end;

end;