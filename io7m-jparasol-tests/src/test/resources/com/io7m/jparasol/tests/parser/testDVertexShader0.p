vertex v is
  parameter x        : integer;
  in        mmv      : matrix_4x4f;
  in        in_pos   : vector_4f;
  out       out_pos  : vector_4f;
  out       out_pos2 : vector_4f;
with
  value pp = M.multV4F (mmv, in_pos);
as
  out out_pos = pp;
  out out_pos2 = pp;
end