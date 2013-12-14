shader fragment f is
  out out_pos : vector_4f as 0;
as
  out out_pos = in_pos;
end;
      
shader vertex v is
  out out_pos : vector_4f;
as
  out out_pos = in_pos;
end;
      
shader program p is
  vertex v;
  fragment f;
end;