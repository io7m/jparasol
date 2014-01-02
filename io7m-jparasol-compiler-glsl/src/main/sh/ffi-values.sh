#!/bin/sh

for f in `cat ffi.txt`
do
  cat <<EOF
  EXPRESSION_EMITTERS.put("$f", new GFFIExpressionEmitter() {
    @Override public @Nonnull GFFIExpression emitExpression(
      final @Nonnull TASTDFunctionExternal f,
      final @Nonnull List<GASTExpression> arguments,
      final @Nonnull GVersion version)
        throws ConstraintError
    {
      return GFFIExpressionEmitters.$f(f,arguments,version);
    }
  });
EOF
done

