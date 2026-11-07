package study.harness;

import study.sort.MergeSort;
import study.sort.QuickSort;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * Một "fork" đo: một JVM, một (thuật toán, dataset, cutoff).
 * Cùng giao thức với JMH SingleShotTime: mỗi iteration = một lần sort trên bản clone mới,
 * clone và kiểm tra đúng/sai nằm ngoài vùng đo; warm-up không tính vào kết quả.
 *
 * Dùng: java -cp target/classes study.harness.Bench <algo> <dataset> <cutoff> <warmup> <measurements> [dataDir]
 * In ra stdout: phase,iteration,time_ns   (phase = W hoặc M)
 */
public class Bench {
    @SuppressWarnings("rawtypes")
    static BiConsumer<Comparable[], Integer> algo(String name) {
        return switch (name) {
            case "merge" -> MergeSort::sort;
            case "quick" -> QuickSort::sort;
            default -> throw new IllegalArgumentException("algo: " + name);
        };
    }

    @SuppressWarnings("rawtypes")
    public static void main(String[] args) throws Exception {
        String algoName = args[0], dataset = args[1];
        int cutoff = Integer.parseInt(args[2]), warmup = Integer.parseInt(args[3]), meas = Integer.parseInt(args[4]);
        Path dir = Path.of(args.length > 5 ? args[5] : "data/inputs");
        var sorter = algo(algoName);
        Integer[] original;
        try (var in = new DataInputStream(new BufferedInputStream(Files.newInputStream(dir.resolve(dataset + ".bin"))))) {
            int n = in.readInt(); original = new Integer[n];
            for (int i = 0; i < n; i++) original[i] = in.readInt();   // boxing ở bước chuẩn bị
        }
        Integer[] expected = original.clone(); Arrays.sort(expected);
        StringBuilder sb = new StringBuilder();
        long sink = 0;
        for (int it = 0; it < warmup + meas; it++) {
            Integer[] working = original.clone();                    // ngoài vùng đo
            long t0 = System.nanoTime();
            sorter.accept(working, cutoff);
            long t1 = System.nanoTime();
            if (!Arrays.equals(expected, working)) throw new IllegalStateException("Incorrect sorting");
            sink += working[working.length / 2];
            sb.append(it < warmup ? "W," : "M,").append(it < warmup ? it : it - warmup).append(',').append(t1 - t0).append('\n');
        }
        System.out.print(sb);
        if (sink == 42) System.err.println();   // chống loại bỏ mã chết
    }
}
