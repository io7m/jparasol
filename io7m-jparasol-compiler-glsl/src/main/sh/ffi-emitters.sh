#!/bin/sh

for f in `cat ffi.txt`
do
  cat <<EOF
  private static @Nonnull GFFIExpression $f(
    final @Nonnull TASTDFunctionExternal f,
    final @Nonnull List<GASTExpression> arguments,
    final @Nonnull GVersion version)
  {
    // TODO: XXX
    throw new UnimplementedCodeException();
  }
EOF
done

