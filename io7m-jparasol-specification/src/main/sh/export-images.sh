#!/bin/sh -e

DPI="140"
OUTPUT="src/main/png"

TYPING_RULES="
conditional
false_constant
float_constant
function_application
function_declaration
integer_constant
let
matrix_new
new_scalar
record_expression
record_projection
true_constant
value_declaration
value_declaration_ascribed
variable
vector_new
vector_swizzle
vector_swizzle_single
shader_vertex_inputs_parameters
shader_vertex_values
shader_vertex_output
shader_fragment_inputs_parameters
shader_fragment_values
shader_fragment_discard
shader_fragment_output
"

for r in ${TYPING_RULES}
do
  echo "type rule: ${r}"
  inkscape \
    --export-id="${r}" \
    --export-id-only \
    --export-dpi=${DPI} \
    --export-background="0xffffffff" \
    --export-png="${OUTPUT}/ty_${r}.png" \
    src/main/svg/typing.svg
done

inkscape \
  --export-dpi=${DPI} \
  --export-background="0xffffffff" \
  --export-png="${OUTPUT}/typing.png" \
  src/main/svg/typing.svg

TYPING_EXAMPLES="
typing_example
typing_example_derivation
"

for r in ${TYPING_EXAMPLES}
do
  echo "type example: ${r}"

  inkscape \
    --export-id="${r}" \
    --export-id-only \
    --export-dpi=${DPI} \
    --export-background="0xffffffff" \
    --export-png="${OUTPUT}/tyex_${r}.png" \
    src/main/svg/typing_example.svg
done

SEMANTICS_RULES="
conditional
expressions
function_application
let
new
record_expression
record_projection
swizzle
top_level
values
shader_vertex_values
shader_fragment_values
"

for r in ${SEMANTICS_RULES}
do
  echo "semantics: ${r}"

  inkscape \
    --export-id="${r}" \
    --export-id-only \
    --export-dpi=${DPI} \
    --export-background="0xffffffff" \
    --export-png="${OUTPUT}/op_${r}.png" \
    src/main/svg/semantics.svg
done

inkscape \
  --export-dpi=${DPI} \
  --export-background="0xffffffff" \
  --export-png="${OUTPUT}/op_semantics.png" \
  src/main/svg/semantics.svg

inkscape \
  --export-dpi=${DPI} \
  --export-background="0xffffffff" \
  --export-png="${OUTPUT}/semantics_example.png" \
  src/main/svg/semantics_example.svg

inkscape \
  --export-dpi=${DPI} \
  --export-background="0xffffffff" \
  --export-png="${OUTPUT}/semantics_example2.png" \
  src/main/svg/semantics_example2.svg

inkscape \
  --export-dpi=${DPI} \
  --export-background="0xffffffff" \
  --export-png="${OUTPUT}/ex_matrix_swizzle_example.png" \
  src/main/svg/matrix_swizzle.svg

