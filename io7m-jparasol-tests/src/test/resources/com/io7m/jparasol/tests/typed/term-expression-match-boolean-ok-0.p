package x.y;

module M is
  value z =
    match false with
      case false: 0
      case true: 1
    end;
end;