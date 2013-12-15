package x.y;

module M is

  value x = record t {
    y = let value x__ = 23; in x__ end
  };

end;
