package x.y;

module M is

  value v2i1 = new vector_2i (0, 1) [x];
  value v2i2 = new vector_2i (0, 1) [x y];
  value v2i3 = new vector_2i (0, 1) [x y x];
  value v2i4 = new vector_2i (0, 1) [x y x y];
  
  value v3i1 = new vector_3i (0, 1, 2) [x];
  value v3i2 = new vector_3i (0, 1, 2) [x y];
  value v3i3 = new vector_3i (0, 1, 2) [x y z];
  value v3i4 = new vector_3i (0, 1, 2) [x y z y];

  value v4i1 = new vector_4i (0, 1, 2, 3) [x];
  value v4i2 = new vector_4i (0, 1, 2, 3) [x y];
  value v4i3 = new vector_4i (0, 1, 2, 3) [x y z];
  value v4i4 = new vector_4i (0, 1, 2, 3) [x y z w];

end;