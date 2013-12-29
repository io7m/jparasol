package x.y;

module M is

  value v2f1 = new vector_2f (0.0, 1.0) [x];
  value v2f2 = new vector_2f (0.0, 1.0) [x y];
  value v2f3 = new vector_2f (0.0, 1.0) [x y x];
  value v2f4 = new vector_2f (0.0, 1.0) [x y x y];
  
  value v3f1 = new vector_3f (0.0, 1.0, 2.0) [x];
  value v3f2 = new vector_3f (0.0, 1.0, 2.0) [x y];
  value v3f3 = new vector_3f (0.0, 1.0, 2.0) [x y z];
  value v3f4 = new vector_3f (0.0, 1.0, 2.0) [x y z y];

  value v4f1 = new vector_4f (0.0, 1.0, 2.0, 3.0) [x];
  value v4f2 = new vector_4f (0.0, 1.0, 2.0, 3.0) [x y];
  value v4f3 = new vector_4f (0.0, 1.0, 2.0, 3.0) [x y z];
  value v4f4 = new vector_4f (0.0, 1.0, 2.0, 3.0) [x y z w];

end;