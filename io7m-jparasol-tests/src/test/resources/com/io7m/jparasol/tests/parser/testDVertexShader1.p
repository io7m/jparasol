vertex v is
  parameter x        : integer;
  in        mmv      : matrix_4x4f;
  in        in_pos   : vector_4f;
  out       out_pos  : vector_4f;
  out       out_pos2 : vector_4f;
as
  out out_pos = in_pos;
  out out_pos2 = in_pos;
end