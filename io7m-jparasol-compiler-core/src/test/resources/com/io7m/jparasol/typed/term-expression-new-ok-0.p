package x.y;

module M is

  value z0   = new integer (23);
  value z1   = new integer (23.0);

  value r0   = new float (23);
  value r1   = new float (23.0);

  value b0   = new boolean (true);

  value v2f0 = new vector_2f (0.0, 1.0);
  value v2f1 = new vector_2f (v2f0);

  value v3f0 = new vector_3f (0.0, 1.0, 2.0);
  value v3f1 = new vector_3f (v2f0, 2.0);
  value v3f2 = new vector_3f (0.0, v2f0);
  value v3f3 = new vector_3f (v3f0);

  value v4f0 = new vector_4f (0.0, 1.0, 2.0, 3.0);
  value v4f1 = new vector_4f (v2f0, 2.0, 3.0);
  value v4f2 = new vector_4f (0.0, v2f0, 3.0);
  value v4f3 = new vector_4f (0.0, 1.0, v2f0);
  value v4f4 = new vector_4f (v2f0, v2f0);
  value v4f5 = new vector_4f (v3f0, 3.0);
  value v4f6 = new vector_4f (0.0, v3f0);
  value v4f7 = new vector_4f (v4f0);

end;