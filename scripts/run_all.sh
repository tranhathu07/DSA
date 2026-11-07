#!/usr/bin/env bash
# Chạy tuần tự 3 lưới (1 CPU, không chạy song song). Có thể chạy lại để tiếp tục.
ROOT="$(cd "$(dirname "$0")/.." && pwd)"; cd "$ROOT"
for g in 100k 10k 1M; do
  java -cp target/classes study.harness.Grid configs/run-$g.properties results/grid-$g
done
echo DONE > results/ALL_DONE
