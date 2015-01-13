#!/bin/sh

exec protoc \
  --java_out=src/main/java \
  -Isrc/main/protobuf \
  src/main/protobuf/program_meta.proto

