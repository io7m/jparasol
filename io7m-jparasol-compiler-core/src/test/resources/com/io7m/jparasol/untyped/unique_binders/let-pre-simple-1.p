package x.y;

module M is

  value y =
    let value y = 23; in
      let value y = y; in
        y
      end
    end;

end;