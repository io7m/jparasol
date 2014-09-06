package x.y;

module M is
  value z =
    match true with
      case false: 23
      case true: false
    end;
end;