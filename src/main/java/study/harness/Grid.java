package study.harness;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Chạy lưới thí nghiệm: mỗi (algo, dataset, cutoff, fork) là một JVM riêng.
 * Thứ tự job được xáo trộn theo orderSeed (tránh sai lệch do nhiệt/độ trôi theo thời gian).
 * Có thể chạy tiếp: job đã có file kết quả sẽ được bỏ qua.
 *
 * Dùng: java -cp target/classes study.harness.Grid <config.properties> <outDir>
 * Config: algos, datasets (hoặc "all"), cutoffs, warmup, measurements, forks, orderSeed, jvmArgs
 */
public class Grid {
    public static void main(String[] args) throws Exception {
        Properties cfg = new Properties();
        try (var in = Files.newInputStream(Path.of(args[0]))) { cfg.load(in); }
        Path out = Path.of(args[1]);
        Path jobsDir = out.resolve("jobs");
        Files.createDirectories(jobsDir);
        if (!Files.exists(out.resolve("grid.properties"))) Files.copy(Path.of(args[0]), out.resolve("grid.properties"));
        String[] algos = cfg.getProperty("algos").split(",");
        String[] cutoffs = cfg.getProperty("cutoffs").split(",");
        int forks = Integer.parseInt(cfg.getProperty("forks"));
        String warmup = cfg.getProperty("warmup"), meas = cfg.getProperty("measurements");
        List<String> datasets = new ArrayList<>();
        if (cfg.getProperty("datasets").trim().equals("all")) {
            var lines = Files.readAllLines(Path.of("data/manifest.csv"));
            for (String l : lines.subList(1, lines.size())) datasets.add(l.split(",")[0]);
        } else for (String d : cfg.getProperty("datasets").split(",")) datasets.add(d.trim());
        List<String[]> jobs = new ArrayList<>();
        for (String a : algos) for (String d : datasets) for (String c : cutoffs)
            for (int f = 0; f < forks; f++) jobs.add(new String[]{a.trim(), d, c.trim(), String.valueOf(f)});
        Collections.shuffle(jobs, new Random(Long.parseLong(cfg.getProperty("orderSeed"))));
        try (var w = Files.newBufferedWriter(out.resolve("schedule.csv"))) {
            w.write("order,algo,dataset,cutoff,fork\n");
            int k = 0; for (var j : jobs) w.write((k++) + "," + String.join(",", j) + "\n");
        }
        List<String> jvm = new ArrayList<>(Arrays.asList(cfg.getProperty("jvmArgs", "").trim().split("\\s+")));
        jvm.removeIf(String::isEmpty);
        long start = System.currentTimeMillis();
        int done = 0;
        for (var j : jobs) {
            Path f = jobsDir.resolve(j[0] + "__" + j[1] + "__" + j[2] + "__" + j[3] + ".csv");
            if (!Files.exists(f)) {
                List<String> cmd = new ArrayList<>();
                cmd.add(Path.of(System.getProperty("java.home"), "bin", "java").toString());
                cmd.addAll(jvm);
                cmd.addAll(List.of("-cp", System.getProperty("java.class.path"), "study.harness.Bench", j[0], j[1], j[2], warmup, meas));
                Process p = new ProcessBuilder(cmd).redirectError(ProcessBuilder.Redirect.INHERIT).start();
                byte[] outBytes = p.getInputStream().readAllBytes();
                if (p.waitFor() != 0) throw new IllegalStateException("job failed: " + String.join(" ", j));
                Path tmp = Path.of(f + ".tmp");
                Files.write(tmp, outBytes);
                Files.move(tmp, f);                       // ghi nguyên tử: file tồn tại <=> job hoàn tất
            }
            done++;
            if (done % 50 == 0 || done == jobs.size())
                System.out.printf("%d/%d jobs, %.1f min%n", done, jobs.size(), (System.currentTimeMillis() - start) / 60000.0);
        }
    }
}
