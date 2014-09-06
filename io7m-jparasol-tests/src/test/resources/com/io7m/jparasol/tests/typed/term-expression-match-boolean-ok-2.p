package x.y;

module M is
  value z =
    match false with
      case true: 1
      default: 0
    end;
end;