package x.y;

module M is
  value z =
    match false with
      case false: 2
      case false: 1
    end;
end;