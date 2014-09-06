package x.y;

module M is
  value z =
    match 23 with
      case 1: 1
      default: 0
    end;
end;