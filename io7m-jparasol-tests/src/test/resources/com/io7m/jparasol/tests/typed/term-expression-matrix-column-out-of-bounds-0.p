package x.y;

module M is

  shader vertex v is
    parameter m : matrix_4x4f;
    out vertex vp : vector_4f;
  with
    value q = column m 4;
  as
    out vp = q;
  end;

end;