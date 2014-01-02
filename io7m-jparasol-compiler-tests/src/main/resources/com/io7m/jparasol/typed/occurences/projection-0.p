package x.y;

module M is

  type t is record
    q : float
  end;

  function f (
    x : float
  ) : float =
    record t { q = x }.q;

end;