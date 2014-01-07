package x.y;

module M is

  import com.io7m.parasol.Vector4f          as V4;

  shader vertex v is
    out vertex f_position_clip : vector_4f;
  with
    value clip_position =
      V4.interpolate (
        new vector_4f (0.0, 0.0, 0.0, 0.0),
        new vector_4f (1.0, 1.0, 1.0, 1.0),
        0.5);
  as
    out f_position_clip = clip_position;
  end;

  shader fragment f is
    in f_position_clip : vector_4f;
    out out_0          : vector_4f as 0;
  with
    value rgba =
      V4.interpolate (
        new vector_4f (0.0, 0.0, 0.0, 0.0),
        new vector_4f (1.0, 1.0, 1.0, 1.0),
        0.5);
  as
    out out_0 = rgba;
  end;

  shader program p is
    vertex   v;
    fragment f;
  end;

end;