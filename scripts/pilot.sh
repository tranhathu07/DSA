#!/usr/bin/env bash
# Pilot chọn warm-up: 100 vòng warm-up + 10 vòng đo, mỗi cấu hình 2 fork.
ROOT="$(cd "$(dirname "$0")/.." && pwd)"; OUT="$ROOT/results/pilot"; mkdir -p "$OUT"; cd "$OUT"
for a in merge quick; do for d in random_10000_1 random_100000_1 random_1000000_1 duplicates_100000_1; do for c in 0 10; do for f in 1 2; do
  [ -s "pilot__${a}__${d}__${c}__${f}.csv" ] && continue
  java -Xms1g -Xmx1g -cp "$ROOT/target/classes" study.harness.Bench $a $d $c 100 10 "$ROOT/data/inputs" | sed "s/^/$a,$d,$c,$f,/" > "pilot__${a}__${d}__${c}__${f}.csv.tmp" \
   && mv "pilot__${a}__${d}__${c}__${f}.csv.tmp" "pilot__${a}__${d}__${c}__${f}.csv"
done; done; done; done
echo DONE > "$OUT/DONE"
