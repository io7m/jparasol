package x.y;

module M is

  shader program p is
    vertex q;
    fragment q;
  end;

  shader program q is
    vertex p;
    fragment p;
  end;

end;